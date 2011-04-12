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

import saskia.ontology.GeoPlanetAPI

import org.apache.log4j.*

/** This class is an interface for the Geoscope table in the WikiRembrandt database. 
  * It stores geoscope information.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  * 
  * CODES:
  * 
  * 200 - OK
  * 404 - Not Found
  * 400 - Closed
  * 301 - Redirect. For example, 'Holland' redirects to 'Netherlands'
  */
class Geoscope extends DBObject implements JSONable {

	static String tablename = "geoscope"
	static String ent_has_tablename = "entity_has_geoscope"
	
	Long geo_id
	String geo_name
	Long geo_woeid
	Integer geo_woeid_type
	String geo_woeid_place
	String geo_woeid_parent
	String geo_woeid_ancestors
	String geo_woeid_belongsto
	String geo_woeid_neighbors
	String geo_woeid_siblings
	String geo_woeid_children
	String geo_geonetpt02
	
	static SaskiaDB db = SaskiaDB.newInstance()
	static GeoPlanetAPI geoplanet = GeoPlanetAPI.newInstance()
	static Logger log = Logger.getLogger("Geoscope")

	static Map type = ['geo_id':'Long', 'geo_name':'String','geo_woeid':'Long',
            'geo_woeid_place':'String', 'geo_woeid_parent':'String', 'geo_woeid_ancestors':'String',
            'geo_woeid_belongsto':'String', 'geo_woeid_neighbors':'String', 'geo_woeid_siblings':'String',
            'geo_woeid_children':'String', 'geo_geonetpt02':'String'] 
 
	/** Get a Geoscope from id.
	 * @param version The id as needle.
	 * return Geoscope result object, or null
	 */

	static List<Geoscope> queryDB(String query, List params = []) {
		List<Geoscope> l = []
		db.getDB().eachRow(query, params, {row  -> 
			Geoscope g = new Geoscope()
			g.geo_id = row['geo_id']
			g.geo_name = row['geo_name']
			if (row['geo_woeid']) g.geo_woeid = row['geo_woeid']
                if (row['geo_woeid_type']) g.geo_woeid_type = row['geo_woeid_type']
                if (row['geo_woeid_place']) g.geo_woeid_place = row['geo_woeid_place']
                if (row['geo_woeid_parent']) g.geo_woeid_parent = row['geo_woeid_parent']
                if (row['geo_woeid_ancestors']) g.geo_woeid_ancestors = row['geo_woeid_ancestors']
                if (row['geo_woeid_belongsto']) g.geo_woeid_belongsto = row['geo_woeid_belongsto']			
                if (row['geo_woeid_neighbors']) g.geo_woeid_neighbors = row['geo_woeid_neighbors']
                if (row['geo_woeid_siblings']) g.geo_woeid_siblings = row['geo_woeid_siblings']
                if (row['geo_woeid_children']) g.geo_woeid_children = row['geo_woeid_children']
                if (row['geo_geonetpt02']) g.geo_geonetpt02 = row['geo_geonetpt02']
			l << g                                                  
		})
		if (l) return l else return null            
	}

	static int deleteGeoscope(Long id) {
		Geoscope g = Geoscope.getFromID(id)
		return g?.removeThisFromDB()
	}
	  
	public List<Entity> hasEntities() {
        List<Entity> e = []
        db.getDB().eachRow("SELECT * FROM ${ent_has_tablename} WHERE ehg_geoscope = ?", [geo_id], {row -> 
                e << Entity.getFromID(row["ehg_entity"])
        })
        return e
    }

	public Map toMap() {
	    return ['geo_id':geo_id, 'geo_name':geo_name, 'geo_woeid':geo_woeid,
	    'geo_woeid_place':geo_woeid_place, 'geo_woeid_parent':geo_woeid_parent, 
		 'geo_woeid_ancestors':geo_woeid_ancestors,
	    'geo_woeid_belongsto':geo_woeid_belongsto, 'geo_woeid_neighbors':geo_woeid_neighbors, 
		 'geo_woeid_siblings':geo_woeid_siblings,
	    'geo_woeid_children':geo_woeid_children, 'geo_geonetpt02':geo_geonetpt02] 
	}
	
	public Map toSimpleMap() {
	    return ['geo_id':geo_id, 'geo_name':geo_name, 'geo_woeid':geo_woeid]
	}
	
	boolean equals(Entity e) {
		return this.toMap().equals(e.toMap())
	}
	                
	static Map listGeoscopes(limit = 10, offset = 0, column = null, needle = null) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0
			
		String where = ""
		String from = " FROM ${tablename}"	
		List params = []	
		if (column && needle) {
		    switch (type[column]) {
		        case 'String': where += " WHERE $column LIKE '%${needle}%'"; break
		        case 'Integer': where += " WHERE $column=? "; params << Integer.parseInt(needle); break
		        case 'Long': where += " WHERE $column=? "; params << Long.parseLong(needle); break
		    }
		}
		    
		String query = "SELECT SQL_CALC_FOUND_ROWS ${tablename}.* $from $where LIMIT ${limit} OFFSET ${offset} "+
		"UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL"
		//log.debug "query = $query params = $params class = "+params*.class
		List<Entity> u 
		try {u = queryDB(query, params) }
		catch(Exception e) {log.error "Error getting Geoscope list: ", e}
		// last "item" it's the count.
		int total = (int)(u.pop().geo_id)
		log.debug "Returning "+u.size()+" results."
		return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
		        "column":column, "value":needle]
	}
	 
	/** Get a Geoscope from id.
	 * @param id The id as needle.
	 * return the Geoscope result object, or null
	 */
	static Geoscope getFromID(long geo_id) {
		if (!geo_id) return null 
		List<Geoscope> geos = queryDB("SELECT * FROM ${tablename} WHERE geo_id=?", [geo_id])
		log.debug "Querying for geo_id $geo_id got Geoscope $geos." 
			// if geoscope is a redirection, use it instead
		if (geos) {
			for (int i=0; i<geos.size(); i++) {
				if (geos[i].geo_woeid_place.startsWith("301\t")) {
        			String new_woeid_string = geos[i].geo_woeid_place.find(/301\t.*?\t(\d+)/) {all, g1 -> return g1}
               		geos[i] = Geoscope.getFromWOEID(Long.parseLong(new_woeid_string))
				}
			}
      // let's hope there is no double redirect!
      }
		if (geos && geos[0].geo_id) return geos[0] else return null
	}	
       
	static List<Geoscope> getFromName(String geo_name, String lang) {
		if (!geo_name || !lang) return null 
		String needle = "${lang}:${geo_name}"
		List<Geoscope> geos = queryDB("SELECT * FROM ${tablename} WHERE geo_name REGEXP '^(.*;)?${needle}(;.*)?\$'",[])
		log.info "Querying for geo_name $needle got Geoscope $geos." 
		 // if geoscope is a redirection, use it instead
		if (geos) {
			for (int i=0; i<geos.size(); i++) {
				if (geos[i].geo_woeid_place.startsWith("301\t")) {
        			String new_woeid_string = geos[i].geo_woeid_place.find(/301\t.*?\t(\d+)/) {all, g1 -> return g1}
               		geos[i] = Geoscope.getFromWOEID(Long.parseLong(new_woeid_string))
				}
			}
			// let's hope there is no double redirect!
      }
		if (geos) return geos else return null
	}	
	
	static updateValue(Long geo_id, column, value) {
	    Geoscope geo = Geoscope.getFromID(geo_id)
	    if (!geo) return -1
	    return geo.updateValue(column, value)
	}
	
	public updateValue(column, value) {
	    def newvalue	    
	    switch (type[column]) {
	        case 'String': newvalue = value; break
	        case 'Integer': newvalue = Integer.parseInt(value); break
	        case 'Long': newvalue = Long.parseLong(value); break
	    }
	    def res = db.getDB().executeUpdate("UPDATE ${tablename} SET ${column}=? WHERE geo_id=?",[newvalue, geo_id])
	    return res
	}
	
  /** Get a Geoscope from id.
   * @param id The id as needle.
   * return the Geoscope result object, or null
   */
	static Geoscope getFromWOEID(long woeid) {
           //if (!woeid) return null - it can be 0
            List<Geoscope> geos = queryDB("SELECT * FROM ${tablename} WHERE geo_woeid=?", [woeid])
            log.info "Querying for woeid $woeid got Geoscope $geos." 
 			
            // if geoscope is a redirection, use it instead
            if (geos) {
				for (int i=0; i<geos.size(); i++) {
					if (geos[i].geo_woeid_place.startsWith("301\t")) {
        				String new_woeid_string = geos[i].geo_woeid_place.find(/301\t.*?\t(\d+)/) {all, g1 -> return g1}
                		geos[i] = Geoscope.getFromWOEID(Long.parseLong(new_woeid_string))
					}
				}
                // let's hope there is no double redirect!
            }

		return (geos? geos[0] : null)
	}
        
   public Map getName() {
		if (!geo_name) return null
		Map m = [:]
		geo_name.split(/;/).each{it -> 
			List l = it.split(/:/)
			m[l[0]]=l[1]
		}
		return m
	}
	
	public boolean isACountry() {
		return geo_woeid_type == GeoPlanetAPI.types["Country"]
	}       

	public boolean isAContinent() {
		return geo_woeid_type == GeoPlanetAPI.types["Continent"]
   }
	
	public boolean isASupername() {
		return geo_woeid_type == GeoPlanetAPI.types["Supername"]
	}
	
	public boolean isAColloquial() {
		return geo_woeid_type == GeoPlanetAPI.types["Colloquial"]
	}
	
	public boolean isAboveCountry() {
		return isAContinent() || isASupername() || isAColloquial()
	}

	static Geoscope getNewGeoscopeForPlacename(String placename) {
		Geoscope geo = new Geoscope()
        
           // let's fetch it! Use terms from the named entity                       
           log.info "New Geoscope, going to get GeoPlanet info for placename $placename"
           geo.geo_woeid_place = geoplanet.getPlaceXMLByPlacename(placename)
           Place place = new Place(geo.geo_woeid_place)
        
           // now, let's fill out name, if exists
           if (place.name) {
            geo.geo_name = place.name.collect{"${it.key}:${it.value}"}.join(";")
            geo.geo_woeid = place.woeid
			// placetypecode 
			List<Integer> code = place.placetypenamecode.values().toList().unique()
			if (code)  geo.geo_woeid_type = code[0] 
            log.info "Got name ${geo.geo_name} and weoid ${geo.geo_woeid} and type ${geo.geo_woeid_type}"                           
           } 
		return geo
	}
        
// called when there's no ancestors in DB, have to go to GeoPlanet
	public List<Long> fetchAncestors() {
		// it happens when GeoPlanet can't ground a geographic entity.
		// we return no ancestors.
		if (!geo_woeid) return []
            
            log.info "Geoscope has no ancesters, fetching GeoPlanet for ancesters"
            List<Long> woeids 
            try {
               woeids = geoplanet.fetchAncestorsForWOEID(geo_woeid)               	
            }	catch (java.io.FileNotFoundException e2) {
            // it occurs when there are other exotic places like Supernames that have no ancestors
            // I'm not sure if ALL of them have or not ancestors, so I better catch the exception.
        	log.error "Error fetching ancestors on GeoPlanet for WOEID ${geo_woeid}, probably there is no ancestors. "
        	closeAncestorsForWOEID(geo_woeid)
            }
            String result
            String date = String.format('%tF %<tT', new Date()) 
            if (woeids) result = "200\t${date}\t"+woeids.join(";")
            else result = "404\t${date}"
            
            // if I have a geo_id, I'm from the DB, so let's update it
            if (geo_id) log.info "Geoscope exists in DB without ancestors. Updating with result $result"
            else log.info "Geoscope exists in DB with ancestors."
            geo_woeid_ancestors = result
        
            // if it's a Geoscope from the DB, but with null ancestors, let's just update it
            if (geo_id) updateAncestorsInDB()
		return woeids
	}
        
	// called when there's no children in DB, have to go to GeoPlanet
	public List<Long> fetchDescendents() {
            // it happens when GeoPlanet can't ground a geographic entity.
            // we return no ancestors.
            if (!geo_woeid) return []
            
            log.info "fetching GeoPlanet for descendents"
            List<Long> woeids 
            try {
               woeids = geoplanet.fetchDescendentsForWOEID(geo_woeid)               	
            }	catch (java.io.FileNotFoundException e2) {
            // it occurs when there are other exotic places like Supernames that have no ancestors
            // I'm not sure if ALL of them have or not ancestors, so I better catch the exception.
        	log.error "Error fetching descendents on GeoPlanet for WOEID ${geo_woeid}, probably there is no descendents. "
        	closeDescendentsForWOEID(geo_woeid)
            }
            String result
            String date = String.format('%tF %<tT', new Date()) 
            if (woeids) result = "200\t${date}\t"+woeids.join(";")
            else result = "404\t${date}"
            
            // if I have a geo_id, I'm from the DB, so let's update it
            if (geo_id) log.info "Geoscope exists in DB without descendents. Updating with result $result"
            else log.info "Geoscope exists in DB with descendents."
            geo_woeid_children = result
        
            // if it's a Geoscope from the DB, but with null ancestors, let's just update it
            if (geo_id) updateDescendentsInDB()
		return woeids
	}

        /** just add  justification of a country entry why it does not have ancestors */
        public void closeAncestorsBecauseItsACountry() {
            String date = String.format('%tF %<tT', new Date()) 
            geo_woeid_ancestors = "400\t${date}"
            if (geo_id) updateAncestorsInDB()
        }
        
        /** just add  justification of a country entry why it does not have ancestors */
        public closeAncestorsForWOEID(long woeid) {
            String date = String.format('%tF %<tT', new Date()) 
            String info = "400\t${date}"
            def updated_doc = db.getDB().executeUpdate(
                "UPDATE ${tablename} SET geo_woeid_ancestors=? where geo_woeid=?", 
                [info, woeid]) 
                log.debug "Updated Geoscope with woeid:${woeid}, closed the ancestors."
            return updated_doc
        }
        
 		/** just add  justification entry why it does not have descendents */
        public closeDescendentsForWOEID(long woeid) {
            String date = String.format('%tF %<tT', new Date()) 
            String info = "400\t${date}"
            def updated_doc = db.getDB().executeUpdate(
                "UPDATE ${tablename} SET geo_woeid_children=? where geo_woeid=?", 
                [info, woeid]) 
                log.debug "Updated Geoscope with woeid:${woeid}, closed the descendents."
            return updated_doc
        }

		/** Get an above-country geoscope, get a country list **/
		List<Geoscope> getCountryDescendents() {
			List<Geoscope> res = []
			List<Long> temp_woeids = []
			if (isAboveCountry()) {
				//log.info "We have a geoscope above country"
				if (geo_woeid_children) temp_woeids = Place.parseDescendentWOEIDInfo(geo_woeid_children)
				else temp_woeids = fetchDescendents()
				//log.info "Got woeids: $temp_woeids"
				temp_woeids.each{woeid -> 
					Geoscope children = Geoscope.getFromWOEID(woeid)
					//log.info "Got children: $children"
									
					// children may have a null entry, that is, we haven't queried GeoPlaetAPI for it. 
					if (!children) {
						children = Geoscope.fetchPlaceForWOEID(woeid) 
					//	log.info "Had to fetch GeoPlanetAPI, children is now $children"    
					}               
                	if (children && children.isACountry()) res << children
					
				}// each temp_woeids
			}//isAboveCountry
			return res
		}

		boolean equals(Geoscope geo) {
			if (!geo) return false
			return this.geo_id == geo.geo_id && this.geo_name == geo.geo_name && geo_woeid == geo.geo_woeid && 
			 geo_woeid_type == geo.geo_woeid_type && geo_woeid_place == geo.geo_woeid_place &&
	 		 geo_woeid_parent == geo.geo_woeid_parent &&  geo_woeid_ancestors == geo.geo_woeid_ancestors && 
	 		geo_woeid_belongsto == geo.geo_woeid_belongsto && geo_woeid_neighbors == geo.geo_woeid_neighbors &&
     		geo_woeid_siblings == geo.geo_woeid_siblings && geo_woeid_children == geo.geo_woeid_children && 
     		geo_geonetpt02 == geo.geo_geonetpt02
		}
        
        /**
         * Give a list of Geoscopes that are ancestors, built from Place info. 
         *  if there's no place nor woeid, ldt's take care of it automagically into the DB
         *  Watch the redirections!
         */
        public static Geoscope fetchPlaceForWOEID(Long woeid) {
                      
            Geoscope geo = Geoscope.getFromWOEID(woeid)
            
            // there is no place in ancestor, let's fetch it
            if (!geo) {
        	log.info "Ancestor has no Geoscope entry in DB, creating a new one"                                  
        	geo = new Geoscope()
                    
        	log.info "Fetching place info on Geoplanet"                                  
        	geo.geo_woeid_place = geoplanet.getPlaceXMLByWOEID(woeid)
        	Place p = new Place(geo.geo_woeid_place)
        	if (p.name) {
        	    geo.geo_name = p.name.collect{"${it.key}:${it.value}"}.join(";")
        	    geo.geo_woeid = p.woeid
        	    geo.geo_woeid_type = p.placetypenamecode.values().toList().pop()
        	    log.info "Got name ${geo.geo_name} and weoid ${geo.geo_woeid} and type ${geo.geo_woeid_type}, adding to the DB"           
        	    // now, if the woeid returned by GeoPlanet is the same, OK...
                if (woeid == geo.geo_woeid) {
                	geo.geo_id = geo.addThisToDB()
                } else {
                	// now, if it's different, we have to add a redirection info, 
                	// and check if the new one is not already on the DB!
                	Geoscope geo2 = Geoscope.getFromWOEID(geo.geo_woeid)

                	// if ther eisn't, let's insert as the new WOEID, and have a redirection on the old one
                	if (!geo2) {
                	    geo.geo_id = geo.addThisToDB()
                	}
                	Geoscope oldgeo = new Geoscope()
                    oldgeo.geo_name = geo.geo_name
                    oldgeo.geo_woeid = woeid // HERE, I use the deprecated WOEID                      
                     oldgeo.geo_woeid_type = geo.geo_woeid_type 
                        
                     String date = String.format('%tF %<tT', new Date()) 
                     String info = "301\t${date}\t"+geo.geo_woeid                
                     oldgeo.geo_woeid_place = info
                     oldgeo.addThisToDB()
                }
            }
            }
            return  geo      
        }
           
	       
        public updateAncestorsInDB() {	
           def updated_doc = db.getDB().executeUpdate(
            "UPDATE ${tablename} SET geo_woeid_ancestors=? where geo_id=?", 
            [geo_woeid_ancestors, geo_id]) 
            log.debug "Updated Geoscope with geo_id:${geo_id} with new ancestors."
            return updated_doc
        }
        
 		public updateDescendentsInDB() {	
           def updated_doc = db.getDB().executeUpdate(
            "UPDATE ${tablename} SET geo_woeid_children=? where geo_id=?", 
            [geo_woeid_children, geo_id]) 
            log.debug "Updated Geoscope with geo_id:${geo_id} with new descendents."
            return updated_doc
        }

        public updatePlaceInDB() {	
            String date = String.format('%tF %<tT', new Date()) 
            String result = "200\t${date}\t"+geo_woeid_place
            def updated_doc = db.getDB().executeUpdate(
            "UPDATE ${tablename} SET geo_woeid_place=? where geo_id=?", 
            [result, geo_id]) 
            log.debug "Updated Geoscope with geo_id:${geo_id} with new place XML."
            return updated_doc
        }

	public Long addThisToDB() {	
		def res = db.getDB().executeInsert("INSERT INTO ${tablename}(geo_name, geo_woeid, geo_woeid_type,"+
      "geo_woeid_place, geo_woeid_parent, geo_woeid_ancestors, geo_woeid_belongsto, geo_woeid_neighbors, "+
      "geo_woeid_siblings, geo_woeid_children, geo_geonetpt02) VALUES(?,?,?,?,?,?,?,?,?,?,?)", 
        [geo_name, geo_woeid, geo_woeid_type, geo_woeid_place, geo_woeid_parent, geo_woeid_ancestors, 
        geo_woeid_belongsto, geo_woeid_neighbors, geo_woeid_siblings, geo_woeid_children, geo_geonetpt02])
		geo_id = (long)res[0][0]
		log.info "Inserted new Geoscope in DB: ${this}"
		return geo_id
	}
	
	public int removeThisFromDB() {
		if (!geo_id) return null
		def res = db.getDB().executeUpdate("DELETE FROM ${tablename} WHERE geo_id=?", [geo_id])	
		log.info "Removed Geoscope ${this} from DB, got $res"
		return res	    
	}
      
	public String toString() {
		return "${geo_id}:${geo_name}"
	}
}