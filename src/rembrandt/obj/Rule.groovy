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
 * Rule class. Stores a list of clauses.
 */
class Rule {

  /** The list of clauses */
  List<Clause> clauses
  
  /** identification of the rule */
  String id, description
  
  /** Semantic classification to copy when rule is matched, and RulePolicy=Rule */
  SemanticClassification sc
  
  /** RulePolicy, to define if the actions are controlled by the rule or the clause */
  RulePolicy policy
  
  /** Addpolicy: sets adding policies to the NEs, in order to dictate the addition type */
  ConflictPolicy addpolicy
  
   /** define the type of action that this rule performs. */
  /** It can be generate an NE, get Meanings, etc. */
  def action
  
  public String toString() {return id + ((description != null)? "("+description+")" : "") }
}