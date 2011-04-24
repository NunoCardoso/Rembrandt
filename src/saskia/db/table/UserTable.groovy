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

package saskia.db.table

import org.apache.log4j.*

import saskia.db.database.SaskiaDB
import saskia.db.obj.User

/** This class is an interface for the RembrandtTag table in the WikiRembrandt database. 
 * It stores tagging information associated to a Rembrandt annotation of documents.
 * Static methods are used to return results from DB, using where clauses.
 * Class methods are used to insert results to DB.  
 */
class UserTable extends DBTable {

	static String tablename = "user"

	static Logger log = Logger.getLogger("User")

	// cache for user info
	Map <Long,User> cacheIDUser
	Map <String,User> cacheAPIKeyUser

	public UserTable(SaskiaDB db) {
		super(db)
		cacheIDUser = [:]
		cacheAPIKeyUser = [:]

	}

	public List<User> queryDB(String query, ArrayList params = []) {
		List l = []
		db.getDB().eachRow(query, params, {row  ->
			l << User.createNew(this, row)
		})
		return (l ? l : null)
	}

	/**
	 * Load the internal cache for users
	 */
	public void refreshUserCache() {
		List l = queryDB("SELECT * FROM ${tablename}")
		l.each{
			cacheIDUser[it.usr_id] = it
			cacheAPIKeyUser[it.usr_api_key] = it
		}
	}

	public HashMap listUsersForAdminUser(limit = 0,  offset = 0, column = null, needle = null) {
		// limit & offset can come as null... they ARE initialized...

		if (!cacheIDUser) refreshUserCache()
		List<User> res = []

		if (column && needle) {
			if (column == "usr_id") {
				User usr = cacheIDUser[Long.parseLong(needle)]
				if (usr) res << usr
			} else {
				Closure pattern
				switch (type[column]) {
					case 'String': pattern = {it."${column}" =~ /(?i)${needle}/}; break
					case 'Boolean': pattern = {it."${column}" == Boolean.parseBoolean(needle)}; break
					case 'Integer': pattern = {it."${column}" == Integer.parseInt(needle)}; break
					case 'Long': pattern = {it."${column}" == Long.parseLong(needle)}; break
				}
				res.addAll(cacheIDUser.values().toList().findAll(pattern))
			}
		} else {
			res = cacheIDUser.values().toList()
		}

		List<User> res2

		if (limit != 0) {
			int lim = (offset+limit-1)
			if (lim > (res.size()-1) ) lim = res.size()-1
			res2 = res[offset..lim]
		}	else {
			res2 = res
		}
		return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
			"result":res2, "column":column, "value":needle]
	}

	public boolean isSuperUserByAPIKey(String api_key) {
		if (!cacheIDUser) refreshUserCache()
		return getFromAPIKey(api_key).isSuperUser()
	}



	public User getFromLogin(String login) {
		if (!login) return null
		if (!cacheIDUser) refreshUserCache()
		return cacheIDUser.values().find{it.usr_login == login}
	}

	public User getFromID(long id) {
		if (!id) return null
		if (!cacheIDUser) refreshUserCache()
		return cacheIDUser[id]
	}

	static User getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("UserTable").getFromID(id)
	}

	public User getFromEmail(String email) {
		if (!email) return null
		if (!cacheIDUser) refreshUserCache()
		return cacheIDUser.values().find{it.usr_email == email}
	}

	static User getFromEmail(SaskiaDB db, String email) {
		return  db.getDBTable("UserTable").getFromEmail(email)
	}

	public User getFromAPIKey(String api_key) {
		if (!api_key) return null
		if (!cacheIDUser) refreshUserCache()
		return cacheAPIKeyUser[api_key]
	}

	static User getFromAPIKey(SaskiaDB db, String api_key) {
		return  db.getDBTable("UserTable").getFromAPIKey(api_key)
	}

	public User getFromTempAPIKey(String tmp_api_key) {
		if (!tmp_api_key) return null
		if (!cacheIDUser) refreshUserCache()
		return cacheIDUser.values().find{it.usr_tmp_api_key == tmp_api_key}
	}

	static User getFromTempAPIKey(SaskiaDB db, String tmp_api_key) {
		return  db.getDBTable("UserTable").getFromTempAPIKey(tmp_api_key)
	}

	static String createPassword(String password) {
		return renoir.util.MD5Hex.digest(password)
	}

}