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

package saskia.io

import org.apache.log4j.*
import saskia.bin.Configuration
import rembrandt.obj.Sentence

class SubjectGround extends DBObject implements JSONable {

	static String tablename = "subject_ground"
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
	
	static Configuration conf = Configuration.newInstance()
	
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("SubjectGround")
	
	static List<SubjectGround> queryDB(String query, ArrayList params = []) {
	    List<SubjectGround> t = []
	    db.getDB().eachRow(query, params, {row  -> 
	        SubjectGround sg = new SubjectGround()
	        sg.sgr_id = row['sgr_id']
	        if (row['sgr_subject']) sg.sgr_subject = Subject.getFromID(row['sgr_subject'] )
	        if (row['sgr_geoscope']) sg.sgr_geoscope = Geoscope.getFromID(row['sgr_geoscope'] )
	        sg.sgr_dbpedia_resource = row['sgr_dbpedia_resource'] 
	        sg.sgr_dbpedia_class = row['sgr_dbpedia_class'] 
	        sg.sgr_wikipedia_category = row['sgr_wikipedia_category']
	        sg.sgr_comment = row['sgr_comment']
	        t << sg
	    })
	    return t
	}

	Map toMap() {
		return ['sgr_id':sgr_id, 'sgr_subject':sgr_subject.toMap(), 'sgr_geoscope':sgr_geoscope.toSimpleMap(),
		'sgr_dbpedia_resource':sgr_dbpedia_resource, 'sgr_dbpedia_class':sgr_dbpedia_class, 
		'sgr_wikipedia_category':sgr_wikipedia_category, 'sgr_comment':sgr_comment]
	}
	

	public Map toSimpleMap() {
	    return toMap()
	}
	
   static Map listSubjectGrounds(limit = 10, offset = 0, column = null, needle = null) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0
		
		String where = ""
		String from = " FROM ${tablename}"	
		List params = []	
		if (column && needle) {
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
		List<Entity> u = queryDB(query, params) 
	
		// last "item" it's the count.
		int total = (int)(u.pop().sgr_id)
		log.debug "Returning "+u.size()+" results."
		return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
	        "column":column, "value":needle]
    }
	

	static SubjectGround getFromID(Long sgr_id) {
	    if (!sgr_id) return null	    
	    List<SubjectGround> sg = queryDB("SELECT * FROM ${tablename} WHERE sgr_id=?", [sgr_id])
	    log.trace "Querying for sgr_id $sgr_id got SubjectGround $sg." 
	    if (sg) return sg[0] 
	    return null
	    
	}	 

	public updateValue(column, value) {
	    def newvalue	    
	    switch (type[column]) {
	        case 'String': newvalue = value; break
	        case 'Long': newvalue = Long.parseLong(value); break
	        case 'Subject': newvalue = Long.parseLong(value); break
	        case 'Geoscope': newvalue = Long.parseLong(value); break
	    }
	    def res = db.getDB().executeUpdate("UPDATE ${tablename} SET ${column}=? WHERE sgr_id=?",[newvalue, sgr_id])
	    return res
	}
	
	/** Get a Subject from id.
	 * @param id The id as needle.
	 * return the Subject result, or null
	 */
	static List<SubjectGround> getFromSubjectIDAndGeoscopeID(Long subject_id, Long geoscope_id) {
	   if (!subject_id) return null
	   String where
	   List params
	   if (!geoscope_id) {
	       where = " sgr_subject=? and sgr_geoscope IS NULL "
	       params = [subject_id]   
	   } else {
	       where = " sgr_subject=? and sgr_geoscope=? "
	       params = [subject_id, geoscope_id]   
	   }
	   List<SubjectGround> sgr = queryDB("SELECT * FROM $tablename WHERE $where", params)
	   return sgr	  
	}	
	
	/** Add this NECategory o the database. Note that a null is a valid insertion...
	 * return 1 if successfully inserted.
	 */	
	public Long addThisToDB() {
	    def res = db.getDB().executeInsert("INSERT INTO ${tablename}(sgr_subject, sgr_geoscope, sgr_dbpedia_resource, "+
		"sgr_dbpedia_class, sgr_wikipedia_category, sgr_comment) VALUES(?,?,?,?,?,?)", 
		[sgr_subject.sbj_id, sgr_geoscope?.geo_id, sgr_dbpedia_resource, sgr_dbpedia_class, 
			sgr_wikipedia_category, sgr_comment])
	   sgr_id = (long)res[0][0]
		log.info "Adding subject_ground to DB: ${this}"
	   return sgr_id
	}	
	
	public int removeThisFromDB() {
		if (!sgr_id) return null
		def res = db.getDB().executeUpdate("DELETE FROM ${tablename} WHERE sgr_id=?", [sgr_id])
		log.info "Removing subject_ground to DB: ${this}, got res $res"
		return res	    
   }
	
	public String toString() {
	    return "${sgr_id}:${sgr_subject}:${sgr_geoscope}"
	}
}