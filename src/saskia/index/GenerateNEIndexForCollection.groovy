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
import rembrandt.obj.SemanticClassification
import saskia.io.RembrandtedDoc
import saskia.db.SaskiaDB
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag

/**
 * @author Nuno Cardoso
 * 
 * This class generates NE index for a given collection. 

 *
 */
class GenerateNEIndexForCollection {
    
    static Configuration conf = Configuration.newInstance()
    static Logger log = Logger.getLogger("IndexGeneration")
    static String NEIndexDirLabel = "ne-index" 
    static String collectionLabel = "col" 
    static String luceneIndexFieldLabel = "ne" 
    int doc_pool_size = conf.getInt("saskia.index.ne.doc_pool_size",1000)
    static LgteIndexWriter newriter // Hash of indexes

   // static List<SemanticClassification> allowedClasses = [ ]
    
    Collection collection
    Map<String, Analyzer> analyzerMap 
    LgteBrokerStemAnalyzer analyzer
    String indexdir
    String sync
    File filestats 
    Map stats
    
    String fileseparator = System.getProperty("file.separator")
    
	static List indexes = [ 
"ne-PESSOA-index", 
"ne-PESSOA-INDIVIDUAL-index", 
"ne-PESSOA-GRUPOIND-index", 
"ne-PESSOA-CARGO-index", 
"ne-PESSOA-GRUPOCARGO-index", 
"ne-PESSOA-MEMBRO-index", 
"ne-PESSOA-GRUPOMEMBRO-index", 
"ne-PESSOA-POVO-index", 
"ne-LOCAL-index", 
"ne-LOCAL-HUMANO-index",  
"ne-LOCAL-HUMANO-RUA-index", 
"ne-LOCAL-HUMANO-PAIS-index",  
"ne-LOCAL-HUMANO-DIVISAO-index", 
"ne-LOCAL-HUMANO-HUMANOREGIAO-index",   
"ne-LOCAL-HUMANO-CONSTRUCAO-index", 
"ne-LOCAL-FISICO-index",  
"ne-LOCAL-FISICO-ILHA-index",  
"ne-LOCAL-FISICO-AGUACURSO-index",  
"ne-LOCAL-FISICO-AGUAMASSA-index",  
"ne-LOCAL-FISICO-RELEVO-index",  
"ne-LOCAL-FISICO-PLANETA-index",  
"ne-LOCAL-FISICO-FISICOREGIAO-index",  
"ne-LOCAL-VIRTUAL-index",  
"ne-LOCAL-VIRTUAL-COMSOCIAL-index",  
"ne-LOCAL-VIRTUAL-SITIO-index",  
"ne-ORGANIZACAO-index",  
"ne-ORGANIZACAO-ADMINISTRACAO-index",  
"ne-ORGANIZACAO-INSTITUICAO-index",  
"ne-ORGANIZACAO-EMPRESA-index",  
"ne-ACONTECIMENTO-index",  
"ne-ACONTECIMENTO-ORGANIZADO-index",  
"ne-ACONTECIMENTO-EVENTO-index",  
"ne-ACONTECIMENTO-EFEMERIDE-index",  
"ne-OBRA-index",  
"ne-OBRA-PLANO-index",  
"ne-OBRA-REPRODUZIDA-index",  
"ne-OBRA-ARTE-index",  
"ne-ABSTRACCAO-index",  
"ne-ABSTRACCAO-NOME-index",  
"ne-ABSTRACCAO-DISCIPLINA-index",  
"ne-ABSTRACCAO-ESTADO-index",  
"ne-ABSTRACCAO-IDEIA-index",  
"ne-COISA-index",  
"ne-COISA-CLASSE-index",  
"ne-COISA-MEMBROCLASSE-index",  
"ne-COISA-OBJECTO-index",  
"ne-COISA-SUBSTANCIA-index",  
"ne-TEMPO-index",  
"ne-TEMPO-TEMPO_CALEND-index",  
"ne-TEMPO-TEMPO_CALEND-DATA-index",  
"ne-TEMPO-TEMPO_CALEND-HORA-index",  
"ne-TEMPO-TEMPO_CALEND-INTERVALO-index",  
"ne-TEMPO-GENERICO-index",  
"ne-TEMPO-DURACAO-index",  
"ne-TEMPO-FREQUENCIA-index",  
"ne-NUMERO-index",  
"ne-NUMERO-CARDINAL-index",  
"ne-NUMERO-ORDINAL-index",   
"ne-NUMERO-TEXTUAL-index",  
"ne-NUMERO-NUMERAL-index",   
"ne-VALOR-index",  
"ne-VALOR-MOEDA-index",  
"ne-VALOR-QUANTIDADE-index",   
"ne-VALOR-CLASSIFICACAO-index",  
"ne-EM-index"
]  

    // indexdir goes to ${rembrandt.home.dir}/index/col-X/
    public GenerateNEIndexForCollection(Collection collection, String indexdir, String sync) {
        
		this.collection=collection
        this.indexdir=indexdir
        this.sync = sync

    	analyzerMap = [:]
   	analyzerMap.put(conf.get("saskia.index.id_label","id"), new LgteNothingAnalyzer())
    	analyzerMap.put(conf.get("saskia.index.docid_label","docid"), new LgteNothingAnalyzer())
     
    	if (collection.col_lang == "pt") {
        	indexes.each{index -> 
				analyzerMap.put(index, LgteAnalyzerManager.getInstance().getLanguagePackage(
                "Portuguese", "stopwords_por.txt").getAnalyzerNoStemming() )
			}
		}	
    	if (collection.col_lang == "en") {
	   		indexes.each{index -> 
        		analyzerMap.put(index, LgteAnalyzerManager.getInstance().getLanguagePackage(
                "English", "snowball-english.list").getAnalyzerNoStemming() )
			}
		}
    
    	analyzer = new LgteBrokerStemAnalyzer(analyzerMap)
    	newriter = new LgteIndexWriter(indexdir, analyzer, true, Model.OkapiBM25Model)
    
       // newriter = new LgteIndexWriter(indexdir, new LgteNothingAnalyzer(), true, Model.OkapiBM25Model)
    
		log.debug "Opening a new NE writer, $newriter"
		filestats = new File(indexdir, "../${NEIndexDirLabel}-collection-stats.txt")
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

			Map rdocs = [:]
            
			/** IF SYNC = RDOC **/
            if (sync == "rdoc") {
				rdocs = RembrandtedDoc.getBatchDocsAndNEsFromRDOCToGenerateNEIndex(collection, limit, stats["processed"])
			} else if (sync == "pool") {
        	    rdocs = RembrandtedDoc.getBatchDocsAndNEsFromPoolToGenerateNEIndex(collection, limit, stats["processed"])
			}
        	
			// if it's null, then there's no more docs to process. Leave the loop.
        	if (!rdocs) {
        	   	log.info "DB returned no more docs, I guess I'm done."	
        	   	return 
        	}
        	docstats.beginBatchOfDocs(limit)
            
            // ADDING STUFF TO INDEX                       
            rdocs.each {rdoc_id, rdoc ->
              	LgteDocumentWrapper ldoc = new LgteDocumentWrapper()
               	ldoc.storeUtokenized(conf.get("saskia.index.id_label","id"), rdoc.doc_original_id)
                	ldoc.storeUtokenized(conf.get("saskia.index.docid_label","docid"), rdoc.doc_id.toString())
                 log.trace "Wrote doc id ${rdoc.doc_original_id}."

                /*****  NEs in body ******/
                rdoc.nes.each{ne -> 
                    String cat = (ne.category == null ? null : ne.category.nec_category)
                    String typ = (ne.type == null ? null : ne.type.net_type)
                    String sub = (ne.subtype == null ? null : ne.subtype.nes_subtype)
                    SemanticClassification cl = new SemanticClassification(cat, typ, sub)
                        
                    String label = generateField(cl)
                    ldoc.indexText(label, ne.name)
                    log.trace "Wrote ${ne.name} for index ${label}"
                        
                    if (!stats.containsKey(label)) stats[label] = 0
                    stats[label]++
                }
                newriter.addDocument(ldoc)  
            }	
            
            docstats.endBatchOfDocs(limit)	
            stats['processed'] += limit
            docstats.printMemUsage()	
        }
        log.debug "Optimizing indexes..."
        newriter.optimize()
        newriter.close()
              
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
    
    static String generateField(SemanticClassification cl) {
		return luceneIndexFieldLabel+"-"+cl.toString().replaceAll("@","")
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
            formatter.printHelp( "java saskia.index.GenerateNEIndexForCollection", o )
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
            indexdir += fileseparator+NEIndexDirLabel

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
        GenerateNEIndexForCollection indexer = new GenerateNEIndexForCollection(
               collection, indexdir, sync)
        
        indexer.doit()
        
        //log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
    }
}
