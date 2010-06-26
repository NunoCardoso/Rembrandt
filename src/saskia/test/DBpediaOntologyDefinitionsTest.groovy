package saskia.test
import saskia.gazetteers.DBpediaOntologyDefinitionsPT;

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import saskia.dbpedia.*

/**
 * @author Nuno Cardoso
 */
class TestDBpediaOntologyDefinitionsPT extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	
	public TestDBpediaOntologyDefinitionsPT() {
		// initialize
		DBpediaOntologyDefinitionsPT db = new DBpediaOntologyDefinitionsPT()
	}
    
    void test1() {
	   assert  ["City"] == DBpediaOntologyDefinitionsPT.getAnswerFromNeedle(["Cidade"])
	   assert  ["Country"] == DBpediaOntologyDefinitionsPT.getAnswerFromNeedle(["País"])
	   assert  ["Country"] == DBpediaOntologyDefinitionsPT.getAnswerFromNeedle(["país"])	
	   assert  ["Country"] == DBpediaOntologyDefinitionsPT.getAnswerFromNeedle(["países"])
	}
}