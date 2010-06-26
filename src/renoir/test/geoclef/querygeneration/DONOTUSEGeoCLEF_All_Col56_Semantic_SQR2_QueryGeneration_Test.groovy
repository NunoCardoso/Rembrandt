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

package renoir.test.geoclef.querygeneration

import org.apache.log4j.Logger

import saskia.bin.Configuration
import renoir.bin.Renoir
import renoir.obj.*

/**
 * @author Nuno Cardoso
 * This generates the BRF version of queries 
 */
class DONOTUSEGeoCLEF_All_col56_Semantic_SQR2_OptimisedBM25_QueryGeneration_Test extends GroovyTestCase {

    Configuration conf = Configuration.newInstance()
    static Logger log = Logger.getLogger("RenoirTest")
    
    public DONOTUSEGeoCLEF_All_col56_Semantic_SQR2_OptimisedBM25_QueryGeneration_Test() {}
    
    public GeoCLEF_Semantic_SQR2_QueryGeneration_Test create(year, collection_id, lang) {
		String homedir = conf.get("rembrandt.home.dir",".")
		String fileseparator = System.getProperty("file.separator")
		return new GeoCLEF_Semantic_SQR2_QueryGeneration_Test(
			"GeoCLEF_${lang.toUpperCase()}_${year}_simple_topic.txt", 
			"GeoCLEF_${lang.toUpperCase()}_${year}_Col56_Semantic_SQR2_OptimisedBM25.query", 
			homedir+fileseparator+"index"+fileseparator+"col-"+collection_id, lang)
    }
    
    void testGeoCLEF2005EN() {
	GeoCLEF_Semantic_SQR2_QueryGeneration_Test o = create(2005, 6, 'en')
	// no opt parameters for this one
	o.generate()
    } 
    void testGeoCLEF2006EN() {
	GeoCLEF_Semantic_SQR2_QueryGeneration_Test o = create(2006, 6,'en')
      	//k1, b, term, doc
	o.generate(1.6d, 0.3d)
    }
    void testGeoCLEF2007EN() {
	GeoCLEF_Semantic_SQR2_QueryGeneration_Test o = create(2007, 6,'en')
	o.generate(1.4d, 0.65d)
    }
    void testGeoCLEF2008EN() {
	GeoCLEF_Semantic_SQR2_QueryGeneration_Test o = create(2008, 6,'en')
	o.generate(1.6d, 0.65d)
    }
    void testGeoCLEF2006PT() {
	GeoCLEF_Semantic_SQR2_QueryGeneration_Test o = create(2006, 5,'pt')
	o.generate(0.4d, 0.4d)
    }
    void testGeoCLEF2007PT() {
	GeoCLEF_Semantic_SQR2_QueryGeneration_Test o = create(2007, 5,'pt')
	o.generate(0.9d, 0.4d)
    }
    void testGeoCLEF2008PT() {
	GeoCLEF_Semantic_SQR2_QueryGeneration_Test o = create(2008, 5,'pt')
	o.generate(1.2d, 0.35d)
    }
}
