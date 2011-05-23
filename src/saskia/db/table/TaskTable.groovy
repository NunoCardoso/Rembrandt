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
import saskia.db.obj.Task
import saskia.db.obj.User

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
class TaskTable extends DBTable {

	static String tablename = "task"

	static Logger log = Logger.getLogger("SaskiaDB")

	Map<Long,Task> cache

	public TaskTable(SaskiaDB db) {
		super(db)
		cache = [:]
	}

	public List<Task> queryDB(String query, ArrayList params) {
		List<Task> res = []
		db.getDB().eachRow(query, params, {row  ->
			res << Task.createNew(this, row)
		})
		return res
	}

	public List<Task> getAllTasks() {
		if (!cache) refreshCache()
		return cache.values().toList()
	}

	public List<Task> getAllTasksForUser(User user) {
		if (!cache) refreshCache()
		return cache.values().toList().findAll{it.tsk_user.equals(user)}
	}

	public int getNumberOfTasksForUser(User user) {
		return getAllTasksForUser(user)?.size()
	}

	public void refreshCache() {
		List<Task> l = queryDB("SELECT * FROM ${tablename}".toString(), [])
		l.each{cache[it.tsk_id] = it}
	}

	public List<Task> filterFromColumnAndNeedle(List<Task> haystack, Map column = null, needle = null) {

		if (column && needle) {

			if (column == "tsk_user") {
				return haystack.findAll{it.tsk_user.usr_id == Long.parseLong(needle)}
			} else if (column == "tsk_collection") {
				return haystack.findAll{it.tsk_collection.col_id == Long.parseLong(needle)}
			} else if (type[column] == 'Integer' || type[column] == 'Long') {
				return haystack.findAll{it."${column}" == Long.parseLong(needle)}
			} else {
				// The rest of the colums are String
				return haystack.findAll{it."${column}" =~ /(?i)${needle}/}
			}
		}
		return haystack
	}

	public List<Task> filterFromLimitAndOffset(List<Task> haystack, int limit = 0, int offset = 0) {
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



	/** 
	 * Get the collection list with an optional colum/needle, page them with a limit/offset
	 */
	public HashMap listTasks (limit = 0, offset = 0, column, needle) {
		List<Task> res = filterFromColumnAndNeedle(getAllTasks(), column, needle)
		List<Task> res2 = filterFromLimitAndOffset(res, limit, offset)
		return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
			"result":res2, "column":column, "value":needle]
	}

	/** 
	 * Get the collection list with an optional colum/needle, page them with a limit/offset
	 */
	public HashMap listTasksForUser (User user, limit = 0, offset = 0, column, needle) {
		List<Task> res = filterFromColumnAndNeedle(getAllTasksForUser(user), column, needle)
		List<Task> res2 = filterFromLimitAndOffset(res, limit, offset)
		return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
			"result":res2, "column":column, "value":needle]
	}

	/** Get a Rembrandt Task from id.
	 * @param id The id as needle.
	 * return Task result object, or null
	 */
	public Task getFromID(Long tsk_id) {
		if (!tsk_id) return null
		if (!cache) refreshCache()
		return cache[tsk_id]
	}

	static Task getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("TaskTable").getFromID(id)
	}
}