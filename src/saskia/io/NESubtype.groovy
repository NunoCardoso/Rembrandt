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

/** This class is an interface for the NEsubtype table in the WikiRembrandt database. 
  * It stores tagging information associated to a NE subtype.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  */
class NESubtype  extends DBObject implements JSONable {

	static String tablename = "ne_subtype"
	static Map<Long,String> all_id_subtype = [:]
	static Map<String,Long> all_subtype_id = [:]
    
	long nes_id
	String nes_subtype
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("NESubtype")

	static List<NESubtype> queryDB(String query, List params = []) {
	    List<NESubtype> s = []
	    db.getDB().eachRow(query, params, {row  -> 
	    s << new NESubtype(nes_id:row['nes_id'], nes_subtype:row['nes_subtype'] )
	    })
	    return s
       }
	
	public Map toMap() {
	    return ["nes_id":nes_id, "nes_subtype":nes_subtype]
	}
	
	public Map toSimpleMap() {
	    return toMap()
	}	
		/** Get all NE categories. It's easier to have them in memory, than hammering the DB 
	 * return List<NECategory> A list of NECategory objects
	 */
	static void createCache() {
	    if (all_id_subtype.isEmpty()) {
               def nes = queryDB("SELECT * FROM ${tablename}")
		log.debug "Searched for all subtypes, got ${nes.size()} entries."
		nes.each{ updateCacheElement( it.nes_id, it.nes_subtype)}     
                
	    }
	}
              
        static updateCacheElement(long id, String subtype) {
            if (!id || !subtype) return
            NESubtype nes = new NESubtype(nes_id:id, nes_subtype:subtype) 
            all_subtype_id[subtype] = nes
            all_id_subtype[id] = nes
        }
	
	/** Get a NESubtype from id.
	 * @param id The id as needle.
	 * return the NESubtype result object, or null
	 */
	static NESubtype getFromID(long nes_id) {
	    if (!nes_id) return null
	    createCache()
	    return all_id_subtype[nes_id]
	}
      
        /** Get a NESubtype from id.
         * @param id The id as needle.
         * return the NESubtype result object, or null
         */
        static NESubtype getFromSubtype(String nes_subtype) {
            if (!nes_subtype) return null
            createCache()
            return all_subtype_id[nes_subtype]
        }
            
           //	NESubtype nes = queryDB("SELECT * FROM ${tablename} WHERE nes_id=?", [nes_id])?.getAt(0)
	//	log.debug "Querying for nes_id $nes_id got NESubtype $nes." 
	//	if (nes.nes_id) return nes else return null
		

	public Long addThisToDB() {
	    def res = db.getDB().executeInsert("INSERT IGNORE INTO ${tablename} VALUES(0,?)", [nes_subtype])
		// returns an auto_increment value	
		if (res) {
			nes_id = (long)res[0][0]
			updateCacheElement(nes_id, nes_subtype)
			log.info "Inserted new NESubtype in DB: ${this}"
		}
		return nes_id
	}	
		
	public int removeThisFromDB() {
		if (!nes_id) return null
		def res = db.getDB().executeUpdate("DELETE FROM ${tablename} WHERE nes_id=?", [nes_id])	
		all_subtype_id.remove(nes_subtype)
		all_id_subtype.remove(nes_id)
		log.info "Removed NESubtype ${this} from DB, got $res"
		return res	    
	}
	
	
	boolean equals(EntityTable e) {
		return this.toMap().equals(e.toMap())
	}
    
	public String toString() {
		return nes_subtype
	}
}