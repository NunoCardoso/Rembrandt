
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

import groovy.util.GroovyTestCase
import rembrandt.gazetteers.CommonClassifications as SC
import renoir.obj.*
import renoir.test.geoclef.pertopic.GeoCLEF_PerTopic_Test
import saskia.bin.Configuration
/**
 * @author Nuno Cardoso
 *
 */
class GeoCLEF_2005_EN_019_Test extends GroovyTestCase {

	Configuration conf
	GeoCLEF_PerTopic_Test pertopic

	public GeoCLEF_2005_EN_019_Test() {
		conf = Configuration.newInstance()
		//	conf.set("saskia.dbpedia.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
		//	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
		//	println "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")
		pertopic = new GeoCLEF_PerTopic_Test(conf)
	}

	void testTopic() {
		String lang = "en"
		String topic = "label:019 Golf tournaments in Europe"

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)

		/** check sentences and question types */
		assert question.sentence*.text == [
			'Golf',
			'tournaments',
			'in',
			'Europe'
		]
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0


		// check detected NEs
		assert question.nes.size() == 2
		assert question.nes[0].terms*.text == ["Golf"]
		assert question.nes[1].terms*.text == ["Europe"]

		/// contains gives false. Use find
		assert question.nes[0].classification.find{it == SC.event_organized}
		assert question.nes[1].classification.find{it == SC.place_human_division}

		// check subjects
		assert !question.subject

		// check conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ["Europe"]

		assert !question.expectedAnswerTypes

		// check answers
		assert !question.answer

		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)

		println refq.toString()
		String reformulated_x = """
label:019 contents:"Golf" contents:tournaments contents:"Europe" entity:Golf 
woeid:24865675 woeid:12577865 woeid:20069817 
woeid:20069818 woeid:23424742 woeid:23424744 woeid:23424750 woeid:23424757 
woeid:23424761 woeid:23424765 woeid:23424771 woeid:23424796 woeid:23424803 
woeid:23424805 woeid:23424810 woeid:23424812 woeid:23424816 woeid:23424819 
woeid:23424825 woeid:23424829 woeid:23424833 woeid:23424843 woeid:23424844 
woeid:23424845 woeid:23424853 woeid:23424874 woeid:23424875 woeid:23424877 
woeid:23424879 woeid:23424881 woeid:23424885 woeid:23424890 woeid:23424892 
woeid:23424897 woeid:23424909 woeid:23424910 woeid:23424923 woeid:23424925 
woeid:23424933 woeid:23424945 woeid:23424947 woeid:23424950 woeid:23424954 
woeid:23424957 woeid:23424975 woeid:23424976 woeid:23424986 woeid:26812346 
woeid:28289413
"""
		assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"")
	}
}
