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

package saskia.test

import saskia.bin.Configuration
import saskia.db.GeoSignatureFactory;
import saskia.db.table.EntityTable;

import org.apache.log4j.Logger
import rembrandt.io.RembrandtStyleTag
import rembrandt.io.UnformattedStyleTag
import rembrandt.io.RembrandtReader
import rembrandt.io.UnformattedReader
import rembrandt.obj.Document
/**
 * @author Nuno Cardoso
 *
 */
class GeoSignatureFactoryTest extends GroovyTestCase{
    
    // doc 1:
    // Porto fica a norte de Portugal. 
    // Lisboa fica mais a sul. 
    // Acima do Porto fica Braga.
    // 5 NEs
    
    static Logger log = Logger.getLogger("SaskiaTest")
    Configuration conf = Configuration.newInstance()
    List processedNEs
    List<Document> source_docs
    List<Document> dgs_solutions
    RembrandtReader reader
    UnformattedReader ureader
    UnformattedStyleTag tag
    
    public GeoSignatureFactoryTest() {
                
        String sourcefile = conf.get("rembrandt.home.dir",".")+
            "/resources/test/GeoSignaturePT_sample.txt"     
        String targetfile = conf.get("rembrandt.home.dir",".")+
           "/resources/test/GeoSignaturePT_sample_output.txt"     
        
        reader = new RembrandtReader(new RembrandtStyleTag("pt"))
        ureader = new UnformattedReader(new UnformattedStyleTag("pt"))
                 
        File f = new File(sourcefile)
        InputStreamReader is = new InputStreamReader(new FileInputStream(f))
        reader.processInputStream(is)
        source_docs = reader.docs
        
        File f2 = new File(targetfile)
        InputStreamReader is2 = new InputStreamReader(new FileInputStream(f2))
        ureader.processInputStream(is2)
        dgs_solutions = ureader.docs       
    }
    

    /*       nes is a list with Map entries:  section: ['dhn_section'], sentence: ['dhn_sentence'],
     term: ['dhn_term'], name: ['nen_name'], type: ['ne_type'], 
     subtype: ['ne_subtype'], entity: ['ent_id'], dbpediaClass: ['ent_dbpedia_class']                 
     */      
    void testGenerate() {
        
        // let's simulate the output from RembrandtedDoc.getBatchDocsAndNEsFromPoolToGenerateGeoSignatures

        def labels 
        GeoSignatureFactory gsf = new GeoSignatureFactory()
        int fail = 0
        
        source_docs.eachWithIndex{doc, i -> 
            
            Map docs = [:]            
            docs[doc.docid]=[lang:doc.lang, nes:[]]
            labels = Class.forName("rembrandt.gazetteers.${doc.lang.toLowerCase()}.SecondHAREMClassificationLabels${doc.lang.toUpperCase()}").newInstance()  
            
            doc.bodyNEs.each{ne -> 
                // getAt(0) - first classification, getAt(0), first dbpediaResource for that classification 
                String dbpediaResource = ne.dbpediaPage.collect{it.value}.getAt(0).getAt(0)
                log.trace "For ne, got dbpediaResource $dbpediaResource"
                EntityTable ent = EntityTable.getFromDBpediaResource(dbpediaResource)
                
                Map entry = [section:"B", sentence:ne.sentenceIndex,
                             term:ne.termIndex, name:ne.printTerms(), type:labels.label[ne.classification[0].t],
                             subtype:labels.label[ne.classification[0].s], entity:ent.ent_id,
                             dbpediaClass:ent.ent_dbpedia_class]
                log.trace "Entry: $entry docid: $doc.docid"
                docs[doc.docid].nes << entry  
            }  
            
            String source_dgs 
            docs.each{docid, stuff -> source_dgs = gsf.generate(docid, stuff)}
            
            // let's compare line by line
            String solution_dgs = dgs_solutions[i].body
            List source_dgs_lines = source_dgs.split(/\n+/)
            List solution_dgs_lines = solution_dgs.split(/\n+/)
           // println "source: "+source_dgs
            source_dgs_lines.eachWithIndex{l, li -> 
            	if (!l.trim().equals(solution_dgs_lines[li].trim())) {
                 log.error "Doc ${doc.docid}: Source line is $l, should be ${solution_dgs_lines[li]}!"
                 fail++   
                }
            }
        }
       
       
       
        assert fail == 0
   }
}
       
 