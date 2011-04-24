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
import saskia.db.GeoSignature;
import saskia.io.*
import org.apache.log4j.*

/**
 * @author Nuno Cardoso
 *
 */
class GeoSignatureTest extends GroovyTestCase{
    
    // doc 1:
    // Porto fica a norte de Portugal. 
    // Lisboa fica mais a sul. 
    // Acima do Porto fica Braga.
    // 5 NEs
    
    static Logger log = Logger.getLogger("SaskiaTest")
    Configuration conf = Configuration.newInstance()
    String xml
    Map hash 
    
    public GeoSignatureFactoryTest() {}
    
  
    void testGenerate() {
        GeoSignature gs = new GeoSignature()
        xml = """
         <GeoSignature version="1.0" totalcount="12">
        <Doc id="539226" original_id="2ght33" lang="pt" />
        <Place count="10" woeid="2391585">
           <NE id="921831">Detroit</NE>
           <Name>Detroit</Name>
           <Type>@HUMANO</Type>
           <DBpediaClass>Area</DBpediaClass>
           <Ancestor woeid="12588795">Wayne</Ancestor>
           <Ancestor woeid="2347581">Michigan</Ancestor>
           <Ancestor woeid="23424977">Estados Unidos da América</Ancestor>
        </Place>
        <Place count="1" woeid="2379574">
           <NE id="921838">Chicago</NE>
           <Name>Chicago</Name>
           <Type>@HUMANO</Type>
           <DBpediaClass>Area</DBpediaClass>
           <Ancestor woeid="12588093">Cook</Ancestor>
           <Ancestor woeid="2347572">Illinois</Ancestor>
           <Ancestor woeid="23424977">Estados Unidos da América</Ancestor>
        </Place>
        <Place count="1" woeid="1580913">
           <NE id="921863">Durban</NE>
           <Name>Durban</Name>
           <Type>@HUMANO</Type>
           <DBpediaClass>Area</DBpediaClass>
           <Ancestor woeid="55921367">Durban</Ancestor>
           <Ancestor woeid="2346982">Kwazulu Natal</Ancestor>
           <Ancestor woeid="23424942">South Africa</Ancestor>
        </Place>
      </GeoSignature>
           """
 
        gs.parse(xml)
        assert gs.version == "1.0"
        assert gs.totalcount == 12
        assert gs.doc_id == 539226
        assert gs.doc_original_id == "2ght33"
        assert gs.doc_lang == "pt"
        assert gs.places.size() == 3
        assert gs.places[2].count == 1
        assert gs.places[2].woeid == 1580913
        assert gs.places[2].ne == "Durban"
        assert gs.places[2].name == "Durban"
        assert gs.places[2].type == "@HUMANO"
        assert gs.places[2].dbpediaclass == "Area"
        assert gs.places[2].ancestors*.name.sort() == ["Durban",'Kwazulu Natal',"South Africa"]
        
        assert gs.places[2].ancestors*.woeid.sort() == [23424942, 2346982, 55921367]        
   }
}
       
 