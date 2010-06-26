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

package rembrandt.io

import rembrandt.obj.*
import rembrandt.gazetteers.CommonClassifications

/**
 * @author Nuno Cardoso
 * StyleTags for CLEF-2008 HTML-ish format.
 */
class CLEF2008StyleTag extends StyleTag {
    
    /** first, let«s convert this named entity...
     * ["LOCAL"],["HUMANO"],["CONSTRUCAO"] goes to "CONSTRUCAO","null","null"
     * ["LOCAL"],["VIRTUAL"],["whatever"] goes to ["VIRTUAL"],["whatever"]
     */
    private NamedEntity transformNE(NamedEntity ne) {
        for (int i=0; i<ne.classification.size(); i++) {
            if ( ne.classification[i].equals(CommonClassifications.place_human_construction)) {
                ne.classification[i] = new SemanticClassification("@CONSTRUCAO", null, null) 
            } 
            if ( ne.classification[i].equals(CommonClassifications.place_virtual)) {
                ne.classification[i] = new SemanticClassification("@VIRTUAL", ne.classification[i].s, null) 
            }
        }
        return ne       
    }
    
    public NamedEntity parseOpenTag(String tag) {
	return null
    }
    public String printOpenTag(NamedEntity ne) {     
        
        String res = ""	 
        ne = transformNE(ne)
        
        // LOCALITY ONLY FOR ACONTECIMENTO.*, LOCAL.HUMANO.CONSTRUCAO e ORGANIZACAO
        //  println "ne $ne passes CLASSIFICATION? "+(ne.category?.contains("ACONTECIMENTO") || 
        //	  ne.category?.contains("ORGANIZACAO") || ne.category?.contains("CONSTRUCAO"))
        if ( ( ne.classification*.category.contains("ACONTECIMENTO") || 
        ne.classification*.category.contains("ORGANIZACAO") || 
        ne.classification*.category.contains("CONSTRUCAO") ) && 
        !ne.locality?.isEmpty() ) {        
            ne.locality.each{local ->  res += "<LOCALITY emid=\"${ne.id}\" from=\"${ne.classification*.category.join(' ')}\">${local.terms.join(' ')}</LOCALITY> "	   }
        }     
        
        if (ne.classification*.category) {
            ne.classification.each{c -> 
                res += "<${c.category}"
                if (ne.id != null) res += " ID=\""+ne.id+"\""
                if (c.type) res += " TIPO=\""+c.type+"\""
                if (c.subtype) res += " SUBTIPO=\""+c.subtype+"\""
                if (ne.corel?.getAt(i) != null) res += " COREL=\""+ne.corel.keySet().join(" ")+"\""
                if (ne.corel?.getAt(i) != null) res += " TIPOREL=\""+ne.corel.values().join(" ")+"\""
                res += ">"
            }
        }
        return res
    }
    
    public String printCloseTag(NamedEntity ne = null) {
        
        String  res = ""
        ne = transformNE(ne)
        
        if (ne.classification*.category) {
            // reverse? Last open, first close... of course.
            ne.classification*.category.reverse().each{res += "</${it}>"}
        }
        return res
    }
    
    public boolean isOpenTag(String tag) {
	return tag.matches(~/<[A-Z]+[^>]*?>/) 	
    } 
    
    public boolean isCloseTag(String tag) {
	return tag.matches(~/<\/[A-Z]+>/) 	
    } 
    
    public boolean isOpenALTTag(String tag) {
        return tag.equals("<ALT>")
    }
    
    public boolean isCloseALTTag(String tag) {
        return tag.equals("</ALT>")
    }
    
    public boolean isOpenSubALTTag(String tag) {
        return tag.equals("<SUBALT>")
    }
    
    public boolean isCloseSubALTTag(String tag) {
        return tag.equals("</SUBALT>")
    }
    
    public String printOpenALTTag() {
	return "<ALT>"
    }
    
    public String printCloseALTTag() {
	return "</ALT>"
    }
    
    public String printOpenSubALTTag(int index) {
	return "<SUBALT>"
    }
    
    public String printCloseSubALTTag(int index) {
	return "</SUBALT>"
    }
}