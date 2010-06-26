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

import rembrandt.io.HTMLDocumentReader
import rembrandt.io.RembrandtWriter
import rembrandt.io.HTMLStyleTag
import rembrandt.io.RembrandtStyleTag

import org.apache.log4j.Logger;
import org.junit.*
import org.junit.runner.*

import rembrandt.bin.RembrandtCorePTforHAREM
import rembrandt.obj.Document
import saskia.bin.Configuration
import org.apache.log4j.Logger


/**
 * @author Nuno Cardoso
 * Tester for ACDCReader class 
 */
public class HTMLReaderAndRembrandtWriterTest extends GroovyTestCase {

     HTMLDocumentReader reader
     RembrandtWriter writer
     RembrandtCorePTforHAREM core
     Document doc
     Configuration conf = Configuration.newInstance()
     List solution
     Logger log = Logger.getLogger("RembrandtTest")
 
     public HTMLReaderAndRembrandtWriterTest() {
	reader = new HTMLDocumentReader(new HTMLStyleTag("pt"))
	writer = new RembrandtWriter(new RembrandtStyleTag("pt"))
	core = new RembrandtCorePTforHAREM(conf)	
	File f = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/HTML_PT_sample.txt")
	File f2 = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/HTML_PT_sample_Rembrandt_output.txt")
	InputStreamReader is = new InputStreamReader(new FileInputStream(f))
	reader.processInputStream(is)
	
	solution = f2.text.split(/\n/)
    }
     
     void testProcess() {
	  // one doc
	 assert  reader.docs.size() == 1 
	   
	 core.releaseRembrandtOnDocument(reader.docs[0])

	 String output = writer.printDocument(reader.docs[0])
	 List l_output = output.split(/\n/)
	 int fails = 0
	 println "<OUTPUT>"
	 println output
	 println "</OUTPUT>"
	 l_output.eachWithIndex{o, i -> 
	     if (o != solution[i]) {
		 log.debug "Line $i: Got $o, should be ${solution[i]} "
		 fails++
	     }
	 }

	 assert fails == 0
	 // now, let's clear the NEs and as  
	 
	 
     }
	 
 }