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

import rembrandt.obj.*
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage
import java.io.StringReader
import java.io.StringWriter

import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 * Retirado de //http://eclipse.dzone.com/announcements/textile-j-is-moving-mylyn-wiki
 * e de http://stackoverflow.com/questions/2863272/wikipedia-java-library-to-remove-wikipedia-text-markup-removal
 */
class MediawikiSyntaxConverter {


	public Document createDocument(String text) {

		// MarkupParser outputs HTML.
		// So, I have to read HTML with my HTMLReader, so I treat the
		// allowed HTML tags wisely.


		HTMLReader reader = new HTMLReader(new HTMLStyleTag("pt"))

		// normalizar os &gt, &lt, &quot;
		text = text.replaceAll(/&lt;/, "<").replaceAll(/&gt;/, ">").replaceAll(/&quot;/, "'").replaceAll(/&amp;/, "&")

		// eliminar os interwikis
		if (text.indexOf("<!--interwiki") > -1)
			text = text.substring(0, text.indexOf("<!--interwiki"))

		// basically, everything that has [[\w+:.*]] (langlings) has to go!
		text = text.replaceAll(/\[\[[\w-]+:[^\]]+\]\]/,"")

		// delete big sequences of \n\n\n\n\n...
		text = text.replaceAll(/\n\n+/,"\n\n")

		// RENDER
		StringWriter writer = new StringWriter();

		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer)
		builder.setEmitAsDocument(false)

		MarkupParser parser = new MarkupParser(new MediaWikiLanguage())
		parser.setBuilder(builder)
		parser.parse(text)

		String html = writer.toString()

		//		println ("!!!")
		//		println html
		//		// dar um pouco mais de espaço:
		html = html.replaceAll(/<\/p>/,"</p>\n")
		html = html.replaceAll(/<li><\/li>/,"")
		// há que eliminar os refs!
		/*	<sup id="_ref-Vik.E2.88.9A.E2.88.8Fr_2005_a" class="reference"><a href="#_note-Vik.E2.88.9A.E2.88.8Fr_2005" title="">[1]</a></sup>;<sup id="_ref-Spr.E2.88.9A.E2.80.A2kr.E2.88.9A.E2.80.A2det_2007_a" class="reference"><a href="#_note-Spr.E2.88.9A.E2.80.A2kr.E2.88.9A.E2.80.A2det_2007" title="">[2]</a></sup>;
		 */

		/*	htmlStr = htmlStr.replaceAll(/<sup id=".*?" class="reference"><a[^>]*>\[\d+\]<\/a><\/sup>/,"")
		 htmlStr = htmlStr.replaceAll(/<li>\{\{Link\}\}<\/li>/, "")
		 htmlStr = htmlStr.replaceAll(/\n\n+/,"\n\n")
		 */

		// Note: the doc.title_sentences and doc.body_sentences are correctly parsed.
		// use those!
		Document doc = reader.createDocument(html)

		return doc
	}

	/** Gets the Mediawiki page on STDIN, converts & outputs HTML in STDOUT */
	static main(args) {

		MediawikiSyntaxConverter w2h = new MediawikiSyntaxConverter()
		RembrandtWriter writer = new RembrandtWriter(new RembrandtStyleTag("pt"))

		String text = ""
		String title = ""
		String from = ""
		if (!args) {
			println "No args found. Expecting STDIN input."
			String line = ""
			from="STDIN"
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			while (line = input.readLine()) {
				text += line
			}
		} else {
			from = "FILE"
			text = new File(args[0]).text
		}

		Document doc = w2h.createDocument(text)

		println writer.printDocument(doc)
	}
}