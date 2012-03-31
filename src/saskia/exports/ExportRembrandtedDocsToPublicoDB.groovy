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
import rembrandt.io.*
import org.apache.log4j.*
import org.apache.commons.cli.*
import saskia.bin.Configuration
import saskia.db.DocStatus
import saskia.db.obj.*
import saskia.db.table.*
import saskia.db.database.*
import saskia.util.validator.*
import groovy.sql.Sql

/** 
 * This class exports Rembrandted Docs into PublicoDB
*/

class ExportRembrandtedDocsToPublicoDB extends Export {
 
	int doc_pool = 100
	String docid
	def target_db
	def target_table
	def total
	int processed
	DocStats docstats
	def writer
    def reader
	
	public ExportRembrandtedDocsToPublicoDB() {
		super()
		/*writer = new RembrandtWriter(new RembrandtStyleTag(
				conf.get("rembrandt.output.styletag.lang", "pt")))
		*/		
		// David's style		
		writer = new UnformattedWriter(new JustCategoryStyleTag("pt"))
		reader = new RembrandtReader(new RembrandtStyleTag(
				conf.get("rembrandt.output.styletag.lang", "pt")))
		
		this.processed = 0
		docstats = new DocStats()
	}
	
	public setTargetDB(target_db) {
		this.target_db = target_db
	}
	
	public setTargetTable(target_table) {
		this.target_table = target_table
	}
	
	public checkTotal() {
		
		// the comment is the document type, and in PublicoDB, there is a table for each type
		this.total = collection.getNumberOfRembrandtedDocsWithComment(this.target_table)
		log.debug("Total rembrandted docs: "+this.total)
	}
	
	public exporter() {
			
		docstats.begin()
		docstats.totalDocs = this.total
		
		List rdocs

		for (int i=this.total; i > 0; i -= doc_pool) {
 
			int limit = (i > this.doc_pool ? this.doc_pool : i)
			log.debug "Initial batch size: ${this.total} Remaining: $i Next pool size: $limit"
		
			docstats.beginBatchOfDocs(limit)
		
			rdocs = db.getDBTable("RembrandtedDocTable").getBatchOfRembrandtedDocsWithComment(
				collection, this.target_table, this.processed, limit)
			
			log.info "rdocs: ${rdocs.size()}"

			if (!rdocs) {
            	log.info "DB returned no more docs, I guess I'm done."	
             	return			
			}
			
			rdocs.each{rdoc -> 
				
				def url = rdoc.doc_id
				Document doc = reader.readDocument(rdoc.doc_content.trim())
				String title = writer.printDocumentHeadContent(doc)
				String body = writer.printDocumentBodyContent(doc)
				
				println "url:${url}\ntitle:${title}\nbody:${body}\n\n";
				status.exported++	
			// extrair primeiro título e subtítulo. 
			
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
		o.addOption("targetdb", true, "target db name to load")
		o.addOption("targettable", true, "target db table to load")
		o.addOption("targetuser", true, "target db user to load")
		o.addOption("targetpass", true, "target db password to load")
		o.addOption("help", false, "Gives this help information")

		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)
		
		ExportRembrandtedDocsToPublicoDB exporter = new ExportRembrandtedDocsToPublicoDB()
		Integer DEFAULT_DOCS = 1000
		String DEFAULT_DB_NAME = "main"
		String DEFAULT_COLLECTION_NAME = "Publico 10" 
		String DEFAULT_ENCODING = conf.get("rembrandt.output.encoding",System.getProperty("file.encoding"))
		

		log.info "******************************************************"
		log.info "* This class exports Rembrandted docs into PublicoDB *"
		log.info "******************************************************"

		// --help
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java "+exporter.class.name, o )
			System.exit(0)
		}
		// --db
		SaskiaDB db = new DBValidator()
			.validate(cmd.getOptionValue("db"), DEFAULT_DB_NAME)
		exporter.setDb(db)
		log.info "DB: $db"

		// --col
		Collection collection = new CollectionValidator(db)
			.validate(cmd.getOptionValue("col"), DEFAULT_COLLECTION_NAME)
		exporter.setCollection(collection)
		log.info "Collection: $collection"

		// target_db
		String targetdb = cmd.getOptionValue("targetdb")
		
		//sourcetable
		String targettable = cmd.getOptionValue("targettable")
		
		// targetuser
		String user = cmd.getOptionValue("targetuser")
		
		//targetpass
		String password = cmd.getOptionValue("targetpass")
		
		String driver = 	'com.mysql.jdbc.Driver'
		String url = 	'jdbc:mysql://127.0.0.1'
		String param = 	'useUnicode=yes&characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true'
		
		def _db
		
		try {
			_db = Sql.newInstance("$url/${targetdb}?${param}", user, password, driver)
		}  catch (Exception e) {
			log.fatal "Can't open db "+"$url/${targetdb}?${param}"+": "+e.getMessage()
			log.fatal "Is your MySQL server running? You should check it out."
		}
		if (_db) {
			log.info "Source database initialized: $driver:$url/$targetdb"
		} else {
			log.fatal "Source database NOT initialized. Exiting."
			System.exit(0)
		}

		exporter.setTargetDB(_db)
		exporter.setTargetTable(targettable)
		exporter.checkTotal()
		exporter.exporter()

		println exporter.statusMessage()
	}
}