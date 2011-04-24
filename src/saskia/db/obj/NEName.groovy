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
class NEName extends DBObject implements JSONable {


	long nen_id
	String nen_name
	int nen_nr_terms

	static Logger log = Logger.getLogger("NEName")


	public NEName(DBTable dbtable) {
		super(dbtable)
	}
	
	static NEName createNew(DBTable dbtable, row) {
		NEName n = new NEName(dbtable)
		if (row['nen_id']) n.nen_id = row['nen_id']
		if (row['nen_name']) n.nen_name = row['nen_name']
		if (row['nen_nr_terms']) n.nen_nr_terms = (int)(row['nen_nr_terms'])
		return n
	}

	public Map toMap() {
		return ["nen_id":nen_id, "nen_name":nen_name]
	}

	public Map toSimpleMap() {
		return toMap()
	}

	boolean equals(NECategory nec) {
		return this.toMap().equals(nec.toMap())
	}

	/** Add this NEName to the database. Note that a null is a valid insertion...
	 * return 1 if successfully inserted.
	 */
	public Long addThisToDB() {
		// returns an auto_increment value
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().tablename} VALUES(0,?,?)",
				[nen_name, nen_nr_terms])
		nen_id = (long)res[0][0]
		getDBTable().idCache[nen_id] = this
		getDBTable().nameCache[nen_name] = this
		log.info "Inserted new NEName in DB: ${this}"
		return nen_id
	}

	public int removeThisFromDB() {
		if (!nen_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} WHERE nen_id=?",
				[nen_id])
		getDBTable().idCache.remove(nen_id)
		getDBTable().nameCache.remove(nen_name)
		log.info "Removed NEName ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return nen_name
	}
}
