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
class Relation extends DBObject implements JSONable {

	Long rel_id
	String rel_relation

	static Logger log = Logger.getLogger("Relation")

	public Relation(DBTable dbtable) {
		super(dbtable)
	}

	static Relation createNew(DBTable dbtable, row) {
		Relation r = new Relation(dbtable)
		if (row['rel_id']) r.rel_id = row['rel_id']
		if (row['rel_relation']) r.rel_relation = row['rel_relation']
		return r
	}
	

	public Map toMap() {
		return ["rel_id":rel_id, "rel_relation":rel_relation]
	}

	public Map toSimpleMap() {
		return toMap()
	}



	/** Add this Relation o the database. Note that a null is a valid insertion...
	 * return 1 if successfully inserted.
	 */
	public Long addThisToDB() {
		if (!rel_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().tablename} VALUES(0,?)",
				[rel_relation])
		// returns an auto_increment value
		return (long)res[0][0]
	}

	public int removeThisFromDB() {
		if (!rel_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} WHERE rel_id=?",
				[rel_id])
		log.info "Removed Relation ${this} from DB, got $res"
		return res
	}

	boolean equals(Relation rel) {
		return this.toMap().equals(rel.toMap())
	}

	public String toString() {
		return rel_relation
	}
}
