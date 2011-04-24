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
package saskia.db.obj

import org.apache.log4j.Logger
import saskia.db.table.DBTable
import saskia.ontology.GeoPlanetAPI

/**
 * @author Nuno Cardoso
 *
 */
class Geoscope extends DBObject implements JSONable {

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

	static Logger log = Logger.getLogger("Geoscope")

	static GeoPlanetAPI geoplanet = GeoPlanetAPI.newInstance()

	
	public Geoscope(DBTable dbtable) {
		super(dbtable)
	}
	
	static Geoscope createNew(DBTable dbtable, row) {
		Geoscope g = new Geoscope(dbtable)
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
		return g
	}
	
	static Map type = ['geo_id':'Long', 'geo_name':'String','geo_woeid':'Long',
		'geo_woeid_place':'String', 'geo_woeid_parent':'String', 'geo_woeid_ancestors':'String',
		'geo_woeid_belongsto':'String', 'geo_woeid_neighbors':'String', 'geo_woeid_siblings':'String',
		'geo_woeid_children':'String', 'geo_geonetpt02':'String']

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

	public List<Entity> hasEntities() {
		List<Entity> e = []
		getDBTable().getSaskiaDB().getDB().eachRow(
				"SELECT * FROM ${getDBTable().ent_has_tablename} WHERE ehg_geoscope = ?", [geo_id], {row ->
					e << Entity.getFromID(row["ehg_entity"])
				})
		return e
	}

	public updateValue(column, value) {
		def newvalue
		switch (type[column]) {
			case 'String': newvalue = value; break
			case 'Integer': newvalue = Integer.parseInt(value); break
			case 'Long': newvalue = Long.parseLong(value); break
		}
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
			"UPDATE ${getDBTable().tablename} SET ${column}=? WHERE geo_id=?",
			[newvalue, geo_id])
		return res
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
			getDBTable().closeAncestorsForWOEID(geo_woeid)
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
			getDBTable().closeDescendentsForWOEID(geo_woeid)
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
				Geoscope children = getDBTable().getFromWOEID(woeid)
				//log.info "Got children: $children"

				// children may have a null entry, that is, we haven't queried GeoPlaetAPI for it.
				if (!children) {
					children = getDBTable().fetchPlaceForWOEID(woeid)
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
	
	public updateAncestorsInDB() {
		def updated_doc = getDBTable().getSaskiaDB().getDB().executeUpdate(
			"UPDATE ${getDBTable().tablename} SET geo_woeid_ancestors=? where geo_id=?",
		[geo_woeid_ancestors, geo_id])
		log.debug "Updated Geoscope with geo_id:${geo_id} with new ancestors."
		return updated_doc
	}

	public updateDescendentsInDB() {
		def updated_doc = getDBTable().getSaskiaDB().getDB().executeUpdate(
			"UPDATE ${getDBTable().tablename} SET geo_woeid_children=? where geo_id=?",
		[geo_woeid_children, geo_id])
		log.debug "Updated Geoscope with geo_id:${geo_id} with new descendents."
		return updated_doc
	}

	public updatePlaceInDB() {
		String date = String.format('%tF %<tT', new Date())
		String result = "200\t${date}\t"+geo_woeid_place
		def updated_doc = getDBTable().getSaskiaDB().getDB().executeUpdate(
			"UPDATE ${getDBTable().tablename} SET geo_woeid_place=? where geo_id=?",
		[result, geo_id])
		log.debug "Updated Geoscope with geo_id:${geo_id} with new place XML."
		return updated_doc
	}

	public Long addThisToDB() {
		def res =  getDBTable().getSaskiaDB().getDB().executeInsert(
			"INSERT INTO ${getDBTable().tablename}(geo_name, geo_woeid, geo_woeid_type,"+
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
		def res =  getDBTable().getSaskiaDB().getDB().executeUpdate(
			"DELETE FROM ${getDBTable().tablename} WHERE geo_id=?", [geo_id])
		log.info "Removed Geoscope ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return "${geo_id}:${geo_name}"
	}
}
