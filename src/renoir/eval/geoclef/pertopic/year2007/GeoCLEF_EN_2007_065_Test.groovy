
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
 class GeoCLEF_EN_2007_065_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2007_065_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:065 free elections in Africa"
/*
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
		assert question.sentence*.text == ['free','elections','in','Africa']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 

		assert question.nes.size() == 1
//		assert question.nes[0].terms*.text == ['OSCE']
//		assert question.nes[0].classification.find{it == SC.organization}
		assert question.nes[0].terms*.text == ['Africa']
		assert question.nes[0].classification.find{it == SC.place_human_division}

	// check subjects 
	//  cities is also captu
		assert !question.subject//.subjectTerms*.text == ['cities']

	// check conditions 
		assert question.conditions.size() == 1
/*		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.subjectTerms*.text == ['cities']
		assert question.conditions[0].object.geoscopeTerms*.text == ['Russia']
		assert question.conditions[0].operator.op == Operator.Locator.Near
*/		
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text == ['Africa']
		
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:casualties']

	// check answers 
		assert !question.answer 
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

		println refq.toString()
		String reformulated_x = """
label:065 contents:free contents:elections contents:"Africa" woeid:24865670 woeid:23424740 woeid:23424745 woeid:23424755 woeid:23424764 woeid:23424774 woeid:23424777 woeid:23424779 woeid:23424780 woeid:23424785 woeid:23424786 woeid:23424792 woeid:23424794 woeid:23424797 woeid:23424802 woeid:23424804 woeid:23424806 woeid:23424808 woeid:23424821 woeid:23424822 woeid:23424824 woeid:23424835 woeid:23424854 woeid:23424863 woeid:23424876 woeid:23424880 woeid:23424882 woeid:23424883 woeid:23424886 woeid:23424889 woeid:23424891 woeid:23424893 woeid:23424894 woeid:23424896 woeid:23424902 woeid:23424906 woeid:23424908 woeid:23424929 woeid:23424931 woeid:23424937 woeid:23424941 woeid:23424942 woeid:23424943 woeid:23424944 woeid:23424946 woeid:23424949 woeid:23424952 woeid:23424965 woeid:23424966 woeid:23424967 woeid:23424973 woeid:23424974 woeid:23424978 woeid:23424987 woeid:23424990 woeid:23424993 woeid:23425003 woeid:23425004
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

