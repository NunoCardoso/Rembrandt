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

import rembrandt.obj.Sentence
import renoir.obj.Question
import renoir.obj.QueryTag
import renoir.suggestion.SuggestionType

import org.apache.log4j.*

class RenoirAnswerDecision {
     
     Question q
     List<QueryTag> tags_
     static Logger log = Logger.getLogger("RenoirMain")
     
     public RenoirAnswerDecision() {
	 	tags_ = [] 
     }
     
	public void slurp(String query, List tags) {
	 	q = new Question(Sentence.simpleTokenize(query))
	 	tags?.each{tag -> 
	 		log.debug "Tag: $tag"
	 		tags_ << new QueryTag(type:SuggestionType.getFromKey(tag.type), 
	 			name:Sentence.simpleTokenize(tag.name), 
	 			desc:tag.desc,
	 			ground:tag.ground)	 		
	 	}
	 	// now, let's try to position them
	 	tags_.eachWithIndex{tag, i -> 
	 		tags_[i].index = q.sentence.indexOf(tag.name)
	 	}
	}
     
     public String getAnswer() {
	    log.debug "Answer: "+q.toString()+" "+tags_
	 	return ""+q.toString()+" "+tags_
     }
}