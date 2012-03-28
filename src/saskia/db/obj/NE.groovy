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

import saskia.db.table.DBTable
import saskia.db.table.EntityTable

/**
 * @author Nuno Cardoso
 *
 */
class NE extends DBObject implements JSONable {

	Long ne_id
	NEName ne_name
	String ne_lang
	NECategory ne_category
	NEType ne_type
	NESubtype ne_subtype
	Entity ne_entity

	static Logger log = Logger.getLogger("NE")

	static Map type = ['ne_id':'Long', 'ne_name':'NEName', 'ne_lang':'String',
		'ne_category':'NECategory', 'ne_type':'NEType', 'ne_subtype':'NESubtype',
		'ne_entity':'Entity']

	public NE(DBTable dbtable) {
		super(dbtable)
	}

	static NE createNew(DBTable dbtable, row) {
		NE n = new NE(dbtable)
		if (row['ne_id']) n.ne_id = row['ne_id']
		if (row['ne_name']) 
			n.ne_name = (row['ne_name'] instanceof NEName ? row['ne_name'] : 
				dbtable.getSaskiaDB().getDBTable("NENameTable").getFromID(row['ne_name']) )
		if (row['ne_lang']) n.ne_lang = row['ne_lang']
		if (row['ne_category']) 
			n.ne_category = (row['ne_category'] instanceof NECategory ? row['ne_category'] : 
				dbtable.getSaskiaDB().getDBTable("NECategoryTable").getFromID(row['ne_category']) )
		if (row['ne_type']) 
			n.ne_type = (row['ne_type'] instanceof NEType ? row['ne_type'] : 
				dbtable.getSaskiaDB().getDBTable("NETypeTable").getFromID(row['ne_type']) )
		if (row['ne_subtype'])
			n.ne_subtype = (row['ne_subtype'] instanceof NESubtype ? row['ne_subtype'] : 
				dbtable.getSaskiaDB().getDBTable("NESubtypeTable").getFromID(row['ne_subtype']) )
		if (row['ne_entity']) 
			n.ne_entity = (row['ne_entity'] instanceof Entity ? row['ne_entity'] : 
				dbtable.getSaskiaDB().getDBTable("EntityTable").getFromID(row['ne_entity']) )
		return n
	}

	public Map toMap() {
		return ["ne_id":ne_id,
			"ne_name":ne_name?.toMap(),
			"ne_lang":ne_lang,
			"ne_category":ne_category?.toMap(),
			"ne_type":ne_type?.toMap(),
			"ne_subtype":ne_subtype?.toMap(),
			"ne_entity":ne_entity?.toMap()]
	}

	public Map toSimpleMap() {
		return toMap()
	}

	// generate a hash key for cache map
	public String getKey() {
		return "${ne_name?.nen_id} $ne_lang ${ne_category?.nec_id} ${ne_type?.net_id} ${ne_subtype?.nes_id} ${ne_entity?.ent_id}"
	}

	public updateValue(column, value) {
		return getDBTable().updateValue(ne_id, column, value);
	}
	
	public Long addThisToDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().tablename}(ne_id, ne_name, ne_lang, ne_category, "+
				"ne_type, ne_subtype, ne_entity) VALUES(0,?,?,?,?,?,?)",
				[
					ne_name.nen_id,
					ne_lang,
					ne_category.nec_id,
					ne_type?.net_id,
					ne_subtype?.nes_id,
					ne_entity?.ent_id
				])
		ne_id = (long)res[0][0]
		getDBTable().neKeyCache[getKey()] = this
		log.info "Inserted new NE in DB: ${this}"
		return ne_id
	}

	public int removeThisFromDB() {
		if (!ne_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM  ${getDBTable().tablename} WHERE ne_id=?", [ne_id])
		getDBTable().neKeyCache.remove(getKey())
		log.info "Removed NE ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return ""+ne_id+":"+ne_name
	}
}
