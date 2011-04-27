
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

package renoir.eval.ntcir.rungeneration

import org.apache.log4j.Logger
import ireval.SetRetrievalEvaluator
import ireval.RetrievalEvaluator.Document
import ireval.RetrievalEvaluator.Judgment
import ireval.Main

import saskia.bin.Configuration
import saskia.db.obj.Collection;
import renoir.bin.Renoir
import renoir.bin.RenoirCore
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser

/**
 * @author Nuno Cardoso
 *
 */
class NTCIR_RunGeneration_Test extends GroovyTestCase {

    Configuration conf
    static Logger log = Logger.getLogger("RenoirTest")
    static String mainTask = "ntcir"

    public List<RenoirQuery> queries = []
    String homedir, queryfile, qrelfile, runfile, logfile, indexdir  
    public Renoir renoir
    File logf, runf 
    int limit = 1000
    int offset = 0
    
    public NTCIR_RunGeneration_Test(String query_file, String qrel_file, String run_file, 
		Collection collection, String lang,	Boolean stem) {
    
 // initialize by reading the topics and qrels
	conf = Configuration.newInstance()
	String fileseparator = System.getProperty("file.separator")
	homedir = conf.get("rembrandt.home.dir",".")
    
	queryfile = homedir+fileseparator+"resources"+fileseparator+"eval"+
            fileseparator+mainTask+fileseparator+"queries"+fileseparator+query_file
        qrelfile = homedir+fileseparator+"resources"+fileseparator+"eval"+
            fileseparator+mainTask+fileseparator+"qrels"+fileseparator+qrel_file
        runfile = homedir+fileseparator+"resources"+fileseparator+"eval"+
            fileseparator+mainTask+fileseparator+"runs"+fileseparator+run_file
        logfile = homedir+fileseparator+"resources"+fileseparator+"eval"+
            fileseparator+mainTask+fileseparator+"logs"+fileseparator+run_file+".log"
    // col-6 is for CHAVE EN collection
       indexdir = homedir+fileseparator+"index"+fileseparator+"col-"+collection.col_id
      
       log.info "Loading index in $indexdir"
    
       	File queryf = new File(queryfile)
	if (!queryf.exists()) throw new IllegalStateException("Topic file $queryfile not found!")
	queryf.eachLine{l -> 
		if (!l.startsWith(/#/)) queries << RenoirQueryParser.parse(l.trim())
	}
	
	File qrelf = new File(qrelfile)
	if (!qrelf.exists()) throw new IllegalStateException("Qrel file $qrelfile not found!")
 
	runf = new File(runfile)
	if (runf.exists()) {
	    print "Overwritting existing file $runfile (y/n)? > "
	    BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
	    String ynae
	    while (!ynae || (ynae && !(ynae.equalsIgnoreCase("y") ||ynae.equalsIgnoreCase("n") ))) {
		ynae = input.readLine().trim()
	    }
	    if (ynae.equalsIgnoreCase("n")) {
		log.info "Exiting then."
		System.exit(0)
	    }
	}
    
	logf = new File(logfile)
    
	renoir = RenoirCore.getCore(conf, collection, indexdir, stem)
 
    }
    
    void submit(RenoirQuery rq) {
              
        
            Map res = renoir.search(rq)
                
            logf.append "Initial query: ${res['initial_query_string']}\n"
            log.info "Initial query: ${res['initial_query_string']}"
            logf.append "Final query: ${res['final_query_string']}\n"
            log.info "Final query: ${res['final_query_string']}"
    
            int total = res["total"]
            int time = res["time"]                
                
            logf.append "Got $total docs in $time msecs.\n"
            log.info "Got $total docs in $time msecs."
                                
            int count = 1000
            String label = rq.paramsForRenoir['label']
                                  
            int from  = res["nr_first_result"]
            int to = res["nr_last_result"]
                             
            for (int i = from; i < to; i++) {
                Map result = res["result"][i]
                runf.append "$label Q0 " + result["doc_original_id"] + " " + count-- + " " + result["score"] + " LABEL\n"
            }
            logf.append "===\n"
        
    }
    
    void evaluate() {
	 
	TreeMap< String, ArrayList<Document> > ranking = Main.loadRanking( runfile)
	TreeMap< String, ArrayList<Judgment> > judgments = Main.loadJudgments( qrelfile )

	SetRetrievalEvaluator setEvaluator = Main.create( ranking, judgments )
	Main.singleEvaluation( setEvaluator )
    }
 
}
