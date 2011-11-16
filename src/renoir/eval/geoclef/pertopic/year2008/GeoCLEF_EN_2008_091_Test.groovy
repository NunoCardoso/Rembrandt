
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
package renoir.eval.geoclef.pertopic.year2008

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
 class GeoCLEF_EN_2008_091_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2008_091_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:091 forest fires in islands of Spain"
/*

label:092 Islamic fundamentalists in Western Europe
label:093 Attacks in Japanese subways
label:094 Demonstrations in German cities
label:095 American troops in the Persian Gulf
label:096 Economic boom in Southeast Asia
label:097 Foreign aid in Sub-Saharan Africa
label:098 Tibetan people in the Indian subcontinent
label:099 Floods in European cities
label:100 Natural disasters in the western USA
*/

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['forest','fires','in','islands','of','Spain']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
		assert question.nes.size() == 1
//		assert question.nes[0].terms*.text == ['G7']
//		assert question.nes[0].classification.find{it == SC.organization}
		assert question.nes[0].terms*.text == ['Spain']
	//	assert question.nes[0].classification.find{it == SC.place_human_country}

	// check subjects 

		assert !question.subject//.subjectTerms*.text == ['fairs']

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
	//	assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.subjectTerms*.text == ['islands']
		assert question.conditions[0].object.geoscopeTerms*.text == ['Spain']
	
/*	assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text ==  ['Lower','Saxony']
*/			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:Fairs']

	// check answers 
		assert !question.answer 
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

//Albania,Algeria,Bosnia and Herzegovina ,Croatia,Cyprus,Egypt,France,Gibraltar,
//,Greece,Israel,Italy,Lebanon,Libya,Malta , Monaco ,Montenegro,Morocco ,
//Slovenia,Spain,Syria,Tunisia,Turkey

		println refq.toString()
		String reformulated_x = """
label:091 contents:forest contents:fires contents:islands contents:"Spain" ne-LOCAL-FISICO-ILHA:"Spain" entity:Graciosa%2C_Canary_Islands entity:Lobos_Island entity:Chinijo_Archipelago entity:Roque_del_Oeste woeid:12478495 entity:Roque_del_Este entity:La_Gomera entity:El_Hierro entity:Alegranza woeid:12478595 entity:Monta%C3%B1a_Clara woeid:12478451 entity:Isla_de_S%27Espalmador entity:Pine_Islands entity:Gymnesian_Islands entity:Formentera entity:Isla_de_sa_Porrassa entity:Es_Vedra entity:Isla_de_Tagomago woeid:12578039 entity:Golf_Son_Gual_Mallorca entity:Divisiones_Regionales_de_F%C3%BAtbol_in_Balearic_Islands entity:Pe%C3%B1%C3%B3n_de_Alhucemas entity:Islas_Chafarinas entity:Pe%C3%B1%C3%B3n_de_V%C3%A9lez_de_la_Gomera entity:San_Marti%C3%B1o entity:Isla_del_Trocadero entity:Medes_Islands entity:Isla_de_Albor%C3%A1n entity:Benidorm_Island entity:C%C3%ADes_Islands entity:Perejil_Island entity:Coelleira entity:A_Illa_de_Arousa entity:Ons_Island entity:Tabarca entity:Columbretes_Islands entity:Cortegada_Island entity:Pheasant_Island entity:Isla_Canela entity:S%C3%A1lvora entity:Isla_de_Santa_Catalina
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

