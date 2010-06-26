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
 * Court Laws for OBRA. Includes rules where plaintiff has category OBRA.
 */
class MasterpieceCourtLawsEN {
    
    static final laws = [
    
    // 99.1 bigger OBRA replaces smaller OBRA
    new Law(id:"P:MAS>=D:MAS", description:"P:MASTERPIECE >= D:MASTERPIECE -> D:Try-Disamb-Catch-Merge-Update-Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.masterpiece]],   
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.masterpiece]],
    decision:CD.TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries )
    
    // 99.2 smaller OBRA does not replace bigger OBRA
    ,new Law(id:"P:MAS<D:MAS", description:"P:MASTERPIECE < D:MASTERPIECE -> P:Discard",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
    plaintiffEvidence:[[SC.masterpiece]],
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.masterpiece]],
    decision:CD.DiscardPlaintiff )
    
    // 2 TEMPO|NUMERO -> PLANO
    // 2.1 artigo <DATA>2/96</DATA> ->  artigo <PLANO>2/96</PLANO>                       
    ,new Law(id:"P:MAS_PLA>=D:[TIM|NUM]", description:"P:MASTERPIECE_PLAN >= D:[TIME|NUMBER] -> D:Repl_Update_Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Type], BC.ExactOrContains],
    plaintiffEvidence:[[SC.masterpiece_plan]],
    // Read: AllOf the plaintiffNE categories must match AtLeastOneOf the [TEMPO or NUMBER] categories	    
    defendantCriteria:[[CC.AllOfThese, CC.ExistsAtLeastOneOfThem, CC.Category]], 
    defendantEvidence:[[SC.time, SC.number]],
    decision:CD.ReplaceCompletelyDefendant ),
    
    // Anything is try-disambiguate-catch-replace when the plaintiff is same size or bigger OBRA REPRODUZIDA or ARTE	    
    //	2.2 Example: Sex -> Livro de Madonna, etc. 'Rio Selvagem',...
    // (D. Pedro V e estátua de D. Pedro V)
    new Law(id:"P:MAS_[REP|WOR]>=D:*", description:"P:MASTERPIECE_[REPRODUCED|WORKOFART] >= D:* -> Try-Disamb-Catch-Repl-Update-Bounds",
    plaintiffCriteria:[[CC.AllOfThese, CC.ExistsAtLeastOneOfThem, CC.Type], BC.ExactOrContains],
    plaintiffEvidence:[[SC.masterpiece_reproduced, SC.masterpiece_workofart]],
    defendantCriteria:[[CC.AnyKnownOrUnknownCategory, null, null]], 
    defendantEvidence:[[]],
    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries )
    
    ]
}