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
import rembrandt.gazetteers.pt.NumberGazetteerPT
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector
/**
 * @author Nuno Cardoso
 * Rules for capturing numeric expressions 
 */
class NumberRulesPT extends NamedEntityDetector {

    List<Rule> rules

    /**
     * Main constructor
     */    
    public NumberRulesPT() {
	
	rules = []
	 	  
        /* Algarismos ordinais: 34º */       
	rules.add(new Rule(id:"NumberRulesPT ORDINAL 1", description:"{34oa}",
	sc:SC.number_ordinal, policy:RulePolicy.Rule, clauses: [ 
	         NumberGazetteerPT.ordinalNumber1c] ))    
	 		 	  
        /* Algarismos. Exemplo: 1  34  32,9  1.000 {32 000} */       
        rules.add(new Rule(id:"NumberRulesPT NUMERAL 1",description:"-?\\d{1,}!",
        sc:SC.number_numeral, policy:RulePolicy.Rule, clauses: [ 
                  NumberGazetteerPT.digit1Pc ] ))    
        
        /* Nomes de números. Example: três, vinte */
        rules.add(new Rule(id:"NumberRulesPT TEXTUAL 1",description:"{[tres|vinte|...]!}",
        sc:SC.number_textual, policy:RulePolicy.Rule, clauses: [ 
                   NumberGazetteerPT.textNumber1c ]))
                
                  
        // Nomes compostos de números até três. Exemplo: vinte e três, cento e três, cento e vinte e três
        rules.add(new Rule(id:"NumberRulesPT TEXTUAL 2",description:"nomeNumero! e! NomeNumero! e? NomeNumero?",
        sc:SC.number_textual, policy:RulePolicy.Rule, clauses: [ 
        	   NEGazetteer.NE_NUMERO_TEXTUAL_1c, ClausesPT.e1c, 
                   NEGazetteer.NE_NUMERO_TEXTUAL2_1c, ClausesPT.e01c, 
                   NEGazetteer.NE_NUMERO_TEXTUAL_01c ]))
                    
        // Número! + [ mil | milhões | etc]+ ex: 3 mil milhões 
        rules.add(new Rule(id:"NUMERO4",description:"{<NUM>! [mil|milhões|...]!+}",
        sc:SC.number_textual, policy:RulePolicy.Rule, clauses: [
                   NEGazetteer.NE_NUMERO_1c, 
                   NumberGazetteerPT.hundredsTextNumber1Pc ]))    
                 
       //  [ mil | milhões | etc]+ ex:  mil milhões 
       // dividi com NUMERO 4 para ter cláusulas mandatórias no início, e facilitar a optimização
        rules.add(new Rule(id:"NUMERO4.1",description:"{[mil|milhões|...]!+}",
        sc:SC.number_textual, policy:RulePolicy.Rule, clauses: [
                   NumberGazetteerPT.hundredsTextNumber1Pc ]))    
   
                   //entre <NUM> a <NUM>
        rules.add(new Rule(id:"NUMERO6",description:"{entre! aos? <NUM>! e|a! aos? <NUM>!}",
        sc:SC.number, policy:RulePolicy.Rule, clauses: [
        	  ClausesPT.entre1c, 
        	  ClausesPT.aos01c, 
                  NEGazetteer.NE_NUMERO_1c, 
                  ClausesPT.ae1c, 
                  ClausesPT.aos01c_duplicate, 
                  NEGazetteer.NE_NUMERO2_1c]))  
          
      
 
    } 
}