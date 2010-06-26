
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
 * Test ALL RULES in EN
 */
 class QuestionRulesForPTTest extends GroovyTestCase{
        
    Configuration conf 
    static Logger log = Logger.getLogger("JUnitTest")
	QuestionAnalyser qa
	QuestionRulesForPT qr
	RembrandtCore core 
	String lang = "pt"
    
	public QuestionRulesForPTTest() {
		conf = Configuration.newInstance()
	//	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url", "null")
		
		// initialize question workers
		qa = new QuestionAnalyser()
	
		// Rembrandt the sentence, add NEs to the Question NE list
		// good, now for the 'apply rules', we can load these NEs and use rules that have NEMatch clauses
		qr = new QuestionRulesForPT()
		
			// Reambrandt worker
		core = Rembrandt.getCore("pt", "harem")

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
    void test_rulesToCaptureSubjects_2_2() {
	
		Question question = process("Sindicatos na Europa")
		
		// basic assertion
		assert question.sentence*.text == ['Sindicatos','na','Europa']
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Europa']

		// Assert subject
		assert question.subject != null
		assert question.subject.subjectTerms*.text == ["Sindicatos"]
		assert question.subject.geoscopeTerms*.text == ["Europa"]
		
		assert question.subject.subjects*.sbj_subject == ["en:[Trade][Unions];pt:[Sindicatos]"]
		assert question.subject.geoscopes*.geo_name.contains("pt:Europa;en:Europe")
		// and, since it was expanded, test a country
		assert question.subject.geoscopes*.geo_name.contains("pt:Albânia;en:Albania")
		assert question.subject.subjectgrounds*.sgr_dbpedia_resource.contains("Category:Trade_unions_in_Albania")
	
		// assert conditions
		assert !question.conditions
	//	assert question.conditions[0].object.ne.terms*.text == ["Europa"]
	//	assert question.conditions[0].object.ne.classification*.c == ["@LOCAL"]
	
	}
	
// 3.1.1	
// writers in Portugal
// wildgooses in Portugal
// wildgooses in Portugal and Spain
	void test_rulesToCaptureConditions_3_1_1() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("escritores em Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal']

		// Assert subject
		assert question.subject != null
		assert question.subject.subjectTerms*.text == ["escritores"]
		assert question.subject.geoscopeTerms*.text == ["Portugal"]
		
		assert question.subject.subjects*.sbj_subject == ["en:[writers];pt:[escritores]"]
		assert question.subject.geoscopes*.geo_name.contains("pt:Portugal;en:Portugal")
		assert question.subject.subjectgrounds*.sgr_dbpedia_resource.contains("Category:Portuguese_writers")
	
		// assert conditions
		assert !question.conditions 
		
		/*** here, since wildgooses does NOT match subject, 'in Portugal' is captured as condition */
		question = process("gambozinos em Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal']

		// Assert subject
		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Portugal']
			
		question = process("gambozinos em Portugal e Espanha")
		
		// basic assertion
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['Portugal']
		assert question.nes[1].terms*.text == ['Espanha']

		// Assert subject
		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 2
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[1].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Portugal']
		assert question.conditions[1].object.ne.terms*.text == ['Espanha']
	}

// 3.1.2
// wildgooses in Portuguese islands 
	void test_rulesToCaptureConditions_3_1_2() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("gambozinos nas ilhas portuguesas")
		
		// basic assertion
		assert question.nes.size() == 0
		// classified as an ISLAND because of external evidence. But don't worry, it does not have DBpedia ground.

		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.geoscopeTerms*.text == ['portuguesas']
		assert question.conditions[0].object.subjectTerms*.text == ['ilhas']
	}
		

// 3.1.3
// wildgooses in islands of Portugal
	void test_rulesToCaptureConditions_3_1_3() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("gambozinos nas ilhas de Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal'] 
		// classified as an ISLAND because of external evidence. But don't worry, it does not have DBpedia ground.

		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.geoscopeTerms*.text == ['Portugal']
		assert question.conditions[0].object.subjectTerms*.text == ['ilhas']
	}
	
// 3.1.4	
// wildgooses near Portugal
// wildgooses near Portugal and Spain
	void test_rulesToCaptureConditions_3_1_4() {

		Question question = process("gambozinos perto de Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal']

		// Assert subject
		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Portugal']
			
		question = process("gambozinos perto de Portugal e de Espanha")
		
		// basic assertion
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['Portugal']
		assert question.nes[1].terms*.text == ['Espanha']

		// Assert subject
		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 2
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[1].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Portugal']
		assert question.conditions[1].object.ne.terms*.text == ['Espanha']
	}
	
// 3.1.5
// wildgooses near Portuguese islands 
	void test_rulesToCaptureConditions_3_1_5() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("gambozinos perto das ilhas portuguesas")
		
		// basic assertion
		assert question.nes.size() == 0
		// classified as an ISLAND because of external evidence. But don't worry, it does not have DBpedia ground.

		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.geoscopeTerms*.text == ['portuguesas']
		assert question.conditions[0].object.subjectTerms*.text == ['ilhas']
	}
		

// 3.1.6
// wildgooses near islands of Portugal
	void test_rulesToCaptureConditions_3_1_6() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("gambozinos perto das ilhas de Portugal")
		
		// basic assertion
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Portugal'] 
		// classified as an ISLAND because of external evidence. But don't worry, it does not have DBpedia ground.

		assert !question.subject
		
		// assert conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.geoscopeTerms*.text == ['Portugal']
		assert question.conditions[0].object.subjectTerms*.text == ['ilhas']
	}

// 3.1.7	
// wildgooses in the north of Portugal
	void test_rulesToCaptureConditions_3_1_7() {
		
		/*** this one will match as a subject, so there will be NO conditions */
		Question question = process("gambozinos no norte de Portugal")
		
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
		Question question = process("gambozinos a menos de 100 km de Portugal")
		
		// basic assertion
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['menos','de','100','km'] 
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


// músicos portugueses na Europa
// músicos portugueses perto da Europa
// músicos portugueses no oeste da Europa
// músicos portugueses perto do oeste da Europa
// músicos portugueses nas ilhas da Europa
// músicos portugueses perto das ilhas da Europa
// músicos portugueses nascidos na Europa 
// músicos portugueses nascidos perto da Europa 
// músicos portugueses nascidos perto das ilhas da Europa 

   
}
