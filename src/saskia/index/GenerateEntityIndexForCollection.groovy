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
package saskia.index

import org.apache.log4j.Logger

import saskia.dbpedia.DBpediaResource
import pt.utl.ist.lucene.LgteDocumentWrapper
import pt.utl.ist.lucene.utils.LgteAnalyzerManager
import pt.utl.ist.lucene.LgteIndexWriter
import pt.utl.ist.lucene.Globals
import pt.utl.ist.lucene.Model
import pt.utl.ist.lucene.analyzer.LgteAnalyzer
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer 
import org.apache.lucene.analysis.Analyzer 

import org.apache.commons.cli.*
import rembrandt.obj.Document
import saskia.bin.Configuration
import rembrandt.io.DocStats
import rembrandt.obj.SemanticClassification
import saskia.db.database.SaskiaMainDB;
import saskia.db.obj.Collection;
import saskia.db.obj.RembrandtedDoc;
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag

/**
 * @author Nuno Cardoso
 * 
 * This class generates NE index for a given collection. 
 *
 */
class GenerateEntityIndexForCollection {
    
    static Configuration conf = Configuration.newInstance()
    static Logger log = Logger.getLogger("IndexGeneration")
    static String EntityIndexDirLabel = "entity-index" 
    static String collectionLabel = "col" 
    int doc_pool_size=conf.getInt("saskia.index.entity.doc_pool_size",1000)
	 String entity_label = conf.get("saskia.index.entity_label","entity")
    static LgteIndexWriter entwriter // Hash of indexes

   // static List<SemanticClassification> allowedClasses = [ ]
    
    Collection collection
    Map<String, Analyzer> analyzerMap 
    LgteBrokerStemAnalyzer analyzer
    String indexdir
    String sync
    File filestats 
    Map stats
    RembrandtReader reader
    String fileseparator = System.getProperty("file.separator")

    // indexdir goes to ${rembrandt.home.dir}/index/col-X/
    public GenerateEntityIndexForCollection(Collection collection, String indexdir, String sync) {
        
		this.collection=collection
        this.indexdir=indexdir
        this.sync = sync
        
        reader = new RembrandtReader(
        	new RembrandtStyleTag(
                conf.get("rembrandt.input.styletag.lang", 
                    conf.get("global.lang"))))
	
    analyzerMap = [:]
    analyzerMap.put(conf.get("saskia.index.id_label","id"), new LgteNothingAnalyzer())
    analyzerMap.put(conf.get("saskia.index.docid_label","docid"), new LgteNothingAnalyzer())
    analyzerMap.put(entity_label, new LgteNothingAnalyzer())

    analyzer = new LgteBrokerStemAnalyzer(analyzerMap)
    entwriter = new LgteIndexWriter(indexdir, analyzer, true, Model.OkapiBM25Model)
    
       // entwriter = new LgteIndexWriter(indexdir, new LgteNothingAnalyzer(), true, Model.OkapiBM25Model)
    
	log.debug "Opening a new entity writer, $entwriter"
	filestats = new File(indexdir, "../${EntityIndexDirLabel}-collection-stats.txt")
	stats = [:]      
    }

    public doit() {
        DocStats docstats = new DocStats()
        docstats.begin()
        
        if (!filestats.exists()) {
            log.debug "stats file does not exist. Creating one."   
            filestats.createNewFile()
            log.info "Pre-analysing collection $collection, please wait."          
            stats['total'] = collection.getNumberOfRembrandtedDocuments()
            stats['processed'] = 0
        } else {
            stats = readFile(filestats)
        }
        
        log.debug "Total number of docs in the collection: "+stats['total']
        docstats.totalDocs = stats['total']
        
        /** ITERATOR **/
        for (int i=stats['total']; i > 0; i -= doc_pool_size) {
            
            int limit = (i > doc_pool_size ? doc_pool_size : i)
            log.debug "Initial batch size: ${stats['total']} Remaining: $i Next pool size: $limit"
            
            /** IF SYNC = RDOC **/
            if (sync == "rdoc") {
        	List rdocs = RembrandtedDoc.getBatchOfRembrandtedDocs(collection, limit, stats["processed"])
        	log.debug "Got ${rdocs?.size()} RembrandtedDoc(s)."
            
        	// if it's null, then there's no more docs to process. Leave the loop.
        	if (!rdocs) {
        	    log.info "DB returned no more docs, I guess I'm done."	
        	    return 
        	}
        	docstats.beginBatchOfDocs(limit)
                            
                rdocs.each {rdoc ->
                    //TODO
                    Document doc = reader.createDocument(rdoc.doc_content)
                    doc.tokenize()
                   
                    LgteDocumentWrapper ldoc = new LgteDocumentWrapper()
                		ldoc.storeUtokenized(conf.get("saskia.index.id_label","id"), rdoc.doc_original_id)
                		ldoc.storeUtokenized(conf.get("saskia.index.docid_label","docid"), rdoc.doc_id.toString())
                    
                    /*****  NEs in body ******/
                    
						  doc.titleNEs?.each{ne -> 
                        ne.dbpediaPage?.values().toList().flatten()?.each{it -> 
									if (it != null) {
										// indexString indexes but does not tokenize it
										ldoc.indexString(entity_label, DBpediaResource.getShortName(it)) 

                           	if (!stats.containsKey(entity_label)) stats[entity_label] = 0
                           	stats[entity_label]++
									}
                        }
                    }
                    doc.bodyNEs?.each{ne -> 
                        ne.dbpediaPage?.values().toList().flatten()?.each{it -> 
									if (it != null) {
										// indexString indexes but does not tokenize it
										ldoc.indexString(entity_label, DBpediaResource.getShortName(it)) 

                           	if (!stats.containsKey(entity_label)) stats[entity_label] = 0
                           	stats[entity_label]++
									}
                        }
                    }
                    if (doc.bodyNEs.size() == 0) {
                		log.warn "Doc ${rdoc.doc_original_id} has NO NEs on its body. Inserting an empty NE"
                    }
                    entwriter.addDocument(ldoc)                                   
                }
                
            /** SYNC = POOL **/
                
            } else if (sync == "pool") {
        	Map rdocs = RembrandtedDoc.getBatchDocsAndNEsToGenerateNEIndex(collection, limit, stats["processed"])
 			/*rdocs[doc_id] = ['lang':XXX, 'doc_original_id':XXX, nes:[]]
   			docs[doc_id].nes << [section:XXX, sentence:XXX, term:XXX, name:XXXX, neid:XXX, 
        	category:XXX, type:XXX, subtype:XXX, entity:XXXX] */
        	log.debug "Got ${rdocs?.size()} RembrandtedDoc(s)."
            
                // if it's null, then there's no more docs to process. Leave the loop.
                if (!rdocs) {
                    log.info "DB returned no more docs, I guess I'm done."	
                    return 
                }
                docstats.beginBatchOfDocs(limit)
                
                rdocs.each {rdoc_id, rdoc ->
                    LgteDocumentWrapper ldoc = new LgteDocumentWrapper()
                		ldoc.storeUtokenized(conf.get("saskia.index.id_label","id"), rdoc.doc_original_id)
                		ldoc.storeUtokenized(conf.get("saskia.index.docid_label","docid"), rdoc.doc_id)
                    log.trace "Wrote doc id ${rdoc.doc_original_id}."

                    /*****  NEs in body ******/
                    rdoc.nes.each{ne -> 
                        if (ne.entity && ne.entity.ent_dbpedia_resource) {
                           ldoc.indexString(entity_label, 
										ne.entity.ent_dbpedia_resource.replaceAll(/-/,"\\-")) // indexString indexes but does not tokenize it
                           if (!stats.containsKey(entity_label)) stats[entity_label] = 0
                           stats[entity_label]++  
						}                    
                    }
                    entwriter.addDocument(ldoc)  
                }	
            }
            
            docstats.endBatchOfDocs(limit)	
            stats['processed'] += limit
            docstats.printMemUsage()	
        }
        log.debug "Optimizing indexes..."
        entwriter.optimize()
        entwriter.close()
        
       
        log.debug "Done. Writing to file."   
        syncToFile(filestats, stats)
        
        log.debug "Opening and closing, to create BM25 counts..."
        LgteIndexWriter writer2 = new LgteIndexWriter(indexdir, analyzer, false, Model.OkapiBM25Model)
        writer2.close()
        log.debug "done."
       // filestats.close()
        docstats.end()
        //  return status
    }
    
    void syncToFile(File filestats, Map stats) {
        stats.each{k,v -> filestats << "$k:$v\n"}
    }
      
    Map readFile(File filestats) {
        Map stats = [:]
        filestats.eachLine{l -> 
            List items = l.trim().split(":")
            if (items[1].matches(/\d+/)) stats[items[0]] = Integer.parseInt(items[1])
            else stats[items[0]] =  items[1]
        }
        return stats
    }
    
    static void main(args) {
        
        Options o = new Options()
        String fileseparator = System.getProperty("file.separator")
        
        o.addOption("col", true, "Collection name or ID")
        o.addOption("sync", true, "Where will we get the NEs - rdoc or pool. rdoc parses the RembrandtedDoc to get NEs, pool will get NEs from DB")
        o.addOption("help", false, "Gives this help information")
        o.addOption("indexdir", false, "directory of the index")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter()
            formatter.printHelp( "java saskia.index.GenerateEntityIndexForCollection", o )
              System.exit(0)
        }
        
        if (!cmd.hasOption("col")) {
            println "No --col arg. Please specify the collection. Exiting."
            System.exit(0)
        }
        
     
        if (!cmd.hasOption("sync")) {
            println "No --sync arg. Please specify the sync mode [rdoc or pool]. Exiting."
            System.exit(0)
        }
        
        /*** VALIDADE COLLECTION ***/
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
        log.info "Got collection $collection"
        
        /*** VALIDADE INDEX DIR ***/
        String indexdir 
        if (!cmd.hasOption("indexdir")) {
            indexdir = conf.get("rembrandt.home.dir")
            if (!indexdir.endsWith(fileseparator)) indexdir += fileseparator
            indexdir += Configuration.newInstance().get("saskia.index.dir","index")       
            
            File f = new File(indexdir)
            if (!f.exists()) f.mkdir()        
            indexdir += fileseparator+collectionLabel+"-"+collection.col_id
            
            f = new File(indexdir)
            if (!f.exists()) f.mkdir()        
            indexdir += fileseparator+EntityIndexDirLabel

            log.info "No --indexdir arg. Using directory $indexdir. "
        } else {
            indexdir = cmd.hasOption("indexdir")
            log.info "Using directory $indexdir. "
        }
        
        /*** VALIDATE SYNC ***/
        
        String sync = cmd.getOptionValue("sync")
        if (!(["rdoc", "pool"].contains(sync))) {
            log.error "Don't know sync ${cmd.getOptionValue('sync')}. Exiting."
            System.exit(0)    
        }
        GenerateEntityIndexForCollection indexer = new GenerateEntityIndexForCollection(
               collection, indexdir, sync)
        
        indexer.doit()
        
        //log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
    }
}
