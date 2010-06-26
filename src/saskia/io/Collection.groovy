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
class Collection {

	static String col_table = "collection"
	Long col_id
	String col_name
        String col_lang
	String col_comment
	Boolean col_new_user_can_read
	
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("SaskiaDB")
	static Map<Long,Collection> cacheIDCollection = [:]

	static List<Collection> queryDB(String query, ArrayList params) {
	    List l = []
	    Collection c = new Collection()
		db.getDB().eachRow(query, params, {row  -> 
			c = new Collection()
			c.col_id = row['col_id']
			c.col_name = row['col_name']
			c.col_lang = row['col_lang']
			c.col_comment = row['col_comment']	
			c.col_new_user_can_read = row['col_new_user_can_read']
			l << c
		})
		if (l) return l else return null
	}

	/**
	 * Load the internal cache for collections
	 */
	static void refreshCache() {
	    List l = queryDB("SELECT * FROM ${col_table}".toString(), [])
	    l.each{cacheIDCollection[it.col_id] = it}
	}
    
	static List<Collection> filterFromColumnAndNeedle(column = null, needle = null) {
	    
	    if (!cacheIDCollection) refreshCache()
	    List<Collection> res = []
	             
	    println "Got column $column and needle $needle"
	                            
	    if (column && needle) {
		
		if (column == "col_id") {
		    Collection col = cacheIDCollection[Long.parseLong(needle)]
		    if (col) res << col
	         } else {	
	             // The rest of the colums are String
	             res.addAll(cacheIDCollection.values().toList().findAll{it."${column}" =~ /(?i)${needle}/})
	         }
	    } else {
		res = cacheIDCollection.values().toList()		
	    }
	    return res
	}
	
	static List<Collection> filterFromLimitAndOffset( List<Collection> res, limit = 0, offset = 0) {
	    List<Collection> res2
	
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
	 */
	static HashMap getCollections( limit = 0, offset = 0, column, needle) {	    
	    List<Collection> res = filterFromColumnAndNeedle(column, needle)         
	    List<Collection> res2 = filterFromLimitAndOffset(res, limit, offset)	   
	    return ["total":res.size(), "offset":offset, "limit":limit, "page":res2.size(),
	            "result":res2, "column":column, "value":needle]
	}	
	
	/** 
	 * same as getCollections, but filtered to showable collections for user
	 */
	static HashMap getShowableCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {
	    
	    List<Collection> res = filterFromColumnAndNeedle(column, needle)      
	    // Filter the res list with showable collections
	    List<Collection> res2 = res.findAll{collection -> user.canReadCollection(collection)}	    
	    List<Collection> res3 = filterFromLimitAndOffset(res2, limit, offset)	
	    
	    return ["total":res2.size(), "offset":offset, "limit":limit, "page":res3.size(),
	            "result":res3, "column":column, "value":needle]
	}
	
	/** 
	 * same as getCollections, but filtered to showable collections for user
	 */
	static HashMap getWritableCollectionsForUser(User user, limit = 0, offset = 0, column = null, needle = null) {
	    
	    List<Collection> res = filterFromColumnAndNeedle(column, needle)      
	    // Filter the res list with showable collections
	    List<Collection> res2 = res.findAll{collection -> user.canWriteCollection(collection)}	    
	    List<Collection> res3 = filterFromLimitAndOffset(res2, limit, offset)	
	    
	    return ["total":res2.size(), "offset":offset, "limit":limit, "page":res3.size(),
	            "result":res3, "column":column, "value":needle]
	}
	
	static Collection getFromName(String name) {
		if (!name) return null   
		if (!cacheIDCollection) refreshCache()
		List c = cacheIDCollection.values().toList().findAll{it.col_name==name}
		log.debug "Querying for collection $name got Collection $c." 
		if (c) return c[0] else return null
	}
	
	static Collection getFromID(Long id) {
		// version has UNIQUE key
		if (!id) return null
		if (!cacheIDCollection) refreshCache()
		Collection c = cacheIDCollection[id]
		log.debug "Querying for collection $id got Collection $c." 
		if (c) return c else return null
	}
	 
	static updateValue(long col_id, String column, newvalue) {          
	    def res = db.getDB().executeUpdate("UPDATE ${col_table} SET ${column}=? WHERE col_id=?",[newvalue, col_id])
	    cacheIDCollection[col_id][column] = newvalue
	    return res
	}
    
	/* 
	 * Returns the number of REMBRANDTed documents for this collection 
	 */
	public int getNumberOfRembrandtedDocuments() {
	    int i
	    db.getDB().eachRow("SELECT count(doc_id) from ${RembrandtedDoc.doc_table}, ${RembrandtedDoc.chd_table} "+
		    "WHERE doc_id=chd_document and chd_collection=?",[col_id], {row -> i = row[0]})
	    return i
	}
    
	// used to add new collections.
	public long addThisToDB() {	
	   def res = db.getDB().executeInsert("INSERT INTO ${col_table} VALUES(0,?,?,?,?)", 
	      [col_name, col_lang, col_comment, col_new_user_can_read])
		// returns an auto_increment value
	        col_id = (long)res[0][0]
	        cacheIDCollection[col_id] = this                      
		return col_id
	}	
    
	
	public removeThisToDB() {	
	    def res = db.getDB().executeUpdate("DELETE FROM ${col_table} where col_id=?",[col_id]) 
	    cacheIDCollection.remove(col_id)
	    return res
	}
    
	public String toString() {
		return "${col_id}:${col_lang}:${col_name}"
	}
}