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

import saskia.io.*
import org.apache.log4j.*
import org.apache.commons.cli.*
import pt.tumba.webstore.*
import org.apache.log4j.*

/**
 * @author Nuno Cardoso
 * 1 2 7 8
 */
class SourceDocStats {
    	
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
        SaskiaDB db = SaskiaDB.newInstance()


        int limit = 500
		  int total = -1
        int offset = 0
        int remaining = -1
        boolean first = true

		  File f = new File("stats.txt")
		  f.write ("nr\tsdoc_id\tsdoc_id\tsdoc_original_id\tsdoc_webstore\tsize\t"+
			"sdoc_lang\tsdoc_comment\tsdoc_date\tsdoc_doc\tsdoc_proc\n")
		
        while ( first || remaining > 0 ) {
 			  Map map = SourceDoc.listSourceDocs(collection, limit,  offset)

           if (first) {
                println "Starting with a batch of ${limit} docs."
					 total = map.total
                println "total: "+total
					 remaining = total - map.page
					 first = false
            } else {
		 			 remaining -= map.page
                println "Got ${map.page} docs to work (${offset} to ${limit+offset}), ${remaining} docs remaining."
            }
         
            map.result.each{sdoc -> 
               f.append (offset+"\t"+sdoc.sdoc_id+"\t"+sdoc.sdoc_original_id+"\t"+sdoc.sdoc_webstore+"\t"+
					sdoc.sdoc_content.size()+"\t"+sdoc.sdoc_lang+"\t"+sdoc.sdoc_comment+"\t"+sdoc.sdoc_date+"\t"+
					sdoc.sdoc_doc+"\t"+sdoc.sdoc_proc+"\n")
					offset++
				}
			
			}	
      	println "Done."     
    }
}
