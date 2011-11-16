
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
package renoir.eval.geoclef.pertopic.year2007

import renoir.eval.geoclef.pertopic.GeoCLEF_PerTopic_Test
import groovy.util.GroovyTestCase
import org.apache.log4j.Logger
import saskia.bin.Configuration
import renoir.obj.*
import rembrandt.gazetteers.CommonClassifications as SC
/**
 * @author Nuno Cardoso
 *
 */
 class GeoCLEF_EN_2007_055_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2007_055_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:055 deaths caused by avalanches occurring in Europe, but not in the Alps"

/*
label:056 Lakes with monsters
label:057 Whisky making in the Scottish Islands
label:058 Travel problems at major airports near to London
label:059 Meetings of the Andean Community of Nations (CAN)
label:060 Casualties in fights in Nagorno-Karabakh
label:061 Airplane crashes close to Russian cities
label:062 OSCE meetings in Eastern Europe
label:063 Water quality along coastlines of the Mediterranean Sea
label:064 Sport events in the french speaking part of Switzerland
label:065 Free elections in Africa
label:066 Economy at the Bosphorus
label:067 F1 circuits where Ayrton Senna competed in 1994
label:068 Rivers with floods
label:069 Death on the Himalaya
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
		assert question.sentence*.text == ['deaths','caused','by','avalanches','occurring','in','Europe',',','but','not','in','the','Alps']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 

		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['Europe']
		assert question.nes[0].classification.find{it == SC.place_human_division}
		assert question.nes[1].terms*.text == ['Alps']
		assert question.nes[1].classification.find{it == SC.place_physical_mountain}

	// check subjects 
	// games is grounded
		assert !question.subject//.subjectTerms*.text == ['Cities']

	// check conditions 
		assert question.conditions.size() == 2
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text == ['Europe']
		assert question.conditions[1].object instanceof QueryGeoscope
		assert question.conditions[1].operator.op == Operator.Locator.In
		assert question.conditions[1].object.ne.terms*.text == ['Alps']
			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:Cities']

	// check answers 
		assert !question.answer 
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

		println refq.toString()
		String reformulated_x = """
label:055 contents:deaths contents:caused contents:avalanches contents:occurring contents:"Europe" contents:"Alps" woeid:24865675 woeid:12577865 woeid:20069817 woeid:20069818 woeid:23424742 woeid:23424744 woeid:23424750 woeid:23424757 woeid:23424761 woeid:23424765 woeid:23424771 woeid:23424796 woeid:23424803 woeid:23424805 woeid:23424810 woeid:23424812 woeid:23424816 woeid:23424819 woeid:23424825 woeid:23424829 woeid:23424833 woeid:23424843 woeid:23424844 woeid:23424845 woeid:23424853 woeid:23424874 woeid:23424875 woeid:23424877 woeid:23424879 woeid:23424881 woeid:23424885 woeid:23424890 woeid:23424892 woeid:23424897 woeid:23424909 woeid:23424910 woeid:23424923 woeid:23424925 woeid:23424933 woeid:23424945 woeid:23424947 woeid:23424950 woeid:23424954 woeid:23424957 woeid:23424975 woeid:23424976 woeid:23424986 woeid:26812346 woeid:28289413 woeid:2353770
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

