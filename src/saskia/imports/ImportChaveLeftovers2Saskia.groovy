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
 
package saskia.imports


import rembrandt.obj.*
import rembrandt.io.*
import rembrandt.tokenizer.*
import java.util.regex.*
import saskia.bin.Configuration
import saskia.db.DocStatus;
import saskia.db.database.SaskiaMainDB;
import saskia.db.obj.*
import saskia.db.table.*;
import saskia.util.I18n
import org.apache.log4j.*

/** 
 * This class imports CHAVE documents in the Saskia database 
 */

class ImportChaveLeftovers2Saskia {
	
	SaskiaMainDB saskia_db
	Configuration conf
	static Logger log = Logger.getLogger("Saskia")
	String saskiaDB
	static Map<String,Collection> collections
	Tag tag 
	String lang
	String filename
	RembrandtWriter writer
	TokenizerPT tok = TokenizerPT.newInstance()

	public ImportChaveLeftovers2Saskia(String filename) {

	    conf = Configuration.newInstance()
	    collections = ["pt":Collection.getFromID(9), "en":Collection.getFromID(10)]	    
	    this.filename = filename
	    writer = new RembrandtWriter(new RembrandtStyleTag("pt")) // sempre em PT!!!
	}
	
	SourceDoc addSourceDoc(String docid, Document doc, String content, Date date_created) {
	    
	   SourceDoc s = SourceDoc.getFromOriginalIDandCollectionIDandLang(
		   docid, collections[doc.lang].col_id, doc.lang)
	  
           if (s) {
               log.warn("SourceDoc with docid $docid in collection ${collections[doc.lang].col_id} exists! Replacing it")
 					   s.sdoc_collection = collections[doc.lang]
						s.sdoc_lang = doc.lang
						s.sdoc_content = content 
						s.sdoc_date = date_created
						s.sdoc_doc = null
						s.sdoc_proc = DocStatus.READY
						s.replaceThisToDB()
						log.debug "Replaced $s into Saskia DB."

           } else {
               s = new SourceDoc(sdoc_original_id:docid,
						sdoc_collection:collections[doc.lang], 
						sdoc_lang:doc.lang, 
						sdoc_content:content, 
						sdoc_date:date_created,
						sdoc_doc:null, 
						sdoc_proc:DocStatus.READY)
		
						try {
		    				s.addThisToDB()
							log.debug "Inserted $s into Saskia DB."
						} catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
		    // VARCHAR primary keys are case insensitive. Sometimes there's redirects that are not redirects, 
		    // and so there's attempts to insert documents with a id with case changes. 
		    // let's just catch it and continue
		    log.warn "Found duplicate entry in DB. Skipping."  
						}
			}
		return s
 	}
	
	public HashMap importDoc(File file) {
	    
	    HashMap status = [imported:0, skipped:0]
	    
	    Matcher m
	    boolean inbody = false
	    int body_sentence_count = 0// to count body line sentences, to detect titles
	    String type // is "GH" or "LAT"
	    
	    Document doc
	    String docid
	    
	    GregorianCalendar date_created

	    StringBuffer body
	    String lang
	
	    file.eachLine{l -> 
	    
	         m = l =~ /<html>/
	         if (m.matches()) { 
	             doc = new Document()
	             docid = null
	             type = null
	             body_sentence_count = 0
	             doc.lang = this.lang
	             doc.taglang = "pt"
	             return
	         }
	         
	         m = l =~ /<\/html>/
	         
	         if (m.matches()) { 
	             
	             doc.docid = docid 
	             doc.lang = lang 
		          doc.body = body.toString()
	             doc.tokenizeBody()
	             String text2 = writer.printDocument(doc)
	             log.debug ("Loaded $docid")
	             SourceDoc sdoc = addSourceDoc(docid, doc, text2, date_created.time) 
	             if (sdoc) {
	        	 		status.imported++ 
	        	 		log.debug ("SourceDoc id: ${sdoc.sdoc_id} webstore: ${sdoc.sdoc_webstore}")
	             } else {
	        	 		status.skipped++ 
	        	 		log.debug ("SourceDoc null")
	             }
	             return
	         }

	         // LATime identifier: month, day, year
	    	 	m = l =~ /<title>PUBLICO-(\d{4})(\d{2})(\d{2})-(\d+)<\/title>/
	    	 	if (m.matches()) {
	    	    
	    	     docid = "PUBLICO-"+m.group(1)+m.group(2)+m.group(3)+"-"+m.group(4)
	    	     type = "PUBLICO"
	    		  lang="pt"
	
	    	     date_created = new GregorianCalendar()
	 	     	  date_created.set(Calendar.YEAR, Integer.parseInt(m.group(1)) )
	 	     	  date_created.set(Calendar.MONTH, Integer.parseInt(m.group(2)) -1)
	 	     	  date_created.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)) )	 	    
	 	        date_created.set(Calendar.HOUR, 0 )	
	 	        date_created.set(Calendar.MINUTE, 0)	
	 	        date_created.set(Calendar.SECOND, 0 )	
	    	     body = new StringBuffer()
	    	     return
	    	 	}
	    	 
	    	   // GH identifier: year, month, day
	    	   m = l =~ /<title>FSP(\d{2})(\d{2})(\d{2})-(\d+)<\/title>/
	    	     	
	    	 	if (m.matches()) {
	    	    
	    	     docid = "FSP"+m.group(1)+m.group(2)+m.group(3)+"-"+m.group(4)
	    	     type = "FSP"
	    		  lang="pt"
	    		 
	    	     date_created = new GregorianCalendar()
	 	        date_created.set(Calendar.YEAR, Integer.parseInt("19"+m.group(1)) )
	 	        date_created.set(Calendar.MONTH, Integer.parseInt(m.group(2)) -1)
	 	        date_created.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)) )	 	    
	 	        date_created.set(Calendar.HOUR, 0 )	
	 	        date_created.set(Calendar.MINUTE, 0)	
	 	        date_created.set(Calendar.SECOND, 0 )	
	    	     body = new StringBuffer()
	    	     return
	    	 }   
		    	
		
		   // LATime identifier: month, day, year
	    	 m = l =~ /<title>LA(\d{2})(\d{2})(\d{2})-(\d+)<\/title>/
	    	 if (m.matches()) {
	    	    
	    	     docid = "LA"+m.group(1)+m.group(2)+m.group(3)+"-"+m.group(4)
	    	     type = "LAT"
	    		  lang="en"
	    		 
	    	     date_created = new GregorianCalendar()
	 	        date_created.set(Calendar.YEAR, Integer.parseInt("19"+m.group(3)) )
	 	        date_created.set(Calendar.MONTH, Integer.parseInt(m.group(1)) -1)
	 	        date_created.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(2)) )	 	    
	 	        date_created.set(Calendar.HOUR, 0 )	
	 	        date_created.set(Calendar.MINUTE, 0)	
	 	        date_created.set(Calendar.SECOND, 0 )	
	    	     body = new StringBuffer()
	    	     return
	    	 }
	    	 
	    	 // GH identifier: year, month, day
	    	 m = l =~ /<title>GH(\d{2})(\d{2})(\d{2})-(\d+)<\/title>/
	    	     	
	    	 if (m.matches()) {
	    	    
	    	     docid = "GH"+m.group(1)+m.group(2)+m.group(3)+"-"+m.group(4)
	    	     type = "GH"
	    		  lang="en"
	    		 
	    	     date_created = new GregorianCalendar()
	 	        date_created.set(Calendar.YEAR, Integer.parseInt("19"+m.group(1)) )
	 	        date_created.set(Calendar.MONTH, Integer.parseInt(m.group(2)) -1)
	 	        date_created.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)) )	 	    
	 	        date_created.set(Calendar.HOUR, 0 )	
	 	        date_created.set(Calendar.MINUTE, 0)	
	 	        date_created.set(Calendar.SECOND, 0 )	
	    	     body = new StringBuffer()
	    	     return
	    	 }   
		    	
	    	 m = l =~ /<body>/
	    	 
	    	 if (m.matches()) {
	    	     inbody = true
	    	     return
	    	    
	    	 }
	    	
	    	 m = l =~ /<\/body>/
	    	 if (m.matches()) {    	     
	    	     inbody = false	 	
	    	     return
	    	 }

	    	 if (inbody) {	  
	    	     
	    	     if (type == "FSP") {
	    		 
	    		 		def punt = l =~ /.*[\.!\?]$/ 
	    		 
	    		 		// promote to title
	    		 		if (body_sentence_count == 0 && l.size() < 70 && !punt.matches()) {	    		     
	    		     		List<Sentence> res = tok.parse(l)
	    		     		if (res && res.size() > 0) doc.title_sentences << res[0]
	    		     		body_sentence_count++
	    		     
	    		 		} else { 	
	    		   // leave as body
	    		     		body.append l +" "
	    		     		body_sentence_count++
	    		 		}   		 
	    		 
	    	     } else if (type == "PUBLICO") {
	    		 
	    		 		def punt = l =~ /.*[\.!\?]$/ 
	    		 
	    		 		// promote as title
	    		 		if (body_sentence_count < 3 && l.size() < 70 && !punt.matches()) {	   		     
	    		     		List<Sentence> res = tok.parse(l)
	    		     		if (res && res.size() > 0) doc.title_sentences << res[0]
	    		     		body_sentence_count++
						} else { 	
	    		   		// leave as body
	    		     		body.append l +" "
	    		     		body_sentence_count++
	    		 		}   		 
	    		 
	    		 } else if (type == "GH") {
	    		 
	    		 	def punt = l =~ /.*[\.!\?]$/ 
	    		 
	    		 	// promote to title
	    		 	if (body_sentence_count == 0 && l.size() < 70 && !punt.matches()) {	    		     
	    		     List<Sentence> res = tok.parse(l)
	    		     if (res && res.size() > 0) doc.title_sentences << res[0]
	    		     body_sentence_count++
	    		     
	    		 	} else { 	
	    		   // leave as body
	    		     body.append l +" "
	    		     body_sentence_count++
	    		 	}   		 
	    		 
	    	    } else if (type == "LAT") {
	    		 // remove <P>
	    		 	def m2 = l =~ /<\/?P>/
		    	 	if (m2.matches()) { 		    	    
		    	     body.append "\n\n"
		    	     return 
		    	  	}
		    	
	    		 	// promote as title
	    		 	if (body_sentence_count == 0) {	    		     
	    		     List<Sentence> res = tok.parse(l)
	    		     if (res && res.size() > 0) doc.title_sentences << res[0]
	    		     body_sentence_count++
	    		 	} else {
	    		     body.append l + " "
	    		     body_sentence_count ++
	    		 	}
	    		 
	    	   }
	
	    	 } // if inbody
	    }// each line 
	    return status           
	}
	

	static void main(args) {
	    
	    String usage = "Usage: saskia.imports.ImportChaveLeftovers2Saskia [file]\n"+
		  "Learn it.";
	  
		if (!args || args.size() != 1 ) {
		    println usage
		    System.exit(0)
		}

		ImportChaveLeftovers2Saskia c2r = new ImportChaveLeftovers2Saskia()
		HashMap status = [read:0, imported:0, skipped:0]

		File file = new File(args[0])
		
		def newstatus = c2r.importDoc(file)
		status.imported += newstatus.imported
		status.skipped += newstatus.skipped
		
		log.info "Done. Saw ${status.read} docs, imported ${status.imported} doc(s), skipped ${status.skipped} doc(s)."
	}
}