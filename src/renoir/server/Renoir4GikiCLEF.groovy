/* Renoir
 * Copyright (C) 2009 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package renoir.server

import org.apache.log4j.*
import renoir.obj.*
import renoir.rules.*
import rembrandt.obj.SentenceWithPoS
import rembrandt.obj.TermWithPoS
import saskia.bin.Configuration


/** Simple test class that reads a file with PoS sentences */
class Renoir4GikiCLEF {
	
    static Logger log = Logger.getLogger("RenoirClientMain")  

	static void main(args) {
		if (args.size() < 2) {
			log.fatal "Usage: Renoir4GikiCLEF [conf-file]."
			System.exit(0)
		}
		
		Configuration conf = Configuration.newInstance(args[0])

// load topics here!
		List topics = [
		'Topic 1',
		'Topic 2',
		'Topic 3'
		]
		
		SentenceWithPoS[] sentences

// parse it with Palavras
		topics.eachWithIndex{t, i -> 
			sentences[i] = PALAVRASWebService.parse(topics)
		}
		
// check
		
		new File(args[1]).eachLine{line ->
			if (line =~ /^#/) {sentence = new SentenceWithPoS(0)} 
			else if (line =~ /^$/) {
				log.debug "Got question $sentence. Querying RembrandtServer." 
		 
 	   			QuestionAnalyser qa = new QuestionAnalyser()
				//Question2SPARQL qd = new Question2SPARQL()
				Question q = new Question(sentence)
				
				// apply rules to determine the query
				qa.applyRules(q, QuestionRulesPT.rulesToDetectQuestionType)
				qa.applyRules(q, QuestionRulesPT.rulesToCaptureQuestionEntities) 
				// execute rules does NOT use clauses.
				qa.executeRules(q, QuestionRulesPT.rulesToDetectEAT) 
				
				// now, from each case, decide the answer
				//qd.query(q)
				
				// I may have answers. 
				if (q.answer)  {
					println "Query done. answer = "+q.answer
					println "Justifications: = "+q.answerJustification
				} else {
				// if not, let's go to good old IR
					println "No answer."			
				}
				q.dump()
			
			
			}
			else {
				// avoid <mwe> tags 
				if (!line.startsWith("<")) {	
					sentence << new TermWithPoS(line)
				}
			} 	
		}  
	}
}