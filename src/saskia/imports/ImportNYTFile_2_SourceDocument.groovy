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

import org.apache.commons.cli.*
import saskia.bin.Configuration
import saskia.db.obj.SourceDoc;

import org.apache.log4j.Logger
import java.util.regex.*
import rembrandt.io.RembrandtWriter
import rembrandt.io.NYTimesReader
import rembrandt.io.UnformattedStyleTag
import rembrandt.io.RembrandtStyleTag

/** 
 * This class imports NYT files to the Source Documents
 */
class ImportNYTFile_2_SourceDocument {
	
		
	Configuration conf = Configuration.newInstance()
	static Logger log = Logger.getLogger("SaskiaImports")
	NYTimesReader reader
	RembrandtWriter writer
	
	public ImportNYTFile_2_SourceDocument() {
		super()
		reader = new NYTimesReader(new UnformattedStyleTag(
			conf.get("rembrandt.input.styletag.lang", "en")))
		writer = new RembrandtWriter(new RembrandtStyleTag(
			conf.get("rembrandt.output.styletag.lang", "pt")))
	}
		
	public HashMap importDocs() {
		reader.processInputStream(this.inputStreamReader)

		reader.docs.each{doc ->
			if (!doc.lang) doc.lang = collection.col_lang
			String content = writer.printDocument(doc)
			SourceDoc s = addSourceDoc(doc.docid, content, doc.lang, new Date(0), "")
			if (s) status.imported++ else status.skipped++
		}
		return status
	}
	
	static void main(args) {
		
		Options o = new Options()
		Configuration conf = Configuration.newInstance()
		
		o.addOption("col", true, "target collection name/id of the DB")
		o.addOption("file", true, "source collection file to load")
		o.addOption("encoding", true, "file encoding")
		o.addOption("help", false, "Gives this help information")
		
		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)
		
		ImportNYTFile_2_SourceDocument importer = new ImportNYTFile_2_SourceDocument()
		String DEFAULT_COLLECTION_NAME = 'New York Times 2002-2005'
		
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

