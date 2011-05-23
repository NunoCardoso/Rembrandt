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

import saskia.db.DocStatus;
import saskia.db.obj.Collection;
import saskia.db.obj.RembrandtedDoc;
import rembrandt.obj.Document
import rembrandt.obj.ListOfNE
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import org.apache.log4j.*

/** 
 * This class resyncs from NEPool to RembrandtedDoc
*/

class ImportNEPool2RembrandtedDocument {
	
	static Logger log = Logger.getLogger("Saskia")
	RembrandtReader reader
	RembrandtedDoc current_rdoc
	Collection collection
	String ynae
	BufferedReader input
	static final int REMBRANDTED_DOC_POOL_SIZE=30

	public ImportNEPool2RembrandtedDocument(String lang) {
	    reader = new RembrandtReader(new RembrandtStyleTag(lang))
        }
	
	public HashMap syncBatchOfDocsFromPool( collection_, int batchSize = 100) {
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
		
		for (int i=batchSize; i > 0; i -= REMBRANDTED_DOC_POOL_SIZE) {
		    
		    int limit = (i > REMBRANDTED_DOC_POOL_SIZE ? REMBRANDTED_DOC_POOL_SIZE : i)
		    log.debug "Initial batchSize: $batchSize Remaining: $i Next pool size: $limit"
		    List<RembrandtedDoc> rdocs =  RembrandtedDoc.getBatchDocsToSyncFromNEPool(collection, limit)

		    log.debug "Got ${rdocs?.size()} RembrandtedDoc(s)."
		    
		    // if it's null, then there's no more docs to process. Leave the loop.
		    if (!rdocs) return status
 
		    rdocs.each {rdoc ->
		    	log.debug "Syncing doc -> NE pool for rdoc ${rdoc.doc_original_id}."
		    	HashMap status_
		    // failcheck is done at the parse method. If REMBRANDT fails, it should release the lock and return 
		    // a failed status, and move to the next one. 
		    	try {
		    	    status_ = syncNEPoolToDoc(rdoc)		
		    	} catch(Exception e) {
		    	    abort()
		    	}
			 	   
		    	if (status_) {
			 		status.sync += status_.sync
			 		status.notsync += status_.notsync	
		    	}
		    }
		}

		 return status
	}

	public HashMap syncDocFromPool( collection_, long doc_id) {
		 log.trace "Requesting NE pool-> doc  sync for doc_id ${doc_id}."
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
		
		// NOT THREAD SAFE!!!
		 RembrandtedDoc rdoc = RembrandtedDoc.getFromID(doc_id)
		 
		 // if there's a RembrandtedDoc on the DB
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
			}//if markedAsBad
			
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
			
		    if (!(rdoc.doc_sync.isGoodToBeSyncedFromNEPool())) {
			
			   if ((!ynae) || (ynae == "y") || (ynae == "n")) {
				    ynae = null
					input = new BufferedReader(new InputStreamReader(System.in))
					while (!(ynae == "a" || ynae == "e" || ynae == "n" || ynae == "y")) {
					    println "RembrandtedDoc ${doc_id} in DB is marked as more RECENT than the NEs in the pool."
					    println "By pressing y or a, you'll lose recent changes to the document. Proceed? ([y]es, [a]lways, [n]o, n[e]ver)"
						ynae = input.readLine().trim()
					}
			   }
		  	   if ( (ynae == "n") || (ynae == "e")) {
					log.warn "Skipping RembrandtedDoc (sync:${rdoc.doc_sync})."
					status.notsync++
					return status
			   }
		    }// if rdoc is good to be synced from NE pool
		 }// if rdoc	
		 		 
		 try {
		    status = syncNEPoolToDoc(rdoc)		
		 } catch(Exception e) {
		     e.printStackTrace()
		     abort()
		 }
		 return status
	}	
	
	/* If you are thinking in calling this function directly, that's a bad idea unless you're pretty SURE 
	 * what you're doing... that is,  make sure the documents are unlocked / good to process, respect 
	 * the database flags for proc, sync and edit, otherwise you're on your own
	 */
	private HashMap syncNEPoolToDoc(RembrandtedDoc rdoc) {	
		
	    HashMap status = [sync:0, notsync:0]
		rdoc.changeEditStatusInDBto(DocStatus.LOCKED)	
		current_rdoc = rdoc // mark it, for abort if SIGINT is called
		log.info "Syncing RembrandtedDoc ${rdoc.doc_id} from NE pool..."
		
		log.trace "Creating a Document from out-of-synced RembrandtedDoc..."
		// Create a Rembrandt document object from rembrandted document text
		Document doc = reader.createDocument(rdoc.doc_content)

		log.trace "Title: ${doc.title_sentences.size()} sentences, ${doc.titleNEs.size()} NEs."
		log.trace "Body: ${doc.body_sentences.size()} sentences, ${doc.bodyNEs.size()} NEs."
	
		log.trace "Now cleaning NEs from doc."
		doc.titleNEs = new ListOfNE()
		doc.bodyNEs = new ListOfNE()
		log.trace "Now adding NEs from pool into the Document."
		
		// add nes
		rdoc.addNEsFromSaskia(doc.titleNEs, "T")
		rdoc.addNEsFromSaskia(doc.bodyNEs, "B")
		log.trace "Title: ${doc.title_sentences.size()} sentences, ${doc.titleNEs.size()} NEs."
		log.trace "Body: ${doc.body_sentences.size()} sentences, ${doc.bodyNEs.size()} NEs."
	
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
	    String usage = "Usage: saskia.imports.ImportNEPool2RembrandtedDocument -fromDB [lang] [target-collection] [doc_id1] [doc_id2] etc...\n"+
		  "       saskia.imports.ImportNEPool2RembrandtedDocument -batchSize [lang] [target-collection] [number-documents]\n"+
		  "Learn it.";
	  
		if (!args || args.size() < 4) {
		   println usage
		   System.exit(0)
		}

		ImportNEPool2RembrandtedDocument nepool2rdoc
		HashMap status = [sync:0, notsync:0]
		
//		log.info "There are "+c.taggedDocs()+" documents already tagged."
//		log.info "There are "+c.untaggedDocs()+" documents to be tagged."
		int rows
//		SaskiaShutdown shutdown
		
		if (args[0] == "-fromDB") {
		    nepool2rdoc = new ImportNEPool2RembrandtedDocument(args[1]) 
//		    shutdown = new SaskiaShutdown(nepool2rdoc)
//		    Runtime.getRuntime().addShutdownHook(shutdown);

		    for (int i=3; i<args.size(); i++) {
			 	HashMap status_ = nepool2rdoc.syncDocFromPool(args[2], Long.parseLong(args[i]) )
			 	if (status_) {
			 	    status.sync += status_.sync
			 	    status.notsync += status_.notsync	
			 	}
			 }
		} else if (args[0] == "-batchSize") { 
		    nepool2rdoc = new ImportNEPool2RembrandtedDocument(args[1]) 
//		    shutdown = new SaskiaShutdown(nepool2rdoc)
//		    Runtime.getRuntime().addShutdownHook(shutdown);

		    status = nepool2rdoc.syncBatchOfDocsFromPool(args[2], Integer.parseInt(args[3]) )
		} else {
			println usage
			System.exit(0)
		}
		log.info "Done. ${status.sync} docs were synced,  ${status.notsync} docs were NOT synced."
//		Runtime.getRuntime().removeShutdownHook(shutdown)
	}
	
}