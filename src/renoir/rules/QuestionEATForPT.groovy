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
import saskia.dbpedia.*
import renoir.obj.Question
import renoir.obj.QuestionType
import renoir.obj.ExpectedAnswerType
/* 
SELECT ?property ?hasValue ?isValueOf
WHERE {
  { <http://dbpedia.org/resource/Paris> ?property ?hasValue }
  UNION
  { ?isValueOf ?property <http://dbpedia.org/resource/Paris> }
}*/


/**
 * @author Nuno Cardoso
 * Main class for detection of NEs in the text.
 * It takes a set of rules and a set of sentences, and outputs extracted NEs.
 */
class QuestionEATForPT extends QuestionEAT {
     
    static Logger log = Logger.getLogger("RenoirQuestionSolver") 
    DBpediaAPI dbpediaAPI
	
    public QuestionEATForPT() {
  	dbpediaAPI = DBpediaAPI.newInstance()
    }
	
    public Question solve(Question q) {
	
    /*********************
     * QuestionType.Which 
     * Example of Which, s=[0], c=[p=1, o=1]: Which is the capital of Portugal?
     * Example of Which, s=[1], c=[p=1, o=1]: Which musicians were born in Portugal?
     **********************/

	if (q.questionType == QuestionType.Which) {
	   // No subject...
	   log.debug "Rule eat-which matched."
	   ExpectedAnswerType expectedAnswerType
	
	   if (!q.subject && q.conditions) {
	       // s=[0], c=[p=1, o=1] ex: Which is the capital of Portugal?
	       if (q.conditions[0].predicate && q.conditions[0].object) {
		 // for 'capital of', grounded to 'PopulatedPlace/capital', it returns ['PopulatedPlace']
		  	 expectedAnswerType = new ExpectedAnswerType()
		    expectedAnswerType.DBpediaOntologyClasses = \
		      DBpediaPropertyDefinitionsPT.getDBpediaClassForEATfromPredicate(q.conditions[0].predicate)
		    expectedAnswerType.DBpediaOntologyClasses?.each{classification -> 
		      expectedAnswerType.categoryHAREM << \
			   DBpediaOntology2SemanticClassification.getClassificationFrom(classification)
			}
			if (expectedAnswerType.DBpediaOntologyClasses)
		    	expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyClass
		 // inferred not from a subject, but from the property
	      }
		  if (expectedAnswerType) q.expectedAnswerTypes << expectedAnswerType

/* If there is a subject... the EAT is the subject. 
  for instance, 'Which portuguese musicians' give 'Category:Portuguese_musicians' (DBpediaOntolgyResource) as EAT
 and 'Which musicians' gives 'musicians -> MusicalArtist' (DBpediaOntologyClass) as EAT
*/
	   } else if (q.subject) {
	       // s=[1], c=[whatever]
	       // go to subject. does it have a SubjectGround, or just a Subject?
	       // I'm really a nice guy for me, I have all this info all sorted out! 
	       def handled = false
		   expectedAnswerType = new ExpectedAnswerType()

	       if (q.subject.ontologyDBpediaClass) {
		      expectedAnswerType.DBpediaOntologyClasses = q.subject.ontologyDBpediaClass
		   	  expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyClass
		      handled = true
	       }
	       // resource is better! 
	       if (q.subject.categoryWikipediaAsDBPediaResource) {
		      expectedAnswerType.DBpediaOntologyResources = q.subject.categoryWikipediaAsDBPediaResource
		      expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyResource
		      handled = true
	       }
	   	   if (q.subject.categoryHAREM) {
	   	      expectedAnswerType.categoryHAREM = q.subject.categoryHAREM
	   	    // if not handled, it's this one where we resolve to. Bad, I know, but that's what we got.
	   	    if (!handled) {
	   		  expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyResource
	   		   handled = true
	   	    }
	   	   }
		   
		   if (expectedAnswerType) q.expectedAnswerTypes << expectedAnswerType
	   }
     }// if QuestionType.Which
	
	
	/******************
	 * QuestionType.Who 
	 * Example of Who, s=[0], c=[p=1, o=1]: Who is the president of Portugal?
	 * Example of Who, s=[1], c=[p=1, o=1]: Who are the musicians  born in Portugal?
	 ******************/

	if (q.questionType == QuestionType.Who) {
	    log.debug "Rule eat-who1 matched."
	     ExpectedAnswerType expectedAnswerType

	    if (!q.subject && q.conditions) {
		
		   	expectedAnswerType = new ExpectedAnswerType()

		// s=[0], c=[p=1, o=1] ex: Who is the capital of Portugal?
			if (q.conditions[0].predicate && q.conditions[0].object) {
			 // for 'capital of', grounded to 'PopulatedPlace/capital', it returns ['PopulatedPlace']
		    	expectedAnswerType.DBpediaOntologyClasses = \
		           DBpediaPropertyDefinitionsPT.getDBpediaClassForEATfromPredicate(q.conditions[0].predicate)
		    	expectedAnswerType.DBpediaOntologyClasses?.each{classification -> 
		        	expectedAnswerType.categoryHAREM << \
		        	DBpediaOntology2SemanticClassification.getClassificationFrom(classification)
		    	}
		    	if (expectedAnswerType.DBpediaOntologyClasses)
					expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyClass
			 // inferred not from a subject, but from the property
		   }
		
		   if (expectedAnswerType) q.expectedAnswerTypes << expectedAnswerType

	// If there is a subject... the EAT is the subject. 
	// for instance, 'Which portuguese musicians' give 'Category:Portuguese_musicians' (DBpediaOntolgyResource) as EAT
	// and 'Which musicians' gives 'musicians -> MusicalArtist' (DBpediaOntologyClass) as EAT

	    } else if (q.subject) {
		
		   	expectedAnswerType = new ExpectedAnswerType()

		// s=[1], c=[whatever]
		// go to subject. does it have a SubjectGround, or just a Subject?
		// I'm really a nice guy for me, I have all this info all sorted out! 
			def handled = false
			if (q.subject.ontologyDBpediaClass) {
		    	expectedAnswerType.DBpediaOntologyClasses = q.subject.ontologyDBpediaClass
		    	expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyClass
		    	handled = true
			}
			// resource is better! 
			if (q.subject.categoryWikipediaAsDBPediaResource) {
		    	expectedAnswerType.DBpediaOntologyResources = q.subject.categoryWikipediaAsDBPediaResource
		    	expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyResource
		    	handled = true
			}
			if (q.subject.categoryHAREM) {
		    	expectedAnswerType.categoryHAREM = q.subject.categoryHAREM
		    // if not handled, it's this one where we resolve to. Bad, I know, but that's what we got.
		    	if (!handled) {
					expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyResource
					handled = true
		    	}
			}
			if (expectedAnswerType) q.expectedAnswerTypes << expectedAnswerType

	    }
	 }// if QuestionType == Who
	
	/******************
	 * QuestionType.None 
	 * Example of None, s=[0], c=[p=0, o=1]: xptos of Portugal
	 * Example of None, s=[1], c=[p=0, o=1]: Cathedrals in Portugal?
	 ******************/
	// By default, None will copy the actions of Which
	if (q.questionType == QuestionType.None) {
	   // No subject...
	   log.debug "Rule eat-none matched."
	   ExpectedAnswerType expectedAnswerType
	
	   if (!q.subject && q.conditions) {
	       // s=[0], c=[p=1, o=0|1] ex: Which is the capital (of Portugal)?
	       if (q.conditions[0].predicate) {
	
		  	 expectedAnswerType = new ExpectedAnswerType()
		     expectedAnswerType.DBpediaOntologyClasses = \
		      DBpediaPropertyDefinitionsPT.getDBpediaClassForEATfromPredicate(q.conditions[0].predicate)
		     expectedAnswerType.DBpediaOntologyClasses?.each{classification -> 
		      expectedAnswerType.categoryHAREM << \
			 DBpediaOntology2SemanticClassification.getClassificationFrom(classification)
		   }
		   if (expectedAnswerType.DBpediaOntologyClasses)
		    	expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyClass
		 // inferred not from a subject, but from the property
	      }
		  if (expectedAnswerType) q.expectedAnswerTypes << expectedAnswerType

// If there is a subject... the EAT is the subject. 
// for instance, 'Which portuguese musicians' give 'Category:Portuguese_musicians' (DBpediaOntolgyResource) as EAT
// and 'Which musicians' gives 'musicians -> MusicalArtist' (DBpediaOntologyClass) as EAT

	   } else if (q.subject) {
	       // s=[1], c=[whatever]
	       // go to subject. does it have a SubjectGround, or just a Subject?
	       // I'm really a nice guy for me, I have all this info all sorted out! 
	       def handled = false
		   expectedAnswerType = new ExpectedAnswerType()

	       if (q.subject.ontologyDBpediaClass) {
		      expectedAnswerType.DBpediaOntologyClasses = q.subject.ontologyDBpediaClass
		   	  expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyClass
		      handled = true
	       }
	       // resource is better! OVERWRITE
	       if (q.subject.categoryWikipediaAsDBPediaResource) {
		      expectedAnswerType.DBpediaOntologyResources = q.subject.categoryWikipediaAsDBPediaResource
		      expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyResource
		      handled = true
	       }
	   	   if (q.subject.categoryHAREM) {
	   	      expectedAnswerType.categoryHAREM = q.subject.categoryHAREM
	   	    // if not handled, it's this one where we resolve to. Bad, I know, but that's what we got.
	   	    if (!handled) {
	   		  expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.DBpediaOntologyResource
	   		   handled = true
	   	    }
	   	   }
		   
		   if (expectedAnswerType) q.expectedAnswerTypes << expectedAnswerType
	   }
     }// if QuestionType.None
	
////////////	
	
	return q
   }// method solve
}// class getQuestionEATforPT


/****** #3: get expectedAnswerType from questionTypes, predicates, sometimes objects ********/
 
 	//static rulesToCaptureEAT = [

 	
    
    
    
    
		// ********************
		// QuestionType.HowMuch -> expect a NUMERO, TEMPO or VALOR and a property
	    // Example of HowMuch s=[], c=[p=1, o=1] How much does a Ferrari F40 weight?"
	
		// ********************
	/*	new Rule(id:'eat-howmuch1', description:'HowMuch, s=[], c=[p=1, o=1]', debug:false,
		action:[{MatcherObject o, Question q -> 
			if (q.questionType == QuestionType.HowMuch) {
				log.debug "Rule eat-howmuch1 matched."
				if (!q.subject && q.conditions) {
					if (q.conditions[0].predicate && q.conditions[0].object) {
						// looking for a number/value, given by a property
						q.expectedAnswerType.categoryHAREM = [SC.number, SC.value]
						q.expectedAnswerType.resolvesTo = ExpectedAnswerType.Type.Property // if there's no subject... it's an expected property
					}
				}
			}
		}]), 
		
		

		// *****************
		// QuestionType.What 
		// *****************
		
		new Rule(id:'eat-what1', description:'What, s=[], c=[p=0, o=1]', debug:false,
		action:[{MatcherObject o, Question q -> 
			if (q.questionType == QuestionType.What) {
				log.debug "Rule eat-what1 matched."
				if (!q.subject && q.conditions) {
					if (!q.conditions[0].predicate && q.conditions[0].object) {
						// THERE IS no EAT classes -- just an explanation to be given.
						// Leave the rest to question2SPARQL
						q.expectedAnswerType.resolvesTo = ExpectedAnswerType.Definition 
					}
				}
			}
		}]),
		
	
		
		// ******************
		// QuestionType.Where 
		// ******************

		new Rule(id:'eat-where1', description:'Where, s=[], c=[p=1, o=1]', debug:false,
		action:[{MatcherObject o, Question q -> 
			if (q.questionType == QuestionType.Where) {
				log.debug "Rule eat-where1 matched."
				if (!q.subject && q.conditions) {
					// ok, clearly we're looking for LOCAL
					if (q.conditions[0].predicate && q.conditions[0].object) {
						q.expectedAnswerType.categoryHAREM = ["LOCAL"]
						q.expectedAnswerType.ontologyClass = ["http://dbpedia.org/ontology/Place"]
						q.expectedAnswerType.resolvesTo = ExpectedAnswerType.OntologyClass 
					}
				}
			}
		}]), */