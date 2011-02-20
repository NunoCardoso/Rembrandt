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
 class GeoCLEF_PT_2006_044_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2006_044_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
//		String topic = "label:044 Venda de armas na antiga Jugoslávia"
		String topic = "label:044 Venda de armas na Jugoslávia"

/*
 
label:045 Turismo no nordeste do Brasil  
label:046 Fogos florestais no norte de Portugal  
label:047 Jogos da Liga dos Campeões no Mediterrâneo  
label:048 Pescas na Terra Nova e na Gronelândia  
label:049 A ETA em França  
label:050 Cidades no Danúbio e Reno  
*/

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
	//	assert question.sentence*.text == ['Venda','de','armas','na','antiga','Jugoslávia']
		assert question.sentence*.text == ['Venda','de','armas','na','Jugoslávia']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 

		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Jugoslávia']
		assert question.nes[0].classification.find{it == SC.place_human_country}				

	// check subjects 
		// armas é capturado, vamos ignorar
		//assert !question.subject

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
	//	assert question.conditions[0].operator.op == Operator.Locator.North
		assert question.conditions[0].object.ne.terms*.text == ['Jugoslávia']
			
	// Then, for the lone condition Subject, we resolve it and add it 			
	//	assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:Shipwrecks']
		
	// check answers 
		assert !question.answer //!= null
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:044 contents:Venda contents:"armas" contents:"Jugoslávia" woeid:26793662 woeid:20069817 woeid:20069818 woeid:23424761 woeid:23424843 woeid:23424890 woeid:23424945 woeid:20069817 woeid:20069818 woeid:23424761 woeid:23424843 woeid:23424890 woeid:23424945
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

