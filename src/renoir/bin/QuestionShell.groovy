
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

import saskia.bin.Configuration
import org.apache.log4j.Logger
import org.apache.commons.cli.* 
import rembrandt.bin.Rembrandt
import rembrandt.obj.Sentence
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser
import renoir.obj.QuestionAnalyser
import renoir.obj.Question
import renoir.rules.*
import rembrandt.bin.*
import rembrandt.tokenizer.*
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.obj.Term
import rembrandt.obj.Document
import renoir.obj.Condition
import renoir.obj.ExpectedAnswerType
import renoir.obj.QuestionType
import renoir.obj.Subject


/**
 * @author Nuno Cardoso
 * This is for monitoring query parsing
 */
class QuestionShell {
    
    static Logger log = Logger.getLogger("RenoirMain")
    
    static void main(args) {
        
        def conf, conffilepath
        String lang
        
        
        Options o = new Options()
        o.addOption("conf", true, "Configuration file")
        o.addOption("lang", true, "lang")
        o.addOption("help", false, "Gives this help information")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java renoir.bin.Renoir", o )
            System.exit(0)
        }
        
       
        if (!cmd.hasOption("conf")) {
            log.info "No configuration file given. Using default configuration file."
            conffilepath = Configuration.defaultconf
        } else {
            conffilepath = cmd.getOptionValue("conf")
        }
  //      log.info "Renoir version ${Rembrandt.getVersion()}. Welcome."
        conf = Configuration.newInstance(conffilepath)
        
         if (cmd.hasOption("lang")) {
            log.info "Lang = $lang"
            lang = cmd.getOptionValue("lang")
        } else {
            lang = conf.get("global.lang")
            log.info "Language defaulted to $lang"
        }
        
        
        /**********/
        /** MAIN **/
        /**********/
        
        String input = "x"
         
        while (input && !(input?.equalsIgnoreCase("exit") || input?.equalsIgnoreCase("quit")) ) {
            
            print "question? "
            
            BufferedReader inp = new BufferedReader(new InputStreamReader(System.in))
            input = inp.readLine()?.trim()

            // default values
            int limit = 10
            int offset = 0
            String label = null
            String output = "list"

            if (input) {
                // this parses the query, gets parameters for RENOIR and for LGTE
        	Question question = new Question()
        	List sentences = TokenizerPT.newInstance().parse(input)
        	
        	question.sentence = sentences[0]
        	QuestionAnalyser qa = new QuestionAnalyser()
    	    
        	RembrandtCore core = Class.forName("rembrandt.bin.RembrandtCore"+lang.toUpperCase()+"forHAREM").newInstance() 	    
        	Document doc= new Document()
        	doc.body_sentences = [question.sentence.clone()]
        	doc.indexBody()
        	doc = core.releaseRembrandtOnDocument(doc)	
    	    // Rembrandt the sentence, add NEs to the Question NE list
    	    
    	    
        	QuestionRules qr = Class.forName("renoir.rules.QuestionRules2"+lang.toUpperCase()).newInstance() 	    
        	
        	qa.applyRules(question, qr.rulesToDetectQuestionType)
        	qa.applyRules(question, qr.rulesToCaptureSubjects)
        	println "Question sentence: ${question.sentence}"
        	println "Question explanation: ${question.explanation}"
        	println "Question expectedAnswerType: ${question.expectedAnswerType}"
        	println "Question questionType: ${question.questionType}"
        	println "Question questionTypeTerms: ${question.questionTypeTerm}"
         	println "Question NEs: ${question.nes}"           
               	println "Question subject: ${question.subject}"           
               	println "Question conditions: ${question.conditions}"            

            }
        }     
    }
}

