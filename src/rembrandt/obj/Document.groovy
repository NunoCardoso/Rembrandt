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

import rembrandt.tokenizer.TokenizerPT
import org.apache.log4j.Logger
import com.thoughtworks.xstream.annotations.*

/**
 * @author Nuno Cardoso
 *  
 * This class encapsulates a Document for REMBRANDT. It maintains a list of synchronized NEs, sentences and terms 
 * that are being added and/or removed according to the applied rules, and may include also metadata (lang, rules) 
 * to reconfigure REMBRANDT core
 */
@XStreamAlias("document")
class Document { //  extending it as Expando gives some weird sync stuff... go to the test unit.

    static Logger log = Logger.getLogger("Document")
    String title
    String body	
    String docid // this is THE ORIGINAL ID
    String taglang
    Date date_created
	
    @XStreamAlias('params')
    Map property = [:]
    
    @XStreamAsAttribute
    @XStreamAlias('language')
    String lang // its language

	@XStreamAsAttribute
    @XStreamAlias('rules')
    String rules // which NER rules to apply

    ListOfNE titleNEs = new ListOfNE()
    ListOfNE bodyNEs = new ListOfNE()
    List<Sentence> title_sentences = []   
    List<Sentence> body_sentences = []   

    @XStreamOmitField
    DocumentIndex titleIndex = new DocumentIndex()
    @XStreamOmitField
    DocumentIndex bodyIndex = new DocumentIndex()

    /** this is a container for NEs that are manually forced to be there 
     * before the parsing begins
     */
    @XStreamOmitField	
    ListOfNE forcedNEs = new ListOfNE()
 			
   /**
    * calls tokenize() and index()
    */
    void preprocess() {
	tokenize()
	index()
    } 
    
    /*
     * Tokenizes and organizes documents into sentences and tokens.
     */
     void tokenize() {
	tokenizeTitle()
	tokenizeBody()
    }
    
     void tokenizeTitle() {
	log.trace "Document: Tokenizing ${(title ? title.size() : 0)} bytes of title text"
	if (title) title_sentences = TokenizerPT.newInstance().parse(title)	
     }
     
     void tokenizeBody() {
	log.trace "Document: Tokenizing ${(body ? body.size() : 0)} bytes of body text" 
	 if (body) body_sentences = TokenizerPT.newInstance().parse(body) 
     }
	
     boolean isTitleTokenized() { return !title_sentences.isEmpty()  }
     boolean isBodyTokenized() { return !body_sentences.isEmpty()  }
     boolean isTitleIndexed() { return titleIndex.index != null  }
     boolean isBodyIndexed() { return bodyIndex.index != null }
  
     /*
      * Indexes title and body sentences 
      */
     void index() {
	 indexTitle()
	 indexBody()
     }
     
    void indexTitle() {  
	log.trace "Document: indexing ${title_sentences?.size()} title sentences"
	if (title_sentences) titleIndex.indexSentences(title_sentences)
    }
    
    void indexBody() {
	log.trace "Document: indexing ${body_sentences?.size()} body sentences" 
	if (body_sentences) bodyIndex.indexSentences(body_sentences)	
    }	

	
    /**
     * Returns the docid
     */
    public String toString() {return docid}
}