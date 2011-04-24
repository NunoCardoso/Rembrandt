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
import saskia.db.obj.Job

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
class JobTable extends DBTable {

	static String tablename = "job"
	static Logger log = Logger.getLogger("Job")

	Map<Long,Job> cache

	public JobTable(SaskiaDB db) {
		super(db)
		cache = [:]
	}

	public List<Job> queryDB(String query, ArrayList params) {
		List<Job> res = []
		db.getDB().eachRow(query, params, {row  ->
			res << Job.createNew(this, row)
		})
		return res
	}

	void refreshCache() {
		List<Job> l = queryDB("SELECT * FROM ${tablename}".toString(), [])
		l.each{cache[it.job_id] = it}
	}

	/** Get a Job from id.
	 * @param id The id as needle.
	 * return Job result object, or null
	 */
	public Job getFromID(Long job_id) {
		if (!job_id) return null
		if (!cache) refreshCache()
		return cache[job_id]
	}

	static Job getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("JobTable").getFromID(id)
	}

	public Job getFromDocIDAndDocType(Long job_doc_id, String job_doc_type) {
		if (!cache) refreshCache()
		return cache.find{k, v -> v.job_doc_id==job_doc_id && v.job_doc_type==job_doc_type}?.collect{it.value}?.getAt(0)
	}
}