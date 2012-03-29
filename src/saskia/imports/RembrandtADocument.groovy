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
import rembrandt.io.*
import rembrandt.obj.Document

import saskia.util.validator.*

/** 
 * Tags documents with REMBRANDT
 */
class RembrandtADocument extends Import {


	// readers
	HTMLReader readerHTML
	RembrandtReader readerRembrandt
	RembrandtWriter writer
	RembrandtStyleTag styletag
        
	String lang
	String taglang

	Doc current_doc
	
	RembrandtCore core 
	String rembrandtversion
	String process_signature	     
	
	/** don't handle big docs */
	Long max_allowed_html_size
	
	Integer doc_pool_size
	
	String mode
	def docs
	
	String defaultanswer
	String answer
	BufferedReader input

	TagTable tagTable
	TaskTable taskTable
	JobTable jobTable
	DocTable docTable

	public RembrandtADocument() {
		super()
		max_allowed_html_size = conf.getLong("saskia.imports.max_allowed_html_size",100000)
		doc_pool_size = conf.getInt("saskia.imports.doc_pool_size",10)
	}
	
	public prepareRembrandt() {
	
		taglang = conf.get("rembrandt.output.styletag.lang", this.lang)
		styletag = new RembrandtStyleTag(taglang)
	    readerHTML = new HTMLReader(new HTMLStyleTag(taglang))
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
		docTable = db.getDBTable("DocTable")
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
				
		for(int i=docs; i > 0; i -= doc_pool_size) {
		    
			int limit = (i > doc_pool_size ? doc_pool_size : i)
			log.debug "Initial batchSize: $docs Remaining: $i Next pool size: $limit"
			List<Doc> docs = docTable.getNextProcessableAndUnlockedDoc(
				task, process_signature, collection, limit)
		   log.debug "Got ${docs?.size()} Doc(s)."
		    
			if (!docs) {
			  log.info "No more docs to process, exiting early."	
			  break
			}
			 	
			docs?.each {doc ->
			 	stats.beginDoc(doc.doc_id)
				importCurrentDoc(doc, collection, task, process_signature, tag)
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
		Doc doc, Collection collection, Task task, String process_signature, Tag tag) {
	
	// first: fetch document from table page
		if (!doc || !tag) {
			log.warn "No Doc to import!"
			return null
		}
		
		String htmlText = null
		String docText = null
		String pageTitle = ""
	
		log.trace "Going to annotate Doc $doc"
		log.trace "Tag used: ${tag}"
		
		doc.doc_job.changeEditStatusInDBto(DocStatus.LOCKED)
		current_doc = doc
		
		if (doc.doc_content) log.debug "HTML content of Doc ${doc} has ${doc.doc_content.size()} bytes." 

		if (doc.doc_content.size() > max_allowed_html_size) {
			log.warn "HTML content of Doc ${doc} is too big (${doc.doc_content.size()} bytes), "+
			"over max allowed size ${max_allowed_html_size}. Skipping." 
			status.skipped++
			doc.changeProcStatusInDBto(DocStatus.TOO_BIG) // mark it so next time we'll not use it
			doc.doc_job.removeThisFromDB() // release him
			current_doc = null
			return  	    
		}
		
		Document d 
	
		if (doc.doc_content.startsWith("<HTML") || doc.doc_content.startsWith("<html"))
		 d = readerHTML.createDocument(doc.doc_content)
		else if (doc.doc_content.startsWith("<DOC")) 
		 d = readerRembrandt.createDocument(doc.doc_content)
		
		log.trace "Document's number of sentences in title: ${d?.title_sentences?.size()}"		
		log.trace "Document's number of sentences in body: ${d?.body_sentences?.size()}"		
		d.docid = doc.doc_id
		d.lang = doc.doc_lang
		d.taglang = taglang
        
		try {
		    d = core.releaseRembrandtOnDocument(d)
		    docText = writer.printDocument(d)
		} catch(Exception e) {
		    log.error "REMBRADNTing document failed: "+e.getMessage()
		    abort()
		    status.failed++
		    return 
		}

		if (!docText) {
			log.error "Did NOT get a valid REMBRANDTed text. Skipping."
			status.skipped++
			doc.changeProcStatusInDBto(DocStatus.FAILED) // mark it so next time we'll not use it			
			doc?.doc_job?.removeThisFromDB() // release him
			current_doc = null
			return 
		}
		
		// it may be a long time before we visited the DB, and I don't trust the autoReconnect 

		if (db.getDB() == null) {
		    log.fatal "DB seems down, locked or whatever, I can't connect to it. Exiting."
		    abort()
		}
        
		doc.removeDocHasNEs()
		
		doc.addNEsToSaskia(doc.titleNEs, "T", doc.doc_lang)
		doc.addNEsToSaskia(doc.bodyNEs, "B", doc.doc_lang)
		doc.associateWithTag(tag)
		status.imported++

		// Important! Having a non-null date says that this is tagged.
		// version is also 0. Version > 0 means taged.
		doc.markedAsTaggedNow()
		
		doc.changeProcStatusInDBto(DocStatus.READY) 
		doc.doc_job.removeThisFromDB()
		current_doc = null
		return
	}

   public abort() {
	    log.warn "\nAborting... backtracking"
	    if (current_doc) {
			current_doc.doc_job?.removeThisFromDB()
			log.warn "Doc job removed."
			current_doc.changeProcStatusInDBto(DocStatus.NOT_READY)
			log.warn "Doc proc status changed to NOT_READY."            
       }  
	}
   
   static void main(args) {  

		Options o = new Options()
		Configuration conf = Configuration.newInstance()
		
		String DEFAULT_DB_NAME = "main"
		String DEFAULT_COLLECTION_NAME = "default_collection"
		String DEFAULT_MODE = "multiple"
		String DEFAULT_DOCS = "100"
		
		o.addOption("db", true, "target Saskia DB (main/test)")
		o.addOption("col", true, "target collection name/id of the DB")
		o.addOption("mode", true, "mode: (single/multiple)")
		o.addOption("docs", true, "number of docs (multiple mode), or docid (single mode)")
		o.addOption("answer", true, "automatic answer for [y]es/[n]o/[a]lways/n[e]ver questions")
		o.addOption("help", false, "Gives this help information")

		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

		RembrandtADocument importer = new RembrandtADocument()

		log.info "*******************************"
		log.info "* This class tags a document. *"
		log.info "*******************************"

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

		def answer = new InteractiveAnswerValidator()
			.validate(cmd.getOptionValue("answer"), null, false)		
		importer.setDefaultanswer(answer)

		importer.prepareRembrandt()
		importer.importer()
		println importer.statusMessage()
	}
}