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
/**
 * @author Nuno Cardoso
 *
 */
class DocGeoSignature extends DBObject { // implements JSONable {

	Long dgs_id
	Long dgs_document
	String dgs_signature
	Tag dgs_tag
	Date dgs_date_created

	// meta information
	String dgs_document_original_id
	Long dgs_document_id

	static Logger log = Logger.getLogger("DocGeoSignature")

	static Map type = ['dgs_id':'Long', 'dgs_document':'Long', 'dgs_signature':'String',
		'dgs_tag':'Tag','dgs_date_created':'Date',
		'dgs_document_original_id':'String', 'dgs_document_id':'Long']
	
	public DocGeoSignature(DBTable dbtable) {
		super(dbtable)
	}
	
	static createNew(DBTable dbtable, row) {
		DocGeoSignature g = new DocGeoSignature(dbtable)
		g.dgs_id = row['dgs_id']
		g.dgs_document = row['dgs_document']
		if (row['dgs_signature']) g.dgs_signature = row['dgs_signature']
		if (row['dgs_tag']) 
			g.dgs_tag = (row['dgs_tag'] instanceof Tag ? row['dgs_tag'] :
			 	dbtable.getSaskiaDB().getDBTable("TagTable").getFromID(row['dgs_tag']) )
		if (row['dgs_date_created']) g.dgs_date_created = (Date)row['dgs_date_created']

		// meta-info
		try {g.dgs_document_original_id = row['doc_original_id']}
		catch(Exception e) {}
		try {g.dgs_document_id = row['doc_id']}

		catch(Exception e) {}
		return g
	}

	public Long addThisToDB() {

		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().tablename}(dgs_document, " +
				"dgs_signature, dgs_tag, dgs_date_created) VALUES(?,?,?, NOW())",
				[dgs_document, dgs_signature, dgs_tag.tag_id])
		long new_dgs_id = (long)res[0][0]
		log.info "Inserted new DocGeoSignature for doc $dgs_document, got new_dgs_id $new_dgs_id"
		getDBTable().idCache[new_dgs_id] = this
		return new_dgs_id
	}

	public int removeThisFromDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} where dgs_id=?",[dgs_id])
		getDBTable().idCache.remove(dgs_id)
		log.info "Removing DocGeoSignature ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return "${dgs_id}:${dgs_document}"
	}
}
