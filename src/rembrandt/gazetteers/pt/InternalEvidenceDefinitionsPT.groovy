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
 
package rembrandt.gazetteers.pt

import rembrandt.gazetteers.pt.PatternsPT
import rembrandt.gazetteers.Patterns
import rembrandt.gazetteers.pt.NumberGazetteerPT
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.obj.SemanticClassification

/**
 * @author Nuno Cardoso
 * This class stores internal evidence definitions for NEs
 * and maps them to the corresponding classification categories.
 */
class InternalEvidenceDefinitionsPT {
    
   static final meanings = [ 
          
       /**************
       /*  1 - LOCAL *   
        **************/
 
       /* LOCAL HUMANO PAÍS */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
	 	Classes.subtype.country), needle:[
        ~/[Ee]miratos?/, ~/[Rr]einos?/ ] ],
       
     //  println meanings.size() 
     
       /* LOCAL HUMANO DIVISÃO */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
	 	Classes.subtype.division), needle:[
       ~/[Aa]ldeia/, ~/[Cc]idade/, ~/[Cc]omuna/, ~/[Cc]oncelho/, ~/[Dd]istrito/, [~/[Es]tado/, PatternsPT.daeo],
       ~/[Ff]reguesia/, ~/[Mm]etrópole/, ~/[Mm]unicípio/, ~/[Vi]la/ ] ],
   
       //println meanings.size()
       
       /* LOCAL HUMANO REGIAO */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
	 	Classes.subtype.region), needle:[       
       [~/[Ee]xtremo/, ~/[Oo]riente/], ~/[Pp]rovíncia/ , ~/[Rr]egião/  ] ],
   
       /* LOCAL HUMANO CONSTRUCAO */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
	 	Classes.subtype.construction), needle:[
       ~/[Aa]eroporto/, ~/[Aa]er[óô]dromo/, ~/[Aa]uditório/, ~/[Aa]utódromos?/, ~/[Bb]arragem/, 
       ~/[Cc]astelo/, ~/[Cc]atedral/, ~/[Cc]apela/, ~/[Cc]ircuito/, ~/[Cc]onvento/, ~/[Ee]st[áú]dio/,
       ~/[Ff]ortaleza/, ~/[Ii]greja/, ~/[Jj]ardi[mn]s?/, ~/[Kk]artódromos?/, ~/[Mm]osteiro/, 
       ~/[Pp]alácio/, ~/[Pp]arque/, [~/[Pp]ista/,PatternsPT.daeo], ~/[Pp]iscina/, ~/[Pp]onte/, 
       [~/[Pp]orto/, PatternsPT.daeo],// forço 'daeo' por causa do Porto
       ~/[Tt]orre/, ~/[Zz]oo/ ] ],
       
       // estrangeirismos
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
	 	Classes.subtype.construction), needle:[
       ~/[Ss]tadium~/ ], reverse:true],
       
       /* LOCAL HUMANO RUA */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
	 	Classes.subtype.street), needle:[
       ~/[Aa]uto-[Ee]strada/, ~/[Aa]venida/, ~/[Aa]vd?\./, ~/[Bb]airro/, ~/[Bb]eco/, ~/[Ee]strada/, 
       ~/[Ii]tinerário/, ~/[Ll]argo/, ~/[Mm]uro/, ~/[Pp]raç(?:et)?a/, ~/[Rr]ua/ ]],
       
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
	 	Classes.subtype.street), needle:[ ~/[Ss]treet/ ], reverse:true ],
       
       /* LOCAL HUMANO OUTRO */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
	 	Classes.subtype.other), needle:[    
       ~/[Pp]raia/, [~/[Ss]alão/, ~/[Nn]obre/], ~/[Ss]ala/ ] ],
            
       /* LOCAL FISICO AGUACURSO */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
	 	Classes.subtype.watercourse), needle:[  
       ~/[Aa]fluente/, ~/[Cc]achoeira/, ~/[Cc]ataratas/, ~/[Rr]io/, ~/[Rr]iacho/ ] ],
       // naão usar "Ribeiro" por causa do nome...
    
       /* LOCAL FISICO AGUAMASSA */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
	 	Classes.subtype.watermass), needle:[  
       ~/[Bb]aía/, ~/[Cc]anal/, [~/[Dd]elta/, PatternsPT.daeo], ~/[Ee]streito/, ~/[Ee]stuário/, 
       ~/[Gg]olfo/, ~/[Ll]agoa?/, ~/[Mm]ar/, ~/[Oo]ceano/ ] ],
       
       /* LOCAL FISICO RELEVO */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
	 	Classes.subtype.mountain), needle:[  
        ~/[Cc]ordilheira/, ~/[Mm]ontanhas?/, ~/[Mm]ontes?/, ~/[Ss]erra/, ~/[Pp]lanície/, 
        ~/[Pp]lanalto/, ~/[Pp]ico/, ~/[Vv]ale/, ~/[Vv]ulcão/ ] ], 
       
       /* LOCAL FISICO PLANETA */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
	 	Classes.subtype.planet), needle:[   
       ~/[Cc]onstelação/, ~/[Pp]laneta/ ] ],
     
       /* LOCAL FISICO ILHA */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
	 	Classes.subtype.island), needle:[  
       ~/[Ii]lhas?/, ~/[Aa]rquipélago/ ] ],         
 
       /* LOCAL FISICO REGIAO */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
	 	Classes.subtype.region), needle:[    
       ~/[Rr]egião/, ~/[Dd]eserto/ ] ],
       
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
	 	Classes.subtype.other), needle:[     
       ~/[Bb]osque/, ~/[Ff]loresta/ ] ],          
       
       /* LOCAL VIRTUAL COMSOCIAL */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, 
	 	Classes.subtype.media), needle:[  
       ~/[Bb]oletim/, ~/[Cc]aderno/, ~/[Dd]iário/, ~/[Ff]olha/, ~/[Gg]azeta/, ~/[Jj]ornal/, 
       ~/[Rr]evista/, ~/[Rr]ádio/, ~/[Tt]elevisão/, ~/[Tt]elejornal/, ~/[Tt]elenovela/, 'TV' ] ],
       
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, 
	 	Classes.subtype.media), needle:[ 
       'FM' ], reverse:true],
       
       /* LOCAL VIRTUAL SITIO */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, 
	 	Classes.subtype.site), needle:[ 
       ~/[Pp]ágina/, ~/[Ss]ite/ ] ] ,
       
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, 
	 	Classes.subtype.site), needle:[ 
       ~/Net/, ~/Online/ ], reverse:true],
       
       /* LOCAL VIRTUAL OBRA */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, 
	 	Classes.subtype.article), needle:[
       ~/[Pp]ublicação/, ~/[Rr]egulamento/ ] ],
  
       /**************
       /* 2 - PESSOA *   
        **************/
        
       /* PESSOA INDIVIDUAL */
       [answer:SemanticClassification.create(Classes.category.person, Classes.type.individual), needle:[
       //Dr Dr. Dra Dra. Drs Drs. Dras. Sr Sr. Sra Srs Srs. Sras 
       ~/[DdSs]r[as]?ª?[s\.]?/,
       //Doutora, Doutor, Doutores
       ~/[Dd]outor[ae]?s?/,
       //Senhores , Doutor, Doutores
       ~/[Ss]enhor[ae]?s?/, 
       // Don, Dom, Dona, D.
       ~/[Dd]o[mn]a?/, ~/[Dd]\./,
       //Professor, Prof.
       ~/[Pp]rofessora?/, ~/[Pp]rof\./,
       //Engenheir[oa]., Eng.
       ~/[Ee]ngenheir[oa]/, ~/[Ee]ng[ºª]?\.?/,
       //Papa.
       ~/Papa/, ['Sua','Santidade'], ['Sua','Alteza'], ~/Ti[ao]/ ] ],
       
       // Profissão/Cargo normal
       [answer:SemanticClassification.create(Classes.category.person, Classes.type.individual), needle:[
       [~/[Aa]lto/, ~/[Cc]omissário/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Aa]lto-[Cc]omissário/,Patterns.CapitalizedAlphaNumWord],                                                                     
       [~/[Cc]hefe/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Cc]omissári[oa]/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Cc]onde(?:ssa)?/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Dd]irec?tor[ae]?s?/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Dd]eputad[oa]/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Dd]uque(?:sa)?/,Patterns.CapitalizedAlphaNumWord], 
       [~/(?:[Ee]x-)?[Pp]residente/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Gg]erente/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Gg]overnadora?/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Jj][uú]ri/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Jj]u[ií]z/,Patterns.CapitalizedAlphaNumWord], 
       [~/[Mm]arqu[êe]sa?/,Patterns.CapitalizedAlphaNumWord],  
       [~/[MmPp]adre/,Patterns.CapitalizedAlphaNumWord],  
       [~/[Pp]remi[êe]r?/,Patterns.CapitalizedAlphaNumWord],  
       [~/[Pp]rimeiro/,~/[Mm]inist[oa]/,Patterns.CapitalizedAlphaNumWord],  
       [~/[Pp]rimeiro-[Mm]inist[oa]/,Patterns.CapitalizedAlphaNumWord],  
       [~/[Mm]inist[oa]/,Patterns.CapitalizedAlphaNumWord],  
       [~/[Ss]enhora?/,~/[Pp]residente/,Patterns.CapitalizedAlphaNumWord],  
       [~/[Pp]rovedora?/, Patterns.CapitalizedAlphaNumWord],  
       [~/[Rr]a[ií]nha/, Patterns.CapitalizedAlphaNumWord],  
       [~/[Rr]ei/,Patterns.CapitalizedAlphaNumWord],  
       [~/[Pp]ríncipe/,Patterns.CapitalizedAlphaNumWord],  
       [~/[Pp]rincesa/,Patterns.CapitalizedAlphaNumWord],  
       [~/[Ss]ecretário/,Patterns.CapitalizedAlphaNumWord] ]],    
       
       // Profissão/Cargo normal
       [answer:SemanticClassification.create(Classes.category.person, Classes.type.position), needle:[
       [~/[Aa]lto/,~/[Cc]omissário/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Aa]lto-[Cc]omissário/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Cc]hefe/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Cc]omissári[oa]/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Cc]onde(ssa)?/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Dd]irec?tora?/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Dd]eputad[oa]/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Dd]uque(sa)?/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/(?:[Ee]x-)?[Pp]residente/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Gg]erente/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Gg]overnadora?/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Jj][uú]ri/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Jj]u[ií]za?/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Mm]arqu(?:ês|esa)/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[MmPp]adre/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]remi[êe]r?/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]rimeiro/,~/[Mm]inist[oa]/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]rimeiro-[Mm]inist[oa]/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Mm]inist[oa]/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Ss]enhora?/,~/[Pp]residente/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]rovedora?/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Rr]a[ií]nha/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Rr]ei/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]ríncipe/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]rincesa/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Ss]ecretário/,Patterns.NotCapitalizedAlphaNumWord] ]],
        
       [answer:SemanticClassification.create(Classes.category.person, Classes.type.positiongroup), needle:[
       [~/[Aa]ltos/,~/[Cc]omissários/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Cc]hefes/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Cc]omissári[oa]s/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Cc]onde(?:ssa)?s/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Dd]irec?tor[ae]?s?/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Dd]eputad[oa]s/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Dd]uque(?:sa)?s/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/(?:[Ee]x-)?[Pp]residentes/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Gg]erentes/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Gg]overnador[ae]?s/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Jj][uú]ris/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Jj]u[ií]z[ae]s?/,Patterns.NotCapitalizedAlphaNumWord], 
       [~/[Mm]arqu[êe]sa?s/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[MmPp]adres/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]remi[êe]r?/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]rimeiros/,~/[Mm]inistr[oa]s/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]rimeiros-[Mm]inistr[oa]s/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Mm]inistr[oa]s/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]residentes/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Pp]rovedora?s/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Rr]a[ií]nhas/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Rr]eis/,Patterns.NotCapitalizedAlphaNumWord],  
       [~/[Ss]ecretários/,Patterns.NotCapitalizedAlphaNumWord] ] ] , 
       
       [answer:SemanticClassification.create(Classes.category.person, Classes.type.membergroup) , needle:[
       ~/[Ee]quipe/, ~/[Tt]unas?/, ~/[Ss]elec?ção/,~/[Nn]acional/ ] ],

       [answer:SemanticClassification.create(Classes.category.person, Classes.type.individualgroup) , needle:[
       ~/[Ff]am[ií]lia/ ] ],
 
       [answer:SemanticClassification.create(Classes.category.person, Classes.type.people) , needle:[
       ~/[Pp]ovo/ ] ],
 
       
       /*******************
       /* 3 - ORGANIZACAO *   
        *******************/
       
       /* ORGANIZACAO EMPRESA */
       [answer:SemanticClassification.create(Classes.category.organization, Classes.type.company), needle:[
       ~/[Ll]da\.?/, ~/[Ss]\.?[Aa]\.?/, ~/[Ii]nc\.?/, ['Assurance','Quality'] ], reverse:true],
       
       [answer:SemanticClassification.create(Classes.category.organization, Classes.type.company), needle:[     
       ~/[Cc]afé/, ~/[Cc]ompanhia/, [~/[Cc]entro/,'de',~/[Ee]studos/], [~/[Cc]entro/,~/[Cc]omercial/], 
        ~/[Cc]ass?ino/, ~/[Cc]inema/, ~/[Gg]aleria/, ~/[Hh]otel/, ~/[Rr]estaurante/, ~/[Ss]hopping/ ] ],
     
       /* ORGANIZACAO INSTITUICAO */
       [answer:SemanticClassification.create(Classes.category.organization, Classes.type.institution), needle:[
       ~/[Aa]ssociação/, [~/[Aa]migos/, PatternsPT.daeos_], ~/[Aa]grupamento/, [~/[Aa]cademia/,'de','Belas-Artes'], 
       [~/[Aa]cademia/,'de','Ciências'], ~/[Aa]quário/, ~/[Aa]gência/, ~/[Bb]ombeiros/, ~/[Bb]iblioteca/, 
       [~/[Cc]omissão/,~/[Nn]acional/], [~/[Cc]ompanhia/,~/[Nn]acional/], ~/[Cc]omit[êé]/, ~/[Cc]onfederação/, 
       ~/[Cc]orpo/, [~/[Cc]ruz/,~/[Vv]ermelha/], ~/[Cc]olégio/, ~/[Ee]scola/, ~/[Ff]aculdade/, ~/[Ff]ederação/, 
       ~/[Ff]undação/, ~/[Hh]ospital/, ~/[Ii]nstitutos?/, ~/[Ll]iceu/,  [~/[Ll]iga/, ~/[Pp]ortuguesa/], ~/[Ll]egião/, ~/[Mm]ovimento/, ~/[Pp]artido/,
       ~/[Pp]olícia/, ~/[Ss]indicato/, ~/[Ss]ociedade/, ~/[Uu]niversidade/ ] ],
       
       [answer:SemanticClassification.create(Classes.category.organization, Classes.type.institution), needle:[
       ~/[Cc]ollege/, ~/[Ss]chool/, ~/[Uu]niversity/ ], reverse:true ],
    
       [answer:SemanticClassification.create(Classes.category.organization, Classes.type.intitution, 
		Classes.subtype.sub), needle:[
       [~/[Jj]unta/,~/[Mm]édica/], [~/[Ss]erviços?/,~/[Aa]cadémicos?/] ] ],
       
       /* ORGANIZACAO ADMINISTRACAO */
       [answer:SemanticClassification.create(Classes.category.organization, Classes.type.administration), needle:[
       ~/[Aa]ssembl[ée]ias?/, [~/[Aa]dministração/,~/[Rr]egional/], ~/[Cc]âmara/, [~/[Cc]omissariado/,~/[Rr]egional/], 
       [~/[Cc]onselho/,'de',~/[Mm]inistros?/], [~/[Dd]irec?ção/,~/[Rr]egional/], ~/[Ee]stado/, [~/[Ee]stado/,~/[Mm]aior/], 
       [~/[Ff]orças?/,~/[Aa]rmadas?/], ~/[Gg]overno/, [~/[Jj]unta/,'de',~/[Ff]reguesia/], ~/[Mm]inistério/, 
       ~/[Pp]arlamento/, ~/[Pp]refeitura/, [~/[Ss]ecretaria/,'de',~/[Ee]stado/] ] ],
       
       [answer:SemanticClassification.create(Classes.category.organization, Classes.type.other), needle:[
       ~/[Aa]cessoria/, ~/[Cc]entro/, ~/[Cc]oordenadoria/, ~/[Cc]onselho/, ~/[Cc]omissão/, ~/[Dd]irec?ção-[Gg]eral/, 
       [~/[Dd]irec?ção/,~/[Gg]eral/], [~/[Dd]irec?ção/,~/[Cc]entral/], ~/[Dd]ivisão/, ~/[Dd]epartamento/, ~/[Dd]e?pto\.?/, 
       ~/[Ff]ilial/, ~/[Gg]abinete/, ~/[Ll]aboratório/, ~/[Nn]úcleo/, ~/[Pp]ainel/, ~/[Ss]ec?tor/, ~/[Ss]ecrtaria(?:do)?/, 
       ~/[Ss]ec?ção/, ~/[Ss]erviços?/, ~/[Ss]ucursal/, ~/[Uu]nião/, [~/[Uu]nidade/,'de'] ] ],
   
       /**********************
       /* 6 - ACONTECIMENTO  *   
        **********************/    
        
        /* ACONTECIMENTO EFEMERIDE */
       [answer:SemanticClassification.create(Classes.category.event, Classes.type.happening), needle:[
       [~/[Bb]atalha/,PatternsPT.daeos_], [~/[Cc]apitulação/,PatternsPT.daeos_], 
       [~/[Dd]ia/,~/[Mm]undial/, PatternsPT.daeos_], ~/[Gg]uerra/, ~/[Ii]ndependência/, 
       [~/[Rr]estauração/,PatternsPT.daeos_], [~/[Rr]evolução/,PatternsPT.daeos_] ] ],
               
       /* ACONTECIMENTO ORGANIZADO */
       [answer:SemanticClassification.create(Classes.category.event, Classes.type.organized), needle:[
       ~/[Cc]ongresso/, ~/[Cc]onferência/, ~/[Cc]oncílio/, ~/[Cc]ampeonato/, ~/[Cc]imeira/, ~/[Ee]ncontros?/, 
       ~/[Ff]eira/, ~/[Ff]esta/, ~/[Ff]estival/, ~/[Jj]ogos/, ~/[Jj]ornadas?/, ~/[Rr]eunião/, 
       [~/[Tt]aça/,PatternsPT.daeos_], ~/[Ss]eminário/ ] ],
       
       /* ACONTECIMENTO EVENTO */
       [answer:SemanticClassification.create(Classes.category.event, Classes.type.pastevent), needle:[       
       [NumberGazetteerPT.eventNumber,~/[Cc]ongresso/], 
       [NumberGazetteerPT.eventNumber,~/[Cc]onferência/], 
       [NumberGazetteerPT.eventNumber,~/[Cc]oncílio/], 
       [NumberGazetteerPT.eventNumber,~/[Cc]ampeonato/], 
       [NumberGazetteerPT.eventNumber,~/[Cc]imeira/],   
       [NumberGazetteerPT.eventNumber,~/[Ee]ncontro/], 
       [NumberGazetteerPT.eventNumber,~/[Ff]eira/],  
       [NumberGazetteerPT.eventNumber,~/[Ff]esta/],  
       [NumberGazetteerPT.eventNumber,~/[Ff]estival/],  
       [NumberGazetteerPT.eventNumber,~/[Jj]ogos/],  
       [NumberGazetteerPT.eventNumber,~/[Jj]ornadas?/],  
       [NumberGazetteerPT.eventNumber,~/[Mm]aratona/], 
       [NumberGazetteerPT.eventNumber,~/[Rr]eunião/],  
       [NumberGazetteerPT.eventNumber,~/[Ss]eminário/], 
       ~/[Cc]oncerto/, [~/[Gg]rande/,~/[Pp]r[êé]mio/], ['GP',PatternsPT.daeos_], ~/[Mm]aratona/ ] ],
                
       /******************
       /* 7 - ABSTRACCAO *   
        ******************/   

        /* ABSTRACCAO DISCIPLINA */ 
       [answer:SemanticClassification.create(Classes.category.abstraction, Classes.type.discipline), needle:[
       ~/[Aa]eronáutica/, ~/[Cc]iências?/, ~/[Cc]ultura/, ~/[Dd]efesa/, ~/[Dd]ireito/, ~/[Ee]conomia/, 
       ~/[Ee]ducação/, ~/[Ee]nfermagem/, ~/[Ee]nsino/, ~/[Ee]ngenharia/, ~/[Ee]statística/, ~/[Ff]ilosofia/, 
       ~/[Ff]inanças/, ~/[Gg]estão/, ~/[Ii]nformática/, ~/[Jj]ustiça/, ~/[Ll]iteratura/, ~/[Ll]ógica/, 
       ~/[Mm]atemática/, ~/[Mm]arketing/, ~/[Mm]edicina/, ~/[Mm]etalurgia/, ~/[Mm]ineração/, ~/[Óo]p?tica/, 
       ~/[Qq]uímica/, ~/[Pp]sicologia/, ~/[Pp]robabilidades?/, ~/[Tt]ecnologia/, ~/[Tt]urismo/ ] ],
       
    
       /* ABSTRACCAO ESTADO */ // Abrange antiga ESCOLA e OBRA
       [answer:SemanticClassification.create(Classes.category.abstraction, Classes.type.state), needle:[
       ~/[Dd]oença/, ~/[Dd]eformação/, ~/[Ss][ií]ndrom[ea]s?/ ] ],      
       
       /* ABSTRACCAO IDEIA */
       [answer:SemanticClassification.create(Classes.category.abstraction, Classes.type.idea), needle:[
       ~/[Dd]ireitos/, ~/[Pp]átria/, ~/[Rr]eforma/, ~/[Cc]omunismo/, ~/[Ii]mpressionismo/, ~/[Nn]eoliberalismo/ ] ],
     
       /*************
       /* 8 - COISA *   
        *************/  
        
        /* COISA CLASSE */
        [answer:SemanticClassification.create(Classes.category.thing, Classes.type.'class'), needle:[
        ~/[Nn]obel/, ~/[Pp]r[êé]mio/ ] ],

       /* COISA OBJECTO */
      // [answer:SemanticClassification.create('COISA/, 'OBJECTO'], needle:[
    
       /* COISA SUBSTANCIA */
       [answer:SemanticClassification.create(Classes.category.thing, Classes.type.substance), needle:[
       ~/[Ee]lemento/, ~/[Ss]ubstância/ ] ],
       
       /************
       /* 9 - OBRA *   
        ************/  
  
        /* OBRA PLANO */
       [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.plan), needle:[
       ~/[Aa]rtigo/, ~/[Aa]cordos?/, ~/[Cc]ódigos?/, [~/[Cc]arta/,PatternsPT.daeos_], ~/[Dd]eclaraç(?:ão|ões)/, 
       ~/[Dd]irec?tiva/, ~/[Dd]ecreto-[Ll]ei/, [~/[Dd]ecreto/,~/[Ll]ei/], ~/[Ee]statuto/, ~/[Ii]mposto/, 
       ~/[Ll]ei/, [~/[Mm]edida/,~/[Pp]rovisória/], ~/[Nn]ormas?/, ~/[Oo]rçamento/, ~/[Oo]fício/, ~/[Pp]lano/, 
       ~/[Pp]rogramas?/, ~/[Pp]rotocolos?/, ~/[Qq]uadro/, ~/[Pp]rojec?to/, ~/[Rr]eceita/, ~/[Rr]eforma/, 
       ~/[Rr]egras?/, ~/[Ss]istema/, ~/[Tt]ratado/ ] ],

       /* OBRA REPRODUZIDA */
       [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.reproduced), needle:[
       ~/[Ll]ivro/ ]], 
       /* OBRA ARTE */
       [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.workofart), needle:[
       ~/[Ee]státua/ ]],
            
       /** ESPECIAIS **/ 
       [answer:SemanticClassification.create(Classes.category.organization, 
		 Classes.type.institution), needle:[
       ~/[Tt]eatro/, ~/[Mm]useu/, ['Centro','Cultural'] ] ],

       [answer:SemanticClassification.create(Classes.category.organization, 
		 Classes.type.administration),
       needle:[  ~/[Dd]iocese/, ~/[Cc]omarca/ ] ],
       
       [answer:SemanticClassification.create(Classes.category.organization, 
		Classes.type.institution) , 
       needle:[ [~/[Gg]rupo/,PatternsPT.daeos_], ~/[Rr]egimento/ ] ]
       
       ]
   
}