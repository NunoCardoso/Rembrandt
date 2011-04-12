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
import saskia.db.SaskiaDB
import saskia.io.SourceDoc
import rembrandt.io.DocStats
import rembrandt.io.HTMLDocumentReader
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtWriter
import saskia.io.Task
import saskia.io.User
import saskia.io.Job
import saskia.io.Collection
import saskia.io.RembrandtedDoc
import saskia.io.Tag
import saskia.io.DocStatus
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag
import rembrandt.obj.Document

/** Picks documents in the Saskia's SOURCE_DOC table (in HTML format),
 *  Tags them with REMBRANDT, stores them in the DOC / REMBRANDTED_DOC tables.
 */
class ImportSourceDocument2RembrandtedDocument {

	static Logger log = Logger.getLogger("SaskiaImports")
	HTMLDocumentReader readerHTML
	RembrandtReader readerRembrandt
	RembrandtWriter writer
	RembrandtStyleTag styletag
        
	String lang
	String taglang
	def db // usado só para verificar a comunicação
	Configuration conf
	RembrandtedDoc current_rdoc
	SourceDoc current_sdoc
	Collection collection
	CommandLine cmd
	
	RembrandtCore core 
	     
	/** don't handle big docs */
	Long max_allowed_html_size
	
	/* internal pool size of SourceDocs. 
	 * I can ask to process 100000 documents, but I'll get small sets at a time, for the sake of concurrency
	 */
	Integer source_doc_pool_size
	
	String ynae
	BufferedReader input
	
	public ImportSourceDocument2RembrandtedDocument(String lang) {
		conf = Configuration.newInstance()
		conf.set("global.lang",lang)
		max_allowed_html_size = conf.getLong("saskia.imports.max_allowed_html_size",100000)
		source_doc_pool_size = conf.getInt("saskia.imports.source_doc_pool_size",10)
		this.lang =lang
		taglang = conf.get("rembrandt.output.styletag.lang", lang)
	    db = SaskiaDB.newInstance()
	    styletag = new RembrandtStyleTag(taglang)
	    readerHTML = new HTMLDocumentReader()
	    readerRembrandt = new RembrandtReader(styletag)
	    writer = new RembrandtWriter(styletag)
	    core = Rembrandt.getCore(lang, "harem")
        }
	
	/**
	 * Parse a batch of source documents.
	 * @param batchSize number of docs to parse. Note that the batch size, the overall number of documents 
	 * to parse by this process, is different from the internal batch size, that's the small sets that are 
	 * being queued to be gentle with the DB and other processes.
	 * @param sdoc_lang the language of the sourcedoc
	 * @param collection_ the name (String) or id (long) of the collection
	 * @param tag_version the tag
	 * @return HashMap with count statistics   
	 */
	public HashMap parseBatch(int batchSize = 100, Collection collection, User user, 
		String process_signature, String tag_version) {

		HashMap status = [inserted:0, updated:0, skipped:0, failed:0]

		// Get tag
		Tag tag = Tag.getFromVersion(tag_version)
		if (!tag) {
			tag = new Tag(tag_version:tag_version, tag_comment:null)
			tag.tag_id = tag.addThisToDB()
		}
	    	    
 		def stats = new DocStats(batchSize)
 		stats.begin()

		String taskname = "task_S2R_"+System.currentTimeMillis()
		Task task = new Task(tsk_user:user, tsk_task:taskname,
			tsk_collection:collection, tsk_type:"S2R",
		   tsk_priority:0, tsk_limit:batchSize, tsk_offset:0, tsk_done:0,
			tsk_scope:"BAT", tsk_persistence:"TMP", tsk_status:"PRO", tsk_comment:"");
			
			task.tsk_id = task.addThisToDB()
				
			for(int i=batchSize; i > 0; i -= source_doc_pool_size) {
		    
		    	int limit = (i > source_doc_pool_size ? source_doc_pool_size : i)
		    	log.debug "Initial batchSize: $batchSize Remaining: $i Next pool size: $limit"
		    	List<SourceDoc> sdocs = SourceDoc.getNextProcessableAndUnlockedDoc(task, process_signature, 
			 		collection, limit)
		    log.debug "Got ${sdocs?.size()} SourceDoc(s)."
		    
			 if (sdocs?.size() == 0) {
			   log.info "No more source docs to process, exiting early."	
				break
			 }
			 	
			 sdocs?.each {sdoc ->
			 	stats.beginDoc(sdoc.sdoc_id)
		    // failcheck is done at the parse method. If REMBRANDT fails, it should release the lock and return 
		    // a failed status, and move to the next one. 
		    	HashMap status_ = parse(sdoc, collection, task, process_signature, tag)

				task.incrementDone()
 				stats.endDoc()
  				stats.printMemUsage()
  			
		    	if (status_) {
		    	    status.inserted += status_.inserted
		    	    status.updated += status_.updated
		    	    status.skipped += status_.skipped	
		    	    status.failed += status_.failed
		    	}
			} 
		}
 		stats.end()	
		// signal that the task is done
		Task.updateValue(task.tsk_id, 'tsk_status', 'FIN')
		return status
	}
	 	
	/** 
	 * This method processes a SourceDoc and inserts into RembrandtedDoc/Doc tables 
	 */
   public HashMap parse(SourceDoc sdoc, Collection collection, Task task, String process_signature, Tag tag) {
	
	// first: fetch document from table page
		if (!sdoc || !tag) return null
		
		HashMap status = [inserted:0, updated:0, skipped:0, failed:0]
		
		String htmlText = null
		String rdocText = null
		String pageTitle = ""
	
		log.trace "Going to convert SourceDoc $sdoc to a RembrandtedDocument"
		log.trace "Tag used: ${tag}"
		
		sdoc.sdoc_job.changeEditStatusInDBto(DocStatus.LOCKED)
		current_sdoc = sdoc
		
		// But first, let's see if there's one already, and if it allows an overwrite
		RembrandtedDoc rdoc = RembrandtedDoc.getFromOriginalDocIDandCollection(
			sdoc.sdoc_original_id, sdoc.sdoc_collection)
		
		if (rdoc) {	    
			//log.warn "There is a RembrandtedDoc ${doc} on the DB."
			if (rdoc.doc_proc.isMarkedAsBad()) {
			    if ((!ynae) || (ynae == "y") || (ynae == "n")) {
				    ynae = null
					if (cmd?.hasOption("answer"))  {
					    ynae = cmd.getOptionValue("answer")	
					    log.debug "RembrandtedDoc ${rdoc.doc_id} in DB is marked as bad, automatic answer is ${ynae}."
					}
				    if (!ynae) input = new BufferedReader(new InputStreamReader(System.in))
					while (!(ynae == "a" || ynae == "e" || ynae == "n" || ynae == "y")) {
					    println "RembrandtedDoc ${rdoc.doc_id} in DB is marked as bad. Proceed? ([y]es, [a]lways, [n]o, n[e]ver)"
  
						ynae = input.readLine().trim()
					}
				    input = null
				}
			    if ( (ynae == "n") || (ynae == "e")) {
					log.warn "RembrandtedDoc ${rdoc.doc_proc} skipped."
					status.skipped++
					return status
			    }
			}
			
			//println "Searching for jobs for "+rdoc.doc_id+", "+RembrandtedDoc.job_doc_type_label
			// let's check if there is a rdoc associated to this rdoc, to see if it's locked or not.
			Job rdocjob = Job.getFromDocIDAndDocType(rdoc.doc_id, RembrandtedDoc.job_doc_type_label)

			//println "Got $rdocjob"
			
			// if there is an associated job, let's test it
			if (rdocjob) rdoc.doc_job = rdocjob

			// don't forget to test if there is a job.
			if (rdoc.doc_job?.job_doc_edit?.isLocked()) {
			    
				if ((!ynae) || (ynae == "y") || (yane == "n")) {
				    ynae = null
					if (cmd?.hasOption("answer"))  {
					    ynae = cmd.getOptionValue("answer")	
					    log.debug "RembrandtedDoc ${rdoc.doc_id} in DB is locked, automatic answer is ${ynae}."

					}
				    if (!ynae) input = new BufferedReader(new InputStreamReader(System.in))
				    while (!(ynae == "a" || ynae == "e" || ynae == "n" || ynae == "y")) {
						println "RembrandtedDoc ${rdoc.doc_id} in DB is locked. Unlock and proceed? ([y]es, [a]lways, [n]o, n[e]ver)"
						ynae = input.readLine().trim()
				    }
				    input = null
				}
				if ( (ynae == "n") || (ynae == "e")) {

				    log.warn "RembrandtedDoc ${rdoc.doc_edit} skipped."
				    status.skipped++
				    return status
				}
		   }
		
		   // not returned - means that rdoc is not locked (== there is no rdocjob), 
			// or we are forcing an overwrite (== there is a rdocjob). 
		   // let's lock the target and complete the 'job' object

			// note that, since this is a batch job, there is no task. 
			if (rdoc.doc_job == null) {
				rdoc.doc_job = new Job(job_task:task, job_worker:process_signature, //  
				job_doc_type:RembrandtedDoc.job_doc_type_label,
				job_doc_id:rdoc.doc_id, job_doc_edit:DocStatus.LOCKED, job_doc_edit_date:new Date())
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
			return status
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
			return status 	    
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
		    e.printStackTrace()
		    abort()
		    status.failed++
		    return status
		}
		
		if (!rdocText) {
			log.error "Did NOT get a valid REMBRANDTed text. Skipping."
			status.skipped++
         if (rdoc) {
				rdoc.doc_job.removeThisFromDB()
			}
			sdoc.changeProcStatusInDBto(DocStatus.FAILED) // mark it so next time we'll not use it			
			sdoc.sdoc_job.removeThisFromDB() // release him
         current_sdoc = null
			return status
		}
		
		// it may be a long time before we visited the DB, and I don't  trust the autoReconnect 

		
		if (db.getDB() == null) {
		    log.fatal "DB seems down, locked or whatever, I can't connect to it. Exiting."
		    abort()
		}
        
		// new doc with the info	
		if (rdoc) {
			
			RembrandtedDoc newrdoc = new RembrandtedDoc(doc_id:rdoc.doc_id,
			doc_collection:collection, doc_original_id:sdoc.sdoc_original_id,
			doc_lang:sdoc.sdoc_lang, doc_content:rdocText, doc_date_created:sdoc.sdoc_date, 
			doc_job:rdoc.doc_job)

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
		    rdoc = new RembrandtedDoc(doc_original_id:sdoc.sdoc_original_id, 
				doc_collection:collection, doc_lang:sdoc.sdoc_lang,
			   doc_date_created:sdoc.sdoc_date, doc_content:rdocText )
			
		    rdoc.addThisToDB()
		    if (!rdoc.doc_id) 
			throw new IllegalStateException("Document $rdoc was added to DB, but hasn't ID! Check it!")
		    
		    rdoc.associateWithTag(tag)
			
		    status.inserted++

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
		log.info "Done. Status: $status"
		return status
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
	    log.info "Exiting."
	}
   
   static void main(args) {  
       
	    Options o = new Options()
	    o.addOption("from", true, "[DB] - one particular document from DB, [batch] - a number of unseen documents")
	    o.addOption("user", true, "User or user_id")
	    o.addOption("col", true, "target collection. Can be id or name")
	    o.addOption("ndocs", true, "number of docs in batch process")
	    o.addOption("docid", true, "id of docs in DB process")
	    o.addOption("answer", true, "automatic answer for [y]es/[n]o/[a]lways/n[e]ver questions")
	    o.addOption("help", false, "Gives this help information")
	    
	    CommandLineParser parser = new GnuParser()
	    CommandLine cmd = parser.parse(o, args)

		Collection collection
		User user
		
	    if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "java saskia.imports.ImportSourceDocument2RembrandtedDocument", o )
			System.exit(0)
	    }

	    // check important vars
	    if (!cmd.hasOption("from")) {
			println "No --from arg. Please specify the type of process (DB or batch). Exiting."
			System.exit(0)
	    }
	    
	    if (!cmd.hasOption("user")) {
			println "No --user arg. Please specify the user. Exiting."
			System.exit(0)
	    }

	    if (!cmd.hasOption("col")) {
			println "No --col arg. Please specify the target collection (id or name). Exiting."
			System.exit(0)
	    }
	
	 // check collection
		try {
			collection = Collection.getFromID(Long.parseLong(cmd.getOptionValue("col")))		
		} catch(Exception e) {
			collection = Collection.getFromName(cmd.getOptionValue("col"))
		}
		if (!collection) {
			log.error "Don't know collection "+cmd.getOptionValue("col")+" to parse documents on. Exiting."
			System.exit(0)
		}

	 // check user
		try {
			user = User.getFromID(Long.parseLong(cmd.getOptionValue("user")))		
		} catch(Exception e) {
			user = User.getFromLogin(cmd.getOptionValue("user"))
		}
		if (!user) {
			log.error "Don't know user "+cmd.getOptionValue("user")+" to parse documents on. Exiting."
			System.exit(0)
		}
		
	    ImportSourceDocument2RembrandtedDocument s2r = new ImportSourceDocument2RembrandtedDocument(
		     collection.col_lang)
	    
	   s2r.cmd = cmd // give options so that we can check the answer arg.
		String process_signature = s2r.toString()
			
	 	HashMap status = [inserted:0, updated:0, skipped:0]
	    
	    // get rembrandtVersion
		String rembrandtversion = Rembrandt.getVersion()
		if (rembrandtversion.indexOf("-") > 0) rembrandtversion =rembrandtversion.substring(0, rembrandtversion.indexOf("-"))
		else rembrandtversion="unknown"
		
		if (cmd.getOptionValue("from")== "DB") {
		   /* List docs = cmd.getOptionValues("docid")
		    if (!docs) {
				println "Set to DB, but no doc_id found. Please set --docid args. Exiting."
				System.exit(0)
		    }
		    docs.each{doc -> 
		    	HashMap status_ = s2r.parseDoc(doc, collection, Rembrandt.version)			 
		    	if (status_) {
		    	    status.imported_ready += status_.imported_ready
		    	    status.imported_notready += status_.imported_notready
		 	 		status.skipped += status_.skipped
		    	}
		    }*/
			println "Not avaliable, sorry."
		} else if (cmd.getOptionValue("from") == "batch") {
		    status = s2r.parseBatch( Integer.parseInt(
			    cmd.getOptionValue("ndocs")), collection, user, process_signature, Rembrandt.version)			
		} else {
			println "Unknown value for --from. Exiting."
			System.exit(0)		    
		}
		log.info "Done. Inserted ${status.inserted} doc(s), updated ${status.updated} doc(s), skipped ${status.skipped} doc(s)."
	}   
}