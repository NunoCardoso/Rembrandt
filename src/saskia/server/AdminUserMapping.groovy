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

import saskia.db.database.SaskiaDB
import saskia.db.obj.*
import saskia.db.table.*

import saskia.util.I18n
import org.apache.log4j.*

public class AdminUserMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n
	SaskiaDB db
    static Logger mainlog = Logger.getLogger("SaskiaServerMain")  
    static Logger errorlog = Logger.getLogger("SaskiaServerErrors")  
    static Logger processlog = Logger.getLogger("SaskiaServerProcessing")  
 
/** Note: this mapping should be used only by superuser folks *managing user stuff*, so 
 * it requires an api_key. For standard uses, the UserMapping is the one that has standard actions.
 */
    public AdminUserMapping(SaskiaDB db) {
        
		this.db = db
        i18n = I18n.newInstance()
        UserTable userTable = db.getDBTable("UserTable")
		
        JSONanswer = {req, par, bind ->
            
            long session = System.currentTimeMillis()
            processlog.debug "Session $session triggered with $par" 
                  
            int limit
				long offset
            def column, value
            
            // core stuff
            String action = par["POST"]["do"] //show, update, etc
            String lang = par["POST"]["lg"] 
            
            ServerMessage sm = new ServerMessage("AdminUserMapping", lang, bind, session, processlog)  
            
            // pager stuff
            if (par["POST"]["l"]) limit = Integer.parseInt(par["POST"]["l"])
				if (!limit) limit = 0
            if (par["POST"]["o"]) offset = Long.parseLong(par["POST"]["o"])
				if (!offset) offset = 0
            if (par["POST"]["c"]) column = par["POST"]["c"]
            if (par["POST"]["v"]) value = par["POST"]["v"]
            
           	String api_key = par["POST"]["api_key"] 
            if (!api_key) api_key = par["COOKIE"]["api_key"]   
            if (!api_key) return sm.noAPIKeyMessage()

            User user = userTable.getFromAPIKey(api_key)           
            if (!user) return sm.userNotFound()
            if (!user.isEnabled()) return sm.userNotEnabled()
				// all Admin*Mappings must have this
				if (!user.isSuperUser()) return sm.noSuperUser()
            if (!action || !lang) return sm.notEnoughVars("do=$action, lg=$lang")        	
            sm.setAction(action)
             
			/*****************/
			/** AUTH */
			/******************/	
			if (action == 'auth') {
			    
	    	    String password = par["POST"]["p"]
				 api_key = par["POST"]["api_key"] 
             if (!api_key) api_key = par["COOKIE"]["api_key"]   
             if (!api_key) return sm.noAPIKeyMessage()

            user_db = userTable.getFromAPIKey(api_key)           
            if (!user_db) return sm.userNotFound()
            if (!user_db.isEnabled()) return sm.userNotEnabled()

	    	   if (user_db.usr_password != password) 
					return sm.statusMessage(-1, i18n.servermessage['wrong_password'][lang])
	    				   	 
	    	   return sm.statusMessage(0, 'OK')
	    	} 
	
	          
            /***************************/
            /** 1.1 show - PAGE USERS **/
            /***************************/
            
            if (action == "list") {
                Map h 
                try {                
                    h = userTable.listUsersForAdminUser(limit, offset, column, value)
                } catch(Exception e) {
                    errorlog.error i18n.servermessage['error_getting_user_list'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_user_list"][lang]+": "+e.getMessage())
                }
                
                h.result.eachWithIndex{u, i -> h.result[i] = u.toMap()}                
                return sm.statusMessageWithPubKey(0,h, user.usr_pub_key)		   
            }
            
            /******************************/
            /** 1.2 update - MODIFY USER **/
            /******************************/
            
            if (action == "update") {
                //requires the column and value parameters
        			long id 
               try {id = Long.parseLong(par["POST"]["id"])}
					catch(Exception e) {}
               if (!id) return sm.notEnoughVars("id=$id")     
               if (value == null || !column)  return sm.notEnoughVars("v=$value, c=$column")
               
					User user_db = User.getFromID(id)
					if (!user_db) return sm.userNotFound()
				
					int res = 0
               try {
             		res = user_db.updateValue(id, column, value)
               } catch(Exception e) {    
                    errorlog.error i18n.servermessage['error_updating_user'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage['error_updating_user'][lang]+": " +e.getMessage())                  
               }                
                //RETURNS 1 IF UPDATED
                return sm.statusMessageWithPubKey(res, user_db.toMap(), user.usr_pub_key)		               
            }
            
	
			/***************/
	    	/** 1.25 show **/
			/***************/	
	    	if (action == 'show') {
			    
	    	    // requires api_key now
				Long ui 
				try {ui = Long.parseLong(par["POST"]["ui"]) }
				catch(Exception e) {}
            if (!ui) return sm.notEnoughVars("ui=$ui")
           
            User user_db = User.getFromID(ui)           
            if (!user_db) return sm.userNotFound()
            
				Map m = user_db.toMap()
				m.current_number_collections_owned = Collection.collectionsOwnedBy(user_db)
	    	   return sm.statusMessageWithPubKey(0,m, user.usr_pub_key)		   
	    	} 
	    	
           
				/***************/
            /** 1.3 create */ 
            /***************/
            
            if (action == "create") {
           		String usr_login = par["POST"]["usr_login"] 
					Boolean usr_enabled
					try {usr_enabled = Boolean.parseBoolean(par["POST"]["usr_enabled"])}
					catch(Exception e) {}
					String usr_groups = par["POST"]["usr_groups"] 
					Boolean usr_superuser 
					try {usr_superuser = Boolean.parseBoolean(par["POST"]["usr_superuser"])}
					catch(Exception e) {}
					String usr_firstname = par["POST"]["usr_firstname"] 
					String usr_lastname = par["POST"]["usr_lastname"] 
					String usr_email = par["POST"]["usr_email"] 
					String usr_password = par["POST"]["usr_password"] 
					String usr_tmp_password  = par["POST"]["usr_tmp_password"] 
					String usr_api_key = par["POST"]["usr_api_key"] 
					String usr_tmp_api_key = par["POST"]["usr_tmp_api_key"] 
					Integer usr_max_number_collections
					try {usr_max_number_collections = Integer.parseInt(par["POST"]["usr_max_number_collections"])}
					catch(Exception e) {}
					Integer usr_max_docs_per_collection
					try {usr_max_docs_per_collection = Integer.parseInt(par["POST"]["usr_max_docs_per_collection"])}
					catch(Exception e) {}
					Integer usr_max_daily_api_calls
					try {usr_max_daily_api_calls = Integer.parseInt(par["POST"]["usr_max_daily_api_calls"])}
					catch(Exception e) {}
					Integer usr_current_daily_api_calls
					try {usr_current_daily_api_calls = Integer.parseInt(par["POST"]["usr_current_daily_api_calls"])}
					catch(Exception e) {}
   				Long usr_total_api_calls
					try {usr_total_api_calls = Long.parseLong(par["POST"]["usr_total_api_calls"])}
					catch(Exception e) {}
   				Date usr_date_last_api_call = new Date()
					
					User user_db = User.getFromLogin(usr_login) 
					if (user_db) return sm.statusMessage(-1, i18n.servermessage['user_already_exists'][lang])
 					
					try {
                     user_db = new User(usr_login:usr_login, usr_enabled:usr_enabled, usr_groups:usr_groups,
							usr_superuser:usr_superuser, usr_firstname:usr_firstname, usr_lastname:usr_lastname, 
							usr_email:usr_email, usr_password:usr_password, usr_tmp_password:usr_tmp_password, 
							usr_api_key:usr_api_key, usr_tmp_api_key:usr_tmp_api_key,
							usr_max_number_collections:usr_max_number_collections,
							usr_max_docs_per_collection:usr_max_docs_per_collection, 
							usr_max_daily_api_calls:usr_max_daily_api_calls,
							usr_current_daily_api_calls:usr_current_daily_api_calls, 
							usr_total_api_calls:usr_total_api_calls, usr_date_last_api_call:usr_date_last_api_call)
							
							
                     user_db.usr_id = user_db.addThisToDB()
                } catch(Exception e) {
                    errorlog.error i18n.servermessage['error_creating_user'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage['error_creating_user'][lang]+": "+e.getMessage())
                }
                        
                // leave like that, to include an id field
                return sm.statusMessageWithPubKey(0, user_db.toMap(), user.usr_pub_key)		   	
            }

         
            /*****************************************/
            /** 1.4 delete - DELETE USER for (user) **/
            /*****************************************/
           
            if (action == "delete") {
                long usr_id 
                try {usr_id=  Long.parseLong(par["POST"]["ui"]) }                     
                catch(Exception e) {}                              
                if (!usr_id)  return sm.notEnoughVars("ui=$usr_id")      
                
					 User user_db = User.getFromID(id)
					 if (!user_db) return sm.userNotFound()
                
					 int res = 0 
                try {
                    res = user_db.removeThisFromDB() 
                } catch(Exception e) {
                  errorlog.error i18n.servermessage['error_deleting_user'][lang]+": "+e.printStackTrace()
						return sm.statusMessage(-1, i18n.servermessage["error_deleting_user"][lang]+": "+e.getMessage())
					}
                  
                //RETURNS 1 IF UPDATED
                return sm.statusMessageWithPubKey(res, i18n.servermessage['ok'][lang], user.usr_pub_key)		   		   	
            }
            return sm.unknownAction(action)	
        }
    }
}
