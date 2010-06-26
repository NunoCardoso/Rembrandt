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

public class AdminGeoscopeMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n
    static Logger log = Logger.getLogger("SaskiaServer") 
    static Logger log2 = Logger.getLogger("SaskiaService") 
 
    public AdminGeoscopeMapping() {
        
        i18n = I18n.newInstance()
        
        JSONanswer = {req, par, bind ->
            long session = System.currentTimeMillis()
            log2.debug "Session $session triggered with $par" 
            
            int limit, offset
            def column, value
            
            // core stuff
            String action = par["POST"]["do"] //show, update, etc
            String lang = par["POST"]["lg"] 
            
            ServerMessage sm = new ServerMessage("AdminGeoscopeMapping", lang, bind, session)  
            
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
            /** 1.1 show - PAGE geoscopes **/
            /***************************/

            if (action == "show") {
        	Map h
                try {
                    log.debug "Querying Geoscopes: limit $limit offset $offset column $column value $value"
                    h = Geoscope.getGeoscopes(limit, offset, column, value)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_geoscope_list"][lang]+": "+e.getMessage())
                }
                
                // you have to "JSONize" the NEs
                h.result.eachWithIndex{geo, i -> h.result[i] = geo.toMap() }

                return sm.statusMessage(0,h)
            }
            
            /***************************/
            /** 1.2 update a Geoscope value **/
            /***************************/
            
            if (action == "update") {
        	
        	Long id
                if (par["POST"]["id"]) id = Long.parseLong(par["POST"]["id"])
                if (!id) return sm.notEnoughVars("id=$id")
                
                if (!column || !value) return sm.notEnoughVars("c=$column v=$value")
                
                Geoscope geo
                try {
                    log.debug "Querying Geoscope with id $id"
                    geo = Geoscope.getFromID(id)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_geoscope_list"][lang]+": "+e.getMessage())                 
                }
                
                def res
                try {
                    res = geo.updateValue(c, v)
                }  catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_updating_geoscope"][lang]+": "+e.getMessage())                 
                }
                return sm.statusMessage(0, geo.toMap())

            } 
                
            /***************************************/
            /** 1.3 delete - DELETE Geoscope (admin only) **/
            /***************************************/
           
            if (action == "delete") {
                Long id 
                try {
                    id = Long.parseLong(par["POST"]["id"])                      
                }catch(Exception e) {}
                               
                if (!id)  return sm.notEnoughVars("id=$id")      
                Geoscope geo 
                try {
                    log.debug "Querying Geoscope with id $id"
                    geo = Geoscope.getFromID(id)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_geoscope_list"][lang]+": "+e.getMessage())                 
                }
                          
                try {
                    log.debug "deleting geoscope $id"
                    answer = geo.deleteGeoscope() 
                } catch(Exception e) {return sm.statusMessage(-1, i18n.servermessage["error_deleting_geoscope"][lang]+": "+e.getMessage())}
                  
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(answer, i18n.servermessage['ok'][lang])		   	
            }    
            
            /***************************************/
            /** 1.4 create **/
            /***************************************/
           
            if (action == "create") {
                  
               String geo_name = par["POST"]["geo_name"]
               Long geo_woeid
               try {
        	   geo_woeid = Long.parseLong(par["POST"]["geo_woeid"])
               } catch(Exception e) {}
               Integer geo_woeid_type 
               try {
        	   geo_woeid_type = Integer.parseInt(par["POST"]["geo_woeid_type"])
               } catch(Exception e) {}
              
               Geoscope geo = new Geoscope(geo_name:geo_name, 
        	       geo_woeid:geo_woeid,
        	       geo_woeid_type:geo_woeid_type)
               try {
                    log.debug "Adding Geoscope"
                    geo.addThisToDB()
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_creating_geoscope"][lang]+": "+e.getMessage())                 
                }
               
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(0, geo.toMap())		   	
            }    
            
            return sm.unknownAction()
        }
    }
}