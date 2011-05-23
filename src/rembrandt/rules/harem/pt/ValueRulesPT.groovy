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
 
package rembrandt.rules.harem.pt

import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.RulePolicy
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.pt.TimeGazetteerPT
import rembrandt.gazetteers.pt.ValueGazetteerPT
import rembrandt.gazetteers.pt.LocalGazetteerPT
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClauses
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector

/**
 * @author Nuno Cardoso
 * Rules for capturing VALOR NEs
 */
class ValueRulesPT extends NamedEntityDetector {

    static List<Rule> rules
 
    /**
     * Main constructor
     */    
    public ValueRulesPT() {

       rules = []	   
 
       /** MOEDA **/
	   
  	   // 1.000.000$00  \\d[\\d.,]$\\d+?
       rules.add(new Rule( id:"VALORMOEDA1", description:"1.000\$00", 
       sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [ ValueGazetteerPT.moneyNumber1c ]))

       // 1.000.000 $ 00  \\d[\\d.,]$\\d+?
       rules.add(new Rule( id:"VALORMOEDA1.5", description:"1.000 \$ 00", 
       sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
	      NEGazetteer.NE_NUMERO_1c, 
	      CommonClauses.moneySymbols1c, 
	      NEGazetteer.NE_NUMERO_01c ]))
        
       // $34.1 , $34.1 mil milhÃµes (aproveitar o NUMERO jÃ¡ detectado)
       rules.add(new Rule( id:"VALORMOEDA2", description:"\$1.000", 
       sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [ 
              ValueGazetteerPT.moneyNumber2_1c, 
              NEGazetteer.NE_NUMERO_01c]))

       // $ 34.1  (aproveitar o NUMERO jÃ¡ detectado)
       rules.add(new Rule( id:"VALORMOEDA2.5", description:"\$ 1.000", 
       sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
              CommonClauses.moneySymbols1c, 
              NEGazetteer.NE_NUMERO_1c ]))	       
	       
      // {VALOR-MOEDA} de? {currencyName}Â {pais}?
       rules.add(new Rule( id:"VALORMOEDA3", description:"{<MOEDA> de? XXX YYY?", 
       sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
	      NEGazetteer.NE_VALOR_MOEDA_1c, 
	      ClausesPT.de01c, 
	      ValueGazetteerPT.currency1c, 
	      LocalGazetteerPT.countryAdjective01c ]))

    // {NUMERO} de? {currencyName}Â {pais}?
       rules.add(new Rule( id:"VALORMOEDA3.5", description:"{<NUMERO> de? XXX YYY?", 
       sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
	      NEGazetteer.NE_NUMERO_1c, 
	      ClausesPT.de01c, 
	      ValueGazetteerPT.currency1c, 
	      LocalGazetteerPT.countryAdjective01c ]))	      

         // US|R {VALOR-MOEDA}
       rules.add(new Rule( id:"VALORMOEDA4", description:"{US|R! <NUMERO|MOEDA>!}", 
       sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [ 
              ValueGazetteerPT.currencyPrefix1c, 
              NEGazetteer.NE_NUMERO_VALOR_MOEDA_1c ]))
       
        // X de <VALOR> , x = cerca,acima,mais,menos,abaixo,aproximadamente 
	rules.add(new Rule( id:"VALORMOEDA5", description:"cerca de <MOEDA>",
	sc:SC.value_currency, policy:RulePolicy.Rule, clauses: [
	      ClausesPT.acimaabaixocerca1c, 
	      ClausesPT.de1c, 
	      NEGazetteer.NE_VALOR_MOEDA_1c ] ))   
	   
       /** VALOR QUANTIDADE */
 	
       // 10 watts   
       rules.add(new Rule( id:"VALORQUANTIDADE1", description:"{<NUM>! de? [units]! por|/? [porUnits]?}", 
       sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
              NEGazetteer.NE_NUMERO_1c, 
              ClausesPT.de01c, 
              ValueGazetteerPT.allUnits1c, 
              ClausesPT.porSlash01c,
              ValueGazetteerPT.porUnits01c ]))

        

        // 10%
        rules.add(new Rule( id:"VALORQUANTIDADE2", description:"XX%.?",
        sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
              ValueGazetteerPT.percNumber1c] ))   
 
	 // 10 %
	rules.add(new Rule( id:"VALORQUANTIDADE2.5", description:"XX %",
	sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
	      NEGazetteer.NE_NUMERO_1c, 
	      CommonClauses.percentage1c]  ))   
 
	// 10 por cento       
	rules.add(new Rule( id:"VALORQUANTIDADE5", description:"XX por cento",
	sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
	      NEGazetteer.NE_NUMERO_1c, 
	      ClausesPT.por1c,
	      Clause.newPlain1Clause("cento")]  ))   
 
	 // <VALOR> acima|abaixo      
	 rules.add(new Rule( id:"VALORQUANTIDADE6", description:"<VALOR> abaixo",
	 sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
	      NEGazetteer.NE_VALOR_QUANTIDADE_1c, 
	      ClausesPT.acimaabaixo1c]  ))   
 
	   // X de <VALOR> , x = cerca, acima,mais,menos,abaixo, aproximadamente
	 rules.add(new Rule( id:"VALORQUANTIDADE7", description:"cerca de <VALOR>",
	 sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
	      ClausesPT.acimaabaixocerca1c, 
	      ClausesPT.de01c, 
	      NEGazetteer.NE_VALOR_QUANTIDADE_1c] ))   

	     // temperaturas coladas, ex: 34ºC
	   rules.add(new Rule( id:"VALORQUANTIDADE8", description:"-?\\d[\\.\\d]*º[CFK]",
        sc:SC.value_quantity, policy:RulePolicy.Rule, clauses: [
            ValueGazetteerPT.temperature1c] ))   
        
  
    /**********************/       
    /* VALOR CLASSIFICACAO */
    /*********************/
            }
}