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

/**
 * @author Nuno Cardoso
 * A style tag that does nothing, good for plain texts.
 */
class UnformattedStyleTag extends StyleTag {
    
    String openTagSymbol = "<"
    String closeTagSymbol = ">"
    String openSentenceSymbol = ""
    String closeSentenceSymbol = ""
    String openTermSymbol = ""
    String closeTermSymbol = ""	
    String lang
    
    public UnformattedStyleTag(String lang) {
	this.lang=lang
    }
    /**
     * Printing the open tag of a NE
     */
    public String printOpenTag(NamedEntity ne) {return ""}
    
    /**
     * Printing the close tag of a NE
     */
    public String printCloseTag(NamedEntity ne) {return ""}
    
    /**
     * Parse an open tag. Returns null
     */
    public NamedEntity parseOpenTag(String tag) {return null}
    
    /**
     * Print an open ALT tag
     */
    public String printOpenALTTag() {return ""}
    
    /**
     * Print a close ALT tag
     */    
    public String printCloseALTTag() {return ""}
    
    /**
     * Print an open sub ALT tag
     */
    public String printOpenSubALTTag(int index) {return ""}
 
    /**
     * Print a close sub ALT tag
     */  
    public String printCloseSubALTTag(int index) {return ""}
    
    /**
     * Checking if the tag can be an opening tag from this StyleTag
     */
    public boolean isOpenTag(String tag) {return false}

    /**
     * Checking if the tag can be a closing tag from this StyleTag
     */
    public boolean isCloseTag(String tag)  {return false}

    /**
     * Checking if the tag can be an opening ALT tag from this StyleTag
     */
    public boolean isOpenALTTag(String tag) {return false}

    /**
     * Checking if the tag can be a closing ALT tag from this StyleTag
     */
    public boolean isCloseALTTag(String tag) {return false}

    /**
     * Checking if the tag can be an opening subALT tag from this StyleTag
     */
    public boolean isOpenSubALTTag(String tag) {return false}

    /**
     * Checking if the tag can be a closing subALT tag from this StyleTag
     */
    public boolean isCloseSubALTTag(String tag)  {return false}
}