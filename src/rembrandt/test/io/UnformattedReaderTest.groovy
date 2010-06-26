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

import rembrandt.io.UnformattedReader
import rembrandt.io.UnformattedWriter
import rembrandt.io.RembrandtStyleTag

import org.junit.*
import org.junit.runner.*

import rembrandt.obj.Document
import saskia.bin.Configuration
import org.apache.log4j.Logger

import org.apache.log4j.Logger


/**
 * @author Nuno Cardoso
 * Tester for ACDCReader class 
 */
public class UnformattedReaderTest extends GroovyTestCase {

    UnformattedReader reader
    UnformattedWriter writer 
    Document doc
    String text
    Configuration conf = Configuration.newInstance()
   
    Logger log = Logger.getLogger("RembrandtTest")
     
     public UnformattedReaderTest() {
        
	reader = new UnformattedReader(new RembrandtStyleTag("pt"))
	File f = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/PlainText_RembrandtTags_PT_sample.txt")
	InputStreamReader is = new InputStreamReader(new FileInputStream(f))
	reader.processInputStream(is)
     }
     
     void testReader() {
	 assert reader.docs.size() == 1 
	 // this doc has already NEs read
	 Document doc = reader.docs[0]
    
        // println doc.bodyNEs
    
         assert doc.bodyNEs[0].sentenceIndex == 0 // rembrandt
         assert doc.bodyNEs[0].termIndex == 1 // rembrandt
        
         assert doc.bodyNEs[1].sentenceIndex == 1 // 25 de Julho de 1606    
         assert doc.bodyNEs[1].termIndex == 2 //  25 de Julho de 1606    
        
         assert doc.bodyNEs[2].sentenceIndex == 1 // Leiden
         assert doc.bodyNEs[2].termIndex == 8 // Leiden
        
         assert doc.bodyNEs[3].sentenceIndex == 1 // Países Baixos    
         assert doc.bodyNEs[3].termIndex == 11 //  Países Baixos    
        
        assert doc.bodyNEs[4].sentenceIndex == 2 // Universidade de Leiden
        assert doc.bodyNEs[4].termIndex == 2 //Universidade de Leiden
        
        assert doc.bodyNEs[5].sentenceIndex == 2 // Leiden    
        assert doc.bodyNEs[5].termIndex == 4 //  Leiden  
                
   
    }

	
 }