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
import rembrandt.obj.RulePolicy
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.pt.ThingGazetteerPT
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector

/**
 * @author Nuno Cardoso
 * List of external rules for COISA category.
 */
class ThingRulesPT extends NamedEntityDetector {
   
    static List<Rule> rules	
    
    /**
     * Main constructor
     */	
    public ThingRulesPT() {
	    
	rules = []
	
	// THING CLASS
	         
	// 1 { [constante]! de! <EM>! } 
	rules.add(new Rule(id:"ExternalEvidence COISA CLASSE 1", description:"{[constante]! de! <EM>!}", 
	sc:SC.thing_class, policy:RulePolicy.Rule, clauses:[ // collect ALL 
	       ThingGazetteerPT.classInclude1c, 
	       ClausesPT.de1c, 
	       NEGazetteer.NE_Anything_1c] ))				
			 
	// THING classmember
		
	// 2 [produtos]! {<EM>!} 
	rules.add(new Rule(id:"ExternalEvidence COISA MEMBROCLASSE 1", description:"[produtos]! {<EM>!}",
	 sc:SC.thing_memberclass, policy:RulePolicy.Clause, clauses:[ 
	       ThingGazetteerPT.classExclude1nc, 
	       NEGazetteer.NE_Anything_1c] ))	
		 
	// THING substance
			 
	 // 3  {[vitaminas?]! <EM>!}
	rules.add(new Rule(id:"ExternalEvidence COISA SUBSTANCIA 1", description:" {[vitaminas?]! <EM>!}",
	 sc:SC.thing_substance, policy:RulePolicy.Rule, clauses:[ // collect ALL 
	       ThingGazetteerPT.substanceInclude1c, 
	       NEGazetteer.NE_Anything_1c] ))				

	// 4 [substâncias!] {<EM>!}
	rules.add(new Rule(id:"ExternalEvidence COISA SUBSTANCIA 2", description:" [substâncias]! {<EM>!}", 
		 sc:SC.thing_substance, policy:RulePolicy.Clause,  clauses:[ 
	       ThingGazetteerPT.substanceExclude1nc, 
	       NEGazetteer.NE_Anything_1c] ))						   
    }
}