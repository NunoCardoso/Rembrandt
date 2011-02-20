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
/** 
 * This class imports NYT files to the Source Documents
 */
class ImportPlainGCSecondHAREM2SourceDocument {
	
	Configuration conf 
	static Logger log = Logger.getLogger("SaskiaImports")
	Collection collection 
	String lang= "pt"
	String filename
    
	public ImportPlainGCSecondHAREM2SourceDocument(String filename) {
        
	    conf = Configuration.newInstance()
	    collection = Collection.getFromName("CD2 do Segundo HAREM")	    
	    if (!collection) 
		throw new IllegalStateException("Don't know where the collection is. Exiting.")
        
	    File f = new File(filename)
            InputStreamReader is = new InputStreamReader(new FileInputStream(f))
            BufferedReader br = new BufferedReader(is)	    
            StringBuffer buffer = new StringBuffer()		    
            String line
            
            String docid = null
            int total = 0
            while ((line = br.readLine()) != null) {  
                
                if (line.startsWith("<DOC ")) {
                    line.find(/ID="(.*?)"/) {all, g1 -> docid = g1}
                    
                } else if (line.matches(/(?i)<\/DOC>/)) {
						total++
						buffer.append(line)
						String html = "<HTML lang=\"pt\">\n<HEAD>\n"
						html += "</HEAD>\n<BODY>\n"
						html += body.toString().trim()+"\n</BODY>\n</HTML>"
	               println "Creating source doc with id ${docid}. HTML is ${html.size()} bytes size."
                    SourceDoc s = new SourceDoc(sdoc_id:docid,
                            sdoc_collection:collection.col_id, 
                            sdoc_lang:lang, 
                            sdoc_content:html, 
                            sdoc_doc:null,
                            sdoc_date:new Date(0),
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
                    buffer = new StringBuffer()
                }  else {
                    buffer.append(line+"\n")
                }      
            }
	    println "Imported $total docs."
      }
	
	static void main(args) {
	    
	    Options o = new Options()
	    o.addOption("file", true, "collection file to load")
	    o.addOption("help", false, "Gives this help information")
	    
	    CommandLineParser parser = new GnuParser()
	    CommandLine cmd = parser.parse(o, args)

	    if (cmd.hasOption("help")) {
		HelpFormatter formatter = new HelpFormatter()
		formatter.printHelp( "java saskia.imports.ImportPlainGCSecondHAREM2SourceDocument", o )
		println "Make sure that the collection 'CD2 do Segundo HAREM' is on Saskia"
		System.exit(0)
	    }

	    if (!cmd.hasOption("file")) {
		println "No --file arg. Please specify the file. Exiting."
		System.exit(0)
	    }
	    
	    ImportPlainGCSecondHAREM2SourceDocument w2s = new ImportPlainGCSecondHAREM2SourceDocument(
		    cmd.getOptionValue("file"))
			
	    HashMap status = w2s.importDocs()
	                      
	    log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
	}
}