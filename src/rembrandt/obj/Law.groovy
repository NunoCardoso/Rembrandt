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
 
package rembrandt.obj

import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 *  
 * Laws are the basis for actions of the Courthouse. When the plaintiff NE and 
 * defendant NEs match the evidences, the action contained in this law is used 
 * by the Courthouse to generate CourtActions.
 */
class Law {

  static Logger log = Logger.getLogger("Courthouse")
 
  List defendantCriteria
  List plaintiffCriteria

  List defendantEvidence
  List plaintiffEvidence
  
  String id
  String description
  CourtDecision decision
    
  /**
   * checks if this law applies to the given named entities.
   * @param defendant defendant NE.
   * @param plaintiff plaintiff NE.
   */
  public boolean matchesEvidence(NamedEntity defendant, NamedEntity plaintiff) {
 
     /* as it is required that both defendant and plaintiff to match all 
      * criteria, we assume the law matches, unless some criteria fails.
      */
       boolean matches = true  

       log.trace "Law dispute between defendant ${defendant} and plaintiff ${plaintiff}"
      
       
       plaintiffCriteria?.eachWithIndex {criteria, i -> 
          log.trace "plaintiff criteria #${i}: ${criteria}"
	
          switch(criteria) {
			
          case List :
				  
              matches = matches && plaintiff.matchesClassification(plaintiffEvidence?.getAt(i), criteria)
              log.trace "Law ${id}: Did criteria ${criteria}, evidence=${plaintiffEvidence?.getAt(i)} matches=${matches}"
              break
	
          case BoundaryCriteria:
              matches = matches && plaintiff.matchesBoundaries(defendant, criteria)
              log.trace "Law ${id}: Did criteria ${criteria},  evidence=${defendant} matches=${matches}"
              break
    	 }
          
          // well, if match already failed, no need to test further...
          if (!matches) return matches  
       }   
       
   
       defendantCriteria?.eachWithIndex {criteria, i -> 
	   log.trace "defendant criteria #${i}: ${criteria}"
  	   
	   switch(criteria) {

	   case List:	   
	       matches = matches && defendant.matchesClassification(defendantEvidence?.getAt(i), criteria)
	       log.trace "Law ${id}: Did criteria ${criteria}, evidence=${defendantEvidence?.getAt(i)} matches=${matches}"
	   break 	
	   				
	   case BoundaryCriteria :
	       matches = matches && defendant.matchesBoundaries(plaintiff, criteria)
	       log.trace "Law ${id}: Did criteria ${criteria}, evidence=${plaintiff} matches=${matches}"
	   break
	   
	   }
	    // well, if match already failed, no need to test defendant...
	    if (!matches) return matches
	 
       }      
    
       // true
       return matches
   }
  
   public String toString() {  return id  }
}