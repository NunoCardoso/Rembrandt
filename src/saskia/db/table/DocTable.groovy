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

package saskia.db.table

import java.util.Map

import org.apache.log4j.*

import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import rembrandt.obj.Document
import rembrandt.obj.ListOfNE
import saskia.bin.Configuration
import saskia.db.DocStatus
import saskia.db.SaskiaWebstore
import saskia.db.database.SaskiaDB
import saskia.db.obj.*
import saskia.dbpedia.DBpediaAPI
import saskia.dbpedia.DBpediaResource
import java.sql.SQLException


/** This class is an interface for the Doc table in the Saskia database. 
 */
class DocTable extends DBTable {

	static String tablename = "doc"
	String dhn_table = "doc_has_ne"
	String dtg_table = "doc_has_tag"

	// Document is already post_processed.
	Configuration conf
	SaskiaWebstore webstore
	RembrandtReader reader

	static Logger log = Logger.getLogger("Doc")

	String lang

	DBpediaAPI dbpedia = DBpediaAPI.newInstance()

	public DocTable(SaskiaDB db) {
		super(db)
		conf = Configuration.newInstance()
		webstore = SaskiaWebstore.newInstance()
		if (!webstore) {
			log.warn "DocTable: webstore not initialized, there may be some errors coming. Check the webstore."
		}
		lang = conf.get("global.lang")
		reader = new RembrandtReader( new RembrandtStyleTag(
			conf.get("rembrandt.input.styletag.lang", conf.get("global.lang"))))

	}

	public List<Doc> queryDB(String query, ArrayList params) {
		List<Doc> l = []
		db.getDB().eachRow(query, params, {row  ->
			l << Doc.createNew(this, row)
		})
		return l
	}

	public Map listDocs(Collection collection, limit = 10,  offset = 0, column = null, needle = null) {

		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		String where = "WHERE doc_collection=? "
		List params = [collection.col_id]
		if (column && needle) {
			switch (Doc.type[column]) {
				case 'String': where += " AND $column LIKE '%${needle}%'"; break
				case 'Long': where += " AND $column=? "; params << Long.parseLong(needle); break
				case 'DocStatus':  where += " AND $column = ?"; params << needle; break
				case 'Date': where += " AND $column = ?"; params << needle; break
			}
		}

		String query = "SELECT SQL_CALC_FOUND_ROWS * FROM ${tablename} "+
				"$where LIMIT ${limit} OFFSET ${offset} UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), "+
				"NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,NULL,NULL,NULL,NULL,NULL"
		log.debug "query = $query params = $params class = "+params*.class
		List u
		try {u = queryDB(query, params) }
		catch(Exception e) {log.error "Error getting doc list: ", e}
		// last "user" is not the user... it's the count.
		Doc fake_doc = u.pop()
		long total = fake_doc.doc_id
		log.debug "Returning "+u.size()+" results."
		return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
			"column":column, "value":needle, "col_id":collection.col_id]
	}

	public List<Doc> getBatchOfDocs(Collection collection, limit = 10,  offset = 0) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		return queryDB("SELECT SQL_CALC_FOUND_ROWS * "+
		" FROM  ${tablename}  WHERE doc_collection=? "+
		"ORDER BY doc_id ASC LIMIT $limit OFFSET $offset", [collection.col_id])
		// ORDER BY doc_id ASC assures that these batches are ordered
	}

	public List<Doc> getBatchOfDocsWithComment(Collection collection, String comment, limit = 10,  offset = 0) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		return queryDB("SELECT SQL_CALC_FOUND_ROWS * "+
		" FROM  ${tablename} WHERE doc_collection=? and doc_comment = ? "+
		"ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id, comment])
		// ORDER BY doc_id ASC assures that these batches are ordered
	}

	public List<Doc> getBatchOfDocsOrderedByOriginalDocId(Collection collection, limit = 10,  offset = 0) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		return queryDB("SELECT SQL_CALC_FOUND_ROWS * "+
		" FROM  ${tablename} WHERE doc_collection=? "+
		"ORDER BY doc_original_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id])
		// ORDER BY doc_original_id ASC assures that these batches are ordered
	}

	public Doc getFromOriginalDocIDandCollection(String doc_original_id, Collection collection) {
		if (!doc_original_id || !collection) return null
		List<Doc> l = queryDB("SELECT * FROM ${tablename} "+
			"WHERE doc_original_id=? AND doc_collection=?",
				[
					doc_original_id,
					collection.col_id,
				])
		log.trace "Querying Saskia for doc_original_id:$doc_original_id, collection $collection, got ${l}"
		return (l ? l[0] : null)
	}

	/** Get a Doc from an id of the Doc table.
	 * @param doc_id The id of the document. 
	 * return the Doc
	 */ 
	public Doc getFromID(long doc_id) {
		if (!doc_id) return null
		List<Doc> l = queryDB("SELECT * FROM ${tablename} WHERE doc_id=?", [doc_id])
		log.info "Querying for doc_id $doc_id, got Doc ${l}"
		return (l ? l[0] : null)
	}

	static Doc getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("DocTable").getFromID(id)
	}

	/** Get a Doc from an id of the Doc table.
	 * @param doc_id The id of the document. 
	 * return the Doc
	 */ 
	public Doc getFromOriginalID(String doc_original_id) {
		if (!doc_original_id) return null
		List<Doc> l = queryDB("SELECT * FROM ${tablename} WHERE doc_original_id=?", [doc_original_id])
		log.info "Querying for doc_original_id $doc_original_id, got Doc ${l}"
		return (l ? l[0] : null)
	}

	static Doc getFromOriginalID(SaskiaDB db, String doc_original_id) {
		return  db.getDBTable("DocTable").getFromOriginalID(doc_original_id)
	}

	/**
	 * Get a batch of Docs from a list of ids
	 */ 
	public List<Doc> getFromOriginalIDs(List<Long> doc_ids, Collection collection) {
		if (!doc_ids || !collection) return null
		String where = "("+doc_ids.join(",")+")"
		List<Doc> l = queryDB("SELECT * FROM ${tablename} WHERE "+
			"doc_collection=? AND doc_original_id IN "+where, [collection.col_id])
		return (l ? l : null)
	}

	/**
	 * This is a call to be used for GeoSignature generation
	 * 
	 select doc_id, dhn_section, dhn_sentence, dhn_term, nen_name, ne_type, ne_subtype, ent_id, ent_dbpedia_class from doc, doc_has_ne, 
	 ne_name, ne RIGHT JOIN entity on ent_id=ne_entity RIGHT JOIN entity_has_geoscope ON ehg_entity=ent_id where doc_id=dhn_doc and 
	 dhn_ne=ne_id and ne_name=nen_id and ne_category=2 and doc_latest_geo_signature IS NULL LIMIT 10;
	 +--------+-------------+--------------+----------+----------------------------+---------+------------+--------+------------------+
	 | doc_id | dhn_section | dhn_sentence | dhn_term | nen_name                   | ne_type | ne_subtype | ent_id |ent_dbpedia_class |
	 +--------+-------------+--------------+----------+----------------------------+---------+------------+--------+------------------+
	 |      1 | B           |            0 |       29 | Estados Unidos da América  |       2 |          2 |        | Country          | 
	 |     28 | B           |           35 |       20 | Estados Unidos da América  |       2 |          2 |        | Country          | 
	 |     37 | B           |           27 |       20 | Estados Unidos da América  |       2 |          2 |        | Country          |  
	 NOT THREAD SAFE!*/
	public Map getBatchDocsAndNEsFromPoolToGenerateGeoSignatures(Collection collection, int limit) {

		Map docs = [:]
		NECategoryTable neCategoryTable = getSaskiaDB().getDBTable("NECategoryTable")
		NETypeTable neTypeTable = getSaskiaDB().getDBTable("NETypeTable")
		NESubtypeTable neSubtypeTable = getSaskiaDB().getDBTable("NESubtypeTable")

		neCategoryTable.createCache()
		neTypeTable.createCache()
		neSubtypeTable.createCache()

		db.getDB().withTransaction{

			log.info "Getting a set of $limit Docs to generate GeoSignatures"
			db.getDB().eachRow("SELECT doc_id, doc_original_id, doc_lang from doc where "+
					"doc_collection=? AND doc_latest_geo_signature IS NULL "+
					"AND doc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND "+
					"doc_id NOT IN (select job_doc_id from job where "+
					" job_doc_edit NOT IN "+
					DocStatus.whereConditionUnlocked()+") LIMIT ${limit}", [collection.col_id], {row ->
						String lang = row['doc_lang']
						long doc_id = (long) row['doc_id']
						docs[doc_id] = ['lang':lang, 'doc_original_id':row['doc_original_id'], nes:[]]

						//def labels = Class.forName("rembrandt.gazetteers.${lang.toLowerCase()}.SecondHAREMClassificationLabels${lang.toUpperCase()}").newInstance()

						int ne_category_local_pt = neCategoryTable.getIDforLOCAL("pt")
						int ne_category_local_en = neCategoryTable.getIDforLOCAL("en")
						int ne_category_local_rm = neCategoryTable.getIDforLOCAL("rembrandt")//
						//conf.get("rembrandt.output.styletag.lang",conf.get("global.lang")))

						// println "$ne_category_local_pt $ne_category_local_en $ne_category_local_rm"

						def query = "SELECT dhn_section, dhn_sentence, dhn_term, nen_name, ne_id, ne_type, "+
								"ne_subtype, ent_id,  ent_dbpedia_class FROM doc_has_ne, ne_name, ne RIGHT JOIN entity ON "+
								"ent_id=ne_entity WHERE dhn_ne=ne_id AND ne_name=nen_id AND "+
								"dhn_doc=${doc_id} AND (ne_category=${ne_category_local_pt} OR ne_category=${ne_category_local_en} "+
								"OR ne_category=${ne_category_local_rm})"

						//println "query : $query"
						db.getDB().eachRow(query, [], {row2 ->
							docs[doc_id].nes << [section:row2['dhn_section'], sentence:row2['dhn_sentence'],
										term:row2['dhn_term'], name:row2['nen_name'], //neid:row2['ne_id'],
										type:neTypeTable.all_id_type[row2['ne_type']], subtype:neSubtypeTable.all_id_subtype[row2['ne_subtype']],
										entity:row2['ent_id'], dbpediaClass:row2['ent_dbpedia_class'] ]
						})
					})
		}// with transaction
		// println "Got docs: $docs"
		return docs
	}

	/*NOT THREAD SAFE!*/
	public Map getBatchDocsAndNEsFromPoolToGenerateTimeSignatures(Collection collection, int limit) {

		Map docs = [:]

		// TODO : change to doc_content
		db.getDB().withTransaction{

			log.info "Getting a set of $limit Docs to generate TimeSignatures"
			db.getDB().eachRow("SELECT doc_id, doc_original_id, doc_webstore, doc_date_created, doc_lang from ${tablename} " +
					" where doc_collection=? AND doc_latest_time_signature IS NULL AND "+
					" doc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND "+
					"doc_id NOT IN (select job_doc_id from job where "+
					"job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+") "+
					"LIMIT ${limit}", [collection.col_id], {row ->
						String doc_content = webstore.retrieve(row['doc_webstore'])
						docs[row['doc_id']] = [lang:row['doc_lang'], original_id:row['doc_original_id'],
							date:(Date)row['doc_date_created'], content:doc_content ]
					})
		}
		return docs
	}

	/*NOT THREAD SAFE!*/
	public Map getBatchDocsAndNEsFromPoolToGenerateNEIndex(Collection collection, int limit = 10,  offset = 0) {

		Map docs = [:]
		NECategoryTable neCategoryTable = getSaskiaDB().getDBTable("NECategoryTable")
		NETypeTable neTypeTable = getSaskiaDB().getDBTable("NETypeTable")
		NESubtypeTable neSubtypeTable = getSaskiaDB().getDBTable("NESubtypeTable")
		EntityTable entityTable = getSaskiaDB().getDBTable("EntityTable")

		neCategoryTable.createCache()
		neTypeTable.createCache()
		neSubtypeTable.createCache()

		db.getDB().withTransaction{

			// log.info "Getting a set of $limit Docs to generate GeoSignatures"
			db.getDB().eachRow(

					"SELECT * FROM ${tablename} "+
					"WHERE doc_collection=? "+
					"ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",
					[collection.col_id], {row ->
						String lang = row['doc_lang']
						long doc_id = (long) row['doc_id']
						docs[doc_id] = ['lang':lang, 'doc_original_id':row['doc_original_id'], nes:[]]

						def query = "SELECT dhn_section, dhn_sentence, dhn_term, ne_id, nen_name, ne_category, ne_type, "+
								"ne_subtype, ne_entity FROM doc_has_ne, ne_name, ne WHERE dhn_ne=ne_id AND "+
								"ne_name=nen_id AND dhn_doc=${doc_id} "

						//println "query : $query"
						db.getDB().eachRow(query, [], {row2 ->
							docs[doc_id].nes << [section:row2['dhn_section'], sentence:row2['dhn_sentence'],
										term:row2['dhn_term'], name:row2['nen_name'], //neid:row2['ne_id'],
										category: (row2['ne_category'] == null ? null : neCategoryTable.getFromID(row2['ne_category'])),
										type: (row2['ne_type'] == null ? null : neTypeTable.getFromID(row2['ne_type'])),
										subtype: (row2['ne_subtype'] == null ? null : neSubtypeTable.getFromID(row2['ne_subtype'])),
										entity: (row2['ne_entity'] == null ? null : entityTable.getFromID(row2['ne_entity']))]

						})
					})
		}// with transaction
		// println "Got docs: $docs"
		return docs
	}

	/**
	 * Add a reference to the latest geo_signature for this document
	 */
	public int addGeoSignatureIDtoDocID(long dgs_id, long doc_id) {
		if (!dgs_id || !doc_id) return null
		int res = db.getDB().executeUpdate("UPDATE ${tablename} SET doc_latest_geo_signature =? WHERE doc_id=?",
				[dgs_id, doc_id])
		return res
	}

	/**
	 * Add a reference to the latest time_signature for this document
	 */
	public int addTimeSignatureIDtoDocID(long dts_id, long doc_id) {
		if (!dts_id || !doc_id) return null
		int res = db.getDB().executeUpdate("UPDATE ${tablename} SET doc_latest_time_signature =? WHERE doc_id=?",
				[dts_id, doc_id])
		return res
	}

	/**
	 * Get pool of  documents in a thread-safe way. 
	 * By enforcing withTransaction, I'm setting autocommit=0
	 * By issuing 'FOR UPDATE' i'm locking other threads on their SELECT/UPDATE until this transaction is commited/rollbacked
	 * if this transaction succeeds, it'll commit, leaving the source docs marked with 'QU'.
	 * if it fails, it'll rollback, leaving them accessible for the next thread
	 * */
	public List<Doc> getNextProcessableAndUnlockedDoc(Task task, String process_signature, Collection doc_collection, int limit = 10) {

		List l = []
		Doc d
		int max_tries = 10
		int tries = 0
		while  (!l && (tries < max_tries)) {
			try {
				db.getDB().withTransaction{
					log.info "Try #${tries+1}: Getting a set of $limit processable Docs"

					def query = "SELECT * FROM ${tablename} WHERE doc_collection=? AND "+
						"doc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND doc_date_tagged IS NULL AND "+
						"doc_id NOT IN (select job_doc_id from ${JobTable.tablename} WHERE "+
						" job_doc_edit NOT IN "+
						DocStatus.whereConditionUnlocked()+") LIMIT ${limit} FOR UPDATE"
					// VERY IMPORTANT, the FOR UPDATE, it locks the table until the transaction is complete

					def params = [doc_collection.col_id]

					db.getDB().eachRow(query, params, {row ->
						d = Doc.createNew(this, row)
						
						// LET's create JOBS to mark the queue
						Job job = Job.createNew(db.getDBTable("JobTable"), 
						 [job_task:task, job_worker:process_signature, 
						  job_doc_id:d.doc_id, job_doc_edit:DocStatus.QUEUED, 
						  job_doc_edit_date:new Date()
						 ])
						 job.job_id = job.addThisToDB()
						 d.doc_job = job
						 l << d
					})
				}
			} catch (org.codehaus.groovy.runtime.InvokerInvocationException iie) {
				log.error iie.getMessage()
			} catch (SQLException sqle) {
				//
				// The two SQL states that are 'retry-able' are 08S01
				// for a communications error, and 40001 for deadlock.
				//
				// Only retry if the error was due to a stale connection,
				// communications problem or deadlock
				//
				String sqlState = sqle.getSQLState()
				log.warn "Doc: Got SQL error $sqlState"
				if ("08S01".equals(sqlState) || "40001".equals(sqlState) || "41000".equals(sqlState)) {
					log.warn "This error is retrieable! Good! Sleeping for 5 seconds..."
					sleep(5000)
					tries++
				}  else tries = max_tries
			}
		} // !l && max tries
		return l
	}
}
