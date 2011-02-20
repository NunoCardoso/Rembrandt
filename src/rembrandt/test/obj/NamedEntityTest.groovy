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
 
package rembrandt.test.obj

import rembrandt.obj.NamedEntity as NE
import rembrandt.obj.Sentence
import rembrandt.obj.Term
import rembrandt.obj.ClassificationCriteria as CC
import rembrandt.obj.BoundaryCriteria as BC
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.CommonClassifications as SC
import org.apache.log4j.Logger
import org.junit.*
import org.junit.runner.*

/**
 * @author Nuno Cardoso
 * Tester forNE.
 * Nuno, keep the tested method names in the comments, for a nice, neat and organized file. Thanks. Nuno. 
 */
public class TestNE extends GroovyTestCase {
	
     Logger log = Logger.getLogger("RembrandtTest")
     
     public TestNE() {}
	
     /** test for Relations
      * addRemation(NE, String)
      * hasRelationWith(NE)
      * hasRelationOfType(NE, String)
      * removeRelation(NE)
      */
     void testAddRelation() {
	 log.info "Testing relations..."
	 NE ne_rembrandt = new NE(id:"1", terms:Sentence.simpleTokenize("Rembrandt"))
	 NE ne_paisesbaixos = new NE(id:"2", terms:Sentence.simpleTokenize("Países Baixos"))
	 String nasceu_em = "NasceuEm"
	 String morreu_em = "MorreuEm"    
	 
	 ne_rembrandt.addRelation(ne_paisesbaixos, nasceu_em)
	 assert ne_rembrandt.corel.containsKey(ne_paisesbaixos.id)
	 assert ne_rembrandt.corel.values().contains(nasceu_em)
	 
	 assert ne_rembrandt.hasRelationWith(ne_paisesbaixos)
	 assert ne_paisesbaixos.hasRelationWith(ne_rembrandt), "relations paisesbaixos = ${ne_paisesbaixos.corel} rembrandt = ${ne_rembrandt.corel}" // is transitive
 	 
	 assert ne_rembrandt.hasRelationOfType(ne_paisesbaixos, nasceu_em)
	 assert ne_paisesbaixos.hasRelationOfType(ne_rembrandt, nasceu_em) // relações são transitivas
	 assert !ne_rembrandt.hasRelationOfType(ne_paisesbaixos, morreu_em)	 
	 
	 
     }
     
     /** test internal works (clone, equals, returns, etc)
      *  clone()
      *  clonePositionAndTerms()
      *  equals(NE)
      *  equalsTerms(NE)
      *  asSentence()
      */
     void testClonageEqualsAndReturns() {
	 log.info "Testing clonage..."
	 NE ne_rembrandt = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.person_individual])
	 ne_rembrandt.wikipediaPage[SC.person_individual] =["http://pt.wikipedia.org/wiki/Rembrandt"]
	 ne_rembrandt.dbpediaPage[SC.person_individual]=["http://dbpedia.org/resource/Rembrandt"]

	 log.info "Testing equals..."	 
	NE ne_rembrandt_clone1 = ne_rembrandt.clone()
	assert ne_rembrandt.equals(ne_rembrandt_clone1)
	assert ne_rembrandt.equalsTerms(ne_rembrandt_clone1)
		
	NE ne_rembrandt_clone2 = ne_rembrandt.clonePositionAndTerms()
	assert ! ne_rembrandt.equals(ne_rembrandt_clone2) // cloning just position and terms, so it's not the same NE
	assert ne_rembrandt.equalsTerms(ne_rembrandt_clone2)
	
	 log.info "Testing Sentence..."
	 Sentence s = new Sentence([new Term("Rembrandt",1)], 1)
	 assert s == ne_rembrandt.asSentence()
     }
     
     /**
      * Test operations with terms
      * hasTermIndex(int)
      * termsMatching(Pattern)
      * partialTermMatch(NE)
      * overlapAtLeastOneTerm(NE)
      * printTerms(String)
      * printTerms()
      */
     void testTermsOperations() {
	 
	 log.info "Testing term operations..."
	 NE ne_rembrandt = new NE(id:"1", 
		 terms:[new Term("Rembrandt",1), new Term("Harmenszoon",2), new Term("van",3), new Term("Rijn",4)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.person_individual])

	 assert ne_rembrandt.hasTermIndex(2)
	 assert !ne_rembrandt.hasTermIndex(0)
	 
	 assert ne_rembrandt.termsMatching(~/an/)*.index == [] // only complete term matches
	 assert ne_rembrandt.termsMatching(~/.*an.*/)*.index == [1,3] // rembrANdt, vAN
	 assert ne_rembrandt.termsMatching(~/abc/) == []

	 NE ne_rembrandt2 = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1)
      	 NE ne_harmenszoon1 = new NE(id:"1", terms:[new Term("Harmenszoon",1)],
      		sentenceIndex:1, termIndex:1)
      	 NE ne_harmenszoon2 = new NE(id:"1", terms:[new Term("Harmenszoon",2)])
       	 NE ne_abc = new NE(id:"1", terms:[new Term("ABC",1)])

	 assert ne_rembrandt.partialTermMatch(ne_rembrandt2)                                         
	 assert ne_rembrandt.partialTermMatch(ne_harmenszoon1)   // don't care about ter mindexes, uses only term text                                    
	 assert ne_rembrandt.partialTermMatch(ne_harmenszoon2)                                         
	
	 assert ne_rembrandt.overlapAtLeastOneTerm(ne_rembrandt2)
	 assert ne_rembrandt.overlapAtLeastOneTerm(ne_harmenszoon1) // uses indexes, not terms
	 
     }
     
     /** 
      * Test classification operations, but not matches
      * hasUnknownClassification()
      * removeUnknownClassification()
      * getKnownClassifications()
      * replaceClassificationFrom(NE)
      * mergeClassification(SemanticClassification)
      */
     void testClassificationOperations() {
	 
	 log.info("Testing classification operations...")
	 NE ne_unknown = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.unknown])

	 NE ne_rembrandt = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.person_individual])
	 ne_rembrandt.wikipediaPage[SC.person_individual] =["http://pt.wikipedia.org/wiki/Rembrandt"]
	 ne_rembrandt.dbpediaPage[SC.person_individual]=["http://dbpedia.org/resource/Rembrandt"]

      	 NE ne_paisesbaixos = new NE(id:"2", terms:[new Term("Países",3),new Term("Baixos",4)], 
     		 sentenceIndex:1, termIndex:3, classification:[SC.place_human_country])
       	 ne_rembrandt.wikipediaPage[SC.place_human_country] =["http://pt.wikipedia.org/wiki/Países_Baixos"]
      	 ne_rembrandt.dbpediaPage[SC.place_human_country]=["http://dbpedia.org/resource/Netherlands"]     	 
    
      	 assert ne_unknown.hasUnknownClassification()
      	 assert !ne_rembrandt.hasUnknownClassification()
      	 
      	 assert ne_unknown.getKnownClassifications() == []
      	 assert ne_rembrandt.getKnownClassifications() == ne_rembrandt.classification  	                                      	                                         	                                                
      	 
      	 ne_unknown.removeUnknownClassification()
      	 assert ne_unknown.classification == [] // removed the EM
      	 ne_unknown.classification << SC.unknown // let's restore it

      	 ne_rembrandt.removeUnknownClassification()
      	 assert ne_rembrandt.classification == [SC.person_individual] // still the same
      	                                        
      	// replace classification - note that the equals() doesn't use the wikipediaPage / dbpediaPage info, so they are equal now                                         
      	 ne_unknown.replaceClassificationFrom(ne_rembrandt)
      	 assert ne_unknown.equals(ne_rembrandt)
      	 ne_unknown.classification = [SC.unknown] // let's restore it
      	    
      	log.info "Testing merging classifications...."
      	// merge classifications - note that merge automatically removes the unknown classification, case it exists                         
      	 ne_unknown.mergeClassificationFrom(ne_rembrandt)
      	 assert ne_unknown.classification == [SC.person_individual]
      	 
      	// adding a broader SC or a matching SC doesn't do nothing... 
      	// note:  if (veredicts.matches(~/EQUAL/) || veredicts.matches(~/NARROWER/)) return
      	 
      	ne_unknown.mergeClassification(SC.person)
        assert ne_unknown.classification == [SC.person_individual]
       	ne_unknown.mergeClassification(SC.person_individual)  	
        assert ne_unknown.classification == [SC.person_individual]
      	                                      
        // different ones go well                                     
        ne_unknown.mergeClassification(SC.place_human)
        assert ne_unknown.classification == [SC.person_individual, SC.place_human], "Got ${ne_unknown.classification} instead"
        // merging a narrower one   
        ne_unknown.mergeClassification(SC.place_human_division)
        assert ne_unknown.classification == [SC.person_individual, SC.place_human_division], "Got ${ne_unknown.classification} instead"
      	                                      
        // adding a different in type and subtype   
        ne_unknown.mergeClassification(SC.place_physical)
        assert ne_unknown.classification == [SC.person_individual, SC.place_human_division, SC.place_physical], "Got ${ne_unknown.classification} instead"
        ne_unknown.mergeClassification(SC.place_human_country)
        assert ne_unknown.classification == [SC.person_individual, SC.place_human_division, SC.place_physical, SC.place_human_country ], "Got ${ne_unknown.classification} instead"
        
        // note that classification is insertion-ordered
     }
     
     /*   test for matchesBoundaries(NE, BoundaryCriteria) 
      */
     void testMatchBoundaries() {
	 
	 log.info "Testing boundary criteria matches...."
 
	 NE ne_1234 = new NE(terms:Sentence.simpleTokenize("1 2 3 4", 0, 1), sentenceIndex:0, termIndex:1)
	 NE ne_1234_2 = new NE(terms:Sentence.simpleTokenize("1 2 3 4", 0, 1), sentenceIndex:0, termIndex:1)
	 NE ne_123 = new NE(terms:Sentence.simpleTokenize("1 2 3", 0, 1), sentenceIndex:0, termIndex:1)
	 NE ne_234 = new NE(terms:Sentence.simpleTokenize("2 3 4", 0, 2), sentenceIndex:0, termIndex:2)
	 NE ne_23 = new NE(terms:Sentence.simpleTokenize("2 3", 0, 2), sentenceIndex:0, termIndex:2)
	 NE ne_345 = new NE(terms:Sentence.simpleTokenize("3 4 5", 0, 3), sentenceIndex:0, termIndex:3)
	 
         assert ne_1234.matchesBoundaries(ne_1234_2, BC.ExactMatch)
         
         assert ne_23.matchesBoundaries(ne_1234, BC.IsContainedByAndCenterJustified)
         assert ne_123.matchesBoundaries(ne_1234, BC.IsContainedByAndLeftJustified)
         assert ne_234.matchesBoundaries(ne_1234, BC.IsContainedByAndRightJustified)
         assert ne_23.matchesBoundaries(ne_1234, BC.IsContainedBy)
         assert ne_123.matchesBoundaries(ne_1234, BC.IsContainedBy)
         assert ne_234.matchesBoundaries(ne_1234, BC.IsContainedBy) 
         
         assert ne_1234.matchesBoundaries(ne_23, BC.ContainsAndCenterJustified)
         assert ne_1234.matchesBoundaries(ne_123, BC.ContainsAndLeftJustified)
         assert ne_1234.matchesBoundaries(ne_234, BC.ContainsAndRightJustified)
         assert ne_1234.matchesBoundaries(ne_23, BC.Contains)
         assert ne_1234.matchesBoundaries(ne_123, BC.Contains)
         assert ne_1234.matchesBoundaries(ne_234, BC.Contains) 

         assert ne_1234.matchesBoundaries(ne_1234, BC.ExactOrContains)
         assert ne_1234.matchesBoundaries(ne_23, BC.ExactOrContains)
         assert ne_1234.matchesBoundaries(ne_1234, BC.ExactOrIsContainedBy)
         assert ne_23.matchesBoundaries(ne_1234, BC.ExactOrIsContainedBy)
 
         assert ne_234.matchesBoundaries(ne_345, BC.Overlapping) 
     }
     
     /*   
      * test for 
      * matchesClassification(NE, ClassificationCriteria) 
      * matchesClassification(List<SemanticClassification>, ClassificationCriteria)
      */
     void testMatchClassifications() {
	 
	 log.info "Testing classification criteria matches...."
   
	 NE ne_unknown = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.unknown])

	 NE ne_person = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.person])

	 NE ne_personindividual = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.person_individual])

	 NE ne_personindividual_placehuman = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.person_individual, SC.place_human])

	 NE ne_place = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.place])
      	                                                   
	 NE ne_placehuman = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.place_human])

	 NE ne_placehuman_placephysical = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.place_human,SC.place_physical])

	 NE ne_placehumandivision = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.place_human_division])

	 NE ne_organization = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.organization])
	
	 NE ne_place_organization = new NE(id:"1", terms:[new Term("Rembrandt",1)], 
		 sentenceIndex:1, termIndex:1, classification:[SC.place, SC.organization])

	 /** allows any category. It doesn't care about what we put in the first argument */
	 assert ne_unknown.matchesClassification(null, CC.AnyKnownOrUnknownCategory)
	 assert ne_personindividual.matchesClassification(null, CC.AnyKnownOrUnknownCategory)
	 assert ne_personindividual_placehuman.matchesClassification(null, CC.AnyKnownOrUnknownCategory)
	 
	 /** allows any known category. It doesn't care about what we put in the first argument */
	 assert ! ne_unknown.matchesClassification(null, CC.AnyKnownCategory) // this one fails now.
	 assert ne_personindividual.matchesClassification(null, CC.AnyKnownCategory)
	 assert ne_personindividual_placehuman.matchesClassification(null, CC.AnyKnownCategory)
	 
	/************************************
	 * Let's test AllOfThese, AllOfThem *
	 ************************************/ 	 		
	 assert ne_placehuman_placephysical.matchesClassification([SC.place], CC.AllOfThese, CC.AllOfThem, CC.Category)
	 assert ne_placehuman_placephysical.matchesClassification([SC.place_human, SC.place_physical], CC.AllOfThese, CC.AllOfThem, CC.Category)
	 assert ! ne_placehuman_placephysical.matchesClassification([SC.place_human], CC.AllOfThese, CC.AllOfThem, CC.Type) // misses the physical one
	 
	 // why this fails? human matches human, physics matches physics, but human does not matches physics and physics does not matches human in the matrix 
	 // if you want this to be true, you should either use {CC.AllOfThese, CC.ExistsAtLeastOneOfThem} or {CC.ExistsAtLeastOneOfThese, CC.AllOfThem}!!
	 assert ! ne_placehuman_placephysical.matchesClassification([SC.place_human, SC.place_physical], CC.AllOfThese, CC.AllOfThem, CC.Type) 
	 // See? This one is true!
	 assert ne_placehuman_placephysical.matchesClassification([SC.place_human, SC.place_physical], CC.AllOfThese, CC.ExistsAtLeastOneOfThem, CC.Type) 

	 // fails - place_human is not matched!
	 assert !ne_placehuman_placephysical.matchesClassification([SC.place, SC.place_physical], CC.AllOfThese, CC.ExistsAtLeastOneOfThem, CC.Type) 
	 
	/**************************************************************
	 * Let's test ExistsAtLeastOneOfThese, ExistsAtLeastOneOfThem *
	 **************************************************************/ 	 		

	 // the match person_individual saves the day here!
	 assert ne_personindividual_placehuman.matchesClassification([SC.person_individual, SC.organization_company], CC.ExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Type) 
         assert ne_placehuman.matchesClassification([SC.place_human], CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Type) 
	  	                        
	/*************************************************************
	 * Let's test NeverAllOfThese, NeverAllOfThem                *
	 * NeverExistsAtLeastOneOfThese, NeverExistsAtLeastOneOfThem *
	 *************************************************************/ 	
	 // Good for "do not match this one"
	 assert !ne_person.matchesClassification([SC.person_individual], CC.AllOfThese, CC.NeverAllOfThem, CC.Category) 
	 assert !ne_personindividual.matchesClassification([SC.person_individual], CC.AllOfThese, CC.NeverAllOfThem, CC.Type) 
	 assert ne_place.matchesClassification([SC.person_individual], CC.AllOfThese, CC.NeverAllOfThem, CC.Category) 

	 // the NE can't be either place nor person - Good for "do not match this one OR one of these"  
	 assert !ne_person.matchesClassification([SC.person_individual], CC.NeverExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category) 
	 assert !ne_person.matchesClassification([SC.person_individual, SC.place_human], CC.NeverExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category) 
	 assert !ne_personindividual.matchesClassification([SC.person_individual, SC.place_human], CC.AllOfThese, CC.NeverExistsAtLeastOneOfThem, CC.Category) 
	 assert !ne_place.matchesClassification([SC.person_individual, SC.place_human], CC.AllOfThese, CC.NeverExistsAtLeastOneOfThem, CC.Category) 
	 assert !ne_personindividual_placehuman.matchesClassification([SC.person_individual, SC.place_human], CC.NeverExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category)	 
	 assert ne_organization.matchesClassification([SC.person_individual, SC.place_human], CC.NeverExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category) 
	 assert !ne_place_organization.matchesClassification([SC.person_individual, SC.place_human], CC.NeverExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category) 
	 
	 // The NE can't be only on this category - Good for NEs that have more than one semanticClassification, and need a disambiguation
	 assert !ne_person.matchesClassification([SC.person_individual], CC.NeverAllOfThese, CC.AllOfThem, CC.Category) 
	 assert !ne_personindividual.matchesClassification([SC.person_individual], CC.NeverAllOfThese, CC.AllOfThem, CC.Category) 
	 assert ne_place.matchesClassification([SC.person_individual], CC.NeverAllOfThese, CC.AllOfThem, CC.Category) 
	 assert ne_placehuman.matchesClassification([SC.person_individual], CC.NeverAllOfThese, CC.AllOfThem, CC.Category)	 
	 assert ne_place_organization.matchesClassification([SC.person_individual], CC.NeverAllOfThese, CC.AllOfThem, CC.Category) 
	 assert !ne_personindividual.matchesClassification([SC.person_individual], CC.NeverAllOfThese, CC.AllOfThem, CC.Category)	 

     }
     
     /**
     * test for disambiguateClassificationFrom(SemanticClassification)
    */
     void testDisambiguateClassificationFrom() {
	 
      	 NE ne_place_human_country = new NE(id:"2", terms:[new Term("Países",3),new Term("Baixos",4)], 
     		 sentenceIndex:1, termIndex:3, classification:[SC.place_human_country])
      	 ne_place_human_country.wikipediaPage[SC.place_human_country] = ["http://pt.wikipedia.org/wiki/Países_Baixos"]
      	 ne_place_human_country.dbpediaPage[SC.place_human_country]= ["http://dbpedia.org/resource/Netherlands"]     	 
  
      	NE ne_confused = new NE(id:"2", terms:[new Term("Países",3),new Term("Baixos",4)], 
      	sentenceIndex:1, termIndex:3, classification:[SC.place_human_country, SC.place_human_division, SC.person_individual])
      	ne_confused.wikipediaPage[SC.place_human_country] = ["http://pt.wikipedia.org/wiki/Países_Baixos_(country)"]
      	ne_confused.dbpediaPage[SC.place_human_country]= ["http://dbpedia.org/resource/Netherlands_(country)"]     	 
        ne_confused.wikipediaPage[SC.place_human_division] = ["http://pt.wikipedia.org/wiki/Países_Baixos_(division)"]
      	ne_confused.dbpediaPage[SC.place_human_division]= ["http://dbpedia.org/resource/Netherlands_(division)"]     	 
      	ne_confused.wikipediaPage[SC.person_individual] = ["http://pt.wikipedia.org/wiki/Países_Baixos_(person)"]
      	ne_confused.dbpediaPage[SC.person_individual]= ["http://dbpedia.org/resource/Netherlands_(person)"]     	 
      	                                                                                            	                                             	                                                	                                                	                                                                    	                                                                                            	                                             	                                                	                                                	                                                              
       	// disambiguate ne_place_human_country with place, place_human or place_human_country will keep it untouched.   
      	 ne_place_human_country.disambiguateClassificationFrom(SC.place)
      	 assert ne_place_human_country.classification == [SC.place_human_country]
      	 ne_place_human_country.disambiguateClassificationFrom(SC.place_human)
      	 assert ne_place_human_country.classification == [SC.place_human_country]
      	 ne_place_human_country.disambiguateClassificationFrom(SC.place_human_country)
      	 assert ne_place_human_country.classification == [SC.place_human_country]
      	                                                  
      	 NE ne_place_human_country2 = ne_place_human_country.clone()   
      	 assert ne_place_human_country2.classification == [SC.place_human_country]
      	 ne_place_human_country2.disambiguateClassificationFrom(SC.organization)       
     	 assert ne_place_human_country2.classification == [] // note that we only disambiguate. Applications should catch empty classifications and replace it
      	 
      	 // Now, for some NEs that have multiple classifications
     	 NE ne_confused2 = ne_confused.clone()
     	 ne_confused2.disambiguateClassificationFrom(SC.place)
    	 assert ne_confused2.classification == [SC.place_human_country, SC.place_human_division]    
    	 assert ne_confused2.wikipediaPage.size() == 2                               
   	 ne_confused2.disambiguateClassificationFrom(SC.place_human)
    	 assert ne_confused2.classification == [SC.place_human_country, SC.place_human_division]    
    	 assert ne_confused2.wikipediaPage.size() == 2                               
  	 ne_confused2.disambiguateClassificationFrom(SC.place_human_country)
    	 assert ne_confused2.classification == [SC.place_human_country]    
    	 assert ne_confused2.wikipediaPage.size() == 1      
    	 
    	 // Note that disambiguation also fills out the broader NEs.
    	 NE ne_place = new NE(id:"2", terms:[new Term("Países",3),new Term("Baixos",4)], 
     		 sentenceIndex:1, termIndex:3, classification:[SC.place])
      	 ne_place.wikipediaPage[SC.place] = ["http://pt.wikipedia.org/wiki/Países_Baixos"]
 	 
      	 ne_place.disambiguateClassificationFrom(SC.place_human_country)
      	 assert ne_place.classification == [SC.place_human_country]
      	 assert ne_place.wikipediaPage.size() == 1
      	 assert ne_place.wikipediaPage.keySet().toList().getAt(0).equals(SC.place), \
      	 	"Got "+ne_place.wikipediaPage+" instead."
    }


   /**
     * test for removeClassification(SemanticClassification)
    */
     void testRemoveClassification() {
	 	
			 NE ne
			
      	 NE ne_place_human_country = new NE(id:"2", terms:[new Term("Países",3),new Term("Baixos",4)], 
     		 sentenceIndex:1, termIndex:3, classification:[SC.place_human_country])
      	 ne_place_human_country.wikipediaPage[SC.place_human_country] = ["http://pt.wikipedia.org/wiki/Países_Baixos"]
      	 ne_place_human_country.dbpediaPage[SC.place_human_country]= ["http://dbpedia.org/resource/Netherlands"]     	 
  
      	NE ne_confused = new NE(id:"2", terms:[new Term("Países",3),new Term("Baixos",4)], 
      	sentenceIndex:1, termIndex:3, classification:[SC.place_human_country, SC.place_human_division, SC.person_individual])
      	ne_confused.wikipediaPage[SC.place_human_country] = ["http://pt.wikipedia.org/wiki/Países_Baixos_(country)"]
      	ne_confused.dbpediaPage[SC.place_human_country]= ["http://dbpedia.org/resource/Netherlands_(country)"]     	 
			ne_confused.wikipediaPage[SC.place_human_division] = ["http://pt.wikipedia.org/wiki/Países_Baixos_(division)"]
      	ne_confused.dbpediaPage[SC.place_human_division]= ["http://dbpedia.org/resource/Netherlands_(division)"]     	 
      	ne_confused.wikipediaPage[SC.person_individual] = ["http://pt.wikipedia.org/wiki/Países_Baixos_(person)"]
      	ne_confused.dbpediaPage[SC.person_individual]= ["http://dbpedia.org/resource/Netherlands_(person)"]     	 
      	                                                                                            	                                             	                                                	                                                	                                                              
 			// I have to clone it, because I'm deleting classifications, I want to keep an original 
      	 ne = ne_place_human_country.clone()
			 ne.removeClassification(SC.place)
      	 assert ne.classification == []
      	 ne = ne_place_human_country.clone()
			 ne.removeClassification(SC.place_human)
      	 assert ne.classification == []
      	 ne = ne_place_human_country.clone()
			 ne.removeClassification(SC.place_human_country)
      	 assert ne.classification == []
      	                                                  
      	 ne = ne_place_human_country.clone()   
      	 assert ne.classification == [SC.place_human_country]
      	 ne.removeClassification(SC.organization)       
     	 	 assert ne.classification == [SC.place_human_country] 
      	 
      	 // Now, for some NEs that have multiple classifications
     	 ne = ne_confused.clone()
     	 ne.removeClassification(SC.place)
    	 assert ne.classification == [SC.person_individual]    
    	 assert ne.wikipediaPage.size() == 1                               
   	 
		 
		 ne = ne_confused.clone()
		 println ne
		 ne.removeClassification(SC.place_human)
    	 println ne
		 assert ne.classification == [SC.person_individual]    
    	 assert ne.wikipediaPage.size() == 1
                              
  	 	 ne = ne_confused.clone()
		 ne.removeClassification(SC.place_human_country)
    	 assert ne.classification == [SC.place_human_division, SC.person_individual]    
    	 assert ne.wikipediaPage.size() == 2      
    	 
    	 NE ne_place = new NE(id:"2", terms:[new Term("Países",3),new Term("Baixos",4)], 
     		 sentenceIndex:1, termIndex:3, classification:[SC.place])
      	 ne_place.wikipediaPage[SC.place] = ["http://pt.wikipedia.org/wiki/Países_Baixos"]
 	 
      	 ne_place.removeClassification(SC.place_human_country)
      	 assert ne_place.classification == [SC.place]
      	 assert ne_place.wikipediaPage.size() == 1
      	 assert ne_place.wikipediaPage.keySet().toList().getAt(0).equals(SC.place), \
      	 	"Got "+ne_place.wikipediaPage+" instead."
    }
      
     /**
      * remaining methods: 
      * lastTokenNumber()
     * printHistory()
     * reportToHistory(String)
     * replaceAdditionalInfoFrom(NE)
     */
}

