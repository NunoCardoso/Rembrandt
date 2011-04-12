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

/** This class is an interface for the Job table in the RembrandtPool database. 
CREATE TABLE `job` (
	 `job_id` BIGINT PRIMARY KEY AUTO_INCREMENT, 
    `job_task` BIGINT NOT NULL, 
    `job_worker` VARCHAR(255) DEFAULT NULL, 
 	 `job_doc_type` CHAR(4) DEFAULT NULL,
	 `job_doc_id` BIGINT NOT NULL, 
	 `job_doc_edit` CHAR(2) DEFAULT NULL,
    `job_doc_edit_date` DATETIME NOT NULL
);

  */
class Job extends DBObject implements JSONable {

	static String tablename = "job"
	Long job_id
	Task job_task
	String job_worker
	String job_doc_type // SDOC, RDOC
	Long job_doc_id
	DocStatus job_doc_edit
	Date job_doc_edit_date
	static Date nulldate = new Date(0)
	
	static SaskiaDB db = SaskiaDB.newInstance()

	static Map type = ['job_id':'Long','job_task':'Task', 'job_worker':'String','job_doc_type':'String',
	  'job_doc_id':'Long','job_doc_edit':'DocStatus','job_doc_edit_date':'Date']

	static Logger log = Logger.getLogger("Job")
	
	static Map<Long,Job> cache = [:]

	static List<Job> queryDB(String query, ArrayList params) {
	    List<Job> res = []
	    db.getDB().eachRow(query, params, {row  -> 
	        Job j = new Job()
				j.job_id = row['job_id']
				j.job_task = Task.getFromID(row['job_task'])
				j.job_doc_type = row['job_doc_type']
				j.job_doc_id = row['job_doc_id']
				j.job_doc_edit =  DocStatus.getFromValue(row['job_doc_edit'])	
				if (row['job_doc_edit_date'] && (Date)row['job_doc_edit_date'] != nulldate) 
				j.job_doc_edit_date = (Date)row['job_doc_edit_date'] 
	    	res << j                   
	    })
	    return res
	}
	
	static void refreshCache() {
	    List<Job> l = queryDB("SELECT * FROM ${tablename}".toString(), [])
	    l.each{cache[it.job_id] = it}
	}
	
	/** Get a Job from id.
	 * @param id The id as needle.
	 * return Job result object, or null
	 */
	static Job getFromID(Long job_id) {
	    if (!job_id) return null
	    if (!cache) refreshCache()
	    return cache[job_id]
	}	
	
	static Job getFromDocIDAndDocType(Long job_doc_id, String job_doc_type) {
	    if (!cache) refreshCache()
	    return cache.find{k, v -> v.job_doc_id==job_doc_id && v.job_doc_type==job_doc_type}?.collect{it.value}?.getAt(0)
	}	
	
	Map toMap() {
	    return ['job_id':job_id,'job_task':job_task.toMap(), 'job_worker':job_worker,'job_doc_type':job_doc_type,
	  'job_doc_id':job_doc_id,'job_doc_edit':job_doc_edit, 'job_doc_edit_date':job_doc_edit_date]
	}
	
	Map toSimpleMap() {
		return toMap()
	}
	
	public int changeEditStatusInDBto(DocStatus status) {	
		def res = db.getDB().executeUpdate("UPDATE ${tablename} SET job_doc_edit=? WHERE job_id=?", 
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
	    if (!cache) refreshCache()	
	    def res = db.getDB().executeInsert("INSERT INTO ${tablename} VALUES(0,?,?,?,?,?,?)", 
		[job_task.tsk_id, job_worker, job_doc_type, job_doc_id, job_doc_edit.text(), job_doc_edit_date])
		// returns an auto_increment value
	    job_id = (long)res[0][0]
	    cache[job_id] = this
		 log.info "Inserted new Job in DB: ${this}"
	    return job_id                           
	}	
	
	public int removeThisFromDB() {
		if (!job_id) throw new IllegalStateException("Can't remove myself from DB if I don't have a job_id")
	   def res = db.getDB().executeUpdate("DELETE FROM ${tablename} where job_id=?",[job_id]) 
		cache.remove(job_id)		
		log.info "Removed Job ${this} from DB, got ${res}"
		return res
	}
	
	boolean equals(Entity e) {
		return this.toMap().equals(e.toMap())
	}
	
	public String toString() {
	    return "${job_id}"
	}
}