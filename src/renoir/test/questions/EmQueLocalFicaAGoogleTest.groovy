package renoir.test.questions

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import renoir.obj.*
import renoir.bin.WriteRun
import renoir.bin.QueryCandidateAnswers
import renoir.rules.*
import rembrandt.obj.*
import saskia.bin.Configuration
import com.thoughtworks.xstream.*

/**
 * Check the detection of question types
 * note: do "echo "XXX" | native2ascii -encoding UTF-8"  to get the \\u XXXX code
 * @author Nuno Cardoso
 */
class EmQueLocalFicaAGoogleTest extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	QuestionAnalyser qa
	Question q
	Configuration conf

	public EmQueLocalFicaAGoogleTest() {
		// initialize
		conf = Configuration.newInstance()
		def path = "data/SPARQL"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "local")
		conf.set("saskia.dbpedia.local.files", "$path/Google.nt, $path/Google_hack.nt")
		qa = new QuestionAnalyser()
	}

	void testEmQueLocalFicaAGoogle() {

		// ****  Em que local fica a Google? ***
		String line = """
em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	S	M	>N	0
local	local	N_Lh	0	S	M	P<	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
a	a	PRP	0	0	0	<SC	0
Google	Google	PROP_org	0	S	M/F	P<	0
?	?	PU	0	0	0	PONT	0
"""

		/* O twist aqui é saber que local pode ser qualquer coisa, de país a cidade */

		SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(
				line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		q = new Question(sentence)
		q.id = "Test-Google-02"

		assert q.questionType == QuestionType.None

		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"


		qa.applyRules(q, QuestionRulesPT.rulesToCaptureQuestionEntities)
		assert q.subject.terms*.text == ["local"], "It should have a subject, it has ${q.subject}"
		assert q.subject.ontologyDBpedia == [
			'http://dbpedia.org/ontology/Place'
		], \
		"It should have a q.subject.ontologyDBpedia, it has ${q.subject.ontologyDBpedia}"

		// local only gives a generic category
		assert q.subject.categoryHAREM == ['LOCAL'], "is ${q.subject.categoryHAREM} instead"
		assert q.subject.typeHAREM == [], "is ${q.subject.typeHAREM} instead"
		assert q.subject.subtypeHAREM == [], "is ${q.subject.subtypeHAREM} instead"

		assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
		Condition c = q.conditions[0]

		assert c.predicate.terms.size() == 1
		assert c.predicate.terms[0].text == "fica"
		assert c.predicate.terms[0].lemma == "ficar"
		assert c.predicate.dbpedia_ontology_property.contains("dbpedia2:location"), \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"
		assert c.predicate.dbpedia_ontology_property.contains("dbpedia2:locationCity"), \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"

		assert c.object.ne.terms.size() == 1, "Condition object has ${c.object.ne.terms.size()} terms."
		assert c.object.haremCategory.contains("ORGANIZACAO")
		assert c.object.dbpediaResource.contains("http://dbpedia.org/resource/Google"), \
		"Condition object DBpedia: ${c.object.dbpediaResource}"
		assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Company"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
		assert c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Internet_search_engines"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

		qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)

		assert q.expectedAnswerType.categoryHAREM.contains('LOCAL')
		assert q.expectedAnswerType.typeHAREM == []
		assert q.expectedAnswerType.subtypeHAREM == []
		assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.ONTOLOGYCLASS

		//	 Question2SPARQL qd = new Question2SPARQL()
		//	 qd.query(q)

		assert q.answer.contains("http://dbpedia.org/resource/Mountain_View%2C_California"),\
		"Question answer is ${q.answer}"
		assert q.answer.contains("http://dbpedia.org/resource/California"),\
		"Question answer is ${q.answer}"

		assert q.answerJustification.contains("http://dbpedia.org/resource/Google"), \
		"Question answerJustification is ${q.answerJustification}"

		// imprimir resultados
		WriteRun wr = new WriteRun()

		List answer = wr.format(q)
		answer.each{it ->
			// map with keys line, boolAnswer and boolJust
			println ""+it.boolAnswer+" "+it.boolJust+" "+it.line
		}
	}
}
