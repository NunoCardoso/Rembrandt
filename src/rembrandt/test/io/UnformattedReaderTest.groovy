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

package rembrandt.test.io

import rembrandt.io.*

import org.junit.*
import org.junit.runner.*

import rembrandt.obj.Document
import saskia.bin.Configuration
import org.apache.log4j.Logger


/**
 * @author Nuno Cardoso
 * Tester for ACDCReader class 
 */

class UnformattedReaderTest extends ReaderTest {


	public UnformattedReaderTest() {
		super()
		this.reader_generated = new UnformattedReader(new RembrandtStyleTag("pt"))
		this.reader_expected = new RembrandtReader(new RembrandtStyleTag("pt"))
	}

	void testReaderPlainSecondHAREM() {
		this.file_input = conf.get("rembrandt.home.dir") + fileseparator + "resources" +
				fileseparator + "test" + fileseparator + "io" + fileseparator + "Rembrandt1File_plainformat_tagged.txt"
		this.file_output =  conf.get("rembrandt.home.dir") + fileseparator + "resources" +
				fileseparator + "test" + fileseparator + "io" + fileseparator + "Rembrandt1File_rembrandtformat_tagged.txt"
		super.processReaders()
		super.evaluateReaders()
	}
}
