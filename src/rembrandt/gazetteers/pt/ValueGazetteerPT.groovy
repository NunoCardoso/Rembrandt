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

import java.util.List;
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for OBRA category.
 */
class ValueGazetteerPT {      
	
     /* PATTERNS */ 
     
    static final List currency = ["EUR","USD","GBP", ~/[Cc]ruzeiros?/, ~/[Cc]ruzados?/, ~/[Ll]ibras?/, ~/[Ee]uros?/, 
	    ~/[Cc]ontos?/, ~/[Rr]eais/, ~/[Pp]esetas?/, ~/[Tt]ostões/, ~/[Pp]esos?/, ~/[Dd]ólar(?:es)?/, 
	    ~/[Dd]inar(?:es)?/, ~/[Ff]rancos?/, ~/[Cc]oroas?/, ~/[Rr]éis/, ~/[Mm]arcos?/, ~/[Ee]scudos?/ ] 
    	
    static final List lengthFullUnits = [[ ~/(?:quiló|decí|centí|milí|micro|nano)?metros?/,~/(?:quadrados?|cúbicos?)/],
              ~/(?:hect|quilo|dec)?ares?/, ~/(?:quiló|decí|centí|milí|micro|nano)?metros?/, ~/jardas?/, ~/polegadas?/,'pés',~/milhas?/]
     
    static final List lengthAbbrvUnits = [ [~/[Kkdcmn]?ms?/,~/[23²³]/],~/[Kkdcmn]?ms?/,~/[Kkdcmn]?m[23²³]/ ]
 
    static final List timeFullUnits = [ ~/(?:mili|nano|micro)?segundos?/,~/minutos?/, ~/horas?/, ~/dias?/, ~/semanas?/,
	    ~/m[êe]s(es)?/, ~/anos?/, ~/décadas?/, ~/séculos?/ ]
 
    static final List timeAbbrvUnits = ['s','m', ~/mins?/, ~/hs?/ ,~/m?se[cg]s?/]
    
    static final List computerFullUnits = [~/(?:[Kk]ilo|[Qq]uilo|[Mm]ega|[Gg]iga|[Tt]era|[Pp]eta)?[kKMGTP]?[Bb][iy]te?s?/]

    static final List computerAbbrvUnits = [~/[kKMGTP][Bb]/, ~/[kKMG][hH]z/,  ~/[kKMGTP][Bb][p\/]s/] // kbps, Gb/s
     
    static final List weightFullUnits = [ ~/(?:(?:k|qu)ilo)?gramas?/,~/toneladas?/,~/arrobas?/,~/libras?/, ~/onças?/ ]
     
    static final List weightAbbrvUnits = [~/[kKm]?gs?/,~/tons?/]	
     
    static final List temperatureUnits =  [~/[º°]/, ~/[º°]?[CFK]/, [~/[º°]/,~/[CKF]/],[ ~/(?:graus|º|°)/,~/(?:centígrados|Kelvin|Celsius|Fahrenheit)/],
	    'Celsius','Fahrenheit','Kelvin']		
 
    static final List energyUnits = [~/(?:[kKM]|[QqKk]u?ilo|[Mm]ega)?W(att)?s?/,'J',~/[Jj]oules?/,~/Cals?/,~/[Cc]alorias?/]
 
    static final List speedUnits = [~/[Mm]\/[Ss]/,~/[Kk]m\/[Hh]/, ~/[Kk]m\/[Ll]/, [~/[Kk]?m/,~/\//, ~/[SshHlL]\.?/] ]

    static final List allUnits = speedUnits + lengthFullUnits + lengthAbbrvUnits + timeFullUnits + timeAbbrvUnits +
    	computerFullUnits + computerAbbrvUnits + weightFullUnits + weightAbbrvUnits + temperatureUnits + energyUnits 	
  
    static final Pattern moneyNumber = ~/\-?\d[\d.,]*[\Q$\€¥£\E]\d*/ 
    
    static final Pattern moneyNumber2 = ~/[\Q$\€¥£\E]\d[\d.,]*/
    
    static final Pattern percNumber = ~/\-?\d[\d.,]*\Q%\E\.*?/
   
    
    /** CLAUSES **/
	  // por {dia|ano|mês}, por {metro|kilograma|metro quadrado}  
    static final Clause porUnits01c = Clause.newConcept01Clause(
	    TimeGazetteerPT.selectedTimeTypesSingle + weightFullUnits + lengthFullUnits, "porUnits")  
    					                		
    static final Clause temperature1c = Clause.newRegex1Clause(~/-?\d[\.\d]*º[CFK]/, "-3.4ºC")	
    	
    static final Clause moneyNumber1c = Clause.newRegex1Clause(moneyNumber, "moneyNumber")
        	
    static final Clause moneyNumber2_1c = Clause.newRegex1Clause(moneyNumber2, "moneyNumber2")

    static final Clause percNumber1c = Clause.newRegex1Clause(percNumber, "percNumber")

    static final Clause currency1c = Clause.newConcept1Clause(currency, "currency")

    static final Clause allUnits1c =  Clause.newConcept1Clause(allUnits, "allUnits")

    static final Clause timeFullUnits1c = Clause.newConcept1Clause(timeFullUnits, "timeUnits1")

    static final Clause root01c = Clause.newRegex01Clause(~/[²³]\.?/, "square|cubic") 
    					                					  
    static final Clause currencyPrefix1c  = Clause.newRegex1Clause(~/(?:US|R)/,"US|R") // US $1000, R $34    					
}