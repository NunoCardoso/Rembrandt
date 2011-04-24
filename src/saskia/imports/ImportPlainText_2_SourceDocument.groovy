
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
import saskia.db.obj.Collection;
import saskia.db.obj.SourceDoc;

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import java.util.regex.*

import rembrandt.obj.Document

import rembrandt.io.UnformattedReader
import rembrandt.io.UnformattedStyleTag
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag

/** 
 * This class imports plain files to the Source Documents
*/


class ImportPlainText_2_SourceDocument extends Import {
	
	Configuration conf = Configuration.newInstance()
	static Logger log = Logger.getLogger("SaskiaImports")
	UnformattedReader reader
	RembrandtWriter writer
	
	public ImportPlainText_2_SourceDocument() {
		super()
		reader = new UnformattedReader(new UnformattedStyleTag(
			conf.get("rembrandt.input.styletag.lang", "pt")))
		writer = new RembrandtWriter(new RembrandtStyleTag(
			conf.get("rembrandt.output.styletag.lang", "pt")))
	}

	public HashMap importDocs() {
		
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
		return status
	}
 	
	static void main(args) {
		
		Options o = new Options()
		Configuration conf = Configuration.newInstance()
		
		o.addOption("file", false, "collection file to load")
		o.addOption("encoding", false, "encoding")
		o.addOption("doc_id", false, "document id")
		o.addOption("lang", false, "language")
		o.addOption("col", false, "collection number/name")
		o.addOption("help", false, "Gives this help information")
	    
		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)
		
		ImportPlainText_2_SourceDocument importer = new ImportPlainText_2_SourceDocument()
		String DEFAULT_COLLECTION_NAME = 'DefaultCollection'
		
		// --help
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java "+importer.class.name, o )
			System.exit(0)
		}
		
		// --col
		Collection collection = importer.validateCollection(cmd.getOptionValue("col"), DEFAULT_COLLECTION_NAME)
		if (!collection) {
			log.fatal "Collection couldn't be found."
			log.fatal "Please make sure you have that collection in the DB before the import."
			System.exit(0)
		}
		
		// --file
		File file = importer.validateFile(cmd.getOptionValue("file"))
		if (!file) {
			log.fatal "No import file found. Please check if the given file exists"
			System.exit(0)
		}
		
		//--encoding
		String encoding = importer.validateEncoding(cmd.getOptionValue("encoding"))
		if (!encoding) {
			log.fatal "No encoding defined. Please specify the encoding of the import file."
			System.exit(0)
		}
		
		importer.setCollection(collection)
		log.info "Collection: $collection"		
		importer.setFile(file)
		importer.setEncoding(encoding)
		log.info "File: $file <"+encoding+"> "	
		
		importer.prepareInputStreamReader()
		HashMap status = importer.importDocs()
		
		log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
	}
}
