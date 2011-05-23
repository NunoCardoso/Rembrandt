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

import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import rembrandt.obj.Document
import rembrandt.obj.Sentence
import saskia.db.database.SaskiaMainDB
import saskia.db.obj.Collection
import saskia.db.obj.RembrandtedDoc
import saskia.util.I18n

class NEPoolStats {

	SaskiaMainDB db
	RembrandtReader reader
	Collection collection
	String lang
	I18n i18n

	public NEPoolStats(Collection collection, String lang) {
		db = SaskiaMainDB.newInstance()
		i18n = I18n.newInstance()
		this.collection = collection
		this.lang=lang
		reader = new RembrandtReader(new RembrandtStyleTag(lang))
	}

	List getRandomNE(int amount = 1) {
		List res=[]
		db.getDB().eachRow("SELECT ne_id, nen_name, nec_category, net_type, nes_subtype "+
				"FROM doc, doc_has_ne, ne_name, ne, ne_category, ne_type, ne_subtype "+
				"WHERE doc_collection=? and doc_id=dhn_doc and dhn_ne =ne_id AND "+
				"ne_name=nen_id and ne_category=nec_id and "+
				"ne_type=net_id and ne_subtype=nes_id order by RAND() LIMIT ${amount}", [collection.col_id], {row ->
					res << ["id":row['ne_id'], "name":row['nen_name'], "category":row['nec_category'],
								"type":row['net_type'], "subtype":row['nes_subtype']]
				})
		return res
	}

	String getTopNEs(int limit = 10) {

		StringBuffer res = new StringBuffer()
		db.getDB().eachRow("SELECT ne_id, nen_name, nec_category, net_type, nes_subtype, count(dhn_ne) as c "+
				"FROM doc, doc_has_ne, ne_name, ne, ne_category, ne_type, ne_subtype WHERE "+
				"doc_collection=? AND doc_id=dhn_doc AND dhn_ne = ne_id AND ne_name=nen_id AND ne_category=nec_id "+
				"AND ne_type=net_id AND ne_subtype=nes_id group by dhn_ne order by c desc LIMIT ${limit}",
				[collection.col_id], {row ->
					res.append "<P><B>${row['c']}</B> - <B><A HREF='#' NEID='${row['ne_id']}' CLASS='DETAILNE'>${row['nen_name']}</A></B>";
					res.append " <I>${row['nec_category']}</I>"
					if (row['net_type']) res.append " <I>${row['net_type']}</I>"
					if (row['nes_subtype']) res.append " <I>${row['nes_subtype']}</I>"
					res.append "</P>"
				})
		return res.toString()
	}

	String getTopNEsForDoc(RembrandtedDoc rdoc, int limit = 10) {

		StringBuffer res = new StringBuffer()
		res.append "<UL  style='text-indent:0;'>\n"
		db.getDB().eachRow("SELECT ne_id, nen_name, nec_category, net_type, nes_subtype, count(dhn_ne) as c "+
				"FROM doc_has_ne, ne_name, ne, ne_category, ne_type, ne_subtype WHERE "+
				"dhn_doc=? and dhn_ne= ne_id and ne_name=nen_id and ne_category=nec_id and ne_type=net_id and "+
				"ne_subtype=nes_id group by dhn_ne order by c desc LIMIT ${limit}", [rdoc.doc_id], {row ->

					res.append "<LI style='text-indent:0px; padding:3px;'>"
					res.append "<B>${row['c']}</B> - <DIV style='display:inline;' CLASS='NE ${row['nec_category']}'>"
					res.append "<A HREF='#' NEID='${row['ne_id']}' CLASS='DETAILNE'>${row['nen_name']}</A></DIV>";
					res.append " <I>${row['nec_category']}</I>"
					if (row['net_type']) res.append " <I>${row['net_type']}</I>"
					if (row['nes_subtype']) res.append " <I>${row['nes_subtype']}</I>"
					res.append "</LI>\n"
				})
		res.append "</UL>\n"
		return res.toString()
	}


	String getNEcategoryDistribution() {

		StringBuffer res = new StringBuffer()

		db.getDB().eachRow("SELECT nec_category, count(nec_category) as c FROM doc_has_ne, "+
				"ne, ne_category, doc WHERE doc_collection=? and doc_id=dhn_doc and "+
				"dhn_ne = ne_id and ne_category=nec_id GROUP BY "+
				"nec_category order by c desc", [collection.col_id],  {row ->
					res.append "<TR>\n\t<TH>${row['nec_category']}</TH>\n\t<TD>${row['c']}</TD>\n</TR>\n"
				})
		return res.toString()
	}

	String getEntitiesFromName(String name) {
		StringBuffer res = new StringBuffer()
		int first_entity_id = 0
		boolean has_entity = false;
		boolean first = true
		db.getDB().eachRow("SELECT ent_id, ent_wikipedia_page, ent_dbpedia_resource, ent_dbpedia_class from "+
				"entity, ne, ne_name where ent_id=ne_entity and ne_name=nen_id and "+
				"nen_name=?", [name], {row ->
					if (first) {
						if (row['ent_dbpedia_resource']) has_entity = true
						first_entity_id = row['ent_id']
						res.append "<P>${i18n.statslabel['DBpediaresource'][lang]}: "+
								(row['ent_dbpedia_resource'] ? row['ent_dbpedia_resource'] : i18n.statslabel['noresource'][lang])+".</P>"
						res.append "<P>${i18n.statslabel['DBpediaresource'][lang]}: "+
								(row['ent_dbpedia_class'] ? row['ent_dbpedia_class'] : i18n.statslabel['noclass'][lang])+".</P>"
						first = false
					}
				})
		if (has_entity) {
			res.append "<P>${i18n.statstitle['differentrepresentations'][lang]}:</P>"
			res.append getNEnamesForEntity(first_entity_id)
		}
		return res.toString()
	}

	String getNEnamesForEntity(long entity_id) {
		StringBuffer res = new StringBuffer()
		db.getDB().eachRow("SELECT ne_id, nen_name, nec_category, net_type, nes_subtype, count(dhn_ne) as c "+
				"FROM doc, doc_has_ne, ne_name, ne, ne_category, ne_type, ne_subtype WHERE "+
				"doc_collection=? doc_id=dhn_doc and dhn_ne = ne_id and ne_name=nen_id and "+
				"ne_category=nec_id and ne_type=net_id "+
				"and ne_subtype=nes_id and ne_entity=? group by dhn_ne order by c desc", [collection.col_id, entity_id], {row ->
					res.append "<P><B>${row['c']}</B> - <B><A HREF='#' NEID='${row['ne_id']}' CLASS='DETAILNE'>${row['nen_name']}</A></B>";
					res.append " <I>${row['nec_category']}</I>"
					if (row['net_type']) res.append " <I>${row['net_type']}</I>"
					if (row['nes_subtype']) res.append " <I>${row['nes_subtype']}</I>"
					res.append "</P>"
				})
		return res.toString()
	}


	/* it's too slow!! */
	List getRelationsForNE(long ne_id, int limit=10) {
		// selects entities within same doc, same sentence, different term
		StringBuffer res
		List l = []
		db.getDB().eachRow("SELECT ne2.ne_id, nen_name, nec_category, net_type, nes_subtype, count(n2.dhn_ne) as c "+
				"FROM ne_name, ne_category, ne_type, ne_subtype, doc_has_ne as n1, doc_has_ne n2, ne as ne1, ne as ne2, "+
				"doc WHERE doc_collection=? AND doc_id=n1.dhn_doc AND n1.dhn_doc = n2.dhn_doc AND "+
				"n1.dhn_sentence= n2.dhn_sentence AND n1.dhn_term != n2.dhn_term AND n1.dhn_ne=? AND n2.dhn_ne=ne2.ne_id "+
				"AND ne2.ne_id!=ne1.ne_id AND ne2.ne_name=nen_id and ne2.ne_category=nec_id AND ne2.ne_type=net_id AND "+
				"ne2.ne_subtype=nes_id GROUP BY n2.dhn_ne order by c desc LIMIT ${limit}", [collection.col_id, ne_id], {row ->
					res = new StringBuffer()
					res.append "<P><B>${row['c']}</B> - <B><A HREF='#' NEID='${row['ne_id']}' CLASS='DETAILNE'>${row['nen_name']}</A></B>";
					res.append " <I>${row['nec_category']}</I>"
					if (row['net_type']) res.append " <I>${row['net_type']}</I>"
					if (row['nes_subtype']) res.append " <I>${row['nes_subtype']}</I>"
					res.append "</P>"
					l << [id:row['ne_id'], name:row['nen_name'], html:res.toString()]
				})

		return l
	}

	String getTimeLineOfNE(List<Long> ne_ids, List<String> ne_names, int number_of_x = 10) {

		HashMap names = [:]
		ne_ids.eachWithIndex{id, i ->
			names[id]=ne_names[i]
		}

		long min
		long max
		HashMap res = [:]
		StringBuffer s = new StringBuffer()
		//println ne_id
		ne_ids.each{ne_id ->
			db.getDB().eachRow("SELECT dhn_doc, count(dhn_doc) as c, doc_date_created FROM "+
					"doc, doc_has_ne WHERE doc_collection=? AND doc_id=dhn_doc "+
					"AND dhn_ne=? group by dhn_doc order by doc_date_created asc",
					[collection.col_id, ne_id], {row ->
						//println "Row:"+row
						if (!res.containsKey(ne_id)) res[ne_id]=[]
						res[ne_id] << [doc:row['dhn_doc'], c:row['c'], date:row['doc_date_created'] ]
						long date = ((Date)row['doc_date_created']).getTime()
						if (!max) {max = date}
						if (!min) {min = date}
						if (max < date) {max = date}
						if (min > date) {min = date}
					})
		}
		if (max-min == 0) {
			return "Not available."
		}
		long diff = (max-min)/number_of_x

		//println res
		s.append "<THEAD>\n\t<TR>\n\t\t<TD></TD>\n"

		String currentlabel = ""
		for (long i = min; i<max; i += diff) {
			String label = new Date(i).format('yyyy-MM-dd')
			if (currentlabel == label) {
				s.append "\t\t<TH></TH>\n"
			} else {
				s.append "\t\t<TH>$label</TH>\n"
				currentlabel = label
			}
		}
		s.append "</TR>\n</THEAD>\n"
		s.append "<TBODY>\n"
		res.each{k, v ->
			s.append "\t<TR>\n\t\t<TH>${names[k]}</TH>\n"
			//println "max: $max min:$min diff:$diff"
			HashMap res2 = [:]
			v.each{r ->
				long rdate = r.date.getTime()
				if (diff == 0) diff = 1 // avoid div by 0
				int index = (int)Math.floor((rdate-min)/diff)
				//println "Doc ${r.doc} goes to index $index, has counts ${r.c}"
				if (!res2[index]) res2[index] = r.c else res2[index] += r.c
			}
			for(int i=0; i<number_of_x; i++) {
				s.append "\t\t<TD>"+(res2[i] ? res2[i] : 0)+"</TD>\n"
			}
			s.append "\t</TR>\n"
		}
		s.append "</TBODY>\n"
		return s.toString()
	}

	String getTopSentencesForNE(long ne_id, int limit_docs=10, int term_window = 4) {
		//select
		StringBuffer res = new StringBuffer()
		db.getDB().eachRow("SELECT dhn_doc, nen_name, nen_nr_terms, dhn_sentence, dhn_term "+
				"FROM  doc_has_ne, doc, ne, ne_name WHERE "+
				"doc_collection=? AND doc_id=dhn_doc "+
				"and dhn_ne=ne_id and ne_name=nen_id and ne_id=? "+
				"and dhn_section=? order by doc_date_created desc LIMIT ${limit_docs}",
				[
					collection.col_id,
					ne_id,
					"B"
				], {row ->
					RembrandtedDoc doc = RembrandtedDoc.getFromID(row['dhn_doc'])
					Document d = reader.createDocument(doc.doc_content)

					// let's get 4 terms back, 4 terms front
					Sentence s = d.body_sentences[row['dhn_sentence']]
					// now, for the limits
					int lowlimit = ((row['dhn_term'] - term_window ) < 0 ? 0 : (row['dhn_term'] - term_window ));
					int highlimit = ((row['dhn_term'] + row['nen_nr_terms'] + term_window ) > (s.size()-1) ?
							(s.size()-1)  : (row['dhn_term'] + row['nen_nr_terms'] + term_window ) );

					String sent = s.subSentence(lowlimit, highlimit).toStringLine()
					String nename = row['nen_name']
					sent = sent.replaceAll(/\b($nename)\b/) {all, g1 -> return "<B>${g1}</B>"}
					res.append "<P><A HREF='#' DOCID='${row['dhn_doc']}' CLASS='DETAILDOC'>"+row['dhn_doc']+"</A> - ";
					res.append ""+(lowlimit == 0 ? "" : "(...) ")+sent
					res.append ""+(highlimit == (s.size()-1) ? "" : " (...)")+"</P>"
				})
		return res.toString()
	}
}