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

import java.util.regex.*

import rembrandt.obj.Document
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag


/** 
 * This class imports plain files to the Source Documents
*/


class ImportPlainText_2_SourceDocument {
	
	Configuration conf 
	static Logger log = Logger.getLogger("SaskiaImports")

	// params collected from args
	Collection collection 
	String lang
	File file
	String encoding
	String doc_id 
	
	RembrandtWriter writer 
	HashMap status

	public ImportPlainText_2_SourceDocument(File file, Collection collection,
			String encoding, String doc_id, String lang) {
        
		conf = Configuration.newInstance()
	   this.collection = collection
		this.encoding = encoding
		this.file = file
		this.lang = lang
		this.doc_id = doc_id
		
		writer = new RembrandtWriter(new RembrandtStyleTag(this.lang))
      status = [imported:0, skipped:0]
	}
	
	HashMap parse() {
	   
		/* let's leave the reader line-by-line, so that it's easy to extend 
		   for custom formats */
		
		StringBuffer buffer = new StringBuffer()
		InputStreamReader is = new InputStreamReader(
			new FileInputStream(this.file), this.encoding)
		BufferedReader br = new BufferedReader(is)
		String l
		while ((l = br.readLine()) != null) {
			buffer.append(l)
		}
		
		/* create a Document out of the text */
		Document doc = new Document()
		doc.body = buffer.toString()
		doc.docid = this.doc_id
		doc.lang = this.lang
		
		/* tokenize and print the document in the REMBRANDT format */
		doc.tokenize()
		String content = writer.printDocument(doc)
		Date date_created = new Date(0)

		/* add to DB */
		addSourceDoc(doc, content, date_created)
		return this.status
   }

   public SourceDoc addSourceDoc(Document doc, String content, Date date) {
		
		SourceDoc s = new SourceDoc(
		  sdoc_original_id:doc.docid,
        sdoc_collection:this.collection, 
        sdoc_lang:doc.lang, 
        sdoc_content:content, 
        sdoc_doc:null,
        sdoc_date:date,
        sdoc_proc:DocStatus.READY,
        sdoc_comment:""
		)
		// by adding to DB, it already checks for duplicates
		
      try {
         def id = s.addThisToDB()
         if (id) {
				log.debug "Document $s inserted into Saskia DB."
				this.status.imported++
			} else {
				log.warn "Document $s was NOT inserted into Saskia DB."
				this.status.skipped++
			}
      } catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
         log.warn "This doc is already on the DB. It's a duplicate, skipping."  
			this.status.skipped++
      } catch(Exception e2) {
         log.warn "Error while trying to write to the DB: " + e2.getMessage()  
			this.status.skipped++
		}
		return s
	}      
 	
	static void main(args) {
	
		Options o = new Options()
		o.addOption("file", false, "collection file to load")
		o.addOption("encoding", false, "encoding")
		o.addOption("doc_id", false, "document id")
		o.addOption("lang", false, "language")
		o.addOption("col", false, "collection number/name")
		o.addOption("help", false, "Gives this help information")
	    
		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)
		Configuration conf = Configuration.newInstance()

	   if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java saskia.imports.ImportPlainText_2_SourceDocument", o )
			System.exit(0)
		}

		/* Saber qual é a colecção */
		Collection collection = null
		
		String collection_ = cmd.getOptionValue("col")

		if (!collection_) {
			print "What is the collection (Name or id)? "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			collection_ = input.readLine().trim()
		}
		
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
			throw new IllegalStateException("Unknown collection $collection_ is. Exiting.")
			System.exit(0)
		} else {
			println "Collection: $collection"
		}    

		/* Saber qual é o ficheiro */
		File file = null
		String file_ = null

	   if (cmd.hasOption("file")) {
			file_ = cmd.getOptionValue("file")
			file = new File(file_)
			if (!file.exists()) {
				println "File ${file_} does not exist. "
				file_ = null
			}
		}
		
		if (!file_) {
		
			print "What is the filename ? "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			file_ = input.readLine().trim()
			
			file = new File(file_)
			if (!file.exists()) {
				println "File ${file_} does not exist. "
				file_ = null
			}
		}
		if (!file_) {
			println "No input file found. Exiting."
			System.exit(0)
		} else {
			println "File: $file"
		}
	    
		/* Saber qual é o encoding do ficheiro */
	   String encoding = ""
	   String encoding_ = conf.get("rembrandt.input.encoding")
      if (!encoding_) encoding_ = System.getProperty('file.encoding')

	   if (!cmd.hasOption("encoding")) {
			print "What is encoding for input filename (default: ${encoding_})? "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			String encoding_2 = input.readLine().trim()
			if (!encoding_2) {
				encoding = encoding_
			} else {
				encoding = encoding_2
			}
		} else {
			encoding = cmd.getOptionValue("encoding")
		}
		
		println "File encoding: $encoding"
	
		/* Saber qual é o doc_id do ficheiro */
	   String doc_id = ""
		String doc_id_ = "doc_"+new Date().format('yyyyMMddHHmmss')
	   if (!cmd.hasOption("doc_id")) {
			print "What is the document_id (default: ${doc_id_})? "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			String doc_id_2 = input.readLine().trim()
			if (!doc_id_2) {
				doc_id = doc_id_
			} else {
				doc_id = doc_id_2
			}
		} else {
			doc_id = cmd.getOptionValue("doc_id")
		}
		
		println "Document id: $doc_id"
	
		/* Saber lang  */
	   String lang = ""
		String lang_ = conf.get("global.lang","pt")
	   if (!cmd.hasOption("lang")) {
			print "What is the document's language (default: ${lang_})? "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			String lang_2 = input.readLine().trim()
			if (!lang_2) {
				lang = lang_
			} else {
				lang = lang_2
			}
		} else {
			lang = cmd.getOptionValue("lang")
		}
		
		println "Document's language: $lang"
	
		ImportPlainText_2_SourceDocument w2s = new ImportPlainText_2_SourceDocument(
			file, collection, encoding, doc_id, lang)
			
		HashMap status = w2s.parse()
		log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
	}
}
