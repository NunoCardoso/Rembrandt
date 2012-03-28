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

package saskia.db.obj

import org.apache.log4j.Logger

import saskia.db.table.*

/**
 * @author Nuno Cardoso
 * A Collection object
 */
class Collection extends DBObject implements JSONable {

	Long col_id
	String col_name
	User col_owner
	String col_lang
	String col_permission
	String col_comment

	static Logger log = Logger.getLogger("Collection")

	static Map type = ['col_id':'Long', 'col_name':'String', 'col_owner':'User',
		'col_lang':'String', 'col_permission':'String', 'col_comment':'String']

	public Collection(DBTable dbtable) {
		super(dbtable)
	}

	static Collection createNew(DBTable dbtable, row) {
		Collection c = new Collection(dbtable)
		if (row['col_id']) c.col_id = row['col_id']
		if (row['col_name']) c.col_name = row['col_name']
		if (row['col_owner'])
			c.col_owner = (row['col_owner'] instanceof User ? 
			row['col_owner'] : 
			dbtable.getSaskiaDB().getDBTable("UserTable").getFromID(row['col_owner']) )
		if (row['col_lang']) c.col_lang = row['col_lang']
		if (row['col_permission']) c.col_permission = row['col_permission']
		if (row['col_comment']) c.col_comment = row['col_comment']
		return c
	}

	Map toMap() {
		return ["col_id":col_id, "col_name":col_name,
			"col_owner":col_owner.toSimpleMap(), "col_lang":col_lang,
			'col_permission':col_permission, "col_comment":col_comment]
	}

	Map toSimpleMap() {
		return ["col_id":col_id, "col_name":col_name]
	}

	public updateValue(column, value) {
		return getDBTable().updateValue(col_id, column, value);
	}
	
	/*
	 * Returns the number of REMBRANDTed documents for this collection
	 */
	public int getNumberOfRembrandtedDocuments() {
		int i
		if (!col_id) throw new IllegalStateException(
			"Can't check the number of source documents of a collection without a collection ID.")
		getDBTable().getSaskiaDB().getDB().eachRow(
				"SELECT count(doc_id) from ${RembrandtedDocTable.tablename} "+
				"WHERE doc_collection=?",[col_id], {row -> i = row[0]})
		return i
	}
	
	public int getNumberOfRembrandtedDocsWithComment(String comment) {

		int i
		getDBTable().getSaskiaDB().getDB().eachRow(
			"SELECT count(*) FROM  ${RembrandtedDocTable.tablename}, ${SourceDocTable.tablename} "+
			" WHERE doc_collection=? and doc_id=sdoc_doc and sdoc_comment = ? "+
			"ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id, comment], {row -> 
			i = row[0]
		})
		return i
	}
	
	/*
	 * Returns the number of source documents for this collection
	 */
	public int getNumberOfSourceDocuments() {
		int i
		if (!col_id) throw new IllegalStateException(
			"Can't check the number of source documents of a collection without a collection ID.")
		getDBTable().getSaskiaDB().getDB().eachRow(
				"SELECT count(sdoc_id) from ${SourceDocTable.tablename} "+
				"WHERE sdoc_collection=?",[col_id], {row -> i = row[0]})
		return i
	}

	public HashMap listSourceDocs(limit = 10,  offset = 0, column = null, needle = null) {
		// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0

		String where = "WHERE sdoc_collection=?"
		List params = [col_id]
		if (column && needle) {

			switch (SourceDoc.type[column]) {
				case 'String': where += " AND $column LIKE '%${needle}%'"; break
				case 'Long': where += " AND $column=? "; params << Long.parseLong(needle); break
				case 'DocStatus':  where += " AND $column = ?"; params << needle; break
				case 'Date': where += " AND $column = ?"; params << needle; break
			}
		}
		String query = "SELECT SQL_CALC_FOUND_ROWS * FROM ${SourceDocTable.tablename} $where "+
				"LIMIT ${limit} OFFSET ${offset} UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), NULL, NULL, NULL, "+
				"NULL, NULL, NULL, NULL, NULL, NULL"
		log.debug "query = $query params = $params class = "+params*.class

		List u
		try {u = dbtable.getSaskiaDB().getDBTable("SourceDocTable").queryDB(query, params) }
		catch(Exception e) {log.error "Error getting source doc list: ", e}

		// last item is not a document... it's the count.
		SourceDoc fakesdoc = u.pop()
		long total = fakesdoc.sdoc_id

		log.debug "Returning "+u.size()+" results."
		return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
			"column":column, "value":needle, "col_id":col_id]
	}


	// used to add new collections.
	public Long addThisToDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().tablename} VALUES(0,?,?,?,?,?)",
				[
					col_name,
					col_owner.usr_id,
					col_lang,
					col_permission,
					col_comment
				])
		col_id = (long)res[0][0]
		getDBTable().cacheIDCollection[col_id] = this
		log.info "Adding collection to DB: ${this}"
		return col_id
	}

	public int removeThisFromDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} where col_id=?",[col_id])
		getDBTable().cacheIDCollection.remove(col_id)
		log.info "Removing collection ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return "${col_id}:${col_lang}:${col_name}"
	}
}