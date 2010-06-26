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
class TestQualEACapitalDePortugal extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
   	QuestionAnalyser qa
	Question q 
	Configuration conf
	
	public TestQualEACapitalDePortugal() {
		// initialize
		 conf = Configuration.newInstance()
		 def path = "data/SPARQL"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "local")
		conf.set("saskia.dbpedia.local.files", "$path/Portugal.nt")
	 	 qa = new QuestionAnalyser()
	}
	
   void testQualEACapitalDePortugal() {
  
	 // **** 5. Qual é a capital de Portugal? ***
	 String line = """
	qual	qual	DET_interr	0	S	M/F	SC>	0
	\u00e9	ser	V_fmc	PR_IND	3S	0	FMV	0
	a	o	DET_artd	0	S	F	>N	0
	capital	capital	N_ac-sign_Lciv	0	S	F	<SUBJ	0
	de	de	PRP	0	0	0	N<	0
	Portugal	Portugal	PROP_civ	0	S	M	P<	0
	?	?	PU	0	0	0	PONT	0
	""" 
				
	 SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
  	 q = new Question(sentence)	
	 assert q.questionType == QuestionType.None
	
	 qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
	 assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"     
	 
	 qa.applyRules(q, QuestionRulesPT.rulesToCaptureQuestionEntities)
	 assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
	 Condition c = q.conditions[0]
	 assert c.predicate.terms.size() == 1, "Condition predicate has ${c.predicate.terms.size()} terms."
	 assert c.predicate.terms[0].text == "capital"
	 assert c.predicate.terms[0].lemma == "capital"
	 assert c.predicate.dbpedia_ontology_property.contains("dbpedia-owl:capital"), \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"

	 assert c.operator == null, "Condition operator is not null, is ${c.operator}."
	
	 assert c.object.ne.terms.size() == 1, "Condition object has ${c.object.ne.terms.size()} terms."
	 assert c.object.haremCategory.contains("LOCAL")
	 assert c.object.dbpediaResource.contains("http://dbpedia.org/resource/Portugal"), \
		"Condition object DBpedia: ${c.object.dbpediaResource}"
	 assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Country") && 
	 c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/PopulatedPlace"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
	 assert c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Portugal") &&
	 c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:European_Union_member_states"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

	 qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
	 assert q.expectedAnswerType.categoryHAREM.contains('LOCAL')
	 assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.PROPERTY
	
	// Question2SPARQL qd = new Question2SPARQL()
	 //qd.query(q)
	 assert q.answer == ["http://dbpedia.org/resource/Lisbon"], "Question answer is ${q.answer}"
	 assert q.answerJustification.contains("http://dbpedia.org/resource/Portugal"), \
		"Question answerJustification is ${q.answerJustification}"
  }
}