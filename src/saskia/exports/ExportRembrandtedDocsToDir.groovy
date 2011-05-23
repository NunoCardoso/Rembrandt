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
 
package saskia.exports

import rembrandt.io.DocStats
import rembrandt.obj.Document
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import org.apache.log4j.*
import org.apache.commons.cli.*
import saskia.bin.Configuration
import saskia.db.DocStatus
import saskia.db.obj.*
import saskia.db.table.*
import saskia.db.database.*
import saskia.util.validator.*
/** 
 * This class exports tagged documents from a collection into files on a directory
*/

class ExportRembrandtedDocsToDir extends Export {
 
	public ExportRembrandtedDocsToDir() {
		super()
	}
	
	public exporter() {
			
		int rows = 0 
		int total = collection.getNumberOfRembrandtedDocuments()
		
		DocStats docstats = new DocStats()
      docstats.begin()
		docstats.totalDocs = total
      int processed = 0
		int file_number = 0
		
		File file
      for (int i=total; i > 0; i -= docs) {
            
			file_number++
			
			OutputStreamWriter out = new OutputStreamWriter(
				new FileOutputStream(
					new File(directory.getCanonicalPath()+fileseparator+
				"col-"+collection.col_id+"-"+
				file_number.toString().padLeft(5,"0")+".txt")),
				this.encoding)
			
			
			out.write("")
				
         int limit = (i > docs ? docs : i)
         log.debug "Initial batch size: ${total} Remaining: $i Next pool size: $limit"
         docstats.beginBatchOfDocs(limit)

         List rdocs = db.getDBTable("RembrandtedDocTable")
				.getBatchOfRembrandtedDocsOrderedByOriginalDocId(
					collection, docs, processed)
         log.debug "Got ${rdocs?.size()} RembrandtedDoc(s)."
         // if it's null, then there's no more docs to process. Leave the loop.
         if (!rdocs) {
             log.info "DB returned no more docs, I guess I'm done."	
             return 
         } 
  
 		   rdocs.each {rdoc ->
		    	out.write("<META DOC_ORIGINAL_ID=\""+rdoc.doc_original_id+"\">\n")
				out.write(rdoc.doc_content.trim()+"\n")
				status.exported++	
		   }
         docstats.endBatchOfDocs(limit)	
         processed += limit	
 			docstats.printMemUsage()
			out.close()
		}
		docstats.end()
	}

	static void main(args) {

		Options o = new Options()
		Configuration conf = Configuration.newInstance()

		o.addOption("db", true, "target Saskia DB (main/test)")
		o.addOption("col", true, "target collection name/id of the DB")
		o.addOption("dir", true, "directory to write files")
		o.addOption("docs", true, "number of docs for each zip file")
		o.addOption("encoding", true, "file encoding")
		o.addOption("help", false, "Gives this help information")

		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)
		
		Integer DEFAULT_DOCS = 1000
		String DEFAULT_DB_NAME = "main"
		String DEFAULT_COLLECTION_NAME = "CD do Segundo HAREM" 
		String DEFAULT_ENCODING = conf.get("rembrandt.output.encoding",System.getProperty("file.encoding"))
		
		ExportRembrandtedDocsToDir exporter = new ExportRembrandtedDocsToDir()

		log.info "*********************************************************"
		log.info "* This class exports a tagged collection into zip files *"
		log.info "* in a given directory.                                 *"
		log.info "*********************************************************"

		// --help
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java "+exporter.class.name, o )
			System.exit(0)
		}
		// --db
		SaskiaDB db = new DBValidator().validate(cmd.getOptionValue("db"), DEFAULT_DB_NAME)
		exporter.setDb(db)
		log.info "DB: $db"

		// --col
		Collection collection = new CollectionValidator().validate(cmd.getOptionValue("col"), DEFAULT_COLLECTION_NAME)
		exporter.setCollection(collection)
		log.info "Collection: $collection"

		String default_dir = conf.get("rembrandt.home.dir")
      if (!default_dir.endsWith(fileseparator)) default_dir += fileseparator
      default_dir += "export"

		// --dir
		File dir = DirectoryValidator.validate(cmd.getOptionValue("dir"), default_dir)
		exporter.setDirectory(dir)

		//--encoding
		String encoding = EncodingValidator.validate(cmd.getOptionValue("encoding"), DEFAULT_ENCODING)
		exporter.setEncoding(encoding)
		log.info "Directory: $dir <"+encoding+"> "

		// --docs		
		Integer docs = exporter.validateDocs(cmd.getOptionValue("docs"), DEFAULT_DOCS)
		if (!docs) {
			log.fatal "Docs not given."
			log.fatal "Please specify if the import mode is a single document or multiple."
			System.exit(0)
		}
		exporter.setDocs(docs)

///////////
//		exporter.prepareOutputStreamReader()
		exporter.exporter()
		println exporter.statusMessage()
	}
}