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
import rembrandt.obj.ClassificationCriteria as CC
import rembrandt.obj.ConflictPolicy
import rembrandt.obj.EntityRelation
import saskia.bin.Configuration
import saskia.bin.AskSaskia
import rembrandt.rules.harem.en.*
import rembrandt.rules.NamedEntityDetector
import rembrandt.rules.SplitNEDetector
import rembrandt.gazetteers.Patterns
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.en.*
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * 
 * Core processing of Rembrandt, for Portuguese texts.
 */
class RembrandtCoreENOnlyTIME extends RembrandtCore {
    
     //Map m = Collections.synchronizedMap(new LinkedHashMap(...));
      
    static Logger log = Logger.getLogger("RembrandtMain")
    static final String lang = "en"
    Configuration conf
    static final Courthouse court = Courthouse.newInstance(lang, "harem")
    
    SecondHAREMClassificationLabelsEN classes   
    NamedEntityDetector number, time, value
    
    /**
     * Main constructor.
     * @param conf Configuration instance.
     */
    public RembrandtCoreENOnlyTIME(Configuration conf) {
	
	this.conf = (conf ? conf : Configuration.newInstance() )		
	
	// preload first batch of rules
	number = new NumberRulesEN()
	time = new TimeGroundingRulesEN()
	value = new ValueRulesEN()	
    }

	/**
	 * This is the main method to release on the document, to tag it.
	 * @param document Untagged document.
	 * @return Tagged document.
	 */
	public Document releaseRembrandtOnDocument(Document document) {
	   
	    long start1 = System.currentTimeMillis()
	    log.info "Unleashing REMBRANDT on document ${document}..."

	    if (document.title && !document.isTitleTokenized()) document.tokenizeTitle()
	    if (document.body && !document.isBodyTokenized()) document.tokenizeBody()
	    if (document.title && !document.isTitleIndexed()) document.indexTitle()	   
	    if (document.body && !document.isBodyIndexed()) document.indexBody()

	    document.titleNEs.court = court
	    document.bodyNEs.court = court   

	   
	    /*****************************************************/
	    /* BASIC INTERNAL EVIDENCE - NUMBER, TIME, VALUE, NE */
	    /*****************************************************/

	    number.processDoc(document)     			   
	    long start2 = System.currentTimeMillis()
	    log.debug( "Done number-hunting in "+(start2-start1)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "
	     
	    time.processDoc(document)    
	    start1 = System.currentTimeMillis()
	    log.debug( "Done time-bashing in "+(start1-start2)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "
	    
	    value.processDoc(document)    
	    start2 = System.currentTimeMillis()
	    log.debug( "Done value-fetching in "+(start2-start1)/1000.0+" secs.")
	    log.trace "Document title<#s:${document.title_sentences.size()},#ne:${document.titleNEs.size()}> body<#s:${document.body_sentences.size()},#ne:${document.bodyNEs.size()}> "
	
	    // Now, I have to sort the NEs and label them.
	    document.titleNEs.sortNEs()
	    document.bodyNEs.sortNEs()

	    document.titleNEs.labelNEs()		 
	    document.bodyNEs.labelNEs()

	   
	    /*************************/
	    /* DELETING NON-TIME NEs */
	    /*************************/

	    List<NamedEntity> NEsToRemove =  document.bodyNEs.findAll{ne ->            
	    return ne.matchesClassification([SC.time],[CC.ExistsAtLeastOneOfThese, CC.NeverAllOfThem, CC.Category])}
	    
	    document.bodyNEs.removeNEs(NEsToRemove)
	        	    	    
	    return document 
	}
}