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

import org.apache.log4j.Logger
import rembrandt.obj.Document
import rembrandt.obj.ListOfNE
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.obj.Term

/**
 * @author Nuno Cardoso
 * Public class from where printer stuff must extend.
 */
 
abstract class Writer {
    
    StyleTag style
    static Logger log = Logger.getLogger("Writer")
    
    String docTag = "DOC"
    String docidAttr = "DOCID"
    String doclangAttr = "LANG"
    String doctaglangAttr = "TAGLANG"
    String rulesAttr = "RULES"
    String headTag = "HEAD"
    String bodyTag = "BODY"
    String fileTag = null
    
    String afterFileHeaderTag = "\n"
    String afterFileFooterTag = "\n"
    String afterDocumentHeaderTag = "\n"
    String afterDocumentFooterTag = "\n"
    String afterDocumentHeadHeaderTag = "\n"
    String afterDocumentHeadFooterTag = "\n"
    String afterDocumentBodyHeaderTag = "\n"
    String afterDocumentBodyFooterTag = "\n"
    String afterDocumentHeadContent = "\n"
    String afterDocumentBodyContent = "\n"
        
    String beforeSentenceBegin = ""
    String afterSentenceEnd = " " // little space
    String beforeTermBegin = ""
    String afterTermEnd = ""
    
    String openSentenceSymbol = ""
    String closeSentenceSymbol = ""
    String openTermSymbol = ""
    String closeTermSymbol = ""
    
    /**
     * All writers must have a style tag, to define how a NE should be tagged
     * @param style the StyleTag object
     */
    public Writer(StyleTag style) {
	this.style=style
    }
    
    /**
     * The output stream header
     */
    public String printHeader() {
 	 String s = "<!-- Rembrandted by v.${rembrandt.bin.Rembrandt.getVersion()} -->\n"
         if (getFileTag()) s += "<${getFileTag()}>${getAfterFileHeaderTag()}"
         return s      
    }
    
    /**
     * The output stream footer
     */
    public String printFooter() {
        return getFileTag() ? "</${getFileTag()}>${getAfterFileFooterTag()}" : ""
     }
        
    /**
     * The document header
     */
    public String printDocumentHeader(Document doc) {
	return getDocTag() ? "<${getDocTag()} ${getDocidAttr()}=\"${doc.docid}\" ${getDoclangAttr()}=\"${doc.lang}\">${getAfterDocumentHeadTag()}" : ""
    }

    public String printDocumentFooter(Document doc) {
	return getDocTag() ? "</${getDocTag()}>${getAfterDocumentFooterTag()}" : ""
    }

    public String printDocumentHeadHeader(Document doc) {
	return getHeadTag() ? "<${getHeadTag()}>${getAfterDocumentHeadHeaderTag()}" : ""
    }

    public String printDocumentHeadContent(Document doc) {
	StringBuffer sb = new StringBuffer()
	doc.title_sentences?.each {sentence ->
	    sb.append printSentence(sentence, doc.titleNEs)  // fim de frase
	}// end doc.sentence.eachWithIndex
	String res = sb.toString().trim() 
	return (res ? res + getAfterDocumentHeadContent() : "")
    }

    public String printDocumentHeadFooter(Document doc)  {
 	return getHeadTag() ? "</${getHeadTag()}>${getAfterDocumentHeadFooterTag()}" : ""
    }

    public String printDocumentBodyHeader(Document doc)  {      
	return getBodyTag() ? "<${getBodyTag()}>${getAfterDocumentBodyHeaderTag()}" : ""
    }
    
    public String printDocumentBodyContent(Document doc) {
	StringBuffer sb = new StringBuffer()
	doc.body_sentences?.each{sentence ->
	   sb.append printSentence(sentence, doc.bodyNEs)  // fim de frase
	}// end doc.sentence.eachWithIndex
	return sb.toString().trim() + getAfterDocumentBodyContent() 		  
    }

    public String printDocumentBodyFooter(Document doc)  {
	return getBodyTag() ? "</${getBodyTag()}>${getAfterDocumentBodyFooterTag()}" : ""
    }

    /**
     * Print full document - calls, in order:
     *  printDocumentHeader(doc) 
     *  printDocumentHeadHeader(doc) 
     *  printDocumentHeadContent(doc) 
     *  printDocumentHeadFooter(doc) 
     *  printDocumentBodyHeader(doc) 
     *  printDocumentBodyContent(doc) 
     *  printDocumentBodyFooter(doc) 
     *  printDocumentFooter(doc) 
     */
    String printDocument(Document doc) {
	StringBuffer sb = new StringBuffer()
	sb.append(printDocumentHeader(doc) )
	sb.append(printDocumentHeadHeader(doc) )
	sb.append(printDocumentHeadContent(doc) )
	sb.append(printDocumentHeadFooter(doc) )
	sb.append(printDocumentBodyHeader(doc) )
	sb.append(printDocumentBodyContent(doc) )
	sb.append(printDocumentBodyFooter(doc) )
	sb.append(printDocumentFooter(doc) )
	return sb.toString()
   }  
   
    /**
     * Prints a term into the given Stringbuffer.
     * I pass StringBuffer to the method, because I may want to know how the current string is doing, 
     * so that I decide if I want to trim it or adding whitespaces depending on the tag to add
     */
   void printTerm(Term term, String tag, StringBuffer currentString) {
     
      // order: ${leftspace}${tag}${rightspace}${term}
     String leftspace = " ", rightspace = " "
     String lastChar = (currentString.size() > 0 ? 
	     currentString.substring(currentString.size()-1, currentString.size()) : "")

     if (tag?.startsWith("</")) leftspace = ""	  
     if (!currentString) leftspace = ""	 
     if (lastChar ==~ /[\{\(]/) leftspace = ""
	 
     if (currentString && lastChar.equals("\n")) rightspace = ""
     if (!tag.startsWith("</")) rightspace = ""
     if (term.text ==~ /[\.,:;!?\)\}]/) {
	 rightspace = ""
	 if (!tag) leftspace = ""    
     }
     //	res += "${space}${XMLUtil.encodeXML(term.text)}"//+" 
	 
   //println "leftspace: ${(leftspace != '')} tag:$tag rightspace:${(rightspace != '')} term.text:${term.text} lastchar:$lastChar" 	 
     currentString.append "${leftspace}${tag}${rightspace}${beforeTermBegin}${term.text}${afterTermEnd}" 
    }

  
   /**
    * Prints a sentence
    */
   String printSentence(Sentence sentence, ListOfNE NEs) {
       if (sentence.isEmpty()) return
       
       List<NamedEntity> lastNEs = null, currentNEs = null
       List<Term> haltedTerms = []
       
       StringBuffer sb = new StringBuffer()
       sb.append getBeforeSentenceBegin()
       sb.append getOpenSentenceSymbol()       
       
       sentence.pointer = 0
       
       while (sentence.pointer < sentence.size()) { // note that we want to walk all visible and hidden terms!
           Term term = sentence[sentence.pointer]
           //println "XXX sentence.pointer = ${sentence.pointer} term = $term term.index = ${term.index} term.hidden = ${term.hidden}"

           // if term is hidden                     
           if (term.hidden) {
               
               // if there's no lastNE open, print it
               if (!lastNEs) {
        	   		printTerm(term, "", sb)
        	   //"\nXXX hidden term: $term\n"
               } else {
        	// if there is, wait. Let's hand the term.hidden to a buffer, then we'll call it after closing the tag 
        	   		haltedTerms << term
        	   //"\n hidden termgone to haltedTerms\n"
               }
    	       sentence.pointer++    	    
           } else {
               
               
              // imagine this: <EM>United <B>States</B> of America</EM>
                                
    	       currentNEs = NEs.getNEsBySentenceAndTermIndex(sentence.index, term.index, NEs.fetchByOverlappingTerm)		    		    
               //println "\nXXX currentNEs: ${currentNEs} for ${sentence.index}, ${term.index}\n"

               if (currentNEs.find{it.alt}) {	     
               
               /**** FOR NEs with ALT *****/					     
               
               // first, flush all LastNEs that are still open
               // The sentence may be over, but NEs are not closed yet
              
                   lastNEs?.each{sb.append style.printCloseTag(it)}
                   lastNEs = null
               
                   String altid = currentNEs[0].alt // take the ALT ID from the first one 
                   List<NamedEntity> altNEs = NEs.getNEsByAltId(altid)
               
                   // Know the boundaries and depth of the whole ALT
                   int leftBoundaryAlt = sentence.pointer            
                   int rightBoundaryAlt = 0, howManyAlts = 0 
                   // look the rightmost term of the NEs to indicate the rightmost ALT position
                   altNEs.each{ne ->                
                     int pointerOfRightmostNETerm = sentence.findPointerOfTermWithIndex(ne.terms[-1].index )
                     if (pointerOfRightmostNETerm > rightBoundaryAlt) rightBoundaryAlt = pointerOfRightmostNETerm
                     if (ne.subalt > howManyAlts) howManyAlts = ne.subalt
                   }                
               
                   sb.append style.printOpenALTTag()		     
               
                   for (int altindex = 1; altindex <= howManyAlts; altindex++ ) {
                   
                      sb.append style.printOpenSubALTTag(altindex)	
                   
                      List<NamedEntity> lastAltNEs = null 
                   
                   // make several reproductions of the terms within the ALT
                   // don't mess up the sentence.pointer, use another iterator variable.
                      for (int pointerAlt = leftBoundaryAlt; pointerAlt <= rightBoundaryAlt; pointerAlt++) {
 
                	  Term altTerm = sentence[pointerAlt]
                           
                	  List<NamedEntity> currentAltNEs = NEs.getNEsBySentenceAndTermIndexAndAlt(
                               sentence.index, altTerm.index, altid, altindex, NEs.fetchByOverlappingTerm)	
                                   
                           // check if there's open NEs to close - generate a tag
                          String tag = printTags(currentAltNEs, lastAltNEs, haltedTerms)
                       // print a term, preceeded by a tag if necessary. 
                          printTerm(altTerm, tag, sb)
                          lastAltNEs = currentAltNEs
                      }
                   // the ALT may be over, but NEs are not closed yet
                      lastAltNEs?.each{sb.append style.printCloseTag(it)}                      					  
                      sb.append style.printCloseSubALTTag(altindex)
                   }// for altindex
               
                   sb.append style.printCloseALTTag()	
                   sentence.pointer = rightBoundaryAlt // jump to the end of the ALT
                   // leave it at rightBoundary, because there's a sentence.pointer++ in the end, which will reposition to the next unseen term.                   
               
               } else { // if currentNes.find it.alt
               
                   /**** FOR NEs without ALT *****/
                   String tag = printTags(currentNEs, lastNEs, haltedTerms)    		    
                   printTerm(term, tag, sb)		    
                   lastNEs = currentNEs
               } 
    	   sentence.pointer++
           } // if !term.hidden
       }// while sentencepointer
       
       // the sentence may be over, but NEs are not closed yet
       lastNEs?.each {sb.append style.printCloseTag(it)}
    
       // getAfterSentenceEnd() forces a getter of the variable afterSentenceEnd automagically
       // (thank you, Groovy). If I used only afterSentenceEnd, it'll just use the one in this class,  
       // and thus was not forced to resort to the sub classes
       return sb.toString().trim() + getCloseSentenceSymbol() + getAfterSentenceEnd()    
   }
   
   /**
    * Output tags if there are NEs that are beginning and/or ending.
    */
   String printTags (List<NamedEntity>currentNEs, List<NamedEntity>lastNEs, List<Term> haltedTerms)  {

       StringBuffer sb = new StringBuffer()
       
       if (currentNEs) {
           if (lastNEs) {
               if (currentNEs != lastNEs) {
                   // There's a change on NEs, let's print that change	
                   // first, close all NEs that are in LastNE but not in CurrentNE
                   lastNEs.findAll{!currentNEs.contains(it)}.each {ne ->							
                       sb.append style.printCloseTag(ne)
                   }
                   // if there's halted terms, print them when all NEs are closed. that is, here.
                   haltedTerms?.each{term -> printTerm(term,"",sb)}
                   haltedTerms.clear()
                   // second, open all NEs that are in CurrentNE, but not in LastNE
                   currentNEs.findAll{!lastNEs.contains(it)}.each {ne ->
                       sb.append style.printOpenTag(ne)	                       
                   }					
               }
           } else { // lastNEs are null or empty              		
               currentNEs.each {sb.append style.printOpenTag(it) }         	
           }
       } else { // currentNEs are null or empty
           lastNEs?.each{sb.append style.printCloseTag(it)}
           // if there's halted terms, print them when all NEs are closed. that is, here.
           haltedTerms?.each{term -> printTerm(term,"",sb)}
           haltedTerms.clear()
       }
       return sb.toString()
   } 
}