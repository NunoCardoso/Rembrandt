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

import saskia.db.obj.Geoscope
import saskia.db.obj.Subject
import saskia.db.table.DBTable

/**
 * @author Nuno Cardoso
 *
 */
class SubjectGround extends DBObject implements JSONable {

	long sgr_id
	Subject sgr_subject = null
	Geoscope sgr_geoscope = null
	String sgr_dbpedia_resource
	String sgr_dbpedia_class
	String sgr_wikipedia_category
	String sgr_comment

	static Map type = ['sgr_id':'Long', 'sgr_subject':'Subject', 'sgr_geoscope':'Geoscope',
		'sgr_dbpedia_resource':'String','sgr_dbpedia_class':'String', 'sgr_wikipedia_category':'String',
		'sgr_comment':'String']

	public SubjectGround(DBTable dbtable) {
		super(dbtable)
	}

	static SubjectGround createFromDB(DBTable dbtable, row) {
		SubjectGround sg = new SubjectGround(dbtable)
		sg.sgr_id = row['sgr_id']
		if (row['sgr_subject']) sg.sgr_subject = Subject.getFromID(row['sgr_subject'] )
		if (row['sgr_geoscope']) 
			sg.sgr_geoscope = (row['sgr_geoscope'] instanceof Geoscope ? 
			row['sgr_geoscope'] : dbtable.getSaskiaDB().getDBTable("GeoscopeTable").getFromID(row['sgr_geoscope'] ) )
		sg.sgr_dbpedia_resource = row['sgr_dbpedia_resource']
		sg.sgr_dbpedia_class = row['sgr_dbpedia_class']
		sg.sgr_wikipedia_category = row['sgr_wikipedia_category']
		sg.sgr_comment = row['sgr_comment']
		return sg
	}

	Map toMap() {
		return ['sgr_id':sgr_id, 'sgr_subject':sgr_subject.toMap(), 'sgr_geoscope':sgr_geoscope.toSimpleMap(),
			'sgr_dbpedia_resource':sgr_dbpedia_resource, 'sgr_dbpedia_class':sgr_dbpedia_class,
			'sgr_wikipedia_category':sgr_wikipedia_category, 'sgr_comment':sgr_comment]
	}


	public Map toSimpleMap() {
		return toMap()
	}

	public updateValue(column, value) {
		def newvalue
		switch (type[column]) {
			case 'String': newvalue = value; break
			case 'Long': newvalue = Long.parseLong(value); break
			case 'Subject': newvalue = Long.parseLong(value); break
			case 'Geoscope': newvalue = Long.parseLong(value); break
		}
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().tablename} SET ${column}=? WHERE sgr_id=?",
				[newvalue, sgr_id])
		return res
	}


	/** Add this NECategory o the database. Note that a null is a valid insertion...
	 * return 1 if successfully inserted.
	 */
	public Long addThisToDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().tablename}(sgr_subject, sgr_geoscope, sgr_dbpedia_resource, "+
				"sgr_dbpedia_class, sgr_wikipedia_category, sgr_comment) VALUES(?,?,?,?,?,?)",
				[
					sgr_subject.sbj_id,
					sgr_geoscope?.geo_id,
					sgr_dbpedia_resource,
					sgr_dbpedia_class,
					sgr_wikipedia_category,
					sgr_comment
				])
		sgr_id = (long)res[0][0]
		log.info "Adding subject_ground to DB: ${this}"
		return sgr_id
	}

	public int removeThisFromDB() {
		if (!sgr_id) return null
		def res = getDBTable().getSaskiaDB()..getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} WHERE sgr_id=?", [sgr_id])
		log.info "Removing subject_ground to DB: ${this}, got res $res"
		return res
	}

	public String toString() {
		return "${sgr_id}:${sgr_subject}:${sgr_geoscope}"
	}
}
