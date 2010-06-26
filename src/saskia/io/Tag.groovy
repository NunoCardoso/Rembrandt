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

/** This class is an interface for the Tag table in the WikiRembrandt database. 
  * It stores tagging information associated to a Rembrandt annotation of documents.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  */
class Tag {

	static String tag_table = "tag"
	Long tag_id
	String tag_version
	String tag_comment
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("SaskiaDB")
	
	static Map<Long,Tag> cache = [:]

	static List<Tag> queryDB(String query, ArrayList params) {
	    List<Tag> res = []
	    db.getDB().eachRow(query, params, {row  -> 
	        Tag t = new Tag()
	    	t.tag_id = row['tag_id']
	    	t.tag_version = row['tag_version']
	    	t.tag_comment = row['tag_comment']
	    	res << t                    
	    })
	    return res
	}
	
	static void refreshCache() {
	    List<Tag> l = queryDB("SELECT * FROM ${tag_table}".toString(), [])
	    l.each{cache[it.tag_id] = it}
	}
	
	/** Get a Rembrandt Tag from a version label.
	 * @param version The version label as needle.
	 * return Tag result object, or null
	 */
	static Tag getFromVersion(String tag_version) {
	    // version has UNIQUE key
	    if (!tag_version) return null
	    if (!cache) refreshCache()
	    /*Tag t = queryDB("SELECT * FROM ${tag_table} WHERE tag_version=?", [tag_version])
		log.debug "Querying for tag_version $tag_version got Tag $t." 
		if (t.tag_id) return t else return null*/
	    return cache.values().toList().find{it.tag_version == tag_version}
	}
	
	/** Get a Rembrandt Tag from id.
	 * @param id The id as needle.
	 * return Tag result object, or null
	 */
	static Tag getFromID(Long tag_id) {
	    if (!tag_id) return null
	    if (!cache) refreshCache()
	    /*Tag t = queryDB("SELECT * FROM ${tag_table} WHERE tag_id=?", [tag_id])
		log.debug "Querying for tag_id $tag_id got Tag $t." 
		if (t.tag_id) return t else return null*/
	    return cache[tag_id]
	}	
	
	Map toMap() {
	    return ["tag_id":tag_id, "tag_version":tag_version, "tag_comment":tag_comment]
	}
	
	/** Add this Rembrandt Tag to the database.
	 * @param version The version label. By default, it's own version field.
	 * @param comment The version comment. By default, it's own comment field.
	 * return 1 if successfully inserted.
	 */	
	public long addThisToDB() {
	    if (!tag_version) {
		log.error "Can't add a Tag without a valid version! Skipping."
		return null
	    }
	    if (!cache) refreshCache()	
	    def res = db.getDB().executeInsert("INSERT INTO ${tag_table} VALUES(0,?,?)", 
		[tag_version, tag_comment])
		// returns an auto_increment value
	    tag_id = (long)res[0][0]
	    cache[tag_id] = this
	    return tag_id                           
	}	
	
	boolean equals(Entity e) {
		return this.toMap().equals(e.toMap())
	}
	
	public String toString() {
	    return "${tag_id}:${tag_version}"
	}
}