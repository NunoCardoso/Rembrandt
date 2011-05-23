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
 *  The Criteria specifies the matching type of the needles (terms, patterns,
 *  list of terms, list of meanings, NEs, etc) to the haystack (sentence of terms, sentences 
 *  with NEs, etc).
 */
enum Criteria {
     
    /** term-to-term match with regular expression */
	RegexMatch, 
	
	/** term-to-term match with equals() */
	PlainMatch, 
	
	/** NE-to-NE match. It requires NECriteria for setting up matching criteria */
	NEMatch, 
	
	/** negation couterpart of RegexMatch */
	NotRegexMatch, 

	/** negation couterpart of PlainMatch */
	NotPlainMatch, 
	
	/** List.of.terms-to-List.of.terms match, with RegexMatch for each one.
	 * It returns true when all pieces are matched true. */
	MultipleTermMatch, 
	
	/** List.of.meanings-to-List.of.terms match, with MultipleRegexMatch for each one.
	 * It returns true when at least one of the meanings is matched. 
	 */
	MeaningMatch,
	
	/**
	 * List.of.list.of.terms-to-List.of.terms match, with MultipleRegexMatch for each one.
	 * It returns true when at least one of the meanings is matched.
	 */
	ConceptMatch, 
	
	BiggerConceptMatch,
	/**
	 * Matches an adjective for a place
	 */
	//PlaceAdjectiveMatch,  
	
	/**
	 * Matches a known place name
	 */
	//PlaceNameMatch,
	
	
//	NotPlaceAdjectiveMatch,  
	
//	NotPlaceNameMatch,
	
	/**
	 * Matches the beginning of a sentence
	 */
	SentenceBeginMatch,
	
	/**
	 * Matches the end of a sentence
	 */
	SentenceEndMatch

}