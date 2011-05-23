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

import saskia.bin.Configuration
import saskia.db.obj.Collection;
import renoir.bin.Renoir
import renoir.obj.*
/**
 * @author Nuno Cardoso
 *
 */
class NTCIR_All_Col7_Semantic_SQR2_StandardBM25_NoStem_RunGeneration_Test extends GroovyTestCase {
 
    Configuration conf
    static Logger log = Logger.getLogger("RenoirTest")
	 static String col = "Col7"
	 static int coln = 7	
	 static String type ="Semantic_SQR2"
	 static String model ="StandardBM25"
	 static String dostem ="NoStem"	
    static Boolean stem = false
	 
    public NTCIR_All_Col7_Semantic_SQR2_StandardBM25_NoStem_RunGeneration_Test() {}
    
    public NTCIR_Baseline_NoQE_RunGeneration_Test create(year, collection_id, lang, weight) {
		return new NTCIR_Baseline_NoQE_RunGeneration_Test(
		"NTCIR_${lang.toUpperCase()}_${year}_${type}.query", 
		"NTCIR_${lang.toUpperCase()}_${year}_relax.qrel", 
		"NTCIR_${lang.toUpperCase()}_${year}_${col}_${type}_${model}_${dostem}_${weight}.run", 
		Collection.getFromID(collection_id), lang, stem)
    }
    
     void testNTCIR2010EN() {
    	NTCIR_Baseline_NoQE_RunGeneration_Test o = create(2010, coln, 'en', "_10")
	   o.generateWithWeight(1.0f, 1.0f, 1.0f)
	 	o = create(2010, coln, 'en', "_05")
	   o.generateWithWeight(1.0f, 0.5f, 0.5f)	
	 	o = create(2010, coln, 'en', "_02")
	   o.generateWithWeight(1.0f, 0.2f, 0.2f)	
	 	o = create(2010, coln, 'en', "_01")
	   o.generateWithWeight(1.0f, 0.1f, 0.1f)	
	 	o = create(2010, coln, 'en', "_001")
	   o.generateWithWeight(1.0f, 0.01f, 0.01f)	
	 	o = create(2010, coln, 'en', "_0001")
	   o.generateWithWeight(1.0f, 0.001f, 0.001f)	
    }
 
}
