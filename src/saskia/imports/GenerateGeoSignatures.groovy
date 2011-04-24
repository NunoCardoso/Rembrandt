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

import saskia.io.Tag
import rembrandt.io.DocStats
import saskia.bin.Configuration
import saskia.db.GeoSignatureFactory;
import saskia.db.obj.Collection;
import saskia.db.table.DocGeoSignatureTable;
import saskia.io.RembrandtedDoc
import org.apache.log4j.Logger
import org.apache.commons.cli.*

/**
 * @author Nuno Cardoso
 * Script to generate geographic signatures for the documents of a given collection
 */
class GenerateGeoSignatures {

    Tag tag
    Collection collection
    static Logger log = Logger.getLogger("GeoSignature")
    int ndocs 
    GeoSignatureFactory gsf
    Configuration conf = Configuration.newInstance()
	 int rembrandted_doc_pool_size = conf.getInt("saskia.imports.rembrandted_doc_pool_size",30)
    String sync

    public GenerateGeoSignatures(Collection collection, String sync, int ndocs) {
        
        this.collection = collection
        this.ndocs = ndocs  
		this.sync = sync
        gsf = new GeoSignatureFactory() 
        tag = Tag.getFromVersion(GeoSignatureFactory.geoSignatureVersionLabel)
        
        if (!tag) {
            tag = new Tag(tag_version:GeoSignatureFactory.geoSignatureVersionLabel, tag_comment:null)
            tag.tag_id = tag.addThisToDB()
        }  
    }

    public Map generate() {
        
        Map status = [generated:0, skipped:0]
        
        def stats = new DocStats(ndocs)
        stats.begin()
        
        for (int i=ndocs; i > 0; i -= rembrandted_doc_pool_size) {
             
            int limit = (i > rembrandted_doc_pool_size ? rembrandted_doc_pool_size : i)
            log.debug "Initial batch size: $ndocs Remaining: $i Next pool size: $limit"
            
            // NOT THREAD-SAFE!

            Map docs 
			if (sync == "pool") {
				docs = RembrandtedDoc.getBatchDocsAndNEsFromPoolToGenerateGeoSignatures(collection, limit)
			} else if (sync == "rdoc") {
				docs = RembrandtedDoc.getBatchDocsAndNEsFromRDOCToGenerateGeoSignatures(collection, limit)
			}
			
            log.debug "Got ${docs?.size()} RembrandtedDoc(s)."
                    
            // if it's null, then there's no more docs to process. Leave the loop.
            if (!docs) return status
                    
            docs.each {doc_id, stuff ->
                
                stats.beginDoc(doc_id)
                
                log.debug "Generating geo-signature for doc $doc_id..."
                HashMap status_
                
                try {
                    DocGeoSignatureTable dgs = new DocGeoSignatureTable()
                    // the String is a XML
                    dgs.dgs_document = doc_id
                    dgs.dgs_signature = gsf.generate(doc_id, stuff)
                    dgs.dgs_tag = tag
                    long dgs_new_id = dgs.addThisToDB()
                    RembrandtedDoc.addGeoSignatureIDtoDocID(dgs_new_id, doc_id)
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
        o.addOption("col", true, "target collection. Can be id or name")
        o.addOption("sync", true, "Get NEs from RDOC or from Pool")
        o.addOption("ndocs", true, "number of docs in batch process")    
        o.addOption("help", false, "Gives this help information")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java saskia.imports.GenerateGeoSignatures", o )
            System.exit(0)
        }
        
        if (!cmd.hasOption("col")) {
            println "No --col arg. Please specify the target collection (id or name). Exiting."
            System.exit(0)
        }
        
        if (!cmd.hasOption("ndocs")) {
            println "No --ndocs arg. Please specify the number of docs to process. Exiting."
            System.exit(0)
        }
        
 		String sync = cmd.getOptionValue("sync")
        if (!(["rdoc", "pool"].contains(sync))) {
            log.error "Don't know sync ${cmd.getOptionValue('sync')}. Exiting."
            System.exit(0)    
        }

        Collection collection 
        try {
            collection = Collection.getFromID(Long.parseLong(cmd.getOptionValue("col")))		
        } catch(Exception e) {
            collection = Collection.getFromName(cmd.getOptionValue("col"))
        }
        if (!collection) {
            log.error "Don't know collection ${cmd.getOptionValue('col')} to parse documents on. Exiting."
            System.exit(0) 
        } 
        
        GenerateGeoSignatures ggs = new GenerateGeoSignatures(
        collection, sync, Integer.parseInt(cmd.getOptionValue("ndocs")))
        
        Map status = ggs.generate()
        
        log.info "Done. Generated ${status.generated} geosignatures, skipped ${status.skipped} geosignatures."
    }   
    
}