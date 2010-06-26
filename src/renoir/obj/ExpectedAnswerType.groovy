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
package renoir.obj

import rembrandt.obj.SemanticClassification

public class ExpectedAnswerType {

	// classification of the EAT
	List<SemanticClassification> categoryHAREM = []
	List<String> DBpediaOntologyClasses = []
	List<String> DBpediaOntologyResources = [] // think of it as Wikipedia categories
	
	// if the EAT resolves to an infobox property, or a wikipedia class
	public enum Type {
	    Property, 
	    DBpediaOntologyResource, 
	    Definition, 
	    HAREMCategory, 
	    DBpediaOntologyClass
	}

	Type resolvesTo
	
	public String toString() {
		return "(resolvesTo:$resolvesTo, categoryHAREM:$categoryHAREM, DBpediaOntologyClasses:$DBpediaOntologyClasses, DBpediaOntologyResources:$DBpediaOntologyResources)"
	}
}