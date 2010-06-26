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

/** This class is an interface for the NEsubtype table in the WikiRembrandt database. 
  * It stores tagging information associated to a NE subtype.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  */
class Relation {

	static String rel_table = "relation"
	static String default_relation = "sameAs"
	long rel_id
	String rel_relation
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("SaskiaDB")

	static List<Relation> queryDB(String query, ArrayList params = []) {
		List<Relation> t = []
		db.getDB().eachRow(query, params, {row  -> 
			t << new Relation(rel_id:row['rel_id'], rel_relation:row['rel_relation'] )
		})
		return t
	}
	
	/** Get all relations. It's easier to have them in memory, than hammering the DB 
	 * return List<Relation> A list of Relation objects
	 */
	static Map getAllRelations() {
		def map = [:]
		def r = queryDB("SELECT * FROM ${rel_table}")
		log.debug "Searched for all relations, got ${r.size()} entries."
		r.each{map[it.rel_relation] = it.rel_id}
		return map
	}
	
	/** Get a Relation from id.
	 * @param id The id as needle.
	 * return the Relation result object, or null
	 */
	static Relation getFromID(long rel_id) {
		if (!rel_id) return null
		Relation r = queryDB("SELECT * FROM ${rel_table} WHERE rel_id=?", [rel_id])?.getAt(0)
		log.debug "Querying for rel_id $rel_id got Relation $r." 
		if (r.rel_id) return r else return null
	}	
	
	/** Add this Relation o the database. Note that a null is a valid insertion...
	 * return 1 if successfully inserted.
	 */	
	public int addThisToDB() {
		def res = db.getDB().executeInsert("INSERT INTO ${rel_table} VALUES(0,?)", [rel_relation])
		// returns an auto_increment value
		return (int)res[0][0]
	}	
	
	public String toString() {
		return rel_relation
	}
}