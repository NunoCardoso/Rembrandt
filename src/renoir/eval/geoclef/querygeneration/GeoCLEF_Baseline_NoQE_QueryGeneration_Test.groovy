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

package renoir.eval.geoclef.querygeneration

import org.apache.log4j.Logger

import saskia.bin.Configuration
import renoir.bin.Renoir
import renoir.obj.*

/**
 * @author Nuno Cardoso
 *
 */
class GeoCLEF_Baseline_NoQE_QueryGeneration_Test extends GeoCLEF_QueryGeneration_Test {

    public GeoCLEF_Baseline_NoQE_QueryGeneration_Test(topic_file, query_file, lang) {    
	super(topic_file, query_file, lang) 	
    }
      
    void generate() {

	super.topics.each{topicline -> 
	   // it eats also the label: stuff   
	   log.debug "Processing topic line: $topicline"
	   super.logf.append "Processing topic line: $topicline\n"
	   RenoirQuery rq = RenoirQueryParser.parse(topicline)
	   Question q = rq.convertToQuestion(lang)
	   // with an unprocessed question, it should be straightforward equal
	   ReformulatedQuery rq2 = QueryReformulator2.reformulate(rq, q)
	   log.debug "Got $rq2"
	   super.logf.append "Got $rq2\n"
	   queryf.append rq2.toString()+"\n"
	}
	       
    }

   
}
