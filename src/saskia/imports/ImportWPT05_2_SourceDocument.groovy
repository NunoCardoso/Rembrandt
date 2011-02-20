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

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import javax.xml.parsers.SAXParserFactory
import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.* 

import rembrandt.obj.Document
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag


/** 
 * This class imports WPT05 files to the Source Documents
 * 
 * The files are in RDF format, and they are huge. So I need a SAX parser
 * I'll use import javax.xml.parsers.SAXParserFactory
 * format:

<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterm="http://purl.org/dc/ter
ms/" xmlns:ore="http://www.openarchives.org/ore/terms/" xmlns:wpt="http://xldb.di.fc.ul.pt/wpt/"> 
  <rdf:Description rdf:about="http://xldb.di.fc.ul.pt/rebil/tools#ReM">
    <ore:describes rdf:resource="http://xldb.di.fc.ul.pt/rebil/tools"/>
    <rdf:type rdf:resource="http://www.openarchives.org/ore/terms/ResourceMap"/>
  </rdf:Description>
  <rdf:Description rdf:about="http://xldb.di.fc.ul.pt/linguateca/primeira_proposta.html">
    <ore:isAggregatedBy rdf:resource="http://xldb.di.fc.ul.pt/linguateca"/>
    <wpt:ipAddr rdf:datatype="http://www.w3.org/2001/XMLSchema#string">194.117.22.87</wpt:ipAddr>
    <wpt:server rdf:datatype="http://www.w3.org/2001/XMLSchema#string">apache</wpt:server>
    <wpt:statusCode rdf:datatype="http://www.w3.org/2001/XMLSchema#int">200</wpt:statusCode>
    <dcterm:modified rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2004-08-30T23:00:00Z</dcterm:modified>
    <wpt:fetched rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2005-07-23T10:22:43Z</wpt:fetched>
    <dc:format rdf:resource="text/html"/>
    <wpt:arcName rdf:resource="WPT-9-20080822122528-00677"/>
    <wpt:filteredText>&gt; XLDB Group - primeira proposta
fcul
</wpt:filteredText>
  <dc:language>pt</dc:language>
</rdf:Description>
 */


class WPT05Handler extends DefaultHandler {
	
	ImportWPT05_2_SourceDocument w2s
	RembrandtWriter writer 

	public WPT05Handler(ImportWPT05_2_SourceDocument w2s) {
		this.w2s = w2s
		writer = new RembrandtWriter(new RembrandtStyleTag("pt"))
	}

	 def text
	 def content
	 Date date_modified
	 Date date_fetched
	 String lang 
	 String id
	
    void startElement(String ns, String localName, String qName, Attributes atts) {
        switch (qName) {
           case 'rdf:Description':
	 			text = ""; content = null; lang = null; id = null;
				break
           case 'dcterm:modified':
				text = "";
				date_modified = null;
				break
           case 'wpt:fetched':
				text = "";
				date_fetched = null;
				break 
         case 'wpt:arcName':
				id = atts.getValue('rdf:resource')
				break 
           case 'dc:language':
				text = "";
				lang = null;
				break 
           case 'wpt:filteredText':
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
			case 'rdf:Description':
				Date date = null
				if (date_modified)
					date = date_modified
				if (!date && date_fetched)
					date = date_fetched
				if (!date)
					date = new Date(0)
				addSourceDoc(id, content, lang, date)
				break
           case 'dcterm:modified':
				date_modified = Date.parse("yyyy-MM-dd'T'HH:mm:ss'Z'", text)
				break
           case 'wpt:fetched':
				date_fetched = Date.parse("yyyy-MM-dd'T'HH:mm:ss'Z'", text)
				break 
           case 'dc:language':
				lang = text;
				break 
           case 'wpt:filteredText':
				Document doc = new Document()
				doc.body = text
				doc.docid = id
				doc.lang = lang
				doc.tokenize()
				content = writer.printDocument(doc)
				break
        }
    }
}

class ImportWPT05_2_SourceDocument {
	
	Configuration conf 
	static Logger log = Logger.getLogger("SaskiaImports")
	Collection collection 
	String lang= "pt"
	String filename
   HashMap status

	File f 
	FileInputStream fis
	
	public ImportWPT05_2_SourceDocument(String filename, Collection collection) {
        
		conf = Configuration.newInstance()
	   this.collection = collection
	
		f= new File(filename)
		fis = new FileInputStream(f)
      status = [imported:0, skipped:0]
	}
	
	public HashMap parse() {
		def handler = new WPT05Handler(this)
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
	
		Options o = new Options()
		o.addOption("file", true, "collection file to load")
		o.addOption("col", true, "collection number/name")
		o.addOption("help", false, "Gives this help information")
	    
		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

	   if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java saskia.imports.ImportWPT05_2_SourceDocument", o )
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
			println "No --file arg. Please specify the file. Exiting."
			System.exit(0)
		}
	    
		ImportWPT05_2_SourceDocument w2s = new ImportWPT05_2_SourceDocument(
			cmd.getOptionValue("file"), collection)
			
		HashMap status = w2s.parse()
		log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
	}
}
