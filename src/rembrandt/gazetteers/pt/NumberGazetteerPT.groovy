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
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for NUMERO category.
 */
class NumberGazetteerPT {
	      
   /* LISTS */	
   
   static final List<Pattern> singleTextNumber = [~/[Uu]ma?/, ~/[Dd]ois/,~/[Dd]uas/, ~/[Tt]rês/, ~/[Qq]uatro/, ~/[Cc]inco/, ~/[Ss]eis/, ~/[Ss]ete/, ~/[Oo]ito/,
         ~/[Nn]ove/, ~/[Dd]ez/, ~/[Oo]nze/, ~/[Dd]oze/, ~/[Tt]reze/, ~/[CcQq][uü]?atorze/, ~/[Qq]uinze/,
         ~/[Dd]e[sz]asseis/, ~/[Dd]e[sz]assete/, ~/[Dd]e[sz]oito/, ~/[Dd]e[sz]anove/, ~/[Cc]em/]
   
   static final List<Pattern> doubleTextNumber = [~/[Vv]inte/, ~/[tT]rinta/, ~/[Qq][üu]arenta/, ~/[Cc]inq[üu]enta/, ~/[Ss]essenta/, ~/[Ss]etenta/,
          ~/[Oo]itenta/, ~/[Nn]oventa/, ~/[Cc]ento/, ~/[Cc]entenas?/, ~/[Dd]uzent[ao]s/, ~/[Tt]rezent[oa]s/,
          ~/[Qq]uatrocent[oa]s/, ~/[Qq]uinhent[oa]s/, ~/[Ss]eiscent[oa]s/, ~/[Ss]etecent[oa]s/,
          ~/[Oo]itocent[oa]s/, ~/[Nn]ovecent[as]/]
   
   static final List<Pattern> hundredsTextNumber = [~/mil/, ~/milh(?:ão|ões|ar|ares)/, ~/bil[hi](?:ão|ões|ar|ares)/, ~/centenas?/]
   	 	
   static final List<Pattern> ordinalTextNumber = [~/primeir[ao]/, ~/segund[oa]/, ~/terceir[oa]/, ~/quart[ao]/, ~/quint[ao]/, ~/sext[ao]/, ~/sép?tim[ao]/,
   	 	/oitav[ao]/, ~/non[ao]/, ~/décim[oa]/, ~/vigésim[ao]/, ~/trigésim[ao]/, ~/quadragésim[ao]/,
   	 	/quinq[uü]agésim[oa]/, ~/sext[uü]agésim[ao]/, ~/sept[uü]agésim[ao]/, ~/octagésim[ao]/, ~/nonagésim[ao]/,
   	 	/centésim[ao]/, ~/milésim[ao]/]
   	 								
   static final Pattern eventNumber = ~/[IVXLMC]+|\\d+[ªºoa]/
   
   static final Pattern ordinalNumber = ~/\d+[ªºoa]/	
     
   static final Pattern digit = ~/\-?\d[\d.,]*/
   
   static final List numberPrefix = [~/[Nn][orº]?.?º?/,['N',~/[.º]/],~/[Nn][uú]m\.?/,~/[Nn][uú]mero/]
                               
   /* CLAUSES */

   static final Clause numberPrefix01nc = Clause.newConcept01Clause(numberPrefix, "number prefix",false)
   
   static final Clause digit1Pc = Clause.newRegex1PClause(digit,"Digit",true)
	
   static final Clause ordinalNumber1c = Clause.newRegex1Clause(ordinalNumber, "Ordinal Number", true)

   static final Clause textNumber1c = Clause.newConcept1Clause((singleTextNumber + doubleTextNumber),"TextNumber", true) 

   static final Clause hundredsTextNumber1Pc = Clause.newConcept1PClause(hundredsTextNumber, "hundreds Text Number", true)	
	   
   static final Clause ordinalTextNumber1Pc = Clause.newConcept1PClause(ordinalTextNumber, "ordinalTextNumber1Pc", true)
	   
   static final Clause ordinalTextNumber01c = Clause.newConcept01Clause(ordinalTextNumber, "ordinalTextNumber01c", true)

   static final Clause publicationNumber1 = Clause.newRegex1Clause(~/\d+\/\d+/, "\\d+/\\d+")

}