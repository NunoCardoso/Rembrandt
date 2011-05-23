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

import saskia.db.obj.Collection
import saskia.db.table.DBTable
import groovy.sql.GroovyResultSet
/**
 * @author Nuno Cardoso
 * A Cache object
 */
class Cache extends DBObject {

	String cac_id
	Collection cac_collection
	String cac_lang
	Date cac_date
	Date cac_expire
	def cac_obj
	static Logger log = Logger.getLogger("Cache")
	
	public Cache(DBTable dbtable) {
		super(dbtable)
	}

	boolean isCacheFresh() {
		log.debug "Chech cahe fresh: cac_expire is "+(long)(cac_expire.getTime())+" d=$d"
		return cac_expire.getTime() > new Date().getTime()
	}
	
	
	static Cache createNew(DBTable dbtable, row) {
		Cache cache = new Cache(dbtable)
		if (row['cac_id']) cache.cac_id = row['cac_id']
		if (row['cac_collection'])
			cache.cac_collection = (row['cac_collection'] instanceof Collection ? 
			row['cac_collection'] :
			dbtable.getSaskiaDB().getDBTable("CollectionTable").getFromID(row['cac_collection']) )
		if (row['cac_lang']) cache.cac_lang = row['cac_lang']
		if (row['cac_date']) cache.cac_date = (Date)row['cac_date']
		if (row['cac_expire']) cache.cac_expire = (Date)row['cac_expire']
		
		if (row instanceof GroovyResultSet && row['cac_obj']) {
			java.sql.Blob blob = row.getBlob('cac_obj')
			byte[] bdata = blob.getBytes(1, (int) blob.length())
		// you have to say explicitly that mediawiki's mediumblob is in UTF-8
			cache.cac_obj = new String(bdata, "UTF-8")
		} else {
			if (row['cac_obj']) cache.cac_obj = row['cac_obj']
		}
			
		return cache
	}
	
	// used to add new collections.
	public Long addThisToDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
			"INSERT INTO ${getDBTable().tablename}(cac_id, cac_collection, cac_date, cac_expire, cac_lang, cac_obj) "+
			"VALUES(?,?, NOW(),?, ?, ?) ON DUPLICATE KEY UPDATE cac_date=NOW(), cac_expire=?, cac_obj=?",
			[cac_id, cac_collection.col_id, cac_expire, cac_lang, cac_obj, cac_expire, cac_obj])
		log.info "Cache added to DB: ${this}"
		return res
	}

	public int removeThisFromDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
			"DELETE FROM ${getDBTable().tablename} where cac_id=?",
			[cac_id])
		log.info "Cache removed from DB: ${cac_id}"
		return res
	}

	public String toString() {
		return ""+cac_id+":"+cac_collection+":"+cac_lang
	}
}
