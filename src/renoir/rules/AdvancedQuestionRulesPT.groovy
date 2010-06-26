
package renoir.rules

import org.apache.log4j.*


/**
 * @author ncardoso
 * Advanced question rules for Portuguese, to handle elaborated topics.
 */
public class AdvancedQuestionRulesPT{

/*	
	static Logger log = Logger.getLogger("RuleMatcher")

	static Clause n_01 = new Clause(name:"n_01", pattern:"N",
			 cardinality:Cardinality.ZeroOrOne, criteria:Criteria.PlainMatch, 
			 termProperty:TermProperty.Type)

	static Clause n_1 = new Clause(name:"n_1", pattern:"N",
			 cardinality:Cardinality.One, criteria:Criteria.PlainMatch, 
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
	static Clause prop_1p = new Clause(name:"prop_1", pattern:"PROP",
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

	/******* #2: RULE PROPERTIES AND OBJECTS *********/

	static GikiPrules = []
	 
	// encontrar (Que) [n? det? n adj?] v det prop+
	
	// GikiP 1
	// Ex: (Que) r�pidos aparecem em os filmes adaptados de a obra O �ltimo dos Moicanos"
/*		new Rule(id:'Advanced spo1', description:'(N, V), PRP, DET?, (N, V), PRP, DET?, N?, PROP', debug:false,
	clauses:[n_1, v_1, prp_1, det_01, n_2, v_2, prp_1, det_01, n_01, prop_1p], 
	action:[{Question q, QuestionMatcherObject o ->
		log.debug "Rule advanced spo2 matched."
		Condition c = new Condition() 
		q.subject = new Subject(terms:o.getTermsMatchedByClause(n_1))
		q.subject.classifySubject()
		c.predicate = new Predicate(terms:o.getTermsMatchedByClause(v_1))
		c.predicate.lookupPredicate()	
		c.operator = new Operator(terms:o.getTermsMatchedByClause(v_1))
		c.operator.lookupOperator()
		c.object = new NEObject(ne:new NamedEntity(terms:o.getTermsMatchedByClause(prop_1p)))
		c.object.classify()
		q.conditions << c	
	}])
	
	]
	
}
/*
	// Ex: (Que) pessoas portuguesas receberam um Nobel?
	new Rule(id:'Advanced spo1', description:'N, ADJ, DET?, PRP?, PROP', debug:false,
	clauses:[n_1, adj_1, v_1p, det_01, prp_01, prop_1p], 
	action:[{Question q, QuestionMatcherObject o ->
		log.debug "Rule advanced spo1 matched."
		Condition c = new Condition() 
		q.subject = new Subject(terms:o.getTermsMatchedByClauses([n_1, adj_1]))
		q.subject.classifySubject()
		c.predicate = new Predicate(terms:o.getTermsMatchedByClause(v_1p))
		c.predicate.lookupPredicate()
		c.operator = new Operator(terms:o.getTermsMatchedByClause(v_1p))
		c.operator.lookupOperator()
		c.object = new NEObject(ne:new NamedEntity(terms:o.getTermsMatchedByClause(prop_1p)))
		c.object.classify()
		q.conditions << c
		
	}]), 
	*/
}