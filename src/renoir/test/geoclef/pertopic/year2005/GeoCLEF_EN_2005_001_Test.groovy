
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
package renoir.test.geoclef.pertopic.year2005

import renoir.test.geoclef.pertopic.GeoCLEF_PerTopic_Test
import groovy.util.GroovyTestCase
import org.apache.log4j.Logger
import saskia.bin.Configuration
import renoir.obj.*
import renoir.rules.*
import rembrandt.obj.Document
import rembrandt.bin.*
import rembrandt.tokenizer.*
import rembrandt.gazetteers.CommonClassifications as SC
/**
 * @author Nuno Cardoso
 *
 */
 class GeoCLEF_EN_2005_001_Test extends GroovyTestCase{
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    
    public GeoCLEF_EN_2005_001_Test() {
		conf = Configuration.newInstance()
	//	conf.set("saskia.dbpedia.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
	//	println "DBpedia sparql service set to http://xldb.di.fc.ul.pt/dbpedia/sparql"	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:001 shark attacks near Australia and California "
	    
		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['shark','attacks','near','Australia','and','California']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0
	
  
	// check detected NEs 
	//	assert question.nes.size() == 2
		assert question.nes.size() == 2
	//println "question nes wikipedia stuff & dbpedia stuff: "+question.nes[0].wikipediaPage+ " "+question.nes[0].dbpediaPage
		assert question.nes[0].terms*.text == ["Australia"]
		assert question.nes[1].terms*.text == ["California"]
    
		/// contains gives false. Use find
		assert question.nes[0].classification.find{it == SC.place_human_country}
		assert question.nes[1].classification.find{it == SC.place_human_division}
		
		assert question.nes[0].dbpediaPage != null
		assert question.nes[1].dbpediaPage != null
	// check subjects 
	
		assert !question.subject
    
	// check conditions 
		assert question.conditions.size() == 2
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[1].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ["Australia"]
		assert question.conditions[1].object.ne.terms*.text == ["California"]

		assert !question.expectedAnswerTypes
		
	// check answers 
		assert !question.answer

	// now, question object full -> reformulated query 
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	// reformulated Query -> string    
	
		println refq.toString()
		String reformulated_x = """
label:001 contents:shark contents:attacks contents:"Australia" contents:"California" 
woeid:23424748 woeid:2347563
"""
		assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  	} 
 }
   

