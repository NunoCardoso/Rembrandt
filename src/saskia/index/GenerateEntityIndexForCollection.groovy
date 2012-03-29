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
import saskia.db.obj.*
import saskia.db.table.*
import saskia.db.database.*
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag

import saskia.util.validator.*

/**
 * @author Nuno Cardoso
 * 
 * This class generates NE index for a given collection. 
 *
 */
class GenerateEntityIndexForCollection extends IndexGenerator {

	static Configuration conf = Configuration.newInstance()
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
	File filestats
	Map stats
	RembrandtReader reader
	String fileseparator = System.getProperty("file.separator")

	// indexdir goes to ${rembrandt.home.dir}/index/db-X/col-X/
	public GenerateEntityIndexForCollection(Collection collection, String indexdir) {
		super()
		this.collection=collection
		this.indexdir=indexdir

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

	public index() {
		DocTable docTable = collection
			.getDBTable().getSaskiaDB().getDBTable("DocTable") 
		DocStats docstats = new DocStats()
		docstats.begin()

		if (!filestats.exists()) {
			log.debug "stats file does not exist. Creating one."
			filestats.createNewFile()
			log.info "Pre-analysing collection $collection, please wait."
			stats['total'] = collection.getNumberOfDocuments()
			stats['processed'] = 0
		} else {
			stats = readFile(filestats)
		}

		log.debug "Total number of docs in the collection: "+stats['total']
		docstats.totalDocs = stats['total']

		/** ITERATOR **/
		for (int i=stats['total']; i > 0; i -= doc_pool_size) {
			Map docs = [:]

			int limit = (i > doc_pool_size ? doc_pool_size : i)
			log.debug "Initial batch size: ${stats['total']} Remaining: $i Next pool size: $limit"

			docs = docTable.getBatchDocsAndNEsFromPoolToGenerateNEIndex(collection, limit, stats["processed"])
			
		// if it's null, then there's no more docs to process. Leave the loop.
			if (!docs) {
				log.info "DB returned no more docs, I guess I'm done."
				return
			}
			docstats.beginBatchOfDocs(limit)

			log.debug "Got ${docs?.size()} Doc(s)."

			// ADDING STUFF TO INDEX
			docs.each {doc_id, doc ->
				LgteDocumentWrapper ldoc = new LgteDocumentWrapper()
				ldoc.storeUtokenized(conf.get("saskia.index.id_label","id"), doc.doc_original_id)
				ldoc.storeUtokenized(conf.get("saskia.index.docid_label","docid"), doc.doc_id.toString())
				log.trace "Wrote doc id ${doc.doc_original_id}."

				/*****  NEs in body ******/
				doc.nes.each{ne ->
					/*ne.dbpediaPage?.values().toList().flatten()?.each{it ->
						if (it != null) {
							// indexString indexes but does not tokenize it
							ldoc.indexString(entity_label, DBpediaResource.getShortName(it))

							if (!stats.containsKey(entity_label)) stats[entity_label] = 0
							stats[entity_label]++
						}
					}*/
					if (ne.entity && ne.entity.ent_dbpedia_resource) {
						ldoc.indexString(entity_label,
							ne.entity.ent_dbpedia_resource.replaceAll(/-/,"\\-")) // indexString indexes but does not tokenize it
						if (!stats.containsKey(entity_label)) stats[entity_label] = 0
						stats[entity_label]++
					}
				}
				if (doc.nes.size() == 0) {
					log.warn "Doc ${doc.doc_original_id} has NO NEs on its body. Inserting an empty NE"
				}
				entwriter.addDocument(ldoc)
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

	static void main(args) {

		Options o = new Options()
		String fileseparator = System.getProperty("file.separator")

		o.addOption("db", true, "database (main/test)")
		o.addOption("col", true, "Collection name or ID")
		o.addOption("db", true, "main or test")
		o.addOption("help", false, "Gives this help information")
		o.addOption("indexdir", false, "directory of the index")

		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java saskia.index.GenerateEntityIndexForCollection", o )
			System.exit(0)
		}

	 	String DEFAULT_COLLECTION_NAME = "CD do Segundo HAREM"
	 	String DEFAULT_DB_NAME = "main"

		SaskiaDB db = new DBValidator()
			.validate(cmd.getOptionValue("db"), DEFAULT_DB_NAME)
	
		Collection collection = new CollectionValidator(db)
			.validate(cmd.getOptionValue("col"), DEFAULT_COLLECTION_NAME)

		String default_indexdir = IndexDirectoryValidator.buildIndexDirectory(
			 conf, collection, EntityIndexDirLabel)

		String indexdir = new IndexDirectoryValidator().validate(
			 cmd.getOptionValue("indexdir"), default_indexdir)
	
		File f = new File(indexdir)
		if (!f.exists()) f.mkdirs()

		GenerateNEIndexForCollection indexer = new GenerateNEIndexForCollection(
				collection, indexdir)

		indexer.index()
		println indexer.statusMessage()
	}
}
