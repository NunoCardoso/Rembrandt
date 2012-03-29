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
import saskia.db.GeoSignature;
import saskia.db.table.*;
import rembrandt.io.DocStats
import saskia.db.obj.*
import saskia.db.table.*
import saskia.db.database.*

import saskia.util.validator.*

/**
 * @author Nuno Cardoso
 * 
 * This class generates NE index for a given collection. 
 *
 */
class GenerateGeoIndexForCollection extends IndexGenerator {

	static Configuration conf = Configuration.newInstance()
	static String geoIndexDirLabel = "geo-index"
	static String collectionLabel = "col"
	static String woeid_label = conf.get("saskia.index.woeid_label","woeid")
	int doc_pool_size = conf.getInt("saskia.index.woeid.doc_pool_size",10000)
	static LgteIndexWriter geowriter // Hash of indexes

	// static List<SemanticClassification> allowedClasses = [ ]

	Collection collection
	Map<String, Analyzer> analyzerMap
	LgteBrokerStemAnalyzer analyzer
	String indexdir
	File filestats
	Map stats
	String fileseparator = System.getProperty("file.separator")

	// indexdir goes to ${rembrandt.home.dir}/index/col-X/
	public GenerateGeoIndexForCollection(Collection collection, String indexdir) {
		super()
		this.collection=collection
		this.indexdir=indexdir

		geowriter = new LgteIndexWriter(indexdir, new LgteNothingAnalyzer(), true, Model.OkapiBM25Model)

		log.debug "Opening a new Geo writer, $geowriter"
		filestats = new File(indexdir, "../${geoIndexDirLabel}-collection-stats.txt")
		stats = [:]
	}

	public index() {
		DocGeoSignatureTable docGeoSignatureTable = collection
			.getDBTable().getSaskiaDB().getDBTable("DocGeoSignatureTable") 
		GeoscopeTable gt = collection
			.getDBTable().getSaskiaDB().getDBTable("GeoscopeTable") 
			
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

		for (int i=stats['total']; i > 0; i -= doc_pool_size) {

			int limit = (i > doc_pool_size ? doc_pool_size : i)
			log.debug "Initial batch size: ${stats['total']} Remaining: $i Next pool size: $limit"

			List geos = docGeoSignatureTable.getBatchOfGeoSignatures(collection, limit, stats["processed"])
			log.debug "Got ${geos?.size()} DocGeoSignatures(s)."

			// if it's null, then there's no more docs to process. Leave the loop.
			if (!geos) {
				log.info "DB returned no more signatures, I guess I'm done."
				return
			}
			docstats.beginBatchOfDocs(limit)

			geos.each {g ->
				// log.debug "I'm with geo $geo"
				
				GeoSignature geosig = new GeoSignature(gt, g)
				
				LgteDocumentWrapper ldoc = new LgteDocumentWrapper()
				ldoc.storeUtokenized(conf.get("saskia.index.id_label","id"), geosig.doc_original_id)
				ldoc.storeUtokenized(conf.get("saskia.index.docid_label","docid"), geosig.doc_id.toString())

				log.trace "ldoc with doc_original_id ${geosig.doc_original_id}"

				/*****  NEs in body ******/
				geosig.places.each{place ->
					log.trace "Adding "+place.woeid.toString()
					ldoc.indexString(woeid_label, place.woeid.toString())
					place.ancestors*.woeid.each{w ->
						log.trace "Adding "+w.toString()
						ldoc.indexString(woeid_label, w.toString())
					}
				}
				geowriter.addDocument(ldoc)
			}

			docstats.endBatchOfDocs(limit)
			stats['processed'] += limit
			docstats.printMemUsage()
		}
		log.debug "Optimizing indexes..."
		geowriter.optimize()
		geowriter.close()


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
		o.addOption("help", false, "Gives this help information")
		o.addOption("indexdir", false, "directory of the index")

		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter()
			formatter.printHelp( "java saskia.index.GenerateGeoIndexForCollection", o )
			System.exit(0)
		}

	 	String DEFAULT_COLLECTION_NAME = "CD do Segundo HAREM"
	 	String DEFAULT_DB_NAME = "main"

		SaskiaDB db = new DBValidator()
			.validate(cmd.getOptionValue("db"), DEFAULT_DB_NAME)
	
		Collection collection = new CollectionValidator(db)
			.validate(cmd.getOptionValue("col"), DEFAULT_COLLECTION_NAME)

		String default_indexdir = IndexDirectoryValidator.buildIndexDirectory(
			 conf, collection, geoIndexDirLabel)

		String indexdir = new IndexDirectoryValidator().validate(
			 cmd.getOptionValue("indexdir"), default_indexdir)
	
		File f = new File(indexdir)
		if (!f.exists()) f.mkdirs()

		GenerateGeoIndexForCollection indexer = new GenerateGeoIndexForCollection(
				collection, indexdir)

		indexer.index()
		println indexer.statusMessage()
	}
}

