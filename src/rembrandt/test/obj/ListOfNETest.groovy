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
import rembrandt.obj.ListOfNE
import rembrandt.obj.SemanticClassification
import rembrandt.obj.Sentence
import rembrandt.obj.ConflictPolicy
import rembrandt.obj.ClassificationCriteria
import rembrandt.obj.Term
import rembrandt.gazetteers.CommonClassifications as SC

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 * Tester for ListOfNE.
 */
public class TestListOfNE extends GroovyTestCase {
	
     ListOfNE NEs
     NE a, b, a_b, x, y, x_y
     Logger log = Logger.getLogger("RembrandtTest")
     	
     public TestListOfNE() {
	 a_b = new NE(terms:[new Term("A",0), new Term("B",1)], classification:[SC.unknown], sentenceIndex:0, termIndex:0, 
		 subalt:1, alt:"altA")
	 a = new NE(terms:[new Term("A",0)], classification:[SC.unknown], sentenceIndex:0, termIndex:0, subalt:2, alt:"altA")
         b = new NE(terms:[new Term("B",1)], classification:[SC.unknown], sentenceIndex:0, termIndex:1, subalt:2, alt:"altA")
	 x_y = new NE(terms:[new Term("X",0), new Term("Y",1)], classification:[SC.unknown], sentenceIndex:1, termIndex:0,
		 subalt:1, alt:"altX")
	 x = new NE(terms:[new Term("X",0)], classification:[SC.unknown], sentenceIndex:1, termIndex:0, subalt:2, alt:"altX")
         y = new NE(terms:[new Term("Y",1)], classification:[SC.unknown], sentenceIndex:1, termIndex:1, subalt:2, alt:"altX")
 
	 List<NE> nes = [y, b, a, x_y, a_b, x]
	 NEs = new ListOfNE(nes)    
     }
	
     /** 
      * test for:
      * rembrandt.obj.ListOfNE.ListOfNE(List<NamedEntity>)
      *      rembrandt.obj.ListOfNE.ListOfNE()
      *      rembrandt.obj.ListOfNE.containsNE(NamedEntity)
      *      rembrandt.obj.ListOfNE.sortNEs()
      *      rembrandt.obj.ListOfNE.labelNEs(String)
      *      rembrandt.obj.ListOfNE.labelNEs()
      */
     void testBasicListOfNE() {
	 
	 log.debug "Testing ListOfNE basics..."
		 
	 assert NEs.contains(a)
	 assert NEs.indexOf(a) == 2
	  
	 // sortNEs
	 NEs.sortNEs()
	 assert (List<NE>)NEs.toArray() == [a_b, a, b, x_y, x, y]
	        
	 // labelNEs
	assert NEs[0].id == null
	NEs.labelNEs()
	assert NEs[0].id == "0"
	NEs.labelNEs("pre")
	assert NEs[0].id == "pre-0"
	    
     }
     /** 
      * test for 
      * rembrandt.obj.ListOfNE.removeNEs(Object)
      *  rembrandt.obj.ListOfNE.generateALT(NamedEntity, List<NamedEntity>, String)
     rembrandt.obj.ListOfNE.generateALT(NamedEntity, List<NamedEntity>)
     rembrandt.obj.ListOfNE.addNEs(Object, ConflictPolicy, String)
     rembrandt.obj.ListOfNE.addNEs(Object, ConflictPolicy)
     rembrandt.obj.ListOfNE.addNEs(Object)
      */
     void testAddAndRemoveNEs() {
	 
	 NEs.sortNEs() 
	 NEs.removeNEs(a_b)
	 assert (List<NE>)NEs.toArray() == [a, b, x_y, x, y], "Got ${NEs.toArray()} instead."// no a_b
	 NEs.removeNEs([b,a])
	 assert (List<NE>)NEs.toArray() == [x_y, x, y], "Got ${NEs.toArray()} instead."// no a and b
	 
	 // generateALT: let's clean the ALT info, just to see how it works
	 a_b.alt = null; a.alt = null; b.alt = null;
	 a_b.subalt = -1; a.subalt = -1; b.subalt = -1;
	 
	 NEs.generateALT(a_b, [a, b])
	 
	 assert NEs[NEs.indexOf(a_b)].alt != null
	 assert NEs[NEs.indexOf(a_b)].subalt == 1
	 assert NEs[NEs.indexOf(a)].alt != null
	 assert NEs[NEs.indexOf(a)].subalt == 2

        // TODO
    /*  addNEs: avaliable policies:
     * ConflictPolicy.CourtBattle
     * ConflictPolicy.JustAdd
     * ConflictPolicy.Overwrite
     * ConflictPolicy.OverwriteWithBigger
     * ConflictPolicy.WriteIfNoExistingNEOverlapping
     * ConflictPolicy.GenerateALT
     */
     }
     
     /** 
      * test for:
      *      rembrandt.obj.ListOfNE.getNEsBySentenceIndex(int)
     rembrandt.obj.ListOfNE.getNEsByAltId(Object)
     rembrandt.obj.ListOfNE.getNEbyID(Object)
     closure rembrandt.obj.ListOfNE.fetchByStartingTerm
     closure rembrandt.obj.ListOfNE.fetchByOverlappingTerm
     rembrandt.obj.ListOfNE.getNEsBySentenceAndTermIndex(int, int, Closure)
     rembrandt.obj.ListOfNE.getNEsBySentenceAndTermIndexAndAlt(int, int, String, int, Closure)
     rembrandt.obj.ListOfNE.getNEsBySentenceAndTermIndexAndClassification(int, int, Object, Closure, Object)
     rembrandt.obj.ListOfNE.getNEsBySentenceAndTermIndexAndClassification(int, int, Object, Closure)
     rembrandt.obj.ListOfNE.getNEsByClassification(Object, Object)
     rembrandt.obj.ListOfNE.getNEsByClassification(Object)
      */
     void testGetNEs() {
	 
	 NEs.sortNEs() 
	 log.debug "Testing getting NEs from ListOfNE..."
	 
	 assert [a_b, a, b] == NEs.getNEsBySentenceIndex(0)
	 assert [] == NEs.getNEsBySentenceIndex(2)
	 
	 assert [a_b, a, b] == NEs.getNEsByAltId("altA")
	 
	 NEs[2].id = "AAA"
	 NE ne = NEs.getNEbyID("AAA")
	 assert 2 == NEs.indexOf(ne)     
	 
	 assert [a_b, a] == NEs.getNEsBySentenceAndTermIndex(0, 0, NEs.fetchByStartingTerm)
	 assert [a_b, a] == NEs.getNEsBySentenceAndTermIndex(0, 0, NEs.fetchByOverlappingTerm)
	 assert [b] == NEs.getNEsBySentenceAndTermIndex(0, 1, NEs.fetchByStartingTerm)
	 assert [a_b, b] == NEs.getNEsBySentenceAndTermIndex(0, 1, NEs.fetchByOverlappingTerm)
	 // getNEsBySentenceAndTermIndexAndAlt will get same results
	 
	// TODO the rest
     }
    
     /* test for rembrandt.obj.ListOfNE.executeVeredicts(List<CourtVeredict>)
      * 
      */

    
    /*
     *  test for rembrandt.obj.ListOfNE.addRelation(Object)
     */
   
}