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

	static NE createFromDBRown(DBTable dbtable, row) {
		NE n = new NE(dbtable)
		n.ne_id = row['ne_id']
		if (row['ne_name']) n.ne_name = NEName.getFromID(dbtable, row['ne_name'])
		n.ne_lang = row['ne_lang']
		if (row['ne_category']) n.ne_category = NECategory.getFromID(dbtable, row['ne_category'])
		if (row['ne_type']) n.ne_type = NEType.getFromID(dbtable, row['ne_type'])
		if (row['ne_subtype']) n.ne_subtype = NESubtype.getFromID(dbtable, row['ne_subtype'])
		if (row['ne_entity']) n.ne_entity = EntityTable.getFromID(dbtable, row['ne_entity'])
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


	public updateNEName(NEName new_ne_name) {

		if (!new_ne_name || !ne_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${tablename} SET ne_name=? where ne_id=?",[new_ne_name.nen_id, ne_id])

		if (res) {
			ne_name = new_ne_name
			String key = getKey()
			if (neKeyCache.containsKey(key)) neKeyCache[key].ne_name = new_ne_name
		}
		return res
	}

	public updateNECategory(NECategory new_ne_category) {
		if (!ne_id) return null
		// new_ne_category can be null
		def res =getDBTable().getSaskiaDB().executeUpdate(
				"UPDATE ${getDBTable().getTablename()} SET ne_category=? where ne_id=?",
				[
					new_ne_category?.nec_id,
					ne_id
				])
		if (res) {
			ne_category = new_ne_category
			String key = getKey()
			if (getDBTable().neKeyCache.containsKey(key))
				getDBTable().neKeyCache[key].ne_category = new_ne_category

		}
		return res
	}

	public updateNEType(NECategory new_ne_type) {
		if (!ne_id) return null
		// new_ne_category can be null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().getTablename()} SET ne_type=? where ne_id=?",
				[new_ne_type?.net_id, ne_id])
		if (res) {
			ne_type = new_ne_type
			String key = getKey()
			if (getDBTable().neKeyCache.containsKey(key))
				getDBTable().neKeyCache[key].ne_type = new_ne_type

		}
		return res
	}

	public updateNESubtype(NECategory new_ne_subtype) {
		if (!ne_id) return null
		// new_ne_category can be null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().getTablename()} SET ne_subtype=? where ne_id=?",
				[
					new_ne_subtype?.nes_id,
					ne_id
				])
		if (res) {
			ne_subtype = new_ne_subtype
			String key = getKey()
			if (getDBTable().neKeyCache.containsKey(key))
				getDBTable().neKeyCache[key].ne_subtype = new_ne_subtype

		}
		return res
	}

	public updateEntity(Entity new_entity) {
		if (!ne_id) return null
		// new_entity can be null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().getTablename()} SET ne_entity=? where ne_id=?",
				[new_entity?.ent_id, ne_id])
		if (res) {
			ne_entity = new_entity
			String key = getKey()
			if (getDBTable().neKeyCache.containsKey(key))
				getDBTable().neKeyCache[key].ne_entity = new_entity

		}
		return res
	}

	public Long addThisToDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().getTablename()}(ne_id, ne_name, ne_lang, ne_category, "+
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
				"DELETE FROM  ${getDBTable().getTablename()} WHERE ne_id=?", [ne_id])
		getDBTable().neKeyCache.remove(getKey())
		log.info "Removed NE ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return ""+ne_id+":"+ne_name
	}
}
