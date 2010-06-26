
/** This file is part of REMBRANDT - Named Entity Recognition Software
 *  (http://xldb.di.fc.ul.pt/Rembrandt)
 *  Copyright (c) 2008-2009, Nuno Cardoso, University of Lisboa and Linguateca.
 *
 *  REMBRANDT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  REMBRANDT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with REMBRANDT. If not, see <http://www.gnu.org/licenses/>.
 */
package renoir.test

import groovy.util.GroovyTestCase
import org.apache.log4j.Logger
import saskia.bin.Configuration
import renoir.obj.*
import renoir.rules.*
import rembrandt.obj.Document
import rembrandt.bin.*
import rembrandt.tokenizer.*
import rembrandt.gazetteers.CommonClassifications as CC
/**
 * @author Nuno Cardoso
 * From here, we can generate 
 *
 */
 class QueryReformulationTest extends GroovyTestCase {
        
    Configuration conf 
    
    public QueryReformulationTest() {
	conf = Configuration.newInstance()
	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
	println "DBpedia sparql service set to http://dbpedia.org/sparql"
    }
        
    Question process(RenoirQuery rq, String lang) {

	
	// create empty question object with the sentence in it
	Question question = rq.convertToQuestion(lang)	
	
	// initialize question workers
	QuestionAnalyser qa = new QuestionAnalyser()
	QuestionEATForPT qeat = new QuestionEATForPT()
	QuestionAnswersForPT qanswer = new QuestionAnswersForPT()
	    
	// Reambrandt worker
	RembrandtCore core = Rembrandt.getCore("pt", "harem")
	Document doc= new Document()
	doc.body_sentences = [question.sentence.clone()]
	doc.indexBody()
	doc = core.releaseRembrandtOnDocument(doc)			
	question.nes = doc.bodyNEs
	
	// Rembrandt the sentence, add NEs to the Question NE list
	// good, now for the 'apply rules', we can load these NEs and use rules that have NEMatch clauses
	QuestionRules qr = new QuestionRules2PT()	    
	qa.applyRulesBrowseQuestion(question, qr.rulesToDetectQuestionType)
	qa.applyRulesBrowseQuestion(question, qr.rulesToCaptureSubjects)
	qa.applyRulesBrowseQuestion(question, qr.rulesToCaptureConditions)
	// from all captured stuff, let's decide on EAT
	question = qeat.solve(question)
	// let's solve it to answers
	question = qanswer.solve(question)
	return question
    }
    
    
   
    void dumpQuestion(Question question) {
	println question.dump()
	println "Question sentence: ${question.sentence}"
	println "Question explanation: ${question.explanation}"
	println "Question expectedAnswerType: ${question.expectedAnswerType}"
	println "Question questionType: ${question.questionType}"
	println "Question questionTypeTerms: ${question.questionTypeTerm}"
	println "Question NEs: ${question.nes}"           
	println "Question subject: ${question.subject}"           
	println "Question conditions: ${question.conditions}" 
    }
	
    void testString1() {
	
	String x = "Quem são os músicos portugueses que vivem em Leiria?"
	    
	RenoirQuery rq = RenoirQueryParser.parse(x)
	Question question = process(rq, "pt")
	//dumpQuestion(question)
     
	/** check sentences and question types */
	assert question.sentence*.text == ['Quem','são','os','músicos','portugueses','que','vivem','em','Leiria','?']
	assert question.questionType == QuestionType.Who
	assert question.questionTypeTerms*.text == ["Quem"]
     
	/** check detected NEs */
	assert question.nes.size() == 1
	//println "question nes wikipedia stuff & dbpedia stuff: "+question.nes[0].wikipediaPage+ " "+question.nes[0].dbpediaPage
	assert question.nes[0].terms*.text == ["Leiria"]
    
	/** check subjects */
	assert question.subject?.subjectTerms*.text == ["músicos"]
    
	assert question.subject?.geoscopeTerms*.text == ["portugueses"]
 	assert question.subject?.categoryWikipediaAsDBPediaResource == ["Category:Portuguese_musicians"]
	// grounded classifications
	assert question.subject?.categoryHAREM == [CC.person_individual]	
	assert question.subject?.ontologyDBpediaClass == ['http://dbpedia.org/ontology/MusicalArtist']
	
	/** check conditions */
	assert question.conditions?.size() == 1
	assert question.conditions[0].predicate.terms*.text == ["vivem","em"]
	assert question.conditions[0].predicate.dbpediaOntologyProperty*.ontologyProperty.sort() \
		== ["Person/homeTown","hometown"]
	assert question.conditions[0].object.terms*.text == ["Leiria"]
	assert question.conditions[0].object.classification?.c == ["@LOCAL"]
	
	//println question.conditions[0].object.wikipediaPage                                                           
	assert question.conditions[0].object.wikipediaPage\
	    .values().toList().flatten() == ["Leiria"] // // the Wikipeida page
	
	
        // In DBpedia3.5, there is no rdf:type that classifies Leiria, so temporarily, this test is commented              
	//assert question.conditions[0].object.dbpediaPage\
	//    .values().toList().flatten() == ['http://dbpedia.org/resource/Leiria']

	/** check EAT */
	assert question.expectedAnswerType != null
	
	assert question.expectedAnswerType.DBpediaOntologyResources == \
	       question.subject.categoryWikipediaAsDBPediaResource
	assert question.expectedAnswerType.resolvesTo == \
		ExpectedAnswerType.Type.DBpediaOntologyResource
	 
	/** check answers **/
	assert question.answer.sort() == ['http://dbpedia.org/resource/David_Fonseca']

	/** now, question object full -> reformulated query */
	ReformulatedQuery refq = QueryReformulator.reformulate(rq, question)                                  
	/* reformulated Query -> string */     
	
	String reformulated_x = "contents:Quem contents:músicos contents:portugueses "+
	"contents:vivem contents:Leiria ne-PESSOA-INDIVIDUAL-index:\"David Fonseca\" "+
	"ne-LOCAL-HUMANO-DIVISAO-index:\"Leiria\" woeid-index:742627"
	assert reformulated_x == refq.toString()
  }
   
}
