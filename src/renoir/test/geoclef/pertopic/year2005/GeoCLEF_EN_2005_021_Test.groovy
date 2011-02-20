
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
package renoir.test.geoclef.pertopic

import groovy.util.GroovyTestCase
import org.apache.log4j.Logger
import saskia.bin.Configuration
import renoir.obj.*
import rembrandt.gazetteers.CommonClassifications as SC
/**
 * @author Nuno Cardoso
 *
 */
 class GeoCLEF_2005_EN_021_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    
    public GeoCLEF_2005_EN_021_Test() {
		conf = Configuration.newInstance()
	//	conf.set("saskia.dbpedia.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
	//	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
	//	println "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:021 Sea rescue in North Sea"

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['Sea','rescue','in','North','Sea']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0
	
  
	// check detected NEs 
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ["Sea"]
		assert question.nes[1].terms*.text == ["North","Sea"]
    
		/// contains gives false. Use find
		assert question.nes[0].classification.find{it == SC.place_physical_watermass}
		assert question.nes[1].classification.find{it == SC.place_physical_watermass}
		
	// check subjects 
		assert !question.subject

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ["North","Sea"]
				
		assert !question.expectedAnswerTypes
	
	// check answers 
		assert !question.answer

		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:021 contents:"Sea" contents:rescue contents:"North Sea" entity:Sea woeid:55959673
"""		
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}
