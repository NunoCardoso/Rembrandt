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

import rembrandt.obj.Document
import rembrandt.obj.ListOfNE
import rembrandt.obj.Sentence
import rembrandt.obj.Term

/**
 * @author Nuno Cardoso
 * This class prints documents with specific sentence and terms format.
 */
class RembrandtWriter extends Writer { 
    
    String openSentenceSymbol = "{"
    String closeSentenceSymbol = "}\n"
    String openTermSymbol = "["
    String closeTermSymbol = "]"
    
    public RembrandtWriter(StyleTag style) {
	super(style)
    }
    
    /**
     * Print a &lt;DOC ID="" LANG=""&gt; tag.
     * @param Document The document.
     * @returns The header.
     */
    public String printDocumentHeader(Document doc) {
        String s =  "<${getDocTag()}"
      //  println "s=$s"
        if (doc.docid) s += " ${getDocidAttr()}=\"${doc.docid}\""
       // println "s=$s"
                if (doc.lang) s += " ${getDoclangAttr()}=\"${doc.lang}\""
       //         println "s=$s"
                 if (doc.taglang) s += " ${getDoctaglangAttr()}=\"${doc.taglang}\""
        //        println "s=$s"
                if (doc.rules) s += " ${getRulesAttr()}=\"${doc.rules}\""
         //       println "s=$s"
                s += ">\n"
        return s
    }
    
    /**
     * Prints a &lt;/DOC&gt; tag.
     * @param Document The document.
     * @returns &lt;/DOC&gt; tag.
     */
    public String printDocumentFooter(Document doc) {
        return  "</DOC>\n"
    }
    
    /**
     * Prints a &lt;TITLE&gt; tag.
     * @param Document The document.
     * @returns &lt;TITLE&gt; tag.
     */
    public String printDocumentHeadHeader(Document document) {
        return "<TITLE>\n"	  
    } 	

    /**
     * Prints the title sentences.
     * @param Document The document.
     * @returns title sentences in REMBRANDT format
     */
    public String printDocumentHeadContent(Document document) {   	  
        return postprocess(super.printDocumentHeadContent(document))	  
    } 	
    
    /**
     * Prints a &lt;/TITLE&gt; tag.
     * @param Document The document.
     * @returns &lt;/TITLE&gt; tag.
     */
    public String printDocumentHeadFooter(Document document) {
        return "</TITLE>\n"	  
    } 	
  
    /**
     * Prints the content of the document.
     * @param document The document to be printed.
     * @return The NE info
     */
    public String printDocumentBodyContent(Document document) {	  
        return postprocess(super.printDocumentBodyContent(document))	  
    } 	
    
    // here I make port-corrections for the final string.
    // for instance: for HTML hidden tags alone in a sentence, like </P>, the tokenizer sees it as 
    // the shadowed version (REMBRANDTTAGMARK), adds it a punctuation mark, returns as a valid sentence. 
    // so, we need to correct stuff like "}\n{</P>[.]}"
    
    public String postprocess(String t) {
        t = t.replaceAll(/(?si)\Q}\E\n\Q{\E(<\/[a-zA-Z0-9]+>)\Q[.]}\E/) {all, g1 -> return "${g1}}"}
        t = t.trim()
        return (t ? t+"\n" : "")
    }
    
    /**
     * Prints one term at a time, with the preambule tag and taking in consideration 
     * the current string.
     */
    void printTerm(Term term, String tag, StringBuffer sb) {

        if (tag)  sb.append tag
        // the term can itself be a tag... that is, a hidden HTML tag that was included meant to be printed, 
        // but not in the form of a valid term. 
        if (term.text.startsWith("<") && term.text.endsWith(">")) {
            sb.append term.text
        } else {
            term.text = term.text.replaceAll(/\Q{\E/, "\\\\{")
            term.text = term.text.replaceAll(/\Q}\E/, "\\\\}")
            term.text = term.text.replaceAll(/\Q[\E/, "\\\\[")
            term.text = term.text.replaceAll(/\Q]\E/, "\\\\]")	    
            sb.append "[${term.text}]"//+" 
        }
    }
}