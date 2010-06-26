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
import saskia.io.WikipediaDB
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * Dump the most frequent links.
 */
class SearchMostFrequentOutlinks {
    def static Logger log = Logger.getLogger("WikipediaSearch")  
    
    public static void main(args) {
        
        def conf
        if (args.size() < 1) conf = Configuration.newInstance()
        else conf = Configuration.newInstance(args[0])
        def wikipedia = WikipediaAPI.newInstance()
        def db = null
        def x = null
        def Closure clos = {row -> x = row[0]}
        
        log.info "Connecting to WikipediaDB..."
        db = WikipediaDB.newInstance().getDB()
        if (db != null )log.info "Connected!"
        
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
        while (true) {
            
            println "Enter query: "
            String line = input.readLine().trim()
            if (line == null || line.length() < 1) break
                println "Parsing "+line
            
            db.eachRow (WikipediaDB.selectMostFrequentOutlinkFromAnchorText, [line], clos) 
            if (x != null)  println wikipedia.getDocFromID(x).get("title")
            // println x
        }// while true
        
        
    }//main  
}// class


