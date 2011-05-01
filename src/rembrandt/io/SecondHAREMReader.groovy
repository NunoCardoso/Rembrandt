package rembrandt.io

import java.util.List;

import rembrandt.obj.Document
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence

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


/**
 * @author Nuno Cardoso
 * This class is a reader for the Second HAREM Collection format, in XML.
 * It is this: 
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;{encoding}&quot; ?&gt;<BR>
 * &lt;colHAREM versao=&quot;{version}&quot;&gt;<BR>
 * &lt;DOC DOCID=&quot;{docid}&quot;&gt;</BR>
 * (text goes here)<BR>
 * &lt;/DOC;&gt;</BR>
 * &lt;colHAREM&gt;</BR>
 * 
 * So, we use Groovy's XmlParser. 
 */
class SecondHAREMReader extends Reader {
      
    def startemmark="STARTREMBRANDTEMMARK"
    def endemmark="ENDREMBRANDTEMMARK"
    def startpmark="STARTREMBRANDTPMARK"
    def endpmark="ENDREMBRANDTPMARK"
    def startaltmark="STARTREMBRANDTALTMARK"
    def endaltmark="ENDREMBRANDTALTMARK"
    def startomitidomark="STARTREMBRANDTOMITIDOMARK"
    def endomitidomark="ENDREMBRANDTOMITIDOMARK"
    def altseparatormark="ALTSEPARATORMARK"
    
    Map<String,String> tags = [:] 
    int number = 0
    
    public SecondHAREMReader(InputStream inputStream, StyleTag style) {
		super(inputStream, style)
    }

	public SecondHAREMReader(StyleTag style) {
		super(style)
	}
	
    private String save(item) {
        tags[++number] = item
        return  " ${startemmark}${number} "
    }
    
    
    private String load(item) {
        return item.replaceAll(/^${startemmark}(\d+)$/) {it, cap -> 
            return tags[Integer.parseInt(cap)]
        }
    }
    
 	public List<Document> readDocuments(int docs_requested = 1) {

		emptyDocumentCache()

		BufferedReader br = new BufferedReader(	
					new InputStreamReader(inputStream))

        StringBuffer buffer = new StringBuffer()		    
        String line
        boolean indoc = false
        String docid = null
        
        while ((line = br.readLine()) != null   && documentsSize() <= docs_requested) {          
			status = ReaderStatus.INPUT_STREAM_BEING_PROCESSED
			
            if (line.matches(/\s*<\/DOC>\s*/)) {
                buffer.append(line+"\n")
                addDocument(createDocument(buffer.toString()) )
                buffer = new StringBuffer()
				if (documentsSize() >= docs_requested)
					return getDocuments()

                docid = null
                indoc = false
            } else if (line.matches(/\s*<DOC DOCID=".*?">\s*/)) {                
                buffer = new StringBuffer()
                buffer.append(line+"\n")
                indoc = true
            } else {
        	if (indoc) buffer.append(line+"\n")
            }       
        }  
		
		status = ReaderStatus.INPUT_STREAM_FINISHED
		return docs

    }

    /**
     * Create a Document from Second HAREM Document
     * @param htmltext the HTML text
     * @return the Document
     */
    public Document createDocument(String text) {
        
      // println "text: $text"
        String docid = null
        String doc = null
        text.findAll(/(?si)<DOC DOCID="(.*?)">(.*?)<\/DOC>/) {all, g1, g2 -> 
            docid=g1;  doc=g2 }
        
        //println "docid: $docid"
        //println "doc: $doc"
        // protect starting EM tags
        doc = doc.replaceAll(/(?si)(<EM [^>]*?>)/) {all, g1->  return " ${save(g1)} "}
        // protect ending EM tags
        doc = doc.replaceAll(/<\/EM>/, " $endemmark ")
        doc = doc.replaceAll(/<P>/, " $startpmark ")
        doc = doc.replaceAll(/<\/P>/, " $endpmark ")
        doc = doc.replaceAll(/<ALT>/, " $startaltmark ")
        doc = doc.replaceAll(/<\/ALT>/, " $endaltmark ")	    	
        doc = doc.replaceAll(/<OMITIDO>/, " $startomitidomark ")
        doc = doc.replaceAll(/<\/OMITIDO>/, " $endomitidomark ")
        // protect alt separators... hopefully, there are no pipes on real text...
        doc = doc.replaceAll(/\|/, " $altseparatormark ")
                
        // now, let's atomize it... 
        Document d = new Document(docid:docid, body:doc)
        d.tokenizeBody()
        parseSentences(d.body_sentences, d)
        d.indexBody()
        return d
    }//createDocument	
     
    public parseSentences(List<Sentence> s, Document doc) {
               
	int correctTermIndex = 0
	int correctSentenceIndex = 0
    
	boolean inalt = false 
        boolean inomitido = false
        long alt = null // unique id to span all NEs in the whole ALT
        int subalt = null // id for each subalt
        int termIndexStartAlt = null
        int termIndexEndAlt = null
        
	NamedEntity ne = null
    
        List<Sentence> s_clone = s.clone() // the cloned list is for collection walking. 
    // I will make changes directly on the s variable (the reference sentences on the doc).
    
        s.clear()
        Sentence s_temp = new Sentence(correctSentenceIndex)
    
	for(sentence in s_clone) { 
          //  println "I'm with sentence $sentence"
            sentence.each{term -> 
        // if starts: create NE, halt correctTermIndex counter
	         
        // case a new named entity:
	    	if (term.text.startsWith(startemmark)) {
	    	    ne = style.parseOpenTag(load(term.text))
		    ne.sentenceIndex = correctSentenceIndex
	    	    ne.termIndex = correctTermIndex
		    if (inalt) {
		       ne.alt = alt 
		       ne.subalt = subalt
                   }
		   log.trace "Got $term, opened NE $ne, alt=${ne.alt}, subalt=$subalt"
	    	} else if (term.text == endemmark) {
	    	    doc.bodyNEs << ne
	    	    ne = null  
	    	    log.trace "Got $term, added NE to doc"				    
	    	} else if (term.text == startpmark) {   
                    s_temp = new Sentence(correctSentenceIndex)
                    correctTermIndex = 0
                    term.index =-1
                    term.hidden = true
                    term.text = "<P>"
                    s_temp << term
                    log.trace "Got $term, started new Sentence ($correctSentenceIndex-1), term index set to 0"
                            
                } else if (term.text == endpmark) {
                    term.index = -1
            	    term.hidden = true
            	    term.text = "</P>"
                    s_temp << term
                    log.trace "Got $term, added Sentence $correctSentenceIndex to list, adding +1."
                    s[correctSentenceIndex++] = s_temp
                
                } else if (term.text == startaltmark) {
                    inalt = true // mark begin of alt
                    alt = new Date().getTime() // identify this ALT with the long date
                    subalt = 1 // mark first subalt
                    termIndexStartAlt = correctTermIndex // mark beginner  term
                    log.trace "Got $term, set alt=$alt, set termIndexStartAlt=$correctTermIndex, subalt to 1"
                    
                } else if (term.text == endaltmark) {
                    inalt = false 
                    alt = null 
                    subalt = null 
                    termIndexStartAlt = null
                    termIndexEndAlt = null
                    log.trace "Got $term, nulled alt, subalt, termaltstart and end"
                    
                } else if (term.text == startomitidomark) {
                    inomitido=true
                    term.index = -1
                    term.hidden = true
                    term.text = "<OMITIDO>"
                    s_temp << term
                    log.trace "Got $term, started omitido"
                } else if (term.text == endomitidomark) {
                    inomitido=false
                    term.index = -1
                    term.hidden = true
                    term.text = "</OMITIDO>"
                    s_temp << term
                    log.trace "Got $term, ended omitido"
                } else if (term.text == altseparatormark) {
                    if (inalt) {
                        subalt++ // switch subalt
                        if (!termIndexEndAlt) termIndexEndAlt = correctTermIndex
                        correctTermIndex = termIndexStartAlt // backtrack the correctTermIndex,
                        log.trace "Got $term in ALT, subalt is now $subalt, termIndexEndAlt set to $termIndexEndAlt, correctTermIndex set to $termIndexStartAlt"
                    } else {
                        term.index = correctTermIndex++
                        term.text = "|"
                        s_temp << term
                        log.trace "Got $term but NOT in ALT, added "
                    }
                        // because it's needed to get the correct index for NEs in ALT.				    
                } else {
                    // if not in alt, just add the term
                    if (!inalt) {
                        term.index = correctTermIndex++
                        s_temp << term
                        // add to the NE if there is one
                        if (ne) {  ne.terms << term }
                        log.trace "Got $term, not in ALT, added "
                    } else if (inalt) {
                        term.index = correctTermIndex++
                        // add terms only if we are at subalt=1 -- avoid repeats.
                        if (subalt == 1) {
                            s_temp << term
                            log.trace "Got $term in ALT, added because subalt is 1 "
                        }
                        if (ne) { ne.terms << term }                      
                    }                   
               }           
           }//s.each term
            
            // sentence_temp empty for the next s_clone batch of terms. 
            if (!s_temp.isEmpty()) {
                try {
                    if (s_temp.size() == 1 && s_temp[0].text.equals(".") && 
                    s[(correctSentenceIndex-1)] && s[(correctSentenceIndex-1)].size() >= 2 && 
                    s[(correctSentenceIndex-1)][-1].text.equalsIgnoreCase("</p>") && 
                    s[(correctSentenceIndex-1)][-2].text.matches(/[\.!?]/)) {
                        // println "Correcting, dropping sentence with a period."     
                    } else {
                 s[correctSentenceIndex++] = s_temp
                }
               }catch(java.lang.ArrayIndexOutOfBoundsException e) {}
                 s_temp = new Sentence(correctSentenceIndex)
                 correctTermIndex = 0
            }
        }// for each sentence
    }
}