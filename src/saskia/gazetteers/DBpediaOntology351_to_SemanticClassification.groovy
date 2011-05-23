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
 
package saskia.gazetteers

import rembrandt.gazetteers.CommonClassifications as SC
/**
 * @author Nuno Cardoso
 * bom para saber, a partir de uma expressão, qual a classe DBpedia que quero.
 * Categorias Wikipédia podem ser vistas como expressões, porque não? Mas há melhores formas de fazer...
 * com a sua ontologia.
 */
class DBpediaOntology351_to_SemanticClassification extends DBpediaOntology_to_SemanticClassification {
	
	List meanings = [ 
		// PESSOA
	[needle:["Person"], answer:SC.person],
	// PESSOA INDIVIDUAL
	[needle:["Actor", "AdultActor", "Ambassador", "AmericanFootballPlayer",
	"Architect", "Artist",	"Astronaut", "Athlete", "BaseballPlayer", "BasketballPlayer", "Boxer",
	"Cardinal",	"BritishRoyalty", "CanadianFootballPlayer", "ChristianBishop","Cleric", "CollegeCoach", 
	"Comedian", "ComicsCharacter", "ComicsCreator", "Cricketer", "Criminal", 
	"Cyclist",  "Celebrity", "Congressman", "Chancellor", "Deputy", "FictionalCharacter", 
	"FigureSkater", "FormulaOneRacer",  "GaelicGamesPlayer",  "Governor", "GridironFootballPlayer",
	"IceHockeyPlayer", "Journalist", "Judge","Lieutenant",
	"Model", "Monarch", "MilitaryPerson",  "MusicalArtist", "MemberOfParliament", 
	"Mayor", "NascarDriver", "NationalCollegiateAthleticAssociationAthlete",
	"OfficeHolder", "Person", "PlayboyPlaymate", "Philosopher", "PokerPlayer", "Politician", 
	"Pope", "President", "PrimeMinister", "RugbyPlayer","Saint", 
	"Scientist", "Senator", "SoccerManager", "SoccerPlayer", "TennisPlayer",
	"VicePresident","VicePrimeMinister", "VoiceActor", "Wrestler", "Writer"], 
	answer:SC.person_individual ],
	
	// PESSOA CARGO
	[needle:["PersonFunction"], answer:SC.person_position ],

	// PESSOA GRUPOMEMBRO
	[needle:["Band","SoccerClub", "HockeyTeam","MilitaryUnit","SportsTeam"],
	 answer:SC.person_membergroup ],

	 // PESSOA POVO
	[needle:["EthnicGroup"],
	 answer:SC.person_people ],

	 //LOCAL 
	[needle:["Place"],answer:SC.place ],
		
	  //LOCAL HUMANO
	[needle:["Infrastructure"], answer:SC.place_human ],

	  //LOCAL HUMANO PAIS
	[needle:["Country"], answer:SC.place_human_country ],
		
	  //LOCAL HUMANO CONSTRUCAO
	[needle:["Airport","Building","Bridge", "HistoricBuilding", "Hospital",
	     "LaunchPad","Lighthouse", "Monument","ShoppingMall", "Skyscraper", "Stadium", "Station"],
	 answer:SC.place_human_construction ],

	 //LOCAL HUMANO RUA
	[needle:["Road"], answer:SC.place_human_street ],
		
	 //LOCAL HUMANO REGIAO
	[needle:["Cave",	"HistoricPlace","Park", "LunarCrater", "ProtectedArea", 
	    "SiteOfSpecialScientificInterest", "SkiArea", "WineRegion"],
	 answer:SC.place_human_humanregion ],
	
	 //LOCAL HUMANO DIVISAO
	[needle:["City","Continent","PopulatedPlace","AdministrativeRegion","Settlement",
	"Town", "Village", "WorldHeritageSite"], 
	 answer:SC.place_human_division ],
		
	//LOCAL FISICO
	[needle:["BodyOfWater"], 
	 answer:SC.place_physical],
		
	//LOCAL FISICO REGIAO
	[needle:["Canal"], 
	 answer:SC.place_physical_physicalregion],
			
	 //LOCAL FISICO RELEVO
	[needle:["Mountain", "MountainPass", "MountainRange", "Valley"], 
	 answer:SC.place_physical_mountain ],
			
	 //LOCAL FISICO AGUACURSO
	[needle:["River","Stream"], 
	 answer:SC.place_physical_watercourse ],
				
	 //LOCAL FISICO AGUAMASSA
	[needle:["Lake"], 
	 answer:SC.place_physical_watermass ],

	 //LOCAL FISICO PLANETA
	[needle:["Planet"], 
	 answer:SC.place_physical_planet ],

	 //LOCAL FISICO ILHA
	[needle:["Island"], 
	answer:SC.place_physical_island ],

	 //LOCAL VIRTUAL SITIO
	[needle:["Website"],
	 answer:SC.place_virtual_site ],

	 //LOCAL VIRTUAL COMSOCIAL
	[needle:["Broadcast","Magazine", "Newspaper", "TelevisionShow", "RadioStation"],
	 answer:SC.place_virtual_media ],

	 //LOCAL VIRTUAL OBRA
//	[needle:["SupremeCourtOfTheUnitedStatesCase"], 
//	 answer:SC.place_virtual_article ],

	 // ORGANIZACAO 
	[needle:["Organisation"],
	 answer:SC.organization ],
		
	 // ORGANIZACAO EMPRESA
	[needle:["Airline", "AmericanFootballTeam", "CanadianFootballTeam", "Company", 
	"RecordLabel", "SoccerClub"],
	 answer:SC.organization_company ],
	 
	 // ORGANIZACAO INSTITUICAO
	[needle:["College","EducationalInstitution", "GeopoliticalOrganisation", 
	   "TradeUnion", "Non-ProfitOrganisation", "School","University"],
	 answer:SC.organization_institution ],

	 // ORGANIZACAO ADMINISTRACAO

	 // COISA CLASSE
	[needle:["Aircraft", "Automobile", "AutomobileEngine", "Disease","Device", "MeanOfTransportation", 
	"MusicGenre", "Language"], answer:SC.thing_class],
		
	 // COISA SUBSTANCIA
	[needle:["ChemicalCompound", "Drug", "Protein"],
	 answer:SC.thing_substance ],
		
	 // COISA OBJECTO
	[needle:["AnatomicalStructure", "Animal","Amphibian",
		"Arachnid","Artery","Archaea", "Bacteria","Beverage", "Bird", "Bone", "Brain","ClubMoss","Conifer",
		"Crustacean", "Cycad",  "Embryology", "Eukaryote", "Fern", "Fish", "FloweringPlant", "Fungus", 
		"Ginkgo", "Gnetophytes","GreenAlga", "Grape", "Insect", "Lymph", "Mammal","Muscle", "Mollusca",
		"Moss",	"Nerve","Plant", "Reptile", "Rocket", "Species", "SpaceShuttle", 
		"SpaceStation", "Spacecraft", "Weapon", "Vein", "Ship"],
	answer:SC.thing_object ],
		
	// ACONTECIMENTO ORGANIZADO
	[needle:["AmericanFootballLeague", "AustralianFootballLeague", "AutoRacingLeague",
	"BaseballLeague", "BasketballLeague", "BowlingLeague", "BoxingLeague","CanadianFootballLeague",
	"Convention","CricketLeague", "CurlingLeague", "CyclingLeague","FilmFestival", "FieldHockeyLeague",
	"GolfLeague", "HandballLeague", "IceHockeyLeague", "InlineHockeyLeague", "LacrosseLeague",
	"MixedMartialArtsLeague", "MotorcycleRacingLeague", "MusicFestival", "Olympics", "PaintballLeague", 
	"PoloLeague", "RadioControlledRacingLeague", "RugbyLeague", "SoccerLeague", "SoftballLeague",
	"SpeedwayLeague", "SportsLeague", "TennisLeague", "VolleyballLeague", "VideogamesLeague",
	 "WomensTennisAssociationTournament"],
	 answer:SC.event_organized ],

	 // ACONTECIMENTO EVENTO
	[needle:["GrandPrix","SportsEvent", "Race", "WrestlingEvent"],
	 answer:SC.event_pastevent ],
		
	// ACONTECIMENTO EFEMERIDE
	[needle:["MilitaryConflict"], 
	 answer:SC.event_happening ],

	 // ACONTECIMENTO
	[needle:["Event"], answer:SC.event ], 
		
	// OBRA 
	[needle:["Work", "Legislature"], answer:SC.masterpiece ],

	// OBRA REPRODUZIDA
	[needle:["Album","Book", "EurovisionSongContestEntry", "Film", 	"Musical", 
	         "MusicalWork", "TelevisionEpisode", "Single", "Song", 	"Software", "VideoGame"],
	 answer:SC.masterpiece_reproduced ],
	
	 //OBRA PLANO
	[needle:["Award","SpaceMission"],
	 answer:SC.masterpiece_plan ],
		
	 // VALOR MOEDA
	 [needle:["Currency"],
	  answer:SC.value_currency ],

	  // VALOR CLASSIFICACAO
	 [needle:["OlympicResult"],
	  answer:SC.value_classification ]

	]

	
/* Not mapped: 	
"Activity"
"Colour"
"Game"
"Sales"
"Sport"
"YearInSpaceflight"
*/



}