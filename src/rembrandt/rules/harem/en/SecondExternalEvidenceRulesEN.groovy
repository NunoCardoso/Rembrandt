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
 
package rembrandt.rules.harem.en

import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.RulePolicy
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.en.MasterpieceGazetteerEN
import rembrandt.gazetteers.en.TimeGazetteerEN
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.gazetteers.CommonClauses
import rembrandt.rules.NamedEntityDetector
/**
 * @author Nuno Cardoso
 * Second layer of external evidences.
 */
class SecondExternalEvidenceRulesEN extends NamedEntityDetector {
   
   List<Rule> rules
   SemanticClassification sc

    /**
     * Main constructor
     */    
   public SecondExternalEvidenceRulesEN() {
	    
	rules = []
		
	
	  
		
		// Jogos Olímpicos de 2004     
		// Não posso usar NE_ACONTECIMENTO_ORGANIZADO de NE_VALORQUANT|TEMPODATA, 
		// pois por enquanto o detectExternalEvidence ancora na EM ACONTECIMENTO, e as comparações 
		// com outras EM estouram. Pora agora, há que usar regex para o ano.
		// Gerar ALT.

	}
}