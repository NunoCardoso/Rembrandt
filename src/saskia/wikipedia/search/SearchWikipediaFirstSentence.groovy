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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.FilterIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

//import org.apache.lucene.search.highlight.Highlighter;
//import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.analysis.TokenStream;

import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * Search wikipedia, return first sentence of the result.
 */
 
def conf
if (args.size() <1) {
    println "No conf file given. Going for default value"
    conf = new Configuration(Rembrandt.defaultconf)
} else {
    conf = new Configuration(args[0])
}

IndexReader reader = IndexReader.open(conf.get("wikipedia.index"));
Searcher searcher = new IndexSearcher(reader);
Analyzer analyzer = new StandardAnalyzer();
BufferedReader input = new BufferedReader(
		  new InputStreamReader(System.in, "UTF-8"))
QueryParser parser = new QueryParser("title",analyzer)

println "Enter query: "
String line = input.readLine()
line = line.trim()
Query query = parser.parse(line)
//QueryScorer scorer = new QueryScorer(query)
//Highlighter h = new Highlighter(scorer)
Hits hits = searcher.search(query)
Document doc = hits.doc(0);
println doc.get("id")+": "+doc.get("title")
println "=====¨====Resumo ==========="
String text = doc.get("text")
TokenStream ts = new StandardAnalyzer().tokenStream("text", new StringReader(text))
//println h.getBestFragments(ts, text, 5,"(...)").replaceAll("\n"," ")
def inlinks = doc.getValues("inlinks") 
if (inlinks) printOrderedList("inlinks",inlinks, 10)
def outlinks = doc.getValues("outlinks") 
if (outlinks) printOrderedList("outlinks",outlinks, 10)
def categories = doc.getValues("categories") 
if (categories) printOrderedList("Categories",categories, 10)
def listings = doc.getValues("listings") 
if (listings) printOrderedList("Listings",listings, 10)
def headings = doc.getValues("headings") 
if (headings) printOrderedList("Headings",headings, 10)
def nes = doc.getValues("nes") 
if (nes) printOrderedList("Top 10 de NEs",nes, 10)

reader.close();
   
    public  printOrderedList(String s, String[] l, int howmany = 0) {
    	return printOrderedList(s, l.toList(), howmany)
    }
 
	public  printOrderedList(String s, List l, int howmany=0) {
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
			entries[0..howmuch].each {println "[$it.key : $it.value]  "}
		else 
			entries.each {println "[$it.key : $it.value]  "}	
	}
