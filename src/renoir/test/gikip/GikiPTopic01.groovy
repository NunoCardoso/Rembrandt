package renoir.test.gikip

import org.junit.*
import org.apache.log4j.*
import renoir.obj.*
import rembrandt.obj.*
import renoir.rules.*
import saskia.bin.Configuration
import com.thoughtworks.xstream.*
import groovy.util.GroovyTestCase



/**
  *
  */
class GikiPTopic01Test extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
   	QuestionAnalyser qa
	Question q 
	Configuration conf
	
	public GikiPTopic01Test() {
		// initialize
		 conf = Configuration.newInstance()
		 def path = "data/gikip"
		// Force the setup to use the training stuff
		conf.set("saskia.dbpedia.mode", "dbpedia")
	//	conf.set("saskia.dbpedia.mode", "local")
		conf.set("saskia.dbpedia.local.files", "$path/Eiffel_Tower.nt, "+
		"$path/Paris.nt, $path/Paris_hack.nt")
	 	 qa = new QuestionAnalyser()

	}
	
	@Test void testGikiPTopic01() {
    
// Tenho de mudar o t�tulo da obra para mai�sculas, para o Rembrandt apanh�-la
    List palavras = [
"que 		[que] KS @SUB", 
"r�pidos 		[r�pido] N M P @SUBJ>",
"aparecem 		[aparecer] <fmc> V PR 3P IND VFIN @FS-STA",
"em 		[em] <sam-> PRP @<ADVL",
"os 		[o] <-sam> DET M P @>N",
"filmes 		[filme] N M P @P<",
"adaptados 		[adaptar] V PCP M P @N<",
"de 		[de] <sam-> PRP @N<",
"a 		[o] <-sam> DET F S @>N",
"obra 		[obra] N F S @P<",
"O=�ltimo=dos=Moicanos 		[O=�ltimo=dos=Moicanos] <<< PROP F S @N< <<<"
]

//	SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(
//			line.toString().trim(), SentenceWithPoS.FROM_PALAVRAS_PoS)
	SentenceWithPoS sentence = SentenceWithPoS.tokenizePoS(palavras, SentenceWithPoS.FROM_PALAVRAS_PoS)
	q = new Question(sentence)	
	 assert q.questionType == QuestionType.None
			
	qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
	assert q.questionType == QuestionType.Which, "Question type is ${q.questionType}"     
	
	qa.matchPolicy = QuestionAnalyser.RETURN_ON_FIRST_RULE_MATCH
	qa.applyRules(q, AdvancedQuestionRulesPT.GikiPrules)
	
	assert q.subject.terms*.text == ["r�pidos"], "It should have a subject, it has ${q.subject}"
	assert q.subject.ontologyDBpedia == ['http://dbpedia.org/ontology/River'], \
		"It should have a q.subject.ontologyDBpedia, it has ${q.subject.ontologyDBpedia}"
		
	// local only gives a generic category	
	 assert q.subject.categoryHAREM == ['LOCAL'], "is ${q.subject.categoryHAREM} instead"
	 assert q.subject.typeHAREM == ['FISICO'], "is ${q.subject.typeHAREM} instead"
	 assert q.subject.subtypeHAREM == ['AGUACURSO'], "is ${q.subject.subtypeHAREM} instead"

	 assert q.conditions.size() == 1, "It should have one condition, it has ${q.conditions.size()}"
	 Condition c = q.conditions[0]
	
	 assert c.predicate.terms.size() == 1
	 assert c.predicate.terms[0].text == "aparecem"
	 assert c.predicate.terms[0].lemma == "aparecer"
	 assert c.predicate.dbpedia_ontology_property.contains("dbpedia2:location"), \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"
	 assert c.predicate.dbpedia_ontology_property.contains("dbpedia2:locationCity"), \
	    "Predicate dbpedia_ontology_property is ${c.predicate.dbpedia_ontology_property}"

	 assert c.object.ne.terms.size() == 4, "Condition object has ${c.object.ne.terms.size()} terms."
	 assert c.object.haremCategory.contains("OBRA")
	 assert c.object.dbpediaResource.contains("http://dbpedia.org/resource/The_Last_of_the_Mohicans_%281992_film%29"), \
		"Condition object DBpedia: ${c.object.dbpediaResource}"
	 assert c.object.dbpediaOntologyClass.contains("http://dbpedia.org/ontology/Film"), \
		"Condition dbpediaOntologyClass: ${c.object.dbpediaOntologyClass}"
	 assert c.object.dbpediaWikipediaCategory.contains("http://dbpedia.org/resource/Category:Films_directed_by_Michael_Mann"), \
		"Condition dbpediaWikipediaCategory: ${c.object.dbpediaWikipediaCategory}"

	 qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT)
	 // HAREM classification
	 assert q.expectedAnswerType.categoryHAREM.contains('LOCAL')
	 assert q.expectedAnswerType.typeHAREM.contains('FISICO')
	 assert q.expectedAnswerType.subtypeHAREM.contains('AGUACURSO')
	// Ontology classes
	 assert q.expectedAnswerType.ontologyClass.contains("http://dbpedia.org/ontology/River"), \
		"Is ${q.expectedAnswerType.ontologyClass} instead."
	// No Wikipedia cats. 
	 assert q.expectedAnswerType.categoryWikipedia == [], "Is ${q.expectedAnswerType.categoryWikipedia} instead."
	 assert q.expectedAnswerType.resolvesTo == ExpectedAnswerType.Type.ONTOLOGYCLASS, "Is ${q.expectedAnswerType.resolvesTo} instead."
	
	
//	 Question2SPARQL qd = new Question2SPARQL()
//	 qd.query(q)
	
	// it's going to fail. No river is located on the movie "Last of the Mohicans"
	 assert q.answer == []
	 assert q.answerJustification == []

	// Plan B... finally!
	

	
	}
}
  