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

public class AdminGeoscopeMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n
	SaskiaDB db
    static Logger mainlog = Logger.getLogger("SaskiaServerMain")  
    static Logger errorlog = Logger.getLogger("SaskiaServerErrors")  
    static Logger processlog = Logger.getLogger("SaskiaServerProcessing")  
 
    public AdminGeoscopeMapping(SaskiaDB db) {
        
        i18n = I18n.newInstance()
        this.db = db
		UserTable userTable = db.getDBTable("UserTable")
		GeoscopeTable geoscopeTable = db.getDBTable("GeoscopeTable")
		
        JSONanswer = {req, par, bind ->
            long session = System.currentTimeMillis()
            processlog.debug "Session $session triggered with $par" 
            
            int limit
				long offset
            def column, value
            
            // core stuff
            String action = par["POST"]["do"] //show, update, etc
            String lang = par["POST"]["lg"] 
            
            ServerMessage sm = new ServerMessage("AdminGeoscopeMapping", lang, bind, session, processlog)  
            
            // pager stuff
            if (par["POST"]["l"]) limit = Integer.parseInt(par["POST"]["l"])
				if (!limit) limit = 0
            if (par["POST"]["o"]) offset = Long.parseLong(par["POST"]["o"])
				if (!offset) offset = 0
            if (par["POST"]["c"]) column = par["POST"]["c"]
            if (par["POST"]["v"]) value = par["POST"]["v"]
            
            // auth stuff
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
           
            /******************************/
            /** 1.1 show - PAGE geoscopes */
            /******************************/

            if (action == "list") {
        			Map h
               try {
                   h = geoscopeTable.listGeoscopes(limit, offset, column, value)
               } catch(Exception e) {
                  errorlog.error i18n.servermessage['error_getting_geoscope_list'][lang]+": "+e.printStackTrace() 
						return sm.statusMessage(-1, i18n.servermessage["error_getting_geoscope_list"][lang]+": "+e.getMessage())
                }
                
                // you have to "JSONize" the NEs
                h.result.eachWithIndex{geo, i -> h.result[i] = geo.toMap() }
                return sm.statusMessageWithPubKey(0,h,user.usr_pub_key)
            }
            
            /*********************************/
            /** 1.2 update a Geoscope value **/
            /*********************************/
            
            if (action == "update") {
        	
        			Long id
               if (par["POST"]["id"]) 
					try {id = Long.parseLong(par["POST"]["id"])}
					catch(Exception e) {}
               if (!id) return sm.notEnoughVars("id=$id")
               if (!column || !value) return sm.notEnoughVars("c=$column v=$value")
                
               Geoscope geo
               try {
                    geo = geoscopeTable.getFromID(id)
               } catch(Exception e) {
                    errorlog.error i18n.servermessage['error_getting_geoscope'][lang]+": "+e.printStackTrace() 
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_geoscope"][lang]+": "+e.getMessage())                 
               }
					int res = 0
               try {
                    res = geo.updateValue(c, v)
               } catch(Exception e) {
                    errorlog.error i18n.servermessage['error_updating_geoscope'][lang]+": "+e.printStackTrace() 
                    return sm.statusMessage(-1, i18n.servermessage["error_updating_geoscope"][lang]+": "+e.getMessage())                 
               }
                return sm.statusMessageWithPubKey(res, geo.toMap(), user.usr_pub_key)
            } 
                
            /**********************************/
            /** 1.3 delete - DELETE Geoscope **/
            /**********************************/
           
            if (action == "delete") {
                Long id 
                try {id = Long.parseLong(par["POST"]["id"])                      
                }catch(Exception e) {}                               
                if (!id)  return sm.notEnoughVars("id=$id")      
                
				Geoscope geo 
                try {
                    geo = geoscopeTable.getFromID(id)
                } catch(Exception e) {
                    errorlog.error i18n.servermessage['error_getting_geoscope'][lang]+": "+e.printStackTrace() 
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_geoscope"][lang]+": "+e.getMessage())                 
                }
                def res          
                try {
                    res = geo.deleteGeoscope() 
                } catch(Exception e) {
                   errorlog.error i18n.servermessage['error_deleting_geoscope'][lang]+": "+e.printStackTrace() 
 						return sm.statusMessage(-1, i18n.servermessage["error_deleting_geoscope"][lang]+": "+e.getMessage())
					 }                 
                //RETURNS 1 IF UPDATED
                return sm.statusMessageWithPubKey(res, i18n.servermessage['ok'][lang],user.usr_pub_key)		   	
            }    
            
            /*************************/
            /** 1.4 create geoscope **/
            /*************************/
           
            if (action == "create") {
                  
               String geo_name = par["POST"]["geo_name"]
               Long geo_woeid
               try {geo_woeid = Long.parseLong(par["POST"]["geo_woeid"])
               } catch(Exception e) {}
               Integer geo_woeid_type 
               try {geo_woeid_type = Integer.parseInt(par["POST"]["geo_woeid_type"])
               } catch(Exception e) {}
              
					if (!geo_name || !geo_woeid || !geo_woeid_type) return sm.notEnoughVars("$geo_name, $geo_woeid, $geo_woeid_type")      

               Geoscope geo 
               try {
                    geo = new Geoscope(geo_name:geo_name, geo_woeid:geo_woeid, geo_woeid_type:geo_woeid_type)
                    geo.geo_id = geo.addThisToDB()
                } catch(Exception e) {
                    errorlog.error i18n.servermessage['error_creating_geoscope'][lang]+": "+e.printStackTrace() 
                    return sm.statusMessage(-1, i18n.servermessage["error_creating_geoscope"][lang]+": "+e.getMessage())                 
                }
               
                return sm.statusMessageWithPubKey(0, geo.toMap(),user.usr_pub_key)	   	
            }    
            
            return sm.unknownAction()
        }
    }
}