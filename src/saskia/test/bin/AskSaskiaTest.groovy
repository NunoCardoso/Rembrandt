/* Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package saskia.test.bin

import org.junit.*
import org.apache.log4j.*

import rembrandt.obj.NamedEntity
import saskia.bin.AskSaskia
import saskia.bin.Configuration
import rembrandt.obj.*
import saskia.dbpedia.*
import rembrandt.gazetteers.CommonClassifications as SC

/**
 * @author Nuno Cardoso
 * Tester for WikipediaAPI.
 */
class AskSaskiaTest extends GroovyTestCase {
	
    static Logger log = Logger.getLogger("JUnit")
	
    AskSaskia saskia_en
    AskSaskia saskia_pt
	DBpediaAPI dbpedia
	
    public AskSaskiaTest() {    
		Configuration conf = Configuration.newInstance()
		saskia_en = AskSaskia.newInstance("en",conf)
		saskia_pt = AskSaskia.newInstance("pt",conf)
		dbpedia = DBpediaAPI.newInstance()
    }
  
    void testClassification() {
	// simple one
		NamedEntity ne1 = new NamedEntity(id:"1", terms:Sentence.simpleTokenize("Leiria"))
		ne1 = saskia_pt.answerMe(ne1)
		println ne1.classification
		assert ne1.classification*.c == ["@LOCAL"]
		//assert ne1.wikipediaPage[ne1.classification[0]] == ["@LOCAL"]
    }

	// test redirect with DBpedia
	void testClassifyDBpedia() {
		
		String resource = dbpedia.getDBpediaResourceFromWikipediaPageTitle("UK", "en")
		NamedEntity ne = new NamedEntity(terms:[new Term("UK",0)], sentenceIndex:0, termIndex:0)
		saskia_en.classifyWithDBpedia(ne, resource, null) 
		println ne
		
		String resource2 = dbpedia.getDBpediaResourceFromWikipediaPageTitle("United Kingdom", "en")
		NamedEntity ne2 = new NamedEntity(terms:[new Term("United",0),new Term("Kingdom"),1], sentenceIndex:0, termIndex:0)
		saskia_en.classifyWithDBpedia(ne2, resource2, null) 
		println ne2
	}
}

    
