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
public class CommitMapping extends WebServiceRestletMapping {
    
    Closure JSONanswer
    I18n i18n   
	SaskiaStats stats
	SaskiaDB db
	
    static Logger mainlog = Logger.getLogger("SaskiaServerMain")  
    static Logger errorlog = Logger.getLogger("SaskiaServerErrors")  
    static Logger processlog = Logger.getLogger("SaskiaServerProcessing")  
 
    public CommitMapping(SaskiaDB db) {
        
		this.db = db
        i18n = I18n.newInstance()
        UserTable userTable = db.getDBTable("UserTable")
        DocTable docTable = db.getDBTable("DocTable")
        CommitTable commitTable = db.getDBTable("CommitTable")
        CollectionTable collectionTable = db.getDBTable("CollectionTable")
		
        JSONanswer = {req, par, bind ->
	
            String lang = par["POST"]["lang"] 
            long session = System.currentTimeMillis()

            ServerMessage sm = new ServerMessage("CommitMapping", lang, bind, session, processlog)  
            processlog.debug "Session $session triggered with $par" 

            String action = req.getAttributes().get("action");

            int limit
			long offset
            def column, value
            
            // pager stuff
            if (par["POST"]["l"]) limit = (int) par["POST"]["l"]
            if (par["POST"]["o"]) offset = (int) par["POST"]["o"]
            if (par["POST"]["c"]) column = par["POST"]["c"]
            if (par["POST"]["v"]) value = ""+par["POST"]["v"]
            
            String api_key = par["POST"]["api_key"] 
            if (!api_key) api_key = par["COOKIE"]["api_key"]   
            if (!api_key) return sm.noAPIKeyMessage()

            User user = userTable.getFromAPIKey(api_key)           
            if (!user) return sm.userNotFound()
            if (!user.isEnabled()) return sm.userNotEnabled()

            if (!action || !lang) return sm.notEnoughVars("do=$action, lg=$lang")
            sm.setAction(action)
             
            /***********************/
            /** 1.1  List Commits **/
            /***********************/

            if (action == "list") {

				Map h
				Long doc_id 
				Doc doc
				Collection collection 
				try {
					doc_id = (long) par["POST"]["cmm_doc"]
				} catch(Exception e) {e.printStackTrace()}
				if (!doc_id) return sm.notEnoughVars("cmm_doc="+par["POST"]["cmm_doc"])                                  
				try {
					doc = docTable.getFromID(doc_id)
				}catch(Exception e) {}
				if (!doc)
					return sm.docNotFound()
				try {
					collection = doc.doc_collection
				} catch(Exception e) {}
				if (!collection)
					return sm.collectionNotFound()

				if (!collectionTable.canRead(user, collection))
					return sm.insufficientPermissions()

				try {
					h = commitTable.listCommits(doc, limit, offset, column, value)
				} catch(Exception e) {
					errorlog.error "Error_getting_doc_list: "+e.printStackTrace()
					return sm.statusMessage(-1,"Error_getting_doc_list: "+e.getMessage())
				}
				
				h.result.eachWithIndex{commit, i -> h.result[i] = commit.toMap() }
				return sm.statusMessage(0, h)                  
			}
               
            /*********************/
            /** 1.2 save commit **/
            /*********************/
            
            if (action == "save") {
        	
				Map h
				Long doc_id 
				Doc doc
				String cmm_commit
				Collection collection 
				try {
					doc_id = (long) par["POST"]["cmm_doc"]
				} catch(Exception e) {e.printStackTrace()}
				try {
					cmm_commit = par["POST"]["cmm_commit"]
				} catch(Exception e) {e.printStackTrace()}
				
				if (!doc_id) return sm.notEnoughVars("cmm_doc="+par["POST"]["cmm_doc"])                                  

				try {
					doc = docTable.getFromID(doc_id)
				}catch(Exception e) {}
				if (!doc)
					return sm.docNotFound()
					
				try {
					collection = doc.doc_collection
				} catch(Exception e) {}
				if (!collection)
					return sm.collectionNotFound()

				if (!collectionTable.canWrite(user, collection))
					return sm.insufficientPermissions()

				Commit c 
                try {
					c = new Commit(db.getDBTable("CommitTable"))
					c.cmm_doc = doc
					c.cmm_date = new Date()
					c.cmm_commit = cmm_commit
					c.cmm_user = user
					c.cmm_id = c.addThisToDB()
				
				} catch(Exception e) {
					errorlog.error "Error_saving_commit: "+e.printStackTrace()
					return sm.statusMessage(-1,"Error_saving_commit: "+e.getMessage())
				}
				return sm.statusMessage(0, c.toSimpleMap()) 	
            }

			
			/***********************/
            /** 1.5 delete commit **/
            /***********************/
           
            if (action == "delete") {
				Map h
				Long cmm_id 
				Doc doc
				Collection collection 
				try {
					cmm_id = (long) par["POST"]["cmm_id"]
				} catch(Exception e) {e.printStackTrace()}
				
				if (!cmm_id) return sm.notEnoughVars("cmm_id="+par["POST"]["cmm_id"])                                  
				try {
					commit = commitTable.getFromID(cmm_id)
					collection = commit.cmm_doc.doc_collection
				} catch(Exception e) {}
				if (!collection)
					return sm.collectionNotFound()

				if (!collectionTable.canWrite(user, collection))
					return sm.insufficientPermissions()

				def res
                try {
					res = commit.deleteThisFromDB()
				
				} catch(Exception e) {
					errorlog.error "Error_deleting_commit: "+e.printStackTrace()
					return sm.statusMessage(-1,"Error_deleting_commit: "+e.getMessage())
				}
				return sm.statusMessage(res, i18n.servermessage['ok'][lang])
            }   
			return sm.unknownAction()
        }
    }
}