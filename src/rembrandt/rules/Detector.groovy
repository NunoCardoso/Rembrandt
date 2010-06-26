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
import rembrandt.obj.Cardinality
import rembrandt.obj.Clause
import rembrandt.obj.Criteria
/**
 * @author Nuno Cardoso
 * Main class for executing rules into documents or sentences, and to generate matching objects.
 * This class has the matching mechanism of clauses against unseen parts of sentences. Refer to the 
 * known subclass to get the proper default action for the successfully matched rules. Note that 
 * you can override the action by giving an action property for each Rule object.
 */
 abstract class Detector {     

    static Logger rlog = Logger.getLogger("RuleMatcher")
	
	/** Return only the first match, do not be greedy */
    static final int RETURN_FIRST_MATCH = 1
	
	/** Return all matches, be greedy */
    static final int RETURN_ALL_MATCHES = 2
	
	/** walk through the sentence to fetch more NEs */
    static final int BROWSE_SENTENCE = 3

	/** do not walk through the sentence, stay in the beginning */
    static final int DO_NOT_BROWSE_SENTENCE = 4

    public abstract Object performActionsOnMatcherObject(MatcherObject mo, Object o)
    
   /**
    * This method is called when the rule was successfully matched.
    * It can be extended to perform additional processing.
    * @param o MatcherObject with the information from matched rules.
    * @param message optional message for logging.
    * @return The MatcherObject o.  
    */
    public MatcherObject reportMatchedRule(MatcherObject o, String message='No message given.') { 
	rlog.debug "RULE FTW: ${o.rule} CapturedTerms:${o.pastMatches.values()} $message"
   	return o
   }
		 
   /**
   * This method is called when the rule was not successfully matched.
   * It can be extended to perform additional processing.
   * @param o MatcherObject with the information from the unmatched rules.
   * @param message optional message for logging.
   * @return null, always. 
   */
    public reportUnmatchedRule(MatcherObject o, String message='No message given') {
        rlog.debug "RULE FAIL: ${o.rule} $message"
        return null
   }
	
   /** The idea of this method is to have a recursive function, along each clause of the rule, and
    * collect information about the candidate NE in its recursive path. The rule is
    * becoming shorter and in the end, if the rule is successfully matched to the terms, it
    * has an empty list of clauses. If a successfully match, the candidate NE jumps
    * to the collected NEs, otherwise, it is discarded and returns false, so that the closure
    * can skip to the next rule
    */
    public matchRule(MatcherObject o) {
        // if there are clauses left  but the sentence is over 
        if (o.thereAreMoreClauses() && !o.thereAreMoreVisibleTerms()) { 
        
            // return false if are more mandatory clauses left (One or OneAndMore)
           List mandatoryClause = []
           // For the One clauses, if last matched while there were terms, jumps to the next clause.
           //. So, this list of unmatched clauses begins in the first unseen clause
           List<Clause> remainingClauses = o.rule.clauses[o.clauseIndex..(o.rule.clauses.size()-1)]
           for (clause in remainingClauses) {
                if (clause.cardinality.equals(Cardinality.One) && 
                    !(clause.criteria.equals (Criteria.SentenceEndMatch) )) mandatoryClause << clause        	    
		if (clause.cardinality.equals(Cardinality.OneOrMore)) {
		      if (!o.pastMatches.containsKey(clause)) mandatoryClause << clause      
		}
            }
           
           if (mandatoryClause) return reportUnmatchedRule(o, "There are mandatory clauses left to check: ${mandatoryClause}.")     
           else return reportMatchedRule(o, "No more mandatory clauses.")     
        }
            
        // Successful condition: list of clauses of the rule is empty.
        // So, do whatever the actios tells it to do. Most of the times,
        // to put the full terms of the candidate NE to the collectedNEs list.   
        if (!o.thereAreMoreClauses()) {

    	// good, out of clauses, perfect match.
        // good, out of clauses and terms, perfect match.
           return reportMatchedRule(o, "Match no more clauses and no more rules")
        }
         // take a clause, and prove it rigt or wrong. 
         // Uses the remaining sentence and current clause
         // null for no-match, [terms: clause:] for match
 
        def matched = o.matchClause()
        rlog.trace "Matched:${matched} on clause ${o.getCurrentClause()}"       
 
        switch (o.getCurrentClause().cardinality) {
       
        /*************
         * ZeroOrOne *
         *************/   
        case Cardinality.ZeroOrOne: 
       		
           rlog.trace "Starting ZeroOrOne, matched=${matched} clause=${o.clauseIndex}:${o.getCurrentClause()} rule=${o.rule}, sentence now =${o.sentence.getUnseenVisibleTerms()}"         
           if (matched)  {
       		o.collectMatchedInfo(matched)
        	o.jumpToNextTerm(matched.terms.size())
           }      
            o.jumpToNextClause()        	
            rlog.trace "Ended ZeroOrOne, next clause=${o.clauseIndex}:${o.getCurrentClause()} rule=${o.rule}, sentence next =${o.sentence.getUnseenVisibleTerms()}"
  	    break // end ZeroOrOne	 
            
  	/**************
  	 * ZeroOrMore *
  	 **************/            
        case Cardinality.ZeroOrMore:     		
            
          rlog.trace  "Starting ZeroOrMore, matched=${matched} clause=${o.clauseIndex}:${o.getCurrentClause()} rule=${o.rule}, sentence now =${o.sentence.getUnseenVisibleTerms()}"
          if (matched)  {
            o.collectMatchedInfo(matched)
            o.jumpToNextTerm(matched.terms.size())  
          }        
          if (!matched) o.jumpToNextClause()                   			
          rlog.trace  "Ended ZeroOrMore, next clause=${o.clauseIndex}:${o.getCurrentClause()} rule=${o.rule}, sentence next =${o.sentence.getUnseenVisibleTerms()}"
	break // end ZeroOrMore
            
 	/************
 	 * One      *
 	 ************/   
 	case Cardinality.One:
 		 	
 	   rlog.trace "Doing One, matched=${matched} clause=${o.clauseIndex}:${o.getCurrentClause()} rule=${o.rule}, sentence now =${o.sentence.getUnseenVisibleTerms()}"
 	   if (!matched) return reportUnmatchedRule(o, "Failed to match a One clause: ${o.getCurrentClause()} in unseen sentence ${o.sentence.getUnseenVisibleTerms()}")				     
           if (matched) {
		o.collectMatchedInfo(matched)			
		o.jumpToNextClause()
		o.jumpToNextTerm(matched.terms.size()) 
           }
  	   rlog.trace  "Ended One, next clause=${o.clauseIndex}:${o.getCurrentClause()} rule=${o.rule}, sentence next =${o.sentence.getUnseenVisibleTerms()}"
	 break // end One

	 /*************
	  * OneOrMore *
	  *************/ 
 	case Cardinality.OneOrMore:
 			    
 	    rlog.trace "Doing OneOrMore, matched=${matched} clause=${o.clauseIndex}:${o.getCurrentClause()} rule=${o.rule}, sentence now =${o.sentence.getUnseenVisibleTerms()}" 	        	
	    if (!matched) return reportUnmatchedRule(o, "failed to match OneOrMore clause: ${o.getCurrentClause()} in unseen sentence ${o.sentence.getUnseenVisibleTerms()}")
 	    if (matched)  {
 		o.collectMatchedInfo(matched)
 		// let's see if next match is not null; it is not null, keep the clause	
 		// if it's null, let's jump to the next one
 		o.jumpToNextTerm(matched.terms.size()) 		
 		if (! o.matchClause() )  o.jumpToNextClause()	        	
 	    }	             
  	    rlog.trace "Ended OneOrMore, next clause=${o.clauseIndex}:${o.getCurrentClause()} rule=${o.rule}, sentence next =${o.sentence.getUnseenVisibleTerms()}"
 	 break           
        } // switch cardinality
        
        return matchRule(o)  
    }// method matchRule
}// class