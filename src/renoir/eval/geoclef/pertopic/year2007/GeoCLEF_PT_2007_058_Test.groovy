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
 class GeoCLEF_PT_2007_058_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2007_058_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
//		String topic = "label:058 Problemas em aeroportos londrinos
		String topic = "label:058 Problemas em aeroportos de Londres"
/*
label:059 Cidades em que houve reuniões da comunidade dos países andinos (CAN)
label:060 Baixas em Nagorno-Karabakh
label:061 Acidentes de avião perto de cidades russas
label:062 Reuniões da OSCE na Europa de Leste
label:063 Qualidade da água na costa mediterrânica
label:064 Acontecimentos desportivos na Suíça francesa
label:065 Eleições livres em África
label:066 Economia no Bósforo
label:067 Pistas em que Ayrton Senna correu em 1994
label:068 Rios com cheias
label:069 Morte nos Himalaias
label:070 Turismo no Norte da Itália
label:071 Problemas sociais na Grande Lisboa
label:072 Costas com tubarões
label:073 Ocorrências na catedral de São Paulo
label:074 Tráfego marítimo nas ilhas portuguesas
label:075 Violações dos direitos humanos na antiga Birmânia
*/

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['Problemas','em','aeroportos','de','Londres']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Londres']
		assert question.nes[0].classification.find{it == SC.place_human_division}
	//	assert question.nes[1].terms*.text == ['Alps']
	//	assert question.nes[1].classification.find{it == SC.place_physical_mountain}

	// check subjects 
	// games is grounded
		assert !question.subject//.subjectTerms*.text == ['Lakes']

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.subjectTerms*.text == ['aeroportos']
		assert question.conditions[0].object.geoscopeTerms*.text == ['Londres']
	//	assert question.conditions[1].object instanceof QueryGeoscope
	//	assert question.conditions[1].operator.op == Operator.Locator.In
	//	assert question.conditions[1].object.ne.terms*.text == ['Alps']

			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:Lakes']

	// check answers 
		assert question.answer != null
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

		println refq.toString()
		String reformulated_x = """
label:058 contents:Problemas contents:aeroportos contents:"Londres" woeid:44418 entity:London_Biggin_Hill_Airport entity:RAF_Northolt entity:RAF_Bentley_Priory entity:RAF_Uxbridge entity:RAF_Stanmore_Park entity:RAF_Fairlop entity:RAF_Kenley entity:RAF_Hornchurch entity:RAF_Hendon entity:RAF_Heston entity:RAF_West_Drayton entity:RAF_Blenheim_Crescent entity:M25_motorway entity:Piccadilly_line entity:Hayes_and_Harlington_railway_station entity:BOAC_Flight_712 entity:LON entity:Heathrow_Airside_Road_Tunnel entity:Heathrow_East entity:Continuous_Descent_Approach entity:Ultra entity:St._George%27s_Interdenominational_Chapel%2C_Heathrow_Airport entity:BAA_Limited entity:Bermuda_Agreement entity:Cranford_Agreement entity:London_Buses_route_X26 entity:British_Airways_Flight_38 entity:Heathrow_Terminal_4_railway_station entity:London_Buses_route_105 entity:London_Buses_route_111 entity:London_Buses_route_140 entity:London_Buses_route_285 entity:London_Buses_route_490 entity:London_Buses_route_A10 entity:London_Buses_route_350 entity:London_Buses_route_482 entity:Quota_Count_system entity:Heathrow_Terminals_1%2C_2%2C_3_tube_station entity:Bermuda_II entity:Heathrow_Cargo_Tunnel entity:Heathrow_Junction_railway_station entity:London_Terminal_Control_Centre entity:Heathrow_third_runway entity:Heathrow_Hub_railway_station entity:Concorde entity:London_Heathrow_Airport entity:Heathrow%2C_London entity:M4_motorway entity:Heathrow_Express entity:British_European_Airways_Flight_548 entity:Heathrow_Terminal_4_tube_station entity:Hatton_Cross_tube_station entity:Heathrow_Terminal_5_station entity:RailAir entity:Heathrow_Connect entity:Heathrow_Central_railway_station entity:Staines_air_disaster entity:Green_Line_route_724 entity:Heathrow_Airtrack entity:Pan_Am_Flight_125 entity:London_Heathrow_Terminal_5 entity:1958_Bristol_Britannia_312_crash entity:London_Heathrow_Terminal_4 entity:London_Heathrow_Terminal_3 entity:London_Heathrow_Terminal_2 entity:London_Heathrow_Terminal_1 entity:Heathrow_Terminal_6 entity:1968_BKS_Air_Transport_Heathrow_crash entity:Expansion_of_London_Heathrow_Airport entity:London_Buses_route_U3 entity:Heathrow_East_Terminal entity:Heathrow_Airport_Central_bus_station entity:Gatwick_Express entity:Brighton_Main_Line entity:Docklands_Light_Railway entity:Southern_%28train_operating_company%29 entity:Stansted_Express entity:First_Capital_Connect entity:Thameslink entity:London_Gatwick_Airport entity:London_Heliport entity:Elstree_Airfield entity:London_Stansted_Airport entity:Damyns_Hall_Aerodrome entity:Denham_Aerodrome entity:Lydd_Airport entity:Heston_Aerodrome entity:Stag_Lane_Aerodrome entity:London_Luton_Airport entity:Croydon_Airport woeid:22475376 entity:London_Southend_Airport entity:Stapleford_Aerodrome entity:Hendon_Aerodrome entity:Beehive_%28Gatwick_Airport%29

"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

