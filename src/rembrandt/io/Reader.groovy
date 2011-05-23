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
import org.apache.log4j.Logger
/**
 * @author Nuno Cardoso
 * Abstract class from where reader subclasses must extend.
 */
abstract class Reader {

	static Logger log = Logger.getLogger("Reader")
	StyleTag style
	InputStreamReader inputStreamReader
	String encoding
	
	ReaderStatus status = ReaderStatus.INPUT_STREAM_NOT_INITIALIZED
	/**
	* Don't let others reach this variable, because it would fill out and empty.
	*/
    private List<Document> docs = []

	/**
	 * The main consctructor gets a StyleTag, when reading documents with tagged NEs. 
	 * If not specified, it's a NullStyleTag, used for documents without tagged NEs.
	 */
	public Reader(InputStreamReader inputStreamReader, StyleTag style) {
		this.encoding = encoding
		this.inputStreamReader = inputStreamReader
		this.style=style
	}

	public Reader(StyleTag style) {
		this.style=style
	}
	
	public void setInputStreamReader(InputStreamReader inputStreamReader) {
		this.inputStreamReader = inputStreamReader
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding
	}
	
	public boolean hasMoreDocumentsToRead() {
		return status == ReaderStatus.INPUT_STREAM_BEING_PROCESSED
	}

	public boolean hasNoMoreDocumentsToRead() {
		return status == ReaderStatus.INPUT_STREAM_FINISHED
	}
	
	public void emptyDocumentCache() {
		docs = []
	}

	public void addDocument(Document doc) {
		docs << doc
	}
	
	public int documentsSize() {
		return docs.size()
	}
	
	public List<Document> getDocuments() {
		return docs
	}
	
	/** retrieve list of documents */
	public abstract List<Document> readDocuments(int howmany)

}