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

/** 
 * This class provides a simple parsing function from Mediawiki format to HTML format.
 * It will discard templates, tables, images and other metadata; it will parse only the body.
*/

class HTMLDocument2TextDocumentConverter {
	
	Logger log = Logger.getLogger("DocumentConverter")
	
	String parse(String text) {
	  return text.replaceAll(/(?i)<title>[^<]*<\/title>/,"").replaceAll(/<[^>]*>/,"").replaceAll(/\n+/,"\n")
	}
}