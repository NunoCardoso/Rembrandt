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
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for OBRA category.
 */
class MasterpieceGazetteerPT {
	      
     /* OBRA PLANO */
     
     static final List<Pattern> publication = [~/[Aa]rtigo/, ~/[Cc]ódigos?/, ~/[Dd]eclaraç(?:ão|ões)/,
          ~/[Dd]irec?tiva/, ~/[Dd]ecreto-[Ll]ei/, [~/[Dd]ecreto/,~/[Ll]ei/], ~/[Ll]ei/,~/[Nn]ormas?/,
          ~/[Oo]rçamento/,~/[Oo]f[ií]cio/,~/[Pp]ublicação/,~/[Rr]egras?/,~/[Rr]esolução/,~/[Tt]ratado/]
        
     /* OBRA REPRODUZIDA */

     static final List<Pattern> entertainment = [~/[Ll]ivro/,~/[Ff]ilme/,~/[Áá]lbum/]
      
     /* OBRA ARTE */

     static final List<Pattern> masterpiece = [~/[Ee]státua/, ~/[Qq]uadro/]
          
     /** CLAUSES **/
     
     static final Clause publication1nc = Clause.newConcept1Clause(publication, "publication",false)
		
     static final Clause entertainment1nc = Clause.newConcept1Clause(entertainment, "entertainment",false)
		
     static final Clause masterpiece1nc = Clause.newConcept1Clause(masterpiece, "masterpiece",false)
		
     static final Clause projecto1nc = Clause.newRegex1Clause(~/projec?to/, "projec?to",false)

}