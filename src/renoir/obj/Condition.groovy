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

package renoir.obj

import rembrandt.obj.Term

class Condition {
	
	Predicate predicate
	Operator operator
	def object // can be QueryGeoscope, 
	// or it can be a geographic-ish Subject (like 'islands of Portugal' or 'portuguese islands')
	
	List<Term> terms // useful to calculate if there is overlap between conditions created by several rules
	
	// check if this condition is contained by condfition c, using terms index
	boolean exactOrContainsCondition(Condition c) {
		if (!terms || !c.terms) throw new IllegalStateException("Can't compare conditions if they have no terms")
       	  return terms[0].index <= c.terms[0].index && 
       	    terms[(terms.size()-1)].index >= c.terms[(c.terms.size()-1)].index && 
		    this.terms.size() >= c.terms.size()
	} 
	
	boolean containsCondition(Condition c) {
		if (!terms || !c.terms) throw new IllegalStateException("Can't compare conditions if they have no terms")
       	  return terms[0].index <= c.terms[0].index && 
       	    terms[(terms.size()-1)].index >= c.terms[(c.terms.size()-1)].index && 
		    this.terms.size() > c.terms.size()
	} 
	
	boolean exactOrContainedByCondition(Condition c) {
		if (!terms || !c.terms) throw new IllegalStateException("Can't compare conditions if they have no terms")
       	  return terms[0].index >= c.terms[0].index && 
       	    terms[(terms.size()-1)].index <= c.terms[(c.terms.size()-1)].index && 
		    this.terms.size() <= c.terms.size()
	} 

	boolean containedByCondition(Condition c) {
		if (!terms || !c.terms) throw new IllegalStateException("Can't compare conditions if they have no terms")
       	  return terms[0].index >= c.terms[0].index && 
       	    terms[(terms.size()-1)].index <= c.terms[(c.terms.size()-1)].index && 
		    this.terms.size() < c.terms.size()
	} 

	public String toString() {
		return "Condition(terms:$terms predicate:$predicate op:$operator object:$object)"
	}
}	