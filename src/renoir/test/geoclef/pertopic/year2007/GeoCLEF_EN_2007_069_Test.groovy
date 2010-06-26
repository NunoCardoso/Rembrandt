
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
package renoir.test.geoclef.pertopic.year2007

import renoir.test.geoclef.pertopic.GeoCLEF_PerTopic_Test
import groovy.util.GroovyTestCase
import org.apache.log4j.Logger
import saskia.bin.Configuration
import renoir.obj.*
import rembrandt.gazetteers.CommonClassifications as SC
/**
 * @author Nuno Cardoso
 *
 */
 class GeoCLEF_EN_2007_069_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2007_069_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:069 death in the Himalaya"
/*

label:070 Tourist attractions in northern Italy
label:071 Social problems in greater Lisbon
label:072 Beaches with sharks
label:073 Events at St. Paul's Cathedral
label:074 Ship traffic around the Portuguese islands
label:075 Violation of human rights in Burma
*/
		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['death','in','the','Himalaya']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

// http://dbpedia.org/page/Bosphorus não tem classe, e QWikipédia não ajuda


	// check detected NEs 
// F1 returns EM only, but it is grounded:  title=Formula One.
		assert question.nes.size() == 1
	//	assert question.nes[0].terms*.text == ['F1']
	//	assert question.nes[0].classification.find{it == SC.abstraction_discipline}
		assert question.nes[0].terms*.text == ['Himalaya']
		assert question.nes[0].classification.find{it == SC.place_physical_mountain}
//		assert question.nes[1].terms*.text == ['1994']
//		assert question.nes[1].classification.find{it == SC.person_individual}

	// check subjects 
	//  cities is also captu
		assert !question.subject//.subjectTerms*.text == ['Rivers']

	// check conditions 
		assert question.conditions.size() == 1
/*		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.subjectTerms*.text == ['cities']
		assert question.conditions[0].object.geoscopeTerms*.text == ['Russia']
		assert question.conditions[0].operator.op == Operator.Locator.Near
*/
		
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text == ['Himalaya']
		
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:casualties']

	// check answers 
		assert !question.answer 
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

// É melhor que não tenha woeid, ele aponta para uma cidade nos EUA!"
		println refq.toString()
		String reformulated_x = """
label:069 contents:death contents:"Himalaya" entity:Himalayas
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

