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
 * Court Laws for NUMERO. Theres' no need for more rules, plaintiff NEs with NUMERO are the first ones to be detected,
 * and they don't challenge non-NUMERO EMs.
 */
class NumberCourtLawsPT {
    
    static final laws = [
    
    // a NE SC.number bigger eats a NE SC.number smaller
    new Law(id:"P:NUM>=P:NUM", description:"P:NUMBER >= D:NUMBER -> D:Try-Disamb-Catch-Replace-Update-Bound",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
	    plaintiffEvidence:[[SC.number]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.number]],
	    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries ),
    
    // a NE SC.number smaller loses to a NE SC.number bigger	 
    new Law(id:"NUMBER Law 1.2", description:"P:NUMBER < D:NUMBER -> P:Discard",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
	    plaintiffEvidence:[[SC.number]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.number]],
	    decision:CD.DiscardPlaintiff )
    
  ]
    
}