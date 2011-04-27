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
package renoir.eval.geoclef.pertopic.year2008

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
 class GeoCLEF_PT_2008_084_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2008_084_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
		String topic = "label:084 Atentados à bomba na Irlanda do Norte" 
/*
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
		assert question.sentence*.text == ['Atentados','à','bomba','na','Irlanda','do','Norte']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

// Sahara is grounded to Deserto do Saara, but can't make a NE out of it.

	// check detected NEs 
		
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Irlanda','do','Norte']
		assert question.nes[0].classification.find{it == SC.place_human_country}
//		assert question.nes[1].terms*.text == ['Península','Ibérica']
//		assert question.nes[1].classification.find{it == SC.place_human_division}
	
	// check subjects 
	// games is grounded
		assert !question.subject//.subjectTerms*.text == ['políticos']

	// check conditions 
		assert question.conditions.size() == 1
/*		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.subjectTerms*.text == ['países']
		assert question.conditions[0].object.geoscopeTerms*.text == ['Mar','Mediterrâneo']
*/		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text == ['Irlanda','do','Norte']
	
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == []
		
	// check answers 
		assert !question.answer
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:084 contents:Atentados contents:bomba contents:"Irlanda do Norte" woeid:20070563
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

