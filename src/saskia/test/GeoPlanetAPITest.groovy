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
import saskia.ontology.GeoPlanetAPI
import org.apache.log4j.Logger
/**
 * @author Nuno Cardoso
 *
 */
class GeoPlanetAPITest extends GroovyTestCase {
    
    Configuration conf = Configuration.newInstance()
    GeoPlanetAPI geoplanet
    static Logger log = Logger.getLogger("JUnitTest")
    
    File file_xml_place_pt, file_xml_place_en
    def xmlns = new groovy.xml.Namespace('http://www.w3.org/XML/1998/namespace')
    
    public GeoPlanetAPITest() {
	file_xml_place_pt = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/GeoPlanet_placePT_example.xml")
    	file_xml_place_en = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/GeoPlanet_placeEN_example.xml")
	geoplanet = GeoPlanetAPI.newInstance(conf)
    }    

    // commented to save API calls
    
/*   void testFetchPlace() {        
        // this is how it should be
        log.info "Test fetching places from GeoPlanet..."
        
        List<String> places = geoplanet.fetchPlaceForPlacename('Lisboa')
        assert places[0] == file_xml_place_pt.text.replaceAll(/\n/,"").trim(), \
        	"places[0] is ${places[0]}, file_xml_place_pt.text is ${file_xml_place_pt.text}"
        assert places[1] == file_xml_place_en.text.replaceAll(/\n/,"").trim(), \
        	"places[1] is ${places[1]}, file_xml_place_en.text is ${file_xml_place_en.text}"
    } 
  */

    void testMergePlaces() {
	List<String> places =[file_xml_place_pt.text.replaceAll(/\n/,"").trim(),
                              file_xml_place_en.text.replaceAll(/\n/,"").trim()] 
        String mergePlace = geoplanet.mergePlaceInfo(places)

	file_xml_placemerge = new File(conf.get("rembrandt.home.dir",".")+
              "/resources/test/GeoPlanet_placeMerge_example.xml")
    
	assert mergePlace == file_xml_placemerge.text.trim()
    }
    
    
        /*def xml_place_pt = new XmlParser().parse(file_xml_place_pt)
        def xml_place_en = new XmlParser().parse(file_xml_place_en)
        String xml_place_pt_string = geoplanet.printXML(xml_place_pt)
        String xml_place_en_string = geoplanet.printXML(xml_place_en)

    
        println mergePlaceInfo([xml_place_pt, xml_place_en])   */
       
}