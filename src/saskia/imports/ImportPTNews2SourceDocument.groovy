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

import saskia.db.SaskiaDB
import saskia.io.SourceDoc
import saskia.io.Collection
import saskia.io.DocStatus
import saskia.bin.Configuration
import saskia.util.I18n
import org.apache.log4j.*

/** 
 * This class imports Wikipedia raw documents in the Saskia database to 
 * HTML documents in the Saskia's page table
*/

class ImportPTNews2Saskia {
	
	SaskiaDB saskia_db
	Configuration conf
	static Logger log = Logger.getLogger("Saskia")
	String snewsDB
	String saskiaDB
	Collection collection
	String lang="pt"
	
	public ImportPTNews2Saskia() {

	    conf = Configuration.newInstance()
	    saskia_db = SaskiaDB.newInstance()

	    snewsDB = "snews"
	    saskiaDB = "saskia"
	    log.debug "Target Saskia DB set to $saskiaDB"
	}
	
	public HashMap importBatchOfDocs(int batchSize = 100, String collection_name) {
	    
	    HashMap status = [imported:0, skipped:0]
	    collection = Collection.getFromName(collection_name)
		if (!collection) {
			log.error "Don't know collection $collection_name to parse documents on. Exiting."
			return status
		}
	    log.trace "Requesting import of a batch of ${batchSize} documents."
		boolean found = false
		
	    String query = "Select * FROM ${snewsDB}.news WHERE ${snewsDB}.news.id NOT IN (SELECT sdoc_id FROM ${saskiaDB}.${SourceDoc.tablename} "+
		    "WHERE sdoc_collection =?) LIMIT ${batchSize}"
		
		saskia_db.getDB().eachRow(query, [collection.col_id], {row ->
		    if (!found) found = true
			HashMap status_ = importDoc(row, collection, lang)
			status.imported += status_.imported
			status.skipped += status_.skipped
		})
		if (!found) log.warn "No documents found. Did NOT made a import."
		return status
	}
			
	
	
	public HashMap importDoc(db_row, Collection collection, String lang) {
	    /*
| id        | int(11)  | NO   | PRI | NULL    | auto_increment | 
| entryid   | text     | YES  |     | NULL    |                | 
| link      | text     | NO   |     | NULL    |                | 
| title     | text     | YES  |     | NULL    |                | 
| content   | text     | YES  |     | NULL    |                | 
| summary   | text     | YES  |     | NULL    |                | 
| category  | text     | YES  |     | NULL    |                | 
| author    | text     | YES  |     | NULL    |                | 
| issued    | datetime | YES  |     | NULL    |                | 
| modified  | datetime | YES  |     | NULL    |                | 
| source    | text     | YES  |     | NULL    |                | 
| retrieved | datetime | YES  |     | NULL    |                | 
| checksum  | text     | YES  |     | NULL 
	      */
	   
	   long id = db_row['id']
	   String comment = ""
	   String html = "<HTML>\n<HEAD>\n";
	   Date date_created
	   // entryid é o url do feed
	   comment += I18n.collection['rssfeedlink'][lang]+":"+db_row['entryid']+"\n"
	   // link  é o url original
	   comment += I18n.collection['pagelink'][lang]+":"+db_row['link']+"\n"
	   // title
	   html += "<TITLE>"+db_row['title'].trim()+"</TITLE>\n";
	   html += "</HEAD>\n</BODY>\n";
	   // content
	   String content = db_row['content'].trim()
	   
	   /**** let's clean content. ****/
	   // 1. TSF has adds on the end
	   int tsf_ads_index = content.indexOf("<p><a href=\"http://feedads")
	   if (tsf_ads_index > 0) content = content.substring(0,tsf_ads_index)
	   // 2. SIC adds initial IMG
	   content = content.replaceAll(/^<IMG[^>]*>/, "")
	   // 3. SIC has an awful tail. starts with a link. 
	   int sic_ads_index = content.indexOf("<a href=\"http://www.sic.pt/online/noticias")
	   if (sic_ads_index > 0) content = content.substring(0,sic_ads_index)
	   html += content
	   // summary 
	   html += "</BODY></HTML>"
	   
	   // category
	   if (db_row['category']) comment += I18n.collection['category'][lang]+": "+db_row['category']+"\n"
	   // author
	   if (db_row['author']) comment += I18n.collection['author'][lang]+": "+db_row['author']+"\n"
	   //issued
	   date_created = (Date)db_row['issued']
	   //modified
	   
	   // source
	   if (db_row['source']) comment += I18n.collection['source'][lang]+": "+db_row['source']+"\n"
	   // retrieved
	   
	   // checksum
	   
	   SourceDoc s = new SourceDoc(sdoc_id:id, 
			sdoc_collection:collection.col_id, 
			sdoc_lang:lang, sdoc_content:html, sdoc_date:date_created, 
			sdoc_comment:comment,
			sdoc_proc:DocStatus.READY, sdoc_edit:DocStatus.UNLOCKED)
		s.addThisToDB()
		
		//println "id: $id\n html: $html\n comment: $comment\n\n" 

		log.debug "Inserted $s into Saskia DB."
		return [imported:1, skipped:0]	
	}
	
	static void main(args) {
	    
	    String usage = "Usage: saskia.imports.ImportPTNews2Saskia -batchSize [target-collection] [number-documents]\n"+
		  "Learn it.";
	  
		if (!args || args.size() < 3 ) {
		    println usage
		    System.exit(0)
		}

		ImportPTNews2Saskia w2r = new ImportPTNews2Saskia()
		HashMap status = [imported:0, skipped:0]
		
		 if (args[0] == "-batchSize") { 
		    status = w2r.importBatchOfDocs( Integer.parseInt(args[2]),  args[1] )
		} else {
		    println usage
		    System.exit(0) 
		}
		log.info "Done. Imported ${status.imported} doc(s), skipped ${status.skipped} doc(s)."
	}
}