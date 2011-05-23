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
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClauses
import rembrandt.gazetteers.Patterns
import rembrandt.rules.MeaningDetector
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * Rules for handling Wikipedia categories.
 */
class WikipediaCategoryRulesEN extends MeaningDetector {
   
    static List<Rule> rules
 
    static final Clause disposableBeginning01 = Clause.newRegex01Clause( 
	~/(?:[Ff]ormer|[Nn]ational|[Pp]rincipal)/, "[Ff]ormer|[Nn]ational|[Pp]rincipal")

    /**
     * Main constructor
     */	
	public WikipediaCategoryRulesEN() {
	    
	rules = []

	// Mexican cities
	rules.add (new Rule(id:"WikipediaCategoryRulesEN 1", clauses:[
	   CommonClauses.capitalizedAlphNum1Pc, 
	   NEGazetteer.meaningEN1c ] ))	
        
	// National Monuments in|of Portugal
	rules.add (new Rule(id:"WikipediaCategoryRulesEN 2", clauses:[
	    disposableBeginning01, 
	    NEGazetteer.meaningEN1c, 
	    ClausesEN.inof1nc, 
	    CommonClauses.capitalizedAlphNum1Pc] ))        
	        
	//  catch-all rule, 
	rules.add (new Rule(id:"WikipediaCategoryRulesEN 99", clauses:[
	    NEGazetteer.meaningEN1c] ))	

	}
}