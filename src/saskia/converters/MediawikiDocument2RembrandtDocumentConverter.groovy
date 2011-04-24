
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
package saskia.converters

import org.apache.log4j.*
import saskia.bin.Configuration
import rembrandt.io.*
import rembrandt.obj.Document

import info.bliki.wiki.model.WikiModel
/** 
 * This class provides a simple parsing function from Mediawiki format to HTML format.
 * It will discard templates, tables, images and other metadata; it will parse only the body.
*/

class MediawikiDocument2RembrandtDocumentConverter {
	
	Logger log = Logger.getLogger("DocumentConverter")
	HTMLDocumentReader reader
	RembrandtWriter writer
	String taglang = "pt"
	String lang
	
	public MediawikiDocument2RembrandtDocumentConverter(String lang = null) {
		this.lang=lang
		reader = new HTMLDocumentReader(new HTMLStyleTag(taglang))
		writer = new RembrandtWriter(new RembrandtStyleTag(taglang))
   }
	
	String parse(String text, String title, String lang, String id) {
		WikiModel wikiModel = new WikiModel("","");

		// normalizar os &gt, &lt, &quot;
		text = text.replaceAll(/&lt;/, "<").replaceAll(/&gt;/, ">").replaceAll(/&quot;/, "'").replaceAll(/&amp;/, "&")
		
		// eliminar os interwikis
		if (text.indexOf("<!--interwiki") > -1) 
			text = text.substring(0, text.indexOf("<!--interwiki"))

      String htmlStr = wikiModel.render(text);
       
		// há que eliminar os refs! 
	/*	<sup id="_ref-Vik.E2.88.9A.E2.88.8Fr_2005_a" class="reference"><a href="#_note-Vik.E2.88.9A.E2.88.8Fr_2005" title="">[1]</a></sup>;<sup id="_ref-Spr.E2.88.9A.E2.80.A2kr.E2.88.9A.E2.80.A2det_2007_a" class="reference"><a href="#_note-Spr.E2.88.9A.E2.80.A2kr.E2.88.9A.E2.80.A2det_2007" title="">[2]</a></sup>;
	*/
	
		htmlStr = htmlStr.replaceAll(/<sup id=".*?" class="reference"><a[^>]*>\[\d+\]<\/a><\/sup>/,"")
		
		
		// ohamar o rembrandt.io.HTMLDocumentReader e RembrandtWriter
		// para atomizar direito sem ligar às etiquetas HTML
		Document doc = reader.createDocument(htmlStr)
		if (id) doc.docid = id
		if (title) doc.title = title
		if (taglang) doc.taglang = taglang
		if (lang) doc.lang = lang else doc.lang = this.lang
		
		String res = writer.printDocument(doc)
		if (res) {
			log.debug "Converted, now it's ${res.size()} bytes long."
			return res
		} else {
			log.debug "Converted, but returning null."
			return null
		}
	}
		
	/** Gets the Mediawiki page on STDIN, converts & outputs HTML in STDOUT */
	static main(args) {
		
		String lang = Configuration.newInstance().get("global.lang") 
		MediawikiDocument2RembrandtDocumentConverter w2h = \
		    new MediawikiDocument2RembrandtDocumentConverter(lang)

		String text = ""
		String title = ""
		if (!args) {
			println "No args found. Expecting STDIN input."
		} 
		String line = ""
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
		while (line = input.readLine()) {text += line}

		def parsedText = w2h.parse(text, "STDIN", lang)
		if (parsedText) println parsedText else println "No content found."
	}
}
