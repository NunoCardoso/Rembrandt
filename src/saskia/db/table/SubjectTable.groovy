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

import rembrandt.obj.Sentence
import saskia.bin.Configuration
import saskia.db.database.SaskiaDB
import saskia.db.obj.Subject

class SubjectTable extends DBTable {

	Configuration conf
	static String tablename = "subject"
	static Logger log = Logger.getLogger("Subject")
	LinkedHashMap<String,List<Subject>> cacheSubject
	LinkedHashMap<Long,Subject> cacheID

	Map conceptList = [:] // to be made, creating a Map that can be used for conceptMatch

	public SubjectTable(SaskiaDB db) {
		super(db)
		conf = Configuration.newInstance()
		// cacheSubject[terms] = Map<Lang:List<Subject>>
		cacheSubject = new LinkedHashMap(conf.getInt("saskia.subject.cache.number",1000), 0.75f, true) // true: access order.
		// NOTE: this has a id -> sbj_subject STRING, NOT subject MAP.
		cacheID = new LinkedHashMap(conf.getInt("saskia.subject.cache.number",1000), 0.75f, true) // true: access order.

	}


	public List<Subject> queryDB(String query, ArrayList params = []) {
		List<Subject> t = []
		db.getDB().eachRow(query, params, {row  ->
			t << Subject.createNew(row)
		})
		return t
	}

	public Map listSubjects(limit = 10, offset = 0, column = null, needle = null) {
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

	public List getConceptListForLang(String lang) {
		if (!lang) throw new IllegalStateException("Give me a lang!")
		if (!conceptList.containsKey(lang)) conceptList[lang] = makeConceptList(lang)
		return conceptList[lang]
	}

	public List makeConceptList(String lang) {
		List res = []

		db.getDB().eachRow("select * from ${getTablename()}", [],  {row ->
			// Make Subject object
			Subject s = Subject.createNew(this, row)
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

	public void addToSubjectCache(Subject s) {
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

	public void removeFromSubjectCache(Subject s) {
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
	public Subject getFromID(long sbj_id) {
		if (!sbj_id) return null
		if (cacheID.containsKey(sbj_id)) return cacheID[sbj_id]

		Subject sbj = queryDB("SELECT * FROM ${getTablename()} WHERE sbj_id=?", [sbj_id])?.getAt(0)
		log.debug "Querying for sbj_id $sbj_id got Subject $sbj."
		cacheID[sbj_id] = sbj
		Subject.addToSubjectCache(sbj)

		if (sbj.sbj_id) return sbj else return null
	}

	static Subject getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("SubjectTable").getFromID(id)
	}


	public List<Subject> getFromSubject(String subject) {
		if (!subject) return null

		List<Subject> sbj = queryDB("SELECT * FROM ${getTablename()} WHERE sbj_subject=?", [subject])
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
	public List<Subject> getFromSubject(String subject_terms, String lang) {

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
		List<Subject> sbjs = queryDB("SELECT * FROM ${getTablename()} WHERE sbj_subject LIKE ?", [needle])
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


}