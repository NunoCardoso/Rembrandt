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

class DirectoryValidator extends Validator {
	
	public File validate(String given_directory, 
		String DEFAULT_DIRECTORY, boolean mandatory = true) {

		String directory
		if (!given_directory) {
			println "Which output directory for export files? (Default: ${DEFAULT_DIRECTORY}) "
			print "> "
			 BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			directory = input.readLine().trim()
			if (!directory) {
				println "Directory will be default: $DEFAULT_DIRECTORY"	
				directory = DEFAULT_DIRECTORY;
			}
		} else {
			directory = given_directory
		}
		
		File file = new File(directory)
		if (file.exists()) {
			println "Directory given $directory exists."
		} else {
			println "Directory given $directory doesn't exist. Creating..."
			file.mkdir()
		}
	
		return file
	}
}