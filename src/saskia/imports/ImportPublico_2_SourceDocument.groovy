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
import groovy.sql.*

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import rembrandt.obj.Document

import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag
import rembrandt.io.UnformattedStyleTag
import saskia.util.validator.*
import rembrandt.io.DocStats

class ImportPublico_2_SourceDocument extends Import {
	
	int doc_pool = 100
	String docid
	def db
	def table
	def total
	int processed
	DocStats docstats
	
	public ImportPublico_2_SourceDocument() {
		super()
		writer = new RembrandtWriter(new RembrandtStyleTag(
				conf.get("rembrandt.output.styletag.lang", "pt")))
		this.processed = 0
		docstats = new DocStats()
	}

	public setSourceDB(db) {
		this.db = db
	}
	
	public setSourceTable(table) {
		this.table = table
	}
	
	public checkTotal() {
		this.db.eachRow("SELECT COUNT(*) FROM "+this.table, [], {row -> this.total = row[0]})
		log.debug("Total source docs: "+this.total)
	}
	
	private readDocuments(offset, limit) {
	
		List docs =[] 

		this.db.eachRow("SELECT * FROM ${this.table} ORDER BY id asc LIMIT ${limit} OFFSET ${offset}", [] , {row ->
			Document doc = new Document()
			doc.docid = row["origLink"]
			doc.body = row["newstext"] 
			doc.title = row["title"]+".\n"+row["subtitle"]
			doc.lang="pt"
			doc.date_created = row["date"]
			doc.tokenizeTitle()
			doc.tokenizeBody()
				
			docs << doc
		}) 
		return docs
	}
	
	public importer() {

		//log.info "Starting the import... "

		docstats.begin()
		docstats.totalDocs = this.total
		
		List docs		
		for (int i=this.total; i > 0; i -= this.doc_pool) {

			int limit = (i > this.doc_pool ? this.doc_pool : i)
			log.debug "Initial batch size: ${this.total} Remaining: $i Next pool size: $limit"
			
			docstats.beginBatchOfDocs(limit)
			
			docs = this.readDocuments(this.processed, limit)
			//log.info "docs: ${docs.size()}"

			docs?.each{doc ->
				if (!doc.lang) doc.lang = collection.col_lang
				String content = writer.printDocument(doc)
				
			//	print "doc: ${content}"
			// adding the table as a comment
				SourceDoc s = addSourceDoc(doc.docid, content, doc.lang, doc.date_created, ""+this.table)
				if (s) status.imported++ else status.skipped++
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
		o.addOption("sourcedb", true, "source db name to load")
		o.addOption("sourcetable", true, "source db table to load")
		o.addOption("sourceuser", true, "source db user to load")
		o.addOption("sourcepass", true, "source db password to load")
		o.addOption("help", false, "Gives this help information")

		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

		ImportPublico_2_SourceDocument importer = new ImportPublico_2_SourceDocument()
		String DEFAULT_COLLECTION_NAME = "Publico 10"
		String DEFAULT_DB_NAME = "main"
		String DEFAULT_ENCODING = conf.get("rembrandt.input.encoding",
				System.getProperty("file.encoding"))

		log.info "**********************************"
		log.info "* This class loads Publico files *"
		log.info "****************** ***************"

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

		// sourcedb
		String sourcedb = cmd.getOptionValue("sourcedb")
		
		//sourcetable
		String sourcetable = cmd.getOptionValue("sourcetable")
		
		// sourceuser
		String user = cmd.getOptionValue("sourceuser")
		
		//sourcepass
		String password = cmd.getOptionValue("sourcepass")
		
		String driver = 	'com.mysql.jdbc.Driver'
		String url = 	'jdbc:mysql://127.0.0.1'
		String param = 	'useUnicode=yes&characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true'
		
		def _db
		
		try {
			_db = Sql.newInstance("$url/${sourcedb}?${param}", user, password, driver)
		}  catch (Exception e) {
			log.fatal "Can't open db "+"$url/${sourcedb}?${param}"+": "+e.getMessage()
			log.fatal "Is your MySQL server running? You should check it out."
		}
		if (_db) {
			log.info "Source database initialized: $driver:$url/$sourcedb"
		} else {
			log.fatal "Source database NOT initialized. Exiting."
			System.exit(0)
		}
		
		importer.setSourceDB(_db)
		importer.setSourceTable(sourcetable)
		importer.checkTotal()
		importer.importer()
	
		println importer.statusMessage()
	}
}
