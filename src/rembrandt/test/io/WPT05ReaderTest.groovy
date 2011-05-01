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
/**
 * @author Nuno Cardoso
 *
 */
class WPT05ReaderTest extends ReaderTest {


	public WPT05ReaderTest() {
		super()
		this.reader_generated = new WPT05Reader(new UnformattedStyleTag("pt"))
		this.reader_expected = new RembrandtReader(new RembrandtStyleTag("pt"))
	}


	//	@org.junit.Ignore("not ready yet")
	void testReaderPlainWPT05() {
		this.file_input = conf.get("rembrandt.home.dir") + fileseparator + "resources" +
				fileseparator + "test" + fileseparator + "collections" + fileseparator + "WPT05-00002_sample_utf8_wpt05format.txt"
		this.file_output =  conf.get("rembrandt.home.dir") + fileseparator + "resources" +
				fileseparator + "test" + fileseparator + "collections" + fileseparator + "WPT05-00002_sample_utf8_rembrandtformat.txt"
		super.processReaders()

		reader_generated.docs?.eachWithIndex{generated_doc, doc_index ->
			println new RembrandtWriter(new RembrandtStyleTag("pt")).printDocument(generated_doc)
			//super.evaluateReaders()
		}
	}
}
