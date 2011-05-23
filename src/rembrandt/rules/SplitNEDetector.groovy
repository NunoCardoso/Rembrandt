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
import rembrandt.obj.Term
import rembrandt.obj.NamedEntity
import rembrandt.obj.ListOfNE
import saskia.bin.AskSaskia
import rembrandt.rules.NamedEntityDetector
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.CommonClauses as CC
/**
 * @author Nuno Cardoso
 * 
 * This is a class used for meaning detection rules over sentences
 *
 */
public class SplitNEDetector extends Detector {

    /**
     * This method detects, collects and returns a list of meanings.
     * It does NOT browse the sentence, and returns first match 
     */
    AskSaskia saskia
    NamedEntityDetector ierules
    static Logger log = Logger.getLogger("SplitNEDetector")


    // gets an internalEvidenceRules([EN|PT])
     public SplitNEDetector(NamedEntityDetector ierules, AskSaskia saskia) {
	 this.ierules = ierules
	 this.saskia = saskia
    }
     
     void processNE(NamedEntity ne, ListOfNE NEs, List<Rule>rules = rules) {
       
       for (rule in rules) {
	 
	 def generatedObject
	// println "rule: $rule sentence: $s"
   	 def matchObject = matchRule(new SplitNEMatcherObject(rule:rule, 
                 original_ne:ne, sentence:ne.asSentence()))
 	 //println "MatchedObject? ${matchObject}"
   	 
   	 if (matchObject) generatedObject = performActionsOnMatcherObject(matchObject, NEs)
   
         // return something that signals I've made something
	 if (generatedObject)  return 	      	  
	}
        
        // if we get here, it's because the SplitNE rules did not matched, or matched but did not performed actions
        // Note that this processNE is called for NEs with gluers, so report the NE that's not handled.
        log.warn "NE $ne was NOT handled in ${this.class.name}."     	
   }
    
   public performActionsOnMatcherObject(MatcherObject o, NEs) {   
        // perform the o.rule.action, if exists, else do the default action
        // for this one, it's better to call the actions outside the MatcherObject.
        def generatedObject
        if (o.rule.action) {
            o.rule.action.each{c -> generatedObject = c(o, NEs) }
        } else throw new IllegalStateException("SplitNEDetector rules MUST have defined actions!" )
        return generatedObject
    }
   
   Closure capture_NE_o_NE = {SplitNEMatcherObject o, ListOfNE NEs ->  
   // println "o.lang=${o.lang}"
   // println "o.pastMatches=${o.pastMatches}"
   	List<Term> terms1 = o.getMatchByClause(CC.notGluer1_1Pc[o.lang]).terms
   // println "terms1=$terms1 terms1.index =${terms1.index}"
    List<Term> terms2 = o.getMatchByClause(CC.notGluer2_1Pc[o.lang]).terms
      //  println "terms2=$terms2"        
	fillSubNE(o, terms1, terms2)    
   } 


   
   // {NE} gluer {NE gluer NE}
   Closure capture_NE_o_NEoNE = {SplitNEMatcherObject o, ListOfNE NEs -> 
	List<Term> terms1 = o.getMatchByClause(CC.notGluer1_1Pc[o.lang]).terms
	List<Term> terms2 = o.getMatchesByClauses([CC.notGluer2_1Pc[o.lang], CC.gluer2_1c[o.lang], CC.notGluer3_1Pc[o.lang]]).terms
	fillSubNE(o, terms1, terms2)           
   } 
   
   Closure capture_NEoNE_o_NE = {SplitNEMatcherObject o, ListOfNE NEs -> 
	List<Term> terms1 = o.getMatchesByClauses([CC.notGluer1_1Pc[o.lang], CC.gluer1_1c[o.lang], CC.notGluer2_1Pc[o.lang]]).terms
	List<Term> terms2 = o.getMatchByClause(CC.notGluer3_1Pc[o.lang]).terms
	fillSubNE(o, terms1, terms2)           
   } 
   
   Closure capture_NEoNE_o_NEoNE = {SplitNEMatcherObject o, ListOfNE NEs -> 
	List<Term> terms1 = o.getMatchesByClauses([CC.notGluer1_1Pc[o.lang], CC.gluer1_1c[o.lang], CC.notGluer2_1Pc[o.lang]]).terms
	List<Term> terms2 = o.getMatchesByClauses([CC.notGluer3_1Pc[o.lang], CC.gluer3_1c[o.lang], CC.notGluer4_1Pc[o.lang]]).terms
	fillSubNE(o, terms1, terms2)
   } 	
   
   void fillSubNE(SplitNEMatcherObject o, List terms1, List terms2) {
   	NamedEntity ne1 = new NamedEntity(terms:terms1,
		sentenceIndex:o.sentence.index, termIndex:terms1[0].index)	    
	NamedEntity ne2 = new NamedEntity(terms:terms2,
		sentenceIndex:o.sentence.index, termIndex:terms2[0].index)    

	o.split_nes = [ne1, ne2]
	o.saskia_nes = [saskia.answerMe(ne1), saskia.answerMe(ne2)]             
	o.ie_nes = [ierules.processInternalEvidenceOnNE(ne1), ierules.processInternalEvidenceOnNE(ne2)]             
 
  }
}
