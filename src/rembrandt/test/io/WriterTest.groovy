/**
 * @author Nuno Cardoso
 *
 */
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

import org.junit.*
import rembrandt.io.*
import rembrandt.obj.*
import saskia.bin.Configuration
import org.junit.runner.*
import org.apache.log4j.Logger
import org.apache.log4j.Level
/**
 * @author Nuno Cardoso
 * Tester for rembrandt.io.Writer classes 
 * 
 * The test is done line by line, on the output. So, 
 * there is one basic RembrandtReader, and the writer test.
 */
public class WriterTest extends GroovyTestCase {

	RembrandtReader reader_generated
	Reader reader_expected
	Writer writer

	Configuration conf
	Logger log = Logger.getLogger("RembrandtTest")
	static String fileseparator = System.getProperty("file.separator")
	String file_input
	String file_output
	List<Document> expected_docs
	List<Document> generated_docs
	List writer_expected = []
	List writer_generated = []

	public WriterTest() {
		conf = Configuration.newInstance()
	}

	void processWriters() {
		File file_source = new File(file_input)
		File file_expected = new File(file_output)

		reader_generated.setInputStreamReader(
			new InputStreamReader(
				new FileInputStream(file_source)))
		
		generated_docs = reader_generated.readDocuments(1000)		

		// use for debug only
		// because Writing output should be evaluated against
		// an existing file output, not against other Writer output
		reader_expected.setInputStreamReader(
			new InputStreamReader(
				new FileInputStream(file_expected)))
		
		expected_docs = reader_expected.readDocuments(1000)		

		generated_docs.each{doc ->
			String output = writer.printDocument(doc)
			writer_generated.addAll(writer_generated.size(), output.split(/\n/))
		}
		writer_expected = file_expected.text.split(/\n+/)
	}

	void evaluateWriters() {

		int fails = 0

		if (log.getLevel() == Level.TRACE) {
			log.trace ("WARNING: EXPECTED stuff may DIFFER from real expected file output")
			expected_docs.each{expected_doc ->
				log.trace "DOC EXPECTED:"
				log.trace new RembrandtWriter(new RembrandtStyleTag("pt")).printDocument(expected_doc)
				log.trace "DOC EXPECTED title sentences:"
				log.trace expected_doc?.title_sentences
				log.trace "DOC EXPECTED body sentences:"
				log.trace expected_doc?.body_sentences
				log.trace "DOC EXPECTED title NEs:"
				log.trace expected_doc?.titleNEs
				log.trace "DOC EXPECTED body NEs:"
				log.trace expected_doc?.bodyNEs
			}
			generated_docs.each{generated_doc ->
				log.trace "DOC GENERATED:"
				log.trace new RembrandtWriter(new RembrandtStyleTag("pt")).printDocument(generated_doc)
				log.trace "DOC GENERATED title sentences:"
				log.trace generated_doc?.title_sentences
				log.trace "DOC GENERATED body sentences:"
				log.trace generated_doc?.body_sentences
				log.trace "DOC GENERATED title NEs:"
				log.trace generated_doc?.titleNEs
				log.trace "DOC GENERATED body NEs:"
				log.trace generated_doc?.bodyNEs
			}
		}

		assert writer_generated.size() == writer_expected.size()

		writer_generated?.eachWithIndex{generated_line, line_index ->

			String expected_line = writer_expected[line_index]

			if (expected_line != generated_line) {
				log.debug "=== Line $line_index ===\nGenerated:\n$generated_line\nshould be:\n$expected_line\n\n"
				fails++
			}
		}
		assert fails == 0
		// now, let's clear the NEs and as
	}
}
