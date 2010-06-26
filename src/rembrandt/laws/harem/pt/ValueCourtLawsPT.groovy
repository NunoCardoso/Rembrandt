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

package rembrandt.laws.harem.pt

import rembrandt.obj.Law
import rembrandt.obj.ClassificationCriteria as CC
import rembrandt.obj.BoundaryCriteria as BC
import rembrandt.obj.CourtDecision as CD
import rembrandt.gazetteers.CommonClassifications as SC

/**
 * @author Nuno Cardoso
 * Court Laws for VALOR
 */
class ValueCourtLawsPT {
    
    static final laws = [
    
     
     new Law(id:"P:VAL>=D:VAL", description:"P:VALUE >= D:VALUE -> D:Try-Disamb-Catch-Merge-Update-bound",
 	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
 	    plaintiffEvidence:[[SC.value]],
 	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
 	    defendantEvidence:[[SC.value]],
 	    decision:CD.TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries )
                      
      // smaller VALUE does not replace bigger VALUE
     ,new Law(id:"P:VAL<D:VAL", description:"P:VALUE < D:VALUE -> P:Discard",
 	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
 	    plaintiffEvidence:[[SC.value]],
 	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
 	    defendantEvidence:[[SC.value]],
 	    decision:CD.DiscardPlaintiff )
                        
    // 2: def: NUMERO pla: VALUE
    // a VALUE replaces NUMERO|TIME , anytime!                       
    ,new Law(id:"P:VAL>=D:(E)[NUM|TIM]", description:"P:VALUE >= D:(E)[NUMERO|TIME] -> D:Try-Disamb-Catch-Repl-Update-bound",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains],
	    plaintiffEvidence:[[SC.value]],
	    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category]], 
	    defendantEvidence:[[SC.number, SC.time]],
	    decision:CD.ReplaceCompletelyDefendant )
       
    //if smaller:  loses.                      
    ,new Law(id:"P:VAL<D:(E)[NUM|TIM]", description:"P:VALUE < D:(E)[NUMERO|TIME] -> P:Discard",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy],
	    plaintiffEvidence:[[SC.value]],
	    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category]], 
	    defendantEvidence:[[SC.number, SC.time]],
	    decision:CD.DiscardPlaintiff )
    
    // By default, VALUE loses to all non-Number, Time, Value, EM
  
    
    ]
}