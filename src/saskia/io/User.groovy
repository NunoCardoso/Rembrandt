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

package saskia.io

import org.apache.log4j.*

/** This class is an interface for the RembrandtTag table in the WikiRembrandt database. 
  * It stores tagging information associated to a Rembrandt annotation of documents.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  */
class User {

	static String usr_table = "user"
	    
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
        
	static HashMap guests = ["en":"Guest","pt":"Convidado"]
	static String guest = "guest" 
	
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("SaskiaDB")

	// cache for user info
	static Map <Long,User> cacheIDUser = [:]
	static Map <String,User> cacheAPIKeyUser = [:]
	                                                                        
	static List<User> queryDB(String query, ArrayList params = []) {
		List l = []
		User u = new User()
		db.getDB().eachRow(query, params, {row  -> 
		u = new User()
		u.usr_id = row['usr_id']
		u.usr_login = row['usr_login']
		u.usr_enabled = row['usr_enabled'] 
		u.usr_groups = row['usr_groups']
		u.usr_superuser = row['usr_superuser']
		u.usr_firstname = row['usr_firstname']
		u.usr_lastname = row['usr_lastname']
		u.usr_email = row['usr_email']
		u.usr_password = row['usr_password']
		u.usr_api_key = row['usr_api_key']				
		u.usr_tmp_password = row['usr_tmp_password']
		u.usr_tmp_api_key = row['usr_tmp_api_key']
		u.usr_pub_key = row['usr_pub_key']
		u.usr_max_number_collections = row['usr_max_number_collections']
		u.usr_max_number_tasks = row['usr_max_number_tasks']
		u.usr_max_docs_per_collection = row['usr_max_docs_per_collection']
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
 		l << u
	   })
   	   return (l ? l : null)
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
   /**
    * Load the internal cache for users
    */
        static void refreshUserCache() {
            List l = queryDB("SELECT * FROM ${usr_table}")
            l.each{ 
					cacheIDUser[it.usr_id] = it 
					cacheAPIKeyUser[it.usr_api_key] = it
				}
        }
        
        public void enableUser() {
	    	usr_enabled=true
	    	cacheIDUser[usr_id].usr_enabled=true
	    	db.getDB().executeUpdate("UPDATE ${usr_table} set usr_enabled=1 where usr_id=?", [usr_id])
		}
	
	   public void generatePubKey() {
			
	    	String s = renoir.util.MD5Hex.digest(usr_login+System.currentTimeMillis())
	    	cacheIDUser[usr_id].usr_pub_key=s
	    	db.getDB().executeUpdate("UPDATE ${usr_table} set usr_pub_key=? where usr_id=?", [s, usr_id])
		}
		
		/** get my groups in a nice way */
		 
			List getGroups() {
			 if (!usr_groups) return null
			 return usr_groups.split(";").findAll{it != ""} 
			}
        
        static HashMap listUsersForAdminUser(limit = 0,  offset = 0, column = null, needle = null) {
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
 	
	static boolean isSuperUserByAPIKey(String api_key) {
	    if (!cacheIDUser) refreshUserCache()
	    return getFromAPIKey(api_key).isSuperUser()
	}
	
	public static boolean isGuestUser(String user, String lang) {
	    println "user $user guests[lang]=${guests[lang]}"
	    return guests[lang] == user
	}
	
	public boolean isSuperUser() {
	    return usr_superuser
	}
	
	public boolean isEnabled() {
	    return usr_enabled
	}		
	
	static User getFromLogin(String login) {
		if (!login) return null
		if (!cacheIDUser) refreshUserCache()
		return cacheIDUser.values().find{it.usr_login == login}
        }
    
	static User getFromID(long id) {
		if (!id) return null
      if (!cacheIDUser) refreshUserCache()
		return cacheIDUser[id]
   }
    
	static User getFromEmail(String email) {
		if (!email) return null
		if (!cacheIDUser) refreshUserCache()
		return cacheIDUser.values().find{it.usr_email == email}
	}
	
	static User getFromAPIKey(String api_key) {
		if (!api_key) return null
		if (!cacheIDUser) refreshUserCache()
		return cacheAPIKeyUser[api_key]
	}
    	
	static User getFromTempAPIKey(String tmp_api_key) {
	    if (!tmp_api_key) return null
	    if (!cacheIDUser) refreshUserCache()
	    return cacheIDUser.values().find{it.usr_tmp_api_key == tmp_api_key}
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
            cacheIDUser[usr_id].usr_current_daily_api_calls = 0
            cacheIDUser[usr_id].usr_date_last_api_call = d
            db.getDB().executeUpdate("UPDATE ${usr_table} set usr_date_last_api_call=NOW(), "+
            "usr_current_daily_api_calls=0 where usr_id=?", [usr_id])        
        }
        
        int addAPIcount() {
			//	println toMap()
            usr_current_daily_api_calls++
            usr_total_api_calls++
            cacheIDUser[usr_id].usr_current_daily_api_calls++
            cacheIDUser[usr_id].usr_total_api_calls++
            db.getDB().executeUpdate("UPDATE ${usr_table} set usr_current_daily_api_calls="+
            "usr_current_daily_api_calls + 1, usr_total_api_calls = usr_total_api_calls + 1, "+
            "usr_date_last_api_call=NOW() where usr_id=?", [usr_id])
            return usr_current_daily_api_calls
        }
        
 
		
	public void insertTempPassword(String password) {
	    db.getDB().executeUpdate("UPDATE ${usr_table} set usr_tmp_password=? where usr_id=?",
		   [password, usr_id])
            cacheIDUser[usr_id].usr_tmp_password=password
	}
	
	public void insertTempAPIKey(String tmp_api_key) {
	    db.getDB().executeUpdate("UPDATE ${usr_table} set usr_tmp_api_key=? where usr_id=?",
		[tmp_api_key, usr_id])
            cacheIDUser[usr_id].usr_tmp_api_key = tmp_api_key
	}
	
	public void updatePasswordAndAPIKeyFromTemp() {
	    db.getDB().executeUpdate("UPDATE ${usr_table} set usr_password=usr_tmp_password, "+
		"usr_api_key=usr_tmp_api_key where usr_id=?", [usr_id])
            cacheIDUser[usr_id].usr_password = cacheIDUser[usr_id].usr_tmp_password
            cacheAPIKeyUser[usr_tmp_api_key] = cacheAPIKeyUser[usr_api_key]
				cacheAPIKeyUser.remove(usr_api_key)
            cacheIDUser[usr_id].usr_api_key = cacheIDUser[usr_id].usr_tmp_api_key  

	}
	
	public void updatePassword(String password) {
	    db.getDB().executeUpdate("UPDATE ${usr_table} set usr_password=? "+
		" where usr_id=?", [password, usr_id])
            cacheIDUser[usr_id].usr_password = password
	}
    
	static int deleteUser(long usr_id) {
	    if (!usr_id) return null
		 User u = User.getFromID(usr_id)
		 return u?.removeThisFromDB()
	}
	
	public removeThisFromDB() {
		 def res = db.getDB().executeUpdate("DELETE FROM ${usr_table} WHERE usr_id=?", [usr_id])
	    cacheIDUser.remove(usr_id)
	    Collection.cacheIDuoc.remove(usr_id)	  
	    return res	    
	}
	
	static updateValue(long usr_id, String column, newvalue) {
	    User usr = new User()
            def el = usr."$column"
            def newval
            if (el instanceof String) newval = newvalue
            else if (el instanceof Boolean) newval = Boolean.parseBoolean(newvalue)
            else if (el instanceof Integer) newval = Integer.parseInt(newvalue)
            else if (el instanceof Long) newval = Long.parseLong(newvalue)
            
	    def res = db.getDB().executeUpdate("UPDATE ${usr_table} SET ${column}=? WHERE usr_id=?",[newval, usr_id])
	    cacheIDUser[usr_id][column] = newval
		 if (column == "usr_api_key") {
			 cacheAPIKeyUser.remove(usr.usr_api_key)
			 usr.usr_api_key = newval
			 cacheAPIKeyUser[newval] = usr
		 }
	    return res
	}
	
	public long addThisToDB() {		
	    def res = db.getDB().executeInsert("INSERT INTO ${usr_table} VALUES(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 
		    [usr_login, usr_enabled, usr_groups, usr_superuser, usr_firstname, usr_lastname, usr_email, usr_password, 
		     usr_tmp_password, usr_api_key, usr_tmp_api_key, usr_pub_key, usr_max_number_collections,
		     usr_max_docs_per_collection, usr_max_daily_api_calls, usr_current_daily_api_calls,
		     usr_total_api_calls, usr_date_last_api_call] )
            usr_id = (long)res[0][0]     
	    cacheIDUser[usr_id] = this
	    // returns an auto_increment value
	    return usr_id
	}	
	
	public boolean equals(User user) {
		return this.usr_id == user.usr_id
	}
	
	public String toString() {
		return "${usr_id}:${usr_login}"
	}
}