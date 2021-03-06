package renoir.eval.geoclef.rungeneration.each

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import renoir.eval.geoclef.rungeneration.GeoCLEF_Baseline_NoQE_RunGeneration_Test

import renoir.bin.Renoir
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser
import saskia.db.obj.Collection;
/**
 * Check the detection of question types
 * @author Nuno Cardoso
 */
class GeoCLEF_2005_EN_Baseline_NoQE_RunGeneration_Test extends GeoCLEF_Baseline_NoQE_RunGeneration_Test {
    
    static Logger log = Logger.getLogger("UnitTest")
    
    static int year = 2005
    static String lang = "en"
    static int collection_id = 6 // CHAVE_EN	
    
    public GeoCLEF_2005_EN_Baseline_NoQE_RunGeneration_Test() {
	// topics, qrels, runs, collection, lang 
	super("GeoCLEF_${lang.toUpperCase()}_${year}_Baseline_NoQE.query", 
		"qrelsGeoCLEF${lang.toUpperCase()}${year}.txt", 
		"GeoCLEF_${lang.toUpperCase()}_${year}_Baseline_noQE.run", 
				Collection.getFromID(collection_id), lang)
    }
    
    /** Change any parameters here. Build a list of RenoirQuery, then send it */
    void testGenerate() {
	super.generate()
    }
    
   /* void testEvaluate() {
   	super.evaluate()
       }*/
    //trec_eval resources/eval/geoclef/qrels/qrelsGeoCLEFEN2005.txt  resources/eval/geoclef/runs/GeoCLEF_EN_2005_baseline_noQE.run
    // java ireval.Main resources/eval/geoclef/runs/GeoCLEF_EN_2005_baseline_noQE.run resources/eval/geoclef/qrels/qrelsGeoCLEFEN2005.txt

}