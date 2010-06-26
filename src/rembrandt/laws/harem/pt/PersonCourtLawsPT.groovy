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
 * Court Laws for PESSOA
 */
class PersonCourtLawsPT {
   
    static final laws = [
    
    // case 99.1: <Isabel II> comes in <PESSOA|LOCAL>.
    // replace it with <rainha Isabel II> <PESSOA>. Hence, the ExistsAtLeastOneOfThese  
    new Law(id:"P:PER>=D:(E)PER", description:"P:PERSON >= D:(E)PERSON -> D:Try-Disamb-Catch-Replace-Update-Bounds",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
	    plaintiffEvidence:[[SC.person]],
	    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.person]],
	    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries )
    
    // case 99.2: NE 'PESSOA' smaller loses to a NE 'PESSOA' bigger	 
    ,new Law(id:"P:PER<D:PER", description:"P:PERSON >= D:PERSON -> P:Discard",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
	    plaintiffEvidence:[[SC.person]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.person]],
	    decision:CD.DiscardPlaintiff )
     
    ////// 
    
    // PERSON - ORG
    // case 2.1: <ORG|LOCAL>Câmara</ORG|LOCAL>, <PESSOA CARGO ou GRUPOCARGO>presidente da Câmara</PESSOA>
    ,new Law(id:"P:PER_[CAR|GCA]>=D:(E)ORG", description:"P:PERSON_[CARGO|GRUPOCARGO] >= D:ORG: Try-Dis-Replace-Update-Bounds",
	    plaintiffCriteria:[[CC.AllOfThese, CC.ExistsAtLeastOneOfThem, CC.Type], BC.ExactOrContains ], 
	    plaintiffEvidence:[[SC.person_position, SC.person_positiongroup]],
	    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.organization]],
	    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries)
    
    // PERSON - LOCAL
    // 3.1 bigger PESSOA CARGO eats LOCAL HUMANO (duque de <EM>Bragança</EM> -> <EM>duque de Bragança</EM>)
    //  (director do <CONSTR>Museu</CONSTR> -> <CARGO>director do Museu</CARGO>)
    //  (Aurora -> irmã <PESSOA>Aurora</PESSOA>, Aurora é uma catrafada de coisas)
    // happens with (professor <EM>Guimar√£es</EM> -> <EM>professor Guimar√£es</EM>)	
    // happens with (deputado <EM>Seabra</EM> -> <EM>Seabra</EM>)	 
    ,new Law(id:"P:PER>=D:(E)LOC_HUM", description:"P:PERSON >= D:(E)LOCA_HUMANO -> D:Try-Dis-Replace-Update-Bounds",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
	    plaintiffEvidence:[[SC.person]],
	    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Type]], 
	    defendantEvidence:[[SC.place_human]],
	    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries), 
    
    // PESSOA - ABSTRACCAO
    // 4.1 bigger PESSOA CARGO eats ABSTRACCAO DISCIPLINA 
    //(ministro da <EM>Cultura</EM> -> ministro da <EM>Cultura</EM>)	 
    new Law(id:"P:PER_POS>=D:ABS_DIS", description:"P:PERSON_POSITION >= D:ABSTRACCAO_DISCIPLINA -> D:Try-Dis-Replace-Update-Bounds",
	    plaintiffCriteria:[[CC.AllOfThese, CC.ExistsAtLeastOneOfThem, CC.Type], BC.ExactOrContains ], 
	    plaintiffEvidence:[[SC.person_position, SC.person_positiongroup]],
	    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.abstraction_discipline]],
	    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries), 
   
    // PESSOA - ACONTECIMENTO	
 
    //( <ACONT>Civil</EM> -> <Pesoa>vice-governador Civil</EM>)	 
    new Law(id:"P:PER>=D:EVE", description:"P:PERSON >= D:EVENT -> D:Try-Dis-Replace-Update-Bounds",
	    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
	    plaintiffEvidence:[[SC.person]],
	    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
	    defendantEvidence:[[SC.event]],
	    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries)
	 
    ]
    
}