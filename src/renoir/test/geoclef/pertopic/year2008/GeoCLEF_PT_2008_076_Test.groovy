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
package renoir.test.geoclef.pertopic.year2008

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
 class GeoCLEF_PT_2008_076_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2008_076_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
	//	String topic = "label:076 Motins em presídios sul americanos" 
		String topic = "label:076 Motins em presídios ou prisões na América do Sul" 

/*
label:076 
label:077 Prémios Nobel nascidos no Norte da Europa
label:078 Eventos desportivos no deserto do Sahara
label:079 Ocupação da capital timorense pela Indonésia
label:080 Políticos exilados na Alemanha
label:081 Cimeiras dos G7 em países mediterrânicos
label:082 Agricultura na Península Ibérica
label:083 Manifestações no Norte de África contra o terrorismo
label:084 Atentados à bomba na Irlanda do Norte
label:085 Realização de testes nucleares no Pacífico Sul
label:086 Monumentos mais visitados na região da capital de França
label:087 Desemprego nos países da OCDE
label:088 Comunidades de emigrantes portuguesas no mundo
label:089 Feiras na Baixa Saxónia
label:090 Poluição ambiental nas águas europeias
label:091 Incêndios florestais nas ilhas espanholas
label:092 Fundamentalistas islâmicos na Europa Ocidental
label:093 Atentados em metropolitanos japoneses
label:094 Manifestações em cidades alemãs
label:095 Tropas americanas no Golfo Pérsico
label:096 Boom económico no sudeste asiático
label:097 Ajuda internacional à Africa subsariana
label:098 Tibetanos no subcontinente indiano
label:099 Inundações em cidades da Europa
label:100 Desastres naturais no Oeste dos Estados Unidos
*/

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['Motins','em','presídios','ou','prisões','na','América','do','Sul']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
	// DBpedia grounds ['Liga','dos','Campeões','da','UEFA'], but it doesn't classify 
	// Wikipedia does not help, either.
	
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['América','do','Sul']
		assert question.nes[0].classification.find{it == SC.place_human_division}
	
	// check subjects 
	// games is grounded
		assert !question.subject//.subjectTerms*.text == ['cidades']

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text == ['América','do','Sul']
	
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:Cities']
		
	// check answers 
		assert !question.answer
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:076 contents:Motins contents:presídios contents:prisões contents:"América do Sul" woeid-index:24865673 woeid-index:23424747 woeid-index:23424762 woeid-index:23424768 woeid-index:23424782 woeid-index:23424787 woeid-index:23424801 woeid-index:23424811 woeid-index:23424814 woeid-index:23424836 woeid-index:23424913 woeid-index:23424917 woeid-index:23424919 woeid-index:23424955 woeid-index:23424979 woeid-index:23424982
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

