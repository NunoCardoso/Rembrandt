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
 class GeoCLEF_PT_2006_027_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2006_027_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
		String topic = "label:027 cidades a menos de 100 quilómetros de Frankfurt" //Francoforte"

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['cidades','a','menos','de','100','quilómetros','de','Frankfurt']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0


	// check detected NEs 
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['menos','de','100','quilómetros']
		assert question.nes[1].terms*.text == ['Frankfurt']
    
		/// contains gives false. Use find
		assert question.nes[0].classification.find{it == SC.value_quantity}
		assert question.nes[1].classification.find{it == SC.place_human_division}
		
	// check subjects 
		assert question.subject.subjectTerms*.text == ['cidades']

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].operator.op == Operator.Locator.Around
		assert question.conditions[0].operator.amount == 100
		assert question.conditions[0].operator.unit == "KM"
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Frankfurt']

	// SPARQL should resolve instances of the SUBJECT against the condition 
	// QueryGeoscope, using dbpedia-owl:location
	
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert question.expectedAnswerTypes.size() == 1
		assert question.expectedAnswerTypes[0].DBpediaOntologyResources == \
		   question.subject.categoryWikipediaAsDBPediaResource
		assert question.expectedAnswerTypes[0].resolvesTo == ExpectedAnswerType.Type.DBpediaOntologyResource
		
	// check answers 
		assert !question.answer
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:027 contents:"cidades" contents:"menos de 100 quilómetros" contents:"Frankfurt" ne-VALOR-QUANTIDADE:"menos de 100 quilómetros" woeid:650272
"""		
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}
