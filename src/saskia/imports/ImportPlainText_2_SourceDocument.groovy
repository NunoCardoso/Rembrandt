
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
import saskia.db.DocStatus;
import saskia.db.obj.*
import saskia.db.database.*

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import java.util.regex.*

import rembrandt.obj.Document

import rembrandt.io.UnformattedReader
import rembrandt.io.UnformattedStyleTag
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag
import saskia.util.validator.*

/** 
 * This class imports plain files to the Source Documents
*/


class ImportPlainText_2_SourceDocument extends Import  {

	
	Configuration conf = Configuration.newInstance()
	UnformattedReader reader
	RembrandtWriter writer
	
	public ImportPlainText_2_SourceDocument() {
		super()
		reader = new UnformattedReader(new UnformattedStyleTag(
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
			if (!doc.docid) doc.docid = "doc_"+new Date().format('yyyyMMddHHmmss')
			String content = writer.printDocument(doc)
			
			// today's date
			Date date_created = new Date()
			
			SourceDoc s = addSourceDoc(doc.docid, content, doc.lang, date_created, "")
			if (s) status.imported++ else status.skipped++
		}
	}
 	
	static void main(args) {
		
		Options o = new Options()
		Configuration conf = Configuration.newInstance()
		
		o.addOption("db", true, "target Saskia DB (main/test)")
		o.addOption("col", false, "collection number/name")
		o.addOption("file", false, "collection file to load")
		o.addOption("encoding", false, "encoding")
		o.addOption("doc_id", false, "document id")
		o.addOption("lang", false, "language")
		o.addOption("help", false, "Gives this help information")
	    
		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)
		
		ImportPlainText_2_SourceDocument importer = new ImportPlainText_2_SourceDocument()

		String DEFAULT_COLLECTION_NAME = 'DefaultCollection'
		String DEFAULT_DB_NAME = 'main'
		String DEFAULT_ENCODING = conf.get("rembrandt.input.encoding",
			System.getProperty("file.encoding"))
		
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
