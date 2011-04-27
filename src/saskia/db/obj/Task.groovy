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

import org.apache.log4j.Logger

import saskia.db.table.DBTable


/**
 * @author Nuno Cardoso
 *
 */
public class Task extends DBObject implements JSONable {

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

	static Logger log = Logger.getLogger("SaskiaDB")

	static Map type = ['tsk_id':'Long','tsk_task':'String', 'tsk_user':'User', 'tsk_collection':'Collection','tsk_type':'String',
		'tsk_priority':'Integer','tsk_limit':'Integer','tsk_offset':'Long','tsk_done':'Integer','tsk_scope':'String',
		'tsk_persistence':'String','tsk_status':'String','tsk_comment':'String']

	public Task(DBTable dbtable) {
		super(dbtable)
	}

	static Task createNew(DBTable dbtable, row) {
		Task t = new Task(dbtable)
		if (row['tsk_id']) t.tsk_id = row['tsk_id']
		if (row['tsk_task']) t.tsk_task = row['tsk_task']
		if (row['tsk_user']) t.tsk_user= (row['tsk_user'] instanceof User? 
			row['tsk_user'] : 
			dbtable.getSaskiaDB().getDBTable("UserTable").getFromID(row['tsk_user']))
		if (row['tsk_collection']) t.tsk_collection = (row['tsk_collection'] instanceof Collection ? 		
			row['tsk_collection'] : 
			dbtable.getSaskiaDB().getDBTable("CollectionTable").getFromID(row['tsk_collection']) )
		if (row['tsk_type']) t.tsk_type = row['tsk_type']
		if (row['tsk_priority']) t.tsk_priority = row['tsk_priority']
		if (row['tsk_limit']) t.tsk_limit = row['tsk_limit']
		if (row['tsk_offset']) t.tsk_offset = row['tsk_offset']
		if (row['tsk_done']) t.tsk_done = row['tsk_done']
		if (row['tsk_scope']) t.tsk_scope = row['tsk_scope']
		if (row['tsk_persistence']) t.tsk_persistence = row['tsk_persistence']
		if (row['tsk_status']) t.tsk_status = row['tsk_status']
		if (row['tsk_comment']) t.tsk_comment = row['tsk_comment']
		return t
	}

	/** generic purpose value update on DB and cache */
	public updateValue(String column, newvalue) {
		if (!tsk_id) throw new IllegalStateException("Task tsk_id is not valid: "+tsk_id)
		def newval
		def object

		switch (type[column]) {
			case 'Integer':
				if (!(newvalue instanceof Integer)) newval = Integer.parseInt(newvalue)
				else newval = newvalue
				break
			case ['Long', 'User', 'Collection']:
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

		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().tablename} SET ${column}=? WHERE tsk_id=?",
				[newval, tsk_id])
				getDBTable().cache[tsk_id][column] = (object ? object : newval)
		return res
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
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().tablename} SET tsk_done=tsk_done+1 where tsk_id=?",
				[tsk_id])
		getDBTable().cache[tsk_id].tsk_done++
	}

	/** Add this Rembrandt Tag to the database.
	 * @param version The version label. By default, it's own version field.
	 * @param comment The version comment. By default, it's own comment field.
	 * return 1 if successfully inserted.
	 */	
	public Long addThisToDB() {

		if (!getDBTable().cache) getDBTable().refreshCache()
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().tablename} VALUES(0,?,?,?,?,?,?,?,?,?,?,?,?)",
				[
					tsk_task,
					tsk_user.usr_id,
					tsk_collection.col_id,
					tsk_type,
					tsk_priority,
					tsk_limit,
					tsk_offset,
					tsk_done,
					tsk_scope,
					tsk_persistence,
					tsk_status,
					tsk_comment
				])
		// returns an auto_increment value
		tsk_id = (Long)res[0][0]
		getDBTable().cache[tsk_id] = this
		log.info "Adding Task to DB: ${this}"
		return tsk_id
	}

	public int removeThisFromDB() {
		if (!tsk_id) throw new IllegalStateException("Can't remove myself from DB if I don't have a tsk_id")
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} where tsk_id=?",[tsk_id])
		getDBTable().cache.remove(tsk_id)
		log.info "Removing Task to DB: ${this}, got res $res"
		return res
	}

	public String toString() {
		return "${tsk_id}:${tsk_task}"
	}

}
