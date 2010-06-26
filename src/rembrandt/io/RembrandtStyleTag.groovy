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

package rembrandt.io

import rembrandt.obj.NamedEntity
import rembrandt.obj.TimeGrounding
import rembrandt.obj.SemanticClassification
import java.util.regex.Matcher
import saskia.dbpedia.DBpediaResource
import saskia.bin.Configuration
import rembrandt.gazetteers.SemanticClassificationDefinitions
import org.apache.log4j.Logger
/**
 * @author Nuno Cardoso
 * Default Rembrandt StyleTags that emcompass a tag for each classification.
 */
class RembrandtStyleTag extends StyleTag {
    
    String NEtag
    String NEidAttr = "ID"
    String categoryAttr = "C1"
    String typeAttr = "C2"
    String subtypeAttr = "C3"
    String relationIdAttr = "RI"
    String relationTypeAttr = "RT"
    String timeGroundingAttr = "TG"
    
    String commentAttr = "H" 
    String altTag = "ALT"

    // Extras
    String sentenceAttr = "S"
    String termAttr = "T"
    String dbpediaAttr="DB" 
    String wikipediaAttr="WK"
	    
    // Basic symbols
    String openTagSymbol = "<"
    String closeTagSymbol = ">"		
    
    SemanticClassificationDefinitions scd
    int verbose
    static Logger log = Logger.getLogger("Reader")
    String lang
    /**
     * Public constructor.
     * @param lang the language of the labels. Default: rembrandt.output.styletag.lang, or 
     * global.lang if it's not specified.
     */
    public RembrandtStyleTag(String lang) {
        this.lang = lang
        scd = Class.forName("rembrandt.gazetteers."+lang+".SecondHAREMClassificationLabels"+(
        	lang.toUpperCase())).newInstance()
        NEtag = SemanticClassificationDefinitions.netag[lang.toLowerCase()]
        verbose = Configuration.newInstance().getInt("rembrandt.output.styletag.verbose", 2)
        log.debug "RembrandtStyleTag initialized for language $lang, verbosity level $verbose, looking for <${NEtag}>"
    }
    
    
    public String printOpenTag(NamedEntity ne) {     
        
	StringBuffer sb = new StringBuffer()       
    
	if (verbose > 2) sb.append "\n===\n"
    
        if (ne.classification) {
           
            ne.classification.each{c ->
              
                sb.append openTagSymbol
                sb.append NEtag

                // Can't do if... when 0, it does not print!
                if (verbose > 0) {
                    if (ne.id) sb.append " ${NEidAttr}=\"${ne.id}\""
                    sb.append " ${sentenceAttr}=\"${ne.sentenceIndex}\""	 
                    sb.append " ${termAttr}=\"${ne.termIndex}\""
                }
                
                if (c.c) sb.append " ${categoryAttr}=\"${scd.label[c.c]}\""
                if (c.t) sb.append " ${typeAttr}=\"${scd.label[c.t]}\""
                if (c.s) sb.append " ${subtypeAttr}=\"${scd.label[c.s]}\""
                
                if (verbose > 1) {
                    
                    if (ne.tg) {sb.append " ${timeGroundingAttr}=\"${ne.tg}\""}
                    
                    if (ne.corel) {sb.append " ${relationIdAttr}=\"${ne.corel.keySet().join(';')}\""
                     sb.append " ${relationTypeAttr}=\"${ne.corel.values().join(';')}\""}
                
                    List<String> wikipages = ne.wikipediaPage[c]
                    if (wikipages) sb.append " ${wikipediaAttr}=\"${wikipages.unique().join(';').replaceAll(' ','_')}\""
                
                    List<String> dbpediapages = ne.dbpediaPage[c]
                    if (dbpediapages) sb.append " ${dbpediaAttr}=\""+dbpediapages.unique().collect{
                         it.replaceAll(/${DBpediaResource.resourcePrefix}/,"")}.join(";")+"\""
                }
                 sb.append closeTagSymbol
            }
            if (verbose > 2) sb.append "<${commentAttr}>\n===\n${ne.printHistory()}\n===\n</${commentAttr}>"    
        }
 
        return sb.toString()
    }
    
    public String printCloseTag(NamedEntity ne = null) {
        
        StringBuffer res = new StringBuffer()    
        if (ne.classification*.c) {
            // reverse? Last open, first close... of course.
            ne.classification*.c.reverse().each{res.append  "${openTagSymbol}/${NEtag}${closeTagSymbol}"}
        }
        return res
    }
    
    public boolean isOpenTag(String tag) {
	return tag ==~ /<${NEtag} [^>]*>/
    }
    
    public boolean isCloseTag(String tag) {
	return tag ==~ /<\/${NEtag}>/
    }    
    
    public boolean isOpenALTTag(String tag) {
        return tag.equals("<ALT>")
    }
    
    public boolean isCloseALTTag(String tag) {
        return tag.equals("</ALT>")
    }
    
    public boolean isOpenSubALTTag(String tag) {
        return tag.equals("<SUBALT>")
    }
    
    public boolean isCloseSubALTTag(String tag) {
        return tag.equals("</SUBALT>")
    }
    
    public String printOpenALTTag() {
	return "<ALT>"
    } 
    
    public String printCloseALTTag() {
	return "</ALT>"
    } 
  
    public String printOpenSubALTTag(int index) {
	return "<SUBALT>"
    } 
    
    public String printCloseSubALTTag(int index) {
	return "</SUBALT>"
    } 
    
    public NamedEntity parseOpenTag(String tag) {
	
	NamedEntity ne = new NamedEntity()
	    
	Matcher m = tag =~ /<${NEtag} ([^>]*)>/
	if (m.matches()) {		
	  Map hash_params = [:]
	  m.group(1).split(/\s+/).each{it -> 
	       Matcher m2 = it =~ /^(.*?)="(.*)"$/
	       if (m2.matches()) {
		   hash_params[m2.group(1)] = m2.group(2)
	       } else {
		   log.warn "Can't understand $it."
	       }
	  }
    
          List c1 = (hash_params.C1 ? scd.label.findAll{it.value == hash_params.C1}.collect{it.key} : null)
          List c2 =  (hash_params.C2 ? scd.label.findAll{it.value == hash_params.C2}.collect{it.key} : null)
          List c3 = (hash_params.C3 ? scd.label.findAll{it.value == hash_params.C3}.collect{it.key} : null)
          String c1s = null
          String c2s = null
          String c3s = null
        
      
          // conflicts: REGIAO - {@HUMANOREGIAO,@FISICOREGIAO}, OBRA: -{@OBRA, @OBRAARTIGO} 
          if (c1?.size() == 1) {c1s = c1[0]}
          else if (c1?.size() > 1) {
            if (lang == "pt" && hash_params.C1 == "OBRA") {c1s = scd.category.masterpiece} //@OBRA
          }
    
          if (c2?.size() == 1) c2s = c2[0]
        
          if (c3?.size() == 1) c3s = c3[0]
          else if (c3?.size() > 1) {
    	  if (lang == "pt") {
    	      if (hash_params.C3 == "OBRA") {c3s == scd.subtype.article} //@OBRAARTIGO
                  if (hash_params.C3 == "REGIAO") {
                    if (c2 == scd.type.human) {c3s == scd.subtype.humanregion} //@HUMANOREGIAO
                    if (c2 == scd.type.physical) {c3s == scd.subtype.physicalregion} //@HUMANOREGIAO
          	      } 
              }
          }
        
        SemanticClassification sc = new SemanticClassification(c1s, c2s, c3s)
	ne.classification << sc
           
        if (hash_params.ID) ne.id = hash_params.ID
        if (hash_params.S) ne.sentenceIndex = Integer.parseInt(hash_params.S)
        if (hash_params.T) ne.termIndex = Integer.parseInt(hash_params.T)

        if (hash_params.TG) ne.tg = TimeGrounding.parseString(hash_params.TG)

        if (sc && hash_params.DB) 
             ne.dbpediaPage[sc] = hash_params.DB.split(/;/).collect{"${DBpediaResource.resourcePrefix}${it}"}

        if (sc && hash_params.WK) 
            ne.wikipediaPage[sc] = hash_params.WK.split(/;/).collect{ "http://${lang}.wikipedia.org/wiki/${it}"}
            
        if (hash_params.RI) {
                List rt = hash_params.RT.split(/;/)
                hash_params.RI.split(/;/).eachWithIndex{ri, i -> ne.corel[ri] = rt[i] }
        }
          	 
	} else {
	    log.warn("Did not matched tag: $tag. Returning null")
	    return null
	} 

	return  ne
   }
}