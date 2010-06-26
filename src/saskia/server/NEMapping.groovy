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
import saskia.stats.SaskiaStats
import saskia.io.NE
import saskia.util.I18n
import org.apache.log4j.*

public class NEMapping extends WebServiceRestletMapping {

    Closure JSONanswer 
    SaskiaStats stats
    static Logger log = Logger.getLogger("SaskiaServer") 
    static Logger log2 = Logger.getLogger("SaskiaService") 
    
    public NEMapping() {
        
       JSONanswer = {req, par, bind ->
        
           long session = System.currentTimeMillis()
           log2.debug "Session $session triggered with $par" 
        
       	   I18n i18n = I18n.newInstance()
       	   long id
       	   String action, collection_name, lang, nename, c1, c2, c3
       	   int s, t
        
       	   if(par["GET"]["id"] && par["GET"]["id"] != "undefined") id =  Long.parseLong(par["GET"]["id"])
       	   action =  par["GET"]["do"]
       	   collection_name = par["GET"]["c"] 
       	   lang = par["GET"]["lg"]
	   if (par["GET"]["ne"] && par["GET"]["ne"] != "undefined" && par["GET"]["ne"] != "null") 
	   nename = par["GET"]["ne"] 
	   if (par["GET"]["s"] && par["GET"]["s"] != "undefined" && par["GET"]["s"] != "null") 
	   s = Integer.parseInt(par["GET"]["s"])
	   if (par["GET"]["t"] && par["GET"]["t"] != "undefined" && par["GET"]["t"] != "null") 
	   t = Integer.parseInt(par["GET"]["t"])
	   if (par["GET"]["c1"] && par["GET"]["c1"] != "undefined" && par["GET"]["c1"] != "null") 
	   c1 = par["GET"]["c1"]
	   if (par["GET"]["c2"] && par["GET"]["c2"] != "undefined" && par["GET"]["c2"] != "null") 
	   c2 = par["GET"]["c2"]
	   if (par["GET"]["c3"] && par["GET"]["c3"] != "undefined" && par["GET"]["c3"] != "null") 
	   c3 = par["GET"]["c2"]
        
	 ServerMessage sm = new ServerMessage("AdminRembrandtedDocMapping", lang, bind, session)  
                    
        /** CHECK COLLECTION **/
        Collection collection = Collection.getFromName(collection_name)
        if (!collection) {
            bind["status"] = -1
            bind["message"] = i18n.servermessage['collection_not_found'][lang]
            log2.debug "$session NEMapping: $bind - collectionname $collection_name"
            return  JSONHelper.toJSON(bind)
        }
        
        User user 
        /** CHECK API KEY / USER **/      
        if (api_key) {
            user = User.getFromAPIKey(api_key)
        } else {
            if (user_login) { 
                if (user_login == User.guests[lang]) user_login=User.guest
                user = User.getFromLogin(user_login)
            } else {
                user_login == User.guests[lang]
                user = User.getFromLogin(user_login)
            }            
        }
        
        /** CHECK USER PERMS **/      
        if (!user.canReadCollection(collection) && !user.isSuperUser()) 
            return sm.statusMessage(-1, i18n.servermessage['user_cant_read_collection'][lang])
       
        
        /*** 1. DETAIL DOC ***/
	    if (action == "detail") {
		bind["id"] = id
		bind["do"] = "detailne"		     
				
		// if we have a NE id, let's go for it. We may not have.
	        NE ne
		if (id) {ne = NE.getFromID(id) }
		else {ne = NE.getFromNameAndDocSentenceTermAndClassification(
		    collection, nename, s, t, c1, c2, c3)}
			    
		if (!ne) {
		    bind["status"] = -1
		    bind["message"] = i18n.servermessage['nenotfound'][lang]
		    log2.debug "$session NEMapping:$action: $bind" 
		    return JSONHelper.toJSON(bind)						
		}

		stats = new SaskiaStats()
		def answer = stats.renderNEPage(ne.ne_id, collection, lang)
		bind["status"] = 0		
		bind["content"] = answer 
		log2.debug "$session NEMapping:$action: status 0 OK" 
		return JSONHelper.toJSON(bind)		
				    
	    }
            bind['status']=-1
            bind['message'] = i18n.servermessage['action_unknown'][lang]
            log2.debug "$session NEMapping: $bind  action $action unknown"
            return JSONHelper.toJSON(bind)	
	    
	}
    }
}
