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

import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
/** 
 * Jsoup
*/
class FilterTextFromHTMLDocument {
	
	Logger log = Logger.getLogger("DocumentConverter")
	
	/* 
	Whitelist levels:
	none()
simpleText()
basic()
basicWithImages()
relaxed()
*/
	static String parse(String text, String level="rembrandt") {

		if (level == "none")
			return Jsoup.clean(text, Whitelist.none())
			// This whitelist allows only simple text formatting: b, em, i, strong, u
		if (level == "simpleText")
			return Jsoup.clean(text, Whitelist.simpleText())
			//: a, b, blockquote, br, cite, code, dd, dl, dt, em, i, li, ol, p, pre, q, small, strike, strong, sub, sup, u, ul, 
		if (level == "basic")
			return Jsoup.clean(text, Whitelist.basic())
		if (level == "rembrandt")
			return Jsoup.clean(text, Whitelist.simpleText().addTags("li").addTags("ol").addTags("ul").addTags("p").addTags("h1").addTags("h2").addTags("h3").addTags("h4").addTags("h5").addTags("h6"))
	
	}
}
