\
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
package renoir.test.geoclef.pertopic.year2006

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
 class GeoCLEF_PT_2006_038_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2006_038_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
		String topic = "label:038 Eclipses solar e lunar no Sudoeste Asiático"

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['Eclipses','solar','e','lunar','no','Sudoeste','Asiático']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 

// Exactamente o mesmo problema do que no 37, médio oriente: DBpeida referencia, só que não 
// classifica, e a wikipédia portuguesa também não. Como tal, sai <EM> , e assim não é detectado  
		assert question.nes.size() == 0
	//	assert question.nes[0].terms*.text == ['Sudoeste','Asiático']
	//	assert question.nes[0].classification.find{it == SC.place_physical_physicalregion}				
	// check subjects 
		assert !question.subject

	// check conditions 
		assert question.conditions.size() == 0
	//	assert question.conditions[0].object instanceof QueryGeoscope
	//	assert question.conditions[0].operator.op == Operator.Locator.In
	//	assert question.conditions[0].object.ne.terms*.text == ['Sudoeste','Asiático']
					
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes

	// check answers 
		assert !question.answer
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:038 contents:Eclipses contents:solar contents:lunar contents:"Sudoeste Asiático" woeid-index:28289414 woeid-index:23424763 woeid-index:23424773 woeid-index:23424776 woeid-index:23424784 woeid-index:23424846 woeid-index:23424869 woeid-index:23424872 woeid-index:23424901 woeid-index:23424921 woeid-index:23424934 woeid-index:23424948 woeid-index:23424960 woeid-index:23424968 woeid-index:23424984 woeid-index:23424763 woeid-index:23424773 woeid-index:23424776 woeid-index:23424784 woeid-index:23424846 woeid-index:23424869 woeid-index:23424872 woeid-index:23424901 woeid-index:23424921 woeid-index:23424934 woeid-index:23424948 woeid-index:23424960 woeid-index:23424968 woeid-index:23424984
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}
