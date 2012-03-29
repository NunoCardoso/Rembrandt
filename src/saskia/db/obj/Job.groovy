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

import saskia.db.DocStatus
import saskia.db.obj.Task
import saskia.db.table.DBTable

/**
 * @author Nuno Cardoso
 *
 */
class Job extends DBObject implements JSONable {

	Long job_id
	Task job_task
	String job_worker
	Long job_doc_id
	DocStatus job_doc_edit
	Date job_doc_edit_date
	static Date nulldate = new Date(0)

	static Logger log = Logger.getLogger("Job")
	
	public Job(DBTable dbtable) {
		super(dbtable)
	}
	
	static Job createNew(DBTable dbtable, row) {
		Job j = new Job(dbtable)
		if (row['job_id']) j.job_id = row['job_id']
		if (row['job_task']) j.job_task = (row['job_task'] instanceof Task ? 
			row['job_task'] : 
			dbtable.getSaskiaDB().getDBTable("TaskTable").getFromID(row['job_task']) )
		if (row['job_doc_id']) j.job_doc_id = row['job_doc_id']
		if (row['job_doc_edit']) 
			j.job_doc_edit = (row['job_doc_edit'] instanceof DocStatus ? 
			row['job_doc_edit'] : DocStatus.getFromValue(row['job_doc_edit']) )
		if (row['job_doc_edit_date'] && (Date)row['job_doc_edit_date'] != nulldate)
			j.job_doc_edit_date = (Date)row['job_doc_edit_date']
		return j
	}
	
	static Map type = ['job_id':'Long','job_task':'Task', 'job_worker':'String',
		'job_doc_id':'Long','job_doc_edit':'DocStatus','job_doc_edit_date':'Date']
  
	
	Map toMap() {
		return ['job_id':job_id,'job_task':job_task.toMap(), 'job_worker':job_worker,
	  'job_doc_id':job_doc_id,'job_doc_edit':job_doc_edit, 'job_doc_edit_date':job_doc_edit_date]
	}
	
	Map toSimpleMap() {
		return toMap()
	}

	
	public int changeEditStatusInDBto(DocStatus status) {
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
			"UPDATE ${getDBTable().tablename} SET job_doc_edit=? WHERE job_id=?",
			[status.text(), job_id])
		log.info "Wrote edit status ${status}(${status.text()}) to job_id ${job_id}, ${res} rows were changed."
		return res
	}	
	
	/** Add this to the database.
	 * @param version The version label. By default, it's own version field.
	 * @param comment The version comment. By default, it's own comment field.
	 * return 1 if successfully inserted.
	 */
	public Long addThisToDB() {
		if (!getDBTable().cache) getDBTable().refreshCache()
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
			"INSERT INTO ${getDBTable().tablename} VALUES(0,?,?,?,?,?)",
		[job_task.tsk_id, job_worker, job_doc_id, job_doc_edit.text(), job_doc_edit_date])
		// returns an auto_increment value
		job_id = (long)res[0][0]
		getDBTable().cache[job_id] = this
		 log.info "Inserted new Job in DB: ${this}"
		return job_id
	}
	
	public int removeThisFromDB() {
		if (!job_id) throw new IllegalStateException("Can't remove myself from DB if I don't have a job_id")
	   def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
		   "DELETE FROM ${getDBTable().tablename} where job_id=?",[job_id])
		getDBTable().cache.remove(job_id)
		log.info "Removed Job ${this} from DB, got ${res}"
		return res
	}
	
	public String toString() {
		return "${job_id}"
	}
}
