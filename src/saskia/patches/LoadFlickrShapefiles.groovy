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
package saskia.patches

import saskia.io.*
import saskia.ontology.GeoPlanetAPI
import org.apache.log4j.*
import org.apache.commons.cli.*

/**
 * @author Nuno Cardoso
 * Loads a shapefile from http://code.flickr.com/blog/2009/05/21/flickr-shapefiles-public-dataset-10/
 * 
 * IMPORTANT: There is a 50k limit of GeoPlanetAPI calls per day.
 */
class LoadFlickrShapefiles {
    
    static Logger log = Logger.getLogger("Patches")
    static GeoPlanetAPI geoplanet = GeoPlanetAPI.newInstance()
    static String ynae
    
    public static void main (String[] args) {
             
        Options o = new Options()
        
        o.addOption("file",true,"File with stuff above")
        o.addOption("from",true,"Start from the given WOEID")
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        if (!cmd.hasOption("file")) {
            log.fatal "No --file arg. Please specify file. Exiting."
            System.exit(0)
        }
        
        Map status_ = [withoutshape:0, withshape:0, shapeadded:0, shapeskipped:0]
        File f = new File(cmd.getOptionValue("file"))
        
        log.info "Reading file $f"
        
        int number_places = 0
        int from_woeid=-1
        
        if (cmd.hasOption("from")) {
            from_woeid = Long.parseLong(cmd.getOptionValue("from"))
        }
        
        boolean inplace = false 
        boolean parse = false
        String buffer = ""
        String woeidString   
        long woeid
        
        f.eachLine{l -> 
            if (l.trim().startsWith("<place ")) {
                inplace = true
                woeidString = l.find(/woe_id="(\d+)"/) {all, g1 -> return g1}
                woeid = Long.parseLong(woeidString)
                if (!parse && (from_woeid == -1 || (from_woeid > 0 && from_woeid == woeid)) ) {
                    parse = true
                }
                if (parse) {
                    buffer = l
                    log.info "Reading info from Flickr shapefile for WOEID $woeid"
                } else {
                    log.info "Skipping WOEID $woeid"
                }
            } else if (l.trim().startsWith("</place>")) {
        	inplace = true
        	if (parse) buffer += l
            
        	number_places++
                if (number_places % 1000 == 0) {
                    log.info "Read place number $number_places" 
                }
        	
                Map status = parsePlace(buffer, woeid)
                status.each{k, v -> status_[k] += v}
        	// Parse it
            } else {
        	if (inplace && parse) buffer += l
            }
            
        }
        log.info "Done. Processed ${status_.withshape} places with shape and ${status_.withoutshape} without shape. "
        log.info "Added ${status_.shapeadded} shapes, skipped ${status_.shapeskipped} shapes. "   
     }
    
     static Map parsePlace(String text, long woeid) {
        
      //  println "woeid: $woeid"
        Map status = [withoutshape:0, withshape:0, shapeadded:0, shapeskipped:0]
        
        List shapes = text.findAll(/<shape .*?\/shape>/)
        // let's select the one with most recent creation date
        String bestshape
        if (shapes) {
            bestshape = shapes[0]
            String bestdateString = bestshape.find(/<shape created="(\d+)"/) {all, g1 -> g1 }
            long bestdate = Long.parseLong(bestdateString)
            if (shapes.size() > 1) {
        	for(int i=1; i<shapes.size(); i++) {
        	    String candidateString = shapes[i].find(/<shape created="(\d+)"/) {all, g1 -> return Long.parseLong(g1) }
        	    long candidate =Long.parseLong(candidateString)
        	    if (candidate > bestdate) {
        		bestdate = candidate
        		bestshape = shapes[i]
        	    }
        	}
            }
        }
        
        if (bestshape) {
            status.withshape++
            
        println "Got shape for woeid $woeid"
        // let's check if we have a Geoscope for this woeid
        
        Geoscope geo = Geoscope.getFromWOEID(woeid)     
        if (!geo) {
            println "There is no geoscope for WOEID $woeid. Getting one."
            geo = Geoscope.fetchPlaceForWOEID(woeid)
            
            /* sometimes, GeoPlanet returns a different woeid ! 
            For example, http://where.yahooapis.com/v1/place/46 returns the same stuff as  
		http://where.yahooapis.com/v1/place/29375050, it's a redirection.
		So, believe in the one being returned.*/
            if (geo.geo_woeid != woeid) {
        	log.warn "Hey, queried for WOEID $woeid, got a different WOEID, ${geo.geo_woeid}. It's a redirection."
        	woeid = geo.geo_woeid
            }
            
        } 
        println "Geoscope: $geo"
        
        // now let's get the place (that is, the Yahoo! mashed XML) associated. 
        Place place = new Place(geo.geo_woeid_place)
        
        boolean writeShape = true
        // does it has a shape?
        if (place.shape) {
            if ((!ynae) || (ynae == "y") || (ynae == "n")) {
                ynae = null
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
                while (!(ynae == "a" || ynae == "e" || ynae == "n" || ynae == "y")) {
                    println "Place $place has a shape. Overwrite it? ([y]es, [a]lways, [n]o, n[e]ver)"
                    ynae = input.readLine().trim()
                }
            }
            if ( (ynae == "n") || (ynae == "e")) {
                log.warn "Skipping shape."  
                writeShape = false
                
            } 
        }
        
        if (writeShape)  { // also default when there is no shape, and we have a bestshape
            //writing shape to Place
            println "BestShape: $bestshape"
            def xml = new XmlParser().parseText("<?xml version=\"1.0\" ?>"+bestshape)
            place.shape = [:]
            place.shapeattributes = [:]
            xml.attributes.each{k, v -> place.shapettributes[k] = v}               
            xml.children().each{c2 ->              
            	if (c2.name().equalsIgnoreCase("polylines")) {
            	    place.shape["polylines"] = [:]
            	    c2.attributes.each{k, v -> place.shape["polylines"][k] = v}
                    c2.children().each{c3 ->                        
                    	if (c3.name().equalsIgnoreCase("polyline")) {
                    	    place.shape["polylines"]["polyline"] = c3.text()
                    	}
                    }
                 }
             }
            println "Shape in it: ${place.shape}"
            geo.geo_woeid_place = place.writeXML()
            geo.updatePlaceInDB()
            status.shapeadded++      
        	
        } else {status.shapeskipped++}
     
     	} // if (bestshape)
        else {
            status.withoutshape++
        }
        return status
    }     
}
