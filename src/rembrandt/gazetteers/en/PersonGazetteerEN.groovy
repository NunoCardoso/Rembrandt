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
 
package rembrandt.gazetteers.en

import rembrandt.obj.*

/**
 * @author Nuno Cardoso
 * Gazetteers for PERSON in English
 */
class PersonGazetteerEN {
 
   // to be included in the NE
   static final cargoIncludeSingle = [~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Aa]mbassador/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Bb]oss/,
	~/(?:[Ee]x)?-?Bishop/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Cc]hanceler/,~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Cc]hief/,
	~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Cc]ount(?:ess)?/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Cc]onsul/,
	~/(?:[Ee]x)?-?[Cc]zar/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Dd]irector/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Gg]eneral-[Dd]irector/,
	~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Dd]uke/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Dd]uchess/,
	~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Gg]overnor/,~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Hh]igh-[Cc]omissioner/,
	~/(?:[Ee]x)?-?[Kk]ing/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Ll]eader/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Mm]arquis/,
	~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Mm]archioness/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Mm]inister/,~/(?:[Ee]x)?-?[Pp]riest/,
	~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Pp]resident/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Pp]rime-[Mm]inister/,
	~/(?:[Ee]x)?-?[Pp]rince(?:ss)?/,~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Pp]rincipal/, ~/(?:[Ee]x)?-?[Qq]ueen/,
	~/(?:[Ee]x)?-?[Rr]abbi/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Ss]ecretary/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Ss]pokes(?:wo)?man/,
	~/(?:[Ee]x)?-?[Ss]ultan/, ~/(?:[Ee]x)?-?viscount(?:ess)?/ ]
   
   // to be included in the NE
   static final cargoIncludePlural = [~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Aa]mbassadors/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Bb]osses/,
	~/(?:[Ee]x)?-?Bishops/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Cc]hancelers/,~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Cc]hiefs/,
	~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Cc]ount(?:esse)?s/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Cc]onsuls/,
	~/(?:[Ee]x)?-?[Cc]zars/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Dd]irectors/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Gg]eneral-[Dd]irectors/,
	~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Dd]ukes/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Dd]uchesses/,
	~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Gg]overnors/,~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Hh]igh-[Cc]omissioners/,
	~/(?:[Ee]x)?-?[Kk]ings/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Ll]eaders/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Mm]arquises/,
	~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Mm]archionesses/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Mm]inisters/,~/(?:[Ee]x)?-?[Pp]riests/,
	~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Pp]residents/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Pp]rime-[Mm]inisters/,
	~/(?:[Ee]x)?-?[Pp]rince(?:sse)?s/,~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Pp]rincipals/, ~/(?:[Ee]x)?-?[Qq]ueens/,
	~/(?:[Ee]x)?-?[Rr]abbis/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Ss]ecretaries/, ~/(?:[Ee]x|[Vv]ice|[Ss]ub)?-?[Ss]pokes(?:wo)?mans/,
	~/(?:[Ee]x)?-?[Ss]ultans/, ~/(?:[Ee]x)?-?viscount(?:esse)?s/ ]
	
   // to be EXCLUDED from the NE
   static final cargoExcludeSingle = [~/(?:ex|vice|sub)?-?mayor/,[~/(?:ex|vice|sub)?-?prime/,'minister'],'comissioner']	
   
   // to be EXCLUDED from the NE
   static final cargoExcludePlural = [~/(?:ex|vice|sub)?-?mayors/,[~/(?:ex|vice|sub)?-?prime/,'ministers']]	
     
   // to be included in the NE
   static final jobInclude = [~/[Aa]rchitects?/, ~/[Dd]octors?/,~/[Pp]rofessors?/,~/[Ss]irs?/]

     // to be EXCLUDED from the NE
   static final jobExclude = [~/[Aa]ctors?/, ~/[Aa]ctress(?:es)?/, ~/[Aa]stronomers?/, ~/[Aa]thletes?/,
    ~/[Aa]uthors?/, ~/[Aa]ttourneys?/, ~/[Dd]rummers?/,~/[Bb]assists?/, ~/[Bo]xeurs?/, ~/[Cc]omposers?/,
    ~/[Cc]riminals?/, ~/[Cc]oaches?/, ~/[Cc]ongress(?:wo)?mans?/,  ~/[Cc]oroners?/, ~/[Dd]iplomats?/, 
    ~/[Dd]eput(?:y|ees)/, ~/[Dd]irectors?/, ~/[Ee]ngineers?/,~/[Ee]conomists?/, ~/[Ee]mperors?/, ~/[Ee]xplorers?/,
    ~/[Ff]ootballers?/, ~/[Gg]enerals?/, ~/[Gg]overnors?/, ~/[Gg]oal-?[Kk]eepers?/,~/[Gg]uitar-?[Pp]layers?/, 
    ~/[Hh]istorians?/, ~/[Hh]umorists?/, ~/[Hh]umanists?/, ~/[Jj]ournalists?/, ~/[Kk]ings?/, ~/[Ll]awyers?/,
    ~/[Mm]ilita(?:y|ies)/, ~/[Mm]usicians?/, ~/[Nn]avigators?/, ~/[Pp]ersonalit(?:y|ies)/, ~/[Pp]ainters?/, 
    ~/[Pp]roducers?/, ~/[Pp]rime-[Mm]inisters?/, [~/[Pp]rime/,~/[Mm]inisters?/], ~/[Pp]remi[eê]rs?/,
    ~/[Pp]hylosophers?/, ~/[Pp]hysicists?/, ~/[Pp]ilots?/, ~/[Pp]oets?/, ~/[Pp]olititians?/, 
    ~/[Qq]ueen?/, ~/[Ss]enators/, ~/[Ss]ingers?/, ~/[Ss]cientists?/, ~/[Ss]culptors?/, 
    ~/[Ww]riters?/ ]
    
   // to be included in the NE
   static final parentescoInclude = [~/grand[pm]a/, ~/grand-?father/,~/(?:great|grand)?-?uncle/,
	   ~/(?:great|grand)?-?aunt/, ~/brother(?:-in-law)?/, ~/sister(?:-in-law)?/]
   
   // to be excluded from the NE
   static final parentescoExclude = [~/dad(?:dy)?/,~/mom(?:my)?/,~/(?:step|god)?-?father(?:-in-law)?/,
	~/(?:step|god)?-?mother(?:-in-law)?/,~/(?:step|god)?-?sons?(?:-in-law)?/,
	~/(?:step|god)?-?daughters?(?:-in-law)?/, ~/(?:great|grand)?-?sons?(?:-in-law)?/,
	~/(?:great|grand)?-?daughters?(?:-in-law)?/,~/(?:great|grand)?-?nephews?/,
	~/(?:great|grand)?-?nieces?/,~/(?:great|grand)?-?cousins?/,~/(?:ex-)?husbands?/,
	~/(?:ex-)?wifes?/,~/(?:boy|girl)friends?/,~/lovers?/]

   static final otherPersonQualifier = ['man','women','gentleman']
   
   static final people =['people'] 
   
   static final otherPersonOccupation = [/[Cc]ommunist/,/[Ss]ocialist/,/[Dd]emocrat/,/[Rr]epublican/ ]
   
   //http://names.mongabay.com/male_names.htm, stayed at rodney
   //http://names.mongabay.com/female_names.htm, stayed at Betty
   static final firstName = [
   'Aaron','Adam','Alan','Alexander','Aleksander','Albert','Alfred','Andrew','Angela','Anthony','Antonio',
     'Arthur','Ashley',
   'Barbara','Barney','Benjamin','Betty','Bill','Billy','Bob','Bobby','Brian','Bryan','Brandon','Bruce',
   'Candice','Carl','Carlos','Carol','Cecilia','Charles','Chris','Christian','Christopher','Clive','Claus','Clarence',
     'Craig',
   'Damon','Dale','Dan','Danny','Daniel','David','Dennis','Dick','Dorothy','Donald','Douglas',
   'Earl','Edwin','Ed','Edward','Elizabeth','Emmanuel','Enrico','Ernest','Eric','Eugene',
   'Frances','Franz','Frank','Franklin','Fred','Frederick','Fay','Federico','Friedrich',
   'Gaston','Gary','Gerald','George','Gianni','Gloria','Gregory',
   'Hans','Hari','Harold','Harry','Heather','Henry','Herbert','Howard',
   'Ian','Iustin',
   'Jack','Jacques','James','Jason','Jeff','Jeffrey','Jennifer','Jeremy','Jesse','Jessica','Jerry','Jim','Jimmy','Jonathan',
      'Joseph','John','Johnny','Jordi','Joe','Joanne','Jose','Joshua','Juan','Justin',
   'Karen','Karl','Kate','Kathrine','Keith','Kenneth','Kevin','Kirk','Kurt',
   'Larry','Lawrence','Leo','Leonard','Linda','Lisa','Louis','Luis',
   'Marcus','Margaret','Mary','Maria','Matthew','Margaret','Mark','Martin','Max','Maxwell','Michael','Michel','Mike',
   'Nancy','Nathan','Neil','Norah','Nicholas',
   'Patrick','Patricia','Paul','Peter','Phillip','Philip',
   'Raffaella','Ralph','Randy','Raul','Ray','Raymond','Richard','Rick','Ricky','Robert','Rodney','Roger','Roland',
   	'Ronald','Roy', 'Rudolf','Russel','Ryan',
   'Samuel','Scott','Sean','Shawn','Stanley','Steve','Steven','Stephen','Susan',
   'Terry','Thomas','Timothy','Tim','Timmy','Todd','Tony','Tom','Tommy',
   'Victor',
   'Wayne','William','Willie','Willy','Walter','Wayne'
   ]
  
   //http://names.mongabay.com/most_common_surnames.htm, stayed atBailey
   static final lastName = [
   'Adams','Aldrin','Allen','Anderson','Armstrong','Austin',
   'Bailey','Baker','Bartoli','Bell','Berger','Bergen','Brown','Brook','Bush','Burdett', 'Burnett','Byrne',
   'Campbell','Carrey','Carter','Cheney','Clark','Collins','Cook','Cosby','Crowley',
   'Daniels','Dalton','Davidson','Davis','Donnovan','Durrant',
   'Edwards','Eisenhower','Evans',
   'Feigl','Freeman','Fox',
   'Garcia','Gellner','Glass','Gordon','Grant','Gonzalez','Goretti','Green',
   'Hahn','Hall','Hossne','Hateley','Harris','Heinrich','Hernandez','Hill','Hughes',
   'Jarrett','Jackson','Johnson','Jones','Jordan',
   'Kennedy','Kerry','Knight','King',
   'Lopez','Langley','Lee','Lewis',
   'McArthur','McCarthy','McStay','McGinlay','McCoist','McCurry',
   'Martin','Martinez','Meyer','Mills','Miller','Mitchell','Mikhailichenko','Moore','Morales','Morris',
      'Mosley','Mowbray','Moore','Morgan','Murphy',
   'Nelson',
   'O\'Brien','O\'Donnell','Olive','Oswald','Owen',
   'Parker','Perez','Peymann','Phillips',
   'Ratzenberger','Reed','Reichenbach','Rossetto','Robinson','Richter','Robertson','Roberts','Rodriguez','Rogers',
   'Sanchez','Schüssel','Scott','Silverstone','Simpson','Smith','Stewart',
   'Taylor','Thomas','Thompson','Turner',
   'Waissman','Walker','Weiss','Willians','Wilson','White','Wright',
   'Young',
   ]

   static final personActionPrefix = [[/(?:leaded|commanded)/,'by']]
   
   /** CLAUSE **/
   
// SINGULARES   
   
   // positions to be included (president, etc)
  static final Clause cargoIncludeSingle1c = Clause.newConcept1Clause(
	  cargoIncludeSingle, "cargoIncludeSingle")
   
   // positions to be excluded  (el-rei, etc)
  static final Clause cargoExcludeSingle1nc = Clause.newConcept1Clause(
      cargoExcludeSingle, "cargoExcludeSingle", false)

   // jobs to be included (priest, etc)
  static final Clause jobInclude1c = Clause.newConcept1Clause(
	  jobInclude, "jobInclude")
 
   // jobs to be excluded (goalkeeper, etc)   
  static final Clause jobExclude1nc = Clause.newConcept1Clause(
	  jobExclude, "jobExclude", false)   
   
   // parent to be included  (brother, etc)
  static final Clause parentescoInclude1c = Clause.newConcept1Clause(
	  parentescoInclude, "parentescoInclude")
 
   // parent to be excluded (nephew, etc) 
  static final Clause parentescoExclude1nc = Clause.newConcept1Clause(
	  parentescoExclude, "parentescoExclude", false)  

// PLURAL

   // positions to be included (presidents, etc)
   static final Clause cargoIncludePlural1c = Clause.newConcept1Clause(
	  cargoIncludePlural, "cargoIncludePlural")

   // positions a serem excluídos (el-reis, etc)
   static final Clause cargoExcludePlural1nc = Clause.newConcept1Clause(
	   cargoExcludePlural, "cargoExcludePlural", false)

// MIXTURES

   // positions to be included + excluded, Single
   static final Clause cargoAllSingle1nc = Clause.newConcept1Clause(
	   (cargoIncludeSingle + cargoExcludeSingle),"all single position", false)
	
  // positions to be included + excluded, Plural
   static final Clause cargoAllPlural1nc = Clause.newConcept1Clause(
	   (cargoIncludePlural + cargoExcludePlural),"all plural position", false)

  // positions to be included + excluded, Single + Plural
   static final Clause cargoAll1nc = Clause.newConcept1Clause(
	   (cargoIncludeSingle + cargoExcludeSingle + 
		cargoIncludePlural + cargoExcludePlural),"all position", false)
	   
  // parent included + excluded
   static final Clause parentescoAll1nc = Clause.newConcept1Clause(
	   (parentescoInclude + parentescoExclude), "all parent", false)
     
   //jobs included + excluded   
   static final Clause jobAll1nc = Clause.newConcept1Clause(
	  (jobInclude + jobExclude), "all jobs", false)

   // jobAll1nc + extra qualifiers
   static final Clause jobAllPlusOtherQualifier1nc = Clause.newConcept1Clause(
	  (jobInclude + jobExclude + otherPersonQualifier), "all jobs plus other qualifiers",false)

   // Adjectives for persons (gentilics + others)
   static final Clause personAdjective01nc = Clause.newConcept01Clause(
	   (otherPersonOccupation + LocalGazetteerEN.cityAdjective + 
		LocalGazetteerEN.regionAdjective + LocalGazetteerEN.alternativeCountryAdjective + 
		LocalGazetteerEN.countryAdjective + LocalGazetteerEN.continentAdjective), 
		"person adjectives", false)  

   // hints for person actions, "Commanded by", etc.
   static final Clause personActionPrefix1nc = Clause.newConcept1Clause(
	personActionPrefix, "person actions", false)
	
   static final Clause people1nc = Clause.newConcept1Clause(people, "people", false)
	   
}