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
import saskia.db.obj.Tag

/** This class is an interface for the Tag table in the WikiRembrandt database. 
 * It stores tagging information associated to a Rembrandt annotation of documents.
 * Static methods are used to return results from DB, using where clauses.
 * Class methods are used to insert results to DB.  
 */
class TagTable extends DBTable {

	static Logger log = Logger.getLogger("Tag")
	static String tablename = "tag"

	Map<Long,Tag> cache

	public TagTable(SaskiaDB db) {
		super(db)
		cache= [:]
	}

	public List<Tag> queryDB(String query, ArrayList params) {
		List<Tag> res = []
		db.getDB().eachRow(query, params, {row  ->
			res << Tag.createFromDBRow(this.owner, row)
		})
		return res
	}

	static void refreshCache() {
		List<Tag> l = queryDB("SELECT * FROM ${getTablename()}".toString(), [])
		l.each{cache[it.tag_id] = it}
	}

	/** Get a Rembrandt Tag from a version label.
	 * @param version The version label as needle.
	 * return Tag result object, or null
	 */
	public Tag getFromVersion(String tag_version) {
		// version has UNIQUE key
		if (!tag_version) return null
		if (!cache) refreshCache()
		/*Tag t = queryDB("SELECT * FROM ${tablename} WHERE tag_version=?", [tag_version])
		 log.debug "Querying for tag_version $tag_version got Tag $t." 
		 if (t.tag_id) return t else return null*/
		return cache.values().toList().find{it.tag_version == tag_version}
	}

	/** Get a Rembrandt Tag from id.
	 * @param id The id as needle.
	 * return Tag result object, or null
	 */
	public Tag getFromID(Long tag_id) {
		if (!tag_id) return null
		if (!cache) refreshCache()
		/*Tag t = queryDB("SELECT * FROM ${tablename} WHERE tag_id=?", [tag_id])
		 log.debug "Querying for tag_id $tag_id got Tag $t." 
		 if (t.tag_id) return t else return null*/
		return cache[tag_id]
	}

	static Tag getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("saskia.db.table.TagTable").getFromID(id)
	}

}