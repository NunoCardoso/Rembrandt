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
 * Court Laws for TEMPO
 */
class TimeCourtLawsEN {
    
    static final laws = [
    
    // be drastic for rules that normally span more than one defendant. 
    //plaintiff: <TEMPO>20 Fev 2007</TEMPO>, defendants:<NUM>20</NUM> <TEMPO>Fev 2007</TEMPO>
    // I can't just update both defendants,they'll overlap!
    new Law(id:"P:TIM>=D:TIM", description:"P:TIME >= D:TIME -> D:Try-Disamb-Catch-Merge-Update-bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.time]],
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.time]],
    decision:CD.ReplaceCompletelyDefendant)
    
    
    // smaller TIME does not replace bigger TIME
    ,new Law(id:"P:TIM<D:TIM", description:"P:TIME < D:TIME -> P:Discard",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
    plaintiffEvidence:[[SC.time]],
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.time]],
    decision:CD.DiscardPlaintiff )
    
    
    // 2: def: NUMERO pla: TEMPO
    // a NE SC.time replaces 'NUMERO', anytime!                       
    ,new Law(id:"P:TIM>=D:NUM", description:"P:TIME >= D:NUMERO -> D:Try-Disamb-Catch-Repl-Update-bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains],
    plaintiffEvidence:[[SC.time]],
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.number]],
    decision:CD.TryDisambiguateDefendant_CatchReplaceCompletely )
    
    ]
    
    
}