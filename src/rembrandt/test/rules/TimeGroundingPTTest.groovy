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

import rembrandt.obj.Document
import rembrandt.obj.TimeGrounding
import saskia.bin.Configuration
import rembrandt.bin.RembrandtCorePTOnlyTIME
import org.apache.log4j.Logger
import rembrandt.io.UnformattedReader
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag

/**
 * @author Nuno Cardoso
 * Generic tester for rules
 */
public class TimeGroundingPTTest extends GroovyTestCase {

     static Logger log = Logger.getLogger("RembrandtTest")
     Configuration conf 
     RembrandtCorePTOnlyTIME core
     List<Document> source_docs
     List<Document> solution_docs
     String lang = "pt"
    
    UnformattedReader reader
    RembrandtWriter writer
     
     public TimeGroundingPTTest() {
        
        conf = Configuration.newInstance()
        core = new RembrandtCorePTOnlyTIME(conf)
        
        String sourcefile = conf.get("rembrandt.home.dir",".")+"/resources/test/TimeGroundingPT_sample.txt"     
        String targetfile = conf.get("rembrandt.home.dir",".")+"/resources/test/TimeGroundingPT_sample_output.txt"     
                    
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
            
            List<TimeGrounding> tgs = []
            // don't care how it was tokenized... go to the source text. 
            solution_doc.body.split(/\n+/).each{tgs <<
                TimeGrounding.parseString(it.trim())}
        
            log.info "REMBRANDTing source doc $source_doc..." 
            core.releaseRembrandtOnDocument(source_doc)

        //println writer.printDocumentBodyContent(doc)
            println "Got ${source_doc.bodyNEs.size()} NEs in doc, got ${tgs.size()} grounded times to compare."           
            source_doc.bodyNEs.eachWithIndex {ne, i ->           
            	if (!(ne.tg.equals(tgs[i]))) {
            	    log.error "Doc. ${source_doc}: TimeGrounding of NE ${ne.printTerms()} is ${ne.tg}, should be ${tgs[i]}. "//Ne is ${ne.tg.dump()}, should be ${tgs[i].dump()}"// ne history: ${ne.printHistory()}"
            	    fail++    
            	}             
            }
        }
	assert fail == 0
     }
 }