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

import static rembrandt.obj.CourtDecision.*
import org.apache.log4j.Logger
import saskia.bin.Configuration
import com.thoughtworks.xstream.annotations.*

/**
 * @author Nuno Cardoso
 * Collects NEs for this doc, and provides methods for handling NEs.
 */

@XStreamAlias("ListOfNE")
class ListOfNE extends ArrayList {
     
    @XStreamOmitField
    Courthouse court
    
    @XStreamOmitField
    ListOfNEIndex index
 
    @XStreamOmitField
    static Logger log = Logger.getLogger("RembrandtMain")

    /**
     * Main constructor. Initializes a default courthouse, and receives a list of NEs to add.
     * @param NEs list of Nes to add now.
     */
    public ListOfNE(List<NamedEntity> NEs = null) {
        index = new ListOfNEIndex()
	if (NEs) NEs.each{add(it)}
    }

    public Object remove(int i) {
        // update sentence index
        index.syncIndexesAfterRemoving(i) 
        return super.remove(i)
    }

    public boolean add(o) {
       // println "Adding: this.size = ${this.size()}"
        // update sentence index
        //println "adding ne $o with sentenceIndex ${((NamedEntity)o).sentenceIndex} to ${this.size()} "
        index.addToSentenceIndex(((NamedEntity)o).sentenceIndex, this.size() )
        return super.add(o)
    }
    
    /**
     * Sorts NEs by sentence number, then by first token number, then by term size (lerger first).
     */
    void sortNEs() {
	this.sort({a, b -> ( a.sentenceIndex == b.sentenceIndex? 
		   ( a.termIndex == b.termIndex ? 
			b.terms.size() <=> a.terms.size() : // larger NEs first
			a.termIndex <=> b.termIndex )
		  : a.sentenceIndex <=> b.sentenceIndex) } ) 
       // remake the sentence indexes
        index.remakeAllSentenceIndex(this)
    }
    
    /**
     * check if there is such NE 
     * @param ne named entity to check
     * @return true if exists, false otherwise.
     */
    public boolean containsNE(NamedEntity ne) {
	return this.indexOf(ne) >= 0
    }
    
    public createTermIndex() {
         index.indexTerms(this)
    }
    
    /**
     * Get NEs for a given sentence
     * @param sentenceIndex the sentence index.
     * @return List of NEs. Returns empty list for no matches.
     */
    public List<NamedEntity> getNEsBySentenceIndex(int sIndex) {
        return index.sentenceIndex[sIndex].collect{this[it]}
       
	//return this.findAll{it -> 
	//println "Testing $it for $sentenceIndex"
	//it.sentenceIndex == sentenceIndex
	//}		
    }
	
    /**
     * Get NEs by ALT id.
     * @param altid the ALT id.
     * @return List of NEs. Returns empty list for no matches.
     */
    public List<NamedEntity> getNEsByAltId(altid) {
	return this.findAll{it.alt == altid}		
    }
	
    /**
     * Get NEs by ID.
     * @param id the NE id.
     * @return Returns a NamedEntity if found, null otherwise.
     */
    public NamedEntity getNEbyID(String id) {
	return this.findAll{it.id == id}?.getAt(0)
    }
	
    /** 
     * Closure for fetch criteria on starting term 	
     */
    public Closure fetchByStartingTerm = {ne, termIndex -> ne.termIndex == termIndex}
	   
    /** 
     * Closure for fetch criteria on overlapping term 
     */
    public Closure fetchByOverlappingTerm =  {ne, termIndex -> ne.hasTermIndex(termIndex)}
	
    /**
     * Get NEs for a given sentence index and term index.
     * @param sentenceIndex sentence index.
     * @param termIndex term index.
     * @param fetchClosure fetching criteria.
     * @return List of matched NE.
     */
    public List<NamedEntity> getNEsBySentenceAndTermIndex(int sentenceIndex, int termIndex, Closure fetchClosure) {
	
	return getNEsBySentenceIndex(sentenceIndex).findAll{fetchClosure(it,termIndex)}
    }

    /**
	 * Get NEs for a given sentence index, term index and ALT criteria
	 * @param sentenceIndex sentence index.
	 * @param termIndex term index.
	 * @param altid ALT id.
	 * @param subalt ALT sub-id.
	 * @param fetchClosure fetching criteria.
	 * @return List of matched NE.
	 */
    public List<NamedEntity> getNEsBySentenceAndTermIndexAndAlt(int sentenceIndex, int termIndex, String altid, int subalt, Closure fetchClosure) {
	return getNEsBySentenceAndTermIndex(sentenceIndex, termIndex, fetchClosure).findAll{ne -> 
	(ne.alt && ne.alt == altid && ne.subalt == subalt)}
    }
	
	/**
	 * Get NEs for a given sentence index, term index and classification criteria.
	 * @param sentenceIndex sentence index.
	 * @param termIndex term index.
	 * @param fetchClosure fetching criteria.
	 * @param classificationCriteria the Classification criteria.
	 * @return List of matched NE.
	 */
    public List<NamedEntity> getNEsBySentenceAndTermIndexAndClassification(
	int sentenceIndex, int termIndex, classificationNeedles, Closure fetchClosure, classificationCriteria = null) {
	return getNEsBySentenceAndTermIndex(sentenceIndex, termIndex, fetchClosure).findAll{
	    it.matchesClassification(classificationNeedles, classificationCriteria)		
	}
    }
	 
	/**
	 * Get NEs for a given classification criteria.
	 * @param sentenceIndex sentence index.
	 * @param termIndex term index.
	 * @param fetchClosure fetching criteria.
	 * @param classificationCriteria the Classification criteria.
	 * @return List of matched NE.
	 */
	public List<NamedEntity> getNEsByClassification(classificationNeedles, classificationCriteria = null) {
	    return this.findAll{it.matchesClassification(classificationNeedles, classificationCriteria)}
	}
    
        public boolean hasIndexOfTerms() {
            return !index.termIndex.isEmpty()
        }
        
        /**
         * Assigns unique labels for each NE, based on a counter and the document ID.
         * @param docid Document id, for generating unique ids.
         */
        void labelNEs(String label_ = null) {
            String label = ""
            if (label_) label = label_+"-" 
            this.eachWithIndex{ne, i -> ne.id = label+i}
        }
         
	/**
	 * safely remove NEs. Attempt to remove relations on other NEs that point ot the leaving NE.
	 * Note: the relation removal is not safe.
	 * @param nesToTrash can be a single NamedEntity, or a List of NamedEntites to remove.
	 */
	 public removeNEs(nesToTrash) {
	     if (nesToTrash instanceof NamedEntity) nesToTrash = [nesToTrash]
	     nesToTrash.each {leavingNE -> 
	     	int index = this.indexOf(leavingNE)
	     	if (index > -1) {
	     	    // remove from other NEs all correlations.
	     	    if (leavingNE.id) {
	     		this.findAll{it.hasRelationWith(leavingNE)}.each{relatedNE -> 
	     		relatedNE.removeRelation(leavingNE)}
	     	    }	     	    
	     	    this.remove(index)	     	    
	     	}
	     	else log.warn "Trying to delete an NE that is no longer here: ${leavingNE}. Skipping." 
	     }
	 }
		     
	/**
	 * Execute the veredicts given by the Courthouse.
	 * Veredicts have a law, a list of defendenteNEs and a plaintiffNE
	 * @param veredicts a list of CourtVeredict
	 */
	void executeVeredicts(List<CourtVeredict> veredicts) {
	    if (!veredicts) return
        
	   
	    veredicts.each{v -> 
           	    
	    	switch (v.law.decision) {
                
	    	case ReplaceCompletelyDefendant:
	    	    removeNEs(v.defendantNEs)
	    	    v.plaintiffNE.reportToHistory("LAW: ${v.law.id} ACTION: Replaced Completely NE(s) ${v.defendantNEs} <history:${v.defendantNEs*.history.join('; ')}>")
	    	    if (!this.contains(v.plaintiffNE)) add(v.plaintiffNE)
                    break
	    	
	    	case [ReplaceClassificationOfDefendant_KeepBoundaries,
	    	    ReplaceClassificationOfDefendant_UpdateBoundaries]:
                    int index
	            v.defendantNEs.each{ne -> 
                        index = indexOf(ne)
	            	this[index].replaceClassificationFrom(v.plaintiffNE)
		    	this[index].reportToHistory("LAW: ${v.law.id} ACTION: Replaced Classification from NE ${v.plaintiffNE} <history:${v.plaintiffNE.history.join('; ')}>")
	            
	    	     if (v.law.decision.equals(ReplaceClassificationOfDefendant_UpdateBoundaries)) {
                   // println "v.law.decision: "+v.law.decision+" plaintiffNE: ${v.plaintiffNE} defendantNEs:${v.defendantNEs}"
	    	      if ( (this[index].termIndex != v.plaintiffNE.termIndex) || 
	    		 (this[index].terms.size() != v.plaintiffNE.terms.size() ) ) {
	    		  this[index].termIndex = v.plaintiffNE.termIndex
	    		  this[index].terms = v.plaintiffNE.terms    		      	    	      
	    		  this[index].reportToHistory("LAW: ${v.law.id} ACTION: Updated boundaries, terms: ${v.plaintiffNE.terms.join(' ')}>")
	    	      }
	    	  }
                }
	        break	
	        
	    	case [MergeClassificationToDefendant_KeepBoundaries,
	    	      MergeClassificationToDefendant_UpdateBoundaries]:
                
                 int index
	            v.defendantNEs.each{ne -> 
                        index = indexOf(ne)
	            	this[index].mergeClassificationFrom(v.plaintiffNE)
		    	this[index].reportToHistory("LAW: ${v.law.id} ACTION: Merged Classification from NE ${v.plaintiffNE} <history:${v.plaintiffNE.history.join('; ')}>")
	            
	    	    if (v.law.decision.equals(MergeClassificationToDefendant_UpdateBoundaries)) {
	    	      if ( (this[index].termIndex != v.plaintiffNE.termIndex) || 
	    		 (this[index].terms.size() != v.plaintiffNE.terms.size() ) ) {
	    		  this[index].termIndex = v.plaintiffNE.termIndex
	    		  this[index].terms = v.plaintiffNE.terms    		      	    	      
	    		  this[index].reportToHistory("LAW: ${v.law.id} ACTION: Updated boundaries, terms: ${v.plaintiffNE.terms.join(' ')}>")
	    	      }
	    	  }
                }
	        break
	        	        
	        case [JustDisambiguateClassificationOfDefendant, 
	              TryDisambiguateDefendant_CatchReplaceCompletely,
	              TryDisambiguateDefendant_CatchReplaceClassification_KeepBoundaries,
	              TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries,	              
	              TryDisambiguateDefendant_CatchMergeClassification_KeepBoundaries,
	              TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries]:
                
	             
                    List<NamedEntity> NesToRemove = []
            
	            v.defendantNEs.each{ne ->         
	             // disambiguations should come from external evidence rules. Those rules enforce one  
	             // semantic classification. If we use a NamedEntity with more than one SemanticClassification., 
	             // it's a huge problem (AND? OR?) so, throw an exception if we're having a plaintiff with more than one SC. 
	                if (v.plaintiffNE.classification.size() > 1) throw new IllegalStateException(
	                	"Can't disambiguate with a PlaintiffNE with more than one SemanticClassification")
	                
	                // where is the defendentNE?
	                int index = this.indexOf(ne)
	            	this[index].disambiguateClassificationFrom(v.plaintiffNE.classification[0])
		    	this[index].reportToHistory("LAW: ${v.law.id} ACTION: Just Disambiguated Classification "+
		    		"from NE ${v.plaintiffNE} <history:${v.plaintiffNE.history.join('; ')}>")
		    		
		    	// The catch clauses	
		    	if (this[index].classification.isEmpty() || this[index].hasUnknownClassification()) {
		    	    if ( v.law.decision.equals(TryDisambiguateDefendant_CatchReplaceCompletely) ) {     
                                  NesToRemove << ne             
                            }
                    
	                    if ( v.law.decision.equals(TryDisambiguateDefendant_CatchReplaceClassification_KeepBoundaries) || 
	                	 v.law.decision.equals(TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries) ) {
			    	this[index].replaceClassificationFrom(v.plaintiffNE)
			    	this[index].reportToHistory("LAW: ${v.law.id} ACTION: CATCH Replaced Classification from NE ${v.plaintiffNE} <history:${v.plaintiffNE.history.join('; ')}>")	
			    				    	       
	                    }  
	                   if ( v.law.decision.equals(TryDisambiguateDefendant_CatchMergeClassification_KeepBoundaries) || 
			        v.law.decision.equals(TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries) )  {
		               	// let's rollback the NE
		               	this[index] = ne
		               	// and then merge
		               	this[index].mergeClassificationFrom(v.plaintiffNE)
				this[index].reportToHistory("LAW: ${v.law.id} ACTION: CATCH Merged Classification from NE ${v.plaintiffNE} <history:${v.plaintiffNE.history.join('; ')}>")	                	
	                   }
                    
	                }
	                
	                // The boundaries clause
	                if ( v.law.decision.equals(TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries) || 
	                     v.law.decision.equals(TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries)) {
		    	    if ( (this[indexOf(ne)].termIndex != v.plaintiffNE.termIndex) || 
		    		    (this[indexOf(ne)].terms.size() != v.plaintiffNE.terms.size() ) ) {
		    		this[indexOf(ne)].termIndex = v.plaintiffNE.termIndex
		    		this[indexOf(ne)].terms = v.plaintiffNE.terms    		      	    	      
		    		this[indexOf(ne)].reportToHistory("LAW: ${v.law.id} ACTION: Updated boundaries, terms: ${v.plaintiffNE.terms.join(' ')}>")
		    	    }
		    	}   
	            }
                //  TODO: remake this.    
                 if (NesToRemove && v.law.decision.equals(TryDisambiguateDefendant_CatchReplaceCompletely) ) {   
                    removeNEs(NesToRemove)
                    v.plaintiffNE.reportToHistory("LAW: ${v.law.id} ACTION: Replaced Completely NE(s) ${NesToRemove} <history:${NesToRemove*.history.join('; ')}>")
                    if (!contains(v.plaintiffNE)) add(v.plaintiffNE)  
                 }
            
	        break 
	        	        
	        case DiscardPlaintiff:
	            // add a footnote 
	             v.defendantNEs.each{ne -> 
	             	this[indexOf(ne)].reportToHistory("LAW: ${v.law.id} ACTION: Prevail against NE ${v.plaintiffNE} <history:${v.plaintiffNE.history.join('; ')}>")
	             }
	        break 
	        
	        case AddPlaintiff: 
	    	    v.plaintiffNE.reportToHistory("LAW: ${v.law.id} ACTION: Just add this>")
	    	    add(v.plaintiffNE)
	    	break
	    	
	    	default:
	    	    throw new IllegalStateException("Don't know decision ${v.law.decision}")
	    	break
	    	}//switch
	    }// each veredict    
	}
	 
	/**
	 * Adds a relation to a NE. For that, it receives a Map with the following information:
	 * [sourceNEid:X, targetNEid:Y, relationType:Z]
	 * searches for the targeted NE, and adds the ID of the source NE, and the relation type.
	 * @param relation Map with all information for adding a relation.
	 */  
	public addRelation(relation) {
    	    int index = indexOf(getNEbyID(relation.sourceNEid))
    	    if (index >= 0) this[index].corel[relation.targetNEid] = relation.relationType    
	}
	 
	/**
	 * Generates ALTs. This has to move out from addNEs, because each NE in the parameter list as a specific purpose. 
	 */
	public generateALT(NamedEntity BigOne, List<NamedEntity> SmallOnes, String toHistory = "LISTOFNE: Generate ALT ACTION: added ALT") {
	     
	    // generate a unique alt.    	     	    
	    String altid = ""+System.currentTimeMillis()	    	    	
	    int indexOfBigOne = this.indexOf(BigOne)
	    if (indexOfBigOne <= 0) {
		add(BigOne)//throw new IllegalStateException("Can't find the Big One ${BigOne}!")
		indexOfBigOne = this.indexOf(BigOne)
	    }
	    if (this[indexOfBigOne].alt) throw new IllegalStateException("Big One $BigOne already has an ALT!")

	    this[indexOfBigOne].alt = altid
	    this[indexOfBigOne].subalt = 1
	    this[indexOfBigOne].reportToHistory(toHistory)
	    SmallOnes.each{ne -> 
	       if (ne.alt) throw new IllegalStateException("Smaller One $ne already has an ALT!")
	       ne.alt = altid
	       ne.subalt = 2
	       ne.reportToHistory(toHistory)
	       this.add(ne)
	    } 	    
	}
	   	    
	/**
	 * Adds new NEs to this list. 
	 * @param newNEs can be a single NamedEntity, or a List with several NamedEntities.
	 * @param policy Sets up the policy for adding NEs. By default, it is the CourtBattle.
	 */
	public addNEs(newNEs, ConflictPolicy policy = ConflictPolicy.CourtBattle, String toHistory = null) {
	    // println "addNEs: got $newNEs type "+newNEs.class.name 
	    if (!newNEs) return    
	    if (newNEs instanceof NamedEntity) newNEs = [newNEs]

	    newNEs.each {newNE -> 

	    	if (!newNE) return
	    	
	    	// ConflictPolicy must be previously copied from rule to new NEs
	    	// here we don't care about rules, just adding NEs
	    	if (newNE.addpolicy && (newNE.addpolicy instanceof ConflictPolicy )) 	    	  
	    	   policy = newNE.addpolicy    	  	    
	    	
	    	// println "NE: $newNE policy: $policy court: $court"  
	    	List<NamedEntity> NEsSharingTerms 
	    	
	    	switch(policy) {
	    	
	    	// here is the only place were we add 'LAW:' history reports 
	    	case ConflictPolicy.CourtBattle:  
	    	    NEsSharingTerms = this.findAll{it.overlapAtLeastOneTerm(newNE)}
                   
	    	    executeVeredicts(court.judgeThis(newNE, NEsSharingTerms))
	    	break
	    	
	    	case ConflictPolicy.JustAdd:
	    	    newNE.reportToHistory (toHistory ? toHistory: "LISTOFNE: JustAdd ACTION: add NE ${newNE}")
	    	    add(newNE)
	    	break
	    	
	    	case ConflictPolicy.Overwrite:
	    	    NEsSharingTerms = this.findAll{it.overlapAtLeastOneTerm(newNE)}
	    	    newNE.reportToHistory(toHistory ? toHistory: "LISTOFNE: Overwriting ACTION: replacing $NEsSharingTerms")
	    	    removeNEs(NEsSharingTerms)
	    	    add(newNE)
	    	break 
	    	
	    	case ConflictPolicy.OverwriteWithBigger:
	    	  NEsSharingTerms = this.findAll{it.overlapAtLeastOneTerm(newNE)}
	    	  if (!NEsSharingTerms) {
	    	      newNE.reportToHistory(toHistory ? toHistory: "LISTOFNE: OverwriteWithBigger ACTION: just adding, no conflict")
	    	      add(newNE)
	    	  } else {	
	    	      NEsSharingTerms.each{ matchedNE -> 
	    	          if (matchedNE.matchesBoundaries(newNE, BoundaryCriteria.IsContainedBy)) {
	    	              removeNEs(matchedNE)
	    	              newNE.reportToHistory(toHistory ? toHistory : "LISTOFNE: OverwriteWithBigger ACTION: replacing $matchedNE")
    	    	              if (!this.contains(newNE)) add(newNE)	    						
	    	             
	    	          }
	    	      }	    		    	    		
	    	 }
	    	 break
	    	
	    	 case ConflictPolicy.WriteIfNoExistingNEOverlapping:
	    	    NEsSharingTerms = this.findAll{it.overlapAtLeastOneTerm(newNE)}
	    	    if (!NEsSharingTerms) {
	    		newNE.reportToHistory(toHistory ? toHistory : "LISTOFNE: WriteIfNoExistingNEOverlapping: ACTION: adding")
	    		add(newNE)
	    	    } 
	    	 break    
	    	  	
	    	case ConflictPolicy.GenerateALT: 
	    	    NEsSharingTerms = this.findAll{it.overlapAtLeastOneTerm(newNE)}
	    	    // TODO I assume the new one is bigger. But I should check
	    	    generateALT(newNE, NEsSharingTerms)
	    	break
	    	}// switch
	    }// newNEs.each newNE	
	} // method
}// class