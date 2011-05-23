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
class DBpediaOntology32 extends DBpediaOntology {
   
    Map<String,String> relations = ["Actor":"Artist", "AdultActor":"Actor", "Aircraft":"MeanOfTransportation", "Airline":"Company",
	  "Airport":"Building", "Album":"MusicalWork", "AnatomicalStructure":"Resource", "Architect":"Person",
	  "Area":"PopulatedPlace", "Artery":"AnatomicalStructure", "Artist":"Person", "Astronaut":"Person",
	  "Athlete":"Person", "Automobile":"MeanOfTransportation", "Archaea":"Species", "Animal":"Eukaryote",
	  "Bacteria":"Species","BadmintonPlayer":"Athlete", "BaseballPlayer":"Athlete", "BasketballPlayer":"Athlete",
	  "Beverage":"Resource", "Bone":"AnatomicalStructure", "Book":"Work", "Boxer":"Athlete",
	  "Brain":"AnatomicalStructure","Bridge":"Building", "BritishRoyalty":"Person",
	  "Broadcast":"Organisation", "Building":"Place", "Band":"Organisation",
	  "Cardinal":"Cleric", "Cave":"Place","ChemicalCompound":"Resource","ChristianBishop":"Cleric",
	  "City":"PopulatedPlace", "Cleric":"Person", "College":"EducationalInstitution",
	  "CollegeCoach":"Person", "Comedian":"Artist","ComicsCharacter":"FictionalCharacter",
	  "ComicsCreator":"Artist","Company":"Organisation", "Convention":"Event", "Country":"PopulatedPlace",
	  "Cricketer":"Athlete", "Criminal":"Person","Currency":"Resource", "Cyclist":"Athlete",
	  "Celebrity":"Person","Congressman":"Polititian","Chancellor":"Polititian",
	  "Diesase":"Resource", "Drug":"Resource",
	  "EducationalInstitution":"Organisation","Embryology":"AnatomicalStructure","EthnicGroup":"Resource",
	  "EurovisionSongContestEntry":"Song","Event":"Resource", "Eukaryote":"Species",
	  "FictionalCharacter":"Person","FigureSkater":"Athlete","Film":"Work","FilmFestival":"Event",
	  "FootballPlayer":"Athlete","FootballTeam":"SportsTeam","FormulaOneRacer":"Athlete","Fungus":"Eukaryote",
	  "FootballManager":"Person",
	  "GaelicGamesPlayer":"Athlete","GrandPrix":"SportsEvent","Grape":"Resource", "Governor":"Polititian",
	  "HistoricBuilding":"Building","HistoricPlace":"Place","HockeyTeam":"SportsTeam","Hospital":"Building",
	  "IceHockeyPlayer":"Athlete","Infrastructure":"Resource","Island":"Place",          
	  "Journalist":"Person","Judge":"Person",
	  "Lake":"Place","Language":"Resource","Legislature":"?","Lighthouse":"Building",
	  "LunarCrater":"Place","Lymph":"AnatomicalStructure", "Legislature":"Organisation",
	  "Magazine":"Work","MeanOfTransportation":"Resource","MilitaryConflict":"Event","MilitaryPerson":"Person",
	  "MilitaryUnit":"Organisation","MixedMartialArtsEvent":"SportsEvent","Model":"Person","Monarch":"Person",
	  "Mountain":"Place","Municipality":"PopulatedPlace","Muscle":"AnatomicalStructure","Monera":"AnatomicalStructure",
	  "MusicFestival":"Event","MusicGenre":"Resource","Musical":"Work","MusicalArtist":"Artist",
	  "MusicalWork":"Work", "MemberOfParliament":"Polititian", "Mayor":"Polititian",
	  "NascarDriver":"Athlete","NationalCollegiateAthleticAssociationAthlete":"Athlete","Nerve":"AnatomicalStructure",
	  "Newspaper":"Work","Non-ProfitOrganisation":"Organisation",
	  "OfficeHolder":"Person","OlympicResult":"Resource","Olympics":"SportsEvent","Organisation":"Resource",
	  "Park":"Place","Person":"Resource","Philosopher":"Person","Place":"Resource","Planet":"Resource",
	  "PlayboyPlaymate":"Person","PokerPlayer":"Athlete","Politician":"Person","Pope":"Cleric",
	  "PopulatedPlace":"Place","ProtectedArea":"Place","Protein":"Resource", "Plant":"Eukaryote","Protista":"Eukaryote",
	  "President":"Polititian", "PrimeMinister":"Polititian",
	  "Race":"SportsEvent","RadioStation":"Organisation","RecordLabel":"Company",
	  "River":"Place","Road":"Infrastructure","RugbyPlayer":"Athlete",
	  "Saint":"Cleric","School":"EducationalInstitution","Scientist":"Person","ShoppingMall":"Building",
	  "Single":"MusicalWork","SiteOfSpecialScientificInterest":"Place","SkiArea":"Place",
	  "Skyscraper":"Building","SoccerClub":"SportsTeam","Software":"Work","Song":"Musicalwork",
	  "Species":"Resource","SportsEvent":"Event","SportsTeam":"Organisation","Stadium":"Building",
	  "Station":"Building","SupremeCourtOfTheUnitedStatesCase":"Resource","SoccerPlayer":"Athlete",
	  "Senator":"Polititian", "Ship":"MeanOfTransportation",
	  "TelevisionEpisode":"Work","TelevisionShow":"Work","TennisPlayer":"Athlete","TradeUnion":"Organisation",
	  "University":"EducationalInstitution",
	  "Vein":"AnatomicalStructure","VideoGame":"Work",
	  "Weapon":"Resource","Website":"Resource","WineRegion":"Place","WomensTennisAssociationTournament":"SportsEvent",
	  "Work":"Resource","WorldHeritageSite":"Place","Wrestler":"Athlete","WrestlingEvent":"Event",
	  "Writer":"Artist"
	 ] 
 
 	 List<String> classes = ["Actor", "AdultActor", "Aircraft", "Airline", "Airport", "Album", "AnatomicalStructure", "Animal",
	   "Architect", "Area",	"Artery", "Artist",	"Astronaut", "Athlete","Automobile", "Archaea",
	   "Bacteria","BadmintonPlayer", "BaseballPlayer", "BasketballPlayer", "Beverage", "Bone", "Book",	"Boxer",
	   "Brain",	"Bridge", "BritishRoyalty", "Broadcast", "Building", "Band",
	   "Cardinal", "Cave", "ChemicalCompound",
	   "ChristianBishop", "City", "Cleric", "College", "CollegeCoach", "Comedian","ComicsCharacter",
	   "ComicsCreator", "Company", "Convention", "Country", "Cricketer", "Criminal", "Currency", "Cyclist",
	   "Celebrity", "Congressman", "Chancellor",
	   "Disease", "Drug",
	   "EducationalInstitution","Embryology","EthnicGroup","EurovisionSongContestEntry","Event","Eukaryote",
	   "FictionalCharacter","FigureSkater","Film","FilmFestival","FootballPlayer","FootballTeam","FormulaOneRacer",
	   "Fungus","FootballManager",
	   "GaelicGamesPlayer","GrandPrix","Grape", "Governor",
	   "HistoricBuilding","HistoricPlace","HockeyTeam","Hospital",
	   "IceHockeyPlayer","Infrastructure","Island",
	   "Journalist","Judge",
	   "Lake","Language","Legislature","Lighthouse","LunarCrater","Lymph","Legislature",
	   "Magazine","MeanOfTransportation","MilitaryConflict","MilitaryPerson","MilitaryUnit","MixedMartialArtsEvent",
	   "Model","Monarch","Mountain","Municipality","Muscle","MusicFestival","MusicGenre","Musical","MusicalArtist",
	   "MusicalWork","Monera", "MemberOfParliament","Mayor",
	   "NascarDriver","NationalCollegiateAthleticAssociationAthlete","Nerve","Newspaper","Non-ProfitOrganisation",
	   "OfficeHolder","OlympicResult","Olympics","Organisation",
	   "Park","Person","Philosopher","Place","Planet","PlayboyPlaymate","PokerPlayer","Politician","Pope",
	   "PopulatedPlace","ProtectedArea","Protein", "Plant", "Protista", "President", "PrimeMinister",
	   "Race","RadioStation","RecordLabel","Resource","River","Road","RugbyPlayer",
	   "Saint","School","Scientist","ShoppingMall","Single","SiteOfSpecialScientificInterest","SkiArea",
	   "Skyscraper","SoccerClub","Software","Song","Species","SportsEvent","SportsTeam","Stadium","Station",
	   "SupremeCourtOfTheUnitedStatesCase", "SoccerPlayer", "Senator","Ship",
	   "TelevisionEpisode","TelevisionShow","TennisPlayer","TradeUnion","Resource",
	   "University",
	   "Vein","VideoGame",
	   "Weapon","Website","WineRegion","WomensTennisAssociationTournament","Work","WorldHeritageSite","Wrestler",
	   "WrestlingEvent","Writer"
	   ]

}