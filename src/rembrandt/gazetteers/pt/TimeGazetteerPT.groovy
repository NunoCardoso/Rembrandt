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
 
package rembrandt.gazetteers.pt

import rembrandt.obj.Clause
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for TEMPO category.
 */
class TimeGazetteerPT {
	      
     /* TEMPO */    
     static final List<Pattern> mesText = [ ~/[Jj]aneiro/, ~/[Ff]evereiro/, ~/[Mm]arço/, ~/[Aa]bril/, ~/[Mm]aio/, 
         ~/[Jj]unho/, ~/[Jj]ulho/, ~/[Aa]gosto/, ~/[Ss]etembro/, ~/[Oo]utubro/, ~/[Nn]ovembro/, ~/[Dd]ezembro/ ] 
     
     static final List<Pattern> mesDim =  [~/[Jj]an\.?/, ~/[Ff]ev\.?/, ~/[Mm]ar\.?/, ~/[Aa]br\.?/, ~/[Mm]ai\.?/, ~/[Jj]un\.?/, ~/[Jj]ul\.?/,
        ~/[Aa]go\.?/, ~/[Ss]et\.?/, ~/[Oo]ut\.?/, ~/[Nn]ov\.?/, ~/[Dd]ez\.?/ ]

     	 					// cuidado com o "ter". 
     static final List<Pattern> weekDay = [ ~/[Ss]eg\./, ~/Segunda-[Ff]eira/, [~/[Ss]egunda/, ~/[Ff]eira/], 
          ~/Ter\./, ~/[Tt]erça-[Ff]eira/,[~/[Tt]erça/, ~/[Ff]eira/], ~/[Qq]ua\./, ~/[Qq]uarta-[Ff]eira/,
          [~/[Qq]uarta/, ~/[Ff]eira/], ~/[Qq]ui\./, ~/[Qq]uinta-[Ff]eira/, [~/[Qq]uinta/, ~/[Ff]eira/],
      	  ~/[Ss]ex\./, ~/[Ss]exta-[Ff]eira/, [~/[Ss]exta/, ~/[Ff]eira/], ~/[Ss][áa]b\.?/, ~/[Ss]ábado/,
          ~/[Dd]om\./, ~/[Dd]omingo/ ]
       
     static final Pattern centuryText = ~/[Ss][ée]c(?:ulo)?s?\.?/
     	 
     static final List<String> selectedTimeTypesSingle = ["segundo", "minuto", "hora", "dia", "semana", "mês",
         "trimestre", "semestre", "ano", "década"]
      
     static final List<String> selectedTimeTypesPlural = ["segundos", "minutos", "horas", "dias", "semanas", "meses",
         "trimestres", "semestres", "anos", "décadas"]
      
     static final List<String> festividades = ["Natal", "Consoada", "Páscoa", "Carnaval", "Entrudo", "Quaresma", ~/[Ff]érias/ ]   
     
     static final List<String> estacoes = [ "Inverno", "Verão", "Outono", "Primavera"]
  
     static final Pattern numeralYear = ~/[12][0-9]\d{2}/
     
     static final List<Pattern> centurySuffix = [~/[AaBbDd]\.?[Cc]\.?/, ~/[Aa]\.?[Dd]\.?/]
	 
     static final Pattern monthYearNumeral = ~/(?:\d{2}[\/-]\d{2}|\d{2}[\/-]\d{4}|\d{1,2}[-\/]\d{1,2}[\/-]\d{2,4}|\d{4}[\/-]\d{2}|\d{4}[-\/]\d{1,2}[\/-]\d{1,2})/
     static final Pattern monthYearTextual =  ~/(?x) (?: \d{2,4}  [\/-] )?  (?: [Jj]an(?:eiro)?|[Ff]ev(?:ereiro)?|[Mm]ar(?:ço)?|[Aa]br(?:il)?| \
	[Mm]aio?|[Jj]u[nl](?:ho)?|[Aa]go(?:sto)?|[Ss]et(?:embro)?|[Oo]ut(?:ubro)?|[Nn]ov(?:embro)?|[Dd]ez(?:embro) ) [\/-] \d{2,4}/ 
	 
     static final Pattern timeFrequencyAdverbs = Pattern.compile("(?:[Dd]iariamente|[Ss]emanalmente|[Mm]ensalmente|"+
		"[Tt]rimestralmente|[Ss]emestralmente|[Aa]nualmente|[Ss]ecularmente|[Ff]requentemente|"+
		"[Pp]eriodicamente)")
	 
	 /** CLAUSES **/
	 
     static final Clause numeralYear1c = Clause.newRegex1Clause(numeralYear, 'numeralYear')

     static final Clause numeralYearII_1c = Clause.newRegex1Clause(~/\d{2}/, "d{2}")
     
   // duplicate
     static final Clause numeralYear1c_duplicate = Clause.newRegex1Clause(numeralYear, 'numeralYear duplicate')

     static final Clause numeralYear01c = Clause.newRegex01Clause(numeralYear, 'numeralYear')
 
     static final Clause mesFull1c = Clause.newConcept1Clause(mesText + mesDim, "Mês", true) 
	
     static final Clause mesText1c = Clause.newConcept1Clause(mesText ,"Mês", true) 
	 
     static final Clause weekDay1c = Clause.newConcept1Clause(weekDay,"DiaSemana",true) 
	
     static final Clause timeFrequenceAdverbs1c = Clause.newRegex1Clause(timeFrequencyAdverbs, "timeFrequencyAdverbs", true)
 
	/** avoid "Rio de Janeiro" **/
     static final Clause notRio1nc =new Clause(cardinality:Cardinality.One, criteria:Criteria.NotPlainMatch, 
			name:"Not Rio", pattern:"Rio", collectable:false)	
	
     static final Clause timeFullUnits01c = Clause.newConcept01Clause(ValueGazetteerPT.timeFullUnits, "timeFullUnits")
		
     static final Clause timeFullUnits1c = Clause.newConcept1Clause(ValueGazetteerPT.timeFullUnits, "timeFullUnits")
		
     static final Clause century1c = Clause.newRegex1Clause(centuryText, "Século", true)	
		
     static final Clause centuryRoman01c = Clause.newRegex01Clause(~/[IVX]+/, "[IVX]+")	
     static final Clause centuryRoman1c = Clause.newRegex1Clause(~/[IVX]+/, "[IVX]+")	
      
     static final Clause monthYearNumeral1c =  Clause.newRegex1Clause(monthYearNumeral, 'monthYearNumber', true)	     
     static final Clause monthYearTextual1c =  Clause.newRegex1Clause(monthYearTextual, 'monthYearTextual', true)	     
		
     static final Clause centurySuffix01c = Clause.newConcept01Clause(centurySuffix, 'centurySuffix') //ac, dc, AC, DC, a.c., d.c, A.C., D.C.
     static final Clause centurySuffix2_01c = Clause.newConcept01Clause(centurySuffix, 'centurySuffix_duplicate') //ac, dc, AC, DC, a.c., d.c, A.C., D.C.
	        
     static final Clause centurySuffix1c =  Clause.newConcept1Clause(centurySuffix, 'centurySuffix') //ac, dc, AC, DC, a.c., d.c, A.C., D.C.		 
		    
     static final Clause festividadesEstacoes1c = Clause.newConcept1Clause(festividades+estacoes,"festividades+estacoes")	

     static final Clause selectedTimeTypesSingle1c = Clause.newConcept1Clause(
		selectedTimeTypesSingle, "selected time types single", true)

     static final Clause selectedTimeTypesSingle01c = Clause.newConcept01Clause(
		selectedTimeTypesSingle, "selected time types single", true)
		
     static final Clause selectedTimeTypesPlural1c = Clause.newConcept1Clause(
	    selectedTimeTypesPlural, "selected time types plural", true)
		
     static final Clause selectedTimeTypesAll1c = Clause.newConcept1Clause(
	     (selectedTimeTypesSingle + selectedTimeTypesPlural), "selected time types single + plural", true)

     static final Clause selectedTimeTypesAll01c = Clause.newConcept01Clause(
	     (selectedTimeTypesSingle + selectedTimeTypesPlural), "selected time types single + plural", true)
}
 