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
 
package rembrandt.bin

import org.apache.log4j.Logger
import rembrandt.obj.Document 
import rembrandt.obj.Courthouse
import rembrandt.obj.ListOfNE
import rembrandt.obj.ListOfNEIndex
import rembrandt.obj.NamedEntity
import rembrandt.obj.SemanticClassification
import rembrandt.obj.BoundaryCriteria
import rembrandt.obj.ConflictPolicy
import rembrandt.obj.EntityRelation
import saskia.wikipedia.WikipediaAPI
import saskia.bin.Configuration
import saskia.bin.AskSaskia
import rembrandt.rules.harem.en.*
import rembrandt.rules.NamedEntityDetector
import rembrandt.rules.SplitNEDetector
import rembrandt.gazetteers.Patterns
import rembrandt.gazetteers.en.*
import java.util.regex.Pattern
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.obj.ClassificationCriteria as CC
/**
 * @author Nuno Cardoso
 * 
 * Core processing of Rembrandt, for English texts.
 */
class RembrandtCoreENforHAREM extends RembrandtCore {
 
    static Logger log = Logger.getLogger("RembrandtMain")
    static final String lang = "en"
    static final String rules = "harem"
    Configuration conf
    static final Courthouse court = Courthouse.newInstance(lang, rules)
    WikipediaAPI wikipedia 
    AskSaskia saskia

    SecondHAREMClassificationLabelsEN classes
    
    NamedEntityDetector number, time, value, nerules, ierules // basic internal evidence rules
    NamedEntityDetector ee_loc, ee_org, ee_per, ee_abs, ee_thi, ee_eve, ee_mas // first external evidence rules
    NamedEntityDetector ee2 // second external evidence rules
    SplitNEDetector split
    EntityRelationCore ercore
    
    /**
     * Main constructor.
     * @param conf Configuration instance.
     */
    public RembrandtCoreENforHAREM(Configuration conf) {
	
	this.conf = Configuration.newInstance() 		
	wikipedia = WikipediaAPI.newInstance(this.lang, this.conf)
	saskia = AskSaskia.newInstance(this.lang, this.conf)
	
	// preload first batch of rules
	number = new NumberRulesEN()
	time = new TimeGroundingRulesEN()
	value = new ValueRulesEN()
	nerules = new NERulesEN()    
	ierules = new InternalEvidenceRulesEN()

	ee_loc = new LocalRulesEN()
	ee_org = new OrganizationRulesEN()
	ee_per = new PersonRulesEN()
	ee_abs = new AbstractionRulesEN()
	ee_thi = new ThingRulesEN()
	ee_eve = new EventRulesEN()
	ee_mas = new MasterpieceRulesEN()
	
	ee2 = new SecondExternalEvidenceRulesEN()
	
	split = new SplitNERulesEN(ierules, saskia)
	classes = new SecondHAREMClassificationLabelsEN()
    }

	/**
	 * This is the main method to release on the document, to tag it.
	 * @param document Untagged document.
	 * @return Tagged document.
	 */
	public Document releaseRembrandtOnDocument(Document document) {

	    def capturedNEs 
	    def start1 = System.currentTimeMillis()
	    log.info "Unleashing REMBRANDT on document ${document}..."

	    if (document.title && !document.isTitleTokenized()) document.tokenizeTitle()
	    if (document.body && !document.isBodyTokenized()) document.tokenizeBody()
	    if (document.title && !document.isTitleIndexed()) document.indexTitle()	   
	    if (document.body && !document.isBodyIndexed()) document.indexBody()

	    document.titleNEs.court = court
	    document.bodyNEs.court = court   

	    /**************/
	    /* PRE-GROUND */
	    /**************/
	    
	    log.debug "Document has ${document.title_sentences.size()} title sentences and ${document.body_sentences.size()} body sentences"
	    // if the document comes with forced NEs, let's handle them first 
	    if (!document.forcedNEs.isEmpty()) {
		log.trace "Got ${document.forcedNEs.size()} forced NEs to handle first."
		document.forcedNEs.each{ne -> 
		   ne = saskia.answerMe(ne)
		   if (ne?.hasKnownClassifications()) { 
		        ne.reportToHistory "SASKIA: ACTION: forcedNE (ne.link)"
	    		document.bodyNEs << ne // no need for JustAdd...	
		   }
		}
	    }
	    def start2 = System.currentTimeMillis()	
	    log.debug( "Done pre-grounded NE in "+(start2-start1)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "
		    

	    /*****************************************************/
	    /* BASIC INTERNAL EVIDENCE - NUMBER, TIME, VALUE, NE */
	    /*****************************************************/

	    number.processDoc(document)     			   
	    start1 = System.currentTimeMillis()
	    log.debug( "Done number-hunting in "+(start1-start2)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "
	     
	    time.processDoc(document)    
	    start2 = System.currentTimeMillis()
	    log.debug( "Done time-bashing in "+(start2-start1)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "
	    
	    value.processDoc(document)    
	    start1 = System.currentTimeMillis()
	    log.debug( "Done value-fetching in "+(start1-start2)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "
	    
	    // find everything that smells like NE
	    nerules.processDoc(document)    	    
	    nerules.eliminateStopwordNEs(document)
	    nerules.readjustNEsBeginningSentences(document)
	    start2 = System.currentTimeMillis()
	    log.debug( "Done NE-beaming in "+(start2-start1)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "

	    /*******************************************/
	    /* ADVANCED INTERNAL EVIDENCE - SASKIA, IE */
	    /*******************************************/
	    
	    // for those NEs with unknown classification, let's go for a AskSaskia + Internal Evidence
	    doSaskiaAndInternalEvidence(document.titleNEs)
	    doSaskiaAndInternalEvidence(document.bodyNEs)
	    
	    start1 = System.currentTimeMillis()
	    log.debug( "Done Saskia + Internal Evidence in "+(start1-start2)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "

	    /************************************/
	    /* FIRST ROUND OF EXTERNAL EVIDENCE */
	    /************************************/

	    log.debug( "Starting first round of external evidences.")
	    long start3 = start2
	    
	    // now, find external Evidence
	    ee_loc.processDoc(document)  
	    start1 = System.currentTimeMillis()
	    log.debug( "Done LOCAL external evidences in "+(start1-start2)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "

	    start2 = System.currentTimeMillis()
	    ee_org.processDoc(document)
	    log.debug( "Done ORGANIZACAO external evidences in "+(start2-start1)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "

	    start1 = System.currentTimeMillis()
	    ee_per.processDoc(document)
	    log.debug( "Done PESSOA external evidences in "+(start1-start2)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "

	    start2 = System.currentTimeMillis()
	    ee_abs.processDoc(document)
	    log.debug( "Done ABSTRACCAO external evidences in "+(start2-start1)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "

    	    start1 = System.currentTimeMillis()
	    ee_thi.processDoc(document)
	    log.debug( "Done COISA external evidences in "+(start1-start2)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "

	    start2 = System.currentTimeMillis()
	    ee_eve.processDoc(document)
	    log.debug( "Done ACONTECIMENTO external evidences in "+(start2-start1)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "

	    start1 = System.currentTimeMillis()
	    ee_mas.processDoc(document)
 	    log.debug( "Done OBRA external evidences in "+(start1-start2)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "
   
	    log.debug( "Done fist round of external evidences Total: "+(start1-start3)/1000.0+" secs.")
	    log.debug( "Now, checking now splittings and ALTs.")
	    
	    /**********************/
	    /* SPLITTING DETECTOR */
	    /**********************/
	    
	    // I'll clone the NE list, so that I can iterate on a list and modify the original, 
	    // avoiding the ConcurrentModificationException. 
	    // The splitter might remove or add NEs on the original ListOfNE, so beware.
	    // restrict no the Nes that have the gluer
	    ListOfNE cloned_titleNEs = document.titleNEs.clone()
	    ListOfNE cloned_bodyNEs =  document.bodyNEs.clone()
	    
	    cloned_titleNEs.findAll{ne -> 
		 !ne.matchesClassification([SC.time], CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category) && 
	      ne.terms.find{term -> term.text ==~ Patterns.gluerPattern[this.lang]}
	    }.each{ne -> split.processNE(ne, document.titleNEs)}
	    cloned_bodyNEs.findAll{ne -> 
		 !ne.matchesClassification([SC.time], CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category) && 
	      ne.terms.find{term -> term.text ==~ Patterns.gluerPattern[this.lang]}
	    }.each{ne -> split.processNE(ne, document.bodyNEs)}
	
	    start2 = System.currentTimeMillis()
	    log.debug( "Done re-splitting and ALTs in "+(start2-start1)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "


	    /*************************************/
	    /* SECOND ROUND OF EXTERNAL EVIDENCE */
	    /*************************************/

	    //ee2.processDoc(document)    
	   
	    
	    /************************/
	    /* REORGANIZING NE LIST */
	    /************************/
	    
	    // Now, I have to sort the NEs and label them.
	    document.titleNEs.sortNEs()
	    document.bodyNEs.sortNEs()

	    document.titleNEs.labelNEs()		 
	    document.bodyNEs.labelNEs()

	    /*****************************/
	    /* ENTITY RELATION DETECTION */
	    /*****************************/

	    log.info "Doing entity relation: "+conf.getBoolean("rembrandt.core.doEntityRelation", true)
	    if (conf.getBoolean("rembrandt.core.doEntityRelation", true)) {
		// now, to find external Evidences, I'll index the Named Entities
		
		ercore = new EntityRelationCore(this.lang, this.rules)			
		ercore.detectEntityRelations(document.bodyNEs, document.body_sentences)    		
		start1 = System.currentTimeMillis()
		log.debug "Done entity relation for ${document.bodyNEs.size()} body NEs in "+(start1-start2)/1000.0+" secs."
		log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "
	    } // if rembrandt.core.doentityrelation == true
		      
        
        /****************/
        /* RESCUING NEs */
        /****************/
        
        List<NamedEntity> NEsToRemove = []
        
        // note: I'm filtering the list to all unknown NE, so I have to indexOf the ne to find its index                         
        document.bodyNEs.findAll{it.hasUnknownClassification()}.each{ne -> 
            
            int thisindex = document.bodyNEs.indexOf(ne)
            /* PLAN A - IDENTITY relations */
            List<NamedEntity> identNEs = document.bodyNEs.findAll{
                it.hasRelationOfType(ne, "ident") &&  !(it.hasUnknownClassification())}
            if (identNEs) identNEs.each{ 
                document.bodyNEs[thisindex].replaceClassificationFrom(it) 
                document.bodyNEs[thisindex].reportToHistory("PLAN: A ACTION: Replaced classification from NE $it")
            }
            
            /* PLAN B - suspect proper names */
            ne = document.bodyNEs[thisindex] // refresh the ne variable
            if (ne.hasUnknownClassification()){ // always check it -- I may have already changed it
                if (suspectedAsProperName(ne)) {	
                    SemanticClassification sc = new SemanticClassification(classes.category.person, classes.type.individual)
                    document.bodyNEs[thisindex].mergeClassification(sc)        				      	    
                    // and recheck if we have any leftover.
                    List<NamedEntity> otherNEs = document.bodyNEs.findAll{
                        it.equalsTerms(ne) && !it.matchesBoundaries(ne, BoundaryCriteria.ExactMatch) && 
                        it.hasUnknownClassification()}
                    if (otherNEs) { otherNEs.each{ otherne -> 
                            log.trace "Adding IDENTITY relation to ${otherne} for $ne"
                            int otherindex = document.bodyNEs.indexOf(otherne) 	
                            document.bodyNEs[otherindex].addRelation(document.bodyNEs[thisindex], EntityRelation.SetIdentity.text)
                            document.bodyNEs[otherindex].mergeClassification(sc)
                        }
                    }		   
                }// if suspectedAsProperName	      
            }//plan B
            
            /* deleting remaining ones */     
            ne = document.bodyNEs[thisindex] // refresh it
            if (ne.hasUnknownClassification()) { // I tried... 		 
                if (conf.getBoolean("rembrandt.core.removeRemainingUnknownNE", true)) {
                    NEsToRemove += ne 
                    log.trace ("Removing NE ${ne}. Reason: We don't know what is it.")
                }	
                if (conf.getBoolean("rembrandt.core.removeTextualNumbers", false)) {
                    if (ne.matchesClassification([SC.number_textual],[CC.AllOfThese, CC.AllOfThem, CC.Type]))
                        NEsToRemove += ne
                }
                
            }
        }//document.bodyNEs.findAll{
        
        document.bodyNEs.removeNEs(NEsToRemove)
        
	        	    
	    /****************/
	    /* CLEAN-UP NEs */
	    /****************/

	 //check for overlapping NEs, like <NE1> sdf sdf <NE2> dsfg </NE1> sdf sdf </NE2>
	 
	    NEsToRemove = []
	    	    
	    document.body_sentences.each{sentence -> 
	        List<NamedEntity> NEList = document.bodyNEs.getNEsBySentenceIndex(sentence.index)
	        if (NEList.size() >= 2) {
	            for(int i = 0; i<NEList.size()-1; i++) {
	        	for (int j = i+1; j<NEList.size(); j++) {
	        	    def ne1 = NEList[i]
	        	    def ne2 = NEList[j]

	        	      if (ne1.matchesBoundaries(ne2, BoundaryCriteria.ExactOrContains) || 
	        		  ne2.matchesBoundaries(ne1, BoundaryCriteria.ExactOrContains)) {
	        		  if (!ne1.alt && !ne2.alt) {
	        		      if (ne1.terms.size() >= ne2.terms.size()) NEsToRemove += ne2
	        		      if (ne2.terms.size() > ne1.terms.size()) NEsToRemove += ne1	
	        		  }
	        	      }
	        	      if (ne1.matchesBoundaries(ne2, BoundaryCriteria.Overlapping)) {
	        		  if (!ne1.alt && !ne2.alt) {
	        		      if (ne1.terms.size() >= ne2.terms.size()) NEsToRemove += ne2
	        		      if (ne2.terms.size() > ne1.terms.size()) NEsToRemove += ne1	
	        		  }
	        	      }//Overlapping				 				
	        	}// for j
	            }// for i
	  	}// at least 2 NE
	    }// doc each sentence
	    
	    if (NEsToRemove) {
		log.warn "NEs that are not in conditions, and should be removed: ${NEsToRemove}"
		document.bodyNEs.removeNEs(NEsToRemove)
	    } 
	  	 

	    /**
	     * Cleanup: sync relations
	     */
	    for (int i = 0; i < document.bodyNEs.size(); i++) {
	    	List relation = document.bodyNEs[i].corel.keySet().toList()
	    	if (relation) {
	    	    for (int j = 0; j<relation.size(); j++) { 
	    	       NamedEntity relNE = document.bodyNEs.getNEbyID(relation[j])
	    	       if (!relNE) document.bodyNEs[i].corel.remove(relation[j])
	    	    }
	    	}
	    }
  	    log.debug "document done."
	    return document 
	}
  
	
    private doSaskiaAndInternalEvidence(ListOfNE NEs) {
        
        NEs.eachWithIndex {ne, i -> 	    
            
            // do this only for unknown NEs, but let's keep the index 'i' unchanged
            if (ne.hasUnknownClassification()) {
                NamedEntity newNE = saskia.answerMe(ne)
                
                if (newNE?.hasKnownClassifications() && newNE != ne) {
                    NEs[i].replaceClassificationFrom(newNE)
                    NEs[i].replaceAdditionalInfoFrom(newNE)
                    NEs[i].reportToHistory("SASKIA1: ACTION: Replaced classification from NE $newNE")
                    
                } else {  
                    newNE = ierules.processInternalEvidenceOnNE(ne) 
                    if (newNE?.hasKnownClassifications() && newNE != ne) {   
                        NEs[i].replaceClassificationFrom(newNE) 
                        NEs[i].reportToHistory("IE1: ACTION: Replaced classification from NE $newNE")
                    } 
                }
                
                // another hack: perhaps it has a preposition in the beginning 
                // (A VW, A Câmara). Remove and see what happens.
                // Or preposition in the end, due to bad EM agglomeration and sentence division.
                // let's test bigger than 1, otherwise we may remove the element, it's not funny to have a termless NE around.
                if (NEs[i].hasUnknownClassification() && NEs[i].terms.size() > 1)  {
                    if (StopwordsEN.beginningStopwordList.contains(NEs[i]?.terms[0]?.text)) {
                        NEs[i].termIndex++
                        NEs[i].terms.remove(0)
                    } else if (StopwordsEN.beginningStopwordList.contains(NEs[i]?.terms[-1]?.text)) {
                        NEs[i].terms.remove(NEs[i].terms[-1])
                    } else if (NEs[i]?.terms[-1]?.text ==~ /\d+/) { // or it has a useless number
                        NEs[i].terms.remove(NEs[i].terms[-1])
                    }
                    NEs[i].reportToHistory("MAIN: ACTION: Trimmed NE, now is ${NEs[i]}")
                    
                    // do it all again. 
                    NamedEntity ne2 = NEs[i].clone()
                    newNE = saskia.answerMe(ne2)		 
                    if (newNE && !newNE.hasUnknownClassification() && newNE != ne2) {
                        NEs[i].replaceClassificationFrom(newNE)
                        NEs[i].replaceAdditionalInfoFrom(newNE)
                        NEs[i].reportToHistory("SASKIA2: ACTION: Replaced classification from NE $newNE")
                        
                    } else {  
                        newNE = ierules.processInternalEvidenceOnNE(ne2) 
                        if (newNE && !newNE.hasUnknownClassification() && newNE != ne2) {   
                            NEs[i].replaceClassificationFrom(newNE) 
                            NEs[i].reportToHistory("IE2: ACTION: Replaced classification from NE $newNE")   			    
                        } 
                    }
                }
            }
        }   	     	    
    }
    
    	
	public boolean suspectedAsProperName(NamedEntity ne) {
	    def nameTerms = 0
	   
	    ne.terms.each{term -> 
	      	if (PersonGazetteerEN.firstName.contains(term.text) ||
	      		PersonGazetteerEN.lastName.contains(term.text)) {nameTerms++}
	      	// if we have enough names	    
	    }
	    if (ne.terms && ((nameTerms * 1.0)/(ne.terms.size() * 1.0)) > 0.49) return true
	    return false	      
	}		
}