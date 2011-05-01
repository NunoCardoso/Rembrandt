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
import rembrandt.obj.SentenceWithPoS
import rembrandt.obj.Term
import rembrandt.obj.TermWithPoS
import org.apache.log4j.Logger
/**
 * @author Nuno Cardoso
 * This class is a reader for ACDC documents (see <a href="http://www.linguateca.pt/ACDC">Linguateca's ACDC page</a> for additional info.) 
 */
class ACDCReader extends Reader {

	def number = 0
	def save = [:]
	def beginDOCPattern = /^<DOC docid=\s*(.*)>$/
	def endDOCPattern = /^<\/DOC>$/
	def emPattern = /^<EM "(.*)">$/
	def dataPattern = /^<data (\d+)>$/
	def catPattern = /^<CATEGORY (.*)>$/
	def beginPPattern = /^<p par=(.+)>$/
	def endPPattern = /^<\/p>$/
	def beginSPattern = /^<s>$/
	def endSPattern = /^<\/s>$/
	def beginTPattern = /^<t>$/
	def endTPattern = /^<\/t>$/
	def beginMWEPattern = /^<mwe (.*)>$/
	def endMWEPattern = /^<\/mwe>$/
	def termPattern = /^(.*?)\s+(.*)$/

	public ACDCReader(InputStream inputStream, StyleTag style) {
		super(inputStream, style)
	}
	
	public ACDCReader(StyleTag style) {
		super(style)
	}

	/**
	 * Processes the ACDC input stream
	 * @param is input stream reader
	 */
	public List<Document> readDocuments(int docs_requested = 1) {
		
		emptyDocumentCache()
				
		Document doc
		List<Sentence> sentences

		int sentenceIndex = 0
		int termIndex = 0
		Sentence s
		boolean matched


		BufferedReader br = new BufferedReader(
			new InputStreamReader(inputStream))
		String line

		while ((line = br.readLine()) != null) {
			log.trace "line:"+line
			status = ReaderStatus.INPUT_STREAM_BEING_PROCESSED
			
			matched = false

			// begin DOC pattern
			if (!matched) {
				def m = line =~ beginDOCPattern
				if (m.matches())  {
					doc = new Document(docid:m.group(1) )
					sentences = []
					matched=true
				}
			}
			//end DOC pattern
			if (!matched) {
				def m = line =~ endDOCPattern
				if (m.matches())  {
					doc.body_sentences = sentences
					doc.preprocess()
					addDocument(doc)
// return if buffer is full 
					if (documentsSize() >= docs_requested) 
						return getDocuments()
						
					matched=true
				}
			}
			// EM pattern
			if (!matched) {
				def m = line =~ emPattern
				if (m.matches())  {
					doc.property['em'] = m.group(1)
					matched=true
				}
			}
			// data pattern
			if (!matched) {
				def m = line =~ dataPattern
				if (m.matches())  {
					doc.property['data'] = m.group(1)
					matched=true
				}
			}
			// CATEGORY pattern
			if (!matched) {
				def m = line =~ catPattern
				if (m.matches())  {
					doc.property['category'] = m.group(1)
					matched=true
				}
			}
			// begin P pattern
			if (!matched) {
				def m = line =~ beginPPattern
				if (m.matches())  {
					matched=true
				}
			}
			// end P pattern
			if (!matched) {
				def m = line =~ endPPattern
				if (m.matches())  {
					matched=true
				}
			}
			// begin T pattern
			if (!matched) {
				def m = line =~ beginTPattern
				if (m.matches())  {
					matched=true
				}
			}
			// end T pattern
			if (!matched) {
				def m = line =~ endTPattern
				if (m.matches())  {
					matched=true
				}
			}
			// begin S pattern
			if (!matched) {
				def m = line =~ beginSPattern
				if (m.matches())  {
					s = new Sentence(sentenceIndex++)
					matched=true
				}
			}
			// end S pattern
			if (!matched) {
				def m = line =~ endSPattern
				if (m.matches())  {
					sentences << s
					termIndex =0
					matched=true
				}
			}
			// begin mwe pattern
			if (!matched) {
				def m = line =~ beginMWEPattern
				if (m.matches())  {
					s << new Term(line, termIndex++, true)
					matched=true
				}
			}

			// end mwe pattern
			if (!matched) {
				def m = line =~ endMWEPattern
				if (m.matches())  {
					s << new Term(line, termIndex++, true)
					matched=true
				}
			}

			// default: terms
			if (!matched) {
				def m = line =~ termPattern
				if (m.matches())  {
					TermWithPoS t = TermWithPoS.parse(m.group(1), termIndex++, SentenceWithPoS.FROM_PALAVRAS_PoS)
					t.lemma = m.group(2)
					s << t
					matched=true
				}
			}
		}
		
		// if i'm here, it's because the remaining docs 
		// is not reaching the amount.
		status = ReaderStatus.INPUT_STREAM_FINISHED
		return getDocuments()
	}
}