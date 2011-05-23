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

import rembrandt.obj.*
import saskia.bin.*

/**
 * @author Nuno Cardoso
 * This is the container for an enchanced query object. 
 * It tokenizes, semantifies it, and does all the magic work
 */
class Question extends Expando {
     
  Sentence sentence
  def id
  String language
    
  List<ExpectedAnswerType> expectedAnswerTypes = []
  QuestionType questionType = QuestionType.None
  List<Term> questionTypeTerms = []
                                                            
/* Subject is the raw entity that'll be classified and generate an 
 * expectedAnswerType, with questionType. 
 */
  Subject subject = null
                                                                                             
/* Conditions will have Maps. One condition is normal, two is really complex.
 * As in "Painters that were born in Germany and died in Italy"
 * Each map consists of:
 * - Predicate - Verb that indicates a relationship. Must be grounded to a 
 *               DBpedia property	 
 * - Operator - A relation between predicate and object. 
 *	           Grounded to an operator (in, more than, before, etc)
 * - Object -  An entity that's referred to in the condition.
 *              Grounded to Wikipedia/DBpedia link and/or HAREM classification  
 */
  List<Condition> conditions = []

  // store REMBRANDT results, can be used in rules
  List<NamedEntity> nes = []

/* Answer and answerJustification */
// TODO: Answer must be also objets, as NamedEntities, etc.                                
  List answer = []
  List answerJustification = []
  List sparqlQueries = [] // for debug
  List explanation



// for other grounded relevant stuff, that is, not answers but important stuff
  List others = [] 

// for entities that are in a geoscope context
  List geoscopes = [] 

  public Question(Sentence sentence, String language) {
	this.sentence = sentence
	this.language = language
  }
  
  public String dump() {
      String s = ""
	  
      this.sentence.each{term -> 

      	s += "${term.text}\t"
      	if (questionTypeTerms?.contains(term)) s += "QUESTION_TYPE("+questionType+")"
      	if (subject?.subjectTerms?.contains(term)) s += "SUBJECT_TERM"
        if (subject?.geoscopeTerms?.contains(term)) s += "SUBJECT_GEOSCOPE_TERM"      	
        conditions?.eachWithIndex{c, i -> 
            if (c.predicate?.terms?.contains(term)) s += "CONDITION_${i}_PREDICATE_TERM" 
            if (c.operator?.terms?.contains(term)) s += "CONDITION_${i}_OPERATOR_TERM" 
            if (c.object?.terms?.contains(term)) s += "CONDITION_${i}_OBJECT_TERM" 
		}
        s += "\n"    
     }
      return s
  }
 
  public String toString() {
		return "Question:(id:$id, QuestionType: $questionType, ExpectedAnswerType: $expectedAnswerType, "+
		"Subjects: $subjects, Conditions: $conditions, Answer: $answer, AnswerJustification: $answerJustification"
	}
}