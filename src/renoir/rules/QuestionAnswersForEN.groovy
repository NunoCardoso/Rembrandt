/* Renoir
 * Copyright (C) 2009 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package renoir.rules

import org.apache.log4j.*
import rembrandt.obj.*
import saskia.bin.Configuration
import saskia.db.table.EntityTable;
import saskia.dbpedia.*
import renoir.obj.Question
import renoir.obj.QuestionType
import renoir.obj.ExpectedAnswerType
import renoir.obj.Subject
import renoir.obj.QueryGeoscope
/* 
SELECT ?property ?hasValue ?isValueOf
WHERE {
  { <http://dbpedia.org/resource/Paris> ?property ?hasValue }
  UNION
  { ?isValueOf ?property <http://dbpedia.org/resource/Paris> }
}*/


/**
 * @author Nuno Cardoso
 */
class QuestionAnswersForEN extends QuestionAnswers {
     
    static Logger log = Logger.getLogger("RenoirQuestionSolver") 
	DBpediaAPI dbpedia
	DBpediaOntology dbpediaontology = DBpediaOntology.getInstance()
	public QuestionAnswersForEN() {
		dbpedia = DBpediaAPI.newInstance()
	}

	public Question solve(Question q) {
		
		String query
		
/*************************/	
/** 1: HOW MANY / MUCH ***/
/************************/
			 
if (q.questionType == QuestionType.HowMuch) {
		}

/***************/	
/** 2: WHICH ***/
/***************/

if (q.questionType == QuestionType.Which) {
	
		}
		
/*************/	
/** 3: WHAT ***/
/*************/

if (q.questionType == QuestionType.What) {
		
		}
		
/*************/	
/** 4: WHO ***/
/*************/

if (q.questionType == QuestionType.Who) {
			
/*** 4.1: if it resolves to a DBpediaOntologyResource 
 ex: dbpedia.org/resource/Category:Portuguese_musicians ***/
    log.debug "4.1 QuestionAnswers: Who engaged"
    q.expectedAnswerTypes?.each{eat -> 
	    if (eat.resolvesTo == ExpectedAnswerType.Type.DBpediaOntologyResource) {
		/** 4.1.1 if we have a condition
		ex: predicate = ["Person/birthPlace"], object = ["Beja"] */
			log.debug "4.1.1 Questionanswers: EAT DBpediaOntologyResource engaged"
				
			if (q.conditions) {
			
			/** 4.1.1.1 Number Conditions: 1, there are predicate/Ontology and object/Ontology */
	    		if (q.conditions.size() == 1 && 
				q.conditions[0].predicate.dbpediaOntologyProperty && 
				q.conditions[0].object.dbpediaPage) {
						
					log.debug "4.1.1.1 Questionanswers: conditions w/predicate and object engaged"
					List<DBpediaProperty> predicate = q.conditions[0].predicate.\
						dbpediaOntologyProperty // 
					List object = q.conditions[0].object.dbpediaPage\
						.values().toList().flatten()

				 	query = "SELECT DISTINCT ?s WHERE { "
  					// ExpentedAnswerType can be a list -> make it a union.
		  			List eat2sparql = eat.DBpediaOntologyResources.\
		    			collect{"{ {?s skos:subject <"+DBpediaResource.getFullName(it)+"> } UNION { "+
		    			"?s skos:subject ?category . ?category skos:broader <"+DBpediaResource.getFullName(it)+"> } }"}
		  			String eat2sparql2 = eat2sparql.join(" UNION ")
					
		  			List cond2sparql = []
		  			predicate.each{p -> 
		     			object.each{o -> 
		        			cond2sparql << "{?s <"+p.getFullName()+"> <"+DBpediaResource.getFullName(o)+">}"
		     			}
		  			}
		  			String cond2sparql2 = cond2sparql.join(" UNION ") 
		  
		  			query += eat2sparql2+" "+cond2sparql2+"}"
		  
		  			log.debug "SPARQL query: $query"
					
		  			def answer = dbpedia.sparql(query)
					q.sparqlQueries << 	query
		  			if (answer) {
		      			q.sparqlQueries << query
		      			answer.each{a -> 
		      				q.answer << a.s.toString()
		      	//		question.answerJustification << object
		      			}
		  			}
		  		}
	    	}
		}  // if (eat.esolvesTo == ExpectedAnswerType.Type.DBpediaOntologyResource) 
    } //     q.expectedAnswerType.each eat -> 
} // if q.questionType == Who
	
/*************/	
/** 5: WHO ***/
/*************/	
	
if (q.questionType == QuestionType.Where) {
	
}
		
/************/	
/* 99: NONE **/
/************/

if (q.questionType == QuestionType.None) {
	
log.debug "99 QuestionAnswers: None engaged"

if (q.expectedAnswerTypes) {
	
q.expectedAnswerTypes.each{eat -> 	

	/*** 99.1: if it resolves to DBpediaOntologyResource(s) 
	ex: http://dbpedia.org/resource/Category:Portuguese_musicians ***/

    if (eat.resolvesTo == ExpectedAnswerType.Type.DBpediaOntologyResource) {

		log.debug "99.1 QuestionAnswers: EAT resolves to DBpediaOntologyResource"

	/** 99.1.1 if we have a subject, for example: [Musics][in][Portugal]*/
		if (q.subject) {

			log.debug "99.1.1 QuestionAnswers: q.s"

			/** 99.1.1.1 If we don't have conditions, ex: [Portuguese][musicians] */
			if (!q.conditions) {

				log.debug "99.1.1.1 q.s & !q.c"
				
				// Subject:1 conditions:o -> let's get instances of s
				query = "SELECT DISTINCT ?s WHERE { "
				List eat2sparql = eat.DBpediaOntologyResources.\
					collect{"{ {?s skos:subject <"+DBpediaResource.getFullName(it)+"> } UNION { "+
					"?s skos:subject ?category . ?category skos:broader <"+DBpediaResource.getFullName(it)+"> } }"}
				String eat2sparql2 = eat2sparql.join(" UNION ")
				query += eat2sparql2+"}"
				log.debug "SPARQL query: $query"
				def answer = dbpedia.sparql(query)
				if (answer) {
					q.sparqlQueries << query
						answer.each{a -> 
						String s = a.s.toString()
						String s2 = DBpediaResource.getShortName(s)
						if (!s2.startsWith("List_of_")) {q.answer << s}
					}
				}
				
			/** 99.1.1.2 If we have conditions, ex: [Portuguese][musicians][died][in][X] */
			
			} else {
				/** Subject:1 conditions:1+ */	
				
				q.conditions.each{c -> 
					if (c.object) {
					
						// if we have a geoscope, let's use it. ex: [wine][regions][in][rivers][of][Europe]
						if (c.object instanceof QueryGeoscope) {
						
							log.debug "99.1.1.2 q.s & q.c (Geoscope)"
							if (c.object.ne && !c.object.geo) {
								c.object.groundNEtoGeoscope()
//								println "c grounded"
							}
							// assuming the dbpedia-owl:location referes to countries
							// let's check if it needs to be expanded to countries
							if (c.object.geoscopeNeedsToBeExpandedtoCountries()) 
								c.object.expandGeoscopeToCountryLevel()
						
							List<String> geoscope_dbpedia_resources = c.object.getDBpediaResourcesFromAllGeoscopes()
						
							// PREPARE THE EAT
							query = "SELECT DISTINCT ?s WHERE { "
							List eat2sparql = eat.DBpediaOntologyResources.\
							collect{"{ {?s skos:subject <"+DBpediaResource.getFullName(it)+"> } UNION { "+
							"?s skos:subject ?category . ?category skos:broader <"+DBpediaResource.getFullName(it)+"> } }"}
							String eat2sparql2 = eat2sparql.join(" UNION ")
							
							// PREPARE THE CONDITION	
							List cond2sparql = geoscope_dbpedia_resources.collect{"{ ?s dbpedia-owl:location <"+
								DBpediaResource.getFullName(it)+">}"}
								String cond2sparql2 = cond2sparql.join(" UNION ") 
							query += eat2sparql2+" "+cond2sparql2+"}"	
						
							log.debug "SPARQL query: $query"
							def answer = dbpedia.sparql(query)
							if (answer) {
								q.sparqlQueries << query
								answer.each{a -> 
									String s = a.s.toString()
									String s2 = DBpediaResource.getShortName(s)
									if (!s2.startsWith("List_of_")) {q.answer << s}
								}
							}
						}
						else if (c.object instanceof Subject) {
							log.debug "99.1.1.2 q.s & q.c (Subject)"
							// it can be a geoscope featuretype+feature, like "rivers in Europe".
							// It's hard to relate it to a subject using properties like dbpedia-owl, 
							// so we can make a generic link, anything.
							
							// so, let's find entities where a subjet relates to this condition_subject:
							// ex: "wine_regions" in "Rivers_of_europe"
							if (c.object.categoryWikipediaAsDBPediaResource) {
								
								query = "SELECT DISTINCT ?s WHERE "
									
								List eat2sparql = eat.DBpediaOntologyResources.\
								collect{"{ {?s skos:subject <"+DBpediaResource.getFullName(it)+"> } UNION { "+
								"?s skos:subject ?category . ?category skos:broader <"+DBpediaResource.getFullName(it)+"> } }"}
								String eat2sparql2 = eat2sparql.join(" UNION ")
							
								List cond2sparql = c.object.categoryWikipediaAsDBPediaResource.\
								collect{"{ {?c skos:subject <"+DBpediaResource.getFullName(it)+"> } UNION { "+
								"?c skos:subject ?category . ?category skos:broader <"+DBpediaResource.getFullName(it)+"> } }"}
								String cond2sparql2 = cond2sparql.join(" UNION ")
							
								query += "{ {"+eat2sparql2+"} {?s dbpedia-owl:location ?location} {" + cond2sparql2 + "} {?c dbpedia-owl:location ?location} }  "
								log.debug "SPARQL query: $query"
								def answer = dbpedia.sparql(query)
								if (answer) {
									q.sparqlQueries << query
									answer.each{a -> 
										String s = a.s.toString()
										String s2 = DBpediaResource.getShortName(s)
										if (!s2.startsWith("List_of_")) {q.answer << s}
									}
								} else {
									// plano b: expand eat 'wine regions' and geoscope-subject 'rivers of europe', add everything to others.
									// where entity like wine rgions or european rivers will make an entrance, and ne 'Europe' will refine woeids
									query = "SELECT DISTINCT ?s WHERE {"+eat2sparql2+"}"
									def answer2 = dbpedia.sparql(query)
									log.debug "SPARQL query: $query GOT "+answer2?.size()+" results"
									if (answer2) {
										q.sparqlQueries << query
										answer2.each{a -> 
											String s = a.s.toString()
											String resource = DBpediaResource.getShortName(s)
											if (!resource.startsWith("List_of_")) {
												// Since "others" is a pool, let's identity by encapsulating in an Entity object
												EntityTable ent = EntityTable.getFromDBpediaResource(DBpediaResource.getShortName(resource))
			        							if (!ent) {
                    								List listOfClasses = dbpedia.getDBpediaOntologyClassFromDBpediaResource(resource)
                    								ent = new EntityTable(
                										ent_dbpedia_resource:DBpediaResource.getShortName(resource),
                										ent_dbpedia_class:dbpediaontology.getNarrowerClassFrom(listOfClasses)
                    								)
                    								ent.ent_id = ent.addThisToDB()
                								}  
												q.others << ent
												
											}
										}
									}	
									query = "SELECT DISTINCT ?c WHERE {"+cond2sparql2+"}"
									answer2 = dbpedia.sparql(query)
									log.debug "SPARQL query: $query GOT "+answer2?.size()+" results"
									if (answer2) {
										q.sparqlQueries << query
										answer2.each{a -> 
											String s = a.c.toString()
											String resource = DBpediaResource.getShortName(s)
											if (!resource.startsWith("List_of_")) {
												// Since "others" is a pool, let's identity by encapsulating in an Entity object
												EntityTable ent = EntityTable.getFromDBpediaResource(DBpediaResource.getShortName(resource))
			        							if (!ent) {
                    								List listOfClasses = dbpedia.getDBpediaOntologyClassFromDBpediaResource(resource)
                    								ent = new EntityTable(
                										ent_dbpedia_resource:DBpediaResource.getShortName(resource),
                										ent_dbpedia_class:dbpediaontology.getNarrowerClassFrom(listOfClasses)
                    								)
                    								ent.ent_id = ent.addThisToDB()
                								}  
												q.others << ent
												
											}
										}
									}
								}
							}
						} // if c.objext instance of
					} // if c.object
				}// q.conditions.each c -> 
			} // if q.conditions
			
		// 99.1.2 Sem subject	
		}  else  {
			
			log.debug "99.1.2 QuestionAnswers: !q.s"

			if (q.conditions) {
				
				q.conditions.each{c -> 
					if (c.object) {
						// if we have a subject as a geoscope, use it ex: something at universities in Scotland

						// 99.1.2.1 // com conditions 

						log.debug "99.1.2.1 QuestionAnswers: !q.s & q.c (Subject)"

						if (c.object instanceof Subject) {
					
						//println "I'm all in with $c and eat $eat"
							if (c.object.categoryWikipediaAsDBPediaResource) {
								
								query = "SELECT DISTINCT ?s WHERE "
									
								List cond2sparql = c.object.categoryWikipediaAsDBPediaResource.\
								collect{"{ {?s skos:subject <"+DBpediaResource.getFullName(it)+"> } UNION { "+
								"?s skos:subject ?category . ?category skos:broader <"+DBpediaResource.getFullName(it)+"> } }"}
								String cond2sparql2 = cond2sparql.join(" UNION ")
							
								query += "{ "+ cond2sparql2 + "} "
								log.debug "SPARQL query: $query"
								def answer = dbpedia.sparql(query)
								if (answer) {
									q.sparqlQueries << query
									answer.each{a -> 
										String s = a.s.toString()
										String resource = DBpediaResource.getShortName(s)
										if (!resource.startsWith("List_of_")) {
										// Since "others" is a pool, let's identity by encapsulating in an Entity object
											EntityTable ent = EntityTable.getFromDBpediaResource(DBpediaResource.getShortName(resource))
											if (!ent) {
												List listOfClasses = dbpedia.getDBpediaOntologyClassFromDBpediaResource(resource)
												ent = new EntityTable(
												ent_dbpedia_resource:DBpediaResource.getShortName(resource),
												ent_dbpedia_class:dbpediaontology.getNarrowerClassFrom(listOfClasses)
												)
												ent.ent_id = ent.addThisToDB()
											}  
											q.others << ent
										}
									}
								}
							}
						}
					}		
				}	
			}
		}// else if (q subject or !q subject)
	} // eat.resolvesTo
} //     q.expectedAnswerType.each eat -> 
} else {// if ( !q.expectedAnswerType)
	
	log.debug "99.2 QuestionAnswers: EAT is null"
	
	/** 99.2.1 if we have a subject, for example: [Musics][in][Portugal]*/
	if (q.subject) {

		log.debug "99.2.1 QuestionAnswers: q.s"
	} else {
		log.debug "99.2.2 QuestionAnswers: !q.s"

		if (q.conditions) {
				
			q.conditions.each{c -> 
				if (c.object) {
					// if we have a subject as a geoscope, use it ex: something at universities in Scotland

					// 99.2.2.1 // com conditions 

					log.debug "99.2.2.1 QuestionAnswers: !q.s & q.c (Subject)"

					if (c.object instanceof Subject) {
					
					//println "I'm all in with $c and eat $eat"
						if (c.object.categoryWikipediaAsDBPediaResource) {
							
							query = "SELECT DISTINCT ?s WHERE "
									
							List cond2sparql = c.object.categoryWikipediaAsDBPediaResource.\
							collect{"{ {?s skos:subject <"+DBpediaResource.getFullName(it)+"> } UNION { "+
							"?s skos:subject ?category . ?category skos:broader <"+DBpediaResource.getFullName(it)+"> } }"}
							String cond2sparql2 = cond2sparql.join(" UNION ")
							
							query += "{ "+ cond2sparql2 + "} "
							log.debug "SPARQL query: $query"
							def answer = dbpedia.sparql(query)
							if (answer) {
								q.sparqlQueries << query
								answer.each{a -> 
									String s = a.s.toString()
									String resource = DBpediaResource.getShortName(s)
									if (!resource.startsWith("List_of_")) {
									// Since "others" is a pool, let's identity by encapsulating in an Entity object
										EntityTable ent = EntityTable.getFromDBpediaResource(DBpediaResource.getShortName(resource))
										if (!ent) {
											List listOfClasses = dbpedia.getDBpediaOntologyClassFromDBpediaResource(resource)
											ent = new EntityTable(
											ent_dbpedia_resource:DBpediaResource.getShortName(resource),
											ent_dbpedia_class:dbpediaontology.getNarrowerClassFrom(listOfClasses)
											)
											ent.ent_id = ent.addThisToDB()
										}  
										q.others << ent
									}
								}
							}
						}
					}
				}		
			}	
		}
	}
} // if !q.expectedAnswerType
} // if QuestionType == None	
	
//////////////
		
	
			
					
					
		return q
	}// method solve
}//class