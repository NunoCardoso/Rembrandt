
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
class GeoCLEF_2005_EN_009_Test extends GroovyTestCase {

	Configuration conf
	GeoCLEF_PerTopic_Test pertopic

	public GeoCLEF_2005_EN_009_Test() {
		conf = Configuration.newInstance()
		//	conf.set("saskia.dbpedia.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
		//	conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
		//	println "DBpedia sparql service set to "+conf.get("saskia.dbpedia.url","none")
		pertopic = new GeoCLEF_PerTopic_Test(conf)
	}

	void testTopic() {
		String lang = "en"
		String topic = "label:009 child labor in Asia"

		RenoirQuery rq = RenoirQueryParser.parse(topic)
		Question question = pertopic.process(rq, lang)
		//dumpQuestion(question)

		/** check sentences and question types */
		assert question.sentence*.text == [
			'child',
			'labor',
			'in',
			'Asia']
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0


		// check detected NEs
		assert question.nes.size() == 1
		//	assert question.nes[0].terms*.text == ["Milk"]
		assert question.nes[0].terms*.text == ["Asia"]

		/// contains gives false. Use find
		//	assert question.nes[0].classification.find{it == SC.thing_object}
		assert question.nes[0].classification.find{it == SC.place_human_division}

		// check subjects

		assert !question.subject

		// check conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ["Asia"]

		assert !question.expectedAnswerTypes

		// check answers
		assert !question.answer

		// now, question object full -> reformulated query
		ReformulatedQuery refq = QueryReformulator2.reformulate(rq, question)
		// reformulated Query -> string

		println refq.toString()
		String reformulated_x = """
label:009 contents:child contents:labor contents:"Asia" 
woeid:24865671 woeid:20070017 woeid:23424738 woeid:23424739 woeid:23424741 
woeid:23424743 woeid:23424753 woeid:23424759 woeid:23424763 woeid:23424770 
woeid:23424773 woeid:23424776 woeid:23424778 woeid:23424781 woeid:23424784 
woeid:23424823 woeid:23424846 woeid:23424848 woeid:23424849 woeid:23424851 
woeid:23424852 woeid:23424855 woeid:23424856 woeid:23424860 woeid:23424864 
woeid:23424865 woeid:23424868 woeid:23424869 woeid:23424870 woeid:23424871 
woeid:23424872 woeid:23424873 woeid:23424887 woeid:23424898 woeid:23424899 
woeid:23424901 woeid:23424911 woeid:23424921 woeid:23424922 woeid:23424928 
woeid:23424930 woeid:23424934 woeid:23424936 woeid:23424938 woeid:23424948 
woeid:23424956 woeid:23424960 woeid:23424961 woeid:23424968 woeid:23424969 
woeid:23424971 woeid:23424972 woeid:23424980 woeid:23424984 woeid:23424997 
woeid:23424998 woeid:23425002 woeid:24865698 woeid:28289408
"""

		assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"")
	}
}
