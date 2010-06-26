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
 
package rembrandt.rules

import org.apache.log4j.Logger
import rembrandt.obj.Rule
import rembrandt.obj.ListOfNE
import rembrandt.obj.Sentence
import rembrandt.obj.NamedEntity
import rembrandt.obj.EntityRelation
/**
 * @author Nuno Cardoso
 * 
 * Class for entity relation detection rules
 *
 */
public class EntityRelationDetector extends Detector {

    static Logger erlog = Logger.getLogger("EntityRelationMain")
    /**
     * This method detects entity relations from a set of entity relation rules.
  	* @param NEs List of two NEs to establish a relation.
  	* @param sentence  A sentence where they are located.
  	* @param rules List of rules for mining the NEs and sentences.
  	* @return Map of relations to add, in [sourceNE:X, targetNE:y, relationType:Z]Êformat
  	*/
     public detectEntityRelation(NamedEntity ne1, NamedEntity ne2, Sentence s, List<Rule> rules = rules) { 
         
	// this is a BROWSE_SENTENCE STRATEGY, RETURN_FIRST_MATCH no optimization.
		 
         for (rule in rules) {
             
             s.resetPointerToFirstVisibleTerm()
     
             while (s.thereAreVisibleTermsAhead()) {
               
          	 MatcherObject o = matchRule( new MatcherObject(rule:rule, 
  	          sentence:s, NEs:new ListOfNE([ne1, ne2])))
  	        		
  	          if (o) {
  	              performActionsOnMatcherObject(o) 
                      // just to make sure I change them...
                      ne1 = o.NEs[0]
                      ne2 = o.NEs[1]
  	              return // no need to go on other rules
  	          }
        	 //movePointerForVisibleTerms
                  s.movePointerForVisibleTerms()
             }   
  	}// each rule
	return 
     } // end method detect

        
    public performActionsOnMatcherObject(MatcherObject o, closure = null) {
	 if (o.rule.action) o.rule.action.each{c -> 
            c(o, o.NEs[0], o.NEs[1]) }
        else addRelationToNEs(o, o.NEs[0],o.NEs[1])
        return
    }        
    
    
   // note: the named entities ne1 and ne2 are references to NEs in a ListOfNE 
   public Closure addRelationToNEs = {MatcherObject o, NamedEntity ne1, NamedEntity ne2 -> 
    
      String targetid = null
      String sourceid = null
      String relationType = null 
    
      o.pastMatches.each{clause, match -> 
      
   	if (clause.role) {
    	    switch (clause.role) {
	        case EntityRelation.CollectID:
	    	   // this is the TARGETED entity
                   if (ne1 == match.nes[0]) targetid = "ne1"
                   if (ne2 == match.nes[0]) targetid = "ne2"            
	    	break
	    	       
	    	case EntityRelation.SetIdentity:
	    	   relationType = EntityRelation.SetIdentity.text 
	    	   if (ne1 == match.nes[0]) sourceid = "ne1"
	    	   if (ne2 == match.nes[0]) sourceid = "ne2" 	   
		break
	    		   
	    	case EntityRelation.SetIncludedBy:
	    	    relationType = EntityRelation.SetIncludedBy.text 
                if (ne1 == match.nes[0]) sourceid = "ne1"
                if (ne2 == match.nes[0]) sourceid = "ne2" 	  
	    	break
	    	       
	    	case EntityRelation.SetIncludes:
	    	    relationType = EntityRelation.SetIncludes.text
                if (ne1 == match.nes[0]) sourceid = "ne1"
                if (ne2 == match.nes[0]) sourceid = "ne2" 	  
	        break
	    		   
	    	case EntityRelation.SetBasedOn:
	    	    relationType = EntityRelation.SetBasedOn.text 
                if (ne1 == match.nes[0]) sourceid = "ne1"
                if (ne2 == match.nes[0]) sourceid = "ne2" 	  
	    	break
	    		   
	    	case EntityRelation.SetOccursOn:
	    	    relationType = EntityRelation.SetOccursOn.text 
                if (ne1 == match.nes[0]) sourceid = "ne1"
                if (ne2 == match.nes[0]) sourceid = "ne2" 	  
	    	break
		    		   
	    	case EntityRelation.SetOther:
	    	    relationType = EntityRelation.SetOther.text 
                if (ne1 == match.nes[0]) sourceid = "ne1"
                if (ne2 == match.nes[0]) sourceid = "ne2" 	  
	    	break
	    }//switch  
    	}// clause.role != null*/
    }// clausesMatched
    if (targetid =="ne1" && sourceid =="ne2") {
       ne2.addRelation(ne1, relationType) 
    } else if (targetid =="ne2" && sourceid =="ne1") {
        ne1.addRelation(ne2, relationType) 
    } else 
	throw new IllegalStateException("Relation detection got a problem.")
  
    }// closure
}
