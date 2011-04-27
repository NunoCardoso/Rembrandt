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
package renoir.eval.geoclef.pertopic.year2006

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
 class GeoCLEF_PT_2006_037_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2006_037_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
		String topic = "label:037 Descobertas arqueológicas no Médio Oriente"

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['Descobertas','arqueológicas','no','Médio','Oriente']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

// DBpedia devolve Médio Oriente, só que não tem classes que peritem classificar, 
// e ao contrário da Eikipédia inglesa, a wikipedia portuguesa não a consehue classificar.
// por isso, devolve <EM> , e o URL da DBpedia não dá para atribuir a uma classificação. 

// copmo tal, vou martelar isto com o que conseuir da inglesa 


	// check detected NEs 

		assert question.nes.size() == 0
		//assert question.nes[0].terms*.text == ['Médio','Oriente']
		/// contains gives false. Use find
		//assert question.nes[0].classification.find{it == SC.place_physical_physicalregion}				
	// check subjects 
		assert !question.subject

	// check conditions 
		assert question.conditions.size() == 0
	//	assert question.conditions[0].object instanceof QueryGeoscope
	//	assert question.conditions[0].operator.op == Operator.Locator.In
	//	assert question.conditions[0].object.ne.terms*.text == ['Médio','Oriente']
					
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes

	// check answers 
		assert !question.answer
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:037 contents:Archeology contents:findings contents:"Middle East" woeid:24865721 woeid:23424738 woeid:23424753 woeid:23424802 woeid:23424851 woeid:23424852 woeid:23424855 woeid:23424860 woeid:23424870 woeid:23424873 woeid:23424898 woeid:23424930 woeid:23424938 woeid:23424956 woeid:23425002 woeid:28289408
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}
