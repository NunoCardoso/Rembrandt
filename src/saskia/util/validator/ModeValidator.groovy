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

class ModeValidator extends Validator {
		
	/**
	 * Validate the mode
	 * 
	 */
	public String validate(String given_mode, String DEFAULT_MODE_NAME, boolean mandatory = true) {

		String mode

		if (!given_mode) {
			println "What is the mode? (single/multiple)"
			println "  - Single: only one document (with given original ID) is tagged." 
			println "  - Multiple: batch n documents will be tagged." 
			println "(Default: ${DEFAULT_MODE_NAME}) "
			print "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			mode = input.readLine().trim()
			if (!mode) mode = DEFAULT_MODE_NAME
		} else {
			mode = given_mode
		}
		if (!mode) {
			log.fatal "Mode not given."
			log.fatal "Please specify if the import mode is a single document or multiple."
			System.exit(0)
		}
		return mode
	}
}
