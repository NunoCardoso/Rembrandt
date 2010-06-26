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
class TestQuantosHabitantesTemLisboa extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
   	QuestionAnalyser qa
	Question q 
	Configuration conf
	
	public TestQuantosHabitantesTemLisboa() {
		// initialize
		 conf = Configuration.newInstance()
		 def path = "data/SPARQL"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "local")
		conf.set("saskia.dbpedia.local.files", 
		  "$path/LisbonFromDBpediaPT.nt, $path/LisbonFromDBpediaService.nt")
	 	 qa = new QuestionAnalyser()
	}
    
  void testQuantosHabitantesTemLisboa() {

	 // **** 1. Quantos habitantes tem Lisboa? ***
	 String line = """quantos	quanto	DET_interr_quant	0	P	M	>N	0
				habitantes	habitante	N_Hnat	0	P	M	SUBJ>	0
				tem	ter	V_fmc	PR_IND	3S	0	FMV	0
				Lisboa	Lisboa	PROP_civ	0	S	F	<ACC	0
				?	?	PU	0	0	0	PONT	0"""
	
	 List palavras_line = ["quantos 		[quanto] <quant> <interr> DET M P @>N",
	"habitantes 		[habitante] N M P @SUBJ>",
	"tem 		[ter] <fmc> V PR 3S IND VFIN @FS-QUE",
	"Lisboa 		[Lisboa] PROP F S @<ACC",
	"?"]
			
	 SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
  	 q = new Question(sentence)	
	 assert q.questionType == QuestionType.None
	
	 qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
	 assert q.questionType == QuestionType.HowMuch, "Question type is ${q.questionType}"
	
     qa.applyRules(q, QuestionRulesPT.rulesToCaptureQuestionEntities)
	 assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
	 Condition c = q.conditions[0]
	 assert c.predicate.terms.size() == 1, "Condition predicate has ${c.predicate.terms.size()} terms."
	 assert c.predicate.terms[0].text == "habitantes"
	 assert c.predicate.terms[0].lemma == "habitante"
	 assert c.predicate.dbpedia_ontology_property.contains("dbpedia2:populationTotal"), \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"
	 assert c.predicate.dbpedia_local_property == ["dbpedia2:popula_percent_E3_percent_A7_percent_E3_percent_A3o"], \
		"Predicate dbpedia_local_property is ${c.predicate.dbpedia_local_property}"

	 assert c.operator.terms.size() == 1, "Condition operator has ${c.operator.terms.size()} terms."
	 assert c.operator.terms[0].text == "tem"
	 assert c.operator.terms[0].lemma == "ter"
	 assert c.operator.predicateOperator == PredicateOperator.Has
	
	 assert c.object.ne.terms.size() == 1, "Condition object has ${c.object.ne.terms.size()} terms."
	 assert c.object.haremCategory == "LOCAL"
	 assert c.object.dbpediaResource == "http://dbpedia.org/resource/Lisbon", "Condition dbpediaResource: ${c.object.dbpediaResource}"

	 assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Municipality") && 
	 c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/PopulatedPlace"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
	 assert c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Cities_in_Portugal") &&
	 c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Capitals_in_Europe"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

	 qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
	 assert q.expectedAnswerType.categoryHAREM.contains('NUMERO')
	 assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.PROPERTY
	
	// Question2SPARQL qd = new Question2SPARQL()
	// qd.query(q)
	 assert q.answer.sort().getAt(0)?.startsWith("509 751"), "Question answer is ${q.answer}"
	 assert q.answer.sort().getAt(1)?.startsWith("564,477"), "Question answer is ${q.answer}"
	 assert q.answerJustification.contains("http://dbpedia.org/resource/Lisbon") , "Question answerJustification is ${q.answerJustification}"
   }
}
