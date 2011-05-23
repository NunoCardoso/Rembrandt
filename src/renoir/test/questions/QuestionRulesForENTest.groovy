
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
package renoir.test.questions

import groovy.util.GroovyTestCase
import org.apache.log4j.Logger
import saskia.bin.Configuration
import saskia.dbpedia.*
import renoir.obj.*
import renoir.rules.*
import rembrandt.obj.Document
import rembrandt.bin.*
import rembrandt.tokenizer.*
import rembrandt.gazetteers.CommonClassifications as CC
/**
 * @author Nuno Cardoso
 * Test ALL RULES in EN
 */
 class QuestionRulesForENTest extends GroovyTestCase{
        
    Configuration conf 
    static Logger log = Logger.getLogger("JUnitTest")
	QuestionAnalyser qa
	QuestionRulesForEN qr
	RembrandtCore core 
	String lang = "en"
    
	public QuestionRulesForENTest() {
		conf = Configuration.newInstance()
	//	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url", "null")
		
		// initialize question workers
		qa = new QuestionAnalyser()
	
		// Rembrandt the sentence, add NEs to the Question NE list
		// good, now for the 'apply rules', we can load these NEs and use rules that have NEMatch clauses
		qr = new QuestionRulesForEN()
		
			// Reambrandt worker
		core = Rembrandt.getCore("en", "harem")

    }
   
    Question process(String query) {
		RenoirQuery rq = RenoirQueryParser.parse(query)
		Question question = rq.convertToQuestion(lang)	
		
		Document doc= new Document()
		doc.body_sentences = [question.sentence.clone()]
		doc.indexBody()
		doc = core.releaseRembrandtOnDocument(doc)			
		question.nes = doc.bodyNEs
	
		qa.applyRulesBrowseQuestion(question, qr.rulesToDetectQuestionType)
		qa.applyRulesBrowseQuestion(question, qr.rulesToCaptureSubjects)
		qa.applyRulesBrowseQuestion(question, qr.rulesToCaptureConditions)
		return question
	}
	
	// rules to capture subjects.
    void notest_rulesToCaptureSubjects_2_2() {
	
		Question question = process("Trade unions in Europe")
		
		// basic assertion
		assert question.sentence*.text == ['Trade','unions','in','Europe']
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Europe']

		// Assert subject
		assert question.subject != null
		assert question.subject.subjectTerms*.text == ["Trade","unions"]
		assert question.subject.geoscopeTerms*.text == ["Europe"]
		
		assert question.subject.subjects*.sbj_subject == ["en:[Trade][Unions];pt:[Sindicatos]"]
		assert question.subject.geoscopes*.geo_name.contains("pt:Europa;en:Europe")
		// and, since it was expanded, test a country
		assert question.subject.geoscopes*.geo_name.contains("pt:Albânia;en:Albania")
		assert question.subject.subjectgrounds*.sgr_dbpedia_resource.contains("Category:Trade_unions_of_Albania")
	
		// assert conditions
		assert question.conditions != null
		assert question.conditions[0].object.ne.terms*.text == ["Europe"]
		assert question.conditions[0].object.ne.classification*.c == ["@LOCAL"]
	
	}
	
// 3.1.1	
// writers in Portugal
// wildgooses in Portugal
// wildgooses in Portugal and Spain
	void test_rulesToCaptureConditions_3_1_1() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("writers in Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal']

		// Assert subject
		assert question.subject != null
		assert question.subject.subjectTerms*.text == ["writers"]
		assert question.subject.geoscopeTerms*.text == ["Portugal"]
		
		assert question.subject.subjects*.sbj_subject == ["en:[writers];pt:[escritores]"]
		assert question.subject.geoscopes*.geo_name.contains("pt:Portugal;en:Portugal")
		assert question.subject.subjectgrounds*.sgr_dbpedia_resource.contains("Category:Portuguese_writers")
	
		// assert conditions
		assert !question.conditions 
		
		/*** here, since wildgooses does NOT match subject, 'in Portugal' is captured as condition */
		question = process("wildgooses in Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal']

		// Assert subject
		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Portugal']
			
		question = process("wildgooses in Portugal and Spain")
		
		// basic assertion
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['Portugal']
		assert question.nes[1].terms*.text == ['Spain']

		// Assert subject
		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 2
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[1].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Portugal']
		assert question.conditions[1].object.ne.terms*.text == ['Spain']
	}

// 3.1.2
// wildgooses in Portuguese islands 
	void test_rulesToCaptureConditions_3_1_2() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("wildgooses in Portuguese islands")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portuguese'] 
		// classified as an ISLAND because of external evidence. But don't worry, it does not have DBpedia ground.

//		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.geoscopeTerms*.text == ['Portuguese']
		assert question.conditions[0].object.subjectTerms*.text == ['islands']
	}
		

// 3.1.3
// wildgooses in islands of Portugal
	void test_rulesToCaptureConditions_3_1_3() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("wildgooses in islands of Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal'] 
		// classified as an ISLAND because of external evidence. But don't worry, it does not have DBpedia ground.

		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.geoscopeTerms*.text == ['Portugal']
		assert question.conditions[0].object.subjectTerms*.text == ['islands']
	}
	
// 3.1.4	
// wildgooses near Portugal
// wildgooses near Portugal and Spain
	void test_rulesToCaptureConditions_3_1_4() {

		Question question = process("wildgooses near Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal']

		// Assert subject
		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Portugal']
			
		question = process("wildgooses near Portugal and Spain")
		
		// basic assertion
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['Portugal']
		assert question.nes[1].terms*.text == ['Spain']

		// Assert subject
		//assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 2
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[1].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Portugal']
		assert question.conditions[1].object.ne.terms*.text == ['Spain']
	}
	
// 3.1.5
// wildgooses near Portuguese islands 
	void test_rulesToCaptureConditions_3_1_5() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("wildgooses near Portuguese islands")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portuguese'] 
		// classified as an ISLAND because of external evidence. But don't worry, it does not have DBpedia ground.

		//assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.geoscopeTerms*.text == ['Portuguese']
		assert question.conditions[0].object.subjectTerms*.text == ['islands']
	}
		

// 3.1.6
// wildgooses near islands of Portugal
	void test_rulesToCaptureConditions_3_1_6() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("wildgooses near islands of Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal'] 
		// classified as an ISLAND because of external evidence. But don't worry, it does not have DBpedia ground.

		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.geoscopeTerms*.text == ['Portugal']
		assert question.conditions[0].object.subjectTerms*.text == ['islands']
	}

// 3.1.7	
// wildgooses in the north of Portugal
	void test_rulesToCaptureConditions_3_1_7() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("wildgooses in the north of Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal'] 
		// classified as an ISLAND because of external evidence. But don't worry, it does not have DBpedia ground.

		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne,terms*.text == ['Portugal']
		assert question.conditions[0].operator.op == Operator.Locator.North
	}

// 3.1.8	
// wildgooses within 100 km of Portugal
	void test_rulesToCaptureConditions_3_1_8() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("wildgooses within 100 km of Portugal")
		
		// basic assertion
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['100','km'] 
		assert question.nes[1].terms*.text == ['Portugal'] 
		// classified as an ISLAND because of external evidence. But don't worry, it does not have DBpedia ground.

		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].predicate instanceof Predicate
		assert question.conditions[0].predicate.dbpediaOntologyProperty*.ontologyProperty == ["location"]
		
		assert question.conditions[0].operator.op == Operator.Locator.Around
		assert question.conditions[0].operator.amount == (double)100
		assert question.conditions[0].operator.unit == "KM"
		
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne,terms*.text == ['Portugal']

		assert !question.subject
		
	}


// 3.2.1
// writers born {in {something}}
// writers born {near {something}}
// writers born {in {something}}

   

   
}
