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
 
package rembrandt.rules.harem.pt

import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.RulePolicy
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.obj.NamedEntity
import rembrandt.obj.Document
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.obj.ListOfNE
import rembrandt.obj.ConflictPolicy
import rembrandt.gazetteers.pt.StopwordsPT
import rembrandt.gazetteers.pt.OrganizationGazetteerPT
import rembrandt.gazetteers.pt.NumberGazetteerPT
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.Patterns
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClauses as CC
import rembrandt.rules.NamedEntityDetector

/**
 * @author Nuno Cardoso
 * Rules for capturing candidates do NE.
 */
class NERulesPT extends NamedEntityDetector {

    static List<Rule> rules
    
    /**
     * Main constructor
     */
     public NERulesPT() {
	
       rules = []
               
	   /* capitalized words 1p */
       rules.add(new Rule(id:"NERulesPT-1", description:"{capitalizedAlphNum1Pc}", sc:SC.unknown, 
          policy:RulePolicy.Rule, clauses:[ CC.capitalizedAlphNum1Pc ] )) 

       	  /* Rio de Janeiro. Note the overwriting...*/
       rules.add(new Rule(id:"NERulesPT-2", description:"{Rio! de! Janeiro!}", sc:SC.unknown,
	  policy:RulePolicy.Rule, addpolicy:ConflictPolicy.Overwrite, clauses:[ 
	       ClausesPT.rio1c, 
	       ClausesPT.de1c,
	       ClausesPT.janeiro1c]  ))
   
	       
       /* NE glued with d[eao]s? */
       rules.add(new Rule(id:"NERulesPT-3.1", description:"{<EM>! d[eao]s? <EM>!}", sc:SC.unknown,
	 policy:RulePolicy.Rule, clauses:[
	      NEGazetteer.NE_EM_1c, 
	      CC.gluer1_1c['pt'], 
	      NEGazetteer.NE_EM2_1c] ))
   
     /* NE glued with d[eao]s? */
       rules.add(new Rule(id:"NERulesPT-3.2", description:"{<EM>! d[eao]s? <EM>!}", sc:SC.unknown,
	  policy:RulePolicy.Rule, clauses:[
	       NEGazetteer.NE_EM_1c, 
	       CC.gluer1_1c['pt'], 
	       NEGazetteer.NE_EM2_1c] ))

	 /* NE glued with d[eao]s? */
       rules.add(new Rule(id:"NERulesPT-3.3", description:"{<EM>! d[eao]s? <EM>!}", sc:SC.unknown,
	   policy:RulePolicy.Rule, clauses:[
	       NEGazetteer.NE_EM_1c, 
	       CC.gluer1_1c['pt'], 
	       NEGazetteer.NE_EM2_1c] ))
  
	   /* NE glued with e */
/*	   rules.add(new Rule(id:"EM2.5", description:"{<EM>! e! <EM>!}",
	   category:category, policy:RulePolicy.Rule, clauses: 
	       [NEGazetteer.NE_EM_1c, ClausesPT.e1c, NEGazetteer.NE_EM2_1c] ))
*/
	       
       // Numeral (\\d+[¼»oa]) ex: 5Â» <EM>               
       rules.add(new Rule(id:"NERulesPT-4", description:"{5¼ <EM>}", sc:SC.unknown,
	policy:RulePolicy.Rule, clauses:[
	   NumberGazetteerPT.ordinalNumber1c, 
	   NEGazetteer.NE_EM_1c] ))       
	   
	   // <EM>! ,! [S.A.|Inc.|Lda.]              
       rules.add(new Rule(id:"NERulesPT-5", description:"{<EM>! ,! Lda|Inc|SA}", sc:SC.unknown,
	policy:RulePolicy.Rule, clauses: [
	   NEGazetteer.NE_EM_1c, 
	   CC.comma1c, 
	   OrganizationGazetteerPT.empresasSuffix1c] ))   
   }
    
    public eliminateStopwordNEs(Document doc) {
	// eliminate all NEs that are single-term, and belong on this list.
	eliminateStopwordNEs(doc.titleNEs)
	eliminateStopwordNEs(doc.bodyNEs)
    }	     
    
    public eliminateStopwordNEs(ListOfNE NEs) {
	List<NamedEntity> NEsToRemove = []
	NEs.each{ne -> 
	    if (ne.terms.size() == 1 && StopwordsPT.stopwordNEs.contains(ne.terms[0].text)) 
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
	   if ((ne.termIndex == 0) && StopwordsPT.stopwordNEs.contains(ne.terms[0].text)) {
	       int index = NEs.indexOf(ne)
	       NEs[index].termIndex++
	       NEs[index].terms.remove(0)
	       // se tiver uma stopword
	       if (NEs[index].terms && NEs[index].terms[0].text ==~ Patterns.gluerPattern['pt']) {
		   NEs[index].termIndex++
		   NEs[index].terms.remove(0)
	       }		    
	       NEs[index].reportToHistory("NERules: NE left-chopped, now has terms ${NEs[index].terms}")
	   }
	} 	
    }
}