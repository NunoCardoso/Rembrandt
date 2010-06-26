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
package saskia.server

import saskia.io.Collection
import saskia.io.User
import saskia.io.SourceDoc
import saskia.util.I18n
import org.apache.log4j.*

public class AdminSourceDocMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer	
    I18n i18n
    static Logger log = Logger.getLogger("SaskiaServer") 
    static Logger log2 = Logger.getLogger("SaskiaService") 
 
    public AdminSourceDocMapping() {
        
        i18n = I18n.newInstance()
        JSONanswer = {req, par, bind ->
            long session = System.currentTimeMillis()
            log2.debug "Session $session triggered with $par" 
            
            int limit, offset
            def column, value
            Collection collection
            
            // core stuff
            String action = par["POST"]["do"] //show, update, etc
            String lang = par["POST"]["lg"] 
            long collection_id = Long.parseLong(par["POST"]["ci"])
            
            ServerMessage sm = new ServerMessage("AdminSourceDocMapping", lang, bind, session)  
            
            // pager stuff
            if (par["POST"]["l"]) limit = Integer.parseInt(par["POST"]["l"])
            if (par["POST"]["o"]) offset = Integer.parseInt(par["POST"]["o"])
            if (par["POST"]["c"]) column = par["POST"]["c"]
            if (par["POST"]["v"]) value = par["POST"]["v"]
            
            // auth stuff
            String api_key = par["POST"]["api_key"] 
            if (!api_key) api_key = par["COOKIE"]["api_key"] 
            if (!api_key) return sm.noAPIKeyMessage()
            
            User user = User.getFromAPIKey(api_key)           
            if (!user) return sm.userNotFound()
            if (!user.isSuperUser()) return sm.noSuperUser()
            if (!user.isEnabled()) return sm.userNotEnabled()
            
            if (!action || !lang || !collection_id) return sm.notEnoughVars(lang, "do=$action, lg=$lang, ci=$collection_id")        	
            sm.setAction(action)
            
            collection = Collection.getFromID(collection_id)
            if (!collection) return sm.statusMessage(-1, i18n.servermessage['no_collection_found'][lang])
    
                        
            /***************************/
            /** 1.1 show multiple SDOC metadata **/
            /***************************/
            Map h 
            
            if (action == "show") {
        	if (!(user.isSuperUser() || user.canReadCollection(collection)) )  
        	    return sm.statusMessage(-1, i18n.servermessage['no_collection_admin'][lang])
        	       	    
                try {
                    log.debug "Querying source docs: limit $limit offset $offset column $column value $value"
                    h = SourceDoc.getSourceDocs(collection, limit, offset, column, value)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_sdoc_list"][lang]+": "+e.getMessage())
                }
                
                // hashmap has total, offset, limit, result as a List<Users>. We need to convert User objects 
                // in a HashMap friendly JSON format
                h.result.eachWithIndex{sdoc, i -> 
                    h.result[i] = ["sdoc_id":sdoc.sdoc_id, "sdoc_original_id":sdoc.sdoc_original_id, 
                    "sdoc_webstore":sdoc.sdoc_webstore, "sdoc_lang":sdoc.sdoc_lang, 
                    "sdoc_comment":sdoc.sdoc_comment, "sdoc_date":sdoc.sdoc_date, 
                    "sdoc_proc":sdoc.sdoc_proc, "sdoc_edit":sdoc.sdoc_edit]
                }
                return sm.statusMessage(0, h)           
            }
            
            /***************************/
            /** 1.2 show a single SDOC content **/
            /***************************/
            
            if (action == "showsdoc") {
                long sdoc_id
                if (par["POST"]["sdoc_id"]) sdoc_id = Long.parseLong(par["POST"]["sdoc_id"])
                if (!sdoc_id) return sm.notEnoughVars("sdoc_id=$sdoc_id")
                
                SourceDoc sdoc 
                try {
                    log.debug "Querying source doc with id $sdoc_id"
                    sdoc = SourceDoc.getFromID(sdoc_id)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_sdoc_list"][lang]+": "+e.getMessage())                 
                }
                
                // hashmap has total, offset, limit, result as a List<Users>. We need to convert User objects 
                // in a HashMap friendly JSON format
                String res 
                try {
                    res= sdoc.getHTML()
                } catch(Exception e) {return sm.statusMessage(-1, e.getMessage())}
                
                return sm.statusMessage(0, res)
            }
            
            return sm.unknownAction()		
        }
    }
}
