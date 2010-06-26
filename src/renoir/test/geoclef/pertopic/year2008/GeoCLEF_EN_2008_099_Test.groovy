
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
 class GeoCLEF_EN_2008_099_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2008_099_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:099 floods in cities of Europe"
/*

label:100 Natural disasters in the western USA
*/

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['floods','in','cities','of','Europe']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Europe']
		assert question.nes[0].classification.find{it == SC.place_human_division}
//		assert question.nes[0].terms*.text == ['Europe']
//		assert question.nes[0].classification.find{it == SC.place_human_division}

	// check subjects 

		assert !question.subject//.subjectTerms*.text == ['cities']

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
	//	assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.subjectTerms*.text == ['cities']
		assert question.conditions[0].object.geoscopeTerms*.text == ['Europe']
	
/*		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text ==  ['Africa']
*/			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:Cities']

	// check answers 
		assert !question.answer 
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

//Albania,Algeria,Bosnia and Herzegovina ,Croatia,Cyprus,Egypt,France,Gibraltar,
//,Greece,Israel,Italy,Lebanon,Libya,Malta , Monaco ,Montenegro,Morocco ,
//Slovenia,Spain,Syria,Tunisia,Turkey

		println refq.toString()
		String reformulated_x = """
label:099 contents:floods contents:cities contents:"Europe" woeid-index:24865675 woeid-index:12577865 woeid-index:20069817 woeid-index:20069818 woeid-index:23424742 woeid-index:23424744 woeid-index:23424750 woeid-index:23424757 woeid-index:23424761 woeid-index:23424765 woeid-index:23424771 woeid-index:23424796 woeid-index:23424803 woeid-index:23424805 woeid-index:23424810 woeid-index:23424812 woeid-index:23424816 woeid-index:23424819 woeid-index:23424825 woeid-index:23424829 woeid-index:23424833 woeid-index:23424843 woeid-index:23424844 woeid-index:23424845 woeid-index:23424853 woeid-index:23424874 woeid-index:23424875 woeid-index:23424877 woeid-index:23424879 woeid-index:23424881 woeid-index:23424885 woeid-index:23424890 woeid-index:23424892 woeid-index:23424897 woeid-index:23424909 woeid-index:23424910 woeid-index:23424923 woeid-index:23424925 woeid-index:23424933 woeid-index:23424945 woeid-index:23424947 woeid-index:23424950 woeid-index:23424954 woeid-index:23424957 woeid-index:23424975 woeid-index:23424976 woeid-index:23424986 woeid-index:26812346 woeid-index:28289413"""

	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

