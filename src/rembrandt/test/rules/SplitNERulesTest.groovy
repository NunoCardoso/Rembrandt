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
 
package rembrandt.test.rules

import rembrandt.bin.RembrandtCore
import saskia.bin.*
import rembrandt.obj.*
import rembrandt.rules.*
import rembrandt.rules.harem.en.*
import rembrandt.rules.harem.pt.*
import rembrandt.gazetteers.CommonClassifications as SC
import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 * Generic tester for rules
 */
public class SplitNERulesTest extends GroovyTestCase {
	SplitNEDetector split_en, split_pt
	AskSaskia saskia_en, saskia_pt
	InternalEvidenceRulesEN ierules_en
	InternalEvidenceRulesPT ierules_pt
		
	 Configuration conf = Configuration.newInstance() 
			
	public SplitNERulesTest() {
		saskia_en = AskSaskia.newInstance("en", conf)
		saskia_pt = AskSaskia.newInstance("pt", conf)
		ierules_en = new InternalEvidenceRulesEN()
		ierules_pt = new InternalEvidenceRulesPT()
		split_en = new SplitNERulesEN(ierules_en, saskia_en)
		split_pt = new SplitNERulesPT(ierules_pt, saskia_pt)
	}
	
	void testEN() {
		
	 	ListOfNE list_ne = new ListOfNE()
	
	 	NamedEntity ne1 = new NamedEntity(
		terms:[new Term("California",2),new Term("and",3),new Term("Australia",4)],
		classification:[SC.unknown],
		sentenceIndex:0, termIndex:2
		)
	 	NamedEntity ne2 = new NamedEntity(
		terms:[new Term("California",2)],
		classification:[SC.place_human_division],
		sentenceIndex:0, termIndex:2
		)	
	 	NamedEntity ne3 = new NamedEntity(
		terms:[new Term("Australia",4)],
		classification:[SC.place_human_country],
		sentenceIndex:0, termIndex:4
		)
		
	 	list_ne << ne1
		
		List cloneNE = list_ne.clone()
		cloneNE.each{ne -> split_en.processNE(ne, list_ne)}
		
	
		assert list_ne == new ListOfNE([ne2, ne3])
	}
	
	// Austrália e Califórnia
	void testPT1() {
		
	 	ListOfNE list_ne = new ListOfNE()
	
	 	NamedEntity ne1 = new NamedEntity(
		terms:[new Term("Califórnia",2),new Term("e",3),new Term("Austrália",4)],
		classification:[SC.unknown],
		sentenceIndex:0,termIndex:2
		)
	 	NamedEntity ne2 = new NamedEntity(
		terms:[new Term("Califórnia",2)],
		classification:[SC.place_human_division],
		sentenceIndex:0,termIndex:2
		)	
	 	NamedEntity ne3 = new NamedEntity(
		terms:[new Term("Austrália",4)],
		classification:[SC.place_human_country, SC.place_human_country],
		sentenceIndex:0,termIndex:4
		)
		
	 	list_ne << ne1
		
		List cloneNE = list_ne.clone()
		cloneNE.each{ne -> split_pt.processNE(ne, list_ne)}
		assert list_ne == new ListOfNE([ne2, ne3])
	}
	
	// Mar do Japão -> don't split!
	void testPT2() {
		
	 	ListOfNE list_ne = new ListOfNE()
	
	 	NamedEntity ne1 = new NamedEntity(
		terms:[new Term("Mar",2),new Term("do",3),new Term("Japão",4)],
		classification:[SC.place_physical_watermass], sentenceIndex:0,termIndex:2 )
	 
	 	list_ne << ne1
		
		List cloneNE = list_ne.clone()
		cloneNE.each{ne -> split_pt.processNE(ne, list_ne)}

		assert list_ne == new ListOfNE([ne1])
	}
	
}