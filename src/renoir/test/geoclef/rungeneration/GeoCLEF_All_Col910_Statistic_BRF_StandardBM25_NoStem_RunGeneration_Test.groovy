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

package renoir.test.geoclef.rungeneration


import org.apache.log4j.Logger

import saskia.bin.Configuration
import saskia.db.obj.Collection;
import renoir.bin.Renoir
import renoir.obj.*
/**
 * @author Nuno Cardoso
 *
 */
class GeoCLEF_All_Col910_Statistic_BRF_StandardBM25_RunGeneration_NoStem_Test extends GroovyTestCase {

    Configuration conf
    static Logger log = Logger.getLogger("RenoirTest")
	 static String col = "Col910"
	 static int col_pt = 9
	 static int col_en = 10	
	 static String type ="Statistic_BRF"
	 static String model ="StandardBM25"
	 static String stem ="NoStem"	
    static boolean dostem = false
	 
    public GeoCLEF_All_Col910_Statistic_BRF_StandardBM25_RunGeneration_NoStem_Test() {}
    
    public GeoCLEF_Baseline_NoQE_RunGeneration_Test create(year, collection_id, lang) {
		return new GeoCLEF_Baseline_NoQE_RunGeneration_Test(
		"GeoCLEF_${lang.toUpperCase()}_${year}_${col}_${type}_${model}_${stem}.query", 
		"qrelsGeoCLEF${lang.toUpperCase()}${year}.txt", 
		"GeoCLEF_${lang.toUpperCase()}_${year}_${col}_${type}_${model}_${stem}.run", 
		Collection.getFromID(collection_id), lang, dostem)
    }
    
    void testGeoCLEF2005EN() {
		GeoCLEF_Baseline_NoQE_RunGeneration_Test o = create(2005, col_en, 'en')
		o.generate()
    }
    
    void testGeoCLEF2006EN() {
	GeoCLEF_Baseline_NoQE_RunGeneration_Test o = create(2006, col_en, 'en')
	o.generate()
    }
    void testGeoCLEF2007EN() {
	GeoCLEF_Baseline_NoQE_RunGeneration_Test o = create(2007, col_en, 'en')
	o.generate()
    }
    void testGeoCLEF2008EN() {
	GeoCLEF_Baseline_NoQE_RunGeneration_Test o = create(2008, col_en, 'en')
	o.generate()
    }
    void testGeoCLEF2006PT() {
	GeoCLEF_Baseline_NoQE_RunGeneration_Test o = create(2006, col_pt, 'pt')
	o.generate()
    }
    void testGeoCLEF2007PT() {
	GeoCLEF_Baseline_NoQE_RunGeneration_Test o = create(2007, col_pt, 'pt')
	o.generate()
    }
    void testGeoCLEF2008PT() {
	GeoCLEF_Baseline_NoQE_RunGeneration_Test o = create(2008, col_pt, 'pt')
	o.generate()
    }
}
