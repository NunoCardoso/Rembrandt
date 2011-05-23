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
 
package renoir.bin

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.*

StandardAnalyzer analyzer = new StandardAnalyzer()
def indexDir = "index/lucene-html-names-index"
def rootDir = "index/wikipedia-pt-html-names"
Directory index = FSDirectory.getDirectory(indexDir, true)
IndexWriter w = new IndexWriter(index, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED)
List files = ["html-bg.lst","html-de.lst","html-en.lst","html-es.lst","html-it.lst",
 "html-nl.lst","html-nn.lst","html-no.lst","html-pt.lst","html-ro.lst"]

/** To see the namespace distribution: 
select page_namespace, count(*) as c from ${LG}_page group by page_namespace order by c desc
*/
println "Starting..."

files.each{f -> 
	def inputstream = new InputStreamReader(new FileInputStream(rootDir+"/"+f), 
	     conf.get("global.encoding", System.getProperty('file.encoding')) 
	) 
	println "Starting file $f..."
	def BufferedReader br = new BufferedReader(inputstream)	    
	def line
	while ((line = br.readLine()) != null) {
		line = line.trim().replaceAll(/^(..\/)articles\/(.*).html$/) {all, g1, g2 -> "${g1}${g2}"}
    	Document doc = new Document()
    	doc.add(new Field("answer", line, Field.Store.YES, Field.Index.UN_TOKENIZED))
    	w.addDocument(doc)
	}
}
w.optimize()
w.close()
println "Done."