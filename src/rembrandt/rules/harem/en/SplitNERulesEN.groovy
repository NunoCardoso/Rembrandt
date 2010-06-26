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


/**
 * @author Nuno Cardoso
 * Rules for splitting NEs
 */
class SplitNERulesEN extends SplitNEDetector {
 
    static String lang = "en"	
    List<Rule> rules
    
    public SplitNERulesEN(NamedEntityDetector ierules, AskSaskia saskia) {
	super(ierules, saskia)
        rules = [

        
        // 1. {NE} gluer {NE} 
        new Rule(id:"Split NE rule 1", description:"{NE} gluer {NE}",
        clauses:[//CC.beginSentence1, 
        CC.notGluer1_1Pc['en'],
        CC.gluer1_1c['en'],
        CC.notGluer2_1Pc['en'],         
        CC.endSentence1], 
        action:[{SplitNEMatcherObject o, ListOfNE NEs -> o.lang=lang}, 
        capture_NE_o_NE, 
        {SplitNEMatcherObject o, ListOfNE NEs -> 
            
           // println "XXX NEs: $NEs"
		//	println "XXX o.saskia_nes: ${o.saskia_nes}"
            /** 1.1 The Main NE has a known classification, and the rightmost subNE is a Place
     * ACTION: Add the rightmost NE as an ALT
     * Exceptions: Main NE is a PESSOA, otherwise <PESSOA>Faria de Guimarães</PESSOA> gives <LOCAL>Guimarães</LOCAL>
     *             Main NE is a TEMPO, there is a town called Anos in France, gives <ALT><T>10 milhões de anos</T>|10 milhõµes de <LOCAL>anos</L></ALT>
     */
            if (!o.original_ne.hasUnknownClassification() &&  
            o.original_ne.matchesClassification([SC.person, SC.time, SC.value], 
            [NeverExistsAtLeastOneOfThese, ExistsAtLeastOneOfThem, rembrandt.obj.ClassificationCriteria.Category]) && // conflicts with groovy.lang.Category
            o.saskia_nes[0]?.matchesClassification([SC.place], 
            [NeverExistsAtLeastOneOfThese, AllOfThem, rembrandt.obj.ClassificationCriteria.Category]) && 
            o.saskia_nes[1]?.matchesClassification([SC.place], 
            [AllOfThese, AllOfThem, rembrandt.obj.ClassificationCriteria.Category])) {
                // there's a ListOfNE called NEs where I can add the split. 
                NEs.generateALT(o.original_ne, [o.saskia_nes[1]], "RULE: SplitNERulesEN 1.1 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")		
            }
            
            /** 1.2 The Main NE has an unknown classification, splitter is 'e' and the subNEs have classifications
     * EXAMPLE: <EM>Portugal e Espanha</EM> -> <LOCAL>Portugal</L> e <LOCAL>Espanha</L>
     *          <EM>PIS e Cofins</EM> -> <PLANO>PIS</L> e <PLANO>Cofins</L>    	
     * ACTION: Remove original NE, add subNEs
     */	 
            else if (o.original_ne.hasUnknownClassification() &&      
            !o.saskia_nes[0]?.hasUnknownClassification() &&  
            !o.saskia_nes[1]?.hasUnknownClassification() && 
            o.getMatchByClause(CC.gluer1_1c['en']).terms?.text.equals(["and"]) ) {	     
                NEs.removeNEs(o.original_ne)
                NEs.addNEs(o.saskia_nes, ConflictPolicy.JustAdd, "RULE: SplitNERulesEN 1.2 ACTION: added from splitted NE ${o.original_ne}")	 
            }
            
            /** 1.3 The main NE is a position, the rightmost NE is a place or organization 
     * EXAMPLE: <EM> Presidente da Bolívia</EM>, <EM>Presidente da UNICER</EM>
     * ACTION: Generate ALT for the rightmost NE
     */
            else if (o.original_ne.matchesClassification([SC.person_position], [ExistsAtLeastOneOfThese, AllOfThem, Type]) && 
            o.saskia_nes[1]?.matchesClassification([SC.place, SC.organization], 
            [ExistsAtLeastOneOfThese, ExistsAtLeastOneOfThem,  rembrandt.obj.ClassificationCriteria.Category]) ) {			   
                NEs.generateALT(o.original_ne, [o.saskia_nes[1]],  "RULE: SplitNERulesEN 1.3 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")	    
            }    
            
            /** 1.4 Check for Persons glued by "and" 
     *  EXAMPLE: <EM>George Bush e Boris Ieltsin</EM> 
     *  ACTION: simple split, remove main NE, add two subNEs
     *  EXCEPTIONS: Note names like  'José Ribeiro e Costa'. Split only for subNEs that are grounded.
     */
            else if (o.original_ne.hasUnknownClassification() && 
            o.saskia_nes[0]?.matchesClassification([SC.person_individual], [AllOfThese, AllOfThem, Type]) && 
            o.saskia_nes[1]?.matchesClassification([SC.person_individual], [AllOfThese, AllOfThem, Type]) && 
            o.getMatchByClause(CC.gluer1_1c['en']).terms?.text.equals(["and"]) ) { // && 
                //o.saskia_nes[0].dbpediaPage && o.saskia_nes[1].dbpediaPage) {  
                NEs.removeNEs(o.original_ne)
                NEs.addNEs(o.saskia_nes, ConflictPolicy.JustAdd, "RULE: SplitNERulesEN 1.4 ACTION: added from splitted NE ${o.original_ne}")		 
            }   	
            
            return true
        } ] ) ,
        
        new Rule(id:"Split NE rule 2A", description:"{NE} gluer {NE gluer NE}",
        clauses:[//CC.beginSentence1, 
        CC.notGluer1_1Pc['en'],
        CC.gluer1_1c['en'],
        CC.notGluer2_1Pc['en'],         
        CC.gluer2_1c['en'],
        CC.notGluer3_1Pc['en'],         
        CC.endSentence1], 		 
        action:[{SplitNEMatcherObject o, ListOfNE NEs  -> o.lang=lang}, 
        capture_NE_o_NEoNE, 
        {SplitNEMatcherObject o, ListOfNE NEs -> 
            
            /** 2.1 - get a place from a organization. 
     * EXAMPLE: Câmara Municipal de Ribeira de Pena
     * ACTION: Generate ALT
     */
            if (o.original_ne.matchesClassification([SC.organization],  [AllOfThese, AllOfThem,  rembrandt.obj.ClassificationCriteria.Category]) &&
            o.saskia_nes[1]?.matchesClassification([SC.place],  [AllOfThese, AllOfThem,  rembrandt.obj.ClassificationCriteria.Category]) ) {
                // there's a ListOfNE called NEs where I can add the split. 
                NEs.generateALT(o.original_ne, [o.saskia_nes[1]], "RULE: SplitNERulesEN 2A.1 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")		
            }
            
            /** 2.2 - get a place from a building. 
     * EXAMPLE: Museu de Ribeira de Pena
     * ACTION: Generate ALT
     */
            if (o.original_ne.matchesClassification([SC.place_human_construction], [ExistsAtLeastOneOfThese, AllOfThem, Subtype]) &&
            o.saskia_nes[1]?.matchesClassification([SC.place], [AllOfThese, AllOfThem,  rembrandt.obj.ClassificationCriteria.Category]) ) {
                // there's a ListOfNE called NEs where I can add the split. 
                NEs.generateALT(o.original_ne, [o.saskia_nes[1]], "RULE: SplitNERulesEN 2A.2 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")			
            }
            return true
        } ] ) ,
        
        new Rule(id:"Split NE rule 2B", description:"{NE gluer NE} gluer {NE}",
        clauses:[//CC.beginSentence1, 
        CC.notGluer1_1Pc['en'],
        CC.gluer1_1c['en'],
        CC.notGluer2_1Pc['en'],         
        CC.gluer2_1c['en'],
        CC.notGluer3_1Pc['en'],         
        CC.endSentence1], 
        action:[{SplitNEMatcherObject o, ListOfNE NEs  -> o.lang=lang}, 
        capture_NEoNE_o_NE, 
        {SplitNEMatcherObject o, ListOfNE NEs  -> 
            
            /** 2A.1 - get a place from a organization. 
     * EXAMPLE: Academia de Belas-Artes de Viena
     * ACTION: Generate ALT
     */
            if (o.original_ne.matchesClassification([SC.organization], [AllOfThese, AllOfThem,  rembrandt.obj.ClassificationCriteria.Category]) &&
            o.saskia_nes[1]?.matchesClassification([SC.place], [AllOfThese, AllOfThem,  rembrandt.obj.ClassificationCriteria.Category]) ) {
                // there's a ListOfNE called NEs where I can add the split. 
                NEs.generateALT(o.original_ne, [o.saskia_nes[1]], "RULE: SplitNERulesEN 2B.1 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")			
            }
            
            /** 2B.2 - 
     * EXAMPLE: Declaração de Consenso de Atlanta - OBRA/PLANO
     * ACTION: Generate ALT
     */
            if (o.original_ne.matchesClassification([SC.masterpiece_plan], [AllOfThese, AllOfThem, Type]) &&
            o.saskia_nes[1]?.matchesClassification([SC.place], [AllOfThese, AllOfThem,  rembrandt.obj.ClassificationCriteria.Category]) ) {
                // there's a ListOfNE called NEs where I can add the split. 
                NEs.generateALT(o.original_ne, [o.saskia_nes[1]], "RULE: SplitNERulesEN 2B.2 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")		
            }
            return true
        } ] ) , 
        
        // 3A should be {NE} gluer {NE gluer NE gluer NE}
        new Rule(id:"Split NE rule 3B", description:"{NE gluer NE} gluer {NE gluer NE}",
        clauses:[//CC.beginSentence1, 
        CC.notGluer1_1Pc['en'],
        CC.gluer1_1c['en'],
        CC.notGluer2_1Pc['en'],         
        CC.gluer2_1c['en'],
        CC.notGluer3_1Pc['en'],         
        CC.gluer3_1c['en'],
        CC.notGluer4_1Pc['en'],   
        CC.endSentence1], 
        action:[{SplitNEMatcherObject o, ListOfNE NEs  -> o.lang=lang}, 
        capture_NEoNE_o_NEoNE, 
        {SplitNEMatcherObject o, ListOfNE NEs  -> 
            
            /** 3B.1 - EXAMPLE Departamento de XXXXXX da YYYYY de ZZZZZZ" 
     * ACTION: Generate ALT for the rightmost NE
     */
            if (o.original_ne.matchesClassification([SC.organization], 
            [ExistsAtLeastOneOfThese, AllOfThem,  rembrandt.obj.ClassificationCriteria.Category]) &&
            o.saskia_nes[1]?.matchesClassification([SC.organization], 
            [ExistsAtLeastOneOfThese, AllOfThem,  rembrandt.obj.ClassificationCriteria.Category]) ) {
                // there's a ListOfNE called NEs where I can add the split. 
                NEs.generateALT(o.original_ne, [o.saskia_nes[1]], "RULE: SplitNERulesEN 3B.1 ACTION: Generate ALT, include NE ${o.saskia_nes[1]}")				   
            }	
            return true
        } ] )  
        ]
    }
}