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
import saskia.db.database.*
import saskia.db.table.*
import groovy.sql.*

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import rembrandt.obj.Document

import rembrandt.io.*
import saskia.util.validator.*

class UpdateDocFormat extends Import {
	
	int doc_pool = 100
	String docid
	def total
	int processed
	DocStats docstats
	HTMLReader readerHTML
	RembrandtReader readerRembrandt
	RembrandtStyleTag styletag
	DocTable docTable 
	Long from = 0
	
	public UpdateDocFormat() {
		super()
		writer = new RembrandtWriter(new RembrandtStyleTag(
				conf.get("rembrandt.output.styletag.lang", "pt")))
		readerHTML = new HTMLReader(new RembrandtStyleTag(
						conf.get("rembrandt.output.styletag.lang", "pt")))
		this.processed = 0
		docstats = new DocStats()
	
	}
	
	public checkTotal() {
		db.getDB().eachRow("SELECT COUNT(*) FROM doc WHERE doc_collection = ?", [collection.col_id], {row -> this.total = row[0]})
		log.debug("Total source docs: "+this.total)
	}

	public setFrom(from) {
		this.from = from
	}

	public importer() {

		docTable = db.getDBTable("DocTable")
		//log.info "Starting the import... "

		docstats.begin()
		docstats.totalDocs = this.total
		
		List docs
		
		for (int i=this.total; i > 0; i -= this.doc_pool) {

			int limit = (i > this.doc_pool ? this.doc_pool : i)
			log.debug "Initial batch size: ${this.total} Remaining: $i Next pool size: $limit"
			
			docstats.beginBatchOfDocs(limit)
			
			docs = []

			
			db.getDB().eachRow("SELECT * FROM doc where doc_collection = ? and doc_id > ? ORDER BY "+
			"doc_id asc LIMIT ${limit} OFFSET ${this.processed}", [collection.col_id, from] , {row ->
				println "Doc: "+row["doc_id"]+":"+row["doc_original_id"]+":"+row["doc_webstore"]
				String content = docTable.getWebstore().retrieve(row["doc_webstore"])
				println "Content: "+content.size()+" bytes."
				
				if (content.startsWith("<HTML")) {
					Document doc = readerHTML.createDocument(content)
				
					doc.docid = row["doc_original_id"]
					doc.lang=row["doc_lang"]
					doc.taglang=row["doc_lang"]
					doc.date_created = row["doc_date_created"]
					docs << doc
				} else {
					println "Skipping doc: "+row["doc_id"]+". It is already done."
				}
			})
	

			docs?.each{doc ->
				String content = writer.printDocument(doc)
			//	print("\n"+doc.date_created+"\n"+content+"\n\n")
			//	print "doc: ${content}"
			// adding the table as a comment
				Doc d = addDoc(doc.docid, content, doc.lang, doc.date_created, "")
				if (d) status.imported++ else status.skipped++
			}

			docstats.endBatchOfDocs(limit)
			this.processed += limit
			docstats.printMemUsage()
		}
		docstats.end()
	}

	static void main(args) {

		Options o = new Options()
		Configuration conf = Configuration.newInstance()

		o.addOption("db", true, "target Saskia DB (main/test)")
		o.addOption("col", true, "target collection name/id of the DB")
		o.addOption("help", false, "Gives this help information")
		o.addOption("from", true, "To resume. Give 0 for all")

		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

		UpdateDocFormat importer = new UpdateDocFormat()
		String DEFAULT_COLLECTION_NAME = ""
		String DEFAULT_DB_NAME = "main"
		String DEFAULT_ENCODING = conf.get("rembrandt.input.encoding",
				System.getProperty("file.encoding"))
		
		log.info "********************************"
		log.info "* This class updates doc files *"
		log.info "********************************"

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
		
		// --from
		long from = 0
		if (cmd.hasOption("from")) {
			from = Long.parseLong(cmd.getOptionValue("from"))
		}
	
		importer.setFrom(from)
		importer.checkTotal()
		importer.importer()
	
		println importer.statusMessage()
	}
}
