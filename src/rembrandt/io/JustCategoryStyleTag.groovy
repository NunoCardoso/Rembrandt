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

import rembrandt.obj.NamedEntity
import rembrandt.obj.TimeGrounding
import rembrandt.obj.SemanticClassification
import java.util.regex.Matcher
import saskia.dbpedia.DBpediaResource
import saskia.bin.Configuration
import rembrandt.gazetteers.SemanticClassificationDefinitions
import org.apache.log4j.Logger
/**
 * @author Nuno Cardoso
 * Default Rembrandt StyleTags that emcompass a tag for each classification.
 */
class JustCategoryStyleTag extends StyleTag {
    
    String NEtag
    
    String altTag = "ALT"

    // Basic symbols
    String openTagSymbol = "<"
    String closeTagSymbol = ">"		
    
    SemanticClassificationDefinitions scd
    int verbose
    static Logger log = Logger.getLogger("Reader")
    String lang
    /**
     * Public constructor.
     * @param lang the language of the labels. Default: rembrandt.output.styletag.lang, or 
     * global.lang if it's not specified.
     */
    public JustCategoryStyleTag(String lang) {
        this.lang = lang
        scd = Class.forName("rembrandt.gazetteers."+lang+".SecondHAREMClassificationLabels"+(
        	lang.toUpperCase())).newInstance()
       
        NEtag = SemanticClassificationDefinitions.netag[lang.toLowerCase()]
        verbose = Configuration.newInstance().getInt("rembrandt.output.styletag.verbose", 2)
        log.debug "RembrandtStyleTag initialized for language $lang, verbosity level $verbose, looking for <${NEtag}>"
    } 
    
    public String printOpenTag(NamedEntity ne) {     
        
	StringBuffer sb = new StringBuffer()       
    
	if (ne.classification) {
           
            List differentclassifications = ne.classification*.c.unique().sort()

            differentclassifications.each{dc -> 
            	sb.append openTagSymbol
            	sb.append scd.label[dc]
            	sb.append closeTagSymbol
            }
        }
        return sb.toString()
    }
    
    public String printCloseTag(NamedEntity ne = null) {
        
        StringBuffer sb = new StringBuffer()    
        
        if (ne.classification) {
            
            List differentclassifications = ne.classification*.c.unique().sort().reverse()
            
            differentclassifications.each{dc -> 
                sb.append openTagSymbol+"/"
                sb.append scd.label[dc]
                sb.append closeTagSymbol
            }
        }
        return sb.toString()     
    }
    
    public boolean isOpenTag(String tag) {
	return tag ==~ /<[^>]*>/ && scd.label.containsValue(tag)
    }
    
    public boolean isCloseTag(String tag) {
	return tag ==~ /<\/[^>]*>/ && scd.label.containsValue(tag)
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
    
    public NamedEntity parseOpenTag(String tag) {
	
	NamedEntity ne = new NamedEntity()
	    
	Matcher m = tag =~ /<$([^>]*)>/
	if (m.matches()) {		
	  String cat = m.group(1)
    
          List c1 = (cat ? scd.label.findAll{it.value == cat}.collect{it.key} : null)
          String c1s
          if (c1?.size() == 1) {c1s = c1[0]}

          SemanticClassification sc = new SemanticClassification(c1s, null, null)
	  ne.classification << sc
 
	} else {
	    log.warn("Did not matched tag: $tag. Returning null")
	    return null
	} 
	return  ne
   }
}