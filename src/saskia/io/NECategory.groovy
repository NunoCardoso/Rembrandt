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

import saskia.db.obj.DBObject;
import saskia.db.obj.JSONable;
import saskia.db.table.EntityTable;

/** This class is an interface for the NECategory table in the WikiRembrandt database. 
  * It stores tagging information associated to a NE category.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  */
class NECategory extends DBObject implements JSONable {

	static String tablename = "ne_category"
	     
	static Map<String,String> local_lang = ["pt":"LOCAL","en":"PLACE","rembrandt":"@LOCAL"]
	static Map<Long,NECategory> all_id_category = [:]
	static Map<String,NECategory> all_category_id = [:]
	                                        
	long nec_id
	String nec_category
    
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("NECategory")

	static int getIDforLOCAL(String lang) {
	    if (!lang) return
	    createCache()	   
	    if (!all_category_id.containsKey(local_lang[lang])) {
		// there's noting in DB, let's create it.
		NECategory nec = new NECategory()
		def id = nec.addThisToDB()
		updateCacheElement(id, local_lang[lang])
	    }
	    return (int)all_category_id[local_lang[lang]].nec_id
	}
    
	static List<NECategory> queryDB(String query, ArrayList params = []) {
	    List<NECategory> t = []
	    db.getDB().eachRow(query, params, {row  -> 
	        t << new NECategory(nec_id:row['nec_id'], nec_category:row['nec_category'] )
	    })
	    return t
	}
	
	/** Get all NE categories. It's easier to have them in memory, than hammering the DB 
	 * return List<NECategory> A list of NECategory objects
	 */
	static void createCache() {
	    if (all_id_category.isEmpty()) {
               def nec = queryDB("SELECT * FROM ${tablename}")
		log.debug "Searched for all categories, got ${nec.size()} entries."
		nec.each{ updateCacheElement( it.nec_id, it.nec_category)}               
	    }
	}
              
        static updateCacheElement(long id, String category) {
            if (!id || !category) return
            NECategory nec = new NECategory(nec_id:id, nec_category:category)
            all_category_id[category] = nec
            all_id_category[id] = nec
        }

	public Map toMap() {
	    return ["nec_id":nec_id, "nec_category":nec_category]
	}

	public Map toSimpleMap() {
	    return toMap()
	}
			
	/** Get a NECategory from id.
	 * @param id The id as needle.
	 * return the NECategory result object, or null
	 */
	static NECategory getFromID(long nec_id) {
		if (!nec_id) return null
		createCache()
		return all_id_category[nec_id]
		//NECategory nec = queryDB("SELECT * FROM ${tablename} WHERE nec_id=?", [nec_id])?.getAt(0)
		//log.debug "Querying for nec_id $nec_id got NECategory $nec." 
		//if (nec.nec_id) return nec else return null
	}	
    
        /** Get a NECategory from id.
         * @param id The id as needle.
         * return the NECategory result object, or null
         */
        static NECategory getFromCategory(String nec_category) {
            if (!nec_category) return null
            createCache()
            return all_category_id[nec_category]
            //NECategory nec = queryDB("SELECT * FROM ${tablename} WHERE nec_category=?", [nec_category])
            //log.debug "Querying for nec_category $nec_category got NECategory $nec." 
            //return nec
        }	
    
	/** Add this NECategory o the database. Note that a null is a valid insertion...
	 * return the id  if either successfully inserted or ignored.
	 */	
	public Long addThisToDB() {
		def res = db.getDB().executeInsert("INSERT IGNORE INTO ${tablename} VALUES(0,?)", [nec_category])
		// returns an auto_increment value	
		if (res) {
			nec_id = (long)res[0][0]
			updateCacheElement(nec_id, nec_category)
			log.info "Inserted new NECategory in DB: ${this}"
		}
		return nec_id
	}	
		
	public int removeThisFromDB() {
		if (!nec_id) return null
		def res = db.getDB().executeUpdate("DELETE FROM ${tablename} WHERE nec_id=?", [nec_id])	
		all_category_id.remove(nec_category)
		all_id_category.remove(nec_id)
		log.info "Removed NECategory ${this} from DB, got $res"
		return res	    
	}
      
	boolean equals(EntityTable e) {
		return this.toMap().equals(e.toMap())
	}
     
	public String toString() {
		return nec_category
	}
}