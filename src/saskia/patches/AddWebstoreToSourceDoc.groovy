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
package saskia.patches;

import saskia.db.SaskiaWebstore
import saskia.db.obj.Collection
import saskia.db.database.SaskiaMainDB

import org.apache.log4j.*
import org.apache.commons.cli.*
import pt.tumba.webstore.*

/**
 * @author Nuno Cardoso
 * 1 2 7 8
 */
class AddWebstoreToSourceDoc {
    	
    static void main(args) {
        
        Options o = new Options()
        String fileseparator = System.getProperty("file.separator")
        
        o.addOption("col", true, "Collection name or ID")
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        
        if (!cmd.hasOption("col")) {
            println "No --col arg. Please specify the collection. Exiting."
            System.exit(0)
        }

        Collection collection 
        try {
            collection = Collection.getFromID(Long.parseLong(cmd.getOptionValue("col")))		
        } catch(Exception e) {
            collection = Collection.getFromName(cmd.getOptionValue("col"))
        }
        if (!collection) {
            println "Don't know collection ${cmd.getOptionValue('col')} to parse documents on. Exiting."
            System.exit(0) 
        } 
        
        println "Initializing collection $collection"
        SaskiaMainDB db = SaskiaMainDB.newInstance()

        String file = System.getenv()["WEBSTORE_CONFIG_FILE"]
        WebStore ws = new WebStore(new File(file))
        println "Webstore: $ws"
        ws.setDefaultCompressMode(WebStore.ZLIB)
        Volume[] volumes = ws.getVolumes(WebStore.WRITABLE)
        Volume volume 
        volumes.each{v -> 
           if (v.volId().equalsIgnoreCase(SaskiaWebstore.VOLUME_SDOC)) volume = v	
        }
        println "Volume: $volume"
        
        int limit = 100
        int counter = 0
        int remaining = 0
        boolean first = true
        int processed_ok = 0
        int processed_ko = 0
        
        List batch
        
        while ( first || remaining > 0 ) {
            batch = []
            if (first) {
                println "Starting with a batch of ${limit} docs."
                first = false
            } else {
                println "Getting batch ${counter} to ${limit+counter}, ${remaining} docs remaining."
            }
         
            String select = "SELECT SQL_CALC_FOUND_ROWS sdoc_id, sdoc_original_id, sdoc_collection, sdoc_lang, sdoc_webstore, sdoc_html FROM "+
                    " source_doc WHERE sdoc_collection=? AND sdoc_webstore IS NULL LIMIT $limit UNION SELECT FOUND_ROWS(), '%%%TOTAL%%%', 0, '', '', ''"
           
            db.getDB().eachRow(select, [collection.col_id], {row -> 
                
                if (row['sdoc_original_id'] == "%%%TOTAL%%%") {
                    remaining = (int)row['sdoc_id']	 
                } else {            
                     java.sql.Blob blob = row.getBlob('sdoc_html')
                     if (blob) {
                	 byte[] bdata = blob.getBytes(1, (int) blob.length())
                	 String html = new String(bdata, "UTF-8")
                    
                	 batch << ["sdoc_id":row['sdoc_id'], "sdoc_original_id":row['sdoc_original_id'], 
                              "sdoc_collection":row['sdoc_collection'], 
                              "sdoc_lang":row['sdoc_lang'], "sdoc_html":html]  
                              counter++
                     }
                }
            })

            //println batch
            try {
                db.getDB().withTransaction{
                    batch.each{b -> 
                       try {                                     
                          Content content = new Content(b.sdoc_html.getBytes())
                            // store a content using the regular option
                           Key key = ws.store(content, volume)
                           String keyString = key.toString()
                         //  println "Key: $keyString"
                           db.getDB().executeUpdate("UPDATE source_doc set sdoc_webstore=? where sdoc_id=? and sdoc_collection=? and sdoc_lang=?",
                             [keyString, b.sdoc_id, b.sdoc_collection, b.sdoc_lang] )              
                           processed_ok++                                                                                      
                    }   catch (Exception we) {
                          println we.getMessage()
                          processed_ko++  
                    }                          
                }
            }
         } catch (Exception e) {
             println e.getMessage()
             processed_ko++                                 
         }  
      }
      println "Processed ok: $processed_ok Ko: $processed_ko"     
    }
}
