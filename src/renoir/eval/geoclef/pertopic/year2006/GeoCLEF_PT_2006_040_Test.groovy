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
 class GeoCLEF_PT_2006_040_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2006_040_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
		String topic = "label:040 cidades perto de vulcões activos"


		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['cidades','perto','de','vulcões','activos']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 

// Exactamente o mesmo problema do que no 37, médio oriente: DBpeida referencia, só que não 
// classifica, e a wikipédia portuguesa também não. Como tal, sai <EM> , e assim não é detectado  
		assert question.nes.size() == 0
//		assert question.nes[0].terms*.text == ['Cáucaso']
//		assert question.nes[0].classification.find{it == SC.place_human}				
	// check subjects 

		assert question.subject.subjectTerms*.text == ["cidades"]

	// check conditions 
		assert question.conditions.size() == 0
//		assert question.conditions[0].object instanceof QueryGeoscope
//		assert question.conditions[0].operator.op == Operator.Locator.South
//		assert question.conditions[0].object.ne.terms*.text == ['Cáucaso']
					
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert question.expectedAnswerTypes[0].DBpediaOntologyResources == ['Category:Cities']

	// check answers 
		assert question.answer != null
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:040 contents:"cidades" contents:perto contents:vulcões contents:activos
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}
