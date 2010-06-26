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
 
package rembrandt.test.rules

import org.apache.log4j.Logger
import saskia.bin.Configuration
import java.util.regex.Pattern
import rembrandt.obj.Document
import rembrandt.obj.DocumentIndex
import rembrandt.obj.Clause
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality
import rembrandt.obj.Rule
import rembrandt.obj.Sentence
import rembrandt.obj.ClassificationCriteria as CC
import rembrandt.obj.Term
import rembrandt.obj.NamedEntity as NE
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.rules.Detector
import rembrandt.rules.RuleMatcherOptimizer
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes

/**
 * @author Nuno Cardoso
 * Tester for RuleMatcerOprimizer.
 */
class TestRuleMatcherOptimizer extends GroovyTestCase {

    static Logger log = Logger.getLogger("RembrandtTest")
    Configuration conf
    Document doc
    RuleMatcherOptimizer rmo
    
    public TestRuleMatcherOptimizer() {	
    
	conf = Configuration.newInstance()
	
	/**
	 * 0: Portugal, oficialmente República Portuguesa, é um país localizado no sudoeste da Europa, 
	 * cujo território se situa na zona ocidental da Península Ibérica e em arquipélagos no Atlântico Norte. 
	 * 1: Possui uma área total de 92.391 km2, e é a nação mais ocidental do continente europeu. 
	 * 2: O território português é delimitado a Norte e a Leste por Espanha e a Sul e Oeste pelo Oceano 
	 * Atlântico, e compreende a parte continental e as regiões autónomas: os arquipélagos dos Açores e da Madeira.
	 * 3: Durante os séculos XV e XVI, Portugal foi uma potência mundial económica, social e cultural, 
	 * constituindo-se o primeiro e o mais duradouro império colonial de amplitude global.
	 */
	
	// load a document
	File f = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/WikipediaPTSample-100words.txt")
	doc = new Document()	
	doc.body = f.text
	doc.preprocess()
	doc.bodyNEs << new NE(terms:[new Term('Portugal',0)],
		sentenceIndex:0,termIndex:0, classification:[SC.place_human_country])
	doc.bodyNEs << new NE(terms:[new Term('República',3),new Term('Portuguesa',4)],
		sentenceIndex:0,termIndex:3, classification:[SC.place_human_country])
	doc.bodyNEs << new NE(terms:[new Term('Europa',13)],
		sentenceIndex:0,termIndex:13, classification:[SC.place_human_division])
	doc.bodyNEs << new NE(terms:[new Term('Península',23),new Term('Ibérica',24)],
		sentenceIndex:0,termIndex:23, classification:[SC.place_human_division])
	doc.bodyNEs << new NE(terms:[new Term('Atlântico',29),new Term('Norte',30)],
		sentenceIndex:0,termIndex:29, classification:[SC.place_human_division]) 
	
	rmo = new RuleMatcherOptimizer(doc)     
   }
    
   void testOptimizationForPlain1() {

       // no need to set categories, rule policies, etc. We're not matching the rule, just triggering 
       // optimizations according to the first clause
       Rule r1 = new Rule(id:"test plain 1", description:"arquipélagos!",
	 clauses:[ Clause.newPlain1Clause("arquipélagos") ] )

       List l1 = doc.bodyIndex.getIndexesForTerm("arquipélagos") 
       assert l1 ==  [[0, 27], [2, 32]] // these are the indexes for the term 'arquipélagos'
                                                                  
       rmo.optimize(r1)
       List<Sentence> body_sentences = rmo.selectedBodySentences
       int sentence_browse_strategy = rmo.body_sentences_browse_strategy

       // with this optimization, we should get the two sentences that have the 'arquipélagos' word, with 
       // the respective sentence pointers set to that term
       assert body_sentences.size() == 2, "Got ${body_sentences.size()} instead"
       
       // note: index = sentence index in document, pointer = term index in sentence
       assert body_sentences[0].index == 0 && body_sentences[0].pointer == 27
       assert body_sentences[1].index == 2 && body_sentences[1].pointer == 32        
       assert sentence_browse_strategy == Detector.DO_NOT_BROWSE_SENTENCE
   }
   
   void testOptimizationForRegex1() {

       // no need to set categories, rule policies, etc. We're not matching the rule, just triggering 
       // optimizations according to the first clause
       Pattern ortug = ~/[Pp]ortug.*/
       
       Rule r1 = new Rule(id:"test regex 1", description:"[Pp]ortug.*!",
	 clauses:[ Clause.newRegex1Clause(ortug, "ortug") ] )

       List l1 = doc.bodyIndex.getJointIndexesForPattern(ortug) 
       assert l1 ==  [[0, 0], [0, 4], [2, 2], [3, 7]] // these are the indexes for the terms in pattern '[Pp]ortug*'
                                                                  
       rmo.optimize(r1)
       List<Sentence> body_sentences = rmo.selectedBodySentences
       int sentence_browse_strategy = rmo.body_sentences_browse_strategy

       // with this optimization, we should four sentences that have the '[Pp]ortug.*' pattern.
       // but wait, four? There are only three! Yes, but the first sentence is returned twice, 
       // with different pointers, so that's easier to skip from the first position to the next one
       assert body_sentences.size() == 4, "Got ${body_sentences.size()} instead"
       
       // note: index = sentence index in document, pointer = term index in sentence
       body_sentences.eachWithIndex{s, i -> 
         assert s.index == l1[i][0], "Got ${s.index} instead of ${l1[i][0]}"
         assert s.pointer == l1[i][1], "Got ${s.pointer} instead of ${l1[i][1]}"
       }
       assert sentence_browse_strategy == Detector.DO_NOT_BROWSE_SENTENCE
   }
   
   void testOptimizationForConcept1() {

       Pattern ortug = ~/.*ortug.*/
       Pattern arquipelagos = ~/arquipélagos?/
       Pattern acores = ~/[Aa]çores?/
	       
       List p1 = ["Europa", ortug, ["continente","nãoexiste"], [arquipelagos, 'dos', acores],
                  arquipelagos,
                  "nãovaiencontrar", ~/naovaiencontar/, ["total","encontra","o","primeiro","termo" ] ]
       
        // no need to set categories, rule policies, etc. We're not matching the rule, just triggering 
        // optimizations according to the first clause
       Rule r1 = new Rule(id:"test concept 1", description:"concept",
	 clauses:[ Clause.newConcept1Clause(p1, "concept") ] )

       Map ls1 = doc.bodyIndex.getSubConceptsAndIndexesForConcept(p1)
       
       assert ls1[[arquipelagos, 'dos', acores]] == [[0, 27], [2, 32]] 
       assert ls1[arquipelagos] == [[0, 27], [2, 32]] 
                                    
       // get an hash if sentence/term index - subconcepts                                                               
       Map ls2 = DocumentIndex.invertConceptIndex(ls1)
       assert ls2[[0,27]] == [[arquipelagos, 'dos', acores], arquipelagos ], "ls2[[0,27]] equals ${ls2[[0,27]]} instead"
                            
       // get only the several indexes matched by the concept//
       List ls3 = DocumentIndex.sortKeysOfInvertedConceptIndex(ls2)
       assert ls3 == [ [0,0] , [0,4] , [0,13] , [0,27] , [1,3] , [1,15] , [2,2] , [2,32] , [3,7] ]
                                                                  
       rmo.optimize(r1)
       
       List<Sentence> body_sentences = rmo.selectedBodySentences
       Map subconcept_index = rmo.body_subconcept_index
       int sentence_browse_strategy = rmo.body_sentences_browse_strategy
     
       assert body_sentences.size() == 9, "Got ${body_sentences.size()} instead"
       
       // note: index = sentence index in document, pointer = term index in sentence
       body_sentences.eachWithIndex{s, i -> 
         log.info "For sentence with index ${s.index} and pointer ${s.pointer}, we have the subconcepts "+subconcept_index[[s.index, s.pointer]]
         assert s.index == ls3[i][0], "Got ${s.index} instead of ${ls3[i][0]}"
         assert s.pointer == ls3[i][1], "Got ${s.pointer} instead of ${ls3[i][1]}"
       }
       assert sentence_browse_strategy == Detector.DO_NOT_BROWSE_SENTENCE
   }
   
   void testOptimizationForMeaning1() {
   
	Document doc2 = new Document()	
	doc2.body = "Arquipélagos de Portugal"
	doc2.preprocess()
	rmo = new RuleMatcherOptimizer(doc2)     
	
	Rule r1 = new Rule(id:"WikipediaCategoryRulesPT", 
			  clauses:[rembrandt.gazetteers.NEGazetteer.meaningPT1c] )	
      
        Map ls1 = doc2.bodyIndex.getSubMeaningsAndIndexesForMeaning(r1.clauses[0].pattern)
       
        Map answer_key = [answer:SemanticClassification.create(Classes.category.place, 
        	Classes.type.physical, Classes.subtype.island), needle:[
        	~/[Aa]rquipélagos/, ~/[Ii]lhas/ ] ] 
        List answer_value = [[0,0]]
       
       assert ls1[answer_key] == ls1[answer_value]
	                                                    
       Map ls2 = DocumentIndex.invertMeaningsIndex(ls1)
       
       assert ls2[answer_value] == ls1[answer_key]
                            
       // get only the several indexes matched by the concept//
       List ls3 = DocumentIndex.sortKeysOfInvertedMeaningsIndex(ls2)
       assert ls3 == [ [0,0] ]
                                                                  
       rmo.optimize(r1)
       
       List<Sentence> body_sentences = rmo.selectedBodySentences
       Map subconcept_index = rmo.body_subconcept_index
       int sentence_browse_strategy = rmo.body_sentences_browse_strategy
     
       assert body_sentences.size() == 1, "Got ${body_sentences.size()} instead"
       
       // note: index = sentence index in document, pointer = term index in sentence
      body_sentences.eachWithIndex{s, i -> 
         log.info "For sentence with index ${s.index} and pointer ${s.pointer}, we have the subconcepts "+subconcept_index[[s.index, s.pointer]]
         assert s.index == ls3[i][0], "Got ${s.index} instead of ${ls3[i][0]}"
         assert s.pointer == ls3[i][1], "Got ${s.pointer} instead of ${ls3[i][1]}"
       }
       assert sentence_browse_strategy == Detector.DO_NOT_BROWSE_SENTENCE
   }
   
   void testOptimizationForNE1() {

       List<CC> necriteria1 = [CC.AllOfThese, CC.AllOfThem, CC.Category]                                                 
       List<CC> necriteria2 = [CC.AllOfThese, CC.AllOfThem, CC.Subtype]
           
       List<SC> neneedle1 = [SC.place]
       List<SC> neneedle2 = [SC.place_human_country]
                                                                                
       Rule r1 = new Rule(id:"test ne 1", description:"ne 1", clauses:[
          new Clause( cardinality:Cardinality.One,  criteria:Criteria.NEMatch, 
          NECriteria:necriteria1, pattern:neneedle1) ] )
       
       Rule r2 = new Rule(id:"test ne 2", description:"ne 2", clauses:[
	  new Clause( cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	  NECriteria:necriteria2, pattern:neneedle2) ] )
       
       rmo.optimize(r1)
       List<Sentence> body_sentences = rmo.selectedBodySentences 
       int sentence_browse_strategy = rmo.body_sentences_browse_strategy
       
       // Há 5 NEs com categoria LOCAL
       assert body_sentences.size() == 5, "Got ${body_sentences.size()} instead"
       List indexes1 = [[0,0], [0,3], [0,13], [0,23], [0,29]]
       
       // note: index = sentence index in document, pointer = term index in sentence
       body_sentences.eachWithIndex{s, i -> 
         assert s.index == indexes1[i][0], "Got ${s.index} instead of ${l1[i][0]}"
         assert s.pointer == indexes1[i][1], "Got ${s.pointer} instead of ${l1[i][1]}"
       }
       assert sentence_browse_strategy == Detector.DO_NOT_BROWSE_SENTENCE
      
       rmo.reset()
       rmo.optimize(r2)
       body_sentences = rmo.selectedBodySentences 
       sentence_browse_strategy = rmo.body_sentences_browse_strategy
       
       // Há 2 NEs com categoria LOCAL, tipo HUMANO e subtipo PAIS
       assert body_sentences.size() == 2, "Got ${body_sentences.size()} instead"
       indexes1 = [[0,0], [0,3]]
       
       // note: index = sentence index in document, pointer = term index in sentence
       body_sentences.eachWithIndex{s, i -> 
         assert s.index == indexes1[i][0], "Got ${s.index} instead of ${l1[i][0]}"
         assert s.pointer == indexes1[i][1], "Got ${s.pointer} instead of ${l1[i][1]}"
       }
       assert sentence_browse_strategy == Detector.DO_NOT_BROWSE_SENTENCE
   }
}
       
       
       