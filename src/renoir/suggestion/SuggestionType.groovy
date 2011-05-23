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

package renoir.suggestion

/**
 *  Semantic wrapper for a text expression 
 */

enum SuggestionType {
     
     WhereQuestion("WQ"),
     
     Predicate("PR"), 
     
     Operator("OP"),
     
     NamedEntity("NE"),
     
     Geoscope("GS"),
     
     DBpediaClass("DB"),
     
     WikipediaCategory("WC"),
     
     WikipediaGroupOfCategories("WG")
   
     private final String text
     	
     SuggestionType(String text) {
	   this.text = text
	 }
     public String text() {return this.text}
	
	 static SuggestionType getFromValue(String text) {
       
	    return values().find{it.text == text}
	}
	
	 public String toString() { return name() }
 }
 