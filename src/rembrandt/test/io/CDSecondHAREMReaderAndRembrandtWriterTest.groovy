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

import rembrandt.io.SecondHAREMCollectionReader
import rembrandt.io.SecondHAREMStyleTag
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag

import org.apache.log4j.Logger;
import org.junit.*
import org.junit.runner.*

import rembrandt.obj.Document
import rembrandt.obj.Sentence
import rembrandt.obj.NamedEntity
import rembrandt.obj.ListOfNE
import saskia.bin.Configuration
import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 * Tester for ACDCReader class 
 */
public class CDSecondHAREMReaderTest extends GroovyTestCase {

     SecondHAREMCollectionReader reader
     RembrandtReader rreader
     List<Document> source_docs
     List<Document> solution_docs
     String text
     Configuration conf = Configuration.newInstance()
     List solution
     Logger log = Logger.getLogger("RembrandtTest")
     
     public CDSecondHAREMReaderTest() {
	reader = new SecondHAREMCollectionReader(new SecondHAREMStyleTag("pt"))
	rreader = new RembrandtReader(new RembrandtStyleTag("pt"))
	File f = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/CD_SecondHAREM_sample.txt")
	File f2 = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/Rembrandt_PT_sample_Rembrandt_output.txt")
    
        InputStreamReader is = new InputStreamReader(new FileInputStream(f))
	reader.processInputStream(is)
	source_docs = reader.docs
        
        InputStreamReader is2 = new InputStreamReader(new FileInputStream(f2))
        rreader.processInputStream(is2)
        solution_docs = reader.docs
               
     }
     
     void testReader() {
	
	 int fails = 0 
	 source_docs?.eachWithIndex{source_doc, doc_index -> 
	    log.info "Doing source doc#${doc_index} $source_doc..."
	    Document solution_doc = solution_docs[doc_index]
	 	
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
   	 assert fails == 0
    	 // now, let's clear the NEs and as  
     }	
 }