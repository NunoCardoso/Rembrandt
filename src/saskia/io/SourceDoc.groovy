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

	static String sdoc_table = "source_doc"
	static Logger log = Logger.getLogger("SaskiaDB")

	/* source doc table fields */
    Long sdoc_id
	String sdoc_original_id
	Long sdoc_collection
	String sdoc_lang	
	String sdoc_webstore
	String sdoc_comment
	Date sdoc_date
	Long sdoc_doc
	DocStatus sdoc_proc = DocStatus.READY
	DocStatus sdoc_edit = DocStatus.UNLOCKED
	Date sdoc_edit_date = null
    
	static Map type = ['sdoc_id':'Long', 'sdoc_original_id':'String', 'sdoc_collection':'Long',
	  'sdoc_lang':'String', 'sdoc_webstore':'String', 'sdoc_comment':'String',
	  'usr_lastname':'String', 'usr_email':'String', 'usr_password':'String',
	  'sdoc_date':'Date', 'sdoc_doc':'Long', 'sdoc_proc':'DocStatus',
	  'sdoc_edit':'DocStatus','sdoc_edit_date':'Date'] 
	                    
	// it's not a table field, it's from webstore
	String sdoc_content
	boolean retrieved_sdoc_content = false
	
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
	    	sd.sdoc_collection = row['sdoc_collection']
		sd.sdoc_lang = row['sdoc_lang']
		sd.sdoc_date = (row['sdoc_date'] ?  (Date)row['sdoc_date']: new Date(0))
		sd.sdoc_doc = (long)row['sdoc_doc']
		if (row['sdoc_edit_date']) sd.sdoc_edit_date = (Date)row['sdoc_edit_date']
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
		sd.sdoc_edit = DocStatus.getFromValue(row['sdoc_edit'])			
		l << sd
	    })
	    return (l ? l : null)
	}	
	
	static HashMap getSourceDocs(Collection collection, limit = 10,  offset = 0, column = null, needle = null) {
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
	    String query = "SELECT SQL_CALC_FOUND_ROWS * FROM ${sdoc_table} $where "+
	    "LIMIT ${limit} OFFSET ${offset} UNION "+
	    "SELECT 0, '', '', FOUND_ROWS(), '', '', '',now(), 0,'','', now()"
	    log.debug "query = $query params = $params class = "+params*.class
        
	    List u 
	    try {u = queryDB(query, params) }
	    catch(Exception e) {log.error "Error getting source doc list: ", e}
        
	    // last item is not a document... it's the count.
	    SourceDoc fakesdoc = u.pop()    
	    int total 
	    try {total = (int)(fakesdoc.sdoc_collection)
	    }catch(Exception e) {log.error "Can't convert ${fakesdoc.sdoc_collection} to int", e}
	    log.debug "Returning "+u.size()+" results."
	    return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
	             "column":column, "value":needle, "col_id":collection.col_id]
	}
    
        /** Get a SourceDoc from an id
         * @param sdoc_id The id of the source document. 
         * return the SourceDoc
         */
        static SourceDoc getFromID(long sdoc_id) {
            if (!sdoc_id) return null 
            List<SourceDoc> l = queryDB("SELECT * FROM ${sdoc_table} WHERE sdoc_id=? ", [sdoc_id])
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
	    List<SourceDoc> l = queryDB("SELECT * FROM ${sdoc_table} WHERE "+
		   "sdoc_original_id=? and sdoc_collection=? and sdoc_lang=?", [sdoc_original_id, sdoc_collection, sdoc_lang])
		//log.trace "Querying for SourceDoc $sdoc_id, got SourceDoc ${l}" 
		return (l ? l[0] : null) 
	}
	
	/**
	 * Get pool of source documents in a thread-safe way. 
	 * By enforcing withTransaction, I'm setting autocommit=0
	 * By issuing 'FOR UPDATE' i'm locking other threads on their SELECT/UPDATE until this transaction is commited/rollbacked
	 * if this transaction succeeds, it'll commit, leaving the source docs marked with 'QU'.
	 * if it fails, it'll rollback, leaving them accessible for the next thread
	 * */
	static List<SourceDoc> getNextProcessableAndUnlockedDoc(String sdoc_lang, long sdoc_collection, int limit = 10) {  
	    if (!sdoc_lang) return null 
	    
	    List l = []
	    SourceDoc sd
            int max_tries = 10
            int tries = 0
            while  (!l && (tries < max_tries)) {    
                try {
        	db.getDB().withTransaction{
        	    log.info "Try #${tries+1}: Getting a set of $limit processable sourceDocs"  
        		   
        		def query = "SELECT * FROM ${sdoc_table} WHERE sdoc_collection=? AND "+
        		"sdoc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND sdoc_edit IN "+
        		DocStatus.whereConditionUnlocked()+" AND sdoc_doc IS NULL "+
        		" LIMIT ${limit} FOR UPDATE"  // VERY IMPORTANT, the FOR UPDATE, it locks the table until the transaction is complete
        
        		def params = [sdoc_collection]
        		db.getDB().eachRow(query, params, {row ->  
        		   log.trace "Got "+ row['sdoc_id']
        		   sd = new SourceDoc()
        		   sd.sdoc_id = row['sdoc_id']
        		   sd.sdoc_original_id = row['sdoc_original_id']
        		   sd.sdoc_collection = row['sdoc_collection']
        		   sd.sdoc_lang = row['sdoc_lang']
        		   sd.sdoc_date = (row['sdoc_date'] ?  (Date)row['sdoc_date']: new Date(0))
                           sd.sdoc_comment = row['sdoc_comment']
                           sd.sdoc_webstore = row['sdoc_webstore']
                           if (sd.sdoc_webstore) sd.sdoc_content = webstore.retrieve(sd.sdoc_webstore)
        		   sd.sdoc_proc = DocStatus.getFromValue(row['sdoc_proc'])			
        		   sd.sdoc_edit = DocStatus.getFromValue(row['sdoc_edit'])			
        		   l << sd
        				
        		   // LET's mark it as QUEUED
        		   db.getDB().executeUpdate("UPDATE ${sdoc_table} SET sdoc_edit=? , sdoc_edit_date=NOW() "+
        			   "WHERE sdoc_collection=? AND sdoc_id=?", 
        			   [DocStatus.QUEUED.text(), sd.sdoc_collection, sd.sdoc_id])
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
		    def res = db.getDB().executeInsert("INSERT INTO ${sdoc_table}(sdoc_original_id, sdoc_webstore, sdoc_collection, sdoc_lang, "+
		" sdoc_comment, sdoc_date, sdoc_doc, sdoc_proc, sdoc_edit) VALUES(?,?,?,?,?,?,?,?,?)", 
		[sdoc_original_id, key, sdoc_collection, sdoc_lang, sdoc_comment, sdoc_date, sdoc_doc, sdoc_proc.text(), sdoc_edit.text()]) 		
		   sdoc_id = (long) res[0][0]
		   sdoc_webstore = key                        
		log.trace "Inserted new SourceDoc, got id $sdoc_id"
               } else {
        	 log.error "Did NOT added source_doc, sdoc_content is empty!"
               }
	    } catch (Exception e) {
		e.printStackTrace()
	    }
	    return this.sdoc_id
	}
	
	public int changeProcStatusInDBto(DocStatus status) {	
		def res = db.getDB().executeUpdate("UPDATE ${sdoc_table} SET sdoc_proc=? WHERE sdoc_id=? and "+
		"sdoc_collection=? and sdoc_lang=?",
			[status.text(), sdoc_id, sdoc_collection, sdoc_lang]) 
		log.trace "Wrote proc status ${status}(${status.text()}) to sdoc_id ${sdoc_id}, ${res} rows were changed."
		return res
	}
	
	public int removeEditDate() {	
		def res = db.getDB().executeUpdate("UPDATE ${sdoc_table} SET sdoc_edit_date=NULL WHERE sdoc_id=? and "+
		"sdoc_collection=? and sdoc_lang=?", [sdoc_id, sdoc_collection, sdoc_lang]) 
		log.trace "Removed sdoc_edit_date to sdoc_id ${sdoc_id}, ${res} rows changed."
		return res
	}
	
	public int addEditDate() {	
		def res = db.getDB().executeUpdate("UPDATE ${sdoc_table} SET sdoc_edit_date=NOW() WHERE sdoc_id=? and "+
		"sdoc_collection=? and sdoc_lang=?", [sdoc_id, sdoc_collection, sdoc_lang]) 
		log.trace "Added sdoc_edit_date to sdoc_id ${sdoc_id}."
		return res
	}
	
	public int addDocID(long doc_id) {	
	    if (!doc_id) return null
		def res = db.getDB().executeUpdate("UPDATE ${sdoc_table} SET sdoc_doc=? WHERE sdoc_id=? and "+
		"sdoc_collection=? and sdoc_lang=?", [doc_id, sdoc_id, sdoc_collection, sdoc_lang]) 
		log.trace "Added sdoc_edit_date to sdoc_id ${sdoc_id}."
		return res
	}
	
	public int changeEditStatusInDBto(DocStatus status) {	
		def res = db.getDB().executeUpdate("UPDATE ${sdoc_table} SET sdoc_edit=? WHERE sdoc_id=? and "+
		"sdoc_collection=? and sdoc_lang=?",
			[status.text(), sdoc_id, sdoc_collection, sdoc_lang]) 
		log.trace "Wrote edit status ${status}(${status.text()}) to sdoc_id ${sdoc_id}, ${res} rows were changed."
		return res
	}
	
	public String toString() {
		return "${sdoc_id}:${sdoc_collection}:${sdoc_lang}"
	}
}
