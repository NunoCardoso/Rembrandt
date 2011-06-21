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

import saskia.db.obj.*;
import saskia.db.table.*
import saskia.stats.SaskiaStats
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
	 SaskiaStats stats

    static Logger mainlog = Logger.getLogger("SaskiaServerMain")  
    static Logger errorlog = Logger.getLogger("SaskiaServerErrors")  
    static Logger processlog = Logger.getLogger("SaskiaServerProcessing")  
 
    public AdminRembrandtedDocMapping() {
        
        i18n = I18n.newInstance()
        
        JSONanswer = {req, par, bind ->
            long session = System.currentTimeMillis()
            processlog.debug "Session $session triggered with $par" 
            
            int limit
 				long offset
            def column, value
            
            // core stuff
            String action = par["POST"]["do"]
            String lang = par["POST"]["lg"] 
       
            ServerMessage sm = new ServerMessage("AdminRembrandtedDocMapping", lang, bind, session, processlog)  
            
            // pager stuff
            if (par["POST"]["l"]) limit = Integer.parseInt(par["POST"]["l"])
            if (par["POST"]["o"]) offset = Long.parseLong(par["POST"]["o"])
            if (par["POST"]["c"]) column = par["POST"]["c"]
            if (par["POST"]["v"]) value = par["POST"]["v"]
            
            String api_key = par["POST"]["api_key"] 
            if (!api_key) api_key = par["COOKIE"]["api_key"]   
            if (!api_key) return sm.noAPIKeyMessage()

            User user = UserTable.getFromAPIKey(api_key)           
            if (!user) return sm.userNotFound()
            if (!user.isEnabled()) return sm.userNotEnabled()
				// all Admin*Mappings must have this
				if (!user.isSuperUser()) return sm.noSuperUser()
            if (!action || !lang) return sm.notEnoughVars("do=$action, lg=$lang")        	
            sm.setAction(action)
             
            /*********************/
            /** 1.1  List RDOCS **/
            /*********************/
                     
            if (action == "list") {
	
            	Map h
            	Long collection_id 
            	Collection collection 
					try {
        				collection_id = Long.parseLong(par["POST"]["ci"])
            	} catch(Exception e) {e.printStackTrace()}
           			if (!collection_id) return sm.notEnoughVars("ci=$ci")                                  
					collection = Collection.getFromID(collection_id)
            	if (!collection) return sm.noCollectionFound()

        			try {
                    h = RembrandtedDoc.listRembrandtedDocs(collection, limit, offset, column, value)
                } catch(Exception e) {
               	  errorlog.error i18n.servermessage['error_getting_rdoc_list'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_rdoc_list"][lang]+": "+e.getMessage())
                }
                
                h.col_id=collection_id
                h.result.eachWithIndex{rdoc, i -> h.result[i] = rdoc.toMap() }
                return sm.statusMessageWithPubKey(0, h, user.usr_pub_key)                  
            }
               
            /***************************/
            /** 1.2 show RDOC content **/
            /***************************/
            
            if (action == "show") {
        	
                Long doc_id
                String format
                if (par["POST"]["doc_id"]) 
					   try {doc_id = Long.parseLong(par["POST"]["doc_id"])}
						catch(Exception e) {}
                if (par["POST"]["format"]) format = par["POST"]["format"]
                if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")
                
                RembrandtedDoc doc 
                try {
                    doc = RembrandtedDoc.getFromID(doc_id)
                } catch(Exception e) {
               	  errorlog.error i18n.servermessage['error_getting_rdoc'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_rdoc"][lang]+": "+e.getMessage())                 
                }  
                return sm.statusMessageWithPubKey(0, doc.toMap(), user.usr_pub_key) 
            }


            /***********************/
            /** 1.3 metadata RDOC **/
            /***********************/
					
        		if (action == "metadata") {
					Long doc_id
               if (par["POST"]["doc_id"]) 
					   try {doc_id = Long.parseLong(par["POST"]["doc_id"])}
						catch(Exception e) {}
               if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")
               if (!lang) return sm.notEnoughVars("lg=$lang")
      			
					RembrandtedDoc doc 
               try {
                    doc = RembrandtedDoc.getFromID(doc_id)
                } catch(Exception e) {
               	  errorlog.error i18n.servermessage['error_getting_rdoc'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_rdoc"][lang]+": "+e.getMessage())                 
                }

           		stats = new SaskiaStats()
            	def answer 
            	try {
        				answer = stats.renderDocPage(doc, collection, lang)
            	} catch(Exception e) {
                	  errorlog.error i18n.servermessage['error_getting_rdoc_metadata'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_rdoc_metadata"][lang]+": "+e.getMessage()) 	
            	}
            
					return sm.statusMessageWithPubKey(0, ['doc_id':doc.doc_id,
					'doc_original_id':doc.doc_original_id, 'doc_content':answer], user.usr_pub_key) 
				}     
				
            /*********************/
            /** 1.5 create RDOC **/
            /*********************/
            				      
				  if (action == "create") {
						// required: doc_original_id, doc_collection, doc_lang, doc_content
						// doc_content will then be converted to doc_webstore
						// generated: doc_webstore, doc_version, doc_date_created, doc_date_tagged, doc_proc, doc_sync

                String doc_original_id =  par["POST"]["doc_original_id"] 
                Long doc_collection
 					 try {doc_collection = Long.parseLong(par["POST"]["doc_collection"])}
					 catch(Exception e) {}
					
                String doc_lang =  par["POST"]["doc_lang"] 
 					 String doc_content =  par["POST"]["doc_content"]
 
                if (!doc_original_id) return sm.notEnoughVars("doc_original_id=$doc_original_id") 
                Collection collection = Collection.getFromID(doc_collection)
					 if (!collection) return sm.collectionNotFound()
                if (!doc_lang) return sm.notEnoughVars("doc_lang=$doc_lang") 
                if (!doc_content) return sm.notEnoughVars("doc_content=$doc_content") 
                                              
					 int number_rdocs = collection.getNumberOfRembrandtedDocuments()
					 if (user.usr_max_docs_per_collection < number_rdocs) 
					    return sm.statusMessage(-1, i18n.servermessage['max_number_documents_per_collection_reached'][lang])
					  	
 				  	  try {
                    RembrandtedDoc doc = new RembrandtedDoc(
							 doc_original_id:doc_original_id, doc_collection:collection, 
							 doc_content:doc_content, doc_lang:doc_lang, doc_date_created:new Date(),
						    doc_version:1, doc_date_tagged:new Date())
						   
						    //rdoc.associateWithTag(tag)
							
							doc.doc_id = doc.addThisToDB()
		    				doc.changeProcStatusInDBto(DocStatus.READY) // mark it so next time we'll not use it
		    				doc.changeSyncStatusInDBto(DocStatus.NOT_SYNCED_DOC_CHANGED) 
						} catch(Exception e) {
                    errorlog.error i18n.servermessage['error_creating_rdoc'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage['error_creating_rdoc'][lang]+": "+e.getMessage())
                }
                        
                // leave like that, to include an id field
                return sm.statusMessageWithPubKey(0, doc.toMap(), user.usr_pub_key) 	
            }
 
            /*********************/
            /** 1.5 update RDOC **/
            /*********************/
            
            if (action == "update") {
        	
					Long doc_id
               String format
               if (par["POST"]["doc_id"]) 
					   try {doc_id = Long.parseLong(par["POST"]["doc_id"])}
						catch(Exception e) {}
               if (par["POST"]["format"]) format = par["POST"]["format"]
               if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")
      
               RembrandtedDoc doc 
               try {
                    doc = RembrandtedDoc.getFromID(doc_id)
                } catch(Exception e) {
               	  errorlog.error i18n.servermessage['error_getting_rdoc'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_rdoc"][lang]+": "+e.getMessage())                 
                }
                
                switch(column) {
	
                  // let's handle this alone, because it's a shared table
						case "doc_tag": 
                	Tag tag
                  try {
							tag = Tag.getFromID(Long.parseLong(value))
                     doc.associateWithTag(tag) 
                  } catch(Exception e)  {
               	   errorlog.error i18n.servermessage['error_updating_rdoc'][lang]+": "+e.printStackTrace()
                    	return sm.statusMessage(-1, i18n.servermessage["error_updating_rdoc"][lang]+": "+e.getMessage())                 
                  }
                  return sm.statusMessage(0, tag.toMap())
                  break  
						
						case "doc_collection": 
						return sm.statusMessage(-1, "Change collection for doc is not allowed")   
						break
						
                  default:
						
						int res = 0
						try {
                    res = doc.updateValue(c, v)
               	} catch(Exception e) {
                    errorlog.error i18n.servermessage['error_updating_rdoc'][lang]+": "+e.printStackTrace() 
                    return sm.statusMessage(-1, i18n.servermessage["error_updating_rdoc"][lang]+": "+e.getMessage())                 
               	}
                	return sm.statusMessageWithPubKey(res, doc.toMap(), user.usr_pub_key) 
            	} 
            }

				
				/*********************/
            /** 1.6 delete RDOC **/
            /*********************/
           
            if (action == "delete") {
                Long doc_id
               if (par["POST"]["doc_id"]) 
					   try {doc_id = Long.parseLong(par["POST"]["doc_id"])}
						catch(Exception e) {}
               if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")  
                
					RembrandtedDoc doc 
               try {
                    doc = RembrandtedDoc.getFromID(doc_id)
                } catch(Exception e) {
               	  errorlog.error i18n.servermessage['error_getting_rdoc'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_rdoc"][lang]+": "+e.getMessage())                 
                }
                def res          
                try {
                    res = doc.deleteThisFromDB() 
                } catch(Exception e) {
	              	  errorlog.error i18n.servermessage['error_deleting_entity'][lang]+": "+e.printStackTrace()
						  return sm.statusMessage(-1, i18n.servermessage["error_deleting_entity"][lang]+": "+e.getMessage())
				    }
                  
                //RETURNS 1 IF UPDATED
                return sm.statusMessageWithPubKey(res, i18n.servermessage['ok'][lang], user.usr_pub_key) 		   	
            }   
				return sm.unknownAction()
        }
    }
}