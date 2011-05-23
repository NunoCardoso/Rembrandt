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
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.pt.EventGazetteerPT
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector
/**
 * @author Nuno Cardoso
 * List of external rules for ACONTECIMENTO category.
 */
class EventRulesPT extends NamedEntityDetector {
   
    static List<Rule> rules
 
 /**
  * Main constructor
  */
    public EventRulesPT() {
	    
	rules = []
	         
	 // 1 [cerimónia]! daeos? { <EM>! } 
	rules.add(new Rule(id:"ExternalEvidence ACONTECIMENTO ORGANIZADO 1", description:"[cerimónia]! daeos? {<EM>!}", 
	sc:SC.event_organized, policy:RulePolicy.Clause, clauses:[ 
	   EventGazetteerPT.eventPrefix1nc, 
	   ClausesPT.daeos01nc, 
	   NEGazetteer.NE_Anything_1c
	] ) )				
    }
}