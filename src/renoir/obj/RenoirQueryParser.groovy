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
package renoir.obj
import java.util.regex.Matcher
import org.apache.log4j.Logger
import pt.utl.ist.lucene.Globals
import rembrandt.tokenizer.TokenizerPT
import rembrandt.obj.Sentence
/**
 * @author Nuno Cardoso
 *
 */
class RenoirQueryParser {
    // col is NOT a variable
    // label can be used, for example, on TREC runs 
    static List RenoirVars = [/label/,/qe/,/search/,/explain/,/output/,/limit/,/offset/,/stem/]
    static List IndexVars = [/.*?-index/,/contents/,/entity/] // contents is the Globals.LUCENE_DEFAULT_FIELD
    static List LGTEVars = [/model/]
    static List LGTEParameters = [/(?i)bm25\..*/,/(?i)QE\..*/,/.*?-weight/]

// in LGTEParameters, if I get a (.*)-weight, convert it to model.field.boost.$1 ,as in 
// queryConfiguration.setProperty("model.field.boost.contents","0.3f");


    static List integerVars = [/limit/,/offset/]
                               
    static String defaultTermField = Globals.LUCENE_DEFAULT_FIELD
                            
    static Logger log = Logger.getLogger("RenoirQuestionSolver")      
    static TokenizerPT tok = TokenizerPT.newInstance()
    
    
    static RenoirQuery parse(String text) {
	
	RenoirQuery q = new RenoirQuery()
	q.queryString = text
	q.sentence = new Sentence(0)
	
	// hide the .:., replace them to § symbols
	String text2 = text.replaceAll(/(?<=.):(?=.)/, "§")
	// Now, if there is a field (given by §), replace in the field all '.' in the left with '±'
	text2 = text2.replaceAll(/(\S*)(?=§)/) {all, g1 -> return g1.replaceAll(/\./, "±")}
	
        List sentences = TokenizerPT.newInstance().parse(text2)
        
        // restore ± to ., restore § to :
        sentences.each{s -> 
           s.each{t -> 
           	t.text = t.text.replaceAll(/±/,".").replaceAll(/§/,":") 
           }
        }
        
      //  println sentences
        // we're intrested in the first sentence.
        if (!sentences) 
            log.warn "Got text $text, but no sentences!"
        
        boolean phraseBegin = false
        boolean phraseEnd = false
        boolean phraseSwitch = false
        
        String currentBIOstate = "O" // use only I and O    
        String currentField = "contents" // for exact phrases that are tokenized    
        
        int termindex = 0 // to use and label the question.sentence terms.
        
        sentences[0].each{it -> // this is a regular Term. We need to concert it into RenoirQueryTerm
                
           String item = it
           RenoirQueryTerm term = new RenoirQueryTerm("") // empty string as text
           phraseBegin = false
           phraseEnd = false
           phraseSwitch = false
           
            Matcher m = item =~ /(.+):(.+)/
           if (m.matches()) {
              term.field = m.group(1)
              item = m.group(2) // rewrite the item without the field
           }
        
           //if it matches a leading quotation marks with something else
           m = item =~ /^"(.+)/
           if (m.matches()) { //"
               phraseBegin = true
               item = m.group(1) // rewrite the item without commas field     
           }
        
           // if it ends with a ^\d+
           m = item =~ /(.+)\Q^\E([\d\.]+)$/
           if (m.matches()) { //"
             term.weight = Float.parseFloat(m.group(2))
             item = m.group(1)
           }
        
           // if it ends with a " 
           m = item =~ /(.+)"$/
           if (m.matches()) { //"
               phraseEnd = true
               item = m.group(1)
           }
           
           // now let's check if it always was a single "
           if (item.equals('"')) {
               phraseSwitch = true     
           }
           
           log.trace "4. Item:$item, term:$term term.field:${term.field}  phraseBegin:$phraseBegin "+
           "phraseEnd:$phraseEnd phraseSwitch:$phraseSwitch"

           // now we have something in item, can be a term, or can be a parameter. 
           // watch out for null stuff
           
            if (term.field) {
        	// first, let's handle the parameters... 
                                   
        	   if (LGTEVars.find{term.field ==~ it}) {      
        	       log.trace "Adding a LGTEVar"
       	    	   	q.paramsForLGTE[term.field] = item        
       	    	   	//currentField = term.field
       	    	   	//currentBIOstate = "O"
        	   } else if (RenoirVars.find{term.field ==~ it}) {   
        	       log.trace "Adding a RenoirVar"
        	       if (integerVars.find{term.field ==~ it}) {
        		   q.paramsForRenoir[term.field] = Integer.parseInt(item)   
        	       } else {
        		   q.paramsForRenoir[term.field] = item        
        	       }
      	    	       //currentField = term.field
       	    	       //currentBIOstate = "O"
        	   } else if (LGTEParameters.find{term.field ==~ it}) {      
        	       log.trace "Adding a LgteQueryConfigurationVar"

					def mx = term.field =~ /^(.*)-weight/
					if (mx.matches()) {
						q.paramsForQueryConfiguration["model.field.boost."+mx.group(1)+"-index"]=""+item+"f"
						
//						queryConfiguration.setProperty("model.field.boost.contents","0.3f");
					} else {
        	       	q.paramsForQueryConfiguration[term.field] = item  
					}   
      	    	       //currentField = term.field
       	    	       //currentBIOstate = "O"
        	   } 
               
               //Now, these are terms with explicit field... 
               //as they are explicit, let's believe what they are, and write to currentX
        	   
        	   else if (IndexVars.find{term.field ==~ it}) {
        	      currentField = term.field
        	      if (phraseBegin) {
        		  term.phraseBIO = "B"
        		  currentBIOstate = "B" // for OB + term added
         		  term.text = item 
         		  term.index = termindex++
                	  q.sentence << term
               		  log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      
        	      }
        	      if (phraseEnd) {    
        		  if (q.sentence[q.sentence.size()-1].phraseBIO != "B") {
        		      term.phraseBIO = "B"
        		  } else { term.phraseBIO = "I" }
        		  
        		  // if we have a weight score, propagate for back terms until finding a B 
        		  if ((term.phraseBIO == "I") && (term.weight)) {
        		      for (int i = q.sentence.size()-1; i >= 0; i--) {
        			  if (q.sentence[i].phraseBIO == "O") break // break before writing
        			  q.sentence[i].weight = term.weight
        			  if (q.sentence[i].phraseBIO == "B") break // break after writing
        			  // if I, keep going
        		      }      
        		  }
            		  currentBIOstate = "O" // IO is a transition 
            		  currentField = defaultTermField // turn off the currentField
            		  term.text = item 
            		  term.index = termindex++
            		  q.sentence << term
               		  log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      
        	      }
        	      if (phraseSwitch) {
        		  // note that this is with '"' only, so it's not to add term
        		  if (currentBIOstate == "O" || currentBIOstate == "IO") {
        		      currentBIOstate = "OB"
        		  } else if (currentBIOstate == "B" || currentBIOstate == "I") {
        		      currentBIOstate = "IO"      
        		      currentField = defaultTermField // turn off the currentField
        		      // if we have a weight score, propagate
        		      if (term.weight) {
            		      	for (int i = q.sentence.size()-1; i >= 0; i--) {
            			  if (q.sentence[i].phraseBIO == "O") break
            			  q.sentence[i].weight = term.weight
              			  if (q.sentence[i].phraseBIO == "B") break // break after writing

            		      	}      
            		      }
        		  } else {
        		     log.error "Problem here! Don't know how to handle $currentBIOstate"
        		  }
        		  
               		  log.trace "Phrase switching: now we have $currentBIOstate"
        	      }
        	      
        	      if (!phraseBegin && !phraseEnd && !phraseSwitch) {
        		  if (currentBIOstate == "O") {
        		      currentBIOstate = "O"
        		      term.phraseBIO = "O"
        	              term.text = item 
        	              term.index = termindex++
                    	      q.sentence << term
        	              log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      

        		  } else if (currentBIOstate == "OB") {
        		      currentBIOstate = "B"
                	      term.phraseBIO = "B"
                              term.text = item 
                              term.index = termindex++
                    	      q.sentence << term
               	              log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      

            		  }  else if (currentBIOstate == "B") {
        		      currentBIOstate = "I"
            		      term.phraseBIO = "I"
            	              term.text = item 
            	          term.index = termindex++
            	          q.sentence << term
            	              log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      

        		  }  else if (currentBIOstate == "I") {
        		      currentBIOstate = "I"
                	      term.phraseBIO = "I"
                		  term.text = item 
                		  term.index = termindex++
                        	  q.sentence << term
                		log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      

            		  }  else if (currentBIOstate == "IO") {
        		      currentBIOstate = "O"
                    	      term.phraseBIO = "O"
                    		  term.text = item 
                    		term.index = termindex++
                      	  q.sentence << term
                    		log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      

                	  }       		      
        	      }
        	   } // else if varsIndex
    
              } else {
        	  
        	  // repeat above, but try to inherit fields inside sentences
        	  if (phraseBegin) {
        	        term.field = currentField
        	  
    		  	term.phraseBIO = "B"
    		        currentBIOstate = "B" // for OB + term added
    		 	term.text = item 
    		 	term.index = termindex++
              	  q.sentence << term
           	        log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      
    	         }
    	      	  if (phraseEnd) {    
    	      	      if (q.sentence[q.sentence.size()-1].phraseBIO != "B") {
    	      		  term.phraseBIO = "B"
    	      	      }else { term.phraseBIO = "I" }
    	      	      
    	      	      if ((term.phraseBIO == "I") && (term.weight)) {
    	      		  for (int i = q.sentence.size()-1; i >= 0; i--) {
    	      		      if (q.sentence[i].phraseBIO == "O") break
    	      		      q.sentence[i].weight = term.weight
          		      if (q.sentence[i].phraseBIO == "B") break // break after writing

    	      		  }      
    	      	      }
    	      	       term.field = currentField
        	       currentBIOstate = "O" // IO is a transition 
        	       currentField = defaultTermField // turn off the currentField
        	       term.text = item 
        	       term.index = termindex++
             	  q.sentence << term
           	       log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      
    	      	  }
    	      	  if (phraseSwitch) {
    		  // note that this is with '"' only, so it's not to add term
    	      	      if (currentBIOstate == "O" || currentBIOstate == "IO") {
    	      		  currentBIOstate = "OB"
    	      	      } else if (currentBIOstate == "B" || currentBIOstate == "I") {
    	      		  currentBIOstate = "IO"  
    	      		      
    	       		  currentField = defaultTermField // turn off the currentField
    	       		  if (term.weight) {
    	       		      for (int i = q.sentence.size()-1; i >= 0; i--) {
    	       			  if (q.sentence[i].phraseBIO == "O") break
    	       			  q.sentence[i].weight = term.weight
    	      			  if (q.sentence[i].phraseBIO == "B") break // break after writing

    	       		      }      
    	       		  }

    	      	      } else {
    	      		  log.error "Problem here! Don't know how to handle $currentBIOstate"
    	      	      }
    	      	      log.trace "Phrase switching: now we have $currentBIOstate"
    	      	  }
    	      
    	      	  if (!phraseBegin && !phraseEnd && !phraseSwitch) {
    	      	      if (currentBIOstate == "O") {
    	      		  currentBIOstate = "O"
    	      		  term.phraseBIO = "O"
    	      		  term.field = currentField  
    	      	          term.text = item 
    	      	term.index = termindex++
      	  q.sentence << term
    	                  log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      

    		      } else if (currentBIOstate == "OB") {
    		          currentBIOstate = "B"
    		          term.phraseBIO = "B"
        	      	  term.field = currentField
        	      	  term.text = item 
        	      	term.index = termindex++
              	  q.sentence << term
           	          log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      

        	      }  else if (currentBIOstate == "B") {
    		      	  currentBIOstate = "I"
        		  term.phraseBIO = "I"
        	      	  term.field = currentField
        	      	  term.text = item 
        	      	term.index = termindex++
              	  q.sentence << term
        	          log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      

        	      }  else if (currentBIOstate == "I") {
    		      	  currentBIOstate = "I"
            	          term.phraseBIO = "I"
        	      	  term.field = currentField
        	      	  term.text = item 
        	      	term.index = termindex++
              	  q.sentence << term
            		  log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      
        	      }  else if (currentBIOstate == "IO") {
    		      	  currentBIOstate = "O"
                	  term.phraseBIO = "O"
        	      	  term.field = currentField
        	      	  term.text = item 
        	      	term.index = termindex++
              	  q.sentence << term
                	  log.trace "Adding a term '${term.text}', BIO ${term.phraseBIO}, field=${term.field}"                	      
        	      }
            	  }       		        
               } // if term.field      
    	    }              
    
	return q  
    }
}
