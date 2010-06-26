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
 * Criteria for comparing classification between named entities.
 * there are two actors: THIS NE and THAT NE (or semantic classification).
 *  -  The restrictions for the cardinality of THIS NE are given by the LEFT half from '_' of the criteria name
 *  -  The restrictions for the cardinality of THAT NE are given by the RIGHT half from '_' of the criteria name
 *   
 * As THIS NE and THAT NE can have one or more semantic classifications, labelled as:
 * 
 *    Cardinality			this NE  			that NE                 
 *  - All of them (A)			AllOfThese			AllOfThem
 *  - Exists one of them (E)	        ExistsAtLeastOneOfThese		ExistsAtLeastOneOfThem
 *  - Never Exists one of them (!E)     NeverExistsAtLeastOneOfThese	NeverExistsAtLeastOneOfThem
 *  - Never All of them (!A) 		NeverAllOfThese			NeverAllOfThem
 *  
 *  There's also the depth criteria level, that has Category, Type and Subtype values
 * 
 */
enum ClassificationCriteria {
     
    /** allow any category */
    AnyKnownOrUnknownCategory, 
 
    /** allow any known category. Forces to be instantiated into a good category*/
    AnyKnownCategory, 
    
    AllOfThese,
    AllOfThem,
    
    ExistsAtLeastOneOfThese,
    ExistsAtLeastOneOfThem,
    
    NeverExistsAtLeastOneOfThese,
    NeverExistsAtLeastOneOfThem,
    
    NeverAllOfThese,
    NeverAllOfThem,
    
    Category,
    Type,
    Subtype
}