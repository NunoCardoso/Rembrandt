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
import saskia.db.obj.DBObject;
import saskia.db.obj.JSONable;
import saskia.db.table.EntityTable;
import rembrandt.obj.Sentence

class Subject extends DBObject implements JSONable {

	static String tablename = "subject"
	Long sbj_id
	String sbj_subject
	Map<String,String> subject
	
	static Configuration conf = Configuration.newInstance()
	
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("Subject")
	static Map type = ['sbj_id':'Long', 'sbj_subject':'String'] 
            
	static Map conceptList = [:] // to be made, creating a Map that can be used for conceptMatch
		
	// cacheSubject[terms] = Map<Lang:List<Subject>>
	static LinkedHashMap<String,List<Subject>> cacheSubject = \
          new LinkedHashMap(conf.getInt("saskia.subject.cache.number",1000), 0.75f, true) // true: access order.  
	
	// NOTE: this has a id -> sbj_subject STRING, NOT subject MAP.
	static LinkedHashMap<Long,Subject> cacheID = \
          new LinkedHashMap(conf.getInt("saskia.subject.cache.number",1000), 0.75f, true) // true: access order.  

	static List<Subject> queryDB(String query, ArrayList params = []) {
	    List<Subject> t = []
	    db.getDB().eachRow(query, params, {row  -> 
	        Subject s = new Subject(sbj_id:row['sbj_id'], sbj_subject:row['sbj_subject'] )
	        s.subject = Subject.parseSubject(s.sbj_subject) // reads sbj_subject, populates subject Map
	        t << s
	    })
	    return t
	}
	
	static int deleteSubject(Long id) {
	    Subject s = Subject.getFromID(id)
	    return s?.removeThisFromDB()
	}

	public Map toMap() {
	    return ['sbj_id':sbj_id, 'sbj_subject':sbj_subject, 'subject':subject]
	}

	public Map toSimpleMap() {
	    return toMap()
	}
	
	static Map listSubjects(limit = 10, offset = 0, column = null, needle = null) {
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
		    }
		}
		    
		String query = "SELECT SQL_CALC_FOUND_ROWS * $from $where LIMIT ${limit} OFFSET ${offset} "+
		"UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), NULL"
		//log.debug "query = $query params = $params class = "+params*.class
		List<EntityTable> u 
		try {u = queryDB(query, params) }
		catch(Exception e) {log.error "Error getting Subject list: ", e}
		// last "item" it's the count.
		int total = (int)(u.pop().sbj_id)
		log.debug "Returning "+u.size()+" results."
		return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
		        "column":column, "value":needle]
	}

	static List getConceptListForLang(String lang) {
	    if (!lang) throw new IllegalStateException("Give me a lang!")
	    if (!conceptList.containsKey(lang)) conceptList[lang] = makeConceptList(lang)
	    return conceptList[lang]
	}
	
	static List makeConceptList(String lang) {
	    List res = []
	      
	    db.getDB().eachRow("select * from ${tablename}", [],  {row ->
	    	// Make Subject object
		Subject s = new Subject(sbj_id:row['sbj_id'], sbj_subject:row['sbj_subject'])
		s.subject = Subject.parseSubject(s.sbj_subject) 
		
		// cache everything first
		if (!cacheID.containsKey(s.sbj_id))  cacheID[s.sbj_id] = s
		
		addToSubjectCache(s)	
	    })
    
	    // can make a map out of 
	    cacheID.each{id, sbj ->
	    	//println "sbj.subject = "+sbj.subject[lang]+" lang=$lang contains="+sbj.subject.containsKey(lang)
	    	if (sbj.subject.containsKey(lang)) {    	    
	    	    Sentence sen = Sentence.getFromTokenizationMarks(sbj.subject[lang])   
        	    List needle = []
        	    sen.each{t ->needle << t.text }   	// add it as strings, for Plain Match
        	    // now, a needle list with Strings mean that each string is an OR pattern,
	    	    // I have to inseert it into another List, so that they all are AND and within one option
	    	    res << [answer:sbj.sbj_id, needle:[needle]]
	    	}
	    	
	    }
	    return res 
	}
	
	static void addToSubjectCache(Subject s) {
	    s.subject.each{lang, sub -> 
	    
	    	//println "lang=$lang sub=$sub"
	   	Map lang_subject = cacheSubject[sub]
	   	// println "lang_subject : $lang_subject"
	   	if (!lang_subject) cacheSubject[sub] = [:]
	   	if (!cacheSubject[sub][lang]) cacheSubject[sub][lang] = []                     
	   	
	   	//                                 println "cacheSubject[sub] = "+cacheSubject[sub]+" cacheSubject[sub][lang]="+cacheSubject[sub][lang]
	   	if (cacheSubject[sub][lang].isEmpty() || !cacheSubject[sub][lang].contains(s)) {
	   	   cacheSubject[sub][lang] << s
	   	} 	 
	   }
	}
	
	static void removeFromSubjectCache(Subject s) {
	    s.subject.each{lang, sub -> 
	    
	   	Map lang_subject = cacheSubject[sub]

	   	if (!lang_subject) cacheSubject[sub] = [:]
	   	if (!cacheSubject[sub][lang]) cacheSubject[sub][lang] = []                     
	   	
	   	//                                 println "cacheSubject[sub] = "+cacheSubject[sub]+" cacheSubject[sub][lang]="+cacheSubject[sub][lang]
	   	if (cacheSubject[sub][lang].isEmpty() || !cacheSubject[sub][lang].contains(s)) {
	   	   cacheSubject[sub][lang] << s
	   	} 	 
	   }
	}
	
	/** Get a Subject from id.
	 * @param id The id as needle.
	 * return the Subject result, or null
	 */
	static Subject getFromID(long sbj_id) {
	   if (!sbj_id) return null
	   if (cacheID.containsKey(sbj_id)) return cacheID[sbj_id]
	   
	   Subject sbj = queryDB("SELECT * FROM ${tablename} WHERE sbj_id=?", [sbj_id])?.getAt(0)
	   log.debug "Querying for sbj_id $sbj_id got Subject $sbj." 
	   cacheID[sbj_id] = sbj
	   Subject.addToSubjectCache(sbj)
	  
	   if (sbj.sbj_id) return sbj else return null
	}	
	
   static List<Subject> getFromSubject(String subject) {
	   if (!subject) return null
	   
	   List<Subject> sbj = queryDB("SELECT * FROM ${tablename} WHERE sbj_subject=?", [subject])
	   log.debug "Querying for sbj_subject $subject got Subject $sbj." 
	   
	   return sbj
	}	
	
	/** Get a Subject from subject.
	 * @param id The terms
	 * return the Subject result, or null
	 */
	
	/*+--------+---------------------------------------+
	  | sbj_id | sbj_subject                           |
	  +--------+---------------------------------------+
	  |   3837 | en:[musicians];pt:[músicos]           | 
	  |   5325 | en:[session][musicians];pt:[músicos]  | 
	  +--------+---------------------------------------+
	 */
	static List<Subject> getFromSubject(String subject_terms, String lang) {
	    
	   List<Subject> res = []
	   if (!subject_terms) return null
	   if (!lang) return null
	   // check if it starts with [. If not, let's mark the tokenization style
	   if (!subject_terms.startsWith("[")) 
	       subject_terms = Sentence.addTokenizationMarks(Sentence.simpleTokenize(subject_terms))
	   
	   Map cachedElement = cacheSubject[subject_terms]
	   if (cachedElement) {
	       List els = cachedElement[lang]
	       els?.each{ res << it} 
	       if (res) return res
	   }
	   
	   String needle = "%${lang}:${subject_terms}%"
	   List<Subject> sbjs = queryDB("SELECT * FROM ${tablename} WHERE sbj_subject LIKE ?", [needle])
	   log.debug "Querying for $subject_terms in lang $lang got Subjects $sbjs." 
	   if (sbjs) {
	       sbjs.each{sbj -> 
	         cacheID[sbj.sbj_id] = sbj
	       	 Subject.addToSubjectCache(sbj) 
	       }
	       return sbjs
	   }
	   return null
	}
	
	static String unparseSubject(Map map) {
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
		def res = db.getDB().executeInsert("INSERT INTO ${tablename} VALUES(0,?)", [sbj_subject])
		// add to the cache. Check both en and pt strings, add to cache
		this.sbj_id = (long)res[0][0]
		if (!cacheID.containsKey(this.sbj_id)) cacheID[this.sbj_id] = this
		Subject.addToSubjectCache(this) 		
		log.info "Adding subject to DB: ${this}"
		return this.sbj_id
	}	
		
	public int removeThisFromDB() {
	    if (!sbj_id) return null
	    def res = db.getDB().executeUpdate("DELETE FROM ${tablename} WHERE sbj_id=?", [sbj_id])
	    cacheSubject.each{key, valuelist -> 
	         if (valuelist.contains(this)) cacheSubject[key].remove(this)
	    }
	    entityIDCache.remove(sbj_id)
		 log.info "Removing subject ${this} from DB, got $res"
	    return res	    
	}
	
		
	public String toString() {
	    return sbj_id
	}
}