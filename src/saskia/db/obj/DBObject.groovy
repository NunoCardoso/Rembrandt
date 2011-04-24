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

import saskia.db.database.SaskiaDB
import saskia.db.table.DBTable
/**
 * @author Nuno Cardoso
 * This is the DBRow abstraction.
 * The extended classes are objects that correspond to rows in a DB table.
 */
abstract class DBObject {

	DBTable dbtable
	//public static queryDB(String query, ArrayList params);

	public DBObject(DBTable dbtable) {
		this.dbtable = dbtable
	}

	public setDBTable(DBTable dbtable) {
		this.dbtable = dbtable
	}

	public DBTable getDBTable() {
		return this.dbtable
	}

	public abstract Long addThisToDB()

	public abstract int removeThisFromDB()
}