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

class SyncValidator {
		
	/**
	 * Validate the mode
	 * 
	 */
	public String validate(given_sync, String DEFAULT_SYNC, boolean manadory) {

		String sync

		if (!given_sync) {
			println "Which the sync method? (allowed:[rdoc, pool] - Default: $DEFAULT_SYNC)"
			println "  - rdoc: analyses RembrandtedDocuments"
			println "  - pool: analyes NE Pool." 
			print "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			sync = input.readLine().trim()
		} else {
			sync = given_sync
		}
		if (!sync && mandatory) {
			log.fatal "Sync not given."
			log.fatal "Please specify sync [rdoc, pool] "
			System.exit(0)
		}
		return sync
	}
}