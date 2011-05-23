/* Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package saskia.wikipedia

import org.xml.sax.InputSource
import saskia.bin.Configuration
import org.apache.log4j.*
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.*
import org.xml.sax.helpers.DefaultHandler

/**
 * @author Nuno Cardoso
 * Indexes the Wikipedia preprocessed XML into DataBase only.
 * 
 * Usage: IndexWikipedia2 [conf] (processHash:[0.1]) (rebuildInlinkTable[0.1])"
 * processHash: If the related_link hash, inlinks_hash and category_hash are to be read when indexed. True for a good index build.
 * rebuildInlinkTable: erases and rebuilds inlink table. Required for new Wikipedia dumps. 
 * indexFullText: For lighter indexes, since we do not yet ue the full text.
 */
class IndexWikipedia2 {
    
    def static Logger log = Logger.getLogger("WikipediaIndexing")
    def static conf 
    
	static void main(args) {

		if (args.size() < 1) {
	        log.info "Usage: IndexWikipedia [conf] (processHash:[0.1]) (rebuildInlinkTable[0.1]) (indexFullText[0.1])"
	        System.exit(0)
	    }
		conf = Configuration.newInstance(args[0]) 	
		 
		if (conf.get("wikipedia.pages.file") == null) 
		    throw new IllegalStateException("Please specify wikipedia.pages.file in configuration.")		
	
		if (conf.get("wikipedia.index") == null) 
		    throw new IllegalStateException("Please specify wikipedia.index path in configuration.")
					
	    def handler = new WikipediaHandler2(conf)
	    
	    handler.initialize(
		    (args[1]?.equals('1') ? true : false), 
			(args[2]?.equals('1') ? true : false),
			(args[3]?.equals('1') ? true : false)
		)
	    def reader = SAXParserFactory.newInstance().newSAXParser().xMLReader
	    reader.contentHandler = handler
	    def inputStream = new FileInputStream(conf.get("wikipedia.pages.file"))
	    handler.prepareForWikipedia()
	    log.info "Now parsing Wikipedia. Please wait..."
	    reader.parse(new InputSource(inputStream))
	    inputStream.close()
	    handler.finalize()
	    log.info "Done!"	    
	}
}