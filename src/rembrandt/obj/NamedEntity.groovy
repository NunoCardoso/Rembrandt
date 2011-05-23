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

import rembrandt.obj.ClassificationCriteria as CC
import static rembrandt.obj.BoundaryCriteria.* // like that, I have the enum types easily
import rembrandt.obj.SemanticClassification
import org.apache.log4j.Logger
import org.apache.log4j.Level
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import com.thoughtworks.xstream.annotations.*
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * Named entity class. Contains related information for the entity, plus several methods 
 * for handling its properties.
 */

@XStreamAlias("NamedEntity")
class NamedEntity {

  /**
   *  ID of the named entity
   *  Do not use IDs for equality or other stuff! 
   *  Use the indexOf instead. The IDs are only filled in when the 
   *  final set of NEs is frozen, and used for Entity Relation Detection.
   */
  String id = null

  /**
   * Field of the document that this NE belongs to. 
   * Title, body? 
   */
  String field = null
  int termIndex = -1
  int sentenceIndex = -1// the index of the sentence
  
  /** Terms of the NE  */
  List<Term> terms = []
  
  /** Categories of the NE */
  List<SemanticClassification> classification = []
  
  /** Map of the Entity Relation info, in id=>type format */
  LinkedHashMap<String,String> corel = new LinkedHashMap()
  
  /** Wikipedia Page Titles, associated to semantic classifications */
  Map<SemanticClassification,List<String>> wikipediaPage = [:]

  /** grounded DBpedia resources, associated to semantic classifications */
  Map<SemanticClassification,List<String>> dbpediaPage = [:]

  /** for NEs that came from a Wiki anchor link, there is already a known wiki/DBpedia resource */
  @XStreamOmitField
  String link
  
  @XStreamOmitField
  List<String> history = []

  @XStreamOmitField
  List<String> comment = []

  String alt = null
  int subalt = -1
  
  ConflictPolicy addpolicy = null

  // a TimeGrounding object, if it's the case
  TimeGrounding tg

  static Logger log = Logger.getLogger("NamedEntity")
  static Level level = log.getLevel()
 
  /**
   * Add a new entity relation for this NE
   * @param targetNE the targeted NE
   * @param relationType the relation type.
   */
  public void addRelation(NamedEntity targetNE, String relationType) {
      if (!this.corel.containsKey(targetNE.id)) this.corel[targetNE.id] = relationType
  }
  
  /**
   * Get a sentence version of this named entity terms
   * @return The terms as a sentence
   */
  public Sentence asSentence() {
      // do NOT use termIndex as pointer. Let pointer be set to 0.
      // termIndex is my artificial index for term organization. pointer is a real List index.
      return new Sentence(this.terms, sentenceIndex)  
      
  }
  /**
   * Clone this NamedEntity. Copies everything. 
   * @return cloned named entity.
   */
  public NamedEntity clone() {
      return new NamedEntity(
	      id: (id ? id.toString() : null),
	      field: (field ? field.clone() : null), 
	      termIndex:termIndex, 
	      sentenceIndex:sentenceIndex,
	      terms:terms.clone(), 
	      classification:classification.clone(), 
	      corel:corel.clone(), 
	      wikipediaPage:wikipediaPage.clone(), 
	      dbpediaPage:dbpediaPage.clone(),
	      link:link, 
	      tg:tg,
	      history:history.clone(), 
	      comment:comment.clone(), 	      
	      alt:alt, subalt:subalt, 
	      addpolicy:addpolicy	     
	  )
  }
  
  /**
   * Clones only the terms and position information.
   * That is, it resets all other information.
   */
  public NamedEntity clonePositionAndTerms() {
      return new NamedEntity(terms:terms.clone(), 
	  termIndex:termIndex,
	  sentenceIndex:sentenceIndex,
	  field:(field ? field.clone() : null) // can't clone null objects...
      )
  }

  /** removes classification from this */ 
  void removeClassification(SemanticClassification that_cl) {
      //println "that_cl: $that_cl"

      List<SemanticClassification> classificationsToRemove = []
      // let's check the hierarchy level of disambiguation
      CC level = that_cl.s ? CC.Subtype : (that_cl.t ? CC.Type : CC.Category ) 
 
      classification.eachWithIndex{this_cl, i ->
      
	 		String veredict = this_cl.compareTo(that_cl)
	 		//println "veredict: $veredict, level: $level"
	 		switch(level) {

	 case CC.Category: 
		 // eliminate everything that's not CATEGORY DIFFERENT 
	     if (!(veredict.equals("CATEGORY DIFFERENT"))) classificationsToRemove << this_cl
	 break
	 
	 case CC.Type:		 
			 // eliminate everything that's not TYPE DIFFERENT
	     if ( ! (veredict.equals("TYPE DIFFERENT") || veredict.equals("CATEGORY DIFFERENT") || 
				 veredict.equals("CATEGORY BROADER") || veredict.equals("TYPE BROADER") )  )	
				classificationsToRemove << this_cl
	break
	 
	 case CC.Subtype:
			 // eliminate everything that's not SUBTYPE DIFFERENT
	     if ( ! ( veredict.equals("TYPE DIFFERENT") || veredict.equals("CATEGORY DIFFERENT") 
				|| veredict.equals("SUBTYPE DIFFERENT") ||  veredict.equals("CATEGORY BROADER") || 
				veredict.equals("TYPE BROADER") || veredict.equals("SUBTYPE BROADER") ) )  	
				classificationsToRemove << this_cl
	 
	 break 
	 }//end switch                                                              
      }// each classification
      
     //println "classificationsToRemove $classificationsToRemove"
     //println "classification1: $classification"
     classificationsToRemove.each{cl -> 
         this.classification = this.classification - cl
         //println "classification2: $classification"
	 if (wikipediaPage.containsKey(cl)) wikipediaPage.remove(cl)
	 if (dbpediaPage.containsKey(cl)) dbpediaPage.remove(cl)		 
      }
    //println "classification3: $classification"

  }
  
  /** filters own classifications  */ 
  void disambiguateClassificationFrom(SemanticClassification that_cl) {
      
      List<SemanticClassification> classificationsToRemove = []
      // let's check the hierarchy level of disambiguation
      CC level = that_cl.s ? CC.Subtype : (that_cl.t ? CC.Type : CC.Category ) 
 
      	 
      classification.eachWithIndex{this_cl, i ->
      
	 String veredict = this_cl.compareTo(that_cl)
	 
	 switch(level) {
	 
	 // eliminate everything that's CATEGORY DIFFERENT
	 // all CATEGORY EQUAL or TYPE/SUBTYPE (meaning that CATEGORY is equal) are allowed to stay
	 case CC.Category: 
	     if (veredict.equals("CATEGORY DIFFERENT")) classificationsToRemove << this_cl
	 break
	 
	 case CC.Type:		 
	 // TYPE is BROADER, complete it
	 // TYPE is EQUAL, allow it to stay
	 // TYPE is NARROWER doesn't apply for CC.Type level
	 // TYPE is DIFFERENT, erase it
	 // For CATEGORY EQUAL, it doesn't apply
	 // CATEGORY DIFFERENT - erase it
	 // for SUBTYPE.*, it means TYPE is EQUAL, so allow them to stay. Don't care if they are EQUAL or DIFFERENT, 
	 // since the disambiguation level is at Type - that's the responsability on the Subtype level (when that_cl goes all way to subtype)
	     if (veredict.equals("TYPE BROADER")) {
		 SemanticClassification old_cl = this_cl.clone()
		 // so that I don't change the reference on CommonClassification cores
		 SemanticClassification tochange = classification[i]
		 tochange.s = that_cl.s                       
		 classification[i] = tochange
		 SemanticClassification new_cl = classification[i].clone()
		 
		 if (wikipediaPage.containsKey(old_cl)) {
		     wikipediaPage[new_cl] = wikipediaPage[old_cl]
		     wikipediaPage.remove(old_cl)                                     
		 }		 
		 if (dbpediaPage.containsKey(old_cl)) {
		     dbpediaPage[new_cl] = dbpediaPage[old_cl]
		     dbpediaPage.remove(old_cl)                                     
		 }
		 
	     }
	     if (veredict.equals("CATEGORY DIFFERENT") || veredict.equals("TYPE DIFFERENT")) {
		 classificationsToRemove << this_cl
	     }    
          break
	 
	 case CC.Subtype:
	  // SUBTYPE NARROWER doesn't apply to this CC.Subtype
	  // SUBTYPE BROADER, TYPE BROADER - complete it
	  // SUBTYPE EQUAL - allow it to stay	  
	  // CATEGORY DIFFERENT, TYPE DIFFERENT, SUBTYPE DIFFERENT - erase it	     
	  // CATEGORY EQUAL, TYPE EQUAL, TYPE NARROWER doesn't apply 	       
	     if (veredict =~ /BROADER/) { // don't worry, there's no CATEGORY BROADER veredict
		 SemanticClassification old_cl = this_cl.clone()		 
		 classification[i] = that_cl
		 SemanticClassification new_cl = classification[i].clone()
		 
		 if (wikipediaPage.containsKey(old_cl)) {
		     wikipediaPage[new_cl] = wikipediaPage[old_cl]
		     wikipediaPage.remove(old_cl)                                     
		 }
		 
		 if (dbpediaPage.containsKey(old_cl)) {
		     dbpediaPage[new_cl] = dbpediaPage[old_cl]
		     dbpediaPage.remove(old_cl)                                     
		 }
	     }
	     if (veredict =~ /DIFFERENT/) {
		 classificationsToRemove << this_cl
	     }     
	 
	 break 
	 }//end switch                                                              
      }// each classification
      
     //println "classificationsToRemove $classificationsToRemove"
     //println "classification: $classification"
     classificationsToRemove.each{cl -> 
         this.classification = this.classification - cl
         //println "classification: $classification"
	 if (wikipediaPage.containsKey(cl)) wikipediaPage.remove(cl)
	 if (dbpediaPage.containsKey(cl)) dbpediaPage.remove(cl)		 
      }
  }
  
  
  /**
   * Overrides the method for NE lookup in lists. Mind that a NE can 
   * be the same, but with different historical trails. 
   * This equals() implementation looks up to the position of the NE 
   * (term indexes, size and sentence index) and classification information 
   * (category, type and subtype)
   * @param ne The named entity to compare.
   * @return true if it matches classification and position, false otherwise.
   */
   public boolean equals(NamedEntity ne) {
       return this.sentenceIndex == ne.sentenceIndex &&
       this.termIndex == ne.termIndex && 
       this.classification == ne.classification && 
       this.field == ne.field && equalsTerms(ne)
   }
	
  /**
   * Compares only the terms for computing equality. Just the terms, not the indexes.
   * So, NEs with the same terms and in different positions in the text return 
   * true on this method, while they return false in the equals() method.
   * @param ne The named entity to compare.
   * @return true if it matches texts of terms.
   */
  public boolean equalsTerms(NamedEntity ne) {
	  if (!ne.terms) {
		if (!this.terms) return true else return false
	 }
     if (this.terms.size() != ne.terms.size()) return false
     for (int i=0; i<this.terms.size(); i++) {
		if (this.terms[i] != ne.terms[i]) return false
     }
     return true
  }
  
  public boolean equalsTerms(List<Term> terms) {
	return equalsTerms(new NamedEntity(terms:terms))
  }
  
  
  
  /**
   * Check if this named entity are already bonded with a relation.
   * @param ne Named entity to compare
   * @return true if there is a bond, false otherwise
   */	
   public boolean hasRelationWith(NamedEntity ne) {
       if (corel.containsKey(ne.id)) return true
       if (ne.corel.containsKey(this.id)) return true
       return false
  }

  /**
   * Same with hasRelationWith, but with a relation type needle.
   * @param ne Named entity to compare
   * @param type The relation type needle.
   * @return true if there is a bond of that type, false otherwise
   */	
   public boolean hasRelationOfType(NamedEntity ne, String type) {
       if (corel.containsKey(ne.id) && corel[ne.id] == type) return true
       if (ne.corel.containsKey(id) && ne.corel[id] == type) return true
       return false
   }
  
  /**
   * check it this named entity contains the given term index
   * @param termIndex term index
   * @return boolean if it contains, false otherwise. 
   */
   public boolean hasTermIndex(int termIndex) {
       return this.termIndex <= termIndex && lastTermIndex() >= termIndex
   }
   
  /**
   * Check it this named entity has no relevant classification.
   * It checks the category list for 'EM', null or empty.
   * @return true if has unknown classification, false otherwise.
   */
   public boolean hasUnknownClassification() {
       return classification.isEmpty() || classification*.c.contains(Classes.unknown)	
   }

  /**
   * Remove unknown classification
   */
   public removeUnknownClassification() {
       classification = getKnownClassifications()
     }	
  
   /**
    * Check if it has known classifications
    * @return true if finds one non-EM classification, false otherwise
    */
   public boolean hasKnownClassifications() {
       return classification.find{it.c != Classes.unknown}
   }
   
   /**
    * returns all known classifications
    * @return a list of known (i.e., non-EM) semantic classifications
    */
  public List<SemanticClassification> getKnownClassifications() {
      return classification.findAll{(it?.c != Classes.unknown)}
  }
	
  /**
   * Compute the last token index number
   * @return int the last token index.
   */
  public int lastTermIndex() {
      return termIndex+terms.size()-1
  }

  /**
   * Test the named entities with the given boundary criteria.
   * @param ne Named entity to test.
   * @param BoundaryCriteria the boundary criteria.
   * @return true if the criteria is satisfied, false otherwise.
   */
  public boolean matchesBoundaries(NamedEntity ne, BoundaryCriteria criteria) {
      
      switch (criteria) {
      
      case ExactMatch:
	return this.sentenceIndex == ne.sentenceIndex && this.termIndex == ne.termIndex && 
	this.field==field && this.lastTermIndex() == ne.lastTermIndex() && this.terms.size() == ne.terms.size()
      break
	  	
      	case IsContainedByAndCenterJustified:
      	    return this.sentenceIndex == ne.sentenceIndex && this.termIndex > ne.termIndex && 
      	this.field==field && this.lastTermIndex() < ne.lastTermIndex() 
      	 break
      	 
      	case IsContainedByAndLeftJustified:    	    
      	    return this.sentenceIndex == ne.sentenceIndex && this.termIndex == ne.termIndex && 
      	this.field==field && this.lastTermIndex() < ne.lastTermIndex() 
        break

      	case IsContainedByAndRightJustified:
      	    return this.sentenceIndex == ne.sentenceIndex && this.termIndex > ne.termIndex && 
      	this.field==field && this.lastTermIndex() == ne.lastTermIndex() 
      	break
  	    
      	case IsContainedBy:
      	    return this.sentenceIndex == ne.sentenceIndex && this.termIndex >= ne.termIndex && 
      	this.field==field && this.lastTermIndex() <= ne.lastTermIndex() && this.terms.size() != ne.terms.size()
        break
      	    
      	case ExactOrIsContainedBy:
      	    return this.sentenceIndex == ne.sentenceIndex && this.termIndex >= ne.termIndex && 
      	this.field==field && this.lastTermIndex() <= ne.lastTermIndex() && this.terms.size() <= ne.terms.size()
        break
        
      	case ContainsAndCenterJustified:
      	    return this.sentenceIndex == ne.sentenceIndex && this.termIndex < ne.termIndex && 
      	this.field==field && this.lastTermIndex() > ne.lastTermIndex() 
      	break
      	
      	case ContainsAndLeftJustified:
      	    return this.sentenceIndex == ne.sentenceIndex && this.termIndex == ne.termIndex && 
      	this.field==field &&  this.lastTermIndex() > ne.lastTermIndex() 
      	break
      	
      	case ContainsAndRightJustified:
      	    return this.sentenceIndex == ne.sentenceIndex && this.termIndex < ne.termIndex && 
      	this.field==field &&    this.lastTermIndex() == ne.lastTermIndex() 
      	break
     
      	case Contains:
      	  return this.sentenceIndex == ne.sentenceIndex && this.termIndex <= ne.termIndex && 
      	this.field==field && this.lastTermIndex() >= ne.lastTermIndex() && this.terms.size() != ne.terms.size()
    	break 
    	
      	case ExactOrContains:
       	  return this.sentenceIndex == ne.sentenceIndex && this.termIndex <= ne.termIndex && 
       	this.field==field &&  this.lastTermIndex() >= ne.lastTermIndex() && this.terms.size() >= ne.terms.size()
    	break
    	
      	case Overlapping:
      	   return this.sentenceIndex == ne.sentenceIndex && this.field==field && 
	    // either ne1 begins earlier, and ends inside ne2
	    ( (this.termIndex < ne.termIndex && this.lastTermIndex() >= ne.termIndex 
		    && this.lastTermIndex() < ne.lastTermIndex()) 
		 || 
		// either ne1 starts inside ne2, and ends after  
		(this.termIndex > ne.termIndex  && this.termIndex <= ne.lastTermIndex() 
		    && this.lastTermIndex() > ne.lastTermIndex()) )
	     break
      }// switch
  }
  

  public boolean matchesClassification(NamedEntity other_ne, CC criteria_thisNE, CC criteria_thatNE = null, CC depth = null) throws Exception {
      return matchesClassification(other_ne.classification, thisNE, thatNE, depth)
  }
   
  public boolean matchesClassification(NamedEntity other_ne, List criteria) throws Exception {
      return matchesClassification(other_ne.classification, criteria?.getAt(0), criteria?.getAt(1), criteria?.getAt(2))
  }

  public boolean matchesClassification(List<SemanticClassification> that, List criteria) throws Exception {
      return matchesClassification(that, criteria?.getAt(0), criteria?.getAt(1), criteria?.getAt(2))
  }
  
  /**
   *  TODO
   */
  public boolean matchesClassification(List<SemanticClassification> that, CC criteria_thisNE, CC criteria_thatNE = null, CC depth = null) throws Exception {
 
      if (!criteria_thisNE) throw new IllegalStateException ("Can't have a null criteria for criteria_thisNE in matchesClassification")
      if (!this.classification) throw new IllegalStateException ("Can't perform a matchesClassification if thisNE (${this}) doesn't have classification")

       // let's do this quick match now
      if (criteria_thisNE == CC.AnyKnownOrUnknownCategory || criteria_thisNE == CC.AnyKnownCategory) {
	  /** allow any known or unknown category. it's just a self-check-if-empty  */
	switch(criteria_thisNE) {
	case CC.AnyKnownOrUnknownCategory:
	return !this.classification.isEmpty()    
	break
	case CC.AnyKnownCategory:
 	return hasKnownClassifications()    
 	break
        }
      }
      
      // now, we're into the core of matchesClassification. Don't allow null 'that', null criteria_thatNE or null depth
      if (!that) throw new IllegalStateException ("Can't allow a null that for criteria_thisNE such as $criteria_thisNE")
      if (!criteria_thatNE) throw new IllegalStateException ("Can't allow a null criteria_thatNE for criteria_thisNE such as $criteria_thisNE")
      if (!depth) throw new IllegalStateException ("Can't allow a null depth for criteria_thisNE such as $criteria_thisNE")   
      
    List veredicts = []  // probably for debugging purpoes only in the future
    List solved_veredicts = []
                             
    this.classification.eachWithIndex{this_cl, i ->
    	veredicts[i] = []
    	solved_veredicts[i] = []
    	that.eachWithIndex{that_cl, j -> 
    	    veredicts[i][j] = this_cl.compareTo(that_cl) 
    	    solved_veredicts[i][j] = SemanticClassification.resolveVeredictTo(veredicts[i][j], depth)
        }
    }
     
    //  println "Solved veredicts: $solved_veredicts"
    int i_thisNE = this.classification.size() // ROWS
    
    switch(criteria_thisNE) {

        case CC.AllOfThese: // forces all ROWS to return true  	
   	   boolean res = true // starts as true because of AllOfThese
   	   
   	   for (int i=0; i < i_thisNE; i++) {  	       
	       boolean res2 = solve_thatNE(solved_veredicts[i], criteria_thatNE) 
	       res = res && res2 // &&: AllOfThese
   	   } 
   	   
   	   return res
   	break // AllOfThese case
    	
   	case CC.ExistsAtLeastOneOfThese:
   	    boolean res = false // starts as false because of ExistsAtLeastOneOfThese
   	   
   	   for (int i=0; i < i_thisNE; i++) {  	       
	       boolean res2 = solve_thatNE(solved_veredicts[i], criteria_thatNE) 
	       res = res || res2 // ||: ExistsAtLeastOneOfThese
   	   } 
   	   
   	   return res
   	 break
   	 
   	case CC.NeverAllOfThese: // forces all ROWS to return true  	
    	   boolean res = true // starts as true because of AllOfThese
    	   
    	   for (int i=0; i < i_thisNE; i++) {  	       
 	       boolean res2 = solve_thatNE(solved_veredicts[i], criteria_thatNE) 
 	       res = res && res2 // &&: AllOfThese
    	   } 
    	   res = !res // NeverAllOfThese = !AllOfThese
    	   return res
    	break // NeverAllOfThese case
     	
    	case CC.NeverExistsAtLeastOneOfThese:
    	    boolean res = false // starts as false because of ExistsAtLeastOneOfThese
    	   
    	   for (int i=0; i < i_thisNE; i++) {  	       
 	       boolean res2 = solve_thatNE(solved_veredicts[i], criteria_thatNE) 
 	       res = res || res2 // ||: ExistsAtLeastOneOfThese
    	   } 
    	   res = !res // NeverExistsAtLeastOneOfThese = !ExistsAtLeastOneOfThese
    	   return res
    	break
        default:
            throw IllegalStateException("Can't handle criteria $criteria_thisNE")
        break
   	}// switch criteria_thisNE
    }// method
  
  
  private boolean solve_thatNE(List veredicts, ClassificationCriteria criteria_thatNE) {
  
      boolean res
      
      switch(criteria_thatNE) {  
	       
         case CC.AllOfThem: 
	    res = true // AllOfThem: starts true 		  
	    for(int i=0; i< veredicts.size(); i++) {
	      res = res && veredicts[i] // &&: AllOfThem
	    }
         break
      
         case CC.ExistsAtLeastOneOfThem: 
           res = false // ExistsAtLeastOneOfThem: starts false 	          
           for(int i=0; i< veredicts.size(); i++) {
	      res = res || veredicts[i] // ||: ExistsAtLeastOneOfThem
	    }
         break
         
         case CC.NeverAllOfThem: 
	    res = true // AllOfThem: starts true 		  
	    for(int i=0; i< veredicts.size(); i++) {
	      res = res && veredicts[i] // &&: AllOfThem
	    }
	    res = !res // !AllOfThem
        break
        
         case CC.NeverExistsAtLeastOneOfThem: 
           res = false // ExistsAtLeastOneOfThem: starts false 	          
           for(int i=0; i< veredicts.size(); i++) {
	      res = res || veredicts[i] // ||: ExistsAtLeastOneOfThem
	    }
           res = !res
         break 
         
         default:
             throw IllegalStateException("Can't handle criteria $criteria_thatNE")
         break
      }// switch criteria_thatNE 
      return res
  }
     	   
  /** 
   * Calles each of the ne's classification, and merges it by calling the other mergeClassification function.
   * @param ne the named entity that will provide classifications to merge
   */
  public void mergeClassificationFrom(NamedEntity ne) {
      ne.classification.each{mergeClassification(it)}  
  }
  
   /** 
    * Merges classifications. Accepts itens in a SemanticClassification or in List<SemanticClassification>.
    * For each cat/typ/sub, if it is completely different, it adds. It is already included, it skips.
    * If it completes one of them, it fills up the missing elements.
    */
   public void mergeClassification(SemanticClassification classification) {
 
       if (!classification) return 
         
       // veredicts for each classification comparison:
	   // DIFFERENT - This classification has a non-null item that clashes
	   // BROADER - This classification is a broader version than the candidate classification
	   // NARROWER - This classification is a narrower version than the candidate classification   
	   // EQUAL - This classification is an exact copy, null included

         List<String> veredicts = []
         if (classification.c.equals(Classes.unknown)) return // not interested in merging an unknown classification, thanks.
         
         List goodClassifications = getKnownClassifications()
  
         // if this NE does not have good classifications, just add the invoming classification, and it's done
         if (!goodClassifications) {
             removeUnknownClassification() 
             this.classification << classification
             return
         } else {
             // resolve conflicts
             getKnownClassifications().each{this_class ->
         	veredicts << this_class.compareTo(classification)
             }
         }
        
	// Let's get rid of 'EM', in case there's a valid veredict to be inserted
	if (veredicts) {
	    //println "Veredicts: $veredicts"
	    removeUnknownClassification() 
		
	    if (veredicts.find{it =~ /EQUAL/} || veredicts.find{it =~ /NARROWER/}) return
	
       // if THIS entity is BROADER - complete it. 
       // can't use closure, otherwise I can't return early from a successful merge!
	    for (int i=0; i<veredicts.size(); i++) { 
	    	if (veredicts[i] =~ /BROADER/) {	    
	    	    this.classification[i] = classification
	    	    return // este return está na closure!!!
	    	}
	    }
     // if the entity is DIFFERENT (it should not have EQUAL, NARROWER or BROADER veredicts)		    
     	    if (veredicts.find{it =~ /DIFFERENT/}) {
     		//println "2 Adding $classification to ${this.classification}"
     		this.classification << classification
     		return
     	    }
   	    
	}    
   }// mergeClassification
   
   /** 
    * Return this NE's history 
    * @return a \n-separated String
    */
   public String printHistory() {
       return history.join("\n")
   }
   
   /**
    * Counts terms matching a needle, returning a List with [term:term, index:i] maps. 
    * @param needle pattern needle
    * @return List of terms matched.
    */
   public List<Term> termsMatching(Pattern needle) {
       return terms.findAll{it.text ==~ needle}
   }
 
  /**
   * Check if one NE is fully contained in the other
   * @param ne named entity to check.
   * @return true if th
   */
  public boolean partialTermMatch(NamedEntity ne) {
       // while comparing terms, indexes do not cope with the intersect.
       // must make my own intersect implementation.
       List<Term> terms1 = this.terms.clone()
       List<Term> terms2 = ne.terms.clone()
       List<Term> terms3 = []      
       int termindex = 0
       boolean termfound = false
       
       if (terms1.size() >= terms2.size()) {
	   terms1.each {term -> 
	     if (terms2[termindex] != null && term == terms2[termindex] ) {
		 termfound = true
		 terms3 += term
       	     }
       	     if (termfound) termindex++
           }
       } else {
	   terms2.each {term -> 
	      if (terms1[termindex] != null && term == terms1[termindex] ) {
		  termfound = true
		  terms3 += term
	      }
	      if (termfound) termindex++
	   }
       }

       if (terms1.size() <= terms2.size()) return terms1 == terms3
       else return terms2 == terms3     
  }
  
  /** 
   * Print terms joined by a separater (space by default).
   * @param separator The term separator
   * @return a string line with all terms. 
   */
  public String printTerms(String separator = " ") {
      return terms*.text.join(separator)
  }

  /**
   * Remove a given relation in thie entity.
   * @param targetNE the targetedNE 
   */
  public void removeRelation(NamedEntity targetNE) {
      def itens = corel.keySet().findAll{it == targetNE.id}
      if (itens) corel.remove(itens)
  }
 
  /**
   * Add an event to this history trail, at the beginning
   * @param event The event to report.
   */
  public void reportToHistory(String event) {
      history.add(0,event)
  }
  
  /**
   * Replaces this classification with the classification from the given NE
   * @param the NE with the source classification 
   */ 
   public void replaceClassificationFrom(NamedEntity ne) {  
       this.classification = ne.classification
  }
   
  /**
   * Replaces wikipedia information from the one of the given NE
   * @param the NE with the source Wikipedia information  
   */
   public void replaceAdditionalInfoFrom(NamedEntity ne) {  
       this.wikipediaPage = ne.wikipediaPage
       this.dbpediaPage = ne.dbpediaPage
  }
   
   /**
    * Test if there is an overlap
    * @param ne The named entity to compare.
    * @return true if they share at least one term, false otherwise.
    */
   public boolean overlapAtLeastOneTerm(NamedEntity ne) { 
        return sentenceIndex == ne.sentenceIndex && field==ne.field && 
       // (termIndex..(lastTermIndex())).find{it >= ne.termIndex && it <= ne.lastTermIndex()}
        (termIndex..(lastTermIndex())).intersect(ne.termIndex..(ne.lastTermIndex()))

   }

   /**
    * Outputs NE to string.
    */
  public String toString() {
      switch(level) {
     
      case Level.DEBUG:  
        return toStringDebugLevel()   
      break
     
      case Level.TRACE:
			return  toStringTraceLevel()  
      
      break 
      
      default:  
        return "NE:"+(id ? "$id:" : "" )+terms.toString()
      break
      }
  }

  public String toStringDebugLevel() {
	 return "NE:"+(id ? "$id:" : "" )+terms.toString()+"$sentenceIndex:$termIndex:"+
        classification
	}
	
	public String toStringTraceLevel() {
		 return "NE:"+(id ? "$id:" : "" )+terms.toString()+"$field:$sentenceIndex:$termIndex:"+
        classification  + ( (alt == null) ? "" : "alt(${alt}):")+( (subalt ==-1) ? "" : "subalt($subalt):")+
        "history:\n"+this.printHistory()
}
}