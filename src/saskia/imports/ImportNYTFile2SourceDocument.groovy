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

import saskia.dbpedia.DBpediaAPI
import saskia.dbpedia.DBpediaOntology
import saskia.wikipedia.WikipediaDocument
import saskia.io.NE
import saskia.io.SourceDoc
import saskia.io.Collection
import saskia.io.DocStatus
import saskia.converters.WikipediaDocument2HTMLDocumentConverter
import saskia.bin.Configuration
import org.apache.log4j.*
import org.apache.commons.cli.*
import java.util.regex.Matcher

/** 
 * This class imports NYT files to the Source Documents
 */
class ImportNYTFile2SourceDocument {
	
	Configuration conf
	static Logger log = Logger.getLogger("Saskia")
	Collection collection 
	String lang= "en"
	String filename

	public ImportNYTFile2SourceDocument(String filename) {

	    conf = Configuration.newInstance()
	    collection = Collection.getFromName("New York Times 2002-2005")	    
	    if (!collection) 
		throw new IllegalStateException("Don't know where the NYT collection is. Exiting.")
	    this.filename = filename
	}
	
	public HashMap importDocs() {
	    
	    HashMap status = [imported:0, skipped:0]
	    
	    Matcher m
	    boolean indoc = false
	    boolean intext = false
	    boolean inheadline = false
	    boolean indateline = false
	    GregorianCalendar date_created
	    String id 
	    StringBuffer title
	    StringBuffer body
	    String headline
	    
	    new File(filename).eachLine{l -> 
	    	    
	    	 m = l =~ /<DOC id="NYT_ENG_(\d{4})(\d{2})(\d{2})\.(\d+)" type=".*"\s?>/
	    	 if (m.matches()) {
	    	     if (indoc || intext || inheadline || indateline) 
	    		 throw new IllegalStateException(" Reading line $l, but I'm still in doc!")
	    	     id = "NYT_ENG_"+m.group(1)+m.group(2)+m.group(3)+"."+m.group(4)
	    	     indoc = true
	    	     headline = null	    	     
	    	     date_created = new GregorianCalendar()
	 	     date_created.set(Calendar.YEAR, Integer.parseInt(m.group(1)) )
	 	     date_created.set(Calendar.MONTH,  Integer.parseInt(m.group(2)) -1)
	 	     date_created.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)) )	 	    
	 	     date_created.set(Calendar.HOUR, 0 )	
	 	     date_created.set(Calendar.MINUTE, 0)	
	 	     date_created.set(Calendar.SECOND, 0 )	
	 	     title = new StringBuffer()
	    	     body = new StringBuffer()
	    	     return
	    	 }
	    	 m = l =~ /<\/DOC>/
	    	 if (m.matches()) {
	    	     if (!indoc || intext || inheadline || indateline) 
	    		 throw new IllegalStateException(" Reading line $l, but I'm not in a doc!")
	    	     String htmltext = "<HTML lang=\"en\">\n<HEAD>\n";
	    	     htmltext += "<TITLE>\n"+ title.toString().trim()+"\n</TITLE>\n"
	    	     htmltext += "</HEAD>\n<BODY>\n"+body.toString().trim()+"\n</BODY>\n</HTML>"
	    	     
	    	   /*  log.debug "========="
	    	     log.debug "id: $id"
	    	     log.debug "date: "+date_created.time
	    	     log.debug "htmltext: $htmltext"
	    	     */
	    	     addSourceDoc(id, htmltext, date_created.time) 
	    	     status.imported++
	    	     indoc = false
	    	     return
	    	 }
	    	 m = l =~ /<HEADLINE>/
	    	 if (m.matches()) {
	    	     if (!indoc || intext || inheadline || indateline) 
	    		 throw new IllegalStateException(" Reading line $l, but I'm still in headline!")
	    	     inheadline = true	
	    	     return
	    	 }
	    	 m = l =~ /<\/HEADLINE>/
	    	 if (m.matches()) {
	    	     if (!indoc || intext || !inheadline || indateline) 
	    		 throw new IllegalStateException(" Reading line $l, but I'm not in headline!")
	    	     inheadline = false	 	
	    	     return
	    	 }
	    	 m = l =~ /<DATELINE>/
	    	 if (m.matches()) {
	    	     if (!indoc || intext || inheadline || indateline) 
	    		 throw new IllegalStateException(" Reading line $l, but I'm still in dateline!")
	    	     indateline = true	 
	    	     return
	    	 }
	    	 m = l =~ /<\/DATELINE>/
	    	 if (m.matches()) {
	    	     if (!indoc || intext || inheadline || !indateline) 
	    		 throw new IllegalStateException(" Reading line $l, but I'm not in dateline!")
	    	     indateline = false	 
	    	     return
	    	 }
	    	 m = l =~ /<TEXT>/
	    	 if (m.matches()) {
	    	     if (!indoc || intext || inheadline || indateline) 
	    		 throw new IllegalStateException(" Reading line $l, but I'm still in text!")
	    	     intext = true	
	    	     return
	    	 }
	    	 m = l =~ /<\/TEXT>/	    	 
	   	 if (m.matches()) {
	    	     if (!indoc || !intext || inheadline || indateline) 
	    		 throw new IllegalStateException(" Reading line $l, but I'm not in text!")
	    	     intext = false	
	    	     return
	    	 }
	    	 m = l =~ /<P>/
	    	 if (m.matches()) {
	    	     body.append "<P>\n"
	    	     return
	    	 }
	    	 m = l =~ /<\/P>/	    	 
	   	 if (m.matches()) {
	    	     body.append "\n</P>\n"	    	     	
	    	     return
	    	 }
	    	 // catch all
	    	 
	    	 if (inheadline) {
	    	     title.append l+" "
	    	     return
	    	 }
	    	 if (indateline) {	    	    
	    	     return
	    	 }
	    	 if (intext) {	    	    
	    	     body.append l +" "
	    	 }
	    }// each line
	    return status          
	}
	
	void addSourceDoc(String id, String htmltext, Date date_created) {
	
		SourceDoc s = new SourceDoc(sdoc_id:id,
			sdoc_collection:collection.col_id, 
			sdoc_lang:'en', 
			sdoc_content:htmltext, 
			sdoc_date:date_created,
			sdoc_proc:DocStatus.READY,
			sdoc_comment:"", 
			sdoc_edit:DocStatus.UNLOCKED)
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
	
	static void main(args) {
	    
	    Options o = new Options()
	    o.addOption("file", true, "NYT collection file to load")
	    o.addOption("help", false, "Gives this help information")
	    
	    CommandLineParser parser = new GnuParser()
	    CommandLine cmd = parser.parse(o, args)

	    if (cmd.hasOption("help")) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "java saskia.imports.ImportNYTFile2SourceDocument", o )
		println "Make sure that the collection 'New York Times 2002-2005' is on Saskia"
		System.exit(0)
	    }

	    if (!cmd.hasOption("file")) {
		println "No --file arg. Please specify the file. Exiting."
		System.exit(0)
	    }
	    
	    ImportNYTFile2SourceDocument w2s = new ImportNYTFile2SourceDocument(
		    cmd.getOptionValue("file"))
			
	    HashMap status = w2s.importDocs()
	                      
	    log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
	}
}