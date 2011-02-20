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

import saskia.io.WikipediaDB
import saskia.io.SaskiaDB
import saskia.io.Tag
import saskia.io.RembrandtedDoc
import saskia.io.Collection
import saskia.io.DocStatus
import saskia.converters.CHAVE2RembrandtedDocumentConverter
import saskia.bin.Configuration
import saskia.util.I18n
import org.apache.log4j.*

/** 
 * This class imports CHAVE documents in the Saskia database 
*/

class ImportChave2Saskia {
	
	SaskiaDB saskia_db
	Configuration conf
	static Logger log = Logger.getLogger("Saskia")
	String saskiaDB
	Collection collection
	CHAVE2RembrandtedDocumentConverter c2rd
	Tag tag 
	String lang
	String ynae

	public ImportChave2Saskia() {

	    conf = Configuration.newInstance()
	    saskia_db = SaskiaDB.newInstance()
	    saskiaDB = "saskia"
	    log.debug "Target Saskia DB set to $saskiaDB"
	    c2rd = new CHAVE2RembrandtedDocumentConverter()
	    tag = Tag.getFromVersion("Imported")
		if (!tag) {
			tag = new Tag(tag_version:"Imported", tag_comment:"Imported")
			tag.tag_id = tag.addThisToDB()
		}
	}
	
	public HashMap importDoc(File file, collection_) {
	    
	    HashMap status = [imported:0, skipped:0]
	    try {
		    collection = Collection.getFromID(Long.parseLong(collection_))		
	    } catch(Exception e) {
			collection = Collection.getFromName(collection_)
	    }
		if (!collection) {
			log.error "Don't know collection $collection_name to parse documents on. Exiting."
			return status
		}
	    
	    log.trace "Requesting import for file $file."
	    // you have to convert it to document...     
	    String id = file.name.replaceAll(/\.html/,"")
	    
	    def date_created = new GregorianCalendar()
	    def m 
	    m = id =~ /FSP(9\d)(\d\d)(\d\d)-(\d+)/
	    if (m.matches()) {

			date_created.set(Calendar.YEAR, 1900 + Integer.parseInt(m.group(1)) )
			date_created.set(Calendar.MONTH,  Integer.parseInt(m.group(2)) -1)
			date_created.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)) )
	    }
	    m = id =~ /PUBLICO-(199\d)(\d\d)(\d\d)-(\d+)/
	    if (m.matches()) {
		    date_created.set(Calendar.YEAR,  Integer.parseInt(m.group(1)) )
			date_created.set(Calendar.MONTH,  Integer.parseInt(m.group(2)) -1)
			date_created.set(Calendar.DAY_OF_MONTH , Integer.parseInt(m.group(3)) )
	    }
            m = id =~ /LA(\d\d)(\d\d)(94)-(\d+)/
            if (m.matches()) {

                        date_created.set(Calendar.YEAR, 1900 + Integer.parseInt(m.group(3)) )
                        date_created.set(Calendar.MONTH,  Integer.parseInt(m.group(2)) -1)
                        date_created.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(1)) )
            }
            m = id =~ /GH(95)(\d\d)(\d\d)-(\d+)/
            if (m.matches()) {
                    date_created.set(Calendar.YEAR,  Integer.parseInt(m.group(1)) )
                        date_created.set(Calendar.MONTH,  Integer.parseInt(m.group(2)) -1)
                        date_created.set(Calendar.DAY_OF_MONTH , Integer.parseInt(m.group(3)) )
            }

		date_created.set(Calendar.HOUR , 0 )
		date_created.set(Calendar.MINUTE , 0 )		
		date_created.set(Calendar.SECOND , 0 )    
	    
	    // Date object is given with a  date_created.time
	    
	    // c2rd converts from CHAVE format to RembrandtedDoc format
	    c2rd.clear()
	    String text = c2rd.parse(file)
	   
	    // let's check if there's a rdoc on the DB
	    RembrandtedDoc rdoc = RembrandtedDoc.getFromOriginalDocIDandCollectionAndLang(		
		    id, collection.col_id, lang)
	
		BufferedReader input
		if (rdoc) {
		    
		    // let's ask when it was never asked, or answered y or n
		    if ((!ynae) || (ynae == "y") || (ynae == "n")) {
			    ynae = null
				input = new BufferedReader(new InputStreamReader(System.in))
				while (!(ynae == "a" || ynae == "e" || ynae == "n" || ynae == "y")) {
				    println "Overwriting RembrandtedDoc ${id} on DB? ([y]es, [a]lways, [n]o, n[e]ver)"
				    ynae = input.readLine().trim()
				}
		    }
		    
		    if ((ynae == "y") || (ynae == "a")) {
				RembrandtedDoc newrdoc = new RembrandtedDoc(doc_id:rdoc.doc_id, 
					doc_collection:collection, doc_original_id:id,
			    doc_lang:lang, doc_date_created:date_created.time, doc_content:text)
				newrdoc.replaceThisToDB()				
				newrdoc.changeEditStatusInDBto(DocStatus.UNLOCKED) // release him
				newrdoc.changeProcStatusInDBto(DocStatus.READY) // mark it so next time we'll not use it
				newrdoc.changeSyncStatusInDBto(DocStatus.NOT_SYNCED_DOC_CHANGED) 
				log.debug "RembrandtDoc ${rdoc.doc_original_id} in DB overwrited."

		    } else if ((ynae == "n") || (ynae == "e")) {
				log.debug "Found ${rdoc.doc_original_id} in Saskia DB, skipping."
		    }
		} 
	   // I need:
	   else {
     		rdoc = new RembrandtedDoc(doc_original_id:id, doc_collection:collection, doc_lang:lang,
		    doc_date_created:date_created.time, doc_content:text)
			
		    rdoc.doc_id = rdoc.addThisToDB()
		    

			rdoc.changeEditStatusInDBto(DocStatus.UNLOCKED) // release him
			rdoc.changeProcStatusInDBto(DocStatus.READY) // mark it so next time we'll not use it
			rdoc.changeSyncStatusInDBto(DocStatus.NOT_SYNCED_DOC_CHANGED) 
			rdoc.associateWithTag(tag)
			log.debug "Inserted doc w/id $id into Saskia DB."
	   }
	   
	
	/*	println "ID: "+id
		println "Collection: "+collection
		println "TEXT: "+text
		println "Date: "+ date_created.time
		*/
		return [imported:1, skipped:0]	
	}
	
	void setLang(String lang) {
	   this.lang=lang
	}

	static void main(args) {
	    
	    String usage = "Usage: saskia.imports.ImportChave2Saskia [dir] [lang] [target-collection]\n"+
		  "Learn it.";
	  
		if (!args || args.size() < 3 ) {
		    println usage
		    System.exit(0)
		}

		ImportChave2Saskia c2r = new ImportChave2Saskia()
		HashMap status = [read:0, imported:0, skipped:0]
		c2r.setLang(args[1])		

		File file = new File(args[0])
		
		if (file.isDirectory()) { 	   
		    file.eachFileRecurse{f -> 
				status.read++
				if (f.name.startsWith("FSP9") || f.name.startsWith("PUBLICO-199") || 
				f.name.startsWith("GH95") || f.name.startsWith("LA")) {
				    def newstatus = c2r.importDoc(f, args[2] )
				    status.imported += newstatus.imported
				    status.skipped += newstatus.skipped
				}
		    }
		} else if (file.isFile()) {
		    status.read++
			if (file.name.startsWith("FSP9") || file.name.startsWith("PUBLICO-199") || 
			   file.name.startsWith("GH95") || file.name.startsWith("LA")) {
			    def newstatus = c2r.importDoc(file, args[2] )
			    status.imported += newstatus.imported
			    status.skipped += newstatus.skipped
			}
		} else {
		    println "Can't process ${args[0]}, it's not a fine nor a directory. Exiting."
		    System.exit(0)
		}		
		
		log.info "Done. Saw ${status.read} docs, imported ${status.imported} doc(s), skipped ${status.skipped} doc(s)."
	}
}