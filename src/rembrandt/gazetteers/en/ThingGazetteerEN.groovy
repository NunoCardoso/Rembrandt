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
 
package rembrandt.gazetteers.en

import rembrandt.obj.Clause

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for THING category.
 */
class ThingGazetteerEN {
     
     /** LISTS **/
     
     /* THING CLASS */
     
    static final List classIncluded = ['constant']
    static final List classExcluded = ['product','caliber'] 
    
    /* THING SUBSTANCE */
 
    static final List substanceIncludePrefix = [~/vitamins?/] // collectables
    static final List substanceExcludePrefix = ['substance','protein'] // not collectables
       
     /** CLAUSES **/
     
    /* THING CLASS */

    static final Clause class1c = Clause.newConcept1Clause(classIncluded, "class prefix", true)
    static final Clause class1nc = Clause.newConcept1Clause(classExcluded, "class prefix", false)
    
    /* THING SUBSTANCE */ 
	
    static final Clause substance1c = Clause.newConcept1Clause(substanceIncludePrefix,"thingSubstanceIncludePrefix",true)		
    static final Clause substance1nc = Clause.newConcept1Clause(substanceExcludePrefix,"thingSubstanceExcludePrefix",false)
}