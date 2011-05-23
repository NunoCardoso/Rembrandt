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


package rembrandt.laws.harem.en

import rembrandt.obj.Law
import rembrandt.obj.ClassificationCriteria as CC
import rembrandt.obj.BoundaryCriteria as BC
import rembrandt.obj.CourtDecision as CD
import rembrandt.gazetteers.CommonClassifications as SC

/**
 * @author Nuno Cardoso
 * Court Laws for EM
 */
class NECourtLawsEN {
    
    static final laws = [
    
    // a NE with category replaces same NE with a category classes.unknown 
    
    // Let's just forget this one, as we will lead the initiative to the plaintiff. 
    // so, let's make the rules thinking on the plaintiff initiative
    
    // a NE classes.unknown exact size or smaller loses to a NE classes.unknown exact size or bigger	 
    new Law(id:"P:EM<=D:EM", description:"P:EM <= D:EM -> P:Discard",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrIsContainedBy], 
    plaintiffEvidence:[[SC.unknown]],
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.unknown]],
    decision:CD.DiscardPlaintiff ),
    
    // Only once I allow a (bigger) EM win over a non-EM: when it's a VALOR or NUMBER
    new Law(id:"P:EM>D:[EM|VAL|NUM]", description:"P:EM > D:[EM|VALUE_NUMBER] -> D:Replace_Update_Bounds",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.Contains], 
    plaintiffEvidence:[[SC.unknown]],
    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category]], 
    defendantEvidence:[[SC.unknown, SC.value, SC.number]],
    decision:CD.ReplaceCompletelyDefendant ),
    
    // These are the two major laws:
    
    // 1): All plaintiffNEs of EM lose to non-EM!
    // Law P:EM>D:[EM|VAL|NUM] is the exception, but since it's first on the queue, if it's matched, this law is not used. 	    
    new Law(id:"P:EM<=>D:!EM", description:"P:EM <=> D:!EM - P:DiscardPlaintiff",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]],
    plaintiffEvidence:[[SC.unknown]],
    defendantCriteria:[[CC.NeverAllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.unknown]],
    decision:CD.DiscardPlaintiff ),     
    
    // 2) the most important: all non-EM wins over EM!
    // This is the only rule where the plaintiff is quite generic. 	    
    
    new Law(id:"P:!EM<=>D:EM", description:"P:!EM <=> D:EM - D:ReplaceCompletely",
    plaintiffCriteria:[[CC.NeverAllOfThese, CC.AllOfThem, CC.Category]],
    plaintiffEvidence:[[SC.unknown]],
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.unknown]],
    decision:CD.ReplaceCompletelyDefendant )  
    ]
    
}