package renoir.test.questions

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import renoir.obj.*
import renoir.rules.*
import rembrandt.obj.*
import saskia.bin.Configuration
import com.thoughtworks.xstream.*

/**
 * Groovy Test for a Question Parser
 */
class QuantoPesaUmFerrariF40Test extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	QuestionAnalyser qa
	Question q
	Configuration conf

	public QuantoPesaUmFerrariF40Test() {
		// initialize
		conf = Configuration.newInstance()
		def path = "data/SPARQL"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "local")
		conf.set("saskia.dbpedia.local.files",
				"$path/FerrariF40FromDBpediaService.nt")
		qa = new QuestionAnalyser()
	}

	void testQuantoPesaUmFerrariF40() {

		// *** 3. Quanto pesa um Ferrari F40? ***
		String line = """<mwe pos=DET>
	quanto	quanto	DET_rel	0	S	M	ADV>	0
	pesa	pesar	V	PR_IND	3S	0	FMV	0
	</mwe>
	um	um	DET_arti	0	S	M	>N	0
	Ferrari	Ferrari=F40	PROP_V	0	S	M	NPHR	0
	F40	Ferrari=F40	PROP_V	0	S	M	NPHR	0
	?	?	PU	0	0	0	PONT	0
	"""

		SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		q = new Question(sentence)
		assert q.questionType == QuestionType.None

		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.HowMuch, "Question type is ${q.questionType}"

		qa.applyRules(q, QuestionRulesPT.rulesToCaptureQuestionEntities)
		assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
		Condition c = q.conditions[0]
		assert c.predicate.terms.size() == 1, "Condition predicate has ${c.predicate.terms.size()} terms."
		assert c.predicate.terms[0].text == "pesa"
		assert c.predicate.terms[0].lemma == "pesar"
		assert c.predicate.dbpedia_ontology_property == ["dbpedia-owl:weight"], \
		"Dbpedia_ontology_property = ${q.conditions[0].predicate.dbpedia_ontology_property}"

		assert c.operator == null, "Condition operator is not null, is ${c.operator}."

		assert c.object.ne.terms.size() == 2, "Condition object has ${c.object.ne.terms.size()} terms."
		assert c.object.haremCategory.contains("COISA")
		assert c.object.dbpediaResource.contains("http://dbpedia.org/resource/Ferrari_F40"), \
		"Condition object DBpedia: ${c.object.dbpediaResource}"
		assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Automobile") &&
		c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/MeanOfTransportation"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
		assert c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Vehicles_introduced_in_1987") &&
		c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Ferrari_vehicles"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

		qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
		assert q.expectedAnswerType.categoryHAREM.contains('VALOR')
		assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.PROPERTY

		// Question2SPARQL qd = new Question2SPARQL()
		// qd.query(q)
		assert q.answer == [
			"1100^^http://dbpedia.org/ontology/kilogram"
		], \
		"Question answer is ${q.answer}"
		assert q.answerJustification.contains("http://dbpedia.org/resource/Ferrari_F40"), \
		"Question answerJustification is ${q.answerJustification}"
	}
}