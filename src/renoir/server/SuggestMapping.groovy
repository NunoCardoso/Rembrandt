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

import saskia.db.database.SaskiaMainDB;

import java.net.URLDecoder
import org.apache.log4j.*

public class SuggestMapping extends WebServiceRestletMapping {
    
    Closure HTMLanswer
    Closure JSONanswer
    SaskiaMainDB db 
    static Logger log2 = Logger.getLogger("RenoirServer") 
    
    public SuggestMapping() {
        
        db = SaskiaMainDB.newInstance()
        
        HTMLanswer = { req, par, bind ->
            return "Sorry, HTML mimetype is not handled. JSON only."	
        }
        
        //	  JSON response
        JSONanswer = { req, par, bind ->
 
            def url = URLDecoder.decode(par["GET"]["q"], "UTF-8")
            def type
            def collection 
            def lang
                        
            if (par["GET"]["t"])  type = par["GET"]["t"]
            //if (par(["GET"]["c"]))  collection = par["GET"]["c"]
            if (par["GET"]["lg"]) lang = par["GET"]["lg"]
            
            def answer = []   
            if (url) {
                if (type && type == "ne") {
                    db.getDB().eachRow("SELECT sug_name, sug_type, sug_desc, sug_ground FROM suggestion WHERE sug_name like('" +url+ "%') "+
                            "AND sug_type=? AND sug_lang=? ORDER BY sug_name", ["NE", lang], {row -> 
                                answer << ""+row['sug_name']+"|"+row['sug_type']+"|"+row['sug_desc']+"|"+row['sug_ground']
                            })	
                } else {
                    db.getDB().eachRow("SELECT sug_name, sug_type, sug_desc, sug_ground FROM suggestion WHERE sug_name like('" +url+ "%') ORDER BY sug_name") {row -> 
                        answer << ""+row['sug_name']+"|"+row['sug_type']+"|"+row['sug_desc']+"|"+row['sug_ground']
                    }
                }
                
            }
            //log.debug "Returning JSON answer:${answer}"
            bind["answer"] = answer
            
            //suggestMapping.modifyStatus
            
            return JSONHelper.toJSON(bind)
            
        }	
    }
}
