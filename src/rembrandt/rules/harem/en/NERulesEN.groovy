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
 
package rembrandt.rules.harem.en

import rembrandt.obj.Document
import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.NamedEntity
import rembrandt.obj.RulePolicy
import rembrandt.obj.ListOfNE
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.Patterns  
import rembrandt.gazetteers.en.StopwordsEN    
import rembrandt.gazetteers.en.OrganizationGazetteerEN
import rembrandt.gazetteers.en.NumberGazetteerEN
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.CommonClauses as CC
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.SemanticClassificationDefinitions
import rembrandt.rules.NamedEntityDetector

/**
 * 
 * @author Nuno Cardoso
 * Rules for capturing candidates do NE.
 */
class NERulesEN extends NamedEntityDetector {

    List<Rule> rules
    SemanticClassification sc = new SemanticClassification(SemanticClassificationDefinitions.unknown)
    
    /**
     * Main constructor
     */
     public NERulesEN() {
	
	rules = []
               
      /* capitalized words 1P
       * Ex: Eiffel Tower
       */
       rules.add(new Rule(id:"NERulesEN 1", description:"{Xxxx}+", sc:sc, 
       policy:RulePolicy.Rule, clauses:[ CC.capitalizedAlph1Pc ] )) 
        
       /* 
        * NE glued with 'of' (Ex: Chamber of Commerce),  's (Ex: England 's Bank)  'and'(Ex: Smith and Sons)
        */
       rules.add(new Rule(id:"NERulesEN 2", description:"{<EM>! gluerEN <EM>!}", sc:sc,
       policy:RulePolicy.Rule, clauses: [NEGazetteer.NE_EM_1c, CC.gluer1_1c['en'], NEGazetteer.NE_EM2_1c] ))
      
       rules.add(new Rule(id:"NERulesEN 3", description:"{<EM>! 's! <EM>!}", sc:sc,
       policy:RulePolicy.Rule, clauses: [NEGazetteer.NE_EM_1c, ClausesEN.apostrophe1c, NEGazetteer.NE_EM2_1c] ))

       rules.add(new Rule(id:"NERulesEN 4", description:"{<EM>! and! <EM>!}", sc:sc, 
       policy:RulePolicy.Rule, clauses: [NEGazetteer.NE_EM_1c, ClausesEN.and1c, NEGazetteer.NE_EM2_1c] ))
       
       /* (\\d+[th|rd|nd|st]) <EM> 
        * Ex: 2nd World War
        */    
       rules.add(new Rule(id:"NERulesEN 5", description:"{(\\d+[th|rd|nd|st])! <EM>!}", sc:sc,
       policy:RulePolicy.Rule, clauses:[NumberGazetteerEN.ordinalNumber1c, NEGazetteer.NE_EM_1c] ))       
	   
       // {<EM>! ,? [Inc.|Corp.]}              
       rules.add(new Rule(id:"NERulesEN 6", description:"{<EM>! ,? [Inc.|Corp.]}", sc:sc,
       policy:RulePolicy.Rule, clauses: [NEGazetteer.NE_EM_1c, CC.comma01c, OrganizationGazetteerEN.companySuffix1c] ))   
   }
    
    
    public eliminateStopwordNEs(Document doc) {
	// eliminate all NEs that are single-term, and belong on this list.
	eliminateStopwordNEs(doc.titleNEs)
	eliminateStopwordNEs(doc.bodyNEs)
    }	     
    
    public eliminateStopwordNEs(ListOfNE NEs) {
        List<NamedEntity> NEsToRemove = []
        NEs.each{ne -> 
            if (ne.terms.size() == 1 && StopwordsEN.stopwordNEs.contains(ne.terms[0].text)) 
                NEsToRemove << ne
        }
        NEs.removeNEs(NEsToRemove)
    }
    
    public readjustNEsBeginningSentences(Document doc) {
	readjustNEsBeginningSentences(doc.titleNEs)
	readjustNEsBeginningSentences(doc.bodyNEs)
    }
	
    public readjustNEsBeginningSentences(ListOfNE NEs) {
    
    // move termIndex and term list for all NEs that begin sentences, and first term is stopword.
	NEs.each{ne -> 
	   if ((ne.termIndex == 0) && StopwordsEN.stopwordNEs.contains(ne.terms[0].text)) {
	       int index = NEs.indexOf(ne)
	       NEs[index].termIndex++
	       NEs[index].terms.remove(0)
	       // se tiver uma stopword
	       if (NEs[index].terms && NEs[index].terms[0].text ==~ Patterns.gluerPattern['en']) {
		   NEs[index].termIndex++
		   NEs[index].terms.remove(0)
	       }						    
	       NEs[index].reportToHistory("NERules: NE left-chopped, now has terms ${NEs[index].terms}")
	   }
	} 	
    }
}