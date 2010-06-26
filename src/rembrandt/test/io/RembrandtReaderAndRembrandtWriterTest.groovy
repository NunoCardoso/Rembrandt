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

import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag

import org.apache.log4j.Logger;
import org.junit.*
import org.junit.runner.*

import rembrandt.bin.RembrandtCorePTforHAREM
import rembrandt.obj.Document
import rembrandt.obj.ListOfNE
import saskia.bin.Configuration
import org.apache.log4j.Logger

import org.apache.log4j.Logger


/**
 * @author Nuno Cardoso
 * Tester for ACDCReader class 
 */
public class RembrandtReaderAndRembrandtWriterTest extends GroovyTestCase {

     RembrandtReader reader
     RembrandtWriter writer
     RembrandtCorePTforHAREM core
     List<Document> source_docs, solution_docs
     String text
     Configuration conf = Configuration.newInstance()
     List solution
     Logger log = Logger.getLogger("RembrandtTest")
     
     public RembrandtReaderAndRembrandtWriterTest() {
	reader = new RembrandtReader(new RembrandtStyleTag("pt"))
	writer = new RembrandtWriter(new RembrandtStyleTag("pt"))
	core = new RembrandtCorePTforHAREM(conf)
	File f = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/Rembrandt_PT_sample.txt")
	File f2 = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/Rembrandt_PT_sample_Rembrandt_output.txt")
    
	InputStreamReader is = new InputStreamReader(new FileInputStream(f))
	reader.processInputStream(is)
	source_docs = reader.docs

	reader.docs = []
        is = new InputStreamReader(new FileInputStream(f2))
        reader.processInputStream(is)
        solution_docs = reader.docs
     }
     
     void testReader() {
	 
        int fails = 0 
        
        source_docs.eachWithIndex{source_doc, i ->
	 // this document will be retagged
	    Document re_tagged_doc = source_doc
        // this document will be used to compare outputs
	    Document solution_doc = solution_docs[i]
    
    // não basta re_tagged_doc.titleNEs.clear(), isso polui os índices  
	    re_tagged_doc.titleNEs = new ListOfNE()
	    re_tagged_doc.bodyNEs = new ListOfNE()
	   
	 //println "re_tagged_doc.title_sentences = "+re_tagged_doc.title_sentences 
	 //println "re_tagged_doc.body_sentences = "+re_tagged_doc.body_sentences 
	    core.releaseRembrandtOnDocument(re_tagged_doc)


	    log.debug("Testing title NEs")
	    source_doc.titleNEs.eachWithIndex{ne, i2->
	     if (ne != re_tagged_doc.titleNEs[i2]) {
		 log.debug "NE $i: Got ${re_tagged_doc.titleNEs[i2]}, should be $ne "
		 fails++
	     }
	    }
	    log.debug("Testing body NEs")
	    source_doc.bodyNEs.eachWithIndex{ne, i2->
	     if (ne != re_tagged_doc.bodyNEs[i2]) {
		 log.debug "NE $i: Got ${re_tagged_doc.bodyNEs[i2]}, should be $ne "
		 fails++
	     }
	    }
            
            List source_doc_print = writer.printDocument(source_doc)
            List solution_doc_print = writer.printDocument(solution_doc)
            source_doc_print.eachWithIndex{line, j -> 
            	if (line != solution_doc_print[j]) {
                    log.debug "Line $i: Source doc line is $line, should be ${solution_doc_print[i]} instead."
                    fail++
                 }
            }
            
         }
	 
	 assert fails == 0
	 // now, let's clear the NEs and as  
	 }
	
 }