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
import saskia.io.Collection
import renoir.bin.Renoir
import renoir.bin.RenoirCore
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser

/**
 * @author Nuno Cardoso
 * Check stemming differences

Segundo o stem-index:

1. portugu há em 29 docs:
3,9,10,19,21,23,24,34,36,38,53,54,57,58,
59,63,66,75,79,81,83,87,88,89,94,100,104,117,120

2.portu em 2: 84 e 97

3.portug em 2: 48 e 94

4.portugal em 44:
3,4,8,11,17,19,22,24,25,26,33,34,35,38,39,40,41,42,48,51,
66,68,69,75,77,78,83,84,86,88,90,92,93,112,13,117,125,126

5.portugues em 29:
13,14,15,16,18,19,23,24,34,40,45,53,59,61,62,63,69,71,74,76,
78,83,84,87,88,90,93,94,104

Segundo o nostem-index:

portugais em 1 doc: 48
portugal em 44 docs:3,4,8,11,17,19,22,24,25,26,33,34,35,38,39,40,41,42,48,51,
66,68,69,75,77,78,83,84,86,88,90,92,93,112,13,117,125,126
portuges em 1 doc: 94
portugues em 29 docs: 3,9,10,19,21,23,24,34,36,38,53,54,57,58,59,
63,66,75,79,81,83,87,88,89,94,100,104,117,120
portuguesa em 18 docs:
14,16,19,23,24,34,40,45,59,69,71,74,76,83,84,90,94,104
portuguesas em 3 docs:
18,53,104
portugueses em 12 docs:
13,15,40,59,61,62,63,78,87,88,93,104



 */
class LGTEstemmersTest extends GroovyTestCase {

    Configuration conf
    static Logger log = Logger.getLogger("RenoirTest")

	// CD of second HAREM
    static Collection col = Collection.getFromID(8)
	 static String lang = "pt"
	
    Renoir renoir_stem
    Renoir renoir_nostem
    int limit = 1000
    int offset = 0
    
    public LGTEstemmersTest() {
    
 		// initialize by reading the topics and qrels
		conf = Configuration.newInstance()
	
		// null indicates to search for default index dir using conf info and collection info
    	renoir_stem = RenoirCore.getCore(conf, col, null, true)
    	renoir_nostem = RenoirCore.getCore(conf, col, null, false)
	 }


	// bm25 normal, ne-LOCAL:Portugal
    void testSimpleBM25oneSimpleNE() {
        
		log.info "Testing simple contents" 
		String s = "model:bm25 qe:no search:true explain:true "+
		"contents:portugueses"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res_stem = renoir_stem.search(rq)
		Map res_nostem = renoir_nostem.search(rq)
        
      log.info "STEM: Final query: ${res_stem['final_query_string']}\n"
      log.info "NO_STEM: Final query: ${res_nostem['final_query_string']}\n"
      log.info "STEM: Got "+res_stem["total"]+" docs in "+res_stem["time"]+" msecs.\n"
      log.info "NO_STEM: Got "+res_nostem["total"]+" docs in "+res_nostem["time"]+" msecs.\n"
      
		log.info "STEM:"
		int top = (res_stem["total"] > 10 ? 10 : res_stem["total"])        
        for (int i = 0; i < top; i++) {
             Map result = res_stem["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 29 documents with contents:portugueses"
		assert res_stem["result"].size() == 29 // terms
		
		log.info "NO STEM:"
		top = (res_nostem["total"] > 10 ? 10 : res_nostem["total"])        
        for (int i = 0; i < top; i++) {
             Map result = res_nostem["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 12 documents with contents:portugueses"
		assert res_nostem["result"].size() == 12// terms
    }

}
