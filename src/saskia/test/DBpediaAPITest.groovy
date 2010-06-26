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

package saskia.test

import org.junit.*
import org.junit.runner.*

import org.apache.log4j.*

import saskia.dbpedia.*
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * Tester for WikipediaAPI.
 */
class TestDBpediaAPI extends GroovyTestCase {
	
	static Logger log = Logger.getLogger("JUnitTest")
	static Configuration conf = Configuration.newInstance()
	DBpediaAPI dbpedia
	
    public TestDBpediaAPI() {    
	   dbpedia = DBpediaAPI.newInstance(conf)
    }
  
    void testGetLabelFromDBpediaResource() {
		String resource_en = "http://dbpedia.org/resource/United_Kingdom"
		String response_en = dbpedia.getLabelFromDBpediaResource(resource_en, 'en') 
		String response_pt = dbpedia.getLabelFromDBpediaResource(resource_en, 'pt') 
		assert response_en == "United Kingdom"
		assert response_pt == "Reino Unido"
	}
			  
 	void testGetDBpediaResourceFromWikipediaURL() {
	 	String resource_en = "United_Kingdom"
		String resource_pt = "Reino_Unido"
		String response_en = dbpedia.getDBpediaResourceFromWikipediaURL(resource_en, 'en') 
		String response_pt = dbpedia.getDBpediaResourceFromWikipediaURL(resource_pt, 'pt') 
		assert response_en == "http://dbpedia.org/resource/United_Kingdom"
//		assert null == "http://dbpedia.org/resource/Reino_Unido"
	}
	
	void testGetDBpediaResourceFromWikipediaPageTitle() {
	 	String resource_en = "United Kingdom"
		String resource_pt = "Reino Unido"
		String response_en = dbpedia.getDBpediaResourceFromWikipediaPageTitle(resource_en, 'en') 
		String response_pt = dbpedia.getDBpediaResourceFromWikipediaPageTitle(resource_pt, 'pt') 
		assert response_en == "http://dbpedia.org/resource/United_Kingdom"
		assert response_pt == "http://dbpedia.org/resource/United_Kingdom" // ele já converte o redirect
	}

	void testGetEverythingFromDBpediaResource() {
	 	String resource_en = "http://dbpedia.org/resource/United_Kingdom"
		String resource_en2 = "http://dbpedia.org/resource/UK"
		Map response_en = dbpedia.getEverythingFromDBpediaResource(resource_en) 
		Map response_en2 = dbpedia.getEverythingFromDBpediaResource(resource_en2) 
		assert response_en['http://xmlns.com/foaf/0.1/page'] == 'http://en.wikipedia.org/wiki/United_Kingdom'
		assert response_en2['http://dbpedia.org/property/redirect'] == 'http://dbpedia.org/resource/United_Kingdom'
	}
	
	
	void testGetDBpediaOntologyClassFromDBpediaResource() {
	 	String resource_en = "http://dbpedia.org/resource/United_Kingdom"
		List response_en = dbpedia.getDBpediaOntologyClassFromDBpediaResource(resource_en) 
		assert response_en.contains("http://dbpedia.org/ontology/Country")
	}
	
	void testGetDBpediaSKOSCategoryFromDBpediaResource() {
		String resource_en = "http://dbpedia.org/resource/United_Kingdom"
		List response_en = dbpedia.getDBpediaSKOSCategoryFromDBpediaResource(resource_en) 
		println response_en
	}
}

