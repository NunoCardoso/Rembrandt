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
 * Court Laws for ACONTECIMENTO. Includes rules where plaintiff has category ACONTECIMENTO
 */
class EventCourtLawsPT {
    
    static final laws = [
    
    // 99.1 bigger ACONTECIMENTO replaces smaller ACONTECIMENTO
    new Law(id:"P:ACO>=P:ACO", description:"P:ACONTECIMENTO >= D:ACONTECIMENTO -> D:Try-Disamb-Catch-Merge-Update-bound",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
	    plaintiffEvidence:[[SC.event]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category] ], 
	    defendantEvidence:[[SC.event]],
	    decision:CD.TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries )
    
    // 99.2 smaller ACONTECIMENTO does not replace bigger ACONTECIMENTO
    ,new Law(id:"P:ACO<P:ACO", description:"P:ACONTECIMENTO >= D:ACONTECIMENTO -> P:Discard",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
	    plaintiffEvidence:[[SC.event]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.event]],
	    decision:CD.DiscardPlaintiff )
       
    // 2. VALOR CLASS -> ACONT
    // happens with <VALOR CLASS>21¼</VALOR>, <ACONT>21¼ Congresso</ACONT> -> bigger only!
    ,new Law(id:"P:ACO>D:VALOR_CLASS", description:"P:ACONTECIMENTO >= D:ACONTECIMENTO -> D:ReplaceCompletely",
	    // ExistsAtLeastOneOfThese referes to the plaintiffNE. AllOfThem refers to the plaintiffEvidence
	    plaintiffCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category], BC.Contains], 
	    plaintiffEvidence:[[SC.event]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Type]], 
	    defendantEvidence:[[SC.value_classification]],
	    decision:CD.ReplaceCompletelyDefendant )
     
    ]
}