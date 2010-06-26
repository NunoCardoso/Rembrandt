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
import saskia.io.Cache
import saskia.io.User
import saskia.util.I18n
import org.apache.log4j.*
import saskia.stats.SaskiaStats

public class AdminCollectionMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n
    SaskiaStats stats
    static Logger log = Logger.getLogger("SaskiaServer") 
    static Logger log2 = Logger.getLogger("SaskiaService") 

    public AdminCollectionMapping() {
        
        i18n = I18n.newInstance()
        
        JSONanswer = {req, par, bind ->
        
            long session = System.currentTimeMillis()
            log2.debug "Session $session triggered with $par" 
            
            int limit, offset
            def column, value
                     
            String action = par["POST"]["do"] //show, update, etc
            String lang = par["POST"]["lg"] 
                                      
            ServerMessage sm = new ServerMessage("AdminCollectionMapping", lang, bind, session)                          
            
            if (par["POST"]["l"]) limit = Integer.parseInt(par["POST"]["l"])
            if (par["POST"]["o"]) offset = Integer.parseInt(par["POST"]["o"])
            if (par["POST"]["c"]) column = par["POST"]["c"]
            if (par["POST"]["v"]) value = par["POST"]["v"]
         
            
            String api_key = par["POST"]["api_key"] 
            if (!api_key) api_key = par["COOKIE"]["api_key"]   
            if (!api_key) return sm.noAPIKeyMessage()
            User user = User.getFromAPIKey(api_key)           
            if (!user) return sm.userNotFound()
            if (!user.isEnabled()) return sm.userNotEnabled()
            if (!action || !lang) return sm.notEnoughVars(lang, "do=$action, lg=$lang")        	
            sm.setAction(action)
            
            /*****************************************************/
            /** 1.1 show - PAGE COLLECTIONS - only for SHOW users **/
            /*****************************************************/
            
            if (action == "show") {
        	
                //log.debug("I'm on show ")
                    	
                Map h 
               
                try {
                    // here is log, not log2. 
                    log.debug ("$session ${user.usr_login} AdminCollectionMapping:show: Requesting collections: limit $limit, offset $offset column $column value $value")
                    if (user.isSuperUser())
                	h = Collection.getCollections(limit, offset, column, value)
                    else 
                	h = Collection.getShowableCollectionsForUser(user, limit, offset, column, value)
                    
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage['error_getting_col_list'][lang]+": "+e.getMessage(), action)
                }
       
                //log.debug "Collections: $collections"
                List res = []
                h.result.eachWithIndex{col, i -> 
                   
                    h.result[i] = ['col_id':col.col_id, 'col_name':col.col_name, 'col_lang':col.col_lang, 
                            'col_comment':col.col_comment, 'col_new_user_can_read':col.col_new_user_can_read]
                            
                }              
                return sm.statusMessage(0, h)        
            }
   
            /***************************************************/
            /** 1.2 update - MODIFY A VALUE - only for ADMIN users */
            /***************************************************/
            
            if (action == "update") {
        	
        	//requires the column and value parameter
        	// the id is from a collection
                long id 
                if (par["POST"]["id"]) id = Long.parseLong(par["POST"]["id"] )
                if (!id) return sm.notEnoughVars("id=$id")
                
                // let's check permissions
                Collection collection = Collection.getFromID(id)
                
                if (!(user.isSuperUser() || user.canAdminCollection(collection)) )                   
                    return sm.statusMessage(-1, i18n.servermessage['no_collection_admin'][lang])
                          
            
                if (value == null || !column) return sm.notEnoughVars("v=$value, c=$column")
                        
                def answer 
                
                try {
                    // here is log, not log2
                    log.debug "${user.usr_login}: Updating collection column $column, val $value,id $id"
                    answer = Collection.updateValue(id, column, value)                     
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage['error_updating_collection'][lang]+": " +e.getMessage())
                }
                
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(answer, value)	// the value will be used on the UI	
            }
            
            /*************************************************/
            /**  1.3 refreshcache - REFRESH STATS COLLECTION - only for ADMIN users */
            /*************************************************/
            
            if (action == "refreshcache") {
        	
        	long id 
        	if (par["POST"]["id"]) id = Long.parseLong(par["POST"]["id"] )
                if (!id) return sm.notEnoughVars("id=$id")
                
                Collection collection = Collection.getFromID(id)
                if (!(user.isSuperUser() || user.canAdminCollection(collection)) )
                    return sm.statusMessage(-1, i18n.servermessage['no_collection_admin'][lang])
               
                stats = new SaskiaStats()
                try {
                    log.debug "refreshing cache for $id $lang"                  
                    stats.forceRefreshCacheOnFrontPage(collection, lang)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage['error_creating_cache_for_collection'][lang]+": " +e.getMessage())
                }              
                sm.statusMessage(0, i18n.servermessage['ok'][lang])		
            }
            
            
            /****************************************/
            /** 1.4 create - CREATE NEW COLLECTION - for ADMIN users or user with credits for**/
            /****************************************/
            
            if (action == "create") {
        	
                String col_name =  par["POST"]["col_name"] 
                String col_comment =  par["POST"]["col_comment"] 
                Boolean col_new_user_can_read 
                 try {col_new_user_can_read = Boolean.parseBoolean(par["POST"]["col_new_user_can_read"]) 
                 } catch(Exception e) {}
                
                if (!col_name || col_new_user_can_read == null) 
                    return sm.notEnoughVars("col_name=$col_name col_new_user_can_read=$col_new_user_can_read") 
                
                 if (!(user.isSuperUser() || user.canCreateCollection()) )
                    return sm.statusMessage(-1, i18n.servermessage['collection_number_limit_reached'][lang])
                                    
                Collection collection = Collection.getFromName(col_name)
                if (collection) return sm.statusMessage(-1, i18n.servermessage['collection_already_exists'][lang])
                
                log.debug "Creating new collection... name=$col_name, new_user_can_read class="+col_new_user_can_read?.class
                  
                try {
                    collection = new Collection(col_name:col_name, col_comment:col_comment, 
                	    col_new_user_can_read:col_new_user_can_read)
                    collection.addThisToDB()
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage['error_creating_collection'][lang]+": "+e.getMessage())
                }
                        
                // leave like that, to include an id field
                bind['status'] = 0
                bind['message'] =  i18n.servermessage['ok'][lang]
                bind['id'] = collection.col_id
                log2.debug "$session AdminCollectionMapping:$action: $bind status 0 OK - collection $col_name created"
                return JSONHelper.toJSON(bind)		
            }
                       
            return sm.unknownAction(action)	
        }       
    }
}