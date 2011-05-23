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
 
package rembrandt.obj

class TermWithPoS extends Term {
    
    String stem // portugueses -> portug
    String type 
    String lemma // portugueses -> portuguese
    Map property = [:]
                       
    public TermWithPoS(String text, int index = 0, boolean hidden = false) {
	super(text, index, hidden)
    }
    
    static TermWithPoS parse(String line, int index, int parser) {
	TermWithPoS t	
	if (parser == SentenceWithPoS.FROM_LINGUATECA_PoS) {
	    List<String> bits = line.trim().split("\t")
	    t = new TermWithPoS(bits[0], index, false)
	    t.lemma = bits[1]
	    String type = bits[2]

	    switch (type) {
	    case ~/^ADV.*/: t.type = 'ADV'; break; 
	    case "V_n":     t.type = 'NV'; break; 
	    case ~/^PRP.*/: t.type = 'PRP'; break; 
	    case ~/^N_.*/:  t.type = 'N'; break;
	    case ~/^N$/:    t.type = 'N'; break;
	    case "ADJ":     t.type = 'ADJ'; break;
	    case ~/^V.*/:   t.type = 'V'; break;
	    case ~/^DET.*/: t.type = 'DET'; break;
	    case ~/^PROP.*/:t.type = 'PROP'; break;
	    case ~/^PERS.*/:t.type = 'PERS'; break;
	    case "PU": 	    t.type = 'PONT'; break;
	    case ~/^KC.*/:  t.type = 'KC'; break;	
	    case ~/^NUM.*/: t.type = 'NUM'; break;
	    case ~/^SPEC.*/:t.type = 'SPEC'; break;
	    default: println "Can't determine type of $type in line $line"
	    }		
	    if (bits[3] != "0") t.property['subclass'] = bits[3]
	    if (bits[4] != "0") t.property['number'] = bits[4] //S/P
	    if (bits[5] != "0") t.property['gender'] = bits[5] //M/F
	                                                    
	} else if (parser == SentenceWithPoS.FROM_PALAVRAS_PoS) {
	    List bits = line.trim().split(/[\s\t]+/)	
	    t = new TermWithPoS(bits[0], index, false)	                     
	    t.lemma = bits[1]?.replaceAll(/[\[\]]/,"")
            if (bits.size() >2) {
        	for (int i=2; i<bits.size(); i++) {
        	    def term = bits[i]
        	    switch (term) {
        	    case ~/^ADV$/: t.type = 'ADV'; break;
        	    case ~/^DET$/: t.type = 'DET'; break;
        	    case ~/^N$/:   t.type = 'N'; break;
        	    case ~/^V$/:   t.type = 'V'; break;
        	    case ~/^PERS$/:t.type = 'PERS'; break;
        	    case ~/^PROP$/:t.type = 'PROP'; break;
        	    case ~/^PRP$/: t.type = 'PRP'; break;
        	    case ~/^SPEC$/:t.type = 'SPEC'; break;
        	    }
        	}
            } else {
        	// there is a punctuation mark
        	def m = bits[0] =~ /^[\.;\?!]+$/
        	if (m.matches()) {
        	    t.lemma = bits[0]
        	    t.type = 'PONT'
        	}	
            }
	}
	return t
    }  
}