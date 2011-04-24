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

package saskia.util.validator

import saskia.db.database.*

class DBValidator {
	
	/**
	 * Let's validate if a given collection name and/or id gets to a
	 * real collection.
	 * @return A collection if it's there, null otherwise
	 */
	public SaskiaDB validate(String db_given_label, String DEFAULT_DB_LABEL, boolean mandatory = true) {

		String db_label
		SaskiaDB db
		
		if (!db_given_label) {
			println "What is the target Saskia DB? (Default: ${DEFAULT_DB_LABEL}) "
			print "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			db_label = input.readLine().trim()
			if (!db_label) db_label = DEFAULT_DB_LABEL
		} else {
			db_label = db_given_label
		}	
		if (!db_label && mandatory) {
			log.fatal "DB couldn't be found."
			log.fatal "Please make sure you have that DB before the import."
			System.exit(0)
		}
		if (db_label == "main") return SaskiaMainDB.newInstance()
		if (db_label == "test") return SaskiaTestDB.newInstance()
		if (mandatory){
			log.fatal "DB couldn't be found."
			log.fatal "Please make sure you have the DB running: $db_label."
			System.exit(0)
		}
	}
}
