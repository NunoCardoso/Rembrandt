
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
package renoir.test.geoclef.pertopic

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
 *
 */
 class GeoCLEF_PerTopic_Test {
        
    Configuration conf 
    
    public GeoCLEF_PerTopic_Test(Configuration conf_ = null) {	
	conf = (conf_ ? conf_ : Configuration.newInstance())
	//conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
	//println "DBpedia sparql service set to http://dbpedia.org/sparql"
    }
        
    Question process(RenoirQuery rq, String lang) {
	
	// create empty question object with the sentence in it
	Question question = rq.convertToQuestion(lang)	
	
	// initialize question workers
	QuestionAnalyser qa = new QuestionAnalyser()
	QuestionEAT qeat = Class.forName("renoir.rules.QuestionEATFor"+lang.toUpperCase()).newInstance()
	QuestionAnswers qanswer = Class.forName("renoir.rules.QuestionAnswersFor"+lang.toUpperCase()).newInstance()
	QuestionRules qr = Class.forName("renoir.rules.QuestionRulesFor"+lang.toUpperCase()).newInstance()
	
	// Reambrandt worker
	RembrandtCore core = Rembrandt.getCore(lang, "harem")
	Document doc= new Document()
	doc.body_sentences = [question.sentence.clone()]
	doc.indexBody()
	doc = core.releaseRembrandtOnDocument(doc)			
	question.nes = doc.bodyNEs
	
	// Rembrandt the sentence, add NEs to the Question NE list
	// good, now for the 'apply rules', we can load these NEs and use rules that have NEMatch clauses
	    
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
	
   
}
