
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
 class GeoCLEF_EN_2008_076_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2008_076_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
	//	String topic = "label:076 Riots in South American prisons"
		String topic = "label:076 Riots in prisons in South America"

/*
label:076 Riots in South American prisons
label:077 Nobel prize winners from Northern European countries
label:078 Sport events in the Sahara
label:079 Invasion of Eastern Timor's capital by Indonesia
label:080 Politicians in exile in Germany
label:081 G7 summits in Mediterranean countries
label:082 Agriculture in the Iberian Peninsula
label:083 Demonstrations against terrorism in Northern Africa
label:084 Bombings in Northern Ireland
label:085 Nuclear tests in the South Pacific
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
		assert question.sentence*.text == ['Riots','in','prisons','in','South','America']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 

		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['South','America']
		assert question.nes[0].classification.find{it == SC.place_human_division}
	//	assert question.nes[1].terms*.text == ['European']
	//	assert question.nes[1].classification.find{it == SC.place_human_division}

	// check subjects 
	// games is grounded
		assert !question.subject//.subjectTerms*.text == ['Cities']

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text == ['South','America']
	//	assert question.conditions[1].object instanceof QueryGeoscope
	//	assert question.conditions[1].operator.op == Operator.Locator.Between
	//	assert question.conditions[1].object.ne.terms*.text == ['European']
			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:Cities']

	// check answers 
		assert !question.answer 
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

		println refq.toString()
		String reformulated_x = """
label:076 contents:Riots contents:prisons contents:"South America" woeid-index:24865673 woeid-index:23424747 woeid-index:23424762 woeid-index:23424768 woeid-index:23424782 woeid-index:23424787 woeid-index:23424801 woeid-index:23424811 woeid-index:23424814 woeid-index:23424836 woeid-index:23424913 woeid-index:23424917 woeid-index:23424919 woeid-index:23424955 woeid-index:23424979 woeid-index:23424982
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

