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
package saskia.db.obj

import rembrandt.obj.ListOfNE;
import saskia.db.DocStatus;
import saskia.db.table.DocGeoSignatureTable
import saskia.db.obj.Entity;
import saskia.db.obj.Geoscope;
import saskia.db.obj.Job;
import saskia.db.obj.Tag;
import saskia.db.table.DBTable

/**
 * @author Nuno Cardoso
 *
 */
class RembrandtedDoc extends DBObject implements JSONable {
	/* doc table fields */
	Long doc_id
	String doc_original_id
	Collection doc_collection
	String doc_webstore
	Integer doc_version = 1
	String doc_lang
	Date doc_date_created
	Date doc_date_tagged
	DocStatus doc_proc
	DocStatus doc_sync
	Long doc_latest_geo_signature
	Long doc_latest_time_signature

	//This is obtained from the webstore
	String doc_content
	boolean retrieved_doc_content = false

	Job doc_job // if this RembrandtDoc is associated to a job, typically: R2P, GEO, TIM

	/* other fields gathered from other table data */
	ListOfNE NEs
	List<Tag> tags
	List<Geoscope> geoscopes
	List<Entity> entities

	static Map type = ['doc_id':'Long', 'doc_original_id':'String', 'doc_collection':'Collection',
		'doc_webstore':'String', 'doc_version':'Integer',
		'doc_lang':'String', 'doc_date_created':'Date', 'doc_date_created':'Date',
		'doc_proc':'DocStatus', 'doc_sync':'DocStatus',
		'doc_latest_geo_signature':'Long', 'doc_latest_time_signature':'Long']

	static RembrandtedDoc createFromDBRow(DBTable table, row) {
		RembrandtedDoc r = new RembrandtedDoc(dbtable)
		r.doc_id = row['doc_id']
		r.doc_original_id = row['doc_original_id']
		if (row['doc_collection']) r.doc_collection = Collection.getFromID(row['doc_collection'])
		r.doc_webstore = row['doc_webstore']
		r.doc_version = row['doc_version']
		if (row['doc_lang']) r.doc_lang = row['doc_lang']
		if (row['doc_date_created'] && (Date)row['doc_date_tagged'] != nulldate)
			r.doc_date_created = (Date)row['doc_date_created'] // it's a java.sql.Timestamp, a subclass of Date
		if (row['doc_date_tagged'] && (Date)row['doc_date_tagged'] != nulldate)
			r.doc_date_tagged = (Date)row['doc_date_tagged'] // it's a java.sql.Timestamp, a subclass of Date
		r.doc_proc = DocStatus.getFromValue(row['doc_proc'])
		r.doc_sync = DocStatus.getFromValue(row['doc_sync'])

		if (row['doc_latest_geo_signature']) r.doc_latest_geo_signature = row['doc_latest_geo_signature']
		if (row['doc_latest_time_signature']) r.doc_latest_time_signature = row['doc_latest_time_signature']
		if (r.doc_webstore) {  try {
				r.doc_content = getDBTable().getWebstore().retrieve(r.doc_webstore)
				r.retrieved_doc_content = true
			}catch(Exception e) { log.warn e.getMessage() }
		}
	}


	public Map toMap() {

		return ["doc_id":doc_id, "doc_collection":doc_collection.toSimpleMap(),
			"doc_original_id":doc_original_id, "doc_version":doc_version,
			"doc_lang":doc_lang, "doc_date_created":doc_date_created,
			"doc_entity":getEntities()?.collect{it.toMap()},
			"doc_tag":getTags()?.collect{it.toMap()},
			"doc_webstore":doc_webstore,
			"doc_date_tagged":doc_date_tagged,
			"doc_proc":doc_proc,
			"doc_sync":doc_sync,
			"doc_content":[
				"title": getTitleFromContent()?.replaceAll(/\n/, " "),
				"body": getBodyFromContent()?.replaceAll(/\n/, " ")
			]
		]
	}

	public Map toSimpleMap() {
		return toMap()
	}

	/** Handle with care! */
	public updateValue(column, value) {
		def newvalue
		switch (type[column]) {
			case 'String': newvalue = value; break
			case 'Long': newvalue = Long.parseLong(value); break
		}
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().getTablename()} SET ${column}=? WHERE doc_id=?",
				[newvalue, doc_id])
		return res
	}

	public DocGeoSignature getGeographicSignature() {
		return getDBTable().getSaskiaDB()
			.getDBTable("saskia.db.table.DocGeoSignatureTable")
			.getFromID(doc_latest_geo_signature)
	}

	public DocTimeSignature getTimeSignature() {
		return getDBTable().getSaskiaDB()
			.getDBTable("saskia.db.table.DocTimeSignatureTable")
			.getFromID(doc_latest_time_signature)
	}


	/* there's a difference between tags = null and tags = [] - the last one says that I cheched the DB */
	/* it both updates the entities var, and returns it */
	List<Tag> getTags() {
		if (tags == null) {
			List res = []
			getDBTable().saskiaDB().getDB().eachRow(
					"SELECT dtg_tag FROM ${dtg_table} WHERE dtg_document=?",
					[doc_id], {row -> res << Tag.getFromID(row[0])})
			tags = res
		}
		return tags
	}



	public int changeProcStatusInDBto(DocStatus status) {
		def res = getDBTable().saskiaDB().getDB().executeUpdate(
				"UPDATE ${RembrandtedDocTable.tablename} SET doc_proc=? WHERE doc_id=?",
				[status.text(), doc_id])
		log.debug "Wrote proc status ${status}(${status.text()}) to doc_id ${doc_id}, ${res} rows were changed."
		return res
	}

	public int changeSyncStatusInDBto(DocStatus status) {
		def res = getDBTable().saskiaDB().getDB().executeUpdate(
				"UPDATE ${RembrandtedDocTable.tablename} SET doc_sync=? WHERE doc_id=?",
				[status.text(), doc_id])
		log.debug "Wrote sync status ${status}(${status.text()}) to doc_id ${doc_id}, ${res} rows were changed."
		return res
	}


	/**
	 * The IGNORE does not generate error for duplicate keys
	 */
	public associateWithEntity(Entity entity) {
		getDBTable().saskiaDB().getDB().executeInsert(
				"INSERT IGNORE INTO ${die_table} VALUES(?,?)",
				[doc_id, entity.ent_id])
	}
	/**
	 * The IGNORE does not generate error for duplicate keys
	 */
	public associateWithTag(Tag tag) {
		//println "going for $doc_id, ${tag.tag_id}"
		getDBTable().saskiaDB().getDB().executeInsert(
				"INSERT IGNORE INTO ${RembrandtedDocTable.dtg_table} VALUES(?,?)",
				[doc_id, tag.tag_id])
	}


	String getTitleFromContent() {
		String s = null
		doc_content?.find(/(?si)<TITLE>(.*?)<\/TITLE>/) {match, g1 -> s = g1.trim()}
		return s
	}

	String getBodyFromContent() {
		// println "rdoc_content = $rdoc_content"
		String s = null
		doc_content?.find(/(?si)<BODY>(.*?)<\/BODY>/) {match, g1 -> s = g1.trim()}
		return s
	}

	String getPlainText(String text) {
		// take off tags
		String res = text.replaceAll(/<[^>]*>/,"")
		// protect escaped stuff
		res = res.replaceAll(/\\([\Q[]{}\E])/) {all, g1 -> "_¤¤¤_${g1.charAt(0).hashCode()}_¤¤¤_"}
		// [ = 91, ] = 93, { = 123, } = 125

		// take off all {, [, ] and } that are not preceeded with \
		res = res.replaceAll(/([\Q{}[]\E])/) {all, g1 ->
			if (g1 == "]") return " " else return ""}

		res = res.replaceAll(/_¤¤¤_(\d+)_¤¤¤_/) {all, g1 ->
			switch (Integer.parseInt(g1)) {
				case 91: return "["; break;
				case 93: return "]"; break;
				case 123: return "{"; break;
				case 125: return "}"; break;
			}
		}
		return res
	}

	/** loads NE info from DB */
	public ListOfNE addNEsFromSaskia(String title_or_body) {
		// TODO
		ListOfNE NEs = new ListOfNE()
		return NEs
	}

	/** Add the fields in this object to the DB
	 * @return The new id for the doc table, from the DB
	 */

	public Long addThisToDB() {
		// try to ground the document to an entity
		try {
			if (doc_content) {
				String key = getDBTable().webstore.store(doc_content, SaskiaWebstore.VOLUME_RDOC)
				log.trace "Got content $doc_content, wrote to Webstore RDOC, got key $key"

				def res = getDBTable().saskiaDB().getDB().executeInsert(
						"INSERT INTO ${RembrandtedDoc.tablename}"+
						"(doc_original_id, doc_collection, doc_webstore, doc_version, doc_lang, "+
						"doc_date_created, doc_date_tagged) VALUES(?,?,?,?,?,?,NOW())",
						[doc_original_id, doc_collection.col_id, key, doc_version, doc_lang, doc_date_created])
				doc_id = (long) res[0][0]
				doc_webstore = key
				log.info "Inserted RembrandtedDoc into DB: ${this}"
			} else {
				log.error "Did NOT inserted RembrandtedDoc into DB: doc_content for {$this} is empty!"
			}
		} catch (Exception e) {
			log.error "Did NOT inserted RembrandtedDoc into DB: ${e.getMessage()}"
		}
		return doc_id
	}

	public Long replaceThisToDB() {
		def res
		try {
			if (doc_content) {
				String key = getDBTable().webstore.store(doc_content, SaskiaWebstore.VOLUME_RDOC)
				log.trace "Got content $doc_content, wrote to Webstore RDOC, got key $key"

				res = getDBTable().saskiaDB().getDB().executeUpdate(
						"UPDATE ${RembrandtedDoc.tablename} SET "+
						"doc_original_id=?, doc_collection=?, doc_webstore=?, doc_lang=?, "+
						"doc_date_tagged=NOW(), doc_version=doc_version+1 WHERE doc_id=? ",
						[doc_original_id, doc_collection.col_id, key, doc_lang, doc_id])
				doc_version++
				log.info "Replaced RembrandtedDoc into DB: ${this}"
			} else {
				log.error "Did NOT replaced RembrandtedDoc into DB: doc_content for {$this} is empty!"
			}
		} catch (Exception e) {
			log.error "Did NOT replaced RembrandtedDoc into DB: ${e.getMessage()}"
		}
		return doc_id
	}

	public int removeThisFromDB() {
		if (!doc_id) return null
		def res = getDBTable().saskiaDB().getDB().executeUpdate(
				"DELETE FROM ${RembrandtedDoc.tablename} where doc_id=?",[doc_id])
		log.info "Removed RembrandtedDoc ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return "RDoc(${doc_id});DocOriginalId(${doc_original_id});Webstore(${doc_webstore});Lang(${doc_lang})"
	}
}
