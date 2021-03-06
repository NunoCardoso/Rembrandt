\
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
package renoir.eval.geoclef.pertopic.year2006

import renoir.eval.geoclef.pertopic.GeoCLEF_PerTopic_Test
import groovy.util.GroovyTestCase
import org.apache.log4j.Logger
import saskia.bin.Configuration
import renoir.obj.*
import rembrandt.gazetteers.CommonClassifications as SC
/**
 * @author Nuno Cardoso
 *
 */
 class GeoCLEF_EN_2006_028_Test extends GroovyTestCase {
        
    Configuration conf 
    GeoCLEF_PerTopic_Test pertopic
    static Logger log = Logger.getLogger("JUnitTest")

    public GeoCLEF_EN_2006_028_Test() {
		conf = Configuration.newInstance()
		log.info "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")	
		pertopic = new GeoCLEF_PerTopic_Test(conf)
    }
	
    void testTopic() {
		String lang = "en"
		String topic = "label:028 snowstorms in North America"

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)
     
		/** check sentences and question types */
		assert question.sentence*.text == ['snowstorms','in','North','America']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0


	// check detected NEs 

		assert question.nes.size() == 1
		assert question.nes[0].terms*.text == ['North','America']
    
		/// contains gives false. Use find
		assert question.nes[0].classification.find{it == SC.place_human_division}		

	// check subjects 
		assert !question.subject

	// check conditions 
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ['North','America']
		
	// SPARQL should resolve instances of the SUBJECT against the condition 
	// QueryGeoscope, using dbpedia-owl:location
	
	// Then, for the lone condition Subject, we resolve it and add it 			
		assert !question.expectedAnswerTypes
		
		// SPARQL query: SELECT DISTINCT ?s WHERE { { {?s skos:subject <http://dbpedia.org/resource/Category:Cities> } UNION { ?s skos:subject ?category . ?category skos:broader <http://dbpedia.org/resource/Category:Cities> } } { ?s dbpedia-owl:location <http://dbpedia.org/resource/Frankfurt_am_Main>}}

	// check answers 
		assert !question.answer
		
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)                                  
	
		println refq.toString()
		String reformulated_x = """
label:028 contents:snowstorms contents:"North America" woeid:24865672 woeid:23424736 woeid:23424737 woeid:23424751 woeid:23424754 woeid:23424756 woeid:23424758 woeid:23424760 woeid:23424775 woeid:23424783 woeid:23424791 woeid:23424793 woeid:23424798 woeid:23424800 woeid:23424807 woeid:23424826 woeid:23424828 woeid:23424831 woeid:23424834 woeid:23424839 woeid:23424841 woeid:23424858 woeid:23424884 woeid:23424888 woeid:23424900 woeid:23424914 woeid:23424915 woeid:23424924 woeid:23424935 woeid:23424939 woeid:23424940 woeid:23424951 woeid:23424958 woeid:23424962 woeid:23424977 woeid:23424981 woeid:23424983 woeid:23424985
"""		
	assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"") 
  } 
}
