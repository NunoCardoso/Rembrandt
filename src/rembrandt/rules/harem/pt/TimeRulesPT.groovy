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
 
package rembrandt.rules.harem.pt

import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.RulePolicy
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.pt.TimeGazetteerPT
import rembrandt.gazetteers.pt.NumberGazetteerPT
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.gazetteers.CommonClauses
import rembrandt.rules.NamedEntityDetector
/**
 * @author Nuno Cardoso
 * Rules for capturing TEMPO entities.
 */
class TimeRulesPT extends NamedEntityDetector {

    List<Rule> rules 

    /**
     * Main constructor
     */    
    public TimeRulesPT () {
	
	 rules = []
	 	
      /**********************/
      /* TIME CALENDAR DATE */
      /**********************/
        
        // estes casos não são atomizados. Ou seja, são apenas um átomo, e não capturados como NUMERO
        // 20/02/2007, 2007/02, 2007/02/20, 20-02-2007, 2007-02, 2007-02-20, 20-02, 20/02  
        // 2/3/2007
        rules.add(new Rule(id:"DATE-completa-1",description:"{XX-/XX-/XX}", policy:RulePolicy.Rule, 
        sc:SC.time_calendar_date, clauses: [ 
                 TimeGazetteerPT.monthYearNumeral1c  ] ) )       
        
           // when month is writtem, but glued
        rules.add(new Rule(id:"DATE-completa-2",description:"{XX-/Fev-/XX}", policy:RulePolicy.Rule, 
        sc:SC.time_calendar_date, clauses: [ 
        TimeGazetteerPT.monthYearTextual1c  ] ) )       

        // 20 / 02 / 2007, 2007 / 02, 2007 / 02 / 20, 20 - 02 - 2007, 2007 - 02, 2007 - 02 - 20
        rules.add(new Rule(id:"DATE-completa-3",description:"{<NUM>! -/! <NUM>! -/? <NUM>?}",
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [ 
                   NEGazetteer.NE_NUMERO_1c, 
                   CommonClauses.slashMinus1c, 
                   NEGazetteer.NE_NUMERO2_1c, 
                   CommonClauses.slashMinus01c,
                   NEGazetteer.NE_NUMERO_01c ] ) )

      
        // século! {NUMERO}! (acdc)?
        rules.add(new Rule(id:"DATE-Seculo-1", description:'{[Ss][é]c(ulo)?s?! <NUM>! ac|dc?}',
                sc:SC.time_calendar_date, policy:RulePolicy.Rule,        		
                clauses: [TimeGazetteerPT.century1c, 
                NEGazetteer.NE_NUMERO_1c, 
                TimeGazetteerPT.centurySuffix01c ] ) )    

       // 3000 a.C.
        rules.add(new Rule(id:"DATE-Seculo-2", description:'{<NUM>! ac|dc!}',
        	sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [     		
        	NEGazetteer.NE_NUMERO_1c, 
        	TimeGazetteerPT.centurySuffix1c] ) )  


        // século! {NUMERO-ROMANO}! (acdc)?
        rules.add(new Rule(id:"DATE-Seculo-3", description:'{[Ss][é]c(ulo)?s?! [IVX]+! ac|dc? e? [IVX]+?}',
                sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [
                TimeGazetteerPT.century1c, 
                TimeGazetteerPT.centuryRoman1c, 
                TimeGazetteerPT.centurySuffix01c,
                ClausesPT.e01c,
                TimeGazetteerPT.centuryRoman01c,
                TimeGazetteerPT.centurySuffix2_01c] ) )    
               
        // [Ff]evereiro, 
        rules.add(new Rule(id:"DATE-mes", description:'{[[Ff]evereiro]}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [      		                
                  TimeGazetteerPT.mesText1c] ) )    
                  
        // [[Ff]evereiro|[Ff]ev]]! [de|,]? <NUM|TEMPO>! -> forçar o número, senão apanho dez, mar, etc. 
        // tempo também, pode ser ano ou século
        rules.add(new Rule(id:"DATE-mes-X", description:'{[[Ff]evereiro|[Ff]ev.?]]! [de|,]? <NUM|TEMPO>!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [      		                
                  TimeGazetteerPT.mesFull1c, 
                  ClausesPT.deCommaSlash01c, 
                  NEGazetteer.NE_TEMPO_NUMERO_1c] ) )    
 
         // splitted so that optimizer can start with 1 clauses         
        rules.add(new Rule(id:"DATE-dia-X", description:'{<NUM>! de? <TEMPO>!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [      		
                  NEGazetteer.NE_NUMERO_1c, 
                  ClausesPT.de01c, 
                  NEGazetteer.NE_TEMPO_DATA_1c] ) )   
                  
        // [Qua|Quarta|Quarta-feira]! ,? <TEMPO DATA>?
        // dá para quarta-feita sozinho, ou acompanhado de data.
        rules.add(new Rule(id:"DATE-diasemana-X", description:'{[[Qua]rta]-feira]! ,? <TEMPO DATA>?}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [
                 TimeGazetteerPT.weekDay1c, 
                 CommonClauses.comma01c, 
                 NEGazetteer.NE_TEMPO_DATA_01c] ) )    
        /** HAREM II stuff */
        
        // Natal|Consoada|Páscoa|Carnaval|Entrudo|Quaresma|Inverno|Primavera|Verão|Outono
        rules.add(new Rule(id:"DATE-seasons", description:'Festividade+estacões',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [
                  TimeGazetteerPT.festividadesEstacoes1c] ) )    


//  segundo!+ [dia|mês|trimestre]! d[aoe]? <TEMPO|NUMERO>!
// ex: no segundo trimenstre de 2007, no terceiro dia da Quaresma
        rules.add(new Rule(id:"DATE-ordinal-type-X", description:'segundo!+ [dia|mês|trimestre]! d[aoe]? <TEMPO|NUMERO>!',
                sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [ 
                NumberGazetteerPT.ordinalTextNumber1Pc, 
                TimeGazetteerPT.selectedTimeTypesAll1c,
                ClausesPT.daeo01c, 
                NEGazetteer.NE_TEMPO_NUMERO_1c] ) )    

// note it can't be TEMPOCALEND10 then TEMPOCALEND11... if 10 matches, then 11 can't have the TEMPO/NUMERO. 
// let's makes a try-detailed-catch-generic approach here.

        //  <NUM>? [dia|mês|semana|ano]! d[aeo]! <TEMPO|NUMERO>!
        // exemplo: dia de Março, mês de Março, dia de Natal, ano de 2007, 21 dias de abril
        // forçar o daeo!, senão apanha "anos 70"
        rules.add(new Rule(id:"DATE-number-type-X", description:'<NUM>? [dia|mês|semana|ano]! d[aeo]! <TEMPO|NUMERO>!',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule,     		
        clauses: [NEGazetteer.NE_NUMERO_01c, 
                  TimeGazetteerPT.selectedTimeTypesAll1c, 
                  ClausesPT.daeo1c, 
                  NEGazetteer.NE_TEMPO_NUMERO_1c] ) )    

     
        // dia! d[aoe]! [Xxxx]!+ +d[aoe]s??  [Xxxx]?+
        //ex: no dia de São Valentim, no dia de Nossa Senhora dos Aflitos, 
        // falta "no dia dos namorados
        rules.add(new Rule(id:"DATE-day-holiday", description:'dia! d[aoe]! [Xxxx]!+ +d[aoe]s?? [Xxxx]?+',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [        		
                  ClausesPT.dia1c, 
                  ClausesPT.daeo1c, 
                  CommonClauses.capitalizedAlphNum1Pc, 
                  ClausesPT.daeos01c, 
                  CommonClauses.capitalizedAlphNum0Pc] ) )    
    
        // em! (princípios|meados|fins)! de! <NUMERO|TEMPO>
        rules.add(new Rule(id:"DATE-middles-X", description:'em! (princípios|meados|fins)! de! <NUMERO|TEMPO>',
        sc:SC.time_calendar_date, policy:RulePolicy.Clause, clauses: [      		
                  ClausesPT.emnao1c, 
                  ClausesPT.fimFinaisMeados1c, 
                  ClausesPT.de1c, 
                  NEGazetteer.NE_TEMPO_NUMERO_1c ] ) )           

      //<NUM> <X!> (depois|antes|desde)! d[ao]! <TEMPO>! 
// (dois dias depois do Natal, depois da Páscoa, três semanas desde o Natal)
rules.add(new Rule(id:"DATE-relative-1", description:'{<NUM>! <[dias|semanas|...]! [depois|antes|desde]! [Dd]?[ao]! <TEMPO>!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses:[     		
        NEGazetteer.NE_NUMERO_1c,        
        TimeGazetteerPT.selectedTimeTypesAll1c, 
        ClausesPT.antesDepoisDesde1c, 
        ClausesPT.aodado1c, 
        NEGazetteer.NE_TEMPO_1c]))

//<TEMPO>? (depois|antes|desde)! d[ao]! <TEMPO>! 
// (dois dias depois do Natal, depois da Páscoa, três semanas desde o Natal)
rules.add(new Rule(id:"DATE-relative-2", description:'{<TEMPO>? [depois|antes|desde]! [Dd]?[ao]! <TEMPO>!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses:[     		
        NEGazetteer.NE_TEMPO_01c, 
        ClausesPT.antesDepoisDesde1c, 
        ClausesPT.aodado1c, 
        NEGazetteer.NE_TEMPO_1c]))
        
        //  em|desde o? <TEMPO>! (seguinte|passado|anterior)?
        // Exemplo: desde 2007, desde o Natal passado, na Páscoa seguinte, Em Janeiro, No Natal, em 2008, no dia seguinte, etc.
        //falta suportar coisas tipo fim de semana, 5¬™ Feira. 
        // Cuidado com o Rio <TEMPO>de Janeiro</TEMPO>!!
        rules.add(new Rule(id:"DATE-relative-3", description:'{em|desde o? <TEMPO>! (seguinte|passado|anterior)?}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses:[       		
             //     TimeGazetteerPT.notRio1nc, 
                  ClausesPT.desdeEmNao1c, 
                  ClausesPT.o01c, 
                  NEGazetteer.NE_TEMPO_1c, 
                  ClausesPT.anteriorPassadoSeguinte01c ] ) )       

      // {<TEMPO>! (seguinte|passado|anterior)!}
rules.add(new Rule(id:"DATE-relative-4", description:'{<TEMPO>! (seguinte|passado|anterior)!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Clause, clauses:[       		
        // ClausesPT.dnao1nc, 
        NEGazetteer.NE_TEMPO_1c, 
        ClausesPT.anteriorPassadoSeguinte1c ] ) )       

    // {<X>! (seguinte|passado|anterior)!}, x = dia, trimestre, etc
rules.add(new Rule(id:"DATE-relative-5", description:'{<X>! (seguinte|passado|anterior)!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Clause, clauses:[       		
        // ClausesPT.dnao1nc, 
        TimeGazetteerPT.selectedTimeTypesAll1c,
        ClausesPT.anteriorPassadoSeguinte1c ] ) )       

	// (antes|depois|desde)! [nd]?[oa]s?! [férias]! d[eao]! <TEMPO>! (ex: nas férias da páscoa) 
	rules.add(new Rule(id:"DATE-holidays-1", description:'(antes|depois|desde)! [nd]?[oa]s?! [férias]! d[eao]! <TEMPO>!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses:[      		
                  ClausesPT.antesDepoisDesde1c, 
                  ClausesPT.dnoas1c, 
                  TimeGazetteerPT.festividadesEstacoes1c, 
                  ClausesPT.daeo1c, 
                  NEGazetteer.NE_TEMPO_1c ] ) )    
 
        // (antes|depois|desde)|(manhã|tarde|noite|madrugada)? de? [ontem|amanhã|hoje|dantes|depois]!
        rules.add(new Rule(id:"DATE-relative-6", description:'{[antes|depois|desde]? de? [ontem|amanhã|hoje|dantes|depois]!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [       		
                  ClausesPT.antesDepoisDesdeMadrugadaManhaMatinaNoiteTarde01c, 
                  ClausesPT.de01c, 
                  ClausesPT.amanhaDantesDepoisHojeOntem1c] ))

        // para! [a|o]! <X>! que! vem!
        rules.add(new Rule(id:"DATE-relative-7", description:'{para! [a|o]! <x>! que! vem!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [
                 ClausesPT.para1c,
                 ClausesPT.a_o1c, 
                 TimeGazetteerPT.selectedTimeTypesAll1c, 
                 ClausesPT.que1c, 
                 ClausesPT.vem1c] ))
        
       // para! [a|o]! <TEMPO>! que! vem!               
        rules.add(new Rule(id:"DATE-relative-8", description:'{para! [a|o]! <TEMPO>! que! vem!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [
        ClausesPT.para1c, 
        ClausesPT.a_o1c, 
        NEGazetteer.NE_TEMPO_1c,
        ClausesPT.que1c, 
        ClausesPT.vem1c] ))
        
       //<NUMERO>! de? <X>! mais! tarde|cedo! (dois anos mais tarde), 68 milh√µes de anos mais tarde
        rules.add(new Rule(id:"DATE-relative-9", description:'<NUMERO>! de? <X>! mais! tarde|cedo!',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses:[  		
                 NEGazetteer.NE_NUMERO_1c, 
                 ClausesPT.de01c, 
                 TimeGazetteerPT.timeFullUnits1c, 
                 ClausesPT.mais1c, 
                 ClausesPT.cedoTarde1c]))
        
        // há|faz! <NUMERO>! <X>! atrás?
        // faz 2 dias, há 300 anos atrás?
        rules.add(new Rule(id:"DATE-relative-10", description:'{há|faz! <NUMERO>! <X>! atrás?}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [       		
                  ClausesPT.fazha1c, 
                  NEGazetteer.NE_NUMERO_1c, 
                  TimeGazetteerPT.timeFullUnits1c, 
                  ClausesPT.atras01c ]))
              
        // agora!recentemente
        rules.add(new Rule(id:"DATE-relative-11", description:'{[agora|recentemente]!}',
        sc:SC.time_calendar_date, policy:RulePolicy.Rule, clauses: [ClausesPT.agoraRecentemente1c] ) )
   
       // N[este]aos! (alturas?|tempos?|momentos?|períodos?|eras?)! do? <x>?, x= dia mês,
       // Nota: falta Cretáceo, Jurássico, etc.
       
  
        // Apanhar anos... Em [12][0-9]\\d{2}
        rules.add(new Rule(id:"DATE-year-catch-1", description:'[Ee]m [12][0-9]\\d{2}',
        sc:SC.time_calendar_date, policy:RulePolicy.Clause, clauses: [
                  ClausesPT.Eem1c, 
                  TimeGazetteerPT.numeralYear1c ]))

        // Apanhar anos... ",! [em|de]! \d\d! ,! " 
        rules.add(new Rule(id:"DATE-year-catch-2", description:",! [em|de]! \\d\\d! ,! ",
        sc:SC.time_calendar_date, policy:RulePolicy.Clause, clauses: [
                  CommonClauses.comma1nc, 
                  ClausesPT.deem1c,
                  TimeGazetteerPT.numeralYearII_1c,  
                  CommonClauses.comma1nc_duplicate ]))

         // Apanhar anos... "desde [12][0-9]\\d{2} [.,]"" 
        rules.add(new Rule(id:"DATE-year-catch-3", description:"desde [12][0-9]\\d{2} [.,] ",
        sc:SC.time_calendar_date, policy:RulePolicy.Clause, clauses:[  		
                  ClausesPT.desde1c, 
                  TimeGazetteerPT.numeralYear1c, 
                  CommonClauses.commaDot1nc ]))
                
	/**********************/  	
	/* TIME CALENDAR HOUR */
	/**********************/
	
       //14h, 14H, 14hs, 14Hs, 14Horas, 14horas  
        rules.add(new Rule(id:"HOUR-14h",description:"{\\d{1,2}[hH](ora)s!}",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [ 
             Clause.newRegex1Clause(~/\d{1,2}[Hh](?:ora)?s?/, "\\d{1,2}[Hh](ora)?s?") ]))
        
       // 14h e? 30m. Há que ter atençao. 30m pode ser metros.
       // Como tal, usar a regra anterior, que já marcou horas.
     	rules.add(new Rule(id:"HOUR-14h-&-30m",description:"{XX[hH](ora)s! e! XXm(inutos?)!}",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [ 
                   NEGazetteer.NE_TEMPO_HORA_1c, 
                   ClausesPT.e01c, 
                   Clause.newRegex1Clause(~/\d{1,2}[Mm](?:inuto)?s?/,"\\d{1,2}[Mm](inuto)?s?") ]))
        
       // 14 h, 14 H, 14 hs, 14 Hs, 14 Horas, 14 horas  
        rules.add(new Rule(id:"HOUR-14_h",description:"\\d{1,2}! [hH](ora)s!",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses:[
                  NEGazetteer.NE_NUMERO_1c, 
                  Clause.newRegex1Clause(~/[Hh](?:ora)?s?/,"[Hh](ora)?s?") ]))
        
        // 14h00, 14H00, 14:00, 14h00m 
        rules.add(new Rule(id:"HOUR-14h:30m",description:"\\d{1,2}[hH:]\\d{1,2}[Mm]?",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [ 
                 Clause.newRegex1Clause(~/\d{1,2}[Hh:]\d{1,2}[Mm]?/, "\\d{1,2}[Hh:]\\d{1,2}[Mm]?") ]))
        
        // 14 horas e 30 minutos.
     	rules.add(new Rule(id:"HOUR-textual",description:"XX horas e XX minutos",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [ 
                   NEGazetteer.NE_TEMPO_HORA_1c, 
                   ClausesPT.e01c, 
                   NEGazetteer.NE_NUMERO_1c, 
                   ClausesPT.minutos1c]))
         
      // antes|depois|desde! das! <HORA>!
       rules.add(new Rule(id:"HOUR-relative-X",description:"[antes|depois|desde]! d?as! <HORA>!",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [
        ClausesPT.antesDepoisDesde1c, 
        ClausesPT.asdas1c,
        NEGazetteer.NE_TEMPO_HORA_1c])) 
        	                  
         
         //<HORA>, <numero> minutos e <numero> segundos
        rules.add(new Rule(id:"HOUR-textual-2",description:"hora por extenso",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [
                   NEGazetteer.NE_TEMPO_HORA_1c, 
                   CommonClauses.comma01c, 
                   NEGazetteer.NE_NUMERO_1c, 
                   ClausesPT.minutos1c, 
                   ClausesPT.e01c, 
                   NEGazetteer.NE_NUMERO2_1c, 
                   ClausesPT.segundos1c])) 
         
           //<HORA>  da tarde, da manhã, da noite, da madrugada, da matina
        rules.add(new Rule(id:"HOUR-afternoon",description:"<HORA> da (manhã|tarde|etc)",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [
                   NEGazetteer.NE_TEMPO_HORA_1c, 
                   ClausesPT.da1c, 
                   ClausesPT.madrugadaManhaMatinaNoiteTarde1c])) 
           
           //<HORA> de ontem
       rules.add(new Rule(id:"HOUR-day",description:"<HORA> de? (ontem|hoje|etc)",
       sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [
                    NEGazetteer.NE_TEMPO_HORA_1c, 
                    ClausesPT.de01c, 
                    ClausesPT.amanhaDantesDepoisHojeOntem1c])) 

// às <HORA>
rules.add(new Rule(id:"HOUR-on-X",description:"[à|às]! <HORA>!",
        sc:SC.time_calendar_hour, policy:RulePolicy.Rule, clauses: [
        ClausesPT.aas1c, 
        NEGazetteer.NE_TEMPO_HORA_1c])) 


    /**************************/  	 
    /* TIME CALENDAR INTERVAL */
    /**************************/    
	  
      rules.add(new Rule(id:"INTERVAL-1",description:"{[entre|de|desde]! as? \\d{4}! e|a(té?)! as? \\d{4}!}",
      sc:SC.time_calendar_interval, policy:RulePolicy.Rule, clauses: [
                    ClausesPT.deDesdeEntre1c, 
                    ClausesPT.as01c, 
                    TimeGazetteerPT.numeralYear1c, 
                    ClausesPT.aatee1c,
                    ClausesPT.as01c_duplicate, 
                    TimeGazetteerPT.numeralYear1c_duplicate] ))
	    

   rules.add(new Rule(id:"INTERVAL-2",description:"{[entre|de|desde]! as? <TEMPO!> e|a(té?)! as? <TEMPO>!}",
        sc:SC.time_calendar_interval, policy:RulePolicy.Rule, clauses: [
        ClausesPT.deDesdeEntre1c, 
        ClausesPT.as01c, 
        NEGazetteer.NE_TEMPO_1c, 
        ClausesPT.aatee1c,
        ClausesPT.as01c_duplicate, 
        NEGazetteer.NE_TEMPO_1c_duplicate] ))

// de 3 a 6 meses
   rules.add(new Rule(id:"INTERVAL-3",description:"{[entre|de|desde]! <NUM!> e|a(té?)! <NUM>! !<dia|mês|semana|...>!}",
        sc:SC.time_calendar_interval, policy:RulePolicy.Rule, clauses: [
        ClausesPT.deDesdeEntre1c, 
        NEGazetteer.NE_NUMERO_1c, 
        ClausesPT.aatee1c,
        NEGazetteer.NE_NUMERO2_1c, 
        TimeGazetteerPT.selectedTimeTypesAll1c] ))
        

        rules.add(new Rule(id:"INTERVAL-decade",description:"anos {NUMERO}",
      sc:SC.time_calendar_interval, policy:RulePolicy.Rule, clauses: [
	            ClausesPT.anos1c, 
	            NEGazetteer.NE_NUMERO_1c ] ) )


     // entre! os! <TEMPO> [ex: entre os <TEMPO>séculos XV e XVI</TEMPO>]
	rules.add(new Rule(id:"INTERVAL-during",description:"durante! os? {TEMPO}!",
        sc:SC.time_calendar_interval, policy:RulePolicy.Rule, clauses: [ 
        ClausesPT.entre1c, 
        ClausesPT.os1c,
        NEGazetteer.NE_TEMPO_1c] ))


      // Apanhar anos... Em 2001-/2002 
      rules.add(new Rule(id:"INTERVAL-two-years", description:'2001-/2002',
      sc:SC.time_calendar_interval, policy:RulePolicy.Clause,  clauses: [
          Clause.newRegex1Clause(~/[12][0-9]\d{2}[-\/][12][0-9]\d{2}/, "[12][0-9]\\d{2}[-/][12][0-9]\\d{2}") ]))
	    
     /*****************/  	
     /* TIME DURATION */
     /*****************/  

// nestas alturas do mês
rules.add(new Rule(id:"DURATION-1", description:'{N[este]os (alturas?|tempos?|momentos?|períodos?|eras?)! do? <x>!}',
        sc:SC.time_duration, policy:RulePolicy.Rule, clauses: [      		
        ClausesPT.NaosNesstaos1nc, 
        ClausesPT.alturasErasMomentosPeriodosTempos1c, 
        ClausesPT.dao01c, 
        TimeGazetteerPT.timeFullUnits1c ]))

// nos períodos do Natal, no período Jurássico
rules.add(new Rule(id:"DURATION-2", description:'{N[este]os (alturas?|tempos?|momentos?|períodos?|eras?)! d[ao]? <TEMPO>!}',
        sc:SC.time_duration, policy:RulePolicy.Rule, clauses: [      		
        ClausesPT.NaosNesstaos1nc, 
        ClausesPT.alturasErasMomentosPeriodosTempos1c, 
        ClausesPT.dao01c, 
        NEGazetteer.NE_TEMPO_1c ]))


       // durante <NUMERO> <X>, x= anos meses, etc.
      rules.add(new Rule(id:"DURATION-3",description:"durante! {NUMERO}! <X>!",
      sc:SC.time_duration, policy:RulePolicy.Rule, clauses: [ 
		ClausesPT.durante1c, NEGazetteer.NE_NUMERO_1c, 
		           TimeGazetteerPT.selectedTimeTypesAll1c] ))
		 
      // durante (muitos|poucos) x = anos|meses, etc
      rules.add(new Rule(id:"DURATION-4",description:"durante! (muitos|poucos)! <X>!",
      sc:SC.time_duration, policy:RulePolicy.Rule, clauses: [ 
                ClausesPT.durante1c, 
                ClausesPT.algumasMuitosPoucos1c, 
		TimeGazetteerPT.selectedTimeTypesAll1c] ))
	     
       // todo o <X>, x = dia (Single), etc.
      rules.add(new Rule(id:"DURATION-5",description:"todo! o! <X>!",
      sc:SC.time_duration, policy:RulePolicy.Rule, clauses: [ 
                ClausesPT.todo1c, 
                ClausesPT.o1c, 
		TimeGazetteerPT.selectedTimeTypesSingle1c] ))

      // durante! os? <TEMPO> [ex: durante os <TEMPO>séculos XV e XVI</TEMPO>], durante a Páscoa
rules.add(new Rule(id:"DURATION-6",description:"durante! aos? {TEMPO}!",
        sc:SC.time_duration, policy:RulePolicy.Rule, clauses: [ 
        ClausesPT.durante1c, 
        ClausesPT.aos_01c,
        NEGazetteer.NE_TEMPO_1c] ))

  	   
     /******************/  	
     /* TIME FREQUENCY */ 
     /******************/  

      rules.add(new Rule(id:"FREQUENCY-1",description:"(diaria|semanal|anual)-mente",
      sc:SC.time_frequency, policy:RulePolicy.Rule, clauses: [ 
	       TimeGazetteerPT.timeFrequenceAdverbs1c] ))
		   
     // todo o <X>, x = dia (Single), etc.
      rules.add(new Rule(id:"FREQUENCY-2",description:"tod[ao]s?! os! <X>!",
      sc:SC.time_frequency, policy:RulePolicy.Rule, clauses: [ 
              ClausesPT.todaos1c, 
              ClausesPT.os1c, 
	      TimeGazetteerPT.selectedTimeTypesPlural1c] ))
		
       // <NUMERO>! vezes! [ao|por]! <x>!
       rules.add(new Rule(id:"FREQUENCY-3",description:"<NUMERO>! vez(es?)! [ao|por]! <x>!",
       sc:SC.time_frequency, policy:RulePolicy.Rule, clauses: [ 
	      NEGazetteer.NE_NUMERO_1c, 
	      ClausesPT.vezvezes1c, 
	      ClausesPT.aopor1c, 
	      TimeGazetteerPT.selectedTimeTypesSingle1c]))		
    }
}