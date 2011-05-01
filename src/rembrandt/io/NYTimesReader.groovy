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

import java.util.List;
import java.util.regex.*

/**
 * @author Nuno Cardoso
 * This is a reader for the NYT collection
 */
class NYTimesReader extends Reader {

	public NYTimesReader(InputStream inputStream, StyleTag style) {
		super(inputStream, style)
	}
	
	public NYTimesReader(StyleTag style) {
		super(style)
	}
	
	/**
	 * Process the HTML input stream
	 */
	public List<Document> readDocuments(int docs_requested = 1) {

		emptyDocumentCache()

		BufferedReader br = new BufferedReader(			
			new InputStreamReader(inputStream))

		StringBuffer buffer = new StringBuffer()
		String line
		Matcher m

		while ((line = br.readLine()) != null  && documentsSize() <= docs_requested ) {
			buffer.append(line+"\n")
			status = ReaderStatus.INPUT_STREAM_BEING_PROCESSED
			if (line.matches(/<\/DOC>/)) {
				addDocument(createDocument(buffer.toString()))
				buffer = new StringBuffer()
				if (documentsSize() >= docs_requested) 
					return getDocuments()
			}
		}
		// case there's no HTML tags
		if (buffer.toString().trim()) 
			addDocument(createDocument(buffer.toString()))
		
		status = ReaderStatus.INPUT_STREAM_FINISHED
		return getDocuments()			
	}

	public Document createDocument(String text) {

		Matcher m
		boolean indoc = false
		boolean intext = false
		boolean inheadline = false
		boolean indateline = false
		GregorianCalendar date_created
		String id
		StringBuffer title
		StringBuffer body
		String headline
		String lang = "en"

		String docid = null
		int total = 0

		text.split (/\n+/).each{l ->

			m = l =~ /<DOC id="NYT_ENG_(\d{4})(\d{2})(\d{2})\.(\d+)" type=".*"\s?>/
			if (m.matches()) {
				if (indoc || intext || inheadline || indateline)
					throw new IllegalStateException(" Reading line $l, but I'm still in doc!")
				id = "NYT_ENG_"+m.group(1)+m.group(2)+m.group(3)+"."+m.group(4)
				indoc = true
				headline = null
				date_created = new GregorianCalendar()
				date_created.set(Calendar.YEAR, Integer.parseInt(m.group(1)) )
				date_created.set(Calendar.MONTH,  Integer.parseInt(m.group(2)) -1)
				date_created.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)) )
				date_created.set(Calendar.HOUR, 0 )
				date_created.set(Calendar.MINUTE, 0)
				date_created.set(Calendar.SECOND, 0 )
				title = new StringBuffer()
				body = new StringBuffer()
			}
			m = l =~ /<\/DOC>/
			if (m.matches()) {
				if (!indoc || intext || inheadline || indateline)
					throw new IllegalStateException(" Reading line $l, but I'm not in a doc!")
				indoc = false
			}
			m = l =~ /<HEADLINE>/
			if (m.matches()) {
				if (!indoc || intext || inheadline || indateline)
					throw new IllegalStateException(" Reading line $l, but I'm still in headline!")
				inheadline = true
			}
			m = l =~ /<\/HEADLINE>/
			if (m.matches()) {
				if (!indoc || intext || !inheadline || indateline)
					throw new IllegalStateException(" Reading line $l, but I'm not in headline!")
				inheadline = false
			}
			m = l =~ /<DATELINE>/
			if (m.matches()) {
				if (!indoc || intext || inheadline || indateline)
					throw new IllegalStateException(" Reading line $l, but I'm still in dateline!")
				indateline = true
			}
			m = l =~ /<\/DATELINE>/
			if (m.matches()) {
				if (!indoc || intext || inheadline || !indateline)
					throw new IllegalStateException(" Reading line $l, but I'm not in dateline!")
				indateline = false
			}
			m = l =~ /<TEXT>/
			if (m.matches()) {
				if (!indoc || intext || inheadline || indateline)
					throw new IllegalStateException(" Reading line $l, but I'm still in text!")
				intext = true
			}
			m = l =~ /<\/TEXT>/
			if (m.matches()) {
				if (!indoc || !intext || inheadline || indateline)
					throw new IllegalStateException(" Reading line $l, but I'm not in text!")
				intext = false
			}
			m = l =~ /<P>/
			if (m.matches()) {
				body.append "<P>\n"
			}
			m = l =~ /<\/P>/
			if (m.matches()) {
				body.append "\n</P>\n"
			}
			// catch all

			if (inheadline) {
				title.append l+" "
			}
			if (indateline) {
			}
			if (intext) {
				body.append l +" "
			}
		}// each line

		// create a document
		Document doc = new Document(title:title, body:body)
		if (docid) doc.docid = docid
		if (lang) doc.lang = lang

		// tokenize it
		doc.tokenizeTitle()
		doc.tokenizeBody()
		// now, fetch the saved anchors, rewrite the term indexes and populate the
		// hacked NE list of the document

		// note how s is just a value, it's passed to parseSentence as a value, but then it's
		// assigned to the collection item for the final modification.
		// BUT the doc is passed as a reference, and I'm filling it with NEs -- I'm changing it!
		// Note that the doc is not the one in the list iteration...

		//	parseSentences(doc.title_sentences, doc)
		//	parseSentences(doc.body_sentences, doc)

		// index it
		doc.indexTitle()
		doc.indexBody()
		return doc
	}
}