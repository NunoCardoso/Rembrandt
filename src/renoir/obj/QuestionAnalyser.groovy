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
import rembrandt.obj.Document
import rembrandt.obj.Rule
import rembrandt.obj.SemanticClassification
import rembrandt.obj.Sentence
import rembrandt.rules.Detector
import rembrandt.rules.MatcherObject


/**
 * @author Nuno Cardoso
 * Main class for detection of patterns in questions, and perform actions on them
 */
class QuestionAnalyser extends Detector {
     
    static Logger log = Logger.getLogger("RuleMatcher") 
    
      /**
       * applies pattern rules to the initial query string, 
       * to create a real question. It can also use info from the question.
       */
    public Question applyRulesBrowseQuestion(Question question, List rules = rules) { 

	for (rule in rules) {

	    // be careful, if the sentence pointer is not reset, rules that advance the pointer 
	    // will make the pointer forward for the next ones! 		   
	    question.sentence.resetPointerToFirstVisibleTerm() // reset the pointer to the first term
		 // sentence pointer will not advance. It should be 0, but make sure of it
	    while ( question.sentence.thereAreVisibleTermsAhead()) {
		def generatedObject
		//println "rule: $rule sentence: ${question.sentence} pointer: ${question.sentence.pointer} indexes: ${question.sentence*.index}"
		def matchObject = matchRule( new MatcherObject(rule:rule, sentence:question.sentence, 
			NEs:question.nes))
		//println "MatchedObject? ${matchObject}"	
		if (matchObject) performActionsOnMatcherObject(matchObject, question)   
		// return first match
		 
		question.sentence.movePointerForVisibleTerms()
	    }
	}
	return question
	
    }
	 
    public Question applyRulesDontBrowseQuestion(Question question, List rules = rules) { 

	for (rule in rules) {

	    // be careful, if the sentence pointer is not reset, rules that advance the pointer 
	    // will make the pointer forward for the next ones! 		   
	    question.sentence.resetPointerToFirstVisibleTerm() // reset the pointer to the first term
		 // sentence pointer will not advance. It should be 0, but make sure of it
	    def generatedObject
	   // println "rule: $rule sentence: ${question.sentence}"
	    def matchObject = matchRule( new MatcherObject(rule:rule, sentence:question.sentence, 
		    NEs:question.nes))
		   println "MatchedObject? ${matchObject}"	
	    if (matchObject) performActionsOnMatcherObject(matchObject, question)   
		// return first match
	
	}
	return question
    }
    
    public performActionsOnMatcherObject(MatcherObject o, q) {
	def actions = o.rule.action   
	if (actions instanceof Closure) actions(o, q)
	else if (actions instanceof List) {	
	  o.rule.action.each{a -> if (a instanceof Closure) a(o, q) }  
	} else {
	     log.error "Tried to apply a rule action, but no Closure found."
	     log.error "Rule class action = ${this.rule.action.class}."
	     throw new IllegalStateException() 
	}   
    }
}// class

