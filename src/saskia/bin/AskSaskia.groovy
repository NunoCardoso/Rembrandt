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
 
package saskia.bin 

import saskia.wikipedia.WikipediaDocument
import saskia.wikipedia.WikipediaAPI
import saskia.wikipedia.WikipediaDefinitions
import saskia.dbpedia.DBpediaAPI
import saskia.dbpedia.DBpediaOntology
import saskia.gazetteers.DBpediaOntology_to_SemanticClassification
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.rules.MeaningDetector
import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 * This class is the core knowledge source for detecting the category of NEs. 
 * It uses the Wikipedia documents and analyses categories and inlinks to 
 * extract meanings for the NE, and mapping them into classification categories.
 */
class AskSaskia {

    WikipediaAPI wikipedia 
    MeaningDetector catmining
    LinkedHashMap<String,NamedEntity> cache
    Configuration conf
    static Logger log = Logger.getLogger("AskSaskia")     
    static Map<String,AskSaskia> _this = [:] // singleton maps
    String lang
    DBpediaAPI dbpedia
	DBpediaOntology dbpediaontology
	DBpediaOntology_to_SemanticClassification dbpediaontology2sc
    
    /**
     * Initialize the singleton object.
     * @param conf The configuration resource.
     * @return the singleton object.
     */
    private AskSaskia(String lang, Configuration conf) {
	 this.conf = conf
	 this.lang = lang
  	 this.cache = new LinkedHashMap(conf.getInt("saskia.necache.number",100), 0.75f, true) // true: access order. 		 
	 wikipedia = WikipediaAPI.newInstance(lang, conf)
	 dbpedia = DBpediaAPI.newInstance(conf)	     
	 catmining = Class.forName("rembrandt.rules.harem."+lang.toLowerCase()+
		   ".WikipediaCategoryRules"+lang.toUpperCase()).newInstance()
	 dbpediaontology = 	DBpediaOntology.getInstance()
	 dbpediaontology2sc = DBpediaOntology_to_SemanticClassification.newInstance()
    }
   
    /**
     * Get an AskSaskia new instance
     * @param conf optional parameter. If not set, use the current Configuration singleton. 
     */
    public static AskSaskia newInstance(String language, conf_ = null) {
	Configuration c
	if (!conf_) c = Configuration.newInstance() else c = conf_
	    		
	AskSaskia instanceToReturn = null
	_this?.keySet().each{		    
	    if (it.equals(language)) {
		log.trace "Recycling an instance for Saskia for $language"
		instanceToReturn = _this[language]
	    }
	}
	if (!instanceToReturn) {
	    log.trace "Building a new instance for Saskia for $language"
	    instanceToReturn = new AskSaskia(language, c) 	
	    _this[language] = instanceToReturn				
	}
	return instanceToReturn		
    }
	    
    /**
     * Ask Saskia to classify this NE. 
     * (warning: it can return an NE with just "EM", for the NEs that have an answer
     * by Saskia, but could not map its meanings to any category).
     * 
     * @param ne the NE that has unknown category
     * @return a cloned NE with a different classification, or null otherwise
     */  
     public NamedEntity answerMe(NamedEntity source_ne) {
        
	 if (!source_ne || !source_ne.terms) return null
	 String ne_text = source_ne.printTerms()
	 if (!ne_text.trim()) return null
		
	 log.info "== AnswerMe: ${ne_text} =="
    
	 // note: don't modify the original ne, I may want to check if changes ocurred!
	 // TODO: rearrange code so that I don't have to clone
    	 NamedEntity ne = source_ne.clone()
        
	 /**if the NE has a link, it means that there is already a known wiki/dbpedia resource
	  * that grounds the NE. Let's use it. Mind that different NEs might have different meanings, 
	  * as in [[French Revolution|revolution]] and [[Industrial Revolution|revolution]] 
	  * It's not up to AskSaskia to figure it out the true meanings now, but at leat don't go to the 
	  * cache, which is for Nes with unknown meaning and therefore is a starting point.
	  */
	 if (!ne.link) {
	     if (cache.containsKey(ne_text)) {
		 def cachedNE = cache.get(ne_text)
		 log.info "0.99 return (from cache): $cachedNE."
		 ne.replaceClassificationFrom(cachedNE)
		 ne.replaceAdditionalInfoFrom(cachedNE)
		 return ne
	     } 
	     
	 } else {
	     // note that we don't add it to cache, as it is a special NE with implicit meaning.
	     log.trace "1.0. It's a NE with link: ${ne.link}. I'll ground it immediately."
	     if (conf.getBoolean("saskia.dbpedia.enabled",true)) {	
		 String resource = dbpedia.getDBpediaResourceFromWikipediaURL(ne.link, lang)
		 log.trace "1.1 got resource: $resource"
		 if (resource) {
		     classifyWithDBpedia(ne, resource, ne.link)
		     log.info "1.99 return (from ne.link): $ne. wikipedia:${ne.wikipediaPage} dbpedia:${ne.dbpediaPage}"
		     return ne
		 }
	     }
	 }
	
	 // imagine that DBpedia resource has no class, so we can't classify it, but Wikipedia can.
	 // we need to save thew resource, to add afterwards into ne.dbpediaPage in the proper sc.
	 String resource
	 
	 // No caches nor links - let's find out the old way. First, Get the dbpedia resource
	 if (conf.getBoolean("saskia.dbpedia.enabled",true)) {
	     log.trace "Saskia's DBpedia enabled."
	     resource = dbpedia.getDBpediaResourceFromWikipediaPageTitle(ne_text, lang)
	     if (resource)  classifyWithDBpedia(ne, resource)
	     else log.trace "DBpedia failed! Will rely on Wikipedia alone"	
	} else {
	    log.info "Saskia's DBpedia disabled. If you enable, results will be more accurate."
	    log.info "Mind that, if you use dbpedia.org web service, be gentle and don't hammer it." 
	}	
	if (!ne.hasUnknownClassification()) {
	    cache.put(ne_text, ne)		
	    log.info "2.99 return (from DBpedia): $ne. wikipedia:${ne.wikipediaPage} dbpedia:${ne.dbpediaPage}"
	    return ne	
	}

	// THIS is for NEs that have unknown classification, let's Wikipedia it

	log.trace "3.0 Get page document from title '${ne_text}'"		
	WikipediaDocument doc = wikipedia.getPageDocumentFromTitle(ne_text)
		
	if (doc) {
	    if (!doc.redirect) {
		log.trace "3.1 Yes, got a doc: id=${doc.id}, title=${doc.theTitle}. Do intelligent stuff with it."
		NamedEntity result = doIntelligentStuffWithDoc(doc, ne, ne_text, resource)
		if (result) cache.put(ne_text, result)
   		log.info "3.98 return (from Wikipedia): $ne. wikipedia:${ne.wikipediaPage} dbpedia:${ne.dbpediaPage}"
		return result 
	    } else {
		log.trace "3.2 Doc has a redirect flag, go for Plan B."
		WikipediaDocument doc2 = wikipedia.getRedirectDocumentFromID(doc.id)
		log.trace "3.3 got: $doc2"
    	        if (doc2) {
    	            String ne_text2 = doc2.theTitle
    	            log.debug "3.4 Yes, got a plan B doc: id=${doc2.id}, title=${doc2.theTitle}. Retrying intelligent stuff with this new doc."
    	            NamedEntity result = doIntelligentStuffWithDoc(doc2, ne, ne_text2, resource, ne_text)
    		    if (result) cache.put(ne_text, result)
    		    log.info "3.99 return (from Wikipedia): $ne. wikipedia:${ne.wikipediaPage} dbpedia:${ne.dbpediaPage}"

    		    return result 
    		}
	   }
	} else {
	    log.trace "X.99 No doc. Nothing. Returning null"
	    return null
    	}		
    }
  
    /**
     * Get category needle, that is, check if a category is the same as the needle.
     * Test the document title, then the original ne_text, then the initialQuery (before goin for mostFrequentInlinks). 
     */
    private String getCategoryNeedle(String category, WikipediaDocument doc, String ne_text, String alternativeQuery) {
	String needle
	if (category == doc.theTitle) needle = doc.theTitle
	else if (category == ne_text) needle = ne_text
	else if (alternativeQuery && category == alternativeQuery) needle = alternativeQuery
	return needle	
    }
    
    /**
     * Called by answerMe to perform tests on Wikipedia documents.
     * @param doc Document to be analysed
     * @return named entity with classification, or null
     */
    NamedEntity doIntelligentStuffWithDoc(WikipediaDocument doc, NamedEntity ne, String ne_text, 
	    String resource, String alternativeQuery = null) {

	// map of categories-in-pages, so that when I map meanings to categories, I assign them to pages
	Map<String,String> catsToPageTitles = [:]
		
	log.trace "3A Depth 0. Categories of doc \"${doc.theTitle}\": ${doc.categories}"
	   
	doc.categories?.eachWithIndex {category0, i0 ->  
	        
	// if category is NOT a disambiguation
	   if (category0 != WikipediaDefinitions.disambiguationUString[this.lang]) {
	       WikipediaDocument doc1 = null
	       String needle0 = getCategoryNeedle(category0, doc, ne_text, alternativeQuery)
		   	  
	       // if there is a self-Category, go there.  
	       if (needle0) doc1 = wikipedia.getCategoryDocumentFromPageID(doc.id)	  
	       if (needle0) log.trace "3A.1 Depth 0->1. Got category needle: '$needle0'. Going for category page."
	       if (doc1) {
	    	    // Depth: 1 
	    	    doc.categories[i0] = doc1.categories.findAll{!it.startsWith("!")}
	    	    log.trace "3A.2 Depth 1: Self-category gave more categories: ${doc.categories[i0]}. NOT going deeper."    		    	    
	    	    doc.categories[i0].each{
	    		if (!catsToPageTitles.containsKey(it)) catsToPageTitles[it] = doc.theTitle   	
	  	 	 // normal category. Just stuff it.
	    	    }
	    	} else {
	    	    log.trace "3A.3 Depth 0: Got normal category '$category0'. Adding."    		    	    
	    	    if (!catsToPageTitles.containsKey(category0)) catsToPageTitles[category0] = doc.theTitle   
	    	 }
	    }	 
	       
	   
	    // this category is a "Disambiguation" category
	    // for example: Partido Social Democrata is a disambiguation page.
	    // But using outlinks with this needle is a smart idea.
	    // From v.0.6.10, is Contains, because there are some pages that have disambiguation + other.	    
	      else if (category0 == WikipediaDefinitions.disambiguationUString[this.lang])  {
		  log.trace "3A.4 Depth: 0. Got disambiguation category. Stripping '$ne_text' of the disambiguation brackets..."
		  String disambiguationNeedle = wikipedia.stripDisambiguationFromTitle(ne_text)
	    	  List<String> selectedOutlinks = doc.filterOutlinksByTargetTitle(disambiguationNeedle)
	    	  log.trace "3A.5 Depth: 0. selected Outlinks for document '$doc.theTitle' = $selectedOutlinks"   
	    	 
	    	 // make this category ready to accept more categories from deeper pages. That is, erase the "Disambiguation" string,
	    	 // replace it with an empty list.
	    	 doc.categories[i0] = []
	    	 
	    	 selectedOutlinks.eachWithIndex {outlink0, o0 -> 
	    	   // new document from outlink. 
	    	   // Depth: 1
	    	   WikipediaDocument doc1 = wikipedia.getPageDocumentFromID(outlink0.target_id)
	    	   if (doc1) {
	    	       log.trace "3A.6 ${(o0+1)}/${selectedOutlinks.size()} Got document from outlink '${outlink0.target_id}'. Depth 0->1."	 	 	     
	    	       log.trace "3A.7 Depth: 1. Categories of doc1 \"${doc1.theTitle}\": $doc1.categories"
	    	       /* assign title and Id, if it is not only a disambiguation page*/		    		    

	    	       doc1.categories.eachWithIndex {category1, i1 ->     	  	    
	    		      // it is unlinkely to think that a disambiguation page links to another disambiguation page...
	    		      // just test for self-categories...
	    	       	  if (category1 != WikipediaDefinitions.disambiguationUString[this.lang]) {
	    		   		       
	    	       	      WikipediaDocument doc2 = null
	    	       	      String needle2 = getCategoryNeedle(category1, doc1, disambiguationNeedle, alternativeQuery)
	    	       	      if (needle2) doc2 = wikipedia.getCategoryDocumentFromPageID(doc1.id)	    		    	
	    	       	      if (needle2) log.trace "3A.8 Depth: 1->2. Got category needle: '$needle2'. Going deeper for category page."
	    	       	      // if it is a self category -> Depth: 2
	    	       	      if (doc2) {
	    	       		  doc1.categories[i1] = doc2.categories.findAll{!it.startsWith("!")}
	    	       		  log.trace "3A.9 Depth: 2. Self-category gave more categories: ${doc1.categories[i1]}. NOT going deeper."
	    	       		  doc1.categories[i1].each{
	    	       		      if (!catsToPageTitles.containsKey(it)) catsToPageTitles[it] = doc1.theTitle  
	    	       		  }		    		
	    	       		  doc2.categories.eachWithIndex{category2, i2->
	    	       		     // Depth:3
	    	       		     // just go for self-category, avoid drifting and stick with it.
	    	       		     WikipediaDocument doc3 = null
	    		    	     String needle3 = getCategoryNeedle(category2, doc2, disambiguationNeedle, alternativeQuery)	    		    	
	    		    	     if (needle3) doc3 = wikipedia.getCategoryDocumentFromPageID(doc2.id)	 
	    		    	     if (needle3) log.debug "3A.10 Depth: 2->3. Got category needle: '$needle3'. Going deeper for category page."
	    		    	     if (doc3) {		    	    	
	    		    		 doc1.categories[i1][i2] = doc3.categories.findAll{!it.startsWith("!")}
	    		    		 log.trace "3A.11 Depth 3. Self-category gave more categories: ${doc1.categories[i1][i2]}. Filling wiki info."
	    		    		 doc1.categories[i1][i2].each{
	    		    		     if (!catsToPageTitles.containsKey(it)) catsToPageTitles[it] = doc1.theTitle 
	    		    		 }
	    		    	     }
	    	       		  }
	    	       	      } else {
	    		    	// plain normal category	
	    	       		  log.trace "3A.12 Depth: 2. Got normal category '$category1'. Filling wiki info."    		    	    
	    	       		  if (!catsToPageTitles.containsKey(category1)) catsToPageTitles[category1] = doc1.theTitle 
	    	       	      }    		    	
	    	       	  }//if category1 != disambiguation, else
	    	       }//doc1.categories.eachWithIndex category1, i1	
	    		 
	    	       doc1.categories = doc1.categories.flatten()    	 		
		    	 	    	 	
	    	       if (doc1.categories?.isEmpty()) log.trace "3A.13 No luck, got nothing from outlinks."
	    	       else {
	    		   log.trace "3A.14 Done with this outlink page. Got categories: ${doc1.categories}. I'm not going deeper. Filling wiki info"	    	 	    
	    		   // doc.categories[i0] is a list that receives categories from documents of each outlink page.
	    		   doc.categories[i0] << doc1.categories
	    	       }
	    	   }//doc1 != null	
	       }//selectedOutlinks
	   }// else if category0 = disambiguation
        }//doc.eachCategory
	 
	//  Acronym: Plan B	
	if (wikipedia.isAcronym(ne_text) &&  (!doc.categories || doc.categories == [WikipediaDefinitions.disambiguationUString[this.lang]])) {  	
 
	    //let's reinitialize doc.categories array. If doc.categories == [wikipedia.disambiguationUString], but nothing else
	    // it's because we need an acronym resolver. Well, reinialize and hope that each outlink fills it with more 
	    // interesting cartegories.
	    doc.categories = []
	    log.trace "3B.1 Depth 0. Still no luck, no good categories. Try for acronym detection. Feching outlinks with no needle"

	    doc.outlinks.each{outlink0 -> 
    	   	if (wikipedia.matchesAcronym(outlink0.target_title, ne_text)) {
    	   	    log.trace "3B.2 Outlink '${outlink0.target_title}' may be an acronym expansion for '$ne_text'"
    		    WikipediaDocument doc1 = wikipedia.getPageDocumentFromID(outlink0.target_id)
    		    if (doc1) {
    			log.trace "3B.3 Depth 0->1. Got doc from outlink '${outlink0.target_title}': ${doc1.theTitle}."	
    			log.trace "3B.4 Depth 1. Categories of doc: ${doc1.categories}"
    			doc1.categories.eachWithIndex {category1, i1 ->    	  			    
    			    // if !disambiguation   			    
    			   if (category1 != WikipediaDefinitions.disambiguationUString[this.lang]) {
	    			        
    			       WikipediaDocument doc2 = null  	  			    	
    	  		       String needle1 = getCategoryNeedle(category1, doc1, outlink0.target_title, alternativeQuery)  
    	  		       if (needle1) doc2 = wikipedia.getCategoryDocumentFromPageID(doc1.id)
    	  		       if (needle1) log.trace "3B.5 Depth 1->2. Got category needle: '$needle1'. Going deeper for category page."  	    		    	  			    	
    	  		       if (doc2) {
    	  			   doc1.categories[i1] = doc2.categories.findAll{!it.startsWith("!")}
    	  			   log.trace "3B.6 Depth 2. Self-category gave more categories: ${doc1.categories[i1]}. Filling wiki info."  
    	  			   doc1.categories[i1].each{
    	  			       if (!catsToPageTitles.containsKey(it)) catsToPageTitles[it] = doc1.theTitle 
    	  			   }
    	  			    	    
    	  			   doc2.categories.eachWithIndex{category2, i2->
    	  			   // if there is a self-Category, go there.
    	  			       WikipediaDocument doc3 = null
    	  			       String needle2 = getCategoryNeedle(category2, doc2, outlink0.target_title, alternativeQuery)      		    	    	    
    	  			       if (needle2) doc3 = wikipedia.getCategoryDocumentFromPageDocumentID(doc2.id)  
    	  			       if (needle2) log.trace "3B.7 Depth 2->3. Got category needle: '$needle2'. Going deeper for category page."  	    		    	  			    	
    	  			       if (doc3) {    		
    	  				   // insert more categories in it
    	  				   doc1.categories[i1][i2] = doc3.categories.findAll{!it.startsWith("!")}
    	  				   log.trace "3B.8 Depth 3. Self-category gave more categories: ${doc1.categories[i1][i2]}. Filling wiki info."	    	  			    	     
    	  				   doc1.categories[i1][i2].each{
    	    	  			       if (!catsToPageTitles.containsKey(it)) catsToPageTitles[it] = doc1.theTitle 
    	    	  			   }
    	  			       }
    	  			   }
    	  		       } else {
    	  			   log.trace "3B.9 Depth 2: Got normal category '$category1'. Adding wiki info."    		
    	  			   if (!catsToPageTitles.containsKey(category1)) catsToPageTitles[category1] = doc1.theTitle 
   	  		       }
    			   // if disambiguation    
    			   } else if (category1 == WikipediaDefinitions.disambiguationUString[this.lang]) {
    			       log.trace "3B.10 Depth: 1. Got disambiguation category. Stripping '$category1' of the disambiguation brackets..."
    			       String disambiguationNeedle1 = wikipedia.stripDisambiguationFromTitle(outlink0.target_title)
    			       // log.debug "3.10.5 Depth: 1. Outlinks for doc1: ${doc1.outlinks}"
    			       List<String> selectedOutlinks1 = doc1.filterOutlinksByTargetTitle(outlink0.target_title)
    			       log.trace "3B.11 Depth: 1. selected Outlinks1 for document '${doc1.theTitle}' = ${selectedOutlinks1}"   
    			       // make this category ready to accept more categories from deeper pages. That is, erase the "Disambiguation" string,
    			       // replace it with an empty list.
    			       doc1.categories[i1] = []
   	 
    			       selectedOutlinks1.eachWithIndex {outlink1, o1 -> 
    			           WikipediaDocument doc2 = wikipedia.getPageDocumentFromID(outlink1.target_id)
	    		    	   if (doc2) {
	    		    	       log.trace "3B.12 Depth: 1->2. ${(o1+1)}/${selectedOutlinks1.size()} Got doc from outlink '${outlink1.target_title}'. "	 	 	     
	    		    	       log.trace "3B.13 Depth: 2. Categories of doc \"${doc2.theTitle}\": ${doc2.categories}"
    	    		    	       doc2.categories.eachWithIndex {category2, i2 ->  	    		   	  	
    	    		    	       // it is unlinkely to think that a disambiguation page links to another disambiguation page...
    	    		    	       // just test for self-categories...
			   	       // if is not disambiguation
    	    		    	           if (category2 != WikipediaDefinitions.disambiguationUString[this.lang]) {
    	    		    	               WikipediaDocument doc3 = null
    	    		    	               String needle3 = getCategoryNeedle(category2, doc2, disambiguationNeedle1, alternativeQuery)
    	    		    	               if (needle3) doc3 = wikipedia.getCategoryDocumentFromPageID(doc2.id)	    		    	
    	    		    	               if (needle3) log.trace "3B.14 Depth: 2->3. Got category needle: '$needle3'. Go for category page."
    	    		    	               // if it is a self category -> Depth: 4
    	    		    	               if (doc3) {
    	    		    	        	   doc2.categories[i2] = doc3.categories.findAll{!it.startsWith("!")}
    	    		    	        	   log.trace "3B.15 Depth: 3. Self-category gave more categories: ${doc2.categories[i2]}. Filling wiki info."
    	    	  				   doc1.categories[i2].each{
    	    	    	  			       if (!catsToPageTitles.containsKey(it)) catsToPageTitles[it] = doc2.theTitle 
    	    	    	  			   }
    	    		    	        	   doc3.categories.eachWithIndex{category3, i3->
    	    		    	        	   	// Depth:4!
    	    		    	        	   	// just go for self-category, avoid drifting and stick with it.
    	    		    	        	       WikipediaDocument doc4 = null
    	    		    	        	       String needle4 = getCategoryNeedle(category3, doc3, disambiguationNeedle1, alternativeQuery)	    		    	
    	    		    	        	       if (needle4) doc4 = wikipedia.getCategoryDocumentFromPageID(doc3.id)		 
    	    		    	        	       if (needle4) log.trace "3B.16 Depth: 3->4. Got category needle: '$needle4'. Go for category page."
    	    		    	        	       if (doc4) {		    	    	
    	    		    	        		   doc2.categories[i2][i3] = doc4.categories.findAll{!it.startsWith("!")}
    	    		    	        		   log.trace "3B.17 Depth 4. Self-category gave more categories: ${doc2.categories[i2][i3]}. Filling wiki info."
    	    	    	  				   doc1.categories[i2][i3].each{
    	    	    	    	  			       if (!catsToPageTitles.containsKey(it)) catsToPageTitles[it] = doc2.theTitle 
    	    	    	    	  			   }
    	    		    	        	       }
    	    		    	        	   }
    	    		    	        	   // plain normal category 
    	    		    	               } else {	
    	    		    	        	   log.trace "3B.18 Depth 3: Got normal category '$category2'. Adding wiki info."    	
    	    		    	        	   if (!catsToPageTitles.containsKey(category2)) catsToPageTitles[category2] = doc2.theTitle 	    	    	  			   
    	    		    	               }
    	    		    	           } // if category2 != wikipedia disambiguationUString
	    		    	       }//doc2.eachCategory
		    		    		   
	    		    	       doc2.categories = doc2.categories.flatten()    	 		
		    	 	    	 	
	    		    	       if (doc2.categories?.isEmpty())  log.trace "3B.19 No luck, got nothing from outlinks."
	    		    	       else {
	    		    		   log.trace "3B.20 Done with this outlink page. Got categories: ${doc2.categories}. I'm not going deeper. Filling wiki info"	    	 	    
	    		    		   doc1.categories[i1] = doc2.categories.findAll{!it.startsWith("!")}
	    	  			   doc1.categories[i1].each{
	    	  			       if (!catsToPageTitles.containsKey(it)) catsToPageTitles[it] = doc2.theTitle 
	    	  			   }
	    		    	       }
	    		    	   }///doc2 != null	
	    		       }//selectedOutlinks1.each
    			    }// end if category disambiguation else    			    	
    			}//doc1.categories.eachWithIndex	    
	  			    
    			doc1.categories =  doc1.categories?.flatten()  	  				    
    			if (doc1.categories?.isEmpty()) log.trace "3B.21 No luck, got nothing from outlinks."
    			else {
    			    log.trace "3B.22 Done with this outlink page. Got categories: ${doc1.categories}. I'm not going deeper. Filling wiki info"	    	 	    
    			    // doc categories is []. doc1 is the product of each doc outlink. So, we append categories.
    			    doc.categories += doc1.categories.findAll{!it.startsWith("!")}
    			}	 
    		    }//doc1 != null
    	   	}//matchesAcronym
	    }//each outlink		    	          	
	 }//isAcronym	    	 	    	 
   
	 doc.categories = doc.categories.flatten() 
	 // get meanings and apply them
    
	 if (!doc.categories.isEmpty()) {
	     log.trace "3C.1 Fetching meanings for categories ${doc.categories}"
					
	     doc.categories.each{cat -> 
	        log.trace "3C.2 category: $cat"  
	        List<SemanticClassification> scs = catmining.processMeanings(Sentence.simpleTokenize(cat))
	        scs?.each{sc ->
	           log.trace "3C.3 Adding new sc ${sc} for category ${cat}"
	           ne.mergeClassification(sc)
	           log.trace "3C.4 Associating sc ${sc} for the page "+catsToPageTitles[cat]
	           if (!ne.wikipediaPage[sc]) ne.wikipediaPage[sc] = []
	           
	           // Add Wikipedia info to NE
	           if (!ne.wikipediaPage[sc].contains(catsToPageTitles[cat]))
	        	   ne.wikipediaPage[sc] << catsToPageTitles[cat]     
	        	                                            
	           
	        }
	    }
	     
	  // There is a special case: we get Leiria, we get the Wikipedia URL and DBpedia resource, 
	  // but DBpedia doesn't have rdf:type for dbpedia-owl (only opencycs and yagos), so it doesn't have a classification.  
	  // If Wikipedia generates classification, and we have a valid, unclassified DBpedia resource,
	     // then complete it
	   if (resource && !ne.wikipediaPage.isEmpty() && ne.dbpediaPage.isEmpty()) {
	       ne.wikipediaPage.each{k, v -> 
	       	   ne.dbpediaPage[k] = [resource]
	       }
	   }
	}
    
	if (ne.classification.isEmpty()) ne.classification << SC.unknown
 	return ne	     	     
    } 


// dbpprop:redirect

	
    /**
     * Classify a NE with DBpedia resource
     * @param ne the NamedEntity to classify. Changes will be made into it
     * @param the resource of the NE 
     */
    public classifyWithDBpedia(NamedEntity ne, String resource, String wikipediaPageTitle = null) {
	
	log.trace "2.0 ne = $ne ne.wikipediaPage = ${ne.wikipediaPage}"
	log.trace "2.1 Got DBpedia resource: $resource"
	
	Map everything = dbpedia.getEverythingFromDBpediaResource(resource)
	if (everything.containsKey('http://dbpedia.org/property/redirect')) {
		log.trace "2.1.1 DBpedia signals a redirect to "+everything['http://dbpedia.org/property/redirect']
		def resource2 = everything['http://dbpedia.org/property/redirect']
		// TODO: Escolho o primeiro nos casos em que há dois ou mais redirects... 
		if (resource2 instanceof List) resource = resource2[0] else resource = resource2
		log.trace "2.1.2 DBpedia resource is now "+resource
	}
	
	List classes = dbpedia.getDBpediaOntologyClassFromDBpediaResource(resource)
	log.trace "2.2 Resource has classes: $classes"

	// get the narrowest class
	String narrower_class = dbpediaontology.getNarrowerClassFrom(classes)
	    
	SemanticClassification sc =  dbpediaontology2sc.getClassificationFrom(narrower_class)
	log.trace "2.3 for narrower class ${narrower_class}, got SemClass ${sc}"
	log.trace "2.4 NE before merge:$ne" 
	if (sc) {
	    ne.mergeClassification(sc)
	    if (!ne.dbpediaPage) ne.dbpediaPage[sc] = [resource] else ne.dbpediaPage[sc] << resource
	}
	log.trace "2.5 NE after merge:$ne" 
	
	
	// if there's a ne.link (=wikipediaPageTitle), use it also for classification. 
	// if there is no wikipediaPageTitle, leave it. Next steps will try to classify them
	if (wikipediaPageTitle) {
	    WikipediaDocument doc = wikipedia.getPageDocumentFromTitle(wikipediaPageTitle)
	    if (doc) {
		log.trace "2.6 Resource has Wiki page: $doc"
		if (!doc.redirect) {
		    if (sc) {
			if (!ne.wikipediaPage[sc]) {
			    ne.wikipediaPage[sc] = [doc.theTitle] 
			                             
			} else {
		    	     ne.wikipediaPage[sc] << doc.theTitle
			}
		    }
		} else if (doc.redirect) {
		    WikipediaDocument doc2 = wikipedia.getRedirectDocumentFromID(doc.id)
		    if (doc2) {
			if (sc) {
			    if (!ne.wikipediaPage[sc]) {
				ne.wikipediaPage[sc] = [doc2.theTitle] 
			    } else {
				ne.wikipediaPage[sc] << doc2.theTitle
			    }
			}
			
		    }
		}
	    }
	}
	log.trace "2.7 ne = $ne ne.wikipediaPage = ${ne.wikipediaPage} ne.dbpediaPage = ${ne.dbpediaPage}"
	return ne
    }//classifyWithDBpedia
}