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

package rembrandt.test.rules

import rembrandt.bin.RembrandtCorePTforHAREM
import saskia.bin.Configuration
import rembrandt.obj.Document
import rembrandt.obj.NamedEntity
import rembrandt.obj.Sentence
import rembrandt.io.UnformattedReader
import rembrandt.io.RembrandtStyleTag


import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 * Tester for LocalRulesPT
 */
public class ValueRulesPTTest extends RulesTest {

	static String path = Configuration.newInstance().get("rembrandt.home.dir",".")

	public ValueRulesPTTest() {
		super("pt","harem",
		"ExternalEvidencePT_value.txt",
		"ExternalEvidencePT_Value_output.txt"

		)
	}

	void testCompareDocs() {
		super.testCompareDocs()
	}
}