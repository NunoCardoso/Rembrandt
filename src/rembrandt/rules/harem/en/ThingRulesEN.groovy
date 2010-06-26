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
import rembrandt.gazetteers.en.ThingGazetteerEN  
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector

/**thing
 * @author Nuno Cardoso
 * List of external rules for COISA category.
 */
class ThingRulesEN extends NamedEntityDetector {
   
    List<Rule> rules	
    SemanticClassification sc

    /**
     * Main constructor
     */	
    public ThingRulesEN() {
	    
	rules = []
		
	// Included
				 		
	// 1 {[constant|...]! of! <EM>!} 
	rules.add(new Rule(id:"ThingRulesEN COISA CLASSE 1",
	description:"{[constant|...]! of! <EM>!}", sc:SC.thing_class, policy:RulePolicy.Rule, // collect ALL 
	clauses:[ ThingGazetteerEN.class1c, ClausesEN.of1c, NEGazetteer.NE_Anything_1c] ))				

	// 2 {<EM>! [constant|...]!} 
	rules.add(new Rule(id:"ThingRulesEN COISA CLASSE 2",
	description:"{<EM>! [constant|...]!}", sc:SC.thing_class, policy:RulePolicy.Rule, // collect ALL 
	clauses:[NEGazetteer.NE_Anything_1c, ThingGazetteerEN.class1c ] ))				

	// Excluded
				 		
	// 3 [product|...]! of! {<EM>!} 
	rules.add(new Rule(id:"ThingRulesEN COISA CLASSE 3",
	description:" [product|...]! of! {<EM>!}", sc:SC.thing_class, policy:RulePolicy.Rule, // collect ALL 
	clauses:[ ThingGazetteerEN.class1nc, ClausesEN.of1nc, NEGazetteer.NE_Anything_1c] ))				

	// 4 {<EM>!} [product|...]!
	rules.add(new Rule(id:"ThingRulesEN COISA CLASSE 4",
	description:"{<EM>!} [product|...]!", sc:SC.thing_class, policy:RulePolicy.Rule, // collect ALL 
	clauses:[NEGazetteer.NE_Anything_1c, ThingGazetteerEN.class1nc ] ))				
	
	// 1 {[vitamin|...]! of? <EM>!} 
	rules.add(new Rule(id:"ThingRulesEN COISA SUBSTANCIA 1",
	description:"{[vitamin|...]! of! <EM>!}", sc:SC.thing_substance, policy:RulePolicy.Rule, // collect ALL 
	clauses:[ ThingGazetteerEN.substance1c, ClausesEN.of01c, NEGazetteer.NE_Anything_1c] ))				

	// 2 {<EM>! [vitamin|...]!} 
	rules.add(new Rule(id:"ThingRulesEN COISA SUBSTANCIA 2",
	description:"{<EM>! [vitamin|...]!}", sc:SC.thing_substance, policy:RulePolicy.Rule, // collect ALL 
	clauses:[NEGazetteer.NE_Anything_1c, ThingGazetteerEN.substance1c ] ))				

	// Excluded
				 		
	// 3 [substance|...]! of? {<EM>!} 
	rules.add(new Rule(id:"ThingRulesEN COISA SUBSTANCIA 3",
	description:" [substance|...]! of! {<EM>!}", sc:SC.thing_substance, policy:RulePolicy.Rule, // collect ALL 
	clauses:[ ThingGazetteerEN.substance1nc, ClausesEN.of01nc, NEGazetteer.NE_Anything_1c] ))				

	// 4 {<EM>!} [substance|...]!
	rules.add(new Rule(id:"ThingRulesEN COISA SUBSTANCIA 4",
	description:"{<EM>!} [substance|...]!", sc:SC.thing_substance, policy:RulePolicy.Rule, // collect ALL 
	clauses:[NEGazetteer.NE_Anything_1c, ThingGazetteerEN.substance1nc ] ))				
			
    }
}