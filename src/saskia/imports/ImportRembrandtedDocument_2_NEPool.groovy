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

import rembrandt.io.DocStats
import rembrandt.obj.Document
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import org.apache.log4j.*
import org.apache.commons.cli.*
import saskia.bin.Configuration
import saskia.db.DocStatus;
import saskia.db.obj.*;
import saskia.db.table.*
import saskia.db.database.*
import saskia.util.validator.*
/** 
 * This class imports Wikipedia raw documents in the Saskia database to 
 * HTML documents in the Saskia's page table
*/

class ImportRembrandtedDocument_2_NEPool extends Import {

	
	RembrandtReader reader

	Integer rembrandted_doc_pool_size

	RembrandtedDoc current_rdoc
	String process_signature	     

	String mode
	def docs
	
	String defaultanswer
	String answer
	BufferedReader input
	
	TaskTable taskTable
	JobTable jobTable
	RembrandtedDocTable rembrandtedDocTable
	NETable neTable
	NENameTable naNameTable
	NECategoryTable neCategoryTable
	NETypeTable neTypeTable
	NESubtypeTable neSubtypeTable
	EntityTable entityTable
	GeoscopeTable geoscopeTable
	
	public ImportRembrandtedDocument_2_NEPool() {
	   super()
		rembrandted_doc_pool_size = conf.getInt("saskia.imports.rembrandted_doc_pool_size",30)
	}
	
	public prepare() {
		reader = new RembrandtReader(new RembrandtStyleTag(conf.get("rembrandt.input.styletag.lang", this.lang)))
		process_signature = this.toString()
		log.info ("Process signature: ${process_signature}")

	 taskTable = db.getDBTable("TaskTable")
	 jobTable = db.getDBTable("JobTable")
	 rembrandtedDocTable = db.getDBTable("RembrandtedDocTable")
	 neTable = db.getDBTable("NETable")
	 naNameTable = db.getDBTable("NENameTable")
	 neCategoryTable = db.getDBTable("NECategoryTable")
	 neTypeTable = db.getDBTable("NETypeTable")
	 neSubtypeTable = db.getDBTable("NESubtypeTable")
	 entityTable = db.getDBTable("EntityTable")
	 geoscopeTable = db.getDBTable("GeoscopeTable")
		
	}
	
	public void setCollection(Collection collection) {
		super.setCollection(collection)
		this.lang = collection.col_lang
	}
	
	public importer() {
		if (mode == "single") {
		  
			println "Single mode avaliable, sorry."
		} else if  (mode == "multiple") {
		   importMultipleDocs()
		}
	} 
		
		
	public importMultipleDocs() {
			
		int rows = 0 
		
		String taskname = "task_R2P_"+System.currentTimeMillis()

		def stats = new DocStats(docs)
		stats.begin()

		Task task = Task.createNew(taskTable, 
			[tsk_user:collection.col_owner, tsk_task:taskname, 
			tsk_collection:collection, tsk_type:"R2P",
		   tsk_priority:0, tsk_limit:docs, tsk_offset:0, tsk_done:0,
			tsk_scope:"BAT", tsk_persistence:"TMP", tsk_status:"PRO", tsk_comment:""
			])
		
		task.tsk_id = task.addThisToDB()
		
		for (int i=docs; i > 0; i -= rembrandted_doc_pool_size) {
		    
		    int limit = (i > rembrandted_doc_pool_size ? rembrandted_doc_pool_size : i)
		    log.debug "Initial batchSize: $docs Remaining: $i Next pool size: $limit"
		    List<RembrandtedDoc> rdocs = rembrandtedDocTable.getBatchDocsToSyncNEPool(
				task, process_signature, collection, limit)
				
		    log.debug "Got ${rdocs?.size()} RembrandtedDoc(s)."
			 // note: these RembrandtDocs come with Jobs on it
		    
		    // if it's null, then there's no more docs to process. Leave the loop.
		    if (!rdocs) {
				log.info "No more docs to analyse, exiting early."	
			  break
			 } 
		    
		    rdocs?.each {rdoc ->
		    	log.debug "Syncing doc -> NE pool for rdoc ${rdoc.doc_original_id}."
		    	stats.beginDoc(rdoc.doc_id)
    			syncNEPoolFromDoc(rdoc, collection, task, process_signature)		
				task.incrementDone()
		    	stats.endDoc()					   			  
		    	stats.printMemUsage()	
		    }
		}
		stats.end()
		task.updateValue('tsk_status', 'FIN')
	}


	/* If you are thinking in calling this function directly, that's a bad idea unless you're pretty SURE 
	 * what you're doing... that is,  make sure the documents are unlocked / good to process, respect 
	 * the database flags for proc, sync and edit, otherwise you're on your own
	 */
	private syncNEPoolFromDoc(RembrandtedDoc rdoc, Collection collection, Task task, String process_signature) {	
		
		try {
			rdoc.doc_job.changeEditStatusInDBto(DocStatus.LOCKED)	
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
		// let's remove it from job list
			rdoc?.doc_job?.removeThisFromDB()
			log.debug "Done. Doc status is now ${DocStatus.SYNCED}."
			current_rdoc = null // mark it, for abort if CTRL-C is pressed

			log.info "RembrandtedDoc ${rdoc.doc_id} synced."
			status.imported++
		} catch(Exception e) {
			log.error "Erro : "+e.getMessage()
			status.failed++
		}
	}
	
	public abort() {
	    log.warn "\nAborting... backtracking with ${current_rdoc?.doc_id}"
	    if (current_rdoc) {
				current_rdoc?.doc_job?.removeThisFromDB()
			log.warn "Removing job ${current_rdoc?.doc_job}"
			current_rdoc.changeProcStatusInDBto(DocStatus.NOT_READY)
			log.warn "Doc proc status changed to NOT_READY."
	    }
	}
	
	
	   static void main(args) {  

		Options o = new Options()
		Configuration conf = Configuration.newInstance()
		
		String DEFAULT_DB_NAME = "main"
		String DEFAULT_COLLECTION_NAME = "default_collection"
		String DEFAULT_MODE = "multiple"
		Integer DEFAULT_DOCS = "100"
		
		o.addOption("db", true, "target Saskia DB (main/test)")
		o.addOption("col", true, "target collection name/id of the DB")
	    o.addOption("mode", true, "mode: (single/multiple)")
	    o.addOption("docs", true, "number of docs (multiple mode), or docid (single mode)")
	    o.addOption("answer", true, "automatic answer for [y]es/[n]o/[a]lways/n[e]ver questions")
		o.addOption("help", false, "Gives this help information")

		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

		ImportSourceDocument_2_RembrandtedDocument importer = new ImportSourceDocument_2_RembrandtedDocument()

		log.info "*********************************************************"
		log.info "* This class loads the Second HAREM+s Golden Collection *"
		log.info "* in a clean formt, into SourceDocuments. For that, all *"
		log.info "* existing NE tags will be stripped.                    *"
		log.info "*********************************************************"

		// --help
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java "+importer.class.name, o )
			System.exit(0)
		}

		// --db
		SaskiaDB db = new DBValidator()
			.validate(cmd.getOptionValue("db"), DEFAULT_DB_NAME)
		importer.setDb(db)
		log.info "DB: $db"

		// --col
		Collection collection = new CollectionValidator(db)
			.validate(cmd.getOptionValue("col"), DEFAULT_COLLECTION_NAME)
		importer.setCollection(collection)
		log.info "Collection: $collection"

		// --mode		
		String mode = new ModeValidator()
			.validate(cmd.getOptionValue("mode"), DEFAULT_MODE)
		importer.setMode(mode)
		log.info "Mode: $mode"
		
		// --docs		
		def docs = DocsValidator()
			.validateDocs(cmd.getOptionValue("docs"), DEFAULT_DOCS)
		importer.setDocs(docs)
		log.info "Docs: $docs "

		importer.prepare()
		importer.importer()
		println importer.statusMessage()
	}
}
