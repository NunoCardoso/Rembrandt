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
import saskia.db.database.*
import saskia.db.table.*

/**
 * @author Nuno Cardoso
 *
 */
class Entity extends DBObject implements JSONable {

	Long ent_id
	String ent_name
	String ent_dbpedia_resource
	String ent_dbpedia_class

	static Logger log = Logger.getLogger("Entity")
	
	static Map type = ['ent_id':'Long', 'ent_name':'String',
		'ent_dbpedia_resource':'String', 'ent_dbpedia_class':'String']
	
	public Entity(DBTable dbtable) {
		super(dbtable)
	}
	
	
	static Entity createNew(DBTable dbtable, row) {
	   Entity e = new Entity(dbtable)
		if (row['ent_id']) e.ent_id = row['ent_id']
		if (row['ent_name']) e.ent_name = row['ent_name']
		if (row['ent_dbpedia_resource']) e.ent_dbpedia_resource = row['ent_dbpedia_resource']
		if (row['ent_dbpedia_class']) e.ent_dbpedia_class=row['ent_dbpedia_class']
		return e
	}
	public Map toMap() {
		return ["ent_id":ent_id, "ent_name":ent_name, "ent_dbpedia_resource":ent_dbpedia_resource,
			"ent_dbpedia_class":ent_dbpedia_class]
	}
	
	public Map toSimpleMap() {
		return toMap()
	}

	public Map getName() {
		if (!ent_name) return null
		Map m = [:]
		ent_name.split(/;/).each{it ->
			List l = it.split(/:/)
			m[l[0]]=l[1]
		}
		return m
	}
	
	public updateValue(column, value) {
		return getDBTable().updateValue(ent_id, column, value);
	}


	public Geoscope hasGeoscope() {
		Geoscope g
		getDBTable().getSaskiaDB().getDB().eachRow(
			"SELECT ${GeoscopeTable.tablename}.* FROM ${GeoscopeTable.tablename}, ${getDBTable().ent_has_geo_table} "+
			"WHERE ehg_entity = ? and ehg_geoscope = geo_id ", [ent_id], {row ->
			if (g) log.warn "Entity ${this} has more than one link to a geoscope!!!"
			else g = Geoscope.createNew(row)
		})
		return g
	}

	public associateWithGeoscope(Long geo_id) {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
			"INSERT IGNORE INTO ${getDBTable().ent_has_geo_table} VALUES(?,?)",
		[ent_id, geo_id])
		return res
	}

	/** Add this Entity to the database. Note that a null is a valid insertion...
	 * return 1 if successfully inserted.
	 */
	public Long addThisToDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
			"INSERT INTO ${getDBTable().tablename} VALUES(0,?,?,?)",
				[ent_name, ent_dbpedia_resource, ent_dbpedia_class] )

		ent_id = (long)res[0][0]
		getDBTable().entityDBPediaResourceCache[ent_dbpedia_resource] = this
		getDBTable().entityIDCache[ent_id] = this
		log.info "Adding entity to DB: ${this}"
		return ent_id
	}
	
	public int removeThisFromDB() {
		if (!ent_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
			"DELETE FROM ${getDBTable().tablename} WHERE ent_id=?", 
			[ent_id])
		getDBTable().entityDBPediaResourceCache.remove(ent_dbpedia_resource)
		getDBTable().entityIDCache.remove(ent_id)
		log.info "Removing entity ${this} from DB, got $res"
		return res
	}

	boolean equals(Entity e) {
		return this.toMap().equals(e.toMap())
	}

	public String toString() {
		return "${ent_id}:${ent_dbpedia_resource}:${ent_dbpedia_class}"
	}

}
