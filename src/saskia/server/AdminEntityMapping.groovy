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

public class AdminEntityMapping extends WebServiceRestletMapping {

	Closure JSONanswer
	I18n i18n
	SaskiaDB db
	static Logger mainlog = Logger.getLogger("SaskiaServerMain")
	static Logger errorlog = Logger.getLogger("SaskiaServerErrors")
	static Logger processlog = Logger.getLogger("SaskiaServerProcessing")

	public AdminEntityMapping(SaskiaDB db) {

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

			// core stuff
			String action = par["POST"]["do"] //show, update, etc
			String lang = par["POST"]["lg"]

			ServerMessage sm = new ServerMessage("AdminEntityMapping", lang, bind, session, processlog)

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

			User user = UserTable.getFromAPIKey(api_key)
			if (!user) return sm.userNotFound()
			if (!user.isEnabled()) return sm.userNotEnabled()
			// all Admin*Mappings must have this
			if (!user.isSuperUser()) return sm.noSuperUser()
			if (!action || !lang) return sm.notEnoughVars("do=$action, lg=$lang")
			sm.setAction(action)

			/******************************/
			/** 1.1 show - PAGE Entities **/
			/******************************/

			if (action == "list") {
				Map h
				try {
					sm.logProcessDebug "Querying Entities: limit $limit offset $offset column $column value $value"
					h = EntityTable.listEntities(limit, offset, column, value)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_entity_list'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_getting_entity_list"][lang]+": "+e.getMessage())
				}

				// you have to "JSONize" the NEs
				h.result.eachWithIndex{entity, i -> h.result[i] = entity.toMap() }
				return sm.statusMessageWithPubKey(0,h, user.usr_pub_key)
			}

			/***************************/
			/** 1.2 update a NE value **/
			/***************************/

			if (action == "update") {

				Long ent_id
				if (par["POST"]["id"])
					try {ent_id = Long.parseLong(par["POST"]["id"])}
					catch(Exception e) {}
				if (!ent_id) return sm.notEnoughVars("id=$ent_id")
				if (!column || !value) return sm.notEnoughVars("c=$column v=$value")

				EntityTable entity
				try {
					log.debug "Querying Entity with id $ent_id"
					entity = EntityTable.getFromID(ent_id)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_entity'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_getting_entity"][lang]+": "+e.getMessage())
				}

				int res = 0
				try {
					res = entity.updateValue(c, v)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_updating_entity'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_updating_entity"][lang]+": "+e.getMessage())
				}
				return sm.statusMessageWithPubKey(res, entity.toMap(), user.usr_pub_key)
			}

			/*********************************************/
			/** 1.3 delete - DELETE Entity (admin only) **/
			/*********************************************/

			if (action == "delete") {
				Long id
				try {id = Long.parseLong(par["POST"]["id"])
				}catch(Exception e) {}
				if (!id)  return sm.notEnoughVars("id=$id")

				EntityTable entity
				try {
					entity = EntityTable.getFromID(id)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_getting_entity'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_getting_entity"][lang]+": "+e.getMessage())
				}
				def res
				try {
					res = entity.removeThisFromDB()
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_deleting_entity'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_deleting_entity"][lang]+": "+e.getMessage())
				}

				//RETURNS 1 IF UPDATED
				return sm.statusMessageWithPubKey(res, i18n.servermessage['ok'][lang], user.usr_pub_key)
			}

			/***************************************/
			/** 1.4 create **/
			/***************************************/

			if (action == "create") {

				String ent_name = par["POST"]["ent_name"]
				String ent_dbpedia_resource = par["POST"]["ent_dbpedia_resource"]
				String ent_dbpedia_class = par["POST"]["ent_dbpedia_class"]
				if (!ent_name || !ent_dbpedia_resource || !ent_dbpedia_class)
					return sm.notEnoughVars("$ent_name $ent_dbpedia_resource $ent_dbpedia_class")

				EntityTable ent = new EntityTable(ent_name:ent_name, ent_dbpedia_resource:ent_dbpedia_resource,
						ent_dbpedia_class:ent_dbpedia_class)
				try {
					log.debug "Adding Entity"
					ent.ent_id = ent.addThisToDB()
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error_creating_entity'][lang]+": "+e.printStackTrace()
					return sm.statusMessage(-1, i18n.servermessage["error_creating_entity"][lang]+": "+e.getMessage())
				}

				//RETURNS 1 IF UPDATED
				return sm.statusMessageWithPubKey(0, ent.toMap(), user.usr_pub_key)
			}
			return sm.unknownAction()
		}
	}
}