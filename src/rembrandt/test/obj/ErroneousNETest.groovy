/* Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package rembrandt.test.obj

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*

import rembrandt.obj.*
import rembrandt.io.*

/**
 * @author Nuno Cardoso
 * Tester for eliminating bad NEs for printing.
 */
class TestErroneousNE extends GroovyTestCase {

	def Logger log = Logger.getLogger("RembrandtTestLogger")
    def printer
    def document

    
	 public TestErroneousNE() {
	    
	  printer = new SecondHAREMDocumentWriter(new SecondHAREMStyleTag("pt"))
	  document = new Document("","Doc-XX")

	  document.sentences = [new Sentence(
		 [new Term(text:"O",index:0), 
		  new Term(text:"Governo",index:1),
		  new Term(text:"de",index:2),
		  new Term(text:"D.",index:3),
		  new Term(text:"Silva",index:4),
		  new Term(text:"caiu",index:5),
		  new Term(text:"hoje",index:6),
		  new Term(text:".",index:7)  ],0),
	   new Sentence([
	       new Term(text:"O",index:0),
	       new Term(text:"José",index:1),
	       new Term(text:"Sócrates",index:2),
	       new Term(text:"não",index:3),
	       new Term(text:"fez",index:4),
	       new Term(text:"nada",index:5),
	       new Term(text:".",index:6)  ],1)  
	  ]
	  
	  def ne1 = new NamedEntity(id:"EM1",
		    sentenceNumber:0, firstTokenNumber:1,	
			terms:[new Term(text:'Governo', index:1),
			       new Term(text:'de',index:2),
			       new Term(text:'Cavaco',index:3),
			       new Term(text: 'Silva', index:4)],
			category:['ORGANIZACAO'], 
			type:['ADMINISTRACAO'])	
	  
	  def ne2 = new NamedEntity(id:"EM2",
		    sentenceNumber:0, firstTokenNumber:3,	
			terms:[new Term(text:'Cavaco',index:3)],
			category:['PESSOA'], 
			type:['INDIVIDUAL'])	

	  def ne3 = new NamedEntity(id:"EM3",
		    sentenceNumber:1, firstTokenNumber:1,	
			terms:[new Term(text:'José',index:1), new Term(text:'Sócrates',index:2) ],
			category:['PESSOA'], 
			type:['INDIVIDUAL'])
	  
	  def ne4 = new NamedEntity(id:"EM4",
		    sentenceNumber:1, firstTokenNumber:2,	
			terms:[new Term(text:'Sócrates',index:2), new Term(text:'não',index:3)],
			category:['PESSOA'], 
			type:['INDIVIDUAL'])	

	  document.NEs.add(ne1)
	  document.NEs.add(ne2)
	  document.NEs.add(ne3)
	  document.NEs.add(ne4)
	}
    
   void testErroneous() {
       
       println "Before:"
       println printer.printFullDoc(document)
       
       def NEsToRemove=[]
       document.sentences.each{sentence -> 
 			def NEList = document.NEs.getNEsBySentenceIndex(sentence.index)
 			if (NEList.size() >= 2) {
 			    for(int i = 0; i<NEList.size()-1; i++) {
 					for (int j = i+1; j<NEList.size(); j++) {
 					    def ane1 = NEList[i]
 					    def ane2 = NEList[j]
			 	// if ne1 overlaps ne2 ou 
			 	// ne1 completely contained in ne2 ou 
			 	// ne2 completely contained in ne1 
			 	// and neither have alts!!
			 	
			 	// genereates nested EMs. Remove the smaller one.
			 			if (ane1.matchesBoundaries(ane2, BoundaryCriteria.ContainsAndCentered) || 
			 				ane2.matchesBoundaries(ane1, BoundaryCriteria.ContainsAndCentered)) {
			 			    if (ane1.alt == null && ane2.alt == null) {
			 					if (ane1.terms.size() >= ane2.terms.size()) NEsToRemove += ane2
			 					if (ane2.terms.size() > ane1.terms.size()) NEsToRemove += ane1	
			 			    }
			 			}
 					    
 					    println "doing $ane1 $ane2"
 					    if (ane2.matchesBoundaries(ane1, BoundaryCriteria.Overlapping)) {
 							if (ane1.alt == null && ane2.alt == null) {
 							    if (ane1.terms.size() >= ane2.terms.size()) NEsToRemove += ane2
 							    if (ane2.terms.size() > ane1.terms.size()) NEsToRemove += ane1	
 							}
 					    }				 				
 					}
 			    }
 			}
       }
 		println "NEs that are not in conditions, and should be removed: ${NEsToRemove}"
 		document.NEs.removeNEs(NEsToRemove)
 		
 	   println "After:"
       println printer.printFullDoc(document)

   }
}