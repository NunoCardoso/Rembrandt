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

class  DocsValidator extends Validator {
	

		String mode
	
	public DocsValidator(String mode) {
		this.mode = mode
	}
	
	public Integer validate(String given_value, String DEFAULT_VALUE, boolean mandatory) {

		if (!given_value) {
			if (mode == "single") {
				println "Which document id for single tagging?"
			} else {
				println "How many documents for multiple tagging? (Default: ${DEFAULT_VALUE}) "
			}
	
			print "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			val = input.readLine().trim()
			
			if (mode == "multiple") {
				if (val) {
					try {
						value = Integer.parseInt(val)
					} catch(Exception e) {println "invalid amount: $mode."}
			 	} else {
						value = DEFAULT_VALUE
				}
			} else {
				value = val
			}
		} else {
			value = Integer.parseInt(given_value)
		}
		if (!value && mandatory) {
			println "You have to specify a docs value. Exiting..."
			System.exit(0)
		}
		return value
	}
}
	