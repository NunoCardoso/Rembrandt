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

import org.apache.log4j.*
import saskia.gazetteers.DBpediaPropertyDefinitionsPT
import rembrandt.obj.Term
import saskia.dbpedia.DBpediaProperty


class Predicate {
	
    Logger log = Logger.getLogger("QuestionParser")
	List<Term> terms
	// this is straight from the main DBpedia ontology
	List<DBpediaProperty> dbpediaOntologyProperty
	// this is from the local infoboxes, not matched to DBpedia ontology
	//List dbpedia_local_property = []
	
	/*void lookupPredicate() {

	// go to DBpedia and see if it is term.
	// let's use the stem.
		def ontologypredicate, localpredicate
		
		//log.debug "Searching ontology predicates for ${terms[0].lemma}"
    	ontologypredicate = DBpediaPropertyDefinitionsPT.getOntologyPropertyFromPredicate(terms*.lemma) 
		//log.debug "Got: $ontologypredicate"
		
	   // ontologyProperties are a String
		if (ontologypredicate) { 
			ontologypredicate.each{ 
				dbpedia_ontology_property << it
				localpredicate = DBpediaPropertyDefinitionsPT.getLocalPredicateFromOntologyProperty(it)
				if (localpredicate) { 
					dbpedia_local_property << localpredicate
				}
			}
		}
	}*/
	
	public String toString() {
		return "Predicate(terms:$terms, dbpediaOntologyProperty:$dbpediaOntologyProperty)"
	
	}
}	
