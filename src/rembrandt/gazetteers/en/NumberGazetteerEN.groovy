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
 * This class stores gazetteers for NUMBER category.
 */
class NumberGazetteerEN {
    
    /* LISTS */	
    
    // *?-? because of "thirty-one"... everything before is also a number.
    static final List<Pattern> singleTextNumber = [~/\w*?-?[Oo]ne/,~/\w*?-?[Tt]wo/,~/\w*?-?[Tt]hree/,~/\w*?-?[Ff]our/,
    ~/\w*?-?[Ff]ive/, ~/\w*?-?[Ss]ix/, ~/\w*?-?[Ss]even/,~/\w*?-?[Ee]ight/, ~/\w*?-?[Nn]ine/,
    ~/[Tt]en/,~/[Ee]leven/,~/[Tt]welve/, ~/[Tt]hirteen/,~/[Ff]ourteen/,~/[Ff]ifteen/,~/[Ss]ixteen/,
    ~/[Ss]eventeen/,~/[Ee]ighteen/,~/[Nn]ineteen/]
    
    static final List<Pattern> tenthsTextNumber = [~/[Tt]wenty/, ~/[tT]hirty/, ~/[Ff]ou?rty/, ~/[Ff]ifty/,
	    ~/[Ss]ixty/, ~/[Ss]eventy/, ~/[Ee]ighty/, ~/[Nn]inety/]
    
    static final List<Pattern> thousandsTextNumber = [~/[Hh]undreds?/, ~/[Tt]housands?/, ~/[BbMm]illions?/, 
	    ~/[Tt]rillions/]
    
    static final List<Pattern> singleOrdinalTextNumber = [~/\w*?-?[Ff]irst/, ~/\w*?-?[Ss]econd/, ~/\w*?-?[Tt]hird/,
    ~/\w*?-?[Ff]ourth/, ~/\w*?-?[ff]ifth/, ~/\w*?-?[Ss]ixth/, ~/\w*?-?[Ss]eventh/, ~/\w*?-?[Ee]ighth/,
    ~/\w*?-?[Nn]inth/, ~/[Tt]enth/, ~/[Ee]leventh/,~/[Tt]welfth/,~/[Tt]hirteenth/,~/[Ff]ourteenth/,
    ~/[Ff]ifteenth/, ~/[Ss]ixteenth/, ~/[Ss]eventeenth/, ~/[Ee]ighteenth/, ~/[Nn]ineteenth/]
    
    static final List<Pattern> tenthsOrdinalTextNumber = [~/[Tt]wentieth/, ~/[Tt]hirtieth/, ~/[Ff]ortieth/, 
         ~/[Ff]iftieth/,~/[Ss]ixtieth/,~/[Ss]eventieth/,~/[Ee]ightieth/,~/[Nn]inetieth/]
    
    static final List<Pattern> thousandsOrdinalTextNumber = [ ~/[Hh]undredth/, ~/[Tt]housandth/, ~/[BbMm]illionth/,
                                                       ~/[Tt]rillionth/]
    
    static final Pattern eventNumber = ~/[IVXLMC]+|\\d+(nd|st|rd|th)/
    
    static final Pattern ordinalNumber = ~/\d+(nd|st|rd|th)/	
    
    static final Pattern digitNumber = ~/\-?\d[\d.,]*/
    
    static final List numberPrefix = [/[Nn]r?.?/,['N','.'],/[Nn]umber/]      	 								
    
    /* CLAUSES */
    
    static final Clause numberPrefix01nc = Clause.newConcept01Clause(
	    numberPrefix, "number prefix",false)
    
    static final Clause digit1Pc = Clause.newRegex1PClause(
	    digitNumber,"Digit",true)
    
    static final Clause ordinalNumber1c = Clause.newRegex1Clause(
	    ordinalNumber, "Ordinal Number", true)
    
    static final Clause textNumber01c = Clause.newConcept01Clause(
    (singleTextNumber + tenthsTextNumber),"textNumber single and tenths", true) 
    
    static final Clause textNumber1Pc = Clause.newConcept1PClause(
    (singleTextNumber + tenthsTextNumber),"textNumber single and tenths", true) 
    
    static final Clause thousandsTextNumber1Pc = Clause.newConcept1PClause(
	    thousandsTextNumber, "thousandsTextNumber1c", true)	
    
    static final Clause ordinalTextNumber1c = Clause.newConcept1Clause(
    (singleOrdinalTextNumber + tenthsOrdinalTextNumber), "ordinalNumberNames1c", true)
    
   static final Clause ordinalTextNumber1Pc = Clause.newConcept1PClause(
    (singleOrdinalTextNumber + tenthsOrdinalTextNumber), "ordinalNumberNames1c", true)

static final Clause thousandsOrdinalTextNumber1Pc = Clause.newConcept1PClause(
	    thousandsOrdinalTextNumber, "thousandsOrdinalTextNumber1c", true)
    
    static final Clause publicationNumber1c = Clause.newRegex1Clause(
	    ~/\d+\/\d+/,"\\d+/\\d+",true)
}