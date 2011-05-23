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
import saskia.db.obj.Cache
import saskia.db.obj.Collection
import saskia.stats.SaskiaStats

/** This class is an interface for the NEName table in the WikiRembrandt database. 
 * It stores tagging information associated to a NE name.
 * Static methods are used to return results from DB, using where clauses.
 * Class methods are used to insert results to DB.  
 */
class CacheTable extends DBTable {

	static Logger log = Logger.getLogger("Cache")
	static String tablename = "cache"

	public CacheTable(SaskiaDB db) {
		super(db)
	}

	public List<Cache> queryDB(String query, ArrayList params = []) {
		List<Cache> list = []
		getSaskiaDB().getDB().eachRow(query, params, {row  ->
			list << Cache.createNew(this, row)
		})
		return list
	}

	/** Get a Cache from id, collection, lang
	 * @param cac_id The id
	 * @param cac_collection The collection
	 * @param cac_lang The language
	 * @return the cache object
	 */
	public Cache getFromIDAndCollectionAndLang(String cac_id, Collection cac_collection, String cac_lang) {
		if (!cac_id || !cac_collection || !cac_lang) return null
		List<Cache> list = queryDB("SELECT * FROM ${tablename} WHERE cac_id=? and cac_collection=? and cac_lang=?",
				[
					cac_id,
					cac_collection.col_id,
					cac_lang
				])
		return (list ? list[0] : null)
	}

	public HashMap getFrontPageCacheDates(Collection collection) {
		HashMap res = [:]
		getSaskiaDB().getDB().eachRow("SELECT cac_lang, cac_date FROM ${tablename} WHERE cac_id=? AND "+
				"cac_collection=?",  [
					SaskiaStats.statsFrontPage,
					collection.col_id
				], { row ->
					res[ row['cac_lang'] ] = (Date)row['cac_date']
				} )
		return res
	}

	void refreshCache(String cac_id, Collection cac_collection, String cac_lang, String cac_obj, long howmuch) {
		Cache c = new Cache(
				cac_id:cac_id, cac_collection:cac_collection,
				cac_lang:cac_lang, cac_obj:cac_obj
				)
		c.cac_expire = new Date( (new Date().getTime()+howmuch) )
		c.addThisToDB()
		log.info "Cache successfully refreshened: ${c}"
	}
}