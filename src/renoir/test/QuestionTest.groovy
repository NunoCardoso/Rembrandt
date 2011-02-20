
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

import groovy.util.GroovyTestCase
import org.apache.log4j.Logger
import saskia.bin.Configuration
import rembrandt.tokenizer.TokenizerPT
import renoir.obj.*

/**
 * @author Nuno Cardoso
 *
 */
 class QuestionTest extends GroovyTestCase{
        
    static Logger log = Logger.getLogger("SaskiaTest")
    Configuration conf = Configuration.newInstance()
    
    public QuestionTest() {}
        
    void testParse() {
        String x = "qe:BRF search:true presidente "+
        "presidente^0.5 \"Aníbal\" \"Cavaco Silva\" "+
        "ne-PESSOA:\"Cavaco Silva\"^2 ne-PESSOA:Aníbal^0.1 "+
        "woeid:l352526 "+
        "model:BM25 ne-PESSOA-weight:2 contents-weight:1" 
        
        // creates a query out of a string 
        RenoirQuery rq = RenoirQueryParser.parse(x) 
        // then prepares a question object to be filled with stuff
        Question q = rq.convertToQuestion()
        
       
        println q.sentence
	/*log.debug "params for Renoir: "+rq.paramsForRenoir
	log.debug "params for LGTE: "+rq.paramsForLGTE
	log.debug "Query terms: "+rq.terms
        */
	
        
    }
}
