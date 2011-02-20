
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
package renoir.test.geoclef.pertopic.year2006

import renoir.test.geoclef.pertopic.GeoCLEF_PerTopic_Test
import groovy.util.GroovyTestCase
import org.apache.log4j.Logger
import saskia.bin.Configuration
import renoir.obj.*
import rembrandt.gazetteers.CommonClassifications as SC
/**
 * @author Nuno Cardoso
 *
 */
 class GeoCLEF_EN_2006_050_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2006_050_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:050 Cities along the Danube and the Rhine"


		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['Cities','along','the','Danube','and','the','Rhine']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0

	// check detected NEs 

		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ['Danube']
		// DBpedia classifies it as place... yet.
		assert question.nes[0].classification.find{it == SC.place}//_physical_watercourse}
		assert question.nes[1].terms*.text == ['Rhine']
		assert question.nes[1].classification.find{it == SC.place_physical_watercourse}

	// check subjects 
	// games is grounded
		assert question.subject.subjectTerms*.text == ['Cities']

	// check conditions 
		assert question.conditions.size() == 2
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].operator.op == Operator.Locator.Along
		assert question.conditions[0].object.ne.terms*.text == ['Danube']
		assert question.conditions[1].object instanceof QueryGeoscope
		assert question.conditions[1].operator.op == Operator.Locator.Along
		assert question.conditions[1].object.ne.terms*.text == ['Rhine']
			
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert question.expectedAnswerTypes[0].DBpediaOntologyResources == ['Category:Cities']

	// check answers 
		assert !question.answer 
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
/*
SPARQL query: SELECT DISTINCT ?s WHERE { { {?s skos:subject <http://dbpedia.org/resource/Category:Cities> } UNION { ?s skos:subject ?category . ?category skos:broader <http://dbpedia.org/resource/Category:Cities> } } { ?s dbpedia-owl:location <http://dbpedia.org/resource/Danube>} UNION { ?s dbpedia-owl:location <http://dbpedia.org/resource/Danube_Delta>}}
Tou
c grounded
SPARQL query: SELECT DISTINCT ?s WHERE { { {?s skos:subject <http://dbpedia.org/resource/Category:Cities> } UNION { ?s skos:subject ?category . ?category skos:broader <http://dbpedia.org/resource/Category:Cities> } } { ?s dbpedia-owl:location <http://dbpedia.org/resource/Rhine>}}

*/	
		println refq.toString()
		String reformulated_x = """
label:050 contents:"Cities" contents:along contents:"Danube" contents:"Rhine" woeid:26354151 woeid:2635412
"""
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}

