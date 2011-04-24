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

import saskia.db.obj.Collection;
import saskia.db.obj.DBObject;
import saskia.db.obj.JSONable;

/** This class is an interface for the Task table in the RembrandtPool database. 
CREATE TABLE `task` (
	 `tsk_id` BIGINT PRIMARY KEY AUTO_INCREMENT, 
    `tsk_user` BIGINT NOT NULL, 
    `tsk_collection` BIGINT NOT NULL, 
	 `tsk_type` CHAR(3) NOT NULL,
    `tsk_priority` INT DEFAULT 0,
    `tsk_limit` INT DEFAULT NULL,
    `tsk_offset` BIGINT DEFAULT NULL,
    `tsk_done` INT DEFAULT NULL,
    `tsk_scope` CHAR(3) DEFAULT NULL, 
    `tsk_persistence` CHAR(3) DEFAULT NULL,      
    `tsk_status` CHAR(3) DEFAULT NULL,
    `tsk_comment` varchar(255) DEFAULT NULL
);

  */
class Task extends DBObject implements JSONable {

	static String tablename = "task"
	Long tsk_id
	String tsk_task
	User tsk_user
	Collection tsk_collection
	String tsk_type // S2R, R2P, GEO, TIM
	Integer tsk_priority
	Integer tsk_limit
	Long tsk_offset
	Integer tsk_done
	String tsk_scope // BAT or SRV - BATCH (command-line executions) or SERVER (SaskiaTaskManager handles it)
	String tsk_persistence // TMP or PER - Temporary or Permanent
	String tsk_status // QUE, PRO, FIN, INT - Queued, processing, Finished, Interrupted,
	String tsk_comment
	static SaskiaDB db = SaskiaDB.newInstance()

	static Map type = ['tsk_id':'Long','tsk_task':'String', 'tsk_user':'User', 'tsk_collection':'Collection','tsk_type':'String',
	  'tsk_priority':'Integer','tsk_limit':'Integer','tsk_offset':'Long','tsk_done':'Integer','tsk_scope':'String',
	  'tsk_persistence':'String','tsk_status':'String','tsk_comment':'String']

	static Logger log = Logger.getLogger("SaskiaDB")
	
	static Map<Long,Task> cache = [:]

	static List<Task> queryDB(String query, ArrayList params) {
	    List<Task> res = []
	    db.getDB().eachRow(query, params, {row  -> 
	        Task t = new Task()
				t.tsk_id = row['tsk_id']
				t.tsk_task = row['tsk_task']
				t.tsk_user= User.getFromID(row['tsk_user'])
				t.tsk_collection = Collection.getFromID(row['tsk_collection'])
				t.tsk_type = row['tsk_type']
				t.tsk_priority = row['tsk_priority']
				t.tsk_limit = row['tsk_limit']
				t.tsk_offset = row['tsk_offset']
				t.tsk_done = row['tsk_done']
				t.tsk_scope = row['tsk_scope']
				t.tsk_persistence = row['tsk_persistence']
				t.tsk_status = row['tsk_status']
				t.tsk_comment = row['tsk_comment']
	    	res << t                    
	    })
	    return res
	}
	
	static List<Task> getAllTasks() {
		if (!cache) refreshCache()
		return cache.values().toList()
	}
	
   static List<Task> getAllTasksForUser(User user) {
		if (!cache) refreshCache()
		return cache.values().toList().findAll{it.tsk_user.equals(user)}
	}
	
	static int getNumberOfTasksForUser(User user) {
		return getAllTasksForUser(user)?.size()
	}
		
	static void refreshCache() {
	    List<Task> l = queryDB("SELECT * FROM ${tablename}".toString(), [])
	    l.each{cache[it.tsk_id] = it}
	}
	
	static List<Task> filterFromColumnAndNeedle(List<Task> haystack, Map column = null, needle = null) {
	   
	    if (column && needle) {
		
			if (column == "tsk_user") {
		    	return haystack.findAll{it.tsk_user.usr_id == Long.parseLong(needle)}
	   	} else if (column == "tsk_collection") {
		    	return haystack.findAll{it.tsk_collection.col_id == Long.parseLong(needle)}
			} else if (type[column] == 'Integer' || type[column] == 'Long') {
				return haystack.findAll{it."${column}" == Long.parseLong(needle)}	
			} else {	 // The rest of the colums are String
	      	return haystack.findAll{it."${column}" =~ /(?i)${needle}/}
	   	}
	    } 
	    return haystack
	}
	
	static List<Task> filterFromLimitAndOffset(List<Task> haystack, int limit = 0, int offset = 0) {
	    List res2 
		if (haystack.isEmpty()) return haystack
		
	  	if (limit != 0) {
	       int lim = (offset+limit-1)
		if (lim > (haystack.size()-1) ) lim = haystack.size()-1
			res2 = haystack[offset..lim]
	    }	else {
			res2 = haystack
	    }
	    return res2
	}
	 
	/** generic purpose value update on DB and cache */
	static updateValue(Long tsk_id, String column, newvalue) {
		 if (!tsk_id) throw new IllegalStateException("Task tsk_id is not valid: "+tsk_id)
		 def newval
		 def object
		
	    switch (type[column]) {
	        case 'Integer': 
					if (!(newvalue instanceof Integer)) newval = Integer.parseInt(newvalue)
					else newval = newvalue
				break
				case ['Long', 'User','Collection']:	
		 			if (newvalue instanceof User) {
						newval = newvalue.usr_id
						object = newvalue
					}
					else if (newvalue instanceof Collection) {
						newval = newvalue.col_id
						object = newvalue
					}
					else if (!(newvalue instanceof Long)) newval = Long.parseLong(newvalue)
				break
				case 'String':
			 	newval = newvalue
		      break
		}
		      
	   def res = db.getDB().executeUpdate("UPDATE ${tablename} SET ${column}=? WHERE tsk_id=?",[newval, tsk_id])
	   cache[tsk_id][column] = (object ? object : newval)
	   return res
	}
	
	/** 
	 * Get the collection list with an optional colum/needle, page them with a limit/offset
	 */
	static HashMap listTasks (limit = 0, offset = 0, column, needle) {	    
	    List<Task> res = filterFromColumnAndNeedle(getAllTasks(), column, needle)         
	    List<Task> res2 = filterFromLimitAndOffset(res, limit, offset)	   
	    return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
	            "result":res2, "column":column, "value":needle]
	}
	
	/** 
	 * Get the collection list with an optional colum/needle, page them with a limit/offset
	 */
	static HashMap listTasksForUser (User user, limit = 0, offset = 0, column, needle) {	    
	    List<Task> res = filterFromColumnAndNeedle(getAllTasksForUser(user), column, needle)         
	    List<Task> res2 = filterFromLimitAndOffset(res, limit, offset)	   
	    return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
	            "result":res2, "column":column, "value":needle]
	}	

	/** Get a Rembrandt Task from id.
	 * @param id The id as needle.
	 * return Task result object, or null
	 */
	static Task getFromID(Long tsk_id) {
	    if (!tsk_id) return null
	    if (!cache) refreshCache()
	    return cache[tsk_id]
	}	
	
	Map toMap() {
	    return ['tsk_id':tsk_id,'tsk_task':tsk_task,'tsk_user':tsk_user.toSimpleMap(), 'tsk_collection':tsk_collection.toSimpleMap(),
	  'tsk_type':tsk_type, 'tsk_priority':tsk_priority,'tsk_limit':tsk_limit,'tsk_offset':tsk_offset,
	  'tsk_done':tsk_done, 'tsk_scope':tsk_scope, 'tsk_persistence':tsk_persistence,'tsk_status':tsk_status,
	  'tsk_comment':tsk_comment]
	}
	
	Map toSimpleMap() {
	    return toMap()
	}
	
	void incrementDone() {
		def res = db.getDB().executeUpdate("UPDATE ${tablename} SET tsk_done=tsk_done+1 where tsk_id=?",[tsk_id])
		cache[tsk_id].tsk_done++
	}
	
	/** Add this Rembrandt Tag to the database.
	 * @param version The version label. By default, it's own version field.
	 * @param comment The version comment. By default, it's own comment field.
	 * return 1 if successfully inserted.
	 */	
	public Long addThisToDB() {
	    
	    if (!cache) refreshCache()	
	    def res = db.getDB().executeInsert("INSERT INTO ${tablename} VALUES(0,?,?,?,?,?,?,?,?,?,?,?,?)", 
		[tsk_task, tsk_user.usr_id, tsk_collection.col_id, tsk_type, tsk_priority, tsk_limit, tsk_offset,
		 tsk_done, tsk_scope, tsk_persistence, tsk_status, tsk_comment ])
		// returns an auto_increment value
	    tsk_id = (long)res[0][0]
	    cache[tsk_id] = this
		 log.info "Adding Task to DB: ${this}"
	    return tsk_id                           
	}	
	
	public int removeThisFromDB() {
		if (!tsk_id) throw new IllegalStateException("Can't remove myself from DB if I don't have a tsk_id")
	   def res = db.getDB().executeUpdate("DELETE FROM ${tablename} where tsk_id=?",[tsk_id]) 
		cache.remove(tsk_id)		
		log.info "Removing Task to DB: ${this}, got res $res"
		return res
	}
	
	public String toString() {
	    return "${tsk_id}:${tsk_task}"
	}
}