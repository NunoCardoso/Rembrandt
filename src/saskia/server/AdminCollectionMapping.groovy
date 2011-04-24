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
import saskia.stats.SaskiaStats
import saskia.util.I18n

public class AdminCollectionMapping extends WebServiceRestletMapping {

	Closure JSONanswer
	I18n i18n
	SaskiaStats stats
	SaskiaDB db
	static Logger mainlog = Logger.getLogger("SaskiaServerMain")
	static Logger errorlog = Logger.getLogger("SaskiaServerErrors")
	static Logger processlog = Logger.getLogger("SaskiaServerProcessing")

	public AdminCollectionMapping(SaskiaDB db) {

		this.db = db
		i18n = I18n.newInstance()
		CollectionTable collectionTable = db.getDBTable("CollectionTable")
		UserTable userTable = db.getDBTable("UserTable")

		JSONanswer = {req, par, bind ->

			long session = System.currentTimeMillis()
			processlog.debug "Session $session triggered with $par"

			int limit
			long offset
			def column, value

			String action = par["POST"]["do"] //show, update, etc
			String lang = par["POST"]["lg"]

			ServerMessage sm = new ServerMessage("AdminCollectionMapping", lang, bind, session, processlog)

			if (par["POST"]["l"]) limit = Integer.parseInt(par["POST"]["l"])
			if (!limit) limit = 0
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
			// all Admin*Mappings must have this
			if (!user.isSuperUser()) return sm.noSuperUser()
			if (!action || !lang) return sm.notEnoughVars("do=$action, lg=$lang")
			sm.setAction(action)

			/***************************************************/
			/** 1.1 show - PAGE COLLECTIONS - for ADMIN users **/
			/***************************************************/

			if (action == "list") {
				Map h
				try {
					h = collectionTable.listCollectionForAdminUser(limit, offset, column, value)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_col_list'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_getting_col_list'][lang]+": "+e.getMessage(), action)
				}

				//log.debug "Collections: $collections"
				h.result.eachWithIndex{col, i ->  h.result[i] = col.toMap()}
				return sm.statusMessageWithPubKey(0, h, user.usr_pub_key)
			}

			/***************************************************/
			/** 1.2 update - MODIFY A VALUE - for ADMIN users */
			/***************************************************/

			if (action == "update") {

				//requires the column and value parameter
				// the id is from a collection
				long id
				if (par["POST"]["id"])
					try {id = Long.parseLong(par["POST"]["id"] )}
					catch(Exception e) {}
				if (!id) return sm.notEnoughVars("id=$id")

				// let's check permissions
				Collection collection = collectionTable.getFromID(id)
				if (!collection) return sm.collectionNotFound()
				if (value == null || !column) return sm.notEnoughVars("v=$value, c=$column")

				int res = 0
				try {
					res = collection.updateValue(column, value)
				} catch(Exception e) {
					errorlog.error (i18n.servermessage['error_updating_collection'][lang]+": "+e.printStackTrace())
					return sm.statusMessage(-1, i18n.servermessage['error_updating_collection'][lang]+": " +e.getMessage())
				}

				//RETURNS 1 IF UPDATED
				return sm.statusMessageWithPubKey(res, collection.toMap(), user.usr_pub_key)
			}

			/************************************************************************/
			/**  1.3 refreshstats - REFRESH STATS COLLECTION - only for ADMIN users */
			/************************************************************************/

			if (action == "refreshstats") {

				long id
				if (par["POST"]["id"])
					try {id = Long.parseLong(par["POST"]["id"] )}
					catch(Exception e) {}
				if (!id) return sm.notEnoughVars("id=$id")

				// let's check permissions
				Collection collection = collectionTable.getFromID(id)
				if (!collection) return sm.collectionNotFound()
				stats = new SaskiaStats()
				try {
					stats.forceRefreshCacheOnFrontPage(collection, lang)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_creating_cache_for_collection'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_creating_cache_for_collection'][lang]+": " +e.getMessage())
				}
				sm.statusMessage(0, i18n.servermessage['ok'][lang])
			}


			/************************************************************/
			/** 1.4 create - CREATE NEW COLLECTION - for ADMIN users    */
			/** I can create collections for others, so let's add stuff */ 
			/************************************************************/

			if (action == "create") {

				String col_name =  par["POST"]["col_name"]
				String col_lang =  par["POST"]["col_lang"]
				String col_comment =  par["POST"]["col_comment"]
				String col_permission =  par["POST"]["col_permission"]
				Long col_owner
				try {col_owner = Long.parseLong(par["POST"]["col_owner"])}
				catch(Exception e) {}
				User owner = User.getFromID(col_owner)

				if (!col_name) return sm.notEnoughVars("col_name=$col_name")
				if (!col_permission) return sm.notEnoughVars("col_permission=$col_permission")
				if (!owner) return sm.userNotFound("col_owner=$col_owner")

				Collection collection = collectionTable.getFromNameAndOwner(col_name, col_owner)
				if (collection) return sm.statusMessage(-1, i18n.servermessage['collection_already_exists'][lang])

				if (!collectionTable.canHaveANewCollection(col_owner)) {
					return sm.statusMessage(-1, i18n.servermessage['user_not_allowed_to_create_more_collection'][lang],
					"Max: "+owner.usr_max_number_collections+ "Now: "+owner.collectionsOwned())
				}
				try {
					collection = new Collection(col_name:col_name, col_comment:col_comment,
							col_owner:owner, col_lang:col_lang, col_permission:col_permission)
					collection.setDBTable(collectionTable)
					collection.col_id = collection.addThisToDB()
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_creating_collection'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage['error_creating_collection'][lang]+": "+e.getMessage())
				}

				// leave like that, to include an id field
				return sm.statusMessageWithPubKey(0, collection.toMap(), user.usr_pub_key)
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
				Collection collection = collectionTable.getFromID(id)
				if (!collection) return sm.collectionNotFound()

				def res
				try {
					res = collection.removeThisFromDB()
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_deleting_collection'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_deleting_collection"][lang]+": "+e.getMessage())
				}
				//RETURNS 1 IF UPDATED
				return sm.statusMessageWithPubKey(res, i18n.servermessage['ok'][lang], user.usr_pub_key)
			}
			return sm.unknownAction(action)
		}
	}
}