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

import java.util.regex.Pattern
import rembrandt.obj.Clause

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for MASTERPIECE category.
 */
class MasterpieceGazetteerEN {
    
    /* MASTERPIECE PLAN  */
    
    static final List<Pattern> decree = [~/[Aa]rtice/,~/[Cc]ode/,~/[Dd]eclaration/,~/[Dd]irective/,
    ~/[Dd]ecree/,~/[Ll]aw/,~/[Nn]orm/, ~/[Pp]ublication/,~/[Rr]ule/,~/[Rr]esolution/,~/[Tt]reaty/]
    
    static final List<String> project = ['project']
    
    /* MASTERPIECE REPRODUCED */
    
    static final List<Pattern> entertainment = [~/[Bb]ook/,~/[Mm]ovie/,~/[Aa]lbum/]
    
    /* MASTERPIECE WORKOFART */
    
    static final List<Pattern> masterpiece = [~/[Ss]tatue/,~/[Pp]ainting/]
    
    /** CLAUSES **/
    
    static final Clause decree1nc = Clause.newConcept1Clause(decree, "decree",false)
    
    static final Clause project1nc = Clause.newConcept1Clause(project, "project",false)
    
    static final Clause entertainment1nc = Clause.newConcept1Clause(entertainment, "entertainment concept",false)
    
    static final Clause masterpiece1nc = Clause.newConcept1Clause(masterpiece, "masterpiece concept",false)
}