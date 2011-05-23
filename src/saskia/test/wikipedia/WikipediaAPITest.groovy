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

package saskia.test.wikipedia

import org.junit.*
import org.junit.runner.*
import org.junit.Assert.*

import org.apache.log4j.*

import org.apache.lucene.document.Document

import rembrandt.obj.NamedEntity
import saskia.wikipedia.*
import saskia.bin.Configuration
import rembrandt.io.*

/**
 * @author Nuno Cardoso
 * Tester for WikipediaAPI.
 */
class WikipediaAPITest extends GroovyTestCase {

	def Logger log = Logger.getLogger("RembrandtTestLogger")

	def wikipedia
	def db

	public WikipediaAPITest() {
		Configuration conf = Configuration.newInstance(Rembrandt.defaultconf)
		wikipedia = WikipediaAPI.newInstance(conf)

		log.info "Starting DB"
		try {
			db = WikipediaDB.newInstance().getDB()
		}  catch (Exception e) {
			log.fatal "Can't open db:"+e.printStackTrace()
		}
	}

	void testAlternativeDocumentFromDB() {
		def newdoc = null
		db.eachRow (WikipediaDB.selectDocumentFromRedirection, ["Cavaco_silva"]) {row -> newdoc = row[0]}
		assert newdoc == "Aníbal_Cavaco_Silva"
	}
	/*
	 void testGetDisambiguationPages() {
	 log.info "testing Wikipedia with "+topic1
	 def doc = wikipedia.getDisambiguationTitlePage(topic1)
	 assert doc instanceof org.apache.lucene.document.Document
	 def doctitle = doc.get("title").trim()
	 def docid = doc.get("id").trim()
	 log.info "ID: ${docid} Title: ${doctitle}"
	 assert doctitle == topic1+" (desambiguação)"
	 }
	 void testGetOutlinks() {
	 def doc = wikipedia.getDisambiguationTitlePage(topic1)
	 def allOutlinks =  wikipedia.getOutlinks(doc)//, topicWithDisambiguationPage)
	 def filteredOutlinks =  wikipedia.getOutlinks(doc, topic1)//, topicWithDisambiguationPage)
	 assert allOutlinks == ["Língua inglesa", 
	 "Nova Iorque", 
	 "Estados Unidos da América", 
	 "Nova Iorque (estado)", 
	 "Manhattan", 
	 "Nova Iorque (Maranhão)"]
	 assert filteredOutlinks == ["Nova Iorque", 
	 "Nova Iorque (estado)", 
	 "Nova Iorque (Maranhão)"]
	 }
	 void testGetListings() {
	 def doc = wikipedia.getDisambiguationTitlePage(topic1)
	 def listings = doc.getValues("listings").toList()
	 //println listings
	 assert listings == ["Nova Iorque - maior cidade dos Estados Unidos da América", 
	 "Nova Iorque - um dos 50 estado dos Estados Unidos", 
	 "Condado de Nova Iorque - condado do estado de Nova Iorque", 
	 "Nova Iorque - município brasileiro no Maranhão"]
	 }
	 void testGetTitlePage() {
	 def doc = wikipedia.getTitlePage("Nova Iorque")
	 assert doc.get("title") == "Nova Iorque", "Error: doc.get('title') is not Nova Iorque"
	 }
	 void testGetCategories() {
	 def doc = wikipedia.getTitlePage("Nova Iorque")
	 def cats = wikipedia.getCategories(doc)
	 assert cats == ["Nova Iorque", "Cidades de Nova Iorque"]
	 }
	 void testGetTextInParenthesis() {
	 def test = "Nova Iorque (desambiguação)"
	 assert wikipedia.getTextInParenthesis(test) == "desambiguação"
	 assert wikipedia.getTextInParenthesis("nothing") == null
	 }
	 void testGetLuceneTokenizedDoc() {
	 def String query = "Nova Iorque"
	 def Document doc = wikipedia.getTitlePage(query)
	 def firstParagraph = wikipedia.getParagraphAt(doc, 0)
	 def sentences = wikipedia.getSentencesAndTerms(firstParagraph)
	 def queryTerms = wikipedia.getSentencesAndTerms(query)
	 assert 1==1
	 }*/
}


