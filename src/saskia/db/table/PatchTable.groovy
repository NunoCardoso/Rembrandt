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
import saskia.db.obj.Patch
import saskia.db.obj.User
import saskia.db.obj.Doc

class PatchTable extends DBTable {

	static String tablename = "patch"

	static Logger log = Logger.getLogger("Patch")
	
	public PatchTable(SaskiaDB db) {
		super(db)
	}

	public List<Patch> queryDB(String query, ArrayList params) {
		List<Patch> res = []
		db.getDB().eachRow(query, params, {row  ->
			res << Patch.createNew(this, row)
		})
		return res
	}

	public List<Patch> filterFromColumnAndNeedle(List<Patch> haystack, Map column = null, needle = null) {

		if (column && needle) {

			if (column == "pat_doc") {
				return haystack.findAll{it.pat_doc.doc_id == Long.parseLong(needle)}
			} else if (Patch.type[column] == 'Integer' || Patch.type[column] == 'Long') {
				return haystack.findAll{it."${column}" == Long.parseLong(needle)}
			} else {
				// The rest of the colums are String
				return haystack.findAll{it."${column}" =~ /(?i)${needle}/}
			}
		}
		return haystack
	}

	public List<Patch> filterFromLimitAndOffset(List<Patch> haystack, int limit = 0, int offset = 0) {
		List res2
		if (haystack.isEmpty()) return haystack

		if (limit != 0) {
			int lim = (offset+limit-1)
			if (lim > (haystack.size()-1) ) lim = haystack.size()-1
			res2 = haystack[offset..lim]
		}	else {
			res2 = haystack
		}
		return res2
	}


	/** generic purpose value update on DB and cache */
	public updateValue(Long pat_id, String column, newvalue) {

		if (!pat_id) throw new IllegalStateException("Patch pat_id is not valid: "+pat_id)
	
		def newval
		def object

		switch (Patch.type[column]) {
			case 'Integer':
				if (!(newvalue instanceof Integer)) newval = Integer.parseInt(newvalue)
				else newval = newvalue
				break
			case ['Long', 'Doc']:
				if (newvalue instanceof Doc) {
					newval = newvalue.doc_id
					object = newvalue
				}
				else if (!(newvalue instanceof Long)) newval = Long.parseLong(newvalue)
				break
			case 'String':
				newval = newvalue
				break
		}

		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().tablename} SET ${column}=? WHERE pat_id=?",
				[newval, pat_id])
		return res
	}
	
	public Patch getFromID(Long pat_id) {
		if (!pat_id) return null
		List<Patch> p = queryDB("SELECT * FROM ${tablename} WHERE pat_id=?", [pat_id])
		if (p && p[0].pat_id) return p[0] else return null
	}
	
	static Patch getFromID(SaskiaDB db, Long id) {
		return db.getDBTable("PatchTable").getFromID(id)
	}
}