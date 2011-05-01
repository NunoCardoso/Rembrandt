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

import java.util.List;

import rembrandt.obj.Document

/**
 * @author Nuno Cardoso
 * This class is a reader from a XStream object
 */
class XStreamReader extends Reader {

	/** retrieve text content from the object */
	def resource

	public XStreamReader(InputStream is, StyleTag style) {
		super(is, style)
	}
	public XStreamReader(StyleTag style) {
		super(style)
	}

	public List<Document> readDocuments(int docs_requested = 1) {

		emptyDocumentCache()

		def BufferedReader br = new BufferedReader(
					new InputStreamReader(is))

		def StringBuffer buffer = new StringBuffer()
		def line
		while ((line = br.readLine()) != null) {
			status = ReaderStatus.INPUT_STREAM_BEING_PROCESSED
			buffer.append(line+"\n")
		}
		def resource = XStream.fromXML(buffer.toString())

		if (resource instanceof Document) docs.add(resource)
		else if (resource instanceof List) {
			if (resource == []) log.warn "XStream input is a list with no documents."
			else {
				if (!(resource[0] instanceof Document)) {
					log.error "Xstream entries are not valid REMBRANDT documents: "+
							resource[0].class.name
				} else {
					resource.each{r ->
						addDocument(r)
					}
				}
			}
		} else {
			log.error "Xstream entries are not valid REMBRANDT documents: "+
					resource.class.name
		}

		return getDocuments()
	}
}