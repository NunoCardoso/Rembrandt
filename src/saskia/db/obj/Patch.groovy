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
public class Patch extends DBObject implements JSONable {

	Long pat_id
	Doc pat_doc
	Long pat_version
	String pat_patch
	
	static Date nulldate = new Date(0)
	static Logger log = Logger.getLogger("Patch")

	static Map type = ['pat_id':'Long','pat_doc':'Doc', 'pat_version':'Long','pat_patch':'String']

	public Patch(DBTable dbtable) {
		super(dbtable)
	}

	static Patch createNew(DBTable dbtable, row) {
		Patch p = new Patch(dbtable)
		if (row['pat_id']) p.pat_id = row['pat_id']
		if (row['pat_doc']) p.pat_doc = dbtable.getSaskiaDB()
			.getDBTable("PatchTable")
			.getFromID(row['pat_doc'])
		if (row['pat_version'])  p.pat_version = row['pat_version']
		if (row['pat_date'] && (Date)row['pat_date'] != nulldate)
				p.pat_date = (Date)row['pat_date'] // it's a java.sql.Timestamp, a subclass of Date
		if (row['pat_patch']) p.pat_patch = row['pat_patch']
		return p
	}

	Map toMap() {
		return ['pat_id':pat_id,'pat_doc':pat_doc.toMap(),'pat_version':pat_version, 
		'pat_date':pat_date,'pat_patch':pat_patch]
	}

	Map toSimpleMap() {
		return toMap()
	}

	public updateValue(column, value) {
		return getDBTable().updateValue(pat_id, column, value);
	}

	/** Add this Patch to the database.
	 */	
	public Long addThisToDB() {

		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
			"INSERT INTO ${getDBTable().tablename} VALUES(0,?,?,?,?)",
			[
				pat_doc.doc_id,
				pat_version,
				pat_date,
				pat_patch
			])
		// returns an auto_increment value
		pat_id = (Long)res[0][0]
		log.info "Adding Patch to DB: ${this}"
		return pat_id
	}

	public int removeThisFromDB() {
		if (!pat_id) throw new IllegalStateException("Can't remove myself from DB if I don't have a pat_id")
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} where pat_id=?",[pat_id])
		log.info "Removing Patch from DB: ${this}, got res $res"
		return res
	}

	public String toString() {
		return "${pat_id}:${pat_doc.doc_id}:${pat_version}"
	}
}
