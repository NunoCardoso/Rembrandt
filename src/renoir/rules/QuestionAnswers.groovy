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
package renoir.rules

/**
 * @author Nuno Cardoso
 *
 */
class QuestionAnswers {

}
	
	// dbpedia2 resolves to dbpedia.org/property/
/*	Closure simple_sparql = {Expando question, predicate, object -> 
		def query = """SELECT ?s WHERE { <${object}> $predicate ?s}"""
		def answer = dbpediaAPI.sparql(query)
		if (answer) answer.each{a -> 
			question.answer << a.s.toString()
			question.answerJustification << object
		}
	}
	
	Closure complex_sparql = {Expando question, predicate, object -> 
		def properties = predicate.dbpedia_ontology_property
		def subjects = question.expectedAnswerType.ontologyClass
		def objects = object.ne.dbpediaPage
		
		subjects.each { s ->
			properties.each { p -> 
				objects.each { o -> 			
					def query = """SELECT DISTINCT ?s  WHERE {
					{ ?s $p <${o}> . ?s rdf:type <${s}> } UNION 
					{<${o}> $p ?s .?s rdf:type <${s}> } }"""
					log.debug "query: $query"
					def answer = dbpediaAPI.sparql(query)
					if (answer) answer.each{a -> 
						if(!question.answer.contains(a.s.toString()))
					     question.answer << a.s.toString()
						if (!question.answerJustification.contains(o)) 
					   question.answerJustification << o
					}
				}//objects
			}// properties
		}//subjects
	}
	
	// solve age
	Closure sparql_getAge = {Expando question, predicate, object -> 
		// get birth date, deathdate
		def queryString = """SELECT ?s WHERE { <${object}> MARK ?s.  
		       FILTER ( datatype(?s) = xsd:date || datatype(?s) = xsd:integer)}"""
		
		// devia-se dar primeiro prioridade ao que est� mapeado na ontologia, 
		// depois as propriedades da DBpedia, depois as propriedades locais das infoboxes 
		def birthDatePropsPriority = ["dbpedia-owl:birthdate"]
		def birthDatePropsSecondary = ["dbpedia2:dateOfBirth"]
		def deathDatePropsPriority = ["dbpedia-owl:deathdate"]
		def deathDatePropsSecondary = ["dbpedia2:dateOfDeath"]
		
		def query, birthDate, deathDate
		birthDatePropsPriority.each{p -> 
		   def answer = dbpediaAPI.sparql(queryString.replaceAll(/MARK/,"$p"))
		    if (answer) {
				def m = answer[0].s.toString() =~ /^(\d{4})[\-\/]\d{2}[\/\-]\d{2}.*/ 
	/*		    if (m.matches()) birthDate = Integer.parseInt(m.group(1))
			}
		}
	
		if (!birthDate) {
			birthDatePropsSecondary.each{p -> 
		      def answer = dbpediaAPI.sparql(queryString.replaceAll(/MARK/,"$p"))
		      if (answer) {
				def m = answer[0].s.toString() =~ /^(\d{4})[\-\/]\d{2}[\/\-]\d{2}.*/
	/*		    if (m.matches()) birthDate = Integer.parseInt(m.group(1))
			  }
		    }		
		}
		
		deathDatePropsPriority.each{p -> 
		    def answer = dbpediaAPI.sparql(queryString.replaceAll(/MARK/,"$p"))
		    if (answer) {
				def m = answer[0].s.toString() =~ /^(\d{4})[\-\/]\d{2}[\/\-]\d{2}.*/
	/*		    if (m.matches())deathDate = Integer.parseInt(m.group(1))
			}
		} 
		if (!deathDate) {
			deathDatePropsSecondary.each{p -> 
				def answer = dbpediaAPI.sparql(queryString.replaceAll(/MARK/,"$p"))
				if (answer) {
					def m = answer[0].s.toString() =~ /^(\d{4})[\-\/]\d{2}[\/\-]\d{2}.*/
	/*				if (m.matches())deathDate = Integer.parseInt(m.group(1))
				}
			}		
		}
		if (birthDate) {
			if (deathDate) {
				// birth and death. 
				// println "Got deathDate! $deathDate"
				question.answer =  (deathDate - birthDate)
				question.answerJustification << object			
			} else {
				question.answer << (new GregorianCalendar().get(Calendar.YEAR) - birthDate)
				question.answerJustification << object
			}
		}
	}
	*/