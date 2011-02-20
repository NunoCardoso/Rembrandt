
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

package renoir.test
import org.apache.log4j.Logger

import saskia.bin.Configuration
import renoir.bin.Renoir
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser

/**
 * @author Nuno Cardoso
 * Use LGTE and Renoir class to check if retrieval results, scores etc are OK

In Col-8 there are 129 docs. 

terms: there are 44 documents that have 'Portugal' as a term.
3,4,8,11,17,19,22,24,25,26,33,34,35,38,39,40,41,42,48,51,53,55,59,60,63,65,66,68,
69,75,77,78,83,84,86,88,90,92,93,112,113,117,125,126.
in Luke, the top10 rank using term index is: 42,40,59,51,78,83,75,53,77,88

there is 2 docs with cavaco: 46, 60. In the 46, there is no 'portugal'
there is 1 doc with '+cavaco +portugal': 60
there is 3 docs with 'PSD': 48,49,60

ne:
in NE-LOCAL-HUMANO-PAIS: there are 39 docs with Portugal on it:
3,4,8,11,17,19,22,24,25,26,33,34,35,38,39,40,42,48,51,53,59,60,63,65,68,
75,77,78,83,84,86,88,90,92,112,113,117,125,126
in NE-PESSOA-INDIVIDUAL:"Cavaco Silva" dá dois docs: 46, 60  (usar SimpleAnalyzer)

entity:
'Portugal' gives 39 docs. Much likely the same from NE.
'An%C3%ADbal_Cavaco_Silva' gives 1 document, 46

woeid: Portugal's woeid is 23424925 gives 55 docs:
note that some of them are Portuguese cities that were expanded to ancestors.
3,4,8,11,16,17,19,20,22,23,24,25,26,32,33,34,35,36,38,39,41,40,42,48,49,51,53,54,56,57,
59,60,63,65,66,68,69,73,75,77,78,79,81,83,84,86,88,90,92,102,112,113,117,125,126

time:
2004 appears in 10 documents: 16,27,55,65,72,80,86,103,120,126

 */
class LGTENormalBM25Test extends GroovyTestCase {

    Configuration conf
    static Logger log = Logger.getLogger("RenoirTest")

	// CD of second HAREM
    static String collection_dir = "col-8"
	static String lang = "pt"
	
    Renoir renoir
    int limit = 1000
    int offset = 0
    
    public LGTENormalBM25Test() {
    
 		// initialize by reading the topics and qrels
		conf = Configuration.newInstance()
		String fileseparator = System.getProperty("file.separator")
		String homedir = conf.get("rembrandt.home.dir",".")
    	String indexdir = homedir+fileseparator+"index"+fileseparator+collection_dir
        log.info "Loading index in $indexdir"
    	renoir = new Renoir(conf, indexdir, lang)
    }

// 1. BM25 NORMAL

//TERM    
	// bm25 normal, contents:Portugal
    void testSimpleBM25oneContent() {
        
		log.info "Testing simple BM25 contents:Portugal" 
		String s = "model:bm25 qe:no search:true explain:true contents:Portugal"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
       	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
            Map result = res["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  "
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 44 documents with contents:Portugal"
		assert res["result"].size() == 44 // terms
//44 termos que resultam de 44 documentos com o termo 'Portugal'
/*
0 59 hub-41899  [[field:contents, score:1.7749345, term:portugal, weight:1.0, doc:59]]
1 40 hub-15590  [[field:contents, score:1.7490537, term:portugal, weight:1.0, doc:40]]
2 75 hub-66526  [[field:contents, score:1.7380478, term:portugal, weight:1.0, doc:75]]
3 78 hub-68694  [[field:contents, score:1.727294, term:portugal, weight:1.0, doc:78]]
4 51 hub-28874  [[field:contents, score:1.6914748, term:portugal, weight:1.0, doc:51]]
5 42 hub-16632  [[field:contents, score:1.6902703, term:portugal, weight:1.0, doc:42]]
6 83 hub-78051  [[field:contents, score:1.6054358, term:portugal, weight:1.0, doc:83]]
7 53 hub-31642  [[field:contents, score:1.5851562, term:portugal, weight:1.0, doc:53]]
8 77 hub-67792  [[field:contents, score:1.5770153, term:portugal, weight:1.0, doc:77]]
9 88 hub-93257  [[field:contents, score:1.5589312, term:portugal, weight:1.0, doc:88]]
*/		
    }

	// bm25 normal, contents:Portugal contents:Cavaco
    void testSimpleBM25twoContent() {
        
		log.info "Testing simple BM25 contents:Portugal contents:Cavaco" 
		String s = "model:bm25 qe:no search:true explain:true contents:Portugal contents:Cavaco"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
       	log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
             Map result = res["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 45 documents with contents:portugal contents:cavaco"
		assert res["result"].size() == 45 // terms
		
// há 43 docs só com 'portugal', 1 doc só com 'cavaco', e 1 doc com 'cavaco' e 'portugal'		
/*
0 46 hub-22322  10.967078[[field:contents, score:10.967078, term:cavaco, weight:1.0, doc:46]]
1 60 hub-43823  8.2019615[[field:contents, score:0.9892119, term:portugal, weight:1.0, doc:60], [field:contents, score:7.21275, term:cavaco, weight:1.0, doc:60]]
2 59 hub-41899  1.7749345[[field:contents, score:1.7749345, term:portugal, weight:1.0, doc:59]]
3 40 hub-15590  1.7490537[[field:contents, score:1.7490537, term:portugal, weight:1.0, doc:40]]
4 75 hub-66526  1.7380478[[field:contents, score:1.7380478, term:portugal, weight:1.0, doc:75]]
5 78 hub-68694  1.727294[[field:contents, score:1.727294, term:portugal, weight:1.0, doc:78]]
6 51 hub-28874  1.6914748[[field:contents, score:1.6914748, term:portugal, weight:1.0, doc:51]]
7 42 hub-16632  1.6902703[[field:contents, score:1.6902703, term:portugal, weight:1.0, doc:42]]
8 83 hub-78051  1.6054358[[field:contents, score:1.6054358, term:portugal, weight:1.0, doc:83]]
9 53 hub-31642  1.5851562[[field:contents, score:1.5851562, term:portugal, weight:1.0, doc:53]]
*/	
    }

//NE
	// bm25 normal, ne-LOCAL:Portugal
    void testSimpleBM25oneSimpleNE() {
        
		log.info "Testing simple BM25 ne-LOCAL-HUMANO-PAIS:Portugal" 
		String s = "model:bm25 qe:no search:true explain:true "+
		"ne-LOCAL-HUMANO-PAIS:Portugal"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
             Map result = res["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 39 documents with ne-LOCAL-HUMANO-PAIS:Portugal"
		assert res["result"].size() == 39 
		
	// há 39 docs com a NE PAÍS Portugal 	
/*
0 51 hub-28874  2.0016265[[field:ne-LOCAL-HUMANO-PAIS, score:2.0016265, term:portugal, weight:1.0, doc:51]]
1 78 hub-68694  2.0016265[[field:ne-LOCAL-HUMANO-PAIS, score:2.0016265, term:portugal, weight:1.0, doc:78]]
2 75 hub-66526  1.9589331[[field:ne-LOCAL-HUMANO-PAIS, score:1.9589331, term:portugal, weight:1.0, doc:75]]
3 38 hjlll  1.9282855[[field:ne-LOCAL-HUMANO-PAIS, score:1.9282855, term:portugal, weight:1.0, doc:38]]
4 42 hub-16632  1.9282855[[field:ne-LOCAL-HUMANO-PAIS, score:1.9282855, term:portugal, weight:1.0, doc:42]]
5 60 hub-43823  1.9282855[[field:ne-LOCAL-HUMANO-PAIS, score:1.9282855, term:portugal, weight:1.0, doc:60]]
6 59 hub-41899  1.8051696[[field:ne-LOCAL-HUMANO-PAIS, score:1.8051696, term:portugal, weight:1.0, doc:59]]
7 65 hub-51467  1.7877141[[field:ne-LOCAL-HUMANO-PAIS, score:1.7877141, term:portugal, weight:1.0, doc:65]]
8 4 H2-Ert75  1.737316[[field:ne-LOCAL-HUMANO-PAIS, score:1.737316, term:portugal, weight:1.0, doc:4]]
9 11 Ntyr-78  1.737316[[field:ne-LOCAL-HUMANO-PAIS, score:1.737316, term:portugal, weight:1.0, doc:11]]
*/
    }

	// bm25 normal, ne-PESSOA:"Cavaco Silva"
    void testSimpleBM25oneComplexNE() {
        
		log.info "Testing simple BM25 ne-PESSOA-INDIVIDUAL:\"Cavaco Silva\"" 
		String s = "model:bm25 qe:no search:true explain:true "+
		"ne-PESSOA-INDIVIDUAL:\"Cavaco Silva\""
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
            Map result = res["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 2 documents with ne-PESSOA-INDIVIDUAL:\"Cavaco Silva\""
		assert res["result"].size() == 2 // terms
// há 2 docs com a NE PESSOA 'Cavaco Silva'
/*
0 46 hub-22322  0.0012168741
1 60 hub-43823  0.0010755749
*/    }

	// bm25 normal, ne-LOCAL:Portugal ne-PESSOA:"Cavaco Silva"
    void testSimpleBM25twoNE() {
        
		log.info "Testing simple BM25 ne-LOCAL-HUMANO-PAIS:Portugal ne-PESSOA-INDIVIDUAL:\"Cavaco Silva\"" 
		String s = "model:bm25 qe:no search:true explain:true "+
		"ne-LOCAL-HUMANO-PAIS:Portugal ne-PESSOA-INDIVIDUAL:\"Cavaco Silva\""
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
            Map result = res["result"][i]
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 40 documents"
		assert res["result"].size() == 40 // terms
    }
//há 38 docs com NE Portugal, 1 doc com NE "Cavaco Silva" sem "Portugal", 1 doc com ne "Cavaco Silva" e "Portugal" 
/*
0 51 hub-28874  2.0016265[[field:ne-LOCAL-HUMANO-PAIS, score:2.0016265, term:portugal, weight:1.0, doc:51]]
1 78 hub-68694  2.0016265[[field:ne-LOCAL-HUMANO-PAIS, score:2.0016265, term:portugal, weight:1.0, doc:78]]
2 75 hub-66526  1.9589331[[field:ne-LOCAL-HUMANO-PAIS, score:1.9589331, term:portugal, weight:1.0, doc:75]]
3 60 hub-43823  1.9293611[[field:ne-LOCAL-HUMANO-PAIS, score:1.9282855, term:portugal, weight:1.0, doc:60]]
4 38 hjlll  1.9282855[[field:ne-LOCAL-HUMANO-PAIS, score:1.9282855, term:portugal, weight:1.0, doc:38]]
5 42 hub-16632  1.9282855[[field:ne-LOCAL-HUMANO-PAIS, score:1.9282855, term:portugal, weight:1.0, doc:42]]
6 59 hub-41899  1.8051696[[field:ne-LOCAL-HUMANO-PAIS, score:1.8051696, term:portugal, weight:1.0, doc:59]]
7 65 hub-51467  1.7877141[[field:ne-LOCAL-HUMANO-PAIS, score:1.7877141, term:portugal, weight:1.0, doc:65]]
8 4 H2-Ert75  1.737316[[field:ne-LOCAL-HUMANO-PAIS, score:1.737316, term:portugal, weight:1.0, doc:4]]
9 11 Ntyr-78  1.737316[[field:ne-LOCAL-HUMANO-PAIS, score:1.737316, term:portugal, weight:1.0, doc:11]]
*/

//Entity
	// bm25 normal, entity:Portugal
    void testSimpleBM25oneEntity() {
        
		log.info "Testing simple BM25 entity:Portugal" 
		String s = "model:bm25 qe:no search:true explain:true "+
		"entity:Portugal"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
             Map result = res["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 39 documents with entity:Portugal"
		assert res["result"].size() == 39 
/*
0 78 hub-68694  2.1222572[[field:entity, score:2.1222572, term:Portugal, weight:1.0, doc:78]]
1 75 hub-66526  2.048026[[field:entity, score:2.048026, term:Portugal, weight:1.0, doc:75]]
2 51 hub-28874  2.0453408[[field:entity, score:2.0453408, term:Portugal, weight:1.0, doc:51]]
3 42 hub-16632  2.0399916[[field:entity, score:2.0399916, term:Portugal, weight:1.0, doc:42]]
4 60 hub-43823  2.0399916[[field:entity, score:2.0399916, term:Portugal, weight:1.0, doc:60]]
5 65 hub-51467  2.0399916[[field:entity, score:2.0399916, term:Portugal, weight:1.0, doc:65]]
6 83 hub-78051  1.9350684[[field:entity, score:1.9350684, term:Portugal, weight:1.0, doc:83]]
7 59 hub-41899  1.844763[[field:entity, score:1.844763, term:Portugal, weight:1.0, doc:59]]
8 88 hub-93257  1.844763[[field:entity, score:1.844763, term:Portugal, weight:1.0, doc:88]]
9 86 hub-83020  1.8274746[[field:entity, score:1.8274746, term:Portugal, weight:1.0, doc:86]]
*/		
    }

	// bm25 normal, entity:Portugal
    void testSimpleBM25twoEntities() {
		log.info "Testing simple BM25 entity:Portugal entity:An%C3%ADbal_Cavaco_Silva "
		String s = "model:bm25 qe:no search:true explain:true "+
		"entity:Portugal entity:An%C3%ADbal_Cavaco_Silva"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
            Map result = res["result"][i]
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 39 documents"
		assert res["result"].size() == 39 // terms
		
// documento em entity "Aníbal Cavaco Silva" só existe 1, e tem entity:Portugal também		
/*
0 78 hub-68694  2.1222572[[field:entity, score:2.1222572, term:Portugal, weight:1.0, doc:78]]
1 75 hub-66526  2.048026[[field:entity, score:2.048026, term:Portugal, weight:1.0, doc:75]]
2 51 hub-28874  2.0453408[[field:entity, score:2.0453408, term:Portugal, weight:1.0, doc:51]]
3 42 hub-16632  2.0399916[[field:entity, score:2.0399916, term:Portugal, weight:1.0, doc:42]]
4 60 hub-43823  2.0399916[[field:entity, score:2.0399916, term:Portugal, weight:1.0, doc:60]]
5 65 hub-51467  2.0399916[[field:entity, score:2.0399916, term:Portugal, weight:1.0, doc:65]]
6 83 hub-78051  1.9350684[[field:entity, score:1.9350684, term:Portugal, weight:1.0, doc:83]]
7 59 hub-41899  1.844763[[field:entity, score:1.844763, term:Portugal, weight:1.0, doc:59]]
8 88 hub-93257  1.844763[[field:entity, score:1.844763, term:Portugal, weight:1.0, doc:88]]
9 86 hub-83020  1.8274746[[field:entity, score:1.8274746, term:Portugal, weight:1.0, doc:86]]
*/
 }

// WOEID
	// bm25 normal, woeid:23424925
	void testSimpleBM25oneWOEID() {
        
		log.info "Testing simple BM25 woeid:23424925" 
		String s = "model:bm25 qe:no search:true explain:true "+
		"woeid:23424925"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
             Map result = res["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 55 documents"
		assert res["result"].size() == 55

//Há 55 docs com woeid de Portugal. São mais que os terms/ne/entity, porque o woeid é também inferido das cidades
/*
0 65 hub-51467  0.68151605[[field:woeid, score:0.68151605, term:23424925, weight:1.0, doc:65]]
1 38 hjlll  0.66246617[[field:woeid, score:0.66246617, term:23424925, weight:1.0, doc:38]]
2 42 hub-16632  0.66246617[[field:woeid, score:0.66246617, term:23424925, weight:1.0, doc:42]]
3 51 hub-28874  0.66246617[[field:woeid, score:0.66246617, term:23424925, weight:1.0, doc:51]]
4 60 hub-43823  0.66246617[[field:woeid, score:0.66246617, term:23424925, weight:1.0, doc:60]]
5 126 wpt-1000772700099419796  0.66246617[[field:woeid, score:0.66246617, term:23424925, weight:1.0, doc:126]]
6 4 H2-Ert75  0.6551017[[field:woeid, score:0.6551017, term:23424925, weight:1.0, doc:4]]
7 11 Ntyr-78  0.6551017[[field:woeid, score:0.6551017, term:23424925, weight:1.0, doc:11]]
8 39 hub-15425  0.6551017[[field:woeid, score:0.6551017, term:23424925, weight:1.0, doc:39]]
9 113 wpt-10034934164544455  0.6551017[[field:woeid, score:0.6551017, term:23424925, weight:1.0, doc:113]]
*/
    }

// TG
	// bm25 normal, time:2004
	void testSimpleBM25oneTG() {
        
		log.info "Testing simple BM25 time:2004" 
		String s = "model:bm25 qe:no search:true explain:true time:2004"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
             Map result = res["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 10 documents"
		assert res["result"].size() == 10 
	// tg:2004 aparece em 10 docs	
/*
0 16 aa40383  5.4753747[[field:tg, score:5.4753747, term:2004, weight:1.0, doc:16]]
1 27 bob-37600  4.948806[[field:tg, score:4.948806, term:2004, weight:1.0, doc:27]]
2 80 hub-74208  4.948806[[field:tg, score:4.948806, term:2004, weight:1.0, doc:80]]
3 86 hub-83020  4.6235275[[field:tg, score:4.6235275, term:2004, weight:1.0, doc:86]]
4 126 wpt-1000772700099419796  4.2423553[[field:tg, score:4.2423553, term:2004, weight:1.0, doc:126]]
5 65 hub-51467  3.6087308[[field:tg, score:3.6087308, term:2004, weight:1.0, doc:65]]
6 55 hub-37819  3.3001506[[field:tg, score:3.3001506, term:2004, weight:1.0, doc:55]]
7 103 ric-42664  3.3001506[[field:tg, score:3.3001506, term:2004, weight:1.0, doc:103]]
8 72 hub-63156  2.1219604[[field:tg, score:2.1219604, term:2004, weight:1.0, doc:72]]
9 120 wpt-1019468604217317242  2.1219604[[field:tg, score:2.1219604, term:2004, weight:1.0, doc:120]]
*/		
    }

	// bm25 normal, time:2004*
	void testSimpleBM25oneTGWithWildcard() {
        
		log.info "Testing simple BM25 time:2004*" 
		String s = "model:bm25 qe:no search:true explain:true time:2004*"
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
             Map result = res["result"][i]
			//println result
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}
		
		log.info "Testing if there is 13 documents"
		assert res["result"].size() == 13 
/* há mais uns...*/	
    }

// Term + NE + ENTITY + WOEID     
	void testSimpleBM25everything() {
        
		log.info "Testing simple BM25 term + ne + entity + woeid" 
		String s = "model:bm25 qe:no search:true explain:true "+
		"contents:portugal contents:cavaco ne-LOCAL-HUMANO-PAIS:Portugal "+
		"ne-PESSOA-INDIVIDUAL:\"Cavaco Silva\" "+
		"entity:Portugal entity:An%C3%ADbal_Cavaco_Silva "+
		"woeid:23424925"
		
		RenoirQuery rq = RenoirQueryParser.parse(s)
		Map res = renoir.search(rq)
                
        log.info "Final query: ${res['final_query_string']}\n"
        log.info "Got "+res["total"]+" docs in "+res["time"]+" msecs.\n"
      	int top = (res["total"] > 10 ? 10 : res["total"])
              
        for (int i = 0; i < top; i++) {
            Map result = res["result"][i]
			print result["i"]+" "+result["doc_id"]+" "+result["doc_original_id"] + "  " + result["score"]
			if (result["partialscore"]) print result["partialscore"]
			print "\n"
		}

		log.info "Testing if there is 58 documents"
		assert res["result"].size() == 58 // terms
    }
/*0 60 hub-43823  12.83378[[field:contents, score:0.9892119, term:portugal, weight:1.0, doc:60], [field:contents, score:7.21275, term:cavaco, weight:1.0, doc:60], [field:ne-LOCAL-HUMANO-PAIS, score:1.9282855, term:portugal, weight:1.0, doc:60], [field:entity, score:2.0399916, term:Portugal, weight:1.0, doc:60], [field:woeid, score:0.66246617, term:23424925, weight:1.0, doc:60]]
1 46 hub-22322  10.968295[[field:contents, score:10.967078, term:cavaco, weight:1.0, doc:46]]
2 78 hub-68694  6.4654894[[field:contents, score:1.727294, term:portugal, weight:1.0, doc:78], [field:ne-LOCAL-HUMANO-PAIS, score:2.0016265, term:portugal, weight:1.0, doc:78], [field:entity, score:2.1222572, term:Portugal, weight:1.0, doc:78], [field:woeid, score:0.6143117, term:23424925, weight:1.0, doc:78]]
3 51 hub-28874  6.4009085[[field:contents, score:1.6914748, term:portugal, weight:1.0, doc:51], [field:ne-LOCAL-HUMANO-PAIS, score:2.0016265, term:portugal, weight:1.0, doc:51], [field:entity, score:2.0453408, term:Portugal, weight:1.0, doc:51], [field:woeid, score:0.66246617, term:23424925, weight:1.0, doc:51]]
4 75 hub-66526  6.3593187[[field:contents, score:1.7380478, term:portugal, weight:1.0, doc:75], [field:ne-LOCAL-HUMANO-PAIS, score:1.9589331, term:portugal, weight:1.0, doc:75], [field:entity, score:2.048026, term:Portugal, weight:1.0, doc:75], [field:woeid, score:0.6143117, term:23424925, weight:1.0, doc:75]]
5 42 hub-16632  6.321014[[field:contents, score:1.6902703, term:portugal, weight:1.0, doc:42], [field:ne-LOCAL-HUMANO-PAIS, score:1.9282855, term:portugal, weight:1.0, doc:42], [field:entity, score:2.0399916, term:Portugal, weight:1.0, doc:42], [field:woeid, score:0.66246617, term:23424925, weight:1.0, doc:42]]
6 59 hub-41899  6.011732[[field:contents, score:1.7749345, term:portugal, weight:1.0, doc:59], [field:ne-LOCAL-HUMANO-PAIS, score:1.8051696, term:portugal, weight:1.0, doc:59], [field:entity, score:1.844763, term:Portugal, weight:1.0, doc:59], [field:woeid, score:0.5868644, term:23424925, weight:1.0, doc:59]]
7 65 hub-51467  5.8943186[[field:contents, score:1.3850971, term:portugal, weight:1.0, doc:65], [field:ne-LOCAL-HUMANO-PAIS, score:1.7877141, term:portugal, weight:1.0, doc:65], [field:entity, score:2.0399916, term:Portugal, weight:1.0, doc:65], [field:woeid, score:0.68151605, term:23424925, weight:1.0, doc:65]]
8 38 hjlll  5.7359157[[field:contents, score:1.3905826, term:portugal, weight:1.0, doc:38], [field:ne-LOCAL-HUMANO-PAIS, score:1.9282855, term:portugal, weight:1.0, doc:38], [field:entity, score:1.7545812, term:Portugal, weight:1.0, doc:38], [field:woeid, score:0.66246617, term:23424925, weight:1.0, doc:38]]
9 83 hub-78051  5.6876016[[field:contents, score:1.6054358, term:portugal, weight:1.0, doc:83], [field:ne-LOCAL-HUMANO-PAIS, score:1.5602331, term:portugal, weight:1.0, doc:83], [field:entity, score:1.9350684, term:Portugal, weight:1.0, doc:83], [field:woeid, score:0.5868644, term:23424925, weight:1.0, doc:83]]
*/

	
}
