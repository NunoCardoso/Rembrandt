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

import rembrandt.obj.Rule
import rembrandt.obj.RulePolicy
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.pt.LocalGazetteerPT
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector

/**
 * @author Nuno Cardoso
 * Rules for external evidence for LOCAL category.
 */
class LocalRulesPT extends NamedEntityDetector {
   
    static List<Rule> rules	
 
 /**   
  * Main constructor
  */
    public LocalRulesPT() {
	    
	rules = []
		
	/******* LOCAL HUMANO *******/
		 
	/******* LOCAL HUMANO REGIAO ******/
			 		
	// 1 [regiao]! deaos? {<EM>!} passa a [LOCAL HUMANO REGIAO]   
	rules.add(new Rule(id:"LocalRulesPT LOCAL HUMANO REGIAO 1", description:"[regiao]! deaos? {<EM>!}", 
	  sc:SC.place_human_humanregion, policy:RulePolicy.Clause, clauses:[ 
		 LocalGazetteerPT.regionPrefix1nc, 
		 ClausesPT.daeos01nc, 
		 NEGazetteer.NE_Anything_1c] ))				

	/******* LOCAL HUMANO DIVISAO ******/
		 
	// 2 [cidade]! deaos? {<EM>!} passa a [LOCAL HUMANO DIVISAO] 
	rules.add(new Rule(id:"LocalRulesPT LOCAL HUMANO DIVISAO 1", description:"[cidade] deaos? {<EM>!}", 
	   sc:SC.place_human_division, policy:RulePolicy.Clause, clauses:[ 
		 LocalGazetteerPT.divisionPrefix1nc, 
		 ClausesPT.daeos01nc, 
		 NEGazetteer.NE_Anything_1c] ))				
						
	/******* LOCAL HUMANO CONSTRUCAO ******/
		     
	// 3 [ponte]! deaos01? {<EM>!} passa a [LOCAL HUMANO CONSTRUCAO] 
	rules.add(new Rule(id:"LocalRulesPT LOCAL HUMANO CONSTRUCAO 1", 
	description:"[ponte] deaos? {<EM>!}", sc:SC.place_human_construction, policy:RulePolicy.Clause, clauses:[ 
		 LocalGazetteerPT.constructionPrefix1nc, 
		 ClausesPT.daeos01nc, 
		 NEGazetteer.NE_Anything_1c] ))				
			
	/******* LOCAL HUMANO RUA ******/
	     
	// 4 [rua|travessa|praça]! deaos? {<EM>!} passa a [LOCAL HUMANO RUA] 
	rules.add(new Rule(id:"LocalRulesPT LOCAL HUMANO RUA 1", 
	description:"[rua|travessa|praça] deaos? {<EM>!}", sc:SC.place_human_street, policy:RulePolicy.Clause, clauses:[ 
		LocalGazetteerPT.streetPrefix1nc, 
		ClausesPT.daeos01nc, 
		NEGazetteer.NE_Anything_1c] ))				

	/******* LOCAL FISICO  ******/
	
	/******* LOCAL FISICO AGUACURSO ******/
		    		
	// 5 [rio]! deaos? {<EM>!} passa a [LOCAL FISICO AGUACURSO] 
	rules.add(new Rule(id:"LocalRulesPT LOCAL FISICO AGUACURSO 1", 
	description:"[rio]! deaos? {<EM>!}", sc:SC.place_physical_watercourse, policy:RulePolicy.Clause, clauses:[ 
	         LocalGazetteerPT.waterCoursePrefix1nc, 
	         ClausesPT.daeos01nc, 
	         NEGazetteer.NE_Anything_1c] ))				

	/******* LOCAL FISICO AGUACURSO ******/
		    		
	// 6 [mar]! deaos01? {<EM>!} passa a [LOCAL FISICO AGUAMASSA] 
	rules.add(new Rule(id:"LocalRulesPT LOCAL FISICO AGUAMASSA 1", 
	description:"[mar]! deaos? {<EM>!}", sc:SC.place_physical_watermass, policy:RulePolicy.Clause, clauses:[ 
		LocalGazetteerPT.waterMassPrefix1nc, 
		ClausesPT.daeos01nc, 
		NEGazetteer.NE_Anything_1c] ))				

	/******* LOCAL FISICO RELEVO ******/
		    		
	// 7 [serra]! deaos? {<EM>!} passa a [LOCAL FISICO RELEVO] 
	rules.add(new Rule(id:"LocalRulesPT LOCAL FISICO RELEVO 1", 
	description:"[serra]! deaos? {<EM>!}",sc:SC.place_physical_mountain, policy:RulePolicy.Clause, clauses:[
		LocalGazetteerPT.mountainPrefix1nc, 
		ClausesPT.daeos01nc, 
		NEGazetteer.NE_Anything_1c] ))				

	/******* LOCAL FISICO PLANETA ******/
		
	// 8 [planeta]! deaos? {<EM>!} passa a [LOCAL FISICO PLANETA] 
	rules.add(new Rule(id:"LocalRulesPT LOCAL FISICO PLANETA 1", 
	description:"[planeta]! deaos? {<EM>!}", sc:SC.place_physical_planet, policy:RulePolicy.Clause, clauses:[
	         LocalGazetteerPT.planetPrefix1nc, 
	         ClausesPT.daeos01nc, 
	         NEGazetteer.NE_Anything_1c] ))				

	/******* LOCAL FISICO ILHA ******/
		
	// 9 [ilha]! deaos? {<EM>!} passa a [LOCAL FISICO ILHA] 
	rules.add(new Rule(id:"LocalRulesPT LOCAL FISICO ILHA 1", 
	description:"[ilha]! deaos? {<EM>!}", sc:SC.place_physical_island, policy:RulePolicy.Clause, clauses:[
	         LocalGazetteerPT.islandPrefix1nc, 
	         ClausesPT.daeos01nc, 
	         NEGazetteer.NE_Anything_1c] ))				
		 
	/******* LOCAL FISICO REGIAO ******/
		    
	// 10 [estreito]! deaos? {<EM>!} passa a [LOCAL FISICO REGIAO] 
	//rules.add(new Rule(id:"LocalRulesPT LOCAL FISICO REGIAO 1", 
	//description:"[estreito]! deaos? {<EM>!}", sc:SC.place_physical_physicalregion, policy:RulePolicy.Clause, clauses:[
	//	LocalGazetteerPT.regionPrefix1nc, 
	//	ClausesPT.daeos01nc, 
	//	NEGazetteer.NE_Anything_Except_NUMERO_1c] ))				
			 
	/******* LOCAL VIRTUAL ******/
				
	/******* LOCAL VIRTUAL SITIO ******/		
				
	// 11 [site|p√°gina]! d[eoa]s? {<EM>!}
	rules.add(new Rule(id:"LocalRulesPT LOCAL VIRTUAL SITIO 1", 
	description:"[site|página]! d[eoa]s? {<EM>!}", sc:SC.place_virtual_site,  policy:RulePolicy.Clause, clauses:[ 
		LocalGazetteerPT.site1nc, 
		ClausesPT.daeos01nc, 
		NEGazetteer.NE_Anything_Except_NUMERO_1c] ))				

	/******* LOCAL VIRTUAL COMSOCIAL ******/		
		
	// 12 [art|publi|lei|revista|etc] d[eoa]s? {<EM>!}
	rules.add(new Rule(id:"LocalRulesPT LOCAL VIRTUAL COMSOCIAL 1", 
	description:"[art|publi|lei|revista|etc] num.º? {<EM>!}", sc:SC.place_virtual_media,  policy:RulePolicy.Clause, clauses:[ 
	        LocalGazetteerPT.media1nc,  
	        ClausesPT.daeos01nc, 
	        NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] ))				
	}
}