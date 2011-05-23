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
 * Conflict Policies are directions on how are we going to add a NE that 
 * can collide with remaining NEs that share terms in the same position. 
 */
enum ConflictPolicy {
    
    /**
     * Overwrite always. Good when splitting NEs and overwritting the first one
     */ 
    Overwrite,
    
    /**
     * If there are NEs already, the bigger one will survive 
     */ 
    OverwriteWithBigger, 
	
     /**
      * Just add, force it regardless of conflicts with other NEs.
      */ 
    JustAdd, 
	
     /**
      * Go for court, for a more intelligent decision.
      * this is the default conflict policy for all new NEs, if nothing is specified otherwise.
      */ 
     CourtBattle,
	
    /**
     *  add only if there is no conflict. If there is, skip writting
     */
     WriteIfNoExistingNEOverlapping, 
	
     /**
      * Produce an ALT. It is just like JustAdd, only that it introduces ALT information 
      * for both new NEs and remaining NEs, in order to establish a link. Good for 
      * possible vague interpretations of NEs. 
      */ 
     GenerateALT, 
     
     /** 
      * Merge classifications if it matches size
      */
     MergeClassificationIfMatchesPositionAndSize
}