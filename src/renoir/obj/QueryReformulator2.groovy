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

import saskia.db.table.EntityTable
import saskia.db.obj.Entity
import saskia.db.obj.Geoscope
import saskia.dbpedia.*
import saskia.bin.*
/**
 * @author Nuno Cardoso
 * 
 */
class QueryReformulator2 extends QueryReformulator {

    static Logger log = Logger.getLogger("RenoirQuestionSolver")
    static DBpediaAPI dbpedia = DBpediaAPI.newInstance()
    static String reformulator = "Reformulator 2"
	static DBpediaOntology dbpediaontology = DBpediaOntology.getInstance()
	static Configuration conf = Configuration.newInstance()
	
    static public ReformulatedQuery reformulate(RenoirQuery initial_query, Question question) {
		
		AskSaskia saskia = AskSaskia.newInstance(question.language)
		ReformulatedQuery ref_query = new ReformulatedQuery(initial_query)
		ref_query.newterms = new Sentence(0)
		ref_query.reformulator = reformulator
		
	/* Strategy: 
	 contents: will only contain original contents
	 ne-*: will only contain answers / others / geoscopes that cannot be grounded to entities 
	 entity: will contain answers / others / geoscopes grounded to entities, but not to woeids
	 woeid: will contain answers / others / geoscopes grounded to woeids
	 time: will contain time expresions
	 
	 2 - Remove stopwords and punctuation	 
	*/
	
	/** 1 : Add ANSWER **/
		question.answer?.each{a -> 
	   
			if (a instanceof String && DBpediaResource.isFullNameDBpediaResource(a)) {
	   			RenoirQueryTerm newterm = new RenoirQueryTerm(
		           DBpediaResource.getShortName(a))
	       		newterm.field = "entity"
				if (!ref_query.newterms.find{it.text == newterm.text && it.field == newterm.field}) { 
					log.debug "Adding $newterm to newterms"
					ref_query.newterms << newterm
				}
			}
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
			

	/** 3. Do for OTHERS */
	question.others?.each{other -> 
		if (other instanceof EntityTable) {
			addEntityToReformulatedQuery(other, ref_query, question.language)
		}
	}

	//println "Ending 2: question.sentence = ${question.sentence} newterms = ${ref_query.newterms}"

	/** 4. remove stopwords **/
	
		ref_query.removeStopwordsAndPunctuation(question.language)
	
	//	println "Ending 3: question.sentence = ${question.sentence} newterms = ${ref_query.newterms}"

		return ref_query
    }
    
    static addNEtoReformulatedQuery(NamedEntity ne, ReformulatedQuery ref_query, String lang) {

		log.debug "Adding NE $ne the ReformulatedQuery"
		ne?.classification?.each{cl -> 

		/** if this NE classification is not associated to a DBpedia entry, let's add as NE **/
		
	   	if (!ne.dbpediaPage.containsKey(cl)) {
	          

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
	       	  
	   // add 'em all, if it's not repeated
			log.debug "NE not grounded, added to ne index: $neterms"
	   		neterms.each{nt -> ref_query.newterms << nt}
	
		/** if this NE is grounded, let's see **/
		
	    } else {
		
			// first, let's take care of time right now 
			if (cl?.c == "@TEMPO" && cl?.t == "@TEMPO_CALEND" && cl?.s == "DATA" && ne.tg) {
		   		List<String> indexes = ne.tg.getTimeIndex()
		   		indexes?.each{index -> 
		   			RenoirQueryTerm tg_term = new RenoirQueryTerm(""+index)
		   			tg_term.field= conf.get('saskia.index.time_label','time')
		   			if (!ref_query.newterms.contains(tg_term)) {
						log.debug "NE grounded with TG, added to time index: $tg_term"
		   	   			ref_query.newterms << tg_term
					}
		   		}
		
		   	} else {
			
	
	   		// if DBpedia grounded and has Geoscope, fetch the WOEID and add geographic index
				log.info "Going for DBpedia-related geoscope stuff. Note: ne.dbpedia = ${ne.dbpediaPage}"
				
	       		ne.dbpediaPage[cl].each{resource -> 
		   			EntityTable ent = EntityTable.getFromDBpediaResource(DBpediaResource.getShortName(resource))
			        if (!ent) {
                    	log.trace "no DBpedia entry ${resource} on DB. Creating a new entry."
                    	// let's get a classification.
                    	List listOfClasses = dbpedia.getDBpediaOntologyClassFromDBpediaResource(resource)
                    	log.trace "Classifying DBpedia resource $resource generated classes ${listOfClasses}" 
                    	log.trace "Narrower one: "+dbpediaontology.getNarrowerClassFrom(listOfClasses) 
                    	ent = new EntityTable(
                			ent_dbpedia_resource:DBpediaResource.getShortName(resource),
                			ent_dbpedia_class:dbpediaontology.getNarrowerClassFrom(listOfClasses)
                    	)
                    	ent.ent_id = ent.addThisToDB()
                	}  

					Geoscope geo
		   			if (cl.c == "@LOCAL" && cl.t != "@VIRTUAL" && cl.s != "@CONSTRUCAO") {
						geo = ent?.hasGeoscope()
						log.info "The entity $ent has geoscope $geo"
					}

				// if geo, let's add woeid
					if (geo) {
						RenoirQueryTerm woeid_term = new RenoirQueryTerm(""+geo.geo_woeid)
						woeid_term.field= conf.get('saskia.index.woeid_label','woeid')
						if (!ref_query.newterms.find{it.text == woeid_term.text && it.field == woeid_term.field}) {
							log.debug "NE grounded as geoscope, added to woeid index: $woeid_term"
							ref_query.newterms << woeid_term
						}
						// now, if we are above COuntry, let's expand it
			   			if (geo.isAboveCountry()) {
							log.info "We have a geoscope above country"
							List<Geoscope> country_childrens = geo.getCountryDescendents()
							country_childrens?.each{children -> 
								RenoirQueryTerm children_woeid_term = new RenoirQueryTerm(""+children.geo_woeid)
								children_woeid_term.field= conf.get('saskia.index.woeid_label','woeid')
								if (!ref_query.newterms.find{it.text == children_woeid_term}) {
									log.debug "NE expaned, added to woeid index: $children_woeid_term"

			   						ref_query.newterms << children_woeid_term
								}
							// add the literal
								Map m = children.getName()
					//		log.info "Adding the woeid ONLY of $m, because it's a Country"
							} // ieach children
						}// if aboveCountry
				// if it does not have geo, let's use the entity index
		       		} else {
		   				if (ent && ent.ent_dbpedia_resource) {
	   						RenoirQueryTerm newterm = new RenoirQueryTerm(ent.ent_dbpedia_resource)
	       					newterm.field = "entity"
							if (!ref_query.newterms.find{it.text == newterm.text && 
								it.field == newterm.field}) {
								log.debug "NE grounded as entity, added to entity index: $newterm"
								ref_query.newterms << newterm
							}
						}
	       			}	
	   			}// each resource
	       	}// else not time
	   	} // ne contains grounded stuff	
		} // each cl
    }// end method

   static addEntityToReformulatedQuery(EntityTable ent, ReformulatedQuery ref_query, String lang) {

		log.debug "Adding Entity $ent the ReformulatedQuery"
		
		Geoscope geo = ent?.hasGeoscope()
		log.info "The entity $ent has geoscope $geo"
		// if geo, let's add woeid
		if (geo) {
			RenoirQueryTerm woeid_term = new RenoirQueryTerm(""+geo.geo_woeid)
			woeid_term.field= conf.get('saskia.index.woeid_label','woeid')
			if (!ref_query.newterms.find{it.text == woeid_term.text && it.field == woeid_term.field}) {
				log.debug "NE grounded as geoscope, added to woeid index: $woeid_term"
				ref_query.newterms << woeid_term
			}
			// now, if we are above COuntry, let's expand it
			if (geo.isAboveCountry()) {
				log.info "We have a geoscope above country"
				List<Geoscope> country_childrens = geo.getCountryDescendents()
				country_childrens?.each{children -> 
					RenoirQueryTerm children_woeid_term = new RenoirQueryTerm(""+children.geo_woeid)
					children_woeid_term.field= conf.get('saskia.index.woeid_label','woeid')
					if (!ref_query.newterms.find{it.text == children_woeid_term}) {
						log.debug "NE expaned, added to woeid index: $children_woeid_term"

						ref_query.newterms << children_woeid_term
					}
				// add the literal
					Map m = children.getName()
		//		log.info "Adding the woeid ONLY of $m, because it's a Country"
				} // ieach children
			}// if aboveCountry
		// if it does not have geo, let's use the entity index
		 } else {
		  	if (ent && ent.ent_dbpedia_resource) {
	   			RenoirQueryTerm newterm = new RenoirQueryTerm(ent.ent_dbpedia_resource)
	    		newterm.field = "entity"
				if (!ref_query.newterms.find{it.text == newterm.text && 
					it.field == newterm.field}) {
					log.debug "NE grounded as entity, added to entity index: $newterm"
					ref_query.newterms << newterm
				}
			}
	    }	
	}// end method
}// end class