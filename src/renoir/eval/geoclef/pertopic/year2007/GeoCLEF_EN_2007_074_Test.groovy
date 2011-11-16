
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
 class GeoCLEF_EN_2007_074_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2007_074_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:073 ship traffic around the Portuguese islands"
/*
label:074 
label:075 Violation of human rights in Burma
*/
		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['ship','traffic','around','the','Portuguese','islands']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

// http://dbpedia.org/page/Bosphorus não tem classe, e QWikipédia não ajuda


	// check detected NEs 
		assert question.nes.size() == 1
//		assert question.nes[0].terms*.text == ['St.','Paul','\'s','Cathedral']
	//	assert question.nes[0].classification.find{it == SC.place_human_construction}
		assert question.nes[0].terms*.text == ['Portuguese']
		assert question.nes[0].classification.find{it == SC.place_physical_island}


	// check subjects 
	//  cities is also captu
	//	assert !question.subject.subjectTerms*.text == ['Tourist','attractions']

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.subjectTerms*.text == ['islands']
		assert question.conditions[0].object.geoscopeTerms*.text == ['Portuguese']
	//	assert question.conditions[0].operator.op == Operator.Locator.Around

		
/*		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text == ['Lisbon']
*/		
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes//[0]//.DBpediaOntologyResources == ['Category:Tourist_attractions']

	// check answers 
		assert !question.answer 
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

// É melhor que não tenha woeid, ele aponta para uma cidade nos EUA!"
		println refq.toString()
		String reformulated_x = """
label:074 contents:ship contents:traffic contents:"Portuguese" contents:islands ne-LOCAL-FISICO-ILHA:"Portuguese"  entity:Sabrina_Island_%28Azores%29 entity:Dom_Jo%C3%A3o_de_Castro_Bank entity:Corvo_Island entity:Dollabarat entity:Formigas entity:Monchique_Islet entity:Culatra_Island entity:Barreta_Island entity:Armona_Island entity:Tavira_Island entity:Cacela_Island entity:Berlenga_Grande_Island entity:Berlengas woeid:742198 entity:Bugio_Island entity:Deserta_Grande_Island entity:Cal_Islet entity:Ilh%C3%A9u_Ch%C3%A3o entity:Selvagem_Grande_Island woeid:12478424 entity:Selvagem_Pequena_Island entity:Ermal_Island entity:Pessegueiro_Island  entity:Terceira_Island entity:Santa_Maria_Island entity:São_Miguel_Island entity:Flores_Island_%28Azores%29 entity:Graciosa_Island_%28Azores%29 entity:Pico_Island entity:São_Jorge_Island entity:Faial_Island entity:Madeira_Island entity:Savage_Islands entity:Porto_Santo_Island"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

