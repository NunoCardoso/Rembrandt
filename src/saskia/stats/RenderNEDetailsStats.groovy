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

import saskia.db.obj.Collection
import saskia.db.obj.NE
import saskia.util.I18n

class RenderNEDetailsStats {

	DocStats docstats
	NEPoolStats nestats
	SimpleDateFormat dateFormat
	I18n i18n

	public String render(long ne_id, Collection collection, String lang) {

		i18n = I18n.newInstance()
		docstats = new DocStats(collection, lang)
		nestats = new NEPoolStats(collection, lang)
		StringBuffer s = new StringBuffer()
		dateFormat = new SimpleDateFormat(i18n.dateformat[lang])

		NE ne = NE.getFromID(ne_id)

		s.append "<DIV ID='stats-header'>${i18n.statstitle['statsforne'][lang]} <B>"
		s.append ne.ne_name.nen_name+"</B> (<I>"+ne.ne_category.nec_category;
		if (ne.ne_type) s.append " "+ne.ne_type.net_type
		if (ne.ne_subtype) s.append " "+ne.ne_subtype.nes_subtype
		s.append "</I>)</DIV>";

		s.append "<DIV ID='stats-top-docs-for-ne-div' class='stats-box'>\n"
		s.append "<P>${i18n.statslabel['docswithmorereferencesto'][lang]} '${ne.ne_name}'</P>"
		s.append docstats.topDocsForNE(ne.ne_id, 5)
		s.append "</DIV>\n"

		s.append "<DIV ID='stats-sentences-with-ne-div' class='stats-box'>\n"
		s.append "<P>${i18n.statslabel['recentsentenceswith'][lang]} '${ne.ne_name}'</P>"
		s.append nestats.getTopSentencesForNE(ne.ne_id, 10, 4)
		s.append "</DIV>\n"

		// it's too slow!
		//	s.append "<DIV ID='stats-related-nes-div' class='stats-box'>\n"
		//	s.append "<P>${i18n.statstitle['relatedNEs'][lang]}:</P>"
		//	List rels = nestats.getRelationsForNE(ne.ne_id, 10) // 10 = top limit
		//	s.append rels*.html.join("\n")
		//	s.append "</DIV>\n"



		s.append "<DIV ID='stats-graph-time-of-ne-div' class='stats-box'>\n"
		s.append "<TABLE CLASS='stats-graph-time-of-ne-table hidden-table'>\n"
		s.append "<CAPTION>"+i18n.statslabel['timelineofthe'][lang]+" "+i18n.statslabel['NE'][lang]
		s.append "'"+ne.ne_name.nen_name+"'</CAPTION>"
		s.append nestats.getTimeLineOfNE([ne.ne_id], [ne.ne_name.nen_name], 20); // 20 = number of ticks
		s.append "</TABLE>\n"
		s.append "</DIV>\n"

		return s.toString()
	}
}