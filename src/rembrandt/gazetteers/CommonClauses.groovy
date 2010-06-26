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

package rembrandt.gazetteers
 
import rembrandt.obj.Clause
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality

/**
 * @author Nuno Cardoso
 * Common Clauses
 */
class CommonClauses {

    /** SENTENCE BEGIN OR END */
    
    static final Clause beginSentence1 = new Clause(name:"beginSentence", collectable:false,
	cardinality:Cardinality.One, criteria:Criteria.SentenceBeginMatch)
 
    static final Clause endSentence1 = new Clause(name:"endSentence", collectable:false,
	cardinality:Cardinality.One, criteria:Criteria.SentenceEndMatch)
  
    static final Map<String,Clause> gluer1_1c =  ['pt':Clause.newRegex1Clause(Patterns.gluerPattern['pt'], "gluer1PT", true),
                                                  'en':Clause.newRegex1Clause(Patterns.gluerPattern['en'], "gluer1EN", true)]
    static final Map<String,Clause> gluer2_1c =  ['pt':Clause.newRegex1Clause(Patterns.gluerPattern['pt'], "gluer2PT", true),
                                                  'en':Clause.newRegex1Clause(Patterns.gluerPattern['en'], "gluer2EN", true)]
    static final Map<String,Clause> gluer3_1c =  ['pt':Clause.newRegex1Clause(Patterns.gluerPattern['pt'], "gluer3PT", true),
                                                  'en':Clause.newRegex1Clause(Patterns.gluerPattern['en'], "gluer3EN", true)]
 
                                                  
    static final Map<String,Clause> notGluer1_1Pc = ['pt':new Clause(name:"notGluer1PT", pattern:Patterns.gluerPattern['pt'],
	     cardinality:Cardinality.OneOrMore, criteria:Criteria.NotRegexMatch, collectable:true), 
	     'en':new Clause(name:"notGluer1EN", pattern:Patterns.gluerPattern['en'],
	     cardinality:Cardinality.OneOrMore, criteria:Criteria.NotRegexMatch, collectable:true)]
		
    static final Map<String,Clause> notGluer2_1Pc = ['pt':new Clause(name:"notGluer2PT", pattern:Patterns.gluerPattern['pt'],
	    cardinality:Cardinality.OneOrMore, criteria:Criteria.NotRegexMatch, collectable:true), 
	    'en':new Clause(name:"notGluer2EN", pattern:Patterns.gluerPattern['en'],
	     cardinality:Cardinality.OneOrMore, criteria:Criteria.NotRegexMatch, collectable:true)]

    static final Map<String,Clause> notGluer3_1Pc = ['pt':new Clause(name:"notGluer3PT", pattern:Patterns.gluerPattern['pt'],
	     cardinality:Cardinality.OneOrMore, criteria:Criteria.NotRegexMatch, collectable:true), 
	     'en':new Clause(name:"notGluer3EN", pattern:Patterns.gluerPattern['en'],
	     cardinality:Cardinality.OneOrMore, criteria:Criteria.NotRegexMatch, collectable:true)]

    static final Map<String,Clause> notGluer4_1Pc = ['pt':new Clause(name:"notGluer4PT", pattern:Patterns.gluerPattern['pt'],
	    cardinality:Cardinality.OneOrMore, criteria:Criteria.NotRegexMatch, collectable:true), 
	    'en':new Clause(name:"notGluer4EN", pattern:Patterns.gluerPattern['en'],
	    cardinality:Cardinality.OneOrMore, criteria:Criteria.NotRegexMatch, collectable:true)]

    
    /** BASIC WORDS */
    
    static final Clause blabla1Pc = Clause.newRegex1PClause(
	    Patterns.NormalAlphaNumWord, "NormalAlphaNumWord", true)

    static final Clause capitalizedAlph1Pc= Clause.newRegex1PClause(
	    Patterns.CapitalizedAlphaWord, "CapitalizedAlphaWord1P", true)

    // One or more Capitalized Word
    static final Clause capitalizedAlphNum1Pc = Clause.newRegex1PClause(
	    Patterns.CapitalizedAlphaNumWord, "InitialAlphaNumericWord1P", true)

    // Zero or more Capitalized Word
    static final Clause capitalizedAlphNum0Pc = Clause.newRegex0PClause(
	    Patterns.CapitalizedAlphaNumWord, "InitialAlphaNumericWord0P", true)
    	 
    // Any word with only letters, digits and . - . Good for collecting something between ""
    static final Clause wordsCollectable0Pc =  Clause.newRegex0PClause(
    	Patterns.Word, "wordsCollectable",true)

  // Any word with only letters, digits and . - . Good for collecting something between ""
    static final Clause wordsCollectable1Pc =  Clause.newRegex1PClause(
    	Patterns.Word, "wordsCollectable",true)

    	// Any word that is NOT a regexMatch of aspas
    static final Clause words2Collectable0Pc = new Clause(name:"words2Collectable", 
	    cardinality:rembrandt.obj.Cardinality.ZeroOrMore,
    	criteria:rembrandt.obj.Criteria.NotRegexMatch, collectable:true, 
    	pattern:Patterns.closeQuotationMark)   
    
   /** ASPAS */
    
    static final Clause aspasOpen01nc = Clause.newRegex01Clause(
	    Patterns.openQuotationMark,'Abrir Aspas', false)
    static final Clause aspasClose01nc = Clause.newRegex01Clause(
	    Patterns.closeQuotationMark,'Fechar Aspas', false)
    static final Clause aspasOpen1nc = Clause.newRegex1Clause(
	    Patterns.openQuotationMark,'Abrir Aspas', false)
    static final Clause aspasClose1nc = Clause.newRegex1Clause(
	    Patterns.openQuotationMark,'Fechar Aspas', false)

    static final Clause comma01c = Clause.newPlain01Clause(",")
    static final Clause comma1c = Clause.newPlain1Clause(",")
    static final Clause comma01nc = Clause.newPlain01Clause(",",",",false)
    static final Clause comma1nc = Clause.newPlain1Clause(",",",",false)
    static final Clause comma1nc_duplicate = Clause.newPlain1Clause(",",",(2)", false)

 // € = \u20ac, £ = \u00a3 em UTF-8
    static final Clause moneySymbols1c = Clause.newRegex1Clause(
	    ~/[\Q$\€£\E]/, "moneySymbols")
    static final Clause percentage1c = Clause.newPlain1Clause("%")
    	
    static final Clause slashMinus01c = Clause.newRegex01Clause(~/[-\/]/,"[-/]?")	
    static final Clause slashMinus1c = Clause.newRegex1Clause(~/[-\/]/,"[-/]!")	

    static final Clause openBrackets1c = Clause.newPlain1Clause("(")
    static final Clause closeBrackets1c = Clause.newPlain1Clause(")")
    static final Clause notCloseBrackets1Pc = new Clause(pattern:")", 
      criteria:rembrandt.obj.Criteria.NotPlainMatch, 
      cardinality:rembrandt.obj.Cardinality.OneOrMore)

    static final Clause commaDot1nc = Clause.newRegex1Clause(~/[,\.]/, ",.", false)

}
 