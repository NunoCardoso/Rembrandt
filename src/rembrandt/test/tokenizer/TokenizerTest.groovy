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
 
package rembrandt.test.tokenizer

import rembrandt.tokenizer.TokenizerPT
import saskia.bin.Configuration
import rembrandt.obj.Document
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.io.UnformattedReader
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag

import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 * Generic tester for tokenizer
 */
public class TokenizerTest extends GroovyTestCase {

     static Logger log = Logger.getLogger("RembrandtTest")
     Configuration conf 
     Document[] source_docs
     Document[] solution_docs
     UnformattedReader ureader
     RembrandtReader rreader
     RembrandtWriter rwriter
     RembrandtStyleTag style
     String lang
     
     public TokenizerTest() {
        
        this.lang="pt"
        
        conf = Configuration.newInstance()
        conf.set("rembrandt.input.encoding","MacRoman") //  all test files must be in MacRoman 
        conf.setInt("rembrandt.input.styletag.verbose",0) //   0 - just classification 
        conf.setInt("rembrandt.output.styletag.verbose",0) //   0 - just classification 

        style = new RembrandtStyleTag(lang)
	ureader = new UnformattedReader(style)
        rreader = new RembrandtReader(style)
        rwriter = new RembrandtWriter(style) 
        
	File f = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/TokenizerPT_sample.txt")
        File f2 = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/TokenizerPT_sample_output.txt")
        InputStreamReader is = new InputStreamReader(new FileInputStream(f))
	ureader.processInputStream(is)
	source_docs = ureader.docs
	
	is = new InputStreamReader(new FileInputStream(f2))
	rreader.processInputStream(is)
	solution_docs = rreader.docs
	
     }
     
     void testCompareDocs() {
           
        int fail = 0
        log.info "Got ${source_docs.size()} source docs and ${solution_docs.size()} solution docs."
        
        source_docs?.eachWithIndex{source_doc, doc_index -> 
            log.info "Doing source doc#${doc_index} $source_doc..."
            Document solution_doc = solution_docs[doc_index]
                     
            // println rwriter.printDocumentBodyContent(source_doc)
      
            source_doc.title_sentences?.eachWithIndex {s1, i ->
                Sentence s2 = solution_doc.title_sentences[i]
                if (!s1.equals(s2)) {
                    log.warn "Different sentences in title!\nSource: "+s1?.dump()+"\nSolution:"+s2?.dump()+"\n"
                    fail++
                }  
            }

            source_doc.body_sentences?.eachWithIndex {s1, i ->
                   
	       Sentence s2 = solution_doc.body_sentences[i]
	       if (!s1.equals(s2)) {
		 log.warn "Different sentences in body!\nSource: "+s1?.dump()+"\nSolution:"+s2?.dump()+"\n"
		 fail++
	       }   
	   }
        }
	assert fail == 0
     }
 }