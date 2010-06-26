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
import rembrandt.gazetteers.SemanticClassificationDefinitions

/**
 * @author Nuno Cardoso
 * StyleTags for HTML-ish format. They are just wrapped in a DIV, for a nice CSS presentation
 */
class HTMLStyleTag extends StyleTag {
    
    String lang
    SemanticClassificationDefinitions scd
    
    public HTMLStyleTag(String lang) {
        this.lang=lang
        scd = Class.forName("rembrandt.gazetteers."+lang+".SecondHAREMClassificationLabels"+(
        	lang.toUpperCase())).newInstance()
    }
    
    public String printOpenTag(NamedEntity ne) {     
        
        String res = ""	 
        
        ne.classification?.each{c ->
                String nameclass = scd.label[c.c]
                res += "<DIV"
                if (ne.id) res += " ID=\""+ne.id+"\""	      
                if (c.t) nameclass += " "+scd.label[c.t]
                if (c.s) nameclass += " "+scd.label[c.s]
                
                res += " CLASS=\"NE "+nameclass+"\""
                if (ne.corel) res += " COREL=\""+ne.corel.keySet().join(" ")+"\""
                if (ne.corel) res += " TIPOREL=\""+ne.corel.values().join(" ")+"\""
                
                if (ne.wikipediaPage[c]) 
                    res += " WK=\""+ne.wikipediaPage[c].collect{ 
                    "http://${lang}.wikipedia.org/wiki/${it.replaceAll(' ','_')}"}.unique().join(";")+"\""
                
                println "ne.dbpediaPage: ${ne.dbpediaPage}"
                // use full resources
                if (ne.dbpediaPage[c]) 
                    res += " DB=\""+ne.dbpediaPage[c].join(";")+"\"" 
                
                res += ">"
        }

        return res
    }
    
    public String printCloseTag(NamedEntity ne = null) {
        
        String  res = ""        
        if (ne.classification) {
            // reverse? Last open, first close... of course.
            ne.classification*.c.reverse().each{res += "</DIV>"}
        }
        return res
    }
    
    // TODO
    public NamedEntity parseOpenTag(String tag) {
        return null       
    }
    
    public boolean isOpenTag(String t) {
	return tag.matches(~/<DIV [^>]*?>/) 	
    }
    
    public boolean isCloseTag(String t) {
	return tag.matches(~/<\/DIV>/) 	
    }
    
    public String printOpenALTTag() {
	return "<DIV CLASS=\"ALT\">"
    } 
    
    public String printCloseALTTag() {
	return "</DIV>"
    } 
  
    public String printOpenSubALTTag(int index) {
	return "<DIV CLASS=\"SUBALT\">"
    } 
    
    public String printCloseSubALTTag(int index) {
	return "</DIV>"
    } 
       
    public boolean isOpenALTTag(String tag) {
        return tag.equals("<DIV CLASS=\"ALT\">")
    }
    
    public boolean isCloseALTTag(String tag) {
        return tag.equals("</DIV>")
    }
    
    public boolean isOpenSubALTTag(String tag) {
        return tag.equals("<DIV CLASS=\"SUBALT\">")
    }
    
    public boolean isCloseSubALTTag(String tag) {
        return tag.equals("</DIV>")
    }
    
}