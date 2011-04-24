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

import saskia.db.obj.User
import saskia.db.obj.RembrandtedDoc
import saskia.db.obj.SourceDoc
import saskia.db.table.DBTable

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
	
	static Collection createFromDBRow(DBTable dbtable, row) {
		Collection c = new Collection(dbtable)
		c.col_id = row['col_id']
		c.col_name = row['col_name']
		c.col_owner = User.getFromID(row['col_owner'])
		c.col_lang = row['col_lang']
		c.col_permission = row['col_permission']
		c.col_comment = row['col_comment']
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

	
	/*
	* Returns the number of REMBRANDTed documents for this collection
	*/
   public int getNumberOfRembrandtedDocuments() {
	   int i
	   if (!col_id) throw new IllegalStateException(
		   "Can't check the number of source documents of a collection without a collection ID.")
	   getDBTable().getSaskiaDB().getDB().eachRow(
		   "SELECT count(doc_id) from ${RembrandtedDoc.tablename} "+
			   "WHERE doc_collection=?",[col_id], {row -> i = row[0]})
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
			"SELECT count(sdoc_id) from ${SourceDoc.tablename} "+
			   "WHERE sdoc_collection=?",[col_id], {row -> i = row[0]})
	   return i
   }
   
    // used to add new collections.
	public Long addThisToDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT INTO ${getDBTable().getTablename()} VALUES(0,?,?,?,?,?)",
				[col_name, col_owner.usr_id, col_lang, col_permission, col_comment])
		col_id = (long)res[0][0]
		getDBTable().cacheIDCollection[col_id] = this
		log.info "Adding collection to DB: ${this}"
		return col_id
	}

	public int removeThisFromDB() {
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().getTablename()} where col_id=?",[col_id])
		getDBTable().cacheIDCollection.remove(col_id)
		log.info "Removing collection ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return "${col_id}:${col_lang}:${col_name}"
	}
}