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
import saskia.db.obj.DocPatch
import saskia.db.obj.User

/** This class is an interface for the docpatch table in the Saskia database. 
CREATE TABLE IF NOT EXISTS `docpatch` (
  `pat_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `pat_doc` INT UNSIGNED NOT NULL,
  `pat_user` INT UNSIGNED NOT NULL,
  `pat_date` DATETIME DEFAULT NULL, 
  `pat_patch` TEXT NULL)
 ENGINE = InnoDB;
 );
 */
class DocPatchTable extends DBTable {

	static String tablename = "docpatch"

	static Logger log = Logger.getLogger("DocPatch")
	
	public DocPatchTable(SaskiaDB db) {
		super(db)
	}

	public List<DocPatch> queryDB(String query, ArrayList params) {
		List<DocPatch> res = []
		db.getDB().eachRow(query, params, {row  ->
			res << DocPatch.createNew(this, row)
		})
		return res
	}

	public List<DocPatch> filterFromColumnAndNeedle(List<DocPatch> haystack, Map column = null, needle = null) {

		if (column && needle) {

			if (column == "pat_user") {
				return haystack.findAll{it.pat_user.usr_id == Long.parseLong(needle)}
			} else if (column == "pat_doc") {
				return haystack.findAll{it.pat_doc.doc_id == Long.parseLong(needle)}
			} else if (type[column] == 'Integer' || type[column] == 'Long') {
				return haystack.findAll{it."${column}" == Long.parseLong(needle)}
			} else {
				// The rest of the colums are String
				return haystack.findAll{it."${column}" =~ /(?i)${needle}/}
			}
		}
		return haystack
	}

	public List<DocPatch> filterFromLimitAndOffset(List<DocPatch> haystack, int limit = 0, int offset = 0) {
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

	/** Get a DocPatch from id.
	 * @param id The id as needle.
	 * return DocPatch result object, or null
	 */
	public DocPatch getFromID(Long pat_id) {
		if (!pat_id) return null
		if (!cache) refreshCache()
		return cache[pat_id]
	}

	static DocPatch getFromID(SaskiaDB db, Long id) {
		return db.getDBTable("DocPatchTable").getFromID(id)
	}
}