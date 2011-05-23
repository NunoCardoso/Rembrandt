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

package rembrandt.rules

import org.apache.log4j.Logger
import rembrandt.obj.*
import rembrandt.rules.*
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.CommonClauses as CC
import saskia.db.obj.*
import saskia.db.table.*
import saskia.db.database.*
import saskia.gazetteers.Places
/**
 * @author Nuno Cardoso
 * 
 * This is a class used for detection pf subjects + places
 *
 */
class SubjectAndPlaceDetector extends Detector {

	List rules_pt, rules_en

	public SubjectAndPlaceDetector() {

		Clause placeAdjective1en = new Clause(name:"placeAdjective1",
				cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
				pattern:Places.getMeaningList("en", "adj"))

		Clause placeAdjective1pt = new Clause(name:"placeAdjective1",
				cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
				pattern:Places.getMeaningList("pt", "adj"))

		Clause placeName1en = new Clause(name:"placeName1",
				cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
				pattern:Places.getMeaningList("en", "name"))

		Clause placeName1pt = new Clause(name:"placeName1",
				cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
				pattern:Places.getMeaningList("pt", "name"))

		Clause subject1en = new Clause(name:"subject1en",
				cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
				pattern:SaskiaMainDB.newInstance().getDBTable("SubjectTable")
				.makeConceptList("en"))

		subject1en.options = ["case_insensitive":true]

		Clause subject1pt = new Clause(name:"subject1pt",
				cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
				pattern:SaskiaMainDB.newInstance().getDBTable("SubjectTable")
				.makeConceptList("pt"))

		subject1pt.options = ["case_insensitive":true]


		rules_pt = [
			new Rule(id:'Sbj_AdjPlc', description:'músicos portugueses',
			//a notPlaceAdjective is a non-greedy "anything except a place adjective"
			clauses:[
				CC.beginSentence1,
				subject1pt,
				placeAdjective1pt
			],
			action:[
				{Expando q, MatcherObject o ->
					q.placeAdjectiveTerms = o.getMatchedTermsByClause(placeAdjective1pt)
					q.placeAdjectiveMatch = o.getMatchByClause(placeAdjective1pt)
					q.subjectMatch = o.getMatchByClause(subject1pt)
					q.subjectTerms =  q.subjectMatch.terms
					q.matchedRuleID = 'Sbj_AdjPlc'
				}
			]) ,
			new Rule(id:'Sbj_de_PlcNam', description:'Músicos de Portugal',
			clauses:[
				CC.beginSentence1,
				subject1pt,
				ClausesPT.dnaeosem1c,
				placeName1pt
			],
			action:[
				{Expando q, MatcherObject o ->
					q.subjectTerms = o.getMatchedTermsByClause(subject1pt)
					q.placeNameMatch = o.getMatchByClause(placeName1pt)
					q.placeNameTerms = q.placeNameMatch.terms
					q.subjectMatch = o.getMatchByClause(subject1pt)
					q.subjectTerms =  q.subjectMatch.terms
					q.matchedRuleID = 'Sbj_de_PlcNam'
				}
			]) ,
			new Rule(id:'Sbj', description:'Músicos',
			clauses:[
				CC.beginSentence1,
				subject1pt,
				CC.endSentence1
			],
			action:[
				{Expando q, MatcherObject o ->
					q.subjectMatch = o.getMatchByClause(subject1pt)
					q.subjectTerms = q.subjectMatch.terms
					q.matchedRuleID = 'Sbj'
				}
			])
		]

		rules_en = [
			new Rule(id:'Adj_Sbj', description:'Portuguese musicians',
			clauses:[
				CC.beginSentence1,
				placeAdjective1en,
				subject1en
			],
			action:[
				{Expando q, MatcherObject o ->
					q.placeAdjectiveTerms = o.getMatchedTermsByClause(placeAdjective1en)
					q.placeAdjectiveMatch = o.getMatchByClause(placeAdjective1en)
					q.subjectMatch = o.getMatchByClause(subject1en)
					q.subjectTerms =  q.subjectMatch.terms
					q.matchedRuleID = 'Adj_Sbj'
				}
			]) ,
			new Rule(id:'Sbj_of_Adj', description:'Musicians of Portugal',
			clauses:[
				CC.beginSentence1,
				subject1en,
				ClausesEN.of1c,
				ClausesEN.the01c,
				placeName1en
			],
			action:[
				{Expando q, MatcherObject o ->
					q.placeNameMatch = o.getMatchByClause(placeName1en)
					q.placeNameTerms = q.placeNameMatch.terms
					q.subjectMatch = o.getMatchByClause(subject1en)
					q.subjectTerms =  q.subjectMatch.terms
					q.matchedRuleID = 'Sbj_of_Adj'
				}
			]) ,
			new Rule(id:'Sbj', description:'Musicians',
			clauses:[
				CC.beginSentence1,
				subject1en,
				CC.endSentence1
			],
			action:[
				{Expando q, MatcherObject o ->
					q.subjectMatch = o.getMatchByClause(subject1en)
					q.subjectTerms = q.subjectMatch.terms
					q.matchedRuleID = 'Sbj'
				}
			])
		]
	}

	/**
	 * This method detects, collects and returns a list of meanings.
	 * It browses the sentence, and returns first match 
	 */
	Expando process(Sentence s, String lang) {

		List rules
		if (lang == "pt") rules = rules_pt
		else if (lang == "en") rules = rules_en

		for (rule in rules) {

			// be careful, if the sentence pointer is not reset, rules that advance the pointer
			// will make the pointer forward for the next ones!
			s.resetPointerToFirstVisibleTerm() // reset the pointer to the first term
			// sentence pointer will not advance. It should be 0, but make sure of it

			// first match, return, else browse the sentence
			// you have to, for patterns like XXXX of Portugal
			// And mind that there is a catch-all rule that can be matched earlier, but that's not we want
			while (s.thereAreVisibleTermsAhead()) {

				def generatedObject
				// println "rule: $rule sentence: $s lang:$lang"
				MatcherObject matchObject = matchRule( new MatcherObject(rule:rule, sentence:s, lang:lang))
				// println "MatchedObject? ${matchObject}"
				if (matchObject) generatedObject = performActionsOnMatcherObject(matchObject)
				// return first match
				if (generatedObject) return generatedObject
				// if not a metch, browse sentence
				s.movePointerForVisibleTerms()
			}
		}
		return null
	}

	public Expando performActionsOnMatcherObject(MatcherObject o, obj = null) {
		Expando q = new Expando()
		def actions = o.rule.action
		if (actions instanceof Closure) {
			actions(q, o)
			return q
		}
		else if (actions instanceof List) {

			o.rule.action.each{a -> if (a instanceof Closure) a(q, o) }
			return q
		} else {
			log.error "Tried to apply a rule action, but no Closure found."
			log.error "Rule class action = ${this.rule.action.class}."
			throw new IllegalStateException()
		}
		return null
	}

}
