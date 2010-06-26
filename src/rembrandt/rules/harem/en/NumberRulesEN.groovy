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

package rembrandt.rules.harem.en

import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.RulePolicy
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.en.NumberGazetteerEN
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector

/**
 * @author Nuno Cardoso
 * Rules for capturing numeric expressions 
 */
class NumberRulesEN extends NamedEntityDetector {

    List<Rule> rules
    SemanticClassification sc_ordinal, sc_textual, sc_numeral, sc_number

    /**
     * Main constructor
     */    
    public NumberRulesEN() {
	
	rules = [] 	
	sc_ordinal = new SemanticClassification(Classes.category.number, Classes.type.ordinal)
	sc_textual = new SemanticClassification(Classes.category.number, Classes.type.textual)
	sc_numeral = new SemanticClassification(Classes.category.number, Classes.type.numeral)
	sc_number = new SemanticClassification(Classes.category.number, Classes.type.number)
	 	  
	 /**
	  * Ordinal numbers: \\d+(st|nd|rd|th)
	  */       
	rules.add(new Rule(id:"NumberRulesEN ORDINAL 1", description:"{\\d+(st|nd|rd|th)}",
    sc:SC.number_ordinal, policy:RulePolicy.Rule, clauses: [ NumberGazetteerEN.ordinalNumber1c] ))    
	 	
        /** 
         * Numbers. Example: 1  34  32,9  1.000 {32 000} -> 1P 
         */       
        rules.add(new Rule(id:"NumberRulesEN NUMERAL 1",description:"{\\d[\\d.,]*!}",
        sc:SC.number_numeral, policy:RulePolicy.Rule, clauses: [ NumberGazetteerEN.digit1Pc ] ))    
        
         /**
         *  Text numbers. Example: three, twenty, twenty three 
         */
        rules.add(new Rule(id:"NumberRulesEN TEXTUAL 1",description:"{[three|twenty]+!}",
        sc:SC.number_textual, policy:RulePolicy.Rule, clauses: [ NumberGazetteerEN.textNumber1Pc ]))
             
         /**
         *  Text ordinal numbers. Example: third, twentieth, 
         */
        rules.add(new Rule(id:"NumberRulesEN ORDINAL 2",description:"{[third|twentieth]+!}",
        sc:sc_ordinal, policy:RulePolicy.Rule, clauses: [ NumberGazetteerEN.ordinalTextNumber1Pc ]))
                 
         /**
         *  Compound Text Numbers
         *  Example: One hundred (and|,) twenty three
         */ 
        rules.add(new Rule(id:"NumberRulesEN TEXTUAL 2",description:"{[One|...]? [hundred|...]! [and|,]? <NUMERO>!}",
        sc:SC.number_textual, policy:RulePolicy.Rule, clauses: [ 
                   NumberGazetteerEN.textNumber01c, NumberGazetteerEN.thousandsTextNumber1Pc, 
                   ClausesEN.andComma01c, NEGazetteer.NE_NUMERO_TEXTUAL_1c ]))
                    

        // <NUMERO> + [ thousand ]+ ex: 3 hundred thousand, hundreds
        rules.add(new Rule(id:"NumberRulesEN TEXTUAL 3",description:"{<NUMERO>? [thousand|...]+}",
        sc:SC.number_textual, policy:RulePolicy.Rule, clauses: [
             NEGazetteer.NE_NUMERO_01c, NumberGazetteerEN.thousandsTextNumber1Pc ]))   
        
         // <NUMERO>? + [ thousandth ]+ ex: 3 hundredth, hundredth
        rules.add(new Rule(id:"NumberRulesEN ORDINAL 3",description:"{<NUMERO>? [thousandth|...]+}",
        sc:SC.number_textual, policy:RulePolicy.Rule, clauses: [ 
             NEGazetteer.NE_NUMERO_01c, NumberGazetteerEN.thousandsOrdinalTextNumber1Pc ]))   
                             
        // {between! <NUM>! and! <NUM>!}
        rules.add(new Rule(id:"NumberRulesEN NUMERO 1",description:"{between! <NUM>! and! <NUM>!}",
        sc:SC.number, policy:RulePolicy.Rule,  clauses: [
             ClausesEN.between1c, NEGazetteer.NE_NUMERO_1c, 
             ClausesEN.and1c, NEGazetteer.NE_NUMERO2_1c]))  
       
    } 
}