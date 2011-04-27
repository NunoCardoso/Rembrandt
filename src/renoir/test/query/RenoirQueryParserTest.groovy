
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
import org.apache.log4j.Logger
import saskia.bin.Configuration
import renoir.obj.*
import pt.utl.ist.lucene.Globals

/**
 * @author Nuno Cardoso
 *
 */
 class RenoirQueryParserTest extends GroovyTestCase{
        
    static Logger log = Logger.getLogger("SaskiaTest")
    Configuration conf = Configuration.newInstance()
    
    public RenoirQueryParserTest() {}
        
    void testParse() {
        String x = "qe:BRF search:true explain:true stem:true presidente "+
        "presidente^0.5 \"Aníbal\" \"Cavaco Silva\" "+
        "ne-PESSOA:\"Cavaco Silva\"^2 ne-PESSOA:Aníbal^0.1 "+
        "woeid:352526 entity:An%C3%ADbal_Cavaco_Silva "+
        "model:BM25 ne-PESSOA-weight:2 contents-weight:1 woeid-weight:0.3 time:1998*"
        
// notar que eu consigo ler time, mas converto para tg, isto porque internamente time é reservado pelo LGTE.

        String rewritten_x = "qe:BRF search:true explain:true stem:true model:BM25 "+
        "ne-PESSOA-weight:2f contents-weight:1f woeid-weight:0.3f "+
			"contents:presidente contents:presidente^0.5 "+
        "contents:\"Aníbal\" contents:\"Cavaco Silva\" ne-PESSOA:\"Cavaco Silva\"^2.0 "+
        "ne-PESSOA:Aníbal^0.1 woeid:352526 entity:An%C3%ADbal_Cavaco_Silva tg:1998*"

        
        RenoirQuery q = RenoirQueryParser.parse(x) 
 
		log.debug "params for Renoir: "+q.paramsForRenoir
		log.debug "params for LGTE: "+q.paramsForLGTE
		log.debug "Query sentence (= terms): "+q.sentence
        
		assert q.paramsForRenoir['qe'] == 'BRF'
 		assert q.paramsForRenoir['search'] == 'true'
 		assert q.paramsForRenoir['explain'] == 'true'
		assert q.paramsForRenoir['stem'] == 'true'
        
 		assert q.paramsForLGTE['model'] == 'BM25'

 		assert q.paramsForQueryConfiguration['model.field.boost.ne-PESSOA'] == '2f'
		assert q.paramsForQueryConfiguration['model.field.boost.contents'] == '1f'
 		assert q.paramsForQueryConfiguration['model.field.boost.woeid'] == '0.3f'
       
		assert q.sentence[0].text == 'presidente'
		assert q.sentence[0].phraseBIO == 'O'    
		assert q.sentence[0].field == conf.get("saskia.index.contents_field","contents") // contents
      
		assert q.sentence[1].text == 'presidente'
		assert q.sentence[1].phraseBIO == 'O'    
		assert q.sentence[1].field == conf.get("saskia.index.contents_field","contents")  // contents
		assert q.sentence[1].weight == 0.5f // contents

		assert q.sentence[2].text == 'Aníbal'
		assert q.sentence[2].phraseBIO == 'B'    
		assert q.sentence[2].field == conf.get("saskia.index.contents_field","contents")  // contents

		assert q.sentence[3].text == 'Cavaco' 
		assert q.sentence[3].phraseBIO == 'B' 
		assert q.sentence[3].field == conf.get("saskia.index.contents_field","contents")  // contents
    
		assert q.sentence[4].text == 'Silva' 
		assert q.sentence[4].phraseBIO == 'I' 
		assert q.sentence[4].field == conf.get("saskia.index.contents_field","contents")  // contents

		assert q.sentence[5].text == 'Cavaco' 
		assert q.sentence[5].phraseBIO == 'B' 
		assert q.sentence[5].field == 'ne-PESSOA'
		assert q.sentence[5].weight == 2.0f
        
		assert q.sentence[6].text == 'Silva' 
		assert q.sentence[6].phraseBIO == 'I' 
		assert q.sentence[6].field == 'ne-PESSOA'
		assert q.sentence[6].weight == 2.0f

		assert q.sentence[7].text == 'Aníbal'
		assert q.sentence[7].field == 'ne-PESSOA'
		assert q.sentence[7].weight == 0.1f
	 
		assert q.sentence[8].text == '352526'
        assert q.sentence[8].field == 'woeid'
            
		assert q.sentence[9].text == 'An%C3%ADbal_Cavaco_Silva'
        assert q.sentence[9].field == 'entity'

		assert q.sentence[10].text == '1998*'
        assert q.sentence[10].field == 'tg'

        println "Generated String: "+q.toString()
        assert q.toString() == rewritten_x    
        
    }
    
    void testParse2() {
	 String x = "label:006 qe:brf search:false limit:1000 offset:0 model:bm25 "+
	 "QE.method:rocchio QE.decay:0.15 QE.doc.num:10 QE.term.num:16 QE.rocchio.alpha:1 "+
	 "QE.rocchio.beta:0.75 bm25.idf.policy:standard bm25.k1:1.2 bm25.b:0.75 "+
	 "contents:oil^94.78931 "
	 
	 RenoirQuery q = RenoirQueryParser.parse(x) 
	 
	 log.debug "params for Renoir: "+q.paramsForRenoir
	 log.debug "params for LGTE: "+q.paramsForLGTE
	 log.debug "Query sentence (= terms): "+q.sentence
	        
	 assert q.paramsForRenoir['label'] == '006'
	 assert q.paramsForRenoir['qe'] == 'brf'
	 assert q.paramsForRenoir['search'] == 'false'
	 assert q.paramsForRenoir['limit'] == 1000	 
         assert q.paramsForRenoir['offset'] == 0        
	 assert q.paramsForLGTE['model'] == 'bm25'
	 assert q.paramsForQueryConfiguration['QE.method'] == 'rocchio'
	 assert q.paramsForQueryConfiguration['QE.decay'] == '0.15'	 
	 assert q.paramsForQueryConfiguration['QE.doc.num'] == '10'
	 assert q.paramsForQueryConfiguration['QE.term.num'] == '16'
	 assert q.paramsForQueryConfiguration['QE.rocchio.alpha'] == '1'
	 assert q.paramsForQueryConfiguration['QE.rocchio.beta'] == '0.75'
	 assert q.paramsForQueryConfiguration['bm25.idf.policy'] == 'standard'
	 assert q.paramsForQueryConfiguration['bm25.k1'] == '1.2'
	 assert q.paramsForQueryConfiguration['bm25.b'] == '0.75'
	
	 assert q.sentence[0].text == 'oil' 
	 assert q.sentence[0].phraseBIO == 'O' 
	 assert q.sentence[0].field == 'contents'
	 assert q.sentence[0].weight == 94.78931f
		 
			
    }

    void testParse3() {
	
		// woeid and time have a filter role, so they don't appear on the query string per se, but as {filter}"
	  String x = "qe:BRF search:true explain:true stem:true presidente "+
        "presidente^0.5 \"Aníbal\" \"Cavaco Silva\" "+
        "ne-PESSOA:\"Cavaco Silva\"^2 ne-PESSOA:Aníbal^0.1 "+
        "woeid:352526 entity:An%C3%ADbal_Cavaco_Silva "+
        "model:BM25 ne-PESSOA-weight:2 contents-weight:1 woeid-weight:0.3 time:1998* "+
		  "entity-filter:no woeid-filter:yes time-filter:yes"

        String rewritten_x = "qe:BRF search:true explain:true stem:true entity-filter:no woeid-filter:yes "+
			"tg-filter:yes model:BM25 ne-PESSOA-weight:2f contents-weight:1f woeid-weight:0.3f "+
			"woeid:352526 tg:1998* contents:presidente contents:presidente^0.5 "+
			"contents:\"Aníbal\" contents:\"Cavaco Silva\" ne-PESSOA:\"Cavaco Silva\"^2.0 ne-PESSOA:Aníbal^0.1 "+
			"entity:An%C3%ADbal_Cavaco_Silva"

	    // println "Generated String: "+q.toString()
        //assert q.toString() == rewritten_x    
			
    }
}
