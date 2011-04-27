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
import org.apache.log4j.Logger
import saskia.bin.Configuration
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.obj.ConflictPolicy
import rembrandt.obj.SemanticClassification
import rembrandt.obj.ListOfNE
import rembrandt.obj.ListOfNEIndex
/**
 * @author Nuno Cardoso
 * Tester for ListOfNEIndex
 */
class ListOfNEIndexTest extends GroovyTestCase {

	static Logger log = Logger.getLogger("RembrandtTest")
	ListOfNE NEs
	NamedEntity ne1, ne2, ne3, ne4, ne5

	public ListOfNEIndexTest() {

		NEs = new ListOfNE()

		ne1 = new NamedEntity(sentenceIndex:0, termIndex:1,
				terms:Sentence.simpleTokenize('Associação Cultural e Recreativa de Torres Vedras', 0, 1),
				classification:[
					new SemanticClassification('ORGANIZACAO','INSTITUICAO') ]
				)

		ne2 = new NamedEntity(sentenceIndex:0, termIndex:1,
				terms:Sentence.simpleTokenize('Associação Cultural', 0, 1),
				classification:[
					new SemanticClassification('ORGANIZACAO','INSTITUICAO') ]
				)

		ne3 = new NamedEntity(sentenceIndex:0, termIndex:6,
				terms:Sentence.simpleTokenize('Torres Vedras',0,6),
				classification:[
					new SemanticClassification('LOCAL','ADMINISTRATIVO') ]
				)

		ne4 = new NamedEntity(sentenceIndex:0, termIndex:4,
				terms:Sentence.simpleTokenize('Recreativa',0,4),
				classification:[
					new SemanticClassification('ABSTRACCAO','DISCIPLINA') ]
				)

		ne5 = new NamedEntity(sentenceIndex:0, termIndex:0,
				terms:Sentence.simpleTokenize('A Associação',0,0),
				classification:[
					new SemanticClassification('ORGANIZACAO','ADMINISTRACAO') ]
				)
	}

	void testSentenceIndex() {

		NEs = new ListOfNE([ne4, ne2, ne1, ne5, ne3])
		assert [0, 1, 2, 3, 4]== NEs.index.sentenceIndex[0]

		NEs.removeNEs([ne4, ne2])
		assert [0, 1, 2]== NEs.index.sentenceIndex[0]

		NEs.sortNEs()
		assert [0, 1, 2]== NEs.index.sentenceIndex[0]
	}

	void testTermIndex() {

		NEs = new ListOfNE([ne1, ne2, ne3, ne4, ne5])
		//NEs.sortNEs() - they are already sorted
		NEs.labelNEs()
		NEs.createTermIndex()

		// getIDsforQuery only uses term text.
		List<Integer> l1 = NEs.index.getNeIDsforQueryTerms(Sentence.simpleTokenize("Associação"))
		assert [0, 1, 4]== l1, "Got $l1 instead of [0,1,4]"
		List<Integer> l2 = NEs.index.getNeIDsforQueryTerms(Sentence.simpleTokenize("Associação Cultural"))
		assert [0, 1]== l2, "Got $l2 instead of [0,1]"
		List<Integer> l3 = NEs.index.getNeIDsforQueryTerms(
				Sentence.simpleTokenize("Associação Cultural")) - NEs.indexOf(ne2)
		assert [0]== l3, "Got $l3 instead of [1]"
		List<Integer> l4 = NEs.index.getNeIDsforQueryTerms(Sentence.simpleTokenize("Cultural Recreativa"))
		assert []== l4, "Got $l4 instead of []"
	}
}