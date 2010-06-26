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
import org.apache.log4j.*
import renoir.util.SHA1 //SHA1.convert(passwd)
import renoir.util.MD5Hex 
import saskia.util.I18n

public class UserMapping extends WebServiceRestletMapping {

	Closure JSONanswer
	Closure HTMLanswer
	I18n i18n
	static Logger log = Logger.getLogger("SaskiaServer") 
	static Logger log2 = Logger.getLogger("SaskiaService") 
	    
	public UserMapping() {
	
	    User user_db
	    i18n = I18n.newInstance()
        
	    JSONanswer = {req, par, bind ->
                long session = System.currentTimeMillis()
                log2.debug "Session $session triggered with $par" 
 
	    // actions: login, register, getcolperm. forgotpassword, changepassword, confirmpassword, confirmregister
	    
	    	def action = par["POST"]["do"]
	    	def lang = par["POST"]["lg"]
	    	
	    	/******** LOGIN *********/
	
	    	if (action == 'login') {
			    
	    	    String user = par["POST"]["u"]
	    	    String password = par["POST"]["p"]
	    	    user_db = User.getFromLogin(user)
	    	    log.info "User: $user_db"
		    // no user found
	    	    if (!user_db) {
	    		bind['status'] = -1
	    		bind['message'] = i18n.servermessage['user_not_found'][lang]
	    		log2.info "$session $session UserMapping:$action: $bind for user $user_db"
	    		return JSONHelper.toJSON(bind)
		    } 
				
	    	    if (user_db.usr_password != password) {
	    		bind['status'] = -1
	    		bind['message'] = i18n.servermessage['wrong_password'][lang]
	    		log2.info "$session UserMapping:$action: $bind for user $user_db"
	    		return JSONHelper.toJSON(bind)
	    	    }
				
	    	    if (!user_db.usr_enabled) {
	    		bind['status'] = -1
	    		bind['message'] = i18n.servermessage['user_not_confirmed'][lang]
	    		log2.info "UserMapping:$action: Returning $bind for user $user_db"
	    		return JSONHelper.toJSON(bind)
	    	    }
				
	    	    bind['status'] = 0
	    	    bind['su'] = user_db.usr_superuser
                    bind['user_id'] = user_db.usr_id
                    bind['firstname'] = user_db.usr_firstname
	    	    bind['lastname'] = user_db.usr_lastname
	    	    bind['api_key'] = user_db.usr_api_key
                
	    	    Map message = [:]
                    List<HashMap> perms = user_db.getUserCollectionPermissionsOn() // blank means all
                    int collections_owned = user_db.collectionsOwned()
                    if (!perms) {
                         bind['message'] = i18n.servermessage['no_permissions_found'][lang]
                    } else {
                        message["max_number_collections_owned"] = user_db.usr_max_number_collections
                        message["collections_owned"] = collections_owned
                        message["perms"] = perms                           
                    } 
                    
                    bind['message'] = message
	    	    log2.info "$session UserMapping:$action: Returning $bind for user $user_db"
	    	    return JSONHelper.toJSON(bind)
	    	} 
			
	    	/*********** REGISTER **************/

	    	if (action == "register") {
	    	    
	    	    def user = par["POST"]["u"]
	    	    def password = par["POST"]["p"]
	    	    def firstname = par["POST"]["fn"]
	    	    def lastname = par["POST"]["ln"]
		    def email = par["POST"]["em"]	
		    user_db = User.getFromLogin(user)
				
		   if (user_db) {
			bind['status'] = -1
			bind['message'] = i18n.servermessage['user_login_already_taken'][lang]
			log2.info "$session UserMapping:$action: Returning $bind for user $user_db"
			return JSONHelper.toJSON(bind)					
		    } 
				
	    	    User newuser = new User(usr_login:user, usr_password:password, 
	    		    usr_firstname:firstname, usr_lastname:lastname, usr_email:email,
	    		    usr_api_key:SHA1.convert(user+firstname), usr_enabled:0,
                            usr_max_number_collections:1, usr_max_docs_per_collection:100) // user is NOT enabled... that's for the confirmation
			    newuser.usr_id = newuser.addThisToDB()
				    
			// add new user_on_collections permissions.
	            List collectionsAllowed = newuser.setNewPermissionsOnCollection()
	            if (newuser.usr_id) {
	        //	def message = "User added. New collections you can see: <BR><UL>";
	        //	collectionsAllowed.each{col -> message += "<LI>${col}</LI>"}
	        //	message += "</UL>"
	        	bind['status'] = 0
	        	bind['api_key'] = newuser.usr_api_key
	        	log2.info "$session UserMapping:$action: Returning $bind for user $user_db"    
	        	return JSONHelper.toJSON(bind)			
	            }
	    	}
			
	    	/********** GET COLLECTION PERMISSIONS ***********/

	    	if (action == "getcolperm") {
                
	    	    String user = par["POST"]["u"]             
	    	    if (User.isGuestUser(user, lang)) {
 	    		user_db = User.getFromLogin(User.guest)
	    	    } else {
	    		String api_key = par["POST"]["api_key"]
	    		if (api_key){ 
	    		     user_db = User.getFromAPIKey(api_key)
	    		}
	    	    }
	    	    if (!user_db) {
	    		bind['status'] = -1
	    		bind['message'] = i18n.servermessage['invalid_api_key'][lang]
	    		log2.info "$session UserMapping:$action: Returning $bind for user $user_db"               
	    		return JSONHelper.toJSON(bind)
	    	    }
	    		
	    	    if (!user_db.usr_enabled) {
	    		bind['status'] = -1
	    		bind['message'] = i18n.servermessage['user_not_confirmed'][lang]
	    		log2.info "$session UserMapping:$action: Returning $bind for user $user_db"    
	    		return JSONHelper.toJSON(bind)
	    	    }
                
                // now, if we're a guest, we do a different thing. 
                if (User.isGuestUser(user, lang)) {
                                 
                     List<HashMap> collections = user_db.getReadableCollections() 
                    // println "collections: $collections" 
                     if (collections) {
                	 bind['status'] = 1
                	 bind['message'] = collections
                     } else {
                	 bind['status'] = -1
                	 bind['message'] = i18n.servermessage['user_not_allowed_to_query_perms'][lang]
                     } 
                     log2.info "$session UserMapping:$action: Returning $bind for user $user_db"    
                     return JSONHelper.toJSON(bind)
                    
                } else if (user_db.isSuperUser()) {
                    
                    List<HashMap> collections = user_db.getAllCollectionsForSuperUser() 
                   // println "collections: $collections" 
                    if (collections) {
               	 	bind['status'] = 1
               	 	bind['message'] = collections
                    } else {
               	 	bind['status'] = -1
               	 	bind['message'] = i18n.servermessage['no_collections_found'][lang]
                    } 
                    log2.info "$session UserMapping:$action: Returning $bind for user $user_db"    
                    return JSONHelper.toJSON(bind)
                   
               } else {
                    Map message = [:]
	    	    List<HashMap> perms = user_db.getUserCollectionPermissionsOn() // blank means all
	    	    int collections_owned = user_db.collectionsOwned()
	    	    if (!perms) {
	    		bind['status'] = -1
	    		bind['message'] = i18n.servermessage['no_permissions_found'][lang]
 	    	    } else {
 	    		message["max_number_collections_owned"] = user_db.usr_max_number_collections
 	    		message["collections_owned"] = collections_owned
 	    		message["perms"] = perms
              		bind['status'] = 0
	    		bind['message'] = message
                          
	    	    } 
	    	    log2.info "$session UserMapping:$action: Returning $bind for user $user_db"  
	    	    return JSONHelper.toJSON(bind)

                }
                }
            
	    	/*********** RECOVER PASSWORD *********/
			
	    	if (action == "recoverpassword") {
	    	    def email = par["POST"]["em"]
	    	    user_db = User.getFromEmail(email)
	    	    if (!user_db) {
	    		bind['status'] = -1
	    		bind['message'] = i18n.servermessage['email_not_found'][lang]
	    		log2.info "$session UserMapping:$action: Returning $bind for user $user_db"  
	    		return JSONHelper.toJSON(bind)						    
	    	    } 
	    	    // to randomly generate it, use SHA1
	    	    def tmp_password = (SHA1.convert(""+new Date().getTime())).substring(0,8)
	    	    def tmp_api_key = SHA1.convert(tmp_password+email)

	    	    // note that to encode password, use MD5, because Javascript can do it too
	    	    user_db.insertTempPassword(MD5Hex.digest(tmp_password))
	    	    user_db.insertTempAPIKey(tmp_api_key)
	    	    bind['status'] = 0
	    	    bind['newpassword'] = tmp_password
	    	    bind['tmp_api_key'] = tmp_api_key
	    	    log2.info "$session UserMapping:$action: Returning $bind for user $user_db"  
	    	    return JSONHelper.toJSON(bind)					     
	    	}
		
	    	/*********** CHANGE PASSWORD *********/
	    	
	    	if (action == "changepassword") {
	    	    def user_login  = par["POST"]["u"]
	    	    def oldpassword  = par["POST"]["op"]
	    	    def newpassword  = par["POST"]["np"]
		   user_db = User.getFromLogin(user_login)
		   if (!user_db) {
		       bind['status'] = -1
		       bind['message'] = i18n.servermessage['user_not_found'][lang]
		       log2.info "$session UserMapping:$action: Returning $bind for user_login $user_login"  
		       return JSONHelper.toJSON(bind)					     				    
		   } 
	    	    if (user_db.usr_password != oldpassword) {
	    		bind['status'] = -1
	    		bind['message'] = i18n.servermessage['old_password_dont_match'][lang]
                        log2.info "$session UserMapping:$action: Returning $bind for user_login $user_login"  
	    	    } else {
	    		user_db.updatePassword(newpassword)
	    		bind['status'] = 0
	    		bind['message'] =i18n.servermessage['password_changed'][lang]	 
	    		log2.info "$session UserMapping:$action: Returning $bind for user_login $user_login"  
	    	    }    
	    	    return JSONHelper.toJSON(bind)					     
		}
                bind['status']=-1
                bind['message'] = i18n.servermessage['action_unknown'][lang]
                log2.debug "$session UserMapping: $bind  action $action unknown"
                return JSONHelper.toJSON(bind)	
	    }
			
	  
	    HTMLanswer = {req, par, bind ->
              long session = System.currentTimeMillis()
              log2.debug "Session $session triggered with $par" 
            
	      def action, tmp_api_key, api_key, message 
	      def lang = "en" 
	      
	      action = par["GET"]["do"]
	      if (par["GET"]["lg"]) lang = par["GET"]["lg"]
	
	    /*********** CONFIRM PASSWORD *********/
	     if (action == "confirmpassword") {
		 tmp_api_key  = par["GET"]["tmp_api_key"]
		 user_db = User.getFromTempAPIKey(tmp_api_key)
			     
		 if (!user_db) {
		     message = i18n.servermessage['user_not_found'][lang]
		     log2.info "$session UserMapping:$action: Returning $message"  
		 } else {
		     user_db.updatePasswordAndAPIKeyFromTemp()				 
		     message = i18n.servermessage['password_and_api_key_changed'][lang]	 
		     log2.info "$session UserMapping:$action: Returning $message"  
		 }
		 return message
	     }  else if (action == "confirmregister") {
		 api_key  = par["GET"]["a"]
		 user_db = User.getFromAPIKey(api_key)
			     
		 if (!user_db) {
		     message = i18n.servermessage['user_not_found'][lang]
		 } else {
		     user_db.enableUser()				 
		     message = i18n.servermessage['user_enabled'][lang]
		 }
                 log2.info "$session UserMapping:$action: Returning $message for user $user_db" 
		 return message
	     }
	      
	        // for HTML requests that I do not want to handle: 
	        return "No HTML, please!"
	    }
	}
}
