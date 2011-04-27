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
class OQueEJoseSaramagoTest extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	QuestionAnalyser qa
	Question q
	Configuration conf

	public OQueEJoseSaramagoTest() {
		// initialize
		conf = Configuration.newInstance()
		def path = "data/SPARQL"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "local")
		conf.set("saskia.dbpedia.local.files",
				"$path/Jose_Saramago.nt")
		qa = new QuestionAnalyser()
	}

	void testOQueEJoseSaramago() {

		// **** 11. O que é Jos\u00e9 Saramago? ***
		String line = """
	o	o	DET_artd	0	S	M	>N	0
	que	que	SPEC_interr	0	S	M	SC>	0
	\u00e9	ser	V_fmc	PR_IND	3S	0	FMV	0
	Jos\u00e9	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SUBJ	0
	Saramago	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SUBJ	0
	?	?	PU	0	0	0	PONT	0
	"""

		SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		q = new Question(sentence)
		assert q.questionType == QuestionType.None

		qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
		assert q.questionType == QuestionType.What, "Question type is ${q.questionType}"

		qa.applyRules(q, QuestionRulesPT.rulesToCaptureQuestionEntities)
		assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
		Condition c = q.conditions[0]
		assert c.predicate == null
		assert c.operator == null, "Condition operator is not null, is ${c.operator}."

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
		assert q.expectedAnswerType.categoryHAREM == []
		assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.DEFINITION

		//	 Question2SPARQL qd = new Question2SPARQL()
		//	 qd.query(q)
		assert q.answer.contains("http://dbpedia.org/ontology/Person"), \
		"Question answer is ${q.answer}"
		assert q.answer.contains("http://dbpedia.org/resource/Category:Portuguese_Nobel_laureates"), \
		"Question answer is ${q.answer}"
		assert q.answerJustification.contains("http://dbpedia.org/resource/Jos%C3%A9_Saramago"), \
		"Question answerJustification is ${q.answerJustification}"
	}
}