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

package rembrandt.obj

import com.thoughtworks.xstream.annotations.* 

/**
 * @author Nuno Cardoso
 * This is a simple class to encapsulate a term. It has a String text and an int index (the position in the sentence),
 * a boolean hidden (used for instance for 'HTML tags as terms', that are not supposed to be used in rule matching).
 */
@XStreamAlias('t')
class Term  {
	
    @XStreamAlias('i')
    int index
    String text
    boolean hidden = false

    /**
     * @return the text field.
     */
    public String toString() {
	return text
    }
    
    /** Main constructor */
    public Term(String text, int index = 0, boolean hidden = false) {
	this.text = text
	this.index = index
	this.hidden = hidden
    }

    /**
     * This equals() method compares only the term text, not the index.
     * @param term Term to be compared.
     * @return boolean if terms match, false otherwise.
     */
    public boolean equals(Term term) {
	return this.text.equals(term.text)
    }
    
    /**
     * This equalsWithIndex() method compares the term text and the index.
     * @param term Term to be compared.
     * @return boolean if terms match, false otherwise.
     */
    public boolean equalsWithIndex(Term term) {
        return this.text.equals(term.text) && this.index == term.index
    }
    /** 
     * Converts a list of String itens to a list of Terms
     * @param list the String list
     * @return the Term list
     */
    public static List<Term> convertToTerms(List<String> list) {
	List<Term> res = []
	list.eachWithIndex{it, i -> res += new Term(it, i) }
	return res
    }
}