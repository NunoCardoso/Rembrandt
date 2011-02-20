
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
	
package renoir.test
import org.apache.log4j.Logger

import saskia.bin.Configuration
import renoir.bin.Renoir
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser

/**
 * @author Nuno Cardoso
 * Use LGTE and Renoir class to check if retrieval results, scores etc are OK

In Col-8 there are 129 docs. 

terms: there are *44* documents that have 'Portugal' as a term.
[3, 4, 8, 11, 17, 19, 22, 24, 25, 26, 33, 34, 35, 38, 39, 40, 41, 42, 48, 51, 53, 55, 59, 
60, 63, 65, 66, 68, 69, 75, 77, 78, 83, 84, 86, 88, 90, 92, 93, 112, 113, 117, 125, 126]

woeid: Portugal's woeid is 23424925 gives *55* docs:
[3, 4, 8, 11, 16, 17, 19, 20, 22, 23, 24, 25, 26, 32, 33, 34, 35, 36, 38, 39, 40, 
41, 42, 48, 49, 51, 53, 54, 56, 57, 59, 60, 63, 65, 66, 68, 69, 73, 75, 77, 78, 79, 
81, 83, 84, 86, 88, 90, 92, 102, 112, 113, 117, 125, 126]


Docs with portugal but no woeid: *2* [55, 93] 

Docs with woeid but no 'portugal': *13* [16, 20, 23, 32, 36, 49, 54, 56, 57, 73, 79, 81, 102]

 - 2 documents with portugal but no woeid
 - 13 documents with woeid but no portugal
 - 42 documents with both

So, contents:portugal + woeid should yield 42 + 2 + 13 = 57

and contents:portugal filter-woeid: should give only 42.

*/


class LGTEFiltersTest extends GroovyTestCase {

    Configuration conf
    static Logger log = Logger.getLogger("RenoirTest")

	// CD of second HAREM
    static String collection_dir = "col-8"
	static String lang = "pt"
	
    Renoir renoir
    int limit = 1000
    int offset = 0
    
    public LGTEFiltersTest() {
    
 		// initialize by reading the topics and qrels
		conf = Configuration.newInstance()
		String fileseparator = System.getProperty("file.separator")
		String homedir = conf.get("rembrandt.home.dir",".")
    	String indexdir = homedir+fileseparator+"index"+fileseparator+collection_dir
        log.info "Loading index in $indexdir"
    	renoir = new Renoir(conf, indexdir, lang)
    }

// FIRST: only contents:portugal
    void testSimpleContent() {
        
		log.info "Testing simple BM25 contents:Portugal" 
		String s = "model:BM25Normalized qe:no search:true explain:true contents:Portugal"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
       log.info "Final query: ${res['final_query_string']}\n"
       log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
       int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
            Map result = res["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  "
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		List ids = []
		 for (int i = 0; i < res["total"]; i++) {
           ids << res["result"][i]["doc_id"]
		}
		println "List of ids: "+ids.sort()
			
		log.info "Testing if there is 44 documents with contents:Portugal"
		assert res["result"].size() == 44 

// correct, 44 docs with portugal as a term 
 /*
0 59 hub-41899  [[field:contents, score:1.0, term:portugal, weight:1.0, doc:59]]
1 40 hub-15590  [[field:contents, score:0.98541874, term:portugal, weight:1.0, doc:40]]
2 75 hub-66526  [[field:contents, score:0.979218, term:portugal, weight:1.0, doc:75]]
3 78 hub-68694  [[field:contents, score:0.97315925, term:portugal, weight:1.0, doc:78]]
4 51 hub-28874  [[field:contents, score:0.95297873, term:portugal, weight:1.0, doc:51]]
5 42 hub-16632  [[field:contents, score:0.9523001, term:portugal, weight:1.0, doc:42]]
6 83 hub-78051  [[field:contents, score:0.9045043, term:portugal, weight:1.0, doc:83]]
7 53 hub-31642  [[field:contents, score:0.8930787, term:portugal, weight:1.0, doc:53]]
8 77 hub-67792  [[field:contents, score:0.8884921, term:portugal, weight:1.0, doc:77]]
9 88 hub-93257  [[field:contents, score:0.8783035, term:portugal, weight:1.0, doc:88]]
List of ids: [3, 4, 8, 11, 17, 19, 22, 24, 25, 26, 33, 34, 35, 38, 39, 40, 41, 42, 48, 51, 53, 55, 59, 60, 63, 65, 66, 68, 69, 75, 77, 78, 83, 84, 86, 88, 90, 92, 93, 112, 113, 117, 125, 126]

*/   }

// SECOND: only woeid:23424925
	void testSimpleWOEID() {
        
		log.info "Testing simple BM25 woeid:23424925" 
		String s = "model:BM25Normalized qe:no search:true explain:true woeid:23424925"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
             Map result = res["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
				
		List ids = []
		 for (int i = 0; i < res["total"]; i++) {
           ids << res["result"][i]["doc_id"]
		}
		println "List of ids: "+ids.sort()
			
		log.info "Testing if there is 55 documents"
		assert res["result"].size() == 55 
		
// correct, there are 55 docs with portugal's woeid
 /*
0 65 hub-51467  1.0[[field:woeid, score:1.0, term:23424925, weight:1.0, doc:65]]
1 38 hjlll  0.9720478[[field:woeid, score:0.9720478, term:23424925, weight:1.0, doc:38]]
2 42 hub-16632  0.9720478[[field:woeid, score:0.9720478, term:23424925, weight:1.0, doc:42]]
3 51 hub-28874  0.9720478[[field:woeid, score:0.9720478, term:23424925, weight:1.0, doc:51]]
4 60 hub-43823  0.9720478[[field:woeid, score:0.9720478, term:23424925, weight:1.0, doc:60]]
5 126 wpt-1000772700099419796  0.9720478[[field:woeid, score:0.9720478, term:23424925, weight:1.0, doc:126]]
6 4 H2-Ert75  0.96124184[[field:woeid, score:0.96124184, term:23424925, weight:1.0, doc:4]]
7 11 Ntyr-78  0.96124184[[field:woeid, score:0.96124184, term:23424925, weight:1.0, doc:11]]
8 39 hub-15425  0.96124184[[field:woeid, score:0.96124184, term:23424925, weight:1.0, doc:39]]
9 113 wpt-10034934164544455  0.96124184[[field:woeid, score:0.96124184, term:23424925, weight:1.0, doc:113]]
List of ids: [3, 4, 8, 11, 16, 17, 19, 20, 22, 23, 24, 25, 26, 32, 33, 34, 35, 36, 38, 39, 40, 41, 42, 48, 49, 51, 53, 54, 56, 57, 59, 60, 63, 65, 66, 68, 69, 73, 75, 77, 78, 79, 81, 83, 84, 86, 88, 90, 92, 102, 112, 113, 117, 125, 126]
*/
   }

// THIRD: Test contents + woeid 
	void testSimpleContentsAndWOEID() {
        
		log.info "Testing simple BM25 contents + woeid" 
		String s = "model:BM25Normalized qe:no search:true explain:true "+
		"contents:portugal woeid:23424925"
		
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
            Map result = res["result"][i]
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		List ids = []
		 for (int i = 0; i < res["total"]; i++) {
           ids << res["result"][i]["doc_id"]
		}
		println "List of ids: "+ids.sort()
			
		log.info "Testing if there is 57 documents"
		assert res["result"].size() == 57 // terms
    }
// ok, here we have 55 docs with portugal's woeid, plus 2: 55, 93

// doc 55 and 93 has term 'portugal' but no woeid
/*
0 51 hub-28874  1.9250265[[field:contents, score:0.95297873, term:portugal, weight:1.0, doc:51], [field:woeid, score:0.9720478, term:23424925, weight:1.0, doc:51]]
1 42 hub-16632  1.9243479[[field:contents, score:0.9523001, term:portugal, weight:1.0, doc:42], [field:woeid, score:0.9720478, term:23424925, weight:1.0, doc:42]]
2 40 hub-15590  1.9107947[[field:contents, score:0.98541874, term:portugal, weight:1.0, doc:40], [field:woeid, score:0.925376, term:23424925, weight:1.0, doc:40]]
3 75 hub-66526  1.880608[[field:contents, score:0.979218, term:portugal, weight:1.0, doc:75], [field:woeid, score:0.90138996, term:23424925, weight:1.0, doc:75]]
4 78 hub-68694  1.8745492[[field:contents, score:0.97315925, term:portugal, weight:1.0, doc:78], [field:woeid, score:0.90138996, term:23424925, weight:1.0, doc:78]]
5 59 hub-41899  1.8611162[[field:contents, score:1.0, term:portugal, weight:1.0, doc:59], [field:woeid, score:0.8611161, term:23424925, weight:1.0, doc:59]]
6 65 hub-51467  1.7803652[[field:contents, score:0.7803652, term:portugal, weight:1.0, doc:65], [field:woeid, score:1.0, term:23424925, weight:1.0, doc:65]]
7 83 hub-78051  1.7656205[[field:contents, score:0.9045043, term:portugal, weight:1.0, doc:83], [field:woeid, score:0.8611161, term:23424925, weight:1.0, doc:83]]
8 38 hjlll  1.7555035[[field:contents, score:0.7834557, term:portugal, weight:1.0, doc:38], [field:woeid, score:0.9720478, term:23424925, weight:1.0, doc:38]]
9 125 ven-098  1.7438293[[field:contents, score:0.78613615, term:portugal, weight:1.0, doc:125], [field:woeid, score:0.9576931, term:23424925, weight:1.0, doc:125]]
List of ids: [3, 4, 8, 11, 16, 17, 19, 20, 22, 23, 24, 25, 26, 32, 33, 34, 35, 36, 38, 39, 40, 41, 42, 48, 49, 51, 53, 54, 55, 56, 57, 59, 60, 63, 65, 66, 68, 69, 73, 75, 77, 78, 79, 81, 83, 84, 86, 88, 90, 92, 93, 102, 112, 113, 117, 125, 126]
*/

// FOURTH: Test contents + woeid, where woeid is a filter! 
	void testSimpleContentsAndWOEIDFiltered() {
        
		log.info "Testing simple BM25 contents + woeid" 
		String s = "model:BM25Normalized qe:no search:true explain:true "+
		"contents:portugal woeid:23424925 woeid-filter:yes"
		
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
            Map result = res["result"][i]
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}

		
		List ids = []
		 for (int i = 0; i < res["total"]; i++) {
           ids << res["result"][i]["doc_id"]
		}
		println "List of ids: "+ids.sort()
			
		log.info "Testing if there is 42 documents"
		assert res["result"].size() == 42 // terms
    }
// there are only 42 docs with both term 'portugal' and woeid portugal
/*
0 59 hub-41899  1.0[[field:contents, score:1.0, term:portugal, weight:1.0, doc:59]]
1 40 hub-15590  0.98541874[[field:contents, score:0.98541874, term:portugal, weight:1.0, doc:40]]
2 75 hub-66526  0.979218[[field:contents, score:0.979218, term:portugal, weight:1.0, doc:75]]
3 78 hub-68694  0.97315925[[field:contents, score:0.97315925, term:portugal, weight:1.0, doc:78]]
4 51 hub-28874  0.95297873[[field:contents, score:0.95297873, term:portugal, weight:1.0, doc:51]]
5 42 hub-16632  0.9523001[[field:contents, score:0.9523001, term:portugal, weight:1.0, doc:42]]
6 83 hub-78051  0.9045043[[field:contents, score:0.9045043, term:portugal, weight:1.0, doc:83]]
7 53 hub-31642  0.8930787[[field:contents, score:0.8930787, term:portugal, weight:1.0, doc:53]]
8 77 hub-67792  0.8884921[[field:contents, score:0.8884921, term:portugal, weight:1.0, doc:77]]
9 88 hub-93257  0.8783035[[field:contents, score:0.8783035, term:portugal, weight:1.0, doc:88]]
List of ids: [3, 4, 8, 11, 17, 19, 22, 24, 25, 26, 33, 34, 35, 38, 39, 40, 41, 42, 48, 51, 53, 59, 60, 63, 65, 66, 68, 69, 75, 77, 78, 83, 84, 86, 88, 90, 92, 112, 113, 117, 125, 126]
*/
	
}
