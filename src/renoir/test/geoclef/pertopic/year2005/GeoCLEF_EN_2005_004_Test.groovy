
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
import renoir.rules.*
import rembrandt.obj.Document
import rembrandt.bin.*
import rembrandt.tokenizer.*
import rembrandt.gazetteers.CommonClassifications as SC
/**
 * @author Nuno Cardoso
 *
 */
 class GeoCLEF_2005_EN_004_Test extends GroovyTestCase{
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    
    public GeoCLEF_2005_EN_004_Test() {
		conf = Configuration.newInstance()
	//	conf.set("saskia.dbpedia.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
	//	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
	//	println "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:004 animal rights actions against the fur industry in Europe and the United States"
	    
		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['animal','rights','actions','against','the','fur','industry','in','Europe','and','the','United','States']	    
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0
	
  
	// check detected NEs 
		assert question.nes.size() == 2
	//println "question nes wikipedia stuff & dbpedia stuff: "+question.nes[0].wikipediaPage+ " "+question.nes[0].dbpediaPage
	//	assert question.nes[0].terms*.text == ["Animal"]
		assert question.nes[0].terms*.text == ["Europe"]
		assert question.nes[1].terms*.text == ["United","States"]
    
		/// contains gives false. Use find
		//assert question.nes[0].classification.find{it == SC.thing_object}
		assert question.nes[0].classification.find{it == SC.place_human_division}
		assert question.nes[1].classification.find{it == SC.place_human_country}
		
	// check subjects 
		// subject matches to "States"
	//	assert !question.subject
    
	// check conditions 
		assert question.conditions.size() == 2
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[1].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ["Europe"]
		assert question.conditions[1].object.ne.terms*.text == ["United","States"]
			
		// como há um subject... mas seja o que for em SPARQL, deve devolver nada de jeito e não deve ser adicionado
		assert !question.expectedAnswerTypes
		
	// check answers 
		assert !question.answer

	// now, question object full -> reformulated query 
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	// reformulated Query -> string    
	
		println refq.toString()
		String reformulated_x = """
label:004 contents:animal contents:rights contents:actions contents:against 
contents:fur contents:industry contents:"Europe" contents:"United States" 
woeid-index:24865675 woeid-index:12577865 woeid-index:20069817 
woeid-index:20069818 woeid-index:23424742 woeid-index:23424744 woeid-index:23424750 woeid-index:23424757 
woeid-index:23424761 woeid-index:23424765 woeid-index:23424771 woeid-index:23424796 woeid-index:23424803 
woeid-index:23424805 woeid-index:23424810 woeid-index:23424812 woeid-index:23424816 woeid-index:23424819 
woeid-index:23424825 woeid-index:23424829 woeid-index:23424833 woeid-index:23424843 woeid-index:23424844 
woeid-index:23424845 woeid-index:23424853 woeid-index:23424874 woeid-index:23424875 woeid-index:23424877 
woeid-index:23424879 woeid-index:23424881 woeid-index:23424885 woeid-index:23424890 woeid-index:23424892 
woeid-index:23424897 woeid-index:23424909 woeid-index:23424910 woeid-index:23424923 woeid-index:23424925 
woeid-index:23424933 woeid-index:23424945 woeid-index:23424947 woeid-index:23424950 woeid-index:23424954 
woeid-index:23424957 woeid-index:23424975 woeid-index:23424976 woeid-index:23424986 woeid-index:26812346 
woeid-index:28289413 woeid-index:23424977
"""

	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}
