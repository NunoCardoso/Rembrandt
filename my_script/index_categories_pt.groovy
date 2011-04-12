import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.*
import groovy.sql.Sql

StandardAnalyzer analyzer = new StandardAnalyzer()
def indexDir = "/Users/ncardoso/Documents/workspace/Saskia/index/pt"
Directory index = FSDirectory.getDirectory(indexDir, true)
IndexWriter w = new IndexWriter(index, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED)

/** To see the namespace distribution: 
select page_namespace, count(*) as c from ${LG}_page group by page_namespace order by c desc
*/
sql = Sql.newInstance("jdbc:mysql://localhost/saskia?useEncoding=yes&characterEncoding=UTF-8", 
"root","","com.mysql.jdbc.Driver")

println "Starting..."
sql.eachRow("select page_id, page_title from pt_page where page_namespace=14", {it ->  
    Document doc = new Document()
    doc.add(new Field("page_id", it.page_id as String, Field.Store.YES, Field.Index.NO))
	doc.add(new Field("page_title", it.page_title.replaceAll("_"," "), 
						Field.Store.YES, Field.Index.ANALYZED))
    w.addDocument(doc)
})
w.optimize()
w.close()
println "Done."