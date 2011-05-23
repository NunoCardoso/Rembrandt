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

import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.CommonClassifications as Classes
/**
 * @author Nuno Cardoso
 * bom para saber, a partir de uma expressão, qual a classe DBpedia que quero.
 * Categorias Wikipédia podem ser vistas como expressões, porque não? Mas há melhores formas de fazer...
 * com a sua ontologia.
 */
class DBpediaOntology32_to_SemanticClassification extends DBpediaOntology_to_SemanticClassification {
	
    List meanings = [ 
	// PESSOA
	[needle:["Person"], 
	answer:SemanticClassification.create(Classes.category.person) ],
	// PESSOA INDIVIDUAL
	[needle:["Actor", "AdultActor", "Architect", "Artist",	"Astronaut", 
	"Athlete", "BadmintonPlayer", "BaseballPlayer", "BasketballPlayer", "Boxer",
	"Cardinal",	"BritishRoyalty", "ChristianBishop","Cleric", "CollegeCoach", 
	"Comedian", "ComicsCharacter", "ComicsCreator", "Cricketer", "Criminal", 
	"Cyclist",  "Celebrity", "Congressman", "Chancellor", "FictionalCharacter", 
	"FigureSkater", "FormulaOneRacer", "FootballManager",  "GaelicGamesPlayer", 
	"FootballPlayer","IceHockeyPlayer", "Journalist", "Judge","Governor", 
	"Model", "Monarch", "MilitaryPerson",  "MusicalArtist", "MemberOfParliament", 
	"Mayor", "NascarDriver", "NationalCollegiateAthleticAssociationAthlete",
	"OfficeHolder", "PlayboyPlaymate", "Philosopher", "PokerPlayer", "Politician", 
	"Pope", "President", "PrimeMinister", "RugbyPlayer","Saint", 
	"Scientist", "SoccerPlayer", "Senator", "TennisPlayer", "Wrestler", 
	"Writer"], 
	answer:SemanticClassification.create(Classes.category.person, Classes.type.individual) ],
		
	// PESSOA GRUPOMEMBRO
	[needle:["Band","FootballTeam", "HockeyTeam","MilitaryUnit","SportsTeam"],
	 answer:SemanticClassification.create(Classes.category.person, Classes.type.membergroup) ],

	 // PESSOA POVO
	[needle:["EthnicGroup"],
	 answer:SemanticClassification.create(Classes.category.person, Classes.type.people) ],

	 //LOCAL 
	[needle:["Place"],
	 answer:SemanticClassification.create(Classes.category.place) ],
		
	  //LOCAL HUMANO
	[needle:["Infrastructure"],
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.human) ],

	  //LOCAL HUMANO PAIS
	[needle:["Country"], 
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.human, Classes.subtype.country) ],
		
	  //LOCAL HUMANO CONSTRUCAO
	[needle:["Airport","Building","Bridge", "HistoricBuilding", "Hospital",
	     "Lighthouse", "ShoppingMall", "Skyscraper", "Stadium", "Station"],
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.human, Classes.subtype.construction) ],

	 //LOCAL HUMANO RUA
	[needle:["Road"], 
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.human, Classes.subtype.street) ],
		
	 //LOCAL HUMANO REGIAO
	[needle:["Area","Cave",	"HistoricPlace","Park", "LunarCrater", "ProtectedArea", 
	    "SiteOfSpecialScientificInterest", "SkiArea", "WineRegion"],
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.human, Classes.subtype.humanregion) ],
	
	 //LOCAL HUMANO DIVISAO
	[needle:["City","Municipality","PopulatedPlace","WorldHeritageSite"], 
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.human, Classes.subtype.division) ],
		
	 //LOCAL FISICO RELEVO
	[needle:["Mountain"], 
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, Classes.subtype.mountain) ],
			
	 //LOCAL FISICO AGUACURSO
	[needle:["River"], 
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, Classes.subtype.watercourse) ],
				
	 //LOCAL FISICO AGUAMASSA
	[needle:["Lake"], 
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, Classes.subtype.watermass) ],

	 //LOCAL FISICO PLANETA
	[needle:["Planet"], 
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, Classes.subtype.planet) ],

	 //LOCAL FISICO ILHA
	[needle:["Island"], 
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, Classes.subtype.island) ],

	 //LOCAL VIRTUAL SITIO
	[needle:["Website"],
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, Classes.subtype.site) ],

	 //LOCAL VIRTUAL COMSOCIAL
	[needle:["Broadcast","Magazine", "Newspaper", "TelevisionShow", "RadioStation"],
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, Classes.subtype.media) ],

	 //LOCAL VIRTUAL OBRA
	[needle:["SupremeCourtOfTheUnitedStatesCase"], 
	 answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, Classes.subtype.article) ],

	 // ORGANIZACAO 
	[needle:["Organisation"],
	 answer:SemanticClassification.create(Classes.category.organization) ],
		
	 // ORGANIZACAO EMPRESA
	[needle:["Airline", "Company", "RecordLabel", "SoccerClub"],
	 answer:SemanticClassification.create(Classes.category.organization, Classes.type.company) ],
	 
	 // ORGANIZACAO INSTITUICAO
	[needle:["College","EducationalInstitution", "Non-ProfitOrganisation", "School","University"],
	 answer:SemanticClassification.create(Classes.category.organization, Classes.type.institution) ],

	 // ORGANIZACAO ADMINISTRACAO

	 // COISA CLASSE
	[needle:["Aircraft", "Automobile", "Disease", "MeanOfTransportation", "MusicGenre", "Language"],
	 answer:SemanticClassification.create(Classes.category.thing, Classes.type.'class') ],
		
	 // COISA SUBSTANCIA
	[needle:["ChemicalCompound", "Drug"],
	 answer:SemanticClassification.create(Classes.category.thing, Classes.type.substance) ],
		
	 // COISA OBJECTO
	[needle:["AnatomicalStructure", "Animal","Artery","Archaea", "Bacteria","Beverage", "Bone", "Brain",
	         "Embryology", "Eukaryote", "Fungus", "Grape", "Lymph", "Monera", "Muscle", "Legislature", 
		"Nerve","Plant", "Protein", "Protista", "Species", "TradeUnion", "Weapon", "Vein", "Ship"],
	answer:SemanticClassification.create(Classes.category.thing, Classes.type.object) ],
		
	// ACONTECIMENTO ORGANIZADO
	[needle:["Convention",	"FilmFestival", "MusicFestival", "Olympics", "MixedMartialArtsEvent", 
	         "SportsEvent",  "WrestlingEvent", "WomensTennisAssociationTournament"],
	 answer:SemanticClassification.create(Classes.category.event, Classes.type.organized) ],

	 // ACONTECIMENTO EVENTO
	[needle:["GrandPrix", "Race"],
	 answer:SemanticClassification.create(Classes.category.event, Classes.type.pastevent) ],
		
	// ACONTECIMENTO EFEMERIDE
	[needle:["MilitaryConflict"], 
	 answer:SemanticClassification.create(Classes.category.event, Classes.type.happening) ],

	 // ACONTECIMENTO
	[needle:["Event"], answer:SemanticClassification.create(Classes.category.event) ], 
		
	// OBRA 
	[needle:["Work"], answer:SemanticClassification.create(Classes.category.masterpiece) ],

	// OBRA REPRODUZIDA
	[needle:["Album","Book", "EurovisionSongContestEntry", "Film", 	"Musical", 
	         "MusicalWork", "TelevisionEpisode", "Single", "Song", 	"Software", "VideoGame"],
	 answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.reproduced) ],
	
	 //OBRA PLANO
		
	 // VALOR MOEDA
	 [needle:["Currency"],
	  answer:SemanticClassification.create(Classes.category.value, Classes.type.currency) ],

	  // VALOR CLASSIFICACAO
	 [needle:["OlympicResult"],
	  answer:SemanticClassification.create(Classes.category.value, Classes.type.classification) ]

	]
	
}
