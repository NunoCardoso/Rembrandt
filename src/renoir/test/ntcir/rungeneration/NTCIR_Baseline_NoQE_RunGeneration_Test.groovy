
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

package renoir.test.ntcir.rungeneration

import org.apache.log4j.Logger
import ireval.SetRetrievalEvaluator
import ireval.RetrievalEvaluator.Document
import ireval.RetrievalEvaluator.Judgment

import saskia.bin.Configuration
import saskia.io.Collection
import renoir.bin.Renoir
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser

/**
 * @author Nuno Cardoso
 *
 */
class NTCIR_Baseline_NoQE_RunGeneration_Test extends NTCIR_RunGeneration_Test {

    static Logger log = Logger.getLogger("RenoirTest")

    int limit=1000
    int offset = 0
    
    public NTCIR_Baseline_NoQE_RunGeneration_Test(
	    String query_file, String qrel_file, String run_file, Collection collection, 
	String lang, Boolean stem) {
			super(query_file, qrel_file, run_file, collection, lang, stem)
	
    }
    
    void generate(bm25_k1 = 2.0d, bm25_b = 0.75d) {
              
        queries.each{query -> 
                 
            query.paramsForRenoir['qe']="no"
            query.paramsForRenoir['search']="true"
            query.paramsForRenoir['limit'] = limit
            query.paramsForRenoir['offset'] = offset  
                  
            query.paramsForLGTE['model'] = "BM25Normalized"
            query.paramsForQueryConfiguration["bm25.idf.policy"] = "standard"
            query.paramsForQueryConfiguration["bm25.k1"] = bm25_k1
            query.paramsForQueryConfiguration["bm25.b"] = bm25_b
            
            logf.append "Parameters for Renoir: ${query.paramsForRenoir}\n"
            logf.append "Parameters for LGTE: ${query.paramsForLGTE}\n"
            logf.append "Query configuration: ${query.paramsForQueryConfiguration}\n"
           
            super.submit(query)
            
        }
    }
      
    void generateWithWeight( entityweight = 1.0f, 
	    woeidweight=1.0f, tgweight = 1.0f) {
	
        queries.each{query -> 
                 
            query.paramsForRenoir['qe']="no"
            query.paramsForRenoir['search']="true"
            query.paramsForRenoir['limit'] = limit
            query.paramsForRenoir['offset'] = offset  
                  
            query.paramsForLGTE['model'] = "BM25Normalized"
            query.paramsForQueryConfiguration["bm25.idf.policy"] = "standard"
            query.paramsForQueryConfiguration["bm25.k1"] =2.0d
            query.paramsForQueryConfiguration["bm25.b"] = 0.75d
            
            query.paramsForQueryConfiguration["model.field.boost.entity-index"] = entityweight
            query.paramsForQueryConfiguration["model.field.boost.woeid-index"] = woeidweight
            query.paramsForQueryConfiguration["model.field.boost.tg-index"] = tgweight

            logf.append "Parameters for Renoir: ${query.paramsForRenoir}\n"
            logf.append "Parameters for LGTE: ${query.paramsForLGTE}\n"
            logf.append "Query configuration: ${query.paramsForQueryConfiguration}\n"
           
            super.submit(query)
            
        }
    }
    
  //  void evaluate() {
//	super.evaluate()
  //  }
        
}
