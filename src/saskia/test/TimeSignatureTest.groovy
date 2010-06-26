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
import saskia.io.*
import org.apache.log4j.*

/**
 * @author Nuno Cardoso
 *
 */
class TimeSignatureTest extends GroovyTestCase{
      
    static Logger log = Logger.getLogger("SaskiaTest")
    Configuration conf = Configuration.newInstance()
    String xml
    Map hash 
    
    public TimeSignatureFactoryTest() {}
    
  
    void testGenerate() {
        TimeSignature ts = new TimeSignature()
        xml = """
      <TimeSignature version="1.0" totalcount="8">
        <Doc id="539353" original_id="hub-81551" lang="pt" />
        <DocDateCreated>19700101</DocDateCreated>
        <Time count="1">
           <NE>1 de Novembro de 1755</NE>
           <TimeGrounding>!:Y+1755M11D01</TimeGrounding>
           <Index>17551101</Index>
        </Time>
        <Time count="1">
           <NE>mar , entre 150 a 500</NE>
           <TimeGrounding>!:M03</TimeGrounding>
           <Index>175503</Index>
        </Time>
        <Time count="1">
           <NE>em 1969</NE>
           <TimeGrounding>!:Y+1969</TimeGrounding>
           <Index>1969</Index>
        </Time>
        <Time count="1">
           <NE>em 1755</NE>
           <TimeGrounding>!:Y+1755</TimeGrounding>
           <Index>1755</Index>
        </Time>
        <Time count="1">
           <NE>2 horas</NE>
           <TimeGrounding>!:h02</TimeGrounding>
           <Index>1755</Index>
        </Time>
      </TimeSignature>     
           """
 
        ts.parse(xml)
        assert ts.version == "1.0"
        assert ts.totalcount == 8
        assert ts.doc_id == 539353
        assert ts.doc_original_id == "hub-81551"
        assert ts.doc_lang == "pt"
        assert ts.date_created == "19700101"
        
        assert ts.timelist.size() == 5
        assert ts.timelist[0].count == 1
        assert ts.timelist[0].ne == "1 de Novembro de 1755"
        assert ts.timelist[0].tg == "!:Y+1755M11D01"
        assert ts.timelist[0].idx == "17551101"
   }
}
       
 