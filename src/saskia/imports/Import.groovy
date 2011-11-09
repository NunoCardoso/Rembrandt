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

package saskia.imports

import rembrandt.obj.Document
import rembrandt.io.Reader
import rembrandt.io.Writer
import saskia.bin.Configuration
import saskia.db.obj.*
import saskia.db.table.*
import saskia.db.database.*
import saskia.db.DocStatus

import org.apache.log4j.Logger
import java.util.regex.*
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException

abstract class Import {

	Configuration conf = Configuration.newInstance()
	static Logger log = Logger.getLogger("Imports")

	Collection collection
	SaskiaDB db
	File file
	HashMap status
	String lang
	String encoding

	Reader reader
	Writer writer
	
	Integer doc_pool = 50

	public Import() {
		this.status = [imported:0, refreshed:0, skipped:0, failed:0]
	}

	public Import(File file, SaskiaDB db, Collection collection,
	String lang, String encoding) {

		this.lang = lang
		this.db = db
		this.collection = collection
		this.encoding = encoding
		this.file = file

		this.status = [imported:0, skipped:0]
		this.inputStreamReader = prepareInputStreamReader()
	}

	public void prepareInputStreamReader() {
		reader.setInputStreamReader(new InputStreamReader(new FileInputStream(this.file)))
		log.debug "Stream prepared."
	}
	
	abstract importer();

	public String statusMessage() {
		String message = "Done."
		status.each{k, v->
			if (v) {
				message << "$v doc(s) $k. "
			}
		}
		return message
	}	
	
	public SourceDoc addSourceDoc(String original_id, String content, String lang, Date date,
	String comment = "") {

		if (!db) {
			log.fatal "No DB specified, so SourceDoc doesn't know where to be stored."
			log.fatal "Please configure your importer to have a DB reference"
			System.exit(0)
		}
		
		SourceDocTable sdt = db.getDBTable("SourceDocTable")
		
		// check if exists in DB
		SourceDoc sourceDocInDB = sdt.getFromOriginalIDandCollectionID(original_id, collection.col_id) 

		// create the New 
		SourceDoc sourceDocNew = SourceDoc.createNew(
				sdt,
				[sdoc_original_id:original_id, 
				 sdoc_collection:collection,
				 sdoc_lang:lang, 
				 sdoc_content:content,
				 sdoc_date:date, 
				 sdo_proc:DocStatus.READY,
				 sdoc_comment:comment]
				)
		
		// if there is sourceDocInDB, give its sdoc_id to the sourceDocNew
		// so that REPLACE is successfully done in the DB
		if (sourceDocInDB) {
			sourceDocNew.sdoc_id = sourceDocInDB.sdoc_id
		}
				
		if (!sourceDocInDB) {
			try {
				sourceDocNew.sdoc_id = sourceDocNew.addThisToDB()
				log.info "SourceDoc $sourceDocNew is now INSERTED into Saskia DB."
				status.imported++
			} 
			// doesn't apply - There is a Primary Key only on sdoc_id
			//catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			//	log.warn "Found duplicate entry in DB. Skipping SourceDoc $s."
			//	status.skipped++
			catch(Exception e2) {
				log.warn "Found error while inserting SourceDoc $sourceDocNew into SaskiaDB. Skipping."
				log.warn "Why? " + e2.getMessage()
				status.skipped++
			}
		// 	
		} else {
			try {
				sourceDocNew.sdoc_id = sourceDocNew.replaceThisToDB()
				log.info "SourceDoc $sourceDocNew is now REPLACED into Saskia DB."
				status.imported++
			}
			// doesn't apply - There is a Primary Key only on sdoc_id
			//catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			//	log.warn "Found duplicate entry in DB. Skipping SourceDoc $s."
			//	status.skipped++
			catch(Exception e2) {
				log.warn "Found error while replacing SourceDoc $sourceDocNew into SaskiaDB. Skipping."
				log.warn "Why? " + e2.getMessage()
				status.skipped++
			}

			
		}
		return sourceDocNew
	}
}