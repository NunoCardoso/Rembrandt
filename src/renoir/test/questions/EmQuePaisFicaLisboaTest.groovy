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
 * Check the detection of question types
 * note: do "echo "XXX" | native2ascii -encoding UTF-8"  to get the \\u XXXX code
 * @author Nuno Cardoso
 */
class EmQuePaisFicaLisboaTest extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	QuestionAnalyser qa
	Question q
	Configuration conf

	public EmQuePaisFicaLisboaTest() {
		// initialize
		conf = Configuration.newInstance()
		def path = "data/SPARQL"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "local")
		conf.set("saskia.dbpedia.local.files", "$path/Portugal.nt, "+
				"$path/LisbonFromDBpediaPT.nt, $path/LisbonFromDBpediaService.nt")
		qa = new QuestionAnalyser()

	}

	void testEmQuePaisFicaLisboa() {

		// ****  Em que pa\u00eds fica Lisboa? ***
		String line = """
	em	em	PRP	0	0	0	ADVL>	0
	que	que	DET_interr	0	S	M	>N	0
	pa\u00eds	pa\u00eds	N_Lciv	0	S	M	P<	0
	fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
	Lisboa	Lisboa	PROP_civ	0	S	F	<SUBJ	0
	?	?	PU	0	0	0	PONT	0
	"""
		SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		q = new Question(sentence)
		assert q.questionType == QuestionType.None

		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"

		qa.applyRules(q, QuestionRulesPT.rulesToCaptureQuestionEntities)
		assert q.subject.terms*.text == ["pa\u00eds"], "It should have a subject, it has ${q.subject}"
		assert q.subject.ontologyDBpedia == [
			'http://dbpedia.org/ontology/Country'
		], \
		"It should have a q.subject.ontologyDBpedia, it has ${q.subject.ontologyDBpedia}"
		assert q.subject.categoryHAREM == ['LOCAL'], "is ${q.subject.categoryHAREM} instead"
		assert q.subject.typeHAREM == ['HUMANO'], "is ${q.subject.typeHAREM} instead"
		assert q.subject.subtypeHAREM == ['PAIS'], "is ${q.subject.subtypeHAREM} instead"

		assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
		Condition c = q.conditions[0]

		assert c.predicate.terms.size() == 1
		assert c.predicate.terms[0].text == "fica"
		assert c.predicate.terms[0].lemma == "ficar"
		assert c.predicate.dbpedia_ontology_property == [], \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"

		assert c.operator.terms.size() == 1
		assert c.operator.terms[0].text == "fica"
		assert c.operator.terms[0].lemma == "ficar"
		assert c.operator.predicateOperator == PredicateOperator.LocatedIn, \
	    "is ${c.operator.predicateOperator} instead"

		assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Municipality") &&
		c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/PopulatedPlace"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
		assert c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Cities_in_Portugal") &&
		c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Capitals_in_Europe"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"


		/*	 qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
		 assert q.expectedAnswerType.categoryHAREM.contains('VALOR')
		 assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Property
		 Question2SPARQL qd = new Question2SPARQL()
		 qd.query(q)
		 assert q.answer == ["1100^^http://dbpedia.org/ontology/kilogram"], \
		 "Question answer is ${q.answer}"
		 assert q.answerJustification.contains("http://dbpedia.org/resource/Ferrari_F40"), \
		 "Question answerJustification is ${q.answerJustification}"
		 */	
	}
}
