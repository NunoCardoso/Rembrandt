
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
package renoir.test.geoclef.pertopic.year2008

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
 class GeoCLEF_EN_2008_085_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2008_085_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
//		String topic = "label:085 Nuclear tests in the South Pacific"
		String topic = "label:085 Nuclear tests in the Pacific"

/*
label:086 Most visited sights in the capital of France and its vicinity
label:087 Unemployment in the OECD countries
label:088 Portuguese immigrant communities in the world
label:089 Trade fairs in Lower Saxony
label:090 Environmental pollution in European waters
label:091 Forest fires on Spanish islands
label:092 Islamic fundamentalists in Western Europe
label:093 Attacks in Japanese subways
label:094 Demonstrations in German cities
label:095 American troops in the Persian Gulf
label:096 Economic boom in Southeast Asia
label:097 Foreign aid in Sub-Saharan Africa
label:098 Tibetan people in the Indian subcontinent
label:099 Floods in European cities
label:100 Natural disasters in the western USA
*/

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['Nuclear','tests','in','the','Pacific']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
		assert question.nes.size() == 1
//		assert question.nes[0].terms*.text == ['G7']
//		assert question.nes[0].classification.find{it == SC.organization}
		assert question.nes[0].terms*.text == ['Pacific']
		assert question.nes[0].classification.find{it == SC.place_physical_watermass}

	// check subjects 

		assert !question.subject//.subjectTerms*.text == ['politicians']

	// check conditions 
		assert question.conditions.size() == 1
/*		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.subjectTerms*.text == ['countries']
		assert question.conditions[0].object.geoscopeTerms*.text == ['Mediterranean','Sea']
*/		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text ==  ['Pacific']
			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == []

	// check answers 
		assert !question.answer 
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

//Albania,Algeria,Bosnia and Herzegovina ,Croatia,Cyprus,Egypt,France,Gibraltar,
//,Greece,Israel,Italy,Lebanon,Libya,Malta , Monaco ,Montenegro,Morocco ,
//Slovenia,Spain,Syria,Tunisia,Turkey

		println refq.toString()
		String reformulated_x = """
label:085 contents:Nuclear contents:tests contents:"Pacific" entity:Pacific_Ocean
 woeid-index:55959717
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

