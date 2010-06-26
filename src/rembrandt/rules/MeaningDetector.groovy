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
import rembrandt.obj.Sentence
import rembrandt.obj.Criteria
import rembrandt.obj.Rule
import rembrandt.obj.SemanticClassification

/**
 * @author Nuno Cardoso
 * 
 * This is a class used for meaning detection rules over sentences
 *
 */
public class MeaningDetector extends Detector {

    /**
     * This method detects, collects and returns a list of meanings.
     * It does NOT browse the sentence, and returns first match 
     */
     List<SemanticClassification> processMeanings(Sentence s, List<Rule>rules = rules) {
       
       for (rule in rules) {

	 // be careful, if the sentence pointer is not reset, rules that advance the pointer 
	 // will make the pointer forward for the next ones! 
	   
	 s.resetPointerToFirstVisibleTerm() // reset the pointer to the first term
	 // sentence pointer will not advance. It should be 0, but make sure of it
	 
	 def generatedObject
	// println "rule: $rule sentence: $s"
   	 def matchObject = matchRule( new MatcherObject(rule:rule, sentence:s))
 	 //println "MatchedObject? ${matchObject}"	
	 if (matchObject) generatedObject = performActionsOnMatcherObject(matchObject)   
         // return first match
	 if (generatedObject)  return generatedObject	      	  
	}
	return null
     }
    
    public performActionsOnMatcherObject(MatcherObject o, obj = null) {
        return o.getMatchedMeanings()
    }
 	     
}
