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

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.XMLStreamConstants

import rembrandt.obj.Document


/** 
 * This class reads WPT05 files, converts into Documents
 * 
 * The files are in RDF format, and they are huge. So I need a SAX parser
 * I'll use import javax.xml.parsers.SAXParserFactory
 * format:
 <?xml version="1.0" encoding="UTF-8"?>
 <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterm="http://purl.org/dc/ter
 ms/" xmlns:ore="http://www.openarchives.org/ore/terms/" xmlns:wpt="http://xldb.di.fc.ul.pt/wpt/"> 
 <rdf:Description rdf:about="http://xldb.di.fc.ul.pt/rebil/tools#ReM">
 <ore:describes rdf:resource="http://xldb.di.fc.ul.pt/rebil/tools"/>
 <rdf:type rdf:resource="http://www.openarchives.org/ore/terms/ResourceMap"/>
 </rdf:Description>
 <rdf:Description rdf:about="http://xldb.di.fc.ul.pt/linguateca/primeira_proposta.html">
 <ore:isAggregatedBy rdf:resource="http://xldb.di.fc.ul.pt/linguateca"/>
 <wpt:ipAddr rdf:datatype="http://www.w3.org/2001/XMLSchema#string">194.117.22.87</wpt:ipAddr>
 <wpt:server rdf:datatype="http://www.w3.org/2001/XMLSchema#string">apache</wpt:server>
 <wpt:statusCode rdf:datatype="http://www.w3.org/2001/XMLSchema#int">200</wpt:statusCode>
 <dcterm:modified rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2004-08-30T23:00:00Z</dcterm:modified>
 <wpt:fetched rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2005-07-23T10:22:43Z</wpt:fetched>
 <dc:format rdf:resource="text/html"/>
 <wpt:arcName rdf:resource="WPT-9-20080822122528-00677"/>
 <wpt:filteredText>&gt; XLDB Group - primeira proposta
 fcul
 </wpt:filteredText>
 <dc:language>pt</dc:language>
 </rdf:Description>
 */

public class WPT05Reader extends Reader {

	XMLStreamReader xmlStreamReader


	public WPT05Reader(InputStream is, StyleTag style) {
		super(is, style)
		this.is = is
		xmlStreamReader =
				XMLInputFactory.newInstance().createXMLStreamReader(is)
	}

	public WPT05Reader( StyleTag style) {
		super(style)
	}

	public setInputStream(InputStream is) {
		this.is = is
		xmlStreamReader =
				XMLInputFactory.newInstance().createXMLStreamReader(is)
	}

	public List<Document> readDocuments(int docs_requested = 1) {
		String text
		String content
		Date date_modified
		Date date_fetched
		String lang
		String id
		Document doc


		emptyDocumentCache()

		// let the handler read the howmany, and save the correct amount
		// of docs in the docs list.
		while (xmlStreamReader.hasNext() && documentsSize() <= docs_requested ) {

			status = ReaderStatus.INPUT_STREAM_BEING_PROCESSED

			try {
				int eventCode = xmlStreamReader.next()

				switch (eventCode) {

					case XMLStreamConstants.START_ELEMENT :

					log.trace "xmlStreamReader.getLocalName():"+xmlStreamReader.getLocalName()
					log.trace "xmlStreamReader.getPrefix():"+xmlStreamReader.getPrefix()
					log.trace "xmlStreamReader.getName():"+xmlStreamReader.getName()
					
						String tagname = (
						xmlStreamReader.getPrefix() ?
						xmlStreamReader.getPrefix()+":"+xmlStreamReader.getLocalName()
						:xmlStreamReader.getLocalName() )

						log.trace "open tagname:"+tagname
						
						switch(tagname) {

							case 'rdf:Description':
								text = "";
								content = null;
								lang = null;
								id = null;
								break
							case 'dcterm:modified':
								text = "";
								date_modified = null;
								break
							case 'wpt:fetched':
								text = "";
								date_fetched = null;
								break
							case 'wpt:arcName':
								text = "";
							
								for (int i=0; i<xmlStreamReader.getAttributeCount(); i++) {
									
									String attname = (
										xmlStreamReader.getAttributePrefix(i) ?
										xmlStreamReader.getAttributePrefix(i)+":"+xmlStreamReader.getAttributeLocalName(i)
										:xmlStreamReader.getAttributeLocalName(i) )

									if (attname.equals("rdf:resource")) {
										id = xmlStreamReader.getAttributeValue(i)
									}
								}
								break
							case 'dc:language':
								text = "";
								lang = null;
								break
							case 'wpt:filteredText':
								text = "";
								content = null;
								break

						}

						break

					case XMLStreamConstants.END_ELEMENT :

						String tagname = (
						xmlStreamReader.getPrefix() ?
						xmlStreamReader.getPrefix()+":"+xmlStreamReader.getLocalName()
						:xmlStreamReader.getLocalName() )
						
						log.trace "close tagname:"+tagname
						text = text?.trim()
						log.trace "text:"+text
						
						switch(tagname) {

							case 'rdf:Description':
								Date date = null
								if (date_modified)
									date = date_modified
								if (!date && date_fetched)
									date = date_fetched
								if (!date)
									date = new Date(0)

							// Há alguns rdf.Description que não possuem doc.
							// se não possuem, passar à frente.
							// testar com id
								if (doc && doc.docid) {
									doc.date_created = date
									doc.preprocess()
									
									addDocument(doc)
									if (documentsSize() >= docs_requested)
										return getDocuments()
								}
								doc = null
								break
							case 'dcterm:modified':
								date_modified = Date.parse("yyyy-MM-dd'T'HH:mm:ss'Z'", text)
								break
							case 'wpt:fetched':
								date_fetched = Date.parse("yyyy-MM-dd'T'HH:mm:ss'Z'", text)
								break
							case 'dc:language':
								lang = text;
								break
							case 'wpt:filteredText':

								log.trace "Creating doc with ${text.size()} text, id:"+id+" lang:"+lang
								doc = new Document()
								doc.body = text
								doc.docid = id
								if (lang) doc.lang = lang
								break
						}
					break

					case XMLStreamConstants.CHARACTERS :
						String t = xmlStreamReader.getText()
						text += t
					break


				}
			}catch (Exception e) {
				log.fatal  "Erro na leitura de XML: "+e.printStackTrace()
			}

		}

		// leftovers
		status = ReaderStatus.INPUT_STREAM_FINISHED
		return getDocuments()
	}
}