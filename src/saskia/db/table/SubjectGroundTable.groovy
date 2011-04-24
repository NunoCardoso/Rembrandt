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

import saskia.db.obj.*

class SubjectGroundTable extends DBTable {

	String tablename = "subject_ground"

	static Logger log = Logger.getLogger("SubjectGround")

	static List<SubjectGround> queryDB(String query, ArrayList params = []) {
		List<SubjectGround> t = []
		db.getDB().eachRow(query, params, {row  ->
			t << SubjectGround.createFromDBRow(this, row)
		})
		return t
	}



	public Map listSubjectGrounds(limit = 10, offset = 0, column = null, needle = null) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		String where = ""
		String from = " FROM ${tablename}"
		List params = []if (column && needle) {
			switch (type[column]) {
				case 'String': where += " WHERE $column LIKE '%${needle}%'"; break
				case 'Long': where += " WHERE $column=? "; params << Long.parseLong(needle); break
				case 'Subject': where += " WHERE $column=?"; params <<  Long.parseLong(needle); break
				case 'Geoscope': where += " WHERE $column=?"; params <<  Long.parseLong(needle); break
			}
		}

		String query = "SELECT SQL_CALC_FOUND_ROWS ${tablename}.* $from $where LIMIT ${limit} OFFSET ${offset} "+
		"UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), NULL, NULL, NULL, NULL, NULL, NULL"
		//log.debug "query = $query params = $params class = "+params*.class
		List<EntityTable> u = queryDB(query, params)

		// last "item" it's the count.
		int total = (int)(u.pop().sgr_id)
		log.debug "Returning "+u.size()+" results."
		return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
			"column":column, "value":needle]
	}


	public SubjectGround getFromID(Long sgr_id) {
		if (!sgr_id) return null
		List<SubjectGround> sg = queryDB("SELECT * FROM ${tablename} WHERE sgr_id=?", [sgr_id])
		log.trace "Querying for sgr_id $sgr_id got SubjectGround $sg."
		if (sg) return sg[0]
		return null

	}


	/** Get a Subject from id.
	 * @param id The id as needle.
	 * return the Subject result, or null
	 */
	public List<SubjectGround> getFromSubjectIDAndGeoscopeID(Long subject_id, Long geoscope_id) {
		if (!subject_id) return null
		String where
		List params
		if (!geoscope_id) {
			where = " sgr_subject=? and sgr_geoscope IS NULL "
			params = [subject_id]} else {
			where = " sgr_subject=? and sgr_geoscope=? "
			params = [subject_id, geoscope_id]}
		List<SubjectGround> sgr = queryDB("SELECT * FROM $tablename WHERE $where", params)
		return sgr
	}

}