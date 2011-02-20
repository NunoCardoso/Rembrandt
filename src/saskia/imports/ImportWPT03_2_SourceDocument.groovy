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

import saskia.bin.Configuration
import saskia.io.Collection
import saskia.io.SourceDoc
import saskia.io.DocStatus

import org.apache.log4j.Logger
import org.apache.commons.cli.*

import java.util.regex.*

import rembrandt.obj.Document
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag


/** 
 * This class imports WPT03 files to the Source Documents

----!!- colecção WPT 03 - separador de documento -!!----
URL: http://7mares.terravista.pt/atlda/
(Content-Length, -1)
(Content-Type, text/html)
(Last-Modified, unknown)
(ServerSW, apache)
(dataRec, 16/04/2003)
(estado, 200)
(filtrado, /vcrMom/data/WEBSTATS/filtrados/20/24/748)
(ip, 62.151.16.12)
(language, unknown)
(prof, 0)
(realSize, 778)
(textSize, 14)
(title, Alves e Trigo)

Alves e Trigo
*/


class ImportWPT03_2_SourceDocument {
	
	Configuration conf 
	static Logger log = Logger.getLogger("SaskiaImports")
	Collection collection 
	String lang= "pt"
	String filename
   RembrandtWriter writer 
	HashMap status
	File f
	String encoding
	
	public ImportWPT03_2_SourceDocument(String filename, Collection collection,
			String encoding) {
        
		conf = Configuration.newInstance()
	   this.collection = collection
		writer = new RembrandtWriter(new RembrandtStyleTag("pt"))
		this.encoding = encoding
		
		f = new File(filename)
      status = [imported:0, skipped:0]
	}
	
	HashMap parse() {
	    Matcher m
		 Boolean indoc = false
		 Boolean inbody = false
		 Boolean inheader = false
		 
		 String text // buffer
		
		 Date date_modified
		 Date date_fetched
		 String lang 
		 String id
		
		 String content 
		 String url 
		 String title
		
		InputStreamReader is = new InputStreamReader(new FileInputStream(f), encoding)
		BufferedReader br = new BufferedReader(is)	    
      String l
      while ((l = br.readLine()) != null) {

			m = l =~ /^----!!- .* WPT 03 - separador de documento -!!----$/
			if (m.matches()) {
				if (inbody && indoc) {
					inbody = false; indoc=false; inheader = false; 
					Document doc = new Document()
					doc.body = text.trim()
					if (title && title != "null")
						doc.title = title
					doc.docid = id
					doc.lang = lang
					doc.tokenize()
					content = writer.printDocument(doc)
				
					Date date = null
					if (date_modified)
						date = date_modified
					if (!date && date_fetched)
						date = date_fetched
					if (!date) 
						date = new Date(0)

					addSourceDoc(id, content, lang, date)
					text = "";title = ""; url = "";content = "";
					lang="";id="";date_modified=null;date_fetched=null;
				}
			} else {

				m = l =~ /^\((.*?), (.*?)\)$/
				if (m.matches()) {
					if (inheader && !inbody) {
						def key = m.group(1)
						def value = m.group(2)
						
						if (key == "Last-Modified") {
							if (value != "unknown") {
								date_modified = Date.parse("dd/MM/yyyy", value)
							}	
						}
						if (key == "dataRec") {
							if (value != "unknown") {
								date_fetched = Date.parse("dd/MM/yyyy", value)
							}	
						}
						if (key == "language") {
							if (value == "portuguese") {
								lang="pt"
							}else if (value == "english") {
								lang="en"
							} else {
								lang="xx"
							}
						}
						if (key == "title") {
							title = value
						}
					} else {
						log.error("Erro: linha $l não é suposto estar aqui!")
						System.exit(0)
					}
					
				} else {
					
					Matcher m2 = l =~ /URL: https?:\/\/(.*)/
					
					if (m2.matches()) {
						if (!inheader) {
							inheader = true; 
							indoc = true
							url = m2.group(1);
						
							// o id vai ser o URL, só que há URLs que, truncados a 255, ficam iguais.
							// vou usar uma hash com 8 números, um '_', depois o URL truncado a 240. 
							String random =  Long.toHexString(Double.doubleToLongBits(Math.random()));
							int index = (url.size() > 240 ? 240 : url.size()) 
							id = random.substring(0,8)+"_"+url.substring(0,index)
						}
					// body 	
					} else {
						if (inbody && !inheader) {
							text += l
						}
						else if (inheader && !inbody) {
							inheader = false;
							inbody = true;
							text = ""
						}
					}
				}
			}
		}
		
		// the last document
		if (text) {
			Document doc = new Document()
			doc.body = text
			doc.docid = id
			doc.lang = lang
			doc.tokenize()
			content = writer.printDocument(doc)
				
			Date date = null
			if (date_modified)
				date = date_modified
			if (!date && date_fetched)
				date = date_fetched
			if (!date) 
				date = new Date(0)

			addSourceDoc(id, content, lang, date)
		}
		return status
   }

   public SourceDoc addSourceDoc(String original_id, String content, String lang, Date date) {
		
		SourceDoc s = new SourceDoc(
			sdoc_original_id:original_id,
        sdoc_collection:collection, 
        sdoc_lang:lang, 
        sdoc_content:content, 
        sdoc_doc:null,
        sdoc_date:date,
        sdoc_proc:DocStatus.READY,
        sdoc_comment:""
		)
		// by adding to DB, it already checks for duplicates
		
      try {
         s.addThisToDB()
         log.debug "Inserted $s into Saskia DB."
			status.imported++
      } catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
         log.warn "Found duplicate entry in DB. Skipping."  
			status.skipped++
      } catch(Exception e2) {
         log.warn "Found other error. Skipping. " + e2.getMessage()  
			status.skipped++
		}
		return s
	
	println "I have $s $content" 	
	}      
 	
	static void main(args) {
	
		Options o = new Options()
		o.addOption("file", true, "collection file to load")
		o.addOption("encoding", false, "encoding")
		o.addOption("col", true, "collection number/name")
		o.addOption("help", false, "Gives this help information")
	    
		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

	   if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java saskia.imports.ImportWPT03_2_SourceDocument", o )
			System.exit(0)
		}

		Collection collection = null
		String collection_ = cmd.getOptionValue("col")
		try {
			collection = Collection.getFromName(collection_)
		}catch(Exception e) {}	
		    
	 	if (!collection) {
			try {
				 collection = Collection.getFromID(
					Integer.parseInt(collection_)	)    
			}catch(Exception e) {}
		}
		
		if (!collection) {
			throw new IllegalStateException("Don't know where the collection $collection_ is. Exiting.")
			System.exit(0)
		}       

	   if (!cmd.hasOption("file")) {
			println "No --file arg. Please specify the file. Exiting."
			System.exit(0)
		}
	    
	   String encoding = ""
	   if (!cmd.hasOption("encoding")) {
			println "No encoding given. ISO-8859-1 (WPT-03 default) used."
			encoding = "ISO-8859-1"
		} else {
			encoding = cmd.getOptionValue("encoding")
		}
		
	    
	
		ImportWPT03_2_SourceDocument w2s = new ImportWPT03_2_SourceDocument(
			cmd.getOptionValue("file"), collection, encoding)
			
		HashMap status = w2s.parse()
		log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
	}
}
