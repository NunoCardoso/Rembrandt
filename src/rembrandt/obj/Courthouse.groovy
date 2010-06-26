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
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 *  The Courthouse is triggered when an NE wants to be added to the document, in 
 *  a ConflictPolicy.CourtBattle policy, that is, with a more intelligent resolution 
 *  against conflicting NEs. The Courthouse reads laws that can be applied to the
 *  NEs on the dispute, and then issues a veredict in the form of a CourtAction list 
 *  of Actions, to be executed by the ListOfNE.
 */
class Courthouse {
	
     static Logger log = Logger.getLogger("Courthouse")
     static Map<String, Courthouse> _this = [:] 
     static List<Law> courtLaws = []
     static Law no_defendent_law = new Law(id:"No-Defendant-Law", decision:CourtDecision.AddPlaintiff)
	
     /**
      * Generate a Courthouse Instance.
      * @param language the language for the laws 
      * @param rules the rule package
      */
     public static Courthouse newInstance(String language, String rules) {
	String key = "${language}-${rules}"
	Courthouse instanceToReturn = null
	_this?.keySet().each{ l->
	    if (l.equals(key)) {
		log.info "Recycling an instance for Courthouse for $language, $rules"
		instanceToReturn = _this[key]
	    }
	}
	
	if (!instanceToReturn) {
	    log.info "Building a new instance for Courthouse for $language, $rules"
	    instanceToReturn = new Courthouse(language, rules) 	
	    _this[key] = instanceToReturn			
	}	    
	return instanceToReturn
     }
	
	
    /**
     * Main constructor. Initializes all laws. 
      */
     private Courthouse(String lang, String rules) {
	    
	String LANG = lang.toUpperCase()
	
	courtLaws+= Class.forName("rembrandt.laws.${rules}.${lang}.NECourtLaws${LANG}").newInstance().laws
	courtLaws+= Class.forName("rembrandt.laws.${rules}.${lang}.NumberCourtLaws${LANG}").newInstance().laws
	courtLaws+= Class.forName("rembrandt.laws.${rules}.${lang}.TimeCourtLaws${LANG}").newInstance().laws
	courtLaws+= Class.forName("rembrandt.laws.${rules}.${lang}.ValueCourtLaws${LANG}").newInstance().laws
	courtLaws+= Class.forName("rembrandt.laws.${rules}.${lang}.LocalCourtLaws${LANG}").newInstance().laws
	courtLaws+= Class.forName("rembrandt.laws.${rules}.${lang}.PersonCourtLaws${LANG}").newInstance().laws
	courtLaws+= Class.forName("rembrandt.laws.${rules}.${lang}.OrganizationCourtLaws${LANG}").newInstance().laws
	courtLaws+= Class.forName("rembrandt.laws.${rules}.${lang}.ThingCourtLaws${LANG}").newInstance().laws
	courtLaws+= Class.forName("rembrandt.laws.${rules}.${lang}.AbstractionCourtLaws${LANG}").newInstance().laws
	courtLaws+= Class.forName("rembrandt.laws.${rules}.${lang}.MasterpieceCourtLaws${LANG}").newInstance().laws	    
     }
	
	/** 
	 * Alternative constructor, for selective laws.
	 * @param laws list of Laws.
	 */
	public Courthouse(laws) {
	    this()
	    addLaws(laws)
	    log.debug "Courthouse: added laws, now with "+courtLaws.size()+" laws."
	}
	
	/** 
	 * Add selected laws.
	 * @param laws list of Laws.
	 */
	public addLaws(laws) {
	    this.courtLaws += laws
	    log.debug "Added laws, now with "+courtLaws.size()+" laws."
	}
	
	/**
	 * Trigger a judgement.
	 * @param defendantNEs Defendant NEs, that is, NEs that were already added, and that 
	 * are conflicting with the plaintiffNE
	 * @param plaintiffNEs plaintiff NE, that is, the NE that wants to be added, but first 
	 * it has to be approved by the Courthouse.
	 */
	public List<CourtVeredict> judgeThis(NamedEntity plaintiffNE, List<NamedEntity>defendantNEs) {

	    // plaintiffNE should exist!
	    if (!plaintiffNE)  throw new IllegalStateException("No laws in courthouse!")
	    if (!courtLaws) throw new IllegalStateException("No laws in courthouse!")

	    // if there is no one to defend, then let the plaintiff be added
	    if (defendantNEs.isEmpty()) {
		log.debug "No defendant, CourtVeredict:AddPlaintiff plaintiffNE $plaintiffNE"
		return [new CourtVeredict(law:no_defendent_law, plaintiffNE:plaintiffNE)]
	    }

	    // only one law will be enforced, but it may apply now to 1 or more defendantNEs
	    // The plaintiffNE is a single NamedEntity. 
	    
	    List<CourtVeredict> veredicts = []
	    
	    defendantNEs.each{defendantNE -> 
	   
	    	List<Law> matchedLaws = courtLaws.findAll{it.matchesEvidence(defendantNE, plaintiffNE)}
	    	
	    	if (!matchedLaws) {
	    	    log.warn "No law appliable for defendant ${defendantNE} and plaintiff ${plaintiffNE}"
	    	    log.warn "Skipping, no adding or deleting!!"
	    	    return
	    	} 

	    	if (matchedLaws.size() > 1) {
                    // Warn only if the decision is different
                    boolean same = true
	    	    CourtDecision cd = matchedLaws[0].decision
	    	    for (int i=1; i<matchedLaws.size(); i++) {
	    		if (cd != matchedLaws[i].decision) {
                           same = false
	    	      	   log.warn "There are several laws appliable for defendant ${defendantNE} and plaintiff ${plaintiffNE} with different decisions"
	    	      	   log.warn "Laws: ${matchedLaws} Decisions: ${matchedLaws*.decision}"
	    		}
	    	    }    
                    if (same) {
                        log.info "Multiple laws found, but same decision." 	  
                    }
	    	}
		    
	    	Law law = matchedLaws[0]
	    	log.debug "Law matched for defendantNE ${defendantNE} and plaintiffNE ${plaintiffNE}: ${law}"
	    	
	    	// note: sometimes there's identic veredicts that affects the same plaintiff, for several defendants
	    	// for example, a plaintiffNE that will delete several defendant NEs.            
	    	// when this happens, add this defendentNE to an existing veredict 
	    	
	    	int v = veredicts.findIndexOf{it.law == law && it.plaintiffNE == plaintiffNE}
	    	if (v >= 0) veredicts[v].defendantNEs << defendantNE
	    	else veredicts << new CourtVeredict(law:law,
	    		defendantNEs:[defendantNE], plaintiffNE:plaintiffNE)
	    	 
	    }
	    return veredicts
	} 
	    	                  		
}