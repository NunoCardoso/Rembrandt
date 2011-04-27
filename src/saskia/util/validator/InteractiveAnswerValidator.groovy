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

class InteractiveAnswerValidator extends Validator {
	
		public String validate(String given_answer, String default_given_answer = null,
			boolean mandatory = false) {

		String answer
		
		if (!given_answer) {
			println "Interactive/automatic questions (for some import decisions, it requires a yes/no/always/never answer)"
			println "Enter blank for interactive questions, enter [y]es, [a]lways, [n]o, n[e]ver for automatic answer"
			print "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			answer = input.readLine().trim()	
		} else {
			answer = given_answer
		}
		
		if (!answer && mandatory) {
			log.fatal "No answer found. Exiting"
			System.exit(0)
		}
		return answer
	}
}
