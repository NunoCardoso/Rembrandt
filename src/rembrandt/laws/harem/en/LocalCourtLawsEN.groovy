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
 * Court Laws for LOCAL
 */
class LocalCourtLawsEN {
    
    static final laws = [
    
    // DESAMBIGUATION BETWEEN PRECISE LOCAL - MUST BE AHEAD OF 99.X RULES
    
    // 2.1 RUA disambiguates other LOCAL HUMAN, equal or bigger
    new Law(id:"P:LOC_HUM_RUA>=D:(E)LOC_HUM", description:"P:LOCAL_HUMANO_RUA >= D:LOCAL_HUMANO -> Try-Disamb-Catch-Merge-Update-Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.place_human_street]],
    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Type]], 
    defendantEvidence:[[SC.place_human]],
    decision:CD.TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries), 
    
    // 2.2  AGUACURSO mixed with other will be disambiguated
    // but "Rio de Janeiro" as AGUACURSO can't write over other Rio Grande or Rio de Janeiro!
    // that's why I'm using ExistsAtLeastOneOfThese instead of NeverAllOfThese, that matches a [SC.place_human_division!]
    new Law(id:"P:(A)LOC_FIS_AGUACURSO>=D:(E)LOC_FIS_AGUACURSO", 
    description:"P:(A)LOCAL_FISICO_AGUACURSO >= D:(E)LOCAL_FISICO_AGUACURSO -> Try-Disamb-Catch-Replace-Update-Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.place_physical_watercourse]],
    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Subtype]], 
    defendantEvidence:[[SC.place_physical_watercourse]],
    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries), 
    
    // 2.2.1  AGUACURSO will not write SC.place_human_division same size
    // "Rio de Janeiro" as AGUACURSO can't write over other Rio Grande or Rio de Janeiro!
    new Law(id:"P:(A)LOC_FIS_AGUACURSO==D:(A)LOC_HUM_DIVISAO", 
    description:"P:(A)LOCAL_FISICO_AGUACURSO == D:(A)LOCAL_HUMANO_DIVISAO -> P:Discard",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.place_physical_watercourse]],
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype]], 
    defendantEvidence:[[SC.place_human_division]],
    decision:CD.DiscardPlaintiff),     
    
    // 2.2.2  Catch all AGUACURSO  for defendant not having AGUACURSO
    new Law(id:"P:(A)LOC_FIS_AGUACURSO>=D:(!E)LOC_HUM_AGUACURSO", 
    description:"P:(A)LOCAL_FISICO_AGUACURSO >= D:(!E)LOC_HUM_AGUACURSO -> Try-Disamb-Catch-Replace-Update-Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.place_physical_watercourse]],
    defendantCriteria:[[CC.NeverExistsAtLeastOneOfThese, CC.AllOfThem, CC.Subtype]], 
    defendantEvidence:[[SC.place_physical_watercourse]],
    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries),     
    
    // 2.3 ILHA  mixed with other will be disambiguated
    new Law(id:"P:LOC_FIS_ILHA>=D:(!A)LOC_FIS_ILHA", 
    description:"P:LOCAL_FISICO_ILHA >= D:(!A)LOCAL_FISICO_ILHA -> Try-Disamb-Catch-Replace-Update-Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.place_physical_island]],
    defendantCriteria:[[CC.NeverAllOfThese, CC.AllOfThem, CC.Subtype]], 
    defendantEvidence:[[SC.place_physical_island]],
    decision:CD.TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries), 
    
    // GENERIC RULES BETWEEN LOCALS
    
    // 99.1 a NE 'LOCAL' exact or bigger wins over a NE 'LOCAL'
    new Law(id:"P:LOC>=P:LOC", description:"P:LOCAL >= D:LOCAL -> D:Try-Disambiguate-Catch-Merge-Update-Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.place]],
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.place]],
    decision:CD.TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries ), 
    
    // 99.2 a NE 'LOCAL' smaller loses to a NE 'LOCAL' bigger	 
    new Law(id:"P:LOC<P:LOC", description:"P:LOCAL < D:LOCAL -> P:Discard",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.IsContainedBy], 
    plaintiffEvidence:[[SC.place]],
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.place]],
    decision:CD.DiscardPlaintiff ),
    
    // DISAMBIGUATION FOR OTHER CATS	   
    
    // 3 def: [NUMERO|TEMPO|DATA], pla:LOCAL 
    // 3.1 NE 'LOCAL' replaces 'NUMERO','TEMPO','DATA', anytime!         
    // Example: rua <DATA>25 de Abril</DATA>	
    new Law(id:"P:LOC>=D:[NUM|TEM|VAL]", description:"P:LOCAL >= D:[NUMERO|TEMPO|DATA|EM] -> ReplaceCompletely",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains],
    plaintiffEvidence:[[SC.place]],
    // Read: In the Plaintiff NE, it should ExistsAtLeastOneOfThese (own categories) that
    // match AtLeastOneOfThem (evidence categories).
    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category]], 
    defendantEvidence:[[SC.number, SC.time, SC.value]],
    decision:CD.ReplaceClassificationOfDefendant_UpdateBoundaries),
    
    
    //  3.2 PESSOA INDIVIDUAL is eaten by RUA, same or bigger
    //  Example: rua <PESSOA>Avelino</PESSOA> -> rua <RUA>Avelino</RUA>
    // Example: <ORG> Escola Política</ORG> mas pode ser rua <RUA>Escola Polótica</RUA>
    
    new Law(id:"P:LOC_HUM_STR>=D:(E)[PER|ORG]", description:"P:LOCAL_HUMANO_RUA >= D:[NUMERO|TEMPO|DATA|EM] -> Try-Disamb-Catch-Replace-Update-Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.place_human_street]],
    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category]], 
    defendantEvidence:[[SC.person, SC.organization]],
    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries), 
    
    // 3.3 PESSOA POVO is eaten by LOCAL FISICO, same or bigger
    //  Example: <POVO>Tapaj√≥s</POVO> -> rio <AGUACURSO>Tapaj√≥s</AGUACURSO>
    new Law(id:"P:LOC_PHY>=D:PER_PEO", description:"P:LOCAL_FISICO >= D:PESSOA_POVO -> Try-Disamb-Catch-Replace-Update-Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Type], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.place_physical]],
    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Type]], 
    defendantEvidence:[[SC.person_people]],
    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries), 
    
    // 3.4 OBRA -> LOCAL
    // Example: rua <OBRA REPRODUZIDA>Central do Brasil</OBRA> -> desambigua para LOCAL, ou então MERGE
    new Law(id:"P:LOC_HUM>=D:MAS", description:"P:LOCAL_HUMANO >= D:OBRA -> Try-Disamb-Catch-Merge-Update-Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Type], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.place_human]],
    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.masterpiece]],
    decision:CD.TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries),
    
    // 3.5 ORGANIZACAO -> LOCAL 1
    // Example: site da <ORG>SONAL</ORG> -> passa para LOCAL SITIO, exact
    new Law(id:"P:LOC_VIR_SIT>=D:ORG", description:"LOCAL_VIRTUAL_SITE >= D:ORGANIZATION -> Try-Disamb-Catch-Repl-Update-Bound",
    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
    plaintiffEvidence:[[SC.place_virtual_site]],
    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
    defendantEvidence:[[SC.organization]],
    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries)
    
    ]
}
    
