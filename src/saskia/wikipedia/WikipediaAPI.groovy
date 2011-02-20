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

import groovy.sql.Sql

import saskia.bin.Configuration
import saskia.io.WikipediaDB
import rembrandt.tokenizer.TokenizerPT
import saskia.util.XMLUtil
import rembrandt.obj.*

import org.apache.lucene.analysis.*
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.*
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.*
import org.apache.log4j.*

/**
 * @author Nuno Cardoso
 * WIkipedia API: Allows an interaction with the Wikipedia indexed documents,
 * to perform common tasks.
 * Note: use the lang property on each WikipediaAPI instancew, not the conf value - they may differ. 
 */
class WikipediaAPI {
	
     /* For Lucene indexes */
	def static IndexReader reader 
	def static Searcher searcher
	def static Analyzer analyzer
	def static QueryParser parser 

	def lang
	def conf
	def db
	
	def static Logger log = Logger.getLogger("WikipediaAPI")
	def static TokenizerPT tok
	
	def idOfDoc = null
	def wikipedia, wikimining, catmining
	
	def static _this = [:]
	/**
	 * Initializes a WikipediaAPI singleton.
	 */	 
	private WikipediaAPI(String lang, Configuration conf_) {
	    this.conf = conf_
	    this.lang = lang

	    tok = TokenizerPT.newInstance()

	    // load Lucene stuff, if necessary
	    /*if(!forIndex.contains("none")) { 
			log.info "Loading Lucene indexes as requested."
			reader = IndexReader.open(this.conf.get("wikipedia.index"))
			searcher = new IndexSearcher(reader)
			analyzer = new StandardAnalyzer()
	    }*/
	    
		try {db = WikipediaDB.newInstance().getDB()}
		catch (Exception e) {log.fatal "Can't open db:"+e.printStackTrace()}
	    
	  	if (db) log.info "WikipediaAPI initialized for language $lang."
	}
	
	/**
	 * Generate a WikipediaAPI Instance.
	 * Language is mandatory! it has to be explicity specified.
	 * @param conf_ optional Configuration resource.
	 */
	public static WikipediaAPI newInstance(String lang, conf_ = null) {
		if (!lang) throw new IllegalStateException("Must have a language.")
	    WikipediaAPI instanceToReturn = null
		Configuration c

	    if (!conf_) c = Configuration.newInstance() else c = conf_    

	    if (!_this.isEmpty()) {
	       _this.keySet().each{
	          if (it.equals(lang)) {
			     log.trace "Recycling an instance for WikipediaAPI for language $lang"
			     instanceToReturn = _this[lang]
	         }
	      }
	    }   
	    if (!instanceToReturn) {
			   log.trace "Building a new instance for WikipediaAPI for language $lang"
			   instanceToReturn = new WikipediaAPI(lang, c) 	
			   _this[lang] = instanceToReturn			
		}
		return instanceToReturn   
	}
	
	static String withUnderscore(String text) {
	    return text.replaceAll(" ","_")
	}
	
	// para modelos recentes 
	static String withoutUnderscore(String text) {
		//if ( text instanceof String )
	    return text.replaceAll("_"," ")
		//else ""
	}
	
	private void updateTimeStatistics(difference, field) {
	    if (timeStatistics.containsKey(field) ) timeStatistics[field] += difference 
	    else timeStatistics[field] = difference
	    
	    if (callStatistics.containsKey(field) ) callStatistics[field]++
	    else callStatistics[field] = 1
	}
	
	public showTimes() {
	    log.info "Times:======"
	    callStatistics.keySet().sort().each{
	    	log.info "$it: called ${callStatistics[it]} times, took "+timeStatistics[it]/1000.0+" seconds."
		}
	}
	

	/**
	 * Strategy: Get redirect.
	 */
	public WikipediaDocument getRedirectDocumentFromTitle(String title) {	
		if (!title) return null
		def newid = null, newtitle=null
	    db.eachRow (WikipediaDB.getSelectIDandTitleFromRedirectionTitle(this.lang), 
		 [withUnderscore(title)]) {row -> 
	         newid = row[0]
	         newtitle = withoutUnderscore(new String(row[1], "UTF-8"))
		}
		if (!newid) return null 
		return new WikipediaDocument(newid, newtitle, this.lang)     	    
	}

	/**
	 * Strategy: Get redirect.
	 */	
	public WikipediaDocument getRedirectDocumentFromID(id) {	
		if (!id) return null
		def newid = null, newtitle=null
	    db.eachRow (WikipediaDB.getSelectIDandTitleFromRedirectionID(this.lang), 
		 [id]) {row -> 
	         newid = row[0]
	         newtitle = withoutUnderscore(new String(row[1], "UTF-8"))
		}
		if (!newid) return null 
		return new WikipediaDocument(newid, newtitle, this.lang)     	    
	}
	
	/** 
	 * Get categories from the DB.
	 * if string != null, it is used as a pattern for link filtering
	 * @param document the document that has the categories
	 * @return List of categories. If no categories, returns empty list.
	 */
	public List getCategoriesFromPageID(id) {
	    // always return a List, so that WikipediaDocument knows that the category fetch 
	    // was made. null means that was not made, [] means it was made but with no results	    
	    def cats = []
	  //   println "tou aqui com id $id."
		db.eachRow (WikipediaDB.getSelectCategoriesFromPageDocumentFromID(this.lang), [id])  {row->
		//db.eachRow ( "select cl_to from pt_categorylinks where cl_from = 4508") {//row->
		//	println "cats: ${row[0]}"
		//	println "cats forced to UTF-8: "+new String(row[0].getBytes(), "UTF-8")
		//	println "cats forced to ISO: "+new String(row[0].getBytes(), "ISO-8859-1")
		//	println id
		//	println row.cl_to

			//if ( row[0] instanceof String){
				cats += withoutUnderscore(new String(row[0], "UTF-8"))				
			//}
			///println row.class
		}   

	    return cats
	}	

	/** 
	 * 
	 * Get categories from the DB.
	 * if string != null, it is used as a pattern for link filtering
	 * @param document the document that has the categories
	 * @return List of categories. If no categories, returns empty list.
	 */
	public List getCategoriesFromPageTitle(String title) {
	     if (!title) return null
	     def cats = []    
		 db.eachRow (WikipediaDB.getSelectCategoriesFromPageDocumentFromTitle(this.lang), [withUnderscore(title) ]) {row-> 
		    cats += withoutUnderscore(new String(row[0],"UTF-8"))    
	     }
	    return cats
	}	
	 
	/** Note that category titles ARE underscored on spaces. */
	// do not use withUnderscore, strange behaviors... leave it to DBconnect. To review the bug.
	public List<WikipediaDocument> getCategoriesFromRegexTitle(String regex) {
	     if (!regex) return null
	     def answer = []    
	//println "Performing "+WikipediaDB.getSelectCategoriesFromRegex(regex, this.lang)
		 db.eachRow (WikipediaDB.getSelectCategoriesFromRegex(regex, this.lang)) {row-> 
			 answer << new WikipediaDocument(row[0], withoutUnderscore(new String(row[1],"UTF-8")), this.lang)
	     }
	    return answer
	}	
	
	/**
	 * Attention, the ID is from a NameSpace:0!! That is, I have the document "Lisbon" that has 
	 * Category:Lisbon, I want the document "Category:Lisbon", that, the document "Lisbon" with namespace 14! 
	 */
	 
	public WikipediaDocument getCategoryDocumentFromPageID(id) {		
	   def newid = null, newtitle = null	
	  // this select seems too slow.	
	    db.eachRow (WikipediaDB.getSelectCategoryDocumentIDandTitleFromPageDocumentID(this.lang), [id])  {row -> 
	        newid = row[0]
	        newtitle = new String(row[1],"UTF-8" )
	    }
		if (newid == null) return null 
		return new WikipediaDocument(newid, newtitle, this.lang)
	}
	

	
	/**
	 * Attention, the ID is from a NameSpace:0!! That is, I have the document "Lisbon" that has 
	 * Category:Lisbon, I want the document "Category:Lisbon", that, the document "Lisbon" with namespace 14! 
	 */
	public WikipediaDocument getCategoryDocumentFromPageTitle(String title) {		
	    if (!title) return null
	    def newid = null, newtitle = null	
		//println "WikipediaAPI: title=$title"
		db.eachRow (WikipediaDB.getSelectCategoryDocumentIDandTitleFromPageDocumentTitle(this.lang), 
			[withUnderscore(title)]) {row -> 
		    newid = row[0]
		    newtitle = new String(row[1], "UTF-8")
		}
		if (!newid) return null 
		//println "id: $newid title=$newtitle"
		return new WikipediaDocument(newid, newtitle, this.lang) 	    
	}

	
	/**
	 * Get disambiguation title page, that is, the needle plus the Wikipedia isambiguation string
	 * @param needle the title page
	 * @return The disambiguation document. Returns null if not found.
	 */
	public Document getDisambiguationDocumentFromTitle(String title) {
	    if (!title) return null
	    return getDocumentFromTitleFromDB(withUnderscore(
		  "${title} (${WikipediaDefinitions.disambiguationLString[this.lang]})") )
	}
	
	/**
	 * Get  document from id, from the DB.
	 * @param id document id.
	 * @return The document. Null if not found.
	 */
	public List<WikipediaDocument> getPageDocumentsFromCategoryTitle(String title) {
	   // get the title, the categories, the links. 
	   def res = []
	   db.eachRow(WikipediaDB.getSelectPageIdTitleWithCategory(this.lang), [withUnderscore(title) ]) {row -> 
	      res += new WikipediaDocument(row[0], new String(row[1], "UTF-8"), this.lang)} 
	   return res
	}

	/**
	 * Get  document from id, from the DB.
	 * @param id document id.
	 * @return The document. Null if not found.
	 */
	public WikipediaDocument getPageDocumentFromID(id) {
		// note: id can be a long.
	   // get the title, the categories, the links. 
	   def title = null
	   db.eachRow(WikipediaDB.getSelectTitleFromPageID(this.lang), [id])
	      {row -> title = new String(row[0], "UTF-8")}
	     // {row -> title = ISO88591toUTF8.convertBadISO88591(row[0])}
	   if (!title) return null
	   return new WikipediaDocument(id, title, this.lang) 
	}

	
	/**
	 * Get document from id, from the DB.
	 * @param id document id.
	 * @return The document. Null if not found.
	 */
	public WikipediaDocument getPageDocumentFromTitle(String title) {
	   // get the title, the categories, the links. 
	   if (!title) return null
	   def id = null 
	   def boolean redirect
	   
	  // def x = db.firstRow(WikipediaDB.selectIDFromPageTitle, [withUnderscore(title) ])
	  // id = x?.getAt(0)
	   //print "Going for "+withUnderscore(title)
	   db.eachRow(WikipediaDB.getSelectIDFromPageTitle(this.lang), [withUnderscore(title) ]) { row -> 
	      id = row[0]
	      redirect = row[1] }
	  //println " gave id $id"
	   if (!id) return null
	  // println redirect.class.name

	   WikipediaDocument nd = new WikipediaDocument(id, title, this.lang)
	   nd.redirect = redirect
       return nd
	}  

	public String getLanguageLinkFrom(String line, String targetLanguage, String sourceLanguage) {
	   def answer 
		db.eachRow(WikipediaDB.getLanguageLink(sourceLanguage), 
		[targetLanguage, withUnderscore(line) ]) { row -> 
	      answer = row[0]
		}
		return answer
	}  

	public String getRawWikipediaTextFromWikipediaPageID(id) {
		if (!id) return null
		String answer = null
	    db.eachRow(WikipediaDB.getRawWikipediaTextFromWikipediaPageID(lang), [id]) { row ->
			java.sql.Blob blob = row.getBlob('old_text')
			byte[] bdata = blob.getBytes(1, (int) blob.length())
			// you have to say explicitly that mediawiki's mediumblob is in UTF-8
			answer = new String(bdata, "UTF-8") 
		}
		return answer
	}  

	/** 
	 * Get Inlinks from database.
	 * Returns List of Maps in the [source:sourceID, anchor:anchorText] syntax 
	 * @param document document that has inlinks. 
	 * @param needle needle for the anchor text.
	 * @return List of inlinks in the given format above.
	 */
	public List getInlinksFromID(id) {
	     // first, get the title; then, get the 
	     def inlinksFromDB = [] 
	     db.eachRow (WikipediaDB.getSelectInlinkIDandTitleFromPageID(this.lang), [id]) {row -> 
	          inlinksFromDB += [source_id:row[0], 
	           source_title:withoutUnderscore(new String(row[1], "UTF-8")) ] 
	          // source_title:ISO88591toUTF8.convertBadISO88591(withoutUnderscore(row[1])) ] 
	      }
	     return inlinksFromDB 
	 }    

	/** 
	 * Get Outlinks from database.
	 * Returns List of Maps in the [source:sourceID, anchor:anchorText] syntax 
	 * @param document document that has inlinks. 
	 * @param needle needle for the anchor text.
	 * @return List of inlinks in the given format above.
	 */
	public List getOutlinksFromID(id) {
	     // first, get the title; then, get the 
	     def outlinksFromDB = [] 
	     db.eachRow (WikipediaDB.getSelectOutlinkIDandTitlePagesFromPageID(this.lang), [id]) {row -> 
	          def nrow = 
	          outlinksFromDB += [target_id:row[0],
	           target_title:withoutUnderscore(new String(row[1], "UTF-8")) ] 
//	          target_title:ISO88591toUTF8.convertBadISO88591(withoutUnderscore(row[1])) ] 
	      }
	     return outlinksFromDB 
	 }
	//selectOutlinkIDandTitlePagesFromPageTitle
	
	/**
	 * Get outlinks of the given NE, based on the information contained 
	 * in the WikipediaPage property. Returns all outlinks.
	 * @param ne named entity to query.
	 * @param outlinks.
	 */
	public List getOutlinksFromNE(NamedEntity ne) {
	    def res = []
	    ne.wikipediaPage.keySet().each{id -> 
	     	getOutlinksFromID(id).each{outlink -> 
	     		res += outlink
	     	}
	     }
	    return res.unique()
	}	
	

/**** OTHER FUNCTIONS *****/	

	/**
	 * Return sentences from the tokenizedText. Howmuch defines the number of 
	 * sentences to return. Number 0 returns all of them.
	 * Sentences are splitted by the '\n' character.
	 * @param tokenizedText tokenized text.
	 * @param howmuch number of sentences to return. Default is 0, that is, all of them.
	 * @return List of Sentences.
	 */
	public List getFirstSentences(List tokenizedText, int howmuch = 0) {
	    def allSentences = []
	    def currentSentence = []
	    tokenizedText.each {token -> 
	    	if (!token.equals("\n")) {
	    	    currentSentence.add(token)
	    	} else {
	    	    allSentences.add(currentSentence)
	    	    currentSentence=[]
	    	}
	    	if ((howmuch > 0) && (allSentences.size() >= howmuch)) return allSentences	    	
	    }
	    return allSentences
	}
	

	/**
	 * Get sentences and terms from paragraph, 
	 * all nicely wrapped in Sentences and Terms objects, 
	 * from the Linguateca tokenizer.
	 * @param paragraph Paragraph string.
	 * @return List of Sentences.
	 */
	public List getSentencesAndTerms(String paragraph) {	    
	    return tok.parse(paragraph)
	}

	/**
	 * get text inside brackets
	 * @param text to parse
	 * @return text in brackets. Returns null if not found.
	 */
	public String getTextInParenthesis(String text) {
	    def matcher = text =~ /.*\((.*)\).*/
	    def matched = matcher.matches()
	    if (matched) return matcher.group(1) else return null 
	}
	
	
	/**
	 * Check if the query might be an acronym.
	 * @param query The query to analyse
	 * @boolean true if it might be an acronym, false otherwise. 
	 */
	public boolean isAcronym(String query) {
	    return (query ==~ /[A-Z]+/)
	}
	
	/**
	 * Check if the list of terms might be an acronym for the givem acronym.
	 * Just use the capitalized words.
	 * 
	 * @param terms List of terms.
	 * @param acronym The acronym to compare
	 * @return true ifit might be an acronym, false otherwise 
	 */
	public boolean matchesAcronym(String terms, String acronym) {
	    def y = ''
	    terms.findAll{it ==~ /[A-Z]+/}.each{y += it}
	    return y  == acronym
	}

	/**
	 * Strips the disambiguation part from the title.
	 * I.e., converts 'Theme (disambiguation)' to 'Theme'.
	 * @param title to parse
	 * @return stripped title, or the title if not found.
	 */
	public String stripDisambiguationFromTitle(String title) {
	    def match = title =~ /^(.*)( \(${WikipediaDefinitions.disambiguationLString[this.lang]}\))$/
	    if (match.matches()) return match.group(1)		
		return title
	}
}