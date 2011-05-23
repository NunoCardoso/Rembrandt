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

import rembrandt.obj.Clause
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for LOCAL.
 * Gentílicos retirados de 
 * http://www.portaldalinguaportuguesa.org/?action=gentilicos&act=list
 */
class LocalGazetteerPT {
	    
    static final List continent = ['América',['América','do', ~/(?:Norte|Sul)/],['América', ~/(?:[Cc]entral|[Ll]atina)/],
     'Antártica','Ásia','Europa',~/Oce[âa]nia/,'África',~/Euro?ásia/,~/Árc?tico/,'Eurafrásia',~/Pang[eé]ia/]
    
    static country = ['Abecásia','Afeganistão',['África','do','Sul'],'Albânia','Alemanha','Andorra',
    'Angola',['Antígua','e','Barbuda'],['Arábia','Saudita'],'Argélia','Argentina',~/Arm[êé]nia/,'Austrália','Áustria','Azerbaijão',
// B
    'Bah?amas','Bangladeche','Bangladesh','Barbados','Barém','Bahrein','Bélgica','Belize',~/Beni[mn]/,~/Bielor?rússia/,
	'Bolívia','Bósnia',['Bósnia','e','Herzegovina'],'Botsuana','Botswana',~/Bra[sz]il/,'Brunei','Bulgária',
	['Burquina','Faso'],['Burkina','Faso'],'Burundi','Butão','Birmânia',
// C
    ['Cabo','Verde'],'Camarões','Cambod?ja','Canadá',~/[CQ]atar/,'Cazaquistão',['República','Centro-Africana'],
    'Chade',['República','T?[Cc]heca'],'Chile',['República','Popular','da','China'],'China','Chipre',~/[SC]ingapura/,
    'Colômbia','Comores','Congo',['República','Democrática','do','Congo'],'Congo-Quinchasa','Congo-Brazavile',
    ~/Cor[ée]ia/, [~/Cor[ée]ia/,'do',~/(?:Norte|Sul)/],['Costa','do','Marfim'],['Côte','d\'Ivoire'],
    ['Costa','Rica'],'Couaite','Croácia','Cuba',
// D    
    'Dinamarca',~/D?[Jj]ibo?uti/,'Dominica',['República','Dominicana'],
// E 
     ~/Egip?to/,['El','Salvador'],['Emirados','Árabes','Unidos'],'Equador',~/Eritr[ée]ia/,'Escócia','Eslováquia',
     ~/Eslov[éê]nia/,'Espanha',['Estados','Unidos','da','América'],['Estados','Unidos'],~/Est[óô]nia/,'Etiópia',
//F
	'Fiji','Filipinas','Finlândia','Formosa','França',
//G
	'Gabão','Gâmbia','Gana','Geórgia','Grã-Bretanha','Granada','Grécia','Guatemala','Guiana','Guiné',
	'Guiné-Bissau',['Guiné','Equatorial'],
//H
	'Haiti','Holanda','Honduras','Hungria',
//I	
	~/[YI][éê]men/,'Índia','Indonésia','Inglaterra',~/Irão?/,'Iraque','Irlanda','Islândia','Israel','Itália',
//J		
	'Jamaica','Japão','Jordânia',
//K	
	~/[KQ]iribati/,~/Kuw[ae]it/,'Kuati','Kosovo',
//L	
	~/La[ou]s/,~/Lesoth?o/,~/Let[óô]nia/,'Líbano','Libéria','Líbia','Listenstaine','Liechtenstein','Lituânia','Luxemburgo',
//M		
	~/Maced[ôó]nia/,'Madagáscar',~/Mal[aá]í?sia/,~/Mala[wu]i/,'Maldivas','Mali','Malta','Marrocos',['Ilhas','Marechal'],
	['Ilhas','Marshall'],~/Maurícias?/,'Mauritânia','México','Mianmar','Micronésia','Moçambique','Moldova','Moldávia',
	~/M[ôó]naco/,'Mongólia','Montenegro',
//N	
	'Nagorno-Carabaque','Nagorno-Karabakh','Namíbia','Nauru','Nepal','Nicarágua','Níger','Nigéria','Noruega',['Nova','Zelândia'],
//O
	~/Omão?/,'Ossétia',['Ossétia','do','Sul'],
//P
	['Países','Baixos '],'Palau','Palestina','Panamá',['Papua','Nova','Guiné'],'Paquistão','Paraguai','Peru',~/Pol[ôó]nia/,'Portugal',
//Q
	~/Qu[êé]nia/,'Quirguistão',
//R
	['Reino','Unido'],~/Rom[êé]nia/,~/R[wu]anda/,'Rússia',
//S	
	['Ilhas','Salomão'],'Samoa',['Santa','Lúcia'],['São','Cristóvão','e','Neves'],['Saint','Kitts','e','Nevis'],
    ['São','Marinho'],['San','Marino'],['São','Tomé','e','Príncipe'],['São','Vicente','e','Granadinas'],
    ['Saara','Ocidental'],'Senegal',[~/Se?ri/,~/Lan[kc]a/],['Serra','Leoa'],'Sérvia',~/Se[iy]chell?es/,
    'Síria','Somália','Somalilândia','Suazilândia','Sudão','Suécia','Suíça','Suriname',
//T
    'Tailândia','Taiwan','Tajiquistão','Tanzânia','Timor-Leste','Togo','Tonga','Transnístria',
    [~/Trini?dade?/,'e','Tobago'],'Tunísia','Turcomanistão','Turquia','Tuvalu',
//U
	'Ucrânia','Uganda','Uruguai', ~/U[zs]bequistão/,
//V
	'Vanuatu',['Cidade','do','Vaticano'],'Vaticano','Venezuela',~/Vietname?/,'Vietnã',
//Z
	'Zâmbia','Zaire', ~/Zimbab[wu][eé]/
	]

    static final List region = ["Curdistão","Escandinávia", ~/Lap[ôó]nia/]
   
    static final List city = []

    static final List continentAdjective = [
    ~/american[oa]s?/,~/asiátic[oa]s?/,~/europe(?:u|ia)s?/,~/african[ao]s?/,~/(?:latino|sul|centro)-american[oa]s?/] 		
   		     
    static final List countryAdjective = [
    ~/[Aa]fegãos?/, ~/[Ss]ul-[Aa]frican[oa]s?/, ~/[Aa]lban[êe]s[ae]?s?/, ~/[Aa]lemão?s?/, ~/[Aa]ngolan[ao]s?/, ~/[Aa]rgelin[ao]s?/,
    ~/[Aa]rgentin[oa]s?/, ~/[Aa]rméni[oa]s?/, ~/[Aa]ustralian[ao]s?/, ~/[Aa]ustríac[oa]s?/, ~/[Aa]zerbaid?jan[oa]s?/, ~/[Áá]rabes?/, 
    ~/[Bb]ah?amenses?/, ~/[Bb]alin[êe]s[ae]?s?/, ~/[Bb]engali/, ~/[Bb]anglade[cs]hian[oa]s?/, ~/[Bb]elgas?/, ~/[Bb]elizenses?/, ~/[Bb]irman[êe]s[ae]?s?/,
    ~/[Bb]elizenh[ao]s?/, ~/[Bb]enin[êe]s[ae]?s?/, ~/[Bb]eninenses?/, ~/[Bb]ieloruss[ao]s?/, ~/[Bb]issauenses?/, ~/[Bb]olivian[ao]s?/,	
    ~/[Bb]ósnios?/, ~/[Bb]rasileiro/, ~/[Bb]runean[ao]s?/, ~/[Bb]úlgar[oa]s?/, ~/[Cc]abo-[Vv]erdian[oa]s?/, ~/[Cc]amaronenses?/, ~/[Cc]heco/, ~/[Cc]hecoslovac[oa]s?/,
    ~/[Cc]ambod?jan[oa]s?/, ~/[Cc]anadian[oa]s?/, ~/[Cc]hilen[oa]s?/, ~/[Cc]hin[êe]s[ea]?s?/, ~/[Cc]ipriotas?/, ~/[CcSs]ingapurenses?/, ~/[Cc]ingal[êe]s[ea]?s?/,
    ~/[Cc]olombian[oa]s?/, ~/[Cc]azaques?/, ~/[Cc]hadiano/, ~/[Cc]ongolês/, ~/[Cc]ongolenses?/, ~/[Cc]osta-[Mm]arfinenses?/, ~/[Cc]osta-[Rr]iquenh[ao]s?/,
    ~/[Cc]roatas?/, ~/[Cc]uban[ao]s?/, ~/[Dd]inamarqu[êe]s[ea]?s?/, ~/[Ee]gíp?ci[oa]s?/, ~/[Ee]quatorian[oa]s?/, ~/[Ee]ritreus?/,
    ~/[Ee]scoc[êe]s[ae]?s?/, ~/[Ee]slovac[ao]s?/, ~/[Ee]sloven[ao]s?/, ~/[Ee]spanh[óo][il]a?s?/, ~/[Ee]stado-[Uu]dinenses?/,
    ~/[Ee]stonian[oa]s?/, ~/[Ee]tíopes?/, ~/[Ff]id?jian[oa]s?/, ~/[Ff]ilipin[oa]s?/, ~/[Ff]inland[eê]s[ea]?s?/, ~/[Ff]ranc[êe]s[ae]?s?/,
    ~/[Gg]abon[eê]s[ea]?s?/, ~/[Gg]ambian[oa]s?/, ~/[Gg]an[êe]s[ea]s?/, ~/[Gg]eorgian[ao]s?/, ~/[Gg]ermânic[oa]s?/, ~/[Gg]ranadin[oa]s?/,
    ~/[Gg]reg[ao]s?/, ~/[Gg]uatemaltecas?/, ~/[Gg]uatemaltenses?/, ~/[Gg]uianenses?/, ~/[Gg]uineenses?/,/[Hh]aitian[oa]s?/,
    ~/[Hh]oland[eê]s[ea]s?/, ~/[Hh]ondorenh[oa]s?/, ~/[Hh]úngar[oa]s?/, ~/[Ii][ée]menitas?/, ~/[Ii]ndian[oa]s?/, ~/[Ii]ndonési[ao]s?/,
    ~/[Ii]ngl[êe]s[ea]?s?/, ~/[Ii]ranian[oa]s?/, ~/[Ii]raquian[oa]s?/, ~/[Ii]rland[êe]s[ea]?s?/, ~/[Ii]sland[eê]s[ea]?s?/, ~/[Ii]sraelitas?/,	
    ~/[Ii]talian[oa]s?/, ~/[Jj]amaican[oa]?s?/, ~/[Jj]apon[êe]s[ea]?s?/, ~/[Jj]ordan[oa]s?/, ~/[Kk][ou]waitian[oa]s?/, ~/[Ll]ao[cs]ian[oa]s?/,
    ~/[Ll]esotian[oa]s?/, ~/[Ll]et(ão|ões)/, ~/[Ll]iban[êe]s[ea]?s?/, ~/[Ll]iberian[oa]s?/, ~/[Ll]íbic?[oa]s?/, ~/[Ll]ituan[oa]s?/,
    ~/[Ll]uxemburgu[êe]s[ae]?s?/, ~/[Mm]acedóni[oa]s?/, ~/[Mm]alai[ao]s?/, ~/[Mm]ala[uv]ian[oa]s?/, ~/[Mm]alt[eê]s[ea]?s?/, ~/[Mm]exican[oa]s?/,
    ~/[Mm]oçambican[ao]s?/, ~/[Mm]oldav[ao]s?/, ~/[Mm]ong[ol|óis]/, ~/[Mm]onegascos?/, ~/[Nn]apolitan[oa]s?/, ~/[Nn]epal[eê]s[ae]?s?/, ~/[Nn]igerian[oa]s?/,
    ~/[Nn]oruegu[êe]s[ea]?s?/, ~/[Nn]eo-?[Zz]eland[êe]s[ea]s?/, ~/[Nn]orte-[Aa]merican[oa]s?/, ~/[Oo]manens[ae]s?/, ~/[Pp]alestin[oa]s/,
    ~/[Pp]alestinian[oa]s?/, ~/[Pp]anamenses?/, ~/[Pp]aquistan[eê]s[ae]?s?/, ~/[Pp]araguai[ao]s?/, ~/[Pp]eruan[ao]s?/, ~/[Pp]olac[oa]s?/,
    ~/[Pp]olon[êe]s[ea]?s?/, ~/[Pp]orto-[Rr]iquenh[ao]s?/, ~/[Pp]ortugu[êe]s[ea]?s?/, ~/[Qq]uenian[oa]s?/, ~/[Qq]irguistan[eê]s[ea]?s?/,
    ~/[Rr]uand[êe]s[ea]?s?/, ~/[Rr]uss[oa]?s?/, ~/[Ss]alvadorenh[oa]s?/, ~/[Ss]amoan[oa]s?/, ~/[Ss]ão-marinenes?/, ~/[ss]enegal[eê]s[ae]?s?/,
    ~/[Ss]erra-leonin[oa]s?/, ~/[Ss]érvi[ao]s?/, ~/[Ss]íri[ao]s?/, ~/[Ss]aoas?/, ~/[Ss]ul-[Cc]orean[oa]s?/, ~/[Ss]omalis?/, ~/[Ss]uazis?/,  
    ~/[Ss]udan[êe]s[ea]?s?/, ~/[Ss]uec[oa]s?/, ~/[Ss]uíç[oa]s?/, ~/[Ss]urinam[êe]s[ea]?s?/, ~/[Tt]ailand[êe]s[ea]?s?/, ~/[Tt]aiwan[êe]s[ea]?s?/,
    ~/[Tt]ajiques?/, ~/[Tt]anzanian[oa]s?/, ~/[Tt]asmanian[oa]s?/, ~/[Tt]imorenses?/, ~/[Tt]rinitári[oa]s?/, ~/[Tt]unisin[oa]s?/, ~/[Tt]urc[oa]s?/,
    ~/[Tt]urquemenistan[eê]s[ea]?s?/, ~/[Tt]uvaluan[oa]s?/, ~/[Uu]gand[êe]s[ea]?s?/, ~/[Uu]ruguai[oa]s?/, ~/[Uu][sz]beques?/, ~/[Uu]cranian[oa]s?/,
    ~/[Vv]enezuelan[oa]s?/, ~/[Vv]ietnamitas?/, ~/[Zz]ambian[oa]s?/, /[Zz]imbabuan[oa]s?/ ]
		 
    static final List alternativeCountryAdjective=[
	 /[Aa]lfacinhas?/, ~/[Cc]ariocas?/, ~/[Ff]luminenses?/, ~/[Hh]ispânic[oa]s?/, ~/[Hh]elénic[oa]s?/, ~/[Hh]indus?/, ~/[Hh]elvétic[oa]s?/,
	 /[Ii]anques?/, ~/[Mm]agiar(?:es)?/, ~/[Mm]aub[ée]re/, ~/[Nn]ortenho/, ~/[Pp]ortistas?/, /[Tt]ripeir[oa]s?/]
	      				
    static regionAdjective = [
        ~/[Aa]çor[ie]an[oa]s?/, ~/[Aa]lentejan[oa]s?/, ~/[Aa]lgarvi[ao]s?/,
        ~/[Bb]arcelenses?/, ~/[Bb]eirão?s?/, ~/[Bb]eirenses?/, ~/[Bb]orgonh[eê]s[ea]?s?/, ~/[Bb]retão?/, ~/[Bb]retões/, ~/[Bb]ritânic[oa]s?/,		
        ~/[Cc]atalão?s?/, ~/[Gg]aleg[oa]s?/, ~/[Cc]alifornian[oa]s?/, ~/[Cc]anarin[ao]s?/, ~/[Cc]antábric[ao]s?/,
        ~/[Cc]antão/, ~/[Cc]anton[êe]s[ae]?s?/, ~/[Cc]aucasian[oa]s?/, ~/[Tt]?[Cc]het?chen[oa]s?/, ~/[Cc]retense/, ~/[Cc]urdos?/,
        ~/[Dd]urienses?/, ~/[Dd]ominiquenses?/, ~/[Ee]scandinav[oa]s?/, ~/[Ee]uropeus?/, ~/[Ff]lamengos?/, ~/[Gg]oan[oa]s?/, ~/[Gg]al[eê]s[ae]?s?/,
        ~/[Gg]ibraltin[ao]s?/, ~/[Hh]avaian[oa]s?/, ~/[Kk]osovar(?:es)?/, ~/[Ii]béric[oa]s?/, ~/[Ll]ap(?:ão|ões)/, ~/[Mm]acaense/, ~/[Mm]adeirenses?/,
        ~/[Mm]inhot[oa]s?/, ~/[Mm]ontenegrin[oa]s?/, ~/[Nn]ordestin[ao]s?/, ~/[Nn]ormand[oa]s?/, ~/[Ss]adin[ao]s?/, ~/[Ss]iberian[oa]s?/,
        ~/[Ss]icilian[oa]s?/, ~/[Tt]ártar[oa]s?/, ~/[Tt]exan[oa]s?/, ~/[Tt]ibetan[oa]s?/, ~/[Tt]oscan[ao]s?/, ~/[Vv]al(?:ão|ões)/
        ]

    static cityAdjective = [
      ~/[Aa]guedenses?/, ~/[Aa]lcobacenses?/, ~/[Aa]lmadenses?/, ~/[Aa]lverquenses?/, ~/[Aa]madorenses?/, ~/[Aa]marantin[ao]s?/, ~/[Aa]lvicastrenses?/,
      ~/[Aa]ndaluz/, ~/[Aa]ngrenses?/, ~/[Aa]teniense/, ~/[Aa]veirenses?/, ~/[Bb]arcelonenses?/, ~/[Bb]arranquenh[oa]s?/,
	~/[Bb]arreirenses?/, ~/[Bb]ejense/, ~/[Bb]elgradin[oa]s?/, ~/[Bb]elo-[Hh]orizontin[oa]s?/, ~/[Bb]erlin[eê]s[ae]?s?/,
	  ~/[Bb]erlinenses?/, ~/[Bb]ilbaín[oa]s?/, ~/[Bb]ogotan[oa]s?/, ~/[Bb]olonh[eê]s[ea]?s?/, ~/[Bb]ombarralenses?/,
	  ~/[Bb]racarenses?/, ~/[Bb]ragantin[ao]s?/, ~/[Cc]abeceirenses?/, ~/[Cc]aldenses?/, ~/[Cc]aminhenses?/, ~/[Cc]ampo-alegrenses?/,
	  ~/[Cc]ampomaiorenses?/, ~/[Cc]antanhedenses?/, ~/[Cc]ascalenses?/, ~/[Pp]aivenses?/, ~/[Cc]astro-[Mm]arinenses?/,
	  ~/[Cc]eloricenses?/, ~/[Cc]onimbricenses?/, ~/[Cc]ovilhanenses?/, ~/[Dd]amascen[oa]s?/, ~/[Dd]ublinenses?/, ~/[Ee]lvenses?/,
	  ~/[Ee]spinhenses?/, ~/[Ee]starrejenses?/, ~/[Ee]stremocenses?/, ~/[ÉéEe]borenses?/, ~/[Ee]gitanienses?/, ~/[Ff]afenses?/, ~/[Ff]lavienses?/, ~/[Ff]amalicenses?/,
	  ~/[Ff]aialenses?/, ~/[Ff]arenses?/, ~/[Ff]elgueirenses?/, ~/[Ff]igueirenses?/, ~/[Ff][il]orenin[oa]s?/, ~/[Ff]unchalenses?/, ~/[Ff]eirenses?/,	  
	  ~/[Gg]aienses?/, ~/[Gg]enovenses?/, ~/[Gg]enov[eê]s[ea]s?/, ~/[Hh]olmienses?/, ~/[Ii]lhavenses?/, ~/[Ll]agoenses?/, ~/[Ll]acobri[gc]enses?/,
	  ~/[Ll]eirienses?/, ~/[Ll]imenh[oa]s?/, ~/[Ll]isboetas?/, ~/[Ll]isbonenses?/, ~/[Ll]ondrin[ao]s?/, ~/[Ll]ouletan[oa]s?/, ~/[Ll]imarenses?/,
	  ~/[Ll]ourenses?/, ~/[Ll]ourinhanenes?/, ~/[Ll]ousa[nd]enses?/, ~/[Ll]ourenço-[Mm]arquin[oa]s?/, ~/[Mm]adrilen[oa]s?/, ~/[Mm]afrenses?/,
	  ~/[Mm]alaguenh[oa]s?/, ~/[Mm]angualdenses?/, ~/[Mm]ertolenses?/, ~/[Mm]ilan[êe]s[ea]?s?/, ~/[Mm]irand[êe]s[ea]?s?/, ~/[Mm]icaelenses?/,
	  ~/[Mm]oncorvenses?/, ~/[Mm]onte-alegrenses?/, ~/[Mm]oscovitas?/, ~/[Nn]apolitan[ao]s?/, ~/[Nn]abantin[oa]s?/, ~/[Nn]ova-[Ii]orquin[oa]s?/, ~/[Oo]bidenses?/,
	  ~/[Oo]demirenses?/, ~/[Oo]divelenses?/, ~/[Oo]eirenses?/, ~/[Oo]lhanenses?/, ~/[Oo]liveirenses?/, ~/[Oo]ureenses?/, ~/[Oo]varenses?/,
	  ~/[Pp]acenses?/, ~/[Pp]acenh[oa]s?/, ~/[Pp]aranaenses?/, ~/[Pp]aredenses?/, ~/[Pp]arisienses?/, ~/[Pp]enafidelenses?/, ~/[Pp]ortuenses?/,
	  ~/[Pp]ombalenses?/, ~/[Pp]ortalegrenses?/, ~/[Pp]ortimonenses?/, ~/[Pp]oveir[ao]s?/, ~/[Qq]uitenh[oa]s?/, ~/[Rr]eguenguenses?/,
	  ~/[Rr]io-maiorenses?/, ~/[Rr]oman[oa]s?/, ~/[Rr]omen[oa]s?/, ~/[Ss]acaven[oa]s?/, ~/[Ss]alamanquenses?/, ~/[Ss]anjoanense/,
	  ~/[Ss]eixalenses?/, ~/[Ss]erpenses?/, ~/[Ss]etubalenses?/, ~/[Ss]evilhan[oa]s?/, ~/[Ss]ineenses?/, ~/[Ss]ilvienses?/, ~/[Ss]intrenses?/,
	  ~/[Tt]angerin[oa]s?/, ~/[Tt]avirenses?/, ~/[Tt]orr[ei]enses?/, ~/[Tt]orres(ão|ões)/, ~/[Tt]rofenses?/, ~/[Tt]irsenses?/, ~/[Tt]urin[êe]s[ea]?s?/,
	  ~/[Uu]berlandenses?/, ~/[Vv]aguenses?/, ~/[Vv]alpacenses?/, ~/[Vv]enezian[ao]s?/, ~/[Vv]ianenses?/, ~/[Vv]ila-condenses?/, ~/[Vv]iseenses?/,
	  ~/[Vv]imaranenses?/, ~/[Zz]agrebin[oa]s?/
    ]    
    
    /* LOCAL */
    
    /* LOCAL HUMANO RUA */
    
  static final List<Pattern> streetPrefix = [~/[Aa]v\./,~/[Aa]venida/,~/[Aa]vd\.?/,~/[Aa]lameda/,~/[Aa]lmd\.?/,~/[Bb]airro/,~/[Bb]eco/,
                                  ~/[Cc]alçada/,~/[Ee]strada/,~/[Ll]argo/,~/[Pp]raç(?:et)?a/,~/[Pp]r?ç\.?/,~/[Rr]u(?:el)?a/, ~/[Rr]\./,
                                  ~/[Rr]otunda/,~/[Tt]ra?v\.?/,~/[Tt]ravessa/,~/[Vv]ia/]
  	 
    /* LOCAL HUMANO  DIVISAO */
  static final List<Pattern> divisionPrefix = [~/[Aa]zinhaga/,~/[Aa]ldeia/,~/[Cc]oncelho/,~/[Cc]idade/,~/[Dd]istrito/,~/[Ff]reguesia/,
                                ~/[Ll]ocalidade/,~/[Pp]ovoação/,~/[Vv]ila/,~/[Zz]ona/]
    
    /* LOCAL HUMANO CONSTRUCAO */
  static final List<Pattern> constructionPrefix = [~/[Pp]onte/,~/[Jj]ardim/,~/[Ee]stádio/]

    /* LOCAL HUMANO REGIAO */
  static final List<Pattern> regionPrefix = [~/[Rr]egião/,~/[Pp]rovíncia/]
    
  /* LOCAL FISICO AGUACURSO */
  static final List<Pattern> waterCoursePrefix = [ ~/[Aa]fluente/, ~/[Rr]ibeir[ao]/, ~/[Cc]achoeira/, ~/[Cc]ataratas/,~/[Rr]io/, ~/[Rr]iacho/]

  /* LOCAL FISICO AGUAMASSA */
  static final List<Pattern> waterMassPrefix = [ ~/[Ee]stuário/, ~/[Oo]ceano/, ~/[Mm]ar/, ~/[Ll]agoa?/, ~/[Cc]anal/, ~/[Gg]olfo/, ~/[Bb]aía/]

  /* LOCAL FISICO RELEVO */ 
  static final List<Pattern> mountainPrefix = [~/[Cc]ordilheira/, ~/[Mm]ontanha/, ~/[Mm]ontes?/, ~/[Ss]erra/, ~/[Pp]lanície/, ~/[Pp]lanalto/, 
	  ~/[Pp]ico/, ~/[Vv]ale/]
    
  /* LOCAL FISICO PLANETA */ 
  static final List<Pattern> planetPrefix = [~/[Pp]laneta/, ~/[Cc]onstelação/]
    
  /* LOCAL FISICO ILHA */ 
  static final List<Pattern> islandPrefix = [~/[Ii]lhas?/, ~/[Aa]rquipélago/]
    
  /* LOCAL FISICO REGION */   
  static final List<Pattern> physicRegionPrefix = [~/[Ee]streito/, ~/[Dd]eserto/]

  /* LOCAL VIRTUAL OBRA */
     
  static final List<Pattern> media = [~/[Bb]oletim/, ~/[Cc]aderno/, ~/[Dd]iário/, ~/[Ff]olha/, ~/[Gg]azeta/,
        ~/[Jj]ornal/, ~/[Rr]evista/, ~/[Rr]ádios/,  /[Tt]elevisão/, ~/[Tt]elenovelas/]
    
  /** Patterns */ 
  static final List<Pattern> localizado = [~/localizad[oa]/, ~/sediad[ao]/]
    
  static final List<String> site = ["site","página"]
	
  static final List<Pattern> compasses = [~/[Nn]orte/,~/[Oo]este/, ~/[Ll]?[Ee]ste/, ~/[Ss]ul/,
	 ~/[Nn]ordeste/,~/[Nn]oroeste/, ~/[Ss]udo?este/]

   /** CLAUSES **/
   
  //localizado|sediado
  static final Clause localizado1nc = Clause.newConcept1Clause(localizado,"localizado",false) 
  static final Clause site1nc = Clause.newConcept1Clause(site,"site",false)
  
  static final Clause countryAdjective01c = Clause.newConcept01Clause(countryAdjective, "countryAdjectives")
  static final Clause streetPrefix1nc = Clause.newConcept1Clause(streetPrefix, "street prefix",false)
  static final Clause divisionPrefix1nc = Clause.newConcept1Clause(divisionPrefix, "division prefix",false)
  static final Clause constructionPrefix1nc = Clause.newConcept1Clause(constructionPrefix, "construction prefix",false)
  static final Clause regionPrefix1nc = Clause.newConcept1Clause(regionPrefix, "region prefix",false)
  static final Clause media1nc = Clause.newConcept1Clause(media, "media",false)
  static final Clause waterCoursePrefix1nc = Clause.newConcept1Clause(waterCoursePrefix, "water course",false)
  static final Clause waterMassPrefix1nc = Clause.newConcept1Clause(waterMassPrefix, "water mass",false)
  static final Clause mountainPrefix1nc = Clause.newConcept1Clause(mountainPrefix, "mountain",false)
  static final Clause planetPrefix1nc = Clause.newConcept1Clause(planetPrefix, "planet",false)
  static final Clause islandPrefix1nc = Clause.newConcept1Clause(islandPrefix, "islands",false)
  static final Clause physicRegionPrefix1nc = Clause.newConcept1Clause(physicRegionPrefix, "regions",false)
 
  static final Clause allLocalAdjective1nc =  Clause.newConcept1Clause( (continentAdjective + 
	  countryAdjective + alternativeCountryAdjective +regionAdjective + cityAdjective),"all local adjective", false) 
  
  static final Clause localAdjective1nc =  Clause.newConcept1Clause(countryAdjective,"country Adjective", false)
  static final Clause continentAdjective1nc =  Clause.newConcept1Clause(continentAdjective,"continent Adjective", false)

  static final Clause compasses1nc =  Clause.newConcept1Clause(compasses, "compasses",false)

}