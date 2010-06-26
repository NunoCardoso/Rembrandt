/**
 * 
 */
package renoir.rules

import org.apache.log4j.*
import rembrandt.obj.Rule
import rembrandt.obj.Clause
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality
import rembrandt.obj.NamedEntity
import rembrandt.obj.TermProperty


/**
 * @author ncardoso
 * Advanced question rules for Portuguese, to handle elaborated topics.
 */
public class GikiCLEFQuestionRulesPT{

	
/*	static Logger log = Logger.getLogger("RuleMatcher")

	static Clause n_01 = new Clause(name:"n_01", pattern:"N",
			 cardinality:Cardinality.ZeroOrOne, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)
	static Clause n_1 = new Clause(name:"n_1", pattern:"N",
			 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)
	static Clause n_1p = new Clause(name:"n_1p", pattern:"N",
			 cardinality:Cardinality.OneOrMore, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)
	static Clause n_2 = new Clause(name:"n_2", pattern:"N",
		 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
		 termProperty:TermProperty.Type)

	static Clause v_1 = new Clause(name:"v_1", pattern:"V",
			 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)
	static Clause v_2 = new Clause(name:"v_2", pattern:"V",
		 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
		 termProperty:TermProperty.Type)
	static Clause v_1p = new Clause(name:"v_1p", pattern:"V",
			 cardinality:Cardinality.OneOrMore, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)
			
	static Clause prop_1p = new Clause(name:"prop_1p", pattern:"PROP",
			 cardinality:Cardinality.OneOrMore, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)
	static Clause prop_2p = new Clause(name:"prop_2p", pattern:"PROP",
			 cardinality:Cardinality.OneOrMore, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)

	static Clause prp_1 = new Clause(name:"prp_1", pattern:"PRP",
			 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)
	static Clause prp_01 = new Clause(name:"prp_01", pattern:"PRP",
			 cardinality:Cardinality.ZeroOrOne, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)

	static Clause det_01 = new Clause(name:"det_01", pattern:"DET",
			 cardinality:Cardinality.ZeroOrOne, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)
	static Clause det_1 = new Clause(name:"det_1", pattern:"DET",
			 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)

	static Clause adj_1 = new Clause(name:"adj_1", pattern:"ADJ",
		 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
		 termProperty:TermProperty.Type)

	static Clause adj_01 = new Clause(name:"adj_1", pattern:"ADJ",
		 cardinality:Cardinality.ZeroOrOne, criteria:Criteria.PlainMatch, 
		 termProperty:TermProperty.Type)

	static Clause spec_1 = new Clause(name:"spec_1", pattern:"SPEC",
		 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
		 termProperty:TermProperty.Type)

	static Clause pont_1 = new Clause(name:"pont_1", pattern:"PONT",
		 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
		 termProperty:TermProperty.Type)


	static Clause que1 =  Clause.newRegex1Clause("[Qq]ue")
	
	/******* #2: RULE PROPERTIES AND OBJECTS *********/

/*	static gikiclefRules = [
	 
	// #1: Lugares em Itália que Ernest Hemingway visitou
			
	new Rule(id:'gikiclef 1', description:'', debug:false,
	clauses:[n_1, prp_1, prop_1p, spec_1, prop_2p, v_1], 
	action:[{Question q, QuestionMatcherObject o ->
		log.debug "Rule gikiclef 1 matched."
		Condition c = new Condition() 
		q.subject = new Subject(terms:o.getTermsMatchedByClauses([n_1, prp_1, prop_1p]))
		q.subject.classifySubject()
		c.predicate = new Predicate(terms:o.getTermsMatchedByClause(v_1))
		c.predicate.lookupPredicate()	
		c.operator = new Operator(terms:o.getTermsMatchedByClause(v_1))
		c.operator.lookupOperator()
		c.object = new NEObject(ne:new NamedEntity(terms:o.getTermsMatchedByClause(prop_2p)))
		c.object.classify()
		q.conditions << c	
	}]), 
	
	// #2: Que países
	// #3: Em que países
	// #5: Que obras liter�rias
	new Rule(id:'gikiclef 2', description:'', debug:false,
	clauses:[ClausesPT.Eem01c, que1, n_1p, adj_01], 
	action:[{Question q, QuestionMatcherObject o ->
		log.debug "Rule gikiclef 2 matched."
		q.subject = new Subject(terms:o.getTermsMatchedByClauses([n_1p, adj_01]))
		q.subject.classifySubject()	
	}]),
	
   // #3: Peter Dunov?
   // #5: C�rpatos?
	   
		new Rule(id:'gikiclef 3', description:'', debug:false,
	clauses:[prop_1p, pont_1], 
	action:[{Question q, QuestionMatcherObject o ->
		log.debug "Rule gikiclef 3 matched."
		if (!q.conditions) {
			Condition c = new Condition() 
			c.object = new NEObject(ne:new NamedEntity(terms:o.getTermsMatchedByClause(prop_1p)))
			c.object.classify()
			q.conditions << c
		}	
	}]), 
	
	// #4: Poetas romenos com...
	new Rule(id:'gikiclef 4', description:'', debug:false,
clauses:[CommonClauses.beginSentence, n_1p, adj_1, ClausesPT.com1c], 
action:[{Question q, QuestionMatcherObject o ->
	log.debug "Rule gikiclef 4 matched."
	q.subject = new Subject(terms:o.getTermsMatchedByClauses([n_1p, adj_1]))
	q.subject.classifySubject()		
   }])	

	
	]
*/	
}
