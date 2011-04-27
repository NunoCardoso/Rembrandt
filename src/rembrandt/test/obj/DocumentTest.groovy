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

package rembrandt.test.obj
import saskia.bin.Configuration

import rembrandt.obj.*

import org.junit.*
import org.junit.runner.*

/**
 * @author Nuno Cardoso
 * Tester for rembrandt.obj.Document
 */
public class DocumentTest extends GroovyTestCase {

	Document doc
	Configuration conf = Configuration.newInstance()
	static String fileseparator = System.getProperty("file.separator")
	String docid = "Rembrandt-WikipediaPTSample"
	String lang = "pt"
	String title = "Rembrandt, retirado da Wikipédia"
	String body = new File(conf.get("rembrandt.home.dir",".")+
	fileseparator + "resources" +fileseparator + "test" +
	fileseparator + "obj" + fileseparator + "Rembrandt-WikipediaPTSample.txt").text

	public DocumentTest() {
		doc = new Document()
		doc.docid= docid
		doc.title= title
		doc.body = body
		doc.lang = lang
		doc.preprocess()
	}

	void testBasics() {
		assert doc.title == title
		assert doc.docid == docid
		assert doc.body == body
		assert doc.lang == lang
	}

	void testIndexes() {
		assert doc.isTitleTokenized()
		assert doc.isBodyTokenized()
		assert doc.isTitleIndexed()
		assert doc.isBodyIndexed()
		assert doc.title_sentences.size() == 1
		assert doc.body_sentences.size() == 7
		// Rembrandt occurs 3 times, on sentence 0 term 0, etc.
		assert [[0, 0], [5, 16], [6, 3]]== doc.bodyIndex.getIndexesForTerm("Rembrandt")
		assert null == doc.bodyIndex.getIndexesForTerm("nomatchhere")
	}
}

