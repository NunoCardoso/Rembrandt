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
import saskia.db.obj.DocTimeSignature
/**
 * @author Nuno Cardoso
 * This is an interface for doc_time_signature table, and also for 
 * GeoSignature generation
 *
 */
class DocTimeSignatureTable extends DBTable {

	Configuration conf

	static Logger log = Logger.getLogger("DocTimeSignature")
	static String tablename = "doc_time_signature"

	LinkedHashMap<Long,DocTimeSignatureTable> idCache


	public DocTimeSignatureTable(SaskiaDB db) {
		super(db)
		conf = Configuration.newInstance()
		idCache = new LinkedHashMap(
				conf.getInt("saskia.doc_time_signature.cache.number",1000), 0.75f, true) // true: access order.
	}

	public List<DocTimeSignatureTable> queryDB(String query, ArrayList params = []) {
		List<DocTimeSignatureTable> res = []
		getSaskiaDB().getDB().eachRow(query, params, {row  ->
			res << DocTimeSignature.createNew(this, row)
		})
		return res
	}

	/** Get a DocTimeSignature from id.
	 * @param id The id as needle.
	 * return the DocTimeSignature result object, or null
	 */
	public DocTimeSignature getFromID(long dts_id) {
		if (!dts_id) return null
		if (idCache.containsKey(dts_id)) return idCache[dts_id]
		List<DocTimeSignatureTable> dts = queryDB("SELECT * FROM ${tablename} WHERE dts_id=?", [dts_id])
		log.info "Querying for dts_id $dts_id got DocTimeSignature $dts."
		if (dts) {
			idCache[dts_id] = dts[0]
			return dts[0]
		}
		return null
	}

	static DocTimeSignature getFromID(SaskiaDB db, Long id) {
		return  db.getDBTable("DocTimeSignatureTable").getFromID(id)
	}

	static List<DocTimeSignature> getBatchOfTimeSignatures(Collection collection, limit = 10,  offset = 0) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		// ORDER BY doc_id ASC ensures that the TimeSignatures are batched just like in other indexes,
		// to ensure Lucene gets identical indexes for identical documents
		return queryDB("SELECT ${DocTimeSignatureTable.tablename}.*, ${RembrandtedDocTable.tablename}.doc_original_id, "+
		"${RembrandtedDocTable.tablename}.doc_id "+
		"FROM ${DocTimeSignatureTable.tablename}, ${RembrandtedDocTable.tablename} "+
		"WHERE doc_collection=? AND doc_id=dts_document "+
		"ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id])

	}
}