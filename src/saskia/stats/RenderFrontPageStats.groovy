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

import saskia.db.obj.Collection;
import saskia.util.I18n

class RenderFrontPageStats {

     DocStats docstats
     NEPoolStats nestats
     I18n i18n
     
     public String render(Collection collection, String lang) {
		i18n = I18n.newInstance()
	    docstats = new DocStats(collection, lang)
	 	nestats = new NEPoolStats(collection, lang)

	 	StringBuffer s = new StringBuffer()
	 	
	 		s.append "<DIV ID='stats-header'>${i18n.statstitle['statsforcollection'][lang]} <B>${collection.col_name}</B> ";
	 		s.append "<DIV ID='stats-search-ne-div'>${i18n.statslabel['searchne'][lang]}: <INPUT TYPE='TEXT' CLASS='ac_input' AUTOCOMPLETE='OFF' ";
	 		s.append "ID='ne' NAME='ne' SIZE=20>\n</DIV>\n</DIV>\n"; 
	 		s.append "<SCRIPT>bindAutoCompleteOnNESearch(\$('#ne'))</SCRIPT>" // force bind of  autocomplete
	 		
	 		s.append "<DIV ID='stats-main-count-div' class='stats-box'>\n"
	 		s.append "<P>${i18n.statslabel['numberdocs'][lang]}: "+docstats.getTotalNumberOfDocs()+"</P>\n"
			s.append "<P>${i18n.statslabel['numberrembrandteddocs'][lang]}: "+docstats.getTotalNumberOfTaggedDocs()+"</P>\n"
	 		s.append "<P>${i18n.statslabel['oldestdoc'][lang]}: "+docstats.getOldestDoc()+"</P>\n"
	 		s.append "<P>${i18n.statslabel['newestdoc'][lang]}: "+docstats.getNewestDoc()+"</P>\n"
	 		s.append "<P>&nbsp;</P>"
	 		s.append "<P>${i18n.statslabel['totalNEs'][lang]}: "+docstats.getTotalNumberOfNEsInDocs()+"</P>\n"
	 		s.append "<P>${i18n.statslabel['totalDistinctNEs'][lang]}: "+docstats.getTotalNumberOfDistinctNEsInDocs()+"</P>\n"
	 		s.append "</DIV>\n" 
	 		
			s.append "<DIV ID='stats-graph-ne-per-doc-table-div' class='stats-box'>\n"
			s.append "<TABLE CLASS='stats-graph-ne-per-doc-table hidden-table'>\n"
			s.append "<CAPTION>${i18n.statslabel['NEsPerDoc'][lang]}</CAPTION>"
			s.append docstats.getTableCountNEsPerDoc(10, i18n.statslabel['numberdocs'][lang]);
			s.append "</TABLE>\n"
			s.append "</DIV>\n"
			
	 		s.append "<DIV ID='stats-header'>${i18n.statstitle['NEs'][lang]}</DIV>\n" 
	 		s.append "<DIV ID='stats-top-ne-div' class='stats-box'>\n"
	 		s.append nestats.getTopNEs(10)
	 		s.append "</DIV>\n"
			s.append "<DIV ID='stats-graph-ne-dist-table-div' class='stats-box'>\n"
			s.append "<TABLE CLASS='stats-graph-ne-dist-table hidden-table'>\n"
			s.append "<CAPTION>${i18n.statslabel['categorydistribution'][lang]}</CAPTION>"
			s.append nestats.getNEcategoryDistribution();
			s.append "</TABLE>\n"
			s.append "</DIV>\n"
	 		// get a random NE
	 		HashMap ne = (nestats.getRandomNE(1))?.getAt(0)
			
	 		s.append "<DIV ID='stats-header'>${i18n.statstitle['NEondisplay'][lang]}: '<B>${ne.name}</B>'</DIV>\n" 
	 		s.append "<DIV ID='stats-ne-names-div' class='stats-box'>\n"
			s.append "<P>${i18n.statslabel['detailson'][lang]} '<B>${ne.name}</B>':</P>"
	 		s.append nestats.getEntitiesFromName(ne.name)
	 			//Brasil: ne_id = 7788,  7789 
	 		s.append "</DIV>\n"	
	 		s.append "<DIV ID='stats-top-docs-for-ne-div' class='stats-box'>\n"
	 		s.append "<P>${i18n.statslabel['docswithmorereferencesto'][lang]} '${ne.name}'</P>"
	 		s.append docstats.topDocsForNE(ne.id, 5)
	 		s.append "</DIV>\n"	 		
	 		s.append "<DIV ID='stats-sentences-with-ne-div' class='stats-box'>\n"
	 		s.append "<P>${i18n.statslabel['recentsentenceswith'][lang]} '${ne.name}'</P>"
	 		s.append nestats.getTopSentencesForNE(ne.id, 10, 4)
	 		s.append "</DIV>\n"	 		
		 		
	 		s.append "<DIV ID='stats-related-nes-div' class='stats-box'>\n"		 		
	 		s.append "<P>${i18n.statstitle['relatedNEs'][lang]}:</P>"
	 		List rels = nestats.getRelationsForNE(ne.id, 10) // 10 = top limit 
	 		s.append rels*.html.join("\n")
			s.append "</DIV>\n"		

	 		//List nes = nestats.getRandomNE(5)
	 		List rels2 = rels[0..<(rels.size() <= 5 ? rels.size() : 5)] // truncate to 5

			
	 		s.append "<DIV ID='stats-graph-time-of-ne-div' class='stats-box'>\n"
			s.append "<TABLE CLASS='stats-graph-time-of-ne-table hidden-table'>\n"
			s.append "<CAPTION>${i18n.statslabel['timelineofthe'][lang]} 5 ${i18n.statslabel['mostrelatedNEs'][lang]}</CAPTION>"			
			s.append nestats.getTimeLineOfNE(rels2*.id, rels2*.name, 20); // 20 = number of ticks
			s.append "</TABLE>\n"
			s.append "</DIV>\n"
			
	 	return s.toString()
     }
}