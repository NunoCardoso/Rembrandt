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
 
package rembrandt.gazetteers.pt

import rembrandt.obj.Clause

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for COISA category.
 */
class ThingGazetteerPT {
     
     /** LISTS **/
     
     /* COISA CLASSE */
     
    static final List classExclude = [~/produtos?/,'calibre']  // no 'de' afterwards    
    
    static final List classInclude = ['constante'] // accepts a 'de' afterwards
    
    /* COISA SUBSTANCIA */
 
    static final List substanceInclude = ['vitaminas'] // collectables
    
    static final List substanceExclude = [~/subst‰ncias?/, ~/prote’nas?/] // collectables
       
     /** CLAUSES **/
     
    /* COISA CLASSE */

    static final Clause classExclude1nc = Clause.newConcept1Clause(classExclude,"COISA CLASSE prefix",false)

    static final Clause classInclude1c = Clause.newConcept1Clause(classInclude,"COISA CLASSE prefix")
	
	/* COISA SUBSTANCIA */ 
    static final Clause substanceInclude1c = Clause.newConcept1Clause(
		substanceInclude,"COISA SUBSTANCIA prefix collectable",true)
		
    static final Clause substanceExclude1nc = Clause.newConcept1Clause(
		substanceExclude,"COISA SUBSTANCIA prefix not collectable",false)
}