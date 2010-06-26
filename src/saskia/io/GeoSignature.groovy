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
package saskia.io

/**
 * @author Nuno Cardoso
 *<GeoSignature version="1.0" totalcount="12">
  <Doc id="539226" original_id="2ght33" lang="pt">
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
 */
class GeoSignature {
    
    String version
    int totalcount
    long doc_id
    String doc_original_id
    String doc_lang
    List places = []
    
    public GeoSignature() {}
    
    public GeoSignature(DocGeoSignature docgeosig) {
	parse(docgeosig.dgs_signature)
    }
    
    // this iterates the places and looks up Geoscope info to see if it can add polyline info
    void addPolylineInfo() {
        
        for(int i=0; i<places.size(); i++) {
            Map place = places[i]
            Geoscope geo = Geoscope.getFromWOEID(place.woeid)
            if (geo) {
                // parse the XML
        	Place p = new Place(geo.geo_woeid_place)
                if (p.shape?.polylines?.polyline) {
                    place["polyline"]=p.shape.polylines.polyline
                    places[i] = place
                }   
            }
        }
    }
    
    void parse(String string) {
           
    	Node gs = new XmlParser().parseText(string)
    	version = gs.attribute("version")
    	totalcount = Integer.parseInt(gs.attribute("totalcount"))

    	gs.children().each{c -> 
           // println "c: $c class: ${c.class.name}"
            switch(c.name().toLowerCase()) {
                case "doc":
                doc_id = Long.parseLong(c.attribute("id"))
                doc_original_id = c.attribute("original_id")
                doc_lang = c.attribute("lang")
                break
                
                case "place":
                Map place = [:]
                place.ancestors = []
                
                place.woeid = Long.parseLong(c.attribute("woeid"))
                place.count = Integer.parseInt(c.attribute("count"))
                c.children().each{c2 -> 
                   // println "c2:$c2 place = $place"
                    switch(c2.name().toLowerCase()) {  
                        case "ne":
                        place.ne = c2.text()
                        break
                        case "name":
                        place.name = c2.text()
                        break
                        case "type":
                        place.type = c2.text()
                        break
                        case "subtype":
                        place.subtype = c2.text()
                        break
                        case "dbpediaclass":
                        place.dbpediaclass = c2.text()
                        break
                        case "ancestor": 
                        place.ancestors << [name:c2.text(), woeid:c2.attribute("woeid")]                      
                        break
                        case "centroid": 
                        place.centroid = [:]
                        c2.children().each{c3 -> 
                            //   println "c2: $c2 c2.getName=${c2.name()} c2.ng.gqn=${c2.name().getQualifiedName()}"
                            place.centroid[c3.name()] = c3.text()
                        }
                        break
                    }
                }
                places << place
            }
        }
    }
}