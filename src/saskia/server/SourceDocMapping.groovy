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

import org.apache.log4j.*

import saskia.db.database.SaskiaDB
import saskia.db.obj.*
import saskia.db.table.*
import saskia.util.I18n

public class SourceDocMapping extends WebServiceRestletMapping {

	Closure JSONanswer
	I18n i18n
	static Logger mainlog = Logger.getLogger("SaskiaServerMain")
	static Logger errorlog = Logger.getLogger("SaskiaServerErrors")
	static Logger processlog = Logger.getLogger("SaskiaServerProcessing")
	SaskiaDB db

	public SourceDocMapping(SaskiaDB db) {

		this.db = db
		i18n = I18n.newInstance()
		UserTable userTable = db.getDBTable("UserTable")
        CollectionTable collectionTable = db.getDBTable("CollectionTable")
        SourceDocTable sourceDocTable = db.getDBTable("SourceDocTable")

		JSONanswer = {req, par, bind ->
			long session = System.currentTimeMillis()
			processlog.debug "Session $session triggered with $par"

			int limit
			long offset
			def column, value

			// core stuff
			String action = par["POST"]["do"]
			String lang = par["POST"]["lg"]

			ServerMessage sm = new ServerMessage("SourceDocMapping", lang, bind, session, processlog)

			// pager stuff
			if (par["POST"]["l"]) limit = Integer.parseInt(par["POST"]["l"])
			if (!limit) limit = 10
			if (par["POST"]["o"]) offset = Long.parseLong(par["POST"]["o"])
			if (!offset) offset = 0
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
			/** 1.1  List SDOCS **/
			/*********************/

			if (action == "list") {
				Map h
				Long collection_id
				Collection collection
				try {
					collection_id = Long.parseLong(par["POST"]["ci"])
				} catch(Exception e) {e.printStackTrace()}
				if (!collection_id) return sm.notEnoughVars("ci=$ci")
				collection = collectionTable.getFromID(collection_id)
				if (!collection) return sm.noCollectionFound()

				// check permissions
				if (!collectionTable.canRead(user, collection))
					return sm.statusMessage(-1, i18n.servermessage['insufficient_permissions'][lang])

				try {
					h = collection.listSourceDocs(limit, offset, column, value)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_sdoc_list'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_getting_sdoc_list"][lang]+": "+e.getMessage())
				}
				// h already has col_id
				h.result.eachWithIndex{sdoc, i -> h.result[i] = sdoc.toListMap() }
				h.perms = collectionTable.getPermissionsFromUserOnCollection(user, collection)
				h.col_id=collection_id
				return sm.statusMessage(0, h)

			}


			/***************************/
			/** 1.2 show SDOC content **/
			/***************************/

			if (action == "show") {

				Long doc_id
				String format
				if (par["POST"]["sdoc_id"])
					try {doc_id = Long.parseLong(par["POST"]["sdoc_id"])}
					catch(Exception e) {}
				if (par["POST"]["format"]) format = par["POST"]["format"]
				if (!doc_id) return sm.notEnoughVars("sdoc_id="+par["POST"]["sdoc_id"])

				SourceDoc doc
				try {
					doc = sourceDocTable.getFromID(doc_id)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_sdoc_list'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_getting_sdoc_list"][lang]+": "+e.getMessage())
				}
				// check permissions
				if (!collectionTable.canRead(user, doc.sdoc_collection))
					return sm.statusMessage(-1, i18n.servermessage['insufficient_permissions'][lang])

				return sm.statusMessage(0, doc.toShowMap())
			}

			/*********************/
			/** 1.3 update SDOC **/
			/*********************/

			if (action == "update") {

				Long doc_id
				String format
				if (par["POST"]["doc_id"])
					try {doc_id = Long.parseLong(par["POST"]["doc_id"])}
					catch(Exception e) {}
				if (par["POST"]["format"]) format = par["POST"]["format"]
				if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")

				if (!collectionTable.canRead(user, collection))
					return sm.statusMessage(-1, i18n.servermessage['insufficient_permissions'][lang])

				SourceDoc doc
				try {
					doc = sourceDocTable.getFromID(doc_id)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_sdoc_list'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_getting_sdoc_list"][lang]+": "+e.getMessage())
				}
				// check permissions
				if (!collectionTable.canAdmin(user, doc.sdoc_collection))
					return sm.statusMessage(-1, i18n.servermessage['insufficient_permissions'][lang])

				switch(column) {

					// let's handle this alone, because it's a shared table

					case "sdoc_collection":
						return sm.statusMessage(-1, "Change collection for sdoc is not allowed")
						break

					default:
						int res = 0
						try {
							res =doc.updateValue(c, v)
						} catch(Exception e) {
							errorlog.error i18n.servermessage['error_updating_sdoc'][lang]+": "+e.printStackTrace()
							return sm.statusMessage(-1, i18n.servermessage["error_updating_sdoc"][lang]+": "+e.getMessage())
						}
						return sm.statusMessage(res, doc.toShowMap())
				}
			}

			/*********************/
			/** 1.4 delete SDOC **/
			/*********************/

			if (action == "delete") {
				Long doc_id
				if (par["POST"]["doc_id"])
					try {doc_id = Long.parseLong(par["POST"]["doc_id"])}
					catch(Exception e) {}
				if (!doc_id) return sm.notEnoughVars("doc_id=$doc_id")

				SourceDoc doc
				try {
					doc = sourceDocTable.getFromID(doc_id)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_sdoc'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_getting_sdoc"][lang]+": "+e.getMessage())
				}
				// check permissions
				if (!collectionTable.canAdmin(user, doc.sdoc_collection))
					return sm.statusMessage(-1, i18n.servermessage['insufficient_permissions'][lang])

				def res
				try {
					res = doc.deleteThisFromDB()
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_deleting_sdoc'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_deleting_sdoc"][lang]+": "+e.getMessage())
				}

				//RETURNS 1 IF UPDATED
				return sm.statusMessage(res, i18n.servermessage['ok'][lang])
			}


			/*********************/
			/** 1.5 create SDOC **/
			/*********************/

			if (action == "create") {
				// required: sdoc_original_id, sdoc_collection, sdoc_lang, sdoc_content, sdoc_comment, sdoc_date
				// sdoc_content will then be converted to sdoc_webstore
				// generated: sdoc_proc

				String sdoc_original_id =  par["POST"]["sdoc_original_id"]
				Long sdoc_collection
				try {sdoc_collection = Long.parseLong(par["POST"]["sdoc_collection"])}
				catch(Exception e) {}

				String sdoc_lang =  par["POST"]["sdoc_lang"]
				String sdoc_content =  par["POST"]["sdoc_content"]
				String sdoc_comment =  par["POST"]["sdoc_comment"]
				Date sdoc_date// = par["POST"]["sdoc_date"]

				if (!sdoc_original_id) return sm.notEnoughVars("sdoc_original_id=$sdoc_original_id")
				Collection collection = collectionTable.getFromID(sdoc_collection)
				if (!collection) return sm.collectionNotFound()
				if (!sdoc_lang) return sm.notEnoughVars("sdoc_lang=$sdoc_lang")
				if (!sdoc_content) return sm.notEnoughVars("sdoc_content=$sdoc_content")
				if (!sdoc_comment) return sm.notEnoughVars("sdoc_comment=$sdoc_comment")
				if (!sdoc_date) sdoc_date = new Date()

				// check permissions
				if (!collectionTable.canAdmin(user, collection))
					return sm.statusMessage(-1, i18n.servermessage['insufficient_permissions'][lang])

				int number_sdocs = collection.getNumberOfSourceDocuments()
				if (user.usr_max_docs_per_collection < number_sdocs)
					return sm.statusMessage(-1, i18n.servermessage['max_number_documents_per_collection_reached'][lang])

				try {
					SourceDoc rdoc = new SourceDoc(
							sdoc_original_id:sdoc_original_id, sdoc_collection:scollection,
							sdoc_content:sdoc_content, sdoc_lang:sdoc_lang, sdoc_date:sdoc_date,
							sdoc_comment:sdoc_comment)

					sdoc.doc_id = sdoc.addThisToDB()
					sdoc.changeProcStatusInDBto(DocStatus.READY) // mark it so next time we'll not use it
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_creating_sdoc'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_creating_sdoc'][lang]+": "+e.getMessage())
				}

				// leave like that, to include an id field
				return sm.statusMessage(0, sdoc.toShowMap())
			}
			return sm.unknownAction()
		}
	}

}
