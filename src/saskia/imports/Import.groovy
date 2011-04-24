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
import saskia.bin.Configuration
import saskia.io.SourceDoc
import org.apache.log4j.Logger
import java.util.regex.*
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException

abstract class Import {
	
	Configuration conf = Configuration.newInstance()
	static Logger log = Logger.getLogger("SaskiaImports")
	Collection collection
	File file
	InputStreamReader inputStreamReader
	HashMap status
	String lang
	String encoding
	
	public Import() {
		this.status = [imported:0, skipped:0]
	}
	
	public prepareInputStreamReader() {
		return new InputStreamReader(new FileInputStream(file), encoding)
	}
	
	public Import(File file, Collection collection,
	String lang, String encoding) {
		
		this.lang = lang
		this.collection = collection
		this.encoding = encoding
		this.file = file
		
		this.status = [imported:0, skipped:0]
		this.inputStreamReader = prepareInputStreamReader()
	}
	
	
	/**
	 * Let's validate if a given collection name and/or id gets to a 
	 * real collection.
	 * @return A collection if it's there, null otherwise
	 */
	Collection validateCollection(String collection_given_name, String DEFAULT_COLLECTION_NAME) {
		
		String collection_name
		
		if (!collection_given_name) {
			println "no collection given. What is the target collection ID/name?"
			println "(Default: ${DEFAULT_COLLECTION_NAME}) "
			println "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			collection_name = input.readLine().trim()
			if (!collection_name) collection_name = DEFAULT_COLLECTION_NAME
		} else {
			collection_name = collection_given_name
		}
		
		return Collection.getFromNameOrID(collection_name)
		
	}
	
	/**
	 * Validade a filename to a file
	 * @param filename The filename
	 * @return File if exists, null otherwise
	 */
	File validateFile(String filename) {
		
		File file = new File(filename)
		if (!file.exists()) {
			println "no file given. What is the file name for the import?"
			println "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			file = new File(input.readLine().trim())
		}
		return file
	}
	
	/**
	 * Validate the encoding
	 * 
	 */
	String validateEncoding(given_encoding) {
		
		String default_encoding = conf.get("rembrandt.input.encoding", System.getProperty("file.encoding"))
		String encoding = null
		if (given_encoding) {
			encoding = given_encoding
		} else {
			println "no file encoding given. What is the import file encoding (default: ${default_encoding})?"
			println "> "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			encoding = new File(input.readLine().trim())
		}
		return encoding
	}
	
	public abstract HashMap importDocs();
	
	public SourceDoc addSourceDoc(String original_id, String content, String lang, Date date,
	String comment = "") {
		
		SourceDoc s = new SourceDoc(
		sdoc_original_id:original_id,
		sdoc_collection:collection,
		sdoc_lang:lang,
		sdoc_content:content,
		sdoc_doc:null,
		sdoc_date:date,
		sdoc_proc:DocStatus.READY,
		sdoc_comment:comment
		)
		
		// by adding to DB, it already checks for duplicates
		try {
			s.addThisToDB()
			log.info "SourceDoc $s is now into Saskia DB."
			status.imported++
		} catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			log.warn "Found duplicate entry in DB. Skipping SourceDoc $s."
			status.skipped++
		} catch(Exception e2) {
			log.warn "Found error while adding SourceDoc $s into SaskiaDB. Skipping."
			log.warn "Why? " + e2.getMessage()
			status.skipped++
		}
		return s
	}	
}