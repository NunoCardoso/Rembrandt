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

import saskia.db.obj.*
import saskia.db.database.*

class CollectionValidator {
	
		/**
	 * Let's validate if a given collection name and/or id gets to a 
	 * real collection.
	 * @return A collection if it's there, null otherwise
	*/
	SaskiaDB db
	
	public CollectionValidator(SaskiaDB db) {
		this.db = db
	}
	
	Collection validate(String colletion_given_name, 
		String DEFAULT_COLLECTION_NAME,
		boolean mandatory = true) {

		String collection_name
		Collection col
		
		if (!collection_given_name) {
			println "What is the target collection ID/name? (Default: ${DEFAULT_COLLECTION_NAME}) "
			print "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			collection_name = input.readLine().trim()
			if (!collection_name) collection_name = DEFAULT_COLLECTION_NAME
		} else {
			collection_name = collection_given_name
		}
		if (!collection_name && mandatory) {
			log.fatal "collection name not given."
			log.fatal "Please make sure you specify the collection"
			System.exit(0)
		}
		
		col = db.getDBTable("CollectionTable").getFromNameOrID(collection_name)
		if (!col && mandatory) {
			log.fatal "Collection couldn't be found."
			log.fatal "Please make sure you have the specified collection in the DB: $colletion_name"
			System.exit(0)
		}
		return col
	}

}