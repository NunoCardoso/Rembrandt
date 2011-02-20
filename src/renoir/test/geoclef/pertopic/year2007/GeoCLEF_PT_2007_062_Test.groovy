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
 class GeoCLEF_PT_2007_062_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2007_062_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
//		String topic = "label:062 Reuniões da OSCE na Europa de Leste"
		String topic = "label:062 Reuniões da OSCE no Leste Europeu"

/*

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
		assert question.sentence*.text == ['Reuniões','da','OSCE','no','Leste','Europeu']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['OSCE']
		assert question.nes[0].classification.find{it == SC.organization}
		assert question.nes[1].terms*.text == ['Leste','Europeu']
		assert question.nes[1].classification.find{it == SC.place_physical_physicalregion}

	// check subjects 
	// games is grounded
		assert !question.subject//.subjectTerms*.text == ['Lakes']

	// check conditions 
		assert question.conditions.size() == 1
//		assert question.conditions[0].object instanceof Subject
//		assert question.conditions[0].object.subjectTerms*.text == ['cidades']
//		assert question.conditions[0].object.geoscopeTerms*.text == ['Rússia']
		//assert question.conditions[0].operator.op == Operator.Locator.Near

		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text == ['Leste','Europeu']
		
			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:Lakes']

	// check answers 
		assert question.answer != null
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  

		println refq.toString()
		String reformulated_x = """
label:062 contents:"OSCE" contents:reuniões contents:"Europa do Leste" entity:Organization_for_Security_and_Co-operation_in_Europe woeid:28289419 woeid:23424771 woeid:23424923 woeid:23424810 woeid:23424877 woeid:23424844 woeid:23424933 woeid:23424976 woeid:23424765 woeid:23424875 woeid:23424805 woeid:23424874 woeid:23424936
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

