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

import renoir.obj.*
import saskia.bin.Configuration
import org.apache.log4j.*
import rembrandt.rules.*
import rembrandt.gazetteers.CommonClassifications as SC

class DBpediaPropertyDefinitionsPT {
    
	static MatcherObject mo = new MatcherObject()
	static Logger log = Logger.getLogger("SaskiaMain")
	
	def static eatFromPredicate = []
	// things like "Person/birthPlace":"Person" ] don't need to be here, they can be inferred!
	// "PopulatedPlace/capital" resolves to a "PopulatedPlace as EAT, but the reasoning part uses capital!
	/*, 
	 [needle:["dbpedia2:locationCity"],						answer:[category:["LOCAL"], type:["HUMANO"], subtype:["DIVISAO"] ] ],
	 [needle:["dbpedia2:populationTotal"],					answer:[category:["NUMERO", "VALOR"] ] ], 
	 [needle:["dbpedia-owl:capital"], 						answer:[category:["LOCAL"], type:["HUMANO"], subtype:["DIVISAO"] ] ],
	 [needle:["dbpedia-owl:nationality"], 					answer:[category:["LOCAL"], type:["HUMANO"], subtype:["PAIS"] ] ],
	 [needle:["dbpedia-owl:weight","dbpedia-owl:height"],	answer:[category:["NUMERO","VALOR"] ] ],	
	]*/

	/*def static predicateMeanings = [
	  [needle:["ter"], answer:PredicateOperator.Has]
	]*/
	
	/*static List getOntologyPropertyFromPredicate(predicate) {
	 	return mo.meaningMatch(predicate, predicatesToOntologyProperties)
	}*/
	
	static List getDBpediaClassForEATfromPredicate(Predicate predicate) {
		List res = []
		// dbpediaOntologyProperty are dbpedia-owl properties. property is the String 
		predicate.dbpediaOntologyProperty?.property?.each{p -> 
			def m = p =~ /(.*)\/(.*)/
			if (m.matches()) {
				String eat = m.group(1)
				// Pernos/birthPlace is a property where it's easy to get the EAT: it's in the left of /
				if (DBpediaOntology.classes.contains(eat)) res << eat
				else log.warn "hey, $p has a / but no correspondence on DBpediaOntology!"
			} else {
				def res2 =  eatFromPredicate[p]
				res2?.each{r -> if (!res.contrains(r)) res << r}
			}
		}
		return res
	}
	
	static PredicateOperator getPredicateMeaning(String predicate) {
		return predicateMeanings.find{it.needle.contains(predicate)}?.answer
	}
}