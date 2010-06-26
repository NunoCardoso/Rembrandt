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
import rembrandt.rules.MatcherObject

class DBpediaPropertyDefinitionsEN {
    
   static MatcherObject mo = new MatcherObject()
   static Logger log = Logger.getLogger("SaskiaMain")
	
   
   // text expressions of predicates, mapped to dbpedia-owl properties 
    static predicatesToOntologyProperties = [
	  [needle:[ ["habitant"],["population"] ], 
			answer: ["dbpedia2:populationTotal","dbpedia2:populationEstimate"] ],
	  [needle:[ ["born"], ["birthdate"] ],   answer:["dbpedia-owl:birthdate"] ], 
	  [needle:[ ["died"] ],			answer:["dbpedia-owl:deathdate"] ],
	  [needle:[ ["located"] ], answer:["dbpedia2:location", "dbpedia2:locationCity"] ],
	// fica situada
	  [needle:["weight"], 			answer:["dbpedia-owl:weight"] ] ,
	  [needle:["height"], 			answer:["dbpedia-owl:height"] ] ,
	  [needle:["capital"],			answer:["dbpedia-owl:capital"] ],
	  [needle:["nacionality"],	answer:["dbpedia-owl:nationality"] ], 
	

			// age is not a DBpedia property on the ontology... I made it up
	  [needle:[ ["year"], ["age"] ], answer:["age"] ]

	]
	// text expressions of operators, mapped to dbpedia-owl properties 
	def static predicateMeanings = [
	   [needle:["have"], answer:PredicateOperator.Has]
	]
	
	// list of dbpedia-owls, and what are the NEobject types 
	def static ontologyToLocalPredicates = [
	]
	
	def static eatFromPredicate = [
	 [needle:["dbpedia2:location"],							answer:[category:["LOCAL"] ] ], 
	 [needle:["dbpedia2:locationCity"],						answer:[category:["LOCAL"], type:["HUMANO"], subtype:["DIVISAO"] ] ],
	 [needle:["dbpedia2:populationTotal"],					answer:[category:["NUMERO", "VALOR"] ] ], 
	 [needle:["dbpedia-owl:capital"], 						answer:[category:["LOCAL"], type:["HUMANO"], subtype:["DIVISAO"] ] ],
	 [needle:["dbpedia-owl:nationality"], 					answer:[category:["LOCAL"], type:["HUMANO"], subtype:["PAIS"] ] ],
	 [needle:["dbpedia-owl:weight","dbpedia-owl:height"],	answer:[category:["NUMERO","VALOR"] ] ],	
	]

	
	
	static List getOntologyPropertyFromPredicate(predicate) {
		// predicate in an array of terms of lemmas
	//	println "predicate:$predicate"
		return mo.meaningMatch(predicate, predicatesToOntologyProperties)
	//	println "answer: $answer"
		// it should be null or a Map
	//	println "ans: "+answer+" size "+answer.size()+" !answer "+(!answer)
		//if (!answer) return null
		//return answer.answer.meaning
	}
	
	static String getLocalPredicateFromOntologyProperty(String ontologyProperty, String lang = null) {
		if (lang == null) lang = Configuration.newInstance().get("global.lang")
		def answer = null
		ontologyToLocalPredicates.each{it -> 
			if ((it.needle == ontologyProperty) && (it.answer.containsKey(lang)) ) 
				answer = it.answer[lang]	
		}
		return answer
	}
	
	static Map getHAREMclassificationsForEATfromPredicate(Predicate predicate) {
	    def res = [:]
	    predicate.dbpedia_ontology_property.each{pred -> 
	       def res2 = eatFromPredicate.find{it.needle.contains(pred)}?.answer
		   if (res2) {
		       if (!res.containsKey('category')) res.category = res2.category
		       else res.category << res2.category
		   }
		}
		if (res.containsKey('category')) res.category = res.category.unique()
		return res
	}
	
	static getPredicateMeaning(String predicate) {
		return mo.meaningMatch(predicate, predicateMeanings)
	}
}