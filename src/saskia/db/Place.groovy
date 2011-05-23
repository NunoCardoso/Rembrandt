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
package saskia.db

import org.apache.log4j.*
import groovy.xml.MarkupBuilder
import saskia.ontology.GeoPlanetAPI


/** This class is an object built from a GeoPlanet-ish XML post-processed element from the 
  *  GeoPlanetAPI and from Flickr shapefiles, that stores it on Geoscope.geo_woeid_place.
  *  It's a huge mashup.
  *  
  *  An example:
  * <PRE>
  * <place xmlns="http://where.yahooapis.com/v1/schema.rng" yahoo:uri="http://where.yahooapis.com/v1/place/742676" xmlns:yahoo="http://www.yahooapis.com/v1/base.rng">
  * <woeid>742676</woeid>
  * <placeTypeName code="7" lang="pt">Pueblo</placeTypeName>
  * <name lang="pt">Lisboa</name>
  * <country type="País" code="PT" lang="pt">Portugal</country>
  * <admin1 type="Distrito" code="PT-11" lang="pt">Lisboa</admin1>
  * <admin2 type="Municipality" code="" lang="pt">Lisboa</admin2>
  * <admin3/>
  * <locality1 type="Pueblo" lang="pt">Lisboa</locality1>
  * <locality2 lang="pt"/>
  * <postal/>
  * <centroid>
  *   <latitude>38.725670</latitude>
  *   <longitude>-9.150370</longitude>
  * </centroid>
  * <boundingBox>
  *   <southWest>
  *     <latitude>38.713249</latitude>
  *   <longitude>-9.248300</longitude>
  *  </southWest>
  *  <northEast>
  *    <latitude>38.818378</latitude>
  *    <longitude>-9.108760</longitude>
  *  </northEast>
  * </boundingBox>
  * <placeTypeName code="7" lang="en">Town</placeTypeName>
  *   <name lang="en">Lisbon</name>
  *   <country type="Country" code="PT" lang="en">Portugal</country>
  *   <admin1 type="District" code="" lang="en">Lisbon</admin1>
  *   <admin2 type="Municipality" code="" lang="en">Lisbon</admin2>
  *   <admin3 lang="en"/>
  *   <locality1 type="Town" lang="en">Lisbon</locality1>
  *   <locality2 lang="en"/>
  *   <shape created="1226808620" alpha="0.00244140625" points="48" edges="18" is_donuthole="0">
  *      <polylines bbox="45.289924621582,-64.878128051758,45.383140563965,-64.686729431152">
  *       <polyline>45.289924621582,-64.774787902832 45.294815063477,-64.777793884277 45.343334197998,-64.815002441406 
  *       45.348041534424,-64.857444763184 45.3469581604,-64.878128051758 45.35835647583,-64.846374511719 
  *       45.369331359863,-64.826805114746 45.335918426514,-64.783111572266 45.334953308105,-64.763374328613 
  *       45.352397918701,-64.725791931152 45.383140563965,-64.686729431152 45.356727600098,-64.708786010742 
  *       45.354694366455,-64.710144042969 45.352397918701,-64.725791931152 45.303268432617,-64.764144897461 
  *       45.290828704834,-64.768005371094 45.290103912354,-64.769035339355 45.289924621582,-64.774787902832
  *       </polyline>
  *     </polylines>
  *    <shapefile url="http://farm4.static.flickr.com/3252/shapefiles/4_20081116_2be02ab71c.tar.gz" />
  *    </shape>
  * </place>
  * </PRE>
  *
*/

class Place {
 
    long woeid
    static Logger log = Logger.getLogger("GeoSignature")
    
    Map placeattributes = [:]
    Map<String,String> name = [:]
    Map<String,String> placetypename = [:]  
    Map<String,Integer> placetypenamecode = [:]  
                                            
    Map<String,String> country = [:]
    Map<String,String> countrycode = [:]   

    Map<String,String> admin1 = [:]
    Map<String,String> admin1code = [:]
    Map<String,String> admin1type = [:]
                                     
    Map<String,String> admin2 = [:]
    Map<String,String> admin2code = [:]
    Map<String,String> admin2type = [:]
                                                                      
    Map<String,String> admin3 = [:]
    Map<String,String> admin3code = [:]
    Map<String,String> admin3type = [:]
                                                                      
    Map<String,String> locality1 = [:]
    Map<String,String> locality1code = [:]  
    Map<String,String> locality1type = [:]
                                                                         
    Map<String,String> locality2 = [:]
    Map<String,String> locality2code = [:]
    Map<String,String> locality2type = [:]
                                                                         
    Map<String,String> postal = [:]   
    Map<String,String> centroid = [:]
    Map<String, Map<String,String>> boundingbox = [:]
                                                   
    Map shape
    Map shapeattributes
                                                                                                   
    public Place(String xml = null) {
    	if (xml) readXML(xml)
    }
    
    public String toString() {
    	return "name:$name;country:$country;admin1:$admin1;admin2:$admin2;admin3:$admin3;locality1:$locality1;locality2:$locality2"
    }
    
    static List<Long> parseAncestorWOEIDInfo(String ancestors) {
    	List<Long> l = []
    // first two lines are a status and a date
    	String xml
    	int status
    	Date date
        ancestors.find(/(?s)^(\d+?)\t(\d+-\d+-\d+ \d+:\d+:\d+)[\s\t]*?(.*)$/) {all, g1, g2, g3 -> 
           status = Integer.parseInt(g1)
           try {
               date = Date.parse("yyyy-m-d k:m:s", g2)
	   } catch(Exception e) {
	       log.warn "Can't parse date $g2"
	   }
	   if (status == 200) {
	       l = g3.trim().split(";").collect{Long.parseLong(it)}
	   }
	}
        //println "Got $l"
    	return (l ? l : null)    
    }
    
 	static List<Long> parseDescendentWOEIDInfo(String descentents) {
    	List<Long> l = []
    // first two lines are a status and a date
    	String xml
    	int status
    	Date date
        descentents.find(/(?s)^(\d+?)\t(\d+-\d+-\d+ \d+:\d+:\d+)[\s\t]*?(.*)$/) {all, g1, g2, g3 -> 
           status = Integer.parseInt(g1)
           try {
               date = Date.parse("yyyy-m-d k:m:s", g2)
	   } catch(Exception e) {
	       log.warn "Can't parse date $g2"
	   }
	   if (status == 200) {
	       l = g3.trim().split(";").collect{Long.parseLong(it)}
	   }
	}
        //println "Got $l"
    	return (l ? l : null)    
    }

    void readXML(String string) {
         
    	// first two lines are a status and a date
    	String xml
    	int status
    	Date date
    	string.find(/(?s)^(\d+?)\t(.*?)\t(.*)$/) {all, g1, g2, g3 -> 
    	   status = Integer.parseInt(g1)
    	   date = Date.parse("yyyy-m-d k:m:s", g2)
    	   xml = g3
    	} 
    
    	if (status == 404) return
        if (status == 301) return
        if (!xml) return
        
    	Node place = new XmlParser().parseText(xml)
        placeattributes = place.attributes()
        
    	place.children().each{c -> 
    	    switch(c.name().getQualifiedName().toLowerCase()) {
    	       case "woeid":
    		   woeid = Long.parseLong(c.text())
    	       break
    			
    	       case "placetypename":
    		   String lang = c.attribute("lang")
    		   if (lang && c.text()) {
    		       placetypename[lang] = c.text()
    		       placetypenamecode[lang] =Integer.parseInt(c.attribute("code"))
    		   }    
    		break
    			
    	       case "name":
    		   String lang = c.attribute("lang")
    		   name[lang]=c.text()				
    		break	
			
    	       case "country":
    		   String lang = c.attribute("lang")
    		   String code = c.attribute("code")
    		   country[lang] = c.text()
    		   if (code) countrycode[lang]=code
    		break	
 
    	       case "admin1":
    		   String lang = c.attribute("lang")
    		   String code = c.attribute("code")
    		   String type = c.attribute("type")
    		   if (c.text()) admin1[lang]=c.text()	
    		   if (code) admin1code[lang]=code
    		   if (type) admin1type[lang]=type
    		break	
    		
    	       case "admin2":
    		   String lang = c.attribute("lang")
    		   String code = c.attribute("code")
    		   String type = c.attribute("type")
    		   if (c.text()) admin2[lang]=c.text()	
    		   if (code) admin2code[lang]=code
    		   if (type) admin2type[lang]=type
    		break	
				
    	       case "admin3":
    		   String lang = c.attribute("lang")
    		   String code = c.attribute("code")
    		   String type = c.attribute("type")
    		   if (c.text()) admin3[lang]=c.text()	
    		   if (code) admin3code[lang]=code
    		   if (type) admin3type[lang]=type
    		   break		
				
    	       case "locality1":
    		   String lang = c.attribute("lang")
    		   String code = c.attribute("code")
    		   String type = c.attribute("type")
    		   if (c.text()) locality1[lang]=c.text()
    		   if (code) locality1code[lang]=code
    		   if (type) locality1type[lang]=type
    		   break	
				
    	       case "locality2":
    		   String lang = c.attribute("lang")
    		   String code = c.attribute("code")
    		   String type = c.attribute("type")
    		   if (c.text()) locality2[lang]=c.text()	
    		   if (code) locality2code[lang]=code
    		   if (type) locality2type[lang]=type
    		   break								
            
            case "postal":
                   String type = c.attribute("type")                  
                   if (c.text()) postal[type]=c.text()	                 
                   break		
            
                case "centroid":
            	c.children().each{c2 -> 
                 //   println "c2: $c2 c2.getName=${c2.name()} c2.ng.gqn=${c2.name().getQualifiedName()}"
            	   centroid[c2.name().getQualifiedName()] = c2.text()
            	}
                break
                
                case "boundingbox":
                c.children().each{c2 -> 
                  //  println "c2: $c2 c2.getName=${c2.name()} c2.ng.gqn=${c2.name().getQualifiedName()}"
                    
                    boundingbox[c2.name().getQualifiedName()] = [:]
                    c2.children().each{c3 -> 
                        boundingbox[c2.name().getQualifiedName()]."${c3.name().getQualifiedName()}" = c3.text()
                    }
                }
                
                case "shape":
                shape = [:]
                shapeattributes =[:] 
                
                c.attributes.each{k, v -> shapeattributes[k] = v}               
                c.children().each{c2 ->              
                    if (c2.name().getQualifiedName().equalsIgnoreCase("polylines")) {
                        shape["polylines"] = [:]
                        c2.attributes.each{k, v -> shape["polylines"][k] = v}
                	c2.children().each{c3 ->                        
                	     if (c3.name().getQualifiedName().equalsIgnoreCase("polyline")) {
                		 shape["polylines"]["polyline"] = c3.text()
                	     }
                        }
                    }
                }
                break   		
             }
    	}
    }
    
    public String writeXML() {
        
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
       
        // GeoPlanetAPI.yahoo is a namespace, get('uri') returns a QName
         xml.place(xmlns: GeoPlanetAPI.rng.getUri(), 'yahoo:uri':placeattributes[GeoPlanetAPI.yahoo.get("uri")], 
        'xmlns:yahoo': GeoPlanetAPI.yahoo.getUri()) {
            woeid(woeid)
            placetypename?.each{lang, o -> 	
            	placeTypeName(o, lang:lang, code:placetypenamecode[lang]) 
            }
            name?.each{lang, o -> name(o, lang:lang) }
            country?.each{lang, o -> 
                Map args = [lang:lang]
                if (countrycode?.lang) args[code] = countrycode.lang
                country(o, args) 
            }
            admin1?.each{lang, o -> 
                Map args = [lang:lang]
                if (admin1code?.lang) args[code] = admin1code.lang
                if (admin1type?.lang) args[type] = admin1type.lang                           
                admin1(o, args) 
            }
            admin2?.each{lang, o -> 
                Map args = [lang:lang]
                if (admin2code?.lang) args[code] = admin2code.lang
                if (admin2type?.lang) args[type] = admin2type.lang                           
                admin2(o, args) 
            }
            admin3?.each{lang, o -> 
                Map args = [lang:lang]
                if (admin3code?.lang) args[code] = admin3code.lang
                if (admin3type?.lang) args[type] = admin3type.lang                           
                admin3(o, args) 
            }
            locality1?.each{lang, o -> 
                Map args = [lang:lang]
                if (locality1code?.lang) args[code] = locality1code.lang
                if (locality1type?.lang) args[type] = locality1type.lang                           
                locality1(o, args) 
            }
            locality2?.each{lang, o -> 
                Map args = [lang:lang]
                if (locality2code?.lang) args[code] = locality2code.lang
                if (locality2type?.lang) args[type] = locality2type.lang                           
                locality1(o, args) 
            }
            postal?.each{type, o -> 
                postal(o, type:type) 
            }
            if (centroid) {
        	centroid() {
        	    centroid.each{k, v -> 
                    	if (k.equalsIgnoreCase("latitude")) latitude(v)
                    	if (k.equalsIgnoreCase("longitude")) longitude(v)
                    }
                }
            }
            if (boundingbox) {
                boundingBox() {
                    boundingbox.each{k, v -> 
                        if (k.equalsIgnoreCase("southWest")) southWest() {
                            v.each{k2, v2 -> 
                                if (k2.equalsIgnoreCase("latitude")) latitude(v2)
                                if (k2.equalsIgnoreCase("longitude")) longitude(v2) 
                            }
                        }
                        if (k.equalsIgnoreCase("southEast")) southEast() {
                            v.each{k2, v2 -> 
                                if (k2.equalsIgnoreCase("latitude")) latitude(v2)
                                if (k2.equalsIgnoreCase("longitude")) longitude(v2) 
                            }
                        }
                        if (k.equalsIgnoreCase("northWest")) northWest() {
                            v.each{k2, v2 -> 
                                if (k2.equalsIgnoreCase("latitude")) latitude(v2)
                                if (k2.equalsIgnoreCase("longitude")) longitude(v2) 
                            }
                        }
                        if (k.equalsIgnoreCase("northEast")) northEast() {
                            v.each{k2, v2 -> 
                                if (k2.equalsIgnoreCase("latitude")) latitude(v2)
                                if (k2.equalsIgnoreCase("longitude")) longitude(v2) 
                            }
                        }
                    }
                }
            }// if boundingbox
            
            if (shape) {
        	shape(shapeattributes) {
        	    if (shape?.polylines) {
        		polylines() {
        		    if (shape.polylines?.polyline) {
        			polyline(shape.polylines.polyline) 
        		    }
        		}
        	    }
                }
            } 
        }
        return writer.toString() 
    } 
}   