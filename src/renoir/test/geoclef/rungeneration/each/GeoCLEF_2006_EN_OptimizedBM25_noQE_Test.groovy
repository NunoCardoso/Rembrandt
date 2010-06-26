package renoir.test.geoclef.rungeneration.each


import ireval.SetRetrievalEvaluator;
import ireval.RetrievalEvaluator.Document;
import ireval.RetrievalEvaluator.Judgment;
import renoir.test.geoclef.rungeneration.GeoCLEF_Baseline_NoQE_RunGeneration_Test
import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import saskia.io.Collection
import renoir.bin.Renoir
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser

/**
 * Check the detection of question types
 * @author Nuno Cardoso
 */
class GeoCLEF_2006_EN_OptimizedBM25_noQE_Test extends GeoCLEF_Baseline_NoQE_RunGeneration_Test {
    
    static Logger log = Logger.getLogger("UnitTest")

    static int year = 2006
    static String lang = "en"
    static String desc = "OptimizedBM25_noQE"
    static int collection_id = 6 // CHAVE_EN	
	
    public GeoCLEF_2006_EN_OptimizedBM25_noQE_Test() {
	// topics, qrels, runs, collection, lang 
	super("GeoCLEF_${lang.toUpperCase()}_${year}_Baseline_OptimizedBM25_NoQE.query", 
		"qrelsGeoCLEF${lang.toUpperCase()}${year}.txt", 
		"GeoCLEF_${lang.toUpperCase()}_${year}_Baseline_OptimizedBM25_NoQE.query", 
				Collection.getFromID(collection_id), lang)
    }
    
    /** Change any parameters here. Build a list of RenoirQuery, then send it */
    void testGenerateRuns() {
	super.headline("=== GeoCLEF_${lang.toUpperCase()}_${year}_${desc} ===")
	super.runReset()
    
	List<RenoirQuery> rqs = []
	int limit = 1000
	int offset = 0
	super.queries.each{q -> 

            RenoirQuery query = RenoirQueryParser.parse(q)
            // let's tell it to do no QE
            query.paramsForRenoir['qe']="no"
            query.paramsForRenoir['limit'] = limit
            query.paramsForRenoir['offset'] = offset  
            
            query.paramsForLGTE['model'] = "bm25"
            
            query.paramsForQueryConfiguration["bm25.idf.policy"] = "standard"
            query.paramsForQueryConfiguration["bm25.k1"] = "1.6d"
            query.paramsForQueryConfiguration["bm25.b"] = "0.3d"
                
            query.paramsForRenoir['limit'] = limit
            query.paramsForRenoir['offset'] = offset  
            
            rqs << query	
	}
	
	super.generateRun(rqs)
    }
         
        //Main eval_main = new ireval.Main()
    /*    TreeMap< String, ArrayList<Document> > ranking = Main.loadRanking( answers);
        TreeMap< String, ArrayList<Judgment> > judgments = Main.loadJudgments( qrelfile);
        
        SetRetrievalEvaluator setEvaluator = Main.create( ranking, judgments );
        Main.singleEvaluation( setEvaluator );
      */  
        
}