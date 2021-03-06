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
import saskia.db.obj.NESubtype

/** This class is an interface for the NEsubtype table in the WikiRembrandt database. 
 * It stores tagging information associated to a NE subtype.
 * Static methods are used to return results from DB, using where clauses.
 * Class methods are used to insert results to DB.  
 */
class NESubtypeTable  extends DBTable {

	static String tablename = "ne_subtype"
	Map<Long,String> all_id_subtype
	Map<String,Long> all_subtype_id

	static Logger log = Logger.getLogger("NESubtype")

	public NESubtypeTable(SaskiaDB db) {
		super(db)
		all_id_subtype = [:]
		all_subtype_id = [:]
	}

	public List<NESubtype> queryDB(String query, ArrayList params = []) {
		List<NESubtype> s = []
		db.getDB().eachRow(query, params, {row  ->
			s << NESubtype.createNew(this, row)
		})
		return s
	}


	/** Get all NE categories. It's easier to have them in memory, than hammering the DB 
	 * return List<NECategory> A list of NECategory objects
	 */
	public void createCache() {
		if (all_id_subtype.isEmpty()) {
			def nes = queryDB("SELECT * FROM ${tablename}")
			log.debug "Searched for all subtypes, got ${nes.size()} entries."
			nes.each{ updateCacheElement(it)}
		}
	}

	public updateCacheElement(NESubtype nes) {
		if (!nes.nes_id || !nes.nes_subtype) return
		all_subtype_id[nes.nes_subtype] = nes
		all_id_subtype[nes.nes_id] = nes
	}

	/** Get a NESubtype from id.
	 * @param id The id as needle.
	 * return the NESubtype result object, or null
	 */
	public NESubtype getFromID(long nes_id) {
		if (!nes_id) return null
		createCache()
		return all_id_subtype[nes_id]
	}

	static NESubtype getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("NESubtypeTable").getFromID(id)
	}

	/** Get a NESubtype from id.
	 * @param id The id as needle.
	 * return the NESubtype result object, or null
	 */
	public NESubtype getFromSubtype(String nes_subtype) {
		if (!nes_subtype) return null
		createCache()
		return all_subtype_id[nes_subtype]
	}

	static NESubtype getFromSubtype(SaskiaDB db, Long id) {
		return  db.getDBTable("NESubtypeTable").getFromSubtype(id)
	}
}