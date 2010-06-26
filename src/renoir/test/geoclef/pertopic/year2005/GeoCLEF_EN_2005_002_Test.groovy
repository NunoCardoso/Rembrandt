
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
 class GeoCLEF_2005_EN_002_Test extends GroovyTestCase{
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    
    public GeoCLEF_2005_EN_002_Test() {
		conf = Configuration.newInstance()
	//	conf.set("saskia.dbpedia.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
	//	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
	//	println "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:002 vegetable exporters in Europe"
	    
		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['vegetable','exporters','in','Europe']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0
	
  
	// check detected NEs 
		assert question.nes.size() == 1
	//println "question nes wikipedia stuff & dbpedia stuff: "+question.nes[0].wikipediaPage+ " "+question.nes[0].dbpediaPage
		assert question.nes[0].terms*.text == ["Europe"]
    
		/// contains gives false. Use find
		assert question.nes[0].classification.find{it == SC.place_human_division}
		
	// check subjects 
	
		assert !question.subject
 
	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ["Europe"]
	
		assert !question.expectedAnswerTypes
		
	// check answers 
		assert !question.answer

	// now, question object full -> reformulated query 
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	// reformulated Query -> string    
	
		println refq.toString()
		String reformulated_x = """
label:002 contents:vegetable contents:exporters contents:"Europe" 
woeid-index:24865675 woeid-index:12577865 woeid-index:20069817 woeid-index:20069818 
woeid-index:23424742 woeid-index:23424744 woeid-index:23424750 woeid-index:23424757 
woeid-index:23424761 woeid-index:23424765 woeid-index:23424771 woeid-index:23424796 
woeid-index:23424803 woeid-index:23424805 woeid-index:23424810 woeid-index:23424812 
woeid-index:23424816 woeid-index:23424819 woeid-index:23424825 woeid-index:23424829 
woeid-index:23424833 woeid-index:23424843 woeid-index:23424844 woeid-index:23424845 
woeid-index:23424853 woeid-index:23424874 woeid-index:23424875 woeid-index:23424877 
woeid-index:23424879 woeid-index:23424881 woeid-index:23424885 woeid-index:23424890 
woeid-index:23424892 woeid-index:23424897 woeid-index:23424909 woeid-index:23424910 
woeid-index:23424923 woeid-index:23424925 woeid-index:23424933 woeid-index:23424945 
woeid-index:23424947 woeid-index:23424950 woeid-index:23424954 woeid-index:23424957 
woeid-index:23424975 woeid-index:23424976 woeid-index:23424986 woeid-index:26812346 
woeid-index:28289413
"""
		assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  }
}

/*
woeid-index:24865675 Europe
woeid-index:12577865 Aland Islands
woeid-index:20069817 Montenegro
woeid-index:20069818 Serbia
woeid-index:23424742 Albania
woeid-index:23424744 Andorra
woeid-index:23424750 Austria
woeid-index:23424757 Belgium
woeid-index:23424761 Bosnia and Herzegovina
woeid-index:23424765 Belarus
woeid-index:23424771 Bulgaria
woeid-index:23424796 Denmark
woeid-index:23424803 Ireland
woeid-index:23424805 Estonia
woeid-index:23424810 Czech
woeid-index:23424812 Finland
woeid-index:23424816 Faroe
woeid-index:23424819 France
woeid-index:23424825 Gibraltar
woeid-index:23424829 Germany
woeid-index:23424833 Greece
woeid-index:23424843 Croatia
woeid-index:23424844 Hungary
woeid-index:23424845 Iceland
woeid-index:23424853 Italy
woeid-index:23424874 Latvia 
woeid-index:23424875 Lithuania
woeid-index:23424877 Slovakia
woeid-index:23424879 Liechenstein
woeid-index:23424881 Luxembourg
woeid-index:23424885 Moldava
woeid-index:23424890 Macedonia
woeid-index:23424892 Monaco
woeid-index:23424897 Malta
woeid-index:23424909 Netherlands
woeid-index:23424910 Norway
woeid-index:23424923 Poland
woeid-index:23424925 Portugal
woeid-index:23424933 Romania
woeid-index:23424945 Slovenia
woeid-index:23424947 San Marino
woeid-index:23424950 Spain
woeid-index:23424954 Sweden
woeid-index:23424957 Switzerland
woeid-index:23424975 UK
woeid-index:23424976 Ukraine
woeid-index:23424986 Vatican
woeid-index:26812346 Cyprus
woeid-index:28289413 Svalbard and Jan Mayen
*/

