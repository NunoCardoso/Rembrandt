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

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.XMLStreamConstants

import rembrandt.obj.Document


/** 
 * This class importsMediawiki XML dump files to the Source Documents
 * uses STAX parser.
 XML is:
 <?xml version="1.0" encoding="utf-8" ?>
 <mediawiki xmlns="http://www.mediawiki.org/xml/export-0.3/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.mediawiki.org/xml/export-0.3/ http://www.mediawiki.org/xml/export-0.3.xsd" version="0.3" xml:lang="en">
 <siteinfo>
 <sitename>Mediawiki</sitename>
 <base>http://nn.wikipedia.org/wiki/Hovudside</base>
 <generator>MediaWiki 1.16wmf4</generator>
 <case>first-letter</case>
 <namespaces>(...)</namespace>
 </namespaces>
 </siteinfo>
 <page>
 <title>Nynorsk</title>
 <id>12</id>
 <revision>
 <id>1531310</id>
 <timestamp>2011-01-25T14:39:19Z</timestamp>
 <contributor>
 <username>Eivindgh</username>
 <id>1460</id>
 </contributor>
 <minor/>
 <comment> Nynorsk i skulen </comment>
 <text xml:space="preserve">
 (...)
 </text>
 </revision>
 </page>
 */



public class MediawikiXMLReader extends Reader {

	XMLStreamReader xmlStreamReader
	MediawikiSyntaxConverter syntaxConverter

	public MediawikiXMLReader(InputStream is, StyleTag style) {
		super(is, style)
		this.is = is
		xmlStreamReader =
				XMLInputFactory.newInstance().createXMLStreamReader(is)
		syntaxConverter = new MediawikiSyntaxConverter()
	}

	public MediawikiXMLReader(StyleTag style) {
		super(style)
	}
	
	public setInputStream(InputStream is) {
		this.is = is
		xmlStreamReader =
			XMLInputFactory.newInstance().createXMLStreamReader(is)
		syntaxConverter = new MediawikiSyntaxConverter()
	} 
	
	/**
	 * Process the HTML input stream
	 */
	public List<Document> readDocuments(int docs_requested = 1) {

		emptyDocumentCache()

		def text // raw XML text
		def content // parsed Wikipedia content
		Date date

		String lang
		String id
		String title
		String doc_id

		Document doc

		// flags
		boolean inpage = false
		boolean inrevision = false
		boolean incontributor = false


		// let the handler read the howmany, and save the correct amount
		// of docs in the docs list.
		while (xmlStreamReader.hasNext() && documentsSize() <= docs_requested ) {

			status = ReaderStatus.INPUT_STREAM_BEING_PROCESSED

			try {
				int eventCode = xmlStreamReader.next()

				switch (eventCode) {

					case XMLStreamConstants.START_ELEMENT :

						String tagname = (
							xmlStreamReader.getPrefix() ? 
							xmlStreamReader.getPrefix()+":"+xmlStreamReader.getLocalName() 
							:xmlStreamReader.getLocalName() )

						switch(tagname) {

							case 'page':
								doc = null;
								text = ""; 
								content = null; 
								id = null;
								inpage = true;
								break

							case 'base':
								lang=null;
								break;

							case 'timestamp':
								text = "";
								date = null;
								break

							case 'revision':
								inrevision = true;
								break;

							case 'title':
								title = "";
								break;

							case 'contributor':
								incontributor=true;
								break;

							// se for de ids que não interessam, não apagar
							case 'id':
								if (inpage && !inrevision & !incontributor) {
									id="";
								}
								break;

							case 'text':
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


						switch(tagname) {
							case 'text':
							// the Document returned by syntaxConverter only has body sentences
							// it misses the title and metadata info
								doc = syntaxConverter.createDocument(text)

								if (title && id)  {
									doc_id = id+"_"+title
									int upperlimit = (doc_id.size() > 250 ? 250: doc_id.size())
									doc_id = doc_id.substring(0, upperlimit);
									doc.docid = doc_id
								}
								if (lang) doc.lang = lang

								if (title) {
									doc.title = title
									doc.tokenizeTitle()
									doc.indexTitle()
								}
								inpage = false;
								text = "";
								break

							case 'base':
								text.findAll(/http:\/\/(\w*).wikipedia.org\/.*/) {all, g1 -> lang = g1}
								text = "";
								break;

							case 'timestamp':
								try {
									date = Date.parse("yyyy-MM-dd'T'HH:mm:ss'Z'", text)
								} catch(Exception e) {
									date = new Date(0)
								}
								text = "";
								break

							case 'revision':
								inrevision = false;
								text = "";
								break;

							case 'contributor':
								incontributor=false;
								text = "";
								break;

							case 'title':
							//				println "Got title: $text"
								title = text.trim();
								text = "";
								break;

							case 'id':
								if (inpage && !inrevision & !incontributor) {
									//					println "Got id: $text"
									id = text.trim();
								}
								text = "";
								break;

							case 'page':
								addDocument(doc)
								text = "";
								
								if (documentsSize() >= docs_requested)
									return getDocuments()
			
								break


						}

					break

					case XMLStreamConstants.CHARACTERS :
						text += xmlStreamReader.getText()
					break

				}
			}
			catch (Exception e) {
				log.fatal  "Erro na leitura de XML: "+e.getMessage()
			}
		}
		
		// leftovers
		status = ReaderStatus.INPUT_STREAM_FINISHED
		return getDocuments()
	}
}
