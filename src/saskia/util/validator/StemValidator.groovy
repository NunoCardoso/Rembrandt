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

class StemValidator extends Validator {
		
	/**
	 * Validate the mode
	 * 
	 */
	public validate(String given_stem, String DEFAULT_STEM, boolean mandatory) {

		boolean stem = null

		if (!given_stem) {
			println "Stem (true/false)? (Default: $DEFAULT_STEM)"
			print "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			String answer = input.readLine().trim()
			stem = Boolean.parseBoolean(answer)
		} else {
			stem = Boolean.parseBoolean(given_stem)
		}
		if (stem == null && mandatory) {
			log.fatal "Stem not given."
			log.fatal "Please specify stem correctly (true or false)."
			System.exit(0)
		}
		return stem
	}
}