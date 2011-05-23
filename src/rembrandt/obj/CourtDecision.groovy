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

/**
 * @author Nuno Cardoso
 * CourtDecisions are veredicts issued by the Courthouse.
 */
enum CourtDecision {

    /**
     * Plaintiff is right. Replace Defendant on its classification and boundaries
     */
    ReplaceCompletelyDefendant,

    /**
     * Plaintiff is right, but just replace Defendant's classification 
     */
    ReplaceClassificationOfDefendant_KeepBoundaries,
    ReplaceClassificationOfDefendant_UpdateBoundaries,

    /** 
     * Merge both into a single named entity 
     */
    MergeClassificationToDefendant_KeepBoundaries,
    MergeClassificationToDefendant_UpdateBoundaries,

    /**
     * Disambiguate the Defendant's classification with the one with the plaintiff
     * Imagine the defendant has several classifications, from a Wikipedia disambiguation page.
     * If plaintiff is an external evidence, use it to filter the defendant's classifations, 
     * Keeping its Wikipedia and DBpeida grounding information
     */
    JustDisambiguateClassificationOfDefendant,
    
    /**
     * same as above, but if disambiguation wipes the classification, act as a 
     * replaceClassificationOfDefendente
     */
    
    TryDisambiguateDefendant_CatchReplaceCompletely,
    
    TryDisambiguateDefendant_CatchReplaceClassification_KeepBoundaries,
    TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries,
    
    TryDisambiguateDefendant_CatchMergeClassification_KeepBoundaries,
    TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries,
  
    /**
     * Plaintiff is wrong. Leave Defendant alone, and acknowledge this
     */     
    DiscardPlaintiff,
    
    /**
     * Just add plaintiff (there were no defendants!) 
     */
    AddPlaintiff

}