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

import saskia.bin.Configuration
import pt.utl.ist.lucene.LgteIndexSearcherWrapper;
import pt.utl.ist.lucene.LgteQuery
import pt.utl.ist.lucene.QueryConfiguration
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer
import pt.utl.ist.lucene.Globals
import rembrandt.obj.Sentence
import rembrandt.obj.Term
import saskia.gazetteers.Stopwords

/**
 * @author Nuno Cardoso
 * It's like RenoirQuery, but it has the added term list, and overloaded toString() funcitons
 * that should be called to generate the reformulated query string
 *
 */
class ReformulatedQuery extends RenoirQuery {
 
   Sentence newterms = null // container for RenoirQueryTerms
	String reformulator // label of the reformulator
	Configuration conf = Configuration.newInstance()
	String contents_label = conf.get("saskia.index.contents_label","contents")

   public ReformulatedQuery(RenoirQuery rq) {	
		super.paramsForRenoir = rq.paramsForRenoir
		super.paramsForLGTE = rq.paramsForLGTE
		super.paramsForQueryConfiguration = rq.paramsForQueryConfiguration
		super.queryString = rq.queryString
		super.sentence = rq.sentence	    
   }
    
    public ReformulatedQuery() {
		super()
    }
    /** useful to debug */
    public String toString() {
	return super.toString() + (newterms ? " " +  printQueryTermString(newterms) : "")
    }
    
    public String toPlainTermString() {
	return super.toPlainTermString() + (newterms ? " " +  printQueryTermString(newterms) : "")
    }
    
    public String toLgteTermString() {
	return super.toLgteTermString() + (newterms ? " " +  printQueryTermString(newterms) : "")
     
    }
       
    
    // it DOES NOT remove stopwords inside sentences! Only outside sentences
    public removeStopwordsAndPunctuation(String lang) {
        Sentence newsentence = new Sentence(sentence.index)
        sentence?.each{t -> 
      // println "A verificar lang $lang t.index = ${t.index} t.text ${t.text}" 
            if (t.field == contents_label && t.phraseBIO == "O" &&  
        	    (Stopwords.stopwords[lang].contains(t.text) ||
        	    (t.text ==~ /[\Q.,:;-!?(){}[]\E]/ )) ) {} else {
        			newsentence << t
            }
		}
        
        sentence = newsentence
        
        Sentence newnewterms = new Sentence(newterms.index)
        newterms?.each{t -> 
      // println "A verificar lang $lang t.index = ${t.index} t.text ${t.text}" 
        if (t.field == contents_label && t.phraseBIO == "O" &&  
        	    (Stopwords.stopwords[lang].contains(t.text) ||
        	    (t.text ==~ /[\Q.,:;-!?(){}[]\E]/ )) ) {} else {
        			newnewterms << t
            }
		}
        newterms = newnewterms
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
        if (!paramsForQueryConfiguration.containsKey("bm25.idf.policy")) 
            queryConfigurationBase.setProperty("bm25.k1","1.2d")
        if (!paramsForQueryConfiguration.containsKey("bm25.idf.policy")) 
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
        if (paramsForLGTE) paramsForLGTE.each{k, v -> s.append("$k:$v ")}
        s.append printQueryTermString(super.sentence)
        if (newterms) s.append(" " + printQueryTermString(newterms))
       // println "s.toString: ${s.toString()}"
        return new LgteQuery(s.toString(), brokerStemAnalyzer, searcher, queryConfigurationBase)
    }  
}
