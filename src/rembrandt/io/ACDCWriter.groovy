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
import rembrandt.obj.TermWithPoS
import saskia.bin.Configuration
/**
 * @author Nuno Cardoso
 * This class is a writer for ACDC documents (see <a href="http://www.linguateca.pt/ACDC">Linguateca's ACDC page</a> for additional info.) 
 */
class ACDCWriter extends Writer {

	String openSentenceSymbol = "<s>\n"
	String closeSentenceSymbol = "</s>\n"

	public ACDCWriter(StyleTag style) {
		super(style)
	}

	/**
	 * Print a document header, the &lt;DOC&gt; tag.
	 * @param Document The document
	 * @returns The header
	 */
	public String printDocumentHeader(Document doc) {
		return "<DOC docid=\"${doc.docid}\">\n<EM \"${doc.property['EM']}\"\n"+
		"<data ${doc.property['data']}>\n<CATEGORY ${doc.property['CATEGORY']}>\n"
	}

	/**
	 * Prints an empty string
	 */
	public String printDocumentHeadHeader(Document doc) {
		return ""
	}

	/**
	 * Prints an empty string
	 */
	public String printDocumentHeadFooter(Document doc) {
		return ""
	}

	/**
	 * Prints an empty string
	 */
	public String printDocumentBodyHeader(Document doc) {
		return ""
	}

	/**
	 * Prints an empty string
	 */
	public String printDocumentBodyFooter(Document doc) {
		return ""
	}

	/**
	 * it's always one tag or term per line
	 */
	void printTerm(Term term, String tag, StringBuffer currentString) {

		String lastChar = (currentString.size() > 0 ?
				currentString.substring(currentString.size()-1, currentString.size()) : "")


		// when closing sentences, it does not get a newline
		if (!lastChar || lastChar != "\n") currentString.append("\n")

		if (tag) {currentString.append "$tag\n" }
		// hided the stuff in term.lemma
		if (term instanceof TermWithPoS) currentString.append term.text+"\t"+term.lemma+"\n"
		else if (term instanceof Term) currentString.append term.text+"\n"
	}

}