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
 * Tester for ACDCReader class 
 */
public class ReaderTest extends GroovyTestCase {

	Reader reader_generated
	RembrandtReader reader_expected
	Configuration conf
	Logger log = Logger.getLogger("RembrandtTest")
	static String fileseparator = System.getProperty("file.separator")
	String file_input
	String file_output

	public ReaderTest() {
		conf = Configuration.newInstance()
	}

	void processReaders() {
		File file_source = new File(file_input)
		File file_expected = new File(file_output)

		reader_generated.processInputStream(new InputStreamReader(
				new FileInputStream(file_source)))
		reader_expected.processInputStream(new InputStreamReader(
				new FileInputStream(file_expected)))
	}

	void evaluateReaders() {

		// same number of docs
		assert reader_generated.docs.size() == reader_expected.docs.size()

		int fails = 0

		reader_generated.docs?.eachWithIndex{generated_doc, doc_index ->

			log.debug "Doing generated doc#${doc_index} $generated_doc..."
			Document expected_doc = reader_expected.docs[doc_index]

			if (log.getLevel() == Level.TRACE) {
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

			generated_doc.title_sentences?.eachWithIndex {s1, i ->
				Sentence s2 = expected_doc?.title_sentences[i]
				if (!s1.equals(s2)) {
					log.warn "Different sentences in body!\nSource: "+s1.dump()+"\nSolution:"+s2.dump()+"\n"
					fails++
				}
				List<NamedEntity> s1_nes = generated_doc.titleNEs.getNEsBySentenceIndex(i)
				List<NamedEntity> s2_nes = expected_doc.titleNEs.getNEsBySentenceIndex(i)
				if (s1_nes.size() != s2_nes.size()) {
					log.warn "Different number of NEs for body sentence #${i}: Source=${s1_nes.size()} Solution=${s2_nes.size()}\n"
					log.warn "Generated NEs: $s1_nes\nExpected NEs:$s2_nes\n"
					fails++
				} else {
					s1_nes.eachWithIndex{s1_ne, i2 ->
						if (!s1_ne.equals(s2_nes[i2])) {
							log.warn "Different NEs in body!"
							log.warn "Generated NE: $s1_ne\nExpected NE:${s2_nes[i2]}\n"
							fails++
						}
					}
				}
			}
			generated_doc.body_sentences?.eachWithIndex {s1, i ->
				Sentence s2 = expected_doc.body_sentences[i]
				if (!s1.equals(s2)) {
					log.warn "Different sentences in body!\nSource: "+s1.dump()+"\nSolution:"+s2.dump()+"\n"
					fails++
				}
				List<NamedEntity> s1_nes = generated_doc.bodyNEs.getNEsBySentenceIndex(i)
				List<NamedEntity> s2_nes = expected_doc.bodyNEs.getNEsBySentenceIndex(i)
				if (s1_nes.size() != s2_nes.size()) {
					log.warn "Different number of NEs for body sentence #${i}: Source=${s1_nes.size()} Solution=${s2_nes.size()}\n"
					log.warn "Generated NEs: $s1_nes\nExpected NEs:$s2_nes\n"
					fails++
				} else {
					s1_nes.eachWithIndex{s1_ne, i2 ->
						if (!s1_ne.equals(s2_nes[i2])) {
							log.warn "Different NEs in body!"
							log.warn "Generated NE: $s1_ne\nExpected NE:${s2_nes[i2]}\n"
							fails++
						}
					}
				}
			}
		}
		assert fails == 0
	}
}
