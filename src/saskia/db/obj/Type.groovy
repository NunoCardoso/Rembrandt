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
class Type extends DBObject {// implements JSONable {
	
	Long typ_id
	String typ_name

	static Logger log = Logger.getLogger("Type")

	public Type(DBTable dbtable) {
		super(dbtable)
	}
	
	public Type(DBTable dbtable, Long typ_id, String typ_name) {
		super(dbtable)
		this.typ_id = typ_id
		this.typ_name = typ_name
	}
	
	static Type createFromDBRow(DBTable dbtable, row) {
		return new Type(dbtable, row['typ_id'], row['typ_name'] )
	}
	
	/** Add this NECategory o the database. Note that a null is a valid insertion...
	* return 1 if successfully inserted.
	*/
   public Long addThisToDB() {
	   def res = getDBTable().getSaskiaDB().getDB().executeInsert(
		   "INSERT INTO ${getTable().getTablename()} VALUES(0,?)", 
		   [typ_name])
	   // returns an auto_increment value
	   log.info "Adding type to DB: ${this}"
	   return (long)res[0][0]
   }

   public int removeThisFromDB() {
	   if (!typ_id) return null
	   def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
		   "DELETE FROM ${getTable().getTablename()} WHERE typ_id=?", 
		   [typ_id])
	   log.info "Removing type to DB: ${this}, got res $res"
	   return res
  }
   
   public String toString() {
	   return typ_name
   }
}
