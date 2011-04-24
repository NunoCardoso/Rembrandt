
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
package renoir.test.geoclef.pertopic.year2005

import groovy.util.GroovyTestCase
import rembrandt.gazetteers.CommonClassifications as SC
import renoir.obj.*
import renoir.test.geoclef.pertopic.GeoCLEF_PerTopic_Test
import saskia.bin.Configuration
/**
 * @author Nuno Cardoso
 *
 */
class GeoCLEF_2005_EN_020_Test extends GroovyTestCase {

	Configuration conf
	GeoCLEF_PerTopic_Test pertopic

	public GeoCLEF_2005_EN_020_Test() {
		conf = Configuration.newInstance()
		//	conf.set("saskia.dbpedia.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
		//	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
		//	println "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")
		pertopic = new GeoCLEF_PerTopic_Test(conf)
	}

	void testTopic() {
		String lang = "en"
		// hack: mudar Islands para islands
		String topic = "label:020 Wind power in the islands of Scotland"

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)

		/** check sentences and question types */
		assert question.sentence*.text == [
			'Wind',
			'power',
			'in',
			'the',
			'islands',
			'of',
			'Scotland'
		]
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0


		// check detected NEs
		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ["Scotland"]

		/// contains gives false. Use find
		assert question.nes[0].classification.find{it == SC.place_physical_island}

		// check subjects
		assert !question.subject

		/*
		 Scottish Islands é um NE LOCAL, portanto faz match no 3.1.1 in the {place}
		 mas também faz no 3.1.2, como sendo subject.
		 */

		// check conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof Subject
		assert question.conditions[0].object.subjectTerms*.text == ["islands"]
		assert question.conditions[0].object.geoscopeTerms*.text == ["Scotland"]


		assert !question.expectedAnswerTypes //*.DBpediaOntologyResources == [[]]

		// check answers
		assert !question.answer

		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)

		println refq.toString()
		String reformulated_x = """
label:020 contents:Wind contents:power contents:islands contents:"Scotland" ne-LOCAL-FISICO-ILHA:"Scotland" entity:Great_Cumbrae entity:Little_Cumbrae entity:The_Eileans entity:Holy_Isle%2C_Firth_of_Clyde entity:Sheep_Island%2C_Argyll entity:Pladda entity:Eilean_Dubh%2C_Kyles_of_Bute woeid:12480559 entity:Islands_of_the_Clyde entity:Burnt_Islands entity:Horse_Isle entity:Lady_Isle entity:Sgat_M%C3%B2r_and_Sgat_Beag entity:Castle_Island%2C_Scotland entity:Isle_of_Bute entity:Sanda_Island entity:Davaar_Island entity:Ailsa_Craig entity:The_Cumbraes entity:Inchmarnock entity:Glunimore_Island entity:Eilean_Dearg%2C_Loch_Riddon entity:Little_Cumbrae_Castle entity:Sinclair_Breweries entity:North_Ronaldsay_sheep entity:Scapa entity:The_Raven_Banner entity:Anastrepta_orcadensis entity:Insular_Scots entity:Cormack_%28surname%29 entity:Orkney entity:Pentland_Firth entity:Henry_I_Sinclair%2C_Earl_of_Orkney entity:BBC_Radio_Orkney entity:Ward_Hill%2C_Hoy entity:Highlands_and_Islands_Fire_and_Rescue_Service entity:Orkney_Herald entity:Happy_Valley_%28garden%29 entity:Highland_Brewing_Company_%28Orkney%29 woeid:36508 entity:Scapa_Flow entity:Northern_Constabulary entity:Stronsay_Beast entity:Orcadian_dialect entity:Orkney_vole entity:The_Superstation_Orkney entity:Bere_%28grain%29 entity:Old_Man_of_Hoy entity:Udal_law entity:Gutter_Sound entity:Flag_of_Orkney entity:Lord_Lieutenant_of_Orkney_and_Shetland entity:Churchill_Barriers entity:The_Orcadian entity:Loch_of_Stenness entity:Yesnaby entity:Orkney_Tunnel entity:Geology_of_Orkney entity:Olvir_Rosta entity:Fidra entity:The_Lamb_%28island%29 entity:Cramond_Island entity:Rosyth_Castle entity:Bass_Rock entity:Inchkeith entity:Craigleith entity:Alloa_Inch entity:Inchgarvie entity:Eyebroughy entity:Islands_of_the_Forth entity:Inchcolm entity:Isle_of_May entity:Inchmickery entity:Preston_Island%2C_Fife entity:Hirta entity:Soay%2C_St_Kilda entity:History_of_St_Kilda entity:Stac_Biorach entity:St_Kilda_Wren entity:Rocabarraigh entity:Stac_an_Armin entity:Stac_Levenish entity:St_Kilda%2C_Scotland entity:The_Edge_of_the_World entity:Boreray%2C_St_Kilda entity:St_Kilda_House_Mouse entity:D%C3%B9n%2C_St_Kilda entity:St_Kilda_Field_Mouse entity:Stac_Lee entity:Eilean_nan_R%C3%B2n entity:Am_Buachaille entity:Calbha_Beag entity:Rabbit_Islands%2C_Scotland entity:Handa%2C_Scotland entity:Oldany_Island entity:Eilean_Hoan entity:Neave_Island entity:Eilean_Choraidh entity:Eilean_an_R%C3%B2in_M%C3%B2r entity:Islands_of_Fleet entity:Little_Ross entity:Hestan_Island entity:Rough_Island%2C_Scotland woeid:2427392 entity:Gigha entity:Muck%2C_Scotland entity:Oronsay%2C_Inner_Hebrides entity:Eigg entity:Sanday%2C_Inner_Hebrides entity:Coll entity:Tiree entity:Ulva entity:Seil entity:Dubh_Artach entity:Inch_Kenneth entity:Isay entity:Eorsa entity:Garbh_Eileach entity:Ascrib_Islands entity:Eilean_Mhic_Coinnich entity:Gunna entity:Hinba entity:Pabay entity:Texa woeid:12480954 entity:Raasay entity:Lismore%2C_Scotland entity:Canna%2C_Scotland entity:Scalpay%2C_Inner_Hebrides entity:Slate_Islands entity:Scarba entity:Treshnish_Isles entity:Erraid entity:Fladda entity:Lampay entity:Longay entity:MacCormaig_Isles entity:Mingay entity:Nave_Island entity:Ornsay entity:Jura%2C_Scotland entity:Small_Isles entity:Soay%2C_Skye entity:South_Rona entity:Garvellachs entity:Garbh_Sgeir entity:Eilean_Donan entity:Eilean_Chathastail entity:Cara_Island entity:Crowlin_Islands entity:Skerryvore entity:B8045_road entity:Eilean_R%C3%ACgh entity:C%C3%A0rna entity:Danna%2C_Scotland entity:Eilean_Shona entity:Gigalum_Island entity:Island_Macaskin entity:Eilean_M%C3%B2r%2C_Crowlin_Islands entity:Fladda-ch%C3%B9ain entity:Bernera%2C_Lismore entity:Lady%27s_Rock entity:Gruinard_Island woeid:12481059 entity:R%C3%B9m entity:Kerrera entity:Gometra entity:Hyskeir entity:Little_Colonsay entity:Wiay%2C_Skye entity:Eileach_an_Naoimh entity:Eilean_B%C3%A0n%2C_Lochalsh entity:Orsay%2C_Inner_Hebrides entity:Eilean_Dubh_M%C3%B2r entity:Isle_of_Ewe entity:Calve_Island entity:Colonsay entity:Samalan_Island entity:Shuna%2C_Loch_Linnhe entity:Eilean_Trodday entity:Harlosh_Island entity:Eilean_Fladday entity:Insh%2C_Slate_Islands entity:Eilean_Mhic_Chrion entity:Eilean_Ighe entity:Tarner_Island entity:Eilean_Tigh entity:Easteray entity:Eilean_Ornsay entity:Torran_Rocks entity:Eilean_Horrisdale entity:South_Ascrib entity:Brough_of_Birsay entity:Torsa entity:Huney entity:Eriska entity:Helliar_Holm entity:Baleshare entity:Black_Holm entity:Brei_Holm entity:Ceann_Ear entity:Ceann_Iar entity:Corn_Holm entity:Kili_Holm entity:Soay_Beag entity:Uyea%2C_Northmavine entity:Isle_Ristol entity:West_Head_of_Papa entity:Sibhinis entity:Stromay entity:Keith_Inch entity:Broch_of_Clickimin entity:Morrich_More entity:Viking_Bergen_Island entity:Neish_Island entity:Cherry_Island_%28Loch_Ness%29 entity:Dunglass_Island entity:Rainish_Eilean_M%C3%B2r entity:Eilean_M%C3%B2r%2C_Loch_Langavat entity:Stroma%2C_Scotland entity:P%26O_Scottish_Ferries entity:1669_Act_for_annexation_of_Orkney_and_Shetland_to_the_Crown entity:Northern_Isles entity:Northlink_Ferries woeid:12481092 entity:Shetland entity:Pentland_Ferries entity:Sea_of_the_Hebrides entity:Fir_Bhreige entity:Barpa_Langass entity:Erskine_Beveridge entity:Hebrides entity:A_Journey_to_the_Western_Islands_of_Scotland entity:Hebrides_Overture entity:Pentland_Skerries entity:Holm_of_Papa entity:Longa_Island entity:Oronsay%2C_Loch_Bracadale entity:Monach_Islands entity:North_Rona entity:Barra_Head entity:Pabbay%2C_Barra%2C_Scotland entity:Muckle_Skerry entity:Sula_Sgeir entity:Holm_of_Houton entity:Oronsay%2C_Loch_Sunart entity:Rockall entity:Belnahua entity:Mingulay entity:Sandray entity:Isle_Martin entity:Haskeir entity:Horse_Island%2C_Summer_Isles entity:Glencripesdale_Estate entity:Orosay entity:Innis_Mh%C3%B2r entity:Eilean_M%C3%B2r%2C_Loch_Dunvegan entity:Flodaigh%2C_Lewis entity:SIBC entity:Belmont%2C_Shetland entity:Bressay_transmitting_station entity:Burra entity:The_Pirate_%28novel%29 entity:Shetland_cattle entity:It%27s_Nice_Up_North entity:Skeld entity:Shetland_Museum entity:Shetland_%28sheep%29 entity:The_New_Shetlander entity:Blackwood_%28whisky_distillery%29 entity:North_Isles entity:Bixter entity:Central_Mainland entity:El_Gran_Grif%C3%B3n entity:Literature_of_Shetland entity:Lunnasting_stone entity:Willie_Hunter entity:Shetland_Islands_Council entity:North_Mainland entity:South_Mainland entity:Grice_%28extinct_pig%29 entity:Virkie entity:Toab%2C_Shetland entity:Heritage_Fiddlers entity:The_Forty_Fiddlers entity:Crown_Dependency_of_Forvik entity:Stuart_Hill_%28sailor%29 entity:Sullom_Voe entity:Fort_Charlotte_%28Shetland%29 entity:Sumburgh_Head entity:Burra_Voe entity:Burrastow entity:Busta_Voe entity:Lord_Lieutenant_of_Shetland entity:Shetlandic entity:Levenwick entity:Scousburgh entity:Sodom%2C_Shetland entity:Uyeasound entity:Lunna_Ness entity:The_Shetland_Times entity:West_Voe_of_Sumburgh entity:Ronas_Hill entity:Shetland_Goose entity:Bongshang entity:Weisdale_Mill entity:Lerwick_Town_Hall entity:Young_Fiddler_of_the_Year woeid:26132 entity:Lochs_of_Spiggie_and_Brow entity:Flag_of_Shetland entity:Grutness entity:BBC_Radio_Shetland entity:Burravoe entity:Burwick entity:B%C3%B6d_of_Gremista entity:Mailand%2C_Shetland entity:Exnaboe entity:Mavis_Grind entity:Old_Haa_Museum entity:Pool_of_Virkie entity:West_Mainland entity:ZE_postcode_area entity:Sandness entity:Shetland_animal_breeds entity:Valhalla_Brewery entity:Vagaland entity:Sixareen entity:Garrison_Theatre entity:Sanday_Light_Railway entity:Hagdale_Chromate_Railway entity:Isle_of_Mull_Railway entity:Lealt_Valley_Diatomite_Railway entity:Rothesay_and_Ettrick_Bay_Light_Railway entity:Foula entity:Mainland%2C_Shetland entity:Unst woeid:12480824 woeid:862338 entity:Vaila entity:Papa_Little entity:Oxna entity:St_Ninian%27s_Isle entity:Burwick_Holm entity:Isle_of_Stenness entity:Linga%2C_Muckle_Roe entity:Linga%2C_Samphrey entity:Holm_of_Noss entity:Langa%2C_Shetland entity:Little_Holm%2C_Scatness entity:Broch_of_West_Burrafirth entity:Yell%2C_Shetland entity:Fetlar entity:Gruney entity:Bound_Skerry entity:Papa%2C_Shetland entity:Housay entity:Bruray entity:Brough_Holm entity:Colsay entity:Grunay entity:Lamba_%28island%29 entity:Papa_Stour entity:West_Burra entity:East_Burra entity:Muckle_Roe entity:Uyea%2C_Unst entity:South_Havra entity:Out_Stack entity:Muckle_Flugga entity:Bigga%2C_Shetland entity:East_Linga entity:Dore_Holm entity:Gloup_Holm entity:Grif_Skerry entity:Horse_Holm entity:Little_Roe entity:Fish_Holm entity:Lunna_Holm entity:West_Linga entity:Scalloway_Islands entity:Sound_Gruney entity:Bressay entity:Whalsay entity:Out_Skerries entity:Balta%2C_Shetland entity:Hascosay entity:Hildasay entity:Vementry entity:Trondra entity:Brother_Isle entity:Linga%2C_Yell entity:Cheynies entity:Samphrey entity:Orfasay entity:Forewick_Holm entity:Uynarey entity:Urie_Lingey entity:Outer_Hebrides entity:Eriskay_Pony entity:Virtual_Hebrides entity:Point%2C_Outer_Hebrides entity:Hebridean_Brewing_Company entity:Loch_Seaforth entity:Lord_Lieutenant_of_the_Western_Isles entity:Hebridean_Celtic_Festival entity:Lewis_Camanachd entity:Uist_Camanachd entity:Traigh_Mh%C3%B2r entity:Bernera_Riot entity:Harris_Tweed entity:Ardmore%2C_Barra entity:Lewis_War_Memorial entity:Flora_and_fauna_of_the_Outer_Hebrides entity:Castlebay entity:Aird_an_R%C3%B9nair entity:Ardhasaig entity:Ceann_a%27_Gh%C3%A0raidh entity:Portvoller entity:Kirkibost entity:Mairi%27s_Wedding entity:West_Loch_Tarbert entity:Soay_Sound entity:Island_of_Rockall_Act_1972 entity:Bishop_of_the_Isles entity:East_Loch_Tarbert entity:Religion_in_the_Outer_Hebrides entity:Harris%2C_Outer_Hebrides woeid:12590460 woeid:464763 entity:Great_Bernera entity:Berneray%2C_North_Uist entity:Shiant_Isles entity:Ronay entity:Eilean_Glas%2C_Scalpay entity:Tahay entity:Pabbay%2C_Harris entity:Eilean_Mhuire entity:Fuday entity:Fuiay entity:Little_Bernera entity:Flodaigh_M%C3%B2r entity:Fuaigh_M%C3%B2r entity:Grimsay%2C_South_East_Benbecula entity:Lewis_and_Harris entity:Vallay entity:Scarp%2C_Scotland entity:Eilean_D%C3%B2mhnuill entity:Bayble_Island entity:Seaforth_Island entity:Calvay entity:Fuaigh_Beag entity:Gighay entity:Ensay%2C_Outer_Hebrides entity:Stuley entity:Vacsay entity:North_Uist entity:Benbecula entity:South_Uist entity:Boreray%2C_North_Uist entity:Sgeotasaigh entity:Eilean_Chaluim_Chille entity:Eilean_Chearstaidh entity:Eilean_Liubhaird entity:Eilean_Mhealasta entity:Eilean_an_Taighe entity:Hermetray entity:Killegray entity:Flodday%2C_Sound_of_Barra entity:Sgarabhaigh entity:Oronsay%2C_Outer_Hebrides entity:Eriskay entity:Taransay entity:Vatersay entity:Grimsay entity:Scalpay%2C_Outer_Hebrides entity:Flannan_Isles entity:Shillay entity:Fiaraidh entity:Eileanan_Iasgaich entity:Flodaigh entity:Garbh_Eilean entity:Hellisay entity:Stockinish_Island entity:Pabaigh_M%C3%B2r entity:Soay_M%C3%B2r entity:Wiay%2C_Uist entity:Lingay%2C_Fiaray entity:Ceabhaigh entity:Fraoch-Eilean entity:An_Sgurr_%28Eigg%29 entity:Beinn_Ruigh_Choinnich entity:Caisteal_Abhail entity:Beinn_Shiantaidh entity:C%C3%ACr_Mh%C3%B2r entity:Ben_More_%28Mull%29 entity:Beinn_an_%C3%92ir entity:Beinn_Tarsuinn_%28Corbett%29 entity:Goat_Fell entity:Beinn_a%27_Chaolais entity:Askival entity:Beinn_Bheigeir entity:Clisham entity:Beinn_Mh%C3%B2r entity:Roineabhal entity:Eilean_Fraoch entity:Holm_%28island%29 entity:Flodday_%28disambiguation%29 entity:Cen%C3%A9l entity:Oronsay entity:Loch_Coruisk entity:Finlaggan entity:Clickimin_Loch entity:Loch_Fada%2C_Colonsay entity:Loch_an_Sgoltaire entity:Loch_Sgadabhagh entity:Loch_Langavat entity:Mugdrum_Island entity:Inchcape entity:Rona entity:Clett entity:Craiglethy entity:Scottish_Islands_Federation
"""
		assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"")
	}
}
