
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
import rembrandt.bin.*
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.tokenizer.*
import renoir.obj.*
import renoir.rules.*
import renoir.eval.geoclef.pertopic.GeoCLEF_PerTopic_Test
import saskia.bin.Configuration
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
		assert question.sentence*.text == [
			'Amnesty',
			'International',
			'human',
			'rights',
			'reports',
			'in',
			'Latin',
			'America'
		]
		assert question.questionType == QuestionType.None
		assert question.questionTypeTerms.size() == 0


		// check detected NEs
		assert question.nes.size() == 2
		//println "question nes wikipedia stuff & dbpedia stuff: "+question.nes[0].wikipediaPage+ " "+question.nes[0].dbpediaPage
		assert question.nes[0].terms*.text == ["Amnesty", "International"]
		assert question.nes[1].terms*.text == ["Latin", "America"]

		/// contains gives false. Use find
		assert question.nes[0].classification.find{it == SC.organization_institution}
		assert question.nes[1].classification.find{it == SC.place_human_division}

		// check subjects

		assert !question.subject

		// check conditions
		assert question.conditions.size() == 1
		assert question.conditions[0].object instanceof QueryGeoscope
		assert question.conditions[0].object.ne.terms*.text == ["Latin", "America"]

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
woeid:24865716 woeid:23424747 woeid:23424760 woeid:23424762 
woeid:23424768 woeid:23424782 woeid:23424787 woeid:23424791 
woeid:23424793 woeid:23424798 woeid:23424800 woeid:23424801 
woeid:23424807 woeid:23424811 woeid:23424831 woeid:23424834 
woeid:23424836 woeid:23424839 woeid:23424841 woeid:23424884 
woeid:23424900 woeid:23424915 woeid:23424917 woeid:23424919 
woeid:23424924 woeid:23424935 woeid:23424951 woeid:23424979 
woeid:23424982'
"""
		assert reformulated_x.replaceAll(/(?m)[\s\n]/,"") == refq.toString().replaceAll(/(?m)[\s\n]/,"")
	}
}

/*
 woeid:24865716 Latin America
 woeid:23424747 Argentina
 woeid:23424760 Belize
 woeid:23424762 Bolivia
 woeid:23424768 Brazil
 woeid:23424782 Chile
 woeid:23424787 Colombia
 woeid:23424791 Costa Rica
 woeid:23424793 Cuba
 woeid:23424798 Dominica
 woeid:23424800 Dominican Republic
 woeid:23424801 Ecuador
 woeid:23424807 El Salvador
 woeid:23424811 French Guiana
 woeid:23424831 Guadeloupe
 woeid:23424834 Guatemala
 woeid:23424836 Guyana
 woeid:23424839 Haiti
 woeid:23424841 Honduras
 woeid:23424884 Martinique
 woeid:23424900 Mexico
 woeid:23424915 Nicaragua
 woeid:23424917 Paraguay
 woeid:23424919 Peru
 woeid:23424924 Panama
 woeid:23424935 Puerto Rico
 woeid:23424951 St. Lucia
 woeid:23424979 Uruguay
 woeid:23424982 Venezuela
 */
