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

import rembrandt.obj.BoundaryCriteria
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.obj.Clause
import rembrandt.obj.SemanticClassification
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality
import rembrandt.obj.Term
import rembrandt.obj.ListOfNE
import rembrandt.obj.Rule
import rembrandt.obj.RulePolicy

import java.util.List;
import java.util.regex.Pattern
import org.apache.log4j.Logger
/** 
 * @author Nuno Cardoso
 * This class is a container for the captured matches between a rule and a sentence piece, 
 * and has all the matched information in a successful rule match to base over a subsequent action
 */
class MatcherObject {
     
    static Logger log = Logger.getLogger("RuleMatcher")
    
    Sentence sentence // the current sentence to be analyzed
    Rule rule // the current rule to be matched
    ListOfNE NEs // a list of NEs on the sentence to be analyzed
    List subConcepts // in optimized rules, concepts are filtered to optimal subsets 
    String lang
    
    // list of matched stuff. LinkedHashMap ensures that the key order is insertion-order                        
    LinkedHashMap <Clause,Map> pastMatches = [:] //
 	
    int clauseIndex // the current clause index in the rule
 	
    /**
     * Get the term in the current term index
     * @return the current term
     */
    Term getCurrentTerm() {
	// In this class, I'm cautious and while moving the pointer I will point always to visible terms
	return sentence.getAt(sentence.pointer)
    }
    
    /**
     * Get the clause in the current clause index
     * @return the current clause
     */
    Clause getCurrentClause() {return rule.clauses.getAt(clauseIndex)}


    /**
     * Jump to the next 'howmuch' clauses
     * @param howmuch if not given, 1
     */
    void jumpToNextClause(int howmuch = 1) {clauseIndex += howmuch}
	
    /**
     * Jump to the next 'howmuch' terms
     * @param howmuch if not given, 1
     */
    void jumpToNextTerm(int howmuch = 1) {
	//println "jumpToNextTerm: pointer=${sentence.pointer} howmuch: $howmuch"
	sentence.movePointerForVisibleTerms(howmuch)
	//println "jumpToNextTerm: now pointer=${sentence.pointer}"
    }
	
    /**
     * check if there are more clauses left
     * @retun true if there is, false otherwise
     */
    boolean thereAreMoreClauses() {return clauseIndex < rule.clauses.size()}
	
    /**
     * check if there are more terms left
     * @retun true if there is, false otherwise
     */
    boolean thereAreMoreVisibleTerms() {return sentence.thereAreVisibleTermsAhead()}
	
    /** 
     * this is a mash-up method, includes collectedTerms, collectedNEs, 
     * collectedClause and collectedMeanings
     * @param matched a HashMap with matched stuff from the successful clause match
     */	 
    void collectMatchedInfo(matched)  {
	
	//println "Collecting info for $matched"
	// if it's a OneOrMore and ZeroOrMore clauses, let's join the collected stuff

	if (pastMatches?.containsKey(matched.clause)) {
	    
	    if (matched.clause.cardinality.equals(Cardinality.ZeroOrMore) || 
	    matched.clause.cardinality.equals(Cardinality.OneOrMore) ) {
	    
		matched.terms?.each{term -> 
		    pastMatches[matched.clause].terms << term	    	
		}
		matched.nes?.each{ne -> 
	    	     pastMatches[matched.clause].nes << ne	    	
		}
	    } else {
		throw new IllegalStateException("Check the rule ${rule}: you must have all clauses different!")
	    }
	} else {
	    pastMatches[matched.clause] = [:]
	    if (matched.terms) pastMatches[matched.clause].terms = matched.terms
	    if (matched.answer) pastMatches[matched.clause].answer = matched.answer
	    if (matched.nes) pastMatches[matched.clause].nes = matched.nes
	}
    }
    		
    /**
     * Returns the collected meanings
     * @return a list of meanings
     */
    List<SemanticClassification> getMatchedMeanings() {
	return pastMatches.findAll{it.key.criteria == Criteria.MeaningMatch}.collect{it.value.answer}
    }
	
    /**
     * Returns the collected nes
     * @return a list of nes
     */
    List<NamedEntity> getMatchedNEs() {
	return pastMatches.findAll{it.key.criteria == Criteria.NEMatch}.collect{it.value.nes}.flatten()
    }
    
     
    /** return
     * Returns the stuff matched by the given clause.
     * @param clause the matched clause
     * @return the list of stuff (terms, NEs, etc) matched by it
     */
    public Map getMatchByClause(Clause clause){	
	   return pastMatches[clause]
    }

  /** return
     * Returns the stuff matched by the given clause id.
     * @param clause the matched clause id
     * @return the list of stuff (terms, NEs, etc) matched by it
     */
    public Map getMatchByClause(String name){
	   //println "pastMatches: $pastMatches name:$name"	
	   return pastMatches.find{it.key.name == name}?.collect{it.value}?.getAt(0)
    }

    /** 
     * Returns the stuff matched by the given clauses.
     * @param clauses the list of matched clauses
     * @return the list of stuff (terms, NEs, etc) matched by it. Note that the list is flattened
     */ 
    public Map getMatchesByClauses(List<Clause> clauses){	
	Map answer = [:]
	clauses.each{clause -> 
        	def o = getMatchByClause(clause)
        	o.each{k, v -> if (!answer.containsKey(k)) answer[k] = v else answer[k].addAll(v) }
          
        }
	return answer
    }
    
    /** 
     * Returns the stuff matched by the given clauses id.
     * @param clauses the list of matched clauses
     * @return the list of stuff (terms, NEs, etc) matched by it. Note that the list is flattened
     */ 
    public Map getMatchesByClauseNames(List<String> names){	
	Map answer = [:]
	Map o = clauses.findAll{names.contains(it.key.name)} 
    o.each{k, v -> if (!answer.containsKey(k)) answer[k] = v else answer[k].addAll(v) }
	return answer
    }
    /**
     * Returns the matched terms by the clauses, for the given policy
     * If the policy is Rule (the default), then all matched clauses contribute with matched terms
     * If the policy is Clause, then only clauses with colletable:true will give terms
     * @param policy a RulePolicy (Rule by default)
     * @return list of terms. Note that clauses of criteria MeaningMatch do not return terms
     */
    public List<Term> getMatchedTerms(RulePolicy policy = RulePolicy.Rule) {
	List<Term> res = []
        pastMatches.each{clause, match -> 
         //  if (clause.criteria != Criteria.MeaningMatch) {
               if (policy == RulePolicy.Rule || ((policy == RulePolicy.Clause && clause.collectable)) )
        	   match.terms?.each{term -> res << term}
         //  }
        }
	return res                  
    }
    
   public List<Term> getMatchedTermsBeforeMatchedClause(Clause c) {
	List<Term> res = []
	boolean matched = false
	pastMatches.each{clause, match -> 
	   if (clause == c) return res    
	   if (!matched) match.terms.each{term -> res << term}
	}	
	return res
   }
   
   // getMatchedTermsBeforeMatchedClause returns matched terms
   // this one resuts the whole left part of the sentence, matched and unmatched
   public List<Term> getMatchedAndUnmatchedTermsBeforeMatchedClause(Clause c) {
	List<Term> res = getMatchedTermsByClause(c) 
	//println "res: $res"
	// não pode ser por ínidce
	List<Term> res2 = []
	if (res) {
	    sentence.each{t -> if (t.index < res[0].index) res2 << t} 
	}	
	//println "res2: $res2"
	return res2	
  }
   
   // getMatchedTermsBeforeMatchedClause returns matched terms
   // this one resuts the whole left part of the sentence, matched and unmatched
   public List<Term> getMatchedAndUnmatchedTermsAfterMatchedClause(Clause c) {
	List<Term> res = getMatchedTermsByClause(c) 	
	List<Term> res2 = []
	if (res) {
	    sentence.each{t -> 
	    //println "t.index: ${t.index} res[0]: ${res[0].class}"
	    if (t.index > res[res.size()-1].index) 
		res2 << t} 
	}	
	return res2	
  }
   
   public List<Term> getMatchedTermsAfterMatchedClause(Clause c) {
	List<Term> res = []
	boolean matched = false
	pastMatches.each{clause, match -> 
	  
		if (matched) match.terms.each{term -> res << term}	
		if (clause == c) {matched = true}    		   

	}	
	return res
  }

   public List<Term> getMatchedTermsByClause(Clause c) {
	List<Term> res = []	
	//println "getTermsMatchedByClause: pastmatches = $pastMatches"
	pastMatches.each{clause, match -> 	    
		if (clause == c) match.terms.each{term -> res << term}          
	}	
	return res
  }
   
   public List<Term> getMatchedTermsByClauses(List<Clause> c) {
	List<Term> res = []	
	//println "getTermsMatchedByClause: pastmatches = $pastMatches"
	pastMatches.each{clause, match -> 	    
		if (c.find {it == clause}) match.terms.each{term -> res << term}          
	}	
	return res
 }
   
    /**
     * returns the String facet out of the Term element, using the clause's termProperty item
     * @param clause the clause. If null, return term's text
     * @return the String out of the Term 
     */
    public String getTermProperty(Term term, Clause clause = null) {
	if (!clause || !clause.termProperty) return term.text
	switch(clause.termProperty) {
	   case TermProperty.Text: 
	       return term.text
	   break
	   case TermProperty.Stem:
	       return term.stem
	   break
	   case TermProperty.Type:
	       return term.type
	   break
	   case TermProperty.Lemma:
	       return term.lemma
	   break
	}
    }
	
    /**
     * Basic regex match. 
     * @param haystack, is a Term/String 
     * @param needle is a java.util.regex.Pattern 
     * @param clause the Clause, to stuff in the match stuff and resolve term-> string of the haystack
     * @return a Map object of matched stuff, or null if not matched
     */
    public regexMatch(haystack, Pattern needle, Clause clause) {	
	if (!haystack || !needle) return null 
	String thisHaystack
    
	if (haystack instanceof String) thisHaystack = haystack	    
	if (haystack instanceof Term) thisHaystack = getTermProperty(haystack, clause)	
	
	boolean case_sensitive = true
	if (clause?.options?.case_insensitive) case_sensitive = false
	
	boolean res
	if (case_sensitive) res = (thisHaystack ==~ needle)
	else res = (thisHaystack ==~ Pattern.compile(needle.pattern(), Pattern.CASE_INSENSITIVE) )
	return ( res ? [terms:[haystack], clause:clause] : null)
    }
	
    /**
     * Basic plain match. 
     * @param haystack, is a Term/String 
     * @param needle is a String
     * @param clause the Clause, to stuff in the match stuff and resolve term-> string of the haystack
     * @return a Map object of matched stuff, or null if not matched
     */
    public plainMatch(haystack, String needle, Clause clause) {
	if (!haystack || !needle) return null 
	String thisHaystack
	
	if (haystack instanceof String) thisHaystack = haystack	    
	if (haystack instanceof Term) thisHaystack = getTermProperty(haystack, clause)	
	
	boolean case_sensitive = true
	if (clause?.options?.case_insensitive) case_sensitive = false
	
	boolean res
	if (case_sensitive) res = thisHaystack.equals(needle) else res = thisHaystack.equalsIgnoreCase(needle)
	
	return (res ? [terms:[haystack], clause:clause] : null)
    }
	
    /**
     * @param haystack, is a Term/String 
     * @param needle is a java.util.regex.Pattern 
     * @param clause the Clause, to stuff in the match stuff and resolve term-> string of the haystack
     * @return a Map object of matched stuff, or null if matched
     */
    public notRegexMatch(haystack, Pattern needle, Clause clause = null) {	  
	if (!haystack || !needle) return null 
	String thisHaystack
	
	if (haystack instanceof String) thisHaystack = haystack	    
	if (haystack instanceof Term) thisHaystack = getTermProperty(haystack, clause)	
	
	boolean case_sensitive = true
	if (clause?.options?.case_insensitive) case_sensitive = false
	
	boolean res
	if (case_sensitive) res = (thisHaystack ==~ needle)
	else res = (thisHaystack ==~ Pattern.compile(needle.pattern(), Pattern.CASE_INSENSITIVE) )
	
	return ( res ? null : [terms:[haystack], clause:clause])
    }
	
    /**
     * Basic non-plain match. 
     * @param haystack, is a Term/String 
     * @param needle is a String
     * @param clause the Clause, to stuff in the match stuff and resolve term-> string of the haystack
     * @return a Map object of matched stuff, or null if matched
     */
    public notPlainMatch(haystack, String needle, Clause clause) {
	if (!haystack || !needle) return null 	    
	String thisHaystack
	
	if (haystack instanceof String) thisHaystack = haystack	    
	if (haystack instanceof Term) thisHaystack = getTermProperty(haystack, clause)	
	
	boolean case_sensitive = true
	if (clause?.options?.case_insensitive) case_sensitive = false
	
	boolean res
	if (case_sensitive) res = thisHaystack.equals(needle) else res = thisHaystack.equalsIgnoreCase(needle)
	
	return (res ? null : [terms:[haystack], clause:clause] )
    }
     
    /*
     * MultipleTermMatch requires that all elements in the haystack match all needles.
     * Note that the haystack might have more elements than needles, it's going to return the 
     * piece that's been matched. 
     * 
     *  For example:  haystack: ['Países','de','África',], needle: [~/País(es)?/, 'de']
     *  it will invoke a regexMatc and a plainMatch, and will return the terms 'Países' and 'de' 
     *  as matched stuff, because the needles are over, even though the haystack continues.
     *  
     *  Note: the haystack must contain only Terms/Strings, the needle must contain a list of 
     *  elements of class String/Pattern
     */

    public multipleTermMatch(List haystack, List needle, Clause clause) {
	    
	// if the needle is bigger tan the haystack, it's a sure no-match
	if (needle.size()  >  haystack.size()) return null

	// sub-collector
	List<Term> terms = []
	Map res 
	
	for (int i=0; i<needle.size(); i++) {
	    if (needle[i] instanceof String) 
		res = plainMatch(haystack[i], needle[i], clause) 		
	    
	    if (needle[i] instanceof Pattern) 
		res = regexMatch(haystack[i], needle[i], clause) 		
	    
	    if (!res) return null else terms.addAll(res.terms)
	}
	return [terms:terms, clause:clause]
    }
    
    /*
     * ConceptMatch has an haystack made of a List, and you must choose from the plainMatch,
     * regexMatch and multipleTermMatch. Note that you return the first good match, so be aware that 
     * this does not handle multiple matches, just the first one.
     * 
     *  Example: 
     *  haystack: ['político','americano'] , 
     *  needle:['Português', ['político',~/[Aa]mericanos?'], 'político']
     *  
     *  It will match the second needle, ['político',~/[Aa]mericanos?'], and return. The third needle, 
     *  'político', could be also a match, but the method already returned.
     */
     public conceptMatch(List haystack, List needles, Clause clause) {
        
      //  if (clause.name=="selected time types single + plural")
      //     println "conceptMatch: haystack $haystack needles:$needles for clause $clause"
	Map res 
	
	for (needle in needles) { // do not use Closure, so that we can return from the method
	    if (needle instanceof String) {
    		res = plainMatch(haystack[0], needle, clause) 	   
	    }
	    else if (needle instanceof Pattern) {
    		res = regexMatch(haystack[0], needle, clause) 	
	    }
	    else if (needle instanceof List) {
		// if it's another list, treat as OR
		// EXPERIMENTAL
		if (needle[0] instanceof List) {
		    
		    needle.each{subneedle -> 		    
		    	def es2 = multipleTermMatch(haystack, subneedle, clause)	
		    	if (res2) res = res2
		    }		    
		} else {
		    res = multipleTermMatch(haystack, needle, clause)	
		}
	    }

//if (clause.name=="selected time types single + plural")
//    println "res: $res"
    
    
	    if (res) return res
	}
	return null
     }
     
     /** unlike the first one, it selects the biggest match */
     public biggerConceptMatch(List haystack, List needles, Clause clause) {
	        
	// println "BiggerConceptMatch called for haystack $haystack"//, needles $needles"
	 List all_res = []
	 def res
	 
	 for (needle in needles) { // do not use Closure, so that we can return from the method
	     if (needle instanceof String) {
		 res = plainMatch(haystack[0], needle, clause) 
		 if (res)  all_res << res
	     }
	     else if (needle instanceof Pattern) {
		 res = regexMatch(haystack[0], needle, clause) 	
		 if (res) all_res << res
	     }
	     else if (needle instanceof List) {
		 if (needle[0] instanceof List) {
		     needle.each{subneedle -> 
		     //println "Got haystack $haystack subneedle $subneedle"
		     	def res2 = multipleTermMatch(haystack, subneedle, clause)	
		     	//println "res2: $res2"
		     	if (res2) {
		     	    // overwrite res only if it's bigger
		     	    if (!res) res = res2
		     	    else if (res2.terms.size() > res.terms.size()) res = res2		     	  
		     	}
		     }		    
		 } else {
		     res = multipleTermMatch(haystack, needle, clause)	
		 }
		 if (res)  all_res << res
		 
	     }
	 }
	// println "Got $all_res"
	 if (!all_res) return null
	 if (all_res.size() == 1) return all_res[0]
	 // let's pick the one with bigger terms
	// println "Indecised between $all_res"                                        
	 all_res.sort({a, b -> a.terms.size() <=> b.terms.size()})                                        
	// println "result: $all_res"             	 
	 return all_res[0]
     }
	 
     /**
      * meaningMatch takes a haystack of String/Terms (~a regular sentence piece),
      * and needle is a meanings list, that is, a list of Map elements, each one 
      * with a needle item (that is a List) and an answer item. 
      * 
      * The haystack should be matched to ALL those needles, and for the matched needles, 
      * one must collect all the answers. There may be more than one answer.
      * 
      * for example: haystack ['Instituto','XPTO']
      * needle: [ (...)[answer:XPTO, needle:[ ~/[Ii]nstituto/ ] ], (...) ]
      * that needle should match, and return a Map 
      *      [terms:['Instituto'] answer:XPTO, needle:~/[Ii]nstituto/, clause:clause] 
      *      matching needle plus the answer
      */
     public meaningMatch(List haystack, List meanings, Clause clause) { 
	//log.debug "meaningMatch begin: haystack=$haystack"
	List collectedMatch = []
	    
	meanings.each{meaning ->
 	
	   def res // to use for good and bad matches
	   List thisHaystack = null
	   if (meaning.reverse?.equals(true) ) thisHaystack = haystack.reverse()
	    	else thisHaystack = haystack
	    	
	   meaning.needle.each{thisNeedle -> 
	   
	      if (thisNeedle instanceof String) 
	    	res = plainMatch(thisHaystack[0], thisNeedle, clause) 		
              if (thisNeedle instanceof Pattern) 
	    	res = regexMatch(thisHaystack[0], thisNeedle, clause) 		
	      if (thisNeedle instanceof List) 
		res = multipleTermMatch(thisHaystack, thisNeedle, clause)	    		
		
	      if (res && !collectedMatch*.needle.contains(thisNeedle))    {
		//  log.debug "Got res: adding haystack ${haystack}, collected: "+ [terms:res.terms, answer:meaning.answer,  needle:thisNeedle]
	    	 collectedMatch << [terms:res.terms, answer:meaning.answer, 
	    	       needle:thisNeedle, clause:clause]
	      }  
	   }
	}// each meaning
	
	//println "meaningMatch: got collectedMatch "+collectedMatch
	// now let's filter meaning needles like ["Serviços","Académicos"] and ["Serviços"],
	// stay with the biggest one.
	def collectedMatch2
	
	if (collectedMatch) {			
	    collectedMatch2 = filterContainedMeanings(collectedMatch)
	      // now, a List of Maps is now a List of a single Map. 
	      // let's return only a map.
	    if (collectedMatch2?.size() == 1 && collectedMatch2[0] instanceof Map)
	           collectedMatch2 = collectedMatch2[0]   
	}
	// now there is only one meaning.
	//	println "collectedMatch2 = $collectedMatch2"
	if (collectedMatch2 && collectedMatch2 instanceof List && collectedMatch2.size() >1)  
	    collectedMatch2 = collectedMatch2[0]
	    //println "Coll = "+collectedMatch2

       	//println "meaningMatch: returning match terms = ${collectedMatch2?.terms}, answer=${collectedMatch2?.answer}"
                                    
	 return ((collectedMatch2?.isEmpty()) ? null : collectedMatch2 )
      }
     
     
     
     /* if there is meaning terms like ["Servi√ßos","Acad√©micos"] and ["Servi√ßos"],
      *  stay with the biggest one. Use terms instead of needles, because needles can 
      *  be different, like Servi√ßos and [Ss]ervi√ßos?
      */
      private List filterContainedMeanings(List meanings) {
	if (!meanings || meanings.size() < 2) return meanings
	boolean contained
	for (int i=0; i<meanings.size()-1; i++) {
	    for (int j=i+1; j<meanings.size(); j++) {
		
		if (meanings[i]?.terms?.size() > meanings[j]?.terms?.size()) {
		    contained = partialTermMatch(meanings[i]?.terms, meanings[j]?.terms)
	 	    if (contained) meanings[j] = null	 	       
		} else if (meanings[j]?.terms?.size() >= meanings[i]?.terms?.size()) {
		    contained = partialTermMatch(meanings[j]?.terms, meanings[i]?.terms)
		    if (contained) meanings[i] = null
		}
	    }	 	    
	}
	return meanings.findAll{it != null}
    }
	    
   /**
    * Check if bigger term list contains the smallter term list, left-justified.
    * @param biggerTerms the bigger term list
    * @param smallerTerms the smaller term list
    * @return true if is left-justified, false otherwise.
    */
    public boolean partialTermMatch(List biggerTerms, List smallerTerms) {
        if (!smallerTerms || !biggerTerms) return false
        List terms = []      
    
	smallerTerms.eachWithIndex {term, i -> 
 	    if (biggerTerms?.getAt(i) == term ) terms << term
        }
	return terms == smallerTerms  
    }  
    
    /**
     * Match of named entities. 
     * Note that you must provide to the List<NamedEntity> NEs the proper set of NEs to be matched with,
     * that is, the NEs that are already detected and are within the sentence boundaries.  
     */
    public NEMatch(List<NamedEntity> haystack_ne, ne_pattern, Clause clause) {
  
	 // if there is a NE as a needle, let's use it. 
	if (ne_pattern instanceof NamedEntity) {
	    for(ne in haystack_ne) {		    
		if (ne_pattern.matchesClassification(ne) && ne_pattern.matchesBoundaries(ne, BoundaryCriteria.ExactMatch) ) 
		    return [clause:clause, terms:ne.terms, nes:[ne]]
	    }
	} else  { // NECriteria must be a List[]
	    for(ne in haystack_ne) {	
		// forget the ne_pattern, we can get the match criteria again from the Clause
		if (ne.matchesClassification(clause.pattern, clause.NECriteria)) 
		    return [clause:clause, terms:ne.terms, nes:[ne]]
	    }	   
	}
	return null
    }
    
    /**
     * Match if we are in the beginning of the sentence
     * That is, if the sentence pointer points to a term that has an termIndex of 0
     * Note: imagine the sentence is <B> Rembrandt </B>. The term <B> will have termIndex=-1,
     * because it's a hidden term. Term 'Rembrandt' will have termIndex 0, term </B> will have 
     * termIndex = -1. The Pointer must be therefore on position 1.
     * 
     */
     public sentenceBeginMatch(List sentence, Clause clause) {
	 if (!sentence) return null
	// println "Sentence: $sentence pointer: ${sentence.pointer} index: ${sentence[sentence.pointer].index}"
	 if (sentence[sentence.pointer].index == 0) return[clause:clause, terms:[]] 
	 return null
     }
    
     /**
      * Match if we are in the end of the sentence
      * The pointer must NOT point to a visible term, or point to a hiden term, but no remaining 
      * visible terms are left. 
      */
      public sentenceEndMatch(List sentence, Clause clause) {
 	 if (!sentence) return null
 	 if (!sentence.thereAreVisibleTermsAhead()) return[clause:clause, terms:[]] 
 	 return null
      }
 	
    /* 
     * Match the current clause o the unseen part of the sentence
     * @return matched stuff in a Map format, or null if not a match
     */
       public matchClause() {
		    
	Clause clause = getCurrentClause() 
	Sentence sentencePiece = sentence.getUnseenVisibleTerms()
	
	//println "SentencePiece: $sentencePiece"
	// it happens for OneOrMore cardinalities, where there's one lookahead machClause() call to check 
	// if the clause must be kept or not, and it might try to match when the sentence is over.
	if (sentencePiece.isEmpty()) return false

	switch(clause.criteria) {
	
	    case Criteria.RegexMatch:
		return regexMatch(sentencePiece[0], clause.pattern, clause)
	    break
	    
	    case Criteria.PlainMatch:
	        return plainMatch(sentencePiece[0], clause.pattern, clause)
	    break
	    
	    case Criteria.NotRegexMatch:
		return notRegexMatch(sentencePiece[0], clause.pattern, clause)
	    break
	    
	    case Criteria.NotPlainMatch:
		return notPlainMatch(sentencePiece[0], clause.pattern, clause)
	    break
	    
	    // requires a needle. It's mostly invoked by conceptMatch, where clauses have 
	    // several multipleTerm subclauses to match on...
      	    case Criteria.MultipleTermMatch:	    	    
      		return multipleTermMatch(sentencePiece, clause.pattern, clause)    	   
      	    break
      	    
      	    case Criteria.MeaningMatch:
      		return meaningMatch(sentencePiece, clause.pattern, clause)    
	    break
	    
	    case Criteria.NEMatch:
		return NEMatch(NEs.getNEsBySentenceAndTermIndex(
			sentence.index, sentence[sentence.pointer].index, NEs.fetchByStartingTerm),
		clause.pattern, clause)    
	    break
	    
	    // if we're using an optimized rule, and the initial concept (in clause.pattern) 
	    // is filtered to a subset (placed in subConcepts), let's use it instead
	    case Criteria.ConceptMatch:
		if (subConcepts) {
                  def res = conceptMatch(sentencePiece, subConcepts, clause) 
                  // if there is res with the subconcept, let's erase it, so that following sentence-walking 
                  // (that is, non-first clauses) does not hit the subconcepts (which are ment only for first-clausers).
                  if (res) subConcepts = []
                  return res
                
		}
		else
		return conceptMatch(sentencePiece, clause.pattern, clause)    
	    break
	    
	    case Criteria.BiggerConceptMatch:
		if (subConcepts) {
                  def res = biggerConceptMatch(sentencePiece, subConcepts, clause) 
                  // if there is res with the subconcept, let's erase it, so that following sentence-walking 
                  // (that is, non-first clauses) does not hit the subconcepts (which are ment only for first-clausers).
                  if (res) subConcepts = []
                  return res
                
		}
		else
		return biggerConceptMatch(sentencePiece, clause.pattern, clause)    
	    break
	    
	    case Criteria.SentenceBeginMatch:
		return sentenceBeginMatch(sentencePiece, clause)
	    break
	    case Criteria.SentenceEndMatch:
		return sentenceEndMatch(sentencePiece, clause)
	    break	     
	}
    }
}