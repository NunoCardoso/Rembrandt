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
import rembrandt.bin.Rembrandt
import rembrandt.bin.RembrandtCore
import rembrandt.obj.Sentence
import rembrandt.obj.Document
import rembrandt.obj.NamedEntity
import rembrandt.obj.TimeGrounding
import rembrandt.obj.TimeGroundingType
import rembrandt.obj.SemanticClassification
import org.apache.log4j.*

import saskia.db.table.EntityTable;
import saskia.db.table.Geoscope;
import saskia.dbpedia.*
import saskia.bin.*
/**
 * @author Nuno Cardoso
 * Gets a Question, returns a ReformulatedQuery, an instance of RenoirQuery
 */
class QueryReformulator1 extends QueryReformulator {

    static Logger log = Logger.getLogger("RenoirQuestionSolver")
    static DBpediaAPI dbpedia = DBpediaAPI.newInstance()
    static String reformulator = "Reformulator 1"
	 static Configuration conf = Configuration.newInstance()

    static public ReformulatedQuery reformulate(RenoirQuery initial_query, Question question) {
		
		AskSaskia saskia = AskSaskia.newInstance(question.language)
	// use initial_query as a mould
		ReformulatedQuery ref_query = new ReformulatedQuery(initial_query)
		ref_query.reformulator = reformulator
		
	/* Strategy: 
	 1 - get from the answers to grounded entities, then select indexes
	// if they are some type of NEs, use NE index. 
	// if they are TIME, use Time signature
	// if they are LOCAL, use Geo signature
	
	 2 - Add related entities from question.nes
	 
	 3 - Remove stopwords and punctuation	 
	*/
		ref_query.newterms = new Sentence(0)
	
	/** 1 : Add ANSWER **/
		question.answer?.each{a -> 
	   
	    // first, if answer is given by a DBpedia Resource URL, let's use it. 
	   
	   		NamedEntity ne
	   
	   /** 1 - Add ANSWERS */
	   		if (DBpediaResource.isFullNameDBpediaResource(a)) {
	   
	       // go from resource to NE
	       		ne = new NamedEntity()
	       		String string = dbpedia.getLabelFromDBpediaResource(a, question.language)
	       		ne.terms = Sentence.simpleTokenize(string).toList()	       
	       		ne = saskia.classifyWithDBpedia(ne, a)
	   
	   		} else {
	       		RembrandtCore core = Rembrandt.getCore(question.language, "harem")
	       		Document doc = new Document()
	       		doc.body_sentences = [Sentence.simpleTokenize(a)]
	       		doc.indexBody()
	       		doc = core.releaseRembrandtOnDocument(doc)			
		
	       		if (doc.bodyNEs.size() == 1) ne = doc.bodyNEs[0]
	       		else log.warn "Warning: got "+doc.bodyNEs()+" for a $a"                                               
		
	       // it can be a date, a number, don't know
	       
	   		}
	   		if (ne && ne.terms) addNEtoReformulatedQuery(ne, ref_query, question.language)
	   
		}// each answer
	
	//println "Ending 1: question.sentence = ${question.sentence} newterms = ${ref_query.newterms}"
	
	/*** 2. DO for QUESTION.NEs*/
	
	// this is a ne pool from where we can copy NEs for condition objects
	
		question.nes?.each{ne -> 
	   		if (ne.terms) addNEtoReformulatedQuery(ne, ref_query, question.language)
	
			// Let's "glue" the content terms, it helps. 
			// to do that, locate the subsentence in the original sentence, then change the BIO info
			// super goes to the renoirQuery super class		
			Sentence ne_sentence = new Sentence(ne.terms)
			// this indexOf compares term text only.
		//	println "ne_sentence: $ne_sentence ref_query.sentence = "+(ref_query.sentence)
			int index = ref_query.sentence.indexOf(ne_sentence)
		//	println "index: $index"
			if (index >= 0) {
				boolean first = true
				for(int i=index; i< index+ne_sentence.size(); i++) {
					// change only if it's not changed!
					if (ref_query.sentence[i].phraseBIO == "O") {
						if (first) {
							ref_query.sentence[i].phraseBIO = "B"
		//				println "changed to "+	ref_query.sentence[i].phraseBIO 				
							first = false
						} else {
							ref_query.sentence[i].phraseBIO = "I"
						}
					}
				}
			}
		}

		// if the question has also grounded subjects, like "trade unions", let's 
		// also put them together
		question.subject?.each{subject -> 
			if (subject.subjectTerms) {	
				Sentence sentence = new Sentence(subject.subjectTerms)
				// this indexOf compares term text only.
		//	println "sentence: $sentence ref_query.sentence = "+(ref_query.sentence)
				int index = ref_query.sentence.indexOf(sentence)
		//	println "index: $index"
				if (index >= 0) {
					boolean first = true
					for(int i=index; i< index+sentence.size(); i++) {
						if (ref_query.sentence[i].phraseBIO == "O") {
							if (first) {
								ref_query.sentence[i].phraseBIO = "B"
		//					println "changed to "+	ref_query.sentence[i].phraseBIO 				
								first = false
							} else {
								ref_query.sentence[i].phraseBIO = "I"
							}
						}
					}
				}
			}
		}
			

	//println "Ending 2: question.sentence = ${question.sentence} newterms = ${ref_query.newterms}"

	/** 3. remove stopwords **/
	
		ref_query.removeStopwordsAndPunctuation(question.language)
	
	//	println "Ending 3: question.sentence = ${question.sentence} newterms = ${ref_query.newterms}"

		return ref_query
    }
    
    public static addNEtoReformulatedQuery(NamedEntity ne, ReformulatedQuery ref_query, String lang) {
	//for each NE type, add to indexes
	   
		ne?.classification?.each{cl -> 
	          
	   		String fieldname = saskia.index.GenerateNEIndexForCollection.generateField(cl)
	   		boolean first = true
	   		List<RenoirQueryTerm> neterms = []
	                                            
	   		ne.terms?.each{term -> 
	       	  	// convert Term to RenoirQueryTerm
	       		RenoirQueryTerm newterm = new RenoirQueryTerm(term)
	       		if (first) {
		   			newterm.phraseBIO = "B"
		   			first = false    
	       		} else {
		   			newterm.phraseBIO = "I"
	       		}
	       		newterm.field = fieldname
	       		neterms << newterm
	   		}
	       	  
	   // add 'em all
	   		neterms.each{nt -> ref_query.newterms << nt}
	       
	   // if LOCAL and DBpedia grounded, fetch the WOEID and add geographic index
	   		if (cl.c == "@LOCAL" && cl.t != "@VIRTUAL") {
				log.info "Going for DBpedia-related geoscope stuff. Note: ne.dbpedia = ${ne.dbpediaPage}"
	       		if (ne.dbpediaPage.containsKey(cl)) {
		   			ne.dbpediaPage[cl].each{resource -> 
		   				EntityTable ent = EntityTable.getFromDBpediaResource(resource)
		   				Geoscope geo = ent?.hasGeoscope()
						log.info "The entity $ent has geoscope $geo"
		   				if (geo) {
		       				RenoirQueryTerm woeid_term = new RenoirQueryTerm(""+geo.geo_woeid)
	       					woeid_term.field= conf.get('saskia.index.woeid_label','woeid')
						//	println "ref_query.newterms="+ref_query.newterms
						//	println "woeid_term = "+woeid_term
						//	println "find = "+(ref_query.newterms.find{it.text == woeid_term.text && it.field == woeid_term.field}) 
		       				if (!ref_query.newterms.find{it.text == woeid_term.text && it.field == woeid_term.field}) 
			   					ref_query.newterms << woeid_term
			
							// now, if we are above COuntry, let's expand it
			   				if (geo.isAboveCountry()) {
								log.info "We have a geoscope above country"
								List<Geoscope> country_childrens = geo.getCountryDescendents()
								country_childrens?.each{children -> 
									RenoirQueryTerm children_woeid_term = new RenoirQueryTerm(""+children.geo_woeid)
									children_woeid_term.field=	conf.get('saskia.index.woeid_label','woeid') 
								//	println "ref_query.newterms="+ref_query.newterms
								//	println "children_woeid_term = "+children_woeid_term
							//		prnitln "find = "+(ref_query.newterms.find{it.text == children_woeid_term}) 
									if (!ref_query.newterms.find{it.text == children_woeid_term}) 
			   							ref_query.newterms << children_woeid_term
									// add the literal
									Map m = children.getName()
									log.info "Adding the woeid ONLY of $m, because it's a Country"
								} // ieach children
							}// if aboveCountry
		       			}	
		   			}
	       		}
	   		}// if LOCAL
	       	  
	   		if (cl?.c == "@TEMPO" && cl?.t == "@TEMPO_CALEND" && cl?.s == "DATA") {
	       		if (ne.tg) {
		   			List<String> indexes = ne.tg.getTimeIndex()
		   			indexes?.each{index -> 
		   				RenoirQueryTerm tg_term = new RenoirQueryTerm(""+index)
		   				tg_term.field= conf.get('saskia.index.time_label','time')
		   				if (!ref_query.newterms.contains(tg_term)) 
		   	    			ref_query.newterms << tg_term
		   			}
		   	    }
	   		}
		} // each cl
    }// end method
}
