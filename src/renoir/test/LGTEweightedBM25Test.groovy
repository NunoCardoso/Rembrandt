
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

terms: there are 44 documents that have 'Portugal' as a term.
3,4,8,11,17,19,22,24,25,26,33,34,35,38,39,40,41,42,48,51,53,55,59,60,63,65,66,68,
69,75,77,78,83,84,86,88,90,92,93,112,113,117,125,126.
in Luke, the top10 rank using term-index is: 42,40,59,51,78,83,75,53,77,88

there is 2 docs with cavaco: 46, 60. In the 46, there is no 'portugal'
there is 1 doc with '+cavaco +portugal': 60
there is 3 docs with 'PSD': 48,49,60

ne:
in NE-LOCAL-HUMANO-PAIS-index: there are 39 docs with Portugal on it:
3,4,8,11,17,19,22,24,25,26,33,34,35,38,39,40,42,48,51,53,59,60,63,65,68,
75,77,78,83,84,86,88,90,92,112,113,117,125,126
in NE-PESSOA-INDIVIDUAL-index:"Cavaco Silva" dá dois docs: 46, 60  (usar SimpleAnalyzer)

entity:
'Portugal' gives 39 docs. Much likely the same from NE.
'An%C3%ADbal_Cavaco_Silva' gives 1 document, 46

woeid: Portugal's woeid is 23424925 gives 57 docs:
note that some of them are Portuguese cities that were expanded to ancestors.
2,3,4,8,11,13,17,19,20,22,23,24,25,26,33,34,35,36,38,39,53,54,56,57,
59,60,62,63,65,66,68,69,71,73,79,83,84,86,88,90,92,102,106,112,113,117,125,126

tg-index:
2004 appears in 10 documents: 16,27,55,65,72,80,86,103,120,126

 */
class LGTEweightedBM25Test extends GroovyTestCase {

    Configuration conf
    static Logger log = Logger.getLogger("RenoirTest")

	// CD of second HAREM
    static String collection_dir = "col-8"
	static String lang = "pt"
	
    Renoir renoir
    int limit = 1000
    int offset = 0
    
    public LGTEweightedBM25Test() {
    
 		// initialize by reading the topics and qrels
		conf = Configuration.newInstance()
		String fileseparator = System.getProperty("file.separator")
		String homedir = conf.get("rembrandt.home.dir",".")
    	String indexdir = homedir+fileseparator+"index"+fileseparator+collection_dir
        log.info "Loading index in $indexdir"
    	renoir = new Renoir(conf, indexdir, lang)
    }

// NORMAL WEIGHTS 

// no weights
	void testBM25everythingNoWeight() {
        
		log.info "Testing simple BM25 term + ne + entity + woeid" 
		String s = "model:BM25Normalized qe:no search:true explain:true "+
		"contents:portugal contents:cavaco ne-LOCAL-HUMANO-PAIS-index:Portugal "+
		"ne-PESSOA-INDIVIDUAL-index:\"Cavaco Silva\" "+
		"entity:Portugal entity:An%C3%ADbal_Cavaco_Silva "+
		"woeid-index:23424925"
		
		RenoirQuery rq = RenoirQueryParser.parse(s)
		
		println rq.paramsForQueryConfiguration
		
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
		log.info "Testing if there is 60 documents"
		assert res["result"].size() == 60 // terms
    }

	void testBM25everythingWithWeight() {
        
		log.info "Testing simple BM25 term + ne + entity + woeid" 
		String s = "model:BM25Normalized qe:no search:true explain:true "+
		"contents:portugal contents:cavaco ne-LOCAL-HUMANO-PAIS-index:Portugal "+
		"ne-PESSOA-INDIVIDUAL-index:\"Cavaco Silva\" "+
		"entity:Portugal entity:An%C3%ADbal_Cavaco_Silva "+
		"woeid-index:23424925 woeid-weight:0.001" //xpto-default-weight:0.4
		
		RenoirQuery rq = RenoirQueryParser.parse(s)
				println rq.paramsForQueryConfiguration
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

		log.info "Testing if there is 60 documents"
		assert res["result"].size() == 60 // terms
    }

	
}
