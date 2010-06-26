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

import saskia.io.DocStatus
import saskia.io.RembrandtedDoc
import saskia.io.Collection
import rembrandt.io.DocStats
import rembrandt.obj.Document
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import org.apache.log4j.*
import org.apache.commons.cli.*
import saskia.bin.Configuration
/** 
 * This class imports Wikipedia raw documents in the Saskia database to 
 * HTML documents in the Saskia's page table
*/

class ImportRembrandtedDocument2NEPool {
	
	static Logger log = Logger.getLogger("SaskiaImports")
	RembrandtedDoc current_rdoc
	Collection collection
	String ynae
	BufferedReader input
	static final int REMBRANDTED_DOC_POOL_SIZE=30
	RembrandtReader reader
	Configuration conf = Configuration.newInstance()
	
	public ImportRembrandtedDocument2NEPool() {
	    reader = new RembrandtReader(new RembrandtStyleTag(conf.get("rembrandt.input.styletag.lang", conf.get("global.lang"))))
        }
	
	public HashMap syncBatchOfDocsToPool( collection_, int batchSize = 100) {
		int rows = 0 
		HashMap status = [sync:0, notsync:0]
		try {
			collection = Collection.getFromID(Long.parseLong(collection_))		
		} catch(Exception e) {
			collection = Collection.getFromName(collection_)
		}
		if (!collection) {
			log.error "Don't know collection $collection_ to parse documents on. Exiting."
			return status
		}
        
		def stats = new DocStats(batchSize)
		stats.begin()

		for (int i=batchSize; i > 0; i -= REMBRANDTED_DOC_POOL_SIZE) {
		    
		    int limit = (i > REMBRANDTED_DOC_POOL_SIZE ? REMBRANDTED_DOC_POOL_SIZE : i)
		    log.debug "Initial batchSize: $batchSize Remaining: $i Next pool size: $limit"
		    List<RembrandtedDoc> rdocs = RembrandtedDoc.getBatchDocsToSyncNEPool(collection, limit)
		    log.debug "Got ${rdocs?.size()} RembrandtedDoc(s)."
		    
		    // if it's null, then there's no more docs to process. Leave the loop.
		    if (!rdocs) return status
		    
		    rdocs.each {rdoc ->
		    	log.debug "Syncing doc -> NE pool for rdoc ${rdoc.doc_original_id}."
		    	stats.beginDoc(rdoc.doc_id)
                
		    	HashMap status_
		    // failcheck is done at the parse method. If REMBRANDT fails, it should release the lock and return 
		    // a failed status, and move to the next one. 
		    	try {
		    	    status_ = syncNEPoolFromDoc(rdoc)		
		    	} catch(Exception e) {
		    	    e.printStackTrace()
		    	    abort()
		    	}
			 
		    	if (status_) {
		    	    status.sync += status_.sync
		    	    status.notsync += status_.notsync	
		    	}
		    	stats.endDoc()					   			  
		    	stats.printMemUsage()	
		    }
		}
		stats.end()
		return status
	}

	public HashMap syncDocToPool( collection_, long doc_id) {
		 log.trace "Requesting doc -> NE pool sync for doc_id ${doc_id}."
		 int rows = 0 
		 HashMap status = [sync:0, notsync:0]
		 try {
			collection = Collection.getFromID(Long.parseLong(collection_))		
		 } catch(Exception e) {
			collection = Collection.getFromName(collection_)
		 }
		 if (!collection) {
			log.error "Don't know collection $collection_ to parse documents on. Exiting."
			return status
		 }		
		 
		 // NOT THREAD SAFE!!
		 RembrandtedDoc rdoc = RembrandtedDoc.getFromID(doc_id)
		 
		 if (rdoc) {			    
		     
			if (rdoc.doc_proc.isMarkedAsBad()) {

			    if ((!ynae) || (ynae == "y") || (ynae == "n")) {
				    ynae = null
					input = new BufferedReader(new InputStreamReader(System.in))
					while (!(ynae == "a" || ynae == "e" || ynae == "n" || ynae == "y")) {
					    println "RembrandtedDoc ${doc_id} in DB is marked as bad. Proceed? ([y]es, [a]lways, [n]o, n[e]ver)"
						ynae = input.readLine().trim()
					}
				}
			    if ( (ynae == "n") || (ynae == "e")) {
					log.warn "Skipping RembrandtedDoc (proc:${rdoc.doc_proc})."
					status.notsync++
					return status
			    }
		    }
			
		    if (rdoc.doc_edit.isLocked()) {
			    
				if ((!ynae) || (ynae == "y") || (ynae == "n")) {
				    ynae = null
					input = new BufferedReader(new InputStreamReader(System.in))
					while (!(ynae == "a" || ynae == "e" || ynae == "n" || ynae == "y")) {
				    	println "RembrandtedDoc ${doc_id} in DB is locked. Unlock and proceed? ([y]es, [a]lways, [n]o, n[e]ver)"
					    ynae = input.readLine().trim()
					}
				}
				if ( (ynae == "n") || (ynae == "e")) {
					log.warn "Skipping RembrandtedDoc (edit:${rdoc.doc_edit})."
					status.notsync++
					return status
		    	}
			}
		    
		    if (!(rdoc.doc_sync.isGoodForSyncingNEPool())) {
		
			   if ((!ynae) || (ynae == "y") || (ynae == "n")) {
				    ynae = null
					input = new BufferedReader(new InputStreamReader(System.in))
					while (!(ynae == "a" || ynae == "e" || ynae == "n" || ynae == "y")) {
					    println "RembrandtedDoc ${doc_id} in DB is marked as OLDER than the NEs in the pool."
					    println "By pressing y or a, you'll lose recent changes made to the NE pool. Proceed? ([y]es, [a]lways, [n]o, n[e]ver)"
						ynae = input.readLine().trim()
					}
			   }
		  	   if ( (ynae == "n") || (ynae == "e")) {
			  	       
		  	       log.warn "RembrandtedDoc ${rdoc.doc_sync} is not good to sync NE pool, I'll skip it."
		  	       status.notsync++
		  	       return status
		  	   }
		    }
		 }// if rdoc

		 try {
			 status = syncNEPoolFromDoc(rdoc)		
		 } catch(Exception e) {
			 abort()
		 }
		 return status
	}	
	
	/* If you are thinking in calling this function directly, that's a bad idea unless you're pretty SURE 
	 * what you're doing... that is,  make sure the documents are unlocked / good to process, respect 
	 * the database flags for proc, sync and edit, otherwise you're on your own
	 */
	private HashMap syncNEPoolFromDoc(RembrandtedDoc rdoc) {	
		
	    HashMap status = [sync:0, notsync:0]
		rdoc.changeEditStatusInDBto(DocStatus.LOCKED)	
		current_rdoc = rdoc // mark it, for abort if SIGINT is called
		log.info "Syncing RembrandtedDoc ${rdoc.doc_id} to NE pool..."
		
		// clean previous entries
		rdoc.removeDocHasNEsFromPool()
		log.trace "Erasing existent doc_has_ne entries from Saskia..."
		// Create a Rembrandt document object from rembrandted document text
        
		// TODO
		Document doc = reader.createDocument(rdoc.doc_content)
		
		log.trace "Created a new Rembrandt document."
		log.trace "Title: ${doc.title_sentences.size()} sentences, ${doc.titleNEs.size()} NEs."
		log.trace "Body: ${doc.body_sentences.size()} sentences, ${doc.bodyNEs.size()} NEs."
		// add nes
		rdoc.addNEsToSaskia(doc.titleNEs, "T", rdoc.doc_lang)
		rdoc.addNEsToSaskia(doc.bodyNEs, "B", rdoc.doc_lang)
		
		rdoc.changeSyncStatusInDBto(DocStatus.SYNCED)	
		rdoc.changeEditStatusInDBto(DocStatus.UNLOCKED)	
		log.debug "Done. Doc status is now ${DocStatus.SYNCED}."
		current_rdoc = null // mark it, for abort if CTRL-C is pressed

		log.info "RembrandtedDoc ${rdoc.doc_id} synced."
		
		status.sync++
		return status
	}
	
	public abort() {
	    log.warn "\nAborting... backtracking with ${current_rdoc?.doc_id}"
	    if (current_rdoc) {
			current_rdoc.changeEditStatusInDBto(DocStatus.UNLOCKED)
			log.warn "Doc edit status changed to UNLOCKED."
			current_rdoc.changeProcStatusInDBto(DocStatus.NOT_READY)
			log.warn "Doc proc status changed to NOT_READY."
	    }
	    log.info "Exiting."
	}
	
	static void main(args) {    
        
        
        Options o = new Options()
        o.addOption("from", true, "[DB] - one particular document from DB, [batch] - a number of unseen documents")
        o.addOption("col", true, "target collection. Can be id or name")
        o.addOption("ndocs", true, "number of docs in batch process")
        o.addOption("docid", true, "id of docs in DB process")
        o.addOption("help", false, "Gives this help information")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java saskia.imports.ImportRembrandtedDocument2NEPool", o )
            System.exit(0)
        }
        
        // check important vars
        if (!cmd.hasOption("from")) {
            println "No --from arg. Please specify the type of process (DB or batch). Exiting."
            System.exit(0)
        }
        
        if (!cmd.hasOption("col")) {
            println "No --col arg. Please specify the target collection (id or name). Exiting."
            System.exit(0)
        }
        
        ImportRembrandtedDocument2NEPool rdoc2nepool = new ImportRembrandtedDocument2NEPool()
        HashMap status = [sync:0, notsync:0]
         
        int rows
        
        if (cmd.getOptionValue("from")== "DB") {
            List docs = cmd.getOptionValues("docid")
            if (!docs) {
                println "Set to DB, but no doc_id found. Please set --docid args. Exiting."
                System.exit(0)
            }
            docs.each{doc -> 
                HashMap status_ = rdoc2nepool.syncNEPoolFromDoc(doc)             
                if (status_) {
                    status.sync += status_.sync
                    status.notsync += status_.notsync
                }
            }
        
        } else if (cmd.getOptionValue("from")== "batch") {
            status = rdoc2nepool.syncBatchOfDocsToPool( 
                    cmd.getOptionValue("col"), Integer.parseInt(cmd.getOptionValue("ndocs")))
         			
        } else {
            println "Unknown value for --from. Exiting."
            System.exit(0)		    
        }
        log.info "Done. Synced ${status.sync} doc(s) successully, and ${status.notsync} doc(s) were not synced."
    }   
}
