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
package saskia.dbpedia

/** This class has utility methods around the DBpedia ontology. */

/**
  *  Differences:

*  Eliminated Resource, new Thing

  *  BadmintonPlayer,FootballManager, FootballTeam, FootballPlayer, Area, Municipality,
  *  Monera, Protista, was eliminated
  *  SoccerManager, SoccerPlayer, SoccerClub(?), "Activity", "AdministrativeRegion",
  * "Ambassador", "AmericanFootballLeague", "AmericanFootballPlayer", "AmericanFootballTeam",
  * "Amphibian", "Arachnid", "AustralianFootballLeague", "AutoRacingLeague", "AutomobileEngine",
  * "Award", "BaseballLeague", "BasketballLeague", "Bird", "BodyOfWater", "BowlingLeague",
  * "BoxingLeague", "CanadianFootballLeague", "CanadianFootballPlayer", "CanadianFootballTeam",
  * "Canal", "ClubMoss", "Colour", "Conifer", "Continent", "CricketLeague", "Crustacean", "CurlingLeague",
  * "Cycad", "CyclingLeague", "Deputy", "Device", "Fern", "FieldHockeyLeague", "Fish", "FloweringPlant",
  * "Game", "GeopoliticalOrganisation", "Ginkgo","Gnetophytes","GolfLeague", "GreenAlga","GridironFootballPlayer",
  * "HandballLeague", "IceHockeyLeague", "InlineHockeyLeague", "Insect", "LacrosseLeague", "LaunchPad",,
  * "Lieutenant", "Mammal", "MixedMartialArtsLeague", "Mollusca", "Monument", "Moss", "MotorcycleRacingLeague",
  * "MountainPass", "MountainRange", "PaintballLeague", "Person", "PersonFunction", "PoloLeague", 
  * "RadioControlledRacingLeague", "Reptile", "Rocket", "RugbyLeague", "Sales", "Settlement", "SoccerLeague",
  * "SoftballLeague", "SpaceMission", "SpaceShuttle", "SpaceStation", "Spacecraft","SpeedwayLeague",
  * "Sport", "SportsLeague", "Stream", "TennisLeague","Town","Valley","VicePresident","VicePrimeMinister",
  * "VideogamesLeague" ,"Village", "VoiceActor", "VolleyballLeague", "YearInSpaceflight" was added
  *
  */
class DBpediaOntology351 extends DBpediaOntology {

  

	   Map<String,String> relations = [
"Species":"Thing", "Place":"Thing",  "Organisation":"Thing", "Website":"Thing", 
"ChemicalCompound":"Thing", "Disease":"Thing", "OlympicResult":"Thing", "Award":"Thing", 
"Infrastructure":"Thing", "Person":"Thing", "Currency":"Thing", "Drug":"Thing", 
"Language":"Thing", "Work":"Thing", "Activity":"Thing", "MusicGenre":"Thing", 
"Planet":"Thing", "Colour":"Thing", "MeanOfTransportation":"Thing", "EthnicGroup":"Thing", 
"PersonFunction":"Thing", "Device":"Thing", "SupremeCourtOfTheUnitedStatesCase":"Thing",
"Event":"Thing", "Sales":"Thing", "Protein":"Thing", "Beverage":"Thing", "AnatomicalStructure":"Thing",
"Actor":"Artist",
"AdministrativeRegion":"PopulatedPlace",  "AdultActor":"Actor", "Aircraft":"MeanOfTransportation",  "Airline":"Company",
"Airport":"Building",     "Album":"MusicalWork",     "Ambassador":"Person",     "AmericanFootballLeague":"SportsLeague",
"AmericanFootballPlayer":"GridironFootballPlayer",       "AmericanFootballTeam":"SportsTeam",      "Amphibian":"Animal",
"Animal":"Eukaryote",  "Arachnid":"Animal",  "Archaea":"Species", "Architect":"Person",  "Artery":"AnatomicalStructure",
"Artist":"Person",       "Astronaut":"Person",      "Athlete":"Person",       "AustralianFootballLeague":"SportsLeague",
"AutoRacingLeague":"SportsLeague",           "Automobile":"MeanOfTransportation",           "AutomobileEngine":"Device",
"Bacteria":"Species",      "BadmintonPlayer":"Athlete",     "Band":"Organisation",      "BaseballLeague":"SportsLeague",
"BaseballPlayer":"Athlete",     "BasketballLeague":"SportsLeague",    "BasketballPlayer":"Athlete",     "Bird":"Animal",
"BodyOfWater":"Place",  "Bone":"AnatomicalStructure", "Book":"Work",  "BowlingLeague":"SportsLeague", "Boxer":"Athlete",
"BoxingLeague":"SportsLeague",     "Brain":"AnatomicalStructure",    "Bridge":"Building",     "BritishRoyalty":"Person",
"Broadcast":"Organisation",                 "Building":"Place",                 "CanadianFootballLeague":"SportsLeague",
"CanadianFootballPlayer":"GridironFootballPlayer",         "CanadianFootballTeam":"SportsTeam",        "Canal":"Stream",
"Cardinal":"Cleric",   "Cave":"Place",   "Celebrity":"Person",  "Chancellor":"Politician",   "ChristianBishop":"Cleric",
"City":"Settlement", "Cleric":"Person", "ClubMoss":"Plant", "College":"EducationalInstitution", "CollegeCoach":"Person",
"Comedian":"Artist",    "ComicsCharacter":"FictionalCharacter",   "ComicsCreator":"Artist",    "Company":"Organisation",
"Congressman":"Politician",        "Conifer":"Plant",         "Continent":"PopulatedPlace",        "Convention":"Event",
"Country":"PopulatedPlace",       "CricketLeague":"SportsLeague",      "Cricketer":"Athlete",       "Criminal":"Person",
"Crustacean":"Animal",       "CurlingLeague":"SportsLeague",      "Cycad":"Plant",       "CyclingLeague":"SportsLeague",
"Cyclist":"Athlete", "Deputy":"Politician", "EducationalInstitution":"Organisation", "Embryology":"AnatomicalStructure",
"Eukaryote":"Species",      "EurovisionSongContestEntry":"Song",     "Fern":"Plant",      "FictionalCharacter":"Person",
"FieldHockeyLeague":"SportsLeague",  "FigureSkater":"Athlete",  "Film":"Work", "FilmFestival":"Event",  "Fish":"Animal",
"FloweringPlant":"Plant",     "FormulaOneRacer":"Athlete",      "Fungus":"Eukaryote",     "GaelicGamesPlayer":"Athlete",
"Game":"Activity",       "GeopoliticalOrganisation":"Organisation",       "Ginkgo":"Plant",       "Gnetophytes":"Plant",
"GolfLeague":"SportsLeague",      "Governor":"Politician",     "GrandPrix":"SportsEvent",      "Grape":"FloweringPlant",
"GreenAlga":"Plant", "GridironFootballPlayer":"Athlete", "HandballLeague":"SportsLeague", "HistoricBuilding":"Building",
"HistoricPlace":"Place",     "HockeyTeam":"SportsTeam",     "Hospital":"Building",     "IceHockeyLeague":"SportsLeague",
"IceHockeyPlayer":"Athlete",    "InlineHockeyLeague":"SportsLeague",    "Insect":"Animal",    "Island":"PopulatedPlace",
"Journalist":"Person", "Judge":"Person",  "LacrosseLeague":"SportsLeague", "Lake":"BodyOfWater", "LaunchPad":"Building",
"Legislature":"Organisation",      "Lieutenant":"Politician",       "Lighthouse":"Building",      "LunarCrater":"Place",
"Lymph":"AnatomicalStructure",           "Magazine":"Work",           "Mammal":"Animal",           "Mayor":"Politician",
"MemberOfParliament":"Politician", "MilitaryConflict":"Event", "MilitaryPerson":"Person", "MilitaryUnit":"Organisation",
"MixedMartialArtsEvent":"SportsEvent",  "MixedMartialArtsLeague":"SportsLeague", "Model":"Person",  "Mollusca":"Animal",
"Monarch":"Person",  "Monument":"Place",  "Moss":"Plant",  "MotorcycleRacingLeague":"SportsLeague",  "Mountain":"Place",
"MountainPass":"Place",      "MountainRange":"Place",      "Muscle":"AnatomicalStructure",      "MusicFestival":"Event",
"Musical":"Work",           "MusicalArtist":"Artist",          "MusicalWork":"Work",           "NascarDriver":"Athlete",
"NationalCollegiateAthleticAssociationAthlete":"Athlete",       "Nerve":"AnatomicalStructure",       "Newspaper":"Work",
"Non-ProfitOrganisation":"Organisation",                "OfficeHolder":"Person",               "Olympics":"SportsEvent",
"PaintballLeague":"SportsLeague",          "Park":"Place",         "Philosopher":"Person",          "Plant":"Eukaryote",
"PlayboyPlaymate":"Person", "PokerPlayer":"Person", "Politician":"Person", "PoloLeague":"SportsLeague", "Pope":"Cleric",
"PopulatedPlace":"Place",      "President":"Politician",     "PrimeMinister":"Politician",      "ProtectedArea":"Place",
"Race":"SportsEvent",            "RadioControlledRacingLeague":"SportsLeague",            "RadioStation":"Organisation",
"RecordLabel":"Company", "Reptile":"Animal", "River":"Stream", "Road":"Infrastructure", "Rocket":"MeanOfTransportation",
"RugbyLeague":"SportsLeague",     "RugbyPlayer":"Athlete",      "Saint":"Cleric",     "School":"EducationalInstitution",
"Scientist":"Person",     "Senator":"Politician",      "Settlement":"PopulatedPlace",     "Ship":"MeanOfTransportation",
"ShoppingMall":"Building",    "Single":"MusicalWork",   "SiteOfSpecialScientificInterest":"Place",    "SkiArea":"Place",
"Skyscraper":"Building",     "SoccerClub":"SportsTeam",     "SoccerLeague":"SportsLeague",     "SoccerManager":"Person",
"SoccerPlayer":"Athlete",        "SoftballLeague":"SportsLeague",        "Software":"Work",        "Song":"MusicalWork",
"SpaceMission":"Event",           "SpaceShuttle":"MeanOfTransportation",          "SpaceStation":"MeanOfTransportation",
"Spacecraft":"MeanOfTransportation",    "SpeedwayLeague":"SportsLeague",   "Sport":"Activity",    "SportsEvent":"Event",
"SportsLeague":"Organisation",       "SportsTeam":"Organisation",      "Stadium":"Building",       "Station":"Building",
"Stream":"BodyOfWater",     "TelevisionEpisode":"Work",      "TelevisionShow":"Work",     "TennisLeague":"SportsLeague",
"TennisPlayer":"Athlete",   "Town":"Settlement",   "TradeUnion":"Organisation",   "University":"EducationalInstitution",
"Valley":"Place",    "Vein":"AnatomicalStructure",    "VicePresident":"Politician",    "VicePrimeMinister":"Politician",
"VideoGame":"Software",      "VideogamesLeague":"SportsLeague",       "Village":"Settlement",      "VoiceActor":"Actor",
"VolleyballLeague":"SportsLeague",                        "Weapon":"Device",                       "WineRegion":"Place",
"WomensTennisAssociationTournament":"SportsEvent",           "WorldHeritageSite":"Place",          "Wrestler":"Athlete",
"WrestlingEvent":"SportsEvent",                      "Writer":"Artist",                      "YearInSpaceflight":"Event"
	 ] 
 
     List<String> classes = [
"Thing", "Activity",  "Actor", "AdministrativeRegion",  "AdultActor",  "Aircraft", "Airline",  "Airport", "Album",  "Ambassador",
"AmericanFootballLeague",   "AmericanFootballPlayer",    "AmericanFootballTeam",   "Amphibian",   "AnatomicalStructure",
"Animal", "Arachnid",  "Archaea", "Architect",  "Artery", "Artist", "Astronaut",  "Athlete", "AustralianFootballLeague",
"AutoRacingLeague", "Automobile", "AutomobileEngine", "Award",  "Bacteria", "BadmintonPlayer", "Band", "BaseballLeague",
"BaseballPlayer",   "BasketballLeague",  "BasketballPlayer",   "Beverage",   "Bird",   "BodyOfWater",  "Bone",   "Book",
"BowlingLeague",   "Boxer",    "BoxingLeague",   "Brain",    "Bridge",   "BritishRoyalty",    "Broadcast",   "Building",
"CanadianFootballLeague",  "CanadianFootballPlayer", "CanadianFootballTeam",  "Canal", "Cardinal",  "Cave", "Celebrity",
"Chancellor", "ChemicalCompound", "ChristianBishop", "City",  "Cleric", "ClubMoss", "College", "CollegeCoach", "Colour",
"Comedian",  "ComicsCharacter",   "ComicsCreator",  "Company",  "Congressman",  "Conifer",   "Continent",  "Convention",
"Country",   "CricketLeague",    "Cricketer",   "Criminal",   "Crustacean",   "CurlingLeague",    "Currency",   "Cycad",
"CyclingLeague",   "Cyclist",   "Deputy",   "Device",   "Disease",   "Drug",   "EducationalInstitution",   "Embryology",
"EthnicGroup",  "Eukaryote", "EurovisionSongContestEntry",  "Event", "Fern",  "FictionalCharacter", "FieldHockeyLeague",
"FigureSkater",  "Film", "FilmFestival",  "Fish",  "FloweringPlant",  "FormulaOneRacer", "Fungus",  "GaelicGamesPlayer",
"Game",   "GeopoliticalOrganisation",  "Ginkgo",   "Gnetophytes",   "GolfLeague",   "Governor",  "GrandPrix",   "Grape",
"GreenAlga", "GridironFootballPlayer", "HandballLeague",  "HistoricBuilding", "HistoricPlace", "HockeyTeam", "Hospital",
"IceHockeyLeague", "IceHockeyPlayer", "Infrastructure", "InlineHockeyLeague", "Insect", "Island", "Journalist", "Judge",
"LacrosseLeague", "Lake",  "Language", "LaunchPad",  "Legislature", "Lieutenant", "Lighthouse",  "LunarCrater", "Lymph",
"Magazine",  "Mammal",  "Mayor",  "MeanOfTransportation",  "MemberOfParliament",  "MilitaryConflict",  "MilitaryPerson",
"MilitaryUnit", "MixedMartialArtsEvent",  "MixedMartialArtsLeague", "Model", "Mollusca", "Monarch",  "Monument", "Moss",
"MotorcycleRacingLeague",   "Mountain",  "MountainPass",   "MountainRange",  "Muscle",   "MusicFestival",  "MusicGenre",
"Musical",  "MusicalArtist",  "MusicalWork",  "NascarDriver",  "NationalCollegiateAthleticAssociationAthlete",  "Nerve",
"Newspaper", "Non-ProfitOrganisation",  "OfficeHolder", "OlympicResult", "Olympics",  "Organisation", "PaintballLeague",
"Park",  "Person",  "PersonFunction",  "Philosopher",  "Place",  "Planet",  "Plant",  "PlayboyPlaymate",  "PokerPlayer",
"Politician", "PoloLeague", "Pope", "PopulatedPlace",  "President", "PrimeMinister", "ProtectedArea", "Protein", "Race",
"RadioControlledRacingLeague",  "RadioStation",  "RecordLabel",  "Reptile", "River",  "Road",  "Rocket",  "RugbyLeague",
"RugbyPlayer",  "Saint", "Sales",  "School",  "Scientist", "Senator",  "Settlement",  "Ship", "ShoppingMall",  "Single",
"SiteOfSpecialScientificInterest",    "SkiArea",    "Skyscraper",   "SoccerClub",    "SoccerLeague",    "SoccerManager",
"SoccerPlayer",  "SoftballLeague", "Software",  "Song",  "SpaceMission",  "SpaceShuttle", "SpaceStation",  "Spacecraft",
"Species",  "SpeedwayLeague",  "Sport", "SportsEvent",  "SportsLeague",  "SportsTeam",  "Stadium", "Station",  "Stream",
"SupremeCourtOfTheUnitedStatesCase",  "TelevisionEpisode",  "TelevisionShow",  "TennisLeague",  "TennisPlayer",  "Town",
"TradeUnion",  "University", "Valley",  "Vein", "VicePresident",  "VicePrimeMinister", "VideoGame",  "VideogamesLeague",
"Village",  "VoiceActor", "VolleyballLeague",  "Weapon",  "Website", "WineRegion",  "WomensTennisAssociationTournament",
"Work",    "WorldHeritageSite",    "Wrestler",    "WrestlingEvent",    "Writer",    "YearInSpaceflight"
	   ]

}
