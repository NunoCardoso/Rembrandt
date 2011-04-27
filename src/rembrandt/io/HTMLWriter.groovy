
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
import rembrandt.obj.Sentence
import rembrandt.obj.NamedEntity
import rembrandt.obj.ListOfNE
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * This class combines Document and NEs into a HTML-ish version.
 */
class HTMLWriter extends Writer {
    
    public HTMLWriter(StyleTag style) {
	super(style)
    }
    
    /**
     * Print the header. Typically, the &lt;HTML&gt tag.
     * @param Document The document.
     * @returns The header.
     */
    public String printDocumentHeader(Document doc) {
        return "<HTML LANG=\"${doc.lang}\">\n"
    }
    
    /**
     * Prints the footer. Typically, the &lt/HTML&gt tag.
     * @param Document The document.
     * @returns The footer.
     */
    public String printDocumentFooter(Document doc) {
        return  "</HTML>\n"
    }
    
    public String printDocumentHeadContent(Document document) {
        StringBuffer sb = new StringBuffer()
        sb.append "<!-- DOC ID=\"${document.docid}\" LANG=\"${document.lang}\" -->\n"
        sb.append "<TITLE>"
        document.title_sentences?.each {sentence ->
           sb.append printSentence(sentence, document.titleNEs)
        }
        sb.append "</TITLE>"
        return sb.toString().trim()+"\n"
    }  
    
    public String printDocumentBodyContent(Document document) {
        String s = super.printDocumentBodyContent(document)
        //post-processing
        s = s.replaceAll(/(?i)<\/p>\s*/) {it -> "$it\n"}
        return s
    }
}