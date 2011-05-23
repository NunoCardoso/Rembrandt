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
 
package rembrandt.rules.harem.en

import rembrandt.obj.TimeGrounding as TG
import rembrandt.obj.*
import rembrandt.rules.*
import rembrandt.gazetteers.en.*
import rembrandt.gazetteers.*
import org.apache.log4j.Logger


/**
 * @author Nuno Cardoso
 * Rules for capturing TEMPO entities.
 */
class TimeGroundingRulesEN extends TimeRulesEN {
    
    static Logger log = Logger.getLogger("TimeGroundingDetector")
     
    int solveMonth(String s) {
        if (s ==~ /\d{2}/) return Integer.parseInt(s)
        if (s ==~ /[Jj]an(uray)?\.?/) return 1
        if (s ==~ /[Ff]eb(ruary)?\.?/) return 2
        if (s ==~ /[Mm]ar(ch)?\.?/) return 3
        if (s ==~ /[Aa]pr(il)?\.?/) return 4
        if (s ==~ /[Mm]ay\.?/) return 5
        if (s ==~ /[Jj]une?\.?/) return 6
        if (s ==~ /[Jj]uly?\.?/) return 7
        if (s ==~ /[Aa]ug(ust)?\.?/) return 8
        if (s ==~ /[Ss]ept?(ember)?\.?/) return 9
        if (s ==~ /[Oo]ct(ober)?\.?/) return 10
        if (s ==~ /[Nn]ov(ember)?\.?/) return 11
        if (s ==~ /[Dd]ec(ember)?\.?/) return 12
        return -1
    }

    // you have to walk each character, minding the last one for correction
    int solveRoman(String s) {
        if (!s) return -1
        if (s.size() == 1) return solveRomanCharacter(s)
        // start with first character
        int res = solveRomanCharacter(s[0])
        if (res == -1) return res
        
        for(int i=1; i<s.size(); i++) {
            switch(s[i]) {
                case "I": res ++ ; break
                case "V": 
                   if (s[(i-1)] == "I") {res -= 1; res += 4}
                   else res += 5; break
                case "X": 
                   if (s[(i-1)] == "I") {res -= 1; res += 9}
                   else res += 10; break
                case "L": 
                    if (s[(i-1)] == "X") {res -= 10; res += 40}
                    else res += 50; break
                case "C": 
                    if (s[(i-1)] == "X") {res -= 10; res += 90}
                    else res += 100; break
                case "D": 
                    if (s[(i-1)] == "C") {res -= 100; res += 400}
                    else res += 500; break
                case "M": 
                    if (s[(i-1)] == "C") {res -= 100; res += 900}
                    else res += 1000; break
                default: 
                    return -1 // don't allow strange chars.
                break
            }                         
         }
        return res
     }

    int solveEra(String s) {
        // ac - antes de cristo 
        // bc - before christ
        // ad - anno domini
        // dc - depois de cristo
        // here, 0 is the 'do not know' value.       
        if ( (s ==~ /(?i).*d.*c.*/) || (s ==~ /(?i).*a.*d.*/)) return 1
        if ( (s ==~ /(?i).*a.*c.*/) || (s ==~ /(?i).*b.*c.*/)) return -1
        return 0        
        
    }
    
    int solveRomanCharacter(String s) {
	switch(s) {
        case "I": return 1; break
        case "V": return 5; break
        case "X": return 10; break
        case "L": return 50; break
        case "C": return 100; break
        case "D": return 500; break
        case "M": return 1000; break   
    }
    return -1
   }
    
    
    int solveNumber(s) {
        if (s instanceof String) s = [s]
        int i = 0 
        s.each{it ->
            if (it instanceof Term) it = it.text
            def m = it =~ /^(\d+)(?:th|nd|rd)?/
            if (m.matches()) {
                i += Integer.parseInt(m.group(1))
            } else {
        	//println "XX begin: it: $it i = $i"
                if (it ==~ /[Oo]ne/) i += 1
                if (it ==~ /[Ff]irst/) i += 1
                if (it ==~ /[Tt]wo/) i += 2 
                if (it ==~ /[Ss]econd/) i += 2 
                if (it ==~ /[Tt]hree/) i += 3
                if (it ==~ /[Tt]hird/) i += 3
                if (it ==~ /[Ff]our(?:th)?/) i += 4
                if (it ==~ /[Ff]ive/) i += 5 
                if (it ==~ /[Ff]ifth/) i += 5 
                if (it ==~ /[Ss]ix(?:th)?/) i += 6
                if (it ==~ /[Ss]even(?:th)?/) i += 7
                if (it ==~ /[Ee]ighth?/) i += 8
                if (it ==~ /[Nn]in(?:e|th)?/) i += 9
                if (it ==~ /[Tt]en(?:th)?/) i += 10
                if (it ==~ /[Ee]leven(?:th)?/) i += 11
                if (it ==~ /[Tt]wel(?:ve|fth)?/) i += 12
                if (it ==~ /[Tt]hirteen(?:th)?/) i += 13
                if (it ==~ /[Ff]ou?rteen(?:th)?/) i += 14
                if (it ==~ /[Ff]ifteen(?:th)?/) i += 15
                if (it ==~ /[Ss]ixteen(?:th)?/) i += 16
                if (it ==~ /[Ss]eventeen(?:th)?/) i += 17
                if (it ==~ /[Ee]iteen(?:th)?/) i += 18
                if (it ==~ /[Nn]ineteen(?:th)?/) i += 19
                if (it ==~ /[Tt]went(?:y|ieth)/) i += 20 
                if (it ==~ /[Tt]hirt(?:y|ieth)/) i += 30
                if (it ==~ /[Ff]ort(?:y|ieth)/) i += 40
                if (it ==~ /[Ff]ift(?:y|ieth)/) i += 50                
                if (it ==~ /[Ss]ixt(?:y|ieth)/) i += 60
                if (it ==~ /[Ss]event(?:y|ieth)/) i += 70              
                if (it ==~ /[Ee]ight(?:y|ieth)/) i += 80
                if (it ==~ /[Nn]inet(?:y|ieth)/) i += 90            
                if (it ==~ /[Hh]undred(?:th)?/) i += 100               
                if (it ==~ /[Tt]housand(?:th)?s?/) i += 1000      
                if (it ==~ /[Mm]illion(?:th)?s?/) i += 1000000 
               // println "XX end: it: $it i = $i"
            }
        }
        if (i == 0) i = -1 // eu não encontro zero, log ose continuar como zero, r
        return i      
    }
    
    // override the default Action
    public performActionsOnMatcherObject(MatcherObject o, obj) {
        
        //1st: call the default generateNEfromRule closure to get a NamedEntity
        NamedEntity newNE = super.generateNEfromRule(o) 
        // 2nd: call the rule action -> that's the action to ground time
        if (!o.rule.action) throw new IllegalStateException("rule ${o.rule} must have an action!")
        // this action will use TimeGrounding info from past NEs collected (if they exist) to 
        // improve the TimeGrounding status, and write it to the newNE.
        
        o.rule.action(o, newNE)  
        return newNE
    }
    
    /**
     * Main constructor
     */    
    public TimeGroundingRulesEN () {
         super()
        // now, the initial action-less rules for Time are set.
        // let's enhance them with grounding actions. variable rules is the super.rules

         int ruleindex
         List<Term> terms
        
	 /***************************/
	 String rule = "DATE-completa-1"
	 /****************************/
    // estes casos não são atomizados. Ou seja, são apenas um átomo, e não capturados como NUMERO
    // 20/02/2007, 2007/02, 2007/02/20, 20-02-2007, 2007-02, 2007-02-20, 20-02, 20/02, 2/3/2007	
    // ~/(?:)\d{2}[\/-]\d{2}|\d{2}[\/-]\d{4}|\d{1,2}[-\/]\d{1,2}[\/-]\d{2,4}|\d{4}[\/-]\d{2}|\d{4}[-\/]\d{1,2}[\/-]\d{1,2}/	
    // Clauses: TimeGazetteerEN.monthYearNumeral1c
    
	 ruleindex = rules.findIndexOf{it.id == rule}
 	 if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")
    
	 rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
        // newNE is the new namedEntity generated from the rule, without TimeGrounding info. 
        // we should resume TimeGrounding info from collected NEs that are going to be replaced
        // since this is the first rule, that's Ok, but the procedure is as follows:
	 	// get ne(s) from a NEMatch-TEMPO clause. If exist TG info, use them. 
	 	// if not, create new object.
	    TG tg = new TG()
	    String thisrule = o.rule.id // remember, we're in a closure... it's better to trust the rule from MatcherObject
        
            Clause needleclause 
            try { needleclause = TimeGazetteerEN.monthYearNumeral1c }
            catch(Exception e) { throw new IllegalStateException("Can't find clause TimeGazetteerEN.monthYearNumeral1c") }

 	    terms = o.pastMatches[needleclause].terms
	    if (!terms) throw new IllegalStateException("Can't find matched stuff by rule $thisrule!")    
        
            terms[0].text.find(/^(\d+)[\/-](\d+)$/) {all, g1, g2 -> 
                
            	// year month & day
                tg.datetype = TimeGroundingType.ABSOLUTE_DATE
                // assumir eras positivas
                tg.era1 = 1
                if (g1.size() == 4) { //it's a year
                    if ( Integer.parseInt(g2) > 12) {
                        tg.era2 = 1
                        tg.datetype = TimeGroundingType.INTERVAL
                        tg.year1 = Integer.parseInt(g1)
                        if (g2.size() == 4) {
                            tg.year2 = Integer.parseInt(g2)
                        } else {
                            tg.year2 = Integer.parseInt(g1.substring(0,2)) * 100 + Integer.parseInt(g2) 
                        }
                    } else {                    
                        tg.year1 = Integer.parseInt(g1)
                        tg.month1 = Integer.parseInt(g2)
                    }          
                } else if (g2.size() == 4) {
                    tg.year1 = Integer.parseInt(g2)
                    tg.month1 = Integer.parseInt(g1)
                } else {
                 /*   try {
                      int i = Integer.parseInt(g1)
                      if (i > 31) {
                	  tg.year1 = i
                          tg.month1 = Integer.parseInt(g2)
                      }
                    } catch (Exception e) {}
                    // como é PT, vamos assumir que o ano é o último
                    if (tg.year1 < 0) {
                        tg.year1 = Integer.parseInt(g2)
                        tg.month1 = Integer.parseInt(g1)
                    }*/
                }
            }// term find two of them
        
	    terms[0].text.find(/^(\d+)[\/-](\d+)[\/-](\d+)$/) {all, g1, g2, g3 -> 
                
                // year month & day
	    	tg.datetype = TimeGroundingType.ABSOLUTE_DATE
	    	// assumir eras positivas
	    	tg.era1 = 1
	    	if (g1.size() == 4) { //it's a year
                    tg.year1 = Integer.parseInt(g1)
                    tg.month1 = Integer.parseInt(g2)
                    tg.day1 = Integer.parseInt(g3)
                 } else if (g3.size() == 4) { //it's a year
                     tg.year1 = Integer.parseInt(g3)
                     tg.month1 = Integer.parseInt(g2)
                     tg.day1 = Integer.parseInt(g1)
                 } else {
                    try {
                       int i = Integer.parseInt(g1)
                       if (i > 31) {
                	   tg.year1 = i
                	   tg.month1 = Integer.parseInt(g2)
                	   tg.day1 = Integer.parseInt(g3)
                       } 
                    } catch (Exception e) {}
                    // como é PT, vamos assumir que o ano é o último
                    if (tg.year1 < 0) {
                	tg.year1 = Integer.parseInt(g3)
                	tg.month1 = Integer.parseInt(g2)
                	tg.day1 = Integer.parseInt(g1)
                    }
                }
	    }// term find three  of them
        
	    // in the end, attach TG info to the new NE, and return it
	    newNE.tg = tg
	    log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
	    return newNE
        } // rule_action
    
        /***************************/
         rule = "DATE-completa-2"
        /****************************/
        //:"{XX-/Fev-/XX}" ex: A data é 1997/Fevereiro/20 , a data é 20-Fev-1997 .

        // Clauses: TimeGazetteerEN.monthYearTextual1c
        
        ruleindex = rules.findIndexOf{it.id == rule} 
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg = new TG()
            String thisrule = o.rule.id 

            Clause needleclause 
            try { needleclause = TimeGazetteerEN.monthYearTextual1c }
            catch(Exception e) { throw new IllegalStateException("Can't find clause TimeGazetteerEN.monthYearTextual1c") }
            
            terms = o.pastMatches[TimeGazetteerEN.monthYearTextual1c].terms
            if (!terms) throw new IllegalStateException("Can't find matched stuff by rule $thisrule, clause ${TimeGazetteerEN.monthYearTextual1c} !")    
            
            tg.datetype = TimeGroundingType.ABSOLUTE_DATE
            // assumir eras positivas
            tg.era1 = 1
            // don't worry about .+... it's up to the rule to verify if it's really a textual month. I'm just splitting the string
            terms[0].text.find(/^(\d+)[\/-](.+)[\/-](\d+)$/) {all, g1, g2, g3 -> 
                try {
                    int i = Integer.parseInt(g1)
                    if (i > 31) {
                	tg.year1 = i
                	tg.month1 = solveMonth(g2)
                        tg.day1 = Integer.parseInt(g3)                         
                    }
                }catch(Exception e) {}
            
                if (tg.year1 < 0) {
                    tg.year1 = Integer.parseInt(g3)
                    tg.month1 = solveMonth(g2)
                    tg.day1 = Integer.parseInt(g1) 
                }
             }
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE
        }

	 /***************************/
	  rule = "DATE-completa-3"
	 /****************************/
   // 20 / 02 / 2007, 2007 / 02, 2007 / 02 / 20, 20 - 02 - 2007, 2007 - 02, 2007 - 02 - 20
   // rules.add(new Rule(id:"TEMPOCALEND1",description:"{<NUM>! -/! <NUM>! -/? <NUM>?}",
   // clauses: [    NEGazetteer.NE_NUMERO_1c,   CommonClauses.slashMinus1c,   
   // NEGazetteer.NE_NUMERO2_1c,   CommonClauses.slashMinus01c, NEGazetteer.NE_NUMERO_01c //
    
	 ruleindex = rules.findIndexOf{it.id == rule}
	 if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")
   
	 rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
      
	   TG tg = new TG()
	   String thisrule = o.rule.id 
       
           int leftterm = -1, middleterm = -1, rightterm = -1 
                                          
           leftterm =  solveNumber(o.pastMatches[NEGazetteer.NE_NUMERO_1c].terms[0].text)  
           middleterm =  solveNumber(o.pastMatches[NEGazetteer.NE_NUMERO2_1c].terms[0].text)                         
           def collectable = o.pastMatches[NEGazetteer.NE_NUMERO_01c]  // as this one is optional...
           if (collectable) rightterm =  solveNumber(collectable.terms[0].text)

           tg.datetype = TimeGroundingType.ABSOLUTE_DATE
        // assumir eras positivas
           tg.era1 = 1
	   //if leftTerm is >31, that's a year all right. The rest is month and day (if exists)
           if (leftterm > 31) {
               tg.year1 = leftterm; tg.month1 = middleterm; tg.day1 = rightterm
           }
	   if (middleterm > 31) { // that's a month / year pattern.
	       tg.year1 = middleterm; tg.month1 = leftterm
	   }
	   // default: year in the end, whether it's >31 or not
	   if (tg.year1 < 0) {
	       tg.year1 = rightterm
	       tg.month1 = middleterm
               tg.day1 = leftterm
	   }
          
	    newNE.tg = tg
	    log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
	    return newNE
       } // rule_action
    
	       
	/***************************/
	 rule = "DATE-Seculo-1"
	/****************************/
	    // description:''{[Ss][é]c(ulo)?s?! <NUM>! ac|dc?}',
	ruleindex = rules.findIndexOf{it.id == rule}
	if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")
	        
	rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
		String thisrule = o.rule.id 
		TG tg = new TG()
		tg.datetype = TimeGroundingType.INTERVAL
        
		def collectable = (o.pastMatches[TimeGazetteerEN.centurySuffix01c])
		if (collectable) tg.era1 = solveEra(collectable.terms[0].text)
       
		// If no one says if it's bc or ad, but I must know to compute the interval. 
		// let's assume positive, if nothins is said.        
		if (tg.era1 == 0) tg.era1 = 1
		tg.era2 = tg.era1   
        
		int seculo = solveNumber(o.pastMatches[NEGazetteer.NE_NUMERO_1c].terms[0].text)    
	        if (tg.era1 == 1) {	            
	            tg.year1 = (seculo -1)* 100 + 1
	            tg.year2 = seculo * 100
	        } else {
	            tg.year2 = (seculo -1) * 100 + 1
	            tg.year1 = seculo * 100
	        }
  	       
		newNE.tg = tg
		log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
		return newNE
	 }
	        
    /***************************/
    rule = "DATE-Seculo-2"
    /****************************/
	/*  3000 {<NUM>! ac|dc!}',NEGazetteer.NE_NUMERO_1c,  TimeGazetteerEN.centurySuffix1c]  */
	/* it can be a year or a century */
    ruleindex = rules.findIndexOf{it.id == rule}
    if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")
    
    rules[ruleindex].action = { MatcherObject o, NamedEntity newNE -> 
        String thisrule = o.rule.id 
        TG tg = new TG()
        
        int era = solveEra(o.pastMatches[TimeGazetteerEN.centurySuffix1c].terms[0].text)
        
        int seculo = solveNumber(o.pastMatches[NEGazetteer.NE_NUMERO_1c].terms[0].text)    

        // note: if século < 50, it's a century. Else, it's a year
        if (seculo < 50) {
            tg.datetype = TimeGroundingType.INTERVAL
            tg.era1 = era
            tg.era2 = era
            if (tg.era1 == 1) {	            
        	tg.year1 = (seculo -1) * 100 + 1
        	tg.year2 = seculo * 100 
            } else {
        	tg.year2 = (seculo-1) * 100 + 1
        	tg.year1 = seculo * 100
            }
        } else {
            tg.datetype = TimeGroundingType.ABSOLUTE_DATE
            tg.era1 = era
            tg.year1 = seculo 
        }
 
        newNE.tg = tg
        log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
        return newNE
    }
      
    /***************************/
    rule = "DATE-Seculo-3"
    /****************************/
    /*  '{[Ss][é]c(ulo)?s?! [IVX]+! ac|dc? e? [IVX]+?}', clauses: 
      TimeGazetteerEN.centuryRoman1c, TimeGazetteerEN.centurySuffix01c,
      TimeGazetteerEN.centuryRoman01c, TimeGazetteerEN.centurySuffix2_01c */
    
    ruleindex = rules.findIndexOf{it.id == rule}
    if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")
    
    rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
        String thisrule = o.rule.id 
        TG tg = new TG()
        tg.datetype = TimeGroundingType.INTERVAL
        
        def collectable = o.pastMatches[TimeGazetteerEN.centurySuffix01c]
  //      println "collectable1: $collectable"
        if (collectable) tg.era1 = solveEra(collectable.terms[0].text)
        collectable = o.pastMatches[TimeGazetteerEN.centurySuffix2_01c]
   //     println "collectable2: $collectable"
        if (collectable) tg.era2 = solveEra(collectable.terms[0].text)
    //    println "era1: ${tg.era1}"
        if (tg.era1 == 0) tg.era1 = 1
        if (tg.era2 == 0) tg.era2 = tg.era1 // if there's no second info, use whathever the first one got.        

        int seculo1 = solveRoman(o.pastMatches[TimeGazetteerEN.centuryRoman1c].terms[0].text)    
        int seculo2 
        collectable = o.pastMatches[TimeGazetteerEN.centuryRoman01c]
        if (collectable) seculo2 = solveRoman(collectable.terms[0].text)

        // vamos assumir que vai do mais antigo para o mais recente
        if (tg.era1 == 1) tg.year1 = (seculo1 -1)* 100 + 1
        else {
            tg.year1 = seculo1 * 100
     //       println "seculo: ${tg.year1}"
        }
        
        if (seculo2 > 0) {
            if (tg.era2 == 1) tg.year2 = seculo2* 100 
            else {
                
                tg.year2 = (seculo2 -1) * 100 +1
            }
            
            if (tg.era2 == -1) tg.year2 = (seculo2 -1) * 100 +1
            else tg.year2 = seculo2 * 100
        } else {
            if (tg.era1 == 1) tg.year2 = seculo1 * 100
            else tg.year2 = (seculo1 -1) * 100 + 1
        }
             
        newNE.tg = tg
        log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
        return newNE
    }
  
        /***************************/
        rule = "DATE-mes"
        /****************************/
        // [Ff]evereiro, etc. clauses: [ TimeGazetteerEN.monthText1c] ) )    
        // I have no access to previous TIME, so let's create a new TG 
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")
        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
                       
            TG tg = new TG()
            // let's assume it's absolute, for now
            tg.datetype = TimeGroundingType.ABSOLUTE_DATE
            // definir eras só quando há anos!

            String thisrule = o.rule.id 
            
            Clause needleclause 
            try { needleclause = TimeGazetteerEN.monthText1c}
            catch(Exception e) { throw new IllegalStateException("Can't find clause TimeGazetteerEN.monthText1c") }
            
            terms = o.pastMatches[TimeGazetteerEN.monthText1c].terms
            if (!terms) throw new IllegalStateException("Can't find matched stuff by rule $thisrule, clause ${needleclause} !")    
            
            tg.month1 = solveMonth(terms[0].text)
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE
        }


        /***************************/
        rule = "DATE-mes-X"
        /****************************/
        // [Ff]evereiro|[Ff]ev]]! [de|,]? <NUM>! -> forçar o número, senão apanho dez, mar, etc. 
        // clauses: TimeGazetteerEN.monthAll1c, ClausesEN.deCommaSlash01c, NEGazetteer.NE_TEMPO_NUMERO_1c    
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")
        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
                       
            TG tg
            // we may have a previous TEMPO clause matched, or a NUMERO
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_NUMERO_1c].nes[0]
            // let's make it easy: if it has t, it's a TEMPO. If not, it should have!
            if(ne?.tg) tg = ne.tg else {
            	tg = new TG()      
            	// let's assume it's absolute, for now
            	tg.datetype = TimeGroundingType.ABSOLUTE_DATE
            // assumir eras positivas
            	tg.era1 = 1
            }
            String thisrule = o.rule.id 
            
            Clause needleclause 
            try { needleclause = TimeGazetteerEN.monthAll1c }
            catch(Exception e) { throw new IllegalStateException("Can't find clause TimeGazetteerEN.monthAll1c") }
            
            terms = o.pastMatches[TimeGazetteerEN.monthAll1c].terms
            if (!terms) throw new IllegalStateException("Can't find matched stuff by rule $thisrule, clause ${needleclause} !")    
            tg.month1 = solveMonth(terms[0].text)

            terms = o.pastMatches[NEGazetteer.NE_TEMPO_NUMERO_1c].terms
            
            // TODO: this also captures centuries and months!
            if ( (terms[0].text ==~ /\d+/) &&  (tg.year1 < 0))
                 {
                tg.year1 = Integer.parseInt(terms[0].text)
                tg.era1 = 1
            }

            int month = solveMonth(terms[0].text)
            if (month > 0) tg.month1 = month
            
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE
        }

        /***************************/
        rule = "DATE-dia-X"
        /****************************/
        // '{<NUM>! de? <TEMPO>!}', NEGazetteer.NE_NUMERO_1c, ClausesEN.de01c, NEGazetteer.NE_TEMPO_DATA_1c
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")
        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            String thisrule = o.rule.id 
            TG tg
            // we have a previous TEMPO clause matched! Let's reuse it!
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_DATA_1c].nes[0]
            if(ne?.tg) tg = ne.tg else tg = new TG()
           
            terms = o.pastMatches[NEGazetteer.NE_NUMERO_1c].terms
            
            // NE_NUMERO_1c collected stuff may be "21", "vinte e um", 
            tg.day1 = solveNumber(terms*.text)

            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE
        }
        
        /***************************/
        rule = "DATE-diasemana-X"
        /****************************/
        // description:'{[[Qua]rta]-feira]! ,? <TEMPO DATA>?}' -> do nothing
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            NamedEntity ne
            TG tg
            String thisrule = o.rule.id           
            def collectable = o.pastMatches[NEGazetteer.NE_TEMPO_DATA_01c]
            if (collectable) ne = collectable.nes[0]
            if(ne?.tg) tg = ne.tg else tg = new TG()
            
            // add just a tg if there's none.
            if (!newNE.tg) newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE
        }

        
        /***************************/
        rule = "DATE-seasons"
        /****************************/
        // Natal|Consoada|Páscoa|Carnaval|Entrudo|Quaresma|Inverno|Primavera|Verão|Outono
        // TimeGazetteerEN.holidaysSeasons1c] ) )    
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            String thisrule = o.rule.id          
            TG tg = new TG()
            String text = o.pastMatches[TimeGazetteerEN.holidaysSeasons1c].terms[0].text
            
            int what = 0 
             
            if (text.equalsIgnoreCase("Christmas") || 
            text.equalsIgnoreCase("Xmas")) { what = 1; tg.era1 = 1; tg.day1 = 25; tg.month1 = 12; }
            
            if (text.equalsIgnoreCase("Winter")) { what = 2; tg.era1 = 1; tg.day1 = 1; tg.month1 = 1; 
                		     tg.era2 = 1; tg.day2 = 22; tg.month2 = 3; }
            if (text.equalsIgnoreCase("Spring")) { what = 2;  tg.era1 = 1; tg.day1 = 23; tg.month1 = 3; 
                		       tg.era2 = 1; tg.day2 = 20; tg.month2 = 6;  }
            if (text.equalsIgnoreCase("Summer")) {  what = 2;  tg.era1 = 1; tg.day1 = 21; tg.month1 = 6; 
                                     tg.era2 = 1; tg.day2 = 22; tg.month2 = 9; }
            if (text.equalsIgnoreCase("Autumn")) {  what = 2;  tg.era1 = 1; tg.day1 = 23; tg.month1 = 9; 
                                       tg.era2 = 1; tg.day2 = 21; tg.month2 = 12;  }      
            
            if (what == 1) tg.datetype = TimeGroundingType.PERIODICAL_DATE
            if (what == 2) tg.datetype = TimeGroundingType.INTERVAL
                        
            // add just a tg if there's none.
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"   
            return newNE
        }
        
        
        /***************************/
        rule = "DATE-ordinal-type-X"
        /****************************/
        //  segundo!+ [dia|mês|trimestre]! d[aoe]? <TEMPO|NUMERO>!
        // ex: no segundo trimestre de 2007, no terceiro dia da Quaresma
        //"DATE-ordinal-type-X", description:'segundo!+ [dia|mês|trimestre]! d[aoe]? <TEMPO|NUMERO>!',
        // NumberGazetteerEN.ordinalTextNumber1Pc, TimeGazetteerEN.selectedTimeTypesAll1c, ClausesEN.daeo01c, NEGazetteer.NE_TEMPO_NUMERO_1c
        
       ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
                   
            String thisrule = o.rule.id          
            TG tg
            // we may have a previous TEMPO clause matched, or a NUMERO
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_NUMERO_1c].nes[0]
            // let's make it easy: if it has t, it's a TEMPO. If not, it should have!
            if(ne?.tg) tg = ne.tg else {
                tg = new TG()      
                // let's assume it's absolute, for now
                tg.datetype = TimeGroundingType.ABSOLUTE_DATE
                // assumir eras positivas
                tg.era1 = 1
            }
            
            terms = o.pastMatches[NEGazetteer.NE_TEMPO_NUMERO_1c].terms
            // it's a year
            if (tg.year1 < 0 && terms[0].text.matches(~/\d+/)) {
                tg.year1 = Integer.parseInt(terms[0].text)
                tg.era1 = 1               
           }
                     
            int number = solveNumber( o.pastMatches[NumberGazetteerEN.ordinalTextNumber1Pc].terms )
            terms = o.pastMatches[TimeGazetteerEN.selectedTimeTypesAll1c].terms
            
            switch (terms[0].text) {
                case ~/seconds?/ : tg.second1 = number; break
                case ~/minutes?/ : tg.minute1 = number; break
                case ~/hours?/ : tg.hour1 = number; break                                
                case ~/days?/ : tg.day1 = number; break     
                case ~/weeks?/ : 
                    tg.datetype = TimeGroundingType.INTERVAL
                    tg.day1 = (number-1) * 7 +1;
                    tg.day2 = number * 7;
                    tg.month2 = tg.month1
                    tg.year2 = tg.year1
                    tg.era2 = tg.era1
                break   
                
                case ~/months?/ : tg.month1 = number; break     
                case ~/quarters?/ :
                    tg.datetype = TimeGroundingType.INTERVAL
                    if (number == 1) {	tg.month1 = 1; tg.day1 = 1; tg.month2 = 3; tg.day2 = 31; } 
                    if (number == 2) {	tg.month1 = 4; tg.day1 = 1; tg.month2 = 6; tg.day2 = 30; }
                    if (number == 3) {	tg.month1 = 7; tg.day1 = 1; tg.month2 = 9; tg.day2 = 30; } 
                    if (number == 4) {	tg.month1 = 10; tg.day1 = 1; tg.month2 = 12; tg.day2 = 31; }
                    
                    tg.year2 = tg.year1
                    tg.era2 = tg.era1
                
                break
                
                case ~/semest(?:re|er)s?/ :
                    tg.datetype = TimeGroundingType.INTERVAL
                    if (number == 1) {	tg.month1 = 1; tg.day1 = 1; tg.month2 = 6; tg.day2 = 30; } 
                    if (number == 2) {	tg.month1 = 7; tg.day1 = 1; tg.month2 = 12; tg.day2 = 31; }
                   
                    tg.year2 = tg.year1
                    tg.era2 = tg.era1         
                break
                
            }
            
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"   
            return newNE         
        }
            
        /***************************/
        rule = "DATE-number-type-X"
        /****************************/
        //  <NUM>? [dia|mês|semana|ano]! d[aeo]! <TEMPO|NUMERO>!
        // exemplo: dia de Março, mês de Março, dia de Natal, ano de 2007, 21 dias de abril
        // forçar o daeo!, senão apanha "anos 70"

        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            String thisrule = o.rule.id          
            TG tg
            // we may have a previous TEMPO clause matched, or a NUMERO
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_NUMERO_1c].nes[0]
            // let's make it easy: if it has t, it's a TEMPO. If not, it should have!
            if(ne?.tg) tg = ne.tg else {
                tg = new TG()      
                // let's assume it's absolute, for now
                tg.datetype = TimeGroundingType.ABSOLUTE_DATE
                // assumir eras positivas
                tg.era1 = 1
            }
              
            int number = -1
            def collectable = o.pastMatches[NEGazetteer.NE_NUMERO_01c]
            if (collectable) number = solveNumber( collectable.terms )
            
            terms = o.pastMatches[TimeGazetteerEN.selectedTimeTypesAll1c].terms
            if (number > 0) {
                switch (terms[0].text) {
                
                case ~/seconds?/ : tg.second1 = number; break
                case ~/minutes?/ : tg.minute1 = number; break
                case ~/hours?/ : tg.hour1 = number; break                                
                case ~/days?/ : tg.day1 = number; break     
                case ~/weeks?/ : 
                    tg.datetype = TimeGroundingType.INTERVAL
                    tg.day1 = (number-1) * 7 +1;
                    tg.day2 = number * 7;
                    tg.month2 = tg.month1
                    tg.year2 = tg.year1
                    tg.era2 = tg.era1
                    break   
                
                case ~/months?/ : tg.month1 = number; break     
                case ~/quarters?/ :
                    tg.datetype = TimeGroundingType.INTERVAL
                    if (number == 1) {	tg.month1 = 1; tg.day1 = 1; tg.month2 = 3; tg.day2 = 31; } 
                    if (number == 2) {	tg.month1 = 4; tg.day1 = 1; tg.month2 = 6; tg.day2 = 30; }
                    if (number == 3) {	tg.month1 = 7; tg.day1 = 1; tg.month2 = 9; tg.day2 = 30; } 
                    if (number == 4) {	tg.month1 = 10; tg.day1 = 1; tg.month2 = 12; tg.day2 = 31; }
                
                    tg.year2 = tg.year1
                    tg.era2 = tg.era1
                
                    break
                
                case ~/semest(?:re|er)s?/ :
                    tg.datetype = TimeGroundingType.INTERVAL
                    if (number == 1) {	tg.month1 = 1; tg.day1 = 1; tg.month2 = 6; tg.day2 = 30; } 
                    if (number == 2) {	tg.month1 = 7; tg.day1 = 1; tg.month2 = 12; tg.day2 = 31; }
                
                    tg.year2 = tg.year1
                    tg.era2 = tg.era1         
                    break
                }               
           }
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"   
            return newNE         
        }


        /***************************/
        rule = "DATE-middles-X"
        /****************************/
        // em! (princípios|meados|fins)! de! <NUMERO|TEMPO>
        // skip it          
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
                   
            TG tg
            String thisrule = o.rule.id           
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_NUMERO_1c].nes[0]
            if(ne?.tg) tg = ne.tg else tg = new TG()
            
            // add just a tg if there's none.
            if (!newNE.tg) newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE
                      
        }
        
        // TODO: it does nothing.
        /***************************/
        rule = "DATE-relative-3"
        /****************************/
        // em! (princípios|meados|fins)! de! <NUMERO|TEMPO>
        // skip it          
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            String thisrule = o.rule.id           
            if (!newNE.tg) newNE.tg = new TG()
            log.debug "rule $thisrule returning tg ${newNE.tg} for NE ${newNE}"
            return newNE
            
        }
        
        /***************************/
        rule = "DATE-relative-4"
        /****************************/
      // {<TEMPO>! (seguinte|passado|anterior)!}
            //NEGazetteer.NE_TEMPO_1c, ClausesEN.afterBeforeSince1c ] ) )       
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            String thisrule = o.rule.id       
            TG tg
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_1c].nes[0]
            if(ne?.tg) tg = ne.tg else tg = new TG()
            String afterBeforeSince1c = o.pastMatches[ClausesEN.afterBeforeSince1c].terms[0].text
            
            tg.datetype = TimeGroundingType.RELATIVE_ALL_DATES_AFTER
            
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }
       
        
        /***************************/
        rule = "HOUR-14h"
        /****************************/
        //   {\\d{1,2}[hH](ora)s!}
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg = new TG()    
            String thisrule = o.rule.id   
            tg.datetype = TimeGroundingType.ABSOLUTE_DATE
            terms = o.getMatchedTerms()
            terms[0].text.find(/^(\d+)\D*$/) {all, g1 -> 
                tg.hour1 = solveNumber(g1)
            } 
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }  
        
        /***************************/
        rule = "HOUR-14h-&-30m"
        /****************************/
        // 14h e? 30m. Há que ter atençao. 30m pode ser metros.
        // NEGazetteer.NE_TEMPO_HORA_1c, ClausesEN.e01c, Clause.newRegex1Clause(~/\d{1,2}[Mm](?:inuto)?s?/,"\\d{1,2}[Mm](inuto)?s?") ]))
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg 
            String thisrule = o.rule.id   
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_HORA_1c].nes[0]
            if(ne?.tg) tg = ne.tg else tg = new TG()
            
            terms = o.getMatchedTerms()
            terms[-1].text.find(/^(\d+)\D*$/) {all, g1 -> 
                tg.minute1 = solveNumber(g1)
            } 
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }  
        
        /***************************/
        rule = "HOUR-14_h"
        /****************************/
        //   {\\d{1,2}[hH](ora)s!}
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg = new TG()    
            String thisrule = o.rule.id   
            tg.datetype = TimeGroundingType.ABSOLUTE_DATE
            terms = o.getMatchedTerms()
            tg.hour1 = solveNumber(terms[0].text)         
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }  
        
        /***************************/
        rule = "HOUR-14 o'clock"
        /****************************/
        //   <NUM> o'clock
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg = new TG()    
            String thisrule = o.rule.id
            tg.hour1 = solveNumber(o.pastMatches[NEGazetteer.NE_NUMERO_1c].terms)            
            tg.datetype = TimeGroundingType.ABSOLUTE_DATE             
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }  
  
        /***************************/
        rule = "HOUR-14h:30m"
        /****************************/
        //   "\\d{1,2}[Hh:]\\d{1,2}[Mm]?"
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg = new TG()    
            String thisrule = o.rule.id   
            tg.datetype = TimeGroundingType.ABSOLUTE_DATE
            terms = o.getMatchedTerms()
            terms[0].text.find(/^(\d+)\D*(\d+)\D*$/) {all, g1, g2 -> 
                tg.hour1 = solveNumber(g1)         
                tg.minute1 = solveNumber(g2)
            }
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }       
        
        /***************************/
        rule = "HOUR-textual"
        /****************************/
        //   "XX horas e XX minutos",
        //  NEGazetteer.NE_TEMPO_HORA_1c, NEGazetteer.NE_NUMERO_1c
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 

            TG tg 
            String thisrule = o.rule.id              
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_HORA_1c].nes[0]
            if(ne?.tg) tg = ne.tg else tg = new TG()
            
            tg.minute1 =  solveNumber(o.pastMatches[NEGazetteer.NE_NUMERO_1c].terms)
           
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }  
        
  
        
        /***************************/
        rule = "HOUR-textual-2"
        /****************************/
        //<HORA>, <numero> minutos e <numero> segundos
        //NEGazetteer.NE_TEMPO_HORA_1c, NEGazetteer.NE_NUMERO_1c, NEGazetteer.NE_NUMERO2_1c
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg 
            String thisrule = o.rule.id   
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_HORA_1c].nes[0]
            if(ne?.tg) tg = ne.tg else tg = new TG()
            
            tg.minute1 = solveNumber(o.pastMatches[NEGazetteer.NE_NUMERO_1c].terms)
            tg.second1 = solveNumber(o.pastMatches[NEGazetteer.NE_NUMERO2_1c].terms)           
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE    
        }
        
       /***************************/
       rule = "HOUR-relative-X"
      /****************************/
            //   antes|depois|desde! das! <HORA>!
            //    ClausesEN.afterBeforeSince1c,  NEGazetteer.NE_TEMPO_HORA_1c
            ruleindex = rules.findIndexOf{it.id == rule}
            if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
            rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
                
                TG tg 
                String thisrule = o.rule.id   
                NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_HORA_1c].nes[0]
                if(ne?.tg) tg = ne.tg else tg = new TG()
                String direction = o.pastMatches[ClausesEN.afterBeforeSince1c].terms[0].text
                
                if (direction.equalsIgnoreCase("before")) tg.datetype = TimeGroundingType.RELATIVE_ALL_DATES_BEFORE
                else  tg.datetype = TimeGroundingType.RELATIVE_ALL_DATES_AFTER
                newNE.tg = tg
                log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
                return newNE                  
       }      
        
        
        /***************************/
        rule = "HOUR-afternoon"
        /****************************/
        //  <HORA>  da tarde, da manhã, da noite, da madrugada, da matina
        //        NEGazetteer.NE_TEMPO_HORA_1c,  ClausesEN.madrugadaManhaMatinaNoiteTarde1c
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg 
            String thisrule = o.rule.id   
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_HORA_1c].nes[0]
            if(ne?.tg) tg = ne.tg else tg = new TG()          
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }      
        
        
        /***************************/
        rule = "HOUR-day"
        /****************************/
        // <HORA> de amanhaDantesDepoisHojeOntem
        //NEGazetteer.NE_TEMPO_HORA_1c, afterBeforeTodayTomorrowYesterday1c
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg 
            TG tg2 =  new TG()
            String thisrule = o.rule.id   
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_HORA_1c].nes[0]
            if(ne?.tg) tg = ne.tg else tg = new TG()    
            
            String direction = o.pastMatches[ClausesEN.afterBeforeTodayTomorrowYesterday1c].terms[0].text
            
            if (direction.equalsIgnoreCase("yesterday")) {
                tg.datetype = TimeGroundingType.RELATIVE_DATE_IN_THE_PAST
                tg.period_type = TimeGrounding.day
                tg.period_amount = 1
                newNE.tg = tg
            }
            if (direction.equalsIgnoreCase("tomorrow")) {
                tg.datetype = TimeGroundingType.RELATIVE_DATE_IN_THE_FUTURE
                tg.period_type = TimeGrounding.day
                tg.period_amount = 1
                newNE.tg = tg
            }

            if (direction.equalsIgnoreCase("after")) {
                tg2.datetype = TimeGroundingType.RELATIVE_DATE_IN_THE_FUTURE
                if (tg.hour1 > 0) {
                    tg2.period_type = TimeGrounding.hour
                    tg2.period_amount = tg.hour1
                } else if (tg.minute1 > 0) {
                   tg2.period_type = TimeGrounding.minute
                   tg2.period_amount = tg.minute1
                } else if (tg.second1 > 0) {
                    tg2.period_type = TimeGrounding.second
                    tg2.period_amount = tg.second1
                }
                newNE.tg = tg2
            }
            
            if (direction.equalsIgnoreCase("before")) {
                tg2.datetype = TimeGroundingType.RELATIVE_DATE_IN_THE_PAST
                if (tg.hour1 > 0) {
                    tg2.period_type = TimeGrounding.hour
                    tg2.period_amount = tg.hour1
                } else if (tg.minute1 > 0) {
                    tg2.period_type = TimeGrounding.minute
                    tg2.period_amount = tg.minute1
                } else if (tg.second1 > 0) {
                    tg2.period_type = TimeGrounding.second
                    tg2.period_amount = tg.second1
                }
                newNE.tg = tg2
            }
            
            log.debug "rule $thisrule returning tg ${newNE.tg} for NE ${newNE}"
            return newNE                  
        }      
        


        /***************************/
        rule = "INTERVAL-1"
        /****************************/
        //{[entre|de|desde]! as? \\d{4}! e|a(té?)! as? \\d{4}!}",
        //TimeGazetteerEN.numeralYear1c TimeGazetteerEN.numeralYear1c_duplicate] ))
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg = new TG()
            String thisrule = o.rule.id   
            tg.year1 = solveNumber(o.pastMatches[TimeGazetteerEN.numeralYear1c].terms)
            tg.year2 = solveNumber(o.pastMatches[TimeGazetteerEN.numeralYear1c_duplicate].terms)
             tg.era1 = 1
             tg.era2 = 1   
            tg.datetype = TimeGroundingType.INTERVAL
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }     
        
        
        /***************************/
        rule = "INTERVAL-2"
        /****************************/
        //{[entre|de|desde]! as? <TEMPO!> e|a(té?)! as? <TEMPO>!}
        //NEGazetteer.NE_TEMPO_1c,NEGazetteer.NE_TEMPO_1c_duplicate] )) 
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg 
            String thisrule = o.rule.id   
            NamedEntity tempo1 = o.pastMatches[NEGazetteer.NE_TEMPO_1c].nes[0]
            NamedEntity tempo2 = o.pastMatches[NEGazetteer.NE_TEMPO_1c_duplicate].nes[0]
                        
            tg = tempo1.tg
            tg.datetype = TimeGroundingType.INTERVAL
           
            tg.era2 = tempo2.tg.era1;  tg.year2 = tempo2.tg.year1;  tg.month2 = tempo2.tg.month1; tg.day2 = tempo2.tg.day1; 
            tg.hour2 = tempo2.tg.hour1;  tg.minute2 = tempo2.tg.minute1; tg.second2 = tempo2.tg.second1; 
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }     
        
     
        /***************************/
        rule = "INTERVAL-decade-1" 
        /****************************/
        // the XXs
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg = new TG()
            String thisrule = o.rule.id 
            terms = o.getMatchedTerms()
            tg.year1 = 1900 + solveNumber( terms[1].text.replaceAll(/\D/,"")) // 'the' is the 0
            tg.year2 =  tg.year1 + 9
            tg.era1 = 1
            tg.era2 = 1   
            tg.datetype = TimeGroundingType.INTERVAL
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }     
        
        /***************************/
        rule = "INTERVAL-decade-2" 
        /****************************/
        // the XX 's"
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg = new TG()
            String thisrule = o.rule.id 
            terms = o.getMatchedTerms()
            tg.year1 = 1900 + solveNumber( terms[1]) // 'the' is the 0
            tg.year2 =  tg.year1 + 9
            tg.era1 = 1
            tg.era2 = 1   
            tg.datetype = TimeGroundingType.INTERVAL
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }     
        
        /***************************/
        rule = "INTERVAL-during"
        /****************************/
        // entre! os! <TEMPO> [ex: entre os <TEMPO>séculos XV e XVI</TEMPO>]
        // NEGazetteer.NE_TEMPO_1c
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg 
            String thisrule = o.rule.id  
            NamedEntity ne = o.pastMatches[NEGazetteer.NE_TEMPO_1c].nes[0] 
            if(ne?.tg) tg = ne.tg else tg = new TG()    
            tg.datetype = TimeGroundingType.INTERVAL
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }     
        
        /***************************/
        rule = "INTERVAL-two-years"
        /****************************/
        // Apanhar anos... Em 2001-/2002 
        // ~/[12][0-9]\d{2}[-\/][12][0-9]\d{2}/
        
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg = new TG()
            String thisrule = o.rule.id 
            terms = o.getMatchedTerms()
            if (!terms) throw new IllegalStateException("Can't find matched stuff by rule $thisrule!") 

            terms[-1].text.find(/^(\d+)[\/-](\d+)$/) {all, g1, g2 -> 
                 tg.year1 = solveNumber(g1)
                tg.year2 = solveNumber(g2)
                tg.era1 = 1
                tg.era2 = 1                                
            }
            tg.datetype = TimeGroundingType.INTERVAL
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }     
       
        /***************************/
        rule = "FREQUENCY-1"
        /****************************/
        //  "(diaria|semanal|anual)-mente",
        //  TimeGazetteerEN.timeFrequenceAdverbs1c 
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE -> 
            
            TG tg = new TG()   
            String thisrule = o.rule.id 
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }  
       
        
        /***************************/
        rule = "FREQUENCY-2"
        /****************************/
        //   todo o <X>, x = dia (Single), etc.
        // TimeGazetteerEN.selectedTimeTypesPlural1c
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE ->            
            TG tg = new TG()   
            String thisrule = o.rule.id 
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }  
        
        /***************************/
        rule = "FREQUENCY-3"
        /****************************/
        //  <NUMERO>! vezes! [ao|por]! <x>!
        // NEGazetteer.NE_NUMERO_1c,TimeGazetteerEN.selectedTimeTypesSingle1c
        ruleindex = rules.findIndexOf{it.id == rule}
        if (ruleindex < 0) throw new IllegalStateException("Can't find rule $rule on TimeRulesEN!")        
        rules[ruleindex].action = {MatcherObject o, NamedEntity newNE ->            
            TG tg = new TG()   
            String thisrule = o.rule.id 
            newNE.tg = tg
            log.debug "rule $thisrule returning tg $tg for NE ${newNE}"
            return newNE                  
        }  

    }
}
