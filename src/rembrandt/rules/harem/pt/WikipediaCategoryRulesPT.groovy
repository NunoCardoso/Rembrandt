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
 
package rembrandt.rules.harem.pt

import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.pt.LocalGazetteerPT
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.Patterns
import rembrandt.gazetteers.CommonClauses
import java.util.regex.Pattern
import rembrandt.rules.MeaningDetector
/**
 * @author Nuno Cardoso
 * Rules for handling Wikipedia categories.
 */
class WikipediaCategoryRulesPT extends MeaningDetector {
   
    static List<Rule> rules
    static final Clause disposableBeginning01 = Clause.newRegex01Clause(
	    ~/(?:[Aa]ntigas?|[Pp]rincipais)/, "[Aa]ntigas?|[Pp]rincipais")
    static final Clause disposableMiddle01 = Clause.newRegex01Clause(
	    ~/(?:[Nn]acionais|[Cc]omplementar(es)?|[Pp]rincipais)/, "[Nn]acionais|[Cc]omplementar(es)?|[Pp]rincipais")

    /**
     * Main constructor
     */	
    public WikipediaCategoryRulesPT() {
	    
	rules = []

    /******* LOCAL ******/
	    
	    // Capitais da Ásia, Cidades da Tailândia, Concelhos da Grande Lisboa,
	    // Monumentos nacionais em Portugal
	rules.add (new Rule(id:"WikipediaCategoryRulesPT 1", clauses:[
	        NEGazetteer.meaningPT1c,  
	        disposableMiddle01, 
	        ClausesPT.dnaeosem1c, 
	        CommonClauses.capitalizedAlphNum1Pc] ))	
        
	     // Cidades europeias, antigas Províncias portuguesas    
        rules.add (new Rule(id:"WikipediaCategoryRulesPT 2", clauses:[
		   disposableBeginning01, 
		   NEGazetteer.meaningPT1c] ))			   	        
		   	 
		 // regra tipo catch-all, se no início ela encontra tudo.  
		 // Continentes
	rules.add (new Rule(id:"WikipediaCategoryRulesPT 99", clauses:[
		   NEGazetteer.meaningPT1c] ))	
	}
}