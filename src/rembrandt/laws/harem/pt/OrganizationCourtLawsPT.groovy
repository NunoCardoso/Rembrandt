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
 * Court Laws for ORGANIZACAO
 */
class OrganizationCourtLawsPT {
    
   static final laws = [
    
    /* ORGANIZACAO / ORGANIZACAO debate */                     
    
    // 99.1 a NE 'ORGANIZACAO' bigger eats a NE 'ORGANIZACAO' smaller
    new Law(id:"P:ORG>=D:ORG", description:"P:ORGANIZACAO >= D:ORGANIZACAO -> D:Try-Disamb-Catch-Replace-Update-Bound",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
	    plaintiffEvidence:[[SC.organization]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.organization]],
	    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries )
    
    // 99.2 a NE 'ORGANIZACAO' smaller loses to a NE 'ORGANIZACAO' bigger	 
    ,new Law(id:"P:ORG<D:ORG", description:"P:ORGANIZACAO >= D:ORGANIZACAO -> P:Discard",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
	    plaintiffEvidence:[[SC.organization]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.organization]],
	    decision:CD.DiscardPlaintiff )
   
    
    /* non-EM / ORGANIZACAO  */
 
    // a NE 'ORGANIZACAO' bigger eats a NE 'ORGANIZACAO' smaller
    ,new Law(id:"P:ORG>=D:!ORG", description:"P:ORGANIZACAO >= D:!ORGANIZACAO -> D:Try-Disamb-Catch-Replace-Update-Bound",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
	    plaintiffEvidence:[[SC.organization]],
	    defendantCriteria:[[CC.NeverAllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.organization]],
	    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries )
    
    // a NE 'ORGANIZACAO' smaller loses to a NE 'ORGANIZACAO' bigger	 
    ,new Law(id:"P:ORG<D:!ORG", description:"P:ORGANIZACAO < D:!ORGANIZACAO -> P:Discard",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
	    plaintiffEvidence:[[SC.organization]],
	    defendantCriteria:[[CC.NeverAllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.organization]],
	    decision:CD.DiscardPlaintiff )   
    ]
}