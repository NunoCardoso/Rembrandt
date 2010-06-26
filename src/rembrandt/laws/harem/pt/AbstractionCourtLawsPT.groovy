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
 * Court Laws for ABSTRACCAO. Includes rules where plaintiff has category ABSTRACCAO
 */
class AbstractionCourtLawsPT {
    
    static final laws = [
    
     // 99.1 same size or Bigger ABSTRACCAO: Try Disambiguate, Catch Merge, update boundaries
    new Law(id:"P:ABS>=D:ABS", description:"P:ABSTRACCAO >= D:ABSTRACCAO -> D:Try-Disamb-Catch-Merge-Update-bound",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
	    plaintiffEvidence:[[SC.abstraction]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.abstraction]],
	    decision:CD.TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries )
                     
     // 99.2. smaller ABSTRACCAO does not replace bigger ABSTRACCAO
    ,new Law(id:"P:ABS<D:ABS", description:"P:ABSTRACCAO < D:ABSTRACCAO -> P:Discard",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
	    plaintiffEvidence:[[SC.abstraction]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.abstraction]],
	    decision:CD.DiscardPlaintiff )
    
    
    // def: PESSOA -> ABSTRACCAO
    // 2.1 Example: dei o nome de <PESSOA>Nuno</PESSOA>. -> <ABSTRACCAO NOME>Nuno</ABSTRACCAO>
    ,new Law(id:"P:ABS_NOM>=D:*", description:"P:ABSTRACCAO_NOME >= D:* ->  D:Try-Disamb-Catch-Repl-Update-bound",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Type], BC.ExactOrContains], 
	    plaintiffEvidence:[[SC.abstraction_name]],
	    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category]], 
	    defendantEvidence:[[SC.number, SC.time, SC.value, SC.place, SC.organization, SC.event, SC.person, SC.thing, SC.masterpiece]],
	    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries ) 
    ] 
}