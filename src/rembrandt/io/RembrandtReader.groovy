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

import java.util.regex.Matcher
import rembrandt.obj.Document
import rembrandt.obj.ListOfNE
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.obj.Term
import rembrandt.obj.SemanticClassification
import org.apache.log4j.Logger
/**
 * @author Nuno Cardoso
 * This class is a reader for Rembrandted documents. 
 */
class RembrandtReader extends Reader {

	public RembrandtReader(StyleTag style) {
		super(style)
	}

	/**
	 * Process the HTML input stream
	 */
	public void processInputStream(InputStreamReader is) {
		BufferedReader br = new BufferedReader(is)
		StringBuffer buffer = new StringBuffer()
		String line
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("%") && !line.startsWith("#") && !(line ==~ /^\s*$/))
				buffer.append(line+"\n")

			if (line ==~ /<\/DOC>/) {
				docs << createDocument(buffer.toString())
				buffer = new StringBuffer()
			}
		}
		if (buffer.toString().trim()) {
			docs << createDocument(buffer.toString())
		}
	}

	public Document createDocument(String text) {

		// 0: outside a sentence, not in a tag
		// 1: outside a sentence, in a tag

		// 10: inside a sentence, outside a term, not in a tag
		// 11: inside a sentence, outside a term, in a tag

		// 20: inside a term
		// 99: inside a term, escaped char

		Document doc = new Document()

		int state = 0

		Sentence s = null // collector for sentences
		Term t = null // collector for terms
		String tag = null // collector for all kinds of tags

		NamedEntity ne = null

		int sindex = 0
		int tindex = 0

		boolean inalt = false
		boolean insubalt = false

		long altid
		int subaltid

		boolean intitle = false
		boolean inbody = false

		text.trim().each{c ->

			switch(state) {
				case 0:
					if (c == "{") {
						s = new Sentence(sindex)
						tindex = 0
						state = 10
					}
					if (c == "<") {
						tag = c
						state = 1
					}
					break

				case 1:
					tag += c
					if (c == ">") {
						state = 0
						Matcher m = tag =~ /<DOC([^>]*)>/

						// DOC

						if (m.matches()) {

							Map hash_params = [:]

							// Hack to overcome bugs that the first line was
							// <DOC DOCID="wpt-1001951616378060469" LANG="pt"TAGLANG="pt" RULES="harem">
							// separate LANG and TAGLANG
							String header = m.group(1)
							header = header.replaceAll(/"TAGLANG="/,"\" TAGLANG=\"")
							header.split(/\s+/).each{it ->
								if (it) {
									Matcher m2 = it =~ /^(.*?)="(.*)"$/
									if (m2.matches()) {
										hash_params[m2.group(1)] = m2.group(2)
									} else {
										log.warn "Can't understand $it."
									}
								}
							}

							if (hash_params.DOCID) doc.docid = hash_params.DOCID
							if (hash_params.LANG) doc.lang = hash_params.LANG
							if (hash_params.TAGLANG) doc.taglang = hash_params.TAGLANG
							if (hash_params.RULES) doc.rules = hash_params.RULES
						}

						// META
						m = tag =~ /<META name="([^"]*)" content="([^"]*)">/
						if (m.matches()) {
							doc.property[m.group(1)] = m.group(2)
						}

						if (tag.equalsIgnoreCase("<TITLE>")) intitle = true
						if (tag.equalsIgnoreCase("</TITLE>")) intitle = false
						if (tag.equalsIgnoreCase("<BODY>")) {
							inbody = true
							// reset sentence index
							sindex = 0
						}
						if (tag.equalsIgnoreCase("</BODY>")) {
							inbody = false
						}
						tag = null
					}
					break

				case 10:
					if (c == "[") {
						t = new Term("", tindex++)
						state = 20
					} else if (c == "}") {
						if (intitle) doc.title_sentences[sindex++] = s
						if (inbody) doc.body_sentences[sindex++] = s
						// accept documents that have no metatags, just sentences - everything goes to the body
						if (!intitle && !inbody) doc.body_sentences[sindex++] = s
						state=0
					} else if (c == "<") {
						tag = c // destroy last string, start new tag
						state = 11
					}
					break

				case 11:
					tag += c
					if (c == ">") {
						state = 10

						// do whatever there is for the tag
						if (style.isOpenTag(tag)) {
							// if there's no NE, let's create.
							// there is, let's add a SemanticClassification
							if (!ne) {
								ne = style.parseOpenTag(tag)
								if (inalt) {
									ne.alt = ""+altid
									ne.subalt = subaltid
								}
							} else {
								NamedEntity otherne = style.parseOpenTag(tag)
								otherne.classification.each{cl ->
									ne.classification << cl
									ne.wikipediaPage[cl] = otherne.wikipediaPage[cl]
									ne.dbpediaPage[cl] = otherne.dbpediaPage[cl]
								}
							}
						} else if (style.isCloseTag(tag)) {
							if (ne) {
								if (intitle) doc.titleNEs.add(ne)
								if (inbody) doc.bodyNEs.add(ne)
								if (!intitle && !inbody) doc.bodyNEs.add(ne)
								ne = null
							} else {
								// don't worry... continue
							}
						} else if (style.isOpenALTTag(tag)) {
							inalt = true
							altid = System.currentTimeMillis()
							subaltid = 0
						} else if (style.isCloseALTTag(tag)) {
							inalt = false
							altid = -1
							subaltid = -1
						} else if (style.isOpenSubALTTag(tag)) {
							subaltid++
							insubalt = true
						} else if (style.isCloseSubALTTag(tag)) {
							insubalt = false
							// is a HTML tag! add it as a hidden term.
							// don't add valid term.index, that's for visible terms only.
						} else {
							s << new Term(tag, -1, true)
						}
					}

					break

				case 20:
					if (c == "]") {
						// atenção, que os subalts repetem o texto!
						// ler só se não houver subalts (-1 / 0) ou se for o primeiro (1)
						if (subaltid <= 1) {
							s << t
						}
						// add to NEs, if there is a NE
						if (ne) ne.terms << t
						state=10
					} else if (c == "\\") {
						state=99
						// add for now... we may keep it, we may remove it.
						t.text += c
					} else {
						t.text += c
					}
					break

				case 99:
					if ((c == "[") || (c == "]") || (c == "{") || (c == "}")) {
						// replace last character
						t.text = t.text.substring(0, t.text.size()-1)+c
					} else {
						t.text += c
					}
					state = 20
					break
			}//switch
		}// each c

		// println "RembradntReader: doc.body_sentences = "+doc.body_sentences
		return doc
	}

	static String parseSimple (String string) {

		int state = 0
		StringBuffer s = new StringBuffer()

		string.trim().each{c ->

			switch(state) {

				case 0:
					if (c == "{") {state = 1}
					break

				case 1:
					if (c == "[") {state=2}
					else if (c == "}") {state=0; s.append "\n"}
					else if (c == "<") {state = 10}
					break

				case 2:
					if (c == "]") {state=1; s.append " "}
					else if (c == "\\") {state=99; s.append c}
					else {s.append c}
					break

				case 10:
					if (c == ">") {state = 1}
				// do whatever there is for the tag
					break

				case 99:
					if ((c == "[") || (c == "]") || (c == "{") || (c == "}")) {
						// replace last character
						s.deleteCharAt(s.length-1)
						s.append c
					} else {s.append c}
					state = 2
					break
			}//switch
		}// each c

		return s.toString()
	}
}//class