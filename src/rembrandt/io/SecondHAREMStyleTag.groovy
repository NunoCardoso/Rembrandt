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
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.SemanticClassificationDefinitions
/**
 * @author Nuno Cardoso
 * StyleTags for the HAREM I evaluation contest
 */
class SecondHAREMStyleTag extends StyleTag {
    
    String NETag
    
    String categoryAttr
    String typeAttr
    String subtypeAttr 
    
    String relationAttr = "COREL"
    String relationTypeAttr = "TIPOREL"	
    String commentAttr
    
    String lang
    SemanticClassificationDefinitions scd
    
    public SecondHAREMStyleTag(String lang) {
        this.lang = lang
        
        switch(lang) {
            case "pt":
            NETag = "EM"
            categoryAttr = "CATEG"
            typeAttr = "TIPO"
            subtypeAttr = "SUBTIPO"
            commentAttr = "COMENT"
            break
            case "en":
            NETag = "NE"
            categoryAttr = "CATEGORY"
            typeAttr = "TYPE"
            subtypeAttr = "SUBTYPE"
            commentAttr = "COMMENT"
            break	
        }	
        
        scd = Class.forName("rembrandt.gazetteers."+lang+".SecondHAREMClassificationLabels"+(
                lang.toUpperCase())).newInstance()
    }
    
    public String printOpenTag(NamedEntity ne) {     
        
        String res = "<${NETag} ID=\"${ne.id}\""
        
        if (ne.classification*.c.findAll{it != null}) 
            res +=" ${categoryAttr}=\""+ne.classification*.c.collect{scd.label[it]}.join("|")+"\""
        
        if (ne.classification*.t.findAll{it != null}) 
            res += " ${typeAttr}=\""+ne.classification*.t.collect{scd.label[it]}.join("|").replaceAll("null","")+"\""
        
        if (ne.classification*.s.findAll{it != null}) 	     
            res += " ${subtypeAttr}=\""+ne.classification*.s.collect{scd.label[it]}.join("|").replaceAll("null","")+"\""
        
        if (ne.corel)  {
            res += " ${relationAttr}=\""+ne.corel.keySet().join(" ")+"\""
            res += " ${relationTypeAttr}=\""+ne.corel.values().join(" ")+"\""
        }
        
        if (!ne.comment) 
            res += " ${commentAttr}=\""+ne.comment.join(";").replaceAll("null","")+"\""
        
        return res+=">"
    }
    
    public NamedEntity parseOpenTag(String tag) {
  
        //<EM ID="H2-dftre765-32" CATEG="PESSOA|LOCAL" TIPO="POVO|HUMANO" SUBTIPO="|PAIS">
        NamedEntity ne = new NamedEntity()
        //  log.trace "tag: $tag"
        def m = tag =~ /<${NETag} (.*)>/
        if (m.matches()) {		
            def hash_params = [:]
            def params = m.group(1).split(/\s+/).each{it -> 
                def m2 = it =~ /^(.*?)="(.*)"$/
                if (m2.matches()) {
                    hash_params[m2.group(1)] = m2.group(2)
                } else {
                    log.warn "Can't understand $it."
                }
            }
            ne.id = hash_params.ID
            
            List categs, types, subtypes
            if (hash_params.containsKey(categoryAttr)) {categs = hash_params[categoryAttr].split(/\|/)}
            if (hash_params.containsKey(typeAttr)) {types = hash_params[typeAttr].split(/\|/)}
            if (hash_params.containsKey(subtypeAttr)) {subtypes = hash_params[subtypeAttr].split(/\|/)}
                
            /*Note: Groovy split() gives space on leading and middle tokens, as in 
             * assert "REGIAO||BATATAS".split(/\|/) == ["REGIAO", "", "BATATAS"]
             * assert "BATATAS||BATATAS".split(/\|/) -> ["BATATAS","" ,"BATATAS"]
             * assert "||BATATAS".split(/\|/) -> ["","" ,"BATATAS"]
             *  But for trailing ones, it's weird
             * assert "BATATAS||".split(/\|/) == ["BATATAS"]
             * So, we have to check indexes for incomplete types and subtypes in the end
             * That's why the code below is complex
             */
            //println "categs = $categs types = $types subtypes = $subtypes" 
            categs?.eachWithIndex{c, i -> 
                
                // here, is the careful code. 
                String cc1 = (categs ? (i < categs.size() ? (categs?.getAt(i) != null ? categs[i]: null) : null) : null) 
                String cc2 = (types ? (i < types.size() ?  (types.getAt(i) != "" ? types[i] : null) : null)  : null)
                String cc3 = (subtypes ? (i < subtypes.size() ?  (subtypes.getAt(i) != "" ? subtypes[i] : null) : null) : null) 
                
                List c1 = (cc1 ? scd.label.findAll{it.value == cc1}.collect{it.key} : null)
                List c2 =  (cc2 ? scd.label.findAll{it.value == cc2}.collect{it.key} : null)
                List c3 = (cc3 ? scd.label.findAll{it.value == cc3}.collect{it.key} : null)
                String c1s = null
                String c2s = null
                String c3s = null
                
                //println "i $i : $cc1 $cc2 $cc3"
                // conflicts: REGIAO - {@HUMANOREGIAO,@FISICOREGIAO}, OBRA: -{@OBRA, @OBRAARTIGO} 
                if (c1?.size() == 1) {c1s = c1[0]}
                else if (c1?.size() > 1) {
                    if (lang == "pt" && cc1 == "OBRA") {c1s = scd.category.masterpiece} //@OBRA
                }
                
                if (c2?.size() == 1) c2s = c2[0]
                
                if (c3?.size() == 1) c3s = c3[0]
                else if (c3?.size() > 1) {
                    if (lang == "pt") {
                        if (cc3 == "OBRA") {c3s == scd.subtype.article} //@OBRAARTIGO
                        if (cc3 == "REGIAO") {
                            if (c2 == scd.type.human) {c3s == scd.subtype.humanregion} //@HUMANOREGIAO
                            if (c2 == scd.type.physical) {c3s == scd.subtype.physicalregion} //@HUMANOREGIAO
                        } 
                    }
                }
                //println "c1s: $c1s c2s: $c2s c3s:$c3s"// $i : $cc1 $cc2 $cc3"
                
                SemanticClassification sc = new SemanticClassification(c1s, c2s, c3s)
                ne.classification << sc
            }
         } else {
                log.error "Did not matched tag: $tag."
         } 
         // log.trace "NamedEntity generated: $ne"
        return  ne
   }       
    
    
    public String printCloseTag(NamedEntity ne = null) {
        return "</$NETag>"
    }
    
    public boolean isOpenTag(String tag) {
        return tag.startsWith(/<EM /)
    }

    public boolean isCloseTag(String tag) {
        return tag.matches(/<\/EM>/)
    }
    
    public String printOpenALTTag() {
	return "<ALT>"
    } 
    
    public String printCloseALTTag() {
	return "</ALT>"
    } 
  
    public String printOpenSubALTTag(int index) {
	return "|"
    } 
    
    public String printCloseSubALTTag(int index) {
	return "|"
    } 
    
    public boolean isOpenALTTag(String tag) {
        return tag.equals("<ALT>")
    }
    
    public boolean isCloseALTTag(String tag) {
        return tag.equals("</ALT>")
    }
    
    public boolean isOpenSubALTTag(String tag) {
        return tag.equals("|")
    }
    
    public boolean isCloseSubALTTag(String tag) {
        return tag.equals("|")
    }
}