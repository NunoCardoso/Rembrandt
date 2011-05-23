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

import java.util.regex.Pattern
/**
 * @author Nuno Cardoso
 * Clauses are small pattern units to be matched to a list of terms and/or NEs.
 * This class stores patterns, roles, policy / criteria flags and other properties to be used 
 * when a rule is successfully matched.
 */
class Clause {
    
    /** The name of the clause */
    String name
    
    /** The cardinality of its application, that is, the lifespan in number of terms 
     * of this clause to be used in successful and unsuccessful matches
     */
    Cardinality cardinality
      
    /** The criteria match style, that is, what is the match comparator */
    Criteria criteria
      
    /** Criteria for NE classification */
    def NECriteria

    /** when rule policy equals RulePolicy.Clause, decide if 
    matched terms are to be collected or not for the resulting action */     
    boolean collectable
      
    /** generic pattern of the clause, to be used by matching comparators  */
    def pattern
    
    /** for options in clause matching, for instance case-insensitive value. */
    Map options 
       
    /** Defines which property from Term object will be used on the pattern match.
	  * Default is its text. Can be switched to stem or type */
    TermProperty termProperty
	
    /** 
     * Role. Good for pointing the role of each clause on the rule.
     * In EntityRelationRules, points the role of each NE in entity detection
     * In WikipediaCategoryRules, points the locality roles.
     */
    EntityRelation role
      
    /**
     * toString.
     */
    String toString() {
	return  ( name ? name : ( pattern ? pattern : criteria ) )
    }
 
    /** 
     * equals method.
     * @param c clause to compare
     * @return true if they are equal
     */
    boolean equals(Clause c) {
	return c.name == this.name && c.cardinality == this.cardinality && 
	c.criteria == this.criteria && c.pattern && this.pattern
    }
   /* Rules for naming clauses: 
    *	Cardinality: 01 (ZeroOrOne), 1(One), 0P (ZeroOrMore), 1P (OneOrMore)
    */
	
	/** 
	 * Quick constructor for Regex pattern, OneOrMore cardinality
	 * @param pattern the regex pattern.
	 * @param name the clause name.
	 * @param true for a collectable clause, false otherwise
	 * @param index list of terms that the clause will match
	 * @return a new clause with the given criteria.
	 */
	public static newRegex1PClause(Pattern pattern, String name = null, boolean collectable = true) {
	    return new Clause(name:(name ? name : pattern), pattern:pattern,
		cardinality:Cardinality.OneOrMore, criteria:Criteria.RegexMatch, 
		collectable:collectable)
	}
	
	/** 
	 * Quick constructor for Regex pattern, ZeroOrMore cardinality
	 * @param pattern the regex pattern.
	 * @param name the clause name.
	 * @param true for a collectable clause, false otherwise
	 * @param index list of terms that the clause will match
	 * @return a new clause with the given criteria.
	 */
	public static newRegex0PClause(Pattern pattern, String name = null, boolean collectable = true) {
		return new Clause(name:(name ? name : pattern), pattern:pattern,
		cardinality:Cardinality.ZeroOrMore, criteria:Criteria.RegexMatch, 
		collectable:collectable)
	}
	 
	/** 
	 * Quick constructor for Regex pattern, One cardinality
	 * @param pattern the regex pattern.
	 * @param name the clause name.
	 * @param true for a collectable clause, false otherwise
	 * @param index list of terms that the clause will match
	 * @return a new clause with the given criteria.
	 */
	public static newRegex1Clause(Pattern pattern, String name = null, boolean collectable = true) {
	    return new Clause(name:(name ? name : pattern), pattern:pattern,
		cardinality:Cardinality.One, criteria:Criteria.RegexMatch, 
		collectable:collectable)
	}
	
	/** 
	 * Quick constructor for Regex pattern, ZeroOrOne cardinality
	 * @param pattern the regex pattern.
	 * @param name the clause name.
	 * @param true for a collectable clause, false otherwise
	 * @param index list of terms that the clause will match
	 * @return a new clause with the given criteria.
	 */
	 public static newRegex01Clause(Pattern pattern, String name = null, boolean collectable = true) {
		return new Clause(name:(name ? name : pattern), pattern:pattern,
		cardinality:Cardinality.ZeroOrOne, criteria:Criteria.RegexMatch, 
		collectable:collectable)
	}

	/** 
	 * Quick constructor for Plain pattern, One cardinality
	 * This constructor will build the index automatically
	 * @param pattern the regex pattern.
	 * @param name the clause name.
	 * @param true for a collectable clause, false otherwise
	 * @return a new clause with the given criteria.
	 */
	public static newPlain1Clause(String pattern, String name = null, collectable = true) {
	     return new Clause(name:(name ? name : pattern), pattern:pattern,
			 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
			 collectable:collectable)
	}

	/** 
	 * Quick constructor for Plain pattern, ZeroOrOne cardinality
	 * @param pattern the regex pattern.
	 * @param name the clause name.
	 * @param true for a collectable clause, false otherwise
	 * @return a new clause with the given criteria.
	 */ 
	public static newPlain01Clause(String pattern, String name = null, collectable = true) {
		 return new Clause(name:(name ? name : pattern), pattern:pattern,
			 cardinality:Cardinality.ZeroOrOne, criteria:Criteria.PlainMatch, 
			 collectable:collectable)
	}
	 
	/** 
	 * Quick constructor for Concept pattern, ZeroOrOne cardinality
	 * @param pattern the regex pattern.
	 * @param name the clause name.
	 * @param true for a collectable clause, false otherwise
	 * @return a new clause with the given criteria.
	 */
	public static newConcept01Clause(List pattern, String name = null, collectable = true) {
		    return new Clause(name:(name == null ? pattern : name), pattern:pattern,
			 cardinality:Cardinality.ZeroOrOne, criteria:Criteria.ConceptMatch, 
			 collectable:collectable)
	}
	
	/** 
	 * Quick constructor for Concept pattern, ZeroOrMore cardinality
	 * @param pattern the regex pattern.
	 * @param name the clause name.
	 * @param true for a collectable clause, false otherwise
	 * @return a new clause with the given criteria.
	 */
	public static newConcept0PClause(List pattern, String name = null, collectable = true) {
		    return new Clause(name:(name == null ? pattern : name), pattern:pattern,
			 cardinality:Cardinality.ZeroOrMore, criteria:Criteria.ConceptMatch, 
			 collectable:collectable)
	}	
	/** 
	 * Quick constructor for Concept pattern, One cardinality
	 * @param pattern the regex pattern.
	 * @param name the clause name.
	 * @param true for a collectable clause, false otherwise
	 * @return a new clause with the given criteria.
	 */
	public static newConcept1Clause(List pattern, String name = null, collectable = true) {
		    return new Clause(name:(name == null ? pattern : name), pattern:pattern,
			 cardinality:Cardinality.One, criteria:Criteria.ConceptMatch, 
			 collectable:collectable)
	}	
	
	/** 
	 * Quick constructor for Concept pattern, OneOrMore cardinality
	 * @param pattern the regex pattern.
	 * @param name the clause name.
	 * @param true for a collectable clause, false otherwise
	 * @return a new clause with the given criteria.
	 */
	public static newConcept1PClause(List pattern, String name = null, collectable = true) {
	    return new Clause(name:(name == null ? pattern : name), pattern:pattern,
		cardinality:Cardinality.OneOrMore, criteria:Criteria.ConceptMatch, 
		collectable:collectable)
   }	
}