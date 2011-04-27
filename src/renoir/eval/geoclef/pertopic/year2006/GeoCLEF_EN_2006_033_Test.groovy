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
package renoir.eval.geoclef.pertopic.year2006

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
 class GeoCLEF_EN_2006_033_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2006_033_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:033 International sports competitions in the Ruhr"


		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['International','sports','competitions','in','the','Ruhr']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0


	// check detected NEs 

		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['Ruhr']
    
		/// contains gives false. Use find
		// DBpedia did not helped, Wikipedia classified as a river.
		assert question.nes[0].classification.find{it == SC.place_physical_watercourse}				

	// check subjects 
		assert question.subject.subjectTerms*.text == ['sports','competitions']

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['Ruhr']
			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert question.expectedAnswerTypes[0].DBpediaOntologyResources == ['Category:Sports_competitions']

	// check answers 
	//	assert !question.answer
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:033 contents:International contents:"sports competitions" contents:"Ruhr" entity:Sports_league entity:League_system entity:Group_9_Rugby_League entity:Calgary_Roller_Derby_Association entity:Indian_Premier_League entity:Pakistan_Super_League entity:P20 entity:Scotiabank_National_T20_Championship entity:Inter-Provincial_Twenty20 entity:Twenty20_Tournament entity:National_Elite_League_Twenty20 entity:Southern_Hemisphere_T20_Tournament entity:Division_%28sport%29 entity:Mid-State_Athletic_Conference entity:National_Touch_League entity:Lega_Nazionale_Dilettanti entity:Capital_Alumni_Network entity:State_Twenty20 entity:Greater_Wichita_Athletic_League entity:Past_Champions_of_Second-Tier_Montenegrin_First_League entity:Status_lists_of_players_in_professional_sports entity:Massachusetts_Seniors_Squash_Rackets_Association entity:Lega_Italiana_Calcio_Professionistico entity:Independent_Schools_Athletic_League entity:Indian_Cricket_League entity:Lega_Nazionale_Professionisti entity:Metropolitan_Bank_Twenty20 entity:Scottish_Handball_League entity:Peruvian_football_league_system entity:Centennial_League entity:Belgium_women%27s_volleyball_Division_of_Honour entity:Belgium_men%27s_volleyball_League entity:Major_League_Baseball_All-Star_Game entity:All-Star_Futures_Game entity:J._League_All-Star_Soccer entity:Major_Indoor_Soccer_League_All-Star_Game entity:Under_Armour_All-America_Game entity:Nippon_Professional_Baseball_All-Star_Game entity:Major_League_Lacrosse_All-Star_Game entity:Blue%E2%80%93Gray_Football_Classic entity:National_Lacrosse_League_All-Star_Game entity:NBL_%28Australia%29_All-Star_Game entity:Toyota_All-Star_Showdown entity:All-star_game entity:NBA_All-Star_Game entity:National_Hockey_League_All-Star_Game entity:WNBA_All-Star_Game entity:Major_League_Soccer_All-Star_Game entity:American_Football_League_All-Star_game entity:All-America_Football_Conference_All-Star_Game entity:NASCAR_Sprint_All-Star_Race entity:E._J._Whitten_Legends_Game entity:CFL_All-Star_Game entity:ECHL_All-Star_Game entity:Skills_competitions entity:Kontinental_Hockey_League_All-Star_Game entity:Offense-Defense_All-American_Bowl entity:Pro_Bowl entity:East-West_All-Star_Game entity:Big_33_Football_Classic entity:College_All-Star_Game entity:Marlboro_Challenge entity:Triple-A_All-Star_Game entity:Roundball_Classic entity:K-League_All-Star_Game entity:HEBA_Greek_All_Star_Game entity:HEBA_Greek_All_Star_Game_2008%E2%80%9309 entity:2010_All_Stars_match entity:Women%27s_Professional_Soccer_All-Star_Game entity:WPS_All-Star_2009 entity:Bayou_Bowl entity:All_Stars_Match entity:Americas_Cross_Country_Championships entity:Gymnasiade entity:Commonwealth_Games entity:Goodwill_Games entity:Deaflympics entity:World_Police_and_Fire_Games entity:3rd_World_Festival_of_Youth_and_Students entity:Cotswold_Games entity:D%C3%A9fi_sportif entity:Gravity_Games entity:National_Games_of_the_People%27s_Republic_of_China entity:West_Asian_Games entity:National_Congress_of_State_Games entity:Games_of_the_Small_States_of_Europe entity:European_Youth_Olympic_Festival entity:Extremity_Games entity:South_American_Games entity:Pan-Armenian_Games entity:Thailand_National_Games entity:Sports_carnival entity:Kingdom_Games entity:Youth_Olympic_Games entity:Thailand_National_Youth_Games entity:CARIFTA_Games entity:National_Peasants%27_Games entity:Paralympic_Games entity:Pan_American_Games entity:Naadam entity:Nordic_Games entity:Military_pentathlon entity:Arafura_Games entity:GANEFO entity:Southeast_Asian_Games entity:Alabama_Sports_Festival entity:East_Asian_Games entity:Big_Sky_State_Games entity:Bolivarian_Games entity:Pacific_Games entity:Military_World_Games entity:Tailteann_Games entity:Rhieia entity:SELL_Student_Games entity:Senior_Olympics entity:Mei-Chu_Tournament entity:Western_Canada_Summer_Games entity:World_Youth_Games entity:Women%27s_Islamic_Games entity:Extreme_sport entity:World_Games entity:Panathenaic_Games entity:Special_Olympics_World_Games entity:Spartakiad entity:Afro-Asian_Games entity:Canadian_Paralympic_Athletics_Championships entity:Commonwealth_Youth_Games entity:National_Sports_Festival_of_Japan entity:Central_African_Games entity:Central_American_and_Caribbean_Games entity:Children_of_Asia_International_Sports_Games entity:Ancient_Olympic_Games entity:Sukma_Games entity:Empire_State_Games entity:Prairie_Rose_State_Games entity:Lusophony_Games entity:Inter-Allied_Games entity:Yves_Rossy entity:Youth_Friendship_Games entity:Micronesian_Games entity:CPLP_Games entity:Polish_Youth_Olympic_Days entity:Winter_Dew_Tour entity:Multi-sport_event entity:Canada_Games entity:Aryan_Games entity:EuroGames_%28LGBT_sporting_event%29 entity:Arctic_Winter_Games entity:Universiade entity:AAU_Junior_Olympic_Games entity:Asian_Indoor_Games entity:Indian_Ocean_Island_Games entity:South_Asian_Games entity:State_Games_of_North_Carolina entity:California_State_Games entity:Central_Asian_Games entity:North_American_Indigenous_Games entity:Respect_Gaymes entity:LG_action_sports_world_tour entity:World_Mind_Sports_Games entity:Las_Vegas_Corporate_Challenge entity:Black_Sea_Games entity:CANUSA_Games entity:Islamic_Solidarity_Games entity:World_Interuniversity_Games entity:Spartakiad_%28Albania%29 entity:Championship entity:AFC_Youth_Championship_1988_qualification entity:Original_Mountain_Marathon entity:Chess_boxing entity:World%27s_Strongest_Woman entity:Off-road_duathlon entity:Korean_National_Sports_Festival entity:Ultraman_%28endurance_challenge%29 entity:World_Highland_Games_Championships entity:Modern_pentathlon entity:Coast_to_Coast_%28race%29 entity:Jay_Challenge entity:Decathlon entity:Women%27s_pentathlon entity:Heptathlon entity:Highland_games entity:Pentathlon entity:Triathlon entity:Adventure_racing entity:XTERRA_Triathlon entity:Multisport entity:Aquathlon entity:Military_patrol entity:Racketlon entity:Octathlon entity:Highlander_Challenge_World_Championships entity:Biathlon entity:Duathlon entity:International_King_of_Sports entity:World%27s_Strongest_Man entity:Triathlon_equipment entity:Alpine_Ironman entity:Off-road_triathlon entity:Biathle entity:Decathlon_scoring_tables entity:Triathlon_one_0_one entity:Frontier_Adventure_Sports_%26_Training entity:%C3%96_till_%C3%B6 entity:Fortissimus entity:Adventure_run entity:Ultra-triathlon entity:K%C4%B1rkp%C4%B1nar entity:Army_Navy_Match entity:Best_Comeback_Athlete_ESPY_Award entity:Best_International_Athlete_ESPY_Award entity:Best_Record-Breaking_Performance_ESPY_Award entity:FAI_World_Grand_Prix_2007 entity:FAI_World_Grand_Prix_2008 entity:GMC_Professional_Grade_Play_ESPY_Award entity:World_Air_Games entity:Under_Armour_Undeniable_Performance_ESPY_Award entity:Best_Coach/Manager_ESPY_Award entity:Kila_Raipur_Sports_Festival entity:Land_Rover_G4_Challenge entity:Sadler%27s_Ultra_Challenge entity:World_Conker_Championships entity:Open_%28sport%29 entity:Best_Moment_ESPY_Award entity:Best_Upset_ESPY_Award entity:Smack-Off entity:Damien_Knabben_Cup entity:Great_River_Race entity:Rowing_on_the_River_Thames entity:Solar_Splash entity:RoboCup entity:International_Birdman entity:World_Professional_Darts_Championship entity:Toughman_Contest entity:Sausage_Race entity:Redneck_Games entity:Best_Breakthrough_Athlete_ESPY_Award entity:Best_Championship_Performance_ESPY_Award entity:World_Radiosport_Team_Championship entity:Friendship_Radiosport_Games entity:King%27s_Cup_Elephant_Polo entity:Outstanding_Team_ESPY_Award entity:Iron_Dog entity:DODDS_European_Championships entity:FAI_World_Grand_Prix_2010-2011 entity:Ruhr"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}
