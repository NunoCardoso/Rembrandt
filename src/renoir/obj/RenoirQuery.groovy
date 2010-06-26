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
package renoir.obj
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer
import pt.utl.ist.lucene.LgteIndexSearcherWrapper
import pt.utl.ist.lucene.QueryConfiguration
import pt.utl.ist.lucene.LgteQuery
import pt.utl.ist.lucene.LgteQueryParser
import pt.utl.ist.lucene.Globals
import saskia.gazetteers.Stopwords
import rembrandt.obj.Sentence
import rembrandt.obj.Term

/**
 * @author Nuno Cardoso
 *
 */
class RenoirQuery {
    
    public Map paramsForRenoir = [:]
    public Map paramsForLGTE = [:] // gets index weights
    public Map paramsForQueryConfiguration = [:]
    public String queryString = null
    // used to contain all terms used for indexes: term, NE, signatures!
    public Sentence sentence // List<RenoirQueryTerm>, a specialization of List<Term>
    
    String printQueryTermString(Sentence sentence) {
	 if (!sentence) return ""
	 StringBuffer s = new StringBuffer()
	 sentence.eachWithIndex{t, i -> 
	     // if it's the end sentence or not, let's look ahead 
	     boolean lastSentenceTerm = (i == (sentence.size() -1) ? true : 
 		 sentence[i+1].phraseBIO != "I" ? true : false)
 	      
 	     switch (t.phraseBIO) {
 	     case "O": 
 		 s.append ("${t.field}:${t.text}"+(t.weight ? "^"+t.weight : "")+" ")
             break
 	     case "B": 
 		 s.append "${t.field}:\"${t.text}"+(lastSentenceTerm ? "\""+
 			 (t.weight ? "^"+t.weight : "") : "")+" " 
             break
 	     case "I": 
 		 s.append "${t.text}"+(lastSentenceTerm ? "\""+
        	     (t.weight ? "^"+t.weight : "") : "")+" "
             break
 	     }
	 }
           
	 return s.toString().trim()
    }
    
    /** useful to debug */
    public String toString() {
	StringBuffer s = new StringBuffer()
        if (paramsForRenoir) paramsForRenoir.each{k, v -> s.append("$k:$v ")}
        if (paramsForLGTE) paramsForLGTE.each{k, v -> 
	      
					s.append("$k:$v ")
				
			}
        if (paramsForQueryConfiguration) paramsForQueryConfiguration.each{k, v -> 

				// let's just make an hack for model.field.boost stuff
				def mx = k =~ /^model.field.boost.(.*)$/
				if (mx.matches()) {
					s.append(""+mx.group(1)+"-weight:$v ")
				} else { 		
					 s.append("$k:$v ")
				}
        }     
        s.append printQueryTermString(sentence)
        return s.toString().trim()
    }
    
    public String toPlainTermString() {
        return  printQueryTermString(sentence)
    }
    
    public String toLgteTermString() {
        StringBuffer s = new StringBuffer()  
        if (paramsForLGTE) paramsForLGTE.each{k, v -> 
			
					s.append("$k:$v ")
				
			}
			s.append printQueryTermString(sentence)
        return s.toString()
    }
  
    // it DOES NOT remove stopwords inside sentences! Only outside sentences
    public removeStopwordsAndPunctuation(lang) {
        Sentence newsentence = new Sentence(sentence.index)
        sentence?.each{t -> 
      // println "A verificar lang $lang t.index = ${t.index} t.text ${t.text}" 
            if (t.field == Globals.LUCENE_DEFAULT_FIELD && t.phraseBIO == "O" &&  
        	    (Stopwords.stopwords[lang].contains(t.text) ||
        	    (t.text ==~ /[\Q.,:;-!?(){}[]\E]/ )) ) {} else {
        			newsentence << t
            }
        }
        sentence = newsentence
    }
     
    public Question convertToQuestion(String lang) {	
	return new Question(sentence, lang)
    }
    
    /**
     * This one hides the parameters for RENOIR, gets QueryConfigurationBase parameters in its class, etc
     * @return the LgteQuery
     */
    public LgteQuery toLgteQuery(LgteBrokerStemAnalyzer brokerStemAnalyzer, 
	    LgteIndexSearcherWrapper searcher) {
        StringBuffer s = new StringBuffer()
        QueryConfiguration queryConfigurationBase = new QueryConfiguration()
        
        if (paramsForQueryConfiguration) paramsForQueryConfiguration.each{k, v -> 
            queryConfigurationBase.setProperty(k,""+v)
        }
        if (!paramsForQueryConfiguration.containsKey("bm25.idf.policy")) 
            queryConfigurationBase.setProperty("bm25.idf.policy","standard")
        if (!paramsForQueryConfiguration.containsKey("bm25.k1")) 
            queryConfigurationBase.setProperty("bm25.k1","1.2d")
        if (!paramsForQueryConfiguration.containsKey("bm25.b")) 
            queryConfigurationBase.setProperty("bm25.b","0.75d")
        
        if (!paramsForQueryConfiguration.containsKey("field.boost.contents")) 
            queryConfigurationBase.setProperty("field.boost.contents","1") 
            
            
        if (!paramsForQueryConfiguration.containsKey("QE.method"))
            queryConfigurationBase.setProperty("QE.method","rocchio")
        if (!paramsForQueryConfiguration.containsKey("QE.decay"))
            queryConfigurationBase.setProperty("QE.decay","0.15")
        if (!paramsForQueryConfiguration.containsKey("QE.doc.num"))
            queryConfigurationBase.setProperty("QE.doc.num","7")
        if (!paramsForQueryConfiguration.containsKey("QE.term.num"))
            queryConfigurationBase.setProperty("QE.term.num","64")
        if (!paramsForQueryConfiguration.containsKey("QE.rocchio.alpha"))
            queryConfigurationBase.setProperty("QE.rocchio.alpha","1")
        if (!paramsForQueryConfiguration.containsKey("QE.rocchio.beta"))
            queryConfigurationBase.setProperty("QE.rocchio.beta","0.75")

           
       // if (paramsForRenoir) paramsForRenoir.each{k, v -> s.append("$k:$v ")}
        if (paramsForLGTE) paramsForLGTE.each{k, v ->
	        
        			s.append("$k:$v ")
				
        }
        s.append printQueryTermString(sentence)
       // println "s.toString: ${s.toString()}"
        return LgteQueryParser.parseQuery(s.toString(), brokerStemAnalyzer, searcher, queryConfigurationBase)
    }
    
    public String toVisibleTerms() {
	return sentence.toStringLine()
    }
}
