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

import saskia.bin.Configuration
import saskia.io.Collection
import saskia.io.SourceDoc
import saskia.io.DocStatus
import saskia.converters.WikipediaDocument2RembrandtDocumentConverter

import org.apache.log4j.Logger
import org.apache.commons.cli.*
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.* 

import java.util.regex.*

import rembrandt.obj.Document
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag


/** 
 * This class importsWikipedia XML dump files to the Source Documents
 * uses SAX.
XML is:

<?xml version="1.0" encoding="utf-8" ?>
<mediawiki xmlns="http://www.mediawiki.org/xml/export-0.3/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.mediawiki.org/xml/export-0.3/ http://www.mediawiki.org/xml/export-0.3.xsd" version="0.3" xml:lang="en">
  <siteinfo>
    <sitename>Wikipedia</sitename>
    <base>http://nn.wikipedia.org/wiki/Hovudside</base>
    <generator>MediaWiki 1.16wmf4</generator>
    <case>first-letter</case>
    <namespaces>(...)</namespace>
    </namespaces>
  </siteinfo>
  <page>
    <title>Nynorsk</title>
    <id>12</id>
    <revision>
      <id>1531310</id>
      <timestamp>2011-01-25T14:39:19Z</timestamp>
      <contributor>
        <username>Eivindgh</username>
        <id>1460</id>
      </contributor>
      <minor/>
      <comment> Nynorsk i skulen </comment>
      <text xml:space="preserve">
		(...)
		</text>
    </revision>
  </page>

*/

class WikipediaXMLHandler extends DefaultHandler {
	
	ImportWikipediaXML_2_SourceDocument w2s
	WikipediaDocument2RembrandtDocumentConverter converter
	static Logger log = Logger.getLogger("SaskiaImports")
	
	public WikipediaXMLHandler(ImportWikipediaXML_2_SourceDocument w2s) {
		this.w2s = w2s
		converter = new WikipediaDocument2RembrandtDocumentConverter(w2s.lang)
	}

	 def text
	 def content
	 Date date
	 String lang
	 String id
	 String title
	 String doc_id
		
	 boolean inpage = false;
	 boolean inrevision = false;
	 boolean incontributor = false;
	
    void startElement(String ns, String localName, String qName, Attributes atts) {
        switch (qName) {

           case 'page':
	 			text = ""; content = null; id = null;
		      inpage = true;
				break

			  case 'base': 
				lang=null;
				break;
 
          case 'timestamp':
				text = "";
				date = null;
				break

			 case 'revision':
				 inrevision = true;
				break;

			 case 'title':
				 title = "";
				break;

			case 'contributor':
				incontributor=true;
 				break;

			// se for de ids que não interessam, não apagar
 			case 'id':
				if (inpage && !inrevision & !incontributor) {
					id="";
				}
 				break;

          case 'text':
				text = "";
				content = null;
				break 
        }
    }

    void characters(char[] chars, int offset, int length) {
       text += new String(chars, offset, length)      
    }

    void endElement(String ns, String localName, String qName) {
        switch (qName) {

        case 'text':
				if (title && id)  {
					doc_id = id+"_"+title
					int upperlimit = (doc_id.size() > 250 ? 250: doc_id.size())
					doc_id = doc_id.substring(0, upperlimit);
				}
				//println("the_id: $the_id")
				content = converter.parse(text.trim(), title, lang, doc_id)
				inpage = false;
				text = "";
				break
				
			  case 'base': 
				text.findAll(/http:\/\/(\w*).wikipedia.org\/.*/) {all, g1 -> lang = g1}
				text = "";
				break;


//		String parsedText = w2h.parse(wdoc.text, wdoc.rawTitle, wdoc.lang)


          case 'timestamp':
				try {
					date = Date.parse("yyyy-MM-dd'T'HH:mm:ss'Z'", text)
				} catch(Exception e) {
					date = new Date(0)
				}
				text = "";
				break

			 case 'revision':
				 inrevision = false;
				text = "";
				break;

			case 'contributor':
				incontributor=false;
				text = "";
 				break;

			 case 'title':
//				println "Got title: $text"
				 title = text.trim();
				text = "";
				break;

 			case 'id':
				if (inpage && !inrevision & !incontributor) {
//					println "Got id: $text"
					id = text.trim();
				}
				text = "";
 				break;

			case 'page':
				log.info("Adding $doc_id to SourceDoc...")
				w2s.addSourceDoc(doc_id, content, lang, date)
				text = "";
				break
        }
    }
}

class ImportWikipediaXML_2_SourceDocument {
	
	Configuration conf 
	static Logger log = Logger.getLogger("SaskiaImports")
	Collection collection 
	String lang
	String filename
   HashMap status

	File f 
	FileInputStream fis
	
	public ImportWikipediaXML_2_SourceDocument(String filename, Collection collection, String lang) {
        
		conf = Configuration.newInstance()
	   this.collection = collection
		this.lang=lang
		
		f= new File(filename)
		fis = new FileInputStream(f)
      status = [imported:0, skipped:0]
	}
	
	public HashMap parse() {
		def handler = new WikipediaXMLHandler(this)
		def reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader()
		reader.setContentHandler(handler)
		reader.parse(new InputSource(fis))
		return status
   }

   public SourceDoc addSourceDoc(String original_id, String content, String lang, Date date) {
		
		SourceDoc s = new SourceDoc(
			sdoc_original_id:original_id,
        sdoc_collection:collection, 
        sdoc_lang:lang, 
        sdoc_content:content, 
        sdoc_doc:null,
        sdoc_date:date,
        sdoc_proc:DocStatus.READY,
        sdoc_comment:""
		)
		// by adding to DB, it already checks for duplicates
		
      try {
         s.addThisToDB()
         log.debug "Inserted $s into Saskia DB."
			status.imported++
      } catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
         log.warn "Found duplicate entry in DB. Skipping."  
			status.skipped++

      }catch(Exception e2) {
         log.warn "Found other error. Skipping. " + e2.getMessage()  
			status.skipped++
		}
		return s
	
	//println "I have $s. $content" 	
	}      
 	
	static void main(args) {
	
		Configuration conf = Configuration.newInstance()
		Options o = new Options()
		o.addOption("file", true, "collection file to load")
		o.addOption("lang", true, "language")
		o.addOption("col", true, "collection number/name")
		o.addOption("help", false, "Gives this help information")
	    
		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

	   if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java saskia.imports.ImportWikipediaXML_2_SourceDocument", o )
			System.exit(0)
		}

		Collection collection = null
		String collection_ = cmd.getOptionValue("col")
		try {
			collection = Collection.getFromName(collection_)
		}catch(Exception e) {}	
		    
	 	if (!collection) {
			try {
				 collection = Collection.getFromID(
					Integer.parseInt(collection_)	)    
			}catch(Exception e) {}
		}
		if (!collection) {
			throw new IllegalStateException("Don't know where the collection $collection_ is. Exiting.")
			System.exit(0)
		}       

	   if (!cmd.hasOption("file")) {
			log.info "No --file arg. Please specify the file. Exiting."
			System.exit(0)
		}
	
		String lang = null
		if (!cmd.hasOption("lang")) {
			lang = conf.get("global.lang")
			log.info "No --lang arg. Using default $lang"
		}   else {
			lang = cmd.getOptionValue("lang") 
			log.info "Using language $lang"
		}
		 
		ImportWikipediaXML_2_SourceDocument w2s = new ImportWikipediaXML_2_SourceDocument(
			cmd.getOptionValue("file"), collection, lang)
			 
		HashMap status = w2s.parse()
		log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
	}
}
