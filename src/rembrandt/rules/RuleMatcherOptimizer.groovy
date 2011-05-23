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

import java.util.Map;

import rembrandt.obj.Document
import rembrandt.obj.DocumentIndex
import rembrandt.obj.Sentence
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.NamedEntity
import rembrandt.obj.Term


/**
 * @author Nuno Cardoso
 * this class takes a main document, and selects the most promising sentences from the title and 
 * body for the given rule 
 * 
 */
class RuleMatcherOptimizer {

    Document doc
    
    List<Sentence> selectedTitleSentences
    List<Sentence> selectedBodySentences
    int title_sentences_browse_strategy
    int body_sentences_browse_strategy
    Map title_subconcept_index
    Map body_subconcept_index  
    
    public RuleMatcherOptimizer(Document doc) {
	this.doc = doc
	reset()
    }
    
    void reset() {
	selectedTitleSentences = []
       	selectedBodySentences = []
       	title_subconcept_index = null                     
       	body_subconcept_index = null                     
       	int title_sentences_browse_strategy = -1                         
      	int body_sentences_browse_strategy = -1                            	       	                      
    }
    
    void optimize(Rule rule) {
	reset()
	 	      
	// let's check the cardinality and criteria for the first clause
	// we'll only work on 1 and 1P cardinalities and change strategy for each criteria
	
	Cardinality cardinality = rule.clauses[0].cardinality
	Criteria criteria = rule.clauses[0].criteria
	
	switch (cardinality) {
	     
	  case [Cardinality.ZeroOrMore, Cardinality.ZeroOrOne]:
		// there's no way we can spped these ones...  
	      selectedTitleSentences = doc.title_sentences?.clone()
	      selectedBodySentences = doc.body_sentences?.clone()
	      title_sentences_browse_strategy = Detector.BROWSE_SENTENCE
	      body_sentences_browse_strategy = Detector.BROWSE_SENTENCE
	      
	  break
	  	  
	  case [Cardinality.One, Cardinality.OneOrMore]:
	      
	  switch(criteria) {
	  
	  /** PlainMatch: search indexes, return sentences with pointers all set */
	    case Criteria.PlainMatch:
		
		if (doc.title_sentences) {
		    if (!doc.isTitleIndexed()) {
			selectedTitleSentences = doc.title_sentences.clone()
			title_sentences_browse_strategy = Detector.BROWSE_SENTENCE
		    } else {		
			List indexes = doc.titleIndex.getIndexesForTerm(rule.clauses[0].pattern)
			indexes.each{index ->
			// get a full sentence, set the pointer to the first term with that index 
		  	    Sentence s = doc.title_sentences.getAt(index[0]).clone() // clone must stay here		      
		  	    s.pointer = s.findPointerOfTermWithIndex(index[1])
		            selectedTitleSentences << s 		    
		        }
			title_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
		    }
		}
		
		if (!doc.isBodyIndexed()) {
		   selectedBodySentences = doc.body_sentences?.clone() // clone must stay here		      
		   body_sentences_browse_strategy = Detector.BROWSE_SENTENCE
		} else {
		 List indexes = doc.bodyIndex.getIndexesForTerm(rule.clauses[0].pattern)
		   indexes.each{index ->
		       // get a full sentence, set the pointer 
		       // note that we have to clone it, we may return duplicates of sentences 
		       // with dofferent pointers
		       Sentence s = doc.body_sentences.getAt(index[0]).clone()// clone must stay here		   
		       s.pointer = s.findPointerOfTermWithIndex(index[1])
		       selectedBodySentences << s 
		   }		
	 	   body_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
		}
	    break
	    
	     /** RegexMatch: search indexes with patterns, return sentences with pointers all set */
	    case Criteria.RegexMatch:
		
		if (doc.title_sentences) {
		    if (!doc.isTitleIndexed()) {
		
			   selectedTitleSentences = doc.title_sentences.clone() // clone must stay here		      
			   title_sentences_browse_strategy = Detector.BROWSE_SENTENCE
		    } else {
			List indexes = doc.titleIndex.getJointIndexesForPattern(rule.clauses[0].pattern)
			indexes.each{index ->
			// get a full sentence, set the pointer 
				Sentence s = doc.title_sentences.getAt(index[0]).clone() // clone must stay here		      
				s.pointer = s.findPointerOfTermWithIndex(index[1])
			        selectedTitleSentences << s 
		        }
		         title_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
		    }
		}
		
		if (!doc.isBodyIndexed()) {
		   selectedBodySentences = doc.body_sentences.clone() // clone must stay here		      
		   body_sentences_browse_strategy = Detector.BROWSE_SENTENCE
		} else {		    		
		   // println "Body sentences: $doc.body_sentences"
		   // println "Index: "+doc.bodyIndex.dump()
		    List indexes = doc.bodyIndex.getJointIndexesForPattern(rule.clauses[0].pattern)
		   // println "Indexes for pattern ${rule.clauses[0].pattern} are $indexes"
		    indexes.each{index ->
		    	// get a full sentence, set the pointer 
		    	Sentence s = doc.body_sentences.getAt(index[0]).clone() // clone must stay here	
		    //	println "for term index ${index[1]}, s.findPointerOfTermWithIndex gives "+s.findPointerOfTermWithIndex(index[1])
		    	s.pointer = s.findPointerOfTermWithIndex(index[1])
		        selectedBodySentences << s 
		    }		
		    body_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
		}
	//	println "selectedBodySentences: "
	//	selectedBodySentences.each{println it.dump()}
	    break
	    
	    /** I can generate a map of sentence/term index - subconcepts, that can be used 
	     * to select the proper subconcepts for each concept match.
	     */
	    case Criteria.ConceptMatch: 

		if (doc.title_sentences) {
		    if (!doc.isTitleIndexed()) {
		        selectedTitleSentences = doc.title_sentences.clone() // clone must stay here		      
		        title_sentences_browse_strategy = Detector.BROWSE_SENTENCE
		    } else {		
			Map ls1 = doc.titleIndex.getSubConceptsAndIndexesForConcept(rule.clauses[0].pattern)
		
		// ok, when applying rule, use this index to get an optimized concept subset 
			title_subconcept_index = DocumentIndex.invertConceptIndex(ls1)
		
		// let's get all unique, sorted indexes
			List title_indexes = DocumentIndex.sortKeysOfInvertedConceptIndex(title_subconcept_index)
		
			title_indexes.each{index ->
		 // get a full sentence, set the pointer 
			    Sentence s = doc.title_sentences.getAt(index[0]).clone() // clone must stay here		      
			    s.pointer = s.findPointerOfTermWithIndex(index[1])
			    selectedTitleSentences << s 
			}
			title_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
		    }
		}
        
		
        	// body 
		if (!doc.isBodyIndexed()) {
			   selectedBodySentences = doc.body_sentences.clone() // clone must stay here		      
			   body_sentences_browse_strategy = Detector.BROWSE_SENTENCE
		} else {	
		Map ls2 = doc.bodyIndex.getSubConceptsAndIndexesForConcept(rule.clauses[0].pattern)
		// ok, when applying rule, use this index to get an optimized concept subset 
		body_subconcept_index = DocumentIndex.invertConceptIndex(ls2)
		
		// let's get all unique, sorted indexes
		List body_indexes = DocumentIndex.sortKeysOfInvertedConceptIndex(body_subconcept_index)
		
		body_indexes.each{index ->
		    // get a full sentence, set the pointer 
		    Sentence s = doc.body_sentences.getAt(index[0]).clone() // clone must stay here		      
		    s.pointer = s.findPointerOfTermWithIndex(index[1])
		    selectedBodySentences << s 
		}
		body_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
		}
	    break
	    
	    
	    case Criteria.MeaningMatch: 

		if (doc.title_sentences) {
		    if (!doc.isTitleIndexed()) {
		        selectedTitleSentences = doc.title_sentences.clone() // clone must stay here		      
		        title_sentences_browse_strategy = Detector.BROWSE_SENTENCE
		    } else {		
			Map ls1 = doc.titleIndex.getSubMeaningsAndIndexesForMeaning(rule.clauses[0].pattern)
		
		// ok, when applying rule, use this index to get an optimized concept subset 
			title_subconcept_index = DocumentIndex.invertMeaningsIndex(ls1)
		
		// let's get all unique, sorted indexes
			List title_indexes = DocumentIndex.sortKeysOfInvertedMeaningsIndex(title_subconcept_index)
		
			title_indexes.each{index ->
		 // get a full sentence, set the pointer 
			    Sentence s = doc.title_sentences.getAt(index[0]).clone() // clone must stay here		      
			    s.pointer = s.findPointerOfTermWithIndex(index[1])
			    selectedTitleSentences << s 
			}
			title_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
		    }
		}
        
		
        	// body 
		if (!doc.isBodyIndexed()) {
			   selectedBodySentences = doc.body_sentences.clone() // clone must stay here		      
			   body_sentences_browse_strategy = Detector.BROWSE_SENTENCE
		} else {	
		Map ls2 = doc.bodyIndex.getSubMeaningsAndIndexesForMeaning(rule.clauses[0].pattern)
		// ok, when applying rule, use this index to get an optimized concept subset 
		body_subconcept_index = DocumentIndex.invertMeaningsIndex(ls2)
		
		// let's get all unique, sorted indexes
		List body_indexes = DocumentIndex.sortKeysOfInvertedMeaningsIndex(body_subconcept_index)
		
		body_indexes.each{index ->
		    // get a full sentence, set the pointer 
		    Sentence s = doc.body_sentences.getAt(index[0]).clone() // clone must stay here		      
		    s.pointer = s.findPointerOfTermWithIndex(index[1])
		    selectedBodySentences << s 
		}
		body_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
		}
	    break
	    
	    // TODO this one
	    case Criteria.NEMatch:
		//println "doc.titleNEs ? ${doc.titleNEs} doc.bodyNEs? ${doc.bodyNEs}"
		List<NamedEntity> nes = doc.titleNEs?.getNEsByClassification(rule.clauses[0].pattern, rule.clauses[0].NECriteria)
		nes.sort({a, b -> 
		   ((a.sentenceIndex == b.sentenceIndex)? a.termIndex <=> b.termIndex
				  : a.sentenceIndex <=> b.sentenceIndex)}) 
         //   println "NES: $nes"
		nes.each{ne -> 
		    Sentence s = doc.title_sentences.getAt(ne.sentenceIndex).clone() // clone must stay here		      
		    s.pointer = s.findPointerOfTermWithIndex(ne.termIndex)
		    selectedTitleSentences << s 			
		}
		title_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
		
		nes = doc.bodyNEs?.getNEsByClassification(rule.clauses[0].pattern, rule.clauses[0].NECriteria)
		nes.sort({a, b -> 
		   ((a.sentenceIndex == b.sentenceIndex)? a.termIndex <=> b.termIndex
				  : a.sentenceIndex <=> b.sentenceIndex)}) 
		nes.each{ne -> 
		    Sentence s = doc.body_sentences.getAt(ne.sentenceIndex).clone() // clone must stay here		      
		    s.pointer = s.findPointerOfTermWithIndex(ne.termIndex)
		    selectedBodySentences << s 			
		}
		body_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 

	    break
	    
	    case Criteria.SentenceBeginMatch:
		
		// return all sentences, set pointer to beginning, and do not browse them
		selectedTitleSentences = doc.title_sentences.findAll{it.pointer == 0}.clone()		
		title_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
		selectedBodySentences = doc.body_sentences.findAll{it.pointer == 0}.clone()		
		body_sentences_browse_strategy = Detector.DO_NOT_BROWSE_SENTENCE 
			
	    break	
	  }
	 
	  break
	  
	  default: 
	      
	      // there's no way we can speed these ones...  
	      selectedTitleSentences = doc.title_sentences.clone()
	      selectedBodySentences = doc.body_sentences.clone()
	      sentence_browse_strategy = Detector.BROWSE_SENTENCE
	      
	  break
	}
    }//optimize method
}