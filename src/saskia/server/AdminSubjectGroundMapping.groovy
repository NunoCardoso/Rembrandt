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

public class AdminSubjectGroundMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n
    static Logger mainlog = Logger.getLogger("SaskiaServerMain")  
    static Logger errorlog = Logger.getLogger("SaskiaServerErrors")  
    static Logger processlog = Logger.getLogger("SaskiaServerProcessing")  
 
    public AdminSubjectGroundMapping() {
        
        i18n = I18n.newInstance()
        
        JSONanswer = {req, par, bind ->
            long session = System.currentTimeMillis()
            processlog.debug "Session $session triggered with $par" 
            
            int limit
				long offset
            def column, value
            
            // core stuff
            String action = par["POST"]["do"] //show, update, etc
            String lang = par["POST"]["lg"] 
            
            ServerMessage sm = new ServerMessage("AdminSubjectGroundMapping", lang, bind, session, processlog)  
            
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

            User user = User.getFromAPIKey(api_key)           
            if (!user) return sm.userNotFound()
            if (!user.isEnabled()) return sm.userNotEnabled()
				// all Admin*Mappings must have this
				if (!user.isSuperUser()) return sm.noSuperUser()
            if (!action || !lang) return sm.notEnoughVars("do=$action, lg=$lang")        	
            sm.setAction(action)
           
             /************/
            /** 1.1 show */
            /*************/
            
            if (action == "list") {
					Map h 
               try {
                  h = SubjectGround.listSubjectGrounds(limit, offset, column, value)
               } catch(Exception e) {
               	errorlog.error i18n.servermessage['error_getting_subjectground_list'][lang]+": "+e.printStackTrace()
     					return sm.statusMessage(-1, i18n.servermessage['error_getting_subjectground_list'][lang]+": "+e.getMessage(), action)
               }
                List res = []
                h.result.eachWithIndex{sgr, i ->  h.result[i] = sgr.toMap()}              
                return sm.statusMessageWithPubKey(0, h, user.usr_pub_key)	       
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
               SubjectGround sg = SubjectGround.getFromID(id)
					if (!sg) return sm.statusMessage(-1, i18n.servermessage['subjectground_not_found'][lang])
					if (value == null || !column) return sm.notEnoughVars("v=$value, c=$column")
                
					 int res = 0        
               try {
                    res = sg.updateValue(column, value)                     
                } catch(Exception e) {
                  errorlog.error (i18n.servermessage['error_updating_subjectground'][lang]+": "+e.printStackTrace())
  						return sm.statusMessage(-1, i18n.servermessage['error_updating_subjectground'][lang]+": " +e.getMessage())
                }
                
                //RETURNS 1 IF UPDATED
                return sm.statusMessageWithPubKey(res, sg.toMap(), user.usr_pub_key)	
            }            
            
            /***************/
            /** 1.3 create */ 
            /***************/
            
            if (action == "create") {

                Long sgr_subject
                Long sgr_geoscope 
					 try {sgr_subject = Long.parseLong(par["POST"]["sgr_subject"] )}
					 catch(Exception e) {}
					 try {sgr_geoscope = Long.parseLong(parpar["POST"]["sgr_geoscope"])}
					 catch(Exception e) {}
                String sgr_dbpedia_resource =  par["POST"]["sgr_dbpedia_resource"] 
                String sgr_dbpedia_class =  par["POST"]["sgr_dbpedia_class"] 
                String sgr_wikipedia_category =  par["POST"]["sgr_wikipedia_category"] 
                String sgr_comment =  par["POST"]["sgr_comment"] 
                 
					 List<SubjectGround> sgrs = SubjectGround.getFromSubjectIDAndGeoscopeID(sgr_subject, sgr_geoscope) 
					 if (collection) return sm.statusMessage(-1, i18n.servermessage['subjectground_already_exists'][lang])
               
                SubjectGround sgr
 					 try {
                     sgr = new SubjectGround(sgr_subject:sgr_subject, sgr_geoscope:sgr_geoscope,
					  	   sgr_dbpedia_resource:sgr_dbpedia_resource, sgr_dbpedia_class:sgr_dbpedia_class, 
					      sgr_wikipedia_category:sgr_wikipedia_category, sgr_comment:sgr_comment)
					
                     sgr.sgr_id = sgr.addThisToDB()
                } catch(Exception e) {
                    errorlog.error i18n.servermessage['error_creating_subjectground'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage['error_creating_subjectground'][lang]+": "+e.getMessage())
                }
                        
                // leave like that, to include an id field
                return sm.statusMessageWithPubKey(0, sgr.toMap(), user.usr_pub_key)		
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
                
                // let's check permissions
                SubjectGround sg = SubjectGround.getFromID(id)
					if (!sg) return sm.statusMessage(-1, i18n.servermessage['subjectground_not_found'][lang])
                 
					def res
					try {
                    res = sg.removeThisFromDB() 
                } catch(Exception e) {
	              	  errorlog.error i18n.servermessage['error_deleting_subjectground'][lang]+": "+e.printStackTrace()
						  return sm.statusMessage(-1, i18n.servermessage["error_deleting_subjectground"][lang]+": "+e.getMessage())
				    }                
                //RETURNS 1 IF UPDATED
                return sm.statusMessageWithPubKey(res, i18n.servermessage['ok'][lang], user.usr_pub_key)		   	
            }    
                       
            return sm.unknownAction(action)	
            
        }
    }
}