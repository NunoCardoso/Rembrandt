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
import saskia.bin.Configuration

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import rembrandt.obj.Document


/** 
 * This class Pagico files to the Source Documents
 * uses STAX parser.
 XML is:
<?xml version="1.0" encoding="UTF-8"?>
<div class="mwx.collection">
      <div class="mwx.article">
	.*
	<div class="mwx.paragraph">
	.*
	<div class="mwx.section">
	.*
	
 */



public class PagicoReader extends HTMLReader {

public PagicoReader(InputStreamReader inputStreamReader, StyleTag style) {
	super(inputStreamReader, style)
	lang = Configuration.newInstance().get("global.lang")
}

public PagicoReader(StyleTag style) {
	super(style)
	lang = Configuration.newInstance().get("global.lang")
}

	/**
	 * Process the HTML input stream
	 */
	public List<Document> readDocuments(int docs_requested = 1) {

		emptyDocumentCache()
		boolean in_body = false;
		boolean langlinks = false;
		String title = null;
		BufferedReader br = new BufferedReader(inputStreamReader)
		StringBuffer buffer = new StringBuffer()
		String  line
		while ((line = br.readLine()) != null && documentsSize() <= docs_requested ) {
		//println "Read line $line"
			if (line.trim() =~ /<body>/) {
				status = ReaderStatus.INPUT_STREAM_BEING_PROCESSED
				in_body = true
			} else if (line.trim() =~ /<\/body>/) {
				in_body = false
			} else if (line.trim() =~ /<ol class=.mwx\.languagelinks.>/) {
			langlinks = true;
			} else if (line.trim().equalsIgnoreCase("</ol>") && langlinks) {
				langlinks = false;
			}			
			
			if (in_body && !langlinks && !line.trim().equalsIgnoreCase("<body>")) {
				buffer.append(line+"\n")
			}
			
			if (line.trim() =~ /<h1>.*<\/h1>/) {
				def m = line.trim() =~ /<h1>(.*)<\/h1>/
				if (m.matches()) {
					if (!title) {
						title = m.group(1)
					}
				} 
			}
		}
		
		// case there's no HTML tags
		
		if (buffer.toString().trim()) {
				def b = buffer.toString()
				if (title) {
					println "title: $title"
					b = "<HEAD>\n<TITLE>"+title+"</TITLE>\n</HEAD>\n"+b
				}
				addDocument(createDocument(b))
		}

		status = ReaderStatus.INPUT_STREAM_FINISHED
		return getDocuments()
	
	}
}
