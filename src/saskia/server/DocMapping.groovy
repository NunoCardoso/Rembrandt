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

import saskia.io.RembrandtedDoc
import saskia.io.User
import saskia.stats.SaskiaStats
import saskia.io.Collection
import saskia.util.I18n
import org.apache.log4j.Logger

/** this is a stripped version of doc, just to see and stuff, probably to manage nes at the doc level */
public class DocMapping extends WebServiceRestletMapping {

   SaskiaStats stats 
   Closure JSONanswer 
   I18n i18n
   static Logger log = Logger.getLogger("SaskiaServer") 
   static Logger log2 = Logger.getLogger("SaskiaService") 

   public DocMapping() {
	    
      JSONanswer = {req, par, bind ->
        
        long session = System.currentTimeMillis()
        
        i18n = I18n.newInstance()
        
          /** GET **/
         String action =  par["GET"]["do"]
         String collection_name = par["GET"]["c"] 
	 String lang = par["GET"]["lg"]   
         String api_key 
         if (par["GET"]["api_key"]) api_key = par["GET"]["api_key"]
         
        ServerMessage sm = new ServerMessage("DocMapping", lang, bind, session)  
                                                 
        Long doc_id 
         try {
            doc_id  =  Long.parseLong(par["GET"]["doc_id"])
         } catch(Exception e) {
             return sm.statusMessage(-1, i18n.servermessage['invalid_id'][lang])
         }
 

        /** CHECK COLLECTION **/
        Collection collection = Collection.getFromName(collection_name)
        if (!collection) return sm.statusMessage(-1, i18n.servermessage['collection_not_found'][lang])
        
        User user 
       
        /** CHECK API KEY / USER. IF NO API_KEY, use Guest instead **/      
        if (api_key) {
            user = User.getFromAPIKey(api_key)
        } else {
            user = User.getFromLogin(User.guest)        
        }
        
         /** CHECK USER PERMS **/      
        if (!user.canReadCollection(collection) && !user.isSuperUser()) 
            return sm.statusMessage(-1, i18n.servermessage['user_cant_read_collection'][lang])
        
        /** GET DOC **/
        RembrandtedDoc rdoc = RembrandtedDoc.getFromID(doc_id)
	 		
	 if (!rdoc) { 
	    bind["status"] = -1
	    bind["do"] = action		
	    bind["doc_id"] = doc_id			
	    bind["message"] = i18n.servermessage['document_not_found'][lang]
	    log2.debug "$session DocMapping: $bind"        
	    return JSONHelper.toJSON(bind)			    
        }
        
        stats = new SaskiaStats()
        String body = rdoc.getBodyFromContent()
        String title = rdoc.getTitleFromContent()
        
	/*** 1. SHOW DOC ***/
        if (action == "show") {
	   bind["status"] = 0
	   bind["do"] = "showdoc"				
	   bind["doc_id"] = rdoc.doc_id
           bind["doc_original_id"] = rdoc.doc_original_id
           bind["dt"] = title.replaceAll(/\n/,"")
           bind["db"] = body.replaceAll(/\n/,"")
           log2.debug "$session DocMapping:$action: status 0 OK"
           return JSONHelper.toJSON(bind)			    
        }
						
        if (action == "detail") {
           
            def answer 
            try {
        	answer = stats.renderDocPage(rdoc, collection, lang)
            } catch(Exception e) {
                bind["status"] = -1
                bind["message"] = "Problens getting doc detail: "+e.getMessage()
                log2.debug "$session DocMapping:$action: $bind" 
                return JSONHelper.toJSON(bind)		
            }
            bind["status"] = 0
            bind["do"] = "detaildoc"				
            bind["doc_id"] = rdoc.doc_id	
            bind["doc_original_id"] = rdoc.doc_original_id
            bind["content"] = answer.replaceAll(/\n/,"")
            log2.debug "$session DocMapping:$action: status 0 OK"
            return JSONHelper.toJSON(bind)			    
        }
        bind['status']=-1
        bind['message'] = i18n.servermessage['action_unknown'][lang]
        log2.debug "$session DocMapping: $bind  action $action unknown"
        return JSONHelper.toJSON(bind)		
      }//JSONclosure
   }
}
