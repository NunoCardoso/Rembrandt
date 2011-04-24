package renoir.rules

import org.apache.log4j.*

import rembrandt.gazetteers.CommonClauses as CC
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.pt.*
import rembrandt.obj.*
import rembrandt.rules.*
import renoir.obj.*
import saskia.bin.*
import saskia.db.database.SaskiaMainDB
import saskia.dbpedia.*
import saskia.gazetteers.*

class QuestionRulesForPT extends QuestionRules {

	static Logger log = Logger.getLogger("RenoirQuestionSolver")

	//clausulas
	static Clause quantoas1 =  Clause.newRegex1Clause(~/[Qq]uant[oa]s?/)
	static Clause qualis1 =  Clause.newRegex1Clause(~/[Qq]ua[li]s?/)
	static Clause que1 =  Clause.newRegex1Clause(~/[Qq]ue/)
	static Clause quem1 = Clause.newRegex1Clause(~/[Qq]uem/)
	static Clause onde1 = Clause.newRegex1Clause(~/A?[Oo]nde/)
	static Clause como1 = Clause.newRegex1Clause(~/[Cc]omo/)
	static Clause porque1 = Clause.newRegex1Clause(~/[Pp]orqu[eê]/)
	static Clause quando1 = Clause.newRegex1Clause(~/[Qq]uando/)
	static Clause chama1 = Clause.newPlain1Clause("chama")

	static Clause placeAdjective1pt = new Clause(name:"placeAdjective1",
	cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
	pattern:Places.getMeaningList("pt", "adj"))

	static Clause placeName1pt = new Clause(name:"placeName1",
	cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
	pattern:Places.getMeaningList("pt", "name"))

	static Clause subject1pt = new Clause(name:"subject1pt",
	cardinality:Cardinality.One, criteria:Criteria.MeaningMatch,
	pattern:SaskiaMainDB.newInstance().getDBTable("saskia.db.table.SubjectTable")
	.makeConceptList("pt"), options:["case_insensitive":true])

	/******* #1: RULES FOR QUESTION TYPE *********/

	static List rulesToDetectQuestionType = [
		// 1.1 Quantos habitantes
		new Rule(id:'quantos1', description:'Quant[oa]s',
		clauses:[CC.beginSentence1, quantoas1],
		action: [
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.HowMuch
				q.questionTypeTerms = o.getMatchedTermsByClause(quantoas1)
				log.debug "quantos1 matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.2
		new Rule(id:'qual1', description:'Qua[il]s',
		clauses:[CC.beginSentence1, qualis1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.Which
				q.questionTypeTerms = o.getMatchedTermsByClause(qualis1)
				log.debug "qual1 matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.3
		new Rule(id:'quem1', description:'Quem',
		clauses:[CC.beginSentence1, quem1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.Who
				q.questionTypeTerms = o.getMatchedTermsByClause(quem1)
				log.debug "quem1 matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.4
		new Rule(id:'onde1', description:'Onde',
		clauses:[CC.beginSentence1, onde1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.Where
				q.questionTypeTerms = o.getMatchedTermsByClause(onde1)
				log.debug "onde1 matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.5
		new Rule(id:'como1', description:'Como',
		clauses:[CC.beginSentence1, como1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.How
				q.questionTypeTerms = o.getMatchedTermsByClause(como1)
				log.debug "como1 matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.6
		new Rule(id:'como2', description:'Como se chama',
		clauses:[
			CC.beginSentence1,
			como1,
			ClausesPT.se1c,
			chama1
		],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.Which
				q.questionTypeTerms = o.getMatchedTermsByClauses([
					como1,
					ClausesPT.se1c,
					chama1
				])
				log.debug "como2 matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.7
		new Rule(id:'que1', description:'Em? que',
		clauses:[
			CC.beginSentence1,
			ClausesPT.Eem01c,
			que1
		],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.Which
				q.questionTypeTerms = o.getMatchedTermsByClauses([ClausesPT.Eem01c, que1])
				log.debug "que1 matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		//1.8 overwrites previous "Que1" rule
		new Rule(id:'que2', description:'O que é',
		clauses:[
			CC.beginSentence1,
			ClausesPT.Oo1c,
			que1,
			ClausesPT.ee1nc
		],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.What
				q.questionTypeTerms = o.getMatchedTermsByClauses([
					ClausesPT.Oo1c,
					que1,
					ClausesPT.ee1nc
				])
				log.debug "que2 matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.9
		new Rule(id:'quando1', description:'Quando',
		clauses:[CC.beginSentence1, quando1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.When
				q.questionTypeTerms = o.getMatchedTermsByClause(quando1)
				log.debug "quando1 matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		),
		// 1.10
		new Rule(id:'porque1', description:'Porquê',
		clauses:[CC.beginSentence1, porque1],
		action:[
			{MatcherObject o, Question q ->
				q.questionType = QuestionType.Why
				q.questionTypeTerms = o.getMatchedTermsByClause(porque1)
				log.debug "porque1 matched: question type is ${q.questionType}, terms matched = ${q.questionTypeTerms}"
			}]
		)
	]

	/******* #2: SUBJECT RULE *********/

	// 2.1
	static List rulesToCaptureSubjects = [
		new Rule(id:'rulesToCaptureSubjects 2.1', description:'músicos portugueses',
		//a notPlaceAdjective is a non-greedy "anything except a place adjective"
		clauses:[
			subject1pt,
			placeAdjective1pt
		],
		action:[
			{MatcherObject o, Question q ->
				// they have the ID of the Subject and Geoscope in the DB
				// Note: Match only once; ex: "portuguese musicians" performed in "portuguese cities".
				// assume questions in active mode. I'll get the second one later, in conditions.
				List<Term> terms = o.getMatchedTermsByClauses([
					subject1pt,
					placeAdjective1pt
				])
				List<Term> beforeterms = o.getMatchedAndUnmatchedTermsBeforeMatchedClause(subject1pt)
				log.debug "${o.rule.id}: matched terms $terms"

				boolean issubject = true
				// let's try to prove it's not
				if (beforeterms) {
					if (beforeterms[(beforeterms.size()-1)].text == "em")
						issubject = false
				}

				if (!q.subject) {
					// note how Question is an Expando.
					if (issubject) {
						q.subjectMatch = o.getMatchByClause(subject1pt)
						q.placeMatch = o.getMatchByClause(placeAdjective1pt)
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
		new Rule(id:'rulesToCaptureSubjects 2.2', description:'Músicos de Portugal',
		clauses:[
			subject1pt,
			ClausesPT.dnaeosem1c,
			placeName1pt
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTermsByClauses([subject1pt, placeName1pt])
				List<Term> beforeterms = o.getMatchedAndUnmatchedTermsBeforeMatchedClause(subject1pt)
				log.debug "${o.rule.id}: matched terms $terms"

				boolean issubject = true
				// let's try to prove it's not
				if (beforeterms) {
					if (beforeterms[(beforeterms.size()-1)].text == "em")
						issubject = false
				}

				if (!q.subject) {
					// note how Question is an Expando.
					if (issubject) {
						q.subjectMatch = o.getMatchByClause(subject1pt)
						q.placeMatch = o.getMatchByClause(placeName1pt)
						q.subject = new Subject()
						q.subject.classifySubject("pt", q.subjectMatch, q.placeMatch)
						log.debug "${o.rule.id}: added subject = ${q.subject}"
					}else {
						log.debug "${o.rule.id}: since there's already a subject ${q.subject}, it's skipped"
					}
				}
			}
		]) ,
		new Rule(id:'rulesToCaptureSubjects 2.3', description:'Músicos',
		clauses:[subject1pt],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTermsByClause(subject1pt)
				List<Term> beforeterms = o.getMatchedAndUnmatchedTermsBeforeMatchedClause(subject1pt)
				log.debug "${o.rule.id}: matched terms $terms"
				boolean issubject = true
				// let's try to prove it's not
				if (beforeterms) {
					if (beforeterms[(beforeterms.size()-1)].text == "em")
						issubject = false
				}

				if (!q.subject) {
					// note how Question is an Expando.
					if (issubject) {
						q.subjectMatch = o.getMatchByClause(subject1pt)
						q.subject = new Subject()
						q.subject.classifySubject("pt", q.subjectMatch, null)
						log.debug "${o.rule.id}: added subject = ${q.subject}"
					}else {
						log.debug "${o.rule.id}: since there's already a subject ${q.subject}, it's skipped"
					}
				}
			}
		])
	]

	/****** #3: get conditions (predicates + operators + NEs) *****/

	// note: 'born in 1954' and 'born in Porto', or 'born in cities near Danube' yields different conditions
	// so I cannot ground predicates without knowing the NE (birthdate or birthplace?) That's why I already have the
	// NEs with me

	static List rulesToCaptureConditions = [
		/****** 3.1 GET GEOSCOPES *****/	

		// NOTE: This rules *may steal* the subject, if there's a 'in {subject}' or 'near {subject}'.
		// the [in] and [near] are mandatory to steal them.

		// "in {place}" will capture also "in {place} and in {place}"
		// don't use endSentence, for tuff like "Musicians born in Portugal and in XI century"
		// of course, this one might be confused with captured subjects!

		/*** 3.1.1 IN {GEOSCOPE} ***/
		new Rule(id:'rulesToCaptureConditions 3.1.1',
		description:'[em|naos?]! {place}! e? [em|naos?]? {place?}',
		clauses:[
			ClausesPT.emnaos1c,
			NEGazetteer.NE_LOCAL_1c,
			ClausesPT.e01c,
			ClausesPT.emnaos01c,
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
		description:'[em|naos?]! {subject}! {adjectivePlaces}!',
		clauses:[
			ClausesPT.emnaos1c,
			subject1pt,
			placeAdjective1pt
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched "+terms

				// note how Question is an Expando.
				q.subjectMatch = o.getMatchByClause(subject1pt)
				q.placeMatch = o.getMatchByClause(placeAdjective1pt)
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
							subject.classifySubject("pt", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions[biggerThan] = c
						} else {
							subject.classifySubject("pt", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions << c
							log.debug "${o.rule.id}: No conflicting conditions, adding Subject as a condition object: $c"
						}
					}
				}
			}
		]),
		/*** 3.1.3 IN {Subject} of {PlaceName}( ex: in islands of Portugal) ***/
		new Rule(id:'rulesToCaptureConditions 3.1.3',
		description:'[em|naos?]! {subject}! [deaos]! {NamePlaces}!',
		clauses:[
			ClausesPT.emnaos1c,
			subject1pt,
			ClausesPT.daeos1nc,
			placeName1pt
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched "+terms

				// note how Question is an Expando.
				q.subjectMatch = o.getMatchByClause(subject1pt)
				q.placeMatch = o.getMatchByClause(placeName1pt)
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
							subject.classifySubject("pt", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions[biggerThan] = c
						} else {
							subject.classifySubject("pt", q.subjectMatch, q.placeMatch)
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
		description:'[perto|arredores]! [daoes?]! {place}! e? [daoes?]? {place?}',
		clauses:[
			ClausesPT.pertoarredores1c,
			ClausesPT.daeos1c,
			NEGazetteer.NE_LOCAL_1c,
			ClausesPT.e01c,
			ClausesPT.daeos01c,
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

				List<Term> naa = o.getMatchedTermsByClause( ClausesPT.pertoarredores1c)

				// with the remaining NEs , build new conditions
				nes?.each{ne ->
					Condition c = new Condition(terms:terms)
					c.object = new QueryGeoscope(ne:ne)
					if (naa*.text == ["perto"])
						c.operator = new Operator(op:Operator.Locator.Near)
					else if (naa*.text == ["arredores"])
						c.operator = new Operator(op:Operator.Locator.Around)

					q.conditions << c
					log.debug "Adding new condition, $c."
				}
			}
		]),
		/*** 3.1.4.1 NEAR {GEOSCOPE} ***/
		new Rule(id:'rulesToCaptureConditions 3.1.4.1',
		description:'entre! [aos?]! {place}! e? [aos?]? {place?}',
		clauses:[
			ClausesPT.entre1c,
			ClausesPT.aos_1c,
			NEGazetteer.NE_LOCAL_1c,
			ClausesPT.e01c,
			ClausesPT.aos_01c,
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

				// with the remaining NEs , build new conditions
				nes?.each{ne ->
					Condition c = new Condition(terms:terms)
					c.object = new QueryGeoscope(ne:ne)
					c.operator = new Operator(op:Operator.Locator.Between)
					q.conditions << c
					log.debug "Adding new condition, $c."
				}
			}
		]),
		/*** 3.1.5 NEAR {PlaceAdjective}{Subject} (ex: in Portuguese islands) ***/
		new Rule(id:'rulesToCaptureConditions 3.1.5',
		description:'[perto|arredores]! [daoes?]!  {subject}! {adjectivePlaces}!',
		clauses:[
			ClausesPT.pertoarredores1c,
			ClausesPT.daeos1c,
			subject1pt,
			placeAdjective1pt
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched terms "+terms

				// note how Question is an Expando.
				q.subjectMatch = o.getMatchByClause(subject1pt)
				q.placeMatch = o.getMatchByClause(placeAdjective1pt)
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
							// ok, it's a good condition
							log.debug "${o.rule.id}: Condition $c is bigger than existing condition ${q.conditions[biggerThan]}, I will overwrite it."
							subject.classifySubject("pt", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions[biggerThan] = c
						} else {
							subject.classifySubject("pt", q.subjectMatch, q.placeMatch)
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
		description:'perto! [daoes?]! {subject}! daeos! {NamePlaces}!',
		clauses:[
			ClausesPT.pertoarredores1c,
			ClausesPT.daeos1c,
			subject1pt,
			ClausesPT.daeos1nc,
			placeName1pt
		],
		action:[
			{MatcherObject o, Question q ->
				List<Term> terms = o.getMatchedTerms()
				log.debug "${o.rule.id} '${o.rule.description}' matched "+terms

				// note how Question is an Expando.
				q.subjectMatch = o.getMatchByClause(subject1pt)
				q.placeMatch = o.getMatchByClause(placeName1pt)
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
							// ok, it's a good condition
							log.debug "${o.rule.id}: Condition $c is bigger than existing condition ${q.conditions[biggerThan]}, I will overwrite it."
							subject.classifySubject("pt", q.subjectMatch, q.placeMatch)
							c.object = subject
							q.conditions[biggerThan] = c
						} else {
							subject.classifySubject("pt", q.subjectMatch, q.placeMatch)
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
		description:'[noa]! [norte|sul|etc]! daeos! {place}!',
		clauses:[
			ClausesPT.nao1c,
			LocalGazetteerPT.compasses1nc,
			ClausesPT.daeos1c,
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
					List<Term> compass_terms = o.getMatchedTermsByClause(LocalGazetteerPT.compasses1nc)
					String termstring = compass_terms*.text.join(" ")
					if (termstring =~ /(?i)nor/) {
						if (termstring =~ /(?i)oeste/) thislocator = Operator.Locator.Northwest
						else if (termstring =~ /(?i)[^o]este/) thislocator = Operator.Locator.Northeast
						else thislocator = Operator.Locator.North
					} else if (termstring =~ /(?i)su/) {
						if (termstring =~ /(?i)oeste/) thislocator = Operator.Locator.Southwest
						else if (termstring =~ /(?i)[^o]este/) thislocator = Operator.Locator.Southeast
						else thislocator = Operator.Locator.South
					} else if (termstring =~ /(?i)oeste/) Operator.Locator.West
					else if (termstring =~ /(?i)este/) Operator.Locator.East

					c.object = new QueryGeoscope(ne:ne)
					c.operator = new Operator(op:thislocator)
					q.conditions << c
					log.debug "Adding new condition, $c."
				}
			}
		]),
		/** 3.1.8 a menos de XXXX km de {GEOSCOPE} **/
		// for "cidades a menos de XXX de Francoforte, you need to give a predicate to generate answers,
		// otherwise it may expand all cities*/
		new Rule(id:'rulesToCaptureConditions 3.1.8',
		description:'<VALOR_QUANTIDADE> of? {place}!',
		clauses:[
			NEGazetteer.NE_VALOR_QUANTIDADE_1c,
			ClausesPT.de01c,
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
							if (t.equalsIgnoreCase("km") || t.equalsIgnoreCase("quilómetros")) c.operator.unit = "KM"
							else if (t.equalsIgnoreCase("menos")) {}
						}
					}
					c.predicate = new Predicate(
							dbpediaOntologyProperty:[
								new DBpediaProperty("location")]
							)

					q.conditions << c
					log.debug "Adding new condition, $c."
				}
			}
		]),
		/***** 3.2 GET PREDICATES FOR GEOSCOPES (or NOT) *****/

		// 3.2.1 PERSON:BIRTHPLACE
		new Rule(id:'BirthPlace', description:'born in {place}',
		clauses:[
			new Clause(name:"birthPlace", cardinality:Cardinality.One,
			criteria:Criteria.MeaningMatch, collectable:true,
			pattern:[
				[answer:new DBpediaProperty("birthPlace"),
					needle:[
						[
							~/nasc(?:idos?|eu|eram)/,
							~/(?:em|n[ao]s?)/
						],
						['data', 'de', 'nascimento']]
				] ]
			),
			NEGazetteer.NE_LOCAL_1c
		],

		action:[
			{MatcherObject o, Question q ->

				// note: we may have already a condition that has the geoscope.
				// if so, fill out the predicate. if not, create a new one.
				log.debug "Rule 3.2.1 'BirthPlace' matched"
				List<Term> terms = o.getMatchedTerms()
				NamedEntity ne = o.getMatchedNEs()?.getAt(0)
				def predicateMatch = o.getMatchByClause("birthPlace")

				int index = -1
				q.conditions?.eachWithIndex{c, i ->
					if (c.object) {
						def c_ne = (c.object instanceof NamedEntity ? c.object :
								(c.object instanceof QueryGeoscope ? c.object?.ne : null) )
						if (ne == c_ne) {
							log.debug "Found a condition with the named entity $ne. Let's add a predicate to it."
							index = i
						}
					}
				}

				Predicate predicate = new Predicate()
				predicate.terms = predicateMatch.terms
				predicate.dbpediaOntologyProperty = [
					predicateMatch.answer] // List
				// old condition
				if (index >= 0) {
					q.conditions[index].predicate = predicate
				} else {
					Condition condition = new Condition(terms:terms)
					condition.predicate = predicate
					condition.object = new QueryGeoscope(ne:ne)
					condition.operator = new Operator(op:Operator.Locator.In)
					q.conditions << c
				}
			}]
		),
		// 3.2.2 PERSON:HOMETOWN
		new Rule(id:'Person/homeTown', description:'lives in {place}',
		clauses:[
			new Clause(name:"homeTown", cardinality:Cardinality.One,
			criteria:Criteria.MeaningMatch, collectable:true,
			pattern:[
				[answer:[
						new DBpediaProperty("Person/homeTown"),
						//3.4
						new DBpediaProperty("hometown")
					], // 3.5
					needle:[
						[~/vivem?/, ~/(?:em|n[ao]s?)/]]
				] ]
			),
			NEGazetteer.NE_LOCAL_1c
		],
		action:[
			{MatcherObject o, Question q ->

				log.debug "Rule 3.2.2 'Person/homeTown' matched"
				List<Term> terms = o.getMatchedTerms()
				NamedEntity ne = o.getMatchedNEs()?.getAt(0)
				def predicateMatch = o.getMatchByClause("homeTown")

				int index = -1
				q.conditions?.eachWithIndex{c, i ->
					if (c.object) {
						def c_ne = (c.object instanceof NamedEntity ? c.object :
								(c.object instanceof QueryGeoscope ? c.object?.ne : null) )
						if (ne == c_ne) {
							log.debug "Found a condition with the named entity $ne. Let's add a predicate to it."
							index = i
						}
					}
				}

				Predicate predicate = new Predicate()
				predicate.terms = predicateMatch.terms
				predicate.dbpediaOntologyProperty = [
					predicateMatch.answer] // List
				// old condition
				if (index >= 0) {
					q.conditions[index].predicate = predicate
				} else {
					Condition condition = new Condition(terms:terms)
					condition.predicate = predicate
					condition.object = new QueryGeoscope(ne:ne)
					condition.operator = new Operator(op:Operator.Locator.In)
					q.conditions << c
				}

			}]
		)
	]
}