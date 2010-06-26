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
 
package rembrandt.rules

import rembrandt.obj.BoundaryCriteria
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.obj.Clause
import rembrandt.obj.SemanticClassification
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality
import rembrandt.obj.Term
import rembrandt.obj.ListOfNE
import rembrandt.obj.Rule
import rembrandt.obj.RulePolicy
import java.util.regex.Pattern
import org.apache.log4j.Logger
/** 
 * @author Nuno Cardoso
 * This class is a MatcherObject for SplitNEDetectors. 
 * It just has some slots for temp variables. 
 * I don't trust Expandos.
 */
class SplitNEMatcherObject extends MatcherObject{
     
    
    String lang // Only SplitNERules and SplitNEDetector use this
    NamedEntity original_ne
    List<NamedEntity> split_nes
    List<NamedEntity> saskia_nes
    List<NamedEntity> ie_nes
  
}