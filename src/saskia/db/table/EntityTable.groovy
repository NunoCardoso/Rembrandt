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

import org.apache.log4j.Logger

import saskia.bin.Configuration
import saskia.db.database.SaskiaDB
import saskia.db.obj.Entity
import saskia.dbpedia.DBpediaResource

/** This class is an interface for the Entity table in the Saskia database. 
 * It stores information that grounds an entity.
 * Static methods are used to return results from DB, using where clauses.
 * Class methods are used to insert results to DB.  
 */
class EntityTable extends DBTable {

	String ent_has_geo_table = "entity_has_geoscope"
	static String tablename = "entity"

	static Logger log = Logger.getLogger("Entity")

	Configuration conf

	LinkedHashMap<String,EntityTable> entityDBPediaResourceCache
	LinkedHashMap<Long,EntityTable> entityIDCache


	public EntityTable(SaskiaDB db) {
		super(db)
		conf = Configuration.newInstance()
		entityDBPediaResourceCache =new LinkedHashMap(
				conf.getInt("saskia.entity.cache.number",1000), 0.75f, true) // true: access order.
		entityIDCache = new LinkedHashMap(
				conf.getInt("saskia.entity.cache.number",1000), 0.75f, true) // true: access order.
	}

	public List<Entity> queryDB(String query, ArrayList params = []) {
		List<Entity> res = []
		getSaskiaDB().getDB().eachRow(query, params, {row ->
			res << Entity.createNew(this, row)
		})
		return res
	}


	public Map listEntities(limit = 10, offset = 0, column = null, needle = null) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		String where = ""
		String from = " FROM ${tablename}"
		List params = []
		if (column && needle) {
			switch (type[column]) {
				case 'String': where += " WHERE $column LIKE '%${needle}%'"; break
				case 'Long': where += " WHERE $column=? "; params << Long.parseLong(needle); break
			}
		}

		String query = "SELECT SQL_CALC_FOUND_ROWS ${tablename}.* $from $where LIMIT ${limit} OFFSET ${offset} "+
				"UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), NULL, NULL, NULL"

		List<Entity> u
		try {u = queryDB(query, params) }
		catch(Exception e) {log.error "Error getting Entity list: ", e}
		// last "item" it's the count.
		int total = (int)(u.pop().ent_id)
		log.debug "Returning "+u.size()+" results."
		return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
			"column":column, "value":needle]
	}

	/** Get an entity from id. 
	 * @param ent_id The id as needle.
	 * return the Entity for that id.
	 */
	public Entity getFromID(Long ent_id) {
		if (!ent_id) return null
		if (entityIDCache.containsKey(ent_id)) return entityIDCache[ent_id]

		List<Entity> e = queryDB("SELECT * FROM ${tablename} WHERE ent_id=?", [ent_id])
		log.trace "Querying for ent_id $ent_id got Entity $e."
		if (e) {
			entityIDCache[ent_id] = e[0]
			return e[0]
		}
		return null
	}


	static Entity getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("EntityTable").getFromID(id)
	}

	/** Get from Wikipedia URL. 
	 * @param ent_wikipedia_page The full wikipedia URL.
	 * return the Entity associated to that URL
	 */

	public List<Entity> getFromName(String ent_name, String lang) {
		if (!ent_name || !lang) return null
		String needle = "${lang}:${ent_name}"
		List<Entity> ents = queryDB("SELECT * FROM ${tablename} WHERE ent_name REGEXP '^(.*;)?${needle}(;.*)?\$'",[])
		log.info "Querying for ent_name $needle got Entity $ents."
		return ents
	}

	/** Get from DBpedia resource. 
	 * This USES cache
	 * @param ent_dbpedia_resource The DBpedia resource.
	 * return the Entity associated to that resource.
	 */
	public Entity getFromDBpediaResource(String ent_dbpedia_resource) {
		// dbpedia_url can be null
		List<Entity> e

		if (entityDBPediaResourceCache.containsKey(ent_dbpedia_resource))
			return entityDBPediaResourceCache[ent_dbpedia_resource]

		if (!ent_dbpedia_resource) {
			e = queryDB("SELECT * FROM ${tablename} WHERE ent_dbpedia_resource IS NULL")
		}else  {
			ent_dbpedia_resource = DBpediaResource.getShortName(ent_dbpedia_resource)
			e = queryDB("SELECT * FROM ${tablename} WHERE ent_dbpedia_resource = ?", [ent_dbpedia_resource])
		}

		log.debug "Querying for ent_dbpedia_resource $ent_dbpedia_resource got Entity $e."
		if (e) {
			entityDBPediaResourceCache[ent_dbpedia_resource] = e[0]
			return e[0]
		}
		return null

	}

	/** Get from DBpedia class. 
	 * @param dbpedia_url The DBpedia class.
	 * return the Entity associated to that resource.
	 */
	public List<Entity> getFromDBpediaClass(String ent_dbpedia_class) {
		// dbpedia_url can be null
		List<Entity> e
		if (!ent_dbpedia_class)
			e = queryDB("SELECT * FROM ${tablename} WHERE ent_dbpedia_class IS NULL")
		else
			e = queryDB("SELECT * FROM ${tablename} WHERE ent_dbpedia_class = ?", [ent_dbpedia_class])
		log.trace "Querying for ent_dbpedia_class $ent_dbpedia_class got Entity $e."
		if (e) return e else return null
	}

}