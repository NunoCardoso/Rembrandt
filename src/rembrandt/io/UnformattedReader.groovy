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

package rembrandt.io

import java.util.List;

import rembrandt.obj.Document
import rembrandt.obj.Sentence
import rembrandt.obj.Term
import rembrandt.obj.NamedEntity
/**
 * @author Nuno Cardoso
 * This class is a reader for plain text format.
 * Assume only one document with no id.
 */
class UnformattedReader extends Reader {
    
    Map<String,String> tag = [:]
    int numbertag = 0
    String tagmark="REMBRANDTTAGMARK"
    
    public UnformattedReader(StyleTag style) {
	super(style)
    }
    
    private String savetag(item) {
        tag[++numbertag] = item
        return  " ${tagmark}${numbertag} "
    }
    
    private String loadtag(item) {
        return item.replaceAll(/^${tagmark}(\d+)$/) {it, cap -> 
            return tag[Integer.parseInt(cap)]
        }
    }
       
    public void processInputStream(InputStreamReader is) {
        
        /* input stream come as:
         
           % title1 (optional)
           % description1 (optional)
           content
           (...)
           content
           % title2 (optional)
           % description2 (optional)         
        
        */ 
        BufferedReader br = new BufferedReader(is)	    
        StringBuffer buffer = new StringBuffer()		    
        String line
        Document doc
        
        while ((line = br.readLine()) != null) {
            // following # or % are just discarted.
            if (line.startsWith("%") || line.startsWith("#")) {
                // first occurence of # or % indicate a new document, with docid of its text 
                if (!buffer) {
                    if (!doc) {
                	doc = new Document()         
                  	doc.docid = line.find(/(?<=[#%]+).+/).trim()
                    }
                    // if doc && !buffer, it's a description comment
                 } else {
                    String text
                    // if buffer is empty, we're on the title description 
                    if (!buffer) {}
                    // if buffer was not empty, it's a new document!! Process the old one.
                    else {
                        text = buffer.toString()
                        text = text.replaceAll(/<[^>]*?>/) {tag -> 
                          if (style.isOpenTag(tag) || style.isCloseTag(tag) || 
                          style.isOpenALTTag() || style.isOpenSubALTTag() || 
                          style.isCloseALTTag() || style.isCloseSubALTTag() ) {
                              return savetag(tag)
                          } else {return tag}
                        }
                    }
                    
                    // now, let's atomize it.
                    doc.body = text        
                    doc.tokenizeBody()
                    
                    // let's walk the sentences, capture NEs and remake them
                    parseSentences(doc.body_sentences, doc)
                    
                    // index body
                    doc.indexBody()
                    docs << doc
                
                    // now, let's open the new one...
                    doc = new Document()
                    doc.docid = line.find(/(?<=[#%]+).+/).trim()
                    buffer = new StringBuffer()
                }
            } else if (!(line ==~ /^\s*$/)) { //ignore blank lines 
              // if there is no Document, create it with blank docid
                //if (!doc) doc = new Document()
                buffer.append(line+"\n")
            }               
        }// while
        
        // if there is still a buffer. close it. 
        if (buffer) {
            if (!doc) doc = new Document()
            String text = buffer.toString()      
            text = text.replaceAll(/<[^>]*?>/) {tag -> 
               if (style.isOpenTag(tag) || style.isCloseTag(tag) || 
               style.isOpenALTTag() || style.isOpenSubALTTag() || 
               style.isCloseALTTag() || style.isCloseSubALTTag() ) {
                return savetag(tag)
               } else {return tag}
           }
            doc.body = text        
            doc.tokenizeBody()
            parseSentences(doc.body_sentences, doc)
            doc.indexBody()
            docs << doc
        }
    }
    
    public Sentence parseSentences(List<Sentence> s, Document doc) {
        
        int sindex = 0
        int tindex = 0
        NamedEntity ne = null 
        int neid = 0
         
        boolean inalt = false
        boolean insubalt = false
        
        long altid
        int subaltid
            
        List<Sentence> s_clone = s.clone() // the cloned list is for collection walking. 
        // I will make changes directly on the s variable (the reference sentences on the doc).
      
        s.clear()
        Sentence s_temp = new Sentence(sindex)
        
        for(sentence in s_clone) { 
            //     println "I'm with sentence $sentence"
            sentence.each{term -> 
                // if starts: create NE, halt correctTermIndex counter
                if (term.text.startsWith(tagmark)){
                    // get original tag
                   String tag = loadtag(term.text)	
                    
                   if (style.isOpenTag(tag)) {
                       // if there's no NE, let's create. 
                       // if there is, let's add a SemanticClassification         	      
                       if (!ne) {
                	   ne = style.parseOpenTag(tag) 
                           // if the tag read has a low verbosity number, we may need to rectify sentenceIndex and termIndex 
                	   if (ne.sentenceIndex < 0) ne.sentenceIndex = sindex
                	   if (ne.termIndex < 0) ne.termIndex = tindex
                    
                	   if (!ne.id) ne.id = ""+(neid++)
                	   if (inalt) {
                	       ne.alt = ""+altid
                	       ne.subalt = subaltid
                	   }
                       } else {
                	   NamedEntity otherne = style.parseOpenTag(tag) 
                	   otherne.classification.each{cl -> 
                	     ne.classification << cl
                	     ne.wikipediaPage[cl] = otherne.wikipediaPage[cl]
                	     ne.dbpediaPage[cl] = otherne.dbpediaPage[cl]
                	   }		  
                       }         	      
                   } else if (style.isCloseTag(tag)) {
                       if (ne) {
                	   doc.bodyNEs.add(ne)	
                	   ne = null
                       } else {}// don't worry... continue          
                   } else if (style.isOpenALTTag(tag)) {
                       inalt = true    
                       altid = System.currentTimeMillis()
                       subaltid = 0
                   }  else if (style.isCloseALTTag(tag)) {
                       inalt = false
                       altid = -1
                       subaltid = -1
                   }  else if (style.isOpenSubALTTag(tag)) {
                       subaltid++
                       insubalt = true
                   } else if (style.isCloseSubALTTag(tag)) {
                       subaltid--
                       insubalt = false
                   } else { 
                       // it's not a tag supported by the style
                       // return it to be used as a term in the text
                       Term t = new Term(tag, tindex++)
                       s_temp << t 
                       if (ne) ne.terms << t
                   }
                } else {
                    // regular terms will keep correctTermIndex counter running
                    // They may be in or out an anchor
                    term.index = tindex++   
                    if (ne) ne.terms << term
                    s_temp << term
                }
            }
            s[sindex++] = s_temp
            s_temp = new Sentence(sindex)
            tindex = 0
        }
    }  
}//class
    