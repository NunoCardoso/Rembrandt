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
import rembrandt.obj.Sentence
import rembrandt.obj.Rule
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality
import rembrandt.obj.EntityRelation
import rembrandt.obj.ClassificationCriteria as CC
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.pt.LocalGazetteerPT
import rembrandt.gazetteers.pt.PersonGazetteerPT
import rembrandt.gazetteers.CommonClauses
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.rules.EntityRelationDetector

/**
 * @author Nuno Cardoso
 * Rules for capturing entity relation for NE in the same sentence.
 */
class EntityRelationRulesPT extends EntityRelationDetector {
    
    static List<Rule> rules
	
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
      
    /**
     * Main constructor
     */  
    public EntityRelationRulesPT() {
	    
	rules = []

	// INCLUIDO/INCLUI
		
	// <LOCAL>! ,! [em|[dn][aoe]]? <LOCAL>!
	rules.add (new Rule(id:"EntityRelationRulesPT-1", description:"<LOCAL>! ,! [em|[dn][aoe]]? <LOCAL>! ",  
	  clauses:[NE_LOCAL_collectable, 
	           CommonClauses.comma1nc, 
	           ClausesPT.dnaeosem01nc, 
	           NE_LOCAL_includes] ) )
	   	
	// <LOCAL>! (! [em|[dn][aoe]]? <LOCAL>!
	rules.add (new Rule(id:"EntityRelationRulesPT-2", description:"<LOCAL>! (! [em|[dn][aoe]]? <LOCAL>!", 
	   clauses:[NE_LOCAL_collectable, 
	            CommonClauses.openBrackets1c, 
	            ClausesPT.dnaeosem01nc, 
	            NE_LOCAL_includes]  ) )
	   		     
	 // SEDE_DE
	     
	 //<EM1>! ,? localizad[oa]|sediad[oa]! n[ao]|em! <EM2>! -> EM2 fica com COREL(EM1), TIPOREL (sede_de)
	 rules.add (new Rule(id:"EntityRelationRulesPT-3", description:"<EM1>! ,? localizad[oa]|sediad[oa]! n[ao]|em! <EM2>!", 
	   clauses:[NE_EM_collectable, 
	            CommonClauses.comma01nc, 
	            LocalGazetteerPT.localizado1nc, 
	            ClausesPT.dnaeosem01nc, 
	            NE_EM_based_on] ) )

	 // OTHER
	     
	 //<EM1>! ,! [presidente|chefe|etc]! [dn][aeo]! <EM2>!
	 rules.add (new Rule(id:"EntityRelationRulesPT-4", description:"<EM1>! ,! [presidente|chefe|etc]! [dn][aeo]! <EM2>!", 
	  clauses:[NE_EM_collectable, 
	           CommonClauses.comma1nc, 
	           PersonGazetteerPT.cargoAll1nc, 
	           ClausesPT.dnaeosem01nc,  
	           NE_EM_other]  ) )
	     
	     //<EM1>! ,! [primo|etc]! daeo! <EM2>!
	 rules.add (new Rule(id:"EntityRelationRulesPT-5", description:"<EM1>! ,! [primo|etc]! daeo! <EM2>!", 
	  clauses:[NE_EM_collectable, 
	           CommonClauses.comma1nc, 
	           PersonGazetteerPT.parentescoAll1nc, 
	           ClausesPT.daeo1nc, 
	           NE_EM_other]  ) )   
    }
}