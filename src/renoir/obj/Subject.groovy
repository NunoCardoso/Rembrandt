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

import rembrandt.obj.Term
import saskia.wikipedia.WikipediaAPI
import saskia.wikipedia.WikipediaDocument
import saskia.bin.Configuration
import org.apache.log4j.*
import saskia.dbpedia.DBpediaOntology
import saskia.gazetteers.DBpediaOntologyDefinitionsPT 
import saskia.gazetteers.DBpediaOntology_to_SemanticClassification
import rembrandt.gazetteers.CommonClassifications as CC
import rembrandt.obj.Clause
import rembrandt.obj.SemanticClassification
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.obj.Rule
import rembrandt.obj.Sentence
import rembrandt.obj.TermProperty
import rembrandt.gazetteers.pt.ClausesPT
import saskia.wizards.SearchWikipediaCategories
import saskia.io.Subject as SubjectDB
import saskia.io.Geoscope as GeoscopeDB
import saskia.io.SubjectGround as SubjectGroundDB
import saskia.io.Entity as EntityDB

class Subject {
	
	static Logger log = Logger.getLogger("SubjectParser")
	static Logger rules_log = Logger.getLogger("RuleMatcher")

	String lang = Configuration.newInstance().get("global.lang") 
	
	WikipediaAPI wiki = WikipediaAPI.newInstance(lang)
	Configuration conf = Configuration.newInstance()
	
	DBpediaOntology dbpedia = DBpediaOntology.getInstance()
	DBpediaOntology_to_SemanticClassification dbpedia2sc = DBpediaOntology_to_SemanticClassification.newInstance()

	// The term raw stuff 
	List<Term> subjectTerms	
	List<Term> geoscopeTerms
	   
	 
	// The DB grounded stuff                    
	List<GeoscopeDB> geoscopes = []
	List<GeoscopeDB> expanded_geoscopes = []
	List<SubjectGroundDB> subjectgrounds = []
	List<SubjectDB> subjects = []
	
	
	// grounded classifications
	List<SemanticClassification> categoryHAREM = []
	List<String> ontologyDBpediaClass =  []
	List<String> categoryWikipediaAsDBPediaResource = [] 
	
	
	void classifySubject(String lang, Map matchedStuffForSubject, Map matchedStuffForGeoscope) {
	   // println "matchedStuffForSubject = $matchedStuffForSubject matchedStuffForGeoscope=$matchedStuffForGeoscope"
	   // matchedStuffForSubject = [terms:[músicos], answer:3837] matchedStuffForGeoscope=[terms:[portugueses], answer:Portugal]
	    
	    // 3837 is the subjectid. answer is the DBpediaResource of the geoscope.
	    // steps: convert geoscope to GeoScope, then ground subject + geoscope into SubjectGround. 
	    // from this, get the WikipediaCategory, DBpediaClass, etc
	  
	    subjectTerms = matchedStuffForSubject?.terms
	    geoscopeTerms = matchedStuffForGeoscope?.terms
		    
	    SubjectDB sbj
	    if (matchedStuffForSubject) {
			sbj = SubjectDB.getFromID(matchedStuffForSubject.answer)
			if (sbj) subjects << sbj
	    }
	    log.debug "Got subjects: $subjects"
	
	    GeoscopeDB geo
	    if (matchedStuffForGeoscope) {
			EntityDB ent = EntityDB.getFromDBpediaResource(matchedStuffForGeoscope.answer)
			geo = ent.hasGeoscope()
			if (geo) geoscopes << geo
			if (geo.isAboveCountry()) {
				List<GeoscopeDB> country_childrens = geo.getCountryDescendents()
				country_childrens?.each{children -> 
					expanded_geoscopes << children
				}
			}	
	    }
	    log.debug "Got geoscopes: $geoscopes"

		// be prepared, we can have lots of subjects and lots of geoscopes (in an OR fashion)
		// if we have "Rivers of Europe" well ground it, let's use it no need to expand, SPARQL does a skos:broader
		// but if it's not grounded, use its expanded stuff like "Rivers_of_Portugal,etcQ
		// finally, there is no scope, let's ground it without
	    List<SubjectGroundDB> sgs = [] 
		subjects.each{s -> 
			if (geoscopes) {
				geoscopes.each{g -> 
					List<SubjectGroundDB> sgs2 = SubjectGroundDB.getFromSubjectIDAndGeoscopeID(s?.sbj_id, g?.geo_id)
					sgs2?.each{sgs << it}
				}
			} else if (expanded_geoscopes) {
				expanded_geoscopes.each{g -> 
					List<SubjectGroundDB> sgs2 = SubjectGroundDB.getFromSubjectIDAndGeoscopeID(s?.sbj_id, g?.geo_id)
					sgs2?.each{sgs << it}
				}
			} else {	
				List<SubjectGroundDB> sgs2 = SubjectGroundDB.getFromSubjectIDAndGeoscopeID(s?.sbj_id, null)
				sgs2?.each{sgs << it}
			}
		}
		// ok, let's try to ground first using SubjectGroundDB. 
	    sgs?.each{sg -> 
		//	println "Got SubjectGround: $sg"
			subjectgrounds << sg
			if (sg.sgr_dbpedia_class) {
		    	fillHAREMCategoriesFromDBpediaClass(sg.sgr_dbpedia_class) 
			}
			if (sg.sgr_dbpedia_resource) { // it's the Category:something
		    	categoryWikipediaAsDBPediaResource << sg.sgr_dbpedia_resource
			}
	    }   
	    log.debug "Got subjectgrounds: $sgs"

	    // find DBpedia stuff, plan B
	  // def ontologyClass = DBpediaOntologyDefinitionsPT.getAnswerFromNeedle(q2.subject)	
	}		
	
	private fillHAREMCategoriesFromDBpediaClass(String line) {
	    if (!line) return
				
	    if (dbpedia.isClass(line)) {
		log.trace("line ${line} is a DBpedia class.")
		def dbpediaclass = DBpediaOntology.getFullName(line)
		ontologyDBpediaClass << dbpediaclass
		def haremclass = dbpedia2sc.getClassificationFrom(dbpediaclass)
		log.trace  "Got HAREM classification ${haremclass} from that DBpedia class." 
		if (haremclass) categoryHAREM << haremclass
	}
	}
	
	public String toString() {
		return "sbj:${subjectTerms};geo:${geoscopeTerms};WK:$categoryWikipediaAsDBPediaResource;DB:$ontologyDBpediaClass"
	}
}	