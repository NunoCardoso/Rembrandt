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

package rembrandt.io

import rembrandt.obj.NamedEntity
import rembrandt.obj.SemanticClassification
import java.util.regex.Matcher
import saskia.dbpedia.DBpediaResource
import rembrandt.gazetteers.SemanticClassificationDefinitions
/**
 * @author Nuno Cardoso
 * The ACDCStyleTag is exactly the same as RembrandtStyleTag, except that we override the 
 * sentence open and close symbols to <s>\n and </s>\n
 */
class ACDCStyleTag extends RembrandtStyleTag {
  
    /**
     * Public constructor.
     * @param lang the language of the labels. Default: rembrandt.output.styletag.lang, or 
     * global.lang if it's not specified.
     */
    public ACDCStyleTag(String lang) {
	super(lang) 
    }
}