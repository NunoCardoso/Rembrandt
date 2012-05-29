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
public class Commit extends DBObject implements JSONable {

	Long cmm_id
	Doc cmm_doc
	User cmm_user
	Date cmm_date
	String cmm_commit
	
	static Date nulldate = new Date(0)
	static Logger log = Logger.getLogger("Commit")

	static Map type = ['cmm_id':'Long','cmm_doc':'Doc', 'cmm_user':'User', 'cmm_date':'Date','cmm_commit':'String']

	public Commit(DBTable dbtable) {
		super(dbtable)
	}

	static Commit createNew(DBTable dbtable, row) {
		Commit c = new Commit(dbtable)
		if (row['cmm_id']) c.cmm_id = row['cmm_id']
		if (row['cmm_doc']) c.cmm_doc = dbtable.getSaskiaDB()
			.getDBTable("DocTable")
			.getFromID(row['cmm_doc'])
		if (row['cmm_user']) c.cmm_user= (row['cmm_user'] instanceof User? 
			row['cmm_user'] : 
			dbtable.getSaskiaDB().getDBTable("UserTable").getFromID(row['cmm_user']))
		if (row['cmm_date'] && (Date)row['cmm_date'] != nulldate)
				c.cmm_date = (Date)row['cmm_date'] // it's a java.sql.Timestamp, a subclass of Date
		if (row['cmm_commit']) c.cmm_commit = row['cmm_commit']
		return c
	}

	Map toMap() {
		return ['cmm_id':cmm_id,'cmm_doc':cmm_doc.toMap(),'cmm_user':cmm_user.toSimpleMap(), 
		'cmm_date':cmm_date,'cmm_commit':cmm_commit]
	}

	Map toSimpleMap() {
		return ['cmm_id':cmm_id,'cmm_doc':cmm_doc.toSimpleMap(),'cmm_user':cmm_user.toSimpleMap(), 
		'cmm_date':cmm_date,'cmm_commit':cmm_commit]
	}

	public updateValue(column, value) {
		return getDBTable().updateValue(cmm_id, column, value);
	}

	/** Add this Commit to the database.
	 */	
	public Long addThisToDB() {

		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
			"INSERT INTO ${getDBTable().tablename} VALUES(0,?,?,?,?)",
			[
				cmm_doc.doc_id,
				cmm_user.usr_id,
				cmm_date,
				cmm_commit
			])
		// returns an auto_increment value
		cmm_id = (Long)res[0][0]
		log.info "Adding Commit to DB: ${this}"
		return cmm_id
	}

	public int removeThisFromDB() {
		if (!cmm_id) throw new IllegalStateException("Can't remove myself from DB if I don't have a cmm_id")
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} where cmm_id=?",[cmm_id])
		log.info "Removing Commit to DB: ${this}, got res $res"
		return res
	}

	public String toString() {
		return "${cmm_id}:${cmm_doc.doc_id}:${cmm_user.usr_login}"
	}
}
