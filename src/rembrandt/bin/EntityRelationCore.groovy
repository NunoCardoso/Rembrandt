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

import rembrandt.obj.SemanticClassification
import rembrandt.obj.ClassificationCriteria as CC
import rembrandt.obj.ListOfNE
import rembrandt.obj.Sentence
import rembrandt.obj.EntityRelation
import rembrandt.obj.NamedEntity
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.rules.EntityRelationDetector
import org.apache.log4j.Logger
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * 
 * This class detects relations between NEs. This should be called after releasing 
 * a RembrandtCore class to the document, to take advantage of the detected categories.
 */
class EntityRelationCore {

   Configuration conf
   String lang
   EntityRelationDetector entityRelationRules
   static Logger log = Logger.getLogger("EntityRelationMain")

   /**
    * Main Constructor.
    */
   public EntityRelationCore(String lang, String rules) {
       this.lang=lang
       this.conf = Configuration.newInstance()
       this.entityRelationRules = Class.forName(
	       "rembrandt.rules.${rules}.${lang}.EntityRelationRules"+lang.toUpperCase()).newInstance()
   }
    
   /**
    * Performs entity relation on a document and returns a processed 
    * @param document A document to be parsed.
    * @return parsed document.
    */
    public detectEntityRelations(ListOfNE NEs, List<Sentence> sentences) {     
 
        if  (NEs.size() < 2) {
	   log.debug "ListOFNE given has only ${NEs.size()} NEs, returning."
	   return
       }
       if (!NEs.hasIndexOfTerms()) NEs.createTermIndex()

       //println "Dup: "+NEs.index.dump()
       NEs.eachWithIndex{ne1, index1 -> 
           // this will match NEs with exact or bigger text that is a \" \" match. Oh, and remove ne1 index!
	   log.debug "Detecting relations for NE $ne1, index $index1"
	   List matchingNEs = NEs.index.getNeIDsforQueryTerms(ne1.terms) - NEs.indexOf(ne1)

           if (matchingNEs) log.trace "matching NEs: $matchingNEs"        
           matchingNEs.each{index2 -> 
            
/*******************
 ** ER1. IDENTITY **
 *******************/
           	
           	NamedEntity ne2 = NEs[index2]
           	log.trace "Comparing NE1 $ne1 to NE2: $ne2 index: $index2" 
           	log.trace "ne2 history: \n"+ne2.printHistory()
           	if (ne1.equalsTerms(ne2) && !ne2.hasRelationWith(ne1)) {
           	    if (ne1.matchesClassification( [SC.time, SC.number, SC.value], 
                       CC.NeverExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category)) { 
           		//Don't waste time on IDing TEMPO, DATA e NUMERO
           		NEs[index1].addRelation(NEs[index2], EntityRelation.SetIdentity.text)   
           		NEs[index1].reportToHistory "RULE: ER1 ACTION: add ER-ident NE: ${ne2} "
           		log.debug "1. IDENTITY: NE ${ne1}(i=${index1}) and NE ${ne2}(i=${index2}) have the same terms."
           	    }
           	}
  
 
//if they have the same Wikipedia page number
 /*if ( ne1.wikipediaPage?.keySet()?.intersect(ne2.wikipediaPage?.keySet()) ) {
	if (!(ne1.matchesClassification( [Classes.category.time, Classes.category.number, 
	Classes.category.value],ClassificationCriteria.AnyCategoryIn))) { 
	    log.debug( "2. Identity: NE ${ne1}(i=${i}) and NE ${ne2}(j=${j}) have same wikipediaPage Id.")
		NEs[i].addRelation(NEs[j], EntityRelation.SetIdentity.text)
		continue
	}
 }*/ 
 
/************************
 ** ER2. PARTIAL MATCH **
 ************************/

           	else if (ne1.partialTermMatch(ne2) && !ne2.hasRelationWith(ne1)) {
           	    // ne2 IS bigger
           	    if (ne1.matchesClassification( [SC.time, SC.number, SC.value], 
                CC.NeverExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category)) { 
			 			 	    
           		// 2.1 for ACONTECIMENTO|PLANO / LOCAL (ex :Jogos Olímpicos de Munique / Munique)
           		if ( (ne2.classification*.c?.contains(Classes.category.event) || 
           		     ne2.classification*.t?.contains(Classes.type.plan) ) && 
		             ne1.classification*.c?.contains(Classes.category.place) ) {
		  	  	NEs[index2].addRelation(NEs[index1], EntityRelation.SetOccursOn.text)
		  	  	NEs[index2].reportToHistory " RULE: ER2.1 ACTION: Add ER-setOccursOn NE: ${NEs[index1]}"
			  	log.debug( "2.1. OccursOn NE ${ne1}(i=${index1}) and NE ${ne2}(i=${index2}).")
			  	
			 // 2.2
           		} else if (ne2.classification*.c?.contains(Classes.category.organization) && 
			        ne1.classification*.c?.contains(Classes.category.place) ) {
			  	// note that i and j are swapped
			 	NEs[index1].addRelation(NEs[index2], EntityRelation.SetBasedOn.text)
			 	NEs[index1].reportToHistory "RULE: ER2.2 ACTION: Add ER-setBasedOn NE: ${NEs[index2]} "
				log.debug( "2.2. SetBasedOn NE ${ne2}(i=${index2}) and NE ${ne1}(j=${index1}).")
				
			// 2.3
           		} else if ( NEs[index2].classification*.c?.contains(Classes.category.place) && 
			     NEs[index1].classification*.c?.contains(Classes.category.person) ) {
			  	// Museu Nacional de Machado de Castro | Machado de Castro
			 	NEs[index1].addRelation(NEs[index2], EntityRelation.SetOther.text)
			 	NEs[index1].reportToHistory "RULE: ER2.3 ACTION: Add ER-setOther NE: ${NEs[index2]}"		 	
				log.debug( "2.3. SetOther NE ${ne2}(i=${index2}) and NE ${ne1}(j=${index1}).")
			
			// 2.4
           		} else if ( NEs[index2].classification*.s?.contains(Classes.subtype.construction) && 
			     NEs[index1].matchesClassification([SC.place_human_humanregion, SC.place_human_division, 
                            SC.place_human_country], CC.ExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Subtype) ) {
			  	// Museu Militar do Porto | Porto. Evitar erros de CONSTRUCAO / CONSTRUCAO 
			 	NEs[index2].addRelation(NEs[index1], EntityRelation.SetBasedOn.text)
			 	NEs[index2].reportToHistory "RULE: ER2.4 ACTION: Add ER-setBasedOn NE: ${NEs[index1]}"		 	
				log.debug( "2.4. SetBasedOn NE ${ne1}(i=${index1}) and NE ${ne2}(j=${index2}).")
				
           		} else if ( NEs[index2].classification*.s?.contains(Classes.subtype.construction) && 
				 NEs[index1].classification*.s?.contains(Classes.subtype.construction) ) {
				// Museu Militar do Porto | Museu Militar
				NEs[index2].addRelation(NEs[index1], EntityRelation.SetIdentity.text)
			 	NEs[index2].reportToHistory "RULE: ER2.5 ACTION: Add ER-ident NE: ${NEs[index1]}"					
				log.debug( "2.5. Identity NE ${ne1}(i=${index1}) and NE ${ne2}(j=${index2}).")
				
           		} else if ( (NEs[index2].classification*.t?.contains(Classes.type.individual) && 
				  NEs[index1].hasUnknownClassification() ) || 
				 (NEs[index1].classification*.t?.contains(Classes.type.individual) && 
				  NEs[index2].hasUnknownClassification() ) )  {
				// José XPTO -> XPTO
				NEs[index2].addRelation(NEs[index1], EntityRelation.SetIdentity.text)	
			 	NEs[index2].reportToHistory "RULE: ER2.6 ACTION: Add ER-ident NE: ${NEs[index1]}"								
				log.debug( "2.6. Identity NE ${ne2}(i=${index2}) and NE ${ne1}(j=${index1}).")
				if (NEs[index2].hasUnknownClassification())  NEs[index2].replaceClassificationFrom(NEs[index1])
				if (NEs[index1].hasUnknownClassification())  NEs[index1].replaceClassificationFrom(NEs[index2])
    
           		}  else {
	  // Rua Sampaio Pina -> Sampaio Pina. 
	  // O primeiro é Rua, e o segundo é EM incógnita, porque não alterar? 
          // Jorge Sampaio / Sampaio. 
	  // Heurística: as maiores EM são mais precisas.
           		    if (NEs[index1].hasUnknownClassification())  NEs[index1].replaceClassificationFrom(NEs[index2])
           		    NEs[index2].addRelation(NEs[index1], EntityRelation.SetIdentity.text)
         		    NEs[index2].reportToHistory "RULE: ER2.7 ACTION: Add ER-setIdentity NE: ${NEs[index1]}"		    
           		    log.debug( "2.7. SetIdentity NE ${ne2}(i=${index2}) and NE ${ne1}(j=${index1}).")
			}
           	    }
           	}
           }
       }
	
/**************
 ** 3. RULES **
 **************/
       
       // as the NEs are neatly sorted out, let's get pairs of [ne1, ne2] on the same sentence
       for (int i=0; i<NEs.size()-1; i++) {
	   if (NEs[i].sentenceIndex == NEs[(i+1)].sentenceIndex) {
               if( !(NEs[i].hasRelationWith(NEs[(i+1)])) ) {
        	   entityRelationRules.detectEntityRelation(NEs[i], NEs[(i+1)], sentences[NEs[i].sentenceIndex])
               }
	   }
       }
    }// method DETECT
}//class
