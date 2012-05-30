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

import saskia.stats.SaskiaStats
import saskia.util.I18n
import org.apache.log4j.*

/**
 * 
 * This class is more for admin stuff, managing rembrandted docs, not its content
 * @author Nuno Cardoso
 *
 */
public class DocMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n   
	SaskiaStats stats
	SaskiaDB db
	
    static Logger mainlog = Logger.getLogger("SaskiaServerMain")  
    static Logger errorlog = Logger.getLogger("SaskiaServerErrors")  
    static Logger processlog = Logger.getLogger("SaskiaServerProcessing")  
 
    public DocMapping(SaskiaDB db) {
        
		this.db = db
        i18n = I18n.newInstance()
        UserTable userTable = db.getDBTable("UserTable")
        CollectionTable collectionTable = db.getDBTable("CollectionTable")
        DocTable docTable = db.getDBTable("DocTable")
		
        JSONanswer = {req, par, bind ->
            long session = System.currentTimeMillis()
            processlog.debug "Session $session triggered with $par" 
            
            int limit
				long offset
            def column, value
            
            // core stuff
           String action = req.getAttributes().get("action");
            String lang = par["POST"]["lg"] 
       
            ServerMessage sm = new ServerMessage("DocMapping", lang, bind, session, processlog)  
            
            // pager stuff
            if (par["POST"]["l"]) limit = (int) par["POST"]["l"]
            if (par["POST"]["o"]) offset = (long) par["POST"]["o"]
            if (par["POST"]["c"]) column = par["POST"]["c"]
            if (par["POST"]["v"]) value = par["POST"]["v"]
            
            String api_key = par["POST"]["api_key"] 
            if (!api_key) api_key = par["COOKIE"]["api_key"]   
            if (!api_key) return sm.noAPIKeyMessage()

            User user = userTable.getFromAPIKey(api_key)           
            if (!user) return sm.userNotFound()
            if (!user.isEnabled()) return sm.userNotEnabled()

            if (!action || !lang) return sm.notEnoughVars("do=$action, lg=$lang")
            sm.setAction(action)
             
            /*********************/
            /** 1.1  List DOCS **/
            /*********************/

            if (action == "list") {

				Map h
            	Long collection_id 
            	Collection collection 
					try {
        				collection_id = (long) par["POST"]["ci"]
            	} catch(Exception e) {e.printStackTrace()}
           		if (!collection_id) return sm.notEnoughVars("ci="+par["POST"]["ci"])                                  
					collection = collectionTable.getFromID(collection_id)
            	if (!collection) return sm.noCollectionFound()

					if (!collectionTable.canRead(user, collection))
						return sm.insufficientPermissions()

        			try {
                    h = docTable.listDocs(collection, limit, offset, column, value)
                } catch(Exception e) {
               	  errorlog.error i18n.servermessage['error_getting_doc_list'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_doc_list"][lang]+": "+e.getMessage())
                }
                
                // h already has col_id   
                h.result.eachWithIndex{doc, i -> h.result[i] = doc.toMap() }
 					 h.perms = collectionTable.getPermissionsFromUserOnCollection(user, collection)
					 h.col_id=collection_id
                return sm.statusMessage(0, h)                  
            }
               
            /**************************/
            /** 1.2 show DOC content **/
            /**************************/
            
            if (action == "show") {
        	
                Long doc_id
                String format
                if (par["POST"]["doc_id"]) 
					   try {doc_id = (long) par["POST"]["doc_id"]}
						catch(Exception e) {}
                if (par["POST"]["format"]) format = par["POST"]["format"]
                if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")
                
                Doc doc 
                try {
                    doc = docTable.getFromID(doc_id)
                } catch(Exception e) {
               	  errorlog.error i18n.servermessage['error_getting_doc'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_doc"][lang]+": "+e.getMessage())                 
                }
  
				// check permissions
				if (!collectionTable.canRead(user, doc.doc_collection))
					return sm.insufficientPermissions()

				def nes = doc.getNEs()
				for (int i = 0; i < nes.size(); i++) {
					// ask each NE to JSON itself
					nes[i]["ne"] = nes[i]["ne"].toMap()
				}
				
				// patches are accepted changes to docs, made of merged commits
				def patches = doc.getPatchesToCurrentVersion()
				for (int i = 0; i < patches.size(); i++) {
					// ask each NE to JSON itself
					patches[i] = patches[i].toMap()
				}
				
				// commits are user changes (not yet approved) to docs 
				def commits = doc.getCommitsForUser(user)
				for (int i = 0; i < commits.size(); i++) {
					// ask each NE to JSON itself
					commits[i] = commits[i].toMap()
				}

				def answer = [
					"doc":doc.toMap(),
					"nes":nes,
					"patches":patches,
					"commits":commits
					
				]
                return sm.statusMessage(0, answer)
            }

            /***********************/
            /** 1.3 metadata DOC **/
            /***********************/
					
        		if (action == "metadata") {
					Long doc_id
               if (par["POST"]["doc_id"]) 
					   try {doc_id = (long) par["POST"]["doc_id"]}
						catch(Exception e) {}
               if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")
               if (!lang) return sm.notEnoughVars("lg=$lang")
      			
					Doc doc 
               try {
                    doc = docTable.getFromID(doc_id)
                } catch(Exception e) {
               	  errorlog.error i18n.servermessage['error_getting_doc'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_doc"][lang]+": "+e.getMessage())                 
                }
					 
					// check permissions
					if (!collectionTable.canRead(user, doc.doc_collection))					
						return sm.insufficientPermissions()

           		stats = new SaskiaStats()
            	def answer 
            	try {
        				answer = stats.renderDocPage(doc, doc.doc_collection, lang)
            	} catch(Exception e) {
                	  errorlog.error i18n.servermessage['error_getting_doc_metadata'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_doc_metadata"][lang]+": "+e.getMessage()) 	
            	}
            
					return sm.statusMessage(0, ['doc_id':doc.doc_id,
					'doc_original_id':doc.doc_original_id, 'doc_content':answer])
				} 
				           
				/*********************/
            /** 1.4 create RDOC **/
            /*********************/
            				      
				  if (action == "create") {
						// required: doc_original_id, doc_collection, doc_lang, doc_content
						// doc_content will then be converted to doc_webstore
						// generated: doc_webstore, doc_version, doc_date_created, doc_date_tagged, doc_proc, doc_sync

                String doc_original_id =  par["POST"]["doc_original_id"] 
                Long doc_collection
 					 try {doc_collection = (long) par["POST"]["doc_collection"]}
					 catch(Exception e) {}
					
                String doc_lang =  par["POST"]["doc_lang"] 
 					 String doc_content =  par["POST"]["doc_content"]
 
                if (!doc_original_id) return sm.notEnoughVars("doc_original_id=$doc_original_id") 
                Collection collection = collectionTable.getFromID(doc_collection)
					 if (!collection) return sm.collectionNotFound()
                if (!doc_lang) return sm.notEnoughVars("doc_lang=$doc_lang") 
                if (!doc_content) return sm.notEnoughVars("doc_content=$doc_content") 
                   
             	  // check permissions
                if (!collectionTable.canAdmin(user, collection)) 
						return sm.insufficientPermissions()
					
					 int number_docs = collection.getNumberOfDocuments()
					 if (user.usr_max_docs_per_collection < number_docs) 
					    return sm.statusMessage(-1, i18n.servermessage['max_number_documents_per_collection_reached'][lang])
					  	
 				  	  try {
                    Doc doc = new Doc(
							 doc_original_id:doc_original_id, doc_collection:collection, 
							 doc_content:doc_content, doc_lang:doc_lang, doc_date_created:new Date(),
						    doc_version:1, doc_date_tagged:new Date())
						   
						    //doc.associateWithTag(tag)
							
							doc.doc_id = doc.addThisToDB()
		    				doc.changeProcStatusInDBto(DocStatus.READY) 
						} catch(Exception e) {
                    errorlog.error i18n.servermessage['error_creating_doc'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage['error_creating_doc'][lang]+": "+e.getMessage())
                }
                        
                // leave like that, to include an id field
                return sm.statusMessage(0, doc.toMap())	
            }
 

            /*********************/
            /** 1.5 update RDOC **/
            /*********************/
            
            if (action == "update") {
        	
					Long doc_id
               String format
               if (par["POST"]["doc_id"]) 
					   try {doc_id = (long) par["POST"]["doc_id"]}
						catch(Exception e) {}
               if (par["POST"]["format"]) format = par["POST"]["format"]
               if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")
      
               Doc doc 
               try {
                    doc = docTable.getFromID(doc_id)
                } catch(Exception e) {
               	  errorlog.error i18n.servermessage['error_getting_doc'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_doc"][lang]+": "+e.getMessage())                 
                }

             	  // check permissions
                
					 if (!doc.doc_collection) return sm.collectionNotFound()
                if (!collectionTable.canAdmin(user, doc.doc_collection)) 
						return sm.insufficientPermissions()
					            
                switch(column) {
	
                  // let's handle this alone, because it's a shared table
						case "doc_tag": 
                	Tag tag
                  try {
							tag = Tag.getFromID((long) value)
                     doc.associateWithTag(tag) 
                  } catch(Exception e)  {
               	   errorlog.error i18n.servermessage['error_updating_doc'][lang]+": "+e.printStackTrace()
                    	return sm.statusMessage(-1, i18n.servermessage["error_updating_doc"][lang]+": "+e.getMessage())                 
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
                    errorlog.error i18n.servermessage['error_updating_doc'][lang]+": "+e.printStackTrace() 
                    return sm.statusMessage(-1, i18n.servermessage["error_updating_doc"][lang]+": "+e.getMessage())                 
               	}
                	return sm.statusMessage(res, doc.toMap())
            	} 
            }
			
			/*********************/
            /** 1.5 delete DOC **/
            /*********************/
           
            if (action == "delete") {
                Long doc_id
               if (par["POST"]["doc_id"]) 
					   try {doc_id = (long) par["POST"]["doc_id"]}
						catch(Exception e) {}
               if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")  
                
					Doc doc 
               try {
                    doc = docTable.getFromID(doc_id)
                } catch(Exception e) {
               	  errorlog.error i18n.servermessage['error_getting_doc'][lang]+": "+e.printStackTrace()
                    return sm.statusMessage(-1, i18n.servermessage["error_getting_doc"][lang]+": "+e.getMessage())                 
                }

            	 // check permissions
                if (!doc.doc_collection) return sm.collectionNotFound()
                if (!collectionTable.canAdmin(user, doc.doc_collection)) 
						return sm.insufficientPermissions()

                def res          
                try {
                    res = doc.deleteThisFromDB() 
                } catch(Exception e) {
	              	  errorlog.error i18n.servermessage['error_deleting_entity'][lang]+": "+e.printStackTrace()
						  return sm.statusMessage(-1, i18n.servermessage["error_deleting_entity"][lang]+": "+e.getMessage())
				    }
                  
                //RETURNS 1 IF UPDATED
                return sm.statusMessage(res, i18n.servermessage['ok'][lang])		   	
            }   

			return sm.unknownAction()
        }
    }
}