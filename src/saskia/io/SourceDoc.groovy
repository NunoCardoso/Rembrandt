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
import saskia.bin.Configuration
import java.sql.SQLException
import java.util.Map;

/** 
 * @author Nuno Cardoso
 * Interface dor source document table
 */
class SourceDoc {

	static String tablename = "source_doc"
   static String job_doc_type_label = "SDOC"

	static Logger log = Logger.getLogger("SaskiaDB")

	/* source doc table fields */
   Long sdoc_id
	String sdoc_original_id
	String sdoc_webstore
	Collection sdoc_collection
	String sdoc_lang	
	String sdoc_comment
	Date sdoc_date
	Long sdoc_doc
	DocStatus sdoc_proc = DocStatus.READY
    
	static Map type = ['sdoc_id':'Long', 'sdoc_original_id':'String', 'sdoc_collection':'Collection',
	  'sdoc_lang':'String', 'sdoc_webstore':'String', 'sdoc_comment':'String',
	  'usr_lastname':'String', 'usr_email':'String', 'usr_password':'String',
	  'sdoc_date':'Date', 'sdoc_doc':'Long', 'sdoc_proc':'DocStatus'] 
	                    
	// it's not a table field, it's from webstore
	String sdoc_content
	boolean retrieved_sdoc_content = false
	
	Job sdoc_job // if this SourceDoc is associated to a job, typically importS2R

	// Document is already post_processed.	
	static SaskiaDB db = SaskiaDB.newInstance()
	static SaskiaWebstore webstore = SaskiaWebstore.newInstance()
	
	static List<SourceDoc> queryDB(String query, ArrayList params) {
	    List l = []
	    SourceDoc sd

	    db.getDB().eachRow(query, params, {row  -> 
	    	sd = new SourceDoc()
	    	sd.sdoc_id = row['sdoc_id']
	    	sd.sdoc_original_id = row['sdoc_original_id']
	    	if (row['sdoc_collection']) sd.sdoc_collection = Collection.getFromID(row['sdoc_collection'])
			sd.sdoc_lang = row['sdoc_lang']
			sd.sdoc_date = (row['sdoc_date'] ?  (Date)row['sdoc_date']: new Date(0))
			if (row['sdoc_doc']) sd.sdoc_doc = row['sdoc_doc']	
			sd.sdoc_comment = row['sdoc_comment']
		 	
		/*java.sql.Blob blob = row.getBlob('sdoc_content')
		byte[] bdata = blob.getBytes(1, (int) blob.length())
		// you have to say explicitly that mediawiki's mediumblob is in UTF-8
		sd.sdoc_content = new String(bdata, "UTF-8")
		*/
		sd.sdoc_webstore = row['sdoc_webstore']
		if (sd.sdoc_webstore) {try {
		    sd.sdoc_content = sd.getContent()
		}catch(Exception e) {log.warn e.getMessage()} }
		
		sd.sdoc_proc = DocStatus.getFromValue(row['sdoc_proc'])			
		l << sd
	    })
	    return (l ? l : null)
	}	
	
	Map toListMap() {
		 return ["sdoc_id":sdoc_id, "sdoc_original_id":sdoc_original_id,
        	"sdoc_collection":sdoc_collection.toSimpleMap(), "sdoc_webstore":sdoc_webstore, 
			"sdoc_lang":sdoc_lang, "sdoc_comment":sdoc_comment, "sdoc_date":sdoc_date, 
			"sdoc_doc":sdoc_doc, "sdoc_proc":sdoc_proc]
	}

	Map toShowMap() {
		 return ["sdoc_id":sdoc_id, "sdoc_original_id":sdoc_original_id,
        	"sdoc_collection":sdoc_collection.toSimpleMap(), "sdoc_webstore":sdoc_webstore, 
			"sdoc_lang":sdoc_lang, "sdoc_comment":sdoc_comment, "sdoc_date":sdoc_date, 
			"sdoc_doc":sdoc_doc, "sdoc_proc":sdoc_proc,
			"sdoc_content":[
				"title": getTitleFromContent()?.replaceAll(/\n/, " "),
            "body": getBodyFromContent()?.replaceAll(/\n/, " ") 
				]
			]
	}


	static HashMap listSourceDocs(Collection collection, limit = 10,  offset = 0, column = null, needle = null) {
	    // limit & offset can come as null... they ARE initialized...
	    if (!limit) limit = 10
	    if (!offset) offset = 0
	    
	    String where = "WHERE sdoc_collection=?"
	    List params = [collection.col_id]
	    if (column && needle) {
		 
	         switch (type[column]) {
	            case 'String': where += " AND $column LIKE '%${needle}%'"; break
	            case 'Long': where += " AND $column=? "; params << Long.parseLong(needle); break
	            case 'DocStatus':  where += " AND $column = ?"; params << needle; break
	            case 'Date': where += " AND $column = ?"; params << needle; break
	         }
	    }
	    String query = "SELECT SQL_CALC_FOUND_ROWS * FROM ${SourceDoc.tablename} $where "+
	    "LIMIT ${limit} OFFSET ${offset} UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), NULL, NULL, NULL, "+
		"NULL, NULL, NULL, NULL, NULL, NULL"
	    log.debug "query = $query params = $params class = "+params*.class
        
	    List u 
	    try {u = queryDB(query, params) }
	    catch(Exception e) {log.error "Error getting source doc list: ", e}
        
	    // last item is not a document... it's the count.
	    SourceDoc fakesdoc = u.pop()    
	  	 long total = fakesdoc.sdoc_id
	    
	    log.debug "Returning "+u.size()+" results."
	    return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
	             "column":column, "value":needle, "col_id":collection.col_id]
	}
    
 /** Handle with care! */
	public updateValue(column, value) {
	    def newvalue	    
	    switch (type[column]) {
	        case 'String': newvalue = value; break
	        case 'Long': newvalue = Long.parseLong(value); break
	    }
	    def res = db.getDB().executeUpdate("UPDATE ${SourceDoc.tablename} SET ${column}=? WHERE doc_id=?",[newvalue, doc_id])
	    return res
	}
	
        /** Get a SourceDoc from an id
         * @param sdoc_id The id of the source document. 
         * return the SourceDoc
         */
        static SourceDoc getFromID(long sdoc_id) {
            if (!sdoc_id) return null 
            List<SourceDoc> l = queryDB("SELECT * FROM ${SourceDoc.tablename} WHERE sdoc_id=? ", [sdoc_id])
            //log.trace "Querying for SourceDoc $sdoc_id, got SourceDoc ${l}" 
            return (l ? l[0] : null) 
        }
	
        String getContent() {
            if (retrieved_sdoc_content && sdoc_content) return sdoc_content
            //try {
            sdoc_content = webstore.retrieve(sdoc_webstore)
            if (sdoc_content) retrieved_sdoc_content = true
            return sdoc_content
	
        }
            
            
        
	/** Get a SourceDoc from an original id, collection and lang
	 * @param sdoc_original_id The original id of the source document. 
	 * @param sdoc_collection the source document collection
	 * @param sdoc_lang The language of the document. 
	 * return the SourceDoc
	 */
	static SourceDoc getFromOriginalIDandCollectionIDandLang(String sdoc_original_id, long sdoc_collection, String sdoc_lang) {
	    if (!sdoc_original_id) return null 
	    List<SourceDoc> l = queryDB("SELECT * FROM ${SourceDoc.tablename} WHERE "+
		   "sdoc_original_id=? and sdoc_collection=? and sdoc_lang=?", [sdoc_original_id, sdoc_collection, sdoc_lang])
		//log.trace "Querying for SourceDoc $sdoc_id, got SourceDoc ${l}" 
		return (l ? l[0] : null) 
	}
	
	/** Get a SourceDoc from an original id and collection
	 * @param sdoc_original_id The original id of the source document. 
	 * @param sdoc_collection the source document collection
	 * return the SourceDoc
	 */
	static SourceDoc getFromOriginalIDandCollectionID(String sdoc_original_id, long sdoc_collection) {
	    if (!sdoc_original_id) return null 
	    List<SourceDoc> l = queryDB("SELECT * FROM ${SourceDoc.tablename} WHERE "+
		   "sdoc_original_id=? and sdoc_collection=?", [sdoc_original_id, sdoc_collection])
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
	static List<SourceDoc> getNextProcessableAndUnlockedDoc(Task task, String process_signature, 
		Collection sdoc_collection, int limit = 10) {  
	    
		List l = []
		SourceDoc sd
		int max_tries = 10
		int tries = 0
		while  (!l && (tries < max_tries)) {    
			try {
				db.getDB().withTransaction{
					log.info "Try #${tries+1}: Getting a set of $limit processable sourceDocs"  
 
					def query = "SELECT * FROM ${SourceDoc.tablename} WHERE sdoc_collection=? AND "+
					"sdoc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND sdoc_doc IS NULL AND "+
					"sdoc_id NOT IN (select job_doc_id from "+Job.tablename+" where job_doc_type='"+job_doc_type_label+
					"' and job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+") LIMIT ${limit} FOR UPDATE"  
					// VERY IMPORTANT, the FOR UPDATE, it locks the table until the transaction is complete
        
					def params = [sdoc_collection.col_id]
					db.getDB().eachRow(query, params, {row ->  
        		   	log.trace "Got "+ row['sdoc_id']
        		   	sd = new SourceDoc()
        		   	sd.sdoc_id = row['sdoc_id']
        		   	sd.sdoc_original_id = row['sdoc_original_id']
	   		    	if (row['sdoc_collection']) sd.sdoc_collection = Collection.getFromID(row['sdoc_collection'])

						sd.sdoc_lang = row['sdoc_lang']
        		   	sd.sdoc_date = (row['sdoc_date'] ?  (Date)row['sdoc_date']: new Date(0))
						sd.sdoc_comment = row['sdoc_comment']
						sd.sdoc_webstore = row['sdoc_webstore']
						if (sd.sdoc_webstore) sd.sdoc_content = webstore.retrieve(sd.sdoc_webstore)
        		   	sd.sdoc_proc = DocStatus.getFromValue(row['sdoc_proc'])			
        		
        		   	l << sd

        		// LET's create JOBS to mark the queue
						Job job = new Job(job_task:task, job_worker:process_signature, job_doc_type:job_doc_type_label,
						job_doc_id:sd.sdoc_id, job_doc_edit:DocStatus.QUEUED, job_doc_edit_date:new Date())
						job.job_id = job.addThisToDB()
						sd.sdoc_job = job
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
	   return (l ? l : null)
	}
	
	public String getTitle() {
	    def m = sdoc_content =~ /(?si).*<TITLE>(.*?)<\/TITLE>.*/
	    if (m.matches()) return  m.group(1).replaceAll("_"," ")
	    return null
	}
	
	/** Add the fields in this object to the DB 
	 * @return The new id for the doc table, from the DB
	 */
	public addThisToDB() {	
	    try {
			 if (sdoc_content) {
		    	String key = webstore.store(sdoc_content, SaskiaWebstore.VOLUME_SDOC)
		    	log.trace "Got new key $key"
		    	def res = db.getDB().executeInsert("INSERT INTO ${SourceDoc.tablename}(sdoc_original_id, sdoc_webstore, sdoc_collection, sdoc_lang, "+
		" sdoc_comment, sdoc_date, sdoc_doc, sdoc_proc) VALUES(?,?,?,?,?,?,?,?)", 
		[sdoc_original_id, key, sdoc_collection.col_id, sdoc_lang, sdoc_comment, sdoc_date, sdoc_doc, sdoc_proc.text()]) 		
		   	sdoc_id = (long) res[0][0]
		   	sdoc_webstore = key                        
				log.trace "Inserted new SourceDoc, got id $sdoc_id"
          } else {
        	 	log.error "Did NOT added source_doc, sdoc_content is empty!"
           }
	    } catch (Exception e) {
        	 	log.error "Did NOT added source_doc to the DB: ${e.getMessage()}"
	    }
		 // important, to know if it was written to the DB (that is, has an assigned id)
	    return this.sdoc_id
	}
	
	/** Add the fields in this object to the DB 
	 * @return The new id for the doc table, from the DB
	 */
	public replaceThisToDB() {	
	    try {
			if (sdoc_content) {
			 // apagar o conteúdo anterior
			 	if (sdoc_webstore) {
			 		try {
						webstore.delete(sdoc_webstore, SaskiaWebstore.VOLUME_SDOC)
					} catch(Exception e) {}
		    		String key = webstore.store(sdoc_content, SaskiaWebstore.VOLUME_SDOC)
		    		log.trace "Got new key $key"
			
			// add sdoc_id so it can make the replacement
		    def res = db.getDB().executeInsert("REPLACE INTO ${SourceDoc.tablename}(sdoc_id, sdoc_original_id, sdoc_webstore, sdoc_collection, sdoc_lang, "+
		" sdoc_comment, sdoc_date, sdoc_doc, sdoc_proc) VALUES(?,?,?,?,?,?,?,?,?)", 
		[sdoc_id, sdoc_original_id, key, sdoc_collection.col_id, sdoc_lang, sdoc_comment, sdoc_date, sdoc_doc, sdoc_proc.text()]) 		
		   sdoc_id = (long) res[0][0]
		   sdoc_webstore = key                        
			log.trace "Replaced new SourceDoc, got id $sdoc_id"
				}
          } else {
        	 	log.error "Did NOT added source_doc, sdoc_content is empty!"
          }
	    } catch (Exception e) {
			e.printStackTrace()
	    }
	    return this.sdoc_id
	}
	
	
	 String getTitleFromContent() {
        String s = null
        sdoc_content?.find(/(?si)<TITLE>(.*?)<\/TITLE>/) {match, g1 -> s = g1.trim()}
        return s   
    }
    
    String getBodyFromContent() {
       // println "rdoc_content = $rdoc_content"
        String s = null
        sdoc_content?.find(/(?si)<BODY>(.*?)<\/BODY>/) {match, g1 -> s = g1.trim()}
        return s           
    }

	public removeThisFromDB() {	
	    def res = db.getDB().executeUpdate("DELETE FROM ${SourceDoc.tablename} where sdoc_id=?",[sdoc_id]) 
	    return res
	}
	
	public int changeProcStatusInDBto(DocStatus status) {	
		def res = db.getDB().executeUpdate("UPDATE ${SourceDoc.tablename} SET sdoc_proc=? WHERE sdoc_id=? and "+
		"sdoc_collection=? and sdoc_lang=?",
			[status.text(), sdoc_id, sdoc_collection.col_id, sdoc_lang]) 
		log.trace "Wrote proc status ${status}(${status.text()}) to sdoc_id ${sdoc_id}, ${res} rows were changed."
		return res
	}
	
	public int addDocID(long doc_id) {	
	    if (!doc_id) return null
		def res = db.getDB().executeUpdate("UPDATE ${SourceDoc.tablename} SET sdoc_doc=? WHERE sdoc_id=? and "+
		"sdoc_collection=? and sdoc_lang=?", [doc_id, sdoc_id, sdoc_collection.col_id, sdoc_lang]) 
		return res
	}
	
	public String toString() {
		return "${sdoc_id}:${sdoc_collection}:${sdoc_lang}"
	}
}
