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

import java.util.Map;

import org.apache.log4j.Logger

/** This class is an interface for the RembrandtTag table in the WikiRembrandt database. 
  * It stores tagging information associated to a Rembrandt annotation of documents.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  */
class Collection  {

	static String col_table = "collection"
	Long col_id
	String col_name
	User col_owner
	String col_lang
	String col_permission
	String col_comment
	
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("SaskiaDB")
	static Map<Long,Collection> cacheIDCollection = [:]

   static Map type = ['col_id':'Long', 'col_name':'String', 'col_owner':'User',
     'col_lang':'String', 'col_permission':'String', 'col_comment':'String'] 

	// cache on user-colllection itens
	static Map<Long,Map> cacheIDuoc = [:] // first ID is the usr_id, second ID is the col_id
	// values: 
	// uoc_can_read : true or false
	// uoc_can_write : true or false
	// uoc_can_admin : true or false
	
	static List queryDB(String query, ArrayList params) {
	    List l = []
	    Collection c = new Collection()
		db.getDB().eachRow(query, params, {row  -> 
			c = new Collection()
			c.col_id = row['col_id']
			c.col_name = row['col_name']
			c.col_owner = User.getFromID(row['col_owner'])
			c.col_lang = row['col_lang']
			c.col_permission = row['col_permission']	
			c.col_comment = row['col_comment']	
			l << c
		})
		return l
	}

	Map toMap() {
		return ["col_id":col_id, "col_name":col_name, 
		"col_owner":col_owner.toSimpleMap(), "col_lang":col_lang,
		'col_permission':col_permission, "col_comment":col_comment]
	}

	Map toSimpleMap() {
		return ["col_id":col_id, "col_name":col_name]
	}
	
   static Map<Long,Collection> getAllCollections() {
	    if (!cacheIDCollection) refreshCache()
		 return cacheIDCollection
	}

	/**
	 * Load the internal cache for collections
	 */
	static void refreshCache() {
	    List l = queryDB("SELECT * FROM ${col_table}".toString(), [])
	    l.each{cacheIDCollection[it.col_id] = it}
	}
	
	/**
    * Load the internal cache for users
    */
   static Map listAccessibleCollectionsForUser(User user) {
 
       if (cacheIDuoc.containsKey(user.usr_id)) return cacheIDuoc[user.usr_id]
       cacheIDuoc[user.usr_id] = [:]
 		 Map collections = Collection.getAllCollections() 
		 List usergroups = user.getGroups()
				
		collections.each{col_id, col -> 
			               
			boolean canread = false
			boolean canwrite = false					
			boolean canadmin = false
			boolean own = false
			
			List ownergroups = col.col_owner.getGroups()
			List commongroups = ownergroups.intersect(usergroups)
			
			if (col.col_permission =~ /......r../ || 
				(col.col_permission =~ /...r...../ && !commongroups.isEmpty()) || 
				(col.col_permission =~ /r......../ && col.col_owner.usr_id == user.usr_id) ) {
					canread = true
			}
			if (col.col_permission =~ /.......w./ || 
				(col.col_permission =~ /....w..../ && !commongroups.isEmpty()) || 
				(col.col_permission =~ /.w......./ && col.col_owner.usr_id == user.usr_id) ) {
					canwrite = true
			}
			if (col.col_permission =~ /........a/ || 
				(col.col_permission =~ /.....a.../ && !commongroups.isEmpty()) || 
				(col.col_permission =~ /..a....../ && col.col_owner.usr_id == user.usr_id) ) {
					canadmin = true
			} 
			if (col.col_owner.usr_id == user.usr_id) {
				own = true
			}
			
			if (canread || canwrite || canadmin || own) {
				if (!cacheIDuoc[user.usr_id].containsKey(col_id)) cacheIDuoc[user.usr_id][col_id] = [:]
				cacheIDuoc[user.usr_id][col_id].uoc_own = own 
         	cacheIDuoc[user.usr_id][col_id].uoc_can_read = canread
         	cacheIDuoc[user.usr_id][col_id].uoc_can_write = canwrite
         	cacheIDuoc[user.usr_id][col_id].uoc_can_admin = canadmin 
		   }      				
		}			
      return cacheIDuoc[user.usr_id]
   }
    
	static List filterFromColumnAndNeedle(List haystack, column = null, needle = null) {
	    
	    if (column && needle) {
		
			if (column == "col_id") {
		    	return haystack.findAll{it.col_id == Long.parseLong(needle)}
		    	
	   	} else if (column == "col_owner") {
				return haystack.findAll{it.col_owner.usr_id == Long.parseLong(needle)}
			}	else {
	         // The rest of the colums are String
	         return haystack.findAll{it."${column}" =~ /(?i)${needle}/}
	      }
	    } 
	    return haystack
	}
	
	static List<Collection> filterFromLimitAndOffset( List<Collection> res, int limit = 0, long offset = 0) {
	   List<Collection> res2
		if (res.isEmpty()) return res
		 
	// limit 0 means all of them - don't filter
	  if (limit != 0) {
	       int lim = (offset+limit-1)
			if (lim > (res.size()-1) ) lim = res.size()-1
			res2 = res[offset..lim]
	   }	else {
			res2 = res
	   }
	   return res2
	}
	 
	/** 
	 * Get the collection list with an optional colum/needle, page them with a limit/offset
	 * All for admin user (that is, no user filtering)
	 */
	static HashMap listCollectionForAdminUser( limit = 0, offset = 0, column, needle) {	

	    List<Collection> res = filterFromColumnAndNeedle(Collection.getAllCollections().values().toList(), column, needle)         
	    List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)	   
	    return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
	            "result":res2, "column":column, "value":needle]
	}	
	
	/** 
	 * Get the collection list with an optional colum/needle, page them with a limit/offset
	 * All for a user (that is, with user filtering), and that the user can read
	 */
	static HashMap listReadableCollectionForUser(User user, limit = 0, offset = 0, column = null, needle = null) {
	  
		Map l = listAccessibleCollectionsForUser(user)
		List<Collection> haystack = l.findAll{col_id, perms -> perms.uoc_can_read == true}
			.collect{col_id, perms -> Collection.getFromID(col_id)}	    		
	   List<Collection> res = filterFromColumnAndNeedle(haystack, column, needle)      
	   List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)	
	    
	   return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
	            "result":res2, "column":column, "value":needle]
	}
	
	/** 
	 * same as above, but filtered to writable collections for user
	 */
	static HashMap listWritableCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {
	    
		Map l = listAccessibleCollectionsForUser(user)
		List<Collection> haystack = l.findAll{col_id, perms -> perms.uoc_can_write == true}
			.collect{col_id, perms -> Collection.getFromID(col_id)}	    		
	   List<Collection> res = filterFromColumnAndNeedle(haystack, column, needle)      
	   List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)	
	    
	   return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
	            "result":res2, "column":column, "value":needle]
	}
	
	/** 
	 * same as above, but filtered to showable collections for user
	 */
	static HashMap listAdminableCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {
	    
	   Map l = listAccessibleCollectionsForUser(user)
		List<Collection> haystack = l.findAll{col_id, perms -> perms.uoc_can_admin == true}
			.collect{col_id, perms -> Collection.getFromID(col_id)}	    		
	   List<Collection> res = filterFromColumnAndNeedle(haystack, column, needle)      
	   List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)	
	    
	   return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
	            "result":res2, "column":column, "value":needle]
	}
	
	/** 
	 * same as above, but filtered to showable collections for user
	 */
	static HashMap listOwnCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {
	    
	    List<Collection> haystack = getAllCollections().values().toList().findAll{it.col_owner.equals(user)}
		 List<Collection> res = filterFromColumnAndNeedle(haystack, column, needle)      
	    List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)	
	    
	    return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
	            "result":res2, "column":column, "value":needle]
	}
	
	/** 
	 * same as above, but filtered to showable collections for user
	 */
	static HashMap listAllCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {
	    
	    Map l = listAccessibleCollectionsForUser(user)
	    List<Collection> haystack = l.collect{col_id, perms -> Collection.getFromID(col_id)}
	    List<Collection> res = filterFromColumnAndNeedle(haystack, column, needle)      
	    List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)	
	 
		 def res3 = []
		 // from the final list, let's collect perms
		 res2.each{col -> res3 << l[col.col_id]}
		
		 return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
	            "result":res2, "perms":res3, "column":column, "value":needle]
	}
	
	static boolean canRead(User user, Collection collection) {
		if (!user) return null
		Map res = listAccessibleCollectionsForUser(user)
		return (res[collection.col_id]? res[collection.col_id]?.uoc_can_read : false)
	}
        
	static boolean canWrite(User user, Collection collection) {
            if (!user) return null
            Map res = listAccessibleCollectionsForUser(user)
            return (res[collection.col_id]? res[collection.col_id]?.uoc_can_write : false)	    
		}
	
	static boolean canAdmin(User user, Collection collection) {
            if (!user) return null
            Map res = listAccessibleCollectionsForUser(user)
            return (res[collection.col_id]? res[collection.col_id]?.uoc_can_admin : false) 
      }
	
	static boolean canHaveANewCollection(User user) {           
            return user.usr_max_number_collections > collectionsOwnedBy(user)	
     }

	static Map getPermissionsFromUserOnCollection(User user, Collection collection) {
            if (!user || !collection) return null
            Map res = listAccessibleCollectionsForUser(user)
            return res[collection.col_id]
	} 
	
	// deprecated. Allow collections with same name, as long as from different users
	static Collection getFromName(String name) {
		if (!name) return null   
		if (!cacheIDCollection) refreshCache()
		List c = cacheIDCollection.values().toList().findAll{it.col_name==name}
		log.debug "Querying for collection $name got Collection $c." 
		if (c) return c[0] else return null
	}
	
	static Collection getFromNameAndOwner(String name, User user) {
		if (!name) return null   
		if (!cacheIDCollection) refreshCache()
		List c = cacheIDCollection.values().toList().findAll{it.col_name==name && it.col_owner.equals(user)}
		log.debug "Querying for collection $name got Collection $c." 
		if (c) return c[0] else return null
	}
	
	
	static getFromID(Long id) {
		// version has UNIQUE key
		if (!id) return null
		if (!cacheIDCollection) refreshCache()
		Collection c = cacheIDCollection[id]
		log.debug "Querying for collection $id got Collection $c." 
		if (c) return c else return null
	}
	 
	public updateValue(String column, value) {  
		 def newvalue	    
		 def object
	    switch (type[column]) {
	        case 'String': newvalue = value; break
	        case 'Long': newvalue = Long.parseLong(value); break
	        case 'User': newvalue = Long.parseLong(value); object = User.getFromID(newvalue); break // value is usr_id
	    }        
	    def res = db.getDB().executeUpdate("UPDATE ${col_table} SET ${column}=? WHERE col_id=?",[newvalue, col_id])
	    // if we have a User (object), add it to cache
		 cacheIDCollection[col_id][column] = (object ? object : newvalue)
	    return res
	}
    
    static int collectionsOwnedBy(User user) {
    	Map collections = listAccessibleCollectionsForUser(user)
    	return collections.findAll{it.value.ouc_own == true}.size()
	}
	/* 
	 * Returns the number of REMBRANDTed documents for this collection 
	 */
	public int getNumberOfRembrandtedDocuments() {
	    int i
	    db.getDB().eachRow("SELECT count(doc_id) from ${RembrandtedDoc.tablename} "+
		    "WHERE doc_collection=?",[col_id], {row -> i = row[0]})
	    return i
	}
    
	/* 
	 * Returns the number of REMBRANDTed documents for this collection 
	 */
	public int getNumberOfSourceDocuments() {
	    int i
	    db.getDB().eachRow("SELECT count(sdoc_id) from ${SourceDoc.tablename} "+
		    "WHERE sdoc_collection=?",[col_id], {row -> i = row[0]})
	    return i
	}
	
	// used to add new collections.
	public long addThisToDB() {	
	   def res = db.getDB().executeInsert("INSERT INTO ${col_table} VALUES(0,?,?,?,?,?)", 
	      [col_name, col_owner.usr_id, col_lang, col_permission, col_comment])
		// returns an auto_increment value
	       col_id = (long)res[0][0]
	       cacheIDCollection[col_id] = this                      
		return col_id
	}	
    
	
	public removeThisFromDB() {	
	    def res = db.getDB().executeUpdate("DELETE FROM ${col_table} where col_id=?",[col_id]) 
	    cacheIDCollection.remove(col_id)
	    return res
	}
    
	public String toString() {
		return "${col_id}:${col_lang}:${col_name}"
	}
}