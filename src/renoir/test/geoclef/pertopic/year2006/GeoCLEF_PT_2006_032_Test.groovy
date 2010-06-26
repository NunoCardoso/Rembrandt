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
 class GeoCLEF_PT_2006_032_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2006_032_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
//		String topic = "label:032 Movimento para a independência do Quebec"
		// grande diferença... 'no' é mais geográfico, 'do' não é capturado pelas regras, e não dá para adicionar, mistura-se com subjexts
		String topic = "label:032 Movimento para a independência no Quebec"
/*
label:033 Competições desportivas internacionais no Ruhr 
label:034 Malária nos trópicos  
label:035 Empréstimos ao antigo Bloco de Leste  
label:036 Indústria automóvel no Mar do Japão  
label:037 Descobertas arqueológicas no Oriente Médio 
label:038 Eclipses solar e lunar no Sudoeste Asiático  
label:039 Tropas russas no sul do Cáucaso  
label:040 Cidades perto de vulcões activos 
label:041 Naufrágios no Oceano Atlântico  
label:042 Eleições regionais no norte da Alemanha  
label:043 Pesquisa científica em universidades da Nova Inglaterra  
label:044 Venda de armas na antiga Jugoslávia  
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
//		assert question.sentence*.text == ['Movimento','para','a','independência','do','Quebec']
		assert question.sentence*.text == ['Movimento','para','a','independência','no','Quebec']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0


	// check detected NEs 

		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['Movimento']
		assert question.nes[1].terms*.text == ['Quebec']
    
		/// contains gives false. Use find
		assert question.nes[1].classification.find{it == SC.place_human_division}				

	// check subjects 
		assert !question.subject

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Quebec']
			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes

	// check answers 
		assert !question.answer
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:032 contents:"Movimento" contents:independência contents:"Quebec" entity:Motion_%28physics%29 woeid-index:2344924
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}
