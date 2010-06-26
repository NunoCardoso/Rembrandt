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

import saskia.gazetteers.DBpediaPropertyDefinitionsPT
import rembrandt.obj.Term

class Operator {
	
	public enum Locator {
	    In, 
		Around, // within and around
		Along,
	    Near, 
		Between,
	    North, 
	    South, 
	    East,
		West,
		Northeast,
		Northwest,
		Southeast,
		Southwest,
		Shores
	}
	
	def op // contains the enum
	def amount // contains some amount, ex :100
	def unit // associates anoumt on units, ex: 100 km
	
	PredicateOperator predicateOperator = null
	List<Term> terms = [] 

	void lookupOperator() {
		if (terms) {	
    	 	predicateOperator = DBpediaPropertyDefinitionsPT.getPredicateMeaning(terms[0].lemma) 
	    }
	}
	
	public String toString() {return "Operator:($op)"}
}