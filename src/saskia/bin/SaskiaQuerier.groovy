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

package saskia.bin

import saskia.db.SaskiaDB
import saskia.io.RembrandtedDoc
import saskia.io.Collection
//import saskia.converters.RembrandtedDocument2PlainTextConverter 
import org.apache.log4j.*

/**
 * Obsolete. Integrate with index retrieval and ranking.
 * @author Nuno Cardoso
 *
 */
class SaskiaQuerier {
     
    static Logger log = Logger.getLogger("Querier")
    SaskiaDB db
    
    public SaskiaQuerier() {
	db = SaskiaDB.newInstance()
    }

    // obsolete
    
  //  public SaskiaQuerier queryWithTerms(String query, 
  //		int threshold_score = 0, Collection collection, int limit=10, int offset= 0) {

  //	    List res = []
  //	    int total
	    
		/*String sql_query = "SELECT SQL_CALC_FOUND_ROWS rdoc_doc, MATCH(rdoc_content) "+
		"AGAINST (? IN NATURAL LANGUAGE MODE) as Relevance FROM rembrandted_doc, "+
		"collection_has_doc WHERE chd_collection=? and chd_document=rdoc_doc and "+
		"MATCH (rdoc_content) AGAINST(? IN NATURAL LANGUAGE MODE) "+
		"HAVING Relevance > ? LIMIT ? OFFSET ? UNION SELECT 0, FOUND_ROWS()"
		 */
        
     /*   	 String sql_query = "SELECT SQL_CALC_FOUND_ROWS rdoc_doc, MATCH(rdoc_body) "+
                 "AGAINST (? IN NATURAL LANGUAGE MODE) as Relevance FROM rembrandted_doc, "+
                 "collection_has_doc WHERE chd_collection=? and chd_document=rdoc_doc and "+
                 "MATCH (rdoc_body) AGAINST(? IN NATURAL LANGUAGE MODE) "+
                 "HAVING Relevance > ? LIMIT ? OFFSET ? UNION SELECT 0, FOUND_ROWS()"
                 
		log.trace "Issuing SQL query $sql_query, query $query, threshold $threshold_score, limit $limit, offset $offset" 

		// note that the last row has "rdoc_doc" not a id, but as a string "total".
		// relevante is not a score, but the total count number.
		db.getDB().eachRow(sql_query,[query, collection.col_id, query, threshold_score, limit, offset]) {row -> 
			//println "row=$row row_rdoc_doc="+row['rdoc_doc']+" class="+row['rdoc_doc'].class
			if (row['rdoc_doc'] == 0) total =(int)( row['Relevance'] )
			 else res << RembrandtedDoc.getFromID( 
				 row['rdoc_doc'] instanceof BigDecimal ? row['rdoc_doc'].longValue() : (long)row['rdoc_doc'])
		}
		
		return ["total":total, "offset":offset, "limit": res.size(),  "result":res]
	}*/
	
/*	public SaskiaQuerier queryWithTermsAndDBpediaClass(String query, List dbpedia_classes, 
		int threshold_score = 0, int limit=10, int offset= 0) {
		if (!dbpedia_classes) return null
		List res = []
		int total
		
		String sql_dbpedia_class = dbpedia_classes.collect{
			"ent_dbpedia_class=\""+DBpediaOntology.getShortName(it)+"\""}.join(" OR ")
		
	/*	String sql_query = "SELECT SQL_CALC_FOUND_ROWS rdoc_doc, MATCH( rdoc_content) "+
		"AGAINST (\"$query\" IN NATURAL LANGUAGE MODE) as Relevance FROM rembrandted_doc, doc, entity "+
		"WHERE rdoc_doc=doc_id AND doc_entity=ent_id AND "+
		"MATCH ( rdoc_content) AGAINST(\"$query\" IN NATURAL LANGUAGE MODE) AND "+
		"($sql_dbpedia_class) HAVING Relevance > ? LIMIT ? OFFSET ? UNION SELECT 'total', FOUND_ROWS()"
*/
/*		String sql_query = "SELECT SQL_CALC_FOUND_ROWS rdoc_doc, MATCH( rdoc_body) "+
 "AGAINST (\"$query\" IN NATURAL LANGUAGE MODE) as Relevance FROM rembrandted_doc, doc, entity "+
 "WHERE rdoc_doc=doc_id AND doc_entity=ent_id AND "+
 "MATCH ( rdoc_body) AGAINST(\"$query\" IN NATURAL LANGUAGE MODE) AND "+
 "($sql_dbpedia_class) HAVING Relevance > ? LIMIT ? OFFSET ? UNION SELECT 'total', FOUND_ROWS()"
 

			log.trace "Issuing SQL query $sql_query, threshold $threshold_score, limit $limit" 
		
		db.getDB().eachRow(sql_query, [threshold_score, limit])
		{row -> if (row['rdoc_doc'] == "total") total =(int)( row['RelevaSaskiaQuerier'] )
			 else res << RembrandtedDoc.getFromID( Long.parseLong(row['rdoc_doc']))
}
			return ["total":total, "offset":offset, "limit": res.size(),  "result":res]
	}	
	 
	/** This class requires a query for both saskia and wikirembrandt databases */
/*	public SaskiaQuerier queryWithTermsAndWikipediaCategory(String query, 
		List wikipedia_categories, int threshold_score = 0, int limit=10, int offset= 0) {
	    
		if (!wikipedia_categories) return null
		List res = []
		int total
		
		def saskiaDB = conf.get("dbpedia.wikipedia.db.name","saskia")
		def wikirembrandtDB = conf.get("dbpedia.wikirembrandt.db.name","wikirembrandt")
		
		String sql_wikipedia_categories = wikipedia_categories.collect{
			"${saskiaDB}.${lang}_categorylinks.cl_to=\""+it.replaceAll(" ","_")+"\""}.join(" OR ")
		
		String sql_query = "SELECT SQL_CALC_FOUND_ROWS ${wikirembrandtDB}.rembrandted_doc.rdoc_doc, "+
		"MATCH(${wikirembrandtDB}.rembrandted_doc.rdoc_content) "+
		"AGAINST (\"$query\" IN NATURAL LANGUAGE MODE) as Relevance FROM ${wikirembrandtDB}.rembrandted_doc, "+
		"${wikirembrandtDB}.doc, ${saskiaDB}.${lang}_categorylinks WHERE ($sql_wikipedia_categories) AND "+
		"${saskiaDB}.${lang}_categorylinks.cl_from=${wikirembrandtDB}.doc.doc_original_id AND "+
		"${wikirembrandtDB}.rembrandted_doc.rdoc_doc=${wikirembrandtDB}.doc.doc_id AND "+
		"MATCH (${wikirembrandtDB}.rembrandted_doc.rdoc_content) "+
		"AGAINST(\"$query\" IN NATURAL LANGUAGE MODE) "+
		"HAVING Relevance > ? LIMIT ? OFFSET ? UNION SELECT 'total', FOUND_ROWS()"
			
		//page_id from pt_page, pt_categorylinks where " and ;	
		log.trace "Issuing SQL query $sql_query, threshold $threshold_score, limit $limit" 
		
		db.getDB().eachRow(sql_query, [threshold_score, limit])
		{row -> if (row['rdoc_doc'] == "total") total =(int)( row['Relevance'] )
			 else res << RembrandtedDoc.getFromID( Long.parseLong(row['rdoc_doc']))
		}
			return ["total":total, "offset":offset, "limit": res.size(),  "result":res]
	}*/
	
/*	static main(args) {
	    SaskiaQuerier q = new SaskiaQuerier()
	    RembrandtedDocument2PlainTextConverter r2t = new RembrandtedDocument2PlainTextConverter()
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in))	
		String line
		Collection collection = Collection.getFromName("wikipedia-pt")
	    while (true) {
			print "SaskiaQuerier> "
			line = input.readLine().trim()
			if (!line) break
			HashMap results = q.queryWithTerms(line, 0, collection, 10, 0)
			if (!results) println "No results."
			else {
			    results.result.each{rdoc ->					
					println "ORIGINAL: "+rdoc.rdoc_content.substring(0,100)
					println "PARSED: "+r2t.parse(rdoc.rdoc_content.substring(0,100))
			    }
			}
	    }
	}*/
}