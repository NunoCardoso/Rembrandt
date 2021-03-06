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

package renoir.eval.ntcir.querygeneration

import org.apache.log4j.Logger

import saskia.bin.Configuration
import renoir.bin.Renoir
import renoir.obj.*

/**
 * @author Nuno Cardoso
 * This generates the BRF version of queries 
 */
class NTCIR_All_Col7_Statistic_BRF_StandardBM25_NoStem_QueryGeneration_Test extends GroovyTestCase {

    Configuration conf = Configuration.newInstance()
    static Logger log = Logger.getLogger("RenoirTest")
      
	 static String model = "StandardBM25"
	 static String type = "Statistic_BRF"
	 static String dostem = "NoStem"
	 static boolean stem = false
	 static String col = "Col7"
    static int coln = 7

    public NTCIR_All_Col7_Statistic_BRF_StandardBM25_NoStem_QueryGeneration_Test() {}
    
    public NTCIR_Statistic_BRF_QueryGeneration_Test create(year, collection_id, lang) {

	String homedir = conf.get("rembrandt.home.dir",".")
	String fileseparator = System.getProperty("file.separator")
	return new NTCIR_Statistic_BRF_QueryGeneration_Test(
		"NTCIR_${lang.toUpperCase()}_${year}_topics.txt", 
		"NTCIR_${lang.toUpperCase()}_${year}_${col}_${type}_${model}_${dostem}.query", 
		homedir+fileseparator+"index"+fileseparator+"col-"+collection_id, lang, stem)
    }
    
    void testNTCIR2010EN() {
      	NTCIR_Statistic_BRF_QueryGeneration_Test o = create(2010, coln, 'en')
	// no opt parameters for this one
	   o.generate()
    } 
  
}
