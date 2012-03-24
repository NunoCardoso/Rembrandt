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
class Tag extends DBObject implements JSONable  {

	Long tag_id
	String tag_version
	String tag_comment
	static Logger log = Logger.getLogger("Tag")

	public Tag(DBTable dbtable) {
		super(dbtable)
	}

	public Tag(DBTable dbtable, String tag_version, String tag_comment) {
		super(dbtable)
		this.tag_version = tag_version
		this.tag_comment = tag_comment
	}
	
	static Tag createNew(DBTable dbtable, row) {
		Tag t = new Tag(dbtable)
		log.warn(row)
		if (row['tag_id']) t.tag_id = row['tag_id']
		if (row['tag_version']) t.tag_version = row['tag_version']
		if (row['tag_comment']) t.tag_comment = row['tag_comment']
		return t
	}

	Map toMap() {
		return ["tag_id":tag_id, "tag_version":tag_version, "tag_comment":tag_comment]
	}


	Map toSimpleMap() {
		return toMap()
	}

	/** Add this Rembrandt Tag to the database.
	 * @param version The version label. By default, it's own version field.
	 * @param comment The version comment. By default, it's own comment field.
	 * return 1 if successfully inserted.
	 */
	public Long addThisToDB() {
		if (!tag_version) {
			log.error "Can't add a Tag without a valid version! Skipping."
			return null
		}
		if (!getDBTable().cache) getDBTable().refreshCache()
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().tablename} VALUES(0,?,?)",
				[tag_version, tag_comment])
		tag_id = (long)res[0][0]
		getDBTable().cache[tag_id] = this
		log.info "Adding tag to DB: ${this}"
		return tag_id
	}

	public int removeThisFromDB() {
		if (!tag_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} WHERE tag_id=?", [tag_id])
		getDBTable().cache.remove(tag_id)
		log.info "Removing tag ${this} from DB, got $res"
		return res
	}

	boolean equals(Tag t) {
		return this.toMap().equals(t.toMap())
	}

	public String toString() {
		return "${tag_id}:${tag_version}"
	}
}
