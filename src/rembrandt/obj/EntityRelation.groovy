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
 *  
 * This is an Enumeration with Entity Relation Actions for the Entity Relation Detection.
 * It stores all types of relations to be labelled on NEs. 
 */
 enum EntityRelation {
    
  /** Collect the ID of thie NE, as the target NE */
  CollectID(""),
  
  /** Set an identity relation */
  SetIdentity("sameAs"),
  
  /** Set an included relation */
  SetIncludedBy("incluido"),
  
  /** Set an includes relation */
  SetIncludes("inclui"),
  
  /** Set a based on relation */
  SetBasedOn("sede_de"),
  
  /** Set an ocuurs on relation */
  SetOccursOn("ocorre_em"),
  
  /** Set an other relation */
  SetOther("outro")

  private final String text 
	
  EntityRelation(String text) {
       this.text = text
  }
 
  public String text() { return text }		 
}