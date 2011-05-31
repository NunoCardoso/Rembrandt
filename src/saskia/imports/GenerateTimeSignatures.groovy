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
import saskia.bin.Configuration
import saskia.db.TimeSignatureFactory;
import saskia.db.obj.*;
import saskia.db.table.*
import saskia.db.database.*
import saskia.util.validator.*

import org.apache.log4j.Logger
import org.apache.commons.cli.*

/**
 * @author Nuno Cardoso
 * Script to generate geographic signatures for the documents of a given collection
 */
class GenerateTimeSignatures {

    static Logger log = Logger.getLogger("GeoSignature")
    Tag tag
    Collection collection
    int docs 
	 Map status 
	 SaskiaDB db
	
    TimeSignatureFactory tsf
    Configuration conf = Configuration.newInstance()
	 int rembrandted_doc_pool_size = conf.getInt("saskia.imports.rembrandted_doc_pool_size",30)
    String sync

    public GenerateTimeSignatures() {
        status = [generated:0, skipped:0, failed:0] 
    }

	 public prepare() {
		 db = collection.getDBTable().getSaskiaDB()
		 tag = db.getDBTable("TagTable")
			.getFromVersion(TimeSignatureFactory.timeSignatureVersionLabel)
        
        if (!tag) {
            tag = Tag.createNew(db.getDBTable("TagTable"),
				[tag_version:TimeSignatureFactory.timeSignatureVersionLabel, 
				tag_comment:null])
				
            tag.tag_id = tag.addThisToDB()
        }  
        tsf = new TimeSignatureFactory()//new Date) 
	 }

    public Map generate() {
        
        def stats = new DocStats(docs)
        stats.begin()
        
		  RembrandtedDocTable rembrandtedDocTable = db.getDBTable("RembrandtedDocTable")
        for (int i=docs; i > 0; i -= rembrandted_doc_pool_size) {
             
            int limit = (i > rembrandted_doc_pool_size ? rembrandted_doc_pool_size : i)
            log.debug "Initial batch size: $docs Remaining: $i Next pool size: $limit"
            
            // NOT THREAD-SAFE!
            Map docs 
			if (sync == "pool") {
				docs = rembrandtedDocTable.getBatchDocsAndNEsFromPoolToGenerateTimeSignatures(
					collection, limit)
			} else if (sync == "rdoc") {
				docs = rembrandtedDocTable.getBatchDocsAndNEsFromRDOCToGenerateTimeSignatures(
					collection, limit)
			}
			log.debug "Got ${docs?.size()} RembrandtedDoc(s)."
                    
            // if it's null, then there's no more docs to process. Leave the loop.
            if (!docs) return status
                    
            docs.each {doc_id, stuff ->
               
                stats.beginDoc(doc_id)
                
                log.debug "Generating time-signature for doc $doc_id..."
                HashMap status_
                
                try {
                    DocTimeSignature dts = DocTimeSignature.createNew(
							db.getDBTable("DocTimeSignatureTable"), 
							[ dts_document:doc_id,
                    	  dts_signature:tsf.generate(doc_id, stuff),
                    	  dts_tag:tag
							])
                    long dts_new_id = dts.addThisToDB()
                    rembrandtedDocTable.addTimeSignatureIDtoDocID(dts_new_id, doc_id)
                    status.generated++
                                       
                } catch(Exception e) {
                     e.printStackTrace()
                     status.skipped++
                     abort()
                }
                stats.endDoc()
                stats.printMemUsage()	
            }
            
         } 
        stats.end()
        return status
    }
    
    public abort() {
        log.warn "\nAborting... "
    }
    
    static void main(args) {  
        
        Options o = new Options()
        o.addOption("db", true, "DB (main/test)")
        o.addOption("col", true, "target collection. Can be id or name")
        o.addOption("sync", true, "Get NEs from RDOC or from Pool")
        o.addOption("docs", true, "number of docs in batch process")    
        o.addOption("help", false, "Gives this help information")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java saskia.imports.GenerateGeoSignatures", o )
            System.exit(0)
        }
		
		String DEFAULT_DB_NAME = "main"
		String DEFAULT_COLLECTION_NAME="none"
		String DEFAULT_DOCS = "10000"
		String DEFAULT_SYNC = "pool"
		
		GenerateTimeSignatures gts = new GenerateTimeSignatures()
 
		// --db
		SaskiaDB db = new DBValidator()
			.validate(cmd.getOptionValue("db"), DEFAULT_DB_NAME)
		log.info "DB: $db "

		// --col
		Collection collection = new CollectionValidator(db)
			.validate(cmd.getOptionValue("col"), DEFAULT_COLLECTION_NAME)
			
		gts.setCollection(collection)
		gts.setTag()
		log.info "Collection: $collection "
		
		// --docs		
		Integer docs = new DocsValidator()
			.validate(cmd.getOptionValue("docs"), DEFAULT_DOCS)
		gts.setDocs(docs)
		log.info "Docs: $docs "
		
		// --sync		
		def sync = new SyncValidator()
		.validate(cmd.getOptionValue("sync"), DEFAULT_SYNC)
		gts.setSync(sync)
		log.info "Sync: sync "
		
      gts.prepare()
      gts.generate()
      log.info "Done. Generated ${gts.status.generated} geosignatures, skipped ${gts.status.skipped} geosignatures."

    }   
}