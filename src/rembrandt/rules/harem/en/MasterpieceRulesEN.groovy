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

import rembrandt.obj.Rule
import rembrandt.obj.Clause
import rembrandt.obj.RulePolicy
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.en.MasterpieceGazetteerEN
import rembrandt.gazetteers.en.NumberGazetteerEN
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.CommonClauses
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector

/** 
 * @author Nuno Cardoso
 * List of external rules for MASTERPIECE category.
 */
class MasterpieceRulesEN extends NamedEntityDetector {
   
    List<Rule> rules	
    SemanticClassification sc	
    /**
      * Main constructor
      */
     public MasterpieceRulesEN() {
	    
	rules = []
		
	/******* MASTERPIECE PLAN *******/
		
	// 1 [law|...]! nr? {<EM>!}
	rules.add(new Rule(id:"MasterpieceRulesEN OBRA PLANO 1", description:"[law|...]! nr? {<EM>!}",
	sc:SC.masterpiece_plan, policy:RulePolicy.Clause, clauses:[ 
	     MasterpieceGazetteerEN.decree1nc, 
	     NumberGazetteerEN.numberPrefix01nc, 
	     NEGazetteer.NE_Anything_1c
	] ))	

	// 2 [law|...]! nr? {\d+/\d+!}
	rules.add(new Rule(id:"MasterpieceRulesEN OBRA PLANO 2", description:"[law|...]! nr? {\\d+/\\d+!}",
	sc:SC.masterpiece_plan, policy:RulePolicy.Clause, clauses:[ 
	     MasterpieceGazetteerEN.decree1nc, 
	     NumberGazetteerEN.numberPrefix01nc, 
	     NumberGazetteerEN.publicationNumber1c
	] ))	
		
	// 3 {<EM>!} [law|...]!
	rules.add(new Rule(id:"MasterpieceRulesEN OBRA PLANO 3", description:"{<EM>!} [law|...]!",
	sc:SC.masterpiece_plan, policy:RulePolicy.Clause, clauses:[ 
	     NEGazetteer.NE_Anything_1c, 
	     MasterpieceGazetteerEN.decree1nc
	] ))		
	
	// 4 [project|...]! "! {.*}! "!
	rules.add(new Rule(id:"MasterpieceRulesEN OBRA PLANO 4", description:"[project|...]! \"! {.*}! \"!",
	sc:SC.masterpiece_plan, policy:RulePolicy.Clause, clauses:[ 
	     MasterpieceGazetteerEN.project1nc, 
	     CommonClauses.aspasOpen1nc, 
	     CommonClauses.words2Collectable0Pc,
	     CommonClauses.aspasClose1nc] ))	
			 		
	/******* MASTERPIECE REPRODUCED *******/
	
	// 1 [book|...]! {<EM>!}
	rules.add(new Rule(id:"MasterpieceRulesEN OBRA REPRODUZIDA 1", description:"[book|...]! {<EM>!}",
	sc:SC.masterpiece_reproduced, policy:RulePolicy.Clause, clauses:[ 
	      MasterpieceGazetteerEN.entertainment1nc,
	      NEGazetteer.NE_Anything_1c
	] ))				
	 
	// 2 {<EM>!} [book|...]! 
	rules.add(new Rule(id:"MasterpieceRulesEN OBRA REPRODUZIDA 2", description:"{<EM>!} [book|...]!",
        sc:SC.masterpiece_reproduced, policy:RulePolicy.Clause, clauses:[ 
	      NEGazetteer.NE_Anything_1c, 
	      MasterpieceGazetteerEN.entertainment1nc		      
	] ))	
	
	// 3 //[book|...]! "! {.*}! "!
	rules.add(new Rule(id:"MasterpieceRulesEN OBRA REPRODUZIDA 3", description:"[book|...]! \"! {.*}! \"!",
        sc:SC.masterpiece_reproduced, policy:RulePolicy.Clause, clauses:[ 
	      MasterpieceGazetteerEN.entertainment1nc, 
	      CommonClauses.aspasOpen1nc, 
	      CommonClauses.words2Collectable0Pc,
	      CommonClauses.aspasClose1nc] ))	

	/******* MASTERPIECE WORKOFART *******/
				
	sc = new SemanticClassification(Classes.category.masterpiece, Classes.type.workofart)

	// 1 [statue]! of? {<EM>}!
	rules.add(new Rule(id:"MasterpieceRulesEN OBRA ARTE 1", description:"[statue|...]! of? {<EM>}!",
        sc:SC.masterpiece_workofart, policy:RulePolicy.Clause, clauses:[ 
	     MasterpieceGazetteerEN.masterpiece1nc, 
	     ClausesEN.of01nc, 
	     NEGazetteer.NE_Anything_1c
	] ))				
			    
	// 2 {<EM>}! [statue]!
	rules.add(new Rule(id:"MasterpieceRulesEN OBRA ARTE 2", description:"{<EM>}! [statue]!",
	sc:SC.masterpiece_workofart, policy:RulePolicy.Clause, clauses:[ 
	     NEGazetteer.NE_Anything_1c,
	     MasterpieceGazetteerEN.masterpiece1nc
	] ))			
    }
}