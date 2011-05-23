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
import rembrandt.gazetteers.en.ValueGazetteerEN
import rembrandt.gazetteers.en.LocalGazetteerEN
import rembrandt.gazetteers.en.TimeGazetteerEN
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClauses
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector

/**
 * @author Nuno Cardoso
 * Rules for capturing VALOR NEs
 */
class ValueRulesEN extends NamedEntityDetector {

    List<Rule> rules
    SemanticClassification sc

    /**
     * Main constructor
     */    
    public ValueRulesEN() {

	rules = []	   

	/** MOEDA **/
        	   
	// 1.000.000$00  \\d[\\d.,]$\\d+?
       rules.add(new Rule( id:"VALORMOEDA1", description:"1.000\$00", 
	  sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [ ValueGazetteerEN.moneyNumber1c ]))

       // 1.000.000 $ 00?  \\d[\\d.,]$\\d+?, $ 34.1
       rules.add(new Rule( id:"VALORMOEDA1.5", description:"{<NUM>! \$! <NUM>?}", 
    sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
	     NEGazetteer.NE_NUMERO_1c, 
        CommonClauses.moneySymbols1c, 
	     NEGazetteer.NE_NUMERO_01c ]))



       // $34.1 , $34.1 hundred millions
       rules.add(new Rule( id:"VALORMOEDA2", description:"\$1.000", 
    sc:SC.value_currency, policy:RulePolicy.Rule, clauses:[
              ValueGazetteerEN.moneyNumber2_1c, 
              NEGazetteer.NE_NUMERO_01c]))

       // $ 300  
rules.add(new Rule( id:"VALORMOEDA2.5", description:"{\$! <NUM>!}", 
sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
        CommonClauses.moneySymbols1c, 
        NEGazetteer.NE_NUMERO_1c ]))
        
       // {<NUMERO|MOEDA>! of? [country|...]? [currency|...]!}�
	   // millions of mexican pesos.
       rules.add(new Rule( id:"VALORMOEDA3", description:"{<CURRENCY>! of? [country|...]? [currency|...]!}", 
    sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
	      NEGazetteer.NE_VALOR_MOEDA_1c, 
	      ClausesEN.of01c, 
	      LocalGazetteerEN.countryAdjective01c, 
	      ValueGazetteerEN.currency1c ]))

     rules.add(new Rule( id:"VALORMOEDA3.5", description:"{<NUMBER>! of? [country|...]? [currency|...]!}", 
    sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
	      NEGazetteer.NE_NUMERO_1c, 
	      ClausesEN.of01c, 
	      LocalGazetteerEN.countryAdjective01c, 
	      ValueGazetteerEN.currency1c ]))

	      // {US|R! <MOEDA>!}
       rules.add(new Rule( id:"VALORMOEDA4", description:"{US|R! <MOEDA>!}", 
    sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
	      ValueGazetteerEN.currencyPrefix1c, 
	      NEGazetteer.NE_VALOR_MOEDA_1c ]))
       
        // {[abaut|less|more|approximately...]! than? <VALOR>!} 
      rules.add(new Rule( id:"VALORMOEDA5", description:"{[abaut|less|more|...]! than? <VALOR>!}",
    sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
	      ClausesEN.aboutAboveApproximatelyBelow1c,
	      ClausesEN.than01c, 
	      NEGazetteer.NE_VALOR_MOEDA_1c] ))     
	   
      
	// 10 watts
    	rules.add(new Rule( id:"VALORQUANTIDADE1", description:"{<NUM>! of? [units]! per? [day|year|...]?", 
        sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
	      NEGazetteer.NE_NUMERO_1c, 
	      ClausesEN.of01c, 
	      ValueGazetteerEN.allUnits1c, 
	      ClausesEN.per01c,
	      TimeGazetteerEN.selectedTimeTypesSingle01c]))
		   	   	  
	 // 10%
	 rules.add(new Rule( id:"VALORQUANTIDADE2", description:"XX%.?",
    sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
	       ValueGazetteerEN.percNumber1c] ))   
 
	 // 10 %
	 rules.add(new Rule( id:"VALORQUANTIDADE2.5", description:"{<NUM>! %!}",
    sc:SC.value_quantity, policy:RulePolicy.Rule,  clauses: [
	       NEGazetteer.NE_NUMERO_1c, 
	       CommonClauses.percentage1c]  ))   
 
	    // 10 percent       
	 rules.add(new Rule( id:"VALORQUANTIDADE5", description:"{<NUM>! percent!}",
    sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
	       NEGazetteer.NE_NUMERO_1c, 
	       ClausesEN.percent1c]  ))   
 
	   // <VALOR>! [above|below|...]!   
	 rules.add(new Rule( id:"VALORQUANTIDADE6", description:"{<VALOR>! [above|below|...]!}",
    sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
	       NEGazetteer.NE_VALOR_QUANTIDADE_1c, 
	       ClausesEN.aboutAboveApproximatelyBelow1c]  ))   
 
	   // [about|above|more|...] <VALOR>!
	rules.add(new Rule( id:"VALORQUANTIDADE7", description:"{[above|below|...]!  <VALOR>!}",
    sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [ 
	        ClausesEN.aboutAboveApproximatelyBelow1c, 
	        NEGazetteer.NE_VALOR_QUANTIDADE_1c]  ))   
        
        // temperaturas coladas, ex: 34ºC
        rules.add(new Rule( id:"VALORQUANTIDADE8", description:"-?\\d[\\.\\d]*º[CFK]",
                sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
                ValueGazetteerEN.temperature1c] ))   
        
  
	 /**********************/       
	//SemanticClassificationDefinitions.type.classification
        /*********************/
    }
}