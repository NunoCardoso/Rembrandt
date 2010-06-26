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

import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes

/**
 * @author Nuno Cardoso
 * This class stores internal evidence definitions for Wikipedia categories,
 * and maps them to the corresponding classification categories.
 */
class WikipediaCategoryDefinitionsEN {
    
   static final List<Map> meanings = [
   
   /* LOCAL HUMANO PAIS */
 
  [answer:SemanticClassification.create(Classes.category.place, 
    Classes.type.human, Classes.subtype.country), needle:[
    ~/[Cc]ountries/ ], reverse:true ],   
   /* LOCAL HUMANO DIVISAO */
   
   // XXX City, etc.
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.human, Classes.subtype.division), needle:[
   ~/[Cc]ities/, ~/[Cc]ounties/, ~/[Dd]istricts/, ~/[Mm]unicipalities/, 
   ~/[Nn]eighbou?rhoods/, ~/[Tt]owns/, ~/[Vv]illages/ ], reverse:true ],  
   // do not use States, or it will be contaminated wirth categories of "XX of United States"
   // rely on State of X"

   // City of XXX
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.human, Classes.subtype.division), needle:[   
   ~/[Cc]ities/, ~/[Cc]ounties/, ~/[Dd]istricts/, ~/[Mm]unicipalities/, 
   ~/[Nn]eighbou?rhoods/, ~/[Ss]tates/, ~/[Tt]owns/, ~/[Vv]illages/] ],                                                                                 
                                                                                 
   /* LOCAL HUMANO REGIAO */
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.human, Classes.subtype.humanregion), needle:[ 
   ~/[Pp]rovinces/, ~/[Rr]egions/, 'NUTS' ], reverse:true],
   
       /* LOCAL HUMANO CONSTRUCAO */
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.human, Classes.subtype.construction), needle:[
   ~/[Aa]irports/, ~/[Aa]irstrips/, ~/[Aa]uditoriums/, ~/[Aa]utodromes/, ~/[Bb]ridges/, 
   ~/[Cc]astles/, ~/[Cc]athedrals/, ~/[Cc]hapels/, ~/[Cc]ircuits/, ~/[Cc]onvents/, ~/[Dd]ams/,
   ~/[Ff]ortresses/, ~/[Cc]hurches/, ~/[Gg]ardens/, ~/[Hh]arbou?rs/, ~/[Mm]onastaries?/, 
   ~/[Pp]alaces/, ~/[Pp]arks/, ~/[Tt]racks/, ~/[Pp]ools/, ~/[Ss]tadiums/, ~/[Ss]tudios/, 
   ~/[Tt]owers/, ~/[Zz]oos/ ], reverse:true],
   
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.human, Classes.subtype.construction), needle:[
   ~/[Aa]irports/, ~/[Aa]irstrips/, ~/[Aa]uditoriums/, ~/[Aa]utodromes/, ~/[Bb]ridges/, 
   ~/[Cc]astles/, ~/[Cc]athedrals/, ~/[Cc]hapels/, ~/[Cc]ircuits/, ~/[Cc]onvents/, ~/[Dd]ams/,
   ~/[Ff]ortresses/, ~/[Cc]hurches/, ~/[Gg]ardens/, ~/[Hh]arbou?rs/, ~/[Mm]onastaries?/, 
   ~/[Pp]alaces/, ~/[Pp]arks/, ~/[Tt]racks/, ~/[Pp]ools/, ~/[Ss]tadiums/, ~/[Ss]tudios/, 
   ~/[Tt]owers/, ~/[Zz]oos/ ] ],
   
   /* LOCAL HUMANO RUA */
   [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.human, Classes.subtype.street), needle:[
   ~/[Hh]ighways/, ~/[Ff]reeways/, ~/[Aa]venues/, ~/[Aa]lleys/, 
   ~/[Rr]oads/, ~/[Bb]oulevards/, ~/[Ss]quares/, ~/[Ss]treets/ ], reverse:true],
   
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.human, Classes.subtype.street), needle:[
   ~/[Hh]ighways/, ~/[Ff]reeways/, ~/[Aa]venues/, ~/[Aa]lleys/, 
   ~/[Rr]oads/, ~/[Bb]oulevards/, ~/[Ss]quares/, ~/[Ss]treets/ ] ],

              
   /* LOCAL FISICO AGUACURSO */
   [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.watercourse), needle:[  
   ~/[Ee]ffluents/, ~/[Ww]aterfalls/, ~/[Rr]ivers/ ], reverse:true],

  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.watercourse), needle:[  
   ~/[Ee]ffluents/, ~/[Ww]aterfalls/, ~/[Ff]alls/, ~/[Rr]ivers/ ] ] ,
   
   /* LOCAL FISICO AGUAMASSA */
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.watermass), needle:[  
   ~/[Bb]ays/, ~/[Cc]hannels/, ~/[Ss]traights/, ~/[Gg]ulfs/, ~/[Ll]akes/, 
   ~/[Ss]eas/, ~/[Oo]ceans/ ], reverse:true],
   
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.watermass), needle:[  
   ~/[Bb]ays/, ~/[Cc]hannels/, ~/[Ss]traights/, ~/[Gg]ulfs/, 
   ~/[Ll]akes/, ~/[Ss]eas/, ~/[Oo]ceans/] ],
   
   /* LOCAL FISICO RELEVO */
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.mountain), needle:[  
   ~/[Bb]eaches/, ~/[Mm]ountains/, ~/[Mm]ounts/, ~/[Ss]ierras/, ~/[Pp]rairies/, 
   ~/[Cc]reeks/, ~/[Pp]eaks/, ~/[Vv]alleys/, ~/[Vv]ulcanoe?s/ ], reverse:true]   , 
   
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.mountain), needle:[  
   ~/[Bb]eaches/, ~/[Mm]ountains/, ~/[Mm]ounts/, ~/[Ss]ierras/, 
   ~/[Pp]rairies/, ~/[Cc]reeks/, ~/[Pp]eaks/, ~/[Vv]alleys/, 
   ~/[Vv]ulcanoe?s/ ] ]    ,
   
   /* LOCAL FISICO PLANETA */
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.planet), needle:[   
   ~/[Cc]onstellations/, ~/[Pp]lanets/ ], reverse:true] ,
 
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.planet), needle:[   
   ~/[Cc]onstellations/, ~/[Pp]lanets/] ] ,
                                                                                     
   /* LOCAL FISICO ILHA */
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.island), needle:[  
   ~/[Ii]slands/, ~/[Ii]sles/, ~/[Aa]rchipelagos/ ], reverse:true] ,
   
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.island), needle:[  
   ~/[Ii]slands/, ~/[Ii]sles/, ~/[Aa]rchipelagos/] ]  ,          
                                                                             
   /* LOCAL FISICO REGIAO */
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.physicalregion), needle:[    
   ~/[Rr]egions/, ~/[Dd]eserts/, ~/[Ss]teppe/ ], reverse:true]   ,
   
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.physicalregion), needle:[    
   ~/[Rr]egions/, ~/[Dd]eserts/, ~/[Ss]teppe/] ]   ,
   
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.physical, Classes.subtype.other), needle:[     
   ~/[Ff]orests/ ], reverse:true]         ,  
   
   /* LOCAL VIRTUAL COMSOCIAL */
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.virtual, Classes.subtype.media), needle:[  
    ~/[Dd]iaries/, ~/[Nn]ewspapers/, ~/[Nn]ews/, ~/[Mm]agazines/, ~/[Tt]elevisions/, 
    /TVs/ ] , reverse:true],
   
  [answer:SemanticClassification.create(Classes.category.place, 
		Classes.type.virtual, Classes.subtype.media), needle:[  
   ~/[Rr]adios/ ]],
   
   /* LOCAL VIRTUAL SITIO */
  [answer:SemanticClassification.create(Classes.category.place, 		
	  Classes.type.virtual, Classes.subtype.site), needle:[ 
    ~/[Ww]ebsites/, ~/[Ss]ites/ ], reverse:true] ,
   
   /* LOCAL VIRTUAL OBRA */
  
       /**************
   /* 2 - PESSOA *   
    **************/
    
   /* PESSOA INDIVIDUAL */
  [answer:SemanticClassification.create(Classes.category.person, 
		Classes.type.individual), needle:[
   ~/[Aa]ttou?rneys/, ~/[Aa]ctors/, ~/[Aa]ctresses/, ~/[Aa]gents/,
   ~/[Aa]irmens/, ~/[Aa]mbassadors/, ~/[Aa]nthropologists/, ~/[Aa]rchbishops/,
   ~/[Aa]rchaeologists/, ~/[Aa]rchitects/, ~/[Aa]stronauts/, ~/[Aa]stronomers/,
   ~/[Aa]thletes/, ~/[Aa]uthors/, ~/[Aa]viators/,
   ~/[Bb]assists/, ~/[Bb]iographers/, ~/[Bb]iologists/,
   ~/[Bb]roker?/, ~/[Bb]usiness(?:man|person|woman)s/, 
   ~/[Cc]ameramans/, ~/[Cc]artographers/, ~/[Cc]artoonists/, ~/[Cc]elebrities/,
   ~/[Cc]ellists/, ~/CEOs/, ['Chief','Executive',~/Officers/], ~/CFOs/,
   ['Chief','Financial', ~/Officers/], ~/[Cc]hefs/, [~/[Cc]hemical/, ~/Engineers/], 
   ~/[Cc]hemists/, ['CIA',~/Agents/], [~/[Cc]ivil/,~/[Ee]ngineers/], ~/[Cc]larinetists/,
   ~/[Cc]oaches/, ~/[Cc]olumnists/, ~/[Cc]omediant?s/, ~/[Cc]omposers/,
   ~/[Cc]onsuls/, ~/[Cc]onsultants/, ~/[Cc]oroners/, ~/[Cc]orrespondents/,
   ~/[Cc]osmetologists/, ~/[Cc]osmonauts/, ~/[Cc]ouriers/, ~/[Cc]ryptographers/,
   ~/[Dd]ancers/, ~/[Dd]eputies/, ~/[Dd]entists/, ~/[Dd]esigners/, ~/[Dd]etectives/,
   ~/[Dd]ictators/, ~/[Dd]iplomats/, ~/[Dd]irectors/, [~/[Dd]isc/, ~/[Jj]ockeys/], ~/[Dd]ivers/,
   ~/[Dd]octors/, ~/[Dd]ramatists/, ~/[Dd]ramaturgs/, ~/[Dd]rivers/, [~/[Dd]rug/,~/[Dd]ealers/],
   ~/[Dd]rummers/, ~/[Ee]cologists/, ~/[Ee]Economists/, ~/[Ee]Editors/, ~/[Ee]ngineers/,
   ~/[Ee]ntertainers/, ~/[Ee]ntrepeneurs/, ~/[Ee]vangelists/, ~/[Ff]armers/, ['FBI', ~/Agents/],
   [~/[Ff]ilm/, ~/[Dd]irectors/], [~/[Ff]ilm/, ~/[Pp]roducers/], ~/[Ff]irefighters/, ~/[Ff]ootballers/,
   ~/[Gg]enealogists/, ~/[Gg]enerals/, ~/[Gg]eographers/, ~/[Gg]eologists/, ~/[Gg]overnors/,
   ~/[Gg]uitarists/, ~/[Gg]unsmiths/, ~/[Gg]ynecologists/, ~/[Hh]airdressers/,
   ~/[Hh]istoriographers/, ~/[Hh]ypnotists/, ~/[Ii]llusionists/, ~/[Ii]nterpreters/,
   ~/[Ii]nventors/, ~/[Ii]nvestigators/, ~/[Jj]ournalists/, ~/[Jj]udges/, ~/[Jj]urists/,
   ~/[Ll]awyers/, ~/[Ll]ecturers/, ~/[Ll]inguists/, ~/[Mm]agicians/, ~/[Mm]agistrates/,
   ~/[Mm]arines/, ~/[Mm]athematicians/, ~/[Mm]echanic(?:ian)s/, ~/[Mm]edics/,
   ~/[Mm]eteorologists/, ~/[Mm]odels/, ~/[Mm]onks/, ~/[Mm]usicians/, ~/[Nn]avigators/,
   ~/[Nn]ecromancers/, ~/[Nn]egotiators/, ~/[Nn]otarys/, ~/[Nn]ovelists/, ~/[Nn]urses/,
   ~/[Oo]bstetricians/, ~/[Oo]ncologists/, ~/[Oo]ntologists/, ~/[Oo]perators/,
   ~/[Oo]phthalmologists/, ~/[Oo]pticians/, ~/[Oo]rganists/, ~/[Oo]rnithologists/,
   ~/[Oo]rthodontists/, ~/[Oo]rthopaedists/, ~/[Oo]torhinolaryngologists/, ~/[Pp]ainters/,
   ~/[Pp]aleontologists/, ~/[Pp]ediatricians/, ~/[Pp]ediatrists/, ~/[Pp]eoples/, ~/[Pp]hilanthropists/,
   ~/[Pp]hilologists/, ~/[Pp]hilosophers/, ~/[Pp]hotographers/, ~/[Pp]hysicians/, ~/[Pp]hysicists/,
   ~/[Pp]hysiotherapists/, ~/[Pp]ianists/, ~/[Pp]ilots/, [~/[Pp]lastic/, ~/[Ss]urgeons/], ~/[Pp]oets/,
   [~/[Pp]olice/,~/[Oo]fficers/], [~/[Pp]olice/,~/[Ii]nspectors/], ~/[Pp]olitic(?:ian|al)s/, ~/[Pp]rofessors/,
   ~/[Pp]rostitutes/, ~/[Pp]sychiatrists/, ~/[Pp]sychics/, ~/[Pp]sychologists/, ~/[Pp]ublishers/,
   ~/[Rr]adiologists/, ~/[Rr]adiographers/, ~/[Rr]eporters/, ~/[Rr]esearchers/, ~/[Ss]ailors/,
   ~/[Ss]cientists/, ~/[Ss]ecretaries/, [~/[Ss]ecretary/,~/[Gg]enerals/], ~/[Ss]heriffs/,
   [~/[Ss]heriff/,~/[Oo]fficers/], ~/[Ss]ingers/, [~/[Ss]oftware/, ~/[Ee]ngineers/], ~/[Ss]oldiers/,
   [~/[Ss]tock/, ~/[Bb]rokers/], ~/[Ss]trippers/, ~/[Ss]tunts/, ~/[Ss]urgeons/, ~/[Ss]wimmers/,
   ~/[Tt]ailors/, ~/[Tt]eachers/, [~/[Tt]ennis/,~/[Pp]layers/], ~/[Tt]heologians/, ~/[Tt]rainers/,
   ~/[Tt]utors/, ~/[Uu]fologists/, ~/[Uu]rologists/, ~/[Vv]eterinarians/, ~/[Vv]ibraphonists/,
   ~/[Vv]iolinists/, ~/[Vv]iolists/, ~/[Ww]eathermans/, ~/[Ww]ebmasters/, ~/[Ww]riters/, ~/[Zz]oologists/
  ] ],
  
  [answer:SemanticClassification.create(Classes.category.person, 
		Classes.type.people), needle:[
   [~/[Ee]thnic/,~/[Gg]ropus/],~/[Cc]ivilizations/ ] ],
                                                                         
   /*******************
   /* 3 - ORGANIZACAO *   
    *******************/
   
   /* ORGANIZACAO EMPRESA */
  
  [answer:SemanticClassification.create(Classes.category.organization, 
		Classes.type.company), needle:[     
   ~/[Cc]ompanies/, ~/[Cc]orporations/, [~/[Ss]hopping/,~/[Cc]ent(?:er|re)s/], ~/[Cc]inemas/,
   ~/[Cc]asinos/, ~/[Tt]heathres/, ~/[Gg]allerys/, ~/[HhMm]otels/, 
   ~/[Mm]useums/, ~/[Rr]estaurants/, ~/[Ss]hoppings/ ], reverse:true],
  
  [answer:SemanticClassification.create(Classes.category.organization, 
		Classes.type.company), needle:[    
   ~/[Cc]ompanies/, ~/[Cc]orporations/, [~/[Ss]hopping/,~/[Cc]ent(?:er|re)s/], ~/[Cc]inemas/,
   ~/[Cc]asinos/, ~/[Tt]heathres/, ~/[Gg]allerys/, ~/[HhMm]otels/, 
   ~/[Mm]useums/, ~/[Rr]estaurants/, ~/[Ss]hoppings/ ] ],
 
   /* ORGANIZACAO INSTITUICAO */
  [answer:SemanticClassification.create(Classes.category.organization, 
		Classes.type.institution), needle:[
   ~/[Aa]ssociations/, ~/[Aa]cademies/, ~/[Aa]quariums/, ~/[Aa]gencies/, 
   [~/[Fi]re/,~/[Bb]rigades/], ~/[Gg]uilds/, ~/[Ll]ibraries/, 
   [~/[Nn]ational/,~/[Cc]ompanies/], ~/[Cc]omitees/, ~/[Cc]onfederations/,  
   [~/[Rr]ed/,~/[Cc]rosses/], ~/[Cc]olleges/, ~/[Ss]chools/, ~/[Ff]aculties/,  
   ~/[Ff]ederations/, ~/[Ff]oundations/, ~/[Hh]ospitals/, ~/[Ii]nstitut(e|ion)s/, ~/[Ll]yceums/,
   ~/[Ll]egions/, ~/[Pp]arties/, ~/[Pp]olices/, ~/[Ss]yndicates/,
   ~/[Uu]niversities/ ] ],
 
   [answer:SemanticClassification.create(Classes.category.organization, 
		Classes.type.institution), needle:[      
	~/[Aa]ssociations/, ~/[Aa]cademies/, ~/[Aa]quariums/, ~/[Aa]gencies/, 
	[~/[Fi]re/,~/[Bb]rigades/].reverse(), ~/[Gg]uilds/, ~/[Ll]ibraries/, 
	[~/[Nn]ational/,~/[Cc]ompanies/].reverse(), ~/[Cc]omitees/, ~/[Cc]onfederations/,  
	[~/[Rr]ed/,~/[Cc]rosses/].reverse(), ~/[Cc]olleges/, ~/[Ss]chools/, ~/[Ff]aculties/,  
	~/[Ff]ederations/, ~/[Ff]oundations/, ~/[Hh]ospitals/, ~/[Ii]nstitut(e|ion)s/, ~/[Ll]yceums/,
	~/[Ll]egions/, ~/[Pp]arties/, ~/[Pp]olices/, ~/[Ss]yndicates/,
	~/[Uu]niversities/], reverse:true],
  
 // [answer:[category:'ORGANIZACAO/, 'INSTITUICAO/, sub"SUB"]
   
   /* ORGANIZACAO ADMINISTRACAO */
  [answer:SemanticClassification.create(Classes.category.organization, 
		Classes.type.administration), needle:[
   ~/[Aa]ssemblies/, ~/[Aa]rmies/, [~/[Aa]ir/,~/[Ff]orces/], ~/[Aa]dministrations/, 
   [~/[Cc]ity/, ~/[Hh]alls/].reverse(), ~/[Cc]omissariates/, ~/[Cc]ouncils/, ~/[Gg]overnments/, 
   ~/[Nn]avys/, ~/[Mm]inisteries/, ~/[Pp]arliaments/, [~/[Ss]ecretaries/,'of',~/[Ss]tate/].reverse() 
   ], reverse:true],
  
  [answer:SemanticClassification.create(Classes.category.organization, 
		Classes.type.administration), needle:[
    ~/[Aa]ssemblies/, ~/[Aa]rmies/, [~/[Aa]ir/,~/[Ff]orces/], ~/[Aa]dministrations/, 
    [~/[Cc]ity/, ~/[Hh]alls/], ~/[Cc]omissariates/, ~/[Cc]ouncils/, ~/[Gg]overnments/, 
    ~/[Nn]avys/, ~/[Mm]inisteries/, ~/[Pp]arliaments/, [~/[Ss]ecretaries/,'of', ~/[Ss]tate/]
    ] ],

   /**********************
   /* 6 - ACONTECIMENTO  *   
    **********************/    
    
   /* ACONTECIMENTO EFEMERIDE */
  [answer:SemanticClassification.create(Classes.category.event, 
		Classes.type.happening), needle:[
   ~/[Bb]attles/, ~/[Cc]apitulations/, [~/[Ww]orld/,~/[Dd]ays/], ~/[Ww]ars/, 
   ~/[Ii]ndependences/, ~/[Rr]estaurations/, ~/[Rr]evolutions/ ], reverse:true],
   
    /* ACONTECIMENTO EFEMERIDE */
  [answer:SemanticClassification.create(Classes.category.event, 
		Classes.type.happening), needle:[
   ~/[Bb]attles/, ~/[Cc]apitulations/, [~/[Ww]orld/,~/[Dd]ays/], 
   ~/[Ww]ars/, ~/[Ii]ndependences/, ~/[Rr]estaurations/, 
   ~/[Rr]evolutions/ ] ],
           
   /* ACONTECIMENTO ORGANIZADO */
  [answer:SemanticClassification.create(Classes.category.event, 
		Classes.type.organized), needle:[
   ~/[Cc]ongresses/, ~/[Cc]onferences/, ~/[Cc]onciliums/, ~/[Cc]hampionships/, 
   ~/[Cc]ups/, ~/[Ss]ummits/, ~/[Ll]eagues/, ~/[Ff]estivals/, ~/[Mm]eetings/,  
   ~/[Gg]ames/, ~/[Rr]eunions/, ~/[Ss]eminars/ ] ],
   
   /* ACONTECIMENTO ORGANIZADO */
  [answer:SemanticClassification.create(Classes.category.event, 
		Classes.type.organized), needle:[
   ~/[Cc]ongresses/, ~/[Cc]onferences/, ~/[Cc]onciliums/, ~/[Cc]hampionships/, 
   ~/[Cc]ups/, ~/[Ss]ummits/, ~/[Ll]eagues/, ~/[Ff]estivals/, ~/[Mm]eetings/,    
   ~/[Gg]ames/, ~/[Rr]eunions/, ~/[Ss]eminars/ ], reverse:true],

  [answer:SemanticClassification.create(Classes.category.event, 
		Classes.type.pastevent), needle:[             
   [~/[Gg]rand/, ~/[Pp]rix.*/], ~/[Mm]arathons/], reverse:true],
            
   /******************
   /* 7 - ABSTRACCAO *   
    ******************/   

   /* ABSTRACCAO DISCIPLINA */ 
  [answer:SemanticClassification.create(Classes.category.abstraction, 
		Classes.type.discipline), needle:[
   ~/[Ss]ciences/, ~/[Dd]efenses/,   
   ~/[Ll]aws/, ~/[Ee]conomies/, ~/[Ee]ducations/, ~/[Nn]urseries/, 
   ~/[Ee]ducations/, ~/[Ee]ngineering/, ~/[Ss]tatistics/, ~/[Pp]hilosophies/,
   ~/[Ff]inances/, ~/[Mm]anagements/, ~/[Jj]ustices/,
   ~/[Ll]iteratures/, ~/[Ll]ogics/, ~/[Mm]ath(?:matic)?s/, ~/[Mm]arketings/, 
   ~/[Mm]edicines/, ~/[Mm]inings/, ~/[Oo]ptics/, 
   ~/[Cc]hemistries/, ~/[Pp]sychologies/, ~/[Tt]echnologies/, 
   ~/[Tt]ourisms/ ] ],
    
   /* ABSTRACCAO ESTADO */ // Abrange antiga ESCOLA e OBRA
  [answer:SemanticClassification.create(Classes.category.abstraction, 
		Classes.type.state), needle:[
   ~/[Dd]iseases/, ~/[Dd]eformations/, ~/[Ss]yndromes/ ] ]   ,

  [answer:SemanticClassification.create(Classes.category.abstraction, 
		Classes.type.state), needle:[
   ~/[Dd]iseases/, ~/[Dd]eformations/, ~/[Ss]yndromes/ ], reverse:true],
   
   /* ABSTRACCAO IDEIA */
  [answer:SemanticClassification.create(Classes.category.abstraction, 
		Classes.type.idea), needle:[       
   ~/[Cc]omunisms/, ~/[Ii]mpressionisms/, ~/[Nn]eoliberalisms/ ] ],
 
   /*************
   /* 8 - COISA *   
    *************/  
    
    /* COISA CLASSE */
  [answer:SemanticClassification.create(Classes.category.thing, 
		Classes.type.'class'), needle:[
   ~/[Pp]rizes/ ], reverse:true],

   /* COISA OBJECTO */
 

   /* COISA SUBSTANCIA */
   
   /************
   /* 9 - OBRA *   
    ************/  
  
        /* OBRA PLANO */
  [answer:SemanticClassification.create(Classes.category.masterpiece, 
		Classes.type.plan), needle:[
   //[~/[Aa]rticles/], widely used for mentioning Wikipedia articles 
   ~/[Aa]greements/, ~/[Cc]odes/, 
   ~/[Dd]eclarations/, ~/[Dd]irectives/, ~/[Dd]ecrees/, ~/[Bb]ills/,
   ~/[Tt]axes/, ~/[Ll]aws/, ~/[Bb]udgets/, ~/[Pp]rojects/,
   ~/[Pp]lans/, ~/[Pp]rograms/, ~/[Pp]rotocols/, 
   ~/[Rr]ules/, ~/[Tt]reaties/ ], reverse:true],
   
   /* OBRA PLANO */
  [answer:SemanticClassification.create(Classes.category.masterpiece, 
		Classes.type.plan), needle:[
   // [~/[Aa]rticles/], 
    ~/[Aa]greements/, ~/[Cc]odes/, 
    ~/[Dd]eclarations/, ~/[Dd]irectives/, ~/[Dd]ecrees/, ~/[Bb]ills/,
    ~/[Tt]axes/, ~/[Ll]aws/, ~/[Bb]udgets/, ~/[Pp]rojects/,
    ~/[Pp]lans/, ~/[Pp]rograms/, ~/[Pp]rotocols/, 
    ~/[Rr]ules/, ~/[Tt]reaties/ ] ]    ,   

   /* OBRA REPRODUZIDA */
  [answer:SemanticClassification.create(Classes.category.masterpiece, 
		Classes.type.reproduced), needle:[
   ~/[Bb]ooks/, ~/[Mm]ovies/ ] ] ,
   
   /* OBRA REPRODUZIDA */
  [answer:SemanticClassification.create(Classes.category.masterpiece, 
		Classes.type.reproduced), needle:[
   ~/[Bb]ooks/, ~/[Mm]ovies/ ], reverse:true]  ,     
   
   /* OBRA ARTE */
  [answer:SemanticClassification.create(Classes.category.masterpiece, 
		Classes.type.workofart), needle:[
   ~/[Ss]tatues/ ] ],
   
   /* OBRA ARTE */
  [answer:SemanticClassification.create(Classes.category.masterpiece, 
		Classes.type.workofart), needle:[
   ~/[Ss]tatues/ ], reverse:true]
   
   //println "meanings size: "+(meanings.size())+" type="+meanings.class.name
   ]
 }