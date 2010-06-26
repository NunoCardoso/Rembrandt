package saskia.test

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import saskia.gazetteers.*

/**
 * @author Nuno Cardoso
 */
class TestWikipediaCategoryPlaces extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	Places wiki_pt
	
	public TestWikipediaCategoryPlaces() {
	    wiki_pt = WikipediaCategoryPlacesPT.newInstance()
	}
    
    void test1() {
	  
	   def answer = wiki_pt.getFromPlaceAdjective(["Romenos"])
	   println answer[0].answer.name
	   assert "Roménia" == answer[0].answer.name, "Got $answer instead."
	   
	   answer = wiki_pt.getFromPlaceAdjective(["Sul-Africanos"])
	   assert ['África','do','Sul'] == answer[0].answer.name, "Got $answer instead."
	
	}
	
	void test2() {
	   def answer = wiki_pt.getFromPlaceName(["Roménia"])
	   assert "[Rr]omen[oa]s?" == answer[0].answer.adj, "Got $answer instead."
	   assert "Roménia" == answer[0].needle, "Got $answer instead."
	
	}
}