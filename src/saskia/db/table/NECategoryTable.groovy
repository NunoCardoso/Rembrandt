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
import saskia.db.obj.NECategory

/** This class is an interface for the NECategory table in the WikiRembrandt database. 
 * It stores tagging information associated to a NE category.
 * Static methods are used to return results from DB, using where clauses.
 * Class methods are used to insert results to DB.  
 */
class NECategoryTable extends DBTable {

	static String tablename = "ne_category"
	static NECategoryTable _this
	static Map<String,String> local_lang = ["pt":"LOCAL","en":"PLACE","rembrandt":"@LOCAL"]

	Map<Long,NECategory> all_id_category
	Map<String,NECategory> all_category_id

	static Logger log = Logger.getLogger("NECategory")

	public NECategoryTable(SaskiaDB db) {
		super(db)
		all_id_category = [:]
		all_category_id = [:]
	}

	public int getIDforLOCAL(String lang) {
		if (!lang) return
		createCache()
		if (!all_category_id.containsKey(local_lang[lang])) {
			// there's noting in DB, let's create it.
			NECategory nec = NECategory.createNew(this, [nec_category:local_lang[lang]])
			nec.nec_id = nec.addThisToDB()
			updateCacheElement(nec)
		}
		return (int)all_category_id[local_lang[lang]].nec_id
	}

	public List<NECategory> queryDB(String query, ArrayList params = []) {
		List<NECategory> t = []
		db.getDB().eachRow(query, params, {row  ->
			t << NECategory.createNew(this,row)
		})
		return t
	}

	/** Get all NE categories. It's easier to have them in memory, than hammering the DB 
	 * return List<NECategory> A list of NECategory objects
	 */
	public void createCache() {
		if (all_id_category.isEmpty()) {
			def nec = queryDB("SELECT * FROM ${tablename}")
			log.debug "Searched for all categories, got ${nec.size()} entries."
			nec.each{ updateCacheElement(it)}
		}
	}

	public updateCacheElement(NECategory nec) {
		if (!nec.nec_id || !nec.nec_category) return
		all_category_id[nec.nec_category] = nec
		all_id_category[nec.nec_id] = nec
	}

	/** Get a NECategory from id.
	 * @param id The id as needle.
	 * return the NECategory result object, or null
	 */
	public NECategory getFromID(Long nec_id) {
		if (!nec_id) return null
		createCache()
		return all_id_category[nec_id]
		//NECategory nec = queryDB("SELECT * FROM ${tablename} WHERE nec_id=?", [nec_id])?.getAt(0)
		//log.debug "Querying for nec_id $nec_id got NECategory $nec."
		//if (nec.nec_id) return nec else return null
	}

	static NECategory getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("NECategoryTable").getFromID(id)
	}

	/** Get a NECategory from id.
	 * @param id The id as needle.
	 * return the NECategory result object, or null
	 */
	public NECategory getFromCategory(String nec_category) {
		if (!nec_category) return null
		createCache()
		return all_category_id[nec_category]
		//NECategory nec = queryDB("SELECT * FROM ${tablename} WHERE nec_category=?", [nec_category])
		//log.debug "Querying for nec_category $nec_category got NECategory $nec."
		//return nec
	}

	static NECategory getFromCategory(SaskiaDB db, Long id) {
		return  db.getDBTable("NECategoryTable").getFromCategory(id)
	}
}