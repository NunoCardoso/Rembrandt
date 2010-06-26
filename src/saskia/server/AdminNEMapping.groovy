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

import saskia.io.Entity
import saskia.io.NE
import saskia.io.User
import saskia.io.NECategory
import saskia.io.NEName
import saskia.io.NESubtype
import saskia.io.NEType
import org.apache.log4j.*
import rembrandt.obj.Sentence
import rembrandt.gazetteers.SemanticClassificationDefinitions
import saskia.util.I18n

public class AdminNEMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n
    static Logger log = Logger.getLogger("SaskiaServer") 
    static Logger log2 = Logger.getLogger("SaskiaService") 
 
    public AdminNEMapping() {
        
        i18n = I18n.newInstance()
        
        JSONanswer = {req, par, bind ->
            long session = System.currentTimeMillis()
            log2.debug "Session $session triggered with $par" 
            
            int limit, offset
            def column, value
            
            // core stuff
            String action = par["POST"]["do"] //show, update, etc
            String lang = par["POST"]["lg"] 
            
            ServerMessage sm = new ServerMessage("AdminNEMapping", lang, bind, session)  
            
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
            
            if (!action || !lang) return sm.notEnoughVars(lang, "do=$action, lg=$lang")      
        	
            sm.setAction(action)
           
            /***************************/
            /** 1.1 show - PAGE NEs **/
            /***************************/

            if (action == "show") {
        	Map h
                try {
                    log.debug "Querying NEs: limit $limit offset $offset column $column value $value"
                    h = NE.getNEs(limit, offset, column, value)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_ne_list"][lang]+": "+e.getMessage())
                }
                
                // you have to "JSONize" the NEs
                h.result.eachWithIndex{ne, i -> h.result[i] = ne.toMap() }

                return sm.statusMessage(0,h)
            }
            
            /***************************/
            /** 1.2 update a NE value **/
            /***************************/
            
            if (action == "update") {
        	
        	Long ne_id
                if (par["POST"]["id"]) ne_id = Long.parseLong(par["POST"]["id"])
                if (!ne_id) return sm.notEnoughVars("id=$ne_id")
                
                NE ne 
                try {
                    log.debug "Querying NE ne with id $ne_id"
                    ne = NE.getFromID(ne_id)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_ne_list"][lang]+": "+e.getMessage())                 
                }
                
                switch(column) {
                    case "ne_name": 
                	NEName ne_name
                	def res 
                        try {
                           ne_name = NEName.getFromID(Long.parseLong(value))
                           res = ne.updateNEName(ne_name)
                        }catch(Exception e)  {
                    	return sm.statusMessage(-1, i18n.servermessage["error_updating_ne"][lang]+": "+e.getMessage())                 
                        }
                        return sm.statusMessage(0, ne_name.toMap())
                    break      
                    case "ne_category": 
                	NECategory ne_category
                	def res 
                        try {
                           ne_category = NECategory.getFromID(Long.parseLong(value))
                           res = ne.updateNECategory(ne_category)
                        }catch(Exception e)  {
                    	return sm.statusMessage(-1, i18n.servermessage["error_updating_ne"][lang]+": "+e.getMessage())                 
                        }
                        return sm.statusMessage(0, ne_category.toMap())
                    break   
                    case "ne_type": 
                	NEType ne_type
                	def res 
                        try {
                           ne_type = NEType.getFromID(Long.parseLong(value))
                           res = ne.updateNEType(ne_type)
                        }catch(Exception e)  {
                    	return sm.statusMessage(-1, i18n.servermessage["error_updating_ne"][lang]+": "+e.getMessage())                 
                        }
                        return sm.statusMessage(0, ne_type.toMap())
                    break   
                    case "ne_category": 
                	NESubtype ne_subtype
                	def res 
                        try {
                           ne_subtype = NESubtype.getFromID(Long.parseLong(value))
                           res = ne.updateNESubtype(ne_subtype)
                        }catch(Exception e)  {
                    	return sm.statusMessage(-1, i18n.servermessage["error_updating_ne"][lang]+": "+e.getMessage())                 
                        }
                        return sm.statusMessage(0, ne_subtype.toMap())
                    break   
                    case "ne_entity": 
                	Entity ne_entity
                	def res 
                        try {
                           ne_entity = Entity.getFromID(Long.parseLong(value))
                           res = ne.updateEntity(ne_entity)
                        }catch(Exception e)  {
                    	return sm.statusMessage(-1, i18n.servermessage["error_updating_ne"][lang]+": "+e.getMessage())                 
                        }
                        return sm.statusMessage(0, ne_entity.toMap())
                    break   
                }
                
                
            } 
                
            /***************************************/
            /** 1.3 delete - DELETE NE (admin only) **/
            /***************************************/
           
            if (action == "delete") {
                long ne_id 
                try {
                    ne_id = Long.parseLong(par["POST"]["id"])                      
                }catch(Exception e) {}
                               
                if (!ne_id)  return sm.notEnoughVars("id=$ne_id")      
                NE ne 
                try {
                    log.debug "Querying NE ne with id $ne_id"
                    ne = NE.getFromID(ne_id)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_ne_list"][lang]+": "+e.getMessage())                 
                }
                          
                try {
                    log.debug "deleting ne $ne_id"
                    answer = NE.deleteNE(ne_id) 
                } catch(Exception e) {return sm.statusMessage(-1, i18n.servermessage["error_deleting_ne"][lang]+": "+e.getMessage())}
                  
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(answer, i18n.servermessage['ok'][lang])		   	
            }    
            
            /***************************************/
            /** 1.4 create **/
            /***************************************/
           
            if (action == "create") {
                  
                // I get ne_name, c1, c2, c3
                def ne_name, c1, c2, c3, ne_lang, ent
                def c1v, c2v, c3v // grounded, or null
                
                if (par["POST"]["ne_name"]) ne_name = par["POST"]["ne_name"]
                if (par["POST"]["ne_lang"]) ne_lang = par["POST"]["ne_lang"]                                                                                                             
                if (par["POST"]["c1"]) c1 = par["POST"]["c1"]
                if (par["POST"]["c2"]) c2 = par["POST"]["c2"]   
                if (par["POST"]["c3"]) c3 = par["POST"]["c3"]
                if (par["POST"]["ent"]) ent = par["POST"]["ent"]
                                                                                                
                // let's decontaminate the vars.
               NEName db_ne_name 
               try {db_ne_name = NEName.getFromName(ne_name)
               if (!db_ne_name) {
        	   Sentence s = Sentence.simpleTokenize(ne_name) 
        	   db_ne_name = new NEName(nen_name:ne_name, nen_nr_terms:s.size())
        	   db_ne_name.addThisToDB()
               }
               }catch(Exception e) {
        	   return sm.statusMessage(-1, i18n.servermessage["error_creating_ne"][lang]+": "+e.getMessage())                 
               }
               
               assert ["en","pt"].contains(ne_lang)
               
               SemanticClassificationDefinitions scd = Class.forName("rembrandt.gazetteers."+lang+".SecondHAREMClassificationLabels"+(
               ne_lang.toUpperCase())).newInstance()
             	
               c1v = ( (c1 == null || c1 == "null") ? null : scd.label.findAll{it.value == c1}.collect{it.key} )
               if (c1v) c1v = NECategory.getFromCategory(c1v)
               c2v = ( (c2 == null || c2 == "null") ? null : scd.label.findAll{it.value == c2}.collect{it.key} )
               if (c2v) c2v = NEType.getFromType(c2v)
               c3v = ( (c3 == null || c3 == "null") ? null : scd.label.findAll{it.value == c3}.collect{it.key} )
               if (c3v) c3v = NESubtype.getFromSubtype(c3v)
         
               // let's see if there is already on DB
               Entity entity = Entity.getFromID(ent)
               
               NE ne2 = NE.getFromNameAndLangAndClassificationAndEntity(db_ne_name, ne_lang, c1v, c2v, c3v, entity) 
        	   
               if (ne2) return sm.statusMessage(-1, i18n.serverMessage['ne_already_exists'][lang])
               
               NE ne = new NE(ne_name:db_ne_name, ne_category:c1v, ne_type:c2v, ne_subtype:c3v, ne_entity:entity)
               try {
                    log.debug "Adding NE"
                    ne = NE.addThisToDB()
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_creating_ne"][lang]+": "+e.getMessage())                 
                }
               
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(0, ne.toMap())		   	
            }    
            return sm.unknownAction()
        }
    }
}