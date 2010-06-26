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

package rembrandt.gazetteers

import java.util.regex.Pattern;

/**
 * @author Nuno Cardoso
 * Basic patterns and clauses already compiled and availabe to reuse.
 */
class Patterns {	
    
    // NOTE: don't use \\p{blabla} stuff. 
    
    static final String latinUpperCharString = "A-ZÁÀÃÂÄÉÈÊÍÖÓÒÔÕØÚÜÇ"	
    static final Pattern LatinUpperChar = Pattern.compile(latinUpperCharString)
    
    static final String latinLowerCharString = "a-záàâãäéèêíöóòôõøúüç"
    static final Pattern LatinLowerChar = Pattern.compile(latinLowerCharString)
    
    static final String capitalizedAlphaNumWordString = "[${latinUpperCharString}0-9][${latinUpperCharString}0-9${latinLowerCharString}.-]*"
    static final Pattern CapitalizedAlphaNumWord= Pattern.compile(capitalizedAlphaNumWordString)
    
    static final String capitalizedAlphaWordString = "[${latinUpperCharString}][${latinUpperCharString}0-9${latinLowerCharString}.-]*"
    static final Pattern CapitalizedAlphaWord = Pattern.compile(capitalizedAlphaWordString)
    
    static final String wordString = "[${latinUpperCharString}${latinLowerCharString}0-9.-]*"
    static final Pattern Word = Pattern.compile(wordString)
    
    static final String notCapitalizedAlphaNumWordString = "[${latinLowerCharString}0-9][${latinUpperCharString}0-9${latinLowerCharString}.-]*"
    static final Pattern NotCapitalizedAlphaNumWord = Pattern.compile(notCapitalizedAlphaNumWordString)
    
    static final String normalAlphaNumWordString = "[${latinUpperCharString}0-9${latinLowerCharString}.-]+"
    static final Pattern NormalAlphaNumWord = Pattern.compile(normalAlphaNumWordString)
	
    static final Pattern openQuotationMark =  ~/[«‘“`'"]+/
    static final Pattern closeQuotationMark = ~/[»’”´'"]+/
    		
    /* Gluer patterns */
    static final Map<String,Pattern> gluerPattern = ['pt': ~/(?:[Dd][aeo]s?|e|\/|-)/, 'en': ~/(?:and|of|'s|-)/]

 
}
