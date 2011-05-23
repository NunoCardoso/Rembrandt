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

/**
 * @author Nuno Cardoso
 *
 */
class RenoirQueryTerm extends Term {
    
    Float weight = null //1.0f
    String field = null
    String phraseBIO = "O" // it is inside or oustide a sentence like "John Doe" Use B I O names. 
	// Default: outside a phrase.
   // String text -> in the Term
    
    public RenoirQueryTerm(String text) {
		super(text)
    }
	
    public RenoirQueryTerm(Term term) {
		super(term.text,term.index,term.hidden)
    }
	
	
	boolean equals(RenoirQueryTerm term) {
		return equalsTextAndFieldAndPhraseBIO(term) 
	}
	
	boolean equalsTextAndFieldAndPhraseBIO(RenoirQueryTerm term) {
		return this.text == term.text && this.field == term.field
	}

    public String toString() {
        String s = field+":"
        if (text?.find(/\s+/)) s += "\"${text}\"" else s += text
        if (weight) s += "^"+weight
        return s
    }
    
}
