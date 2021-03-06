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

import rembrandt.obj.Document
import rembrandt.obj.Sentence
import rembrandt.obj.NamedEntity
import rembrandt.obj.ListOfNE
import rembrandt.bin.*
import rembrandt.util.XMLUtil

/**
 * @author Nuno Cardoso
 * This class prints NEInfo
 */
class UnformattedWriter extends Writer { 
    
    // setting tags to null makes them not printable
    String bodyTag = null
    String headTag = null
    String docTag = null
    
    // let's newline the sentences
    String afterSentenceEnd = "\n"
    
    public UnformattedWriter(StyleTag style) {
		super(style)
    }  
}