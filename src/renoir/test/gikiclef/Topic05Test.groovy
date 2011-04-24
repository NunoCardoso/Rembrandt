package renoir.test.gikiclef

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import com.thoughtworks.xstream.*

import java.util.zip.*

import renoir.obj.*
import renoir.rules.*
import renoir.bin.WriteRun

import rembrandt.obj.*
import rembrandt.util.XMLUtil
import rembrandt.bin.*

import saskia.bin.Configuration
import saskia.dbpedia.DBpediaAPI
import saskia.ontology.*




class TestTopic05 extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
   	QuestionAnalyser qa
	Question q 
	Configuration conf
	XStream xstream
	WriteRun wr
	DBpediaAPI dbpedia
	
	public TestTopic05() {
		// initialize
		conf = Configuration.newInstance()
		qa = new QuestionAnalyser()
		xstream = new XStream()
		wr =  new WriteRun()
		dbpedia = DBpediaAPI.newInstance()
		 
	}
	
	void testGenerateTopic05() {

	 // ****  T�pico: ***	
	 String linguateca_line = """Que	que	DET_interr	0	P	F	>N	0
obras	obra	N	0	P	F	SUBJ>	0
liter�rias	liter�rio	ADJ	0	P	F	N<	0
de	de	PRP	0	0	0	N<	0
autores	autor	N	0	P	M	P<	0
n�o	n�o	ADV	0	0	0	>A	0
romenos	romeno	ADJ	0	P	M	N<	0
t�m	ter	V_fmc	PR_IND	3P	0	FMV	0
por	por	PRP	0	0	0	<ADVL	0
tema	tema	N	0	S	M	P<	0
os	o	DET_artd	0	P	M	>N	0
C�rpatos	C�rpatos	PROP	0	P	M	<ACC	0
?	?	PU	0	0	0	PONT	0
"""			

	 SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(
		linguateca_line, SentenceWithPoS.FROM_LINGUATECA_PoS)
  	 q = new Question(sentence)
	 q.id = "GC-2009-04"

	 // strategy: map Wikipedia class, search for remainder words. 
		 // Assert qestion type 
	 assert q.questionType == QuestionType.None	
	 qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
	 assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"
	
	 // Assert query parse
	 qa.applyRules(q, GikiCLEFQuestionRulesPT.gikiclefRules)

	 assert q.subject.terms*.text == ["obras","liter�rias"], "Got ${q.subject.terms*.text} instead"
	 assert q.subject.scope*.text == [], "Got ${q.subject.scope*.text} instead"
	 assert q.subject.groundedScope == null, "Got ${q.subject.groundedScope.name} instead"

	 assert q.subject.ontologyDBpedia == ['http://dbpedia.org/ontology/Work'], \
			"Got ${q.subject.ontologyDBpedia} instead"
	 assert q.subject.categoryWikipedia == [], \
			"Got ${q.subject.categoryWikipedia} instead"
	 assert q.subject.categoryHAREM == ['OBRA'], "Got ${q.subject.categoryHAREM} instead"
   
	 // Got an object
	 assert q.conditions.size() == 1, "Got ${q.conditions.size()} conditions instead."
	 
	 Condition c = q.conditions[0]
	 assert c.predicate == null, "Condition predicate has ${c.predicate.terms.size()} terms."

	 assert c.object.ne.terms*.text == ["C�rpatos"], "Got ${c.object.ne.terms*.text} instead."
	 println c.object.ne
	 assert c.object.ne.subtype == ["RELEVO"], "Got ${c.object.ne.category} instead."
	 assert c.object.ne.dbpediaPage == ["http://dbpedia.org/resource/Carpathian_Mountains"], \
		"Got ${c.object.ne.dbpediaPage} instead"
		
// Carpathin mountains does not have a DBpedia Ontology class... strange, but true
//	 assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Mountain"), \
//		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
	 assert c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Mountain_ranges_of_Serbia"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

		// let's pass the subject to EAT
	 qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
	 assert !q.expectedAnswerType.categoryWikipedia, \
	   "Got ${q.expectedAnswerType.categoryWikipedia} instead."
	 assert q.expectedAnswerType.categoryHAREM.contains('OBRA'), \
	   "Got ${q.expectedAnswerType.categoryHAREM} instead."
	assert q.expectedAnswerType.ontologyClass.contains('http://dbpedia.org/ontology/Work')

	assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.DBpediaOntologyClass
	
	 // Assert results
	// Question2SPARQL qd = new Question2SPARQL()
	// qd.query(q)
	
	// no results, because there's no predicate to map
	 assert q.answer == []
	 assert q.answerJustification == []
	

	  // PlanB_Strategy1: go to page of Carpathian Mountains, find masterpieces over there

	  
	// PlanB_Strategy3 planb3 = new PlanB_Strategy3()
//	plan1.
	/*List<RembrandtedDoc> docs = planb3.wr.queryWithTermsAndWikipediaCategory(
		q.sentence.toStringLine(), q.expectedAnswerType.categoryWikipedia)
	docs.each{doc -> 
		println "Got document $doc, adding ${doc.rdoc_title} as answer."
		q.answer << doc.rdoc_title.replaceAll(" ","_") // category: 
	}
	
	 List answer = wr.format(q, "pt")
	answer.each{it -> println it.line}
	  */ 
	}
	
	
    
}