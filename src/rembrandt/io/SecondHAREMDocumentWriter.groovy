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
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.obj.ListOfNE
import saskia.bin.Configuration
import rembrandt.bin.Rembrandt
import rembrandt.util.XMLUtil

/**
 * @author Nuno Cardoso
 * This class combines document and NEs into a printed text version, and 
 * defines how is the printing style.
 */
class SecondHAREMDocumentWriter extends Writer {
    
    String afterSentenceEnd = "\n" // little space

    public SecondHAREMDocumentWriter(StyleTag style) {
	super(style)
    }
    
    /**
     * Print the header
     */
    public String printHeader() {
        StringBuffer sb = new StringBuffer()
	sb.append XMLUtil.getXMLheader(Configuration.newInstance().get('rembrandt.output.encoding'))
        sb.append "\n<colHAREM version=\"Rembrandted by v.${Rembrandt.getVersion()}\">\n"
        return sb.toString()
    }
    
    /**
     * Print the footer
     */
    public String printFooter() {
        return "</colHAREM>\n"
    }
    
    /**
     * Print the header. Typically, the &lt;DOC&gt; tag.
     * @param Document The document.
     * @returns The header.
     */
    public String printDocumentHeader(Document doc) {
        return "<DOC DOCID=\"${doc.docid}\">\n"
    }
    
    /**
     * Prints a &lt;/DOC&gt; tag.
     * @param Document The document.
     * @returns The footer.
     */
    public String printDocumentFooter(Document doc) {
        return "</DOC>\n"
    }
    
    /**
     * prints an empty string
     */
    public String printDocumentHeadHeader(Document doc) {
        return ""
    }
    
    /**
     * Prints the title, but since there's none, I will return an ampty string
     */
    public String printDocumentHeadContent(Document document) {
       return ""
    }
    
    /**
     * prints an empty string
     */
    public String printDocumentHeadFooter(Document doc) {
        return ""
    }
    
    /**
     * prints an empty string
     */
    public String printDocumentBodyHeader(Document doc) {
        return ""
    }
    
    /**
     * prints an empty string
     */
    public String printDocumentBodyFooter(Document doc) {
        return ""
    }
}