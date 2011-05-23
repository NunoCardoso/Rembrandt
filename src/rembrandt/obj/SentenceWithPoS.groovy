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
 * This class extends a Sentence object with part-of-speech tagged terms.
 */
class SentenceWithPoS extends Sentence {
  
   /**
    * Main constructor
    * @param index the sentence index
    */
   public SentenceWithPoS(int index) {super(index)}

   public static int FROM_LINGUATECA_PoS = 1
   public static int FROM_PALAVRAS_PoS = 2

   /**
    * Parser for lines with PoS tags 
    */
   static SentenceWithPoS tokenizePoS(line, int pos = FROM_LINGUATECA_PoS) {
	SentenceWithPoS sentence = new SentenceWithPoS(0)
	List tokens
		
	if (pos == FROM_LINGUATECA_PoS) {
	    tokens = line.split("\n") // List type avoids a conversion to String[]
	    tokens.eachWithIndex{token, i -> 
		if (!token.trim().startsWith("<")) {
		    sentence << new TermWithPoS(token.trim(), i, pos)
		}
	    }
	}
	else if (pos == FROM_PALAVRAS_PoS) {
	    tokens = line
	    int tokenCount = 0
	    tokens.each{token -> 
	    	def m = token =~ /^([\S=]+)([\s\t]+)(.*)$/
	    	if (m.matches()) {
	    	    List names = m.group(1).split(/=/)
	    	    names.each{n -> 
	    	    	def newToken = "$n"+m.group(2)+m.group(3) 
	    	    	sentence << new TermWithPoS(newToken.trim(), tokenCount++, pos)	
	    	    }
	    	} else {
	    	    sentence << new TermWithPoS(token.trim(), tokenCount++, pos)
	    	}
	    }
	}			
	return sentence
   }
}// class