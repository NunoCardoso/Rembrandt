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
import rembrandt.gazetteers.en.AbstractionGazetteerEN
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClauses
import rembrandt.rules.NamedEntityDetector 

/**
 * @author Nuno Cardoso
 * 
 * List of external rules for ABSTRACTION category.
 */
class AbstractionRulesEN extends NamedEntityDetector  {
   
    List<Rule> rules
    SemanticClassification sc 
  
    public AbstractionRulesEN() {
	    
	rules = []
		
	/******* ABSTRACCAO NOME ******/
	    
	/* 1 "[[known as]! the? "? {<EM>!} "? */
	rules.add(new Rule(id:"AbstractionRulesEN 1", 
	description:"[known as|...]! the? \"? {<EM>!} \"?", sc:SC.abstraction_name,
	policy:RulePolicy.Clause, clauses:[
	    AbstractionGazetteerEN.abstractionNamePrefix1nc, 	    
	    ClausesEN.the01nc,
	    CommonClauses.aspasOpen01nc, 
	    NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c,
	    CommonClauses.aspasClose01nc
	] ))				

	/******* ABSTRACCAO ESTADO ******/
	   
	sc = SemanticClassification.create(Classes.category.abstraction, Classes.type.state)
		
	/* 2 {<EM>!} 's? [disease|syndrome]!  */	  	    
	rules.add(new Rule(id:"AbstractionRulesEN 2", 
	description:"{<EM>!} 's? [disease|syndrome]!", sc:SC.abstraction_state,
	policy:RulePolicy.Clause, clauses:[ 
	      NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c, 
	      ClausesEN.apostrophe01nc,
	      AbstractionGazetteerEN.deseasesPrefix1nc
	] ))         
   }
}