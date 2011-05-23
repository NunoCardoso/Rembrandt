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
import com.thoughtworks.xstream.*

/**
 * @author Nuno Cardoso
 * This class outpus documents in XStream serialization
 */
class XStreamWriter extends Writer {
    
    public XStreamWriter(StyleTag style) {
	super(style)
    }
    
    public String printDocumentHeader(Document doc) {
        return ''
    }
    
    public String printDocumentFooter(Document doc) {
        return ''
    }
    
    public String printDocumentHeadHeader(Document doc) {
        return ''
    }
    
    public String printDocumentHeadFooter(Document doc) {
        return ''
    }
    
    public String printDocumentBodyHeader(Document doc) {
        return ''
    }
    
    public String printDocumentBodyFooter(Document doc) {
        return ''
    }    
    
    /**
     * Prints the XStream serialization of the document.
     * @param document The document to be printed.
     * @return the XStream output.
     */
    public String printDocumentHeadContent(Document document) {
        return XStream.toXML(document.title_sentence)
    }
    
    public String printDocumentBodyContent(Document document) {
        return XStream.toXML(document.body_sentence)
    }
}