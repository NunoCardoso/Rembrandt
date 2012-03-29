package renoir.eval.gikiclef

import java.util.zip.*

import org.apache.log4j.*
import org.junit.*
import org.junit.runner.*

import rembrandt.bin.*
import rembrandt.obj.*
import renoir.bin.WriteRun
import renoir.obj.*
import renoir.rules.*
import saskia.bin.Configuration
import saskia.dbpedia.*
import saskia.ontology.*

import com.thoughtworks.xstream.*


class TestTopic03 extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	QuestionAnalyser qa
	Question q
	Configuration conf
	XStream xstream
	WriteRun wr
	DBpediaAPI dbpedia

	public TestTopic03() {
		// initialize
		conf = Configuration.newInstance()
		conf.set("saskia.dbpedia.mode", "webservice")
		//conf.set("saskia.dbpedia.url", "http://dbpedia.org/sparql")
		qa = new QuestionAnalyser()
		xstream = new XStream()
		wr =  new WriteRun()
		dbpedia = DBpediaAPI.newInstance()

	}

	void testGenerateTopic03() {

		// ****  T�pico: ***
		String linguateca_line = """Em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	P	M	>N	0
pa�ses	pa�s	N	0	P	M	P<	0
<mwe pos=PRP>
fora	fora	ADV	0	0	0	ADVL>	0
</mwe>
da	de+o	PRP+DET_artd	0	S	F	A<+>N	0
Bulg�ria	Bulg�ria	PROP	0	S	F	P<	0
foram	ser	V_fmc	PS/MQP_IND	3P	0	FAUX	0
publicadas	publicar	V	PCP	P	F	IMV_#ICL-AUX<	0
opini�es	opini�o	N	0	P	F	<SUBJ	0
sobre	sobre	PRP	0	0	0	N<	0
as	o	DET_artd	0	P	F	>N	0
ideias	ideia	N	0	P	F	P<	0
de	de	PRP	0	0	0	N<	0
Peter	Peter=Deunov	PROP	0	S	M	P<	0
Deunov	Peter=Deunov	PROP	0	S	M	P<	0
?	?	PU	0	0	0	PONT	0
"""
		SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line, SentenceWithPoS.FROM_LINGUATECA_PoS)
		q = new Question(sentence)
		q.id = "GC-2009-03"

		// Assert qestion type
		assert q.questionType == QuestionType.None
		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"

		// Assert query parse
		qa.applyRules(q, GikiCLEFQuestionRulesPT.gikiclefRules)
		/*
		 println q.subject.terms
		 println q.subject.ontologyDBpedia
		 println q.subject.categoryHAREM
		 println q.subject.scope
		 println q.subject.groundedScope
		 */

		assert q.subject.terms*.text == ["pa�ses"], "Got ${q.subject.terms*.text} instead"

		assert q.subject.ontologyDBpedia == [
			'http://dbpedia.org/ontology/Country'
		], \
		"Got ${q.subject.ontologyDBpedia} instead"
		assert q.subject.subtypeHAREM == ['PAIS'], "Got ${q.subject.subtypeHAREM} instead"

		assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
		Condition c = q.conditions[0]

		assert c.predicate == null, "Got ${c.predicate} instead."


		assert c.object.ne.terms*.text == ["Peter", "Deunov"], "Got ${c.object.ne.terms*.text} instead."
		assert c.object.ne.category  == ["PESSOA"]
		assert c.object.ne.dbpediaPage == [
			"http://dbpedia.org/resource/Peter_Deunov"
		], \
		"Got ${c.object.dbpediaResource} instead"

		assert c.object.dbpediaOntologyClass.contains(
		"http://dbpedia.org/ontology/Philosopher"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
		assert c.object.dbpediaWikipediaCategory.contains(
		"http://dbpedia.org/resource/Category:Bulgarian_Theosophists"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

		// Assert Question EAT
		qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
		assert q.expectedAnswerType.subtypeHAREM.contains('PAIS')
		assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.ONTOLOGYCLASS

		// Assert results
		// Question2SPARQL qd = new Question2SPARQL()
		// qd.query(q)

		// no results, because there's no predicate to map
		assert q.answer == []
		assert q.answerJustification == []

		// plan B - get page of Peter Dunov, get countries.
		//	PlanB_Strategy1 planb1 = new PlanB_Strategy1()
		//	planb1.q = q

		// Add Wikipedia page id of Peter Dunov to the list of pages to visit.
		// Get tagged version of Wikipedia pages of Ernest Hemingway
		//	println "Wikipedia page(s) of ${c.object.ne.printTerms()} is(are) ${c.object.ne.wikipediaPage.keySet().toList()}"
		//	List<Doc> docs = planb1.getWikipediaRembrandtedPages(
		//		c.object.ne.wikipediaPage.keySet().toList())


		/*swer = wr.format(q)
		 answer.each{it -> println it.line}
		 */
	}
}
