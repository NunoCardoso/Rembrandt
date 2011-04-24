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
package saskia.db.table

import org.apache.log4j.*

import saskia.db.Place
import saskia.db.database.SaskiaDB
import saskia.db.obj.Geoscope
import saskia.ontology.GeoPlanetAPI

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
class GeoscopeTable extends DBTable {

	String tablename = "geoscope"
	String ent_has_tablename = "entity_has_geoscope"

	static GeoPlanetAPI geoplanet = GeoPlanetAPI.newInstance()

	static Logger log = Logger.getLogger("Geoscope")

	public GeoscopeTable(SaskiaDB db) {
		super(db, tablename)
	}

	static List<Geoscope> queryDB(String query, List params = []) {
		List<Geoscope> l = []
		getSaskiaDB().getDB().eachRow(query, params, {row  ->
			l << Geoscope.createFromDBRow(this.owner, row)
		})
		if (l) return l else return null
	}

	public Map listGeoscopes(limit = 10, offset = 0, column = null, needle = null) {
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
		List<EntityTable> u
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
	public Geoscope getFromID(Long geo_id) {
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

	public List<Geoscope> getFromName(String geo_name, String lang) {
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


	/** Get a Geoscope from id.
	 * @param id The id as needle.
	 * return the Geoscope result object, or null
	 */
	public Geoscope getFromWOEID(long woeid) {
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

	public Geoscope getNewGeoscopeForPlacename(String placename) {
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



	/**
	 * Give a list of Geoscopes that are ancestors, built from Place info. 
	 *  if there's no place nor woeid, ldt's take care of it automagically into the DB
	 *  Watch the redirections!
	 */
	public Geoscope fetchPlaceForWOEID(Long woeid) {

		Geoscope geo = getFromWOEID(woeid)

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
					Geoscope geo2 = getFromWOEID(geo.geo_woeid)

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

	/** just add  justification of a country entry why it does not have ancestors */
	public closeAncestorsForWOEID(long woeid) {
		String date = String.format('%tF %<tT', new Date())
		String info = "400\t${date}"
		def updated_doc = getSaskia().getDB().executeUpdate(
				"UPDATE ${tablename} SET geo_woeid_ancestors=? where geo_woeid=?",
				[info, woeid])
		log.debug "Updated Geoscope with woeid:${woeid}, closed the ancestors."
		return updated_doc
	}

	/** just add  justification entry why it does not have descendents */
	public closeDescendentsForWOEID(long woeid) {
		String date = String.format('%tF %<tT', new Date())
		String info = "400\t${date}"
		def updated_doc = getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${tablename} SET geo_woeid_children=? where geo_woeid=?",
				[info, woeid])
		log.debug "Updated Geoscope with woeid:${woeid}, closed the descendents."
		return updated_doc
	}

}