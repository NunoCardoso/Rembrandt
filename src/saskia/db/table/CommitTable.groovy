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
import saskia.db.obj.Commit
import saskia.db.obj.Doc
import saskia.db.obj.User

class CommitTable extends DBTable {

	static String tablename = "commit"

	static Logger log = Logger.getLogger("Commit")
	
	public CommitTable(SaskiaDB db) {
		super(db)
	}

	public List<Commit> queryDB(String query, ArrayList params) {
		List<Commit> res = []
		db.getDB().eachRow(query, params, {row  ->
			res << Commit.createNew(this, row)
		})
		return res
	}

	public Map listCommits(Doc doc, limit = 10,  offset = 0, column = null, needle = null) {

		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		String where = "WHERE cmm_doc=? "
		List params = [doc.doc_id]
		if (column && needle) {
			switch (Commit.type[column]) {
				case 'String': where += " AND $column LIKE '%${needle}%'"; break
				case 'Long': where += " AND $column=? "; params << Long.parseLong(needle); break
				case 'User':  where += " AND $column = ?"; params << Long.parseLong(needle); break
				case 'Doc':  where += " AND $column = ?"; params << Long.parseLong(needle); break
				case 'Date': where += " AND $column = ?"; params << needle; break
			}
		}

		String query = "SELECT SQL_CALC_FOUND_ROWS * FROM ${tablename} "+
				"$where LIMIT ${limit} OFFSET ${offset} UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), "+
				"NULL, NULL, NULL, NULL"
		log.debug "query = $query params = $params class = "+params*.class
		List u
		try {u = queryDB(query, params) }
		catch(Exception e) {log.error "Error getting commit list: ", e}
		// last "user" is not the user... it's the count.
		Commit fake_commit = u.pop()
		long total = fake_commit.cmm_id
		log.debug "Returning "+u.size()+" results."
		return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
			"column":column, "value":needle, "doc_id":doc.doc_id]
	}

	public List<Commit> filterFromColumnAndNeedle(List<Commit> haystack, Map column = null, needle = null) {

		if (column && needle) {

			if (column == "cmm_user") {
				return haystack.findAll{it.cmm_user.usr_id == Long.parseLong(needle)}
			} else if (column == "cmm_doc") {
				return haystack.findAll{it.cmm_doc.doc_id == Long.parseLong(needle)}
			} else if (Commit.type[column] == 'Integer' || Commit.type[column] == 'Long') {
				return haystack.findAll{it."${column}" == Long.parseLong(needle)}
			} else {
				// The rest of the colums are String
				return haystack.findAll{it."${column}" =~ /(?i)${needle}/}
			}
		}
		return haystack
	}

	public List<Commit> filterFromLimitAndOffset(List<Commit> haystack, int limit = 0, int offset = 0) {
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
	public updateValue(Long cmm_id, String column, newvalue) {

		if (!cmm_id) throw new IllegalStateException("Commit cmm_id is not valid: "+cmm_id)
	
		def newval
		def object

		switch (Commit.type[column]) {
			case 'Integer':
				if (!(newvalue instanceof Integer)) newval = Integer.parseInt(newvalue)
				else newval = newvalue
				break
			case ['Long', 'User', 'Doc']:
				if (newvalue instanceof User) {
					newval = newvalue.usr_id
					object = newvalue
				}
				else if (newvalue instanceof Doc) {
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
				"UPDATE ${tablename} SET ${column}=? WHERE cmm_id=?",
				[newval, cmm_id])
		return res
	}
	
	public Commit getFromID(Long cmm_id) {
		if (!cmm_id) return null
		List<Commit> c = queryDB("SELECT * FROM ${tablename} WHERE cmm_id=?", [cmm_id])
		if (c && c[0].cmm_id) return c[0] else return null
	}
	
	static Commit getFromID(SaskiaDB db, Long id) {
		return db.getDBTable("CommitTable").getFromID(id)
	}
}