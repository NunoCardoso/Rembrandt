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
package renoir.test.query

import groovy.util.GroovyTestCase
import renoir.obj.*
import renoir.suggestion.*
import org.apache.log4j.Logger


/**
 * @author Nuno Cardoso
 *
 */
class QueryTagTest extends GroovyTestCase {
    
    def Logger log = Logger.getLogger("UnitTest")
    
    void test1() {
        String x1 = '[{"name": "Porque", "type": "WQ", "desc": undefined, "ground": undefined}]'
        List<QueryTag> l1 = QueryTag.parseTags(x1)
        println QueryTag.toJSON(l1)
        assert '[{"name":"Porque", "type":"WQ"}]' == QueryTag.toJSON(l1)
        String x2 = '[{"name": "Portugal", "type": "NE", "desc": "LOCAL HUMANO DIVISAO", "ground": 2535235}]'
        List<QueryTag> l2 = QueryTag.parseTags(x2)
        println QueryTag.toJSON(l2)
        assert '[{"name":"Portugal", "type":"NE", "ground":"2535235", "category": "LOCAL HUMANO DIVISAO"}]' == QueryTag.toJSON(l2)
        String x3 = '[{"name": "Chips", "type": "NE", "desc": "EM", "ground": "19396"}]'
        List<QueryTag> l3 = QueryTag.parseTags(x3)
        println QueryTag.toJSON(l3) 
        assert '[{"name":"Chips", "type":"NE", "ground":"19396", "category": "EM"}]' ==  QueryTag.toJSON(l3)         
    }
}
