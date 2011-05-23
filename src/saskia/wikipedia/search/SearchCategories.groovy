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

package saskia.wikipedia.search

import org.apache.log4j.*
import saskia.wikipedia.WikipediaAPI
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * Dump the categories of a Wikipedia page. 
 */
class SearchCategories {
    
    def static Logger log = Logger.getLogger("WikipediaSearch")  
    
    public static void main(args) {
        
        def conf
        if (args.size() < 1) conf = Configuration.newInstance()
        else conf = Configuration.newInstance(args[0])
        def wikipedia = WikipediaAPI.newInstance("pt")
        
        
        BufferedReader input = new BufferedReader( new InputStreamReader(System.in))
        while (true) {
            
            println "Enter query: "
            String line = input.readLine().trim()
            if (line == null || line.length() == -1) break
                if (line.length() == 0)  break
                println "Parsing "+line
            
            def start1 = System.currentTimeMillis()
            
            def document = wikipedia.getPageDocumentFromTitle(line)
            //println "Document: "+document
            if (document != null) {
                def categories = document.getCategories()
                // def sentences = wikipedia.getSentencesAndTerms(firstParagraph)
                //def queryTerms = wikipedia.getSentencesAndTerms(line)
                def start2 = System.currentTimeMillis()       
                println categories    
                println "Done in "+(start2-start1)+" msecs."
                
            } else {
                println "No docs."
            }
        }
    }
}


