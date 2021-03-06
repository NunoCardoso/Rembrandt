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


/** This class is an interface for the RembrandtedDoc and Doc tables in the WikiRembrandt 
 * database. There is a 1:1 relation between them - Doc has the metadata, RembrandtDoc
 * the contents. The reason is that RembrandtedDoc has a FULLTEXT index and needs to be 
 * a MySQL myISAM table (InnoDB does not have FULLTEXT), but Doc is a InnoDB to support 
 * all the foreign keys and keep the integrity of the database (which MyISAM does not support yet).
 */

class RembrandtedDocTable extends DBTable {

	static String tablename = "doc"
	String die_table = "doc_is_entity"
	String dhn_table = "doc_has_ne"
	String dtg_table = "doc_has_tag"
	static String job_doc_type_label = "RDOC"

	// Document is already post_processed.
	Configuration conf
	SaskiaWebstore webstore
	RembrandtReader reader

	static Logger log = Logger.getLogger("RembrandtedDoc")

	String lang

	DBpediaAPI dbpedia = DBpediaAPI.newInstance()

	public RembrandtedDocTable(SaskiaDB db) {
		super(db)
		conf = Configuration.newInstance()
		webstore = SaskiaWebstore.newInstance()
		if (!webstore) {
			log.warn "RembrandtedDocTable: webstore not initialized, there may be some errors coming. Chewck the webstore."
		}
		lang = conf.get("global.lang")
		reader = new RembrandtReader( new RembrandtStyleTag(
				conf.get("rembrandt.input.styletag.lang", conf.get("global.lang"))))

	}
	
	public SaskiaWebstore getWebstore() {
		return this.webstore
	}

	public List<RembrandtedDoc> queryDB(String query, ArrayList params) {
		List<RembrandtedDoc> l = []
		db.getDB().eachRow(query, params, {row  ->
			l << RembrandtedDoc.createNew(this, row)
		})
		return l
	}


	public Map listRembrandtedDocs(Collection collection, limit = 10,  offset = 0, column = null, needle = null) {

		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		String where = "WHERE doc_collection=? "
		List params = [collection.col_id]
		if (column && needle) {
			switch (RembrandtedDoc.type[column]) {
				case 'String': where += " AND $column LIKE '%${needle}%'"; break
				case 'Long': where += " AND $column=? "; params << Long.parseLong(needle); break
				case 'DocStatus':  where += " AND $column = ?"; params << needle; break
				case 'Date': where += " AND $column = ?"; params << needle; break
			}
		}

		String query = "SELECT SQL_CALC_FOUND_ROWS * FROM ${tablename} "+
				"$where LIMIT ${limit} OFFSET ${offset} UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), "+
				"NULL, NULL, NULL, NULL, NULL, NULL,NULL,NULL,NULL,NULL,NULL"
		log.debug "query = $query params = $params class = "+params*.class
		List u
		try {u = queryDB(query, params) }
		catch(Exception e) {log.error "Error getting rembrandted doc list: ", e}
		// last "user" is not the user... it's the count.
		RembrandtedDoc fake_rdoc = u.pop()
		long total = fake_rdoc.doc_id
		log.debug "Returning "+u.size()+" results."
		return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
			"column":column, "value":needle, "col_id":collection.col_id]
	}


	public List<RembrandtedDoc> getBatchOfRembrandtedDocs(Collection collection, limit = 10,  offset = 0) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		return queryDB("SELECT SQL_CALC_FOUND_ROWS * "+
		" FROM  ${tablename}  WHERE doc_collection=? "+
		"ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id])
		// ORDER BY doc_id ASC assures that these batches are ordered
	}

	public List<RembrandtedDoc> getBatchOfRembrandtedDocsWithComment(Collection collection, String comment, limit = 10,  offset = 0) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		return queryDB("SELECT SQL_CALC_FOUND_ROWS * "+
		" FROM  ${tablename}, source_doc  WHERE doc_collection=? and doc_id=sdoc_doc and sdoc_comment = ? "+
		"ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id, comment])
		// ORDER BY doc_id ASC assures that these batches are ordered
	}

	public List<RembrandtedDoc> getBatchOfRembrandtedDocsOrderedByOriginalDocId(Collection collection, limit = 10,  offset = 0) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		return queryDB("SELECT SQL_CALC_FOUND_ROWS * "+
		" FROM  ${tablename}  WHERE doc_collection=? "+
		"ORDER BY doc_original_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id])
		// ORDER BY doc_original_id ASC assures that these batches are ordered
	}

	public RembrandtedDoc getFromOriginalDocIDandCollection(String doc_original_id, Collection collection) {
		if (!doc_original_id || !collection) return null
		List<RembrandtedDoc> l = queryDB("SELECT * FROM ${tablename} "+
				"WHERE doc_original_id=? AND doc_collection=?",
				[
					doc_original_id,
					collection.col_id,
				])
		log.trace "Querying Saskia for doc_original_id:$doc_original_id, collection $collection, got ${l}"
		return (l ? l[0] : null)
	}

	/** Get a RembrandtedDoc from an id of the Doc table.
	 * @param doc_id The id of the document. 
	 * return the RembrandtedDoc
	 */ 
	public RembrandtedDoc getFromID(long doc_id) {
		if (!doc_id) return null
		List<RembrandtedDoc> l = queryDB("SELECT * FROM ${tablename} WHERE doc_id=?", [doc_id])
		log.info "Querying for doc_id $doc_id, got RembrandtedDoc ${l}"
		return (l ? l[0] : null)
	}

	static RembrandtedDoc getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("RembrandtedDocTable").getFromID(id)
	}

	/** Get a RembrandtedDoc from an id of the Doc table.
	 * @param doc_id The id of the document. 
	 * return the RembrandtedDoc
	 */ 
	public RembrandtedDoc getFromOriginalID(String doc_original_id) {
		if (!doc_original_id) return null
		List<RembrandtedDoc> l = queryDB("SELECT * FROM ${tablename} WHERE doc_original_id=?", [doc_original_id])
		log.info "Querying for doc_original_id $doc_original_id, got RembrandtedDoc ${l}"
		return (l ? l[0] : null)
	}

	static RembrandtedDoc getFromOriginalID(SaskiaDB db, String doc_original_id) {
		return  db.getDBTable("RembrandtedDoceTable").getFromOriginalID(doc_original_id)
	}

	/**
	 * Get a batch of RembrandtedDocs from a list of ids
	 */ 
	public List<RembrandtedDoc> getFromOriginalIDs(List<Long> doc_ids, Collection collection) {
		if (!doc_ids || !collection) return null
		String where = "("+doc_ids.join(",")+")"
		List<RembrandtedDoc> l = queryDB("SELECT * FROM ${tablename} WHERE "+
				"doc_collection=? AND doc_original_id IN "+where, [collection.col_id])
		return (l ? l : null)
	}

	/** Get a batch of RembrandtedDocs good to sync NE pool
	 * @param batchsize The batchsize, default to 100
	 * This method is now treadsafe
	 * return A list of RembrandtedDocs
	 */ 
	public List<RembrandtedDoc> getBatchDocsToSyncNEPool(Task task, String process_signature,
	Collection collection, int limit = 30) {

		List<RembrandtedDoc> l = []
		RembrandtedDoc r

		db.getDB().withTransaction{
			log.info "Getting a set of $limit processable rembrandtedDocs to sync to NE pool"

			def query = "SELECT * FROM ${tablename} WHERE doc_collection=? AND doc_proc IN "+
					DocStatus.whereConditionGoodToProcess()+" AND doc_sync in "+DocStatus.whereConditionGoodToSyncNEPool()+
					" AND doc_id NOT IN (select job_doc_id from ${JobTable.tablename} where job_doc_type='"+
					job_doc_type_label+"' AND job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+
					") LIMIT ${limit} FOR UPDATE"
			// VERY IMPORTANT, the FOR UPDATE, it locks the table until the transaction is complete

			def params = [collection.col_id]
			log.trace query
			log.trace params

			db.getDB().eachRow(query, params, {row ->
				r = RembrandtedDoc.createNew(this, row)
				l << r

				// LET's create JOBS to mark the queue
				Job job = Job.createNew(db.getDBTable("JobTable"), 
						 [job_task:task, job_worker:process_signature, job_doc_type:job_doc_type_label,
						  job_doc_id:r.doc_id, job_doc_edit:DocStatus.QUEUED, job_doc_edit_date:new Date()
						 ])
					
				job.job_id = job.addThisToDB()
				r.doc_job = job
			})
		}// with transaction
		return l
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

			log.info "Getting a set of $limit RembrandtedDocs to generate GeoSignatures"
			db.getDB().eachRow("SELECT doc_id, doc_original_id, doc_lang from doc where "+
					"doc_collection=? AND doc_latest_geo_signature IS NULL "+
					"AND doc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND "+ "doc_sync in "+
					DocStatus.whereConditionSynced()+" AND doc_id NOT IN (select job_doc_id from job where "+
					"job_doc_type='"+job_doc_type_label+"' and job_doc_edit NOT IN "+
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

	public Map getBatchDocsAndNEsFromRDOCToGenerateGeoSignatures(Collection collection, int limit) {

		Map docs = [:]
		NECategoryTable neCategoryTable = getSaskiaDB().getDBTable("NECategoryTable")
		NENameTable neNameTable = getSaskiaDB().getDBTable("NENameTable")
		NETable neTable = getSaskiaDB().getDBTable("NETable")
		NETypeTable neTypeTable = getSaskiaDB().getDBTable("NETypeTable")
		NESubtypeTable neSubtypeTable = getSaskiaDB().getDBTable("NESubtypeTable")
		EntityTable entityTable = getSaskiaDB().getDBTable("EntityTable")

		neCategoryTable.createCache()
		neTypeTable.createCache()
		neSubtypeTable.createCache()

		List docs_list =

				db.getDB().eachRow("SELECT doc_id, doc_original_id, doc_webstore, doc_lang from doc where "+
				"doc_collection=? AND doc_latest_geo_signature IS NULL "+
				"AND doc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND "+
				//"doc_sync in "+DocStatus.whereConditionSynced()+" AND
				"doc_id NOT IN (select job_doc_id from job where "+
				"job_doc_type='"+job_doc_type_label+"' and job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+") "+
				" LIMIT ${limit}", [collection.col_id], {row ->
					String lang = row['doc_lang']
					long doc_id = (long) row['doc_id']
					docs[doc_id] = ['lang':lang, 'doc_original_id':row['doc_original_id'], nes:[]]
					String content
					if (row['doc_webstore']) {
						try {
							content = webstore.retrieve(row['doc_webstore'])
						}catch(Exception e) { log.warn e.getMessage() }
					}
					Document doc = reader.createDocument(content)
					doc.tokenize()

					doc.titleNEs?.each{ne ->

						String section = 'T'

						ne.classification?.each{cl ->

							NECategory category = (cl.c? neCategoryTable.getFromCategory(cl.c) : null)
							NEType type = (cl.t? neTypeTable.getFromType(cl.t) : null)
							NESubtype subtype = (cl.s? neSubtypeTable.getFromSubtype(cl.s) : null)

							// use only those who are LOCAL

							if (cl.c == "@LOCAL") {
								Entity e = (ne.dbpediaPage.containsKey(cl) ?
										(ne.dbpediaPage[cl] instanceof List ?
										(!ne.dbpediaPage[cl].isEmpty() ? entityTable.getFromDBpediaResource(ne.dbpediaPage[cl][0]) : null)
										: entityTable.getFromDBpediaResource(ne.dbpediaPage[cl])
										) : null)

								if (!e) {
									// if it does not have an Entity, let's check if NE can help us
									NEName ne_name = neNameTable.getFromName(ne.printTerms())
									NE ne2 = neTable.getFromNameAndLangAndClassificationAndNonNullEntity(
										ne_name, lang, category, type, subtype)
									if (ne2 && ne2.ne_entity) e = ne2.ne_entity
								}


								// don't care about those who does not have Entity
								if (e) docs[doc_id].nes << [section:section, sentence:ne.sentenceIndex,
									term:ne.termIndex, name:ne.printTerms(), //neid:row2['ne_id'],
									//category:category,
									type:type, subtype:subtype, entity:e.ent_id, dbpediaClass:e.ent_dbpedia_class ]
							}
						}
					}
					doc.bodyNEs?.each{ne ->

						String section = 'B'

						ne.classification?.each{cl ->

							NECategory category = (cl.c? neCategoryTable.getFromCategory(cl.c) : null)
							NEType type = (cl.t? neTypeTable.getFromType(cl.t) : null)
							NESubtype subtype = (cl.s? neSubtypeTable.getFromSubtype(cl.s) : null)

							// use only those who are LOCAL

							if (cl.c == "@LOCAL") {
								Entity e = (ne.dbpediaPage.containsKey(cl) ?
										(ne.dbpediaPage[cl] instanceof List ?
										(!ne.dbpediaPage[cl].isEmpty() ? entityTable.getFromDBpediaResource(ne.dbpediaPage[cl][0]) : null)
										: entityTable.getFromDBpediaResource(ne.dbpediaPage[cl])
										) : null)

								if (!e) {
									// if it does not have an Entity, let's check if NE can help us
									NEName ne_name = neNameTable.getFromName(ne.printTerms())
									NE ne2 = neTable.getFromNameAndLangAndClassificationAndNonNullEntity(ne_name, lang, category, type, subtype)
									if (ne2 && ne2.ne_entity) e = ne2.ne_entity
								}

								// don't care about those who does not have Entity
								if (e) docs[doc_id].nes << [section:section, sentence:ne.sentenceIndex,
									term:ne.termIndex, name:ne.printTerms(), //neid:row2['ne_id'],
									//category:category,
									type:type, subtype:subtype, entity:e.ent_id, dbpediaClass:e.ent_dbpedia_class ]
							}
						}
					}
				})
		return docs
	}

	/*NOT THREAD SAFE!*/
	public Map getBatchDocsAndNEsFromPoolToGenerateTimeSignatures(Collection collection, int limit) {

		Map docs = [:]

		// TODO : change to rdoc_content
		db.getDB().withTransaction{

			log.info "Getting a set of $limit RembrandtedDocs to generate TimeSignatures"
			db.getDB().eachRow("SELECT doc_id, doc_original_id, doc_webstore, doc_date_created, doc_lang from ${tablename} " +
					" where doc_collection=? AND doc_latest_time_signature IS NULL AND "+
					" doc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND "+
					" doc_sync IN "+DocStatus.whereConditionSynced()+" AND "+
					"doc_id NOT IN (select job_doc_id from job where "+
					"job_doc_type='"+job_doc_type_label+"' and job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+") "+
					"LIMIT ${limit}", [collection.col_id], {row ->


						String doc_content = webstore.retrieve(row['doc_webstore'])

						docs[row['doc_id']] = [lang:row['doc_lang'], original_id:row['doc_original_id'],
									date:(Date)row['doc_date_created'], content:doc_content ]

					})
		}
		return docs
	}

	public Map getBatchDocsAndNEsFromRDOCToGenerateTimeSignatures(Collection collection, int limit) {

		Map docs = [:]

		// TODO : change to rdoc_content
		db.getDB().withTransaction{

			log.info "Getting a set of $limit RembrandtedDocs to generate TimeSignatures"
			db.getDB().eachRow("SELECT doc_id, doc_original_id, doc_webstore, doc_date_created, doc_lang from ${tablename} " +
					" where doc_collection=? AND doc_latest_time_signature IS NULL AND "+
					" doc_proc IN "+DocStatus.whereConditionGoodToProcess()+
					" AND doc_id NOT IN (select job_doc_id from job where "+
					"job_doc_type='"+job_doc_type_label+"' and job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+") "+
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

			// log.info "Getting a set of $limit RembrandtedDocs to generate GeoSignatures"
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

	/*NOT THREAD SAFE!*/    
	static Map getBatchDocsAndNEsFromRDOCToGenerateNEIndex(Collection collection, int limit= 10, offset = 0) {

		Map rdocs = [:]
		NECategoryTable neCategoryTable = getSaskiaDB().getDBTable("NECategoryTable")
		NENameTable neNameTable = getSaskiaDB().getDBTable("NENameTable")
		NETable neTable = getSaskiaDB().getDBTable("NETable")
		NETypeTable neTypeTable = getSaskiaDB().getDBTable("NETypeTable")
		NESubtypeTable neSubtypeTable = getSaskiaDB().getDBTable("NESubtypeTable")
		EntityTable entityTable = getSaskiaDB().getDBTable("EntityTable")

		neCategoryTable.createCache()
		neTypeTable.createCache()
		neSubtypeTable.createCache()


		List rdocs_list = getBatchOfRembrandtedDocs(collection, limit, offset)
		log.debug "Got ${rdocs?.size()} RembrandtedDoc(s)."
		rdocs_list?.each {rdoc ->
			Document doc = reader.createDocument(rdoc.doc_content)
			doc.tokenize()

			rdocs[rdoc.doc_id] = ['lang':rdoc.lang, 'doc_original_id':rdoc.doc_original_id, nes:[]]

			doc.titleNEs?.each{ne ->

				String section = 'T'

				ne.classification?.each{cl ->

					NECategory category = (cl.c? neCategoryTable.getFromCategory(cl.c) : null)
					NEType type = (cl.t? neTypeTable.getFromType(cl.t) : null)
					NESubtype subtype = (cl.s? neSubtypeTable.getFromSubtype(cl.s) : null)

					Entity e = (ne.dbpediaPage.containsKey(cl) ?
							(ne.dbpediaPage[cl] instanceof List ?
							(!ne.dbpediaPage[cl].isEmpty() ? entityTable.getFromDBpediaResource(ne.dbpediaPage[cl][0]) : null)
							: entityTable.getFromDBpediaResource(ne.dbpediaPage[cl])
							) : null)

					if (!e) {
						// if it does not have an Entity, let's check if NE can help us
						if (!(cl.c == "@TEMPO" || cl.c == "@VALOR" || cl.c == "@NUMERO")) {
							NEName ne_name = neNameTable.getFromName(ne.printTerms())
							NE ne2 = neTable.getFromNameAndLangAndClassificationAndNonNullEntity(ne_name, rdoc.lang, category, type, subtype)
							if (ne2 && ne2.ne_entity) e = ne2.ne_entity
						}
					}

					rdocs[rdoc.doc_id].nes << [section:section, sentence:ne.sentenceIndex,
								term:ne.termIndex, name:ne.printTerms(), //neid:row2['ne_id'],
								category:category, type:type, subtype:subtype, entity:e ]
				}
			}
			doc.bodyNEs?.each{ne ->

				String section = 'B'

				ne.classification?.each{cl ->

					NECategory category = (cl.c? neCategoryTable.getFromCategory(cl.c) : null)
					NEType type = (cl.t? neTypeTable.getFromType(cl.t) : null)
					NESubtype subtype = (cl.s? neSubtypeTable.getFromSubtype(cl.s) : null)

					Entity e = (ne.dbpediaPage.containsKey(cl) ?
							(ne.dbpediaPage[cl] instanceof List ?
							(!ne.dbpediaPage[cl].isEmpty() ? entityTable.getFromDBpediaResource(ne.dbpediaPage[cl][0]) : null)
							: entityTable.getFromDBpediaResource(ne.dbpediaPage[cl])
							) : null)

					if (!e) {
						// if it does not have an Entity, let's check if NE can help us
						if (!(cl.c == "@TEMPO" || cl.c == "@VALOR" || cl.c == "@NUMERO")) {
							NEName ne_name = neNameTable.getFromName(ne.printTerms())
							NE ne2 = neTable.getFromNameAndLangAndClassificationAndNonNullEntity(ne_name, rdoc.lang, category, type, subtype)
							if (ne2 && ne2.ne_entity) e = ne2.ne_entity
						}
					}

					rdocs[rdoc.doc_id].nes << [section:section, sentence:ne.sentenceIndex,
								term:ne.termIndex, name:ne.printTerms(), //neid:row2['ne_id'],
								category:category, type:type, subtype:subtype, entity:e ]
				}
			}
		}
		return rdocs
	}

	/** Get a batch of RembrandtedDocs to sync FROM the NE pool.
	 * This method is now threadsafe
	 * @param batchsize The batchsize, default to 100
	 * return A list of RembrandtedDocs
	 */ 
	public List<RembrandtedDoc> getBatchDocsToSyncFromNEPool(Task task, Collection collection,
	String process_signature, int batchSize = 30) {
		List<RembrandtedDoc> l = []
		RembrandtedDoc r

		db.getDB().withTransaction{
			log.info "Getting a set of $limit processable rembrandtedDocs to sync from NE pool"

			def query = "SELECT HIGH_PRIORITY * FROM ${tablename} WHERE "+
					"doc_collection=? AND doc_proc IN "+
					DocStatus.whereConditionGoodToProcess()+" AND "+
					"doc_sync in "+DocStatus.whereConditionGoodToSyncFromNEPool()+
					" AND doc_id NOT IN (select job_doc_id from job where job_doc_type='"+job_doc_type_label+"' and "+
					"job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+") LIMIT ${limit} FOR UPDATE"  // VERY IMPORTANT, the FOR UPDATE, it locks the table until the transaction is complete

			def params = [collection.col_id]
			log.trace query
			log.trace params

			db.getDB().eachRow(query, params, {row ->
				r = RembrandtedDoc.createNew(db.getDBTable("RembrandtedDocTable"), 
				 [doc_id:row['doc_id'], doc_original_id:row['doc_original_id'], 
				 doc_webstore:row['doc_webstore'], doc_lang:row['doc_lang'],
				 doc_date_created:row['doc_date_created'], // it's a java.sql.Timestamp, a subclass of Date
				 doc_date_tagged:row['doc_date_tagged'], // it's a java.sql.Timestamp, a subclass of Date
				 doc_proc:row['doc_proc'],
				 doc_sync:row['doc_sync']
				])
				
				l << r

				// LET's create JOBS to mark the queue
				Job job = Job.createNew(db.getDBTable("JobTable"), 
						 [job_task:task, job_worker:process_signature, job_doc_type:job_doc_type_label,
						  job_doc_id:r.doc_id, job_doc_edit:DocStatus.QUEUED, job_doc_edit_date:new Date()
						 ])

				job.job_id = job.addThisToDB()
				r.doc_job = job

			})
		}// with transaction
		return (l ? l : null)
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
}
