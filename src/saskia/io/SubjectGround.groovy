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

class SubjectGround {

	static String tablename = "subject_ground"
	long sgr_id
	Subject sgr_subject = null
	Geoscope sgr_geoscope = null
	String sgr_dbpedia_resource
	String sgr_dbpedia_class
	String sgr_wikipedia_category
	String sgr_comment
	
	static Configuration conf = Configuration.newInstance()
	
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("SaskiaDB")
	
	static List<SubjectGround> queryDB(String query, ArrayList params = []) {
	    List<SubjectGround> t = []
	    db.getDB().eachRow(query, params, {row  -> 
	        SubjectGround sg = new SubjectGround()
	        sg.sgr_id = row['sgr_id']
	        sg.sgr_subject = Subject.getFromID(row['sgr_subject'] )
	        if (row['sgr_geoscope']) sg.sgr_geoscope = Geoscope.getFromID(row['sgr_geoscope'] )
	        sg.sgr_dbpedia_resource = row['sgr_dbpedia_resource'] 
	        sg.sgr_dbpedia_class = row['sgr_dbpedia_class'] 
	        sg.sgr_wikipedia_category = row['sgr_wikipedia_category']
	        sg.sgr_comment = row['sgr_comment']
	        t << sg
	    })
	    return t
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
	public long addThisToDB() {
	    def res = db.getDB().executeInsert("INSERT INTO ${tablename}(sgr_subject, sgr_geoscope, sgr_dbpedia_resource, "+
		"sgr_dbpedia_class, sgr_wikipedia_category, sgr_comment) VALUES(?,?,?,?,?,?)", 
		[sgr_subject.sbj_id, sgr_geoscope?.geo_id, sgr_dbpedia_resource, sgr_dbpedia_class, 
			sgr_wikipedia_category, sgr_comment])
	    this.sgr_id = (long)res[0][0]
	   return this.sgr_id
	}	
	
	public String toString() {
	    return "${sgr_id}:${sgr_subject}:${sgr_geoscope}"
	}
}