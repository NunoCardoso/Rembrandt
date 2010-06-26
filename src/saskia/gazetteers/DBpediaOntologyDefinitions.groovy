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
 
package saskia.gazetteers

import saskia.bin.Configuration 
import java.util.regex.Pattern 

class DBpediaOntologyDefinitions {
	
	static DBpediaOntologyDefinitions _this 
	Configuration conf = Configuration.newInstance()
	
	
	public DBpediaOntologyDefinitions newInstance() {
		if (!_this) {
			String version = conf.get("saskia.dbpedia.version","3.5.1")
			_this = Class.forName("saskia.dbpedia.DBpediaOntology"+version.replaceAll(".","")+"Definitions").newInstance()
			if (!_this) 
				throw new IllegalStateException("Can't load DBpedia internals, no support for version $version")
		}
		return _this	
	}
	
	List generateMeaning(String lang) {
	  List res = []
	  //println "generateNameConcept for lang $lang"
	  _this.definitions.each{definition -> 
	   def res2 = [needle:[], answer:definition.answer]	    
	   if (definition?.needle?.get(lang)) {
	       def need = definition?.needle?.get(lang)
	       if (need instanceof String || need instanceof Pattern) res2.needle << need
	       // for lists within lists, there is no need of a special OR -> just add each list in the needle,
	       // the MeaningMatch will perform the AND and OR behavior as expected
	       if (need instanceof List) {
		  	  if (need[0] instanceof String || need[0] instanceof Pattern) res2.needle << need
		   	   else if (need[0] instanceof List) {
		       		need.each{need2 -> res2.needle << need2}
		   		}
	       }	       
	   }
	   if (res2.needle) res << res2
	  }
	  return res
	}
}