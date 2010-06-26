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

import saskia.io.User
import saskia.util.I18n
import org.apache.log4j.*

public class AdminUserMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n
    static Logger log = Logger.getLogger("SaskiaServer") 
    static Logger log2 = Logger.getLogger("SaskiaService") 
 
/** Note: this mapping should be used only by superuser folks *managing user stuff*, so 
 * it requires an api_key. For standard uses, the UserMapping is the one that has standard actions.
 */
    public AdminUserMapping() {
        
        i18n = I18n.newInstance()
        
        JSONanswer = {req, par, bind ->
            
            long session = System.currentTimeMillis()
            log2.debug "Session $session triggered with $par" 
                  
            int limit, offset
            def column, value
            
            // core stuff
            String action = par["POST"]["do"] //show, update, etc
            String lang = par["POST"]["lg"] 
            
            ServerMessage sm = new ServerMessage("AdminUserMapping", lang, bind, session)  
            
            // pager stuff
            if (par["POST"]["l"]) limit = Integer.parseInt(par["POST"]["l"])
            if (par["POST"]["o"]) offset = Integer.parseInt(par["POST"]["o"])
            if (par["POST"]["c"]) column = par["POST"]["c"]
            if (par["POST"]["v"]) value = par["POST"]["v"]
            
            // user stuff
            String api_key = par["POST"]["api_key"] 
            if (!api_key) api_key = par["COOKIE"]["api_key"]   
            if (!api_key) return sm.noAPIKeyMessage()
            User user = User.getFromAPIKey(api_key)           
            if (!user) return sm.userNotFound()
            if (!user.isSuperUser()) return sm.noSuperUser()
            if (!user.isEnabled()) return sm.userNotEnabled()
            if (!action || !lang) return sm.notEnoughVars(lang, "do=$action, lg=$lang")        	
            sm.setAction(action)
           
            
            /***************************/
            /** 1.1 show - PAGE USERS - for SU users only **/
            /***************************/
            
            if (action == "show") {
                Map h 
                try {
                    log.debug "Querying users: limit $limit offset $offset column $column value $value"
                    h = User.getUsers(limit, offset, column, value)
                } catch(Exception e) {
                    e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_user_list"][lang]+": "+e.getMessage())
                }
                
                // hashmap has total, offset, limit, result as a List<Users>. We need to convert User objects 
                // in a HashMap friendly JSON format
                h.result.eachWithIndex{u, i -> 
                    h.result[i] = ["usr_id":u.usr_id, "usr_login":u.usr_login, "usr_enabled":u.usr_enabled, 
                    "usr_superuser":u.usr_superuser, "usr_firstname":u.usr_firstname, "usr_lastname":u.usr_lastname, 
                    "usr_email":u.usr_email, "usr_api_key":u.usr_api_key, "usr_max_number_collections":u.usr_max_number_collections, 
                    "usr_max_docs_per_collection":u.usr_max_docs_per_collection, "usr_max_daily_api_calls":u.usr_max_daily_api_calls,
                    "usr_current_daily_api_calls":u.usr_current_daily_api_calls, "usr_total_api_calls":u.usr_total_api_calls]
                }
                return sm.statusMessage(0,h)
            }
            
            /******************************/
            /** 1.2 update - MODIFY USER - for SU users only **/
            /******************************/
            
            if (action == "update") {
                //requires the column and value parameters
        	long id 
                if (par["POST"]["id"]) id = Long.parseLong(par["POST"]["id"] )
                if (!id) return sm.notEnoughVars("id=$id")     
                if (value == null || !column)  return sm.notEnoughVars("v=$value, c=$column")
                
                def answer 
                try {
                    log.debug "Updating user column $column, val $value, id $id"
                    answer = User.updateValue(id, column, value)
                } catch(Exception e) {    
                    e.printStackTrace()             
                    return sm.statusMessage(-1, i18n.servermessage['error_updating_user'][lang]+": " +e.getMessage())
                    
                }
                
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(answer, value)
              
            }
            
            /***************************************/
            /** 1.2 getcolperm - GET USER PERMISSIONS for user **/
            /***************************************/
            
            if (action == "getcolperm") {
                long usr_id 
                try {usr_id =  Long.parseLong(par["POST"]["ui"])}
                catch(Exception e) {}
                
                if (!usr_id)  return sm.notEnoughVars("ui=$usr_id")                   
                User user_db = User.getFromID(usr_id)
                if (!user_db) return sm.userNotFound()
                
                List<HashMap> perms = user_db.getUserCollectionPermissionsOn() // blank means all
                if (!perms) return sm.statusMessage(-1, i18n.servermessage['no_permissions_found'][lang])
                else return sm.statusMessage(0, perms)            
            }
                        
            /***************************************/
            /** 1.3 setperm - SET USER PERMISSIONS for (user, col) **/
            /***************************************/
           
            if (action == "setperm") {
                long usr_id, col_id 
                try {
                    usr_id=  Long.parseLong(par["POST"]["ui"]) 
                    col_id =  Long.parseLong(par["POST"]["ci"])                                  
                }catch(Exception e) {}
                               
                if (!usr_id || !col_id)  return sm.notEnoughVars("ci=$col_id, ui=$usr_id")      
                
                def answer 
                try {
                    log.debug "Updating user_on_collection column $column, val $value, usr_id $usr_id, col_id $col_id"
                    answer = User.setUserCollectionPermissions(usr_id, col_id, column, value) 
                } catch(Exception e) {return sm.statusMessage(-1, i18n.servermessage["error_updating_permission"][lang]+": "+e.getMessage())}
                  
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(answer, value)             	
            }
         
            /***************************************/
            /** 1.3 delete - DELETE USER for (user) **/
            /***************************************/
           
            if (action == "delete") {
                long usr_id 
                try {
                    usr_id=  Long.parseLong(par["POST"]["ui"])                      
                }catch(Exception e) {}
                               
                if (!usr_id)  return sm.notEnoughVars("ui=$usr_id")      
                
                def answer 
                try {
                    log.debug "deleting user $usr_id"
                    answer = User.deleteUser(usr_id) 
                } catch(Exception e) {return sm.statusMessage(-1, i18n.servermessage["error_deleting_user"][lang]+": "+e.getMessage())}
                  
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(answer, i18n.servermessage['ok'][lang])		   	
            }
            return sm.unknownAction(action)	
        }
    }
}
