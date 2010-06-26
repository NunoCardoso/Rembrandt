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
package saskia.ontology

import saskia.bin.Configuration
import org.apache.log4j.Logger
import groovy.xml.Namespace

/**
 * @author Nuno Cardoso
 * 
 * This is a GeoPlanet API interface 
 *
 */
class GeoPlanetAPI {  
    
    static Logger log = Logger.getLogger("GeoPlanetAPI")
    static GeoPlanetAPI _this
    static String apikey
    static String queryPlaceURL = "http://where.yahooapis.com/v1/places.q" 
    static String placeURL = "http://where.yahooapis.com/v1/place/"	
    static Namespace xmlns = new groovy.xml.Namespace('http://www.w3.org/XML/1998/namespace')
    static Namespace rng = new groovy.xml.Namespace('http://where.yahooapis.com/v1/schema.rng')
    static Namespace yahoo = new groovy.xml.Namespace('http://www.yahooapis.com/v1/base.rng')
    static Map types = [
						"Town":7,
		// 	One of the major populated places within a country. This category includes incorporated cities 
		// and towns, major unincorporated towns and villages.
						"Admin":8, 	
		//One of the primary administrative areas within a country. Place type names associated 
		// with this place type include: State, Province, Prefecture, Country, Region, Federal District.
						"Admin2":9, 	
		// One of the secondary administrative areas within a country. Place type names associated 
		// with this place type include: County, Province, Parish, Department, District.
						"Admin3":10,
		//	One of the tertiary administrative areas within a country. Place type names associated with 
		// this place type include: Commune, Municipality, District, Ward.
						"Postal Code":11,
		// 	One of the postal code areas within a country. This category includes both full postal codes 
		//(such as those in UK and CA) and partial postal codes. Examples include: SW1A 0AA (UK), 
		//90210 (US), 179-0074 (JP).
						"Country":12,
	   // One of the countries and dependent territories defined by the ISO 3166-1 standard.
						"Island":13,
						"Airport":14, // pt: Aeropuerto
						"Drainage":15, // Drenagem
						"Land Feature":16, // Terra Característica
						"Supername":19,
		// 	A place that refers to a region consisting of multiple countries or an historical country 
		// that has been dissolved into current countries. Examples include Scandinavia, Latin America, 
		// USSR, Yugoslavia, Western Europe, and Central America.
						"Point of Interest":20, 
						"Suburb":22,
		// 	One of the subdivisions within a town. This category includes suburbs, neighborhoods, wards.
						"Colloquial":24,
		// Examples are New England, French Riviera, 関西地方(Kansai Region), South East England, Pacific States, 
		// and Chubu Region.
						"Historical State":26, //"Estadual Histórico"
						"Historical County":27, // Condado Histórico
						"Continent":29,
  	   // One of the major land masses on the earth. GeoPlanet is built on a seven-continent model: 
	   // Asia (24865671), Africa (24865670), North America (24865672), South America (24865673), 
	   // Antarctica (28289421), Europe (24865675), and Pacific (Australia, New Zealand, and the 
	   // other islands in the Pacific Ocean -- 24865674).
						"Time Zone":31,
		// A place that refers to an area defined by the Olson standard. Examples include America/Los Angeles, 
		// Asia/Tokyo, Europe/Madrid.]
						"Historical Town":35, //Cidade Histórica<
						"Sea":38
		]
    Configuration conf
/*
http://where.yahooapis.com/v1/places.q('Portugal')?ap
http://where.yahooapis.com/v1/place/23424925/children?appid=4BRSnHPV34HYpwBrOfGATRwQnVKjocXU9ptBmEidG7UBOQF4LzCwdvHVipMZ_W8C8Fym

// obter distritos de Portugal
http://where.yahooapis.com/v1/place/23424925/children.type(8)?count=0&appid=4BRSnHPV34HYpwBrOfGATRwQnVKjocXU9ptBmEidG7UBOQF4LzCwdvHVipMZ_W8C8Fym

http://where.yahooapis.com/v1/place/23424925/descendants.type(7)?appid=4BRSnHPV34HYpwBrOfGATRwQnVKjocXU9ptBmEidG7UBOQF4LzCwdvHVipMZ_W8C8Fym

count=100

type(12)
?appid=4BRSnHPV34HYpwBrOfGATRwQnVKjocXU9ptBmEidG7UBOQF4LzCwdvHVipMZ_W8C8Fym


Portugal: 23424925
places.type(12)

*/
    
    /**
     * Get instance of the GeoPlanet singleton
     */
    public static GeoPlanetAPI newInstance(conf_ = null) {
	Configuration c
	if (!conf_) c = Configuration.newInstance() else c = conf_ 
	apikey = c.get("saskia.geoplanet.apikey")
	if (!apikey) {
	    log.fatal "No GeoPlanet API Key? Get one now!" 
	}
	if (!_this) _this = new GeoPlanetAPI(c)
    
       // SETTING PROXIES
	boolean setProxy = c.getBoolean("saskia.geoplanet.proxy.enabled",false)
	if (!setProxy) log.info "Proxy if OFF for GeoPlanet web service."
	else {
	    System.setProperty("http.proxySet","true")   
            System.setProperty("http.proxyHost",c.get("saskia.geoplanet.proxy.host"))   
            System.setProperty("http.proxyPort",c.get("saskia.geoplanet.proxy.port"))   
            log.info "Proxy if ON for GeoPlanet web service, set to "+
             	System.getProperty("http.proxyHost")+":"+System.getProperty("http.proxyPort")
        }

	log.info "GeoPlanetAPI initialized"
	return _this
    }

    private GeoPlanetAPI(Configuration conf) {
	this.conf=conf
    }

    public String getURLtoSolvePlacenameToPlace(String place, String lang) {
	return queryPlaceURL+"('"+java.net.URLEncoder.encode(place, "UTF-8")+"')?lang=${lang}&appid="+apikey
    }
    
    public String getURLofPlaceForWOEID(long woeid, String lang = null) {
        String l = (lang ? "lang=${lang}&" : "")
        return placeURL+woeid+"?${l}appid="+apikey
    }
    
    public String getURLofAncestorsForWOEID(long woeid, String lang = null) {
        // no need of select=long, I just want WOEIDs
        String l = (lang ? "lang=${lang}&" : "")
        return placeURL+woeid+"/ancestors?${l}appid="+apikey
    }

//http://where.yahooapis.com/v1/place/55949068/children;count=0?lang=en&appid=
     public String getURLofDescendentsForWOEID(long woeid, String lang = null) {
        // no need of select=long, I just want WOEIDs
        String l = (lang ? "lang=${lang}&" : "")
        return placeURL+woeid+"/children;count=0?${l}appid="+apikey
    }

    /**
     * this fetches GeoPlanet info for PT and EN for a placename
     * merges it, and adds a time stamp, adds a 200 or a 404 code
     */
    String getPlaceXMLByPlacename(String place) {
	String result = mergePlaceInfo(fetchPlaceForPlacename(place))
	String date = String.format('%tF %<tT', new Date()) // format: 1970-01-01 01:00:00
	if (result) return "200\t${date}\t$result"
        else return "404\t${date}"
     }
    
    /**
     * this fetches GeoPlanet info for PT and EN for a woeid
     * merges it, and adds a time stamp, adds a 200 or a 404 code
     */
    String getPlaceXMLByWOEID(long woeid) {
        String result = mergePlaceInfo(fetchPlaceForWOEID(woeid))
	String date = String.format('%tF %<tT', new Date()) // format: 1970-01-01 01:00:00
        if (result) return "200\t${date}\t$result"
        else return "404\t${date}"
    }
    
    /**
     * Fetch the contents of a Web page
     * @param string the URL string
     * @return the page string
     */
    public String fetchURL(String string_url) {
        
        URL url = string_url.toURL()
        def connection = url.openConnection()
        if(connection.responseCode == 200) {
            //println "Got 200!"
            return connection.content.text
        }else if (connection.responseCode == 404) {
            log.warn "Got a 404 code for URL "+string_url
            return null
        }else {
            log.warn " Got Code: "+connection.responseCode
            log.warn "Message: "+connection.responseMessage
   // connection.headerFields.each{println it}
   // println "Text: "+connection.getContent()
            System.exit(0)
        }
        return null
    }

    
    
    /** 
     * Use this to merge info for a single place in several languages
     */
    public String mergePlaceInfo(List<String> places) {
        if (!places) return null
        Node xml  
        places.each{p ->
            
            Node place = new XmlParser().parseText(p)
            // note: it can be "places" or "place". 

            // note: it can also return <places yahoo:start="0" yahoo:count="0" yahoo:total="0"/>
            if (place.attribute(yahoo.count) == 0) {
        	log.warn "Note: Got 0 places"
                return ""
            }
            
            // if it's "places", let's use place'0' 
            if (place.name() == rng.places) place = place.place[0]    
            
           if (!place) {
                log.warn "Note: Got 0 places"
                return ""
            }
        
            //println "place: $place"
            String lang_place = place.attribute(xmlns.lang)    
            place.attributes().remove(xmlns.lang) // remove lang info from header

            //change placeTypeName, name, country, admin1-3, locality1-2         
            place.placeTypeName?.getAt(0)?.'@lang' = lang_place
            place.name?.getAt(0)?.'@lang' = lang_place
            place.country?.getAt(0)?.'@lang' = lang_place
            place.admin1?.getAt(0)?.'@lang' = lang_place
            if (place.admin2?.getAt(0)?.value()) place.admin2?.getAt(0)?.'@lang' = lang_place
            if (place.admin3?.getAt(0)?.value()) place.admin3?.getAt(0)?.'@lang' = lang_place
            place.locality1?.getAt(0)?.'@lang' = lang_place
            place.locality2?.getAt(0)?.'@lang' = lang_place
            
            // now, let's merge stuff
            if (!xml) xml = place
            else {
                // first, let's check if were's talking about the same place
                if (xml.woeid[0].value() != place.woeid[0].value()) 
                    throw new IllegalStateException("WOEIDs don't match!")
                
                if (!xml.placeTypeName.find{it.attribute('lang') == lang_place})  {
                    Map args = place.placeTypeName?.getAt(0)?.attributes()
                    args['lang'] = lang_place
                    xml.appendNode('placeTypeName', args, place.placeTypeName?.getAt(0)?.value())
                }
                
                if (!xml.name.find{it.attribute('lang') == lang_place})  {
                    Map args = place.name?.getAt(0)?.attributes()
                    args['lang'] = lang_place
                    xml.appendNode('name', args, place.name?.getAt(0)?.value())
                }
                
                if (!xml.country.find{it.attribute('lang') == lang_place})  {
                    Map args = place.country?.getAt(0)?.attributes()
                    args['lang'] = lang_place
                    xml.appendNode('country', args, place.country?.getAt(0)?.value())
                }
                
                if (!xml.admin1.find{it.attribute('lang') == lang_place})  {
                    Map args = place.admin1?.getAt(0)?.attributes()
                    args['lang'] = lang_place
                    xml.appendNode('admin1', args, place.admin1?.getAt(0)?.value())
                }
                
                if (!xml.admin2.find{it.attribute('lang') == lang_place})  {
                    Map args = place.admin2?.getAt(0)?.attributes()
                    args['lang'] = lang_place
                    xml.appendNode('admin2', args, place.admin2?.getAt(0)?.value())
                }
                
                if (!xml.admin3.find{it.attribute('lang') == lang_place})  {
                    Map args = place.admin3?.getAt(0)?.attributes()
                    args['lang'] = lang_place
                    xml.appendNode('admin3', args, place.admin3?.getAt(0)?.value())
                }       
                if (!xml.locality1.find{it.attribute('lang') == lang_place})  {
                    Map args = place.locality1?.getAt(0)?.attributes()
                    args['lang'] = lang_place
                    xml.appendNode('locality1', args, place.locality1?.getAt(0)?.value())
                }
                
                if (!xml.locality2.find{it.attribute('lang') == lang_place})  {
                    Map args = place.admin3?.getAt(0)?.attributes()
                    args['lang'] = lang_place
                    xml.appendNode('locality2', args, place.locality2?.getAt(0)?.value())
                }
            }// else 
        }// each place
        
        return printXML(xml)
    }
    
    public String printXML(Node xml) {
	def writer = new StringWriter()
	def printer = new XmlNodePrinter(new PrintWriter(writer))
	printer.setPreserveWhitespace(true)
	printer.print(xml)
	return writer.toString() 
    }
   
    /**
     * Queries Yahoo! GeoPlanet for a place, using 
     * Calls 2 times the API, one of PT, other for EN
     */
    public List<String> fetchPlaceForPlacename(String place) {
	String place_pt = fetchURL(getURLtoSolvePlacenameToPlace(place, "pt"))
        log.info "Fetching place for Placename $place lang pt, got an answer with ${place_pt?.size()} bytes."
        String place_en = fetchURL(getURLtoSolvePlacenameToPlace(place, "en"))
        log.info "Fetching place for Placename $place lang en, got an answer with ${place_en?.size()} bytes."
        if (!place_pt && !place_en) return null // when gives 404 for an unknown WOEID
        return [place_pt, place_en]
    }
    
    public List<String> fetchPlaceForWOEID(long woeid) {
        String place_pt = fetchURL(getURLofPlaceForWOEID(woeid, "pt"))
        log.info "Fetching place for WOEID $woeid, lang pt, got an answer with ${place_pt?.size()} bytes."
        String place_en = fetchURL(getURLofPlaceForWOEID(woeid, "en"))
        log.info "Fetching place for WOEID $woeid, lang en, got an answer with ${place_pt?.size()} bytes."
        if (!place_pt && !place_en) return null // when gives 404 for an unknown WOEID
        return [place_pt, place_en]
    }
    
    /**
     * Queries Yahoo! GeoPlanet for ancestors
     * @param the WOEID
     * @return String with the list of WOEIDS, separated by white spaces
     */
    public List<Long> fetchAncestorsForWOEID(long woeid) {
        List<Long> woeids = []
        String xml_ancestors = fetchURL(getURLofAncestorsForWOEID(woeid))
        log.info "Fetching ancestors for WOEID $woeid, got an answer with ${xml_ancestors} bytes."
   		// note that we may not get ancestors (ex: Africa)
        if (xml_ancestors) {
			Node places = new XmlParser().parseText(xml_ancestors)
        	places.place.each{p -> 
           		woeids << Long.parseLong(p.woeid[0].text())
        	}
		}
        return woeids
    }

 /**
     * Queries Yahoo! GeoPlanet for children
     * @param the WOEID
     * @return String with the list of WOEIDS, separated by white spaces
     */
    public List<Long> fetchDescendentsForWOEID(long woeid) {
        List<Long> woeids = []
        String xml_descendents = fetchURL(getURLofDescendentsForWOEID(woeid))
        log.info "Fetching descendents for WOEID $woeid, got an answer with ${xml_descendents} bytes."   
        // note that we may not get descendents (ex: Africa)
        if (xml_descendents) {
			Node places = new XmlParser().parseText(xml_descendents)
        	places.place.each{p -> 
           		woeids << Long.parseLong(p.woeid[0].text())
        	}
		}

        return woeids
    }
}