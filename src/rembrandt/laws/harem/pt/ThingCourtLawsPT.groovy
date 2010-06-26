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
 * Court Laws for COISA
 */
class ThingCourtLawsPT {
   
    static final laws = [
    
     new Law(id:"P:THI>=D:THI", description:"P:THING >= D:THING -> D:Try-Disamb-Catch-Merge-Update-bound",
 	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
 	    plaintiffEvidence:[[SC.thing]],
 	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
 	    defendantEvidence:[[SC.thing]],
 	    decision:CD.TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries )
                      
      // smaller THING does not replace bigger THING
     ,new Law(id:"P:THI<D:THI", description:"P:THING < D:THING -> P:Discard",
 	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
 	    plaintiffEvidence:[[SC.thing]],
 	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
 	    defendantEvidence:[[SC.thing]],
 	    decision:CD.DiscardPlaintiff )
         
    // def: ORG pla: COISA 
    // ex: produtos Nestlé. it is replaced, for same size.
    // TODO  we should keep DBpeida/wikipedia info...
    ,new Law(id:"P:THI=D:ORG", description:"P:THING = D:ORGANIZATION ->  D:Try-Disamb-Catch-Replace-Update-bound",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
	    plaintiffEvidence:[[SC.thing]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactMatch], 
	    defendantEvidence:[[SC.organization]],
	    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries)
    
    ]
}