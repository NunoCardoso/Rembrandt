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

import java.util.List;
import java.util.regex.Pattern;

import rembrandt.obj.Clause

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for VALUE category.
 */
class ValueGazetteerEN {      
	
     /* PATTERNS */ 
     
    static final List currency = ["EUR","USD","GBP",~/[Pp]ounds?/,[~/[Ss]terling/,~/[Pp]ounds/],~/[Ee]uros?/,
	    ~/[Dd]ollars?/, ~/[Dd]inars?/,~/[Pp]enn(?:y|ies)/,~/[Cc]ents?/] 
	
    static final List lengthFullUnits = [~/(?:quilo|deci|centi|milli|micro|nano)?meters?/,
	    [~/(?:squared?|cubic)/, ~/(?:quilo|deci|centi|mili|micro|nano)?meters?/], 
           ~/f(?:oo|eet)|inch(?:es)?|yards?|miles?/,
            [ ~/(?:squared?|cubic)/, ~/f(?:oo|eet)|inch(?:es)?|yards?|miles?/] ]
     
    static final List lengthAbbrvUnits = [ [~/(?:[Kkdcmn]?m|yd|in|ft)/, ~/[23\u00B2\u00B3\u2072\u2073]/],
         ~/(?:[Kkdcmn]?m|yd|in|ft)[23\u00B2\u00B3\u2072\u2073]?/ ]
 
    static final List timeFullUnits = [~/(?:milli|nano|micro)?seconds?/, ~/minutes?/, ~/hours?/, 
        ~/days?/, ~/weeks?/, ~/months?/, ~/years?/, ~/decades?/, ~/centur(y|ies)/]
 
    static final List timeAbbrvUnits = ['s','m', ~/mins?/, ~/hr?s?/, ~/m?secs?/]
     
    static final List computerFullUnits = [ ~/(?:[Kk]ilo|[Qq]uilo|[Mm]ega|[Gg]iga|[Tt]era|[Pp]eta)?[kKMGTP]?[Bb][iy]te?s?/ ]
 
    static final List computerAbbrvUnits = [ ~/[kKMGTP][Bb]/, ~/[kKMG][hH]z/, ~/[kKMGTP][Bb][p\/]s/] // kbps, Gb/s
     
    static final List weightFullUnits = [ ~/(?:kilo|quilo)?grams?/, ~/pounds?/, ~/ounces?/]
     
    static final List weightAbbrvUnits = [ ~/[kKm]?gs?/, ~/[Tt]ons?/]	

     static final List temperatureUnits =  [~/[º°]/, ~/[º°]?[CFK]/, [~/[º°]/,~/[CKF]/],[ ~/(?:degrees|º|°)/, 
         ~/(?:[Cc]entigrades|[Kk]elvin|[Cc]elsius|[Ff]ahrenheit)/ ], 'Celsius','Fahrenheit','Kelvin']		
 
    static final List energyUnits = [ ~/(?:[kKM]|[QqKk]u?ilo|[Mm]ega)?W(?:att)?s?/ ,'J', 
	    /[Jj]oules?/, /Cals?/, /[Cc]alories?/, 'BTU']

    static final List speedUnits = [ ~/[Mm]\/[Ss]/, ~/[Kk]m\/[Hh]/, ~/[Kk]m\/[Ll]/, [~/[Kk]?m/,'/',~/[SshHlL]\.?/] ]

    static final List allUnits = speedUnits + lengthFullUnits + lengthAbbrvUnits + timeFullUnits + timeAbbrvUnits +
    	computerFullUnits + computerAbbrvUnits + weightFullUnits + weightAbbrvUnits + temperatureUnits + energyUnits 	

        static final Pattern moneyNumber = ~/\-?\d[\d.,]*[\Q$\€¥£\E]\d*/ 
        
        static final Pattern moneyNumber2 = ~/[\Q$\€¥£\E]\d[\d.,]*/
        
        static final Pattern percNumber = ~/\-?\d[\d.,]*\Q%\E\.*?/

    /** CLAUSES **/

     final Clause porUnits01c = Clause.newConcept01Clause(
       TimeGazetteerPT.selectedTimeTypesSingle + weightFullUnits + lengthFullUnits, "porUnits")  
        	                                                                        
     static final Clause temperature1c = Clause.newRegex1Clause(~/-?\d[\.\d]*º[CFK]/, "-3.4ºC")  
     
    static final Clause moneyNumber1c = Clause.newRegex1Clause(moneyNumber, "moneyNumber")

    static final Clause moneyNumber2_1c = Clause.newRegex1Clause(moneyNumber2, "moneyNumber2")

    static final Clause percNumber1c = Clause.newRegex1Clause(percNumber, "percNumber")

    static final Clause currency1c = Clause.newConcept1Clause(currency, "currency")

    static final Clause allUnits1c = Clause.newConcept1Clause(allUnits, "allUnits")

    static final Clause timeFullUnits1c = Clause.newConcept1Clause(timeFullUnits, "timeUnits1")

    static final Clause root01c = Clause.newRegex01Clause(~/[²³]\.?/, "square|cubic") 
    				  
    static final Clause currencyPrefix1c  = Clause.newRegex1Clause( ~/(?:US|R)/, "US|R") // US $1000, R $34

}