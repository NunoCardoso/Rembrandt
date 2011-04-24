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
class NEType extends DBObject implements JSONable {


	Long net_id
	String net_type
	static Logger log = Logger.getLogger("NEType")

	public NEType(DBTable dbtable) {
		super(dbtable)
	}
	
	
	public NEType(DBTable dbtable, Long net_id, String net_type) {
		super(dbtable)
		this.net_id = net_id
		this.net_type = net_type
	}
	
	
	static NEType createFromDBRow(DBTable dbtable, row) {
		return new NEType(dbtable, row['net_id'], row['net_type'] )
	}

	public Map toMap() {
		return ["net_id":net_id, "net_type":net_type]
	}

	public Map toSimpleMap() {
		return toMap()
	}

	public Long addThisToDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT IGNORE INTO ${getDBTable().getTablename()} VALUES(0,?)",
				[net_type])
		// returns an auto_increment value
		if (res) {
			net_id = (long)res[0][0]
			getDBTable().updateCacheElement(net_id, net_type)
			log.info "Inserted new NEType in DB: ${this}"
		}
		return net_id
	}

	public int removeThisFromDB() {
		if (!nes_id) return null
		def res = getDBTable().getSaskiaDB().executeUpdate(
				"DELETE FROM ${getDBTable().getTablename()} WHERE net_id=?", [net_id])
		getDBTable().all_type_id.remove(net_type)
		getDBTable().all_id_type.remove(net_id)
		log.info "Removed NEType ${this} from DB, got $res"
		return res
	}

	boolean equals(NEType net) {
		return this.toMap().equals(net.toMap())
	}

	public String toString() {
		return net_type
	}
}
