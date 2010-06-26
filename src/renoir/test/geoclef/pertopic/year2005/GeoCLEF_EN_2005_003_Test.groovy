
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
 class GeoCLEF_2005_EN_003_Test extends GroovyTestCase{
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    
    public GeoCLEF_2005_EN_003_Test() {
		conf = Configuration.newInstance()
	//	conf.set("saskia.dbpedia.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
	//	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
	//	println "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:003 Amnesty International human rights reports in Latin America"
	    
		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['Amnesty','International','human','rights','reports','in','Latin','America']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0
	
  
	// check detected NEs 
		assert question.nes.size() == 2
	//println "question nes wikipedia stuff & dbpedia stuff: "+question.nes[0].wikipediaPage+ " "+question.nes[0].dbpediaPage
		assert question.nes[0].terms*.text == ["Amnesty","International"]
		assert question.nes[1].terms*.text == ["Latin","America"]
    
		/// contains gives false. Use find
		assert question.nes[0].classification.find{it == SC.organization_institution}
		assert question.nes[1].classification.find{it == SC.place_human_division}
		
	// check subjects 
	
		assert !question.subject
		
	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ["Latin","America"]
	
		assert question.expectedAnswerTypes*.resolvesTo == []
		
	// check answers 
		assert !question.answer

	// now, question object full -> reformulated query 
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	// reformulated Query -> string    
	
		println refq.toString()
		String reformulated_x = """
label:003 contents:"Amnesty International" contents:human contents:rights contents:reports 
contents:"Latin America" entity:Amnesty_International 
woeid-index:24865716 woeid-index:23424747 woeid-index:23424760 woeid-index:23424762 
woeid-index:23424768 woeid-index:23424782 woeid-index:23424787 woeid-index:23424791 
woeid-index:23424793 woeid-index:23424798 woeid-index:23424800 woeid-index:23424801 
woeid-index:23424807 woeid-index:23424811 woeid-index:23424831 woeid-index:23424834 
woeid-index:23424836 woeid-index:23424839 woeid-index:23424841 woeid-index:23424884 
woeid-index:23424900 woeid-index:23424915 woeid-index:23424917 woeid-index:23424919 
woeid-index:23424924 woeid-index:23424935 woeid-index:23424951 woeid-index:23424979 
woeid-index:23424982'
"""	
		assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

/*
woeid-index:24865716 Latin America
woeid-index:23424747 Argentina
woeid-index:23424760 Belize
woeid-index:23424762 Bolivia
woeid-index:23424768 Brazil
woeid-index:23424782 Chile
woeid-index:23424787 Colombia
woeid-index:23424791 Costa Rica
woeid-index:23424793 Cuba
woeid-index:23424798 Dominica
woeid-index:23424800 Dominican Republic
woeid-index:23424801 Ecuador
woeid-index:23424807 El Salvador
woeid-index:23424811 French Guiana
woeid-index:23424831 Guadeloupe
woeid-index:23424834 Guatemala
woeid-index:23424836 Guyana
woeid-index:23424839 Haiti
woeid-index:23424841 Honduras
woeid-index:23424884 Martinique
woeid-index:23424900 Mexico
woeid-index:23424915 Nicaragua
woeid-index:23424917 Paraguay
woeid-index:23424919 Peru
woeid-index:23424924 Panama
woeid-index:23424935 Puerto Rico
woeid-index:23424951 St. Lucia
woeid-index:23424979 Uruguay
woeid-index:23424982 Venezuela
*/
	