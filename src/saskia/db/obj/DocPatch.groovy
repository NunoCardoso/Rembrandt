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
public class DocPatch extends DBObject implements JSONable {

	Long pat_id
	RembrandtedDoc pat_doc
	User pat_user
	Date pat_date
	String pat_patch
	
	static Date nulldate = new Date(0)
	static Logger log = Logger.getLogger("DocPatch")

	static Map type = ['pat_id':'Long','pat_doc':'RembrandtedDoc', 'pat_user':'User', 'pat_date':'Date','pat_patch':'String']

	public DocPatch(DBTable dbtable) {
		super(dbtable)
	}

	static DocPatch createNew(DBTable dbtable, row) {
		DocPatch dp = new DocPatch(dbtable)
		if (row['pat_id']) dp.pat_id = row['pat_id']
		if (row['pat_doc']) dp.pat_doc = dbtable.getSaskiaDB()
			.getDBTable("RembrandtedDocTable")
			.getFromID(row['pat_doc'])
		if (row['pat_user']) dp.pat_user= (row['pat_user'] instanceof User? 
			row['pat_user'] : 
			dbtable.getSaskiaDB().getDBTable("UserTable").getFromID(row['pat_user']))
		if (row['pat_date'] && (Date)row['pat_date'] != nulldate)
				dp.pat_date = (Date)row['pat_date'] // it's a java.sql.Timestamp, a subclass of Date
		if (row['pat_patch']) dp.pat_patch = row['pat_patch']
		return dp
	}

	Map toMap() {
		return ['pat_id':pat_id,'pat_doc':pat_doc.toMap(),'pat_user':pat_user.toSimpleMap(), 'pat_date':pat_date,'pat_patch':pat_patch]
	}

	Map toSimpleMap() {
		return toMap()
	}

	public updateValue(column, value) {
		return getDBTable().updateValue(pat_id, column, value);
	}

	/** Add this DocPatch to the database.
	 * @param version The version label. By default, it's own version field.
	 * @param comment The version comment. By default, it's own comment field.
	 * return 1 if successfully inserted.
	 */	
	public Long addThisToDB() {

		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().tablename} VALUES(0,?,?,?,?)",
				[
					pat_doc.doc_id,
					pat_user.usr_id,
					pat_date,
					pat_patch
				])
		// returns an auto_increment value
		pat_id = (Long)res[0][0]
		getDBTable().cache[pat_id] = this
		log.info "Adding DocPatch to DB: ${this}"
		return pat_id
	}

	public int removeThisFromDB() {
		if (!pat_id) throw new IllegalStateException("Can't remove myself from DB if I don't have a pat_id")
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} where pat_id=?",[pat_id])
		getDBTable().cache.remove(pat_id)
		log.info "Removing DocPatch to DB: ${this}, got res $res"
		return res
	}

	public String toString() {
		return "${pat_id}:${pat_doc.doc_id}"
	}

}
