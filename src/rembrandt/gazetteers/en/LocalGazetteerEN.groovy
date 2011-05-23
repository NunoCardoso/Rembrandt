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

import rembrandt.obj.Clause
import java.util.regex.Pattern
/**
 * @author Nuno Cardoso
 * This class stores gazetteers for LOCAL.
 * Gentilics taken from http://en.wikipedia.org/wiki/List_of_gentilics
 */
class LocalGazetteerEN {
    
    static final List continent = ['America',[~/(?:North|Central|Latin|South)/,'America'],
    'Antarctica','Asia','Europe','Oceania','Africa','Eurasia','Pangaea',
    'Euroasia','Arctic','Eurafrasia']
    
    static final List country = ['Abkhazia','Afghanistan','Albania','Algeria','Andorra','Angola',
    ['American','Samoa'], ['Antigua','and','Barbuda'],'Argentina','Armenia','Aruba',
    'Australia','Austria','Azerbaijan',
    // B
    'Bahamas','Bahrein','Bangladesh','Barbados','Belarus','Belgium','Belize','Benin','Bermuda',
    'Bhutan','Bolivia','Bosnia',['Bosnia','and','Herzegovina'],'Botswana','Brazil','Brunei',
    'Bulgaria',['Burkina','Faso'],'Burundi',
    // C
    'Camaroon','Cambodia','Canada',['Cape','Verde'],['Cayman','Islands'],['Central','African','Republic'],
    'Chad','Chile',['People\'s','Rpublic','of','China'],'China','Colombia','Comores','Congo',
    ['Democratic','Republic','of','Congo'], ['Costa','Rica'],['Côte','d\'Ivoire'],'Croatia',
    'Cuba','Cyprus',['Czech','Republic'],
    // D    
    'Denmark','Djibouti','Dominica',['Dominican','Republic'],
    // E
    ['East','Timor'],'Ecuador','Egypt',['El','Salvador'],'England',['Equatorial','Guinea'],'Eritrea',
    'Estonia','Ethiopia',
    //F
    ['Falkland','Islands'],['Faroe','Islands'],'Fiji','Finland','France',['French','Polynesia'],
    //G
    'Gabon','Gambia','Georgia','Germany','Ghana','Gibraltar','Great-Britain','Grenada','Greece',
    'Greenland','Guam','Guatemala','Guernsey','Guinea',
    'Guinea-Bissau','Guyana',
    //H
    'Haiti','Holland','Honduras',['Hong','Kong'],'Hungary',
    //I	
    'Iceland','India','Indonesia','Iran','Iraq','Ireland','Israel','Italy',['Ivory','Coast'],
    //J		
    'Jamaica','Japan','Jordan',
    //K	
    'Kazakhstan','Kenya','Kiribati','Kuwait','Kosovo','Kyrgyzstan',[~/(?:North|South)/,'Korea'],
    //L	
    'Laos','Latvia','Lesotho','Lebanon','Liberia','Lybia','Liechtenstein','Lithuania','Luxembourg',
    //M		
    'Macao','Macedonia','Madagascar','Malaysia','Malawi','Maldives','Mali','Malta',['Marshall','Islands'],
    'Mauritius','Mauritania','Mexico','Micronesia','Moldova','Monaco','Mongolia','Montenegro','Morocco',
    'Mozambique','Myanmar',
    //N	
    'Nagorno-Karabakh','Namibia','Nauru','Netherlands','Nepal',['New','Zealand'],'Nicaragua','Niger',
    'Nigeria','Norway',
    //O
    'Oman',
    //P
    'Pakistan','Palau','Palestine','Panama',['Papua','New','Guinea'],'Paraguay','Peru',
    'Philippines','Poland','Portugal',['Puerto','Rico'],
    //Q
    'Qatar',
    //R
    'Romania','Rwanda','Russia',
    //S	
    ['Saint','Barthélemy'],['Saint','Helena'],['Saint','Kitts','and','Nevis'],['Saint','Lucia'],
    ['Saint','Martin'],['Saint','Pierre','and','Miquelon'],['Saint Vincent','and','the','Grenadines'],'Samoa',
    ['San','Marino'],['São','Tomé','and','Príncipe'],['Saudi','Arabia'],'Scotland','Senegal','Serbia ','Seychelles',
    ['Sierra','Leone'],'Singapore','Slovakia','Slovenia Slovenia',['Solomon','Islands'],'Somalia',
    'Somaliland',['South','Africa'],['South','Ossetia'],'Spain',['Sri','Lanka'],'Sudan','Suriname',
    'Svalbard','Swaziland','Sweden','Switzerland','Syria',
    
    //T
    'Thailandi','Taiwan','Tajikistan','Tanzania','Togo','Tonga','Transnistria',
    ['Trinidad','and','Tobago'],'Tunisia','Turkmenistan','Turkey','Tuvalu',
    //U
    'Ukraine','Uganda',['United','Arab','Emirates'],'Uruguay','Uzbekistan', 
    ['United',~/(?:States|Kingdom)/],['United','States','of','America'],
    //V
    'Vanuatu',['Vatican','City'],'Vatican','Venezuela','Vietnam',['Virgin','Islands'],
    // W
    'Wales',
    //Y
    'Yemen',
    //Z
    'Zambia','Zimbabwe','Zaire'
    ]
    
    static final List region=["Kurdistan","Scandinavia"]
    
    static final List city = []
    
    static final List continentAdjective = [
    ~/(?:latin|north|central|south)?-?americans?/, ~/asians?/, ~/europeans?/, ~/africans?/] 		
    
    static final List countryAdjective = [
    ~/[Aa]fghans?/,~/[Aa]lbanians?/,~/[Aa]lgerians?/,[~/[Aa]merican/,~/[Ss]amoans?/],~/[Aa]ndorrans?/,~/[Aa]nguillans?/,
    ~/[Aa]ngolans?/,~/[Aa]ntiguans?/,~/[Aa]rgentine(an)?s?/,~/[Aa]rmenians?/,~/[Aa]rubans?/,~/[Aa]ustralians/,
    ~/[Aa]ustrians/,~/[Aa]zeris/,~/[Aa]zerbaijanis?/,~/[Bb]ahamians/,~/[Bb]ahrainis/,~/[Bb]angladeshis?/,
    ~/[Bb]arbadians?/,~/[Bb]elarusians?/,~/[Bb]elgians?/,~/[Bb]elizeans?/,~/[Bb]enin(?:ese|ois)s?/,~/[Bb]ermudi?ans?/,
    ~/[Bb]hutaneses?/,~/[Bb]olivians?/,~/[Bb]osnia[kn]s?/,~/[BbMm][ao]tswana(ns)?/,~/[Bb]razilians?/,~/[Bb]runeians?/,
    ~/[Bb]ulgarians?/,~/[Bb]urkinabe/,~/[Bb]amar/,~/[Bb]urmeses?/,~/[Bb]urundians?/,~/[Cc]ambodians/,~/[Cc]ameroonians/,
    ~/[Cc]anadians?/,[~/[Cc]ape/,~/[Vv]erdeans?/],~/[Cc]aymanians?/,[~/[Cc]entral/,~/[Aa]fricans?/],~/[Cc]hadians?/,
    ~/[Cc]hileans?/,~/[Cc]hineses?/,~/[Cc]olombians/,~/[Cc]omorians/,~/[Cc]ongolese/,[~/[Cc]osta/,~/[Rr]icans?/],
    ~/[Ii]vorians?/,~/[Cc]roat(ian)?s?/,~/[Cc]ubans?/,~/[Cc]ypriots?/,~/[Cc]zechs?/,~/[Dd]anes?/,~/[Dd]jiboutians?/,
    ~/[Dd]ominicans?/,~/[Dd]utch/,~/[Ee]cuadorians?/,~/[Ee]gyptians?/,~/[Ss]alvadorans?/,~/[Ee]nglish/,~/[Ee]quatoguineans?/,
    ~/[Ee]ritreans?/,~/[Ee]stonians?/,~/[Ee]thiopians?/,~/[Ff]aroese/,~/[Ff]ijians?/,~/[Ff]inns?/,~/[Ff]rench(?:man|woman)?/,
    ~/[Gg]aboneses/,~/[Gg]ambians?/,~/[Gg]eorgians?/,~/[Gg]ermans?/,~/[Gg]hanaians?/,~/[Gg]ibraltarians?/,
    ~/[Gg]reeks?/,~/[Gg]recians?/,~/[Gg]reenlanders?/,~/[Gg]renadians?/,~/[Gg]uamanians?/,~/[Gg]uatemalans?/,
    ~/[Gg]uineans?/,~/[Gg]uyanese/,~/[Hh]aitians?/,~/[Hh]erzegovinians?/,~/[Hh]ondurans?/,~/[Hh]ongkongers?/,[~/[Hh]ong/,~/[Kk]ongers/],
    ~/[Hh]ungarians?/,~/[Ii]celanders?/,~/[Ii]ndians?/,~/[Ii]ndonesians?/,~/[Ii]ranians?/,~/[Ii]raqis?/,~/[Ii]rish/,
    ~/[Ii]sraelis?/,~/[Ii]talians?/,~/[Jj]amaicans?/,~/[Jj]apanese/,~/[Jj]ordanians/,~/[Kk]azakh(?:stani)?s?/,
    ~/[Kk]enyans?/,~/[Ii]-[Kk]iribati/,~/[Kk]oreans?/,[~/[Ss]outh|[NN]orth/,~/[Kk]oreans?/],~/[Kk]osovars/,
    ~/[Kk]uwaitis?/,~/[Kk][iy]rgh?[yi]z(?:stanis)?/,~/[Ll]ao(?:s|tians?)/,~/[Ll]atvians?/,~/[Ll]etts?/,~/[Ll]ebanese/,
    ~/[Bb]asotho/,~/[Ll]iberians?/,~/[Ll]ibyans?/,~/[Ll]iechtensteiners?/,~/[Ll]ithuanians?/,~/[Ll]uxembourgers?/,
    ~/[Mm]acanese/,~/[Mm]acedonians?/,~/[MM]alagasy/,~/[Mm]alawians?/,~/[Mm]alaysians?/,~/[Mm]aldivians?/,~/[Mm]alians?/,
    ~/[Mm]altese/,~/[Mm]arshallese/,~/[Mm]artiniquais?/,~/[Mm]auritanians?/,~/[Mm]auritians?/,~/[Mm]ahora(?:n|is)/,
    ~/[Mm]anx/,~/[Mm]exicans?/,~/[Mm]icronesians?/,~/[Mm]oldovans?/,~/[Mm]onacans/,~/[Mm]onégasques?/,~/[Mm]ongol(?:ian)?s?/,
    ~/[Mm]ontenegrins?/,~/[Mm]oroccans?/,~/[Mm]ozambicans?/,~/[Nn]amibians?/,~/[Nn]auruans?/,~/[Nn]epalese/,[~/[Nn]ew/,~/[Zz]ealanders?/],
    ~/[Nn]icaraguans/,~/[Nn]igeri[ae]ns?/,~/[Nn]orwegians?/,~/[Oo]manis?/,~/[Pp]akistanis?/,~/[Pp]alestinians?/,
    ~/[Pp]alauans?/,~/[Pp]anamanians?/,[~/[Pp]apua/,~/[Nn]ew/,~/[Gg]uineans?/],~/[Pp]apuans?/,~/[Pp]araguayans?/,~/[Pp]eruvians?/,
    ~/[Ff]ilipinos?/,~/[Pp]oles/,~/[Pp]ortugueses?/,[~/[Pp]uerto/,~/[Rr]icans?/],~/[Qq]ataris?/,~/[Rr]omanians?/,~/[Rr]ussians?/,
    ~/[Rr]wandans?/,[~/[Ss](?:ain)?t\.?/,~/[Hh]elenians?/],[~/[Ss](?:ain)?t\.?/,~/[Ll]ucians?/],~/[Kk]ittitians?/,~/[Nn]evisians?/,
    [~/[Ss](?:ain)?t\.?-?([Pp]ierrais)?/,~/[Pp]ierrais?/],~/[Mm]iquelonnais?/,[~/[Ss](?:ain)?t\.?/,~/[Vv]incentians?/],~/[Vv]incentians?/,
    ~/[Ss]amoans?/,~/[Ss]ammarinese/,[~/[Ss]ão/,~/[Tt]om[ée]ans?/],~/Saudis?/,[~/[Ss]audi/,~/[Aa]rabians?/],~/[Ss]cot(?:tish)?s?/,
    ~/[Ss]enegalese/,~/[Ss]erb(?:ian)?s?/,~/[Ss]eychellois/,[~/[Ss]ierra/,~/[Ll]eoneans?/],~/[Ss]ingaporeans?/,~/[Ss]lovaks?/,
    ~/[Ss]loven(e|ian)s?/,~/[Ss]omalis?/,~/[Ss]omalilanders?/,[~/[Ss]outh/,~/[Aa]fricans?/],~/[Ss]pani(sh|ard)s/,[~/[Ss]ri/,~/[Ll]ankans?/],
    ~/[Ss]udanese/,~/[Ss]urinamers?/,~/[Ss]wazis?/,~/[Ss]wed(?:e|ish)s?/,~/[Ss]wiss/,~/[Tt]aiwanese/,~/[Tt]ajik(?:istani)?s?/,~/[Tt]anzanians?/,
    ~/[Tt]hai/,~/[Tt]ogolese/,~/[Tt]ongans?/,~/[Tt]rinidadians?/,~/[Tt]obagonians?/,~/[Tt]unisians?/,~/[Tt]urk(?:ish)?s?/,~/[Tt]urkmens?/,
    ~/[Tt]imorese/,~/[Tt]uvaluans?/,~/[Uu]gandans?/,~/[Uu]kranians?/,~/[Ee]mir(ian|ati)s?/,~/[Aa]mericans?/,~/[Uu]ruguayans?/,
    ~/[Uu]zbek(?:istani)?s?/,~/[Nn]i-[Vv]anuatu/,~/[Vv]enezuelans?/,~/[Vv]ietnamese/,~/[Ww]elsh/,~/[Yy]emenis?/,~/[Zz]ambians?/,~/[Zz]imbabweans?/]
    
    static final List alternativeCountryAdjective=[
    ~/[Aa]ussies?/,~/[Bb]ajans/,~/[Cc]anucks?/,~/[Hh]ellene?s?/,~/[Mm]agyars?/,~/[Yy]ankees?/]
    
    static final List regionAdjective = [~/[Bb]ritish/,~/[Bb]ritons?/, 
    ~/[Cc]atalans?/,~/[Cc]alifornians?/,~/[Cc]antonese/,~/[Cc]aucasian/,~/[Tt]?[Cc]het?chen/,~/[Cc]retense/,~/[Kk]urds?/,
    ~/[Ss]candinavians?/,~/[Ff]lemish/,~/[Hh]awaians?/,~/[Ii]berians?/,~/[Ss]iberians?/,
    ~/[Ss]icilians?/,~/[Tt]exans?/,~/[Tt]ibeti?ans?/,~/[Tt]oscans?/,~/[VvWw]aloons?/
    ]
    
    static final List cityAdjective = [  ]    
    
    /* LOCAL */
    
    /* LOCAL HUMANO RUA */
    
    static final List streetPrefix = [~/[Aa]v\./,~/[Aa]venue/,~/[Bb]oulevard/,~/[Bb]vd\./,~/[Aa]lley/,
    ~/[Rr]oute/,~/[Rr]oad/,~/[Ss]quare/,~/[Ss]q\./,~/[Ss]treet/,~/[St]r?\./,~/[Ss]peedway/]
    
    /* LOCAL HUMANO  DIVISAO */
    static final List divisionPrefix = [~/[Tt]own/,~/[Cc]ounty/,~/[Cc]ity/,~/[Dd]istrict/,~/[Mm]unicipality/,
    ~/[Ll]ocalidade/,~/[Ss]tate/, ~/[Vv]illage/]
    
    /* LOCAL HUMANO CONSTRUCAO */
    static final List constructionPrefix = [~/[Bb]ridge/,~/[Gg]arden/,~/[Ss]tadium/]
    
    /* LOCAL HUMANO REGIAO */
    static final List regionPrefix = [~/[Rr]egion/,~/[Pp]rovince/]
    
    /* LOCAL FISICO AGUACURSO */
    static final List waterCoursePrefix = [~/[Ee]ffluent/,~/[Ss]tream/,~/[Ww]ater[Ff]alls?/, ~/[Rr]iver/]
    
    /* LOCAL FISICO AGUAMASSA */
    static final List waterMassPrefix = [~/[Oo]cean/,~/[Ss]ea/,~/[Ll]ake/,~/[Pp]ond/,~/[Cc]hannel/,~/[Gg]ulf/,~/[Bb]ay/]
    
    /* LOCAL FISICO RELEVO */ 
    static final List mountainPrefix = [~/[Mm]ountains?/,~/[Mm]ounts?/,~/[Pp]rairie/,~/[Cc]reek/,~/[Pp]eak/,~/[Vv]alley/]
    
    /* LOCAL FISICO PLANETA */ 
    static final List planetPrefix = [~/[Pp]lanet/,~/[Cc]onstellation/,~/[Ss]atellite/]
    
    /* LOCAL FISICO ILHA */ 
    static final List islandPrefix = [~/[Ii]slands?/,~/[Aa]rchipelago/]
    
    /* LOCAL FISICO REGION */   
    static final List physicRegionPrefix = [~/[Dd]esert/]
    
    /* LOCAL VIRTUAL OBRA */    
    static final List media = [ ~/[Nn]ewspaper/,~/[Mm]agazine/,~/[Rr]adio/,"TV"]
    
    /** Patterns */ 
    static final List<String> based = ["based"] // based on X
    
    static final List<String> site = ["site","website","webpage"]
    
	static final List<Pattern> compasses = [~/[Nn]orth(?:ern)?/,~/[Ww]est(?:ern)?/, ~/[Ee]ast(?:ern)?/, ~/[Ss]outh(?:ern)?/,
	 ~/[Nn]orth-?[Ww]est(?:ern)?/,~/[Nn]orth-?[Ee]ast(?:ern)?/, ~/[Ss]outh-?[Ww]est(?:ern)?/,~/[Ss]outh-?[Ee]ast(?:ern)?/]
	
    /** CLAUSES **/
    
    //localizado|sediado
    static final Clause based1nc = Clause.newConcept1Clause(based,"based",false) 
    static final Clause site1nc = Clause.newConcept1Clause(site,"site",false)	
    
    static final Clause street1nc = Clause.newConcept1Clause(streetPrefix, "street prefix",false)
    static final Clause division1nc = Clause.newConcept1Clause(divisionPrefix, "division prefix",false)
    static final Clause construction1nc = Clause.newConcept1Clause(constructionPrefix, "construction prefix",false)
    static final Clause humanregion1nc = Clause.newConcept1Clause(regionPrefix, "region prefix",false)
    static final Clause media1nc = Clause.newConcept1Clause(media, "media",false)
    static final Clause waterCourse1nc = Clause.newConcept1Clause(waterCoursePrefix, "water course",false)
    static final Clause waterMass1nc = Clause.newConcept1Clause(waterMassPrefix, "water mass",false)
    static final Clause mountain1nc = Clause.newConcept1Clause(mountainPrefix, "mountain",false)
    static final Clause planet1nc = Clause.newConcept1Clause(planetPrefix, "planet",false)
    static final Clause island1nc = Clause.newConcept1Clause(islandPrefix, "islands",false) 
    static final Clause physicRegion1nc = Clause.newConcept1Clause(physicRegionPrefix, "regions",false)
    
    static final Clause allLocalAdjective1nc =  Clause.newConcept1Clause( (continentAdjective + 
    countryAdjective + alternativeCountryAdjective +regionAdjective + cityAdjective),"all local adjective", false)   
    static final Clause countryAdjective01c = Clause.newConcept01Clause(countryAdjective, "country adjective", true)  
    static final Clause countryAdjective1nc = Clause.newConcept1Clause(countryAdjective, "country adjective", false)   
    static final Clause continentAdjective1nc =  Clause.newConcept1Clause(continentAdjective, "continent adjective",false)

	static final Clause compasses1nc =  Clause.newConcept1Clause(compasses, "compasses",false)
}