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

import saskia.db.table.DBTable
import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 *
 */
public class Subject extends DBObject implements JSONable {

	Long sbj_id
	String sbj_subject
	Map<String,String> subject

	static Logger log = Logger.getLogger("Subject")
	static Map type = ['sbj_id':'Long', 'sbj_subject':'String'] 

	public Subject(DBTable dbtable, Long sbj_id, String sbj_subject) {
		super(dbtable)
		this.sbj_id = sbj_id
		this.sbj_subject = sbj_subject
	}

	static Subject createFromDBRow(DBTable dbtable, row) {

		Subject s = new Subject(dbtable, row['sbj_id'], row['sbj_subject'] )
		s.subject = Subject.parseSubject(s.sbj_subject) // reads sbj_subject, populates subject Map
		return s
	}


	public Map toMap() {
		return ['sbj_id':sbj_id, 'sbj_subject':sbj_subject, 'subject':subject]
	}

	public Map toSimpleMap() {
		return toMap()
	}

	String unparseSubject(Map map) {
		if (!map) return
				List s = []
						// convert something like "en:[xxx];pt:[xxx]" into a hash[lang]
						map.each{k, v ->  s << "${k}:${v}"}
		return s.join(";")    
	}

	static Map parseSubject(String subject) {
		if (!subject) return
				Map s = [:]
						// convert something like "en:[xxx];pt:[xxx]" into a hash[lang]
						List itens = subject.split(/;/)
						itens?.each{
			List fields = it.split(/:/)
					s[fields[0]] = fields[1] 
		}
		return s
	}

	static updateValue(Long id, column, value) {
		Subject sbj = Subject.getFromID(id)
				if (!id) return -1
						return sbj.updateValue(column, value)
	}

	public updateValue(column, value) {
		def newvalue	    
		switch (type[column]) {
			case 'String': newvalue = value; break
			case 'Long': newvalue = Long.parseLong(value); break
		}
		def res = db.getDB().executeUpdate("UPDATE ${tablename} SET ${column}=? WHERE sbj_id=?",[newvalue, sbj_id])
				cacheSubject.each{key, valuelist -> 
				int index = valuelist.indexOf(this)
				if (index) cacheSubject[key][index][column] = newvalue
		}
		cacheID[this][column] = newvalue
				return res
	}

	/** Add this NECategory o the database. Note that a null is a valid insertion...
	 * return 1 if successfully inserted.
	 */	
	public Long addThisToDB() {
		if (!subject) throw new IllegalStateException ("I have to parse subject before putting in the DB!")
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${tablename} VALUES(0,?)", [sbj_subject])
				// add to the cache. Check both en and pt strings, add to cache
				this.sbj_id = (long)res[0][0]
						if (!cacheID.containsKey(this.sbj_id)) cacheID[this.sbj_id] = this
						getDBTable().addToSubjectCache(this) 		
						log.info "Adding subject to DB: ${this}"
						return this.sbj_id
	}	

	public int removeThisFromDB() {
		if (!sbj_id) return null
				def res = getDBTable().getSaskiaDB().getDB().executeUpdate("DELETE FROM ${tablename} WHERE sbj_id=?", [sbj_id])
				cacheSubject.each{key, valuelist -> 
				if (valuelist.contains(this)) cacheSubject[key].remove(this)
		}
		getDBTable().entityIDCache.remove(sbj_id)
		log.info "Removing subject ${this} from DB, got $res"
		return res	    
	}


	public String toString() {
		return sbj_id
	}

}
