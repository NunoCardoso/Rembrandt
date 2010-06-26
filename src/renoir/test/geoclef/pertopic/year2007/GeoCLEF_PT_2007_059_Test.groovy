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
package renoir.test.geoclef.pertopic.year2007

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
 class GeoCLEF_PT_2007_059_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2007_059_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
//		String topic = "label:059 cidades em que houve reuniões da Comunidade dos Países Andinos"// (CAN)"
		String topic = "label:059 reuniões da Comunidade Andina de Nações"// (CAN)"
/*
l
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
		assert question.sentence*.text == ['reuniões','da','Comunidade','Andina','de','Nações']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Comunidade','Andina','de','Nações']
		assert question.nes[0].classification.find{it == SC.organization}
	//	assert question.nes[1].terms*.text == ['Alps']
	//	assert question.nes[1].classification.find{it == SC.place_physical_mountain}

	// check subjects 
	// games is grounded
		assert !question.subject//.subjectTerms*.text == ['Lakes']

	// check conditions 
		assert question.conditions.size() == 0
//		assert question.conditions[0].object instanceof Subject
//		assert question.conditions[0].object.subjectTerms*.text == ['aeroportos']
//		assert question.conditions[0].object.geoscopeTerms*.text == ['Londres']
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
label:059 contents:reuniões contents:"Comunidade Andina de Nações" entity:Andean_Community_of_Nations
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

