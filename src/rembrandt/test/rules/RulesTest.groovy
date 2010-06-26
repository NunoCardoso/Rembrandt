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
 
package rembrandt.test.rules

import rembrandt.bin.RembrandtCore
import saskia.bin.Configuration
import rembrandt.obj.Document
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.io.UnformattedReader
import rembrandt.io.RembrandtStyleTag
import rembrandt.io.RembrandtWriter

import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 * Generic tester for rules
 */
public class RulesTest extends GroovyTestCase {

     static Logger log = Logger.getLogger("RembrandtTest")
     Configuration conf 
     RembrandtCore core
     List<Document> source_docs
     List<Document> solution_docs
     UnformattedReader reader
     RembrandtWriter writer
     String lang
     
     public RulesTest(String lang, String rules, String sourcefile, String targetfile) {
        
        this.lang=lang	 
        conf = Configuration.newInstance()
       // conf.set("rembrandt.input.encoding","MacRoman") //  all test files must be in MacRoman 
       conf.setBoolean("rembrandt.core.removeRemainingUnknownNE",true) 
        conf.setInt("rembrandt.input.styletag.verbose",0) //   0 - just classification 
        conf.setInt("rembrandt.output.styletag.verbose",0) //   0 - just classification 
	core = Class.forName("rembrandt.bin.RembrandtCore"+lang.toUpperCase()+"for"+
		 rules.toUpperCase()).newInstance(conf)

	reader = new UnformattedReader(new RembrandtStyleTag(lang))
        writer = new RembrandtWriter(new RembrandtStyleTag(lang)) // caso queria debug
        	 
	File f = new File(sourcefile)
 	InputStreamReader is = new InputStreamReader(new FileInputStream(f))
	reader.processInputStream(is)
	source_docs = reader.docs
	  
	reader.docs = []  
        
	File f2 = new File(targetfile)
	is = new InputStreamReader(new FileInputStream(f2))
	reader.processInputStream(is)
	solution_docs = reader.docs
     }
     
     void testCompareDocs() {
           
        int fail = 0
        log.info "Got ${source_docs.size()} source docs and ${solution_docs.size()} solution docs."
                  
        source_docs?.eachWithIndex{source_doc, doc_index -> 
            log.info "Doing source doc#${doc_index} $source_doc..."
            Document solution_doc = solution_docs[doc_index]
            source_doc.lang=lang
            solution_doc.lang=lang
            
            log.info "REMBRANDTing source doc $source_doc..." 
            core.releaseRembrandtOnDocument(source_doc)

            //println writer.printDocumentBodyContent(source_doc)
            
            source_doc.title_sentences?.eachWithIndex {s1, i ->
                Sentence s2 = solution_doc.title_sentences[i]
                if (!s1.equals(s2)) {
                    log.warn "Different sentences in title!\nSource: "+s1.dump()+"\nSolution:"+s2.dump()+"\n"
                    fail++
                }
                List<NamedEntity> s1_nes = source_doc.titleNEs.getNEsBySentenceIndex(i)
                List<NamedEntity> s2_nes = solution_doc.titleNEs.getNEsBySentenceIndex(i)
                if (s1_nes.size() != s2_nes.size()) {
                    log.warn "Different number of NEs for title sentence #${i}: Source=${s1_nes.size()} Solution=${s2_nes.size()}\n"
                    log.warn "Source NEs: $s1_nes\nSolution NEs:$s2_nes\n"
                    fail++
                } else {
                    s1_nes.eachWithIndex{s1_ne, i2 ->
                        if (s1_ne != s2_nes[i2]) {
                            log.warn "Different NEs in title!"
                            log.warn "Source NE: $s1_ne\nSolution NE:${s2_nes[i2]}\n"
                            fail++
                        }
                    }
                }	     
            }

            source_doc.body_sentences?.eachWithIndex {s1, i ->
	       Sentence s2 = solution_doc.body_sentences[i]
	       if (!s1.equals(s2)) {
		 log.warn "Different sentences in body!\nSource: "+s1.dump()+"\nSolution:"+s2.dump()+"\n"
		 fail++
	       }
	       List<NamedEntity> s1_nes = source_doc.bodyNEs.getNEsBySentenceIndex(i)
	       List<NamedEntity> s2_nes = solution_doc.bodyNEs.getNEsBySentenceIndex(i)
	       if (s1_nes.size() != s2_nes.size()) {
		 log.warn "Different number of NEs for body sentence #${i}: Source=${s1_nes.size()} Solution=${s2_nes.size()}\n"
		 log.warn "Source NEs: $s1_nes\nSolution NEs:$s2_nes\n"
		 fail++
	       } else {
		 s1_nes.eachWithIndex{s1_ne, i2 ->
		    if (s1_ne != s2_nes[i2]) {
			 log.warn "Different NEs in body!"
			 log.warn "Source NE: $s1_ne\nSolution NE:${s2_nes[i2]}\n"
			 fail++
		    }
		 }
	       }	     
	   }
        }
	assert fail == 0
     }
 }