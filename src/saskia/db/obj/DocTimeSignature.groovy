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
import saskia.db.table.DBTable
import saskia.db.obj.Tag

/**
 * @author Nuno Cardoso
 *
 */
class DocTimeSignature extends DBObject {

	Long dts_id
	Long dts_document
	String dts_signature
	Tag dts_tag
	Date dts_date_created

	// meta

	Long dts_document_id
	String dts_document_original_id

	static Logger log = Logger.getLogger("DocTimeSignature")
	
	public DocTimeSignature(DBTable dbtable) {
		super(dbtable)
	}
	
	static createFromDBRow(DBTable dbtable, row) {
		DocTimeSignature g = new DocTimeSignature(dbtable)
		g.dts_id = row['dts_id']
		g.dts_document = row['dts_document']
		if (row['dts_signature']) g.dts_signature = row['dts_signature']
		if (row['dts_tag']) g.dts_tag = Tag.getFromID(row['dts_tag'])
		if (row['dts_date_created']) g.dts_date_created = (Date)row['dts_date_created']

		//meta
		try {g.dts_document_id = row['doc_id']}
		catch(Exception e) {}
		try {g.dts_document_original_id = row['doc_original_id']}
		catch(Exception e) {}
		return g
	}

	public Long addThisToDB() {

		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().getTablename()}(dts_document, " +
				"dts_signature, dts_tag, dts_date_created) VALUES(?,?,?, NOW())",
				[dts_document, dts_signature, dts_tag.tag_id])
		long new_dts_id = (long)res[0][0]
		log.info "Inserted new DocTimeSignature for doc $dts_document, got new_dts_id $new_dts_id"
		getDBTable().idCache[new_dts_id] = this
		return new_dts_id
	}

	public int removeThisFromDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().getTablename()} where dts_id=?",
				[dts_id])
		getDBTable().idCache.remove(dts_id)
		log.info "Removing DocTimeSignature ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return "${dts_id}:${dts_document}"
	}
}
