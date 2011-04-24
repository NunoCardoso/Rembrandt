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
package saskia.db.table

import org.apache.log4j.Logger

import saskia.bin.Configuration
import saskia.db.database.SaskiaDB
import saskia.db.obj.Collection
import saskia.db.obj.DocGeoSignature
/**
 * @author Nuno Cardoso
 * This is an interface for doc_geo_signature table, and also for 
 * GeoSignature generation
 *
 */
class DocGeoSignatureTable extends DBTable {

	Configuration conf
	static Logger log = Logger.getLogger("DocGeoSignature")

	LinkedHashMap<Long,DocGeoSignatureTable> idCache


	public DocGeoSignatureTable(SaskiaDB db) {
		super(db, "doc_geo_signature")
		conf = Configuration.newInstance()
		idCache = new LinkedHashMap(
				conf.getInt("saskia.doc_geo_signature.cache.number",1000), 0.75f, true) // true: access order.
	}

	public List<DocGeoSignatureTable> queryDB(String query, ArrayList params = []) {

		List<DocGeoSignatureTable> res = []
		DocGeoSignatureTable g

		getSaskiaDB().getDB().eachRow(query, params, {row  ->
			res << DocGeoSignature.createFromDBRow(this.owner, row)
		})
		return res
	}

	/** Get a DocGeoSignature from id.
	 * @param id The id as needle.
	 * return the DocGeoSignature result object, or null
	 */
	public DocGeoSignature getFromID(Long dgs_id) {
		if (!dgs_id) return null
		if (idCache.containsKey(dgs_id)) return idCache[dgs_id]
		List<DocGeoSignatureTable> dgs = queryDB("SELECT * FROM ${tablename} WHERE dgs_id=?", [dgs_id])
		log.info "Querying for dgs_id $dgs_id got DocGeoSignature $dgs."
		if (dgs) {
			idCache[dgs_id] = dgs[0]
			return dgs[0]
		}
		return null
	}

	public List<DocGeoSignature> getBatchOfGeoSignatures(Collection collection, limit = 10,  offset = 0) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		// it HAS TO FOLLOW RembrnadtedDoc order, so that LuceneIDs are the same!


		// ORDER BY doc_id ASC ensures that the GeoSignatures are batched just like in other indexes,
		// to ensure Lucene gets identical indexes for identical documents
		return queryDB("SELECT ${DocGeoSignatureTable.tablename}.*, ${RembrandtedDocTable.tablename}.doc_id, "+
		"${RembrandtedDocTable.tablename}.doc_original_id "+
		"FROM ${DocGeoSignatureTable.tablename}, ${RembrandtedDocTable.tablename} "+
		"WHERE doc_collection=? AND doc_id=dgs_document "+
		"ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id])
	}
}