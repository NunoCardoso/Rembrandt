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

package saskia.test

import saskia.bin.Configuration
import saskia.db.TimeSignatureFactory;
import saskia.io.*
import org.apache.log4j.Logger
import rembrandt.io.RembrandtStyleTag
import rembrandt.io.UnformattedStyleTag
import rembrandt.io.RembrandtReader
import rembrandt.io.UnformattedReader
import rembrandt.obj.Document
/**
 * @author Nuno Cardoso
 *
 */
class TimeSignatureFactoryTest extends GroovyTestCase{
    
    // doc 1:
    // Visitei o Porto em 27 de Maio de 2007. 
    // Fiquei até 29 de Maio
    // no dia 30 fui embora.
    
    static Logger log = Logger.getLogger("SaskiaTest")
    Configuration conf = Configuration.newInstance()
    List processedNEs
    List<Document> source_docs
    List<Document> dts_solutions
    RembrandtReader reader
    UnformattedReader ureader
    UnformattedStyleTag tag
    
    public TimeSignatureFactoryTest() {
                
        String sourcefile = conf.get("rembrandt.home.dir",".")+
            "/resources/test/TimeSignaturePT_sample.txt"     
        String targetfile = conf.get("rembrandt.home.dir",".")+
           "/resources/test/TimeSignaturePT_sample_output.txt"     
        
        reader = new RembrandtReader(new RembrandtStyleTag("pt"))
        ureader = new UnformattedReader(new UnformattedStyleTag("pt"))
                 
        File f = new File(sourcefile)
        InputStreamReader is = new InputStreamReader(new FileInputStream(f))
        reader.processInputStream(is)
        source_docs = reader.docs
        
        File f2 = new File(targetfile)
        InputStreamReader is2 = new InputStreamReader(new FileInputStream(f2))
        ureader.processInputStream(is2)
        dts_solutions = ureader.docs       
    }
    

    /*       nes is a list with Map entries:  section: ['dhn_section'], sentence: ['dhn_sentence'],
     term: ['dhn_term'], name: ['nen_name'], type: ['ne_type'], 
     subtype: ['ne_subtype'], entity: ['ent_id'], dbpediaClass: ['ent_dbpedia_class']                 
     */      
    void testGenerate() {
        
        // let's simulate the output from RembrandtedDoc.getBatchDocsAndNEsFromPoolToGenerateGeoSignatures

        def labels 
        TimeSignatureFactory tsf = new TimeSignatureFactory()
        int fail = 0
        
        source_docs.eachWithIndex{doc, i -> 
            
            //println "Doc: $doc NEs body: ${doc.bodyNEs}"
            
            doc.preprocess()
            
            String source_dts = tsf.generate(doc.docid, [lang:doc.lang, original_id:null, 
            date:new Date(0), doc:doc])
            
            // let's compare line by line
            String solution_dts = dts_solutions[i].body
            
            log.info "Solution $i:\n=======\n${solution_dts}\n========\n"
            List source_dts_lines = source_dts.split(/\n+/)
            List solution_dts_lines = solution_dts.split(/\n+/)
            
            source_dts_lines.eachWithIndex{l, li -> 
            	if (!l.trim().equals(solution_dts_lines[li].trim())) {
                 log.error "Doc ${doc.docid}: Source line is $l, should be ${solution_dts_lines[li]}!"
                 fail++   
                }
            }               
        }
        assert fail == 0
   }
}