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
 
package rembrandt.test.obj

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.Logger
import saskia.bin.Configuration
import rembrandt.obj.Document
import rembrandt.obj.DocumentIndex
import java.util.regex.Pattern
/**
 * @author Nuno Cardoso
 * Tester for Lucene Regex.
 */
class TestDocumentIndex extends GroovyTestCase {

    static Logger log = Logger.getLogger("RembrandtTest")
    Configuration conf
    Document doc
    
    public TestDocumentIndex() {
	conf = Configuration.newInstance()
	
	/**
	 * 0: Portugal, oficialmente República Portuguesa, é um país localizado no sudoeste da Europa, 
	 * cujo território se situa na zona ocidental da Península Ibérica e em arquipélagos no Atlântico Norte. 
	 * 1: Possui uma área total de 92.391 km2, e é a nação mais ocidental do continente europeu. 
	 * 2: O território português é delimitado a Norte e a Leste por Espanha e a Sul e Oeste pelo Oceano 
	 * Atlântico, e compreende a parte continental e as regiões autónomas: os arquipélagos dos Açores e da Madeira.
	 * 3: Durante os séculos XV e XVI, Portugal foi uma potência mundial económica, social e cultural, 
	 * constituindo-se o primeiro e o mais duradouro império colonial de amplitude global.
	 */
	File f = new File(conf.get("rembrandt.home.dir",".")+"/resources/test/WikipediaPTSample-100words.txt")
	doc = new Document()	
	doc.body = f.text
	doc.preprocess()
   }
    
   void testGetIndexesForTerm() {
       
       String p1 = "Europa"
       String p2 = "europa"
	            
       List ls1 = doc.bodyIndex.getIndexesForTerm(p1)
       List ls2 = doc.bodyIndex.getIndexesForTerm(p2)
       
       assert  ls1 ==  [[0, 13]], "Got $ls1 instead"
       assert  ls2 == null, "Got $ls2 instead"      
   }
    
   void testGetTermsForPattern() {	
       
       Pattern p1 = ~/.*urop.*/
       Pattern p2 = ~/.*ortug.*/
	      
       List l1 = ['Europa','europeu']
       List l2 = ['Portugal','Portuguesa','português']
                  
       List ls1 = doc.bodyIndex.getTermsForPattern(p1)
       List ls2 = doc.bodyIndex.getTermsForPattern(p2)
       
       assert  l1 == ls1, "Got $ls1 instead"
       assert  l2 == ls2, "Got $ls2 instead"
    }  
   
   void testGetJointIndexesForPattern() {
       
       Pattern portug = ~/.*ortug.*/
       
       List l1 = doc.bodyIndex.getJointIndexesForPattern(portug)
       
       List ls1 = [[0, 0], [0, 4], [2, 2], [3, 7]]
       
       assert l1 == ls1,  "Got $l1 instead of $ls1"           

   }
   
    void testSimpleConceptMatch() {	
       
       Pattern ortug = ~/.*ortug.*/
       Pattern arquipelagos = ~/arquipélagos?/
       Pattern acores = ~/[Aa]çores?/
	       
       List p1 = ["Europa", ortug, ["continente","nãoexiste"], [arquipelagos, 'dos', acores],
                  arquipelagos,
                  "nãovaiencontrar", ~/naovaiencontar/, ["total","encontra","o","primeiro","termo" ] ]
               
       Map ls1 = doc.bodyIndex.getSubConceptsAndIndexesForConcept(p1)
 
       assert ls1['Europa'] == [[0, 13]] // há uma 'Europa' no texto, frase 0, termo 13
       assert ls1['europa'] == null   // não há europa em letra minúscula   
                   
       assert ls1[ortug] == [[0, 0], [0, 4], [2, 2], [3, 7]] // índices de Portugal, Portuguesa, português
       assert ls1[['continente', 'nãoexiste']] == [[1, 15]] // um índice visto apenas em 'continente'
                                                   
      /* Fazer o teste assim falha. Não sei porquê, mas comparar Patterns compilados funciona mal
       * assert ls1[[~/arquipélagos?/, 'dos', ~/[Aa]çores?/]] == [[0, 27], [2, 32]] 
       */
       assert ls1[[arquipelagos, 'dos', acores]] == [[0, 27], [2, 32]] 
       assert ls1[arquipelagos] == [[0, 27], [2, 32]] 
       assert ls1[['total', 'encontra', 'o', 'primeiro', 'termo']] == [[1, 3]]
                           
       // get an hash if sentence/term index - subconcepts                                                               
       Map ls2 = DocumentIndex.invertConceptIndex(ls1)
       
       assert ls2[[0,0]] == [ortug], "ls2[[0,0]] equals ${ls2[[0,0]]} instead"
       assert ls2[[0,4]] == [ortug], "ls2[[0,4]] equals ${ls2[[0,4]]} instead"
       assert ls2[[0,13]] == ['Europa'] , "ls2[[0,13]] equals ${ls2[[0,13]]} instead"
       assert ls2[[0,27]] == [[arquipelagos, 'dos', acores], arquipelagos ], "ls2[[0,27]] equals ${ls2[[0,27]]} instead"
                            
       // get only the several indexes matched by the concept//
       List ls3 = DocumentIndex.sortKeysOfInvertedConceptIndex(ls2)
       assert ls3 == [ [0,0] , [0,4] , [0,13] , [0,27] , [1,3] , [1,15] , [2,2] , [2,32] , [3,7] ]

    }  
    
    // using List.grep(pattern) gives ~5 seconds, using Lucene's RegexQuery gives ~6 seconds
   /* void testPerformance() {
	long start1 = System.currentTimeMillis()
	10000.times {testSimpleConceptMatch()}
	long start2 = System.currentTimeMillis()
	log.info "Repeated 10000 times simpleConceptMatch in ${start2-start1} msecs."
    }*/
    
    
}