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

import saskia.db.obj.Collection;
import saskia.db.table.NE;
import saskia.io.User
import saskia.stats.SaskiaStats
import saskia.util.I18n
import org.apache.log4j.*

public class NEMapping extends WebServiceRestletMapping {

    Closure JSONanswer 
    SaskiaStats stats
    static Logger mainlog = Logger.getLogger("SaskiaServerMain")  
    static Logger errorlog = Logger.getLogger("SaskiaServerErrors")  
    static Logger processlog = Logger.getLogger("SaskiaServerProcessing")  
    
    public NEMapping() {
        
       JSONanswer = {req, par, bind ->
        
           long session = System.currentTimeMillis()
           processlog.debug "Session $session triggered with $par" 
        
       	  I18n i18n = I18n.newInstance()
       	  Long id
       	  String action, collection_id, lang, nename, c1, c2, c3
       	  int s, t
        
       	  if (par["POST"]["id"] && par["POST"]["id"] != "undefined") 
			  try {id =  Long.parseLong(par["POST"]["id"])}
			  catch(Exception e) {}
       	  action =  par["POST"]["do"]
       	  if (par["POST"]["ci"]) 
		     try {collection_id = par["POST"]["ci"]}
		     catch(Exception e) {}
								
       	   lang = par["POST"]["lg"]

       		String api_key = par["POST"]["api_key"] 
            if (!api_key) api_key = par["COOKIE"]["api_key"]   
            if (!api_key) return sm.noAPIKeyMessage()

            User user = User.getFromAPIKey(api_key)           
            if (!user) return sm.userNotFound()
            if (!user.isEnabled()) return sm.userNotEnabled()
            if (!action || !lang) return sm.notEnoughVars(lang, "do=$action, lg=$lang")        	
            sm.setAction(action)
 
	   		if (par["POST"]["ne"] && par["POST"]["ne"] != "undefined" && par["POST"]["ne"] != "null") 
	   			nename = par["POST"]["ne"] 
	   		if (par["POST"]["s"] && par["POST"]["s"] != "undefined" && par["POST"]["s"] != "null") 
	   			s = Integer.parseInt(par["POST"]["s"])
	   		if (par["POST"]["t"] && par["POST"]["t"] != "undefined" && par["POST"]["t"] != "null") 
	   			t = Integer.parseInt(par["POST"]["t"])
	   		if (par["POST"]["c1"] && par["POST"]["c1"] != "undefined" && par["POST"]["c1"] != "null") 
	   			c1 = par["POST"]["c1"]
		   	if (par["POST"]["c2"] && par["POST"]["c2"] != "undefined" && par["POST"]["c2"] != "null") 
	   			c2 = par["POST"]["c2"]
	   		if (par["POST"]["c3"] && par["POST"]["c3"] != "undefined" && par["POST"]["c3"] != "null") 
	   			c3 = par["POST"]["c2"]
  
	 			ServerMessage sm = new ServerMessage("AdminRembrandtedDocMapping", lang, bind, session, processlog)  
       
            /******************/
            /** 1.5 metadata **/
            /******************/
 	    		if (action == "metadata") {
						     
				// if we have a NE id, let's go for it. We may not have.
	        		NE ne
					if (id) {ne = NE.getFromID(id) }
					else {ne = NE.getFromNameAndDocSentenceTermAndClassification(
		    			collection, nename, s, t, c1, c2, c3)}
			    	if (!ne) return sm.statusMessage(-1,i18n.servermessage['ne_not_found'][lang])

					stats = new SaskiaStats()
					def answer = stats.renderNEPage(ne.ne_id, collection, lang)
					return sm.statusMessage(0, answer)
				}	
            return sm.unknownAction(action)			    
			}
    }
}