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
package saskia.io

import org.apache.log4j.*
import saskia.io.Collection
import saskia.stats.SaskiaStats

/** This class is an interface for the NEName table in the WikiRembrandt database. 
  * It stores tagging information associated to a NE name.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  */
class Cache {

	static String cac_table = "cache"
	String cac_id
	Collection cac_collection
	String cac_lang
	Date cac_date
	Date cac_expire
	def cac_obj
	
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("SaskiaDB")

	static Cache queryDB(String query, ArrayList params = []) {
	    Cache  c = new Cache()
	    db.getDB().eachRow(query, params, {row  -> 
	        c.cac_id = row['cac_id']
		c.cac_collection = Collection.getFromID(row['cac_collection'])
		c.cac_lang = row['cac_lang']
		c.cac_date = (Date)row['cac_date']
		c.cac_expire = (Date)row['cac_expire']
		java.sql.Blob blob = row.getBlob('cac_obj')
		byte[] bdata = blob.getBytes(1, (int) blob.length())
		 // you have to say explicitly that mediawiki's mediumblob is in UTF-8
		c.cac_obj = new String(bdata, "UTF-8")
	    })
	    return (c.cac_id ? c : null)
	}
	
	boolean isCacheFresh() {
	   // log.debug "cac_expire: "+(long)(cac_expire.getTime())+" d=$d"
	    return cac_expire.getTime() > new Date().getTime()
	}
	
	
	static HashMap getFrontPageCacheDates(Collection collection) {
	    HashMap res = [:]
	    db.getDB().eachRow("SELECT cac_lang, cac_date FROM ${cac_table} WHERE cac_id=? AND "+
		    "cac_collection=?",  [SaskiaStats.statsFrontPage, collection.col_id], { row -> 
		    	res[ row['cac_lang'] ] = (Date)row['cac_date'] })
	    return res	
	}
	
	void refreshCache(String cac_id, Collection cac_collection, String cac_lang, String cac_obj, long howmuch) {
	    Date cac_expire = new Date( (new Date().getTime()+howmuch))
	    db.getDB().executeInsert("INSERT INTO cache(cac_id, cac_collection, cac_date, cac_expire, cac_lang, cac_obj) "+
	 		"VALUES(?,?, NOW(),?, ?, ?) ON DUPLICATE KEY UPDATE cac_date=NOW(), cac_expire=?, cac_obj=?", 
	 			[cac_id, cac_collection.col_id, cac_expire, cac_lang, cac_obj, cac_expire, cac_obj])
	
	}
	
	
	/** Get a NEName from id.
	 * @param id The id as needle.
	 * return the NEName result object, or null
	 */
	static Cache getFromIDAndCollectionAndLang(String cac_id, Collection cac_collection, String cac_lang) {
		if (!cac_id || !cac_collection || !cac_lang) return null
		Cache c = queryDB("SELECT * FROM ${cac_table} WHERE cac_id=? and cac_collection=? and cac_lang=?",
			[cac_id, cac_collection.col_id, cac_lang])
		 return c // can be null
	}		
	
	public String toString() {
		return ""+cac_id+":"+cac_collection+":"+cac_lang
	}
}