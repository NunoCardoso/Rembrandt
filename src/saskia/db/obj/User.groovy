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
package saskia.db.obj

import saskia.db.table.DBTable

import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 *
 */
class User extends DBObject implements JSONable {

	static Logger log = Logger.getLogger("User")
	static HashMap guests = ["en":"Guest","pt":"Convidado"]
	static String guest = "guest"

	Long usr_id
	String usr_login
	Boolean usr_enabled
	String usr_groups
	Boolean usr_superuser
	String usr_firstname
	String usr_lastname
	String usr_email
	String usr_password
	String usr_tmp_password
	String usr_api_key
	String usr_tmp_api_key
	String usr_pub_key
	Integer usr_max_number_collections
	Integer usr_max_number_tasks
	Integer usr_max_docs_per_collection
	Integer usr_max_daily_api_calls
	Integer usr_current_daily_api_calls
	Long usr_total_api_calls
	Date usr_date_last_api_call

	static Map type = ['usr_id':'Long',
		'usr_login':'String', 'usr_enabled':'Boolean',
		'usr_groups':'String','usr_superuser':'Boolean',
		'usr_superuser':'String', 'usr_firstname':'String',
		'usr_lastname':'String', 'usr_email':'String',
		'usr_password':'String', 'usr_tmp_password':'String',
		'usr_api_key':'String', 'usr_tmp_api_key':'String','usr_pub_key':'String',
		'usr_max_number_collections':'String','usr_max_number_collections':'String',
		'usr_max_docs_per_collection':'Integer',
		'usr_max_daily_api_calls':'Integer', 'usr_current_daily_api_calls':'Integer',
		'usr_total_api_calls':'Long','usr_date_last_api_call':'Date']

	public User(DBTable dbtable) {
		super(dbtable)
	}

	static User createNew(DBTable dbtable, row) {
		User u = new User(dbtable)
		if (row['usr_id']) u.usr_id = row['usr_id']
		if (row['usr_login']) u.usr_login = row['usr_login']
		if (row['usr_enabled']) u.usr_enabled = row['usr_enabled']
		if (row['usr_groups']) u.usr_groups = row['usr_groups']
		if (row['usr_superuser']) u.usr_superuser = row['usr_superuser']
		if (row['usr_firstname']) u.usr_firstname = row['usr_firstname']
		if (row['usr_lastname']) u.usr_lastname = row['usr_lastname']
		if (row['usr_email']) u.usr_email = row['usr_email']
		if (row['usr_password']) u.usr_password = row['usr_password']
		if (row['usr_api_key']) u.usr_api_key = row['usr_api_key']
		if (row['usr_tmp_password']) u.usr_tmp_password = row['usr_tmp_password']
		if (row['usr_tmp_api_key']) u.usr_tmp_api_key = row['usr_tmp_api_key']
		if (row['usr_pub_key']) u.usr_pub_key = row['usr_pub_key']
		if (row['usr_max_number_collections']) u.usr_max_number_collections = row['usr_max_number_collections']
		if (row['usr_max_number_tasks']) u.usr_max_number_tasks = row['usr_max_number_tasks']
		if (row['usr_max_docs_per_collection']) u.usr_max_docs_per_collection = row['usr_max_docs_per_collection']
		if (row['usr_max_daily_api_calls'])
			u.usr_max_daily_api_calls = row['usr_max_daily_api_calls']

		if (row['usr_current_daily_api_calls'])
			u.usr_current_daily_api_calls = row['usr_current_daily_api_calls']

		if (u.usr_current_daily_api_calls == null)
			u.usr_current_daily_api_calls = 0

		if (row['usr_total_api_calls'])
			u.usr_total_api_calls = row['usr_total_api_calls']
		if (row['usr_date_last_api_call'])
			u.usr_date_last_api_call = (Date)(row['usr_date_last_api_call'])
		return u
	}


	public Map toMap() {
		return ['usr_id':usr_id, 'usr_login':usr_login,
			'usr_enabled':usr_enabled,
			'usr_groups':usr_groups,
			'usr_firstname':usr_firstname, 'usr_lastname':usr_lastname,
			'usr_email':usr_email, 'usr_api_key':usr_api_key,
			'usr_max_number_collections':usr_max_number_collections,
			'usr_max_number_tasks':usr_max_number_tasks,
			'usr_max_docs_per_collection':usr_max_docs_per_collection,
			'usr_max_daily_api_calls':usr_max_daily_api_calls,
			'usr_current_daily_api_calls':usr_current_daily_api_calls,
			'usr_total_api_calls':usr_total_api_calls,
			'usr_date_last_api_call':usr_date_last_api_call
		]
	}

	public Map toSimpleMap() {
		return ['usr_id':usr_id, 'usr_login':usr_login]
	}


	public static boolean isGuestUser(String user, String lang) {
		//		println "user $user guests[lang]=${guests[lang]}"
		return guests[lang] == user
	}

	public void enableUser() {
		usr_enabled=true
		getDBTable().cacheIDUser[usr_id].usr_enabled=true
		getDBTable().getSaskiaDB().getDB().executeUpdate("UPDATE ${getDBTable().tablename} set usr_enabled=1 where usr_id=?", [usr_id])
	}

	public void generatePubKey() {

		String s = renoir.util.MD5Hex.digest(usr_login+System.currentTimeMillis())
		getDBTable().cacheIDUser[usr_id].usr_pub_key=s
		getDBTable().getSaskiaDB().getDB().executeUpdate("UPDATE ${getDBTable().tablename} set usr_pub_key=? where usr_id=?", [s, usr_id])
	}

	/** get my groups in a nice way */

	List getGroups() {
		if (!usr_groups) return null
		return usr_groups.split(";").findAll{it != ""}
	}

	public boolean isSuperUser() {
		return usr_superuser
	}

	public boolean isEnabled() {
		return usr_enabled
	}

	boolean canExecuteAPICall() {
		GregorianCalendar now = new GregorianCalendar()
		GregorianCalendar lastAPIcall = new GregorianCalendar()
		lastAPIcall.time = (usr_date_last_api_call ? usr_date_last_api_call : new Date(0))
		if ( (now.get(Calendar.DAY_OF_MONTH) != lastAPIcall.get(Calendar.DAY_OF_MONTH) ) ||
		(now.get(Calendar.MONTH) != lastAPIcall.get(Calendar.MONTH) )  ||
		(now.get(Calendar.YEAR) != lastAPIcall.get(Calendar.YEAR) ) ) {
			// set new day, reset the API
			resetAPIdailyCounter()
			return true
		} else {
			return (usr_current_daily_api_calls < usr_max_daily_api_calls)
		}
	}

	void resetAPIdailyCounter() {
		Date d = new Date()
		usr_current_daily_api_calls = 0
		usr_date_last_api_call = d
		getDBTable().cacheIDUser[usr_id].usr_current_daily_api_calls = 0
		getDBTable().cacheIDUser[usr_id].usr_date_last_api_call = d
		getDBTable().getSaskiaDB().getDB().executeUpdate("UPDATE ${getDBTable().tablename} set usr_date_last_api_call=NOW(), "+
				"usr_current_daily_api_calls=0 where usr_id=?", [usr_id])
	}

	int addAPIcount() {
		//	println toMap()
		usr_current_daily_api_calls++
		usr_total_api_calls++
		getDBTable().cacheIDUser[usr_id].usr_current_daily_api_calls++
		getDBTable().cacheIDUser[usr_id].usr_total_api_calls++
		getDBTable().getSaskiaDB().getDB().executeUpdate("UPDATE ${getDBTable().tablename} set usr_current_daily_api_calls="+
				"usr_current_daily_api_calls + 1, usr_total_api_calls = usr_total_api_calls + 1, "+
				"usr_date_last_api_call=NOW() where usr_id=?", [usr_id])
		return usr_current_daily_api_calls
	}



	public void insertTempPassword(String password) {
		getDBTable().getSaskiaDB().getDB().executeUpdate("UPDATE ${getDBTable().tablename} set usr_tmp_password=? where usr_id=?",
				[password, usr_id])
		getDBTable().cacheIDUser[usr_id].usr_tmp_password=password
	}

	public void insertTempAPIKey(String tmp_api_key) {
		getDBTable().getSaskiaDB().getDB().executeUpdate("UPDATE ${getDBTable().tablename} set usr_tmp_api_key=? where usr_id=?",
				[tmp_api_key, usr_id])
		getDBTable().cacheIDUser[usr_id].usr_tmp_api_key = tmp_api_key
	}

	public void updatePasswordAndAPIKeyFromTemp() {
		getDBTable().getSaskiaDB().getDB().executeUpdate("UPDATE ${getDBTable().tablename} set usr_password=usr_tmp_password, "+
				"usr_api_key=usr_tmp_api_key where usr_id=?", [usr_id])
		getDBTable().cacheIDUser[usr_id].usr_password = getDBTable().cacheIDUser[usr_id].usr_tmp_password
		getDBTable().cacheAPIKeyUser[usr_tmp_api_key] = getDBTable().cacheAPIKeyUser[usr_api_key]
		getDBTable().cacheAPIKeyUser.remove(usr_api_key)
		getDBTable().cacheIDUser[usr_id].usr_api_key = getDBTable().cacheIDUser[usr_id].usr_tmp_api_key
	}

	public void updatePassword(String password) {
		getDBTable().getSaskiaDB().getDB().executeUpdate("UPDATE ${getDBTable().tablename} set usr_password=? "+
				" where usr_id=?", [password, usr_id])
		getDBTable().cacheIDUser[usr_id].usr_password = password
	}


	public updateValue(String column, newvalue) {
		User usr = new User()
		def el = usr."$column"
		def newval
		if (el instanceof String) newval = newvalue
		else if (el instanceof Boolean) newval = Boolean.parseBoolean(newvalue)
		else if (el instanceof Integer) newval = Integer.parseInt(newvalue)
		else if (el instanceof Long) newval = Long.parseLong(newvalue)

		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
			"UPDATE ${getDBTable().tablename} SET ${column}=? WHERE usr_id=?",
			[newval, usr_id])
		getDBTable().cacheIDUser[usr_id][column] = newval
		if (column == "usr_api_key") {
			getDBTable().cacheAPIKeyUser.remove(usr.usr_api_key)
			usr.usr_api_key = newval
			getDBTable().cacheAPIKeyUser[newval] = usr
		}
		return res
	}

	public Long addThisToDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert("INSERT INTO ${getDBTable().tablename} "+
				"VALUES(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				[
					usr_login,
					usr_enabled,
					usr_groups,
					usr_superuser,
					usr_firstname,
					usr_lastname,
					usr_email,
					usr_password,
					usr_tmp_password,
					usr_api_key,
					usr_tmp_api_key,
					usr_pub_key,
					usr_max_number_collections,
					usr_max_number_tasks,
					usr_max_docs_per_collection,
					usr_max_daily_api_calls,
					usr_current_daily_api_calls,
					usr_total_api_calls,
					usr_date_last_api_call]
				)
		usr_id = (long)res[0][0]
		getDBTable().cacheIDUser[usr_id] = this
		log.info "Inserted User into DB: ${this}"
		return usr_id
	}

	public int removeThisFromDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
			"DELETE FROM ${getDBTable().tablename} WHERE usr_id=?", 
			[usr_id])
		getDBTable().cacheIDUser.remove(usr_id)
		getDBTable().getSaskiaDB().getInstance("CollectionTable").cacheIDuoc.remove(usr_id)
		log.info "Removed User ${this} into DB, got res $res"
		return res
	}

	public boolean equals(User user) {
		return this.usr_id == user.usr_id
	}

	public String toString() {
		return "${usr_id}:${usr_login}"
	}
}
