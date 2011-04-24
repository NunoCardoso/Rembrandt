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

package saskia.server

import java.util.Map
import java.util.List
import java.util.Set

import saskia.db.DocStatus;

/**
 * @author rlopes
 *Adapted and improved from http://www.iuliandogariu.com/2006/09/getting-groovy-with-json/
 */
public class JSONHelper {

	public static String toJSON(obj) {
		String ret = "${obj}"
		
		if (obj == null) {
			ret = "null"
		}
		else if (obj instanceof String) {
//			def escaped = obj.replace("\"", "\\\"")
// diferença entre replace e replaceAll, é que o primeiro não interpreta regex			
//			ret = "\"${escaped}\""
			def escaped = obj.replace("\"", "\\\"") 
			// importante para manter o escape do formato REMBRANDT
			escaped = escaped.replace("\\[", "\\\\[")
			escaped = escaped.replace("\\]", "\\\\]")
			ret = "\"${escaped}\""
		}
		else if (obj instanceof Date || obj instanceof DocStatus) {
			def escaped = obj.toString().replace("\"", "\\\"")
			ret = "\"${escaped}\""
		}
		else if (obj instanceof Map) {
	        ret = "{" + obj.keySet().inject("") { accu, k ->
	            (accu.length() == 0? "": "${accu},") +
	            "\"${k}\": ${JSONHelper.toJSON(obj[k])}"
	        } + "}"
	    }
	    else if (obj instanceof List || obj instanceof Set) {
	        ret = "[" + obj.inject("") { accu, item ->
	            (accu.length() == 0? "": "${accu},") + JSONHelper.toJSON(item)
	        } + "]"
	    }
		
	    return ret.replaceAll(/\n/,"")
	}
	
}
