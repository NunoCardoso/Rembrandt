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

import org.apache.log4j.Logger
import rembrandt.obj.Rule
import rembrandt.obj.NamedEntity
import rembrandt.obj.ConflictPolicy
import rembrandt.obj.Criteria
import rembrandt.obj.ListOfNE
import rembrandt.gazetteers.CommonClassifications as SC
import static rembrandt.obj.ClassificationCriteria.*
import rembrandt.rules.SplitNEMatcherObject
import rembrandt.rules.NamedEntityDetector
import rembrandt.rules.SplitNEDetector
import rembrandt.gazetteers.CommonClauses as CC
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes 
import saskia.bin.AskSaskia
import rembrandt.gazetteers.pt.PersonGazetteerPT

/**
 * @author Nuno Cardoso
 * Rules for splitting NEs
 */
class SplitNERulesPT extends SplitNEDetector {
 
	static String lang = "pt"	
	List<Rule> rules
	static Logger log = Logger.getLogger("SplitNEDetector")

	public SplitNERulesPT(NamedEntityDetector ierules, AskSaskia saskia) {
		super(ierules, saskia)
		rules = [

		// 1. {NE} gluer {NE} 
		new Rule(id:"Split NE rule 1", description:"{NE} gluer {NE}",
			clauses:[//CC.beginSentence1, 
	         CC.notGluer1_1Pc['pt'],
	         CC.gluer1_1c['pt'],
	         CC.notGluer2_1Pc['pt'],         
	         CC.endSentence1], 
			action:[
				{SplitNEMatcherObject o, ListOfNE NEs -> o.lang=lang}, 
	         capture_NE_o_NE, 
	         {SplitNEMatcherObject o, ListOfNE NEs -> 
            
			log.debug("Rule Split NE rule 1 matched. NEs splitted: ${o.saskia_nes}")
		 
		/** 1.1 The Main NE has a known classification, and the rightmost subNE is a Place
		* ACTION: Add the rightmost NE as an ALT
		* Exceptions: Main NE is a PESSOA, otherwise <PESSOA>Faria de Guimarães</PESSOA> 
		* gives <LOCAL>Guimarães</LOCAL>
		* Main NE is a TEMPO, there is a town called Anos in France, gives 
		* <ALT><T>10 milhões de anos</T>|10 milhõµes de <LOCAL>anos</L></ALT>
		*/
			if (!o.original_ne.hasUnknownClassification() &&  
				o.original_ne.matchesClassification([SC.person, SC.time, SC.value], 
					[NeverExistsAtLeastOneOfThese, 
					ExistsAtLeastOneOfThem, 
					rembrandt.obj.ClassificationCriteria.Category]) && // conflicts with groovy.lang.Category
				o.saskia_nes[0]?.matchesClassification([SC.place], 
					[NeverExistsAtLeastOneOfThese, 
					AllOfThem, 
					rembrandt.obj.ClassificationCriteria.Category]) && 
				o.saskia_nes[1]?.matchesClassification([SC.place], 
					[AllOfThese, 
					AllOfThem, 
					rembrandt.obj.ClassificationCriteria.Category])) {

			// there's a ListOfNE called NEs where I can add the split. 
			log.debug("Rule SplitNERulesPT 1.1 with action Generate ALT performed. \
				include NE ${o.saskia_nes[1]}")
			//	println "Rule 1.1 in action"
			NEs.generateALT(o.original_ne, [o.saskia_nes[1]], 
				"RULE: SplitNERulesPT 1.1 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")		
		 		}
	 
		/** 1.2 The Main NE has an unknown classification, splitter is 'e' and the 
		* subNEs have classifications
		* EXAMPLE: <EM>Portugal e Espanha</EM> -> <LOCAL>Portugal</L> e <LOCAL>Espanha</L>
		*         <EM>PIS e Cofins</EM> -> <PLANO>PIS</L> e <PLANO>Cofins</L>    	
		* ACTION: Remove original NE, add subNEs
		*/	 
	 		else if (o.original_ne.hasUnknownClassification() &&      
				!o.saskia_nes[0]?.hasUnknownClassification() && 
				!o.saskia_nes[1]?.hasUnknownClassification() && 
				o.getMatchByClause(CC.gluer1_1c['pt']).terms?.text.equals(["e"]) ) {	     

			log.debug("Rule SplitNERulesPT 1.2 with action Replaced ${o.original_ne} \
				with ${o.saskia_nes}")
			
			NEs.removeNEs(o.original_ne)
			NEs.addNEs(o.saskia_nes, ConflictPolicy.JustAdd, 
				"RULE: SplitNERulesPT 1.2 ACTION: added from splitted NE ${o.original_ne}")
				}

	 /** 1.3 The main NE is a position, the rightmost NE is a place or organization 
	  * EXAMPLE: <EM> Presidente da Bolívia</EM>, <EM>Presidente da UNICER</EM>
	  * ACTION: Generate ALT for the rightmost NE
	  */
	 		else if (o.original_ne.matchesClassification([SC.person_position], 
				[ExistsAtLeastOneOfThese, 
				AllOfThem,
				Type]) && 
					o.saskia_nes[1]?.matchesClassification([SC.place, SC.organization], 
				[ExistsAtLeastOneOfThese, 
				ExistsAtLeastOneOfThem,  
				rembrandt.obj.ClassificationCriteria.Category]) ) {			   

			log.debug("Rule SplitNERulesPT 1.3 with action Gerenate ALT, include \
				NE ${o.saskia_nes[1]}")
			NEs.generateALT(o.original_ne, [o.saskia_nes[1]], 
				"RULE: SplitNERulesPT 1.3 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")	    
	 }    
        	
	/** 1.4 Check for Persons glued by "e" 
	 *  EXAMPLE: <EM>George Bush e Boris Ieltsin</EM> 
	 *  ACTION: simple split, remove main NE, add two subNEs
	 *  EXCEPTIONS: Note names like  'José Ribeiro e Costa'. Split only for 
	 * subNEs that are grounded.
	 */
	 else if (o.original_ne.hasUnknownClassification() && 
	o.getMatchByClause(CC.gluer1_1c['pt']).terms?.text.equals(["e"])) { 
	
		boolean tosplit = false
				
		if (o.saskia_nes[0]?.matchesClassification(
			[SC.person_individual], 
			[AllOfThese, AllOfThem,  
			rembrandt.obj.ClassificationCriteria.Type]) && 
			o.saskia_nes[1]?.matchesClassification(
			[SC.person_individual],
			[AllOfThese, AllOfThem, 
			rembrandt.obj.ClassificationCriteria.Type]) ) {

			log.debug("Rule SplitNERulesPT 1.4.1, both grounded Persons matched.")
						
			tosplit = true	
				
		} else if (o.saskia_nes[0]?.matchesClassification(
			[SC.person_individual], 
			[AllOfThese, AllOfThem,  
			rembrandt.obj.ClassificationCriteria.Type]) && 
			o.saskia_nes[1]?.hasUnknownClassification() ){
					
			log.debug("Rule SplitNERulesPT 1.4.2, grounded Person + ungrounded Person matched.")

			int nameTerms = 0
			o.saskia_nes[1].terms?.each{term -> 
	      if (PersonGazetteerPT.firstName.contains(term.text) ||
	      PersonGazetteerPT.lastName.contains(term.text)) {nameTerms++}
      	}

	    	if (o.saskia_nes[1].terms && 
				((nameTerms * 1.0)/(o.saskia_nes[1].terms.size() * 1.0)) > 0.49) {
				o.saskia_nes[1].classification = [SC.person_individual]
				tosplit = true
			}	
		} else if (o.saskia_nes[1]?.matchesClassification(
			[SC.person_individual], 
			[AllOfThese, AllOfThem,  
			rembrandt.obj.ClassificationCriteria.Type]) && 
			o.saskia_nes[0]?.hasUnknownClassification() ){

			log.debug("Rule SplitNERulesPT 1.4.3, ungrounded Person + grounded Person matched.")

			int nameTerms = 0
			o.saskia_nes[0].terms?.each{term -> 
	      if (PersonGazetteerPT.firstName.contains(term.text) ||
	      PersonGazetteerPT.lastName.contains(term.text)) {nameTerms++}
      	}
 	    	if (o.saskia_nes[0].terms && 
				((nameTerms * 1.0)/(o.saskia_nes[0].terms.size() * 1.0)) > 0.49) {
				o.saskia_nes[0].classification = [SC.person_individual]
				tosplit = true
			}	
		} else if (o.saskia_nes[0]?.hasUnknownClassification() && 
			o.saskia_nes[1]?.hasUnknownClassification() ) {

			log.debug("Rule SplitNERulesPT 1.4.4, 2x ungrounded Persons matched.")
				
			int nameTerms0 = 0
			o.saskia_nes[0].terms?.each{term -> 
	      	if (PersonGazetteerPT.firstName.contains(term.text) ||
	      	PersonGazetteerPT.lastName.contains(term.text)) {nameTerms0++}
      	}

			int nameTerms1 = 0
			o.saskia_nes[1].terms?.each{term -> 
	      	if (PersonGazetteerPT.firstName.contains(term.text) ||
	      	PersonGazetteerPT.lastName.contains(term.text)) {nameTerms1++}
      	}

 	    	if ( (o.saskia_nes[0].terms && 
				((nameTerms0 * 1.0)/(o.saskia_nes[0].terms.size() * 1.0)) > 0.49)  &&  
				( (o.saskia_nes[1].terms && 
				((nameTerms1 * 1.0)/(o.saskia_nes[1].terms.size() * 1.0)) > 0.49) ) ) {
				o.saskia_nes[0].classification = [SC.person_individual]
				o.saskia_nes[1].classification = [SC.person_individual]
				tosplit = true
			
			}
		}
		if (tosplit) {
					
		log.debug("Rule SplitNERulesPT 1.4 with action Replace NE \
			${o.original_ne} with ${o.saskia_nes}")

		NEs.removeNEs(o.original_ne)	
		NEs.addNEs(o.saskia_nes, ConflictPolicy.JustAdd, 
			"RULE: SplitNERulesPT 1.4 ACTION: added from splitted NE ${o.original_ne}")
			}		 
		}   	
        
	 	return true
	
	 } ] ) ,
	 
	 new Rule(id:"Split NE rule 2A", description:"{NE} gluer {NE gluer NE}",
	 clauses:[//CC.beginSentence1, 
	          CC.notGluer1_1Pc['pt'],
	          CC.gluer1_1c['pt'],
	          CC.notGluer2_1Pc['pt'],         
	          CC.gluer2_1c['pt'],
	          CC.notGluer3_1Pc['pt'],         
	          CC.endSentence1], 		 
	 action:[{SplitNEMatcherObject o, ListOfNE NEs  -> o.lang=lang}, 
	         capture_NE_o_NEoNE, 
	         {SplitNEMatcherObject o, ListOfNE NEs -> 
	 
		log.debug("Rule Split NE rule 2A matched. NEs splitted: ${o.saskia_nes}")

	   /** 2A.1 - get a place from a organization. 
	    * EXAMPLE: Câmara Municipal de Ribeira de Pena
	    * ACTION: Generate ALT
	    */
	    if (o.original_ne.matchesClassification([SC.organization],  [AllOfThese, AllOfThem,  rembrandt.obj.ClassificationCriteria.Category]) &&
		o.saskia_nes[1]?.matchesClassification([SC.place],  [AllOfThese, AllOfThem,  rembrandt.obj.ClassificationCriteria.Category]) ) {
			// there's a ListOfNE called NEs where I can add the split. 

		log.debug("Rule SplitNERulesPT 2A.1 with action Generate ALT performed. \
				include NE ${o.saskia_nes[1]}")

		NEs.generateALT(o.original_ne, [o.saskia_nes[1]], 
			"RULE: SplitNERulesPT 2A.1 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")		
	   }
	    
	    /** 2A.2 - get a place from a building. 
	     * EXAMPLE: Museu de Ribeira de Pena
	     * ACTION: Generate ALT
	     */
	    if (o.original_ne.matchesClassification(
			[SC.place_human_construction], 
			[ExistsAtLeastOneOfThese, AllOfThem, Subtype]) &&
		o.saskia_nes[1]?.matchesClassification(
			[SC.place], 
			[AllOfThese, AllOfThem,  
			rembrandt.obj.ClassificationCriteria.Category]) ) {
		// there's a ListOfNE called NEs where I can add the split. 

			log.debug("Rule SplitNERulesPT 2A.2 with action Generate ALT performed. \
				include NE ${o.saskia_nes[1]}")

			NEs.generateALT(o.original_ne, [o.saskia_nes[1]], 
				"RULE: SplitNERulesPT 2A.2 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")			
	    }
	
		  /** 2A.3 - persons 
	     * EXAMPLE: Francisco Lopes e Jerónimo de Sousa
	     * ACTION: replace EM para PESSOA
	     */
		   if (o.original_ne.hasUnknownClassification()) {
			
				// temos de verificar os casos onde: 
				// 2A.3.1 os dois tem DBpedia e sáo PESSOA
				boolean tosplit = false
				
				if (o.saskia_nes[0]?.matchesClassification(
					[SC.person], 
					[AllOfThese, AllOfThem,  
					rembrandt.obj.ClassificationCriteria.Category]) && 
				o.saskia_nes[1]?.matchesClassification(
					[SC.person],
					[AllOfThese, AllOfThem, 
					rembrandt.obj.ClassificationCriteria.Category]) ) {

					log.debug("Rule SplitNERulesPT 2A.3.1, both grounded Persons matched.")
						
					tosplit = true	
				
				// 2A.3.2 um deles não tem
				} else if (o.saskia_nes[0]?.matchesClassification(
					[SC.person], 
					[AllOfThese, AllOfThem,  
					rembrandt.obj.ClassificationCriteria.Category]) && 
					o.saskia_nes[1]?.hasUnknownClassification() ){
					
					log.debug("Rule SplitNERulesPT 2A.3.2, grounded Person + ungrounded Person matched.")

					int nameTerms = 0
					o.saskia_nes[1].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms++}
      	   	}

	    	    	if (o.saskia_nes[1].terms && 
						((nameTerms * 1.0)/(o.saskia_nes[1].terms.size() * 1.0)) > 0.49) {
						
						o.saskia_nes[1].classification = [SC.person_individual]
						tosplit = true
					}	
				} else if (o.saskia_nes[1]?.matchesClassification(
					[SC.person], 
					[AllOfThese, AllOfThem,  
					rembrandt.obj.ClassificationCriteria.Category]) && 
					o.saskia_nes[0]?.hasUnknownClassification() ){

					log.debug("Rule SplitNERulesPT 2A.3.3, ungrounded Person + grounded Person matched.")

					int nameTerms = 0
					o.saskia_nes[0].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms++}
      	   	}


	    	    	if (o.saskia_nes[0].terms && 
						((nameTerms * 1.0)/(o.saskia_nes[0].terms.size() * 1.0)) > 0.49) {
						o.saskia_nes[0].classification = [SC.person_individual]
						tosplit = true
					}	
					
				} else if (o.saskia_nes[0]?.hasUnknownClassification() && 
				o.saskia_nes[1]?.hasUnknownClassification() ) {

					log.debug("Rule SplitNERulesPT 2A.3.4, 2x ungrounded Persons matched.")
				
					int nameTerms0 = 0
					o.saskia_nes[0].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms0++}
      	   	}

					int nameTerms1 = 0
					o.saskia_nes[1].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms1++}
      	   	}


	    	    	if ( (o.saskia_nes[0].terms && 
						((nameTerms0 * 1.0)/(o.saskia_nes[0].terms.size() * 1.0)) > 0.49)  &&  
						( (o.saskia_nes[1].terms && 
						((nameTerms1 * 1.0)/(o.saskia_nes[1].terms.size() * 1.0)) > 0.49) ) ) {
						o.saskia_nes[0].classification = [SC.person_individual]
						o.saskia_nes[1].classification = [SC.person_individual]
						tosplit = true
					

					}
				}
		// 2. só um tem DBpedia e é pessoa, o outro tem de se ver pelos nomes
		// 3. nenhum tem DBpedia, mas são padrões ...

				if (tosplit) {
					log.debug("Rule SplitNERulesPT 2A.3 with action Replace ${o.original_ne} \
				with NEs ${o.saskia_nes}")

	        		NEs.removeNEs(o.original_ne)
	        		NEs.addNEs(o.saskia_nes, ConflictPolicy.JustAdd, 
		"RULE: SplitNERulesPT 2A.3 ACTION: added from splitted NE ${o.original_ne}")	
				}	
	    }
	    return true
	   } ] ) ,
	 
	 new Rule(id:"Split NE rule 2B", description:"{NE gluer NE} gluer {NE}",
	 clauses:[//CC.beginSentence1, 
	          CC.notGluer1_1Pc['pt'],
	          CC.gluer1_1c['pt'],
	          CC.notGluer2_1Pc['pt'],         
	          CC.gluer2_1c['pt'],
	          CC.notGluer3_1Pc['pt'],         
	          CC.endSentence1], 
	 action:[{SplitNEMatcherObject o, ListOfNE NEs  -> o.lang=lang}, 
	         capture_NEoNE_o_NE, 
	         {SplitNEMatcherObject o, ListOfNE NEs  -> 
	 
		log.debug("Rule Split NE rule 2B matched. NEs splitted: ${o.saskia_nes}")

	   /** 2B.1 - get a place from a organization. 
	    * EXAMPLE: Academia de Belas-Artes de Viena
	    * ACTION: Generate ALT
	    */
	    if (o.original_ne.matchesClassification(
			[SC.organization], 
			[AllOfThese, AllOfThem,  
			rembrandt.obj.ClassificationCriteria.Category]) &&
			 o.saskia_nes[1]?.matchesClassification(
				[SC.place],
				[AllOfThese, AllOfThem,  
				rembrandt.obj.ClassificationCriteria.Category]) ) {

			log.debug("Rule SplitNERulesPT 2B.1 with action Generate ALT performed. \
				include NE ${o.saskia_nes[1]}")
				
			NEs.generateALT(o.original_ne, [o.saskia_nes[1]], 
				"RULE: SplitNERulesPT 2B.1 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")			
	   }
	    
	    /** 2B.2 - 
	     * EXAMPLE: Declaração de Consenso de Atlanta - OBRA/PLANO
	     * ACTION: Generate ALT
	     */
	    if (o.original_ne.matchesClassification(
			[SC.masterpiece_plan], 
			[AllOfThese, AllOfThem, Type]) &&
			o.saskia_nes[1]?.matchesClassification(
				[SC.place],
				[AllOfThese, AllOfThem,  
				rembrandt.obj.ClassificationCriteria.Category]) ) {

			log.debug("Rule SplitNERulesPT 2B.2 with action Generate ALT performed. \
				include NE ${o.saskia_nes[1]}")

			NEs.generateALT(o.original_ne, [o.saskia_nes[1]], 
				"RULE: SplitNERulesPT 2B.2 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")		
	    }
	
			/** 2B.3 - persons 
	     * EXAMPLE: Jerónimo de Sousa e Francisco Lopes
	     * ACTION: replace EM para PESSOA
	     */
		    if (o.original_ne.hasUnknownClassification()) {
			
			
							// temos de verificar os casos onde: 
				// 2B.3.1 os dois tem DBpedia e sáo PESSOA
				boolean tosplit = false
				
				if (o.saskia_nes[0]?.matchesClassification(
					[SC.person], 
					[AllOfThese, AllOfThem,  
					rembrandt.obj.ClassificationCriteria.Category]) && 
				o.saskia_nes[1]?.matchesClassification(
					[SC.person],
					[AllOfThese, AllOfThem, 
					rembrandt.obj.ClassificationCriteria.Category]) ) {
						
					tosplit = true	
				
				// 2B.3.2 um deles não tem
				} else if (o.saskia_nes[0]?.matchesClassification(
					[SC.person], 
					[AllOfThese, AllOfThem,  
					rembrandt.obj.ClassificationCriteria.Category]) && 
				o.saskia_nes[1]?.hasUnknownClassification() ){
					
					int nameTerms = 0
					o.saskia_nes[1].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms++}
      	   	}
	    	    	if (o.saskia_nes[1].terms && 
						((nameTerms * 1.0)/(o.saskia_nes[1].terms.size() * 1.0)) > 0.49) {
						o.saskia_nes[1].classification = [SC.person_individual]
						tosplit = true
					}								
				} else if (o.saskia_nes[1]?.matchesClassification(
					[SC.person], 
					[AllOfThese, AllOfThem,  
					rembrandt.obj.ClassificationCriteria.Category]) && 
				o.saskia_nes[0]?.hasUnknownClassification() ){

					int nameTerms = 0
					o.saskia_nes[0].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms++}
      	   	}
	    	    	if (o.saskia_nes[0].terms && 
						((nameTerms * 1.0)/(o.saskia_nes[0].terms.size() * 1.0)) > 0.49) {
							o.saskia_nes[0].classification = [SC.person_individual]
						
						tosplit = true
					}
				} else if (o.saskia_nes[0]?.hasUnknownClassification() && 
				o.saskia_nes[1]?.hasUnknownClassification() ) {
				
					int nameTerms0 = 0
					o.saskia_nes[0].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms0++}
      	   	}

					int nameTerms1 = 0
					o.saskia_nes[1].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms1++}
      	   	}
	    	    	if ( (o.saskia_nes[0].terms && 
						((nameTerms0 * 1.0)/(o.saskia_nes[0].terms.size() * 1.0)) > 0.49)  &&  
						( (o.saskia_nes[1].terms && 
						((nameTerms1 * 1.0)/(o.saskia_nes[1].terms.size() * 1.0)) > 0.49) ) ) {
						o.saskia_nes[0].classification = [SC.person_individual]
						o.saskia_nes[1].classification = [SC.person_individual]
							
						tosplit = true
					}
				}
		// 2. só um tem DBpedia e é pessoa, o outro tem de se ver pelos nomes
		// 3. nenhum tem DBpedia, mas são padrões ...

				if (tosplit) {
	
				log.debug("Rule SplitNERulesPT 2B.3 with action Replace ${o.original_ne} \
				with NEs ${o.saskia_nes}")

	        NEs.removeNEs(o.original_ne)
	        NEs.addNEs(o.saskia_nes, ConflictPolicy.JustAdd, 
				"RULE: SplitNERulesPT 2B.3 ACTION: added from splitted NE ${o.original_ne}")		
	    }
		}
	    return true
	   } ] ) , 
    		
	// 3A should be {NE} gluer {NE gluer NE gluer NE}
	
    	new Rule(id:"Split NE rule 3B", description:"{NE gluer NE} gluer {NE gluer NE}",
	 clauses:[//CC.beginSentence1, 
	          CC.notGluer1_1Pc['pt'],
	          CC.gluer1_1c['pt'],
	          CC.notGluer2_1Pc['pt'],         
	          CC.gluer2_1c['pt'],
	          CC.notGluer3_1Pc['pt'],         
	          CC.gluer3_1c['pt'],
	          CC.notGluer4_1Pc['pt'],   
	          CC.endSentence1], 
	 action:[{SplitNEMatcherObject o, ListOfNE NEs  -> o.lang=lang}, 
	         capture_NEoNE_o_NEoNE, 
	         {SplitNEMatcherObject o, ListOfNE NEs  -> 

		log.debug("Rule Split NE rule 3B matched. NEs splitted: ${o.saskia_nes}")
	 
	   /** 3B.1 - EXAMPLE Departamento de XXXXXX da YYYYY de ZZZZZZ" 
	    * ACTION: Generate ALT for the rightmost NE
	    */
	    if (o.original_ne.matchesClassification(
			[SC.organization], 
		   [ExistsAtLeastOneOfThese, AllOfThem, 
			rembrandt.obj.ClassificationCriteria.Category]) &&
		o.saskia_nes[1]?.matchesClassification(
			[SC.organization], 
			[ExistsAtLeastOneOfThese, AllOfThem,  
			rembrandt.obj.ClassificationCriteria.Category]) ) {

			log.debug("Rule SplitNERulesPT 3B.1 with action Generate ALT performed. \
				include NE ${o.saskia_nes[1]}")

			NEs.generateALT(o.original_ne, [o.saskia_nes[1]], 
				"RULE: SplitNERulesPT 3B.1 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")				   
	   }
	
		/** 3B.3 - persons 
	     * EXAMPLE: Jerónimo de Sousa e Francisco de Lopes
	     * ACTION: replace EM para PESSOA
	     */
		    if (o.original_ne.hasUnknownClassification()) {
			
			
				// temos de verificar os casos onde: 
				// 3B.3.1 os dois tem DBpedia e sáo PESSOA
				boolean tosplit = false
				
				if (o.saskia_nes[0]?.matchesClassification(
					[SC.person], 
					[AllOfThese, AllOfThem,  
					rembrandt.obj.ClassificationCriteria.Category]) && 
				o.saskia_nes[1]?.matchesClassification(
					[SC.person],
					[AllOfThese, AllOfThem, 
					rembrandt.obj.ClassificationCriteria.Category]) ) {
						
					tosplit = true	
				
				// 3B.3.2 um deles não tem
				} else if (o.saskia_nes[0]?.matchesClassification(
					[SC.person], 
					[AllOfThese, AllOfThem,  
					rembrandt.obj.ClassificationCriteria.Category]) && 
				o.saskia_nes[1]?.hasUnknownClassification() ){
					
					int nameTerms = 0
					o.saskia_nes[1].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms++}
      	   	}
	    	    	if (o.saskia_nes[1].terms && 
						((nameTerms * 1.0)/(o.saskia_nes[1].terms.size() * 1.0)) > 0.49) {
						o.saskia_nes[1].classification = [SC.person_individual]

						tosplit = true
					}
				} else if (o.saskia_nes[1]?.matchesClassification(
					[SC.person], 
					[AllOfThese, AllOfThem,  
					rembrandt.obj.ClassificationCriteria.Category]) && 
				o.saskia_nes[0]?.hasUnknownClassification() ){

					int nameTerms = 0
					o.saskia_nes[0].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms++}
      	   	}
	    	    	if (o.saskia_nes[0].terms && 
						((nameTerms * 1.0)/(o.saskia_nes[0].terms.size() * 1.0)) > 0.49) {
						o.saskia_nes[0].classification = [SC.person_individual]
		
						tosplit = true
					}
				} else if (o.saskia_nes[0]?.hasUnknownClassification() && 
				o.saskia_nes[1]?.hasUnknownClassification() ) {
				
					int nameTerms0 = 0
					o.saskia_nes[0].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms0++}
      	   	}

					int nameTerms1 = 0
					o.saskia_nes[1].terms?.each{term -> 
	      		if (PersonGazetteerPT.firstName.contains(term.text) ||
	      		PersonGazetteerPT.lastName.contains(term.text)) {nameTerms1++}
      	   	}
	    	    	if ( (o.saskia_nes[0].terms && 
						((nameTerms0 * 1.0)/(o.saskia_nes[0].terms.size() * 1.0)) > 0.49)  &&  
						( (o.saskia_nes[1].terms && 
						((nameTerms1 * 1.0)/(o.saskia_nes[1].terms.size() * 1.0)) > 0.49) ) ) {
						o.saskia_nes[0].classification = [SC.person_individual]
						o.saskia_nes[1].classification = [SC.person_individual]

						tosplit = true
					}
				}
		// 2. só um tem DBpedia e é pessoa, o outro tem de se ver pelos nomes
		// 3. nenhum tem DBpedia, mas são padrões ...

				if (tosplit) {
	

				log.debug("Rule SplitNERulesPT 3B.3 with action Replace ${o.original_ne} \
				with NEs ${o.saskia_nes}")

	        NEs.removeNEs(o.original_ne)
	        NEs.addNEs(o.saskia_nes, ConflictPolicy.JustAdd, 
				"RULE: SplitNERulesPT 3B.3 ACTION: added from splitted NE ${o.original_ne}")		
	    	}
		}
	    	return true
	
		} ] )  
      ]
   }
}