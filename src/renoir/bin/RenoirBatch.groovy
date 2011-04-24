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
package renoir.bin

import saskia.bin.Configuration
import saskia.db.obj.Collection;

import org.apache.log4j.Logger
import org.apache.commons.cli.* 
import pt.utl.ist.lucene.LgteIndexSearcherWrapper
import pt.utl.ist.lucene.LgteHits
import org.apache.lucene.index.LgteIsolatedIndexReader
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.index.IndexReader
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormat
import pt.utl.ist.lucene.LgteIndexManager
import pt.utl.ist.lucene.Model 
import rembrandt.bin.Rembrandt
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser


/**
 * @author Nuno Cardoso
 *
 */
class RenoirBatch {
    
    static Logger log = Logger.getLogger("RenoirMain")
    
    static void main(args) {
        
        def conf, conffilepath
        
        Options o = new Options()
        o.addOption("conf", true, "Configuration file")
        o.addOption("col", true, "Collection to be queried")
        o.addOption("input", true, "Input file")
        o.addOption("output", true, "Output file")
        o.addOption("help", false, "Gives this help information")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java renoir.bin.Renoir", o )
            System.exit(0)
        }
        
        if (!cmd.hasOption("conf")) {
            log.info "No configuration file given. Using default configuration file."
            conffilepath = Configuration.defaultconf
        } else {
            conffilepath = cmd.getOptionValue("conf")
        }
      //  log.info "Renoir version ${Rembrandt.getVersion()}. Welcome."
        conf = Configuration.newInstance(conffilepath)
        
        Collection collection 
        if (cmd.hasOption("col")) {
            log.info "Read collection parameter "+cmd.getOptionValue("col")
            try {
                collection = Collection.getFromID(Long.parseLong(cmd.getOptionValue("col")))		
            } catch(Exception e) {
                collection = Collection.getFromName(cmd.getOptionValue("col"))
            }
        } else {
            log.error "Please give the --col option. Exiting."
            System.exit(0) 
        }
        
        if (!collection) {
             log.error "Don't know collection ${cmd.getOptionValue('col')} to parse documents on. Exiting."
             System.exit(0) 
        } 
        
        if (!cmd.hasOption("input")) {
            log.error "Please give --input file. Exiting."
            System.exit(0) 
        } 
        if (!cmd.hasOption("output")) {
            log.error "Please give --output file. Exiting."
            System.exit(0) 
        } 
        File inputfile = new File(cmd.getOptionValue("input"))
        if (!inputfile.exists()) {
            log.error "Input file does not exist. Exiting."
            System.exit(0) 
        }
        File outputfile = new File(cmd.getOptionValue("output"))
        if (outputfile.exists()) {
            log.warn "Output file exists. It will be overwritten."
        }
        outputfile.write("")
        
        /**********/
        /** MAIN **/
        /**********/
        Renoir renoir = RenoirCore.getCore(conf, collection)
        log.info "Starting Renoir Core for index ${renoir.idx_dir}"
        
        inputfile.eachLine{l -> 

            // default values
            int limit = 10
            int offset = 0
            String label = null
            String output = "list"

           
            // this parses the query, gets parameters for RENOIR and for LGTE
            RenoirQuery q = RenoirQueryParser.parse(l)
            Map res = renoir.search(q)
            int total = res['total']
            log.info "Got ${res['total']} docs."
            //println "res: $res" 
            
            // let's check print options
            
            if (q.paramsForRenoir.containsKey("limit")) limit = q.paramsForRenoir['limit']
            if (q.paramsForRenoir.containsKey("offset")) offset = q.paramsForRenoir['offset']
            if (q.paramsForRenoir.containsKey("label")) label = q.paramsForRenoir['label']
            if (q.paramsForRenoir.containsKey("output")) output = q.paramsForRenoir['output']
            
            log.info "limit: $limit offset:$offset label:$label output:$output"
            int count = 1000
            int toplimit = ((offset+limit) > total ? total : (offset+limit))

            /** HEADER **/
            switch(output) {
                case ["snippet", "list"]: 
            //    println "id\tdoc\tscore\tpartial score"
                break
            }
    
            for (int i = offset; i < toplimit; i++) {
                def r = res["result"][i]
                
                switch(output) {
                    case "list":           
                    println "${r['i']}\t${r['docid']}\t${r['score']}\t${r['partialscore']}"
                    break
                    
                    case "trec":
                    outputfile.append "$label Q0 " + r["docid"] + " " + count-- + " " + r["score"] + " GREASE\n"
                    break
                }
            }
            
        }     
    }
}

