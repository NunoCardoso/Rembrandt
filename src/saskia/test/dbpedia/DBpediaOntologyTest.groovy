package saskia.test.dbpedia

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import saskia.dbpedia.*

/**
 * @author Nuno Cardoso
 */
class DBpediaOntologyTest extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	String shortName1, fullName1, shortName2, fullName2, shortName3, fullName3
	String shortName4, fullName4, shortName5, fullName5
	def wrongName1
	DBpediaOntology dbpediaontology

	public DBpediaOntologyTest() {
		// initialize

		dbpediaontology = DBpediaOntology.getInstance()

		shortName1 = "City"
		fullName1 = "http://dbpedia.org/ontology/City"
		shortName2 = "PopulatedPlace"
		fullName2 = "http://dbpedia.org/ontology/PopulatedPlace"
		shortName3 = "Place"
		fullName3 = "http://dbpedia.org/ontology/Place"
		shortName4 = "Resource"
		fullName4 = "http://dbpedia.org/ontology/Resource"
		shortName5 = "Thing"
		fullName5 = "http://dbpedia.org/ontology/Thing"
		wrongName1 = "XXXX"
	}

	void testIdOfAndGetName() {
		assert fullName1 == dbpediaontology.getNameFromID(dbpediaontology.idOf(fullName1))
		assert fullName1 == dbpediaontology.getNameFromID(dbpediaontology.idOf(shortName1))
		assert shortName1 == dbpediaontology.getNameFromID(dbpediaontology.idOf(fullName1), true)
		assert shortName1 == dbpediaontology.getNameFromID(dbpediaontology.idOf(shortName1), true)
	}

	void testFullAndShortName() {
		assert fullName1 == dbpediaontology.getFullName(fullName1)
		assert fullName1 == dbpediaontology.getFullName(shortName1)
		assert shortName1 == dbpediaontology.getShortName(fullName1)
		assert shortName1 == dbpediaontology.getShortName(shortName1)
	}

	void testClass() {
		assert true == 	dbpediaontology.isClass(fullName1)
		assert true == 	dbpediaontology.isClass(shortName1)
		assert false == dbpediaontology.isClass(wrongName1)
	}

	void testSuperClassesOf() {
		// Thing has not super classes
		if (dbpediaontology.getVersion() == "3.2") {
			assert []== dbpediaontology.getSuperClassesOf(shortName4)
			assert []== dbpediaontology.getSuperClassesOf(fullName4)
			assert [
				'Place',
				'PopulatedPlace',
				'Resource'
			] == dbpediaontology.getSuperClassesOf(shortName1).sort(),
			"Got "+dbpediaontology.getSuperClassesOf(shortName1).sort()
			assert [
				'Place',
				'PopulatedPlace',
				'Resource'
			] 	== dbpediaontology.getSuperClassesOf(fullName1).sort()
		}
		if (dbpediaontology.getVersion() == "3.5.1") {
			assert []== dbpediaontology.getSuperClassesOf(shortName5)
			assert []== dbpediaontology.getSuperClassesOf(fullName5)
			assert [
				'Place',
				'PopulatedPlace',
				'Settlement',
				'Thing'
			] == dbpediaontology.getSuperClassesOf(shortName1).sort(), \
			"Got "+dbpediaontology.getSuperClassesOf(shortName1).sort()
			assert [
				'Place',
				'PopulatedPlace',
				'Settlement',
				'Thing'
			] == dbpediaontology.getSuperClassesOf(fullName1).sort()
		}
	}

	void testIsSubClassOf() {
		assert true == dbpediaontology.isSubClassOf(shortName1, fullName2)
		assert true == dbpediaontology.isSubClassOf(fullName1, shortName2)
		if (dbpediaontology.getVersion() == "3.2") {
			assert true == dbpediaontology.isSubClassOf(fullName1, fullName4)
			assert true == dbpediaontology.isSubClassOf(shortName1, shortName4)
			assert false == dbpediaontology.isSubClassOf(shortName4, fullName2)
		}
		if (dbpediaontology.getVersion() == "3.5.1") {
			assert true == dbpediaontology.isSubClassOf(fullName1, fullName5)
			assert true == dbpediaontology.isSubClassOf(shortName1, shortName5)
			assert false == dbpediaontology.isSubClassOf(shortName5, fullName2)
		}

		assert false == dbpediaontology.isSubClassOf(fullName3, shortName2)
	}

	void testBroaderAndNarrowerClass() {
		assert shortName4 == dbpediaontology.getBroaderClassFrom([
			fullName4,
			fullName1,
			shortName3,
			shortName2
		]), \
			"Got "+dbpediaontology.getBroaderClassFrom([
			fullName4,
			fullName1,
			shortName3,
			shortName2
		])
		assert shortName1 == dbpediaontology.getNarrowerClassFrom([
			fullName3,
			fullName2,
			shortName4,
			shortName1
		])
		assert 'Book' == dbpediaontology.getNarrowerClassFrom([
			'http://dbpedia.org/ontology/Work',
			'http://dbpedia.org/ontology/Book',
			'http://dbpedia.org/ontology/Resource'
		])
	}

	void testIsAPlace() {
		assert dbpediaontology.isAPlace("Place") == true
		assert dbpediaontology.isAPlace("Continent") == true
		assert dbpediaontology.isAPlace("http://dbpedia.org/ontology/Place") == true
		assert dbpediaontology.isAPlace("http://dbpedia.org/ontology/Continent") == true
		assert dbpediaontology.isAPlace("Animal") == false
		assert dbpediaontology.isAPlace("http://dbpedia.org/ontology/Animal") == false
	}
}