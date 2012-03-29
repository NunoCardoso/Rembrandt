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

import saskia.db.DocStatus;
import saskia.db.database.WikipediaDB;
import saskia.db.obj.*;
import saskia.db.table.*;
import saskia.dbpedia.DBpediaAPI
import saskia.dbpedia.DBpediaOntology
import saskia.wikipedia.WikipediaDocument
import saskia.bin.Configuration
import org.apache.log4j.*
import org.apache.commons.cli.*

/** 
 * This class imports SELECTED Wikipedia raw documents from the Saskia database to 
 * HTML documents in the Saskia's page table. 
 * 
 * Procedure: 
 *  - Reads batchSize docs that are not in the Doc already
 *  - For each Wikipedia doc, checks if it has a DBpedia class.
 *  - If not, check if title has a matching NE on the Saskia. 
 *  - If true, it's imported to Doc with HTML text, doc_proc READY. Comment has the DBpedia / NE association
 *  - If false, it's imported to Doc with HTML text, doc_proc NOT READY (lock is for process purposes)
 */

class ImportSelectedWikipediaDocument {

	WikipediaDB wikipedia_db
	Configuration conf = Configuration.newInstance()
	static Logger log = Logger.getLogger("Saskia")
	String wikipediaDB

	Collection collection
	DBpediaAPI dbpedia
	String lang
	static final int WIKI_DOC_POOL_SIZE=50
	DBpediaOntology dbpediaontology

	public ImportSelectedWikipediaDocument(String lang) {

		this.lang=lang
		if (conf.getBoolean("saskia.wikipedia.enabled",true)) {
			wikipedia_db = WikipediaDB.newInstance()
		} else {
			log.fatal("Wikipedia mode is off, as requested in saskia.wikipedia.enabled.")
			log.fatal("How am I supposed to import Wikipedia documents, if it's forbidden to access WikipediaDB?! Exiting...")
			System.exit(0)
		}


		dbpedia = DBpediaAPI.newInstance()
		dbpediaontology = DBpediaOntology.getInstance()

		wikipediaDB = conf.get("dbpedia.wikipedia.db.name","saskia")
		log.info "Source Wikipedia DB set to $wikipediaDB"
	}

	public HashMap importBatchOfDocs(int batchSize = 100, collection_, String lang) {

		HashMap status = [imported_ready:0, imported_notready:0, skipped:0]
		try {
			collection = Collection.getFromID(Long.parseLong(collection_))
		} catch(Exception e) {
			collection = Collection.getFromName(collection_)
		}
		if (!collection) {
			log.error "Don't know collection $collection_ to parse documents on. Exiting."
			return status
		}
		boolean found = false

		/*   String query = "Select ${saskiaDB}.${lang}_page.page_id, ${saskiaDB}.${lang}_page.page_title, "+
		 "${saskiaDB}.${lang}_page.page_touched, ${saskiaDB}.${lang}_text.old_text FROM "+
		 "${saskiaDB}.${lang}_text, ${saskiaDB}.${lang}_page WHERE ${saskiaDB}.${lang}_page.page_latest"+
		 "=${saskiaDB}.${lang}_text.old_id AND ${saskiaDB}.${lang}_page.page_is_redirect=0 AND "+
		 "${saskiaDB}.${lang}_page.page_namespace=0 AND "+
		 "${saskiaDB}.${lang}_page.page_title NOT IN "+
		 "(SELECT doc_id FROM ${saskiaDB}.${Doc.tablename} "+
		 "WHERE doc_collection =?) LIMIT ${batchSize}"
		 */

		/* This query does the same - outer joins rather than subqueries  */
		//http://www.databasejournal.com/features/mysql/article.php/3434641/MySQL-Subqueries.htm
		/*		   SELECT saskia.en_page.page_id, saskia.en_page.page_title FROM saskia.en_text, saskia.en_page 
		 LEFT JOIN saskia.doc ON saskia.en_page.page_title=saskia.doc.doc_id
		 WHERE saskia.en_page.page_latest=saskia.en_text.old_id AND saskia.en_page.page_is_redirect=0 AND 
		 saskia.en_page.page_namespace=0 AND doc_collection=3 AND saskia.en_page.page_title IS NULL LIMIT 100;
		 */
		/* String query = "Select ${saskiaDB}.${lang}_page.page_id, ${saskiaDB}.${lang}_page.page_title, "+
		 "${saskiaDB}.${lang}_page.page_touched, ${saskiaDB}.${lang}_text.old_text "+
		 " LEFT JOIN  ${saskiaDB}.${Doc.tablename} ON "+
		 " ${saskiaDB}.${lang}_page.page_title = ${saskiaDB}.${Doc.tablename}.doc_id "+
		 "FROM ${saskiaDB}.${lang}_text, ${saskiaDB}.${lang}_page WHERE ${saskiaDB}.${lang}_page.page_latest"+
		 "=${saskiaDB}.${lang}_text.old_id AND ${saskiaDB}.${lang}_page.page_is_redirect=0 AND "+
		 "${saskiaDB}.${lang}_page.page_namespace=0 AND "+
		 "${saskiaDB}.${lang}_page.page_title IS NULL LIMIT ${batchSize}"
		 */
		/* this also...
		 SELECT saskia.en_page.page_id, saskia.en_page.page_title FROM saskia.en_text, saskia.en_page WHERE 
		 saskia.en_page.page_latest=saskia.en_text.old_id AND saskia.en_page.page_is_redirect=0 AND 
		 saskia.en_page.page_namespace=0 AND NOT EXISTS (SELECT doc_id FROM saskia.doc 
		 WHERE doc_collection=3 and saskia.en_page.page_title=saskia.doc.doc_id) LIMIT 100;
		 */

		for(int i=batchSize; i > 0; i -= WIKI_DOC_POOL_SIZE) {

			int limit = (i > WIKI_DOC_POOL_SIZE ? WIKI_DOC_POOL_SIZE : i)
			log.debug "Initial batchSize: $batchSize Remaining: $i Next pool size: $limit"

			String query = "Select ${wikipediaDB}.${lang}_page.page_id, ${wikipediaDB}.${lang}_page.page_title, "+
					"${wikipediaDB}.${lang}_page.page_touched, ${wikipediaDB}.${lang}_text.old_text FROM "+
					"${wikipediaDB}.${lang}_text, ${wikipediaDB}.${lang}_page WHERE ${wikipediaDB}.${lang}_page.page_latest"+
					"=${wikipediaDB}.${lang}_text.old_id AND ${wikipediaDB}.${lang}_page.page_is_redirect=0 AND "+
					"${wikipediaDB}.${lang}_page.page_namespace=0 AND ${wikipediaDB}.${lang}_page.page_doc IS FALSE AND "+
					"${wikipediaDB}.${lang}_page.page_edit='UL' LIMIT ${limit} FOR UPDATE" // very important, to allow concurrent

			List<WikipediaDocument> wdocs = []
			wikipedia_db.getDB().withTransaction{
				wikipedia_db.getDB().eachRow(query, [], {row ->
					if (!found) found = true
					wdocs << getWikipediaDocument(row)
					wikipedia_db.getDB().executeUpdate("UPDATE ${wikipediaDB}.${lang}_page SET page_edit=? WHERE page_id=? ",
							[
								DocStatus.QUEUED.text(),
								row['page_id']
							])
				})
			}

			wdocs.each{wdoc ->
				HashMap status_ = importDoc(wdoc, collection)
				status.imported_ready += status_.imported_ready
				status.imported_notready += status_.imported_notready
				status.skipped += status_.skipped
				println "Releasing doc ${wdoc.id}"
				wikipedia_db.getDB().executeUpdate("UPDATE ${wikipediaDB}.${lang}_page SET page_doc=TRUE, page_edit='UL' "+
						" WHERE page_id=?",[wdoc.id])
			}
		}

		if (!found) log.warn "No Wikipedia documents found. Did NOT made a import."
		return status
	}

	// NOT THREAD_SAFE!!
	public HashMap importDocFromWikipedia(long doc_id, collection_, String lang) {

		HashMap status = [imported_ready:0, imported_notready:0, skipped:0]
		try {
			collection = Collection.getFromID(Long.parseLong(collection_))
		} catch(Exception e) {
			collection = Collection.getFromName(collection_)
		}
		if (!collection) {
			log.error "Don't know collection $collection_ to parse documents on. Exiting."
			return status
		}
		log.trace "Requesting import of a Wikipedia document, lang ${lang}, id ${doc_id}."
		boolean found = false

		String query = "Select ${wikipediaDB}.${lang}_page.page_id, ${wikipediaDB}.${lang}_page.page_title, "+
				"${wikipediaDB}.${lang}_page.page_touched, ${wikipediaDB}.${lang}_text.old_text FROM "+
				"${wikipediaDB}.${lang}_text, ${wikipediaDB}.${lang}_page where ${wikipediaDB}.${lang}_page.page_latest="+
				"${wikipediaDB}.${lang}_text.old_id and ${wikipediaDB}.${lang}_page.page_id =?"

		wikipedia_db.getDB().eachRow(query, [doc_id], {row ->
			found = true
			HashMap status_ = importDoc(getWikipediaDocument(row), collection)
			status.imported_ready += status_.imported_ready
			status.imported_notready += status_.imported_notready
			status.skipped += status_.skipped
		})
		if (!found)
			log.warn "No Wikipedia document found. Did NOT made a import."

		return status
	}

	public HashMap importDoc(WikipediaDocument wdoc, Collection collection) {

		String comment = ""
		// title
		log.debug "Got title: ${wdoc.theTitle}, lang ${wdoc.lang}"
		String dbpediaresource = dbpedia.getDBpediaResourceFromWikipediaURL(wdoc.rawTitle, wdoc.lang)

		/** add it, but label as NOT RELEVANT */
		if (!dbpediaresource) {
			log.debug "Title ${wdoc.rawTitle} did NOT had a DBpedia resource. "
			comment = "DBpedia resource = null"
			addDoc(wdoc, DocStatus.WIKIPEDIA_DOC_NOT_RELEVANT, comment, collection)
			return [imported_ready:0, imported_notready:1, skipped:0]
		}

		List dbpediaclasses = dbpedia.getDBpediaOntologyClassFromDBpediaResource(dbpediaresource)

		/** add it, but label as READY */
		if (dbpediaclasses) {
			log.debug "DBpedia resource ${dbpediaresource} HAS DBpedia ontology classes: $dbpediaclasses"
			String theclass = dbpediaontology.getNarrowerClassFrom(dbpediaclasses)
			log.debug "Narrower class: $theclass"
			comment = "DBpedia resource = ${dbpediaresource}, DBpedia class = ${theclass}"
			addDoc(wdoc, DocStatus.READY, comment, collection)
			return [imported_ready:1, imported_notready:0, skipped:0]

			/** add it, but label as READY or NOT RELEVANT, depending...*/
		} else {
			log.debug "DBpedia resource ${dbpediaresource} does NOT have classes, going for NE name"
			def ne = NE.getFromNameAndLang(wdoc.theTitle, wdoc.lang)
			if (ne) {
				log.debug "Title ${wdoc.theTitle} HAS a NE ${NE}"
				comment = "DBpedia resource = ${dbpediaresource}, DBpedia class = null, NE id = ${ne.ne_id}"
				addDoc(wdoc, DocStatus.READY, comment, collection)
				return [imported_ready:1, imported_notready:0, skipped:0]

			} else {
				log.debug "Title ${wdoc.theTitle} does NOT have a NE ${NE}"
				comment = "DBpedia resource = ${dbpediaresource}, DBpedia class = null, NE id = null"
				addDoc(wdoc, DocStatus.WIKIPEDIA_DOC_NOT_RELEVANT, comment, collection)
				return [imported_ready:0, imported_notready:1, skipped:0]
			}
		}


	}

	public WikipediaDocument getWikipediaDocument(groovy.sql.GroovyResultSet row) {

		java.sql.Blob blob = row.getBlob('old_text')
		byte[] bdata = blob.getBytes(1, (int) blob.length())
		// you have to say explicitly that mediawiki's mediumblob is in UTF-8
		// don't allow configuration: wikipedia DB is always in UTF-8!
		String text = new String(bdata, "UTF-8")

		String datetouched = row['page_touched'] // ex, 20080622185108
		Date date
		def m = datetouched =~ /^(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})$/
		if (m.matches()) {
			date = new GregorianCalendar(Integer.parseInt(m.group(1)), (Integer.parseInt(m.group(2))+1),
					Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)),
					Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6))).getTime()
		} else {
			date = new Date(0)
		}
		WikipediaDocument w = new WikipediaDocument(row['page_id'], row['page_title'], lang)
		w.date = date
		w.text = text
		return w
	}

	void addDoc(WikipediaDocument wdoc, proc_stat, comment, collection) {


		String parsedText = null
		// TODO
		// String parsedText = w2h.parse(wdoc.text, wdoc.rawTitle, wdoc.lang)

		Doc s = new Doc(doc_original_id:wdoc.rawTitle,
				doc_collection:collection.col_id,
				doc_lang:wdoc.lang,
				doc_content:parsedText,
				doc_date:wdoc.date,
				doc_proc:proc_stat,
				doc_comment:comment
			)
		try {
			s.addThisToDB()
			log.debug "Inserted $s into Saskia DB."
		} catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			// VARCHAR primary keys are case insensitive. Sometimes there's redirects that are not redirects,
			// and so there's attempts to insert documents with a id with case changes.
			// let's just catch it and continue
			log.warn "Found duplicate entry in DB. Skipping."
		}

	}

	static void main(args) {

		Options o = new Options()
		o.addOption("from", true, "[DB] - one particular document from DB, [batch] - a number of unseen documents")
		o.addOption("lang", true, "language of documents / collections")
		o.addOption("col", true, "target collection. Can be id or name")
		o.addOption("ndocs", true, "number of docs in batch process")
		o.addOption("docid", true, "id of docs in DB process")
		o.addOption("help", false, "Gives this help information")

		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "java saskia.imports.ImportSelectedWikipediaDocument", o )
			System.exit(0)
		}

		// check important vars
		if (!cmd.hasOption("from")) {
			println "No --from arg. Please specify the type of process (DB or batch). Exiting."
			System.exit(0)
		}

		if (!cmd.hasOption("lang")) {
			println "No --lang arg. Please specify the language (2 lowercase letters). Exiting."
			System.exit(0)
		}

		if (!cmd.hasOption("col")) {
			println "No --col arg. Please specify the target collection (id or name). Exiting."
			System.exit(0)
		}

		ImportSelectedWikipediaDocument w2s = new ImportSelectedWikipediaDocument(
				cmd.getOptionValue("lang"))

		HashMap status = [imported:0, skipped:0]

		if (cmd.getOptionValue("from")== "DB") {
			List docs = cmd.getOptionValues("docid")
			if (!docs) {
				println "Set to DB, but no doc_id found. Please set --docid args. Exiting."
				System.exit(0)
			}
			docs.each{doc ->
				HashMap status_ = w2s.importDocFromWikipedia(Long.parseLong(doc),
						cmd.getOptionValue("col"), cmd.getOptionValue("lang"))
				if (status_) {
					status.imported_ready += status_.imported_ready
					status.imported_notready += status_.imported_notready
					status.skipped += status_.skipped
				}
			}
		} else if (cmd.getOptionValue("from")== "batch") {
			status = w2s.importBatchOfDocs( Integer.parseInt(
					cmd.getOptionValue("ndocs")), cmd.getOptionValue("col"), cmd.getOptionValue("lang"))
		} else {
			println "Unknown value for --from. Exiting."
			System.exit(0)
		}
		log.info "Done. ${status.imported_ready} doc(s) imported as READY, ${status.imported_notready} doc(s) imported as NOT READY, skipped ${status.skipped} doc(s)."
	}
}