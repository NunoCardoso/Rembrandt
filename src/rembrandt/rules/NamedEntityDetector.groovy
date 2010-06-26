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
 
package rembrandt.rules

import groovy.lang.Closure;

import org.apache.log4j.Logger
import rembrandt.obj.Rule
import rembrandt.obj.Document
import rembrandt.obj.ListOfNE
import rembrandt.obj.Sentence
import rembrandt.obj.Term
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.obj.Clause
import rembrandt.obj.NamedEntity
import rembrandt.obj.SemanticClassification
/**
 * @author Nuno Cardoso
 * 
 * This class is used for NE-detection rules over document sentences.
 * It's great for the generation of new NEs over text patterns that do not have NEs,
 * but it's not exclusive - note that you can change rule behavior. 
 *
 */
public class NamedEntityDetector extends Detector {

     static Logger log = Logger.getLogger("Detector")

     /**
      * this class apply rules to to the documents and performs subsequent actions for 
      * successfully mat hed rules.
      * @param doc Document with the sentences.
      * @param rules The set of rules.
      * @return Document with new NEs
      */
     public processDoc(Document doc, List<Rule>rules = rules) {

	// log.debug "Processing document ${doc} with set of ${rules?.size()} rules"
	// log.debug "Doc has ${doc.title_sentences.size()} title sentences and ${doc.body_sentences.size()} body sentences"

	 RuleMatcherOptimizer rmo = new RuleMatcherOptimizer(doc)

	 for (rule in rules) {
	 //    println "rule: $rule rules: $rules doc:${doc.title?.size()+', '+doc.body?.size()}"
	 //    println "sentences: ${doc.title_sentences?.size()+', '+doc.body_sentences?.size()}"
	     rmo.optimize(rule)
	     
	     List<Sentence> title_sentences = rmo.selectedTitleSentences
	     List<Sentence> body_sentences = rmo.selectedBodySentences
	     int title_sentences_browse_strategy = rmo.title_sentences_browse_strategy
	     int body_sentences_browse_strategy = rmo.body_sentences_browse_strategy
	     Map title_subconcept_index = rmo.title_subconcept_index // for optimized subset of concepts in ConceptMatch
	     Map body_subconcept_index = rmo.body_subconcept_index // for optimized subset of concepts in ConceptMatch
	     
	     log.debug "Rule ${rule} reduced doc to # sentences title:${title_sentences.size()}, body:${body_sentences.size()}"
	     
	     // optimized list of sentences. Nice.
	     title_sentences?.each {sentence ->
	     
	        // don't reset the pointer, it's on the optimized index for DO_NOT_BROWSE_SENTENCES. Memorize it 
	        // so that we can restore it for other rules. Remember, walking sentences changes the pointer. 
	        int initialpointer = sentence.pointer // do not remove!
	        
	     	while (sentence.thereAreVisibleTermsAhead()) {
	    	    	     	    
	     	    if (!sentence[sentence.pointer].hidden) {
	     	    // for concepts, let's check index for a subset of concepts
	     		List subconcepts
	     		if(title_subconcept_index) subconcepts = title_subconcept_index[ 
	     		      [ sentence.index , sentence[sentence.pointer].index ] ]
	     	    // note: it's sentence[sentence.pointer].index, the term.index is the one used in the subconcept_index
	     	   
	     		def generatedObject
	     		def matchObject = matchRule( new MatcherObject(rule:rule, sentence:sentence, 
	     			NEs:new ListOfNE(doc.titleNEs.getNEsBySentenceIndex(sentence.index)),
	     			subConcepts:subconcepts ) )
		
	            // if there is an object, call the rule action. It should generate a NE.
	     	    // if no rule action given, use the default one
    
	     		if (matchObject) generatedObject = performActionsOnMatcherObject(matchObject)	     		
	     		if (generatedObject && generatedObject instanceof NamedEntity) doc.titleNEs.addNEs(generatedObject) 
	     		
	     	    }
	     	    if (title_sentences_browse_strategy == DO_NOT_BROWSE_SENTENCE) break 	   	
		    else sentence.movePointerForVisibleTerms()  
	     	} 
	        
	        sentence.pointer = initialpointer // restores sentence pointer for other rules. Do not remove!
	    }// title_sentences each

	    //println "body_sentences = "+body_sentences
	    body_sentences?.each {sentence ->
	      //  println "ok,body sentence, sentence = "+sentence.dump()
	        int initialpointer = sentence.pointer // do not remove!

	   	while (sentence.thereAreVisibleTermsAhead()) {
	    	     	    
	     	    if (!sentence[sentence.pointer].hidden) {
	     	    // for concepts, let's check index for a subset of concepts
	     		List subconcepts
	     		if(body_subconcept_index) subconcepts = body_subconcept_index[ 
	     		      [ sentence.index , sentence[sentence.pointer].index ] ]
	     	    // note: it's sentence[sentence.pointer].index, the term.index is the one used in the subconcept_index
	     	   

	     	        //log.debug "NamedEntityDetector: Rule:${rule}, s:${sentence} s.index:${sentence.index}, s.pointer:${sentence.pointer}"
	     	        def generatedObject
	     	        def matchObject = matchRule( new MatcherObject(rule:rule, sentence:sentence, 
	     	        	NEs:new ListOfNE(doc.bodyNEs.getNEsBySentenceIndex(sentence.index)),
	     	        	subConcepts:subconcepts) )
			
	            // if there is an object, call the rule action. It should generate a NE.
	     	    // if no rule action given, use the default one
	    
                    
	     	        if (matchObject) generatedObject = performActionsOnMatcherObject(matchObject)	     		
                        if (generatedObject && generatedObject instanceof NamedEntity) doc.bodyNEs.addNEs(generatedObject) 
                    
	     	    }
	     	    if (body_sentences_browse_strategy == DO_NOT_BROWSE_SENTENCE) break 	   	
	     	    else sentence.movePointerForVisibleTerms()  
	     	}   	
	        sentence.pointer = initialpointer // restores sentence pointer! do not remove!
	    }// body_sentences each
	 }// each rule
     } // end method detect 
        
     /** 
      * This method invokes the rule's action on itself, to generate something. 
      * If a closure is given as argument, it will perform it instead.
      * Note that this closure just takes the collected stuff from the MarcherObject to generate 
      * a new object, it does not modify anything outside the MatcherObject
      */
     public performActionsOnMatcherObject(MatcherObject o, closure = null) {
        // if a closure is given as optional argument, let's execute it, overriding rule actions.
        if (closure && closure instanceof Closure) return closure(o)
        
 	def actions = o.rule.action   
 	// if rules give no action, and method argument closure is also null, let's use the default one
 	if (!actions) return NamedEntityDetector.generateNEfromRule(o)
        	  
 	if (actions instanceof Closure) return actions(o)
 	else if (actions instanceof List) {	
 	    def obj
 	    o.rule.actions.each{action -> if (c instanceof Closure) obj = action(o) }  
 	    return obj
 	} else {
 	    log.error "Tried to apply a rule action, but no Closure found."
 	    log.error "Rule class action = ${this.rule.action.class}."
 	    throw new IllegalStateException() 
 	}
     }
    
     public NamedEntity processInternalEvidenceOnNE(NamedEntity ne, List<Rule>rules = rules) {
	          
	  Sentence s = new Sentence(ne.terms.clone(), ne.sentenceIndex)
	  s.resetPointerToFirstVisibleTerm()
	  
	  for (rule in rules) {
	      def generatedObject
     	      def matcherObject = matchRule( new MatcherObject(rule:rule, sentence:s))
	       // since the sentence is the query terms, the sentenceTermIndex defaults to 0	     		
     	      if (matcherObject) generatedObject = performActionsOnMatcherObject(matcherObject, NamedEntityDetector.generateNEfromMeanings)	      
	      if (generatedObject && generatedObject instanceof NamedEntity) return generatedObject	      		
	      return null
	  }
     }
	     		
     /** 
      * this is the standard action to generate a new NE from the matched object
      */
     static Closure generateNEfromRule = {MatcherObject o ->  
	
      	// hack para external evidence: se alguma das EM capturadas pertence a uma alt, então não mexer
     	// a external evidence pode estragar o que foi já feito
     	List<NamedEntity> matchedNEs = o.getMatchedNEs()
     	if (matchedNEs.find{it.alt != null}) return null
 
    	NamedEntity newNE = new NamedEntity(sentenceIndex:o.sentence.index)
 	
     	if (!o.rule.sc) throw new IllegalStateException("Can't execute a rule if it does not have a sc!")
     	newNE.classification << o.rule.sc
     	
	newNE.addpolicy = o.rule.addpolicy  
	newNE.terms = o.getMatchedTerms(o.rule?.policy)
	if (newNE.terms.isEmpty()) {
	    log.warn "generating a NE with no terms. Something is wrong with your rule? I'll return null!"
	    return null
	}
	newNE.termIndex = newNE.terms[0].index
	newNE.reportToHistory "RULE: ${o.rule.id} ACTION: generate NE $newNE"
    	log.trace("generateNEfromRule: final NE = $newNE")
	return newNE
     } 

     static Closure generateNEfromMeanings = {MatcherObject o ->
     	
     // note that ne.terms awaits for a List<Term>, so let's call a  List<Term> toList() in the Sentence object
         NamedEntity newNE = new NamedEntity(terms:o.sentence.toList(), sentenceIndex:o.sentence.index)
         newNE.termIndex = newNE.terms[0].index
         
         List answers = o.pastMatches.values().collect{if (it.answer) return it.answer}
         if (answers.isEmpty()) return null
         answers.each{a -> newNE.mergeClassification(a) }
         return newNE
     }//closure end
}