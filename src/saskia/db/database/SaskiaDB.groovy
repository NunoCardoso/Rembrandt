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
package saskia.db.database

import saskia.db.table.DBTable

import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * Abstract class for Saskia databases
 */
abstract class SaskiaDB extends DB {

	Map tables

	public SaskiaDB(Configuration conf) {
		super(conf)
		tables = [:]
	}

	// multiple singleton
	public DBTable getDBTable(String classname) {
		DBTable tableToReturn = null
		String targetClassName = "rembrandt.obj.table."+classname
		if (tables.containsKey(targetClassName))
			tableToReturn = tables[targetClassName]

		if (!tableToReturn) {
			try {
				// calls the getInstance(SaskiaDB db) static method from all DBTable objects
				tableToReturn = Class.forName(targetClassName).getInstance(this)
				

			}catch(Exception e) {
				log.error "Can't load DBTable $targetClassName."
				e.printStackTrace()
			}
			tables[targetClassName] << tableToReturn
		}
		return tableToReturn
	}
}