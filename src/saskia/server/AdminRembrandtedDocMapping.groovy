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
import saskia.io.User
import saskia.io.RembrandtedDoc
import saskia.io.Tag
import saskia.io.Entity
import saskia.util.I18n
import org.apache.log4j.*

/**
 * 
 * This class is more for admin stuff, managing rembrandted docs, not its content
 * @author Nuno Cardoso
 *
 */
public class AdminRembrandtedDocMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n
    static Logger log = Logger.getLogger("SaskiaServer") 
    static Logger log2 = Logger.getLogger("SaskiaService") 
 
    public AdminRembrandtedDocMapping() {
        
        i18n = I18n.newInstance()
        
        JSONanswer = {req, par, bind ->
            long session = System.currentTimeMillis()
            log2.debug "Session $session triggered with $par" 
            
            int limit, offset
            def column, value
            Collection collection
            
            // core stuff
            String action = par["POST"]["do"] //show, update, etc
            String lang = par["POST"]["lg"] 
            long collection_id 
            try {
        	if (par["POST"]["ci"]) collection_id = Long.parseLong(par["POST"]["ci"])
            } catch(Exception e) {e.printStackTrace()}
            
            ServerMessage sm = new ServerMessage("AdminRembrandtedDocMapping", lang, bind, session)  
            
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
            
            if (!user.isEnabled()) return sm.userNotEnabled()
            
            if (!action || !lang) 
        	return sm.notEnoughVars("do=$action, lg=$lang")      
        	
            sm.setAction(action)
            
            collection = Collection.getFromID(collection_id)
            if (!collection) return sm.statusMessage(-1, i18n.servermessage['no_collection_found'][lang])                                     
  
            /***************************/
            /** 1.1 show - PAGE RDOCS **/
            /***************************/
                     
            Map h
            
            if (action == "show") {
        	
             	if (!(user.isSuperUser() || user.canReadCollection(collection)) )  
             	    return sm.statusMessage(-1, i18n.servermessage['no_collection_admin'][lang])
               	
                try {
                    log.debug "Querying rembrandt docs: limit $limit offset $offset column $column value $value"
                    h = RembrandtedDoc.getRembrandtedDocs(collection, limit, offset, column, value)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_rdoc_list"][lang]+": "+e.getMessage())
                }
                
                h['collection_id'] = collection.col_id
                
                // hashmap has total, offset, limit, result as a List<Users>. We need to convert User objects 
                // in a HashMap friendly JSON format
                h.result.eachWithIndex{rdoc, i -> 
                    // "doc_entity":rdoc.doc_entity?.ent_dbpedia_resource, 
                    //  "doc_tag":rdoc.doc_tag.tag_version, 
                    h.result[i] = rdoc.toMap()
                 }
                 return sm.statusMessage(0, h)                  
            }
               
            /***************************/
            /** 1.2 show a single RDOC content **/
            /***************************/
            
            if (action == "showrdoc") {
        	if (!(user.isSuperUser() || user.canReadCollection(collection)) ) 
        	    return sm.statusMessage(-1, i18n.servermessage['no_collection_admin'][lang])
        	    
                long doc_id
                String format
                if (par["POST"]["doc_id"]) doc_id = Long.parseLong(par["POST"]["doc_id"])
                if (par["POST"]["format"]) format = par["POST"]["format"]
                if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")
                
                RembrandtedDoc doc 
                try {
                    log.debug "Querying Rembrandted doc with id $doc_id"
                    doc = RembrandtedDoc.getFromID(doc_id)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_rdoc_list"][lang]+": "+e.getMessage())                 
                }
                
                String title, body
                try {
                   
                    //RembrandtReader reader = new RembrandtReader(new RembrandtStyleTag(lang))
                    //rembrandt.obj.Document doc = reader.createDocument(doc.doc_content)
                    
                    title = doc.getTitleFromContent()
                    body = doc.getBodyFromContent()
                    title = title?.replaceAll(/\n/, " ")
                    body = body?.replaceAll(/\n/, " ")
                } catch(Exception e) {return sm.statusMessage(-1, e.getMessage())}
               
                return sm.statusMessage(0, [dt:title, db:body])
            }
            
            /***************************/
            /** 1.3 update a value **/
            /***************************/
            
            if (action == "update") {
        	if (!(user.isSuperUser() || user.canWriteCollection(collection)) ) 
        	    return sm.statusMessage(-1, i18n.servermessage['no_collection_admin'][lang])
                Long doc_id
                // column is already here, as well as value
                
                String format
                if (par["POST"]["id"]) doc_id = Long.parseLong(par["POST"]["id"])
                if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")
                
                RembrandtedDoc doc 
                try {
                    log.debug "Querying Rembrandted doc with id $doc_id"
                    doc = RembrandtedDoc.getFromID(doc_id)
                } catch(Exception e) {
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_rdoc_list"][lang]+": "+e.getMessage())                 
                }
                
                switch(column) {
                    case "doc_tag": 
                	Tag tag
                        try {
                           tag = Tag.getFromID(Long.parseLong(value))
                           doc.associateWithTag(tag) 
                        }catch(Exception e)  {
                    	return sm.statusMessage(-1, i18n.servermessage["error_updating_rdoc"][lang]+": "+e.getMessage())                 
                        }
                        return sm.statusMessage(0, tag.toMap())
                    break                     
                }
            }
            
            return sm.unknownAction()
        }
    }
}