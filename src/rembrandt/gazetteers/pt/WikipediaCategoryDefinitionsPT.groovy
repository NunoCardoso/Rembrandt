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

import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes

/**
 * @author Nuno Cardoso
 * This class stores internal evidence definitions for Wikipedia categories,
 * and maps them to the corresponding classification categories.
 * 
 * WARNING: Encourage plural matches. Singular matches may be misleading. 
 * For example: "Paulo Portas" -> "Category:Partido Popular". FAIL
 * "PP" -> "Category:"Partidos de Portugal" WIN
 * 
 */
class WikipediaCategoryDefinitionsPT {
    
   static final List<Map> meanings = [
   
       /**************
       /*  1 - LOCAL *   
        **************/
 
       /* LOCAL HUMANO PAÍS */
        [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.human, Classes.subtype.country), needle:[
      ~/[Pp]aíses/, ~/[Dd]emocracias/, ~/[Rr]epúblicas/, ~/[Rr]einos/, ~/[Ee]miratos/,
      ~/[Nn]ações/, ~/[Dd]ependências/, ~/[Tt]errotórios/, ~/[Aa]utónomos/ ]  ], 
  
       /* LOCAL HUMANO DIVISÃO */  
        [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.human, Classes.subtype.division), needle:[
      ~/[Aa]ldeias/, ~/[Áá]reas/, ~/[Mm]etropolitanas/, ~/[Cc]apitais/, ~/[Cc]idades/,
      ~/[Cc]omunas/, ~/[Cc]oncelhos/, ~/[Cc]ontinentes/, ~/[Ee]stados/, ~/[Ff]reguesias/,
      ~/[Ll]ocalidades/, ~/[Mm]etrópoles/, ~/[Mm]unicípios/, ~/[Pp]rovíncias/,
      ~/[Uu]nidades/, ~/[Ff]ederativas/, ~/[Vi]las/ ] ],

       /* LOCAL HUMANO REGIAO */
        [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.human, Classes.subtype.region), needle:[
      [~/[Cc]lassificação/, 'dos', ~/[Pp]aíses/], [~/[Gg]randes/, ~/[Áá]reas/, ~/[Mm]etropolitanas/],
      ~/[Rr]egiões/, [~/NUTS/, ~/I*/] ] ],
       
       /* LOCAL HUMANO CONSTRUCAO */
       // 'Casa de' não é boa ideia, há Casa de Bourbon... 
       [answer:SemanticClassification.create(Classes.category.place, 
	       Classes.type.human, Classes.subtype.construction), needle:[
       ~/[Aa]eroportos/, ~/[Aa]er[óô]dromos/, ~/[Bb]arragens/, ~/[Cc]onstruções/, 
       ~/[Cc]atedrais/, ~/[Cc]astelos/, ~/[Cc]inemas/, ~/[Cc]onventos/, [~/[Ee]spaços/, ~/[Vv]erdes/], 
       ~/[Ee]stádios/, ~/[Ee]difícios/, ~/[Ff]ortalezas/, ~/[Ii]grejas/, ~/[Jj]ardins/, ~/[Mm]useus/,
       ~/[Mm]onumentos/, ~/[Mm]osteiros/, ~/[Pp]alácios/, [~/[Pp]atrimónios/, ~/[Ee]dificados/], 
       ~/[Pp]ontes/, ~/[Pp]ortos/, ~/[Tt]eatros/, ~/[Tt]orres/, ~/[Tt]eatros/ ] ],
       
       /* LOCAL HUMANO RUA */
       [answer:SemanticClassification.create(Classes.category.place, 
	       Classes.type.human, Classes.subtype.street), needle:[
       ~/[Aa]uto-[Ee]stradas/, ~/[Aa]venidas/, ~/[Bb]ecos/, ~/[Ee]stradas/,
       ~/[Ii]tinerários/, ~/[Ll]argos/, ~/[Pp]raças/, ~/[Rr]uas/, ~/[Tt]oponímias/ ] ],

       /* LOCAL FISICO AGUACURSO */
       [answer:SemanticClassification.create(Classes.category.place, 
	       Classes.type.physical, Classes.subtype.watercourse), needle:[
       ~/[Aa]fluentes/, ~/[Cc]achoeiras/, ~/[Cc]ataratas/, ~/[Rr]ios/, 
       ~/[Rr]iachos/, ~/[Rr]ibeiros/ ] ],

       /* LOCAL FISICO AGUAMASSA */
        [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.physical, Classes.subtype.watermass), needle:[
       ~/[Bb]acias/, ~/[Hh]idrográficas/, ~/[Bb]aías/, ~/[Cc]ana[il]s/, ~/[Gg]olfos/, 
       ~/[Ll]agos/, ~/[Mm]ares/, ~/[Oo]ceanos/ ] ],
       
       /* LOCAL FISICO RELEVO */
        [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.physical, Classes.subtype.mountain), needle:[
       [~/[Aa]cidentes/, ~/[Gg]eográficos/], ~/[Cc]ordilheiras/, ~/[Mm]ontanhas/, ~/[Mm]ontes/,
        ~/[Pp]lanícies/, ~/[Pp]lanaltos/, ~/[Pp]icos/, ~/[Ss]erras/, ~/[Vv]ales/ ] ],   
       
       /* LOCAL FISICO PLANETA */
        [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.physical, Classes.subtype.planet), needle:[
       ~/[Pp]lanetas/, ~/[Ee]strelas/, ~/[Ss]atélites/, ~/[Cc]onstelações/, 
       [~/[Ss]istemas/, ~/[Ss]olares/] ] ],
     
       /* LOCAL FISICO ILHA */
        [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.physical, Classes.subtype.island), needle:[
       ~/[Aa]rquipélagos/, ~/[Ii]lhas/ ] ],          
 
       /* LOCAL FISICO REGIAO */
        [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.physical, Classes.subtype.region), needle:[
       ~/[Bb]osques/, ~/[Ee]streitos/, ~/[Ff]lorestas/ ] ],
       
       /* LOCAL FISICO  */
       [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical), 
		needle:[ ~/[Gg]eografias/ ] ],
        
       /* LOCAL VIRTUAL COMSOCIAL */
       [answer:SemanticClassification.create( Classes.category.place, 
	       Classes.type.virtual, Classes.subtype.media), needle:[
       [~/[Cc]anais/, 'de', ~/[Tt]elevisão/], ~/[Ee]ditores/, [~/[Ee]stações/, 'de', ~/[Rr]ádio/], 
       ~/[Jj]orna[li]s?/, ~/[Mm][éí]dias?/, [~/[Pp]rogramas/, 'de', ~/[Tt]elevisão/], ~/[Rr]ádios/,
       [~/[Rr]edes/, 'de', ~/[Tt]elevisão/], ~/[Rr]evistas/, ~/[Tt]elevisões/, ~/[Tt]elejornais/,
       ~/[Tt]elenovelas/ ] ],
            
       /* LOCAL VIRTUAL SITIO */
        [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.virtual, Classes.subtype.site), needle:[
        [~/[Rr]edes/, 'de', ~/[Cc]omputadores/] ] ],

       /* LOCAL VIRTUAL OBRA */
        [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.virtual, Classes.subtype.article), needle:[
        ~/[Ee]nciclopédias/ ] ],

       /**************
       /* 2 - PESSOA *   
        **************/
        
       /* PESSOA INDIVIDUAL */
        //Ver Categoria:Brasileiros_por_ocupação  
       [answer:SemanticClassification.create(Classes.category.person, Classes.type.individual), needle:[
       ~/[Aa]c?tores/, ~/[Aa]c?trizes/, ~/[Aa]lpinistas/, ~/[Aa]ntropólogos/, ~/[Aa]stronautas/,
       ~/[Aa]rquitec?t[oa]s/, ~/[Aa]str[ôó]nom[oa]s/, ~/[Aa]tletas/, ~/[Aa]utor[ae]s/, 
       ~/[Aa]dvogad[ao]s/, ~/[Aa]presentador[ea]s/, ~/[Aa]rgumentistas/, ~/[Aa]rqueólogos/, 
       ~/[Áá]rbitros/, ~/[Aa]rtistas/, ~/[Aa]viadores/, ~/[Bb]ateristas/, ~/[Bb]anqueiros/,  
       ~/[Bb]eatos/, ~/[Bb]ibliotecários/, ~/[Bb]ispos/, ~/[Bb]aixistas/,  ~/[Cc]ardeais/, 
       ~/[Cc]artunistas/, ~/[Cc]artógrafos/, ~/[Cc]antor[ae]s/, ~/[Cc]eramistas/, 
       ~/[Cc]ineastas/, ~/[Cc]inegrafistas/, ~/[Cc]ientistas/, ~/[Cc]ompositor[ae]s/, 
       ~/[Cc]ríticos/, ~/[Cc]olunistas/, ~/[Cc]riminosos/, ~/[Cc]oron[ée]is/, ~/DJs/, 
       ~/[Dd]ançarinos/, ~/[Dd]esenhistas/, ~/[Dd]esportistas/, [~/[Dd]irectores/, 'de', ~/[Pp]rogramas?/], 
       ~/[Dd]irigentes/, ~/[Dd]iplomatas/,  ~/[Dd]ramaturg[oa]s/, ~/[Dd]eputad[oa]s/, 
       ~/[Ee]scritor[ae]s?/, ~/[Ee]mpresários/, ~/[Ee]ncenadores/, ~/[Ee]stilistas/, 
       ~/[Ee]tnógrafos/, ~/[Ee]scultor[ae]s/, ~/[Ee]ngenheir[ao]s/, ~/[Ee]conomistas/, 
       ~/[Ee]xplorador[ae]s/, ~/[Ff]ilantropos/, ~/[Ff]utebolistas/, ~/[Ff]il[ôó]sof[ao]s/, 
       ~/[Ff]ísic[ao]s/, ~/[Ff]otógrafos/, ~/[Gg]enerais/, ~/[Gg]estores/, 
       ~/[Gg]overnador[ea]s/, ~/[Gg]regos/, ~/[Aa]ntigos/, ~/[Gg]uitarristas/, ~/[Hh]istoriadores/, 
       ~/[Hh]umoristas/, ~/[Hh]umanistas/, ~/[Ii]mperador[ea]s/, ~/[Ii]lustradores/, 
       ~/[Ii]nventores/, ~/[Jj]ogador[ea]s?/, ~/[Jj]ornalistas?/, ~/[Jj]uristas?/, ~/[Jj]udocas?/, 
       ~/[Jj]uízes/, ~/[Ll]inguistas/, ~/[Mm]aestros/, ~/[Mm]agistrados/, ~/[Mm]atemáticos/, ~/[Mm]édicos/, 
       ~/[Mm]édiu[mn]s/, ~/[Mm]ilitares/, ~/[Mm]inistros/, ~/[Mm]odelos/, ~/[Mm]úsicos/, 
       ~/[Nn]avegadores/, ~/[Oo]rquestradores/, ~/[Pp]adres/, ~/[Pp]edagogos/, 
       ~/[Pp]rocuradores-[Gg]erais/, ~/[Pp]ugilistas/, ~/[Pp]ersonalidades/, ~/[Pp]intor[ae]s/, 
       ~/[Pp]rimeir[ao]s-[Mm]inistr[ao]s/, [~/[Pp]rimeir[ao]s/, ~/[Mm]inistr[ao]s/], ~/[Pp]remi[eê]r/, 
       ~/[Pp]refeitos/, ~/[Pp]residentes/, ~/[Pp]rofessor[ae]s/, ~/[Pp]ilotos/, ~/[Pp]oetas/, 
       ~/[Pp]olíticos/, ~/[Pp]rodutor[ae]s/, ~/[Pp]sicólogos/, ~/[Pp]siquiatras/, ~/[Pp]renomes/,
       ~/[Rr]evolucionários/, ~/[Rr]ealizador[ae]s/, ~/[Rr]omancistas/, ~/[Rr]eis/, 
       ~/[Rr]aínhas/, ~/[Rr]egentes/, ~/[Rr]adialistas/, ~/[Rr]eligiosos/, ~/[Rr]omancistas/, 
       ~/[Ss]enador[ea]s/, ~/[Ss]exólogos/, ~/[Ss]ociólogos/, ~/[Ss]indicalistas/, 
       ~/[Tt]enistas/, ~/[Tt]eólogos/, ~/[Tt]ipógrafos/, ~/[Tt]oureiros/, ~/[Tt]radutores/,     
       ~/[Tt]reinador[ea]s/, ~/[Vv]eterinários/, ~/[Zz]oólogos/,
       
       // PESSOA OU ABSTRACÇAO?
       ~/[Aa]njos/, ~/[Dd]ivindades/, ~/[Pp]ersonagens/, [~/[Ss]eres/, ~/[Mm]íticos/] ] ] ,
             
       /* PESSOA CARGO */
       [answer:SemanticClassification.create(Classes.category.person, Classes.type.position), needle:[                                                                    
       [~/[Cc]argos/, ~/[Pp]úblicos/], [~/[Tt]ítulos/, 'de', ~/[Nn]obreza/] ] ],
       
       /* PESSOA GRUPOMEMBRO */
       [answer:SemanticClassification.create(Classes.category.person, Classes.type.membergroup), needle:[
       [~/[Bb]andas/, PatternsPT.daeos_], [~/[Cc]ompanhias/,PatternsPT.daeos_], 
       ~/[Ee]quip[ae]s/,  ~/[Tt]imes/, ] ],   
 
       /* PESSOA POVO */
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.people), 
                     needle:[~/[Pp]ovos/ ] ],
       
       /*******************
       /* 3 - ORGANIZACAO *   
        *******************/
       
       /* ORGANIZACAO EMPRESA */
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.company), needle:[
        [~/[Cc]entros/, ~/[Cc]omerciais/], ~/[Ee]mpresas/, ~/[Gg]ravadoras/ ] ],

       /* ORGANIZACAO INSTITUICAO */
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.institution), needle:[
        ~/[Aa]ssociações/, ~/[Cc]ol[êé]gios/, [~/[Cc]lubes/, ~/de/, ~/[Ff]utebol/], ~/[Ee]scolas/, 
        [~/[Ee]mpresas/, ~/[Ee]statais/], ~/[Hh]ospitais/, 
        ~/[Ii]nstituições/, ~/[Ii]nstitutos/, [~/[Pp]artidos/, ~/[Pp]olíticos/], 
        [~/[Pp]artidos/, ~/[Ee]xtintos/], ~/[Uu]niversidades/ ] ],
       
       /* ORGANIZACAO ADMINISTRACAO */
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.administration), needle:[
        [~/[Aa]dministração/, ~/[Pp]ública/], [~/[Aa]dministração/, ~/[Mm]ilitar/], 
        [~/[Aa]gências/, ~/[Ee]spacia(?:l|is)/], ~/[Cc]âmaras/, [~/[Ff]orças/, ~/[Aa]rmadas/], 
        [~/[Nn]ações/, ~/[Uu]nidas/], [~/[Oo]rganizações/, ~/[Ii]nternacionais/],
 		  [~/[Óó]rgãos/, ~/[Ll]egislativos/],
        [~/[Oo]rganismos/, ~/[Ee]speciais/], ~/[Pp]arlamentos/, ~/[Tt]ribuna(?:l|is)/, 
	     [~/[Uu]nião/, ~/[Ee]uropeia/] ] ], 
    
       /**************
       /* 4 - TEMPO  *   
        **************/
       
       /* TEMPO  TEMPO_CALEND DATA */
       [answer:SemanticClassification.create(Classes.category.time, Classes.type.calendar,
	       Classes.subtype.date), needle:[
	[~/[Aa]nos/, 'do', ~/[Ss]éculo/], [~/[Dd]atas/, ~/[Cc]omemorativas/], [~/[Dd]ias/, 'da', ~/[Ss]emana/], 
	[~/[Es]stações/, 'do', ~/[Aa]no/], ~/[Ff]estividades/,  ~/[Pp]ré-[Hh]istória/ ] ] ,   
    
       /* TEMPO  TEMPO_CALEND DATA */
       [answer:SemanticClassification.create(Classes.category.time, Classes.type.duration), 
             needle:[ ['História', 'por', 'período' ] ] ],  
       
       /**************
       /* 5 - VALOR  *   
        **************/  
     
       /**********************
       /* 6 - ACONTECIMENTO  *   
        **********************/    
        
        /* ACONTECIMENTO EFEMERIDE */
         [answer:SemanticClassification.create(Classes.category.event, Classes.type.happening), 
             needle:[ [~/[Aa]contecimentos/, ~/[Dd]esportivos/], [~/[Dd]esportos/, 'de', ~/\d{4}/],  // overrides "Desportos"
                      ~/[Gg]uerras/, ~/[Rr]evoltas/, ~/[Rr]evoluções/ ] ],     
        
        /* ACONTECIMENTO ORGANIZADO */
        [answer:SemanticClassification.create(Classes.category.event, Classes.type.organized), needle:[
        ['Competições', 'desportivas'], ["Eventos","multi-esportivos"] ] ],
       
        /* ACONTECIMENTO EVENTO */
        [answer:SemanticClassification.create(Classes.category.event, Classes.type.pastevent), needle:[
	  [~/GPs/, 'de', ~/[Ff]órmula/ ] ] ],
 
       /******************
       /* 7 - ABSTRACCAO *   
        ******************/   
       
       /* ABSTRACCAO DISCIPLINA */ 
        [answer:SemanticClassification.create(Classes.category.abstraction, Classes.type.discipline), needle:[
        // Ciência e tecnologia                                                                        
        [~/[Cc]iências/, ~/[Hh]umanas/], [~/[Cc]iências/, ~/[Ss]ociais/], [~/[Cc]iências/, 'da', ~/[Ss]aúde/],        
        [~/[Cc]iências/, ~/[Ee]xac?tas/], ~/[Ee]conomia/, ~/[Ee]ngenharia/, ~/[Ff]ísica/, ~/[Tt]ecnologia/,        
        // Movimentos 
        [~/[Mm]ovimentos/, ~/[Aa]rtísticos/], [~/[Mm]ovimentos/, ~/[Cc]ulturais/], ~/[Cc]ristianismo/,  
        // Política
        ~/[Dd]itaduras/, [~/[Rr]egimes/, ~/[Pp]olíticos/], ~/[Ss]ociedade/,    
        // outros
        ~/[Aa]utomobilismos/, ~/[Dd]esportos/,  [~/G[éê]neros/, ~/[Mm]usicais/] ] ],  

       /* ABSTRACCAO ESTADO */ 
        [answer:SemanticClassification.create(Classes.category.abstraction, Classes.type.state), needle:[
        ~/[Dd]oenças/, ~/[Dd]eformações/, ~/[Ss][ií]ndrom[ea]s/ ] ],    
        
       /* ABSTRACCAO IDEIA */
        [answer:SemanticClassification.create(Classes.category.abstraction, Classes.type.idea), needle:[
       // Direitos da Mulher
        ~/[Dd]ireitos/] ],
       
       /*************
       /* 8 - COISA *   
        *************/  
        
        /* COISA CLASSE */
        [answer:SemanticClassification.create(Classes.category.thing, Classes.type.'class'), needle:[
        ~/[Ee]muladores/, [~/[Ff]ormatos/, ~/[Dd]igitais/], [~/[Pp]rotocolos/, ~/[Ii]nternet/], //HTTP, SOCKS      
        ~/[Ss]oftwares/, [~/[Tt]erminologia/, ~/[Ii]nformática/] ] ],     // SPAM
         
       /* COISA OBJECTO */
        [answer:SemanticClassification.create(Classes.category.thing, Classes.type.object), needle:[
       ~/[Aa]viões/, ~/[Ff]oguetões/, ~/[Vv]eículos/ ] ],       
            
       /* COISA SUBSTANCIA */
       [answer:SemanticClassification.create(Classes.category.thing, Classes.type.substance), needle:[
       ~/[Dd]rogas/, [~/[Ee]lementos/, ~/[Qq]uímicos/] ] ],   
       
       /************
       /* 9 - OBRA *   
        ************/  
  
       /* OBRA PLANO */
        [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.plan), needle:[
        ~/[Cc]onstituições/, ~/[Ii]mpostos/, [~/[Pp]rogramas/, ~/[Ss]ociais/], 
        [~/[Tt]ác?ticas/, 'de'], ~/[Tt]ratados/ ] ] ,  // 4-4-3 ! :) Tácticas de futebol
             
       /* OBRA REPRODUZIDA */
       [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.reproduced), needle:[
       ~/[áÁ]lbu[mn]s/, ~/[Ff]ilmes/,  ~/[Ll]ivros/, [~/[Pp]eças/, 'de', ~/[Tt]eatro/ ] ] ] ,   
              
       /* OBRA ARTE */
       [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.workofart), needle:[
       ~/[Ee]sculturas/, ~/[Pp]inturas/ ] ]
       ]
}