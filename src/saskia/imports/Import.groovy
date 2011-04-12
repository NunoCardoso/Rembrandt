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

abstract class Import {
	
	Configuration conf 
	static Logger log = Logger.getLogger("SaskiaImports")
	Collection collection 
	File file
	InputStreamReader inputStreamReader
	HashMap status
	String lang

	/*
   RembrandtWriter writer 
	String ynae
	*/
	
	public class Import(File file, Collection collection,
			String lang, String encoding) {
        
		this.conf = Configuration.newInstance()
		this.lang = lang
	   this.collection = collection
		this.inputStreamReader = new InputStreamReader(new FileInputStream(file), encoding)
		this.status = [imported:0, skipped:0]
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
         log.warn "Found error while adding SourceDoc $s into SaskiaDB. Skipping".
			log.warn "Why? " + e2.getMessage()  
			status.skipped++
		}
		return s
	}      
}