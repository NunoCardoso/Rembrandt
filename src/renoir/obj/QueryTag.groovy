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
 
package renoir.obj

import rembrandt.obj.Sentence
import renoir.suggestion.SuggestionType
import renoir.server.JSONHelper
import net.sf.json.JSONArray

import org.apache.log4j.Logger
class QueryTag {
     
     SuggestionType type
     Sentence name
     Map ground = [:]
     static Logger log = Logger.getLogger("SaskiaMain")
     int index
     
     public String toString() {
	 	return "${type}:${name}:${ground}:${index}"           
     }

     // I don't trust JSON parsers...
    // Example os String: [{"name": "Porque", "type": "WQ", "desc": undefined, "ground": undefined}]
    // State 0: outside. 
    // State 1: inside the array 
    // State 2: inside a Map
    // State 3: inside a Key
    // State 4: inside a value
    //  State 5: indei
    // state 98, 99: On a \ 
     
     static List slurpTagJSON(String string) {
        // 
        List res = []
        Map map 
        int state = 0
        String key = null
        String value = null
        
        string.trim().each{c -> 
           switch(state) {
                case 0:
                // It's a [
                   state = 1
                   if (c != "[") throw new IllegalStateException("Must be a [!")
                 break
                
                case 1: 
                if (c == "]") {
                    state = 0
                } else if (c == "{") {
                    state = 2
                    map = [:]
                } else if (c == ":") {
                } else if (c == ",") {
                   
                } else throw new IllegalStateException("Must be a {!")
                
           	break
        
        	case 2: 
        	    if (c == "\"") {
                        if (!key) {
                            // it's a new key  
                            key = ""
                            state = 3
                        } else if (!value) {       
                            value=""
                    	state = 4
                        } 
                        
        	    } else if (c == ":") {
                    } else if (c == ",") { 
                	//println "key: $key value: $value"
                	if (value == "undefined") value = null
                    //println "value $value"
                	map[key] = value
                	key = null
                	value = null
        	    } else if (c == "}") {
        		state = 1
        		// there's probable a leftover
                        if (key) {
                            if (value == "undefined") value = null
                            map[key] = value
                            key = null
                            value = null
                        }
        		res << map
        	    } else if (c.matches(/\d/)) {
        		
        		if (key) {
                           if (!value) value = c
                           else value += c
        		}
        	    }
        
                break
                
                case 3: 
                    if (c == "\\") {
                	state=98
                        key += c
                    	// add for now... we may keep it, we may remove it.
                    } else if (c == '"') {
                        state = 2
                    } else {
                	key += c
                    }
                   
                break
                
                case 4: 
                if (c == "\\") {
                    state=99
                    value += c
                    // add for now... we may keep it, we may remove it.
                } else if (c == '"') {
                    state = 2
                } else {
                    value += c
                }
                
                break
            
                case 98:
                if (c == '"' || c == '\\') {
                    // replace last character
                    key = key.substring(0, key.size()-1)+c
                } else { 
                    key += c
                }
                state = 3
                break

                case 99:
                    if (c == '"' || c == '\\' || c == "'") {
                        // replace last character
                        value = value.substring(0, key.size()-1)+c
                    } else { 
                        value += c
                    }
                    state = 4
                    break
            }//switch
        }// each c
        
        // println "RembradntReader: doc.body_sentences = "+doc.body_sentences
        return res
    }
    
    // makes a JSON version out of it
    static String toJSON(List<QueryTag> tags) {
	List t = tags
	
        StringBuffer sb = new StringBuffer()
        sb.append "["
        t.each{tag -> 
           sb.append "{\"name\":\""
           sb.append tag.name.toStringLine().replaceAll(/(["'])/) {all, g1 -> "\\"+g1}
           sb.append "\", \"type\":\""       
           sb.append tag.type.text
           sb.append "\""         
           def id = tag.ground['id']
           if (id) {
            sb.append ", \"ground\":\"$id\"" 
            tag.ground.remove("id")		
           }
           tag.ground.each{g -> 
            	sb.append ", \""+g.key.replaceAll(/(["'])/) {all, g1 -> "\\"+g1}
            	sb.append "\": \""+g.value.replaceAll(/(["'])/) {all, g1 -> "\\"+g1}
            	sb.append "\""
           }
           sb.append "},"
        }
	// if there are elements, remove trailing comma 
        if (t) {sb.setCharAt(sb.size()-1, "]".toCharArray()[0])} else {sb.append "]"}
        return sb.toString()
    }
    
    static List<QueryTag> parseTags(String string) {
        
        List x = slurpTagJSON(string)
        List<QueryTag> res = []
        
        x.each{it ->       
           QueryTag qt = new QueryTag() 
           if (it['type']) qt.type = SuggestionType.getFromValue(it['type'])
           if (it['name']) qt.name = Sentence.simpleTokenize(it['name'])
           if (it['desc']) {
               switch(qt.type) {
                case [SuggestionType.WhereQuestion, SuggestionType.Predicate,
                SuggestionType.Operator,SuggestionType.Geoscope,
                SuggestionType.DBpediaClass, SuggestionType.WikipediaCategory, 
                SuggestionType.WikipediaGroupOfCategories]: 
                qt.ground['desc'] = it['desc']
                break
                
                case [SuggestionType.NamedEntity] :
                qt.ground['category'] = it['desc']
                break
            }
          }
        if (it['ground']) {
            switch(qt.type) {
                case [SuggestionType.WhereQuestion, SuggestionType.Predicate,
                SuggestionType.Operator,SuggestionType.Geoscope,
                SuggestionType.DBpediaClass, SuggestionType.WikipediaCategory, 
                SuggestionType.WikipediaGroupOfCategories, SuggestionType.NamedEntity]: 
                                   if (it['ground'].matches(/\d+/)) {
                       qt.ground['id'] = Long.parseLong(it['ground'])
                       
                   }
                break
            }
         }
         res << qt
        }
        log.info "res="+res
        return res
        //	[{"name": "Porto", "type": "NE", "desc": undefined, "ground": undefined}]       LIST
    }
}