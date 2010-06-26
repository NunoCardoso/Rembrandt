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
 * This class stores gazetteers for TIME category.
 */
class TimeGazetteerEN {
	      
     /* PATTERNS */    
     
     static final List<Pattern> monthText = [~/[Jj]anuary/, ~/[Ff]ebruary/, ~/[Mm]arch/, ~/[Aa]pril/, ~/[Mm]ay/,
       ~/[Jj]une/, ~/[Jj]uly/, ~/[Aa]ugust/, ~/[Ss]eptember/, ~/[Oo]ctober/, ~/[Nn]ovember/, ~/[Dd]ecember/] 
     
     static final List<Pattern> monthDim =  [~/[Jj]an/, ~/[Ff]eb/, ~/[Mm]ar/, ~/[Aa]pr/, ~/[Mm]ay/, ~/[Jj]un/, 
        ~/[Jj]ul/, ~/[Aa]ug/, ~/[Ss]ep/, ~/[Oo]ct/, ~/[Nn]ov/, ~/[Dd]ec/]
   	 					
     static final List<Pattern> dayWeekText = [~/[Mm]on\./, ~/[Mm]onday/, ~/Tue\./, ~/[Tt]uesday/, ~/[Ww]ed\./,
	~/[Ww]ednesday/, ~/[Tt]hu\./, ~/[Tt]hursday/, ~/[Ff]ri\./, ~/[Ff]riday/, ~/[Ss]at\./, 
	~/[Ss]aturday/, ~/[Ss]un\./, ~/[Ss]unday/]
       
     static final Pattern centuryText = ~/[Cc]entury/
     	 
     static final List selectedTimeTypesSingle = ["second","minute","hour","day","week","month",
              "quarter", ~/trimest(?:re|er)/, ~/semest(?:re|er)/,"year","decade"]
      
     static final List selectedTimeTypesPlural = ["seconds","minutes","hours","days","weeks",
             "months", ~/trimest(?:re|er)s/,"quarters", ~/semest(?:re|er)s/,"years","decades"]
      
     static final List<String> holidays = ["Christmas","Xmas","Easter","Carnival"]
     
     static final List<String> seasons = ["Winter","Summer","Autumn","Spring"]
     
     static final Pattern timeFrequencyAdverbs = ~/(?:[Dd]aily|[Ww]eekly|[Mm]onthly|[Tt]rimestrally|[Qq]uarterly|[Ss]emestrally|[Aa]nnually|[Ff]requently|[Pp]eriodically)/
  
     static final Pattern numeralYear = ~/[12][0-9]\d\d/
	 
     static final Pattern centuryNumber = ~/[IVX]+(?:nd|st|rd|th)?/
	     
     static final Pattern centurySuffix = ~/[AaBb]\.?[Cc]\.?/

     static final Pattern monthYearNumeral = ~/(?:\d{2}[\/-]\d{2}|\d{2}[\/-]\d{4}|\d{1,2}[-\/]\d{1,2}[\/-]\d{2,4}|\d{4}[\/-]\d{2}|\d{4}[-\/]\d{1,2}[\/-]\d{1,2})/
     static final Pattern monthYearTextual =  ~/(?x) (?: \d{2,4}  [\/-] )?  (?: [Jj]an(?:uary)?|[Ff]eb(?:ruary)?|[Mm]ar(?:ch)?|[Aa]pr(?:il)?| \
        [Mm]ay|[Jj]une?|[Jj]uly?|[Aa]ug(?:ust)?|[Ss]ep(?:tember)?|[Oo]ct(?:ober)?|[Nn]ov(?:ember)?|[Dd]ec(?:ember) ) [\/-] \d{2,4}/ 

        
 /** CLAUSES **/
	 
     static final Clause numeralYear1c = Clause.newRegex1Clause(numeralYear, "numeralYear")    
// duplicate
     static final Clause numeralYear1c_duplicate = Clause.newRegex1Clause(numeralYear,"numeralYear duplicate")
     
     static final Clause monthAll1c = Clause.newConcept1Clause(monthText + monthDim, "Month") 
	
     static final Clause monthText1c = Clause.newConcept1Clause(monthText ,"Month") 
	
     static final Clause dayWeek1c = Clause.newConcept1Clause(dayWeekText,"dayWeek") 
	
     static final Clause timeFrequencyAdverbs1c = Clause.newRegex1Clause(
	     timeFrequencyAdverbs, "timeFrequencyAdverbs")
 			
     static final Clause timeFullUnits01c = Clause.newConcept01Clause(
	     ValueGazetteerEN.timeFullUnits,"timeFullUnits")
		
     static final Clause timeFullUnits1c = Clause.newConcept1Clause(
	     ValueGazetteerEN.timeFullUnits,"timeFullUnits")
		
     static final Clause century01c = Clause.newRegex01Clause(centuryText,"century")	
     static final Clause century1c = Clause.newRegex1Clause(centuryText,"century")	
		
     static final Clause centuryRoman1c = Clause.newRegex1Clause(~/[IVX]+/,"[IVX]+")
    static final Clause centuryRoman01c = Clause.newRegex1Clause(~/[IVX]+/,"[IVX]+")

    static final Clause monthYearNumeral1c =  Clause.newRegex1Clause(monthYearNumeral, 'monthYearNumber', true)
             
     static final Clause monthYearTextual1c =  Clause.newRegex1Clause(monthYearTextual, 'monthYearTextual', true)
    
     static final Clause centuryNumber1c = Clause.newRegex1Clause(centuryNumber, "centuryNumber")
		
     static final Clause centurySuffix01c = Clause.newRegex01Clause(centurySuffix, 'centurySuffix') //ac, dc, AC, DC, a.c., d.c, A.C., D.C.
     static final Clause centurySuffix2_01c = Clause.newRegex01Clause(centurySuffix, 'centurySuffix_duplicate') //ac, dc, AC, DC, a.c., d.c, A.C., D.C.

     static final Clause centurySuffix1c = Clause.newRegex1Clause(centurySuffix, "centurySuffix") //bc, ac, BC, BC, b.c., a.c, B.C., A.C.
	
     static final Clause holidaysSeasons01c = Clause.newConcept01Clause(holidays+seasons,"holidays+seasons")	
	
     static final Clause holidaysSeasons1c = Clause.newConcept1Clause(holidays+seasons,"holidays+seasons")	

     static final Clause selectedTimeTypesSingle01c = Clause.newConcept01Clause(
	selectedTimeTypesSingle, "selected time types single", true)

     static final Clause selectedTimeTypesSingle1c = Clause.newConcept1Clause(
	selectedTimeTypesSingle, "selected time types single", true)
		
     static final Clause selectedTimeTypesPlural1c = Clause.newConcept1Clause(
	selectedTimeTypesPlural, "selected time types plural", true)
		
     static final Clause selectedTimeTypesAll1c = Clause.newConcept1Clause(
	(selectedTimeTypesSingle + selectedTimeTypesPlural), "selected time types single + plural", true)

     static final Clause selectedTimeTypesAll01c = Clause.newConcept01Clause(
	(selectedTimeTypesSingle + selectedTimeTypesPlural), "selected time types single + plural", true)
}