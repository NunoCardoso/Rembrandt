/* Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package saskia.wikipedia
 
import java.util.regex.Pattern

import org.apache.lucene.document.Document

/**
 * @author Nuno Cardoso
 * This should be a class that abstracts the fact the a document about a 
 * certain page of Wikipedia comes from a group of document fields in the DB, 
 * or if it is a Lucene document. 
 * 
 * It implements a group of fields.
 */
class WikipediaDocument {
	
    def long id
    def int namespace
    def String rawTitle = null
    def String theTitle = null
    def String lang
    def boolean redirect
    def String text
    def Date date 
    
    def WikipediaAPI wiki
    
    def List inlinks = null
    def List outlinks = null
    def List categories = null
    def String content = null
    
    public WikipediaDocument(id, title, lang) {
	    // ids from Wikipedia DB come in Long type.
		if (id instanceof Integer) id = (long)id
		if (id instanceof String) id = Long.parseLong(id)
		this.id = id
		this.lang=lang
		this.wiki = WikipediaAPI.newInstance(lang)
		this.rawTitle = WikipediaAPI.withUnderscore(title)
		this.theTitle = WikipediaAPI.withoutUnderscore(title)
    }
    
  
    static WikipediaDocument parseLucene(Document doc) {
		
	    def inlinks_ = []
	    def outlinks_ = []
	    def categories_ = doc.getValues("categories")    
	
	    doc.getValues("inlinks").each {link -> 
	          def m = link =~ /\[(.*)\]\[(.*)\]/
		      if (m.matches()) 
			      inlinks_ += [source:m.group(1), anchor:m.group(2)]
	    }  
	    
	    doc.getValues("outlinks").each {link -> outlinks_ += [target:link]}
	   
		return new WikipediaDocument(
			id:Long.parseLong(doc.get("id")), 
			rawTitle:WikipediaAPI.withUnderscore(doc.get("title")),
			theTitle:doc.get("title"),
			inlinks:inlinks_,
			outlinks:outlinks_,
			categories:(categories_ == null? [] : 
			    categories_.toList().collect{it.replaceAll("(.*):","")})
			)
    }
    
    /* if this document is built by the DB, it is builted on demand, that is: 
     * it starts with ID and Title. If we require categories or inlinks or outlinks (= null), 
     * we go fetch it on demand. If they were already fectched and gave nothing, then 
     * they are [], not null; null triggers another search.
     * 
     * Note that doc.categories elsewhere, in Groovy, indeed calls this getCategories() method.
     */
    public List getInlinks() {
	   if (inlinks == null)  inlinks = wiki.getInlinksFromID(id)
       return inlinks
    }
  
    public List getOutlinks() {
	  if (outlinks == null)  outlinks = wiki.getOutlinksFromID(id)
      return outlinks    
    }   
    
     public List getCategories() {
	 //  println "Feching categories for $theTitle. null? "+(categories == null)+" size? "+(categories?.size())
	   if (categories == null) categories = wiki.getCategoriesFromPageID(id).collect{
		   it.replaceAll("${WikipediaDefinitions.categoryString[lang]}:","")} 
	   return categories
     }
     
	/** 
	 * Get categories from document. if string != null, it is used as a pattern for link filtering
	 * @param document the document that has the categories
	 * @return List of categories. If no categories, returns empty list.
	 */
	 
	public List getCategories(String needle) {
	  if (needle != null) needle = "(?i)"+needle else needle=".*"  
      return getCategories().findAll{it ==~ Pattern.compile(needle)}
	 }
	
	/** 
	 * Switcher function for getInlinksfromIndex and getInlinksfromDB.
	 * Executes one of this functions, depending on the configuration values.
	 */
	public List filterInlinksBySourceTitle(String needle) { 
	    if (needle != null) needle = "(?i)"+needle else needle=".*" 
		return getInlinks().findAll{it.source_title ==~ Pattern.compile(needle) && !(it.source_title =~ /^!.*/ )}
	 }
	
	/** 
	 * Switcher function for getOutlinksfromIndex and getOutlinksfromDB.
	 * Executes one of this functions, depending on the configuration values.
	 */
	public List filterOutlinksByTargetTitle(String needle) {
	     if (needle) {
		 //got java.util.regex.PatternSyntaxException: Unclosed group near index 33
		 //(?i)Margaret II ((disambiguation)
		 // dunno what its, but it's on the ==~ line, so let's hack for now
		 needle = needle.replaceAll(/([^\\])([\(\)])/) {all, g1, g2 -> return "${g1}\\${g2}"} // 
		 needle = "(?i)"+needle 
	     } else {needle=".*" }
		 return getOutlinks().findAll{it.target_title ==~ Pattern.compile(needle) && !(it.target_title =~ /^!.*/ )}	     	     
	}
	
	/* TODO: filterOutlinksByAnchorTitle */
	
	public String toString() {
	    return "$id:$theTitle"
	}
 }