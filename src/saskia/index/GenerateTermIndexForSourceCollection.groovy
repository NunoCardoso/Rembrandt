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
import rembrandt.io.DocStats
import saskia.db.obj.*
import saskia.db.table.*
import saskia.db.database.*
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag

import saskia.util.validator.*

/**
 * @author Nuno Cardoso
 * 
 * This class generates term index for a given collection. 
 *
 */
class GenerateTermIndexForSourceCollection extends IndexGenerator {

	static Configuration conf = Configuration.newInstance()

	static String termWithoutStemIndexDir = "term-nostem-index"
	static String termWithStemIndexDir = "term-stem-index"
	int doc_pool_size=conf.getInt("saskia.index.term.doc_pool_size",1000)

	Collection collection
	Map<String, Analyzer> analyzerMap
	LgteBrokerStemAnalyzer analyzer
	String indexdir
	LgteIndexWriter termwriter
	File filestats
	Map stats
	RembrandtReader reader

	public GenerateTermIndexForSourceCollection(Collection collection, String indexdir,
	boolean dostem) {
		
		super()
		this.collection=collection
		this.indexdir=indexdir
		reader = new RembrandtReader(
				new RembrandtStyleTag(
				conf.get("rembrandt.input.styletag.lang", conf.get("global.lang"))))

		analyzerMap = [:]
		analyzerMap.put(conf.get("saskia.index.id_label","id"), new LgteNothingAnalyzer())
		analyzerMap.put(conf.get("saskia.index.docid_label","docid"), new LgteNothingAnalyzer())

		if (dostem) {
			if (collection.col_lang == "pt")
				analyzerMap.put(conf.get("saskia.index.contents_label","contents"),
						LgteAnalyzerManager.getInstance().getLanguagePackage(
						"Portuguese", "stopwords_por.txt").getAnalyzerWithStemming() )

			if (collection.col_lang == "en")
				analyzerMap.put(conf.get("saskia.index.contents_label","contents"),
						LgteAnalyzerManager.getInstance().getLanguagePackage(
						"English", "snowball-english.list").getAnalyzerWithStemming() )
		} else {

			if (collection.col_lang == "pt")
				analyzerMap.put(conf.get("saskia.index.contents_label","contents"),
						LgteAnalyzerManager.getInstance().getLanguagePackage(
						"Portuguese", "stopwords_por.txt").getAnalyzerNoStemming() )

			if (collection.col_lang == "en")
				analyzerMap.put(conf.get("saskia.index.contents_label","contents"),
						LgteAnalyzerManager.getInstance().getLanguagePackage(
						"English", "snowball-english.list").getAnalyzerNoStemming() )
		}


		analyzer = new LgteBrokerStemAnalyzer(analyzerMap)
		termwriter = new LgteIndexWriter(indexdir, analyzer, true, Model.OkapiBM25Model)

		log.debug "Opening a new index writer, $termwriter"
		if (dostem) filestats = new File(indexdir, "../${termWithStemIndexDir}-collection-stats.txt")
		else filestats = new File(indexdir, "../${termWithoutStemIndexDir}-collection-stats.txt")
		stats = [:]
	}

	public index() {
		
		SourceDocTable sourceDocTable = collection
			.getDBTable().getSaskiaDB().getDBTable("SourceDocTable") 

		DocStats docstats = new DocStats()
		docstats.begin()

		//log.debug "Writer: $writer"
		if (!filestats.exists()) {
			log.debug "stats file does not exist. Creating one."
			filestats.createNewFile()
			log.info "Pre-analysing collection $collection, please wait."
			stats['total'] = collection.getNumberOfSourceDocuments()
			stats['processed'] = 0

		} else {
			stats = readFile(filestats)
		}
		log.debug "Total number of source docs in the collection: "+stats['total']

		docstats.totalDocs = stats['total']

		for (int i=stats['total']; i > 0; i -= doc_pool_size) {

			int limit = (i > doc_pool_size ? doc_pool_size : i)
			log.debug "Initial batch size: ${stats['total']} Remaining: $i Next pool size: $limit"

			List sdocs = sourceDocTable.getBatchOfSourceDocs(collection, limit, stats["processed"])
			log.debug "Got ${sdocs?.size()} SourceDoc(s)."

			// if it's null, then there's no more docs to process. Leave the loop.
			if (!sdocs) {
				log.info "DB returned no more docs, I guess I'm done."
				return
			}
			docstats.beginBatchOfDocs(limit)
			int doc_title_sentences, doc_body_sentences, doc_title_terms, doc_body_terms
			sdocs.each {sdoc ->

				//TODO
				Document doc = reader.createDocument(sdoc.getContent())

				doc.tokenize()
				doc_title_sentences = 0
				doc_body_sentences = 0
				doc_title_terms = 0
				doc_body_terms = 0
				/*****  String representation of the doc body ******/
				String bodytext = doc.body_sentences?.collect{it.toStringLine()}.join("\n").trim()
				String titletext = doc.title_sentences?.collect{it.toStringLine()}.join("\n").trim()

				doc.title_sentences?.each{it ->
					if (!it.isEmpty()) {
						doc_title_sentences++
						doc_title_terms += it.size()
					}
				}
				doc.body_sentences?.each{it ->
					if (!it.isEmpty()) {
						doc_body_sentences++
						doc_body_terms += it.size()
					}
				}
				log.trace "bodytext: $bodytext"
				if (!bodytext && !titletext) log.warn "Doc ${sdoc.sdoc_original_id} does NOT have titletext and bodytext."
				LgteDocumentWrapper ldoc = new LgteDocumentWrapper()

				ldoc.storeUtokenized(conf.get("saskia.index.id_label","id"), sdoc.sdoc_original_id)
				ldoc.storeUtokenized(conf.get("saskia.index.docid_label","docid"), sdoc.sdoc_id.toString())

				if (doc_body_sentences > 0 && doc_body_terms > 0) {
					if (doc_title_sentences > 0 && doc_title_terms > 0) {
						ldoc.indexText(conf.get("saskia.index.title_label","title"),titletext)
						ldoc.indexText(conf.get("saskia.index.contents_label","contents"),titletext+"\n"+bodytext)
					} else {
						ldoc.indexText(conf.get("saskia.index.contents_label","contents"),bodytext)
					}
				} else {
					if (doc_title_sentences > 0 && doc_title_terms > 0) {
						ldoc.indexText(conf.get("saskia.index.title_label","title"),titletext)
						ldoc.indexText(conf.get("saskia.index.contents_label","contents"),titletext)
					} else {
						log.warn "Doc ${sdoc.sdoc_original_id} does NOT have titletext and bodytext."
					}
				}

				log.debug "Doc "+sdoc.sdoc_id.toString()+":"+sdoc.sdoc_original_id+" - Title:"+doc_title_sentences+":"+doc_title_terms+" Body:"+doc_body_sentences+":"+doc_body_terms+" titletext:"+titletext.size()+" bodytext:"+bodytext.size()
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


	static void main(args) {
		
		Configuration conf = Configuration.newInstance()
		Options o = new Options()
		String fileseparator = System.getProperty("file.separator")

		o.addOption("db", true, "database (main/test)")
		o.addOption("col", true, "Collection name or ID")
		o.addOption("stem", true, "stem while indexing")
		o.addOption("help", false, "Gives this help information")
		o.addOption("indexdir", false, "directory of the index")

		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java saskia.imports.GenerateTermIndexForSourceCollection", o )
			System.exit(0)
		}

	 	String DEFAULT_COLLECTION_NAME = "CD do Segundo HAREM"
	 	String DEFAULT_DB_NAME = "main"
	 	String DEFAULT_STEMMING = true

		SaskiaDB db = new DBValidator()
			.validate(cmd.getOptionValue("db"), DEFAULT_DB_NAME)
	
		Collection collection = new CollectionValidator(db)
			.validate(cmd.getOptionValue("col"), DEFAULT_COLLECTION_NAME)

		boolean stem = new StemValidator()
			.validate(cmd.getOptionValue("stem"), DEFAULT_STEMMING, false)

		String index_final_dir
		if (stem) index_final_dir = termWithStemIndexDir
		else index_final_dir = termWithoutStemIndexDir

		String default_indexdir = IndexDirectoryValidator.buildIndexDirectory(
			 conf, collection, index_final_dir)

		String indexdir = new IndexDirectoryValidator().validate(
			 cmd.getOptionValue("indexdir"), default_indexdir)
	
		File f = new File(indexdir)
		if (!f.exists()) f.mkdirs()

		GenerateTermIndexForSourceCollection indexer = new GenerateTermIndexForSourceCollection(collection, indexdir, stem)

		indexer.index()
		println indexer.statusMessage()
	}
}
