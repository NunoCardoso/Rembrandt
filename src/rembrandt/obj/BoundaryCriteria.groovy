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
 * Criteria for Named Entity boundaries.
 */
enum BoundaryCriteria {
    /** first and last match*/
    ExactMatch, 
    
    /** Is contained by, and does not share boundaries. */
    IsContainedByAndCenterJustified,

    /** Is contained by, and aligned with the beginning */
    IsContainedByAndLeftJustified,
    
    /** Is contained by, and aligned with the end */
    IsContainedByAndRightJustified,
    
    /** Is contained by, gathers all cases above. */
    IsContainedBy,
    
    /** Contained in, and does not share boundaries. */
    ContainsAndCenterJustified,

    /** Contained in, and aligned with the beginning */
    ContainsAndLeftJustified,
    
    /** Contained in, and aligned with the end */
    ContainsAndRightJustified,
    
    /** contains, spanning all above */
    Contains,
    
    /** exact or contains */
    ExactOrContains,
    
    /** exact or contained by */
    ExactOrIsContainedBy,
    
    /**shares terms, but is overlapping like a staircase */
    Overlapping 
}