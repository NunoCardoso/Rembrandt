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
import com.thoughtworks.xstream.annotations.*

/**
 * @author Nuno Cardoso
 * Sentence class. Basically is a List with an index for the sentence position in the document.
 */

@XStreamAlias('s')
class Sentence extends ArrayList {
	
    @XStreamAlias('i')
    int index // the sentence index, within the document
    
    @XStreamOmitField
    int pointer // an internal term index pointer, useful for sentence-walking
    // the pointer IS an INDEX for the ArrayList elements. It's INDEPENDENT from the 
    // term indexes.
    // TERM INDEXES are the position of VISIBLE terms in the sentence.
    // SENTENCE POINTER is a pointer for the current active term being processed.
    // We should use the sentence pointer to walk the sentence, and jump over hidden terms.
    
    
    /**
     * Main constructor
     * @param index sentence index
     */
    public Sentence(int index, int pointer = 0) {
      super()
      this.index = index
      this.pointer = pointer
    }
	
    /**
     * Main constructor.
     * Adds List of terms (can be Terms or Strings).
      * @param terms List of terms or strings.
     * @param index sentence index in the document. Default is 0.
     * @param pointer the term pointer, for sentence-walking. Default is 0. 
     */
    public Sentence(List terms, int index = 0, int pointer = 0) {
      super()
      
      // Note: do NOT reset the term indexes! I need to create subSentences 
      // with intact term indexes.

      this.index=index
      this.pointer = pointer
      terms.eachWithIndex{term, i -> 
         Term t 
         if (term instanceof Term) {
             t = term
	     if (term.hidden) t.index = -1 
	 } else 
	     if (term instanceof String) {
	     t = new Term(term, i++)
	 } 
	 this.add(t)
      }
   }
    
    /**
     * Clone the sentence
     * @return a cloned sentence
     */
    public Sentence clone() {
	Sentence s = new Sentence(this.index, this.pointer)
	s.addAll(this)
	return s
    }
      
    /** 
     * Compares sentences. It does not use pointer. Calls term.equals() on all terms.
     */
    public equals(Sentence s) {
        if (!s) return null
        if (s.size() != this.size()) return false
        if (s.index != this.index) return false
        this.eachWithIndex{it, i -> 
            if (!it.equalsWithIndex (s[i]) ) return false
        }
        return true
    }
    
    /**
     * ToString() method calls the super method. 
     */
    public String toString() {return "$index:$pointer:"+super.toString()}
	
    /**
     * Casts as List<Term>, that is, loses index and pointer info.
     */
    public List<Term> toList() {
	return (List<Term>) this
    }
    /**
     * return the internal pointer index
     * @return the pointer index
     */
    public int getPointer() {return pointer} 
    
    /**
     * Get a sentence image with terms that are not hidden.
     * @return the sentence with no hidden terms
     */
    public Sentence getVisibleTerms() {
	return new Sentence(this.findAll{!it.hidden}, this.index)	
    }
    
    /** 
     * Test if there are hidden terms ahead.
     */
    boolean thereAreVisibleTermsAhead() {
   	// return early false if pointer is beyond the sentence size.
	if (this.pointer >= this.size()) return false
	return this[this.pointer..<this.size()].find{!it.hidden && it.index >= 0}
    }  
	
    /** Sets the poniter to the first visible term */
    public void resetPointerToFirstVisibleTerm() {
	this.pointer = this.findIndexOf{!it.hidden}	
    }
    
    /**
     * Advance the pointer on n visible terms to the right.
     * note that we can set the pointer beyond the term indexes. Note: 
     * 
     * Sentence:  [A] [B] [C]
     * Term index  0   1   2
     * Pointer:  0   1   2   3
     * 
     * Having the pointer at 2 says that term 2 (C) is still unseen. 
     * Having the pointer at 3 says that all terms were seem. 
     */
    void movePointerForVisibleTerms(int howmuch = 1) {
	if (this.isEmpty()) return
	// Allow howmuch == 0. For instance, SentenceBeginMatches return true with matched.terms=[],
	// and howmuch=0 is what it's going to be really collected. 
	if (howmuch < 0) throw new IllegalStateException("Howmuch must be integer > 0")
	// if we're already at the sentence limit, place the pointer beyond, and return
	
        int i = howmuch
	while (i > 0) {
	   if (this.pointer >= this.size()) break // I was already at the sentence limit, pointer is now beyond
	   this.pointer++
	   if (this?.getAt(this.pointer)) {
	       if (this[this.pointer].hidden == false) i-- // lower the counter only on visible terms  
	   }
	}
    }
    
    /** 
     * Returns a simple representation of this Sentence, with non-hidden terms separated by a white space
     * @return the sentence as a simple String representation
     */
    public String toStringLine() {
	return getVisibleTerms().join(" ")
    }

    /**
     * Returns a Sentence with a simple tokenization procedure
     * @param line The string to be tokenized
     * @param index the new sentence index. 0 if not given
     * @param pointer the sentence pointer. Will affect term indexes. 0 if not given 
     * @return the new sentence
     */
    static Sentence simpleTokenize(String line, int index = 0, int pointer = 0) {
	 Sentence s = new Sentence(index, pointer)
	 StringTokenizer st = new StringTokenizer(line)
	 int i = pointer
     	 while (st.hasMoreTokens()) {s << new Term(st.nextToken(), i++) }	 
 	 return s
    }
    
    Sentence getUnseenVisibleTerms() {
	return new Sentence(this[this.pointer..<this.size()].findAll{!it.hidden}, this.index)	
    }
    
    Sentence subSentence(int low, int high) {
	Sentence s = new Sentence(this.index)
        for(int i = low; i<= hight; i++) {s << this[i]}
        return s
    }
    
    int findPointerOfTermWithIndex(int index) {
	return this.findIndexOf{it.index == index}
    }
    
    String dump() {
    return ""+index+":"+pointer+":"+this.collect{""+it.text+":"+it.index+":"+it.hidden}
    }

   /* 
    * Get the index of the first match of a sub sentence
    * @param needle the Sentence used as matching needle
    * return the sentence index if matched, -1 if not matched
    */
    int indexOf(Sentence needle) {
	if (!needle) return null
	if (needle.size() > this.size()) return -1
	    
	boolean partialmatch 
	    
	for (int i=0; i<size()-needle.size()+1; i++) {
	    partialmatch = true
	    for (int j=0; j<needle.size(); j++) {
	//	println ""+(needle[j].text)+" == " +this[(i+j)].text+" = "+(needle[j].text == this[(i+j)].text)
		partialmatch = partialmatch && (needle[j].text == this[(i+j)].text) 
	    }
	//	println "partialmatch: $partialmatch"
	    if (partialmatch) return i
	}
	return -1
    }
    
    /** 
     * Get tokenization marks
     */
    static String addTokenizationMarks(Sentence s) {
	String res = ""
	s.each{t -> res += "["+t.text.replaceAll(/[\[\]]/) {all -> "\\"+all}+"]"}
	return res
    }
    
    static Sentence getFromTokenizationMarks(String s) {
	//println "I have STRING $s"
	
	// first, protect \[ and \]
	String s2 = s.replaceAll("§","§§").replaceAll("±","±±").replaceAll(/\\\[/,"±").replaceAll(/\\\]/,"§")
	Sentence sen = new Sentence(0)

	s2.findAll(/(?<=\[)[^\]]*(?=\])/).eachWithIndex{e, i -> 
	   e = e.replaceAll(/(?<!±)±(?!±)/,"[").replaceAll(/(?<!§)§(?!§)/,"]").replaceAll(/§§/,"§").replaceAll(/±±/,"±")
	   sen << new Term(e, i)
	}
	
	return sen
    }
    
    boolean containsTermText(String term) {
		return containsTermText(new Term(term))
    }

    boolean containsTermText(Term term) {
		return find{it == term}
    }
}