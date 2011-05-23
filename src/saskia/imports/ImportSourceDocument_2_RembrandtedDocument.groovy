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

import org.apache.log4j.*
import org.apache.commons.cli.*

import saskia.bin.Configuration
import rembrandt.obj.Document
import rembrandt.bin.Rembrandt
import rembrandt.bin.RembrandtCore
import saskia.db.DocStatus;
import saskia.db.database.*
import saskia.db.obj.*
import saskia.db.table.*
import rembrandt.io.DocStats
import rembrandt.io.HTMLReader
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag
import rembrandt.obj.Document

import saskia.util.validator.*

/** Picks documents in the Saskia's SOURCE_DOC table (in HTML format),
 *  Tags them with REMBRANDT, stores them in the DOC / REMBRANDTED_DOC tables.
 */
class ImportSourceDocument_2_RembrandtedDocument extends Import {


	// readers
	HTMLReader readerHTML
	RembrandtReader readerRembrandt
	RembrandtWriter writer
	RembrandtStyleTag styletag
        
	String lang
	String taglang

	RembrandtedDoc current_rdoc
	SourceDoc current_sdoc
	
	RembrandtCore core 
	String rembrandtversion
	String process_signature	     
	
	/** don't handle big docs */
	Long max_allowed_html_size
	
	Integer source_doc_pool_size
	
	String mode
	def docs
	
	String defaultanswer
	String answer
	BufferedReader input

	TagTable tagTable
	TaskTable taskTable
	JobTable jobTable
	SourceDocTable sourceDocTable
	RembrandtedDocTable rembrandtedDocTable

	public ImportSourceDocument_2_RembrandtedDocument() {
		super()
		max_allowed_html_size = conf.getLong("saskia.imports.max_allowed_html_size",100000)
		source_doc_pool_size = conf.getInt("saskia.imports.source_doc_pool_size",10)
	}
	
	public prepareRembrandt() {
	
		taglang = conf.get("rembrandt.output.styletag.lang", this.lang)
		styletag = new RembrandtStyleTag(taglang)
	   readerHTML = new HTMLReader()
	   readerRembrandt = new RembrandtReader(styletag)
	   writer = new RembrandtWriter(styletag)
	   core = Rembrandt.getCore(this.lang, "harem")
		log.info ("Rembrandt core initialized: ${core.class.name}")

		// get rembrandtVersion
		rembrandtversion = Rembrandt.getVersion()
		if (rembrandtversion.indexOf("-") > 0) rembrandtversion =rembrandtversion.substring(0, rembrandtversion.indexOf("-"))
		else rembrandtversion="unknown"
		log.info ("Rembrandt version: ${rembrandtversion}")
		
		process_signature = this.toString()
		log.info ("Process signature: ${process_signature}")

		// Get tag
		tagTable = db.getDBTable("TagTable")
		jobTable = db.getDBTable("JobTable")
		taskTable = db.getDBTable("TaskTable")
		sourceDocTable = db.getDBTable("SourceDocTable")
		rembrandtedDocTable = db.getDBTable("RembrandtedDocTable")
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
		
		Tag tag = tagTable.getFromVersion(rembrandtversion)
		if (!tag) {
			tag = new Tag( tagTable, rembrandtversion, "")
			tag.tag_id = tag.addThisToDB()
		}
	   	    
		def stats = new DocStats(docs)
		stats.begin()
		
		String taskname = "task_S2R_"+System.currentTimeMillis()
		Task task = Task.createNew(taskTable, 
			[tsk_user:collection.col_owner, tsk_task:taskname,
			 tsk_collection:collection, tsk_type:"S2R",
		    tsk_priority:0, tsk_limit:docs, tsk_offset:0, tsk_done:0,
			 tsk_scope:"BAT", tsk_persistence:"TMP", tsk_status:"PRO", tsk_comment:""
			])
			
		task.tsk_id = task.addThisToDB()
				
		for(int i=docs; i > 0; i -= source_doc_pool_size) {
		    
			int limit = (i > source_doc_pool_size ? source_doc_pool_size : i)
			log.debug "Initial batchSize: $docs Remaining: $i Next pool size: $limit"
			List<SourceDoc> sdocs = sourceDocTable.getNextProcessableAndUnlockedDoc(
				task, process_signature, collection, limit)
		   log.debug "Got ${sdocs?.size()} SourceDoc(s)."
		    
			if (!sdocs) {
			  log.info "No more source docs to process, exiting early."	
			  break
			}
			 	
			sdocs?.each {sdoc ->
			 	stats.beginDoc(sdoc.sdoc_id)
		   	importCurrentDoc(sdoc, collection, task, process_signature, tag)
				task.incrementDone()
				stats.endDoc()
				stats.printMemUsage()
			} 
 		}	
		stats.end()	
		// signal that the task is done
		task.updateValue('tsk_status', 'FIN')
	}
	 	
   private importCurrentDoc(
		SourceDoc sdoc, Collection collection, Task task, String process_signature, Tag tag) {
	
	// first: fetch document from table page
		if (!sdoc || !tag) {
			log.warn "No SourceDoc to import!"
			return null
		}
		
		String htmlText = null
		String rdocText = null
		String pageTitle = ""
	
		log.trace "Going to convert SourceDoc $sdoc to a RembrandtedDocument"
		log.trace "Tag used: ${tag}"
		
		sdoc.sdoc_job.changeEditStatusInDBto(DocStatus.LOCKED)
		current_sdoc = sdoc
		
		// But first, let's see if there's one already, and if it allows an overwrite
		RembrandtedDoc rdoc = rembrandtedDocTable.getFromOriginalDocIDandCollection(
			sdoc.sdoc_original_id, sdoc.sdoc_collection)
		
		if (rdoc) {	    
			//log.warn "There is a RembrandtedDoc ${doc} on the DB."
			if (rdoc.doc_proc.isMarkedAsBad()) {
			    if (defaultanswer) {
					answer = defaultanswer
					log.debug "RembrandtedDoc ${rdoc.doc_id} in DB is marked as bad, automatic answer is ${answer}."
				} else {
				   input = new BufferedReader(new InputStreamReader(System.in))
					while (!(answer == "a" || answer == "e" || answer == "n" || answer == "y")) {
					    println "RembrandtedDoc ${rdoc.doc_id} in DB is marked as bad. Proceed? ([y]es, [a]lways, [n]o, n[e]ver)"
  
						answer = input.readLine().trim()
					}
				   input = null
				}
			   if ( (answer == "n") || (answer == "e")) {
					log.warn "RembrandtedDoc ${rdoc.doc_proc} skipped."
					status.skipped++
					return 
			   }
			}
			
			//println "Searching for jobs for "+rdoc.doc_id+", "+RembrandtedDoc.job_doc_type_label
			// let's check if there is a rdoc associated to this rdoc, to see if it's locked or not.
			Job rdocjob = jobTable.getFromDocIDAndDocType(rdoc.doc_id, 
				RembrandtedDocTable.job_doc_type_label)
			
			// if there is an associated job, let's test it
			if (rdocjob) rdoc.doc_job = rdocjob

			// don't forget to test if there is a job.
			if (rdoc.doc_job?.job_doc_edit?.isLocked()) {
			    
				if (defaultanswer) {
					answer = defaultanswer
					log.debug "RembrandtedDoc ${rdoc.doc_id} in DB is locked, automatic answer is ${answer}."
				} else {
					 input = new BufferedReader(new InputStreamReader(System.in))
				    while (!(answer == "a" || answer == "e" || answer == "n" || answer == "y")) {
						println "RembrandtedDoc ${rdoc.doc_id} in DB is locked. Unlock and proceed? ([y]es, [a]lways, [n]o, n[e]ver)"
						answer = input.readLine().trim()
				    }
				    input = null
				}
				if ( (answer == "n") || (answer == "e")) {

				    log.warn "RembrandtedDoc ${rdoc.doc_edit} skipped."
				    status.skipped++
				    return 
				}
		   }
		
		   // not returned - means that rdoc is not locked (== there is no rdocjob), 
			// or we are forcing an overwrite (== there is a rdocjob). 
		   // let's lock the target and complete the 'job' object

			// note that, since this is a batch job, there is no task. 
			if (rdoc.doc_job == null) {
				rdoc.doc_job = Job.createNew(jobTable, 
					[job_task:task, job_worker:process_signature, //  
					job_doc_type:RembrandtedDocTabke.job_doc_type_label,
					job_doc_id:rdoc.doc_id, job_doc_edit:DocStatus.LOCKED, job_doc_edit_date:new Date()
					])
				rdoc.doc_job.job_id = rdoc.doc_job.addThisToDB()
			} else {
				rdoc.doc_job.job_doc_id = rdoc.doc_id
				rdoc.doc_job.job_doc_edit = DocStatus.LOCKED
			}
		   current_rdoc = rdoc
		}
		
		if (sdoc.sdoc_content) log.debug "HTML content of SourceDoc ${sdoc} has ${sdoc.sdoc_content.size()} bytes." 

		else {
			log.warn "HTML content of SourceDoc ${sdoc} is empty. Skipping." 
			status.skipped++
			rdoc?.doc_job?.removeThisFromDB() // check first if rdoc is not null					
			sdoc.changeProcStatusInDBto(DocStatus.FAILED) // mark it so next time we'll not use it			
			sdoc.sdoc_job.removeThisFromDB() // release him
			current_sdoc = null
			return 
		}
		// go form HTML to REMBRANDT and tokenized versions.
		log.trace "Creating a REMBRANDT document with HTML text of ${sdoc.sdoc_content.size()} bytes." 
		
		if (sdoc.sdoc_content.size() > max_allowed_html_size) {
			log.warn "HTML content of SourceDoc ${sdoc} is too big (${sdoc.sdoc_content.size()} bytes), over max allowed size ${max_allowed_html_size}. Skipping." 
			status.skipped++
			rdoc?.doc_job?.removeThisFromDB()
			sdoc.changeProcStatusInDBto(DocStatus.TOO_BIG) // mark it so next time we'll not use it
			sdoc.sdoc_job.removeThisFromDB() // release him
			current_sdoc = null
			return  	    
		}
		
		Document d 
	
		if (sdoc.sdoc_content.startsWith("<HTML") || sdoc.sdoc_content.startsWith("<html"))
		 d = readerHTML.createDocument(sdoc.sdoc_content)
		else if (sdoc.sdoc_content.startsWith("<DOC")) 
		 d = readerRembrandt.createDocument(sdoc.sdoc_content)
		
		log.trace "Document's number of sentences in title: ${d?.title_sentences?.size()}"		
		log.trace "Document's number of sentences in body: ${d?.body_sentences?.size()}"		
		d.docid = sdoc.sdoc_id
		d.lang = sdoc.sdoc_lang
		d.taglang = taglang
        
		try {
		    d = core.releaseRembrandtOnDocument(d)
		    rdocText = writer.printDocument(d)
		} catch(Exception e) {
		    log.error "REMBRADNTing document failed: "+e.getMessage()
		    abort()
		    status.failed++
		    return 
		}

		if (!rdocText) {
			log.error "Did NOT get a valid REMBRANDTed text. Skipping."
			status.skipped++
         rdoc?.doc_job?.removeThisFromDB()
			sdoc.changeProcStatusInDBto(DocStatus.FAILED) // mark it so next time we'll not use it			
			sdoc?.sdoc_job?.removeThisFromDB() // release him
         current_sdoc = null
			return 
		}
		
		// it may be a long time before we visited the DB, and I don't  trust the autoReconnect 

		if (db.getDB() == null) {
		    log.fatal "DB seems down, locked or whatever, I can't connect to it. Exiting."
		    abort()
		}
        
		// new doc with the info	
		if (rdoc) {
			
			RembrandtedDoc newrdoc = RembrandtedDoc.createNew(rembrandtedDocTable, 
				[doc_id:rdoc.doc_id,
				doc_collection:collection, doc_original_id:sdoc.sdoc_original_id,
				doc_lang:sdoc.sdoc_lang, doc_content:rdocText, doc_date_created:sdoc.sdoc_date, 
				doc_job:rdoc.doc_job]
			)

			newrdoc.replaceThisToDB()
			newrdoc.associateWithTag(tag)
			
			status.updated++
			newrdoc.doc_job.removeThisFromDB()
			
			current_rdoc = null
			sdoc.sdoc_job.removeThisFromDB()
			sdoc.addDocID(rdoc.doc_id)
			
			newrdoc.changeProcStatusInDBto(DocStatus.READY) // mark it so next time we'll not use it
			sdoc.changeProcStatusInDBto(DocStatus.READY) // mark it so next time we'll not use it

			newrdoc.changeSyncStatusInDBto(DocStatus.NOT_SYNCED_DOC_CHANGED) 
		} 
				
		else if (!rdoc)  {
				
		    rdoc = RembrandtedDoc.createNew(rembrandtedDocTable, 
				[doc_original_id:sdoc.sdoc_original_id, 
				doc_collection:collection, doc_lang:sdoc.sdoc_lang,
			   doc_date_created:sdoc.sdoc_date, doc_content:rdocText]
			 )
			
		    rdoc.doc_id = rdoc.addThisToDB()
		    if (!rdoc.doc_id) 
				throw new IllegalStateException("Document $rdoc was added to DB, but hasn't ID! Check it!")
		    
		    rdoc.associateWithTag(tag)
			
		    status.imported++

		    rdoc.changeProcStatusInDBto(DocStatus.READY) // mark it so next time we'll not use it
		    rdoc.changeSyncStatusInDBto(DocStatus.NOT_SYNCED_DOC_CHANGED) 
		    // no need to check jobs on this rdoc - it's brand new, so no job
			 
		    sdoc.addDocID(rdoc.doc_id)
		    sdoc.changeProcStatusInDBto(DocStatus.READY) // mark it so next time we'll not use it
		    rdoc.changeSyncStatusInDBto(DocStatus.NOT_SYNCED_DOC_CHANGED) 
		    sdoc.sdoc_job.removeThisFromDB()

		    current_rdoc = null
		    current_sdoc = null
		    //rdoc.checkDocHasEntity()
		}
		return
	}

   public abort() {
	    log.warn "\nAborting... backtracking"
	    if (current_rdoc) {
			current_rdoc.doc_job?.removeThisFromDB()
			log.warn "RembrandtedDoc job removed."
			current_rdoc.changeProcStatusInDBto(DocStatus.NOT_READY)
			log.warn "RembrandtedDoc proc status changed to NOT_READY."            
       }  
	    if (current_sdoc) {
			current_sdoc.changeProcStatusInDBto(DocStatus.FAILED)
			log.warn "SourceDoc proc status changed to FAILED."
			current_sdoc.sdoc_job?.removeThisFromDB()
			log.warn "SourceDoc job removed."
	 	}
	}
   
   static void main(args) {  

		Options o = new Options()
		Configuration conf = Configuration.newInstance()
		
		String DEFAULT_DB_NAME = "main"
		String DEFAULT_COLLECTION_NAME = "default_collection"
		String DEFAULT_MODE = "multiple"
		Integer DEFAULT_DOCS = 100
		
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
		def docs = new DocsValidator(mode)
			.validate(cmd.getOptionValue("docs"), DEFAULT_DOCS)
		importer.setDocs(docs)
		log.info "Docs: $docs "

		def answer = new InteractiveAnwserValidator()
			.validate(cmd.getOptionValue("answer"), null, false)		
		importer.setDefaultanswer(answer)

		importer.prepareRembrandt()
		importer.importer()
		println importer.statusMessage()
	}
}