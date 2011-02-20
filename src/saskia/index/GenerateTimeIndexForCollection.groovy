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
import saskia.bin.Configuration
import saskia.io.Collection
import saskia.io.TimeSignature
import rembrandt.io.DocStats
import saskia.io.DocTimeSignature


/**
 * @author Nuno Cardoso
 * 
 * This class generates NE index for a given collection. 

 *
 */
class GenerateTimeIndexForCollection {
    
    static Configuration conf = Configuration.newInstance()
    static Logger log = Logger.getLogger("IndexGeneration")
    static String timeIndexDirLabel = "time-index" 
    static String collectionLabel = "col" 
    static String time_label = conf.get("saskia.index.time_label","tg") 
    int doc_pool_size = conf.getInt("saskia.index.time.doc_pool_size",10000)
    static LgteIndexWriter timewriter // Hash of indexes

   // static List<SemanticClassification> allowedClasses = [ ]
    
    Collection collection
    Map<String, Analyzer> analyzerMap 
    LgteBrokerStemAnalyzer analyzer
    String indexdir
    File filestats 
    Map stats
    String lang
     String fileseparator = System.getProperty("file.separator")
    
    // indexdir goes to ${rembrandt.home.dir}/index/col-X/
    public GenerateTimeIndexForCollection(Collection collection, String lang, String indexdir) {
        
		this.collection=collection
        this.lang=lang
        this.indexdir=indexdir
        
        analyzerMap = [:]
    	analyzerMap.put(conf.get("saskia.index.id_label","id"), new LgteNothingAnalyzer())
    	analyzerMap.put(conf.get("saskia.index.docid_label","docid"), new LgteNothingAnalyzer())
    	analyzerMap.put(time_label, LgteAnalyzerManager.getInstance().getLanguagePackage(
        "English", "snowball-english.list").getAnalyzerNoStemming())

        analyzer = new LgteBrokerStemAnalyzer(analyzerMap)

        timewriter = new LgteIndexWriter(indexdir, analyzer, true, Model.OkapiBM25Model)
    
	log.debug "Opening a new Time writer, $timewriter"
	filestats = new File(indexdir, "../${timeIndexDirLabel}-collection-stats.txt")
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
        
        for (int i=stats['total']; i > 0; i -= doc_pool_size) {
            
            int limit = (i > doc_pool_size ? doc_pool_size : i)
            log.debug "Initial batch size: ${stats['total']} Remaining: $i Next pool size: $limit"
            
            List ts = DocTimeSignature.getBatchOfTimeSignatures(collection, limit, stats["processed"])
            log.debug "Got ${ts?.size()} DocTimeSignatures(s)."
            
            // if it's null, then there's no more docs to process. Leave the loop.
            if (!ts) {
                log.info "DB returned no more signatures, I guess I'm done."	
                return 
            }
            docstats.beginBatchOfDocs(limit)
                            
            ts.each {t ->
                //log.debug "I'm with geo $geo"
                TimeSignature timesig = new TimeSignature(t)
                
                LgteDocumentWrapper ldoc = new LgteDocumentWrapper()
                ldoc.storeUtokenized(conf.get("saskia.index.id_label","id"), timesig.doc_original_id)
                ldoc.storeUtokenized(conf.get("saskia.index.docid_label","docid"), timesig.doc_id.toString())

                log.trace "ldoc with doc_original_id $timesig.doc_original_id"
                
                /*****  NEs in body ******/
                timesig.timelist.each{time -> 
                    log.trace "Adding "+time.idx.toString()
                    ldoc.indexString(time_label, time.idx.toString())                    
                }
                
                timewriter.addDocument(ldoc)                                   
            }
            
            docstats.endBatchOfDocs(limit)	
            stats['processed'] += limit
            docstats.printMemUsage()	
        }
        log.debug "Optimizing indexes..."
        timewriter.optimize()
        timewriter.close()
        
       
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
        o.addOption("help", false, "Gives this help information")
        o.addOption("indexdir", false, "directory of the index")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter()
            formatter.printHelp( "java saskia.index.GenerateTimeIndexForCollection", o )
             System.exit(0)
        }
        
        if (!cmd.hasOption("col")) {
            println "No --col arg. Please specify the collection. Exiting."
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
        log.info "Got collection $collection"
        String indexdir 
        if (!cmd.hasOption("indexdir")) {
            indexdir = conf.get("rembrandt.home.dir", "." )
            if (!indexdir.endsWith(fileseparator)) indexdir += fileseparator
            indexdir += conf.get("saskia.index.dir","index")       
            
            File f = new File(indexdir)
            if (!f.exists()) f.mkdir()        
            indexdir += fileseparator+collectionLabel+"-"+collection.col_id
            
            f = new File(indexdir)
            if (!f.exists()) f.mkdir()        
            indexdir += fileseparator+timeIndexDirLabel

            log.info "No --indexdir arg. Using directory $indexdir. "
        } else {
            indexdir = cmd.hasOption("indexdir")
            log.info "Using directory $indexdir. "
        }
        
        GenerateTimeIndexForCollection indexer = new GenerateTimeIndexForCollection(
               collection, cmd.getOptionValue("lang"), indexdir)
        
        indexer.doit()
        
        //log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
    }
}
