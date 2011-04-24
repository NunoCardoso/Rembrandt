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

import renoir.util.MD5Hex
import renoir.util.SHA1
import saskia.db.obj.Collection
import saskia.db.obj.User
import saskia.util.I18n

public class UserMapping extends WebServiceRestletMapping {

	Closure JSONanswer
	Closure HTMLanswer
	I18n i18n
	static Logger mainlog = Logger.getLogger("SaskiaServerMain")
	static Logger errorlog = Logger.getLogger("SaskiaServerErrors")
	static Logger processlog = Logger.getLogger("SaskiaServerProcessing")

	public UserMapping() {

		User user_db
		i18n = I18n.newInstance()

		JSONanswer = {req, par, bind ->

			long session = System.currentTimeMillis()
			processlog.debug "Session $session triggered with $par"

			def action
			if (par["POST"]["do"]) action = par["POST"]["do"]
			if (!action && par["GET"]["do"]) action = par["GET"]["do"]
			def lang = par["POST"]["lg"]

			ServerMessage sm = new ServerMessage("UserMapping-JSONanswer", lang, bind, session, processlog)

			// as this may come from requests of new users, no point on check apikeys now...

			/***************/
			/** 1.1 LOGIN **/
			/***************/	
			if (action == 'login') {

				String user = par["POST"]["u"]
				String password = par["POST"]["p"]
				try {
					user_db = User.getFromLogin(user)
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error'][lang]+": "+e.printStackTrace()
					returm sm.statusMessage(-1, i18n.servermessage['error'][lang], e.getMessage())
				}
				// no user found
				if (!user_db) return sm.userNotFound()
				if (user_db.usr_password != password)
					return sm.statusMessage(-1, i18n.servermessage['wrong_password'][lang])

				if (!user_db.usr_enabled)
					return sm.statusMessage(-1, i18n.servermessage['user_not_confirmed'][lang])

				Map m = user_db.toMap()

				// add something to the cookie, so that we can authenticate later at client-side
				if (user_db.isSuperUser()) {
					m['usr_pub_key'] = user_db.usr_pub_key
					m['usr_pub_key_decoder'] = renoir.util.MD5Hex.digest(user_db.usr_pub_key)
				}
				return sm.statusMessage(0, m)
			}


			/*****************/
			/** AUTH */
			/******************/	
			if (action == 'auth') {

				String password = par["POST"]["p"]
				String api_key = par["POST"]["api_key"]
				if (!api_key) api_key = par["COOKIE"]["api_key"]
				if (!api_key) return sm.noAPIKeyMessage()

				user_db = User.getFromAPIKey(api_key)
				if (!user_db) return sm.userNotFound()
				if (!user_db.isEnabled()) return sm.userNotEnabled()

				if (user_db.usr_password != password)
					return sm.statusMessage(-1, i18n.servermessage['wrong_password'][lang])

				return sm.statusMessage(0, 'OK')
			}



			/******************/
			/** 1.2 REGISTER **/
			/******************/	
			if (action == "register") { // or 'create'

				def user = par["POST"]["u"]
				def password = par["POST"]["p"]
				def firstname = par["POST"]["fn"]
				def lastname = par["POST"]["ln"]
				def email = par["POST"]["em"]
				user_db = User.getFromLogin(user)

				if (user_db) return sm.statusMessage(-1, i18n.servermessage['user_login_already_taken'][lang])

				User newuser = new User(usr_login:user, usr_password:password,
						usr_firstname:firstname, usr_lastname:lastname, usr_email:email,
						usr_api_key:SHA1.convert(user+firstname), usr_enabled:0,// user is NOT enabled... that's for the confirmation
						usr_max_number_collections:1, usr_max_docs_per_collection:100)
				try {
					newuser.usr_id = newuser.addThisToDB()
				} catch(Exception e) {
					errorlog.error i18n.servermessage['error'][lang]+": "+e.printStackTrace()
					returm sm.statusMessage(-1, i18n.servermessage['error'][lang], e.getMessage())
				}
				return sm.statusMessage(0, newuser.toMap())
			}

			/***************/
			/** 1.25 show **/
			/***************/	
			if (action == 'show') {

				// requires api_key now
				String api_key = par["POST"]["api_key"]
				if (!api_key) api_key = par["COOKIE"]["api_key"]
				if (!api_key) return sm.noAPIKeyMessage()

				User user = User.getFromAPIKey(api_key)
				if (!user) return sm.userNotFound()
				if (!user.isEnabled()) return sm.userNotEnabled()

				Map m = user.toMap()
				m.current_number_collections_owned = Collection.collectionsOwnedBy(user)
				return sm.statusMessage(0,m)
			}

			/**************************/
			/** 1.3 RECOVER PASSWORD **/
			/**************************/	

			if (action == "recoverpassword") {
				def email = par["POST"]["em"]
				user_db = User.getFromEmail(email)
				if (!user_db) return sm.statusMessage(-1,i18n.servermessage['email_not_found'][lang])

				// to randomly generate it, use SHA1
				def tmp_password = (SHA1.convert(""+new Date().getTime())).substring(0,8)
				def tmp_api_key = SHA1.convert(tmp_password+email)

				// note that to encode password, use MD5, because Javascript can do it too
				user_db.insertTempPassword(MD5Hex.digest(tmp_password))
				user_db.insertTempAPIKey(tmp_api_key)
				bind['status'] = 0
				bind['newpassword'] = tmp_password
				bind['tmp_api_key'] = tmp_api_key
				log2.info "$session UserMapping:$action: Returning $bind for user $user_db"
				return JSONHelper.toJSON(bind)
			}

			/*************************/
			/** 1.4 CHANGE PASSWORD **/
			/*************************/	

			if (action == "changepassword") {
				def user_login  = par["POST"]["u"]
				def oldpassword  = par["POST"]["op"]
				def newpassword  = par["POST"]["np"]
				user_db = User.getFromLogin(user_login)
				if (!user_db) return sm.userNotFound()
				if (user_db.usr_password != oldpassword)
					returm sm.statusMessage(-1,i18n.servermessage['old_password_dont_match'][lang])

				user_db.updatePassword(newpassword)
				return sm.statusMessage(0, i18n.servermessage['password_changed'][lang])
			}
			return sm.unknownAction()
		}

		HTMLanswer = {req, par, bind ->
			long session = System.currentTimeMillis()
			processlog.debug "Session $session triggered with $par"

			def action, tmp_api_key, api_key, message
			def lang = "en"

			action = par["GET"]["do"]
			if (par["GET"]["lg"]) lang = par["GET"]["lg"]

			ServerMessage sm = new ServerMessage("UserMapping-HTMLanswer", lang, bind, session, processlog)

			/*************************/
			/** 2.1 CONFIRM PASSWORD **/
			/*************************/	

			if (action == "confirmpassword") {
				tmp_api_key  = par["GET"]["tmp_api_key"]
				user_db = User.getFromTempAPIKey(tmp_api_key)

				if (!user_db) return i18n['user_not_found'][lang]
				user_db.updatePasswordAndAPIKeyFromTemp()
				message = i18n.servermessage['password_and_api_key_changed'][lang]
				sm.logProcessDebug "Updated password"
				return message
			}

			/*************************/
			/** 2.2 CONFIRM REGISTRATION **/
			/*************************/	

			if (action == "confirmregister") {
				api_key  = par["GET"]["a"]
				user_db = User.getFromAPIKey(api_key)

				if (!user_db) {
					message = i18n.servermessage['user_not_found'][lang]
				} else {
					user_db.enableUser()
					user_db.generatePubKey()
					message = i18n.servermessage['user_enabled'][lang]+". "
				}
				sm.logProcessDebug "Confirmed registration"
				return message
			}

			// for HTML requests that I do not want to handle:
			return "Action unknown"
		}
	}
}
