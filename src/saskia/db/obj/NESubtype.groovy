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

import saskia.db.table.DBTable
import org.apache.log4j.Logger


/**
 * @author Nuno Cardoso
 *
 */
class NESubtype extends DBObject implements JSONable{

	Long nes_id
	String nes_subtype

	static Logger log = Logger.getLogger("NESubtype")

	public NESubtype(DBTable dbtable) {
		super(dbtable)
	}
	
	public NESubtype(DBTable dbtable, Long nes_id, String nes_subtype) {
		super(dbtable)
		this.nes_id = nes_id
		this.nes_subtype = nes_subtype
	}
	
	static NESubtype createFromDBRow(dbtable, row) {
		return new NESubtype(dbtable, row['nes_id'], row['nes_subtype'] )
	}
	
	public Map toMap() {
		return ["nes_id":nes_id, "nes_subtype":nes_subtype]
	}

	public Map toSimpleMap() {
		return toMap()
	}
	
	
	public Long addThisToDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
			"INSERT IGNORE INTO ${getDBTable().getTablename()} VALUES(0,?)", [nes_subtype])
		// returns an auto_increment value
		if (res) {
			nes_id = (long)res[0][0]
			getDBTable().updateCacheElement(nes_id, nes_subtype)
			log.info "Inserted new NESubtype in DB: ${this}"
		}
		return nes_id
	}

	public int removeThisFromDB() {
		if (!nes_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
			"DELETE FROM ${getDBTable().getTablename()} WHERE nes_id=?", [nes_id])
		getDBTable().all_subtype_id.remove(nes_subtype)
		getDBTable().all_id_subtype.remove(nes_id)
		log.info "Removed NESubtype ${this} from DB, got $res"
		return res
	}


	boolean equals(NESubtype nes) {
		return this.toMap().equals(nes.toMap())
	}

	public String toString() {
		return nes_subtype
	}
}
