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
class TestOndeFicaATorreDeBelem extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
   	QuestionAnalyser qa
	Question q 
	Configuration conf
	
	public TestOndeFicaATorreDeBelem() {
		// initialize
		 conf = Configuration.newInstance()
		 def path = "data/SPARQL"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "local")
//		conf.set("saskia.dbpedia.mode", "dbpedia")
		conf.set("saskia.dbpedia.local.files", "$path/Torre_de_Belem.nt, $path/Torre_de_Belem_hack.nt")
	 	 qa = new QuestionAnalyser()

	}
	
	void testOndeFicaATorreDeBelem() {
  
	 // ****  Onde fica a Torre de Bel�m? ***
	 String line = """
onde	onde	ADV_aloc_interr	0	0	0	ADVS>	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
a	a	PRP	0	0	0	<SC	0
Torre	Torre=de=Bel\u00e9m	PROP_top	0	S	M/F	P<	0
de	Torre=de=Bel\u00e9m	PROP_top	0	S	M/F	P<	0
Bel\u00e9m	Torre=de=Bel\u00e9m	PROP_top	0	S	M/F	P<	0
?	?	PU	0	0	0	PONT	0""" 
	 SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(
		line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
  	 q = new Question(sentence)	
	 assert q.questionType == QuestionType.None
	
	 qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
	 assert q.questionType == QuestionType.Where, "Question type is ${q.questionType}"     
	 
	 qa.applyRules(q, QuestionRulesPT.rulesToCaptureQuestionEntities)
	 assert q.subject == null
	
	assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
	 Condition c = q.conditions[0]
	
	 assert c.predicate.terms.size() == 1
	 assert c.predicate.terms[0].text == "fica"
	 assert c.predicate.terms[0].lemma == "ficar"
	 assert c.predicate.dbpedia_ontology_property.contains("dbpedia2:location"), \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"
	 assert c.predicate.dbpedia_ontology_property.contains("dbpedia2:locationCity"), \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"

	 assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Place"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
	 assert c.object.dbpediaWikipediaCategory.contains(
		"http://dbpedia.org/resource/Category:Buildings_and_structures_in_Lisbon"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

	 qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)

	 assert q.expectedAnswerType.categoryHAREM == ["LOCAL"]
	 assert q.expectedAnswerType.typeHAREM == []
	 assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.ONTOLOGYCLASS
	
//	 Question2SPARQL qd = new Question2SPARQL()
//	 qd.query(q)
	 assert q.answer == ["http://dbpedia.org/resource/Lisbon"], \
		"Question answer is ${q.answer}"
	 assert q.answerJustification.contains("http://dbpedia.org/resource/Bel%C3%A9m_Tower"), \
		"Question answerJustification is ${q.answerJustification}"
	
  }

}
