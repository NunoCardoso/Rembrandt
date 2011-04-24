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

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.*
import javax.xml.parsers.SAXParserFactory

import rembrandt.obj.Document
import saskia.converters.MediawikiDocument2RembrandtDocumentConverter


/** 
 * This class importsMediawiki XML dump files to the Source Documents
 * uses SAX.
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

class MediawikiXMLHandler extends DefaultHandler {
	
	MediawikiXMLReader _this_reader
	MediawikiDocument2RembrandtDocumentConverter converter
	
	def text
	def content
	Date date
	String lang
	String id
	String title
	String doc_id
	
	boolean inpage = false
	boolean inrevision = false
	boolean incontributor = false
	
	public MediawikiXMLHandler(MediawikiXMLReader this_) {
		_this_reader = this_
		// I will infer the doc language and set the
		// converter language before using it.
		converter = new MediawikiDocument2RembrandtDocumentConverter(null)
	}
	void startElement(String ns, String localName, String qName, Attributes atts) {
		switch (qName) {
			
			case 'page':
						text = ""; content = null; id = null;
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
	}
	
	void characters(char[] chars, int offset, int length) {
		text += new String(chars, offset, length)
	}
	
	void endElement(String ns, String localName, String qName) {
		switch (qName) {
			
			case 'text':
				if (title && id)  {
					doc_id = id+"_"+title
					int upperlimit = (doc_id.size() > 250 ? 250: doc_id.size())
					doc_id = doc_id.substring(0, upperlimit);
				}
				if (lang) converter.lang = lang
				content = converter.parse(text.trim(), title, lang, doc_id)
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
				Document doc = new Document()
				doc.body = text
				doc.docid = id
				doc.lang = (lang ? lang : null)
				doc.tokenize()	
				_this_reader.docs << doc
				
				
				text = "";
				break
		}
	}
}


public class MediawikiXMLReader extends Reader {
	
	public MediawikiXMLReader(StyleTag style) {
		super(style)
	}
	
	/**
	 * Process the HTML input stream
	 */
	public void processInputStream(InputStreamReader is) {
		def handler = new MediawikiXMLHandler(this)
		def reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader()
		reader.setContentHandler(handler)
		reader.parse(new InputSource(is))
	}
}
