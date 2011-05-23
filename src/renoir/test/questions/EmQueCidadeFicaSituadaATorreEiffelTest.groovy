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
class EmQueCidadeFicaSituadaATorreEiffelTest extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	QuestionAnalyser qa
	Question q
	Configuration conf

	public EmQueCidadeFicaSituadaATorreEiffelTest() {
		// initialize
		conf = Configuration.newInstance()
		def path = "data/SPARQL"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "local")
		conf.set("saskia.dbpedia.local.files", "$path/Eiffel_Tower.nt, "+
				"$path/Paris.nt, $path/Paris_hack.nt")
		qa = new QuestionAnalyser()

	}

	void testEmQueCidadeFicaSituadaATorreEiffel() {

		// ****  Em que cidade fica a Torre Eiffel? ***
		String line = """
em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	S	F	>N	0
cidade	cidade	N_Lciv	0	S	F	P<	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
situada	situar	V	PCP	S	F	<SC	0
a	o	DET_artd	0	S	F	>N	0
Torre	Torre=Eiffel	PROP_top	0	S	M/F	P<	0
Eiffel	Torre=Eiffel	PROP_top	0	S	M/F	P<	0
?	?	PU	0	0	0	PONT	0
"""
		SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(
				line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)

		assert sentence*.type == [
			"PRP",
			"DET",
			"N",
			"V",
			"V",
			"DET",
			"PROP",
			"PROP",
			"PONT"
		], \
		"Got ${sentence*.type} instead."
		q = new Question(sentence)
		assert q.questionType == QuestionType.None

		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"

		qa.applyRules(q, QuestionRulesPT.rulesToCaptureQuestionEntities)
		assert q.subject.terms*.text == ["cidade"], "It should have a subject, it has ${q.subject}"
		assert q.subject.ontologyDBpedia == [
			'http://dbpedia.org/ontology/City'
		], \
		"It should have a q.subject.ontologyDBpedia, it has ${q.subject.ontologyDBpedia}"
		assert q.subject.categoryHAREM == ['LOCAL'], "is ${q.subject.categoryHAREM} instead"
		assert q.subject.typeHAREM == ['HUMANO'], "is ${q.subject.typeHAREM} instead"
		assert q.subject.subtypeHAREM == ['DIVISAO'], "is ${q.subject.subtypeHAREM} instead"

		assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
		Condition c = q.conditions[0]

		assert c.predicate.terms.size() == 2
		assert c.predicate.terms*.text == ["fica", "situada"]
		assert c.predicate.terms*.lemma == ["ficar", "situar"]
		assert c.predicate.dbpedia_ontology_property.contains("dbpedia2:location"), \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"
		assert c.predicate.dbpedia_ontology_property.contains("dbpedia2:locationCity"), \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"

		assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Building") &&
		c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Skyscraper"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
		assert c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Landmarks_in_France") &&
		c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Skyscrapers_in_Paris"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

		qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)

		assert q.expectedAnswerType.categoryHAREM.contains('LOCAL')
		assert q.expectedAnswerType.typeHAREM.contains('HUMANO')
		assert q.expectedAnswerType.subtypeHAREM.contains('DIVISAO')
		assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.ONTOLOGYCLASS

		//	 Question2SPARQL qd = new Question2SPARQL()
		//	 qd.query(q)
		assert q.answer == [
			"http://dbpedia.org/resource/Paris"
		], \
		"Question answer is ${q.answer}"
		assert q.answerJustification.contains("http://dbpedia.org/resource/Eiffel_Tower"), \
		"Question answerJustification is ${q.answerJustification}"

	}
}
