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

import org.apache.log4j.Logger

import saskia.db.DocStatus
import saskia.db.obj.Job
import saskia.db.table.DBTable
import saskia.db.SaskiaWebstore
import pt.tumba.webstore.exceptions.VolumeUnreachable
import groovy.sql.*
/**
 * @author Nuno Cardoso
 *
 */
public class SourceDoc extends DBObject implements JSONable {

	static Logger log = Logger.getLogger("SourceDoc")


	/* source doc table fields */
	Long sdoc_id
	String sdoc_original_id
	String sdoc_webstore
	Collection sdoc_collection
	String sdoc_lang
	String sdoc_comment
	Date sdoc_date
	Long sdoc_doc
	DocStatus sdoc_proc = DocStatus.READY

	static Map type = ['sdoc_id':'Long', 'sdoc_original_id':'String', 'sdoc_collection':'Collection',
		'sdoc_lang':'String', 'sdoc_webstore':'String', 'sdoc_comment':'String',
		'usr_lastname':'String', 'usr_email':'String', 'usr_password':'String',
		'sdoc_date':'Date', 'sdoc_doc':'Long', 'sdoc_proc':'DocStatus']

	// it's not a table field, it's from webstore
	String sdoc_content
	boolean retrieved_sdoc_content = false

	Job sdoc_job // if this SourceDoc is associated to a job, typically importS2R

	public SourceDoc(DBTable dbtable) {
		super(dbtable)
	}

	static createNew(DBTable dbtable, row) {
		SourceDoc sd = new SourceDoc(dbtable)
		if (row['sdoc_id']) sd.sdoc_id = row['sdoc_id']
		if (row['sdoc_original_id']) sd.sdoc_original_id = row['sdoc_original_id']
		if (row['sdoc_collection']) 
			sd.sdoc_collection = (row['sdoc_collection'] instanceof Collection ? 
				row['sdoc_collection'] : dbtable.getSaskiaDB().getDBTable("CollectionTable").getFromID(row['sdoc_collection']) )
		if (row['sdoc_lang']) sd.sdoc_lang = row['sdoc_lang']
		sd.sdoc_date = (row['sdoc_date'] ?  (Date)row['sdoc_date']: new Date(0))
		if (row['sdoc_doc']) sd.sdoc_doc = row['sdoc_doc']
		if (row['sdoc_comment']) sd.sdoc_comment = row['sdoc_comment']
		if (row['sdoc_proc']) 
			sd.sdoc_proc = (row['sdoc_proc'] instanceof DocStatus ? 
			row['sdoc_proc'] : DocStatus.getFromValue(row['sdoc_proc']))

		if (row['sdoc_webstore']) sd.sdoc_webstore = row['sdoc_webstore']
		
		// A new SourceDoc coming from the DB...
		if (row instanceof GroovyResultSet && row['sdoc_webstore']) {try {
				sd.sdoc_content = sd.getContent()
				sd.retrieved_sdoc_content = true
			}catch(Exception e) {log.warn e.getMessage()} 

		// but it also may be a new SourceDoc to be added to the DB...
		}
		if (!(row instanceof GroovyResultSet) && !row['sdoc_webstore'] && row.containsKey('sdoc_content')) {  
			try {
				sd.sdoc_content = row['sdoc_content']
				sd.retrieved_sdoc_content = false
			}catch(Exception e) { log.warn e.getMessage() }
		} 		

		return sd
	}

	public Map toMap() {
		return toShowMap()
	}

	public Map toSimpleMap() {
		return toListMap()
	}

	Map toListMap() {
		return ["sdoc_id":sdoc_id, "sdoc_original_id":sdoc_original_id,
			"sdoc_collection":sdoc_collection.toSimpleMap(), "sdoc_webstore":sdoc_webstore,
			"sdoc_lang":sdoc_lang, "sdoc_comment":sdoc_comment, "sdoc_date":sdoc_date,
			"sdoc_doc":sdoc_doc, "sdoc_proc":sdoc_proc]
	}

	Map toShowMap() {
		return ["sdoc_id":sdoc_id, "sdoc_original_id":sdoc_original_id,
			"sdoc_collection":sdoc_collection.toSimpleMap(), "sdoc_webstore":sdoc_webstore,
			"sdoc_lang":sdoc_lang, "sdoc_comment":sdoc_comment, "sdoc_date":sdoc_date,
			"sdoc_doc":sdoc_doc, "sdoc_proc":sdoc_proc,
			"sdoc_content":[
				"title": getTitleFromContent()?.replaceAll(/\n/, " "),
				"body": getBodyFromContent()?.replaceAll(/\n/, " ")
			]
		]
	}
	/** Handle with care! */
	public updateValue(column, value) {
		def newvalue
		switch (type[column]) {
			case 'String': newvalue = value; break
			case 'Long': newvalue = Long.parseLong(value); break
		}
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().tablename} SET ${column}=? WHERE doc_id=?",
				[newvalue, doc_id])
		return res
	}


	String getContent() {
		if (retrieved_sdoc_content && sdoc_content) return sdoc_content
		//try {
		sdoc_content = getDBTable().getWebstore().retrieve(sdoc_webstore)
		if (sdoc_content) retrieved_sdoc_content = true
		return sdoc_content

	}


	public String getTitle() {
		def m = sdoc_content =~ /(?si).*<TITLE>(.*?)<\/TITLE>.*/
		if (m.matches()) return  m.group(1).replaceAll("_"," ")
		return null
	}

	String getTitleFromContent() {
		String s = null
		sdoc_content?.find(/(?si)<TITLE>(.*?)<\/TITLE>/) {match, g1 -> s = g1.trim()}
		return s
	}

	String getBodyFromContent() {
		// println "rdoc_content = $rdoc_content"
		String s = null
		sdoc_content?.find(/(?si)<BODY>(.*?)<\/BODY>/) {match, g1 -> s = g1.trim()}
		return s
	}



	public int changeProcStatusInDBto(DocStatus status) {
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().tablename} SET sdoc_proc=? WHERE sdoc_id=? and "+
				"sdoc_collection=? and sdoc_lang=?",
				[
					status.text(),
					sdoc_id,
					sdoc_collection.col_id,
					sdoc_lang
				])
		if (res) log.info "SourceDoc ${this} got status changed to $status (${status.text()})"
		return res
	}

	public int addDocID(Long doc_id) {
		if (!doc_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().tablename} SET sdoc_doc=? WHERE sdoc_id=? and "+
				"sdoc_collection=? and sdoc_lang=?", [
					doc_id,
					sdoc_id,
					sdoc_collection.col_id,
					sdoc_lang
				])
		if (res) log.info "SourceDoc ${this} got associated to RembrandtedDoc ${doc_id}"
		return res
	}

	/** Add the fields in this object to the DB 
	 * @return The new id for the doc table, from the DB
	 */
	public Long addThisToDB() {

		try {
			String key
			if (sdoc_content) {
				key = getDBTable().getWebstore().store(sdoc_content, SaskiaWebstore.VOLUME_SDOC)
				log.info "Got content (${sdoc_content.size()} bytes), wrote to Webstore SDOC, got key $key"
				def res = getDBTable().getSaskiaDB().getDB().executeInsert(
						"INSERT INTO ${getDBTable().tablename}"+
						"(sdoc_original_id, sdoc_webstore, sdoc_collection, sdoc_lang, "+
						" sdoc_comment, sdoc_date, sdoc_doc, sdoc_proc) VALUES(?,?,?,?,?,?,?,?)",
						[
							sdoc_original_id,
							key,
							sdoc_collection.col_id,
							sdoc_lang,
							sdoc_comment,
							sdoc_date,
							sdoc_doc,
							sdoc_proc.text()
						])
				sdoc_id = (long) res[0][0]
				sdoc_webstore = key
				log.info "Inserted SourceDoc into DB: ${this}"
			} else {
				log.error "Did NOT inserted SourceDoc into DB: sdoc_content for {$this} is empty!"
			}
		} catch (VolumeUnreachable ve) {
			log.fatal "SourceDoc can't proceed with addThisToDB. Reason: Webstore volume ${SaskiaWebstore.VOLUME_SDOC} is not running."
			log.fatal "Please launch the volume with 'webstore -l ${SaskiaWebstore.VOLUME_SDOC}'"
			System.exit(0)
		}
		catch (Exception e) {
			log.error "Did NOT inserted SourceDoc into DB: ${e.getMessage()}"
		}
		return sdoc_id
	}

	/** Add the fields in this object to the DB 
	 * @return The new id for the doc table, from the DB
	 */
	public Long replaceThisToDB() {

		try {
			String key
			if (sdoc_content) {
				// apagar o conteúdo anterior
				if (sdoc_webstore) {
					try {
						getDBTable().getWebstore().delete(sdoc_webstore, SaskiaWebstore.VOLUME_SDOC)
						log.info "Replacing SourceDoc: deleted webstore $sdoc_webstore."
					} catch(Exception e) {
						log.error "Error while replacing SourceDoc: "+e.getMessage()
					}
				}
				key = getDBTable().getWebstore().store(sdoc_content, SaskiaWebstore.VOLUME_SDOC)
				log.info "Got content (${sdoc_content.size()} bytes), wrote to Webstore SDOC, got key $key"
				
				// add sdoc_id so it can make the replacement
				def res = getDBTable().getSaskiaDB().getDB().executeInsert(
						"REPLACE INTO ${getDBTable().tablename}(sdoc_id, sdoc_original_id, sdoc_webstore, "+
						"sdoc_collection, sdoc_lang, sdoc_comment, sdoc_date, sdoc_doc, sdoc_proc) "+
						"VALUES(?,?,?,?,?,?,?,?,?)", [
							sdoc_id,
							sdoc_original_id,
							key,
							sdoc_collection.col_id,
							sdoc_lang,
							sdoc_comment,
							sdoc_date,
							sdoc_doc,
							sdoc_proc.text()
						])
				sdoc_id = (long) res[0][0]
				sdoc_webstore = key
				log.info "Replaced SourceDoc into DB: ${this}"
			} else {
				log.error "Did NOT replaced SourceDoc into DB: ${e.getMessage()}"
			}
		}catch (VolumeUnreachable ve) {
			log.fatal "SourceDoc can't proceed with addThisToDB. Reason: Webstore volume ${SaskiaWebstore.VOLUME_SDOC} is not running."
			log.fatal "Please launch the volume with 'webstore -l ${SaskiaWebstore.VOLUME_SDOC}' and try again."
			System.exit(0)
		} catch (Exception e) {
			log.error "Error while replaced SourceDoc into DB: ${e.getMessage()}"
		}
		return sdoc_id
	}

	public int removeThisFromDB() {
		if (!sdoc_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} where sdoc_id=?",[sdoc_id])
		log.info "Removed SourceDoc ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return "${sdoc_id}:${sdoc_collection}:${sdoc_lang}"
	}

}
