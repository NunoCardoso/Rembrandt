/* Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package saskia.wikipedia

import javax.xml.parsers.SAXParserFactory
import org.xml.sax.*
import org.xml.sax.helpers.DefaultHandler
import groovy.sql.Sql
import saskia.util.ProgressBar

import org.apache.lucene.index.IndexWriter
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field

import saskia.bin.Configuration
import rembrandt.io.*
import org.apache.log4j.*

/**
 * @author Nuno Cardoso
 * Wikipedia SAX handler. It does all the indexing work.
 * 
 * Wikipedia preprocessed XML comes in this format.
 * &lt;page id=&quot;220&quot; orglength=&quot;18412&quot; newlength=&quot;16093&quot; 
 * stub=&quot;0&quot; categories=&quot;1&quot; outlinks=&quot;117&quot; urls=&quot;7&quot;&gt;
 * &lt;title&gt;Astronomia&lt;/title&gt;
 * &lt;categories&gt;14983&lt;/categories&gt;
 * &lt;links&gt;2345 467 45 467 235 (...)&lt;/links&gt;
 * &lt;urls&gt;
 * http://...
 * &lt;/urls&gt;
 * &lt;text&gt;
 * (...)
 * &lt;/text&gt;
 * &lt;/page&gt;
 * &lt;/mediawiki&gt;
 */
class WikipediaHandler2 extends DefaultHandler {

   def title = '' // container for title
   def text = '' // container for text
   def id = '' // container for id
   
   def s=''
   def l = ''
   def h = ''
   def n = ''
   
   def bool_listings = false
   def bool_headings = false
   def bool_nes = false
   
   def do_nes = false // do NEs or not
   def do_listings = false // do listings or not
   def do_headings = false // do headings or not
   def doIndexFullText // do full text
   
   def relatedpages = [] // container for related pages
   def headings = [] // container for <H?>
   def listings = [] // <LI>
   //def inlinks = [] fetched in the DB
   def outlinks = [] // container for outlinks
   def categories = [] // container for categories
   def nes = [] // container for NEs (note: are all [[x]]. But when there is no link, they are erased on inlinks.
 
   def hashIdTitle = [:] // converter id for title
   def hashRelated = [:] 
   
   // other stuff
   def writer, matcher, pb, db
   def count = 1
   
   def Configuration conf
   def static Logger log = Logger.getLogger("WikipediaIndexing")
   
   /** set up the configuration */
   public WikipediaHandler(conf) {
       this.conf=conf
    }
	  
   /**
    * sets up hash tables, if doReadAuxHashes = true, and 
    * rebuilds inlinks table, if doBuildInlinksTable = true
    * configures doIndexFullText flag.
    */
   void initialize(boolean doReadAuxHashes = true, 
	               boolean doBuildInlinksTable = false, 
	               boolean doIndexFullText = false) {

	   this.doIndexFullText = doIndexFullText   
	   
	   log.info "Wikipedia Indexer configuration:"
	   log.info " 1. doReadAuxHashes? $doReadAuxHashes."
	   log.info " 2. doBuildInlinksTable? $doBuildInlinksTable."
	   log.info " 3. doIndexFullText? $doIndexFullText."   
		   
	   pb = new ProgressBar()
	   
	   try {	
	       writer = new IndexWriter(conf.get("wikipedia.index"), new StandardAnalyzer(), true)
	   }  catch (Exception e) {
	       log.warn "Can't open index for write."
	       e.printStackTrace()
	   }
   
	   log.info "Opening DB ${conf.get('db.url')}"	 
	   db = WikipediaDB.newInstance().getDB()	   

      if (doReadAuxHashes) {
   	 
    	/** open and populate id title hash **/
   	  	log.info "Opening ${conf.get('wikipedia.idtitle.file')}"
   	  	def f = new File(conf.get('wikipedia.idtitle.file'))
   	  	pb.setTotalvalue("wc -l ${conf.get('wikipedia.idtitle.file')}".execute().text.split(" ").getAt(0).trim())
    	 
   	  	f.eachLine { line ->  	  	
   	  		matcher = line =~ /^(\d+)\t(.*)$/
   	  		if (matcher) hashIdTitle[matcher.group(1)]=matcher.group(2)
   	  		pb.incrementValue()
   	  		pb.printProgressBar()		
   	  	}	  
   	  	log.info "\nDone. Read "+hashIdTitle.size()+" records."
 
   	    /** open and populate related hash **/
   	  	log.info "Opening ${conf.get('wikipedia.related.file')}"  	 
   	  	f = new File(conf.get('wikipedia.related.file'))
   	  	pb.reset()
   	  	pb.setTotalvalue("wc -l ${conf.get('wikipedia.related.file')}".execute().text.split(" ").getAt(0).trim())
   	  	f.eachLine { line -> 
   	  		matcher = line =~ /^(\d+)\t(.*)$/
   	  		if (matcher) hashRelated[matcher.group(1)]=matcher.group(2)
   	  		pb.incrementValue()
   	  		pb.printProgressBar()		 	 
   	  	}
   	  	log.info "\nDone. Read "+hashRelated.size()+" records." 
   	  	
      }//doreadHashes
   
      if (doBuildInlinksTable) {
  	 
    	// open inlinks file
   	  	log.info "Opening ${conf.get('wikipedia.inlinks.file')}"
   	  	def f = new File(conf.get('wikipedia.inlinks.file'))
   	  	pb.setTotalvalue("wc -l ${conf.get('wikipedia.inlinks.file')}".execute().text.split(" ").getAt(0).trim())
   	  	log.info "cleaning table."
   	  	// clean the database.
   	  	db.execute clean
   	  	f.eachLine { line ->  	  	
   	  		matcher = line =~ /^(\d+)\t(\d+)\t(.*)$/
   	  		if (matcher) {
   	  	    	                                    // target ID    	source ID	      anchor text
   	  	       db.execute DBConnect.insertInlinks, [matcher.group(1), matcher.group(2), matcher.group(3)]
   	  		}
   	  		pb.incrementValue()
   	  		pb.printProgressBar()			  		
   	  	}	  
   	  	log.info "\nDone. Read "+hashIdTitle.size()+" records."  	  
	 }// if doBuildInlinksTable
   }//initialize()
   
   /** 
    * SAX starting elements.
    */
   void startElement (String namespace, String localname, String qName, Attributes atts) {
	   switch (qName) {
	   case 'page':
		   	  relatedpages = []		 
		   	  headings=[]
		   	  nes=[]
		   	  listings=[]
		   	  categories = []
		   	  //inlinks = []
		   	  outlinks = []
		   	  title = ''
		   	  text = ''
		   	  id = atts.getValue('id')
			  break   
	   case 'title': s = '';  break
	   case 'categories': s = ''; break         
	   case 'links': s = ''; break
	   case 'urls': s=''; break
	   case 'text': s=''; break
	   case ~/^(EM|em)$/ :	
		      n = ''
		   	  bool_nes = true;
		   	  break   
	   case ~/^[Bb]$/:	 break  
	   case ~/^[Ii]$/:	  break    	   	
	   case ~/^(LI|li)$/:
   		      l= ''
   		      bool_listings = true;
 		   	  break 
   	   case ~/^[Hh]\d/:	// <H1>..<H6>	
   	   		  h= ''
  		      bool_headings = true; 
   	   		break   	  
 	   }	
   }

   /** 
    * SAX closing elements.
    */
   void endElement(String namespace, String localname, String qName) {
     switch (qName) {
	   case 'page':
		   addToDocument(title, text, id, relatedpages, headings, outlinks, categories, nes)
			  break   
	   case 'title':
		   	  title = s.toString()
	          break
	   case 'categories':
	       	  def cats = s.toString()
	       	  if (cats != null && cats.size() > 0) 
	       		  cats.split(" ").each{ it -> 
	       		  if (hashIdTitle[it] != null) categories.add(hashIdTitle[it])}
	       	  break
	   case 'links':
		   	 // este break é para os outlinks. 
		   	  def outl = s.toString()
		   	  if (outl != null && outl.size() > 0) 
		   		  outl.split(" ").each{ it ->
		   		  if (hashIdTitle[it] != null ) outlinks.add(hashIdTitle[it])}	   	  
	          break
	   case 'urls':
		   	 break
	   case 'text':
		   	  text += s.toString()
		   	  break
	   case ~/^(EM|em)$/ :
		   // no caso de EM emparelhadas, tipo <EM>..<EM>---</EM>...</EM>, eu recolho sempre as de dentro.
		   // como tal, o primeiro </EM> vem com bool_ne = true, o outro vem com bool_ne 0 false. Ou seja, 
		   // só recolho quando bool_ne=true. Assim, evito links grandes por mau parsing da wikipedia.
		   if (bool_nes) {
			   def ne = n.toString()
			   if (ne != null && ne.size() > 0) { 
					nes.add(ne.trim())	 
					bool_nes = false
				}
		   }
  			break  			 
	   case ~/^[Bb]$/:	
		      //nada
		   	  break  
	   case ~/^[Ii]$/:		
		      //nada
		   	  break    	   			   	  
	   case ~/^(LI|li)$/:	
		   def listing = l.toString()
		   if (listing != null && listing.size() > 0) { 
		      listing = listing.replaceAll("\n"," ")
		   	  listings.add(listing.trim())	 
		   	  bool_listings = false
		   }
		   break 
   	   case ~/^[Hh]\d/:	// <H1>..<H6>		
   			def heading = h.toString()
   			if (heading != null && heading.size() > 0 && !(heading =~ /^\s+$/)) { 
   				heading = heading.replaceAll("\n"," ")
   				headings.add(heading.trim())	 
   				bool_headings = false
   			}
	   		break 
       }
    }

   /**
    * Prepare for Wikipedia indexing
    * Estimates the number of docs based on the hashIdTitle size.
    */
   public prepareForWikipedia() {
	   pb.reset()
	   // n. de docs = tamanho da hash idTitle
	   pb.setTotalvalue(hashIdTitle.size() < 2 ?  500000 : hashIdTitle.size())
	   log.info "initalizing pb: "+pb.getTotalvalue()
   }

   /**
    * Index a document.
    */
   private addToDocument(
   		 String title, String text, String id, List relatedpages, 
   		 List headings, List outlinks, List categories, List nes) {
   	try {
	      Document doc = new Document()

	      def int numid = Integer.parseInt(id)
	      def boolean debug = false
	      
	      def inlinks = db.rows("SELECT source, anchor FROM "+conf.get('db.table.pagelinks')+" WHERE target=${numid}")	      
	      def outlinks2 = db.rows("SELECT target, anchor FROM "+conf.get('db.table.pagelinks')+" WHERE source=${numid}")	      

	      log.debug "adding "+id+": "+title
	      doc.add(new Field("id",id, Field.Store.YES, Field.Index.UN_TOKENIZED))
	      log.debug "adding title "+title
	      doc.add(new Field("title",XMLUtil.trimNewlines(XMLUtil.decodeXML(title)), Field.Store.YES, Field.Index.TOKENIZED))
	      if (doIndexFullText) {
		  		log.debug "adding text size "+text.size()
		  		doc.add(new Field("text", XMLUtil.trimNewlines(XMLUtil.decodeXML(text)), Field.Store.YES, Field.Index.TOKENIZED))
	      }
	      log.debug "hashIdTitle:"+hashIdTitle.size()+"relatedpages:"+relatedpages.size()+" Headings:"+headings.size()+" inlinks:"+inlinks.size()+" outlinks:"+outlinks.size()+" categories:"+categories.size()+" listings:"+listings.size()+" nes:"+nes.size() 

		  log.debug "adding relatedpages "+relatedpages
	      relatedpages?.each {doc.add(new Field("relatedpages", it, Field.Store.YES, Field.Index.TOKENIZED))}
	      
		  log.debug "adding headings "+headings
	      headings?.each {doc.add(new Field("headings", it, Field.Store.YES, Field.Index.TOKENIZED))}
	      
	      log.debug "adding inlinks "+inlinks
		  inlinks?.each {doc.add(new Field("inlinks", "["+it.source+"]["+it.anchor+"]", Field.Store.YES, Field.Index.TOKENIZED))}
	      
		  log.debug  "adding outlinks "+outlinks2
		  outlinks2?.each {doc.add(new Field("outlinks", "["+it.target+"]["+it.anchor+"]", Field.Store.YES, Field.Index.TOKENIZED))}  
   		 
		  log.debug  "adding categories "+categories
	      categories?.each {doc.add(new Field("categories", it, Field.Store.YES, Field.Index.TOKENIZED))}

		  log.debug  "adding listings "+listings
		  listings?.each {doc.add(new Field("listings", it, Field.Store.YES, Field.Index.TOKENIZED))}
	     
		  log.debug  "adding nes "+nes
	      nes?.each {doc.add(new Field("nes", it, Field.Store.YES, Field.Index.TOKENIZED))}

	      writer.addDocument(doc)
	     
		  pb.incrementValue()
	  	  pb.printProgressBar()		
   	  	
      } catch (Exception e) {  e.printStackTrace() }
	}
   
   /** 
    * SAX character manipulation
    */
    public void characters(char[] buffer, int start, int length) {
        s += new String(   buffer, start, length);
      if (bool_listings) {l += new String(buffer, start, length);}
      if (bool_headings) {h += new String(buffer, start, length);}
      if (bool_nes) {n += new String(buffer, start, length);}
    }

    /**
     * Finalize stuff.
     */
    void finalize() {
       db.close()
       log.info "\nOptimizing index..."
       writer.optimize() 
       log.info "Done."
       writer.close()
     }
}
