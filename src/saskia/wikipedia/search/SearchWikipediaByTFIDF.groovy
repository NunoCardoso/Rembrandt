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
import org.apache.lucene.document.Document
import org.apache.lucene.*
import org.apache.lucene.index.*
import org.apache.lucene.queryParser.*
import org.apache.lucene.search.*
import org.apache.lucene.analysis.*
import org.apache.lucene.analysis.standard.*
import saskia.bin.Configuration
import saskia.wikipedia.WikipediaAPI

/**
 * @author Nuno Cardoso
 * Search wikipedia using the Lucene search.
 */
public class SearchWikipediaByTFIDF {
    
    def static Logger log = Logger.getLogger("WikipediaSearch")  
    
    
    public static void main(args) {
        
        def conf
        if (args.size() < 1) conf = Configuration.newInstance()
        else conf = Configuration.newInstance(args[0])
        
        def wikipedia = WikipediaAPI.newInstance("pt")	 
        def IndexReader reader = IndexReader.open(conf.get("wikipedia.index"));
        def Searcher searcher = new IndexSearcher(reader);
        def Analyzer analyzer = new StandardAnalyzer();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, "UTF-8"))
        QueryParser parser = new QueryParser("title",analyzer)
        BufferedReader inp = new BufferedReader( new InputStreamReader(System.in))
        
        while (true) {
            println "Enter query: "
            def String line = inp.readLine().trim()
            if (line == null || line.length() == -1) break
                println "Parsing "+line
            def Query query = parser.parse(line)
            def Hits hits = searcher.search(query)
            def Document doc
            if (hits != null && hits.length() > 0 ) {
                doc = hits.doc(0)
            } else {
                println "No hits."
            }
            
            if (doc == null) {
                println "No docs."
            } else {
                
                println doc.get("id")+": "+doc.get("title")
                println "=========Resumo ==========="
                String text = doc.get("text")
                println text
                def inlinks = doc.getValues("inlinks") 
                if (inlinks) SearchWikipediaByTFIDF.printOrderedList("inlinks",inlinks)
                def outlinks = doc.getValues("outlinks") 
                if (outlinks) SearchWikipediaByTFIDF.printOrderedList("outlinks",outlinks)
                def categories = doc.getValues("categories") 
                if (categories) SearchWikipediaByTFIDF.printOrderedList("Categories",categories)
                def listings = doc.getValues("listings") 
                if (listings) SearchWikipediaByTFIDF.printOrderedList("Listings",listings)
                def headings = doc.getValues("headings") 
                if (headings) SearchWikipediaByTFIDF.printOrderedList("Headings",headings)
                def nes = doc.getValues("nes") 
                if (nes) SearchWikipediaByTFIDF.printOrderedList("Top 10 de NEs",nes, 10)
                
            }//else
        }//while true
    }//main
    
    
    
    public static List printOrderedList(String s, String[] l, int howmany = 0) {
        return printOrderedList(s, l.toList(), howmany)
    }
    
    public static printOrderedList(String s, List l, int howmany=0) {
        if (l == null) return ''
        def hash = [:]
        println "======= $s ========"
        l.each {
            if (hash[it] != null) hash[it]++ 
            else hash[it]=1
        }
        
        def howmuch = (hash.size() < howmany? hash.size()-1 : howmany)		
        def entries = hash.entrySet().sort{a,b -> b.value <=> a.value}
        if (howmany != 0) 
            entries[0..howmuch].each {println "[$it.key : $it.value]"}
        else 
            entries.each {println "[$it.key : $it.value]"}	
    }
}

