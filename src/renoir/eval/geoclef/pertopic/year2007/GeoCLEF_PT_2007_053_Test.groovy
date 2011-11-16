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
 class GeoCLEF_PT_2007_053_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_PT_2007_053_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "pt"
	//	String topic = "label:053 Investigação científica em universidades da costa leste da Escócia"
		String topic = "label:053 Investigação científica em universidades da Escócia"
		//Santo André" 

/*
label:054 Prejuízos causados por chuvas ácidas no Norte da Europa
label:055 Mortes causadas por avalanches na Europa excluindo os Alpes
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
		assert question.sentence*.text == ['Investigação','científica','em','universidades','da','Escócia']//'Santo','André']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 
// Santo Andre´dá DBpedia certa, mas sem classificaçaõ, vai para a Wikipédia qie dá como sendo uma pessoa.
	
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Escócia']//['Santo','André']
		assert question.nes[0].classification.find{it == SC.place_human_country}
	
	// check subjects 
	// games is grounded
		assert !question.subject//.subjectTerms*.text == ['cidades']

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
	//	assert question.conditions[0].operator.op == Operator.Locator.Near
		assert question.conditions[0].object.subjectTerms*.text == ['universidades']
		assert question.conditions[0].object.geoscopeTerms*.text == ['Escócia']
		
		assert !question.expectedAnswerTypes//[0].DBpediaOntologyResources == ['Category:Cities']
		
	// check answers 
		assert !question.answer
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:053 contents:Investigação contents:científica contents:universidades contents:"Escócia" woeid:12578048 entity:Subcity_Radio entity:AHDS_Performing_Arts entity:Glasgow_University_Guardian entity:University_of_Glasgow_Medical_School entity:Glasgow_University_Magazine entity:Robertson_Centre_for_Biostatistics entity:University_of_Glasgow_Faculty_of_Veterinary_Medicine entity:Scottish_Corpus_of_Texts_and_Speech entity:University_Marine_Biological_Station_Millport entity:Snell_Exhibition entity:ESharp entity:Glasgow_Clinical_Trials_Unit entity:University_of_Glasgow entity:Glasgow_University_Students%27_Representative_Council entity:Rector_of_the_University_of_Glasgow entity:Glasgow_and_Aberdeen_Universities_%28UK_Parliament_constituency%29 entity:Archives_of_the_University_of_Glasgow entity:Screen_%28journal%29 entity:Humanities_Advanced_Technology_and_Information_Institute entity:Glasgow_University_Union entity:Queen_Margaret_Union entity:Trinity_College%2C_Glasgow entity:Association_for_Scottish_Literary_Studies entity:Principal_of_the_University_of_Glasgow entity:Electronic_Resource_Preservation_and_Access_Network entity:History%2C_Classics_and_Archaeology_Subject_Centre entity:Student_Theatre_at_Glasgow entity:Glasgow_University_Sports_Association entity:University_of_Glasgow_School_of_Law entity:Glasgow_University_F.C. entity:University_of_Glasgow_Memorial_Chapel entity:Glasgow_Cardiovascular_Research_Centre entity:Lion_and_Unicorn_Staircase entity:University_of_Glasgow_Memorial_Gates entity:Scottish_Institute_of_Sport entity:Stirling_University_Rugby_Football_Club entity:Commonwealth_Games_Council_for_Scotland entity:University_of_Stirling entity:Macrobert entity:Stirling_Clansmen entity:Scottish_Swimming entity:Stirling_University_F.C. entity:Bumblebee_Conservation_Trust entity:Duncan_of_Jordanstone_College_of_Art_and_Design entity:University_of_Dundee_School_of_Law entity:University_of_Dundee entity:Centre_for_Energy%2C_Petroleum_and_Mineral_Law_and_Policy entity:Dundee_University_Students%27_Association entity:Rector_of_the_University_of_Dundee entity:The_Enterprise_Gym entity:University_of_Dundee_Botanic_Garden entity:Educating_Rita entity:Rough_Science entity:Educating_Rita_%28film%29 entity:University_Centre_Hastings entity:Open2.net entity:OpenLearn entity:Open_University entity:Universitas_Terbuka entity:Open_University_Students_Association entity:Pennyland_project entity:Bell_College entity:Scottish_Centre_for_Enabling_Technologies entity:University_of_the_West_of_Scotland entity:Napier_Mavericks entity:Employment_Research_Institute entity:International_Teledemocracy_Centre entity:Transport_Research_Institute entity:Screen_Academy_Scotland entity:The_Journal_%28student_newspaper%29 entity:Craiglockhart_Hydropathic entity:Centre_for_Timber_Engineering entity:Edinburgh_Napier_University entity:Merchiston_Castle entity:Bill_Buchanan_%28professor%29 entity:Queen_Margaret_University entity:International_Centre_for_the_Study_of_Planned_Events entity:Ancient_university entity:Ancient_university_governance_in_Scotland entity:Students%27_Representative_Council entity:Undergraduate_gowns_in_Scotland entity:Ancient_universities_of_Scotland entity:Academic_Senate entity:University_Court entity:General_Council_%28Scottish_university%29 entity:University_constituency entity:Sponsio_Academica entity:Meal_Monday entity:St_Salvator%27s_College%2C_St_Andrews entity:Master_of_the_United_College entity:Hamilton_Hall entity:Deans_Court entity:Lady_Literate_in_Arts entity:The_Saint_%28UK_newspaper%29 entity:New_Hall_%28St_Andrews%29 entity:Arch%C3%A9_%28research_center%29 entity:University_of_St_Andrews entity:Academic_dress_of_the_University_of_St_Andrews entity:General_Council_of_the_University_of_St_Andrews entity:History_of_the_University_of_St_Andrews entity:Bute_Medical_School entity:Bute_Medical_Society entity:St_Andrews_Prize_for_the_Environment entity:St_Salvator%27s_Chapel entity:Records_of_the_Parliaments_of_Scotland entity:University_of_St_Andrews_Union_Debating_Society entity:Edinburgh_and_St_Andrews_Universities_%28UK_Parliament_constituency%29 entity:Governance_of_the_University_of_St_Andrews entity:University_of_St_Andrews_Students%27_Association entity:Gatty_Marine_Laboratory entity:St_John%27s_College%2C_St_Andrews entity:Albany_Park%2C_St_Andrews entity:May_Ball entity:St_Mary%27s_College%2C_St_Andrews entity:St_Leonard%27s_College_%28University_of_St_Andrews%29 entity:United_College%2C_St_Andrews entity:Quaestor_%28University_of_St_Andrews%29 entity:David_Russell_Apartments entity:University_of_St_Andrews_Football_Club entity:Independent_Student_Groups_in_St_Andrews entity:University_of_St_Andrews_Athletic_Union entity:Museum_of_the_University_of_St_Andrews_%28MUSA%29 entity:UHI_Millennium_Institute entity:GCU_Roughriders entity:James_Goold_Hall entity:University_of_Strathclyde entity:Jordanhill_College entity:University_of_Strathclyde_department_of_Geography_and_Sociology entity:Strathclyde_Business_School entity:Royal_College_of_Science_and_Technology entity:Royal_Scottish_Geographical_Society entity:Strathclyde_Law_School entity:University_of_Strathclyde_Law_Arts_%26_Social_Sciences entity:University_of_Strathclyde_Department_of_Government entity:University_of_Strathclyde_Students%27_Association entity:Livingstone_Tower entity:University_of_Strathclyde_Computer_and_Informational_Sciences_Department entity:University_of_Strathclyde_Faculty_of_Education entity:Strathclyde_Institute_of_Pharmacy_and_Biomedical_Sciences entity:University_of_Strathclyde_Faculty_of_Science entity:Gray%27s_School_of_Art entity:Robert_Gordon_University entity:Scott_Sutherland_School_of_Architecture_and_The_Built_Environment entity:School_of_Computing_%28Robert_Gordon_University%29 entity:National_Union_of_Students_Scotland entity:Teviot_Row_House entity:Aberdeen_University_Sports_Union entity:Aberdeen_University_Students%27_Association entity:Coalition_of_Higher_Education_Students_in_Scotland entity:Glasgow_Caledonian_University_Students%27_Association entity:Edinburgh_University_Students%27_Association entity:Edinburgh_University_Sports_Union entity:Heriot-Watt_University entity:Edinburgh_Business_School entity:Heriot-Watt_University_Hockey_Club entity:Heriot-Watt_University_Dubai entity:Hong_Kong_College_of_Engineering entity:Glasgow_Caledonian_University entity:The_Spoken_Word_Project entity:Radio_Caley entity:University_of_Edinburgh_School_of_Law entity:University_of_Aberdeen_School_of_Law entity:University_of_Abertay_Dundee entity:Federation_of_Student_Nationalists entity:Bejan entity:Scottish_Labour_Students entity:Carnegie_Trust_for_the_Universities_of_Scotland entity:Combined_Scottish_Universities_%28UK_Parliament_constituency%29 entity:Nations_in_Scottish_universities entity:Principal_%28academia%29
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

