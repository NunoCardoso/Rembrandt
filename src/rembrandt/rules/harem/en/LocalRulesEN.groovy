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
import rembrandt.gazetteers.en.LocalGazetteerEN
import rembrandt.gazetteers.en.NumberGazetteerEN
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.Patterns
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector

/**
 * @author Nuno Cardoso
 * Rules for external evidence for LOCAL category.
 */
class LocalRulesEN extends NamedEntityDetector  {
   
    List<Rule> rules	
    SemanticClassification sc 
 /**   
  * Main constructor
  */
   public LocalRulesEN() {
	    
	rules = []
		
	/******* LOCAL HUMANO *******/
				 
	 /******* LOCAL HUMANO REGIAO ******/
		
	 // 1 [region|...]! of? {<EM>!} 
	 rules.add(new Rule(id:"LocalRulesEN LOCAL HUMANO REGIAO 1", description:"[region|...]! of? {<EM>!}",
	 sc:SC.place_human_humanregion, policy:RulePolicy.Clause, clauses:[ 
	             LocalGazetteerEN.humanregion1nc, 
	             ClausesEN.of01nc, 
	             NEGazetteer.NE_Anything_1c
	 ] ))				

	  // 2 {<EM>!} [region|...]! 
	 rules.add(new Rule(id:"LocalRulesEN LOCAL HUMANO REGIAO 2", description:"{<EM>!} [region|...]!",
	 sc:SC.place_human_humanregion, policy:RulePolicy.Clause, clauses:[ 
	             NEGazetteer.NE_Anything_1c,                                                                      
	             LocalGazetteerEN.humanregion1nc	            
	 ] ))				

	 /******* LOCAL HUMANO DIVISAO ******/
	
	 // 1 [city|...]! deaos01? {<EM>!} passa a [LOCAL HUMANO DIVISAO] 
	 rules.add(new Rule(id:"LocalRulesEN LOCAL HUMANO DIVISAO 1", description:"[city|...]! of? {<EM>!}",
	 sc:SC.place_human_division, policy:RulePolicy.Clause, clauses:[ 
	             LocalGazetteerEN.division1nc, 
	             ClausesEN.of01nc, 
	             NEGazetteer.NE_Anything_1c
	 ] ))				

	  // 2 {<EM>!} [city|...]! 
	 rules.add(new Rule(id:"LocalRulesEN LOCAL HUMANO DIVISAO 2", description:"{<EM>!} [city|...]!",
	 sc:SC.place_human_division, policy:RulePolicy.Clause, clauses:[ 
	             NEGazetteer.NE_Anything_1c,                                                                      
	             LocalGazetteerEN.division1nc	            
	 ] ))			 
		 
	 /******* LOCAL HUMANO CONSTRUCAO ******/
    
	 // 1 [bridge|...]! of? {<EM>!} passa a [LOCAL HUMANO CONSTRUCAO] 
	 rules.add(new Rule(id:"LocalRulesEN LOCAL HUMANO CONSTRUCAO 1", description:"[bridge|...]! of? {<EM>!}",
	 sc:SC.place_human_construction, policy:RulePolicy.Clause, clauses:[ 
	             LocalGazetteerEN.construction1nc, 
	             ClausesEN.of01nc, 
	             NEGazetteer.NE_Anything_1c
	 ] ))				
			
		     
	 // 2 {<EM>!} [bridge|...]! passa a [LOCAL HUMANO CONSTRUCAO] 
	 rules.add(new Rule(id:"LocalRulesEN LOCAL HUMANO CONSTRUCAO 2", description:"{<EM>!} [bridge|...]!",
	 sc:SC.place_human_construction, policy:RulePolicy.Clause, clauses:[ 
	             NEGazetteer.NE_Anything_1c,
	             LocalGazetteerEN.construction1nc
	 ] ))				
					 
	 /******* LOCAL HUMANO RUA ******/
	
	 // 1 [street|...]! of? {<EM>!} passa a [LOCAL HUMANO RUA] 
	 rules.add(new Rule(id:"LocalRulesEN LOCAL HUMANO RUA 1", description:"[street|...]! of? {<EM>!}",
	 sc:SC.place_human_street, policy:RulePolicy.Clause, clauses:[ 
	             LocalGazetteerEN.street1nc, 
	             ClausesEN.of01nc, 
	             NEGazetteer.NE_Anything_1c
	 ] ))		    
	    
	// 2 {<EM>!} [street|...]! passa a [LOCAL HUMANO RUA] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL HUMANO RUA 2", description:"{<EM>!} [street|...]!",
	sc:SC.place_human_street, policy:RulePolicy.Clause, clauses:[ 
	             NEGazetteer.NE_Anything_1c,
	             LocalGazetteerEN.street1nc
	] ))
		
	/******* LOCAL FISICO  ******/
	
	/******* LOCAL FISICO AGUACURSO ******/
			
	// 1 [river|...]! of? {<EM>!} passa a [LOCAL FISICO AGUACURSO] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL FISICO AGUACURSO 1", description:"[river|...]! of? {<EM>!}",
	sc:SC.place_physical_watercourse, policy:RulePolicy.Clause, clauses:[ 
	             LocalGazetteerEN.waterCourse1nc,
	             ClausesEN.of01nc,
	             NEGazetteer.NE_Anything_1c
	] ))	
		
	// 2 {<EM>!} [river|...]! passa a [LOCAL FISICO AGUACURSO] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL FISICO AGUACURSO 2", description:"{<EM>!} [river|...]!",
	sc:SC.place_physical_watercourse, policy:RulePolicy.Clause, clauses:[ 
	             NEGazetteer.NE_Anything_1c,
	             LocalGazetteerEN.waterCourse1nc
	] ))
		
	/******* LOCAL FISICO AGUACURSO ******/
	
	// 1 [sea|...]! of? {<EM>!} passa a [LOCAL FISICO AGUAMASSA] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL FISICO AGUAMASSA 1", description:"[sea|...]! of? {<EM>!}",
	sc:SC.place_physical_watermass, policy:RulePolicy.Clause, clauses:[ 
	             LocalGazetteerEN.waterMass1nc,
	             ClausesEN.of01nc, 
	             NEGazetteer.NE_Anything_1c
	] ))				

	// 2 {<EM>!} [sea|...]! passa a [LOCAL FISICO AGUAMASSA] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL FISICO AGUAMASSA 2", description:"{<EM>!} [sea|...]!",
	sc:SC.place_physical_watermass, policy:RulePolicy.Clause, clauses:[ 
	             NEGazetteer.NE_Anything_1c, 
		     LocalGazetteerEN.waterMass1nc
	] ))			
		
	/******* LOCAL FISICO RELEVO ******/
		
	// 1 [mountain|...]! of? {<EM>!} passa a [LOCAL FISICO RELEVO] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL FISICO RELEVO 1", description:"[mountain|...]! of? {<EM>!}",
	sc:SC.place_physical_mountain, policy:RulePolicy.Clause, clauses:[
	             LocalGazetteerEN.mountain1nc, 
	             ClausesEN.of01nc, 
	             NEGazetteer.NE_Anything_1c
	] ))				
	    
	// 2 {<EM>!} [mountain|...]! passa a [LOCAL FISICO RELEVO] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL FISICO RELEVO 2", description:"{<EM>!} [mountain|...]!",
	sc:SC.place_physical_mountain, policy:RulePolicy.Clause, clauses:[
	             NEGazetteer.NE_Anything_1c,
	             LocalGazetteerEN.mountain1nc		            
	] ))	
		
	/******* LOCAL FISICO PLANETA ******/
		
	// 1 [planet|...]! of? {<EM>!} passa a [LOCAL FISICO PLANETA] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL FISICO PLANETA 1", description:"[planet|...]! of? {<EM>!}",
	sc:SC.place_physical_planet, policy:RulePolicy.Clause, clauses:[
	             LocalGazetteerEN.planet1nc, 
	             ClausesEN.of01nc, 
	             NEGazetteer.NE_Anything_1c
	] ))				

	// 2 {<EM>!} [planet|...]! passa a [LOCAL FISICO PLANETA] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL FISICO PLANETA 2", description:"{<EM>!} [planet|...]!",
	sc:SC.place_physical_planet, policy:RulePolicy.Clause, clauses:[
	             NEGazetteer.NE_Anything_1c,
	             LocalGazetteerEN.planet1nc	            
	] ))			
		
	/******* LOCAL FISICO ILHA ******/
		
	// 1 [island|...]! of? {<EM>!} passa a [LOCAL FISICO ILHA] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL FISICO ILHA 1", description:"[island|...]! of? {<EM>!}",
	sc:SC.place_physical_island, policy:RulePolicy.Clause, clauses:[
	             LocalGazetteerEN.island1nc, 
	             ClausesEN.of01nc, 
	             NEGazetteer.NE_Anything_1c
	] ))				
		 
	// 2 {<EM>!} [island|...]! passa a [LOCAL FISICO ILHA] 
	rules.add(new Rule(id:"LocalRulesEN LOCAL FISICO ILHA 2", description:"{<EM>!} [island|...]!",
	sc:SC.place_physical_island, policy:RulePolicy.Clause, clauses:[
	             NEGazetteer.NE_Anything_1c, 
	             LocalGazetteerEN.island1nc 	            
	] ))
		
				
			 			 
	/******* LOCAL VIRTUAL ******/
		
	/******* LOCAL VIRTUAL SITIO ******/		
		
		
	// 1 [website|...]! of? {<EM>!}
	rules.add(new Rule(id:"LocalRulesEN LOCAL VIRTUAL SITIO 1", description:"[website|...]! of? {<EM>!}",
	sc:SC.place_virtual_site, policy:RulePolicy.Clause, clauses:[ 
	             LocalGazetteerEN.site1nc, 
	             ClausesEN.of01nc, 
	             NEGazetteer.NE_Anything_Except_NUMERO_1c
	] ))				

	// 2 {<EM>!} [website|...]!
	rules.add(new Rule(id:"LocalRulesEN LOCAL VIRTUAL SITIO 2", description:"{<EM>!} [website|...]!",
	sc:SC.place_virtual_site, policy:RulePolicy.Clause, clauses:[ 
	             NEGazetteer.NE_Anything_Except_NUMERO_1c, 
	             LocalGazetteerEN.site1nc	             
	] ))				

	/******* LOCAL VIRTUAL COMSOCIAL ******/		
		
	// 1 [magazine|...]! {<EM>!}
	rules.add(new Rule(id:"LocalRulesEN LOCAL VIRTUAL COMSOCIAL 1", description:"[magazine|...]! {<EM>!}",
	sc:SC.place_virtual_media, policy:RulePolicy.Clause, clauses:[ 
	              LocalGazetteerEN.media1nc, 
	              NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c
	] ))				
		
	// 2 {<EM>!} [magazine|...]! 
	rules.add(new Rule(id:"LocalRulesEN LOCAL VIRTUAL COMSOCIAL 2", description:"{<EM>!} [magazine|...]!",
	sc:SC.place_virtual_media, policy:RulePolicy.Clause, clauses:[ 		            
	             NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c,  
	             LocalGazetteerEN.media1nc, 
	] ))	
   }
}