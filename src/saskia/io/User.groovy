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
	static String uoc_table = "user_on_collection"
	    
	Long usr_id
	String usr_login
	Boolean usr_enabled
	Boolean usr_superuser
	String usr_firstname
	String usr_lastname
	String usr_email
	String usr_password
	String usr_tmp_password 
	String usr_api_key
	String usr_tmp_api_key
	Integer usr_max_number_collections
	Integer usr_max_docs_per_collection
	Integer usr_max_daily_api_calls
	Integer usr_current_daily_api_calls
        Long usr_total_api_calls
        Date usr_date_last_api_call
        
        static Map type = ['usr_id':'Long', 'usr_login':'String', 'usr_enabled':'Boolean',
          'usr_superuser':'Boolean', 'usr_superuser':'String', 'usr_firstname':'String',
          'usr_lastname':'String', 'usr_email':'String', 'usr_password':'String',
          'usr_tmp_password':'String', 'usr_api_key':'String', 'usr_tmp_api_key':'String',
          'usr_max_number_collections':'String','usr_max_docs_per_collection':'Integer',
          'usr_max_daily_api_calls':'Integer', 'usr_current_daily_api_calls':'Integer',
          'usr_total_api_calls':'Long','usr_date_last_api_call':'Date'] 
        
	static HashMap guests = ["en":"Guest","pt":"Convidado"]
	static String guest = "guest" 
	
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("SaskiaDB")

	// cache for user info
	static Map <Long,User> cacheIDUser = [:]
 	
	// cache on user-colllection itens
	static Map<Long,Map> cacheIDuoc = [:] // first ID is the usr_id, second ID is the col_id
	// values: 
	// uoc_can_read : true or false
	// uoc_can_write : true or false
	// uoc_can_admin : true or false
	                                                                        
	static List<User> queryDB(String query, ArrayList params = []) {
	    List l = []
	    User u = new User()
	    db.getDB().eachRow(query, params, {row  -> 
		u = new User()
		u.usr_id = row['usr_id']
		u.usr_login = row['usr_login']
		u.usr_enabled = row['usr_enabled'] 
		u.usr_superuser = row['usr_superuser']
		u.usr_firstname = row['usr_firstname']
		u.usr_lastname = row['usr_lastname']
		u.usr_email = row['usr_email']
		u.usr_password = row['usr_password']
		u.usr_api_key = row['usr_api_key']				
		u.usr_tmp_password = row['usr_tmp_password']
		u.usr_tmp_api_key = row['usr_tmp_api_key']
		u.usr_max_number_collections = row['usr_max_number_collections']
		u.usr_max_docs_per_collection = row['usr_max_docs_per_collection']
                if (row['usr_max_daily_api_calls']) 
                     u.usr_max_daily_api_calls = row['usr_max_daily_api_calls']
                if (row['usr_current_daily_api_calls']) 
                     u.usr_current_daily_api_calls = row['usr_current_daily_api_calls']
                if (row['usr_current_daily_api_calls']) 
                     u.usr_current_daily_api_calls = row['usr_current_daily_api_calls']
                if (row['usr_total_api_calls']) 
                     u.usr_total_api_calls = row['usr_total_api_calls']
                if (row['usr_date_last_api_call']) 
                     u.usr_date_last_api_call = (Date)(row['usr_date_last_api_call'])
 		l << u
	   })
   	   return (l ? l : null)
	}
    
        /**
         * Load the internal cache for users
         */
        static void refreshUserCache() {
            List l = queryDB("SELECT * FROM ${usr_table}")
            l.each{  cacheIDUser[it.usr_id] = it }
        }
        
        public void enableUser() {
	    usr_enabled=true
	    cacheIDUser[usr_id].usr_enabled=true
	    db.getDB().executeUpdate("UPDATE ${usr_table} set usr_enabled=1 where usr_id=?", [usr_id])
	}
	
	/**
         * Load the internal cache for users
         */
        static Map getUserOnCollectionInfoForUser(long usr_id) {
            //println "getUserOnCollectionInfoForUser called for $usr_id"
            if (cacheIDuoc.containsKey(usr_id)) return cacheIDuoc[usr_id]
            cacheIDuoc[usr_id] = [:]
            db.getDB().eachRow("SELECT * FROM ${uoc_table} WHERE uoc_user=?",[usr_id], {row -> 
                long col_id = row['uoc_collection']
                if (!cacheIDuoc[usr_id].containsKey(col_id)) cacheIDuoc[usr_id][col_id] = [:]
                cacheIDuoc[usr_id][col_id].uoc_own = row['uoc_own'] 
                cacheIDuoc[usr_id][col_id].uoc_can_read = row['uoc_can_read'] 
                cacheIDuoc[usr_id][col_id].uoc_can_write = row['uoc_can_write'] 
                cacheIDuoc[usr_id][col_id].uoc_can_admin = row['uoc_can_admin']                
         }) 
         return cacheIDuoc[usr_id]
        }
        
        static HashMap getUsers( limit = 0,  offset = 0, column = null, needle = null) {
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
	    User u = cacheIDUser.values().toList().find{it.usr_api_key == api_key}
	    return u.usr_superuser
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
		
	static setUserCollectionPermissions(long usr_id, long col_id, String column, newvalue) {
	    def res = db.getDB().executeUpdate("UPDATE ${uoc_table} SET ${column}=? WHERE uoc_user=? and uoc_collection=?",
		    [newvalue, usr_id, col_id])
            if (!cacheIDuoc[usr_id]) cacheIDuoc[usr_id] = [:]
            if (!cacheIDuoc[usr_id][col_id])  cacheIDuoc[usr_id][col_id] = [:]
            cacheIDuoc[usr_id][col_id].'$column' = newvalue
	    return res
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
            return cacheIDUser.values().find{it.usr_api_key == api_key}
	}
    	
	static User getFromTempAPIKey(String tmp_api_key) {
	    if (!tmp_api_key) return null
	    if (!cacheIDUser) refreshUserCache()
	    return cacheIDUser.values().find{it.usr_tmp_api_key == tmp_api_key}
       }
    
        int collectionsOwned() {
            if (!usr_id) return null
            int i = 0
            db.getDB().eachRow("SELECT count(*) from user_on_collection WHERE "+
		    "uoc_user=? and uoc_own = 1",[usr_id], {row -> i = row[i]})
            return i
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
            usr_current_daily_api_calls++
            usr_total_api_calls++
            cacheIDUser[usr_id].usr_current_daily_api_calls++
            cacheIDUser[usr_id].usr_total_api_calls++
            db.getDB().executeUpdate("UPDATE ${usr_table} set usr_current_daily_api_calls="+
            "usr_current_daily_api_calls + 1, usr_total_api_calls = usr_total_api_calls + 1, "+
            "usr_date_last_api_call=NOW() where usr_id=?", [usr_id])
            return usr_current_daily_api_calls
        }
        
        boolean canReadCollection(Collection collection) {
            if (!collection) return null
            Map res = getUserOnCollectionInfoForUser(usr_id)
            //println "res: $res"
            //Note: if there is nothing on user_on_collection, it's because user CAN'T DO ANYTHING.
            // so, return false if not found.
             return (res[collection.col_id]? res[collection.col_id]?.uoc_can_read : false)
        }
        
	boolean canWriteCollection(Collection collection) {
            if (!collection) return null
            Map res = getUserOnCollectionInfoForUser(usr_id)
            return (res[collection.col_id]? res[collection.col_id]?.uoc_can_write : false)	    
	}
	
	boolean canAdminCollection(Collection collection) {
            if (!collection) return null
            Map res = getUserOnCollectionInfoForUser(usr_id)
            return (res[collection.col_id]? res[collection.col_id]?.uoc_can_admin : false) 
        }
	
	boolean canCreateCollection() {           
            return usr_max_number_collections > collectionsOwned()       	
        }
	
	/* if null, uses all collections */
	public List<HashMap> getUserCollectionPermissionsOn(Collection collection = null) {
	    def res = []
      	    Map map = getUserOnCollectionInfoForUser(usr_id)
	    if (!collection) {
		map.each{col_id, perms-> 
		  Collection col = Collection.getFromID(col_id)
		  res << [id:col_id, col_name:col.col_name, own:perms.uoc_own, can_read:perms.uoc_can_read, 
			can_write:perms.uoc_can_write, can_admin:perms.uoc_can_admin]
		}  
	    } else {
		res << [id:collection.col_id, col_name:col.col_name, own:perms.uoc_own, can_read:map[col_id].uoc_can_read, 
		        can_write:map[col_id].uoc_can_write, can_admin:map[col_id].uoc_can_admin]
	    }
	    return (res ? res: null)
	}
	
	/* if null, uses all collections */
	public List<HashMap> getReadableCollections() {
	    List res = []
	    Map map = getUserOnCollectionInfoForUser(usr_id)
            map.findAll{col_id, perms -> perms.uoc_can_read}.each{col_id, perms -> 
               Collection collection = Collection.getFromID(col_id)
               res << ["col_id":collection.col_id,"col_lang":collection.col_lang,
                       "col_name":collection.col_name, "col_comment":collection.col_comment]
           }
	   return res 
	}
	
	public List<HashMap> getAllCollectionsForSuperUser() {
	    List res = []
	    if (!Collection.cacheIDCollection) Collection.refreshCache()
	    Collection.cacheIDCollection.each{col_id, collection -> 
               res << ["col_id":collection.col_id,"col_lang":collection.col_lang,
                       "col_name":collection.col_name, "col_comment":collection.col_comment]
           }
	   return res 
	}
	
	public List setNewPermissionsOnCollection() {
	    if (!usr_id) return null
	    List res = []
	    Map map = getUserOnCollectionInfoForUser(usr_id) // force it to have parm info on user 
	    db.getDB().executeInsert("INSERT INTO user_on_collection(uoc_user, uoc_collection, uoc_can_read) "+
		    "SELECT ${usr_id}, col_id, col_new_user_can_read from collection where col_new_user_can_read=1")
		db.getDB().eachRow("SELECT col_name, col_id FROM user_on_collection, collection WHERE "+
		    "col_id=uoc_collection and uoc_user=? and uoc_can_read=1", [usr_id], {row -> 
		    res << row[0] // col_name
		    long col_id = row[1]
                    if (!cacheIDuoc[usr_id]) cacheIDuoc[usr_id] = [:]
                    if (!cacheIDuoc[usr_id][col_id])  cacheIDuoc[usr_id][col_id] = [:]
                    cacheIDuoc[usr_id][col_id].uoc_can_read = 1
		})
		return res
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
		"usr_api_key=usr_tmp_api_key  where usr_id=?", [usr_id])
            cacheIDUser[usr_id].usr_password = cacheIDUser[usr_id].usr_tmp_password
            cacheIDUser[usr_id].usr_api_key = cacheIDUser[usr_id].usr_tmp_api_key           
	}
	
	public void updatePassword(String password) {
	    db.getDB().executeUpdate("UPDATE ${usr_table} set usr_password=? "+
		" where usr_id=?", [password, usr_id])
            cacheIDUser[usr_id].usr_password = password
	}
    
	static int deleteUser(long usr_id) {
	    if (!usr_id) return null
	    def res = db.getDB().executeUpdate("DELETE FROM ${usr_table} WHERE usr_id=?", [usr_id])
	    cacheIDUser.remove(usr_id)
	    cacheIDuoc.remove(usr_id)	  
	    println cacheIDUser
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
	    return res
	}
	
	public long addThisToDB() {		
	    def res = db.getDB().executeInsert("INSERT INTO ${usr_table} VALUES(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 
		    [usr_login, usr_enabled, usr_superuser, usr_firstname, usr_lastname, usr_email, usr_password, 
		     usr_tmp_password, usr_api_key, usr_tmp_api_key, usr_max_number_collections,
		     usr_max_docs_per_collection, usr_max_daily_api_calls, usr_current_daily_api_calls,
		     usr_total_api_calls, usr_date_last_api_call] )
            usr_id = (long)res[0][0]     
	    cacheIDUser[usr_id] = this
	    // returns an auto_increment value
	    return usr_id
	}	
	
	public String toString() {
		return "${usr_id}:${usr_login}"
	}
}