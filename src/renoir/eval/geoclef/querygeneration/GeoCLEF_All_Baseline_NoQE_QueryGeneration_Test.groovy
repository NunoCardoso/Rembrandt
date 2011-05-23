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

package renoir.eval.geoclef.querygeneration

import org.apache.log4j.Logger

import saskia.bin.Configuration
import renoir.bin.Renoir
import renoir.obj.*

/**
 * @author Nuno Cardoso
 *
 */
class GeoCLEF_All_Baseline_NoQE_QueryGeneration_Test extends GroovyTestCase {

    Configuration conf
    static Logger log = Logger.getLogger("RenoirTest")
    
    public GeoCLEF_All_Baseline_NoQE_QueryGeneration_Test() {}
    
    public GeoCLEF_Baseline_NoQE_QueryGeneration_Test create(year, lang) {
	return new GeoCLEF_Baseline_NoQE_QueryGeneration_Test(
		"GeoCLEF_${lang.toUpperCase()}_${year}_simple_topic.txt", 
		"GeoCLEF_${lang.toUpperCase()}_${year}_Baseline_NoQE.query", 
		lang)
    }
    
    void testGeoCLEF2005EN() {
		GeoCLEF_Baseline_NoQE_QueryGeneration_Test o = create(2005, 'en')
		o.generate()
    }
    
    void testGeoCLEF2006EN() {
		GeoCLEF_Baseline_NoQE_QueryGeneration_Test o = create(2006, 'en')
		o.generate()
    }
    void testGeoCLEF2007EN() {
		GeoCLEF_Baseline_NoQE_QueryGeneration_Test o = create(2007, 'en')
		o.generate()
    }
    void testGeoCLEF2008EN() {
		GeoCLEF_Baseline_NoQE_QueryGeneration_Test o = create(2008, 'en')
		o.generate()
    }
    void testGeoCLEF2006PT() {
		GeoCLEF_Baseline_NoQE_QueryGeneration_Test o = create(2006, 'pt')
		o.generate()
    }
    void testGeoCLEF2007PT() {
		GeoCLEF_Baseline_NoQE_QueryGeneration_Test o = create(2007, 'pt')
		o.generate()
    }
    void testGeoCLEF2008PT() {
		GeoCLEF_Baseline_NoQE_QueryGeneration_Test o = create(2008, 'pt')
		o.generate()
    }
}
