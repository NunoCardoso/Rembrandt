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

import saskia.db.obj.Type
import saskia.db.database.SaskiaDB


class TypeTable {

	static String tablename = "type"
	
	static Logger log = Logger.getLogger("Type")

	Map<Long,Type> cache

	public TypeTable(DBTable dbtable) {
		super(db)
		cache= [:]
	}
	
	public List<Type> queryDB(String query, ArrayList params = []) {
		List<Type> t = []
		db.getDB().eachRow(query, params, {row  -> 
			t << Type.createFromDBRow(this.owner, row)
		})
		return t
	}
	
	public void refreshCache() {
	    List<Type> l = queryDB("SELECT * FROM ${tablename}".toString(), [])
	    l.each{cache[it.typ_id] = it}
	}
	
	/** Get all NE categories. It's easier to have them in memory, than hammering the DB 
	 * return List<NECategory> A list of NECategory objects
	 */
	public Map getAllTypes() {
		def map = [:]
		def typ = queryDB("SELECT * FROM ${tablename}")
		log.debug "Searched for all doc types, got ${typ.size()} entries."
		typ.each{ map[it.typ_name] = it.typ_id}
		return map 
	}
	
	/** Get a NECategory from id.
	 * @param id The id as needle.
	 * return the NECategory result object, or null
	 */
	public Type getFromID(long typ_id) {
		if (!typ_id) return null
		Type typ = queryDB("SELECT * FROM ${tablename} WHERE typ_id=?", [typ_id])?.getAt(0)
		log.debug "Querying for typ_id $typ_id got Type $typ." 
		if (typ.typ_id) return typ else return null
	}	
	

}