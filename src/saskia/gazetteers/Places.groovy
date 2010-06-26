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

import rembrandt.obj.Term
import rembrandt.rules.MatcherObject
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * Place gazetteer
 */
class Places {
   
    Places _this
    static Map meanings = [:] // caches concepts for rule match. Caches with lang -> type[adj, name]
    MatcherObject mo = new MatcherObject()
    
    static List places = [

//Continents
	[name:[en:'America',pt:'América'], adj:[pt:~/[Aa]merican[ao]s?/], dbpediaresource:"Americas"], // Americans adjective is too messy
	[name:[en:['North','America'], pt:['América','do','Norte']], 
	       adj:[en:[[~/[Nn]orth-?[Aa]mericans?/],[~/[Nn]orth/,~/[Aa]mericans?/]], pt:[[~/[Nn]orte-?[Aa]merican[oa]s?/],[~/[Nn]orte/,~/[Aa]merican[oa]s?/]] ], 
	           dbpediaresource:"North_America"],
	[name:[en:['South','America'], pt:['América','do','Sul']], 
	 	adj:[en:[[~/[Ss]outh-?[Aa]mericans?/], [~/[Ss]outh/,~/[Aa]mericans?/]], pt:[[~/[Ss]ul-?[Aa]merican[oa]s?/], [~/[Ss]ul/,~/[Aa]merican[oa]s?/]]], 
	 	dbpediaresource:"South_America"],
	[name:[en:['Central','America'], pt:['América','[Cc]entral']], 
	 	adj:[en:~/[Cc]entral-?[Aa]mericans?/, pt:~/[Cc]entral-?[Aa]merican[oa]s?/], dbpediaresource:"Central_America"],
	[name:[en:['Latin','America'], pt:['América','[Ll]atina']], 
	 	adj:[en:[[~/[Ll]atin-?[Aa]mericans?/],[~/[Ll]atin/,~/[Aa]mericans?/]], pt:[[/[Ll]atino-?[Aa]merican[oa]s?/],[/[Ll]atino/,~/[Aa]merican[oa]s?/]] ], 
	 	dbpediaresource:"Latin_America"],
	[name:[en:['Middle','East'], pt:[['Médio','Oriente'],['Oriente','Médio']] ],
	 	adj:[en:[[~/[Mm]iddle-[Ee]asterns?/],[~/[Mm]iddle/,~/[Ee]asterns?/]] ], dbpediaresource:"Middle_East"],
	[name:[en:~/Antarc?tica/, pt:'Antárctica'], 
	 	adj:[en:~/[Aa]ntarc?ticans?/, pt:~/[Aa]ntárc?tic[oa]s?/], dbpediaresource:"Antarctica"],
	[name:[en:'Asia', pt:"Ásia"], adj:[en:~/[Aa]sians?/, pt:~/[Aa]siátic[oa]s?/], dbpediaresource:"Asia"],
	[name:[en:'Europe', pt:'Europa'], adj:[en:~/[Ee]uropeans?/,pt:~/[Ee]urope(u|ia)s?/], dbpediaresource:"Europe"],
	[name:[en:'Africa', pt:'África'], adj:[en:~/[Aa]fricans?/, pt:~/[Aa]frican[oa]s?/], dbpediaresource:"Africa"],
	[name:[en:'Oceania', pt:"Oceânia"], adj:[en:~/[Oo]ceanians?/, pt:~/[Oo]ceânic[oa]s?/], dbpediaresource:"Oceania"],
	[name:[en:'Euroasia', pr:~/Euro?ásia/], adj:[en:~/[Ee]urasians?/, pt:~/[Ee]urasian[oa]s?/], dbpediaresource:"Euroasia"],
	[name:[en:'Arctic', pt:~/Árc?tico/], adj:[en:~/[Aa]rc?tics?/, pt:~/[Áá]rc?tic[oa]s?/], dbpediaresource:"Arctic"],
	[name:[en:'Eurafrasia', pt:'Eurafrásia'], adj:null, dbpediaresource:"Eurafrasia"],
	[name:[en:'Pangea', pt:~/Pang[ée]ia/], adj:null, dbpediaresource:"Pangea"],

// Countries: A
	[name:[en:'Abkhazia', pt:'Abecásia'], 
	  	adj:[en:~/[Aa]bkhaz(?:ians?)?/, pt:~/[Aa]becassian[oa]s?/], dbpediaresource:"Abkhazia"],
	[name:[en:'Afghanistan', pt:'Afeganistão'], 
	 	adj:[en:~/[Aa]fghans?/, pt:~/[Aa]fegãos?/], dbpediaresource:"Afghanistan"],
	[name:[en:'Albania', pt:'Albânia'], 
	 	adj:[en:~/[Aa]lbanians?/, pt:~/[Aa]lban[êe]s[ae]?s?/], dbpediaresource:"Albania"],
	[name:[en:'Algeria', pt:'Argélia'],
	        adj:[en:~/[Aa]lgerians?/, pt:~/[Aa]rgelin[ao]s?/], dbpediaresource:"Algeria"],
	[name:[en:['American','Samoa'], pt:['Samoa','Americana'] ],
	        adj:[en:~/[Ss]amoans?/, pt:~/[Ss]amoans?/], dbpediaresource:"American_Samoa"],
	[name:[en:'Andorra', pt:'Andorra'], 
	 	adj:[en:~/[Aa]ndorrans?/, pt:~/[Aa]ndorrenh[oa]s?/], dbpediaresource:"Andorra"],
	[name:[en:'Angola', pt:'Angola'], 
	       adj:[en:~/[Aa]ngolans?/, pt:~/[Aa]ngolan[ao]s?/], dbpediaresource:"Angola"],
	[name:[en:['Antigua','and','Barbuda'], pt:['Antígua','e','Barbuda']],
	 	adj:[en:~/(?:[Aa]ntiguans?|[Bb]arbudans?)/], dbpediaresource:"Antigua_and_Barbuda"],
	[name:[en:'Argentina', pt:'Argentina'], 
	 	adj:[en:~/[Aa]rgentin(?:e|ea|a)ns?/, pt:~/[Aa]rgentin[oa]s?/], dbpediaresource:"Argentina"],
	[name:[en:'Armenia', pt:~/Arm[êé]nia/], 
	 	adj:[en:~/[Aa]rmenians?/, pt:~/[Aa]rm[êé]ni[oa]s?/], dbpediaresource:"Armenia"],
	[name:[en:'Aruba', pt:'Aruba'], 
	 	adj:[en:~/[Aa]rubans?/], dbpediaresource:"Aruba"],
	[name:[en:'Australia', pt:'Austrália'],
	 	adj:[en:~/[Aa]ustralians?/, pt:~/[Aa]ustralian[ao]s?/], dbpediaresource:"Australia"],
	[name:[en:'Austria', pt:'Áustria'],
	 	adj:[en:~/[Aa]ustrians?/, pt:~/[Aa]ustríac[oa]s?/], dbpediaresource:"Austria"],
	[name:[en:'Azerbaijan', pt:'Azerbaijão'],
	 	adj:[en:~/[Aa]zer(?:baidjan)?is?/, pt:~/[Aa]zerbaid?jan[oa]s?/], dbpediaresource:"Azerbaijan"],
	
//B
	[name:[en:'Bahamas', pt:~/Bah?amas/], 
	 	adj:[en:~/[Bb]ahamians?/, pt:~/[Bb]ah?amenses?/], dbpediaresource:"Bahamas"],
	[name:[en:'Bahrain', pt:[['Barém'],['Bahrain']] ], 
	 	adj:[en:~/[Bb]ahrainis?/], dbpediaresource:"Bahrain"],
	[name:[en:'Bangladesh', pt:~/Banglade[cs]he?/], 
	 	adj:[en:~/[Bb]angladeshis?/, pt:~/[Bb]anglade[cs]hian[oa]s?/], dbpediaresource:"Bangladesh"],
	[name:[en:'Barbados', pt:'Barbados'], 
	 	adj:[en:~/[Bb]arbadians?/], dbpediaresource:"Barbados"],
	[name:[en:'Belarus', pt:~/Bielor?rússia/],
	 	adj:[en:~/[Bb]elarusians?/, pt:~/[Bb]ieloruss[ao]s?/], dbpediaresource:"Belarus"],
	[name:[en:'Belgium', pt:'Bélgica'],
	 	adj:[en:~/[Bb]elgians?/, pt:~/[Bb]elgas?/], dbpediaresource:"Belgium"],
	[name:[en:'Belize', pt:"Belize"],
	 	adj:[en:~/[Bb]elizeans?/, pt:~/[Bb]elizen(s|nh)[oae]s?/], dbpediaresource:"Belize"],
	[name:[en:'Benin', pt:~/Beni[mn]/], 
	 	adj:[en:~/[Bb]enin(?:ese|ois)/, pt:~/[Bb]enin[êe]n?s[ae]?s?/], dbpediaresource:"Benin"],
	[name:[en:'Bermuda', pt:~/Bermudas?/],
	 	adj:[en:~/[Bb]ermudi?ans?/], dbpediaresource:"Bermuda"],
	[name:[en:'Buthan', pt:'Butão'], 
	 	adj:[en:~/[Bb]uthaneses?/], dbpediaresource:"Buthan"],
	[name:[en:'Bolivia', pt:'Bolívia'],
	 	adj:[en:~/[Bb]olivians?/, pt:~/[Bb]olivian[ao]s?/], dbpediaresource:"Bolivia"],
	[name:[en:[['Bosnia'],['Bosnia','and','Herzegovina']], pt: [['Bósnia'],['Bósnia','e','Herzegovina']] ],
	       adj:[en:~/[Bb]osnia[nk]s?/, pt:~/[Bb]ósni[ao]s?/], dbpediaresource:"Bosnia_and_Herzegovina"],
	[name:[en:'Botswana', pt:~/Bots[uw]ana/],
	 	adj:[en:~/[Bb][oa]tswanan?s?/], dbpediaresource:"Botswana"],
	[name:[en:'Brazil', pt:'Brasil'],
	 	adj:[en:~/[Bb]razilians?/, pt:~/[Bb]ra[sz]ileir[oa]s?/], dbpediaresource:"Brazil"],
	[name:[en:['British','Virgin','Islands'], pt:['Ilhas','Virgem'] ],
	       adj:null, dbpediaresource:"British_Virgin_Islands"],
	[name:[en:'Brunei', pt:'Brunei'],
	 	adj:[en:~/[Bb]runeians?/, pt:~/[Bb]runean[ao]s?/], dbpediaresource:"Brunei"],
	[name:[en:'Bulgaria', pt:'Bulgária'],
	 	adj:[en:~/[Bb]ulgarians?/, pt:~/[Bb]úlgar[oa]s?/], dbpediaresource:"Bulgaria"],
	[name:[en:['Burkina','Faso'], pt:[~/Bur(?:qu|k)ina/,'Faso']] , 
	 	adj:[en:~/[Bb]urkinabe/], dbpediaresource:"Burkina_Faso"],
	[name:[en:[['Burma'],['Myanmar']], pt:[['Birmânia'], [~/Mianmar?/]] ], 
	       	adj:[en:~/[Bb](?:amar|urmese)/, pt:~/[Bb]irman[êe]s[ae]?s?/], dbpediaresource:"Myanmar"],
	[name:[en:'Burundi', pt:'Burundi'],
	 	adj:[en:~/[Bb]urundians?/], dbpediaresource:"Burundi"],

//C
	[name:[en:'Cambodia', pt:~/Cambod?ja/], 
	 	adj:[en:~/[Cc]ambodians?/, pt:~/[Cc]ambod?jan[oa]s?/], dbpediaresource:"Cambodia"],
	[name:[en:'Cameroon', pt:"Camarões"], 
	 	adj:[en:~/[Cc]ameroonians?/, pt:~/[Cc]amaronenses?/], dbpediaresource:"Cameroon"],
	[name:[en:'Canada', pt:"Canadá"], 
	 	adj:[en:~/[Cc]anadians?/, pt:~/[Cc]anadian[oa]s?/], dbpediaresource:"Canada"],
	[name:[en:['Cape','Verde'], pt:['Cabo','Verde']], 
	 	adj:[en:~/[Cc]ape-[Vv]erdians?/, pt:~/[Cc]abo-[Vv]erdian[oa]s?/], dbpediaresource:"Cape_Verde"],
	[name:[en:['Cayman','Islands'], pt:['Ilhas','Caimão']], 
	 	adj:[en:~/[Cc]aymanians?/], dbpediaresource:"Cayman_Islands"],
	[name:[en:['Central','African','Republic'], pt:['República','Centro-Africana']], 
	 	adj:[en:~/[Cc]entral-[Aa]fricans?/], dbpediaresource:"Central_African_Republic"],
	[name:[en:'Chad', pt:"Chade"], 
	 	adj:[en:~/[Cc]hadians?/, pt:~/[Cc]hadian[oa]s?/], dbpediaresource:"Chad"],
	[name:[en:'Chile', pt:"Chile"], 
	 	adj:[en:~/[Cc]hileans?/, pt: ~/[Cc]hilen[oa]s?/], dbpediaresource:"Chile"],
	[name:[en:[['China'],['People\'s','Republic','of','China']], pt:[['China'],['República','Popular','da','China']] ], 
	 	adj:[en:~/[Cc]hineses?/, pt:~/[Cc]hin[êe]s[ea]?s?/], dbpediaresource:["China","People's_Republic_of_China"]],
	[name:[en:['Christmas','Island'], pt:["Ilha",'de','Natal']], 
	 	adj:null, dbpediaresource:"Christmas_Island"],
	[name:[en:['Cocos','Islands'], pt:["Ilhas","Cocos"]], 
	 	adj:null, dbpediaresource:"Cocos_Islands"],
	[name:[en:'Colombia', pt:"Colômbia"], 
	 	adj:[en:~/[Cc]olombians?/, pt:~/[Cc]olombian[oa]s?/], dbpediaresource:"Colombia"],
	[name:[en:'Comoros', pt:"Comores"], 
	 	adj:[en:~/[Cc]omorians?/], dbpediaresource:"Comoros"],
	[name:[en:[['Congo'],['Democratic','Republic','of','Congo'],['Congo-Kinshasa']], 
	       pt:[['Congo'],['República','Democrática','do','Congo'],[~/Congo-[KQ]uin[sc]hasa/],['Zaire']] ], 
	       adj:[en:~/[Cc]ongolese/, pt:~/[Cc]ongol[eê]s[ea]?s?/], dbpediaresource:"Democratic_Republic_of_the_Congo"],
	[name:[en:[['Congo'],['Republic','of','the','Congo'],['Congo-Brazzaville']], 
	       pt:[['Congo'],['República','do','Congo'],[~/Congo-Brazz?avill?e/]] ], 
	       adj:[en:~/[Cc]ongolese/, pt:~/[Cc]ongol[eê]s[ea]?s?/], dbpediaresource:"Republic_of_the_Congo"],		       	
	[name:[en:['Cook','Islands'], pt:["Ilhas","Cook"]], 
	 	adj:[en:[~/[Cc]ook/,~/[Ii]slanders?/]], dbpediaresource:"Cook_Islands"],
	[name:[en:['Costa','Rica'], pt:['Costa','Rica']], 
	 	adj:[en:~/[Cc]osta-[Rr]icans?/, pt:~/[Cc]osta-[Rr]iquenh[ao]s?/], dbpediaresource:"Costa_Rica"],
	[name:[en:'Croatia', pt:"Croácia"], 
	 	adj:[en:~/[Cc]roat(?:ian)?s?/, pt:~/[Cc]roatas?/], dbpediaresource:"Croatia"],
	[name:[en:'Cuba', pt:"Cuba"], 
	 	adj:[en:~/[Cc]ubans?/, pt:~/[Cc]uban[oa]s?/], dbpediaresource:"Cuba"],
	[name:[en:[['Côte','d\'Ivoire'],['Ivory','Coast']], pt:[['Côte','d\'Ivoire'],['Costa','do','Marfim']] ],
	 	adj:[pt:~/[Cc]osta-[Mm]arfinenses?/], dbpediaresource:"Côte_d'Ivoire"],
	[name:[en:'Cyprus', pt:"Chipre"], 
	 	adj:[en:~/[Cc]ypriots?/, pt:~/[Cc]ipriotas?/], dbpediaresource:"Cyprus"],
	[name:[en:[['Czech','Republic'],['Czech']], pt:[['República',~/T?[Cc]heca/],["Checoslováquia"]] ], 
	        adj:[en:~/[Cc]zechs?/, pt:[[~/[Cc]hec[oa]s?/], [~/[Cc]hecoslovac[oa]s?/]] ], dbpediaresource:"Czech_Republic"],
			
//D    
        [name:[en:'Denmark', pt:"Dinamarca"], 
         	adj:[en:~/[Dd]an(?:ish|es?)/, pt:~/[Dd]inamarqu[êe]s[ea]?s?/], dbpediaresource:"Denmark"],
 	[name:[en:'Djibouti', pt:~/D?[Jj]ibo?uti/], 
 	 	adj:[en:~/[Dd]jiboutians?/], dbpediaresource:"Djibouti"],
 	[name:[en:'Dominica', pt:"Dominica"], 
 	 	adj:[en:~/[Dd]ominicans?/, pt:~/[Dd]ominican[oa]s?/], dbpediaresource:"Dominica"],
	[name:[en:['Dominican','Republic'], pt:['República','Dominicana']], 
	 	adj:[en:~/[Dd]ominicans?/, pt:~/[Dd]ominican[oa]s?/], dbpediaresource:"Dominican_Republic"],

//E		
	[name:[en:['East','Timor'], pt:['Timor','Leste']], 
	 	adj:[en:~/[Tt]imorese/, pt:~/[Tt]imorenses?/], dbpediaresource:"East_Timor"],
	[name:[en:'Ecuador', pt:"Equador"], 
	 	adj:[en:~/[Ee]cuadorians?/, pt:~/[Ee]quatorian[oa]s?/], dbpediaresource:"Ecuador"],
	[name:[en:'Egypt', pt:~/Egip?to/], 
	 	adj:[en:~/[Ee]gyptians?/, pt:~/[Ee]gíp?ci[oa]s?/], dbpediaresource:"Egypt"],
	[name:[en:['El','Salvador'], pt:['El','Salvador']], 
	 	adj:[en:~/[Ss]alvadorans?/, pt: ~/[Ss]alvadorenh[oa]s?/], dbpediaresource:"El_Salvador"],
	[name:[en:'England', pt:"Inglaterra"], 
	 	adj:[en:~/[Ee]nglish/, pt:~/[Ii]ngl[êe]s[ae]?s?/], dbpediaresource:"England"],
	[name:[en:['Equatorial','Guinea'], pt:["Guiné","Equatorial"]], 
	 	adj:[en:~/[Ee]quatoguineans?/], dbpediaresource:"Equatorial_Guinea"],
	[name:[en:'Eritrea', pt:"Eritreia"], 
	 	adj:[en:~/[Ee]ritreans?/, pt:~/[Ee]ritreus?/], dbpediaresource:"Eritrea"],	
	[name:[en:'Estonia', pt:~/Est[óô]nia/], 
	 	adj:[en:~/[Ee]stonians?/, pt:~/[Ee]stonian[oa]s?/], dbpediaresource:"Estonia"],
	[name:[en:'Ethiopia', pt:"Etópia"], 
	 	adj:[en:~/[Ee]thiopians?/, pt:~/[Ee]tíopes?/], dbpediaresource:"Ethiopia"], 	
	
//F
	
	[name:[en:['Falkland','Islands'], pt:[['Ilhas','Falkland'], ['Ilhas','Maldivas']] ], 
	 	adj:[en:[~/[Ff]alkland/,~/[Ii]slanders?/]], dbpediaresource:"Falkland_Islands"],
	[name:[en:['Faroe','Islands'], pt:['Ilhas',~/Faro[Êeé]/]], 
	 	adj:[en:~/[Ff]aroese/], dbpediaresource:"Faroe_Islands"],
	[name:[en:'Fiji', pt:"Fiji"], 
	 	adj:[en:~/[Ff]ijians?/, pt:~/[Ff]id?jian[oa]s?/], dbpediaresource:"Fiji"],
	[name:[en:'Finland', pt:"Finlândia"], 
	 	adj:[en:~/[Ff]inn(?:ish)?s?/, pt:~/[Ff]inland[eê]s[ea]?s?/], dbpediaresource:"Finland"],
	[name:[en:'France', pt:"França"], 
	 	adj:[en:~/[Ff]rench/, pt:~/[Ff]ranc[êe]s[ae]?s?/], dbpediaresource:"France"],
	[name:[en:['French','Guiana'], pt:['Guiana','Francesa']], 
	 	adj:[en:~/[Ff]rench-[Gg]uianese/], dbpediaresource:"French_Guiana"],
	[name:[en:['French','Polynesia'], pt:['Polinésia','Francesa']], 
	 	adj:[en:~/[Ff]rench-[Pp]olynesians?/], dbpediaresource:"French_Polynesia"],

//G
	[name:[en:'Gabon', pt:"Gabão"], 
	 	adj:[en:~/[Gg]abonese/, pt:~/[Gg]abon[eê]s[ea]?s?/], dbpediaresource:"Gabon"],
	[name:[en:'Gambia', pt:"Gâmbia"], 
	 	adj:[en:~/[Gg]ambians?/, pt:~/[Gg]ambian[oa]s?/], dbpediaresource:"Gambia"],
	[name:[en:'Georgia', pt:"Geórgia"], 
	 	adj:[en:~/[Gg]eorgians?/, pt:~/[Gg]eorgian[ao]s?/], dbpediaresource:"Georgia_(country)"],
	[name:[en:'Germany', pt:"Alemanha"], 
	 	adj:[en:~/[Gg]ermans?/, pt: ~/[Aa]lemã[eo]?s?/], dbpediaresource:"Germany"],	
	[name:[en:'Ghana', pt:"Gana"], 
	 	adj:[en:~/[Gg]hanians?/, pt:~/[Gg]an[êe]s[ea]s?/], dbpediaresource:"Ghana"],
	[name:[en:'Gibraltar', pt:"Gibraltar"], 
	 	adj:[en:~/[Gg]ibraltar(?:ian)?s?/], dbpediaresource:"Gibraltar"],
	[name:[en:['Great','Britain'], pt:"Grã-Bretanha"], 
	 	adj:[en:~/[Bb]rit(?:ish|ons)/, pt:~/[Bb]ritânic[ao]s?/], dbpediaresource:"Great_Britain"],
	[name:[en:'Greece', pt:"Grécia"], 
	 	adj:[en:~/[Gg]reeks?/, pt:~/[Gg]reg[ao]s?/], dbpediaresource:"Greece"],
	[name:[en:'Greenland', pt:"Gronelândia"], 
	 	adj:[en:~/[Gg]reenland(?:ic|er)s?/], dbpediaresource:"Greenland"],
	[name:[en:'Grenada', pt:"Granada"], 
	 	adj:[en:~/[Gg]renadians?/, pt:~/[Gg]ranadin[oa]s?/], dbpediaresource:"Grenada"],
	[name:[en:'Guadeloupe', pt:"Guadalupe"], 
	 	adj:[en:~/[Gg]uadeloup(?:e|ian)s?/], dbpediaresource:"Guadeloupe"],
	[name:[en:'Guam', pt:"Guam"], 
	 	adj:[en:~/[Gg]uamanians?/], dbpediaresource:"Guam"],
	[name:[en:'Guatemala', pt:"Guatemala"], 
	 	adj:[en:~/[Gg]uatemalans?/, pt:~/[Gg]uatemaltecas?/], dbpediaresource:"Guatemala"],
	[name:[en:'Guinea', pt:[["Guiné"],["Guiné-Conacri"]] ], 
	       	adj:[en:~/[Gg]uineans?/, pt:~/[Gg]uineenses?/], dbpediaresource:"Guinea"],	
	[name:[en:'Guinea-Bissau', pt:'Guiné-Bissau'], 
	 	adj:[en:~/[Gg]uineans?/, pt: ~/[Bb]issauenses?/], dbpediaresource:"Guinea-Bissau"],
	[name:[en:'Guyana', pt:"Guiana"], 
	 	adj:[en:~/[Gg]uyanese/, pt:~/[Gg]uianenses?/], dbpediaresource:"Guyana"],

//H
	[name:[en:'Haiti', pt:"Haiti"], 
 		adj:[en:~/[Hh]aitians?/, pt:~/[Hh]aitian[oa]s?/], dbpediaresource:"Haiti"],
	[name:[en:'Honduras', pt:"Honduras"], 
	 	adj:[en:~/[Hh]ondurans?/, pt:~/[Hh]ondurenh[oa]s?/], dbpediaresource:"Honduras"],
	[name:[en:['Hong','Kong'], pt:['Hong','Kong']], 
	 	adj:[en:~/[Hh]ongkong(?:ers|ese)/], dbpediaresource:"Hong_Kong"],
	[name:[en:'Hungary', pt:"Hungary"], 
	 	adj:[en:~/[Hh]ungarians?/, pt:~/[Hh]úngar[oa]s?/], dbpediaresource:"Hungary"],
	[name:[en:'Holland', pt:"Holanda"], 
	 	adj:[pt:~/[Hh]oland[êe]s[ea]?s?/], dbpediaresource:"Netherlands"],
//I	
	[name:[en:'Iceland', pt:"Islândia"], 
 		adj:[en:~/[Ii]celand(?:ic|er)s?/, pt:~/[Ii]sland[eê]s[ea]?s?/], dbpediaresource:"Iceland"],
	[name:[en:'India', pt:"India"], 
	 	adj:[en:~/[Ii]ndians?/, pt:~/[Ii]ndian[oa]s?/], dbpediaresource:"India"],
	[name:[en:'Indonesia', pt:"Indonésia"], 
	 	adj:[en:~/[Ii]ndonesians?/, pt:~/[Ii]ndonési[ao]s?/], dbpediaresource:"Indonesia"],
	[name:[en:'Iran', pt:~/Irão?/], 
	 	adj:[en:~/[Ii]ranians?/, pt:~/[Ii]ranian[oa]s?/], dbpediaresource:"Iran"],
	[name:[en:'Iraq', pt:"Iraque"], 
	 	adj:[en:~/[Ii]raqis?/, pt:~/[Ii]raquian[oa]s?/], dbpediaresource:"Iraq"],
	[name:[en:'Ireland', pt:"Irlanda"], 
	 	adj:[en:~/[Ii]rish/, pt:~/[Ii]rland[êe]s[ea]?s?/], dbpediaresource:"Ireland"],
	[name:[en:['Isle','of','Man'], pt:["Ilha","de","Man"]], 
	 	adj:[en:~/[Mm]anx/], dbpediaresource:"Isle_of_Man"],	
	[name:[en:'Israel', pt:"Israel"], 
	 	adj:[en:~/[Ii]sraelis?/, pt:~/[Ii]sraelitas?/], dbpediaresource:"Israel"],
	[name:[en:'Italy', pt:"Itália"], adj:[en:~/[Ii]talians?/, pt:~/[Ii]talian[oa]s?/], dbpediaresource:"Italy"],
	
//J		
	[name:[en:'Jamaica', pt:"Jamaica"], 
	 	adj:[en:~/[Jj]amaicans?/, pt: ~/[Jj]amaican[oa]?s?/], dbpediaresource:"Jamaica"],
	[name:[en:'Japan', pt:"Japão"], 
	 	adj:[en:~/[Jj]apanese/, pt: ~/[Jj]apon[êe]s[ea]?s?/], dbpediaresource:"Japan"],
	[name:[en:'Jordan', pt:"Jordânia"], 
	 	adj:[en:~/[Jj]ordanians?/, pt: ~/[Jj]ordan[oa]s?/], dbpediaresource:"Jordan"],
	
//K	
	[name:[en:'Kazakhstan', pt:"Cazaquistão"], 
	 	adj:[en:~/[Kk]azakh(?:stani)?s?/, pt:~/[Cc]azaques?/], dbpediaresource:"Kazakhstan"],
	[name:[en:'Kenya', pt:"Quénia"], 
	 	adj:[en:~/[Kk]eyans?/, pt:~/[Qq]uenian[oa]s?/], dbpediaresource:"Kanya"],
	[name:[en:'Kiribati', pt:~/[KQ]iribati/], 
	 	adj:[en:~/[Kk]iribati/], dbpediaresource:"Kiribati"],
	[name:[en:['North','Korea'], pt:[~/Cor[ée]ia/,'do','Norte']], 
	 	adj:[en:[[~/(?:[Nn]orth)?-?[Kk]oreans?/],[~/[Nn]orth/,~/[Kk]oreans?/]], pt:[[~/[Nn]orte-[Cc]orean[oa]s?/],[~/[Nn]orte/,~/[Cc]orean[oa]s?/]] ], dbpediaresource:"North_Korea"],
	[name:[en:['South','Korea'], pt:[~/Cor[ée]ia/,'do','Sul']], 
	 	adj:[en:[[~/(?:[Ss]outh)?-?[Kk]oreans?/],[~/[Ss]outh/,~/[Kk]oreans?/]], pt:[[~/[Ss]ul-[Cc]orean[oa]s?/],[~/[Ss]ul/,~/[Cc]orean[oa]s?/]] ], dbpediaresource:"South_Korea"],
	[name:[en:'Kuwait', pt:~/[CK][uo][wu][ae]ite?/], 
	 	adj:[en:~/[Kk]uwaitis?/, pt:~/[CcKk][ou][uw][ae]itian[oa]s?/], dbpediaresource:"Kuwait"],
	[name:[en:'Kosovo', pt:"Kosovo"], 
	 	adj:[en:~/[Kk]osovars?/, pt:~/[Kk]osovar(es)?/], dbpediaresource:"Kosovo"],
	[name:[en:'Kyrgyzstan', pt:"Quirjizistão"], adj:[en:~/[Kk]yrgh?[iy]z(?:stani)?/, pt:""], dbpediaresource:"Kyrgyzstan"],//Kyrgyzstani, Kyrgyz, Kirgiz, Kirghiz

//L	
	[name:[en:'Laos', pt:~/La[ou]s/], 
 		adj:[en:~/[Ll]ao(?:tian)?s?/, pt:~/[Ll]ao[cs]ian[oa]s?/], dbpediaresource:"Laos"],
	[name:[en:'Latvia', pt:~/Let[óô]nia/], 
	 	adj:[en:~/[Ll](?:atvians|ett)s?/, pt:~/[Ll]et(ão|ões)/], dbpediaresource:"Latvia"],
	[name:[en:'Lebanon', pt:"Líbano"], 
	 	adj:[en:~/[Ll]ebanese/, pt:~/[Ll]iban[êe]s[ea]?s?/], dbpediaresource:"Lebanon"],
	[name:[en:'Lesotho', pt:~/Lesoth?o/], 
	 	adj:[en:~/[Bb]asotho/, pt:~/[Ll]esoth?ian[oa]s?/], dbpediaresource:"Lesotho"],
	[name:[en:'Liberia', pt:"Libéria"], 
	 	adj:[en:~/[Ll]iberians?/, pt:~/[Ll]iberian[oa]s?/], dbpediaresource:"Liberia"],
	[name:[en:'Libya', pt:"Líbia"], 
	 	adj:[en:~/[Ll]ibyans?/, pt:~/[Ll]íbic?[oa]s?/], dbpediaresource:"Libya"],
	[name:[en:'Liechtenstein', pt:[['Listenstaine'], ["Liechtenstein"]] ],
		adj:[en:~/[Ll]iechtensteiners?/], dbpediaresource:"Liechtenstein"],
	[name:[en:'Lithuania', pt:"Lituânia"], 
	 	adj:[en:~/[Ll]ithuanians?/, pt:~/[Ll]ituan[oa]s?/], dbpediaresource:"Lithuania"],
	[name:[en:'Luxembourg', pt:"Luxemburgo"], 
	 	adj:[en:~/[Ll]uxembourg(?:ers)?s?/, pt:~/[Ll]uxemburgu[êe]s[ae]?s?/], dbpediaresource:"Luxembourg"],
	
//M		
	[name:[en:~/Maca[ou]/, pt:"Macau"], 
	 	adj:[en:~/[Mm]acanese/, pt:~/[Mm]acaenses?/], dbpediaresource:"Macau"],
	[name:[en:[['Republic','of','Macedonia'], ['Macedonia']], pt:[['República','da',~/Maced[ôó]nia/], [~/Maced[óô]nia/]] ], 
		adj:[en:~/[Mm]acedonians?/, pt:~/[Mm]acedóni[oa]s?/], dbpediaresource:"Republic_of_Macedonia"],
	[name:[en:'Madagascar', pt:"Madagáscar"], 
	 	adj:[en:~/[Mm]alagasy/], dbpediaresource:"Madagascar"],
	[name:[en:'Malawi', pt:~/Mala[uw]i/], 
	 	adj:[en:~/[Mm]alawians?/, pt:~/[Mm]ala[uv]ian[oa]s?/], dbpediaresource:"Malawi"],
	[name:[en:'Malaysia', pt:"Malásia"], 
	 	adj:[en:~/[Mm]alaysians?/, pt:~/[Mm]alai[ao]s?/], dbpediaresource:"Malaysia"],
	[name:[en:'Maldivas', pt:[["Maldivas"], ["Ilhas","Maldivas"]] ], 
	       	adj:[en:~/[Mm]aldivians?/], dbpediaresource:"Maldives"],
	[name:[en:'Mali', pt:"Mali"], 
	 	adj:[en:~/[Mm]alians?/], dbpediaresource:"Mali"],
	[name:[en:'Malta', pt:"Malta"], 
	 	adj:[en:~/[Mm]altese/, pt:~/[Mm]alt[eê]s[ea]?s?/], dbpediaresource:"Malta"],	
	[name:[en:['Marshall','Islands'], pt:['Ilhas',~/Mar(ec|s)hall?/]], 
	 	adj:[en:~/[Mm]arshallese/], dbpediaresource:"Marshall_Islands"],
	[name:[en:'Martinique', pt:"Martinica"], 
	 	adj:[en:~/[Mm]artini(?:quai|can)s?/], dbpediaresource:"Martinique"],
	[name:[en:'Mauritania', pt:"Mauritânia"], 
	 	adj:[en:~/[Mm]auritanians?/, pt:~/[Mm]auritan[oa]s?/], dbpediaresource:"Mauritania"],
	[name:[en:'Mauritius', pt:"Maurícias"], 
	 	adj:[en:~/[Mm]auritians?/], dbpediaresource:"Mauritius"],
	[name:[en:'Mayotte', pt:"Mayotte"], 
	 	adj:[en:~/[Mm]ahora[ni]s?/], dbpediaresource:"Mayotte"],
	[name:[en:'Mexico', pt:"México"], 
	 	adj:[en:~/[Mm]exicans?/, pt:~/[Mm]exican[oa]s?/], dbpediaresource:"Mexico"],
	[name:[en:'Micronesia', pt:"Micronésia"], 
	 	adj:[en:~/[Mm]icronesians?/], dbpediaresource:"Micronesia"],
	[name:[en:'Moldova', pt:~/Mold[áo]vi?a/], 
	 	adj:[en:~/[Mm]oldovans?/, pt:~/[Mm]oldav[ao]s?/], dbpediaresource:"Moldova"],
	[name:[en:'Monaco', pt:~/M[ôó]naco/], 
	 	adj:[en:~/[Mm]on(?:acan|[ée]gasque)s?/, pt:~/[Mm]onegasc[ao]s?/], dbpediaresource:"Monaco"],
	[name:[en:'Mongolia', pt:"Mongólia"], 
	 	adj:[en:~/[Mm]ongol(?:ian)?s?/, pt: ~/[Mm]ong(?:ol|óis)/], dbpediaresource:"Mongolia"],
	[name:[en:'Montenegro', pt:"Montenegro"],
	 	adj:[en:~/[Mm]ontenegrins?/, pt:~/[Mm]onte-?[Nn]egrin[oa]s?/], dbpediaresource:"Montenegro"],
	[name:[en:'Montserrat', pt:~/Mont?serrat/], 
	 	adj:[en:~/[Mm]ontserratians?/], dbpediaresource:"Montserrat"],
	[name:[en:'Morocco', pt:"Marrocos"], 
	 	adj:[en:~/[Mm]oroccans?/, pt:~/[Mm]arroquin[oa]s?/], dbpediaresource:"Morocco"],
	[name:[en:'Mozambique', pt:"Moçambique"], 
	 	adj:[en:~/[Mm]ozambicans?/, pt:~/[Mm]oçambican[ao]s?/], dbpediaresource:"Mozambique"],
	
//N		
	[name:[en:"Nagorno-Karabakh", pt:~/Nagorno-[KC]araba(?:que|kh)/], 
	 	adj:null, dbpediaresource:"Nagorno-Karabakh"],	
	[name:[en:'Namibia', pt:"Namíbia"], 
	 	adj:[en:~/[Nn]amibians?/], dbpediaresource:"Namibia"],
	[name:[en:'Nauru', pt:"Nauru"], 
	 	adj:[en:~/[Nn]auruans?/], dbpediaresource:"Nauru"],
	[name:[en:'Nepal', pt:"Nepal"], 
	 	adj:[en:~/[Nn]epal(?:i|ese)/, pt:~/[Nn]epal[eê]s[ae]?s?/], dbpediaresource:"Nepal"],
	[name:[en:'Neterlands', pt:["Países","Baixos"]], 
	 	adj:[en:~/[Dd]utch/, pt:~/[Hh]oland[êe]s[ea]?s?/], dbpediaresource:"Netherlands"],
	[name:[en:['Neterlands','Antilles'], pt:["Antilhas","Holandesas"]], 
	 	adj:[en:~/[Dd]utch-[Aa]ntilleans?/], dbpediaresource:"Netherlands_Antilles"],
	[name:[en:['New','Caledonia'], pt:["Nova",~/Caled[óô]nia/] ], 
	       	adj:[en:~/[Nn]ew-[Cc]aledonians?/], dbpediaresource:"New_Caledonia"],
	[name:[en:['New','Zealand'], pt:["Nova","Zelândia"]], 
	 	adj:[en:~/[Nn]ew-[Zz]ealanders?/, pt:~/[Nn]eo-?[Zz]eland[êe]s[ea]s?/], dbpediaresource:"New_Zealand"],
	[name:[en:'Nicaragua', pt:"Nicarágua"], 
	 	adj:[en:~/[Nn]icaraguans?/, pt:~/[Nn]icaraguens[ea]s?/], dbpediaresource:"Nicaragua"],
	[name:[en:'Niue', pt:"Niue"], 
	 	adj:[en:~/[Nn]iueans?/], dbpediaresource:"Niue"],
	[name:[en:'Niger', pt:"Níger"], 
	 	adj:[en:~/[Nn]igeriens?/], dbpediaresource:"Niger"],
	[name:[en:'Nigeria', pt:"Nigéria"], 
	 	adj:[en:~/[Nn]igerians?/, pt:~/[Nn]igerian[oa]s?/], dbpediaresource:"Nigeria"],
	[name:[en:'Norway', pt:"Noruega"], 
	 	adj:[en:~/[Nn]orwegians?/, pt:~/[Nn]oruegu[êe]s[ea]?s?/], dbpediaresource:"Norway"],
	[name:[en:['Northern','Ireland'], pt:["Irlanda","do","Norte"]], 
	 	adj:[en:~/[Nn]orthern-[Ii]rish/], dbpediaresource:"Northern_Ireland"],
	[name:[en:['Northern','Marianas'], pt:"Marianas"], 
	 	adj:[en:~/[Nn]orthern-[Mm]arianans?/], dbpediaresource:"Northern_Marianas"],
	
//O
	[name:[en:'Oman', pt:~/Omão?/], 
	 	adj:[en:~/[Oo]manis?/, pt:~/[Oo]manens[ae]s?/], dbpediaresource:"Oman"],
	
//P
	[name:[en:'Pakistan', pt:"Paquistão"], 
	 	adj:[en:~/[Pp]akistanis?/, pt:~/[Pp]aquistan[eê]s[ae]?s?/], dbpediaresource:"Pakistan"],
	[name:[en:'Palestine', pt:"Palestina"], 
	 	adj:[en:~/[Pp]alestinians?/, pt:~/[Pp]alestin(ian)?[oa]s/], dbpediaresource:"Palestine"],
	[name:[en:'Palau', pt:"Palau"], 
	 	adj:[en:~/[Pp]alauans?/], dbpediaresource:"Palau"],
	[name:[en:'Panama', pt:"Panamá"], 
	 	adj:[en:~/[Pp]anamanans?/, pt:~/[Pp]anamenses?/], dbpediaresource:"Panama"],
	[name:[en:['Papua','New','Guinea'], pt:['Papua','Nova','Guiné']], 
	 	adj:[en:~/[Pp]apuans?/], dbpediaresource:"Papua_New_Guinea"],
	[name:[en:'Paraguay', pt:"Paraguai"], 
	 	adj:[en:~/[Pp]araguayans?/, pt:~/[Pp]araguai[ao]s?/], dbpediaresource:"Paraguay"],
	[name:[en:'Peru', pt:"Perú"], 
	 	adj:[en:~/[Pp]eruvians?/, pt:~/[Pp]eruan[ao]s?/], dbpediaresource:"Peru"],
	[name:[en:'Philippines', pt:"Filipinas"], 
	 	adj:[en:~/(?:[Pp]hilippine|[Ff]ilipinos?)?/, pt: ~/[Ff]ilipin[oa]s?/], dbpediaresource:"Philippines"],
	[name:[en:['Pitcairn','Island'], pt:["Ilhas","Pitcairn"]], 
	       	adj:[en:~/[Pp]itcairn-[Ii]slanders?/], dbpediaresource:"Pitcairn_Island"],
	[name:[en:'Poland', pt:~/Pol[óô]nia/],
	 	adj:[en:~/[Pp]ol(?:ish|es?)/, pt:~/[Pp]ol(?:ac[oa]|on[|ee]s[ea]?)s?/], dbpediaresource:"Poland"],
	[name:[en:'Portugal', pt:"Portugal"], 
	 	adj:[en:~/[Pp]ortuguese/, pt:~/[Pp]ortugu[êe]s[ea]?s?/], dbpediaresource:"Portugal"],
	[name:[en:['Puerto','Rico'], pt:['Porto','Rico']], 
	 	adj:[en:~/[Pp]uerto-[Rr]icans?/, pt:~/[Pp]orto-[Rr]iquenh[ao]s?/], dbpediaresource:"Puerto_Rico"],
			
//Q	
    [name:[en:'Qatar', pt:~/[CQ]atar/], 
      	adj:[en:~/[Qq]ataris?/, pt:~/[CcQq]atares?/], dbpediaresource:"Qatar"],

//R
     [name:[en:['Republic','of','Ireland'], pt:['República','da','Irlanda']], 
      	adj:[en:~/[Ii]rish/, pt:~/[Ii]rland[êe]s[ea]?s?/], dbpediaresource:"Republic_of_Ireland"],
     [name:[en:'Réunion', pt:["Ilha",~/d[ae]/, "Reunião"] ], 
        adj:[en:~/[Rr]éunion(?:ese|nais)/], dbpediaresource:"Réunion"],
     [name:[en:'Romania', pt:"Roménia"], 
      	adj:[en:~/[Rr]omanians?/, pt:~/[Rr]omen[oa]s?/], dbpediaresource:"Romania"],
     [name:[en:'Russia', pt:"Rússia"], 
	adj:[en:~/[Rr]ussians?/, pt:~/[Rr]uss[oa]?s?/], dbpediaresource:"Russia"],
     [name:[en:'Rwanda', pt:~/R[uw]anda/], 
	adj:[en:~/[Rr]wandans?/, pt:~/[Rr]uand[êe]s[ea]?s?/], dbpediaresource:"Rwanda"],

//S	
     [name:[en:[~/S(?:ain)?t\.?/,'Helena'], pt:["Santa","Helena"]], 
       	adj:[en:~/[Ss]a(?:int)?t\\.?-[Hh]elenians?/], dbpediaresource:"Saint_Helena"],
     [name:[en:[~/S(?:ain)?t\.?/,'Kitts','and','Nevis'], pt:['São','Cristóvão','e','Neves']], 
      	adj:[en:~/(?:[Kk]ittitian|[Nn]evisian)s?/], dbpediaresource:"Saint_Kitts_and_Nevis"],
     [name:[en:[~/S(?:ain)?t\.?/,'Lucia'], pt:['Santa',~/L[uú]cia/]], 
      	adj:[en:~/[Ss]a(?:int)?t\\.?-[Ll]ucians?/], dbpediaresource:"Saint_Lucia"],
     [name:[en:['Saint-Pierre','and','Miquelon'], pt:['Saint-Pierre','e','Miquelon']],
      	adj:[en:~/(?:[Ss]aint-[Pp]ierrais|[Mm]iquelonnais)/], dbpediaresource:"Saint-Pierre_and_Miquelon"],
     [name:[en:[~/S(?:ain)?t\.?/,'Vincent','and','the','Grenadines'], pt:['São','Vicente','e','Granadinas']], 
      	adj:[en:~/[Ss]a(?:int)?t\\.?-[Vv]incentians?/], dbpediaresource:"Saint_Vincent_and_the_Grenadines"],
     [name:[en:'Samoa', pt:"Samoa"], 
      	adj:[en:~/[Ss]amoans?/, pt:~/[Ss]amoan[oa]s?/], dbpediaresource:"Samoa"],
     [name:[en:['San','Marino'], pt:[~/S[ãa][on]/,~/Mari[ñn]h?o/]], 
      	adj:[en:~/[Ss]ammarinese/, pt:~/[Ss]ão-marinenes?/], dbpediaresource:"San_Marino"],
     [name:[en:['São','Tomé','and','Príncipe'], pt:['São','Tomé','e','Príncipe']], 
      	adj:[en:~/[Ss][ãa]o-[Tt]om[eé]ans?/], dbpediaresource:"Sao_Tomé_and_Príncipe"],
     [name:[en:['Saudi','Arabia'], pt:["Arábia","Saudita"]], 
      	adj:[en:~/[Ss]audis?/, pt:~/[Ss]auditas?/], dbpediaresource:"Saudi_Arabia"],
     [name:[en:'Scotland', pt:~/Esc[ôó]cia/], 
      	adj:[en:~/[Ss]cot(?:tish)?s?/, pt:~/[Ee]scoc[êe]s[ae]?s?/], dbpediaresource:"Scotland"],
     [name:[en:'Senegal', pt:"Senegal"], 
      	adj:[en:~/[Ss]enegalese/, pt:~/[Ss]enegal[eê]s[ae]?s?/], dbpediaresource:"Senegal"],
     [name:[en:'Serbia', pt:"Sérvia"], 
      	adj:[en:~/[Ss]erb(?:ian)?s?/, pt:~/[Ss]érvi[ao]s?/], dbpediaresource:"Serbia"],
     [name:[en:'Seychelles', pt:~/Se[iy]chell?es/], 
      	adj:[en:~/[Ss]eychellois/], dbpediaresource:"Seychelles"],
     [name:[en:['Sierra','Leone'], pt:["Serra","Leoa"]], 
      	adj:[en:~/[Ss]ierra-[Ll]eoneans?/, pt:~/[Ss]erra-leonin[oa]s?/], dbpediaresource:"Sierra_Leone"],
     [name:[en:'Singapore', pt:~/[SC]ingapura/], 
	adj:[en:~/[Ss]ingaporeans?/, pt:~/[CcSs]ingapurenses?/], dbpediaresource:"Singapore"],
     [name:[en:'Slovakia', pt:"Eslováquia"], 
      	adj:[en:~/[Ss]lovaks?/, pt:~/[Ee]slovac[ao]s?/], dbpediaresource:"Slovakia"],
     [name:[en:'Slovenia', pt:~/Eslov[êé]nia/], 
        adj:[en:~/[Ss]loven(?:e|ian)s?/, pt:~/[Ee]sloven[ao]s?/], dbpediaresource:"Slovenia"],
     [name:[en:['Solomon','Islands'], pt:["Ilhas","Salomão"]], 
        adj:[en:~/[Ss]olomon-[Ii]slanders?/], dbpediaresource:"Solomon_Islands"],
     [name:[en:'Somalia', pt:"Somália"], 
     	adj:[en:~/[Ss]omalis?/, pt:~/[Ss]omalis?/], dbpediaresource:"Somalia"],
     [name:[en:['South','Africa'], pt:""], 
      	adj:[en:[[~/[Ss]outh-[Aa]fricans?/],[~/[Ss]outh/,~/[Aa]fricans?/]], pt:[[~/[Ss]ul-[Aa]frican[oa]s?/],[~/[Ss]ul/,~/[Aa]frican[oa]s?/]] ], dbpediaresource:"South_Africa"],
     [name:[en:['South','Ossetia'], pt:["Ossétia","do","Sul"]], 
        adj:[en:[[~/[Ss]outh-[Oo]ssetians?/],[~/[Ss]outh/,~/[Oo]ssetians?/]] ], dbpediaresource:"South_Ossetia"],
     [name:[en:'Spain', pt:"Espanha"], 
      	adj:[en:~/[Ss]pani(?:sh|ards?)/, pt:~/[Ee]spanh[óo][il]a?s?/], dbpediaresource:"Spain"],
     [name:[en:['Sri','Lanka'], pt:[~/Se?ri/,~/Lan[kc]a/]], 
      	adj:[en:~/[Ss]ri-?[Ll]ankans?/], dbpediaresource:"Sri_Lanka"],
     [name:[en:'Sudan', pt:"Sudão"], 
      	adj:[en:~/[Ss]udanese/, pt:~/[Ss]udan[êe]s[ea]?s?/], dbpediaresource:"Sudan"],
     [name:[en:'Surinam', pt:"Suriname"], 
      	adj:[en:~/[Ss]uriname(?:rs|se)/, pt:~/[Ss]urinam[êe]s[ea]?s?/], dbpediaresource:"Surinam"],
     [name:[en:'Swaziland', pt:"Suazilândia"], 
      	adj:[en:~/[Ss]wazis?/, pt:~/[Ss]uazis?/], dbpediaresource:"Swaziland"],
     [name:[en:'Sweden', pt:"Suécia"], 
      	adj:[en:~/[Ss]wed(?:ish|es?)/, pt:~/[Ss]uec[oa]s?/], dbpediaresource:"Sweden"],
     [name:[en:'Switzerland', pt:"Suíça"], 
        adj:[en:~/[Ss]wiss/, pt:~/[Ss]uíç[oa]s?/], dbpediaresource:"Switzerland"],
     [name:[en:'Syria', pt:"Síria"], 
        adj:[en:~/[Ss]yrians?/, pt:~/[Ss]íri[ao]s?/], dbpediaresource:"Syria"],
	
//T
     [name:[en:'Taiwan', pt:[["Taiwan"],["Formosa"]] ], 
       	adj:[en:~/[Tt]aiwanese/, pt:~/[Tt]aiwand?[êe]s[ea]?s?/], dbpediaresource:"Taiwan"],
     [name:[en:'Tajikistan', pt:"Tajiquistão"], 
	 adj:[en:~/[Tt]ajik(?:istani)?s?/, pt:~/[Tt]ajiques?/], dbpediaresource:"Tajikistan"],
     [name:[en:'Tanzania', pt:"Tanzânia"], 
	 adj:[en:~/[Tt]anzanians?/, pt:~/[Tt]anzanian[oa]s?/], dbpediaresource:"Tanzania"],
     [name:[en:'Thailand', pt:"Tailândia"], 
      	adj:[en:~/[Tt]hai/, pt:~/[Tt]ailand[êe]s[ea]?s?/], dbpediaresource:"Thailand"],
     [name:[en:'Togo', pt:"Togo"], 
	 adj:[en:~/[Tt]ogolese/, pt:~/[Tt]ogol[êe]s[ae]?s?/], dbpediaresource:"Togo"],
     [name:[en:'Tasmania', pt:"Tasmânia"], 
      	adj:[en:~/[Tt]asmanians?/, pt:~/[Tt]asmanian[oa]s?/], dbpediaresource:"Tasmania"],
     [name:[en:'Tonga', pt:"Tonga"], 
     	adj:[en:~/[Tt]ongans/], dbpediaresource:"Tonga"],
     [name:[en:['Trinidad','and','Tobago'], pt:['Trinidad','e','Tobago']], 
	 adj:[en:~/[Tt](?:rinidadian|rini|rinibagonian|obagonian)s?/, pt:~/[Tt]rinitári[oa]s?/], dbpediaresource:"Trinidad_and_Tobago"],	
     [name:[en:'Tunisia', pt:"Tunísia"], 
      	adj:[en:~/[Tt]unisians?/, pt:~/[Tt]unisin[oa]s?/], dbpediaresource:"Tunisia"],
     [name:[en:'Turkey', pt:"Turquia"], 
	 adj:[en:~/[Tt]urk(?:ish)?s?/, pt:~/[Tt]urc[oa]s?/], dbpediaresource:"Turkey"],
     [name:[en:'Turkmenistan', pt:"Turcomanistão"], 
	 adj:[en:~/[Tt]urkmens?/, pt:~/[Tt]ur(?:que|co)m[ae]nistan[eê]s[ea]?s?/], dbpediaresource:"Turkmenistan"],
     [name:[en:'Tuvalu', pt:"Tuvalu"], 
      	 adj:[en:~/[Tt]uvaluans?/, pt:~/[Tt]uvaluan[oa]s?/], dbpediaresource:"Tuvalu"],
//U
	
     [name:[en:'Uganda', pt:"Uganda"], 
      	 adj:[en:~/[Uu]gandans?/, pt:~/[Uu]gand[êe]s[ea]?s?/], dbpediaresource:"Uganda"],
     [name:[en:'Ukraine', pt:"Ucrânia"], 
      	 adj:[en:~/[Uu]krainians?/, pt:~/[Uu]cranian[oa]s?/], dbpediaresource:"Ukraine"],
     [name:[en:['United','Arab','Emirates'], pt:['Emirados','Árabes','Unidos']], 
         adj:[en:~/[Ee]mir(?:ati|ian)s?/], dbpediaresource:"United_arab_Emirates"],
     [name:[en:['United','Kingdom'], pt:["Reino","Unido"] ], 
          adj:[en:~/[Bb]rit(?:ish|ons?)/, pt:~/[Bb]ritânic[ao]s?/], dbpediaresource:"United_Kingdom"],
	// first, the bigger one!!
     [name:[en:[['United','States','of','America'],['United','States'],['USA']], pt:[['Estados','Unidos','da','América'],['Estados','Unidos'],['EUA']] ], 
      	   adj:[en:~/[Aa]mericans?/, pt:~/[Aa]mericanos?/], dbpediaresource:"United_States"],
     [name:[en:'Uruguay', pt:"Uruguai"], 
      	   adj:[en:~/[Uu]ruguayans?/, pt:~/[Uu]ruguai[oa]s?/], dbpediaresource:"Uruguay"],
     [name:[en:'Uzbekistan', pt:~/U[zs]bequistão/], 
	    adj:[en:~/[Uu]zbek(?:istanis?)?/, pt:~/[Uu][sz]beques?/], dbpediaresource:"Uzbekistan"],
	
//V
      [name:[en:'Vanuatu', pt:"Vanuatu"], 
       	adj:[en:/(?:[Nn]i-)?[Vv]anuatu(?:an)?/], dbpediaresource:"Vanuatu"],
      [name:[en:[['Vatican'],['Vatican','City']], pt:[["Vaticano"],["Cidade","do","Vaticano"]] ], 
        adj:null, dbpediaresource:"Vatican"],
      [name:[en:'Venezuela', pt:"Venezuela"], 
        adj:[en:~/[Vv]enezuelans?/, pt:~/[Vv]enezuelan[oa]s?/], dbpediaresource:"Venezuela"],
      [name:[en:'Vietnam', pt:~/Vietn(?:ã|ame?)/], 
        adj:[en:~/[Vv]ietnamese/, pt:~/[Vv]ietnamitas?/], dbpediaresource:"Vietnam"],
      [name:[en:['Virgin','Islands'], pt:["Ilhas","Virgem"]], 
       	adj:[en:~/[Vv]irgin-[Ii]slanders?/], dbpediaresource:"Virgin_Islands"],

//W
     [name:[en:'Wales', pt:['País','de','Gales']], 
  	adj:[en:~/[Ww]elsh/, pt:~/[Gg]aleses?/], dbpediaresource:"Wales"],
     [name:[en:['Wallis','and','Futuna'], pt:['Wallis','e','Futuna']], 
      	adj:[en:"(?:[Ww]allisians?|[Ff]utunans?)"], dbpediaresource:"Wallis_and_Futuna"],
     [name:[en:['Western','Sahara'], pt:[~/Sah?ara/,'Ocidental']], 
      	adj:[en:~/[Ss]ahra(?:w|wi|wian|ouian|oui)s?/], dbpediaresource:"Western_Sahara"],
 	
//Y
     [name:[en:'Yemen', pt:~/[YI][éê]men/], 
      	adj:[en:/[Yy]emenis?/, pt:~/[Ii][ée]menitas?/], dbpediaresource:"Yenem"],
//Z
    [name:[en:'Zambia', pt:"Zâmbia"], 
     	adj:[en:~/[Zz]ambians?/, pt:~/[Zz]ambian[oa]s?/], dbpediaresource:"Zambia"],
     [name:[en:'Zimbabwe', pt:~/Zimbab[wu][eé]/], 
      	adj:[en:~/[Zz]imbabweans?/, pt:~/[Zz]imbabuan[oa]s?/], dbpediaresource:"Zimbabwe"],

// REGIONS
	[name:[en:['New','England'], pt:["Nova","Inglaterra"]], 
     	adj:[en:~/[Nn]ew-?englanders?/], dbpediaresource:"New_England"],

// CITIES
	[name:[en:'London', pt:"Londres"], 
     	adj:[en:~/[Ll]ondoners?/, pt:~/[Ll]ondrinos?/], dbpediaresource:"London"],

// SEAS
	[name:[en:['Mediterranean','Sea'], pt:["Mar","Mediterrâneo"]], 
     	adj:[:], dbpediaresource:"Mediterranean_Sea"]


]

    public newInstance() {
	if (!_this) _this = new Places()	
	return _this
    }
     
    static List getMeaningList(String lang, String type) {
	if (!lang || !type) return null
	if (type != "name" && type != "adj") throw new IllegalStateException("Type is name or adj")
	if (!meanings.containsKey(lang)) meanings[lang] = [:]
	if (!meanings[lang].containsKey(type)) {
	    if (type == "name") meanings[lang][type] = generateNameMeaning(lang)
	    if (type == "adj") meanings[lang][type] = generateAdjectiveMeaning(lang)
	}
	return meanings[lang][type]
    }
    
    static List generateNameMeaning(String lang) {
	List res = []
	//println "generateNameConcept for lang $lang"
	places.each{place -> 
	//    println "generateNameMeaning: place: $place place.name.lang = "+place?.name?.get(lang)
	    def res2 = [needle:[], answer:place.dbpediaresource]	    
	   if (place?.name?.get(lang)) {
	       def need = place?.name?.get(lang)
	       if (need instanceof String || need instanceof Pattern) res2.needle << need
	       // for lists within lists, there is no need of a special OR -> just add each list in the needle,
	       // the MeaningMatch will perform the AND and OR behavior as expected
	       if (need instanceof List) {
		   if (need[0] instanceof String || need[0] instanceof Pattern) res2.needle << need
		   else if (need[0] instanceof List) {
		       need.each{need2 -> res2.needle << need2}
		   }
	       }	       
	   }
	   if (res2.needle) res << res2
	}
	//println "generateNameMeaning res: $res"
	return res
    }
    
    static List generateAdjectiveMeaning(String lang) {
	List res = []
	//println "generateNameConcept for lang $lang"
	places.each{place -> 
	 //   println "generateNameMeaning: place: $place place.adj.lang = "+place?.adj?.get(lang)
	   def res2 = [needle:[], answer:place.dbpediaresource]	    
	   if (place?.adj?.get(lang)) {
	       def need = place?.adj?.get(lang)
	       if (need instanceof String || need instanceof Pattern) res2.needle << need
	       // for lists within lists, there is no need of a special OR -> just add each list in the needle,
	       // the MeaningMatch will perform the AND and OR behavior as expected
	       if (need instanceof List) {
		   if (need[0] instanceof String || need[0] instanceof Pattern) res2.needle << need
		   else if (need[0] instanceof List) {
		       need.each{need2 -> res2.needle << need2}
		   }
	       }	       
	   }
	   if (res2.needle) res << res2
	}
//	println "generateNameMeaning res: $res"
	return res
    }
}