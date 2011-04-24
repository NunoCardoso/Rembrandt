/** This file is part of REMBRANDT - Named Entity Recognition Software
 *  (http://xldb.di.fc.ul.pt/Rembrandt)
 *  Copyright (c) 2008-2009, Nuno Cardoso, University of Lisboa and Linguateca.
 *
 *  REMBRANDT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  REMBRANDT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with REMBRANDT. If not, see <http://www.gnu.org/licenses/>.
 */
 
package saskia.imports

import saskia.io.SourceDoc
import saskia.bin.Configuration
import saskia.db.DocStatus;
import saskia.db.obj.Collection;

import org.apache.log4j.*
import org.apache.commons.cli.*
import java.util.regex.Matcher
import rembrandt.obj.*
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag
import rembrandt.tokenizer.TokenizerPT
/** 
 * This class imports NYT files to the Source Documents
 */
class ImportCLEFCollectionPT2SourceDocument {
	
	Configuration conf
	static Logger log = Logger.getLogger("SaskiaImports")
	Collection collection 
	String lang= "pt"
	String taglang = "pt"  // sempre em PT!!!
	String filename
	RembrandtWriter writer
	RembrandtStyleTag styletag
	TokenizerPT tok = TokenizerPT.newInstance()

	public ImportCLEFCollectionPT2SourceDocument(String filename) {

	    conf = Configuration.newInstance()
	    collection = Collection.getFromName("Público 94-95 e Folha de São Paulo 94-95")	    
	    if (!collection) 
		throw new IllegalStateException("Don't know where the collection is. Exiting.")
	    this.filename = filename
	    styletag = new RembrandtStyleTag(taglang) // sempre em PT!!!
	    writer = new RembrandtWriter(styletag)
	}
	
	public HashMap importDocs() {
	    
	    // what to do here? The title is the sdoc_original_id. 
	    // The real title should be copied from the first line.
	    // 
	    // in PUBLICO, detect title analysing if top 3 lines are < 70 and do not end in .!?
	    // if then, MOVE it
	    //
	    // in FOLHA, you better not look at it. it's all BODY
	    	    
	    // on the header, use: <DOC DOCID="original_doc_id" LANG="pt" TAGLANG="pt">
	     
	    
	    HashMap status = [imported:0, skipped:0]
	    
	    Matcher m
	    boolean inbody = false
	    int body_sentence_count = 0// to count body line sentences, to detect titles
	    String type // is "GH" or "LAT"
	    
	    Document doc
	    String docid
	    
	    GregorianCalendar date_created

	    StringBuffer body
	    
	    new File(filename).eachLine{l -> 
	    
	         m = l =~ /<html>/
	         if (m.matches()) { 
	             doc = new Document()
	             docid = null
	             type = null
	             body_sentence_count = 0
	             doc.lang = this.lang
	             doc.taglang = this.taglang
	             return
	         }
	         
	         m = l =~ /<\/html>/
	         
	         if (m.matches()) { 
	             
	             doc.docid = docid 
	             doc.body = body.toString()
	             doc.tokenizeBody()
	             String text2 = writer.printDocument(doc)
	             log.debug ("Loaded $docid")
	             SourceDoc sdoc = addSourceDoc(docid, text2, date_created.time) 
	             if (sdoc) {
	        	 status.imported++ 
	        	 log.debug ("SourceDoc id: ${sdoc.sdoc_id} webstore: ${sdoc.sdoc_webstore}")
	             } else {
	        	 status.skipped++ 
	        	 log.debug ("SourceDoc null")
	             }
	             return
	         }

	         // LATime identifier: month, day, year
	    	 m = l =~ /<title>PUBLICO-(\d{4})(\d{2})(\d{2})-(\d+)<\/title>/
	    	 if (m.matches()) {
	    	    
	    	     docid = "PUBLICO-"+m.group(1)+m.group(2)+m.group(3)+"-"+m.group(4)
	    	     type = "PUBLICO"
	    		 
	    	     date_created = new GregorianCalendar()
	 	     date_created.set(Calendar.YEAR, Integer.parseInt(m.group(1)) )
	 	     date_created.set(Calendar.MONTH, Integer.parseInt(m.group(2)) -1)
	 	     date_created.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)) )	 	    
	 	     date_created.set(Calendar.HOUR, 0 )	
	 	     date_created.set(Calendar.MINUTE, 0)	
	 	     date_created.set(Calendar.SECOND, 0 )	
	    	     body = new StringBuffer()
	    	     return
	    	 }
	    	 
	    	 // GH identifier: year, month, day
	    	 m = l =~ /<title>FSP(\d{2})(\d{2})(\d{2})-(\d+)<\/title>/
	    	     	
	    	 if (m.matches()) {
	    	    
	    	     docid = "FSP"+m.group(1)+m.group(2)+m.group(3)+"-"+m.group(4)
	    	     type = "FSP"
	    		 
	    	     date_created = new GregorianCalendar()
	 	     date_created.set(Calendar.YEAR, Integer.parseInt("19"+m.group(1)) )
	 	     date_created.set(Calendar.MONTH, Integer.parseInt(m.group(2)) -1)
	 	     date_created.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)) )	 	    
	 	     date_created.set(Calendar.HOUR, 0 )	
	 	     date_created.set(Calendar.MINUTE, 0)	
	 	     date_created.set(Calendar.SECOND, 0 )	
	    	     body = new StringBuffer()
	    	     return
	    	 }   
		    	
	    	 m = l =~ /<body>/
	    	 
	    	 if (m.matches()) {
	    	     inbody = true
	    	     return
	    	    
	    	 }
	    	
	    	 m = l =~ /<\/body>/
	    	 if (m.matches()) {    	     
	    	     inbody = false	 	
	    	     return
	    	 }

	    	 if (inbody) {	  
	    	     
	    	     if (type == "FSP") {
	    		 
	    		 def punt = l =~ /.*[\.!\?]$/ 
	    		 
	    		 // promote to title
	    		 if (body_sentence_count == 0 && l.size() < 70 && 
	    			 !punt.matches()) {	    		     
	    		     List<Sentence> res = tok.parse(l)
	    		     if (res && res.size() > 0) doc.title_sentences << res[0]
	    		     body_sentence_count++
	    		     
	    		 } else { 	
	    		   // leave as body
	    		     body.append l +" "
	    		     body_sentence_count++
	    		 }   		 
	    		 
	    	     } else if (type == "PUBLICO") {
	    		 
	    		 def punt = l =~ /.*[\.!\?]$/ 
	    		 
	    		 // promote as title
	    		 if (body_sentence_count < 3 && l.size() < 70 && 
	    			 !punt.matches()) {	   		     
	    		     List<Sentence> res = tok.parse(l)
	    		     if (res && res.size() > 0) doc.title_sentences << res[0]
	    		     body_sentence_count++
	    		 } else {
	    		     body.append l + " "
	    		     body_sentence_count ++
	    		 }
	    		 
	    	     }
	    	 }
	    }// each line 
	    return status           
	}
	
	SourceDoc addSourceDoc(String docid, String content, Date date_created) {
	    
	   SourceDoc s = SourceDoc.getFromOriginalIDandCollectionIDandLang(
		   docid, collection.col_id, lang)
	  
           if (s) {
               log.warn("SourceDoc with docid $docid in collection ${collection.col_id} exists! Skipping")
               return null
           } else {
               s = new SourceDoc(sdoc_original_id:docid,
			sdoc_collection:collection.col_id, 
			sdoc_lang:lang, 
			sdoc_content:content, 
			sdoc_date:date_created,
			sdoc_proc:DocStatus.READY,
			sdoc_edit:DocStatus.UNLOCKED)
		
		try {
		    s.addThisToDB()
			log.debug "Inserted $s into Saskia DB."
		} catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
		    // VARCHAR primary keys are case insensitive. Sometimes there's redirects that are not redirects, 
		    // and so there's attempts to insert documents with a id with case changes. 
		    // let's just catch it and continue
		    log.warn "Found duplicate entry in DB. Skipping."  
		}
		
		return s
           }
 	}
	
	static void main(args) {
	    
	    println "Recommended usage: java -server -Xms128m -Xmx256m -Dfile.encoding=UTF-8 saskia.imports.ImportCLEFCollectionPT2SourceDocument --file=/home/user/PUBLICO9401_UTF8.txt"
	    Options o = new Options()
	    o.addOption("file", true, "NYT collection file to load")
	    o.addOption("help", false, "Gives this help information")
	    
	    CommandLineParser parser = new GnuParser()
	    CommandLine cmd = parser.parse(o, args)

	    if (cmd.hasOption("help")) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "java saskia.imports.ImportCLEFCollectionPT2SourceDocument", o )
		println "Make sure that the collection 'Público 94-95 e Folha de São Paulo 94-95' is on Saskia"
		System.exit(0)
	    }

	    if (!cmd.hasOption("file")) {
		println "No --file arg. Please specify the file. Exiting."
		System.exit(0)
	    }
	    
	    ImportCLEFCollectionPT2SourceDocument w2s = new ImportCLEFCollectionPT2SourceDocument(
		    cmd.getOptionValue("file"))
			
	    HashMap status = w2s.importDocs()
	                      
	    log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
	}
}