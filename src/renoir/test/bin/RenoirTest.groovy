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
package renoir.test.bin

import org.apache.log4j.*
import org.apache.commons.cli.*
import saskia.bin.Configuration
import rembrandt.bin.Rembrandt
import pt.utl.ist.lucene.LgteHits
import renoir.bin.Renoir

/**
 * @author Nuno Cardoso
 *
 */

class RenoirTest {
    
    static Logger log = Logger.getLogger("SaskiaTest")
    
    public static void main(String[] args) {
         
	Renoir renoir
        Options o = new Options()
        String fileseparator = System.getProperty("file.separator")
        Configuration conf 
        
        o.addOption("col", true, "Collection name or ID")
        o.addOption("lang", true, "Collection language")
        o.addOption("help", false, "Gives this help information")
        o.addOption("indexdir", false, "directory of the index")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        Collection collection 
        def conffilepath
        
        /** CHECK CONF **/
        if (!cmd.hasOption("conf")) {
            log.info "No configuration file given. Using default configuration file."
            conffilepath = Configuration.defaultconf
        } else {
            conffilepath = cmd.getOptionValue("conf")
        }
     //   log.info "RenoirTest version ${Rembrandt.getVersion()}. Welcome."
        conf = Configuration.newInstance(conffilepath)

         String indexdir = conf.get("rembrandt.home.dir")
        if (!indexdir.endsWith(fileseparator)) indexdir += fileseparator
        indexdir += Renoir.mainIndexDir  
        
        /** CHECK COLLECTION **/
        if (cmd.hasOption("col")) {            
            try {
                collection = Collection.getFromID(Long.parseLong(cmd.getOptionValue("col")))		
            } catch(Exception e) {
                collection = Collection.getFromName(cmd.getOptionValue("col"))
            }
            if (!collection) {
                log.error "Don't know collection ${cmd.getOptionValue('col')} to parse documents on. Exiting."
                System.exit(0) 
            } 
        }
        
        if (collection) {
            log.info "Using collection $collection"  
            indexdir += fileseparator+Renoir.mainCollectionPrefix+"-"+collection.col_id
        } else {
            log.info "No collection given, using default collection in conf file"
            String conf_default_index_dir = conf.get(
                    "renoir.collection.index.default")
            if  (!conf_default_index_dir) {
                log.fatal "No default collection given, either on arg or in conf file. Exiting."
                System.exit(0)
            } else {
                indexdir += fileseparator+conf_default_index_dir
            }
        }
        
       /** CHECK INDEXES **/
        log.info "Base index dir: $indexdir" 
        log.info "Checking for term index... " + (new File(indexdir+fileseparator+"term-index").isDirectory())
        log.info "Checking for NE index... " + (new File(indexdir+fileseparator+"ne-index").isDirectory())
        log.info "Checking for geo index... " + (new File(indexdir+fileseparator+"geo-index").isDirectory())
        log.info "Checking for time index... " + (new File(indexdir+fileseparator+"time-index").isDirectory())
           
        renoir = new Renoir(conf, indexdir)
        
        String query = "x"
        while (query) {
            print "query? "
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
            query = input.readLine().trim()
            if (query) {
                println "Querying $query"
                LgteHits results = renoir.query(query)
                if (!results) println "No docs found."
                else {
                    int total = results.length()
                    println "Query $query got $total docs."
                    int show = (total > 10 ? 10 : total)
                    for(int i=0; i<show; i++) {
                        println "#$i: "+results.doc(0).get("id") + " - " + results.score(0)
                    }
                }
            }
        }     
    }
}
      

