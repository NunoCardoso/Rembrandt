
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
package renoir.eval.geoclef.pertopic.year2005

import groovy.util.GroovyTestCase
import rembrandt.gazetteers.CommonClassifications as SC
import renoir.obj.*
import renoir.eval.geoclef.pertopic.GeoCLEF_PerTopic_Test
import saskia.bin.Configuration
/**
 * @author Nuno Cardoso
 *
 */
class GeoCLEF_2005_EN_011_Test extends GroovyTestCase {

	Configuration conf
	GeoCLEF_PerTopic_Test pertopic

	public GeoCLEF_2005_EN_011_Test() {
		conf = Configuration.newInstance()
		//	conf.set("saskia.dbpedia.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
		//	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
		//	println "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")
		pertopic = new GeoCLEF_PerTopic_Test(conf)
	}

	void testTopic() {
		String lang = "en"
		String topic = "label:011 Roman cities in the UK and Germany"

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)

		/** check sentences and question types */
		assert question.sentence*.text == [
			'Roman',
			'cities',
			'in',
			'the',
			'UK',
			'and',
			'Germany'
		]
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0


		// check detected NEs
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ["UK"]
		assert question.nes[1].terms*.text == ["Germany"]

		/// contains gives false. Use find
		assert question.nes[0].classification.find{it == SC.place_human_country}
		assert question.nes[1].classification.find{it == SC.place_human_country}

		// check subjects

		assert question.subject.subjectTerms*.text == ["cities"]


		// check conditions
		assert question.conditions.size() == 2
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[1].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ["UK"]
		assert question.conditions[1].object.ne.terms*.text == ["Germany"]

		assert question.expectedAnswerTypes[0].DBpediaOntologyResources == ['Category:Cities']
		assert question.expectedAnswerTypes[0].resolvesTo == ExpectedAnswerType.Type.DBpediaOntologyResource

		// check answers
		assert !question.answer

		// now, question object full -> reformulated query
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)
		// reformulated Query -> string

		/** De notar que UK é referenciado para United_Kingdon no AskSaskia.classifyDBpedia(). 
		 usando o dbprop:redirect, e é graças a isso que temos o WOEID de United_Kingdom (23424975) */	
		println refq.toString()
		String reformulated_x = """
label:011 contents:Roman contents:"cities" contents:"UK" contents:"Germany" woeid:23424975 woeid:23424829
"""

		assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"")
	}
}
