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

package renoir.test.geoclef.querygeneration

import org.apache.log4j.Logger

import saskia.bin.Configuration
import renoir.bin.Renoir
import renoir.obj.*

/**
 * @author Nuno Cardoso
 * This generates the BRF version of queries 
 */
class GeoCLEF_Statistic_BRF_QueryGeneration_Test extends GeoCLEF_QueryGeneration_Test {

    static Logger log = Logger.getLogger("RenoirTest")
    int limit = 1000
    int offset = 0
    Renoir renoir
    
    public GeoCLEF_Statistic_BRF_QueryGeneration_Test(topic_file, query_file, collection, lang, Boolean stem) {
	super(topic_file, query_file, lang)
	renoir = new Renoir(Configuration.newInstance(), collection, lang, stem)
	
    }
      
    void generate(bm25_k1 = 1.2d, bm25_b = 0.75d, topkterm = 16, topkdoc = 10) {

	topics.each{topicline -> 
	   // it eats also the label: stuff   
	   log.debug "Topic line: $topicline"
	   super.logf.append "Topic line: $topicline\n"
	   
	   RenoirQuery query = RenoirQueryParser.parse(topicline)
	   
	    // let's tell it to do BRF QE
	   query.paramsForRenoir['qe']="brf"
	   query.paramsForRenoir['search']="false" // we just want to generate
	   query.paramsForRenoir['limit'] = limit
	   query.paramsForRenoir['offset'] = offset  
	         
	   query.paramsForLGTE['model'] = "bm25"
	   query.paramsForQueryConfiguration["QE.method"] = "rocchio"
	   query.paramsForQueryConfiguration["QE.decay"] = "0.15"
	   query.paramsForQueryConfiguration["QE.doc.num"] = "${topkdoc}"
	   query.paramsForQueryConfiguration["QE.term.num"] = "${topkterm}"
	   query.paramsForQueryConfiguration["QE.rocchio.alpha"] ="1"
	   query.paramsForQueryConfiguration["QE.rocchio.beta"] = "0.75"
    	       
	   query.paramsForQueryConfiguration["bm25.idf.policy"] = "standard"
	   query.paramsForQueryConfiguration["bm25.k1"] = "${bm25_k1}"
	   query.paramsForQueryConfiguration["bm25.b"] = "${bm25_b}"

	   log.debug "Iniital query string w/configuration: ${query.toString()}" 
	   super.logf.append "Iniital query string w/configuration: ${query.toString()}\n" 
	   Question question = query.convertToQuestion(lang)
	   
	   Map res = renoir.search(query)
	   // Let's REFORMULATE
	  
	   log.debug "Got: "+res["final_query_string"]
	   RenoirQuery query2 =  RenoirQueryParser.parse(res["final_query_string"])
	                         
	   // with an unprocessed question, it should be straightforward equal
	   ReformulatedQuery ref_query = new ReformulatedQuery(query)
	  
	   // let's fuse them
	   ref_query.sentence = query2.sentence
	   
	   // now, add terms from 
	   log.debug "Final query string: $ref_query"
	   super.logf.append "Final query string: $ref_query\n"
	   super.queryf.append ref_query.toString()+"\n"
	}
	       
    }

}
