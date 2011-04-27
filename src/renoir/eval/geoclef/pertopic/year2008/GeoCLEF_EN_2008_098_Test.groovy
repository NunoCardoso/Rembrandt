
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
package renoir.eval.geoclef.pertopic.year2008

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
 class GeoCLEF_EN_2008_098_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2008_098_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:098 Tibetan people in the Indian subcontinent"
/*
label:099 Floods in European cities
label:100 Natural disasters in the western USA
*/

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['Tibetan','people','in','the','Indian','subcontinent']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Tibetan']
		assert question.nes[0].classification.find{it == SC.person_people}
//		assert question.nes[0].terms*.text == ['Europe']
//		assert question.nes[0].classification.find{it == SC.place_human_division}

	// check subjects 

		assert question.subject.subjectTerms*.text == ['people']

	// check conditions 
		assert question.conditions.size() == 0
/*		assert question.conditions[0].object instanceof Subject
	//	assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.subjectTerms*.text == ['cities']
		assert question.conditions[0].object.geoscopeTerms*.text == ['German']
*/	
/*		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text ==  ['Africa']
*/			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert question.expectedAnswerTypes[0].DBpediaOntologyResources == []//'Category:Cities']

	// check answers 
		assert !question.answer 
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

//Albania,Algeria,Bosnia and Herzegovina ,Croatia,Cyprus,Egypt,France,Gibraltar,
//,Greece,Israel,Italy,Lebanon,Libya,Malta , Monaco ,Montenegro,Morocco ,
//Slovenia,Spain,Syria,Tunisia,Turkey

		println refq.toString()
		String reformulated_x = """
label:096 contents:economic contents:boom contents:"Southeast Asia" woeid:28289414 woeid:23424763 woeid:23424773 woeid:23424776 woeid:23424784 woeid:23424846 woeid:23424869 woeid:23424872 woeid:23424901 woeid:23424921 woeid:23424934 woeid:23424948 woeid:23424960 woeid:23424968 woeid:23424984 woeid:23424763 woeid:23424773 woeid:23424776 woeid:23424784 woeid:23424846 woeid:23424869 woeid:23424872 woeid:23424901 woeid:23424921 woeid:23424934 woeid:23424948 woeid:23424960 woeid:23424968 woeid:23424984
"""

	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

