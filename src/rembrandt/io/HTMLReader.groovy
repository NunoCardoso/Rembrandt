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

import rembrandt.obj.Document
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.obj.Term
/**
 * @author Nuno Cardoso
 * This class is a reader for HTML documents. 
 * It adds candidate NEs from <B>, <I> and <A>. It discards all other 
 */
class HTMLReader extends Reader {

	int numberanchor = 0
	int numbertag = 0

	// A (Anchor) is already handled... no need to include here.
	List<String> allowedHTMLtags = [
		"B",
		"I",
		"P",
		"BR",
		"HR",
		"H1",
		"H2",
		"H3",
		"H4",
		"H5",
		"H6",
		"OL",
		"UL",
		"LI"
	]
	List<String> newlinetags = [
		"BR",
		"P",
		"H1",
		"H2",
		"H3",
		"H4",
		"H5",
		"H6",
		"OL",
		"UL",
		"LI"
	]
	Map<String,String> anchor = [:]
	Map<String,String> tag = [:]

	String startanchormark="STARTREMBRANDTANCHORMARK"
	String endanchormark="ENDREMBRANDTANCHORMARK"
	String tagmark="REMBRANDTTAGMARK"
	String forcesentencetagmark="REMBRANDTFORCESENTENCETAGMARK"

	String lang

	public HTMLReader(InputStream inputStream, StyleTag style) {
		super(inputStream, style)
		lang = Configuration.newInstance().get("global.lang")
	}

	public HTMLReader(StyleTag style) {
		super(style)
		lang = Configuration.newInstance().get("global.lang")
	}
	
	private String saveanchor(item) {
		anchor[++numberanchor] = item
		return  " ${startanchormark}${numberanchor} "
	}

	private String loadanchor(item) {
		return item.replaceAll(/^${startanchormark}(\d+)$/) {it, cap ->
			return anchor[Integer.parseInt(cap)]
		}
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

	/**
	 * Process the input stream
	 */
	public List<Document> readDocuments(int docs_requested = 1) {
		emptyDocumentCache()
		
		BufferedReader br = new BufferedReader(
			new InputStreamReader(inputStream))
		StringBuffer buffer = new StringBuffer()
		String  line
		while ((line = br.readLine()) != null && documentsSize() <= docs_requested ) {
			status = ReaderStatus.INPUT_STREAM_BEING_PROCESSED
			buffer.append(line+"\n")
			if (line.matches(/(?i)<\/HTML>/)) {
				addDocument(createDocument(buffer.toString()))
				buffer = new StringBuffer()
				// return if there is enough of them
				if (documentsSize() >= docs_requested)
					return getDocuments()

			}
			if (line =~ /(?i)<HTML\s*LANG.*?>/) {
				line.find(/(?i)LANG="(.*?)"/) {all, g1 ->   lang = g1}
			}
		}
		
		
		// case there's no HTML tags
		if (buffer.toString().trim()) 
			addDocument(createDocument(buffer.toString()))
		
		status = ReaderStatus.INPUT_STREAM_FINISHED
		return getDocuments()

	}


	/**
	 * Create a Document from HTML text
	 * @param htmltext the HTML text
	 * @return the Document
	 */
	public Document createDocument(String htmltext) {


		String title, docid, lang
		// note that I capture the title, then I remove the tags and the title
		htmltext = htmltext.replaceAll(/(?si)<TITLE>(.*?)<\/TITLE>/) {all, g1 -> title=g1; return ""}
		htmltext = htmltext.replaceAll(/(?si)<!-- DOC ID="(.*)" LANG="(.*)" -->/) { all, g1, g2 ->
			docid=g1; lang=g2; return ""}

		// Protect the anchors, they might have pre-grounded info

		// old one: only parses href. New one parses other <A> params.
		//	htmltext = htmltext.replaceAll(/(?si)(?:<\s*A\s*HREF=["'])(.*?)(?:["'][^>]*>)([^<]*)(?:<\s*\/A\s*>)/) {all, g1, g2 ->
		htmltext = htmltext.replaceAll(/(?si)(?:<\s*A\s*)([^>]*)>([^<]*)(?:<\s*\/A\s*>)/) {all, g1, g2 ->
			// if anchor starts with http://, that's not pre-grounded info. We are not interested
			//if (g1.startsWith("http://")) return g2
			//else
			def g3 = "${saveanchor(g1)}${g2} ${endanchormark} "
			return g3
		}

		// protect allowed HTML tags, discard remaining HTML tags.
		htmltext = htmltext.replaceAll(/(?si)<\s*(\/?)([a-zA-Z0-9]+)([^>]*)\s*>/) {all, g1, g2, g3 ->

			StringBuffer sb = new StringBuffer()
			if (allowedHTMLtags.contains(g2.toUpperCase())) {
				sb.append(savetag("<${g1}${g2}${g3}>")).append(" ")
				// if it's a HTML tag that terminates a sentence, let's add a \n
				if (newlinetags.contains(g2.toUpperCase()) &&
				(g1 == "/" || g2.equalsIgnoreCase("BR") )) {
					sb.append " ${forcesentencetagmark} "
				}
			} else {  sb.append " " }
			return sb.toString()
		}


		htmltext = htmltext.replaceAll(/\n+/,"\n")

		// create a document
		Document doc = new Document(title:title, body:htmltext)
		if (docid) doc.docid = docid
		doc.lang = this.lang

		// tokenize it
		doc.tokenizeTitle()
		doc.tokenizeBody()
		// now, fetch the saved anchors, rewrite the term indexes and populate the
		// hacked NE list of the document

		// note how s is just a value, it's passed to parseSentence as a value, but then it's
		//  assigned to the collection item for the final modification.
		// BUT the doc is passed as a reference, and I'm filling it with NEs -- I'm changing it!
		// Note that the doc is not the one in the list iteration...

		parseSentences(doc.title_sentences, doc)
		parseSentences(doc.body_sentences, doc)

		// index it
		doc.indexTitle()
		doc.indexBody()
		return doc
	}//createDocument

	public void parseSentences(List<Sentence> s, Document doc) {

		int correctTermIndex = 0
		int correctSentenceIndex = 0
		boolean insideAnchor = false
		NamedEntity ne = null

		List<Sentence> s_clone = s.clone() // the cloned list is for collection walking.
		// I wikk make changes directly on the s variable (the reference sentences on the doc).

		s.clear()
		Sentence s_temp = new Sentence(correctSentenceIndex)

		for(sentence in s_clone) {
			//     println "I'm with sentence $sentence"
			sentence.each{term ->
				// if starts: create NE, halt correctTermIndex counter
				if (term.text.startsWith(startanchormark)) {
					insideAnchor = true

					/* If I want to use <A> as forcedNEs
					 // create a new NamedEntity
					 ne = new NamedEntity(sentenceIndex:correctSentenceIndex, termIndex:correctTermIndex)
					 // get anchor ref, put it in comment
					 ne.link = loadanchor(term.text)
					 //log.debug "ne.link in HTMLDocReader: "+ne.link
					 // if ends: finish NE, halt correctTermIndex counter
					 */

					// if I just want to use as regular markup
					term.index = -1 // don't increment
					term.hidden = true
					term.text = "<A "+loadanchor(term.text)+">"
					s_temp << term

					//
				} else if (term.text.equals(endanchormark)) {

					/* If I want to use <A> as forcedNEs				
					 insideAnchor = false
					 doc.forcedNEs << ne
					 ne = null
					 */					

					// if I just want to use as regular markup
					term.index = -1 // don't increment
					term.hidden = true
					term.text = "</A>"
					s_temp << term

					//

				} else if (term.text.startsWith(tagmark)){
					// it's a HTML tag: let's replace it, and hide it
					term.index = -1 // don't increment
					term.hidden = true
					term.text = loadtag(term.text)
					s_temp << term
					// force a sentence break, don't add the term -- I add it to mark this forced break
				}else if (term.text.equals(forcesentencetagmark)) {
					// but if the current sentence is just a </P> tag, it should belong to the
					// previous sentence... let's correct that
					if (s_temp.size() == 1 && s_temp[0].text.equalsIgnoreCase("</p>")) {
						//	println "Correcting, adding ${s_temp} to ${correctSentenceIndex -1}"
						Term pterm = new Term(s_temp[0].text, -1)
						pterm.hidden = true
						s[(correctSentenceIndex-1)] << pterm
					} else {
						//	println "Adding $s_temp to $correctSentenceIndex"
						s[correctSentenceIndex++] = s_temp
					}
					s_temp = new Sentence(correctSentenceIndex)
					correctTermIndex = 0

				} else {
					// regular terms will keep correctTermIndex counter running
					// They may be in or out an anchor
					term.index = correctTermIndex++
					//	if (insideAnchor) println "Adding term $term to NE"

					/* If I want to use <A> as forcedNEs				
					 if (insideAnchor) ne.terms << term
					 */
					s_temp << term
				}
			}
			// if the s_temp has terms, let's add it.
			// if it's empty, it's probably because of a forced sentence break followed by a normal sentence break. THis will hold the
			// sentence_temp empty for the next s_clone batch of terms.
			if (!s_temp.isEmpty()) {

				// 	println ""+(s_temp.size() == 1)+" "+s_temp[0].text+" "+
				//       	(s.size() > 1 ? s[(correctSentenceIndex-1)] : null)


				// Ugly hack: when the tokenizer sees "XX . YY", where YY is a disguised </P> tag, it converts to "XX . YY ."
				// so, we have "XX . </P> ." We added </P> to the right sentence before. Here, we'll just erase the added period.
				try {
					if (s_temp.size() == 1 && s_temp[0].text.equals(".") &&
					s[(correctSentenceIndex-1)] && s[(correctSentenceIndex-1)].size() >= 2 &&
					s[(correctSentenceIndex-1)][-1].text.equalsIgnoreCase("</p>") &&
					s[(correctSentenceIndex-1)][-2].text.matches(/[\.!?]/)) {
						// println "Correcting, dropping sentence with a period."
					} else {
						//println "End:Adding $s_temp to $correctSentenceIndex"
						s[correctSentenceIndex++] = s_temp
					}
				}catch(java.lang.ArrayIndexOutOfBoundsException e) {}
				s_temp = new Sentence(correctSentenceIndex)
				correctTermIndex = 0
			}
		}
	}
}//class