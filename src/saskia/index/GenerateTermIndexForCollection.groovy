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
import rembrandt.obj.Document
import saskia.bin.Configuration
import saskia.io.Collection
import rembrandt.io.DocStats
import saskia.io.RembrandtedDoc
import saskia.io.SaskiaDB
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag

/**
 * @author Nuno Cardoso
 * 
 * This class generates term index for a given collection. 

 *
 */
class GenerateTermIndexForCollection {
    
    static Configuration conf = Configuration.newInstance()
    static Logger log = Logger.getLogger("IndexGeneration")
    static String termWithoutStemIndexDir = "term-nostem-index" 
    static String termWithStemIndexDir = "term-stem-index" 
    static String collectionLabel = "col" 
    static final int DOC_POOL_SIZE=1000

    Collection collection
    Map<String, Analyzer> analyzerMap 
    LgteBrokerStemAnalyzer analyzer
    String indexdir
    LgteIndexWriter termwriter 
    File filestats 
    Map stats
    String lang
    RembrandtReader reader
    
    public GenerateTermIndexForCollection(Collection collection, String lang, String indexdir,
	    boolean dostem) {
			this.collection=collection
        this.lang=lang
        this.indexdir=indexdir
        reader = new RembrandtReader(
        	new RembrandtStyleTag(
                conf.get("rembrandt.input.styletag.lang", conf.get("global.lang"))))
       
        analyzerMap = [:]
        analyzerMap.put(Globals.DOCUMENT_ID_FIELD, new LgteNothingAnalyzer())
		
		if (dostem) {
        if (lang == "pt") 
            analyzerMap.put(Globals.LUCENE_DEFAULT_FIELD,LgteAnalyzerManager.getInstance().getLanguagePackage(
                    "Portuguese", "stopwords_por.txt").getAnalyzerWithStemming() )

        if (lang == "en")
            analyzerMap.put(Globals.LUCENE_DEFAULT_FIELD, LgteAnalyzerManager.getInstance().getLanguagePackage(
                    "English", "snowball-english.list").getAnalyzerWithStemming() )
		} else {

        if (lang == "pt") 
            analyzerMap.put(Globals.LUCENE_DEFAULT_FIELD,LgteAnalyzerManager.getInstance().getLanguagePackage(
                    "Portuguese", "stopwords_por.txt").getAnalyzerNoStemming() )

        if (lang == "en")
            analyzerMap.put(Globals.LUCENE_DEFAULT_FIELD, LgteAnalyzerManager.getInstance().getLanguagePackage(
                    "English", "snowball-english.list").getAnalyzerNoStemming() )
		}
        

        analyzer = new LgteBrokerStemAnalyzer(analyzerMap)
        termwriter = new LgteIndexWriter(indexdir, analyzer, true, Model.OkapiBM25Model)
	
        log.debug "Opening a new index writer, $termwriter"
        if (dostem) filestats = new File(indexdir, "../${termWithStemIndexDir}-collection-stats.txt")
        else filestats = new File(indexdir, "../${termWithoutStemIndexDir}-collection-stats.txt")
        stats = [:]      
    }
    
    public doit() {
        DocStats docstats = new DocStats()
        docstats.begin()
        
        //log.debug "Writer: $writer"     
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
        
        for (int i=stats['total']; i > 0; i -= DOC_POOL_SIZE) {
            
            int limit = (i > DOC_POOL_SIZE ? DOC_POOL_SIZE : i)
            log.debug "Initial batch size: ${stats['total']} Remaining: $i Next pool size: $limit"
            
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
                
                /*****  String representation of the doc body ******/
                String bodytext = doc.body_sentences.collect{it.toStringLine()}.join("\n")
                String titletext = doc.title_sentences?.collect{it.toStringLine()}.join("\n")

                log.trace "bodytext: $bodytext"
                if (!bodytext) log.warn "Doc $doc does NOT have bodytext."
                LgteDocumentWrapper ldoc = new LgteDocumentWrapper()
                
                ldoc.storeUtokenized(Globals.DOCUMENT_ID_FIELD,rdoc.doc_original_id)
                ldoc.indexText("title",titletext)
                ldoc.indexText(Globals.LUCENE_DEFAULT_FIELD,bodytext)
             
                termwriter.addDocument(ldoc)
            }
            docstats.endBatchOfDocs(limit)	
            stats['processed'] += limit
            docstats.printMemUsage()	
            
            
        }
        log.debug "Optimizing index..."
        termwriter.optimize()
        termwriter.close()
        log.debug "Done. Writing to file."   
        syncToFile(filestats, stats)
        
        log.debug "Opening and closing, to create BM25 counts..."
        LgteIndexWriter writer2 = new LgteIndexWriter(indexdir, analyzer, false, Model.OkapiBM25Model)
        writer2.close()
        log.debug "done."
        //filestats.close()
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
        o.addOption("stem", true, "stem while indexing")
        o.addOption("lang", true, "Collection language")
        o.addOption("help", false, "Gives this help information")
        o.addOption("indexdir", false, "directory of the index")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter()
            formatter.printHelp( "java saskia.imports.GenerateTermIndexForCollection", o )
            System.exit(0)
        }
        
        if (!cmd.hasOption("col")) {
            println "No --col arg. Please specify the collection. Exiting."
            System.exit(0)
        }

       if (!cmd.hasOption("stem")) {
            println "No --stem arg. Please specify the stemming. Exiting."
            System.exit(0)
        }
        
        if (!cmd.hasOption("lang")) {
            println "No --lang arg. Please specify the collection language. Exiting."
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
        
		  boolean stem
		  def stem_answer = cmd.getOptionValue("stem") 
		  if (stem_answer.equalsIgnoreCase("true") || stem_answer.equalsIgnoreCase("1"))
		 	 stem = true 
			else 
			stem = false

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
				if (stem) indexdir += fileseparator+termWithStemIndexDir 
				else indexdir += fileseparator+termWithoutStemIndexDir 

            log.info "No --indexdir arg. Using directory $indexdir. "
        } else {
            indexdir = cmd.hasOption("indexdir")
            log.info "Using directory $indexdir. "
        }
        
        GenerateTermIndexForCollection indexer = new GenerateTermIndexForCollection(
               collection, cmd.getOptionValue("lang"), indexdir, stem)
        
        indexer.doit()
        
        //log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
    }
}
