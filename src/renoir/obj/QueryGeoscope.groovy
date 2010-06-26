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
 package renoir.obj

import rembrandt.obj.NamedEntity
import saskia.io.Entity
import saskia.io.Geoscope
import org.apache.log4j.*

class QueryGeoscope {
	
	static Logger log = Logger.getLogger("RenoirMain")

	NamedEntity ne  // contains the detected NE
	
	Geoscope geo
	List<Geoscope> expanded_country_geos
	
	public groundNEtoGeoscope() {
		List dbpediaresources = ne.dbpediaPage.values().toList().flatten()
		dbpediaresources?.each{res -> 
	 		Entity ent = Entity.getFromDBpediaResource(res)
			Geoscope g = ent?.hasGeoscope()
			if (g) {
				if (!geo) {
					geo = g
				} else {
					println "QueryGeoscope: I have already a Geoscope $geo, don't need $g"
				}
			} else {
				"QueryGeoscope: Did not get a Geoscope from entity $ent"
			}
		}
	}
	
	public boolean geoscopeNeedsToBeExpandedtoCountries() {
		if (!geo) {
			println "QueryGeoscope: don't have a geo, don't know if I need to be expanded."
			return false
		}
		return geo.isAboveCountry()
	}
	
	public expandGeoscopeToCountryLevel() {
		expanded_country_geos = geo.getCountryDescendents()
	}
	
	public List<String> getDBpediaResourcesFromAllGeoscopes() {
		List<String> res = []
		if (geo) {
			List<Entity> ents = geo.hasEntities()
			ents?.each{ent -> 
				if (ent?.ent_dbpedia_resource) res << ent.ent_dbpedia_resource
			}
		}
		expanded_country_geos?.each{geo -> 
			List<Entity> ents = geo.hasEntities()
			ents?.each{ent -> 
				if (ent?.ent_dbpedia_resource && 
				ent?.ent_dbpedia_class == "Country") res << ent.ent_dbpedia_resource
			}
		}
		return res 
	}	

	public String toString() {return "ne:${ne}"}
}