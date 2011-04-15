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

import rembrandt.io.ACDCReader

import org.junit.*
import rembrandt.bin.*
import rembrandt.io.*
import saskia.bin.Configuration
import org.junit.runner.*
import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 * Tester for ACDCReader class 
 */
public class ACDCReaderAndACDCWriterTest extends GroovyTestCase {

     ACDCReader reader
     ACDCWriter writer
     RembrandtCore core
     Configuration conf
     String text 
     List text_output 
     Logger log = Logger.getLogger("RembrandtTest")
     
    public ACDCReaderAndACDCWriterTest() {
	 conf = Configuration.newInstance()
	 conf.setBoolean("rembrandt.output.styletag.verbose",false)
	 reader = new ACDCReader()
	 writer = new ACDCWriter(new ACDCStyleTag("pt"))
	 core = new RembrandtCorePTforHAREM(conf)
	 File f = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/ACDC_PT_sample.txt")
	 File f2 = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/ACDC_PT_sample_ACDC_output.txt")
	 text = f.text
	 text_output = f2.text.split(/\n/)
	 reader.processString(text)
     }
     
     void testProcess() {
	    // one doc
	 assert  reader.docs.size() == 1 
	   // four sentences
	 assert reader.docs[0].body_sentences.size() == 4, "Got "+reader.docs[0].body_sentences.size()+" instead."
	   
	 core.releaseRembrandtOnDocument(reader.docs[0])

	 String output = writer.printDocument(reader.docs[0])
	 List l_output = output.split(/\n+/)
	 int fails = 0
	 println "<OUTPUT>"
	 println output
	 println "</OUTPUT>"
	 l_output.eachWithIndex{o, i -> 
	     if (o != text_output[i]) {
		 log.debug "Line $i: Got $o, should be ${text_output[i]} "
		 fails++
	     }
	 }

	 assert fails == 0
	 // now, let's clear the NEs and as  
	 
	 }
 }