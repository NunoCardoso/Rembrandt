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

package renoir.eval.geoclef.querygeneration.each


import org.apache.log4j.Logger

import renoir.obj.*
import renoir.eval.geoclef.querygeneration.GeoCLEF_Baseline_NoQE_QueryGeneration_Test
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 *
 */
class GeoCLEF_2008_PT_Baseline_NoQE_QueryGeneration_Test extends GeoCLEF_Baseline_NoQE_QueryGeneration_Test {

	static int year = 2008
	static String lang = "pt"

	public GeoCLEF_2008_PT_Baseline_NoQE_QueryGeneration_Test() {
		super("GeoCLEF_${lang.toUpperCase()}_${year}_simple_topic.txt",
		"GeoCLEF_${lang.toUpperCase()}_${year}_baseline_query.txt",
		lang)
	}

	void testGenerate() {
		log.debug "Starting query generation..."
		super.generate()
		log.debug "Query generation done."
	}
}
