package renoir.bin

import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.queryParser.*
import org.apache.lucene.search.regex.*
import org.apache.lucene.search.*
import org.apache.lucene.document.*
import org.apache.lucene.index.*
import org.apache.lucene.store.*
import org.apache.lucene.analysis.snowball.*
import groovy.sql.Sql

class QueryCandidateAnswers {
	

    def indexDir = "index/lucene-html-names-index"
	Directory index = FSDirectory.getDirectory(indexDir, false)
	IndexSearcher searcher  = new IndexSearcher(index)
	Hits hits
	Query query
	
	public boolean isOnTheList(String line) {
		if (!line) return false
		QueryParser qp = new QueryParser("answer", new WhitespaceAnalyzer())
		line = line.replaceAll(/articles/,"").replaceAll(/^(.*).html$/) {all, g1 -> "$g1"}
		if (!line) return false
		query = qp.parse(line)	
		if (!query) return false
		hits = searcher.search(query) 
		println " got ${hits.length}"
		if (hits.length > 0) return true
		return false
	}
	
	public query(String line) {
		query = new QueryParser("answer", new WhitespaceAnalyzer()).parse(line)
		hits = searcher.search(query) 
		for (int i = 0; i < hits.length; i++) {
			println "answer: "+hits.doc(i).get("answer")
		}
	}

	static main(args) {
	QueryCandidateAnswers qca = new QueryCandidateAnswers()
	qca.query("nn/1/0/3/1037")
	qca.query("nn/%/_/_/%")
	qca.query("pt/p/a/’/Pa’ses") // gets 0 if this is saved in UTF-8
	}
}