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

     List<Document> docs
	  List<String> solution_lines = []

     String text
     Configuration conf = Configuration.newInstance()
     
	  Logger log = Logger.getLogger("RembrandtTest")
     
     public RembrandtReaderAndRembrandtWriterTest() {
	  reader = new RembrandtReader(new RembrandtStyleTag("pt"))
	  writer = new RembrandtWriter(new RembrandtStyleTag("pt"))

	  File f = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/Rembrandt_PT_sample.txt")
	
	  // read as string lines
	  f.eachLine{l -> solution_lines << l}
	  
	  // read as docs
	  InputStreamReader is = new InputStreamReader(new FileInputStream(f))
	  reader.processInputStream(is)
	  docs = reader.docs

     }
     
     void testReader() {
	 
        int fails = 0 
        List<String> written_lines = []
        docs.eachWithIndex{doc, i ->
	
				doc.body_sentences.eachWithIndex{s, x -> 
					println "$x: $s"
				}
				
				doc.bodyNEs.eachWithIndex{ne, x -> 
					println "$x: "+ne.toStringTraceLevel()
				}
				
	 		   String doc_print = writer.printDocument(doc)
				doc_print.split("\n").each{written_lines << it}
		  }
        written_lines.eachWithIndex{line, j -> 
            if (line != solution_lines[j]) {
             log.debug "=== Line $j ===\nGenerated doc:\n$line\nshould be:\n${solution_lines[j]}\n\n"
            fails++
             }
        }
 
	 	assert fails == 0
	 // now, let's clear the NEs and as  
	 }
	
 }