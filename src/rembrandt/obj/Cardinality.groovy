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

/**
 * @author Nuno Cardoso
 * Criteria for repeating rule clauses on terms.
 */
enum Cardinality {
    
    /**
     * ZeroOrOne, also known as 01.
     * If clause matches term, collects the term and proceed to next clause (if exists).
     * If clause do not matches terms, proceed to next clause (if exists).
     * This is an OPTIONAL clause, that is, it does not forces the rule to fail if 
     * there are remaining ZeroOrOne clauses that were not tested.
     */
    ZeroOrOne, 
    
    /**
     * ZeroOrOne, also known as 0P.
     * If clause matches term, collects the term and does NOT proceed to next clause.
     * If clause do not matches terms, proceed to next clause (if exists).
     * This is an OPTIONAL clause, that is, it does not forces the rule to fail if 
     * there are remaining ZeroOrMore clauses that were not tested.
     */
    ZeroOrMore, 
    
    /**
     * One, also known as 1.
     * If clause matches term, collects the term and proceed to next clause.
     * If clause do not matches terms, the rule fails.
     * This is a MANDATORY clause, that is, it forces the rule to fail if 
     * there are remaining One clauses that were not tested.
     */
    One, 
    
    /**
     * OneOrMore, also known as 1P.
     * If clause matches term, collects the term and does NOT proceed to next clause.
     * If clause do not matches terms, check if there was a previous match with
     * this same clause. If true, proceed to next clause. If not, the rule fails.
     * This is a MANDATORY clause, that is, it forces the rule to fail if 
     * there are remaining One clauses that were not tested.
     */
    OneOrMore, 

     /**
      * None, this is for clauses that have only the purpose of 
      * adding additional conditions like beginSentence or endSentence, but 
      * will not collect terms
      */  
    None
}