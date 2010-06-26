package saskia.test
import saskia.gazetteers.DBpediaPropertyDefinitionsPT;

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import saskia.dbpedia.*
import renoir.obj.Predicate

/**
 * @author Nuno Cardoso
 */
class TestDBpediaPropertyDefinitionsPT extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	
	public TestDBpediaPropertyDefinitionsPT() {
		// initialize
	}
    
    void test1() {

		def list = DBpediaPropertyDefinitionsPT.getOntologyPropertyFromPredicate("habitante")
		assert list.contains("dbpedia2:populationTotal"), "List: $list"
		assert list.contains("dbpedia2:populationEstimate"), "List: $list"

		 list = DBpediaPropertyDefinitionsPT.getOntologyPropertyFromPredicate("popula\u00e7\u00e3o")
		assert list.contains("dbpedia2:populationTotal"), "List: $list"
		assert list.contains("dbpedia2:populationEstimate"), "List: $list"
	}
	
	void test2() {
		Predicate p = new Predicate(dbpedia_ontology_property:["dbpedia2:populationTotal"])
		dsaskia.gazetteers.DBpediaPropertyDefinitionsPT.getHAREMclassificationsForEATfromPredicate(p)
		assert list.category.contains("NUMERO")
	}
}	