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

package rembrandt.obj

import org.apache.log4j.Logger
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * 
 * The ListOfNEIndex class mantains an index of terms from all NEs, pointing to the NE index
 */
public class ListOfNEIndex {

    HashMap<Integer,List<Integer>> sentenceIndex // index of <sentence number, ne.indexOf)
    HashMap<String,List> termIndex // index of <term, <sentence-index, term-index, ne.indexOf>
    
    static Logger log = Logger.getLogger("DocumentIndex")
  
    public ListOfNEIndex() {
        termIndex = [:]
        sentenceIndex = [:]
    }
    
    /** add the given sentences to a HashMap position index and a RAMDirectory Lucene term index
     * @param sentences The sentences to parse and add to the index
     */
     void indexTerms(ListOfNE NEs) {
     
	//i = new RAMDirectory()
	//w = new IndexWriter(i, new WhitespaceAnalyzer(), true) // NOTE the whitespaceAnalyser

	termIndex = [:]
	NEs.eachWithIndex{ne, i -> 
	   ne.terms.each{t -> 
              List key = this.termIndex[t.text]
              if (key) this.termIndex[t.text] << [ne.sentenceIndex, t.index, i]
              else this.termIndex[t.text] = [[ ne.sentenceIndex, t.index, i]]
            }   
        }	
     }
    
    /** 
     * adds a NE to the sentence index
     * @param sIndex the sentence index
     * @param neIndex the NE index in the ListOfNE
     */
    void addToSentenceIndex(int sIndex, int neIndex) {
        
        if (sIndex < 0 || neIndex < 0) throw new IllegalStateException("Cannot add invalid indexes (sentenceIndex $sIndex neIndex $neIndex)")
       // println "Requesting to add sIndex $sIndex, neIndex $neIndex"
        List<Integer> key = this.sentenceIndex[sIndex]
        if (!key) this.sentenceIndex[sIndex] = [neIndex]
        else {
            if (this.sentenceIndex[sIndex].contains(neIndex)) 
                throw new IllegalStateException("sentence Index $sentenceIndex already has neIndex $neIndex!")
            
                
        this.sentenceIndex[sIndex] << neIndex 
       // println "Now: ${this.sentenceIndex[sIndex]}"
                            
        }
    }
    
    /**
     * Rebuild the sentence index.
     * Call this if you make heavy changes on ListOfNE, as in sortNEs()
     */
    void remakeAllSentenceIndex(ListOfNE NEs) {
        sentenceIndex = [:]
        NEs.eachWithIndex{ne, i -> addToSentenceIndex(ne.sentenceIndex , i)}
    }
    
    /**
     * Sync the sentence indexes / ne index after a NE is removed.
     */
    void syncIndexesAfterRemoving(int neIndex) {
         this.sentenceIndex.collect{k,v -> 
            if (v.contains(neIndex)) v.remove(v.indexOf(neIndex))
            v.eachWithIndex{it, i ->        
                if (it > neIndex) v[i] = --it 
            } 
        } 
    }

    /** 
     * dump the index info for sentence index and term index
     */
    public String dump() {
        StringBuffer sb = new StringBuffer()
        sb.append "Sentence index:\n"
        this.sentenceIndex.each{sb.append  "${it.key}: ${it.value}\n"}   
        sb.append "Term index:\n"
        this.termIndex.each{sb.append  "${it.key}: ${it.value}\n"}
        return sb.toString()
    }
   
    public List<Integer> getNeIDsforQueryTerms(List<Term> needle_terms) {
        
        List partialResults = null
        
        int increment=0
        
        needle_terms.each{needle_term -> 
            
            List indexes = this.termIndex[needle_term.text] // gets all sentenceIndex, termIndex and neId that has the text
            if (partialResults == null) {
        	partialResults = indexes // first term, let's add all candidate indexes                  
            } else {
        	// if there are indexes from previous terms, let's use this term as a filter
               List newIndexes = []
               indexes.each{i -> // index is [sentenceIndex, first matched term.index, ne.id]
                 // let's find previous indexes that match indexOf(ne), sentence and then new term 
               	  newIndexes.addAll(partialResults.findAll{
                    it[2] == i[2] && it[0] == i[0] && (it[1] +increment) == i[1]})
                //println "newIndexes: $newIndexes"
               }
               partialResults = newIndexes
            }
            increment++
        }
       // println  "partial results: $partialResults"
        return partialResults.collect{it[2]}.sort() //I only want ne.ids  
       //println  "partial results: $partialResults"  
    }
}
