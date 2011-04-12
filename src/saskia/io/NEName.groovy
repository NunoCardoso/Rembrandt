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
import saskia.bin.Configuration

/** This class is an interface for the NEName table in the WikiRembrandt database. 
  * It stores tagging information associated to a NE name.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  */
class NEName extends DBObject implements JSONable {

	static String tablename = "ne_name"
	long nen_id
	String nen_name
	int nen_nr_terms
    
	static Configuration conf = Configuration.newInstance()
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("NEName")
	
	static LinkedHashMap<Long,Geoscope> idCache = \
           new LinkedHashMap(conf.getInt("saskia.nename.cache.number",1000), 0.75f, true) // true: access order.  
	static LinkedHashMap<Long,Geoscope> nameCache = \
           new LinkedHashMap(conf.getInt("saskia.nename.cache.number",1000), 0.75f, true) // true: access order.  

	/** 
	 * DB query method 
	 */
	static List<NEName> queryDB(String query, ArrayList params = []) {
	    List<NEName> res = []
	    NEName n
    	    db.getDB().eachRow(query, params, {row  -> 
    	        n = new NEName()
    	        n.nen_id = row['nen_id']
    	        n.nen_name = row['nen_name']
    	        n.nen_nr_terms = (int)(row['nen_nr_terms'])
                res << n
    	    })
    	    return (res ? res : null)
	}
	
	
	/** Get a NEName from id.
	 * @param id The id as needle.
	 * return the NEName result object, or null
	 */
	static NEName getFromID(long nen_id) {
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
	
	/** Get a NEName from id.
	 * @param id The id as needle.
	 * return the NEName result object, or null
	 */
	static NEName getFromName(String nen_name) {
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
	
	public Map toMap() {
	   return ["nen_id":nen_id, "nen_name":nen_name]
	}
	
		
	public Map toSimpleMap() {
	   return toMap()
	}
	
	/** Add this NEName to the database. Note that a null is a valid insertion...
	 * return 1 if successfully inserted.
	 */	
	public Long addThisToDB() {
	    // returns an auto_increment value
            def res = db.getDB().executeInsert("INSERT INTO ${tablename} VALUES(0,?,?)", 
            [nen_name, nen_nr_terms])
            nen_id = (long)res[0][0]
            idCache[nen_id] = this
            nameCache[nen_name] = this
				log.info "Inserted new NEName in DB: ${this}"
            return nen_id 
	}	

	public int removeThisFromDB() {
		if (!nen_id) return null
		def res = db.getDB().executeUpdate("DELETE FROM ${tablename} WHERE nen_id=?", [nen_id])	
		idCache.remove(nen_id)
		nameCache.remove(nen_name)
		log.info "Removed NEName ${this} from DB, got $res"
		return res	    
	}
		
	public String toString() {
	    return nen_name
	}
}