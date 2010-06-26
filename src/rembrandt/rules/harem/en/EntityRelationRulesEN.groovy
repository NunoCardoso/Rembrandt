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

import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality
import rembrandt.obj.EntityRelation
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.obj.ClassificationCriteria as CC
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.gazetteers.en.LocalGazetteerEN
import rembrandt.gazetteers.en.PersonGazetteerEN
import rembrandt.gazetteers.CommonClauses
import rembrandt.rules.EntityRelationDetector
/**
 * Rules for capturing entity relation for NE in the same sentence.
 */
class EntityRelationRulesEN extends EntityRelationDetector {
    
    List<Rule> rules
	
static final Clause NE_LOCAL_collectable = new Clause(name:"NE_LOCAL_1", cardinality:Cardinality.One, 
		criteria:Criteria.NEMatch, NECriteria:[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category], 
		pattern:[SC.place], role:EntityRelation.CollectID)

	    static final Clause NE_LOCAL_includes = new Clause(name:"NE_LOCAL_1", cardinality:Cardinality.One, 
		criteria:Criteria.NEMatch, NECriteria:[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category], 
		pattern:[SC.place], role:EntityRelation.SetIncludes)

	    static final Clause NE_EM_collectable = new Clause(name:"NE_EM_collectable", cardinality:Cardinality.One, 
		criteria:Criteria.NEMatch, NECriteria:[CC.AnyKnownOrUnknownCategory,null,null], 
		pattern:[], role:EntityRelation.CollectID)

	    static final Clause NE_EM_based_on = new Clause(name:"NE_EM_collectable", cardinality:Cardinality.One, 
		criteria:Criteria.NEMatch, NECriteria:[CC.AnyKnownOrUnknownCategory,null,null], 
		pattern:[], role:EntityRelation.SetBasedOn)

	    static final Clause NE_EM_other = new Clause(name:"NE_EM_collectable", cardinality:Cardinality.One, 
		criteria:Criteria.NEMatch, NECriteria:[CC.AnyKnownOrUnknownCategory,null,null], 
		pattern:[], role:EntityRelation.SetOther)
	   
         
    public EntityRelationRules() {
	    
	rules = []

	/** INCLUIDO/INCLUI **/
		
	// 1 <LOCAL>! ,! of? <LOCAL>!
	rules.add (new Rule(id:"EntityRelationRulesEN 1", description:"<LOCAL>! ,! of? <LOCAL>! ",
	  clauses:[NE_LOCAL_collectable, 
	           CommonClauses.comma1nc, 
	           ClausesEN.of01nc, 
	           NE_LOCAL_includes]
	  ) )
	   	
	  // 2 <LOCAL>! (! of? <LOCAL>!
	rules.add (new Rule(id:"EntityRelationRulesEN 2", description:"<LOCAL>! (! of? <LOCAL>!",  
	   clauses:[NE_LOCAL_collectable, 
	            CommonClauses.openBrackets1, 
	            ClausesEN.of01nc, 
	            NE_LOCAL_includes]
	    ) )
	   		     
	/** BASED_ON **/
	    
	// 3 <EM1>! ,? based! [io]n|at! <EM2>! -> EM2 fica com COREL(EM1), TIPOREL (sede_de)
	rules.add (new Rule(id:"EntityRelationRulesEN 3", description:"<EM1>! ,? [based]! [io]n|at! <EM2>!",  
        clauses:[NE_EM_collectable, 
                 CommonClauses.comma01nc, 
                 LocalGazetteerEN.based1, 
                 ClausesEN.ation1c, 
                 NE_EM_based_on] ) )

         /** OTHER **/
	    
         // 4 <EM1>! ,! [president|chief|etc]! of! the? <EM2>!
        rules.add (new Rule(id:"EntityRelationRulesEN 4", description:"<EM1>! ,! [president|chief|etc]! of! the? <EM2>!", 
        clauses:[NE_EM_collectable, 
                 CommonClauses.comma1nc, 
                 PersonGazetteerEN.cargo1c, 
                 ClausesEN.of1c, 
                 ClausesEN.the01c, 
                 NE_EM_other] ) )
	     
	// 5 <EM1>! ,! [primo|etc]! daeo! <EM2>!
	 rules.add (new Rule(id:"EntityRelationRulesEN 5", description:"<EM1>! ,! [primo|etc]! daeo! <EM2>!",
	 clauses:[NE_EM_collectable, 
	          CommonClauses.comma1nc, 
	          PersonGazetteerEN.parentesco1c, 
	          ClausesEN.daeo1, 
	          NE_EM_other] ) )
	     
    }
}