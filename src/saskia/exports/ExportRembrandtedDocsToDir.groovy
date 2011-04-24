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
 
package saskia.exports

import saskia.io.RembrandtedDoc
import rembrandt.io.DocStats
import saskia.io.Task
import saskia.io.User
import rembrandt.obj.Document
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import org.apache.log4j.*
import org.apache.commons.cli.*
import saskia.bin.Configuration
import saskia.db.DocStatus;
import saskia.db.obj.Collection;
import saskia.db.table.Job;

/** 
 * This class exports tagged documents from a collection into files on a directory
*/

class ExportRembrandtedDocsToDir {
	
	static Logger log = Logger.getLogger("SaskiaExports")
	static String fileseparator = System.getProperty("file.separator")
	static Configuration conf = Configuration.newInstance()

	Collection collection
	
	public ExportRembrandtedDocsToDir() {}
	
	public HashMap dump(Collection collection, File dir, int batch) {
			
		int rows = 0 
		HashMap status = [exported:0, not_exported:0]
		
		int total = collection.getNumberOfRembrandtedDocuments()
		DocStats docstats = new DocStats()
      docstats.begin()
		docstats.totalDocs = total
      int processed = 0
		int file_number = 0
  		File file
      for (int i=total; i > 0; i -= batch) {
            
				file_number++
				file = new File(dir.getCanonicalPath()+"/"+"col-"+collection.col_id+"-"+file_number.toString().padLeft(5,"0")+".txt")
				file.write("")
				
            int limit = (i > batch ? batch : i)
            log.debug "Initial batch size: ${total} Remaining: $i Next pool size: $limit"
            docstats.beginBatchOfDocs(limit)

            List rdocs = RembrandtedDoc.getBatchOfRembrandtedDocsOrderedByOriginalDocId(collection, limit, processed)
            log.debug "Got ${rdocs?.size()} RembrandtedDoc(s)."
            
            // if it's null, then there's no more docs to process. Leave the loop.
            if (!rdocs) {
                log.info "DB returned no more docs, I guess I'm done."	
                return 
            } 
  
 		    	rdocs.each {rdoc ->
		    		file.append("<META DOC_ORIGINAL_ID=\""+rdoc.doc_original_id+"\">\n")
					file.append(rdoc.doc_content.trim()+"\n")
					status.exported++	
		    	}
            docstats.endBatchOfDocs(limit)	
            processed += limit	
 				docstats.printMemUsage()
		}
				   			  
		docstats.end()
		return status
	}

	static void main(args) {    
        
        Options o = new Options()
        o.addOption("col", true, "target collection. Can be id or name")
        o.addOption("dir", true, "dir for file output")
        o.addOption("ndocs", true, "nr docs per zip")
        o.addOption("help", false, "Gives this help information")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
		  Collection collection
		  User user 
		
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java saskia.exports.ExportRembrandtedDocsToDir", o )
            System.exit(0)
        }
        
        if (!cmd.hasOption("col")) {
            println "No --col arg. Please specify the target collection (id or name). Exiting."
            System.exit(0)
        }
        
			try {
				collection = Collection.getFromID(Long.parseLong(cmd.getOptionValue("col")))		
		  } catch(Exception e) {
				collection = Collection.getFromName(cmd.getOptionValue("col"))
			}
			if (!collection) {
				log.error "Don't know collection "+cmd.getOptionValue("col")+" to parse documents on. Exiting."
			   System.exit(0)
			}
        

        String dir 

        if (!cmd.hasOption("dir")) {
				log.warn "No option --dir given. Going to create a dir called export on "+conf.get("rembrandt.home.dir")
				dir = conf.get("rembrandt.home.dir")
            if (!dir.endsWith(fileseparator)) dir += fileseparator
            dir += "export"
			} else {
				dir = cmd.getOptionValue("dir")
			}
       	File f = new File(dir)
         if (f.exists()) {
				log.error "directory "+dir+" exists. Please empty it or change to another dir."
			  	System.exit(0)
			} else {
				f.mkdir()       
			} 
         log.info "Using directory $dir. "
        
		int ndocs = 10000
 		if (!cmd.hasOption("ndocs")) {
            println "No --ndocs arg. Using 10000."
       } else {
			ndocs = Integer.parseInt(cmd.getOptionValue("ndocs"))
		}
        ExportRembrandtedDocsToDir obj = new ExportRembrandtedDocsToDir()
        HashMap status = obj.dump(collection, f, ndocs)

        log.info "Done. Exported ${status.exported} doc(s) successully, and ${status.not_exported} doc(s) were not exported."
    }   
}
