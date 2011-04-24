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

import saskia.bin.Configuration
import saskia.db.obj.Entity
import saskia.db.table.GeoscopeTable
import saskia.db.obj.Geoscope
import saskia.dbpedia.DBpediaOntology
import org.apache.log4j.Logger
import rembrandt.util.XMLUtil

/**
 * @author Nuno Cardoso
 * XML representation of a GeoSignature
 * 
 * <GeoSignature totalcount="32">
 * <Doc id="35235" original_id="NYT_2525" lang="pt">
 * <Place titlecount="0" bodycount="20" woeid="3342523"> 
 *     <name>Califórnia</name> 
 *     <alternativename>Calif</alternativename>
 *     <type>human</type>
 *     <subtype>division</subtype>
 *     <dbpediaClass>Place</dbpeidaClass>
 *     <ancestor WOEID="357246">Estados Unidos da América</ancestor>
 * </Place>
 * <Place titlecount="1" bodycount="12" WOEID="45235">
 *     <name>Lisboa</name>
 *     <ancestor WOEID="73682">Lisboa</ancestor>
 *     <ancestor WOEID="336235">Portugal</ancestor>
 * </Place>
 * </GeoSignture>
 *
 */
class GeoSignatureFactory {
    
    // for the tag
    static Logger log = Logger.getLogger("GeoSignature")
    static String geoSignatureVersionNumber = "1.0"
    static String geoSignatureVersionLabel="GeoSignature v"+geoSignatureVersionNumber
    static Configuration conf = Configuration.newInstance()
    Geoscope nullgeo
    static LinkedHashMap<Long,Geoscope> entityGeoscopeCache = \
        new LinkedHashMap(conf.getInt("saskia.geoscope.cache.number",1000), 0.75f, true) // true: access order.  
    static LinkedHashMap<Long,Geoscope> woeidGeoscopeCache = \
        new LinkedHashMap(conf.getInt("saskia.geoscope.cache.number",1000), 0.75f, true) // true: access order.  
	static DBpediaOntology dbpediaontology = DBpediaOntology.getInstance()
	GeoscopeTable gt
	
    public GeoSignatureFactory(GeoscopeTable gt) {
		this.gt = gt
        println "Generating null geo"
        nullgeo = gt.getFromWOEID(0)
        println "Got $nullgeo"
        nullgeo.geo_name = null
    }

    /*
    nes is a list with Map entries:  section: ['dhn_section'], sentence: ['dhn_sentence'],
        term: ['dhn_term'], name: ['nen_name'], type: ['ne_type'], 
        subtype: ['ne_subtype'], entity: ['ent_id'], dbpediaClass: ['ent_dbpedia_class']                 

     Also, this one has the responsibility to fetch all Places for ancestors, if required.     
	*/
    String generate(doc_id, Map stuff) {
        
        // stuff comes with doc lang and doc nes.
        String lang = stuff.lang

	LinkedHashMap processedNEs = [:] // ent_id,  list with [Geoscope geo, List<Geoscope> ancestors, int count]
	                                 // LinkedHashMap so that we can sort it. 
        stuff.nes.each{ne -> 
           // println "ne: $ne"
            /*********** STEP 1: Make sure all Geoscopes are grounded into Entities ********/
            
            Entity e = new Entity(ent_id:ne.entity, ent_dbpedia_class:ne.dbpediaClass)
            Geoscope geo
            Map<Long,Geoscope> ancestors = [:]
            
            if (processedNEs.containsKey(ne.entity)) {
                processedNEs[ne.entity].count++
        	return // leave this closure iteration now 
            }
            
            // if it's a geographic one, let's ground it on GeoPlanet
             
            // always test if it's a DBpedia item. We're going to make also non-DBpedia grounded info
            if (e.ent_dbpedia_class && dbpediaontology.isAPlace(e.ent_dbpedia_class)) {
                
               
                boolean from_cache = false // to know if it's going to be a cached entry  
                
                geo = entityGeoscopeCache.get(e.ent_id) // first, check the Entity cache
                if (geo) {
                    from_cache = true
                    log.info "Geoscope already on Entity CACHE, id=${geo.geo_id} woeid=${geo.geo_woeid} name=${geo.geo_name}"
                } else {
                    geo = e.hasGeoscope() // it uses only ent_id, so we're cool... let's go to the DB to find in Entity_has_Geoscope table 
                    if (geo) {
                        log.info "Geoscope already on the DB in Entity_has_Geoscope, id=${geo.geo_id} ent_id=${e.ent_id} woeid=${geo.geo_woeid} name=${geo.geo_name}"      
                        
                    } else  { // there is no Geoscope either in the CACHE nor in the entity_has_geoscope table
                	// we have to get the WOEID for this ENTITY,    
                	// Use the GeoPlanetAPI
                	// geo does NOT have a DB's ID yet
                	geo = gt.getNewGeoscopeForPlacename(ne.name)                    
                	log.info "No Geoscope associated to Entity ${e.ent_id}, name ${ne.name}, used GeoPlanet to get a new one"
                        if (!geo || !geo.geo_name) {
                            log.info "Geoplanet returned 0 places for this ne (${ne.name})."
                            log.info "Associating with nullgeo, id ${nullgeo.geo_id}."
                            e.associateWithGeoscope(nullgeo.geo_id)
                            entityGeoscopeCache[e.ent_id] = nullgeo     
                        } else {                                           
                            // let's see if the WOEID cache has the Geoscope we're looking for...                      
                            // try the cache, to see if it has an element WITH valid geo_id (that is, it was obtained from the DB) 
                            Geoscope geo2 = woeidGeoscopeCache.get(geo.geo_woeid)
                            if (geo2 && geo2.geo_id) {
                                log.info "Got Geoscope $geo2 from CACHE which did not had been associated to an Entity. Let's do it."
                                // let's associate 
                                e.associateWithGeoscope(geo2.geo_id)
                                entityGeoscopeCache.put(e.ent_id, geo2)
                                geo = geo2
                            } else {
                                // else go to the DB to search for the Geoscope
                                geo2 = gt.getFromWOEID(geo.geo_woeid)
                                if (geo2) {
                                    log.info "Got Geoscope $geo2 from DB which did not had been associated to an Entity. Let's do it."
                                    woeidGeoscopeCache.put(geo2.geo_woeid, geo2)
                                    e.associateWithGeoscope(geo2.geo_id)
                                    entityGeoscopeCache[e.ent_id] = geo2
                                    log.info "updating both caches for Geoscope $geo2, associated to Entity ${e.ent_id}."
                                    geo = geo2
                                } else {
                                   //not in DB? add it
                                    log.info "Geoscope $geo is NOT in DB, adding it and associating to Entity ${e.ent_id}."
                                    geo.geo_id = geo.addThisToDB()
                                    log.info "updating both caches."
                                    woeidGeoscopeCache[geo.geo_woeid] = geo
                                    e.associateWithGeoscope(geo.geo_id)
                                    entityGeoscopeCache[e.ent_id] = geo
                                }
                            }            
                        }                                                    
                    } 
                }    
                
                assert geo != null
                           
                // let's get ancestors and fetch Places for them             
                // BUT NOT IF it is a country. 
                
                if (geo.geo_name) { // check if it's not the nullgeo
                    
                    if (!geo.isACountry()) {
                        
                        List<Long> ancestor_woeids
                        
                        if (geo?.geo_woeid_ancestors) { // it's already in the cache
                            ancestor_woeids = Place.parseAncestorWOEIDInfo(geo?.geo_woeid_ancestors) 
                            log.info "List of ancestor WOEIDS for Geoscope ${geo} obtained from CACHE."
                            
                        } else {                
                            ancestor_woeids = geo.fetchAncestors() // updates DB with ancestor info in DB automatically
                            // let's refresh the cache
                            log.info "List of ancestor WOEIDS for Geoscope ${geo} obtained from DB."
                            log.info "Refreshing both caches for geo $geo, entity ${e.ent_id}"
                            entityGeoscopeCache[e.ent_id] = geo
                            woeidGeoscopeCache[geo.geo_woeid] = geo
                        }
                                           
                        ancestor_woeids.each{ancestor_woeid -> 
                         
                          // let's use WOEID cache  
                          Geoscope ancestor = woeidGeoscopeCache.get(ancestor_woeid)
                        
                          if (ancestor) {
                              log.info "Got ancestor $ancestor from CACHE for woeid $ancestor_woeid"
                              ancestors[ancestor_woeid]=ancestor
                          } else {
                              // Let's see the DB, it creates new Geoscopes in DB for ancestor w/out places, if necessary
                             try {
                        	 ancestor = gt.fetchPlaceForWOEID(ancestor_woeid)
                                 woeidGeoscopeCache.put(ancestor_woeid, ancestor)
                                 log.info "Got ancestor $ancestor from DB, updated cache for woeid $ancestor_woeid" 
                                 ancestors[ancestor_woeid]=ancestor
                                
                             }   catch (java.io.FileNotFoundException e2) {
                             // it occurs when there are other exotic places like Supernames that have no ancestors
                             // I'm not sure if ALL of them have or not ancestors, so I better catch the exception.
                        	 log.error "Error fetching ancestors on GeoPlanet for Geoscope ${geo}, probably there is no ancestors. "
                        	// ancestors[ancestor_woeid]=ancestor
                             }
                         }   
                      }
                        
                  } else { // it isACountry
                	// if it does not have yet information on ancestors, let's update it in the DB
                        if (!geo.geo_woeid_ancestors) {
                            geo.closeAncestorsBecauseItsACountry() //updates DB with a 400 message in geo_woeid_ancestors, to close the loop
                            // and update it in the cache 
                            String date = String.format('%tF %<tT', new Date())  
                            geo.geo_woeid_ancestors = "400\t${date}"
                            entityGeoscopeCache[e.ent_id] = geo
                            log.info "Geoscope $geo is a country and has no ancestor information, closing it with a 400 entry"
                        }
                  }
                    
                  processedNEs[ne.entity]= [count:1, type:ne.type, subtype:ne.subtype, 
                        nename:ne.name, neid:ne.neid, dbpediaClass:ne.dbpediaClass,
                        geo:geo, ancestors:(ancestors ? ancestors : [:])] // para que o size seja pelo menos 0
                  
                } 
            }// if isAPlace
            else {
        	// if it's not a place according to DBpedia...
                log.warn "Note: ${ne.name} of type ${ne.type} and subtype ${ne.subtype} has no DBpedia Place class, will NOT be added to GeoSignature"
            }            
        }// each ne
        
        
        // now that we have Entity, Geoscope and ancestor's Geoscopes, let's update the XML factory 
        StringBuffer xml = new StringBuffer()
        // bigger counts in from as first criteria, biggest number of ancestors (~ more fine-grained) as second criteria
        
        //println "processedNEs before sort: $processedNEs" 
        processedNEs=processedNEs.sort({a, b -> a.value.count == b.value.count ? b.value.ancestors?.size() <=> a.value.ancestors?.size() : b.value.count <=> a.value.count })
        //println "processedNEs after sort: $processedNEs" 

        int totalCount =  processedNEs.collect{it.value.count}.sum()
        if (totalCount == null) totalCount = 0
                
        xml.append "<GeoSignature version=\"${geoSignatureVersionNumber}\" totalcount=\"${totalCount}\">\n"
        xml.append "  <Doc id=\"${doc_id}\" original_id=\"${stuff.doc_original_id}\" lang=\"${lang}\" />\n"
        processedNEs.each{ent, it -> 
            Place place = new Place(it.geo.geo_woeid_place)
            xml.append "  <Place count=\"${it.count}\" woeid=\"${place.woeid}\">\n"
            
            if (it.nename) xml.append "     <NE"+(it.neid ? " id=\"${it.neid}\"" : "")+">${XMLUtil.encodeAmpersand(it.nename)}</NE>\n"
            if (place.name[lang]) xml.append "     <Name>${XMLUtil.encodeAmpersand(place.name[lang])}</Name>\n"
            if (it.type) xml.append "     <Type>${it.type}</Type>\n"
            if (it.subtype) xml.append "     <Subtype>${it.subtype}</Subtype>\n"
            xml.append "     <DBpediaClass>${it.dbpediaClass}</DBpediaClass>\n"
            it.ancestors.each{woeid, geo_ancestor -> 
            	Place p = new Place(geo_ancestor.geo_woeid_place)
                xml.append "     <Ancestor woeid=\"${woeid}\">${XMLUtil.encodeAmpersand(p.name[lang])}</Ancestor>\n"
            }
            if (place.centroid) {
        	xml.append "     <Centroid>\n"
        	xml.append "        <Latitude>${place.centroid['latitude']}</Latitude>\n"
        	xml.append "        <Longitude>${place.centroid['longitude']}</Longitude>\n"
        	xml.append "     </Centroid>\n"
            }
            if (place.boundingbox) {
        	xml.append "     <BoundingBox>\n"
        	place.boundingbox.each{key, value -> 
                   xml.append "       <${key}>\n"
                   xml.append "        <Latitude>${value['latitude']}</Latitude>\n"
                   xml.append "        <Longitude>${value['longitude']}</Longitude>\n"
                   xml.append "       </${key}>\n"
                
                }
        	xml.append "     </BoundingBox>\n"
            }           
            xml.append "  </Place>\n"
        }             
        xml.append "</GeoSignature>"
        
        return xml.toString()
                            
    } // method generate 
}// class