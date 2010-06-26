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

import saskia.io.*
import java.net.URLDecoder
import org.apache.log4j.*

public class SuggestMapping2 extends WebServiceRestletMapping {
    
    Closure JSONanswer
    SaskiaDB db 
    static Logger log2 = Logger.getLogger("RenoirServer") 
     
    public SuggestMapping2() {
        
        db = SaskiaDB.newInstance()
        
           //	  JSON response
        JSONanswer = { req, par, bind ->
 
            def q = URLDecoder.decode(par["GET"]["q"], "UTF-8")
            def type
            def collection 
            def lang
                        
            if (par["GET"]["t"])  type = par["GET"]["t"]
            if (par["GET"]["lg"]) lang = par["GET"]["lg"]
            
            List answer = []   
            if (q && type) {
        	// type is: tag, ne_name, ne_category, ne_type, ne_subtype, entity, geoscope, subject, 
        	switch(type) {
                    case "doc_tag":
                	if (!Tag.cache) Tag.refreshCache()
                	List<Tag> res = Tag.cache.values().toList().findAll{it.tag_version =~ /(?i)${q}/}   
                	res.each{answer << [it.tag_id, it.tag_version] }           	
                    break	
                    case "ne_name":
                	db.getDB().eachRow("SELECT nen_id, nen_name FROM ne_name WHERE nen_name like('" +q+ "%') ORDER BY nen_name", 
                	{row ->  answer << [row['nen_id'], row['nen_name']] })	                	
                    break
                    case "ne_category":
                	NECategory.createCache() // it checks if there is one, relax
                	List<NECategory> res = NECategory.all_id_category.values().toList().findAll{it.nec_category =~ /(?i)${q}/}   
                	res.each{answer << [it.nec_id, it.nec_category] }
                    break
                    case "ne_type":
                	NEType.createCache() // it checks if there is one, relax
                	List<NEType> res = NEType.all_id_type.values().toList().findAll{it.net_type =~ /(?i)${q}/}   
                	res.each{answer << [it.net_id, it.net_type] }            	
                    break                  
                    case "ne_subtype":
                	NESubtype.createCache() // it checks if there is one, relax
                	List<NESubtype> res = NESubtype.all_id_subtype.values().toList().findAll{it.nes_subtype =~ /(?i)${q}/}   
                	res.each{answer << [it.nes_id, it.nes_subtype] }  
                    break
                    case "entity":
                       	db.getDB().eachRow("SELECT ent_id, ent_dbpedia_resource FROM entity WHERE ent_dbpedia_resource like('" +q+ "%') ORDER BY ent_dbpedia_resource", 
                       	{row ->  answer << [row['ent_id'], row['ent_dbpedia_resource'] ] })               	
                    break
                    case "geoscope":
                       	db.getDB().eachRow("SELECT geo_id, geo_name FROM geoscope WHERE geo_name like('%" +q+ "%') ORDER BY geo_name", 
                      	{row ->  answer << [row['geo_id'], row['geo_name'] ] })               	                    
                    break
                    case "subject":
                      	db.getDB().eachRow("SELECT sbj_id, sbj_subject FROM subject WHERE sbj_subject like('" +q+ "%') ORDER BY sbj_subject", 
                        {row ->  answer << [row['sbj_id'], +row['sbj_subject'] ] })               	   	
                    break	
        	}
            }
           // println "answer: "+answer
            //log.debug "Returning JSON answer:${answer}"
            bind["status"] = 0
            bind["message"] = answer
            return JSONHelper.toJSON(bind)
            
        }	
    }
}
