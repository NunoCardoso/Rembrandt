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

import saskia.db.database.SaskiaMainDB
import saskia.db.obj.Collection
import saskia.db.table.UserTable
import saskia.db.obj.User
import saskia.stats.SaskiaStats
import saskia.util.I18n

public class AdminStatsMapping extends WebServiceRestletMapping {

	Closure JSONanswer
	I18n i18n
	static Logger mainlog = Logger.getLogger("SaskiaServerMain")
	static Logger errorlog = Logger.getLogger("SaskiaServerErrors")
	static Logger processlog = Logger.getLogger("SaskiaServerProcessing")
	SaskiaMainDB db

	/** Note: this mapping should be used only by superuser folks *managing user stuff*, so 
	 * it requires an api_key. For standard uses, the UserMapping is the one that has standard actions.
	 */
	public AdminStatsMapping() {

		i18n = I18n.newInstance()
		db = SaskiaMainDB.newInstance()

		JSONanswer = {req, par, bind ->

			long session = System.currentTimeMillis()
			processlog.debug "Session $session triggered with $par"

			Collection collection

			// core stuff
			String action = par["POST"]["do"] //show, update, etc
			String lang = par["POST"]["lg"]

			ServerMessage sm = new ServerMessage("AdminStatsMapping", lang, bind, session, processlog)

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

			/***************************************/
			/** 1.1 show - STATS for collectionid **/
			/**************************************/

			if (action == "show") {
				long col_id
				try {
					col_id = Long.parseLong(par["POST"]["ci"])
				} catch(Exception e){}

				if (!col_id) return sm.notEnoughVars("ci=$col_id")
				collection = Collection.getFromID(col_id)

				Map dates = Cache.getFrontPageCacheDates(db, collection)
				Map h = [:]
				h['cache_dates'] = dates
				SaskiaStats stats = new SaskiaStats()
				h['message'] = stats.renderFrontPage(collection.col_name, lang)
				return sm.statusMessageWithPubKey(0,h, user.usr_pub_key)
			}

			return sm.unknownAction(action)
		}
	}
}
