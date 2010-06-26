
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
 class GeoCLEF_EN_2008_081_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2008_081_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
	//	String topic = "label:081 G7 summits in Mediterranean countries"
		String topic = "label:081 G7 summits in countries of the Mediterranean Sea"

/*
Category:Countries_of_the_Mediterranean_Sea
countries: sbj_id=1323
Mediterratean_Sea: ent_id=11741, Mediterranean:ent_id=39131
geo_id: 18049
insert into subject_ground values(0,1323,18049,'Category:Countries_of_the_Mediterranean_Sea','Country','en:Countries_of_the_Mediterranean_Sea','Manually added');
*/


		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['G7','summits','in','countries','of','the','Mediterranean','Sea']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
		assert question.nes.size() == 1
//		assert question.nes[0].terms*.text == ['G7']
//		assert question.nes[0].classification.find{it == SC.organization}
		assert question.nes[0].terms*.text == ['Mediterranean','Sea']
		assert question.nes[0].classification.find{it == SC.place_physical_watermass}

	// check subjects 

		assert !question.subject//.subjectTerms*.text == ['politicians']

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
//		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.subjectTerms*.text == ['countries']
		assert question.conditions[0].object.geoscopeTerms*.text == ['Mediterranean','Sea']
	//	assert question.conditions[1].object instanceof QueryGeoscope
	//	assert question.conditions[1].operator.op == Operator.Locator.Between
	//	assert question.conditions[1].object.ne.terms*.text == ['European']
			
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
label:081 contents:G7 contents:summits contents:countries contents:"Mediterranean Sea" woeid-index:55959718 woeid-index:23424742 woeid-index:23424740 woeid-index:23424761 woeid-index:23424843 woeid-index:26812346 woeid-index:23424802 woeid-index:23424819 woeid-index:23424825 woeid-index:23424833 woeid-index:23424852 woeid-index:23424853 woeid-index:23424873 woeid-index:23424882 woeid-index:23424897 woeid-index:23424892
woeid-index:20069817 woeid-index:23424893 woeid-index:23424945 woeid-index:23424950 woeid-index:23424956 woeid-index:23424967 woeid-index:23424969 
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

