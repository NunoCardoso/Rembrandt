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
 class GeoCLEF_PT_2007_055_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2007_055_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
	// 'nos Alpes é padrão que se apanha, 'excluindo os alpes' não é padrão que se apanhe
		String lang = "pt"
//		String topic = "label:055 mortes causadas por avalanches na Europa excluindo os Alpes"
		String topic = "label:055 mortes causadas por avalanches na Europa mas não nos Alpes"

/*
label:056 Lagos com monstros
label:057 Uísque de ilhas escocesas
label:058 Problemas em aeroportos londrinos
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
		assert question.sentence*.text == ['mortes','causadas','por','avalanches','na','Europa','mas','não','nos','Alpes']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
	
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['Europa']
		assert question.nes[0].classification.find{it == SC.place_human_division}
		assert question.nes[1].terms*.text == ['Alpes']
		assert question.nes[1].classification.find{it == SC.place_physical_mountain}	
	// check subjects 
	// games is grounded
		assert !question.subject//.subjectTerms*.text == ['cidades']

	// check conditions 
		assert question.conditions.size() == 2
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.In
		assert question.conditions[0].object.ne.terms*.text == ['Europa']
		assert question.conditions[1].object instanceof QueryGeoscope
		assert question.conditions[1].operator.op == Operator.Locator.In
		assert question.conditions[1].object.ne.terms*.text == ['Alpes']
		
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:Cities']
		
	// check answers 
		assert !question.answer
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:055 contents:mortes contents:causadas contents:avalanches contents:"Europa" contents:"Alpes" woeid:24865675 woeid:12577865 woeid:20069817 woeid:20069818 woeid:23424742 woeid:23424744 woeid:23424750 woeid:23424757 woeid:23424761 woeid:23424765 woeid:23424771 woeid:23424796 woeid:23424803 woeid:23424805 woeid:23424810 woeid:23424812 woeid:23424816 woeid:23424819 woeid:23424825 woeid:23424829 woeid:23424833 woeid:23424843 woeid:23424844 woeid:23424845 woeid:23424853 woeid:23424874 woeid:23424875 woeid:23424877 woeid:23424879 woeid:23424881 woeid:23424885 woeid:23424890 woeid:23424892 woeid:23424897 woeid:23424909 woeid:23424910 woeid:23424923 woeid:23424925 woeid:23424933 woeid:23424945 woeid:23424947 woeid:23424950 woeid:23424954 woeid:23424957 woeid:23424975 woeid:23424976 woeid:23424986 woeid:26812346 woeid:28289413 woeid:2353770

"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

