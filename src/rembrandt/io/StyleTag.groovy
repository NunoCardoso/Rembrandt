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
 * Abstract class from where style tags must be extended.
 */
abstract class StyleTag {
    
    String NETag   // Label for NamedEntity tag
    String NEidAttr // Label for ID attribute
    String categoryAttr // Label for category attribute
    String typeAttr // label for type attribute
    String subtypeAttr // label for subtype attribute
    String relationAttr // label for relation ids attribute
    String relationTypeAttr // label for relation types attribute
    String commentAttr  // label for comments attribute
    
    // Basic symbols
    String openTagSymbol = "<"
    String closeTagSymbol = ">"
    String openSentenceSymbol = ""
    String closeSentenceSymbol = ""
    String openTermSymbol = ""
    String closeTermSymbol = ""
    
    public StyleTag() {}
    
    /**
     * Printing the open tag of a NE
     */
    public abstract String printOpenTag(NamedEntity ne) 
    
    /**
     * Printing the close tag of a NE
     */
    public abstract String printCloseTag(NamedEntity ne)
      
    /**
     * Print an open ALT tag
     */
    public abstract String printOpenALTTag()
    
    /**
     * Print a close ALT tag
     */    
    public abstract String printCloseALTTag()
    
    /**
     * Print an open sub ALT tag
     */
    public abstract String printOpenSubALTTag(int index)
 
    /**
     * Print a close sub ALT tag
     */  
    public abstract String printCloseSubALTTag(int index)

    /** 
     * Parse an open tag that begins a NamedEntity 
     */
    public abstract NamedEntity parseOpenTag(String tag) 

/** BOOLEANS **/
    
    /**
     * Checking if the tag can be an opening tag from this StyleTag
     */
    public abstract boolean isOpenTag(String tag)

    /**
     * Checking if the tag can be a closing tag from this StyleTag
     */
    public abstract boolean isCloseTag(String tag)

    /**
     * Checking if the tag can be an opening ALT tag from this StyleTag
     */
    public abstract boolean isOpenALTTag(String tag) 

    /**
     * Checking if the tag can be a closing ALT tag from this StyleTag
     */
    public abstract boolean isCloseALTTag(String tag) 

    /**
     * Checking if the tag can be an opening subALT tag from this StyleTag
     */
    public abstract boolean isOpenSubALTTag(String tag)

    /**
     * Checking if the tag can be a closing subALT tag from this StyleTag
     */
    public abstract boolean isCloseSubALTTag(String tag) 
}