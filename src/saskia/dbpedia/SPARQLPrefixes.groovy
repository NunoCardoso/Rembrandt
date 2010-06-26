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

package saskia.dbpedia

/** This is a gazetteer for SPARQL prefixes */
class SPARQLPrefixes extends HashMap {

	/** Main constructor, it populates the map with SPARQL prefixes.
	 */
	public SPARQLPrefixes() {
		put("dbpedia2","http://dbpedia.org/property/")
		put("dbpedia-owl","http://dbpedia.org/ontology/")
		put("dbpedia","http://dbpedia.org/")
		put("dc","http://purl.org/dc/elements/1.1/")
		put("dcterms","http://purl.org/dc/terms/")
		put("foaf","http://xmlns.com/foaf/0.1/")
		put('geo','http://www.w3.org/2003/01/geo/wgs84_pos#')
		put('georss','http://www.georss.org/georss/')
		put('geonames','http://www.geonames.org/ontology#')
		put('gn', 'http://xldb.di.fc.ul.pt/xldb/publications/2009/10/geo-net#')
		put('gnpt', 'http://xldb.di.fc.ul.pt/xldb/publications/2009/10/geo-net-pt#')
		put('gnpt02', 'http://xldb.di.fc.ul.pt/xldb/publications/2009/10/geo-net-pt-02#')
		put('opencyc','http://sw.opencyc.org/2008/06/10/concept/en/') 
		put('opencyc','http://sw.opencyc.org/2008/06/10/concept/')		
		put("owl","http://www.w3.org/2002/07/owl#")
		put("rdfs","http://www.w3.org/2000/01/rdf-schema#")
		put("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#")
		put("skos","http://www.w3.org/2004/02/skos/core#")
		put('units','http://dbpedia.org/units/')
		put('umbel-sc','http://umbel.org/umbel/sc/')
		put('umbel-ac','http://umbel.org/umbel/ac/') 
		put('wikicompany','http://dbpedia.openlinksw.com/wikicompany/')
		put("xsd","http://www.w3.org/2001/XMLSchema#")
		put('yago','http://dbpedia.org/class/yago/')
		put('yago','http://mpii.de/yago/resource/')
	}
		/*  'http://dbpedia.org/resource/', 'dbpedia',
    'http://dbpedia.org/property/', 'p',
    
	DB.DBA.XML_SET_NS_DECL ('sioc', 'http://rdfs.org/sioc/ns#', 2);


	
	/** Get a prefix header for SPARQL queries.
	 * @return the SPARQL prefixes
	 */
	public String getAll() {
	   return keySet().collect{"PREFIX ${it}: <${get(it)}>"}.join("\n")
	}
	
}