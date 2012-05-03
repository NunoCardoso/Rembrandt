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

package saskia.stats

import java.text.SimpleDateFormat

import rembrandt.io.HTMLWriter
import rembrandt.io.HTMLStyleTag
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import saskia.db.DocStatus
import saskia.db.SaskiaWebstore
import saskia.db.database.SaskiaMainDB
import saskia.db.obj.Collection
import saskia.db.obj.Doc
import saskia.util.I18n

class DocStats {

	SaskiaMainDB db
	Collection collection
	RembrandtReader reader
	HTMLWriter writer
	String lang
	SimpleDateFormat dateFormat
	I18n i18n
	static SaskiaWebstore webstore = SaskiaWebstore.newInstance()

	public DocStats(Collection collection, String lang) {
		db = SaskiaMainDB.newInstance()
		this.collection = collection
		reader = new RembrandtReader(new RembrandtStyleTag(lang))
		writer = new HTMLWriter(new HTMLStyleTag(lang))
		i18n = I18n.newInstance()
		dateFormat = new SimpleDateFormat(i18n.dateformat[lang])
	}

	int getTotalNumberOfDocs() {
		int total
		db.getDB().eachRow("SELECT COUNT(*) FROM "+Doc.tablename+" WHERE "+
				"doc_collection=?", [collection.col_id], {row ->
					total = row[0]
				})
		return total
	}

	int getTotalNumberOfTaggedDocs() {
		int total
		db.getDB().eachRow("SELECT COUNT(*) FROM "+Doc.tablename+
				" WHERE doc_collection=? AND "+
				"doc_date_tagged IS NOT NULL", [collection.col_id], {row -> total = row[0]})
		return total
	}

	/* well, it's NE classifications, not NEs... */
	int getTotalNumberOfNEsInDocs() {
		int total
		db.getDB().eachRow("SELECT COUNT(*) FROM "+Doc.dhn_table+
				" WHERE doc_collection=? AND doc_id=dhn_doc",
				[collection.col_id],  {row -> total = row[0] })
		return total
	}

	int getTotalNumberOfDistinctNEsInDocs() {
		int total
		db.getDB().eachRow("SELECT COUNT(DISTINCT(dhn_ne)) FROM "+Doc.dhn_table+", "+
				Doc.tablename+" WHERE doc_collection=? AND doc_id=dhn_doc",
				[collection.col_id],  {row -> total = row[0] })
		return total
	}

	String getOldestDoc() {
		Date d
		db.getDB().eachRow("SELECT MIN(doc_date_created) FROM "+Doc.tablename+
				" WHERE doc_collection=? ",
				[collection.col_id], {row -> d = row[0]})
		return dateFormat.format(d)
	}

	String getNewestDoc() {
		Date d
		db.getDB().eachRow("SELECT MAX(doc_date_created) FROM "+Doc.tablename+
				" WHERE doc_collection=? ",
				[collection.col_id], {row -> d = row[0]})
		return dateFormat.format(d)
	}

	String topDocsForNE(long ne_id, int num_docs) {
		StringBuffer res = new StringBuffer()
		db.getDB().eachRow("select doc_id, doc_webstore, count(doc_id) as c from doc, "+
				" doc_has_ne WHERE doc_collection=? "+
				"AND doc_id=dhn_doc and dhn_ne=? group by doc_id order by c desc limit ${num_docs}",
				[collection.col_id, ne_id], {row ->
					String content = webstore.retrieve(r.doc_webstore)
					// this is JUST to use the title extraction... it's a bad Doc, very incomplete
					Doc doc = new Doc(doc_content:content)
					String title = doc.getTitleFromContent()

					res.append "<P><B>${row['c']}</B> - <A HREF='#' CLASS='DETAILDOC' DOCID='${row['doc_id']}'>"+
							(title.startsWith("{") ? RembrandtReader.parseSimple(title) : title)+"</A></P>"
					//	}
				})
		return res.toString()
	}

	int	getNumberNEinDocTitle(Doc doc) {
		int res
		db.getDB().eachRow("select count(dhn_ne) as c FROM doc_has_ne WHERE dhn_doc=? and dhn_section=?",
				[doc.doc_id, "T"], {row ->   res = row[0]})
		return res
	}

	int	getNumberNEinDocBody(Doc doc) {
		int res
		db.getDB().eachRow("select count(dhn_ne) as c FROM doc_has_ne WHERE dhn_doc=? and dhn_section=?",
				[doc.doc_id, "B"], {row ->   res = row[0]})
		return res
	}

	String getTableCountNEsPerDoc(int number_of_bars = 10, String label = "") {

		StringBuffer s = new StringBuffer()
		int max
		int min
		HashMap res =[:]
		HashMap pool= [:]

		db.getDB().eachRow("select doc_id, count(dhn_doc) as c from  doc, doc_has_ne "+
				"where doc_collection=? and doc_id=dhn_doc group by dhn_doc order by c desc",
				[collection.col_id], {row ->

					if (!max) {
						max = row['c']
					}
					if (!min) {
						min = row['c']
					}
					if (max < row['c']) {
						max = row['c']
					}
					if (min > row['c']) {
						min = row['c']
					}
					res[row['doc_id']] = row['c']
				})

		long barwidth = Math.floor((max-min)/number_of_bars)
		//println "max=$max min=$min barwidth=$barwidth"

		s.append "<THEAD>\n\t<TR>\n\t\t<TD></TD>\n"
		for (long i = min; i<max; i += barwidth) {
			s.append "\t\t<TH>${i}-${i+barwidth-1}</TH>\n"
		}
		s.append "\t</TR>\n</THEAD>\n"
		s.append "<TBODY>\n\t<TR>\n\t\t<TH>$label</TH>\n"

		res.each{k, v ->
			int index = (int)Math.floor(v/barwidth)
			if (pool.containsKey(index)) pool[index]++ else pool[index] = 1
		}

		for(int i=0; i<number_of_bars; i++) {
			s.append "\t\t<TD>"+(pool[i] ? pool[i] : 0)+"</TD>\n"
			pool?.remove(i)
		}

		// rest... let's add
		List total = pool?.values()?.toList()
		s.append "\t\t<TD>"+(total ? total.sum(): 0)+"</TD>\n"
		s.append "\t</TR>\n</TBODY>\n"
		return s.toString()
	}


}