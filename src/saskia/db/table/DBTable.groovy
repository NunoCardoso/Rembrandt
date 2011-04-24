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

import saskia.db.database.SaskiaDB
import saskia.db.obj.DBObject

/**
 * @author Nuno Cardoso
 * This is the DBTable abstraction.
 * The extended classes are objects that correspond to tables in a DB.
 * It gives a good abstraction for table-wise operations.
 * 
 */
abstract class DBTable {

	SaskiaDB db   
	String tablename
	
	public DBTable(SaskiaDB db, String tablename) {
		this.db = db
		this.tablename = tablename
	} 
	
	/**
	 * Interface for table queries and returning encapsulated objects
	 * @param query The query to submit to this table
	 * @param params The query parameters
	 * @param closure The closure to transform a DB row into a DBObject
	 * @return A list of DBObjects
	 */
	public abstract List<DBObject> queryDB(String query, ArrayList params)
	
	public setSaskiaDB(SaskiaDB db) {
		this.db = db
	}
	
	public SaskiaDB getSaskiaDB() {
		return this.db
	}	
	
	public setTablename(String tablename) {
		this.tablename = tablename
	}
	
	public String getTablename() {
		return this.tablename
	}
}