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
package saskia.test.db

import org.apache.log4j.*
import org.junit.*
import org.junit.runner.*

import saskia.db.obj.*
import saskia.db.table.*


/**
 * @author Nuno Cardoso
 */
class GeoscopeTest extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	Subject subject

	public GeoscopeTest() {
	}

	// Holland has a 301 redirect to Netherlands.
	void testRedirectGeoscope() {
		List<Geoscope> holland = Geoscope.getFromName("Holanda","pt")
		Geoscope netherlands = Geoscope.getFromWOEID(23424909)
		assert holland[0] == netherlands
	}


}