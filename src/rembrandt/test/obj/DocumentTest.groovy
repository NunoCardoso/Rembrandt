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

import rembrandt.obj.*

import org.junit.*
import org.junit.runner.*

/**
 * @author Nuno Cardoso
 * Tester for rembrandt.obj.Document
 */
public class TestDocument extends GroovyTestCase {
		
	Document doc
	String docid = "x1"	
	String lang = "pt"  
	String title = "Rembrandt, retirado da Wikipédia"
	String body="""
Rembrandt Harmenszoon van Rijn nasceu em 15 de julho de 1606 (tradicionalmente) mas provavelmente em 1607 em Leiden, Países Baixos. 
Fontes conflitantes afirmam que sua família era formada por 7, 9, ou 10 filhos. 
Seu pai era moleiro, e sua mãe, filha de um padeiro. 
Quando criança ele teve aulas de latim e foi matriculado na Universidade de Leiden, embora desde cedo demonstrasse inclinação para a pintura. 
Pouco depois ele se tornou aprendiz do pintor histórico de Leiden, Jacob van Swanenburgh. 
Depois de um breve mas importante aprendizado com o famoso pintor Pieter Lastman em Amesterdão, Rembrandt abriu um estúdio em Leiden, dividindo-o com seu colega Jan Lievens. 
Em 1627, Rembrandt passou a aceitar alunos, entre eles Gerrit Dou.
	"""
	
	 public TestDocument() {
	   doc = new Document()
	   doc.docid= docid
	   doc.title= title
	   doc.body = body
	   doc.lang = lang
	   doc.preprocess()
	   assert doc.isTitleTokenized()
	   assert doc.isBodyTokenized()
	   assert doc.isTitleIndexed()
	   assert doc.isBodyIndexed()
	 }
	
	void testBasics() {
	    assert doc.title == title
	    assert doc.docid == docid
	    assert doc.body == body
	    assert doc.lang == lang
	}
	
	void testIndexes() {
	    assert doc.title_sentences.size() == 1
	    assert doc.body_sentences.size() == 7
	    // Rembrandt occurs 3 times, on sentence 0 term 0, etc. 
	    assert [[0, 0], [5, 16], [6, 3]] == doc.bodyIndex.getIndexesForTerm("Rembrandt")
	    assert null == doc.bodyIndex.getIndexesForTerm("nomatchhere")
	}
}

