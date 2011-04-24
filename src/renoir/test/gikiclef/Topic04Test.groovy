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
import saskia.dbpedia.*
import saskia.ontology.*




class TestTopic04 extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
   	QuestionAnalyser qa
	Question q 
	Configuration conf
	//XStream xstream
	WriteRun wr
	DBpediaAPI dbpedia
	
	public TestTopic04() {
		// initialize
		conf = Configuration.newInstance()
		qa = new QuestionAnalyser()
		//xstream = new XStream()
		wr =  new WriteRun()
		dbpedia = DBpediaAPI.newInstance()
		 
	}
	
	void testGenerateTopic04() {

	 // ****  T�pico: ***	
	 String linguateca_line = """Poetas	poeta	N	0	P	M	NPHR	0
romenos	romeno	ADJ	0	P	M	N<	0
com	com	PRP	0	0	0	N<	0
livros	livro	N	0	P	M	P<	0
de	de	PRP	0	0	0	N<	0
baladas	balada	N	0	P	F	P<	0
publicados	publicar	V	PCP	P	M	N<	0
at�	at�	PRP	0	0	0	ADVL	0
1941	1941	NUM_card	0	P	M/F	P<	0
.	.	PU	0	0	0	PONT	0
"""			
	 SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(
		linguateca_line, SentenceWithPoS.FROM_LINGUATECA_PoS)
  	 q = new Question(sentence)
	 q.id = "GC-2009-04"

	 // strategy: map Wikipedia class, search for remainder words. 
		 // Assert qestion type 
	 assert q.questionType == QuestionType.None	
	 qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
	 assert q.questionType == QuestionType.None, "Question type is ${q.questionType}"
	
	 // Assert query parse
	 qa.applyRules(q, GikiCLEFQuestionRulesPT.gikiclefRules)

	 assert q.subject.terms*.text == ["Poetas","romenos"], "Got ${q.subject.terms*.text} instead"
	 assert q.subject.scope*.text == ['romenos'], "Got ${q.subject.scope*.text} instead"
	 assert q.subject.groundedScope.name == ['Rom�nia'], "Got ${q.subject.groundedScope.name} instead"

	 assert q.subject.ontologyDBpedia == ['http://dbpedia.org/ontology/Artist'], \
			"Got ${q.subject.ontologyDBpedia} instead"
	 assert q.subject.categoryWikipedia == ['Poetas da Rom�nia'], \
			"Got ${q.subject.categoryWikipedia} instead"
	 assert q.subject.categoryHAREM == ['PESSOA'], "Got ${q.subject.categoryHAREM} instead"
   
	 // nothing else...
	 assert q.conditions.size() == 0, "Got ${q.conditions.size()} conditions instead."
	 
	 // let's pass the subject to EAT

	 qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
	 assert q.expectedAnswerType.categoryWikipedia.contains('Poetas da Rom�nia'), \
	   "Got ${q.expectedAnswerType.categoryWikipedia} instead."
	 assert q.expectedAnswerType.categoryHAREM.contains('PESSOA'), \
	   "Got ${q.expectedAnswerType.categoryHAREM} instead."
	assert q.expectedAnswerType.ontologyClass.contains('http://dbpedia.org/ontology/Artist')

	assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.WikipediaCategory
	
	 // Assert results
	// Question2SPARQL qd = new Question2SPARQL()
	// qd.query(q)
	
	// no results, because there's no predicate to map
	 assert q.answer == []
	 assert q.answerJustification == []
	
	 // select across databases  
	 // select saskia.pt_page.page_id from saskia.pt_page, wikirembrandt.doc where wikirembrandt.doc.doc_original_id=808 and wikirembrandt.doc.doc_original_id=saskia.pt_page.page_id;
	   
	  // get docs with category "Poetas da Rom�nia"
	  // select page_id from pt_page, pt_categorylinks where cl_to="Poetas_da_Rom�nia" and cl_from=page_id;
/*+---------+
| page_id |
+---------+
|  123615 | 
|  231034 | 
|   75873 | 
|  212773 | 
| 1604056 | 
+---------+
*/

	//def ids = [123615 , 231034, 75873, 212773, 1604056 ]
	//new PlanB_Strategy1().getWikipediaRembrandtedPages(ids)
	  
	// PlanB_Strategy3 planb3 = new PlanB_Strategy3()
//	plan1.
/*	List<RembrandtedDoc> docs = planb3.wr.queryWithTermsAndWikipediaCategory(
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