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
package renoir.server


import org.apache.log4j.Logger 
import renoir.bin.RenoirCore
import renoir.bin.Renoir
import renoir.obj.RenoirQuery
import renoir.obj.QueryTag
import renoir.obj.RenoirQueryParser
import saskia.bin.Configuration
import saskia.io.RembrandtedDoc
import saskia.io.DocGeoSignature
import saskia.io.GeoSignature
import saskia.io.Collection
import saskia.io.User
//import saskia.converters.RembrandtedDocument2PlainTextConverter
import saskia.util.I18n

import org.apache.lucene.search.highlight.Highlighter
import org.apache.lucene.search.highlight.QueryScorer
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.search.Query as LuceneQuery
import org.apache.lucene.analysis.TokenStream

public class SearchMapping extends WebServiceRestletMapping {

    	Renoir renoir
	Closure JSONanswer
	User user
	Collection collection
        Configuration conf = Configuration.newInstance()
	I18n i18n
	static Logger log2 = Logger.getLogger("RenoirSearch") 

	public SearchMapping() {
	
	    JSONanswer = {req, par, bind ->
            
            	long session = System.currentTimeMillis()
            	log2.debug "Session $session triggered with $par" 
            
	    	i18n = I18n.newInstance()
	    	
	    	/*****************************/
                /* collecting vars
                /*****************************/
	    	
	    	// GET
	    	String query = java.net.URLDecoder.decode(par["GET"]["q"])

	    	// POST
	    	Long col_id 
	    	
	    	String user_login
		if (par["POST"]["u"])  user_login = par["POST"]["u"]
		String api_key = par["POST"]["api_key"]
		String lang = par["POST"]["lg"]
		int limit = (par["POST"]["l"] ? Integer.parseInt(par["POST"]["l"]) : 10)
		int offset = (par["POST"]["o"] ?  Integer.parseInt(par["POST"]["o"]) : 0)
        
		ServerMessage sm = new ServerMessage("SearchMapping", lang, bind, session)  

            	// collection checkup
            	try {col_id = Long.parseLong( par["POST"]["ci"])
	    	}catch(Exception e) {return  sm.notEnoughVars("ci=$ci")}
	    	collection = Collection.getFromID(col_id)
		if (!collection) return sm.statusMessage(-1, i18n.servermessage['collection_not_found'][lang])
		  
		// POST: advanced options
		List tags
		String qe, model
		boolean maps = false, feedback = false
		if (par["POST"]["t"]) tags = QueryTag.parseTags(par["POST"]["t"])
		if (par["POST"]["qe"]) qe = par["POST"]["qe"]
		if (par["POST"]["model"]) model = par["POST"]["model"]
                if (par["POST"]["maps"]) maps = Boolean.parseBoolean(par["POST"]["maps"])
                if (par["POST"]["feedback"]) feedback = Boolean.parseBoolean(par["POST"]["feedback"])            

                	    	
	    	/*****************************/
                /* user authenticating
                /*****************************/

                if (api_key) user = User.getFromAPIKey(api_key)
            	else {
                    if (user_login && user_login != User.guests[lang]) user = User.getFromLogin(user_login)
                    else user = User.getFromLogin(User.guest)          
	    	}
                
		if (!user || (!user.canReadCollection(collection) && !user.isSuperUser()) )         
	            return sm.statusMessage(-1, i18n.servermessage['user_cant_read_collection'][lang])
		
	    
                /*****************************/
                /* RENOIR creation
                /*****************************/
	    
                try {
                    renoir = RenoirCore.getCore(conf, collection)
                } catch(Exception e) {return sm.statusMessage(-1, e.getMessage())}
                
		List result = []
		Map answer 
        
	    	
	    	/*****************************/
                /* query parsing & generation
                /*****************************/

		RenoirQuery q = RenoirQueryParser.parse(query)
        
 	        // meter aqui o limit e offset - this overwrites the parameters 
		q.paramsForRenoir["limit"] = limit
		q.paramsForRenoir["offset"] = offset
		q.paramsForRenoir["search"] = true
		// meter o qe, model, tags. Tags are already parsed	
		q.paramsForRenoir["qe"] =  (qe ? qe : "no")
		
		q.paramsForLGTE["model"] =  (model ? model : "bm25")
		
	    	/*****************************/
                /* SEARCH!
                /*****************************/

		// o meu maps e feedback ficam aqui
		try {
		    log2.debug "User $user is performing search $q"
		    answer = renoir.search(q)
        	}catch(Exception e) {
        	    e.printStackTrace(); return sm.statusMessage(-1, "Search went wrong: "+e.getMessage())
        	}
		 	
	    	
	    	/*****************************/
                /* result parsing
                /*****************************/

		bind["nr_results_shown"] = answer["nr_results_shown"]
		bind["nr_first_result"] = answer["nr_first_result"]
		bind["nr_last_result"] = answer["nr_last_result"]
		bind["total"] = answer["total"]
        
		// PREPARE EXPLANATION
		Map explanation = [:]
		log2.debug "Got tags: $tags"
		if (tags) explanation["tags"] = QueryTag.toJSON(tags) 

                // if feedback
                if (feedback) {
                    explanation["feedback"] = "Query submitted: "+q.toString()
                }
        
		if (explanation) bind["explanation"] = explanation
		
		// creating snippets and geo info
                QueryParser qp = new QueryParser("contents", new StandardAnalyzer())
                LuceneQuery lquery = qp.parse(q.toVisibleTerms())
                QueryScorer scorer = new QueryScorer(lquery)
                Highlighter h = new Highlighter(scorer)
                   
		answer["result"].each{r -> 
            
		   Map res
		   
			// now let's convert doc_original_id from the collection to the text
		   try {
			RembrandtedDoc rdoc = RembrandtedDoc.getFromOriginalDocIDandCollection(r["docid"],collection)
			String body = rdoc.getPlainText(rdoc.getBodyFromContent())
			String title = rdoc.getPlainText(rdoc.getTitleFromContent())
			TokenStream ts = new StandardAnalyzer().tokenStream("text", new StringReader(body))

			res = [           
                            'i':r["i"],
                            'title':title.replaceAll(/\n/,""), 
                            'doc_id':rdoc.doc_id, 
                            'doc_original_id':rdoc.doc_original_id, 
                            'abstract':h.getBestFragments(ts, body, 5,"(...)").replaceAll("\n"," "),
                            //'body':body.replaceAll(/\n/,""), 
                            'size':rdoc.getBodyFromContent().size(), 
                            'date':""+rdoc.doc_date_created,
                            // comment is not supported now
                            'score':r["score"],			 
                            'partial_score':r["partialscore"]
                        ]
            
			List coordinates = [], polylines = []
			if (maps) { 
			    // get from table doc_geo_signature the stuff for this document
			    DocGeoSignature dgs = rdoc.getGeographicSignature()
			    if (dgs) {
				// get the dgs_signature. Note that is has only ancestors/centroid/bb info, no shape.
				GeoSignature geosig = new GeoSignature(dgs)
				// go to the Geoscope table and see if we can add shapes on it
				geosig.addPolylineInfo()

				/** gives a list of centroids. Each centroid is a Map, easily JSONable. */ 
				coordinates = geosig.places*.centroid
				res['coordinates'] = coordinates
				/** several polilynes, one for each entity in the document */
				polylines = geosig.places*.polyline
				res['polylines'] = polylines.findAll{it != null}
			    }
			}
		  
                        result << res
            
		   } catch(Exception ex) {
		      bind["status"] = -1
		      bind["message"] = "Search result parsing went wrong: "+ex.getMessage()+ex.printStackTrace()
		      log2.debug "$session SearchMapping: $bind"
		      return JSONHelper.toJSON(bind)		    
		   }
		   
		}
		bind["status"] = 0
		bind["result"] = result
		
		log2.debug "$session SearchMapping: returning status 0, OK"
		return JSONHelper.toJSON(bind)		
	    }
	 }
	
	 private String parse(String string) {
	     string = r2t.parse(string)
	 	if (string) string = string.substring(0,(string.size() > 300 ? 300 : string.size()))
	 		     // \n are not valid json chars
	 	return string.replaceAll(/\n/,"")
	 }
}
