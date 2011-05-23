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
import rembrandt.gazetteers.en.OrganizationGazetteerEN
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector

/**
 * @author Nuno Cardoso
 * Rules for capturing ORGANIZATION categories 
 */
class OrganizationRulesEN extends NamedEntityDetector  {
   
    List<Rule> rules	
    SemanticClassification sc

   /**
     * Main constructor
     */	

    public OrganizationRulesEN() {
	    
	rules = []	
		
	/******* ORGANIZACAO EMPRESA *******/
		 
	// empresa <EM>
	rules.add(new Rule(id:"External Evidence COMPANY 1", description:"company! {<EM>!}",
	sc:SC.organization_company, policy:RulePolicy.Clause, clauses:[ 
	     OrganizationGazetteerEN.company1nc, 
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] ))				
	}
}