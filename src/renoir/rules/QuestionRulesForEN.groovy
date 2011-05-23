package renoir.rules

import static rembrandt.obj.BoundaryCriteria.*

import org.apache.log4j.*

import rembrandt.gazetteers.CommonClauses as CC
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.en.*
import rembrandt.obj.*
import rembrandt.rules.*
import renoir.obj.*
import saskia.bin.*
import saskia.db.database.SaskiaMainDB
import saskia.dbpedia.*
import saskia.gazetteers.*

class QuestionRulesForEN extends QuestionRules {

	static Logger log = Logger.getLogger("RenoirQuestionSolver")

	static Clause how1 =  Clause.newRegex1Clause(~/[Hh]ow/)
	static Clause manymuch1 =  Clause.newRegex1Clause(~/[Mm](?:any|uch)/)
	static Clause which1 =  Clause.newRegex1Clause(~/[Ww]hich/)
	static Clause ion01 =  Clause.newRegex01Clause(~/[IiOo]n/)
	static Clause what1 =  Clause.newRegex1Clause(~/[Ww]hat/)
	static Clause who1 = Clause.newRegex1Clause(~/[Ww]ho/)
	static Clause where1 = Clause.newRegex1Clause(~/[Ww]here/)
	static Clause why1 = Clause.newRegex1Clause(~/[Ww]hy/)
	static Clause when1 = Clause.newRegex1Clause(~/[Ww]hen/)

	static Clause placeAdjective1en = new Clause(name:"placeAdjective1",
	cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
	pattern:Places.getMeaningList("en", "adj"))

	static Clause placeName1en = new Clause(name:"placeName1",
	cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
	pattern:Places.getMeaningList("en", "name"))

	static Clause subject1en = new Clause(name:"subject1en",
	cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
	pattern:SaskiaMainDB.newInstance().getDBTable("SubjectTable")
	.makeConceptList("en"), options:["case_insensitive":true])

	/******* #1: RULES FOR QUESTION TYPE *********/

	static List rulesToDetectQuestionType = [
		// 1.1
		new Rule(id:'howmanymuch1', description:'How m(any|uch)',
		clauses:[
			CC.beginSentence1,
			how1,
			manymuch1
		],
		action: [
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.HowMuch
				q.questionTypeTerms = o.getMatchedTermsByClauses([how1, manymuch1])
				log.debug "${o.rule.id} matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.2: in which, on which, which
		new Rule(id:'which1', description:'([io]n?) Which',
		clauses:[
			CC.beginSentence1,
			ion01,
			which1
		],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.Which
				q.questionTypeTerms = o.getMatchedTermsByClauses([ion01, which1])
				log.debug "${o.rule.id} matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.3
		new Rule(id:'who1', description:'Who',
		clauses:[CC.beginSentence1, who1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.Who
				q.questionTypeTerms = o.getMatchedTermsByClause(who1)
				log.debug "${o.rule.id} matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.4
		new Rule(id:'where1', description:'Where',
		clauses:[CC.beginSentence1, where1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.Where
				q.questionTypeTerms = o.getMatchedTermsByClause(where1)
				log.debug "${o.rule.id} matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.5
		new Rule(id:'how1', description:'How',
		clauses:[CC.beginSentence1, how1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.How
				q.questionTypeTerms = o.getMatchedTermsByClause(how1)
				log.debug "${o.rule.id} matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.6 overwrites previous "Que1" rule
		new Rule(id:'what1', description:'What',
		clauses:[CC.beginSentence1, what1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.What
				q.questionTypeTerms = o.getMatchedTermsByClause(what1)
				log.debug "${o.rule.id} matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.7
		new Rule(id:'when1', description:'When',
		clauses:[CC.beginSentence1, when1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.When
				q.questionTypeTerms = o.getMatchedTermsByClause(when1)
				log.debug "${o.rule.id} matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.8
		new Rule(id:'why1', description:'Why',
		clauses:[CC.beginSentence1, why1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.Why
				q.questionTypeTerms = o.getMatchedTermsByClause(why1)
				log.debug "${o.rule.id} matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		)
	]


	/******* #2: SUBJECT RULE *********/

	// 2.1
	static List rulesToCaptureSubjects = [
		new Rule(id:'rulesToCaptureSubjects 2.1', description:'Portuguese musicians',
		clauses:[
			placeAdjective1en,
			subject1en
		],
		action:[
			{MatcherObject o, Question q ->

				// they have the ID of the Subject and Geoscope in the DB
				// Note: Match only once; ex: "portuguese musicians" performed in "portuguese cities".
				// assume questions in active mode. I'll get the second one later, in conditions.
				// How do I distinguish them? Test the "in" or "near" prepositions before it
				List<Term> terms = o.getMatchedTermsByClauses([
					placeAdjective1en,
					subject1en
				])
				List<Term> beforeterms = o.getMatchedAndUnmatchedTermsBeforeMatchedClause(placeAdjective1en)
				log.debug "${o.rule.id}: matched terms $terms"

				boolean issubject = true
				// let's try to prove it's not
				if (beforeterms) {
					if (beforeterms[(beforeterms.size()-1)].text == "in" ||
					beforeterms[(beforeterms.size()-1)].text == "near" ||
					(beforeterms.size()-2 >= 0 && beforeterms[(beforeterms.size()-1)].text == "around") )
						issubject = false
				}

				if (!q.subject) {
					// note how Question is an Expando.
					if (issubject) {
						q.subjectMatch = o.getMatchByClause(subject1en)
						q.placeMatch = o.getMatchByClause(placeAdjective1en)
						q.subject = new Subject()
						q.subject.classifySubject("en", q.subjectMatch, q.placeMatch)
						log.debug "${o.rule.id}: added subject = ${q.subject}"
					} else {
						// let the condition rules catch it
						log.debug "${o.rule.id}: looks like ${terms} may be a contition, so I skipped"
					}
				}else {
					log.debug "${o.rule.id}: since there's already a subject ${q.subject}, it's skipped"
				}
			}
		]),
		// 2.2
		new Rule(id:'rulesToCaptureSubjects 2.2', description:'Musicians [of|in] Portugal',
		clauses:[
			subject1en,
			ClausesEN.inof1nc,
			placeName1en
		],
		action:[
			{MatcherObject o, Question q ->

				List<Term> terms = o.getMatchedTermsByClauses([
					subject1en,
					ClausesEN.inof1nc,
					placeName1en
				])
				List<Term> beforeterms = o.getMatchedAndUnmatchedTermsBeforeMatchedClause(subject1en)
				log.debug "${o.rule.id}: matched terms $terms"
				log.debug "${o.rule.id}: matched beforeterms $beforeterms"
				boolean issubject = true
				// let's try to prove it's not
				if (beforeterms) {
					if (beforeterms[(beforeterms.size()-1)].text == "in" ||
					beforeterms[(beforeterms.size()-1)].text == "near")
						issubject = false
				}


				println "issubject: $issubject"
				if (!q.subject) {
					if (issubject) {
						// note how Question is an Expando.
						q.subjectMatch = o.getMatchByClause(subject1en)
						q.placeMatch = o.getMatchByClause(placeName1en)
						q.subject = new Subject()
						q.subject.classifySubject("en", q.subjectMatch, q.placeMatch)
						log.debug "${o.rule.id}: added subject = ${q.subject}"
					} else {
						// let the condition rules catch it
						log.debug "${o.rule.id}: looks like ${terms} may be a contition, so I skipped"
					}
				}else {
					log.debug "${o.rule.id}: since there's already a subject ${q.subject}, it's skipped"
				}
			}
		]),
		// 2.3
		new Rule(id:'rulesToCaptureSubjects 2.3', description:'Musicians',
		clauses:[subject1en],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTermsByClause(subject1en)
				List<Term> beforeterms = o.getMatchedAndUnmatchedTermsBeforeMatchedClause(subject1en)
				log.debug "${o.rule.id}: matched terms $terms"
				boolean issubject = true
				if (beforeterms) {
					if (beforeterms[(beforeterms.size()-1)].text == "in" ||
					beforeterms[(beforeterms.size()-1)].text == "near")
						issubject = false
				}



				if (!q.subject) {
					if (issubject) {
						q.subjectMatch = o.getMatchByClause(subject1en)

						// problem: there is a subject, 'states', that is matched when there is a
						// 'United States'.
						// Solution: Let's check if matched terms belong or not to an NE
						NamedEntity matched_ne = null

						// let's make a fake NE out of subject terms, easier to compare positions
						NamedEntity subject_ne = new NamedEntity(terms:q.subjectMatch.terms,
								sentenceIndex:0, termIndex:q.subjectMatch.terms[0].index)

						q.nes.each{ne ->
							if (ne.matchesBoundaries(subject_ne, ExactOrContains))
								matched_ne = ne
						}

						if (!matched_ne) {
							q.subject = new Subject()
							q.subject.classifySubject("en", q.subjectMatch, null)
							log.debug "${o.rule.id}: added subject = ${q.subject}"
						} else {
							log.debug "${o.rule.id}: looks like ${terms} are within a NE ${matched_ne}, so I skipped"
						}
					} else {
						// let the condition rules catch it
						log.debug "${o.rule.id}: looks like ${terms} may be a contition, so I skipped"
					}
				}else {
					log.debug "${o.rule.id}: since there's already a subject ${q.subject}, it's skipped"
				}
			}
		])
	]

	/**************************************************************/
	/****** #3: get conditions (predicates + operators + NEs) *****/
	/**************************************************************/	

	static List rulesToCaptureConditions = [
		/****** 3.1 GET GEOSCOPES *****/	

		// NOTE: This rules *may steal* the subject, if there's a 'in {subject}' or 'near {subject}'.
		// the [in] and [near] are mandatory to steal them.

		// "in {place}" will capture also "in {place} and in {place}"
		// don't use endSentence, for tuff like "Musicians born in Portugal and in XI century"
		// of course, this one might be confused with captured subjects!

		/*** 3.1.1 IN {GEOSCOPE} ***/
		new Rule(id:'rulesToCaptureConditions 3.1.1',
		description:'[in|to]! the? {place}! and? in? the? {place?}',
		clauses:[
			ClausesEN.into1c,
			ClausesEN.the01c,
			NEGazetteer.NE_LOCAL_1c,
			ClausesEN.and01c,
			ClausesEN.in01c,
			ClausesEN.the01c,
			NEGazetteer.NE_LOCAL_01c
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched terms "+terms

				List<NamedEntity> nes = o.getMatchedNEs()
				// let's walk the nes, chek if they weren't already captured in subjects or past conditions
				List nes2 = nes.clone()
				//println "nes2: $nes2 q.subject = "+q.subject
				nes2.each{ne ->
					if (q.subject && ne.equalsTerms(q.subject.geoscopeTerms)) {
						log.debug "${o.rule.id}: Captured NE $ne is already on subject ${q.subject}. Removing."
						nes.remove(ne)
					}
					// previous conditions may contain a bigger pattern, as in "rivers in Europe" vs "in Europe"
					// and since the first one is a
					q.conditions?.each{c ->
						if (c.object) {
							def c_ne = (c.object instanceof NamedEntity ? c.object :
									(c.object instanceof QueryGeoscope ? c.object?.ne : null) )
							if (ne == c_ne) {
								log.debug "${o.rule.id}: Captured NE $ne is already on condition $c. Removing."
								nes.remove(ne)
							}
						}
					}
				}//nes2.each ne->

				// with the remaining NEs , build new conditions
				nes?.each{ne ->
					Condition c = new Condition(terms:terms)
					c.object = new QueryGeoscope(ne:ne)
					c.operator = new Operator(op:Operator.Locator.In)
					q.conditions << c
					log.debug "Adding new condition, $c."
				}
			}
		]),
		/*** 3.1.2 IN {PlaceAdjective}{Subject} (ex: in Portuguese islands) ***/
		new Rule(id:'rulesToCaptureConditions 3.1.2',
		description:'[in|to]! the? {adjectivePlaces}! {subject}!',
		clauses:[
			ClausesEN.into1c,
			ClausesEN.the01c,
			placeAdjective1en,
			subject1en
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched "+terms

				// note how Question is an Expando.
				q.subjectMatch = o.getMatchByClause(subject1en)
				q.placeMatch = o.getMatchByClause(placeAdjective1en)
				Subject subject = new Subject()
				Condition c = new Condition(terms:terms)

				// note: if this is the q.SUBJECT, like in 'wildgooses in Portuguese islands',
				// then it is misplaced, it should be a condition. let's move it.
				if (q.subject && q.subject.subjectTerms == q.subjectMatch.terms &&
				q.subject.geoscopeTerms == q.placeMatch.terms) {
					c.object = q.subject
					q.conditions << c
					q.subject = null
					log.debug "${o.rule.id}: *moved* Subject to a condition object: $c"

				} else {

					int biggerThan = -1
					int smallerThan = -1
					q.conditions.eachWithIndex{c2, i ->
						if (c.containedByCondition(c2)) smallerThan = i
						if (c.containsCondition(c2)) biggerThan = i
					}
					println "biggerThan:$biggerThan smaller:$smallerThan"

					if (smallerThan > -1) {
						log.debug "${o.rule.id}: Condition $c is smaller than existing condition ${q.conditions[smallerThan]}, so I skipped."
						// let's just check if there is a bigger one. If so, skip this addition
					} else {
						if (biggerThan > -1) {
							// ok, it's a good condition
							log.debug "${o.rule.id}: Condition $c is bigger than existing condition ${q.conditions[biggerThan]}, I will overwrite it."
							subject.classifySubject("en", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions[biggerThan] = c
						} else {
							subject.classifySubject("en", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions << c
							log.debug "${o.rule.id}: No conflicting conditions, adding Subject as a condition object: $c"
						}
					}
				}
			}
		]),
		/*** 3.1.3 IN {Subject} of {PlaceName}( ex: in islands of the? Portugal) ***/
		new Rule(id:'rulesToCaptureConditions 3.1.3',
		description:'[in|to]! the? {subject}! [in|of]! the? {NamePlaces}!',
		clauses:[
			ClausesEN.into1c,
			ClausesEN.the01c,
			subject1en,
			ClausesEN.inof1nc,
			ClausesEN.the01nc,
			placeName1en
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched "+terms

				// note how Question is an Expando.
				q.subjectMatch = o.getMatchByClause(subject1en)
				q.placeMatch = o.getMatchByClause(placeName1en)
				Subject subject = new Subject()
				Condition c = new Condition(terms:terms)

				// note: if this is the q.SUBJECT, like in 'wildgooses in Portuguese islands',
				// then it is misplaced, it should be a condition. let's move it.
				if (q.subject && q.subject.subjectTerms == q.subjectMatch.terms &&
				q.subject.geoscopeTerms == q.placeMatch.terms) {
					c.object = q.subject
					q.conditions << c
					q.subject = null
					log.debug "${o.rule.id}: *moved* Subject to a condition object: $c"
				} else {
					int biggerThan = -1
					int smallerThan = -1
					q.conditions.eachWithIndex{c2, i ->
						if (c.containedByCondition(c2)) smallerThan = i
						if (c.containsCondition(c2)) biggerThan = i
					}

					if (smallerThan > -1) {
						log.debug "${o.rule.id}: Condition $c is smaller than existing condition ${q.conditions[smallerThan]}, so I skipped."
						// let's just check if there is a bigger one. If so, skip this addition
					} else {
						if (biggerThan > -1) {
							// ok, it's a good condition
							log.debug "${o.rule.id}: Condition $c is bigger than existing condition ${q.conditions[biggerThan]}, I will overwrite it."
							subject.classifySubject("en", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions[biggerThan] = c
						} else {
							subject.classifySubject("en", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions << c
							log.debug "${o.rule.id}: No conflicting conditions, adding Subject as a condition object: $c"
						}
					}

				}
			}
		]),
		/*** 3.1.4 NEAR {GEOSCOPE} ***/
		new Rule(id:'rulesToCaptureConditions 3.1.4',
		description:'[near|around|along|between]! the? {place}! and? in? the? {place?}',
		clauses:[
			ClausesEN.neararoundalongbetween1c,
			ClausesEN.the01c,
			NEGazetteer.NE_LOCAL_1c,
			ClausesEN.and01c,
			ClausesEN.the01nc,
			NEGazetteer.NE_LOCAL_01c
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched terms "+terms
				List<NamedEntity> nes = o.getMatchedNEs()
				// let's walk the nes, chek if they weren't already captured in subjects or past conditions
				List nes2 = nes.clone()
				nes2.each{ne ->
					if (q.subject && ne.equalsTerms(q.subject.geoscopeTerms)) {
						log.debug "${o.rule.id}: Captured NE $ne is already on subject ${q.subject}. Removing."
						nes.remove(ne)
					}
					// conditions may contain an object or a QueryGeoscope
					q.conditions?.each{c ->
						if (c.object) {
							def c_ne = (c.object instanceof NamedEntity ? c.object :
									(c.object instanceof QueryGeoscope ? c.object?.ne : null) )
							if (ne == c_ne) {
								log.debug "${o.rule.id}: Captured NE $ne is already on condition $c. Removing."
								nes.remove(ne)
							}
						}
					}
				}//nes2.each ne->

				List<Term> naa = o.getMatchedTermsByClause(ClausesEN.neararoundalongbetween1c)

				// with the remaining NEs , build new conditions
				nes?.each{ne ->
					Condition c = new Condition(terms:terms)
					c.object = new QueryGeoscope(ne:ne)

					if (naa*.text == ["along"])
						c.operator = new Operator(op:Operator.Locator.Along)
					else if (naa*.text == ["around"])
						c.operator = new Operator(op:Operator.Locator.Around)
					else if (naa*.text == ["between"])
						c.operator = new Operator(op:Operator.Locator.Between)
					else
						c.operator = new Operator(op:Operator.Locator.Near)

					q.conditions << c
					log.debug "Adding new condition, $c."
				}
			}
		]),
		/*** 3.1.5 NEAR {PlaceAdjective}{Subject} (ex: near Portuguese islands) ***/
		new Rule(id:'rulesToCaptureConditions 3.1.5',
		description:'[near|around|along|between]! the? {adjectivePlaces}! {subject}!',
		clauses:[
			ClausesEN.neararoundalongbetween1c,
			ClausesEN.the01c,
			placeAdjective1en,
			subject1en
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched "+terms

				// note how Question is an Expando.
				q.subjectMatch = o.getMatchByClause(subject1en)
				q.placeMatch = o.getMatchByClause(placeAdjective1en)
				Subject subject = new Subject()
				Condition c = new Condition(terms:terms)
				if (q.subject && q.subject.subjectTerms == q.subjectMatch.terms &&
				q.subject.geoscopeTerms == q.placeMatch.terms) {

					c.object = q.subject
					q.conditions << c
					q.subject = null
					log.debug "${o.rule.id}: *moved* Subject to a condition object: $c"
				} else {

					int biggerThan = -1
					int smallerThan = -1
					q.conditions.eachWithIndex{c2, i ->
						if (c.containedByCondition(c2)) smallerThan = i
						if (c.containsCondition(c2)) biggerThan = i
					}


					if (smallerThan > -1) {
						log.debug "${o.rule.id}: Condition $c is smaller than existing condition ${q.conditions[smallerThan]}, so I skipped."
						// let's just check if there is a bigger one. If so, skip this addition
					} else {

						List<Term> naa = o.getMatchedTermsByClause(ClausesEN.neararoundalongbetween1c)
						if (naa*.text == ["along"])
							c.operator = new Operator(op:Operator.Locator.Along)
						else if (naa*.text == ["around"])
							c.operator = new Operator(op:Operator.Locator.Around)
						else
							c.operator = new Operator(op:Operator.Locator.Near)
						if (biggerThan > -1) {
							// ok, it's a good condition

							log.debug "${o.rule.id}: Condition $c is bigger than existing condition ${q.conditions[biggerThan]}, I will overwrite it."

							subject.classifySubject("en", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions[biggerThan] = c
						} else {
							subject.classifySubject("en", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions << c
							log.debug "${o.rule.id}: No conflicting conditions, adding Subject as a condition object: $c"
						}
					}
				}
			}
		]),
		/*** 3.1.6 IN {Subject} of {PlaceName}( ex: in islands of Portugal) ***/
		new Rule(id:'rulesToCaptureConditions 3.1.6',
		description:'[near|around|along|between]! the? {subject}! [in|of]! {NamePlaces}!',
		clauses:[
			ClausesEN.neararoundalongbetween1c,
			ClausesEN.the01c,
			subject1en,
			ClausesEN.inof1nc,
			placeName1en
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched "+terms

				// note how Question is an Expando.
				q.subjectMatch = o.getMatchByClause(subject1en)
				q.placeMatch = o.getMatchByClause(placeName1en)
				Subject subject = new Subject()
				Condition c = new Condition(terms:terms)
				if (q.subject && q.subject.subjectTerms == q.subjectMatch.terms &&
				q.subject.geoscopeTerms == q.placeMatch.terms) {
					c.object = q.subject
					q.conditions << c
					q.subject = null
					log.debug "${o.rule.id}: *moved* Subject to a condition object: $c"
				} else {

					int biggerThan = -1
					int smallerThan = -1
					q.conditions.eachWithIndex{c2, i ->
						if (c.containedByCondition(c2)) smallerThan = i
						if (c.containsCondition(c2)) biggerThan = i
					}


					if (smallerThan > -1) {
						log.debug "${o.rule.id}: Condition $c is smaller than existing condition ${q.conditions[smallerThan]}, so I skipped."
						// let's just check if there is a bigger one. If so, skip this addition
					} else {
						if (biggerThan > -1) {

							List<Term> naa = o.getMatchedTermsByClause(ClausesEN.neararoundalongbetween1c)
							if (naa*.text == ["along"])
								c.operator = new Operator(op:Operator.Locator.Along)
							else if (naa*.text == ["around"])
								c.operator = new Operator(op:Operator.Locator.Around)
							else
								c.operator = new Operator(op:Operator.Locator.Near)
							// ok, it's a good condition
							log.debug "${o.rule.id}: Condition $c is bigger than existing condition ${q.conditions[biggerThan]}, I will overwrite it."
							subject.classifySubject("en", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions[biggerThan] = c
						} else {
							subject.classifySubject("en", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions << c
							log.debug "${o.rule.id}: No conflicting conditions, adding Subject as a condition object: $c"
						}
					}
				}
			}
		]),
		/** 3.1.7 [NORTH|SOUTH|ETC] {GEOSCOPE} **/
		new Rule(id:'rulesToCaptureConditions 3.1.7',
		description:'[in|to]! the? [north|south|etc-ern]! part? of? {place}!',
		clauses:[
			ClausesEN.into1c,
			ClausesEN.the01c,
			LocalGazetteerEN.compasses1nc,
			ClausesEN.part01c,
			ClausesEN.of01c,
			NEGazetteer.NE_LOCAL_1c
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched "+terms
				List<NamedEntity> nes = o.getMatchedNEs()
				// let's walk the nes, chek if they weren't already captured in subjects or past conditions
				List nes2 = nes.clone()
				nes2.each{ne ->
					if (q.subject && ne.equalsTerms(q.subject.geoscopeTerms)) {
						log.debug "Captured NE $ne is already on subject ${q.subject}. Removing."
						nes.remove(ne)
					}
					// conditions may contain an object or a QueryGeoscope
					q.conditions?.each{c ->
						if (c.object) {
							def c_ne = (c.object instanceof NamedEntity ? c.object :
									(c.object instanceof QueryGeoscope ? c.object?.ne : null) )
							if (ne == c_ne) {
								log.debug "Captured NE $ne is already on condition $c. Removing."
								nes.remove(ne)
							}
						}
					}
				}//nes2.each ne->

				// with the remaining NEs , build new conditions
				nes?.each{ne ->
					Condition c = new Condition(terms:terms)
					def thislocator
					List<Term> compass_terms = o.getMatchedTermsByClause(LocalGazetteerEN.compasses1nc)
					String termstring = compass_terms*.text.join(" ")
					if (termstring =~ /(?i)north/) {
						if (termstring =~ /(?i)west/) thislocator = Operator.Locator.Northwest
						else if (termstring =~ /(?i)east/) thislocator = Operator.Locator.Northeast
						else thislocator = Operator.Locator.North
					} else if (termstring =~ /(?i)south/) {
						if (termstring =~ /(?i)west/) thislocator = Operator.Locator.Southwest
						else if (termstring =~ /(?i)east/) thislocator = Operator.Locator.Southeast
						else thislocator = Operator.Locator.South
					} else if (termstring =~ /(?i)west/) Operator.Locator.West
					else if (termstring =~ /(?i)east/) Operator.Locator.East

					c.object = new QueryGeoscope(ne:ne)
					c.operator = new Operator(op:thislocator)
					q.conditions << c
					log.debug "Adding new condition, $c."
				}
			}
		]),
		/** 3.1.8 Within XXXX km of {GEOSCOPE} **/
		// for "cities within X km of Frankfort, you need to give a predicate to generate answers,
		// otherwise it may expand all cities*/
		new Rule(id:'rulesToCaptureConditions 3.1.8',
		description:'within! <VALOR_QUANTIDADE> of? {place}!',
		clauses:[
			ClausesEN.within1c,
			NEGazetteer.NE_VALOR_QUANTIDADE_1c,
			ClausesEN.of01c,
			NEGazetteer.NE_LOCAL_1c
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched "+terms
				List<NamedEntity> nes = o.getMatchedNEs()
				// let's walk the nes, chek if they weren't already captured in subjects or past conditions
				List nes2 = nes.clone()
				nes2.each{ne ->
					if (q.subject && ne.equalsTerms(q.subject.geoscopeTerms)) {
						log.debug "Captured NE $ne is already on subject ${q.subject}. Removing."
						nes.remove(ne)
					}
					// conditions may contain an object or a QueryGeoscope
					q.conditions?.each{c ->
						if (c.object) {
							def c_ne = (c.object instanceof NamedEntity ? c.object :
									(c.object instanceof QueryGeoscope ? c.object?.ne : null) )
							if (ne == c_ne) {
								log.debug "Captured NE $ne is already on condition $c. Removing."
								nes.remove(ne)
							}
						}
					}
				}//nes2.each ne->

				// with the remaining NEs , build new conditions
				if (nes.size() == 2) {

					Condition c = new Condition(terms:terms)
					c.object = new QueryGeoscope(ne:nes[1])
					c.operator = new Operator(op:Operator.Locator.Around)
					nes[0].terms*.text.each{t ->
						if (t =~ /[\d\.,]+/) c.operator.amount = Double.parseDouble(t)
						else {
							if (t.equalsIgnoreCase("km")) c.operator.unit = "KM"
						}
					}
					c.predicate = new Predicate(
							dbpediaOntologyProperty:[
								new DBpediaProperty("location")
							], //3.4
							terms:o.getMatchedTermsByClause(ClausesEN.within1c)
							)

					q.conditions << c
					log.debug "Adding new condition, $c."
				}
			}
		])
	]
}