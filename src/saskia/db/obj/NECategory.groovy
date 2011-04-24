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
package saskia.db.obj

import org.apache.log4j.Logger

import saskia.db.table.DBTable

/**
 * @author Nuno Cardoso
 *
 */
class NECategory extends DBObject implements JSONable {

	Long nec_id
	String nec_category
	static Logger log = Logger.getLogger("NECategory")
	
	
	public NECategory(DBTable dbtable) {
		super(dbtable)
	}
	
	static NECategory createNew(DBTable dbtable, row) {
		NECategory nec = new NECategory(dbtable)
		if (row['nec_id']) nec.nec_id = row['nec_id']
		if (row['nec_category']) nec.nec_category = row['nec_category']
		return nec
	}
		
	public Map toMap() {
		return ["nec_id":nec_id, "nec_category":nec_category]
	}

	public Map toSimpleMap() {
		return toMap()
	}
	
	/** Add this NECategory o the database. Note that a null is a valid insertion...
	* return the id  if either successfully inserted or ignored.
	*/
   public Long addThisToDB() {
	   def res = getDBTable().getSaskiaDB().getDB().executeInsert(
		   "INSERT IGNORE INTO ${getDBTable().tablename} VALUES(0,?)", 
		   [nec_category])
	   // returns an auto_increment value
	   if (res) {
		   nec_id = (long)res[0][0]
		   getDBTable().updateCacheElement(nec_id, nec_category)
		   log.info "Inserted new NECategory in DB: ${this}"
	   }
	   return nec_id
   }

   public int removeThisFromDB() {
	   if (!nec_id) return null
	   def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
		   "DELETE FROM ${getDBTable().tablename} WHERE nec_id=?", 
		   [nec_id])
	   getDBTable().all_category_id.remove(nec_category)
	   getDBTable().all_id_category.remove(nec_id)
	   log.info "Removed NECategory ${this} from DB, got $res"
	   return res
   }

   boolean equals(NECategory nec) {
	   return this.toMap().equals(nec.toMap())
   }

   public String toString() {
	   return nec_category
   }
}
