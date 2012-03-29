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
package saskia.converters

import org.apache.log4j.*
import rembrandt.obj.Document
import rembrandt.obj.NamedEntity
import rembrandt.obj.SemanticClassification
import rembrandt.obj.ListOfNE
import rembrandt.obj.Sentence
import rembrandt.obj.Term
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag


class CHAVE2DocumentConverter {
	
	static Logger log = Logger.getLogger("DocumentConverter")
	Document doc
	RembrandtWriter rw
	def tags = [:] 
	def number = 0
	def startemmark="STARTREMBRANDTEMMARK"
	def endemmark="ENDREMBRANDTEMMARK"
	// there is no P, no ALT, only tags. Sweet!
	
	public CHAVE2DocumentConverter() {
	    rw = new RembrandtWriter(new RembrandtStyleTag("pt"))
	}
	
	public void clear() {
	   tags = [:] 
	}
	
	private String saveStartMark(item) {
		tags[++number] = item
		return  " ${startemmark}${number} "
	}
	
	private String saveEndMark(item) {
		tags[++number] = item
		return  " ${endemmark}${number} "
	}
	
	
	private String loadStartMark(item) {
	    return item.replaceAll(/^${startemmark}(\d+)$/) {it, cap -> 
		  return tags[Integer.parseInt(cap)]
	    }
	}

	private String loadEndMark(item) {
	    return item.replaceAll(/^${endemmark}(\d+)$/) {it, cap -> 
		  return tags[Integer.parseInt(cap)]
	    }
	}
	
	public String parse(File file) {
	   
	    String filename = file.name
	    String text = ""
	    file.eachLine{text += it}	   
	    
	    log.trace "File to process: $text"

	   	String id
	   	text.find(/(?si)<TITLE>(.*?)<\/TITLE>/) {all, g1 -> id = g1 }
	   	// let's get rid of all HTML tags..
	   	
	   	String t 
	   	t = text.find(/(?si)<BODY>(.*?)<\/BODY>/) {all, g1 -> t = g1 }  	    
	   //	println "text = $text \n\n t = $t"

	    // erase all LOCALITY    
	    t = t.replaceAll(/(?si)(<LOCALITY[^>]*?>[^<]*?<\/LOCALITY>)/) {all, g1->
	    	 //println "erasing $g1"
	    	 return ""
	    }

	   	
	    // REPLACING CONSTRUCAO 
	     t = t.replaceAll(/<CONSTRUCAO>/, "<LOCAL TIPO=\"HUMANO\" SUBTIPO=\"CONSTRUCAO\">") 
	     t = t.replaceAll(/<\/CONSTRUCAO>/, "</LOCAL>") 
	     
	     // REPLACING VIRTUAL 
	     t = t.replaceAll(/<VIRTUAL>/, "<LOCAL TIPO=\"VIRTUAL\">") 
	     t = t.replaceAll(/<\/VIRTUAL>/, "</LOCAL>") 
	     
	     t = t.replaceAll(/<VIRTUAL TIPO="(.*?)">/) {all, g1 -> 
	         return "<LOCAL TIPO=\"VIRTUAL\" SUBTIPO=\"${g1}\">"
	     }
	     
	     // let's protect close tags
	     t = t.replaceAll(/(?si)(<\/[^>]*?>)/) {all, g1 ->  
	    	return " ${saveEndMark(g1)} "
	     }
	     // let's protect remaining (== open) tags
	     t = t.replaceAll(/(?si)(<[^>]*?>)/) {all, g1 ->  
	    	return " ${saveStartMark(g1)} "
	     }
		     	 	

		   log.trace "File all prep to be tokenized: $t"
		    
	   	// now, let's atomize it... 
	   	Document d = new Document(docid:id, body:t)
	    d.preprocess() 	
	    
	   log.trace "Body sentences: "+ d.body_sentences
	    // now, we have to rebuild term by term:
	    // protected terms are reconverted (EM).
	    
	    d.body_sentences.eachWithIndex{sen, i -> 
	    	d.body_sentences[i] = parseSentence(sen, i, d)
	    }   
	   	//println d.body_sentences
	    return rw.printDocumentBody(d)
	 }

	  
	public Sentence parseSentence(Sentence s, int index, Document doc) {

	    int correctTermIndex = 0
	
	    List<NamedEntity> nes = []
	    
		Sentence s2 = new Sentence(index)
	    //log.debug "Sentence: $s"
		s.each{term -> 
			// case a new named entity:
			if (term.text.startsWith(startemmark)) {
			    NamedEntity ne = parseEMtag(loadStartMark(term.text))
			    ne.sentenceIndex=index
			    ne.termIndex=correctTermIndex			   
				log.trace "Got $term, opened NE $ne, there's ${nes.size()} nes open."
				nes << ne
			} else if (term.text.startsWith(endemmark)) {
			    if (!nes) {
					log.error "Closing EM, but there's no open EM!"
			    } 
			    		  
			    NamedEntity ne = nes.pop()
			    // let's confirm 
			    String endtag = loadEndMark(term.text)
			    def m = endtag =~ /<\/([^>]*?)>/
			    if (m.matches()) {
					def closingtag =  m.group(1)
					if (ne.classification.category[0] != closingtag) {
					    log.warn "Closing category = $closingtag, but opening category = ${ne.classification.category} for ne $ne!"
					    log.warn "But don't worry, i'll close the NE anyway."
					}
			    }
				doc.bodyNEs << ne		
				log.trace "added NE $ne to doc"				    
			} else {
				term.index = correctTermIndex++
				s2 << term
				// add to the NE if there is one
				nes.each{ne -> 
					ne.terms << term 
				}	
				log.trace "Got $term, added "
			}
	    }
		//println "Sentence2: $s2"

	    return s2
	 }
 
 	public NamedEntity parseEMtag(String tag) {
	    //<LOCAL TIPO="HUMANO" SUBTIPO="PAIS">
	    NamedEntity ne = new NamedEntity()
	    log.trace "tag: $tag"
	    def m = tag =~ /<(.*)>/
	    def hash_params = [:]
	    
	    if (m.matches()) {		
			
			List params = m.group(1).split(/ /)
			// first is always a category, without = or "	. Let's remove it from params
			hash_params['CATEG'] = params.remove(0)
			params.each{it -> 
				def m2 = it =~ /^(.*?)="(.*)"$/
				if (m2.matches()) {
				    hash_params[m2.group(1)] = m2.group(2)
				} else {
				    log.warn "Can't understand $it."
				}
			}
	    }
			
		def categ = null, type = null, subtype = null
		if(hash_params.containsKey('CATEG')) {categ = hash_params['CATEG']}
		if(hash_params.containsKey('TIPO')) {type = hash_params['TIPO']}
		if(hash_params.containsKey('SUBTIPO')) {subtype = hash_params['SUBTIPO']}
		
		ne.classification << new SemanticClassification(categ, type, subtype)

	   // log.trace "NamedEntity generated: $ne"
	    return  ne
	}
 }