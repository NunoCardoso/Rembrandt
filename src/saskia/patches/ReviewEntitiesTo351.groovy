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

import saskia.bin.Configuration

import saskia.db.table.*;
import saskia.db.obj.*;
import saskia.dbpedia.DBpediaAPI
import saskia.dbpedia.DBpediaOntology
import org.apache.log4j.Logger
import org.apache.commons.cli.*
import saskia.db.database.SaskiaMainDB


/**
 * I loop through the entities in the DB, re-classify them with the new 3.5.1 classes,
 * for those who are a Place and do not have Geoscope, fill it out */ 

class ReviewEntitiesTo351 {

	Configuration conf = Configuration.newInstance()
	static Logger log = Logger.getLogger("Patches")
	DBpediaOntology dbpediaontology
	SaskiaMainDB db
	DBpediaAPI dbpedia
	Geoscope nullgeo
	
	public ReviewEntitiesTo351() {
		conf = Configuration.newInstance()
		dbpediaontology = DBpediaOntology.getInstance()
		db = SaskiaMainDB.newInstance()
		dbpedia = DBpediaAPI.newInstance()
	    nullgeo = Geoscope.getFromWOEID(0)
        log.info "Got $nullgeo"
        nullgeo.geo_name = null
	}

	public Map parse() {
		
		Map status = [processed:0, untouched:0, upgraded:0, geotagged:0, error:0]
		int limit = 100
        int counter = 0
        int total = 0
		int remaining = 10000
       
        
        List batch
        
        while ( remaining > 0 ) {
            batch = []
            log.info  "Getting batch ${counter} to ${limit+counter}, ${remaining} entities remaining."
           
         
			//  ent_id , ent_wikipedia_page , ent_dbpedia_resource, ent_dbpedia_class 
            String select = "SELECT SQL_CALC_FOUND_ROWS * FROM ${EntityTable.ent_table} "+
                    "LIMIT $limit OFFSET $counter UNION SELECT FOUND_ROWS(), '%%%TOTAL%%%', '', ''"
           
            db.getDB().eachRow(select, [], {row -> 
                
                if (row['ent_wikipedia_page'] == "%%%TOTAL%%%") {
                    total = (int)row['ent_id']	 
                } else {            
                   batch << new EntityTable(ent_id:row['ent_id'], ent_wikipedia_page:row['ent_wikipedia_page'], 
                          ent_dbpedia_resource:row['ent_dbpedia_resource'],ent_dbpedia_class:row['ent_dbpedia_class'])
                  counter++
				  
                }
            })
			remaining = (total - counter) 
			
			batch.each{entity -> 
				// get a new Entity from DBpedia
				String current_class = entity.ent_dbpedia_class
				List listOfClasses = dbpedia.getDBpediaOntologyClassFromDBpediaResource(entity.ent_dbpedia_resource)
                String new_class = dbpediaontology.getNarrowerClassFrom(listOfClasses) 
				log.info "===BEGIN: entity $entity"
				log.info "has class ${current_class}, with 3.5.1 is now $new_class"
				
				status.processed++
			
				// 1) classes are the same
				if (current_class == new_class) {
					log.info "Are the same, leaving it untouched."
					log.info "===END: entity $entity"
					status.untouched++
				} else {
					log.info  "Are different, upgrading."
										
				    try {
						def res = db.getDB().executeUpdate(
					  "UPDATE ${EntityTable.ent_table} SET ent_dbpedia_class=? WHERE ent_id=?",[new_class, entity.ent_id])
				    	log.info "Got update result $res"
						status.upgraded++
					}catch(Exception e) {
						log.info  "Got exception: "+e.getMessage()
						status.error++
					}
					
					Geoscope geo
					Map<Long,Geoscope> ancestors = [:]

					if (dbpediaontology.isAPlace(new_class)) {
						log.info "It's a place."

						boolean from_cache = false // to know if it's going to be a cached entry  
                        geo = entity.hasGeoscope() // it uses only ent_id, so we're cool... let's go to the DB to find in Entity_has_Geoscope table 
                  		if (geo) {
                       		log.info "Geoscope already on the DB in Entity_has_Geoscope, id=${geo.geo_id} ent_id=${entity.ent_id} woeid=${geo.geo_woeid} name=${geo.geo_name}"       
                  		} else  { // there is no Geoscope either in the CACHE nor in the entity_has_geoscope table
                			// we have to get the WOEID for this ENTITY,    
                			// Use the GeoPlanetAPI
                			// geo does NOT have a DB's ID yet
							String newname = entity.ent_dbpedia_resource.replaceAll(/_/," ")
							log.info "Getting geoscope in GeoPlanetAPI for $newname"
                	 		geo = Geoscope.getNewGeoscopeForPlacename(newname)                    
                	 		log.info "No Geoscope associated to Entity ${entity.ent_id}, name ${newname}, used GeoPlanet to get a new one"
                    		if (!geo || !geo.geo_name) {
                            	log.info "Geoplanet returned 0 places for this ne (${newname})."
                            	log.info "Associating with nullgeo, id ${nullgeo.geo_id}."
                            	entity.associateWithGeoscope(nullgeo.geo_id)
                             } else {                                           
                            // let's see if the WOEID cache has the Geoscope we're looking for...                      
                            // try the cache, to see if it has an element WITH valid geo_id (that is, it was obtained from the DB) 
                            	Geoscope geo2 = Geoscope.getFromWOEID(geo.geo_woeid)
                                if (geo2) {
                                    log.info "Got Geoscope $geo2 from DB which did not had been associated to an Entity. Let's do it."
                                    entity.associateWithGeoscope(geo2.geo_id)
                                    log.info "updating both caches for Geoscope $geo2, associated to Entity ${entity.ent_id}."
                                    geo = geo2
                                } else {
                                   //not in DB? add it
                                    log.info "Geoscope $geo is NOT in DB, adding it and associating to Entity ${entity.ent_id}."
                                    geo.geo_id = geo.addThisToDB()
                                    entity.associateWithGeoscope(geo.geo_id)
                                }
								status.geotagged++
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
                            		log.info "Refreshing both caches for geo $geo, entity ${entity.ent_id}"
                        		}
                                           
                        		ancestor_woeids.each{ancestor_woeid -> 
                         
                          			// let's use WOEID cache  
                          			try {
                        	 			def ancestor = Geoscope.fetchPlaceForWOEID(ancestor_woeid)
                             			log.info "Got ancestor $ancestor from DB, updated cache for woeid $ancestor_woeid" 
                             			ancestors[ancestor_woeid]=ancestor
                                
                             		}   catch (java.io.FileNotFoundException e2) {
                             // it occurs when there are other exotic places like Supernames that have no ancestors
                             // I'm not sure if ALL of them have or not ancestors, so I better catch the exception.
                        	 			log.error "Error fetching ancestors on GeoPlanet for Geoscope ${geo}, probably there is no ancestors. "
                        	// ancestors[ancestor_woeid]=ancestor
                             		}
                         		}   
                        	} else { // it isACountry
                	// if it does not have yet information on ancestors, let's update it in the DB
                        		if (!geo.geo_woeid_ancestors) {
                            		geo.closeAncestorsBecauseItsACountry() //updates DB with a 400 message in geo_woeid_ancestors, to close the loop
                            		// and update it in the cache 
                            		String date = String.format('%tF %<tT', new Date())  
                            		geo.geo_woeid_ancestors = "400\t${date}"
                            		log.info "Geoscope $geo is a country and has no ancestor information, closing it with a 400 entry"
                        		}
                  			}
                		} 
            		}// if isAPlace
				
					// let's get a brand new refreshed Entity
					EntityTable final_e
				 	db.getDB().eachRow("SELECT * FROM ${EntityTable.ent_table} WHERE ent_id=?", [entity.ent_id], {row -> 
                		final_e = new EntityTable(ent_id:row['ent_id'], ent_wikipedia_page:row['ent_wikipedia_page'], 
                          ent_dbpedia_resource:row['ent_dbpedia_resource'],ent_dbpedia_class:row['ent_dbpedia_class'])

                	})

					log.info "===END: entity $final_e"
				} // different, upgrading
			} // batch.each
		}//while all
		return status
	}	

 	public static void main (String[] args) {
              
     //   Options o = new Options()
        
        /*o.addOption("file",true,"File with stuff above")
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
          
        if (!cmd.hasOption("file")) {
            println "No --file arg. Please specify file. Exiting."
            System.exit(0)
        }*/
		
        ReviewEntitiesTo351 obj = new ReviewEntitiesTo351()
		Map status = obj.parse()
		println "Status: $status"
	}
}
/*	public checkEntity(long ent_id) {
		
 // always test if it's a DBpedia item. We're going to make also non-DBpedia grounded info
        
        
*/
