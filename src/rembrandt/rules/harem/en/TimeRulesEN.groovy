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

import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.RulePolicy
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.en.TimeGazetteerEN
import rembrandt.gazetteers.en.NumberGazetteerEN
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClauses
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector
 
/**
 * @author Nuno Cardoso
 * Rules for capturing TIME entities.
 */
class TimeRulesEN extends NamedEntityDetector {

    List<Rule> rules
    SemanticClassification sc

    /**
     * Main constructor
     */    
    public TimeRulesEN() {
	
	rules = []

        // estes casos n�o s�o atomizados. Ou seja, s�o apenas um �tomo, e n�o capturados como NUMERO
        // 20/02/2007, 2007/02, 2007/02/20, 20-02-2007, 2007-02, 2007-02-20, 20-02, 20/02  
        // 2/3/2007
	rules.add(new Rule(id:"DATE-completa-1",description:"{XX-/XX-/XX}",
            policy:RulePolicy.Rule, sc:SC.time_calendar_date, clauses: [ 
                   TimeGazetteerEN.monthYearNumeral1c]))       

       rules.add(new Rule(id:"DATE-completa-2",description:"{XX-/Fev-/XX}", policy:RulePolicy.Rule, 
        sc:SC.time_calendar_date, clauses: [ 
        TimeGazetteerEN.monthYearTextual1c  ] ) )     
        
        // 20 / 02 / 2007, 2007 / 02, 2007 / 02 / 20, 20 - 02 - 2007, 2007 - 02, 2007 - 02 - 20
         // 20 - 02, 20 / 02,  
        rules.add(new Rule(id:"DATE-completa-3",description:"{<NUM> -/! <NUM> -/? </NUM>?}",
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [ 
                   NEGazetteer.NE_NUMERO_1c, 
                   CommonClauses.slashMinus1c, 
                   NEGazetteer.NE_NUMERO2_1c, 
                   CommonClauses.slashMinus01c, 
                   NEGazetteer.NE_NUMERO_01c ]))

// século! {NUMERO}! (acdc)?
rules.add(new Rule(id:"DATE-Seculo-1", description:'{[Ss][é]c(ulo)?s?! <NUM>! ac|dc?}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule,                       
        clauses: [TimeGazetteerEN.century1c, 
        NEGazetteer.NE_NUMERO_1c, 
        TimeGazetteerEN.centurySuffix01c ] ) )    

// 3000 a.C.
rules.add(new Rule(id:"DATE-Seculo-2", description:'{<NUM>! ac|dc!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [                    
        NEGazetteer.NE_NUMERO_1c, 
        TimeGazetteerEN.centurySuffix1c] ) )  


// século! {NUMERO-ROMANO}! (acdc)?
rules.add(new Rule(id:"DATE-Seculo-3", description:'{[Ss][é]c(ulo)?s?! [IVX]+! ac|dc? e? [IVX]+?}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [
        TimeGazetteerEN.century1c, 
        TimeGazetteerEN.centuryRoman1c, 
        TimeGazetteerEN.centurySuffix01c,
        ClausesEN.and01c,
        TimeGazetteerEN.centuryRoman01c,
        TimeGazetteerEN.centurySuffix2_01c] ) )    

 // [Ff]evereiro, 
rules.add(new Rule(id:"DATE-mes", description:'{[[Ff]evereiro]}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [                                    
        TimeGazetteerEN.monthText1c] ) )    

 // [[Ff]evereiro|[Ff]ev]]! [de|,]? <NUM|TEMPO>! -> forçar o número, senão apanho dez, mar, etc. 
// tempo também, pode ser ano ou século
rules.add(new Rule(id:"DATE-mes-X", description:'{[[Ff]evereiro|[Ff]ev.?]]! [de|,]? <NUM|TEMPO>!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [                                    
        TimeGazetteerEN.monthAll1c, 
        ClausesEN.ofCommaSlash01c, 
        NEGazetteer.NE_TEMPO_NUMERO_1c] ) )    


// splitted so that optimizer can start with 1 clauses         
rules.add(new Rule(id:"DATE-dia-X", description:'{<NUM>! of? <TEMPO>!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [      		
        NEGazetteer.NE_NUMERO_1c, 
        ClausesEN.of01c, 
        NEGazetteer.NE_TEMPO_DATA_1c] ) ) 
       
        // {[Wed.|Wednesday|...]! ,? <TIME DATE>?}, 
        // {[Wed.|Wednesday|...]!
        rules.add(new Rule(id:"DATE-diasemana-X", description:'{[Wed.|Wednesday|...]! ,? <TIME DATE>?}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [
                 TimeGazetteerEN.dayWeek1c, 
                 CommonClauses.comma01c, 
                 NEGazetteer.NE_TEMPO_DATA_01c] ) )    
              

        // [Christmas|Summer|...] [day|week|...]?
        rules.add(new Rule(id:"DATE-seasons", description:'{[holidays|seasons|...]! [day|week|...]?}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [     		
                  TimeGazetteerEN.holidaysSeasons1c, 
                  TimeGazetteerEN.selectedTimeTypesAll01c] ) )    


//  segundo!+ [dia|mês|trimestre]! d[aoe]? <TEMPO|NUMERO>!
// ex: no segundo trimenstre de 2007, no terceiro dia da Quaresma
rules.add(new Rule(id:"DATE-ordinal-type-X", description:'segundo!+ [dia|mês|trimestre]! d[aoe]? <TEMPO|NUMERO>!',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [ 
        NumberGazetteerEN.ordinalTextNumber1Pc, 
        TimeGazetteerEN.selectedTimeTypesAll1c,
        ClausesEN.of01c, 
        NEGazetteer.NE_TEMPO_NUMERO_1c] ) )    

        //  {<NUM>? [day|month|...]! of? <TIME|NUM>!}
        // exemplo: 3rd day of March, month of April, year of 2007
        rules.add(new Rule(id:"DATE-number-type-X", description:'{<NUM>? [day|month|...]! of? <TIME|NUM>!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [      		
                  NEGazetteer.NE_NUMERO_01c, 
                  TimeGazetteerEN.selectedTimeTypesAll1c, 
                  ClausesEN.of01c, 
                  NEGazetteer.NE_TEMPO_NUMERO_1c] ) )    
         
        // in! the! {[begin(nings)|ends|finals]! of! <NUMERO|TEMPO>!}
        rules.add(new Rule(id:"DATE-middles-X", description:'in! the! {[begin(nings)|ends|finals]! of! <NUMERO|TEMPO>!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Clause, clauses: [
                  ClausesEN.Iin1nc, 
                  ClausesEN.the1nc, 
                  ClausesEN.beginningEndFinal1c, 
                  ClausesEN.of1c, 
                  NEGazetteer.NE_TEMPO_NUMERO_1c] ) )           
          
    
	    // {[after|before|since]! the? <TEMPO>! [vacations|holidays]?}
        // Before 2007 holidays 
	rules.add(new Rule(id:"DATE-relative-4", description:'[after|before|since]! the? <TEMPO>! [vacations|holidays]?',
    sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [
                 ClausesEN.afterBeforeSince1c, 
                 ClausesEN.the01c, 
                 NEGazetteer.NE_TEMPO_1c, 
                 TimeGazetteerEN.holidaysSeasons01c ] ) )    
 
             
/*********************/  	
// HOUR
/*********************/
	
       //14h, 14H, 14hs, 14Hs, 14Horas, 14horas  
        rules.add(new Rule(id:"HOUR-14h",description:"XX[hH](our)s?",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [ 
                 Clause.newRegex1Clause(~/\d{1,2}[Hh](?:our)?s?/, "\\d{1,2}[Hh](our)?s?") ]))
        
       // 14h and? 30m. Warning 30m may be meters. Force the first clause.
     	rules.add(new Rule(id:"HOUR-14h-&-30m", description:"XX[hH](our)?s? and XXm(inute)?s?",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [ 
                   NEGazetteer.NE_TEMPO_HORA_1c, 
                   ClausesEN.and01c, 
                   Clause.newRegex1Clause(~/\d{1,2}[Mm](?:inute)?s?/,"\\d{1,2}[Mm](inute)?s?") ]))
        
       // 14 h, 14 H, 14 hs, 14 Hs, 14 Hours, 14 hours  
        rules.add(new Rule(id:"HOUR-14_h", description:"{<NUM!> [hH](our)?s?}",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [ 
                   NEGazetteer.NE_NUMERO_1c, 
                   Clause.newRegex1Clause(~/[Hh](?:our)?s?/,"[Hh](our)?s?") ]))
        
       rules.add(new Rule(id:"HOUR-14 o'clock", description:"{<NUM>} o'clock! ",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [ 
        NEGazetteer.NE_NUMERO_1c, 
        Clause.newRegex1Clause(~/o'[Cc]lock/,"o'clock") ]))

        // 14h00, 14H00, 14:00, 14h00m 
        rules.add(new Rule(id:"HOUR-14h:30m", description:"XX[hH]XXm?",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [
                   Clause.newRegex1Clause(~/\d{1,2}[Hh:]\d{1,2}[Mm]?/, "\\d{1,2}[Hh:]\\d{1,2}[Mm]?") ]))
        
        // {14! hours! and! 30! minutes!}.
     	rules.add(new Rule(id:"HOUR-textual", description:"XX hours and XX minutes",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [ 
                   NEGazetteer.NE_TEMPO_HORA_1c, 
                   ClausesEN.and01c, 
                   NEGazetteer.NE_NUMERO_1c, 
                   ClausesEN.minutes1c]))
                           
        // {[after|before]! <TIME HOUR>!}
        rules.add(new Rule(id:"HOUR-relative-X", description:"{[after|before]! <TIME HOUR>!}",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule,clauses: [
                   ClausesEN.afterBeforeSince1c, 
                   NEGazetteer.NE_TEMPO_HORA_1c])) 
        
         //{<HOUR>! ,! <NUM>! minutes! and! <NUM>! seconds!}
        rules.add(new Rule(id:"HOUR-textual-2", description:"hour verbosely",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule,clauses: [ 
        	   NEGazetteer.NE_TEMPO_HORA_1c, 
        	   CommonClauses.comma01c, 
                   NEGazetteer.NE_NUMERO_1c, 
                   ClausesEN.minutes1c, 
                   ClausesEN.and01c, 
                   NEGazetteer.NE_NUMERO2_1c, 
                   ClausesEN.seconds1c])) 
         
        //{<HOUR>! in? the? [afternoon|night|morning]!}
        rules.add(new Rule(id:"HOUR-afternoon", description:"{<HOUR>! in? the? [afternoon|night|morning]!}",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [
        	  NEGazetteer.NE_TEMPO_HORA_1c, 
        	  ClausesEN.in01c, 
                  ClausesEN.the01c, 
                  ClausesEN.afternoonMorningNight1c])) 
           
        //{<HOUR>! of? [yestarday|tomorrow|...]!}
        rules.add(new Rule(id:"HOUR-day",description:"{<HOUR>! of? [yestarday|tomorrow|...]!}",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [ 
        	  NEGazetteer.NE_TEMPO_HORA_1c, 
        	  ClausesEN.of01c, 
                  ClausesEN.afterBeforeTodayTomorrowYesterday1c])) 

    /*********************/  	
//interval
    /*********************/    
	  
	// {[Between|since]! \\d{4}! [and|(un|')til]! \\d{4}}!
	rules.add(new Rule(id:"INTERVAL-1",description:"{[Between|from|since]! \\d{4}! [and|(un|')til]! \\d{4}!}",
	sc:SC.time_calendar_interval, policy:RulePolicy.Rule, clauses: [ 
		ClausesEN.betweenFromSince1c, 
		TimeGazetteerEN.numeralYear1c, 
	        ClausesEN.andUntil1c, 
	        TimeGazetteerEN.numeralYear1c_duplicate] ))
  

	  // {[Between|since]! <TIME>! [and|(un|')til]! <TIME>}!
	rules.add(new Rule(id:"INTERVAL-2",description:"{[Between|from|since]! \\d{4}! [and|(un|')til]! \\d{4}!}",
	sc:SC.time_calendar_interval, policy:RulePolicy.Rule, clauses: [ 
	         ClausesEN.betweenFromSince1c, 
	         NEGazetteer.NE_TEMPO_1c, 
	         ClausesEN.andUntil1c, 
	         NEGazetteer.NE_TEMPO_1c_duplicate] ))

// it has to be AFTER INTERVAL-2, so that the 'between' and 'until' are not part of the TIME NEs.

// {[in|since|until|on]! (following|past|last|next)? <TIME>!}       
// Example: Since 2007, Since last Christmas, on next Easter, In January, On Summer, 
//on the following day is not covered
rules.add(new Rule(id:"DATE-relative-3", description:'em|no|desde o? TEMPO seguinte|anterior',
        sc:SC.time_calendar_date, policy:RulePolicy.Clause, clauses: [
        ClausesEN.ionSinceUntil1c, 
        ClausesEN.followingPastLastNext01c, 
        NEGazetteer.NE_TEMPO_1c ] ) )     
        
	// the 50s,  
	rules.add(new Rule(id:"INTERVAL-decade-1",description:"the! \\d{2}s!",
	sc:SC.time_calendar_interval, policy:RulePolicy.Rule, clauses: [ 
	         ClausesEN.the1nc, 
	         Clause.newRegex1Clause(~/\d{2}s/, "\\d{2}s")] ))
	    
	// the 50 's,  
	rules.add(new Rule(id:"INTERVAL-decade-2",description:"the! {\\d{2}! 's!}",
	sc:SC.time_calendar_interval, policy:RulePolicy.Rule, clauses: [ 
	         ClausesEN.the1nc, 
	         Clause.newRegex1Clause(~/\d{2}/,"\\d{2}"),
	         ClausesEN.apostrophe1c] ))

		
	         // during! the? {TEMPO}!
	rules.add(new Rule(id:"INTERVAL-during",description:"{during! the? TEMPO!}",
        sc:SC.time_calendar_interval, policy:RulePolicy.Rule, clauses: [ 
        ClausesEN.during1c, 
        ClausesEN.the01c, 
        NEGazetteer.NE_TEMPO_1c] ))
        
	    // Getting years... In 2001-/2002 
        rules.add(new Rule(id:"INTERVAL-two-years", description:'[Ii]n! {2001[-/]2002!}',
        sc:SC.time_calendar_interval, policy:RulePolicy.Clause, clauses: [ 
                 ClausesEN.Iin1nc, 
                 Clause.newRegex1Clause(~/[12][0-9]\d{2}[-\/][12][0-9]\d{2}/, "[12][0-9]\\d{2}[-/][12][0-9]\\d{2}") ]))

		 			    
	/*********************/  	
	// frequency
	/*********************/  
		
	rules.add(new Rule(id:"FREQUENCY-1",description:"{[daily|weekly|...]}",
	sc:SC.time_frequency, policy:RulePolicy.Rule, clauses: [ 
	         TimeGazetteerEN.timeFrequencyAdverbs1c] ))
		   
	 // all <X>, x = day (Single), etc.
	rules.add(new Rule(id:"FREQUENCY-2",description:"{all! [day|week]!}",
	sc:SC.time_frequency, policy:RulePolicy.Rule, clauses: [ 
	        ClausesEN.all1c, 
	        TimeGazetteerEN.selectedTimeTypesSingle1c] ))
		
	// <NUMERO>! times! per! <x>!
	rules.add(new Rule(id:"FREQUENCY-3",description:"{<NUMERO>! times! per! <x>!}",
	sc:SC.time_frequency, policy:RulePolicy.Rule, clauses: [ 
	        NEGazetteer.NE_NUMERO_1c, 
	        ClausesEN.times1c, 
		ClausesEN.per1c, 
		TimeGazetteerEN.selectedTimeTypesSingle1c] ))		
	}
}