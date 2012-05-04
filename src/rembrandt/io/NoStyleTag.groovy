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
 * Default Outputs NO style tag
 */
class NoStyleTag extends StyleTag {
    
    String NEtag
    
    String altTag = ""

    // Basic symbols
    String openTagSymbol = ""
    String closeTagSymbol = ""		
    
    static Logger log = Logger.getLogger("Reader")
    String lang

    /**
     * Public constructor.
     * @param lang the language of the labels. Default: rembrandt.output.styletag.lang, or 
     * global.lang if it's not specified.
     */
    public NoStyleTag(String lang) {
        this.lang = lang
    } 
    
    public String printOpenTag(NamedEntity ne) {     
        return "";
    }
    
    public String printCloseTag(NamedEntity ne = null) {
        return "";
    }
    
    public boolean isOpenTag(String tag) {
		return false
    }
    
    public boolean isCloseTag(String tag) {
		return false
    }    
    
    public boolean isOpenALTTag(String tag) {
		return false
    }
    
    public boolean isCloseALTTag(String tag) {
		return false
    }
    
    public boolean isOpenSubALTTag(String tag) {
		return false
    }
    
    public boolean isCloseSubALTTag(String tag) {
		return false
    }
    
    public String printOpenALTTag() {
		return ""
    } 
    
    public String printCloseALTTag() {
		return ""
    } 
  
    public String printOpenSubALTTag(int index) {
		return ""
    } 
    
    public String printCloseSubALTTag(int index) {
		return ""
    } 
    
    public NamedEntity parseOpenTag(String tag) {
		return null
   }
}