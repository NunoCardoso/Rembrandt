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
class TestQuantosAnosTemJoseSaramago extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
   	QuestionAnalyser qa
	Question q 
	Configuration conf
	
	public TestQuantosAnosTemJoseSaramago() {
		// initialize
		 conf = Configuration.newInstance()
		 def path = "data/SPARQL"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "local")
		conf.set("saskia.dbpedia.local.files", 
		  "$path/Jose_Saramago.nt")
	 	 qa = new QuestionAnalyser()
	}
    
   void testQuantosAnosTemJoseSaramago() {
  
	 // *** 2. Quantos anos tem José Saramago? ***
	 String line = """quantos	quanto	DET_interr_quant	0	P	M	>N	0
				anos	ano	N_unit_dur_temp_per	0	P	M	SUBJ>	0
				tem	ter	V_fmc	PR_IND	3S	0	FMV	0
				Jos\u00e9	Jos\u00e9=Saramago	PROP_genre	0	S	M	<ACC	0
				Saramago	Jos\u00e9=Saramago	PROP_genre	0	S	M	<ACC	0
				?	?	PU	0	0	0	PONT	0""" 
				
	 SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
  	 q = new Question(sentence)	
	 assert q.questionType == QuestionType.None
	
	 qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
	 assert q.questionType == QuestionType.HowMuch, "Question type is ${q.questionType}"     
	 
	 qa.applyRules(q, QuestionRulesPT.rulesToCaptureQuestionEntities)
	 assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
	 Condition c = q.conditions[0]
	 assert c.predicate.terms.size() == 1, "Condition predicate has ${c.predicate.terms.size()} terms."
	 assert c.predicate.terms[0].text == "anos"
	 assert c.predicate.terms[0].lemma == "ano"
	 assert c.predicate.dbpedia_ontology_property == ["age"], "Dbpedia_ontology_property = ${q.conditions[0].predicate.dbpedia_ontology_property}"

	 assert c.operator.terms.size() == 1, "Condition operator has ${q.conditions[0].operator.terms.size()} terms."
	 assert c.operator.terms[0].text == "tem"
	 assert c.operator.terms[0].lemma == "ter"
	 assert c.operator.predicateOperator == PredicateOperator.Has
	
	 assert c.object.ne.terms.size() == 2, "Condition object has ${c.object.ne.terms.size()} terms."
	 assert c.object.haremCategory.contains("PESSOA")
	 assert c.object.dbpediaResource.contains("http://dbpedia.org/resource/Jos%C3%A9_Saramago"), \
		"Condition object DBpedia: ${c.object.dbpediaResource}"
	 assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Writer") && 
	 c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Person"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
	 assert c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Portuguese_novelists") &&
	 c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Portuguese_Nobel_laureates"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

	 qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
	 assert q.expectedAnswerType.categoryHAREM.contains('TEMPO')
	 assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.PROPERTY
	
	// Question2SPARQL qd = new Question2SPARQL()
	// qd.query(q)
	 def saramagoAge = (new GregorianCalendar().get(Calendar.YEAR) - 1922)
	 assert q.answer == [saramagoAge], "Question answer is ${q.answer}"
	 assert q.answerJustification.contains("http://dbpedia.org/resource/Jos%C3%A9_Saramago"), \
		"Question answerJustification is ${q.answerJustification}"
		
   }
}