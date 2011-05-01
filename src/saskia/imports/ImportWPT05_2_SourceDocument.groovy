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
import saskia.db.obj.*
import saskia.db.table.*
import saskia.db.database.*

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import rembrandt.obj.Document

import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag
import rembrandt.io.WPT05Reader
import rembrandt.io.UnformattedStyleTag
import saskia.util.validator.*


class ImportWPT05_2_SourceDocument extends Import {
   
   Configuration conf = Configuration.newInstance()
   WPT05Reader reader
   RembrandtWriter writer
   
   public ImportWPT05_2_SourceDocument() {
	   super()
	   reader = new WPT05Reader(new UnformattedStyleTag(
		   conf.get("rembrandt.input.styletag.lang", "pt")))
	   writer = new RembrandtWriter(new RembrandtStyleTag(
		   conf.get("rembrandt.output.styletag.lang", "pt")))
   }
   
   public importer() {
	   
	   // connect the file input stream reader to the SecondHaremReader
	   reader.processInputStream(this.inputStreamReader)
	   
	   // discard all existing NEs
	   reader.docs.each{doc ->
		   if (!doc.lang) doc.lang = collection.col_lang
		   String content = writer.printDocument(doc)
		   SourceDoc s = addSourceDoc(doc.docid, content, doc.lang, doc.date_created, "")
		   if (s) status.imported++ else status.skipped++
	   }
   }
   
   static void main(args) {
	   
	   Options o = new Options()
	   Configuration conf = Configuration.newInstance()
	   
		o.addOption("db", true, "target Saskia DB (main/test)")
	   o.addOption("col", true, "target collection name/id of the DB")
	   o.addOption("file", true, "source collection file to load")
	   o.addOption("encoding", true, "file encoding")
	   o.addOption("help", false, "Gives this help information")
	   
	   CommandLineParser parser = new GnuParser()
	   CommandLine cmd = parser.parse(o, args)
	   
	   ImportWPT05_2_SourceDocument importer = new ImportWPT05_2_SourceDocument()
	   String DEFAULT_COLLECTION_NAME = "WPT 05"
	   String DEFAULT_DB_NAME = "main"
	 	String DEFAULT_ENCODING = conf.get("rembrandt.input.encoding",
			System.getProperty("file.encoding"))
	   
	   log.info "******************************************"
	   log.info "* This class loads the WPT 05 Collection *"
	   log.info "******************************************"
   
	   // --help
	   if (cmd.hasOption("help")) {
		   HelpFormatter formatter = new HelpFormatter()
		   formatter.printHelp( "java "+importer.class.name, o )
		   System.exit(0)
	   }

		// --db
		SaskiaDB db = new DBValidator()
			.validate(cmd.getOptionValue("db"), DEFAULT_DB_NAME)	
		importer.setDb(db)
		log.info "DB: $db"

		// --col
		Collection collection = new CollectionValidator(db)
			.validate(cmd.getOptionValue("col"), DEFAULT_COLLECTION_NAME)
		importer.setCollection(collection)
		log.info "Collection: $collection"		
		
		// --file
		File file = new FileValidator()
			.validate(cmd.getOptionValue("file"), null, true)
	
		//--encoding
		String encoding = new EncodingValidator()
			.validate(cmd.getOptionValue("encoding"), DEFAULT_ENCODING)
	
		importer.setFile(file)
		importer.setEncoding(encoding)
		log.info "File: $file <"+encoding+"> "	
		
		importer.prepareInputStreamReader()
		importer.importer()
		log.info importer.statusMessage()
		
	}
}
