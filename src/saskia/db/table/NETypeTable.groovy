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

import saskia.db.database.SaskiaDB
import saskia.db.obj.NEType

/** This class is an interface for the NEType table in the WikiRembrandt database. 
 * It stores tagging information associated to a NE Type.
 * Static methods are used to return results from DB, using where clauses.
 * Class methods are used to insert results to DB.  
 */
class NETypeTable extends DBTable {

	static String tablename = "ne_type"
	Map<Long,String> all_id_type
	Map<String,Long> all_type_id

	static Logger log = Logger.getLogger("NEType")

	public NETypeTable(SaskiaDB db) {
		super(db)
		all_id_type = [:]
		all_type_id = [:]
	}


	public List<NEType> queryDB(String query, ArrayList params = []) {
		List<NEType> t = []
		db.getDB().eachRow(query, params, {row  ->
			t << NEType.createNew(this, row)
		})
		return t
	}


	/** Get all NE categories. It's easier to have them in memory, than hammering the DB 
	 * return List<NECategory> A list of NECategory objects
	 */
	public void createCache() {
		if (all_id_type.isEmpty()) {
			def net = queryDB("SELECT * FROM ${tablename}")
			log.debug "Searched for all types, got ${net.size()} entries."
			net.each{updateCacheElement(it)}
		}
	}

	public updateCacheElement(NEType net) {
		if (!net.net_id || !net.net_type) return
		all_type_id[net.net_type] = net
		all_id_type[net.net_id] = net
	}

	/** Get a NEType from id.
	 * @param id The id as needle.
	 * return the NEType result object, or null
	 */
	public NEType getFromID(long net_id) {
		if (!net_id) return null
		createCache()
		return all_id_type[net_id]
		//NEType net = queryDB("SELECT * FROM ${tablename} WHERE net_id=?", [net_id])?.getAt(0)
		//log.debug "Querying for net_id $net_id got NEType $net."
		//if (net.net_id) return net else return null
	}

	static NEType getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("NETypeTable").getFromID(id)
	}

	/** Get a NESubtype from id.
	 * @param id The id as needle.
	 * return the NESubtype result object, or null
	 */
	public NEType getFromType(String net_type) {
		if (!net_type) return null
		createCache()
		return all_type_id[net_type]
	}

	static NEType getFromType(SaskiaDB db, Long id) {
		return  db.getDBTable("NETypeTable").getFromType(id)
	}
}