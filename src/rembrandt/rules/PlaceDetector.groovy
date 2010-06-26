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
 
package rembrandt.rules

import org.apache.log4j.Logger
import rembrandt.obj.*
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.pt.ClausesPT
import saskia.gazetteers.Places

/**
 * @author Nuno Cardoso
 * 
 * This is a class used for meaning detection rules over sentences
 *
 */
public class PlaceDetector extends Detector {
    
    String lang
    List rules 
    
    public PlaceDetector(String lang) {
	this.lang=lang
	Clause placeAdjective1 = new Clause(name:"placeAdjective1",
	   cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
	   pattern:Places.getMeaningList(lang, "adj"))
	//Clause notPlaceAdjective1p = new Clause(name:"notPlaceAdjective1p",
	 //  cardinality:Cardinality.One, criteria:Criteria.NotPlaceAdjectiveMatch)
	Clause placeName1 = new Clause(name:"placeName1",
	   cardinality:Cardinality.One, criteria:Criteria.MeaningMatch, 
	   pattern:Places.getMeaningList(lang, "name"))
	//Clause notPlaceName1 = new Clause(name:"notPlaceName1p",		
	  // cardinality:Cardinality.One, criteria:Criteria.NotPlaceNameMatch)
	
	//println "placeAdjective1 pattern: "+placeAdjective1.pattern
	//println "placeName1 pattern: "+placeName1.pattern
	if (lang == "pt") {
	   rules = [
	   new Rule(id:'N_Adj', description:'XXX portugueses', 
		   //a notPlaceAdjective is a non-greedy "anything except a place adjective" 
	      clauses:[placeAdjective1], 
		 action:[{Expando q, MatcherObject o -> 
			//println "OOO: ${o.pastMatches}"
		     q.subjectTerms = o.getMatchedAndUnmatchedTermsBeforeMatchedClause(placeAdjective1)
		    // println "q.subjectTerms: ${q.subjectTerms}"
		     
		     q.placeAdjectiveTerms = o.getMatchedTermsByClause(placeAdjective1)
		     q.match = o.getMatchByClause(placeAdjective1)
		     q.matchedRuleID = 'N_Adj'
		  }]) ,
	   new Rule(id:'N_de_N', description:'XXX de Portugal',
	      clauses:[ClausesPT.dnaeosem1c, placeName1], 
		action:[{Expando q, MatcherObject o -> 

		     q.subjectTerms = o.getMatchedAndUnmatchedTermsBeforeMatchedClause(ClausesPT.dnaeosem1c)
		     q.placeNameTerms = o.getMatchedTermsByClause(placeName1)
	             q.match = o.getMatchByClause(placeName1)
     	             q.matchedRuleID = 'N_de_N'
	          }])
	    ]
       } else if (lang == "en") {
	   rules = [
	     new Rule(id:'Adj_N', description:'Portuguese XX', 
		 clauses:[placeAdjective1], 
	 	 action:[{Expando q, MatcherObject o -> 	 	     
		     q.subjectTerms = o.getMatchedAndUnmatchedTermsAfterMatchedClause(placeAdjective1)
 		     q.placeAdjectiveTerms = o.getMatchedTermsByClause(placeAdjective1)
 		     q.match = o.getMatchByClause(placeAdjective1)
 		     q.matchedRuleID = 'Adj_N'
 		  }]) ,
 	   new Rule(id:'N_of_N', description:'XXX of the Portugal', 
 	      clauses:[ClausesEN.of1c, ClausesEN.the01c, placeName1], 
 		action:[{Expando q, MatcherObject o -> 
 		     
 		     q.subjectTerms = o.getMatchedAndUnmatchedTermsBeforeMatchedClause(ClausesEN.of1c)
 		     q.placeNameTerms = o.getMatchedTermsByClause(placeName1)
 	             q.match = o.getMatchByClause(placeName1)
      	             q.matchedRuleID = 'N_of_N'
 	          }])
 	    ]
       }
    }
    /**
     * This method detects, collects and returns a list of meanings.
     * It browses the sentence, and returns first match 
     */
     Expando process(Sentence s) {
       
       for (rule in rules) {
 
	 // be careful, if the sentence pointer is not reset, rules that advance the pointer 
	 // will make the pointer forward for the next ones! 
	   
	 s.resetPointerToFirstVisibleTerm() // reset the pointer to the first term
	 // sentence pointer will not advance. It should be 0, but make sure of it
	 
	 // first match, return, else browse the sentence
	 // you have to, for patterns like XXXX of Portugal
	  while (s.thereAreVisibleTermsAhead()) {
               
	      def generatedObject
	      // println "rule: $rule sentence: $s"
	       def matchObject = matchRule( new MatcherObject(rule:rule, sentence:s, lang:lang))
	       //println "MatchedObject? ${matchObject}"	
	       if (matchObject) generatedObject = performActionsOnMatcherObject(matchObject)   
	       // return first match
	       if (generatedObject)  return generatedObject	 
		
	       s.movePointerForVisibleTerms()
          }      	  
	}
	return null
     }
    
    public Expando performActionsOnMatcherObject(MatcherObject o, obj = null) {
	Expando q = new Expando()
	def actions = o.rule.action   
	if (actions instanceof Closure) {
	     actions(q, o)
	     return q
	}
	else if (actions instanceof List) {	
	     
	     o.rule.action.each{a -> if (a instanceof Closure) a(q, o) }  
	     return q
	 } else {
	     log.error "Tried to apply a rule action, but no Closure found."
	     log.error "Rule class action = ${this.rule.action.class}."
	     throw new IllegalStateException() 
	 }   
       return null
    }
 	     
}
