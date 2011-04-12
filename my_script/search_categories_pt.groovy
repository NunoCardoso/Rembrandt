#!/usr/bin/env groovy

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.queryParser.*
import org.apache.lucene.search.regex.*
import org.apache.lucene.search.*
import org.apache.lucene.document.*
import org.apache.lucene.index.*
import org.apache.lucene.store.*
import org.apache.lucene.analysis.snowball.*
import groovy.sql.Sql

def sourceEncoding = System.getProperty("file.encoding")
StandardAnalyzer analyzer = new StandardAnalyzer()
def rootDir = "/Users/ncardoso/Documents/workspace/Renoir2/data/gikiclef"
def indexDir = "${rootDir}/index"
Directory index = FSDirectory.getDirectory(indexDir, false)
IndexSearcher searcher  = new IndexSearcher(index)
//Query query = new QueryParser("page_title", new StandardAnalyzer()).parse(args[0])
//QueryParser qp = new QueryParser("page_title", new SnowballAnalyzer("Portuguese"))
//Query query = qp.parse(saskia.util.StringUtil.convert(args[0], sourceEncoding, "UTF-8"))

RegexQuery query = new RegexQuery(new Term("page_title",args[0]))
query.setRegexImplementation(new JavaUtilRegexCapabilities())
println "Query: "+query.toString()
Hits hits = searcher.search(query) 
//println "hits=${hits.length}"
println hits.length()
 for (int i = 0; i < hits.length; i++) {
     println "title: "+hits.doc(i)
}
//	println "id: "+hits.doc(i).get("id")+" title: "+hits.doc(i).get("title")
//}
