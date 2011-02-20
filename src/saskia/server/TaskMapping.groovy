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

import saskia.io.*
import saskia.util.I18n
import org.apache.log4j.*

public class TaskMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n
    static Logger mainlog = Logger.getLogger("SaskiaServerMain")  
    static Logger errorlog = Logger.getLogger("SaskiaServerErrors")  
    static Logger processlog = Logger.getLogger("SaskiaServerProcessing")  
 
    public TaskMapping() {
        
        i18n = I18n.newInstance()
        
        JSONanswer = {req, par, bind ->
            long session = System.currentTimeMillis()
            log2.debug "Session $session triggered with $par" 
            
            int limit, offset
            def column, value
            
            // core stuff
            String action = par["POST"]["do"] //show, update, etc
            String lang = par["POST"]["lg"] 
            
            ServerMessage sm = new ServerMessage("TaskMapping", lang, bind, session)  
            
            // pager stuff
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
            if (!action || !lang) return sm.notEnoughVars("do=$action, lg=$lang")        	
            sm.setAction(action)          
           
            /*************/
            /** 1.1 list */
            /*************/
            
            if (action == "list") {
					Map h 
               try {
                  h = Task.listTasksForUser(user, limit, offset, column, value)
               } catch(Exception e) {
               	errorlog.error i18n.servermessage['error_getting_task_list'][lang]+": "+e.printStackTrace()
     					return sm.statusMessage(-1, i18n.servermessage['error_getting_task_list'][lang]+": "+e.getMessage(), action)
               }
                List res = []
                h.result.eachWithIndex{tsk, i ->  h.result[i] = tsk.toMap()}              
                return sm.statusMessage(0, h)        
            }
   
            /***************/
            /** 1.2 update */
            /***************/
            
            if (action == "update") {
        	
        		//requires the column and value parameter
        		// the id is from a collection
            	long id 
					if (par["POST"]["id"]) 
					try {id = Long.parseLong(par["POST"]["id"] )}
					catch(Exception e) {}
					if (!id) return sm.notEnoughVars("id=$id")
                
                // let's check permissions
               Task tsk = Task.getFromID(id)						
					if (!tsk) return sm.statusMessage(-1, i18n.servermessage['task_not_found'][lang])
					if (!tsk.tsk_user.equals(user)) return sm.insufficientPermissions("user:$user")
					if (value == null || !column) return sm.notEnoughVars("v=$value, c=$column")
                  
      			int res = 0
               try {
                    res = tsk.updateValue(column, value)                     
                } catch(Exception e) {
                  errorlog.error (i18n.servermessage['error_updating_task'][lang]+": "+e.printStackTrace())
  						return sm.statusMessage(-1, i18n.servermessage['error_updating_task'][lang]+": " +e.getMessage())
                }
                
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(res, tsk.toMap())	// the value will be used on the UI	
            }            
            
            /***************/
            /** 1.3 create */ 
            /***************/
            
            if (action == "create") {
           
					User tsk_user
					Collection tsk_collection
					try {tsk_user = User.getFromID(Long.parseLong(par["POST"]["tsk_user"]))
					}catch(Exception e) {}
					try {tsk_collection = Collection.getFromID(Long.parseLong(par["POST"]["tsk_collection"]))
					}catch(Exception e) {}
					String tsk_type = par["POST"]["tsk_type"]
					Integer tsk_priority
					try {tsk_priority = Integer.parseInt(par["POST"]["tsk_priority"])}
					catch(Exception e) {}
					Integer tsk_limit
					try {tsk_limit = Integer.parseInt(par["POST"]["tsk_limit"])}
					catch(Exception e) {}
					Long tsk_offset 
					try {tsk_offset = Long.parseLong(par["POST"]["tsk_offset"])}
					catch(Exception e) {}
					Integer tsk_done 
					try {tsk_done =  Integer.parseInt(par["POST"]["tsk_done"])}
					catch(Exception e) {}
					String tsk_scope = par["POST"]["tsk_scope"] 
					String tsk_persistence = par["POST"]["tsk_persistence"]
					String tsk_status = par["POST"]["tsk_status"]
					String tsk_comment = par["POST"]["tsk_comment"]
	
		//			 List<Task> tsks = Task.getFrom??(??) 
		//			 if (tsks) return sm.statusMessage(-1, i18n.servermessage['task_already_exists'][lang])
               int number_tasks = task.getNumberOfTasksForUser(user)
					 if (user.usr_max_number_tasks < number_tasks) 
					    return sm.statusMessage(-1, i18n.servermessage['max_number_tasks_reached'][lang])
					  						
                Task tsk
 					 try {
                     tsk = new Task(tsk_user:tsk_user, tsk_collection:tsk_collection, tsk_type:tsk_type,
							tsk_priority:tsk_priority, tsk_limit:tsk_limit, tsk_offset:tsk_offset, tsk_done:tsk_done,
							tsk_scope:tsk_scope, tsk_persistence:tsk_persistence, tsk_status:tsk_status, tsk_comment:tsk_comment)
					
                     tsk.tsk_id = tsk.addThisToDB()
                } catch(Exception e) {
                    errorlog.error i18n.servermessage['error_creating_task'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage['error_creating_task'][lang]+": "+e.getMessage())
                }
                        
                // leave like that, to include an id field
                return sm.statusMessage(0, tsk.toMap())	
            }

				/****************/
            /** 1.4 delete **/
            /****************/
           
            if (action == "delete") {
                	long id 
					if (par["POST"]["id"]) 
					try {id = Long.parseLong(par["POST"]["id"] )}
					catch(Exception e) {}
					if (!id) return sm.notEnoughVars("id=$id")
               
               Task tsk = Task.getFromID(id)
					if (!tsk) return sm.statusMessage(-1, i18n.servermessage['task_not_found'][lang])
 					if (!tsk.tsk_user.equals(user)) return sm.insufficientPermissions("user:$user")
                
					def res
					try {
                    res = tsk.removeThisFromDB() 
                } catch(Exception e) {
	              	  errorlog.error i18n.servermessage['error_deleting_task'][lang]+": "+e.printStackTrace()
						  return sm.statusMessage(-1, i18n.servermessage["error_deleting_task"][lang]+": "+e.getMessage())
				    }                
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(res, i18n.servermessage['ok'][lang])		   	
            }    
            
            return sm.unknownAction()
        }
    }
}

