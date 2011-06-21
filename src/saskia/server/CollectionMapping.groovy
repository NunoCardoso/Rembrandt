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

import saskia.db.obj.*
import saskia.db.table.*
import saskia.stats.SaskiaStats
import saskia.util.I18n

public class CollectionMapping extends WebServiceRestletMapping {

	Closure JSONanswer
	I18n i18n
	SaskiaStats stats
	static Logger mainlog = Logger.getLogger("SaskiaServerMain")
	static Logger errorlog = Logger.getLogger("SaskiaServerErrors")
	static Logger processlog = Logger.getLogger("SaskiaServerProcessing")

	public CollectionMapping() {

		i18n = I18n.newInstance()

		JSONanswer = {req, par, bind ->

			long session = System.currentTimeMillis()
			processlog.debug "Session $session triggered with $par"

			int limit
			long offset
			def column, value

			String action = par["POST"]["do"] //show, update, etc
			String lang = par["POST"]["lg"]

			ServerMessage sm = new ServerMessage("CollectionMapping", lang, bind, session, processlog)

			String api_key = par["POST"]["api_key"]
			if (!api_key) api_key = par["COOKIE"]["api_key"]
			if (!api_key) return sm.noAPIKeyMessage()

			User user = UserTable.getFromAPIKey(api_key)
			if (!user) return sm.userNotFound()
			if (!user.isEnabled()) return sm.userNotEnabled()
			if (!action || !lang) return sm.notEnoughVars(lang, "do=$action, lg=$lang")
			sm.setAction(action)

			/***********************************/
			/** 1.1 list - only for this user **/
			/***********************************/

			if (par["POST"]["l"]) limit = Integer.parseInt(par["POST"]["l"])
			if (!limit) limit = 10
			if (par["POST"]["o"]) offset = Long.parseLong(par["POST"]["o"])
			if (!offset) offset = 0
			if (par["POST"]["c"]) column = par["POST"]["c"]
			if (par["POST"]["v"]) value = par["POST"]["v"]

			/*****************************************/
			/** 1.1.1 list-own - only for this user **/
			/*****************************************/

			if (action == "list-own") {

				Map h
				try {
					h = Collection.listOwnCollectionsForUser(user, limit, offset, column, value)
				}  catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_col_list'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_getting_col_list'][lang]+": "+e.getMessage(), action)
				}

				h.result.eachWithIndex{col, i -> h.result[i] = col.toMap()}
				return sm.statusMessage(0, h)
			}

			/*****************************************/
			/** 1.1.2 list-own - only for this user **/
			/*****************************************/

			if (action == "list-admin") {

				Map h
				try {
					h = Collection.listAdminableCollectionsForUser(user, limit, offset, column, value)
				}  catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_col_list'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_getting_col_list'][lang]+": "+e.getMessage(), action)
				}

				h.result.eachWithIndex{col, i -> h.result[i] = col.toMap()}
				return sm.statusMessage(0, h)
			}

			/*******************************************/
			/** 1.1.3 list-write - only for this user **/
			/*******************************************/

			if (action == "list-write") {

				Map h
				try {
					h = Collection.listWritableCollectionsForUser(user, limit, offset, column, value)
				}  catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_col_list'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_getting_col_list'][lang]+": "+e.getMessage(), action)
				}

				h.result.eachWithIndex{col, i -> h.result[i] = col.toMap()}
				return sm.statusMessage(0, h)
			}

			/*****************************************/
			/** 1.1.4 list-read - only for this user **/
			/*****************************************/

			if (action == "list-read") {

				Map h
				try {
					h = Collection.listReadableCollectionsForUser(user, limit, offset, column, value)
				}  catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_col_list'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_getting_col_list'][lang]+": "+e.getMessage(), action)
				}

				h.result.eachWithIndex{col, i -> h.result[i] = col.toMap()}
				return sm.statusMessage(0, h)
			}

			/********************/
			/** 1.1.5 list-all **/
			/********************/

			if (action == "list-all") {

				Map h
				try {
					h = Collection.listAllCollectionsForUser(user, limit, offset, column, value)
				}  catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_col_list'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_getting_col_list'][lang]+": "+e.getMessage(), action)
				}

				h.result.eachWithIndex{col, i -> h.result[i] = col.toMap()}
				h.max_number_collections_owned = user.usr_max_number_collections
				h.collections_owned = Collection.collectionsOwnedBy(user)
				return sm.statusMessage(0, h)
			}

			/***************/
			/** 1.2 show **/
			/***************/	
			if (action == 'show') {

				long id // id from a collection
				if (par["POST"]["id"]) id = Long.parseLong(par["POST"]["id"] )
				if (!id) return sm.notEnoughVars("id=$id")

				Collection collection = Collection.getFromID(id)
				if (!collection) return sm.collectionNotFound()
				if (!Collection.canRead(user, collection))
					return sm.statusMessage(-1, i18n.servermessage['insufficient_privileges'][lang])


				Map m = collection.toMap()
				m['number_sdocs'] = collection.getNumberOfSourceDocuments()
				m['number_rdocs'] = collection.getNumberOfRembrandtedDocuments()

				return sm.statusMessage(0,m)
			}

			/*************************************************************************/
			/** 1.3 update - MODIFY A VALUE - only for users with admin capabilities */
			/*************************************************************************/

			if (action == "update") {

				long id // id from a collection
				if (par["POST"]["id"]) id = Long.parseLong(par["POST"]["id"] )
				if (!id) return sm.notEnoughVars("id=$id")

				// let's check permissions
				Collection collection = Collection.getFromID(id)
				if (!collection) return sm.collectionNotFound()
				if (!Collection.canAdmin(user, collection))
					return sm.statusMessage(-1, i18n.servermessage['no_collection_admin'][lang])

				if (value == null || !column) return sm.notEnoughVars("v=$value, c=$column")

				int res = 0

				try {
					res = collection.updateValue(column, value)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_updating_collection'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_updating_collection'][lang]+": " +e.getMessage())
				}

				//RETURNS 1 IF UPDATED
				return sm.statusMessage(res, collection.toMap())
			}


			/*********************************************************/
			/** 1.4 create - CREATE NEW COLLECTION - for simple users */
			/** I can create collections for me only */ 
			/*********************************************************/

			if (action == "create") {

				String col_name =  par["POST"]["col_name"]
				String col_lang =  par["POST"]["col_lang"]
				String col_comment =  par["POST"]["col_comment"]
				String col_permission =  par["POST"]["col_permission"]
				User col_owner = user

				if (!col_name) return sm.notEnoughVars("col_name=$col_name")
				if (!col_permission) return sm.notEnoughVars("col_permission=$col_permission")

				Collection collection = Collection.getFromNameAndOwner(col_name, col_owner)
				if (collection) return sm.statusMessage(-1, i18n.servermessage['collection_already_exists'][lang])

				if (!Collection.canHaveANewCollection(col_owner)) {
					return sm.statusMessage(-1, i18n.servermessage['user_not_allowed_to_create_more_collection'][lang],
					"Max: "+col_owner.usr_max_number_collections)
				}
				try {
					collection = new Collection(col_name:col_name, col_comment:col_comment,
							col_owner:col_owner, col_lang:col_lang, col_permission:col_permission)
					collection.col_id = collection.addThisToDB()
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_creating_collection'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_creating_collection'][lang]+": "+e.getMessage())
				}

				// leave like that, to include an id field
				return sm.statusMessage(0, collection.toMap())
			}

			/************************************/
			/** 1.5 delete - DELETE collection **/
			/************************************/

			if (action == "delete") {
				long id
				if (par["POST"]["id"])
					try {id = Long.parseLong(par["POST"]["id"] )}
					catch(Exception e) {}
				if (!id) return sm.notEnoughVars("id=$id")

				// let's check permissions
				Collection collection = Collection.getFromID(id)
				if (!collection) return sm.collectionNotFound()
				if (!Collection.canAdmin(user, collection))
					return sm.statusMessage(-1, i18n.servermessage['no_collection_admin'][lang])

				int res = -1
				try {
					res = collection.removeThisFromDB()
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_deleting_collection'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_deleting_collection"][lang]+": "+e.getMessage())
				}
				//RETURNS 1 IF UPDATED
				return sm.statusMessage(res, i18n.servermessage['ok'][lang])
			}

			/************************************************************************/
			/**  1.6 refreshcache - REFRESH STATS COLLECTION - only for ADMIN users */
			/************************************************************************/

			if (action == "refreshcache") {

				long id
				if (par["POST"]["id"])
					try {id = Long.parseLong(par["POST"]["id"] )}
					catch(Exception e) {}
				if (!id) return sm.notEnoughVars("id=$id")

				// let's check permissions
				Collection collection = Collection.getFromID(id)
				if (!collection) return sm.collectionNotFound()
				if (!Collection.canAdmin(user, collection))
					return sm.statusMessage(-1, i18n.servermessage['no_collection_admin'][lang])

				stats = new SaskiaStats()
				try {
					stats.forceRefreshCacheOnFrontPage(collection, lang)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_creating_cache_for_collection'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_creating_cache_for_collection'][lang]+": " +e.getMessage())
				}
				sm.statusMessage(0, i18n.servermessage['ok'][lang])
			}

			return sm.unknownAction(action)
		}
	}
}