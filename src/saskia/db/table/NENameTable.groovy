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

import org.apache.log4j.*

import saskia.bin.Configuration
import saskia.db.database.SaskiaDB
import saskia.db.obj.Geoscope
import saskia.db.obj.NEName

/** This class is an interface for the NEName table in the WikiRembrandt database. 
 * It stores tagging information associated to a NE name.
 * Static methods are used to return results from DB, using where clauses.
 * Class methods are used to insert results to DB.  
 */
class NENameTable extends DBTable {

	static String tablename = "ne_name"

	Configuration conf

	static Logger log = Logger.getLogger("NEName")

	LinkedHashMap<Long,Geoscope> idCache
	LinkedHashMap<Long,Geoscope> nameCache

	public NENameTable(SaskiaDB db) {
		super(db)
		conf = Configuration.newInstance()
		idCache = new LinkedHashMap(
				conf.getInt("saskia.nename.cache.number",1000), 0.75f, true) // true: access order.
		nameCache = new LinkedHashMap(
				conf.getInt("saskia.nename.cache.number",1000), 0.75f, true) // true: access order.

	}
	/** 
	 * DB query method 
	 */
	public List<NEName> queryDB(String query, ArrayList params = []) {
		List<NEName> res = []
		db.getDB().eachRow(query, params, {row  ->
			res << NEName.createFronDBRow(this.owner,row)
		})
		return res
	}


	/** Get a NEName from id.
	 * @param id The id as needle.
	 * return the NEName result object, or null
	 */
	public NEName getFromID(Long nen_id) {
		if (!nen_id) return null
		// if from cache, return
		if (idCache.containsKey(nen_id)) return idCache[nen_id]

		List<NEName> nen = queryDB("SELECT * FROM ${tablename} WHERE nen_id=?", [nen_id])
		log.debug "Querying for nen_id $nen_id got NEName $nen."
		if (nen) {
			idCache[nen.nen_id] = nen[0]
			nameCache[nen.nen_name] = nen[0]
			return nen[0]
		}
		return null
	}

	static NEName getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("saskia.db.table.NENameTable").getFromID(id)
	}

	/** Get a NEName from id.
	 * @param id The id as needle.
	 * return the NEName result object, or null
	 */
	public NEName getFromName(String nen_name) {
		if (!nen_name) return null
		// if from cache, return
		if (nameCache.containsKey(nen_name)) return nameCache[nen_name]

		List<NEName> nen = queryDB("SELECT * FROM ${tablename} WHERE nen_name=?", [nen_name])
		log.debug "Querying for nen_name '${nen_name}' got NEName $nen."
		if (nen) {
			idCache[nen.nen_id] = nen[0]
			nameCache[nen.nen_name] = nen[0]
			return nen[0]
		}
		return null
	}

	static NEName getFromName(SaskiaDB db, Long id) {
		return  db.getDBTable("saskia.db.table.NENameTable").getFromName(id)
	}

}