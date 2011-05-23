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

import rembrandt.obj.Rule
import rembrandt.obj.Clause
import rembrandt.obj.RulePolicy
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.pt.MasterpieceGazetteerPT
import rembrandt.gazetteers.pt.NumberGazetteerPT
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClauses
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector
/** 
 * @author Nuno Cardoso
 * List of external rules for ABSTRACCAO category.
 */
class MasterpieceRulesPT extends NamedEntityDetector {
   
    static List<Rule> rules	
 	
    /**
     * Main constructor
     */
    public MasterpieceRulesPT() {
	    
	rules = []	
		
	/******* OBRA PLANO *******/

	// 1 [art|lei]! nº? {<EM>!}
	rules.add(new Rule(id:"MasterpieceRulesPT OBRA PLANO 1", description:"[art|lei]! nº? {<EM>!}",
	sc:SC.masterpiece_plan, policy:RulePolicy.Clause, clauses:[ 
	      MasterpieceGazetteerPT.publication1nc, 
	      NumberGazetteerPT.numberPrefix01nc, 
	      NEGazetteer.NE_Anything_1c] ))	
	      
	//  projecto! "? <EM>! "?
	rules.add(new Rule(id:"MasterpieceRulesPT OBRA PLANO 2", description:"projecto! \"? <EM>! \"?",
		sc:SC.masterpiece_plan, policy:RulePolicy.Clause, clauses:[ 
	        MasterpieceGazetteerPT.projecto1nc, 
		CommonClauses.aspasOpen1nc, 
		CommonClauses.words2Collectable0Pc, 
		CommonClauses.aspasClose1nc] ))	 	
		
	/******* OBRA REPRODUZIDA *******/
	   	    
	// 2 [filme|livro]! {<EM>!}
	rules.add(new Rule(id:"MasterpieceRulesPT OBRA REPRODUZIDA 1", description:"[filme|livro]! {<EM>!}",
		sc:SC.masterpiece_reproduced, policy:RulePolicy.Clause, clauses:[ 
	       MasterpieceGazetteerPT.entertainment1nc, 
	       NEGazetteer.NE_Anything_1c] ))				

		
	//3 livro ".*"
	rules.add(new Rule(id:"MasterpieceRulesPT OBRA REPRODUZIDA 2", description:"[filme|livro]! \"! {<EM>!} \"!",
		sc:SC.masterpiece_reproduced, policy:RulePolicy.Clause, clauses:[ 
	        MasterpieceGazetteerPT.entertainment1nc, 
		CommonClauses.aspasOpen1nc, 
		CommonClauses.words2Collectable0Pc, 
		CommonClauses.aspasClose1nc] ))	
	    //   println rules
       
       	/******* OBRA ARTE *******/

	// 4 [estátua|quadro]! deaos01nc? {<EM>}!
	rules.add(new Rule(id:"MasterpieceRulesPT OBRA ARTE 1", description:"[estátua|quadro]! deaos? {<EM>}!",
		sc:SC.masterpiece_workofart, policy:RulePolicy.Clause, clauses:[ 
		MasterpieceGazetteerPT.masterpiece1nc, 
		ClausesPT.daeos01nc, 
		NEGazetteer.NE_Anything_1c] ))					    
	}
    
   			
		
}