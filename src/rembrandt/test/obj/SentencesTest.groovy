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

package rembrandt.test.obj

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*

import rembrandt.obj.*

/**
 * @author Nuno Cardoso
 * Tester for Sentence.
 */
class SentencesTest extends GroovyTestCase {

	static Logger log = Logger.getLogger("RembrandtTest")


	public SentencesTest() {
	}

	/**
	 * Create new sentences
	 *   rembrandt.obj.Sentence.Sentence(int, int)
	 *   rembrandt.obj.Sentence.Sentence(int)
	 *   rembrandt.obj.Sentence.Sentence(List, int, int)
	 *   rembrandt.obj.Sentence.Sentence(List, int)
	 *   rembrandt.obj.Sentence.Sentence(List)
	 *   rembrandt.obj.Sentence.toList()
	 *   rembrandt.obj.Sentence.getPointer()
	 */
	void testNewSentence() {
		Sentence s1 = new Sentence(1,2)
		assert s1.index == 1
		assert s1.pointer == 2
		assert s1.getPointer() == 2
		Sentence s2 = new Sentence(1)
		assert s2.index == 1
		assert s2.pointer == 0

		List<Term> terms = [
			new Term("A",0),
			new Term("B",1)
		]
		Sentence s3 = new Sentence(terms)
		assert s3.index == 0
		assert s3.pointer == 0
		assert terms == s3.toList()
	}

	/**
	 *  rembrandt.obj.Sentence.getVisibleTerms()
	 *  rembrandt.obj.Sentence.thereAreVisibleTermsAhead()
	 *  rembrandt.obj.Sentence.resetPointerToFirstVisibleTerm()
	 *  rembrandt.obj.Sentence.movePointerForVisibleTerms(int)
	 *  rembrandt.obj.Sentence.movePointerForVisibleTerms()
	 *  rembrandt.obj.Sentence.getUnseenVisibleTerms()
	 *  rembrandt.obj.Sentence.findPointerOfTermWithIndex(int)
	 */

	void testVisibleTerms() {

		// O <B>REMBRANDT>/B> é um <I>pintor</I>.
		// 6 visible terms, 10 terms total.
		List<Term> mixedterms = [
			new Term("O",0,false),
			new Term("<B>",-1, true),
			new Term("REMBRANDT",1,false),
			new Term("</B>",-1,true),
			new Term("é",2,false),
			new Term("um",3, false),
			new Term("<I>",-1,true),
			new Term("pintor",4,false),
			new Term("</I>",-1,true),
			new Term(".",5,false)
		]
		List<Term> visterms = [
			new Term("O",0,false),
			new Term("REMBRANDT",1,false),
			new Term("é",2,false),
			new Term("um",3, false),
			new Term("pintor",4,false),
			new Term(".",5,false)
		]

		Sentence mixed = new Sentence(mixedterms)
		Sentence visible = new Sentence(visterms)

		assert mixed.size() == 10
		assert mixed.getVisibleTerms().size() == 6
		assert mixed.getUnseenVisibleTerms().size() == 6
		assert visible.size() == 6
		assert visible.getVisibleTerms().size() == 6
		assert visible.getUnseenVisibleTerms().size() == 6

		mixed.pointer = 1 // over <B>
		assert mixed.getUnseenVisibleTerms().size() == 5, "Got ${mixed.getUnseenVisibleTerms().size()} instead."
		mixed.pointer = 2 // over REMBRANDT
		assert mixed.getUnseenVisibleTerms().size() == 5, "Got ${mixed.getUnseenVisibleTerms().size()} instead."
		assert mixed.thereAreVisibleTermsAhead()
		mixed.pointer = 8 // over '</I>'
		assert mixed.getUnseenVisibleTerms().size() == 1, "Got ${mixed.getUnseenVisibleTerms().size()} instead."
		mixed.movePointerForVisibleTerms()
		assert mixed.pointer == 9 // over '.'
		assert mixed.getUnseenVisibleTerms().size() == 1, "Got ${mixed.getUnseenVisibleTerms().size()} instead."
		assert mixed.thereAreVisibleTermsAhead()
		mixed.movePointerForVisibleTerms()
		assert mixed.pointer == 10 // over nothing
		assert mixed.getUnseenVisibleTerms().size() == 0, "Got ${mixed.getUnseenVisibleTerms().size()} instead."
		assert ! mixed.thereAreVisibleTermsAhead()
		mixed.movePointerForVisibleTerms() // doesn't move further
		assert mixed.pointer == 10 // over nothing

		mixed.resetPointerToFirstVisibleTerm()
		assert mixed.pointer == 0
		mixed.movePointerForVisibleTerms() // by default, jumps one unit
		assert mixed.pointer == 2 // jumps over the hiddden <B>, lands on REMBRANDT

		assert mixed.getUnseenVisibleTerms().size() == 5

		assert mixed.findPointerOfTermWithIndex(0) == 0
		assert mixed.findPointerOfTermWithIndex(1) == 2, "Got ${mixed.findPointerOfTermWithIndex(1)} instead."
		assert mixed.findPointerOfTermWithIndex(2) == 4, "Got ${mixed.findPointerOfTermWithIndex(2)} instead."
		assert mixed.findPointerOfTermWithIndex(3) == 5, "Got ${mixed.findPointerOfTermWithIndex(3)} instead."
		assert mixed.findPointerOfTermWithIndex(4) == 7, "Got ${mixed.findPointerOfTermWithIndex(4)} instead."
		assert mixed.findPointerOfTermWithIndex(99) == -1, "Got ${mixed.findPointerOfTermWithIndex(99)} instead."

	}

	public testMarkTokenization() {
		String s = "Eu tenho uma fra[se para divi]dir."
		Sentence sen = Sentence.simpleTokenize()
		String s2 = Sentence.addTokenizationMarks(sen)
		assert s2 == "[Eu][tenho][uma][fra\\[se][para][divi\\]dir.]"
		Sentence sen2 =  Sentence.getFromTokenizationMarks(sen)
		assert sen2[3].text == "fra[se"
	}

	/* rembrandt.obj.Sentence.clone()
	 rembrandt.obj.Sentence.toString()    
	 rembrandt.obj.Sentence.toStringLine()
	 rembrandt.obj.Sentence.simpleTokenize(String, int, int)
	 rembrandt.obj.Sentence.simpleTokenize(String, int)
	 rembrandt.obj.Sentence.simpleTokenize(String)
	 rembrandt.obj.Sentence.dump()
	 rembrandt.obj.Sentence.indexOf(Sentence)
	 }  */
}