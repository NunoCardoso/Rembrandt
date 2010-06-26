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
 * Dump outlinks from the index.
 */
class SearchOutlinksFromIndex {
     
    def static Logger log = Logger.getLogger("WikipediaSearch")  
    
    public static void main(args) {
        
        def conf
        if (args.size() < 1) conf = Configuration.newInstance()
        else conf = Configuration.newInstance(args[0])
        
        def wikipedia = WikipediaAPI.newInstance()
        
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
        while (true) {
            
            println "Enter query: "
            String line = input.readLine().trim()
            if (line == null || line.length() < 1) break
                println "Parsing "+line
            def document = wikipedia.getTitlePage(line)
            if (document != null) {
                def outlinksFromIndex = wikipedia.getOutlinks(document)
                outlinksFromIndex.each {outlink -> 
                    // formato: ["+it.target+"]["+it.anchor+"]
                    def matcher = outlink =~ /\[(.*)\]\[(.*)]/
                    matcher.matches()
                    println "target:"+matcher.group(1)+" anchor text:"+matcher.group(2)
                }
            } else {
                println "Doc null. Try again, doh!"
            }
        }// while true
        
        
    }//main  
}// class


