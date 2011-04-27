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
/////////////////////////////////
// Imports.

package rembrandt.test.suite

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

import rembrandt.test.io.*

@RunWith(Suite.class)

@SuiteClasses( [
	ACDCReaderTest.class,
	ACDCWriterTest.class,
	HTMLReaderTest.class,
	HTMLWriterTest.class,
	MediawikiXMLReaderTest.class,
	NYTimesReaderTest.class,
	RembrandtReaderTest.class,
	RembrandtWriterTest.class,
	SecondHAREMReaderTest.class,
	SecondHAREMWriterTest.class,
	UnformattedReaderTest.class,
	WPT03ReaderTest.class,
	WPT05ReaderTest.class
])

/// /// /// /// /// /// /// /// /// /// /// /// /// /// /// ////// /// /// /// /// /// /// /// ///
// Name your Test Suit Class here, you should point the IntelliJ JUnit Configuration to this class.
public class RembrandtIOTestSuite {
}
