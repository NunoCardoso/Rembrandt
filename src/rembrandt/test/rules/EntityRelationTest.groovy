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

import org.apache.log4j.Logger

import rembrandt.io.UnformattedReader
import rembrandt.io.UnformattedWriter
import rembrandt.io.RembrandtStyleTag
import rembrandt.obj.Document
import rembrandt.obj.Sentence
import rembrandt.obj.NamedEntity
import saskia.bin.Configuration
import rembrandt.bin.EntityRelationCore

/**
 * @author Nuno Cardoso
 * Tester for Entity Relation Core and Entity Relation Detector.
 */
class EntityRelationTest extends GroovyTestCase {
	
    UnformattedReader reader
    UnformattedWriter writer
    Document source_doc, solution_doc
    String text
    Configuration conf = Configuration.newInstance()
    EntityRelationCore core

    Logger log = Logger.getLogger("RembrandtTest")
    
    public EntityRelationTest() {       
        reader = new UnformattedReader(new RembrandtStyleTag("pt"))
        writer = new UnformattedWriter(new RembrandtStyleTag("pt"))
        File f = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/EntityRelationPT_sample.txt")     
        InputStreamReader is = new InputStreamReader(new FileInputStream(f))
        reader.processInputStream(is)
        source_doc = reader.docs[0]
        source_doc.lang="pt"
        
        //println source_doc.bodyNEs
        core = new EntityRelationCore("pt","harem")
        core.detectEntityRelations(source_doc.bodyNEs, source_doc.body_sentences)
         
        reader.docs = []                        
        File f2 = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/EntityRelationPT_sample_output.txt")
        is = new InputStreamReader(new FileInputStream(f2))
        reader.processInputStream(is)
        solution_doc = reader.docs[0]
        solution_doc.lang="pt"
    }
    
    
    void testRelations() {
	
        int fail = 0
        source_doc.body_sentences.eachWithIndex {s1, i ->
            Sentence s2 = solution_doc.body_sentences[i]
            if (!s1.equals(s2)) {
                log.debug "Different sentences!\nSource: "+s1.dump()+"\nSolution:"+s2.dump()+"\n"
                fail++
            }
            List<NamedEntity> s1_nes = source_doc.bodyNEs.getNEsBySentenceIndex(i)
            List<NamedEntity> s2_nes = solution_doc.bodyNEs.getNEsBySentenceIndex(i)
            if (s1_nes.size() != s2_nes.size()) {
                log.debug "Different number of NEs for sentence #${i}: Source=${s1_nes.size()} Solution=${s2_nes.size()}\n"
                log.debug "Source NEs: $s1_nes\nSolution NEs:$s2_nes\n"
                fail++
            } else {
                s1_nes.eachWithIndex{s1_ne, i2 ->
                    if (s1_ne != s2_nes[i2]) {
                        log.debug "Different NEs!"
                        log.debug "Source NE: $s1_ne\nSolution NE:${s2_nes[i2]}\n"
                        fail++
                    } else {
                	if (s1_ne.corel != s2_nes[i2].corel) {
                	    log.debug "Different NE relations!"
                	    log.debug "Source NE: ${s1_ne.corel}\nSolution NE:${s2_nes[i2].corel}\n"
                	    fail++
                	}
                	
                    }       
                }
            }	     
        }
        assert fail == 0
   }
}
    
    
