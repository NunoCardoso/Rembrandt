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
import rembrandt.obj.ClassificationCriteria as CC

class SemanticClassification {
	
    String c, t, s
    
    public SemanticClassification(String category=null, String type=null, String subtype=null) {
           c = category
           t = type
           s = subtype
    }
    
    public static SemanticClassification create(String category=null, String type=null, String subtype=null) {
	return new SemanticClassification(category, type, subtype)
    }
	
    /** 
     * return true when the compsrison results return EQUAL
     * @param hc the semantic classification to compare
     * @return true if equal, false otherwise
     */
    public boolean equals(SemanticClassification hc) {
	return (compareTo(hc) =~ /EQUAL/)
    }
    
    /** 
     * Clone this
     * @return a clones SemanticClassification
     */
    public SemanticClassification clone() {
	return new SemanticClassification(c,t,s)
    }
	
    /**
     * This function returns a string with the label:
     *  DIFFERENT - the classification of THIS NE has a non-null item that clashes
     *  BROADER - the classification of THIS NE is BROADER than the candidate classification. 
     *      Example: this.ne = PESSOA, that.ne = PESSOA/INDIVIDUAL
     *	NARROWER - the classification of THIS NE is NARROWER than the candidate classification   
     *      Example: this.ne = PESSOA/INDIVIDUAL, that.ne = PESSOA
     *  EQUAL - This classification is an exact copy, null included
     *  
     *  @param hc semantic classification to compare
     *  @return the string with the comparison result
     */
    public String compareTo(SemanticClassification hc) { 
	    // veredicts for each classification comparison:
       if (!this.c || !hc.c) throw new IllegalStateException("Can't compare if we have null categories!")

        if (this.c != hc.c) return "CATEGORY DIFFERENT"
	
	if (!this.t && hc.t)  return "TYPE BROADER"
	if (this.t && !hc.t) return "TYPE NARROWER"
	if (this.t != hc.t) return "TYPE DIFFERENT"
	// types are equal, null or not
	
	if (!this.s && hc.s)  return "SUBTYPE BROADER"
	if (this.s && !hc.s) return "SUBTYPE NARROWER"
	if (this.s != hc.s) return "SUBTYPE DIFFERENT"
	
	if (!this.s && !this.t) return "CATEGORY EQUAL"
	if (!this.s) return "TYPE EQUAL"
	return "SUBTYPE EQUAL"
    }
    
    static boolean resolveVeredictTo(String veredict, CC criteria) {
	if ((!veredict || !criteria)  || !([CC.Category, CC.Type, CC.Subtype].contains(criteria))) 
	    throw new IllegalStateException("Can't resolve veredict with null veredict or illegal criteria $criteria")
	
	switch(criteria) {
	case CC.Category: 
	    // only with CATEGORY DIFFERENT the veredict compares false 
	    // with CATEGORY EQUAL, TYPE*  or SUBTYPE* implies that the categories matched.
	    if (veredict.equals("CATEGORY DIFFERENT")) return false
	    return true
	break
	
	case CC.Type: 
	 // if has a CATEGORY, return false. It means that the SemanticClassifications that gave this veredict 
	 // are not detailed enough in Type to conclude anything
	    if (veredict =~ /CATEGORY/) return false

	    // if it has a TYPE, return true only if it has a TYPE EQUAL. 
	 // TYPE NARROWER or BROADER implies that one of the SemanticClassifications got stuck only in Category level.
	 // TYPE DIFFERENT is clearly a return false
	    if (veredict.equals("TYPE EQUAL")) return true
	    else if  (veredict ==~ /^TYPE .*/) return false
	    
	 // if it has a SUBTYPE, return always true - it implies that TYPE was matched EQUAL.   
	    if (veredict =~ /SUBTYPE/) return true
	break
	
	case CC.Subtype:
	// Only case true here is that if veredict is SUBTYPE EQUAL.  
	    if (veredict.equals("SUBTYPE EQUAL")) return true
	    return false
	break    
	
	default: 
	    throw new IllegalStateException("Can't use criteria $criteria")
	break
	}
    }

    public String toString() {
        // this assigns a name for NE index, so look out if you're going to change it.
	return "${c}"+(t ? "-"+t: "")+(s ? "-"+s : "")
    }
}

