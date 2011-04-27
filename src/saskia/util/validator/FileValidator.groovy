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

class FileValidator extends Validator {
	
		/**
	 * Validade a filename to a file
	 * @param filename The filename
	 * @return File if exists, null otherwise
	 */
	public File validate(String filename, String default_filename = null, boolean mandatory = true) {

		File file = new File(filename)
		if (!file.exists()) {
			println "While file for document import?"
			print "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			file = new File(input.readLine().trim())
		}
		
		if (!file && mandatory) {
			log.fatal "No file found. Please check if the given file exists"
			System.exit(0)
		}
		return file
	}
}


