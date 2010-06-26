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

import saskia.io.Collection
import saskia.io.RembrandtedDoc
import saskia.io.DocGeoSignature
import saskia.io.GeoSignature
import saskia.io.DocTimeSignature
import saskia.util.I18n
import rembrandt.obj.Document
import rembrandt.io.RembrandtReader
import rembrandt.io.HTMLDocumentWriter
import rembrandt.io.HTMLStyleTag
import rembrandt.io.RembrandtStyleTag
import java.text.SimpleDateFormat

class RenderDocDetailsStats { 

     RembrandtDocStats docstats
     NEPoolStats nestats
     RembrandtReader reader
     HTMLDocumentWriter writer
     SimpleDateFormat dateFormat
     I18n i18n
     
     public String render(RembrandtedDoc rdoc, Collection collection, String lang) {
	    i18n = I18n.newInstance()
	    docstats = new RembrandtDocStats(collection, lang)
	    nestats = new NEPoolStats(collection, lang)
	    reader = new RembrandtReader(new RembrandtStyleTag(lang))
            writer = new HTMLDocumentWriter(new HTMLStyleTag(lang))
            Document doc = reader.createDocument(rdoc.doc_content)

	    StringBuffer s = new StringBuffer()
	    dateFormat = new SimpleDateFormat(i18n.dateformat[lang])
        
        
	    s.append "<DIV ID='stats-header'>${i18n.statstitle['statsfordoc'][lang]} <B>"
            
	    s.append ""+(doc.title_sentences ? writer.printDocumentHeadContent(doc) : rdoc.doc_original_id)+"</B></DIV>"; 

	    s.append "<DIV ID='stats-doc-details-div' class='stats-box'>\n"
	    s.append "<P><B>${i18n.statstitle['docdetails'][lang]}</B></P>";
	    s.append "<P>${i18n.statslabel['date_created'][lang]}: "+dateFormat.format(rdoc.doc_date_created)+"</P>\n"
	    s.append "<P>${i18n.statslabel['date_tagged'][lang]}: "+dateFormat.format(rdoc.doc_date_tagged)+"</P>\n"
	    s.append "<P>${i18n.statslabel['tag_version'][lang]}"+rdoc.getTags().join(", ")+"</P>\n"
	    s.append "</DIV>\n" 
	
	 	
	    s.append "<DIV ID='stats-doc-stats-div' class='stats-box'>\n"
	    s.append "<P><B>${i18n.statstitle['neandsentences'][lang]}</B></P>"
	    s.append "<P>${i18n.statslabel['numbersentencestitle'][lang]}: "+(doc.title_sentences? doc.title_sentences.size() : 0)+"</P>\n"
	    s.append "<P>${i18n.statslabel['numbersentencesbody'][lang]}: "+(doc.body_sentences? doc.body_sentences.size() : 0)+"</P>\n"
	    s.append "<P>${i18n.statslabel['numberneindoctitle'][lang]}: "+(doc.titleNEs ? doc.titleNEs.size() : 0)+"</P>\n"
	    s.append "<P>${i18n.statslabel['numberneindocbody'][lang]}: "+(doc.bodyNEs ? doc.bodyNEs.size() : 0)+"</P>\n"
	    s.append "<P>${i18n.statslabel['numberneinpooltitle'][lang]}: "+docstats.getNumberNEinDocTitle(rdoc)+"</P>\n"
	    s.append "<P>${i18n.statslabel['numberneinpoolbody'][lang]}: "+docstats.getNumberNEinDocBody(rdoc)+"</P>\n"
	    s.append "</DIV>\n" 
		
	 	
	    s.append "<DIV ID='stats-nes-on-doc-div' class='stats-box'>\n"
	    s.append "<P><B>${i18n.statstitle['top10nes'][lang]}</B></P>"
	    s.append nestats.getTopNEsForDoc(rdoc)
	    s.append "</DIV>\n" 
        
            s.append "<DIV style='height:300px; overflow:scroll;' ID='stats-doc-geo-signature-div' class='stats-box'>\n"
            s.append "<P><B>${i18n.statstitle['doc_geo_signature'][lang]}</B></P>"
            DocGeoSignature dgs = rdoc.getGeographicSignature()
            String ss = dgs?.dgs_signature.replaceAll(/</,'&lt;').replaceAll(/>/,'&gt;').replaceAll(/\n/,"<BR>")
            s.append ss
            s.append "</DIV>\n" 
            
            s.append "<DIV style='height:300px; overflow:scroll;' ID='stats-doc-time-signature-div' class='stats-box'>\n"
            s.append "<P><B>${i18n.statstitle['doc_time_signature'][lang]}</B></P>"
            DocTimeSignature dts = rdoc.getTimeSignature()
            ss = dts?.dts_signature.replaceAll(/</,'&lt;').replaceAll(/>/,'&gt;').replaceAll(/\n/,"<BR>")
            s.append ss
            s.append "</DIV>\n" 
            
            List coordinates
            if (dgs) {
                GeoSignature geosig = new GeoSignature(dgs)
                coordinates = geosig.places*.centroid
            }
	    // points[0] = {"latitude":24t2.23, }
            s.append "<DIV ID='stats-map-div' class='stats-box'>\n"
            s.append "<P><B>${i18n.statstitle['doc_map'][lang]}</B></P>"
            s.append "<DIV ID='stats-map' style='width:300px; height:300px;'><SCRIPT>createGoogleMap('stats-map', ["
            List s2 = []
            coordinates?.eachWithIndex{c, i ->
               s2 += "{'Latitude':"+c['Latitude']+", 'Longitude':"+c['Longitude']+"}"      
            }
            s.append s2.join(",")+"]);</SCRIPT>"
            s.append "</DIV></DIV>\n" 
            
            return s.toString()
     }
}