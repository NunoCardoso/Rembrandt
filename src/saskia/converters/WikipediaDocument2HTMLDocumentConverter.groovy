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
import saskia.wikipedia.WikipediaDefinitions
import saskia.bin.Configuration

/** 
 * This class provides a simple parsing function from Mediawiki format to HTML format.
 * It will discard templates, tables, images and other metadata; it will parse only the body.
*/

class WikipediaDocument2HTMLDocumentConverter {
	
	Logger log = Logger.getLogger("DocumentConverter")
	
	String parse(String text, String title, String lang) {
		log.debug "Going to parse page '${title}' (${text.size()} bytes) on lang $lang."
		if (text =~ /^(?i)#REDIRECT.*/) {
			log.debug "Got a redirection: $text. Returning empty string." 
			return ""
		}
	
		// here goes filters that are multiline 

		// eliminate multiple line templates
		text = text.replaceAll(/(?si)\Q{{\E\s?[^\n]*\n.*?\n\s*\Q}}\E\s*\n/,"")
		// eliminate content between tags that are unlikely to have 
		// interesting content for NLP
		text = text.replaceAll(/(?si)<\s*(nowiki|math|pre|gallery)\s*>.*?<\/\s*(nowiki|math|pre|gallery)\s*>/,"")
		// divs, fonts... tags with options
		text = text.replaceAll(/(?si)<\/?(div|font)[^>]*?>/,"")
		// refs
		text = text.replaceAll(/(?si)<\s*ref\s*>.*?<\/\s*ref\s*>/,"")
		// other tags without options
		text = text.replaceAll(/(?si)<\/?\s*(big|small|center|blockquote)\s*>/,"")
		// comments
		text = text.replaceAll(/(?si)<!--(.*?)-->/,"")
		
		int currentOrderedListLevel = 0
		int currentUnorderedListLevel = 0
		int tableLevel = 0
			
		List t = text.split(/\n/)
		
		for (int i=0; i<t.size(); i++) { 
		
		// fail fast: stuff in tables are to be deleted.
		
		// begin tables
		 if (t[i] =~ /^\Q{|\E.*/) {
			tableLevel++
			t[i]=""
			continue  // skips the rest of code, proceeds to next t[i]
		 }
		
		 // end tables
		 if (t[i] =~ /^\Q|}\E.*/) {
			tableLevel--
			t[i]=""
			continue // skips the rest of code, proceeds to next t[i]
		 }
		
		 // table contents
		 if (tableLevel > 0) {
		 	t[i]=""
			continue // skips the rest of code, proceeds to next t[i]
		 }
			
		// eliminate one line templates
			t[i] = t[i].replaceAll(/\Q{{\E[^\Q}\E]*?\Q}}\E/, "")
			
		// replace bolds, italics
			t[i] = t[i].replaceAll(/'''''([^']*)'''''/) {all, g1 -> "<B><I>${g1}</I></B>"} //'
			t[i] = t[i].replaceAll(/'''([^']*)'''/) {all, g1 -> "<B>${g1}</B>"} //'
			t[i] = t[i].replaceAll(/''([^']*)''/) {all, g1 -> "<I>${g1}</I>"} //'
		// eliminate signatures
			t[i] = t[i].replaceAll(/~~~~?~?/,"")

		// Headings
			t[i] = t[i].replaceAll(/=====\s*([^=]*?)\s*=====/) {all, g1 -> "<H5>${g1}</H5>"}
			t[i] = t[i].replaceAll(/====\s*([^=]*?)\s*====/) {all, g1 -> "<H4>${g1}</H4>"}
			t[i] = t[i].replaceAll(/===\s*([^=]*?)\s*===/) {all, g1 -> "<H3>${g1}</H3>"}
			t[i] = t[i].replaceAll(/==\s*([^=]*?)\s*==/) {all, g1 -> "<H2>${g1}</H2>"}
			t[i] = t[i].replaceAll(/=\s*([^=]*?)\s*=/) {all, g1 -> "<H1>${g1}</H1>"}

		// check if there's a list to close
		if ( (! t[i].startsWith("*")) && (currentUnorderedListLevel > 0) ) {
			for(int j = currentUnorderedListLevel -1; j >= 0; j--) {
			 t[i] = "</UL>\n"+t[i]
			}
			currentUnorderedListLevel = 0
		}
		if ( (! t[i].startsWith("#")) && (currentOrderedListLevel > 0) ) {
			for(int j = currentOrderedListLevel -1; j >= 0; j--) {
			 t[i] = "</OL>\n"+t[i]
			}
			currentOrderedListLevel = 0
		}	
		
		//Unordered Lists
			t[i] = t[i].replaceAll(/^(\*+)\s*(.*)$/) {all, g1, g2 ->
				def res = ""
				int newLevelIndent = g1.size()
				// if newLevelIndent is the same

				if (currentUnorderedListLevel < newLevelIndent) {
					for(int j = currentUnorderedListLevel +1; 
					j <= newLevelIndent; j++) {
						res += "<UL>\n"
					}
					currentUnorderedListLevel = newLevelIndent
				} else if (currentUnorderedListLevel > newLevelIndent) {
					for(int j = currentUnorderedListLevel -1; 
					j >= newLevelIndent; j--) {
						res += "</UL>\n"
					}
					currentUnorderedListLevel = newLevelIndent
				} 
				res += "<LI>${g2}"
				return res
			}
			
		//Unrdered Lists
			t[i] = t[i].replaceAll(/^(#+)\s*(.*)$/) {all, g1, g2 ->
				def res = ""
				int newLevelIndent = g1.size()
				// if newLevelIndent is the same
				if (currentOrderedListLevel < newLevelIndent) {
					for(int j = currentOrderedListLevel +1; 
					j <= newLevelIndent; j++) {
						res += "<OL>\n"
					}
					currentOrderedListLevel = newLevelIndent
				} else if (currentOrderedListLevel > newLevelIndent) {
					for(int j = currentOrderedListLevel -1; 
					j >= newLevelIndent; j--) {
						res += "</OL>\n"
					}
					currentOrderedListLevel = newLevelIndent
				} 
				res += "<LI>${g2}"
				return res
			}
			
		// definitions are discarded
			t[i] = t[i].replaceAll(/^(;+)\s*(.*)$/) {all, g1, g2 -> return "${g2}"}
			
		// indentations are discarded
			t[i] = t[i].replaceAll(/^(:+)\s*(.*)$/) {all, g1, g2 -> return "${g2}"}
				
		// link handling:
		// loof for [[ (.*) ]], where \1  does not have [[ nor ]] 
			t[i] = t[i].replaceAll(/\Q[[\E([^\[\]][^\[\]]*)\Q]]\E/) {all, g1 -> 
				def namespace, namespaceIndex, pipe, afterPipe, pipeIndex
				namespaceIndex = g1.indexOf(":")
				pipeIndex = g1.indexOf("|")
				
				if (namespaceIndex > -1) 
					namespace = g1.substring(0, namespaceIndex)
				if (pipeIndex > -1) {
					pipe = g1.substring(0, pipeIndex)
				 	afterPipe = g1.substring(pipeIndex+1, g1.size())
				}
				
				// eliminate unintersting namespaces.
				if (namespace)  {
					log.trace "Discarding $namespace: $g1"
					return ""
				} else if (!namespace) {
				// simple link... 
					if (!pipe) return "<A HREF=\"${g1.replaceAll(/ /,'_')}\">${g1}</A>"
					else return "<A HREF=\"${pipe.replaceAll(/ /,'_')}\">${afterPipe}</A>"
				}	
			}
			
			// after this first link swipe, there might have more of them.
			// For instance, links inside a [[Image:]] 
			t[i] = t[i].replaceAll(/\Q[[\E([^\[\]][^\[\]]*)\Q]]\E/) {all, g1 -> 
				def namespace, namespaceIndex
				namespaceIndex = g1.indexOf(":")

				if (namespaceIndex > -1) namespace = g1.substring(0, namespaceIndex)
				
				// eliminate unintersting namespaces.
				if (namespace)  {
					//log.trace "2nd swipe: Discarding $namespace: $g1"
					return ""
				}
			}
			
			// regular links... [http: ]
			t[i] = t[i].replaceAll(/\Q[\E(http:\/\/[^]]*)\Q]\E/) {all, g1 -> 
				//println "g1: ${g1}"
				List tokens = g1.split(/ /)
				return "<A HREF=\""+tokens[0]+"\">"+tokens[1..<(tokens.size())].join(" ")+"</a>"
			}
			
			// cleaning stuff... empty headers
			t[i] = t[i].replaceAll(/^<H\d><\/H\d>$/,"")	
		}

		
		// remake \n
		text = t.join("\n").replaceAll(/\n+/,"\n")
		if (text) text = "<HTML>\n<HEAD>\n<TITLE>${title.replaceAll(/_/,' ')}</TITLE>\n</HEAD>\n"+
			   "<BODY>\n${text.trim()}\n</BODY>\n</HTML>"
		if (text) {
			log.debug "Converted, now it's ${text.size()} bytes long."
			return text
		} else {
			log.debug "Converted, but returning null."
			return null
		}
	}
	
	
	/** Gets the Mediawiki page on STDIN, converts & outputs HTML in STDOUT */
	static main(args) {
		
		String lang = Configuration.newInstance().get("global.lang") 
		WikipediaDocument2HTMLDocumentConverter w2h = \
		    new WikipediaDocument2HTMLDocumentConverter()

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