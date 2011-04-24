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

import java.sql.SQLException

import org.apache.log4j.*

import saskia.bin.Configuration
import saskia.db.DocStatus
import saskia.db.SaskiaWebstore
import saskia.db.database.SaskiaDB
import saskia.db.obj.*

/** 
 * @author Nuno Cardoso
 * Interface for source document table
 */
class SourceDocTable extends DBTable {

	static String job_doc_type_label = "SDOC"
	static String tablename = "source_doc"

	static Logger log = Logger.getLogger("SaskiaDB")

	Configuration conf
	SaskiaWebstore webstore

	public SourceDocTable(SaskiaDB db) {
		super(db)
		conf = Configuration.newInstance()
		webstore = SaskiaWebstore.newInstance()
	}

	public List<SourceDoc> queryDB(String query, ArrayList params) {
		List l = []

		db.getDB().eachRow(query, params, {row  ->
			l << SourceDoc.createNew(this,row)
		})
		return (l ? l : null)
	}


	/** Get a SourceDoc from an id
	 * @param sdoc_id The id of the source document. 
	 * return the SourceDoc
	 */
	public SourceDoc getFromID(long sdoc_id) {
		if (!sdoc_id) return null
		List<SourceDoc> l = queryDB("SELECT * FROM ${SourceDoc.tablename} WHERE sdoc_id=? ", [sdoc_id])
		//log.trace "Querying for SourceDoc $sdoc_id, got SourceDoc ${l}"
		return (l ? l[0] : null)
	}

	static SourceDoc getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("SourceDocTable").getFromID(id)
	}


	/** Get a SourceDoc from an original id, collection and lang
	 * @param sdoc_original_id The original id of the source document. 
	 * @param sdoc_collection the source document collection
	 * @param sdoc_lang The language of the document. 
	 * return the SourceDoc
	 */
	public SourceDoc getFromOriginalIDandCollectionIDandLang(String sdoc_original_id, long sdoc_collection, String sdoc_lang) {
		if (!sdoc_original_id) return null
		List<SourceDoc> l = queryDB("SELECT * FROM ${SourceDoc.tablename} WHERE "+
				"sdoc_original_id=? and sdoc_collection=? and sdoc_lang=?", [
					sdoc_original_id,
					sdoc_collection,
					sdoc_lang
				])
		//log.trace "Querying for SourceDoc $sdoc_id, got SourceDoc ${l}"
		return (l ? l[0] : null)
	}

	/** Get a SourceDoc from an original id and collection
	 * @param sdoc_original_id The original id of the source document. 
	 * @param sdoc_collection the source document collection
	 * return the SourceDoc
	 */
	public SourceDoc getFromOriginalIDandCollectionID(String sdoc_original_id, long sdoc_collection) {
		if (!sdoc_original_id) return null
		List<SourceDoc> l = queryDB("SELECT * FROM ${SourceDoc.tablename} WHERE "+
				"sdoc_original_id=? and sdoc_collection=?", [
					sdoc_original_id,
					sdoc_collection
				])
		//log.trace "Querying for SourceDoc ${sdoc_id}, got SourceDoc ${l}"
		return (l ? l[0] : null)
	}


	/**
	 * Get pool of source documents in a thread-safe way. 
	 * By enforcing withTransaction, I'm setting autocommit=0
	 * By issuing 'FOR UPDATE' i'm locking other threads on their SELECT/UPDATE until this transaction is commited/rollbacked
	 * if this transaction succeeds, it'll commit, leaving the source docs marked with 'QU'.
	 * if it fails, it'll rollback, leaving them accessible for the next thread
	 * */
	public List<SourceDoc> getNextProcessableAndUnlockedDoc(Task task, String process_signature,
	Collection sdoc_collection, int limit = 10) {

		List l = []
		SourceDoc sd
		int max_tries = 10
		int tries = 0
		while  (!l && (tries < max_tries)) {
			try {
				db.getDB().withTransaction{
					log.info "Try #${tries+1}: Getting a set of $limit processable sourceDocs"

					def query = "SELECT * FROM ${tablename} WHERE sdoc_collection=? AND "+
						"sdoc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND sdoc_doc IS NULL AND "+
						"sdoc_id NOT IN (select job_doc_id from ${JobTable.tablename} WHERE "+
						"job_doc_type='"+job_doc_type_label+"' and job_doc_edit NOT IN "+
						DocStatus.whereConditionUnlocked()+") LIMIT ${limit} FOR UPDATE"
					// VERY IMPORTANT, the FOR UPDATE, it locks the table until the transaction is complete

					def params = [sdoc_collection.col_id]

					db.getDB().eachRow(query, params, {row ->
						sd = SourceDoc.createNew(this, row)
						
						// LET's create JOBS to mark the queue
						Job job = Job.createNew(db.getDBTable("JobTable"), 
						 [job_task:task, job_worker:process_signature, job_doc_type:job_doc_type_label,
						  job_doc_id:sd.sdoc_id, job_doc_edit:DocStatus.QUEUED, job_doc_edit_date:new Date()
						 ])
						 job.job_id = job.addThisToDB()
						 sd.sdoc_job = job
						 l << sd
						
					})
				}
			} catch (org.codehaus.groovy.runtime.InvokerInvocationException iie) {
				log.error iie.getMessage()
			} catch (SQLException sqle) {
				//
				// The two SQL states that are 'retry-able' are 08S01
				// for a communications error, and 40001 for deadlock.
				//
				// Only retry if the error was due to a stale connection,
				// communications problem or deadlock
				//
				String sqlState = sqle.getSQLState()
				log.warn "SourceDoc: Got SQL error $sqlState"
				if ("08S01".equals(sqlState) || "40001".equals(sqlState) || "41000".equals(sqlState)) {
					log.warn "This error is retrieable! Good! Sleeping for 5 seconds..."
					sleep(5000)
					tries++
				}  else tries = max_tries
			}
		} // !l && max tries
		return l
	}
}
