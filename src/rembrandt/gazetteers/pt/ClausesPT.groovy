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
import rembrandt.obj.ClassificationCriteria
import java.util.regex.Pattern
/**
 * @author Nuno Cardoso
 * Gazetteer of common clauses.
 */
class ClausesPT {
    
        static final Clause aatee1c = Clause.newRegex1Clause( ~/a(té)?|e/, "a|até|e", true)	
	static final Clause aas1c = Clause.newPlain1Clause('às')
	static final Clause acimaabaixo1c  = Clause.newRegex1Clause(~/(?:acima|abaixo)/, "acima|abaixo", true)
	static final Clause acimaabaixocerca1c  = Clause.newRegex1Clause(
		~/(?:acima|abaixo|aproximadamente|cerca|mais|menos)/, "acima|abaixo|aproximadamente|cerca|mais|menos")
	static final Clause adverb01c = Clause.newRegex01Clause(~/.*mente/, ".*mente")
	static final Clause ae1c = Clause.newRegex1Clause(~/[ae]/)
	static final Clause agoraRecentemente1c = Clause.newRegex1Clause(
		~/(?:agora|recentemente)/, "agora|recentemente")
	static final Clause algumasMuitosPoucos1c = Clause.newRegex1Clause(
		~/(?:algu[nm]a?s?|muit[ao]s?|pouc[ao]s?)/, "algu[nm]a?s?|muit[ao]s?|pouc[ao]s?")
	static final Clause alturasErasMomentosPeriodosTempos1c = Clause.newRegex1Clause(
		~/(?:alturas?|tempos?|momentos?|períodos?|eras?)/, "alturas?|tempos?|momentos?|períodos?|eras?")
	static final Clause amanhaDantesDepoisHojeOntem1c = Clause.newRegex1Clause(
		~/(?:amanhã|antes|atrás|hoje|depois|ontem)/, "amanhã|antes|atrás|hoje|depois|ontem")
	static final Clause anos1c = Clause.newPlain1Clause("anos")
	static final Clause antesDepoisDesde1c  = Clause.newRegex1Clause(~/(?:[Aa]ntes|[Dd]epois|[Dd]esde)/, "antes|depois|desde")
	static final Clause antesDepoisDesdeMadrugadaManhaMatinaNoiteTarde01c = Clause.newRegex01Clause(
		~/(?:antes|desde|depois|madrugada|manhã|matina|noite|tarde)/, "antes|desde|depois|madrugada|manhã|matina|noite|tarde")	
	static final Clause anteriorPassadoSeguinte01c  = Clause.newRegex01Clause(
		~/(?:anterior|passado|seguinte)/, "anterior|passado|seguinte")
	static final Clause anteriorPassadoSeguinte1c  = Clause.newRegex1Clause(
		~/(?:anterior|passado|seguinte)/, "anterior|passado|seguinte")
	static final Clause aodado1c = Clause.newRegex1Clause(~/[Dd]?[oa]/, "[Dd]?[oa]")
	static final Clause a_o1c = Clause.newRegex1Clause(~/[ao]/,"[ao]")	
	static final Clause ao1c = Clause.newPlain1Clause("ao")	
	static final Clause aos1c = Clause.newRegex1Clause(PatternsPT.aos, "[ao]s")	
	static final Clause aos_1c = Clause.newRegex1Clause(PatternsPT.aos_, "[ao]s?")	
	static final Clause aos01c = Clause.newRegex01Clause(PatternsPT.aos, "[ao]s")
	static final Clause aos_01c = Clause.newRegex01Clause(PatternsPT.aos_, "[ao]s?")
	static final Clause aos01c_duplicate = Clause.newRegex01Clause(PatternsPT.aos, "[ao]s duplicate")
	static final Clause aono1 = Clause.newRegex1Clause(~/[na]o/, "[na]o")	
	static final Clause aosnos1c = Clause.newRegex1Clause(~/[na]os?/, "[na]os?")	
	static final Clause aoAO1nc = Clause.newRegex1Clause(~/[AOao]/,"[AOao]",false)	
	static final Clause aopor1c =  Clause.newRegex1Clause(PatternsPT.aopor, "(?:ao|por)")	
	static final Clause aouma1c =  Clause.newRegex1Clause(PatternsPT.aouma, "[ao]|uma?")
	static final Clause aouma1nc = Clause.newRegex1Clause(PatternsPT.aouma, "[oa]|uma?",false)	
	static final Clause as1c = Clause.newPlain1Clause("as")	
	static final Clause asdas1c = Clause.newRegex1Clause(~/d?as/, "d?as")
	static final Clause as01c = Clause.newPlain01Clause("as")	
	static final Clause as01c_duplicate = Clause.newPlain01Clause("as","as duplicate")
	static final Clause atras01c = Clause.newPlain01Clause("atrás")	

	static final Clause cedoTarde1c = Clause.newRegex1Clause(~/(?:cedo|tarde)/, "cedo|tarde")
	static final Clause com1c = Clause.newRegex1Clause(~/[Cc]om/, "[Cc]om")
   	
	static final Clause da1c = Clause.newPlain1Clause("da")
	static final Clause das1c = Clause.newPlain1Clause("das")	
	static final Clause daeo01c = Clause.newRegex01Clause(PatternsPT.daeo, "d[aeo]")
	static final Clause daeo1c = Clause.newRegex1Clause(PatternsPT.daeo, "d[aeo]")
	static final Clause daeo01nc = Clause.newRegex01Clause(PatternsPT.daeo, "d[aeo]",false)
	static final Clause daeo1nc = Clause.newRegex1Clause(PatternsPT.daeo, "d[aeo]",false)
	static final Clause daeos01c = Clause.newRegex01Clause(PatternsPT.daeos_, "d[aeo]s?")
	static final Clause daeos01nc = Clause.newRegex01Clause(PatternsPT.daeos_,"d[aeo]s?",false)
	static final Clause daeos1c = Clause.newRegex1Clause(PatternsPT.daeos_,"d[aeo]s?")
	static final Clause daeos1nc = Clause.newRegex1Clause(PatternsPT.daeos_,"d[aeo]s?", false)
	static final Clause dao01c = Clause.newRegex1Clause(~/d[ao]/,"d[ao]")	
	static final Clause de01c = Clause.newPlain01Clause("de")
	static final Clause de1c = Clause.newPlain1Clause("de")
	static final Clause de01nc = Clause.newPlain01Clause("de","de",false)
	static final Clause de1nc = Clause.newPlain1Clause("de","de",false) 
	static final Clause deCommaSlash01c = Clause.newRegex01Clause(~/(?:de|,|\/)/, "de|,|/")
	static final Clause deDesdeEntre1c = Clause.newRegex1Clause(~/(?:[Dd]e(sde)?|[Ee]ntre)/, "[Dd]e(sde)?|[Ee]ntre")
	static final Clause deem1c = Clause.newRegex1Clause(~/(?:de|em)/, "de|em")
	static final Clause desde1c = Clause.newPlain1Clause("desde")
	static final Clause dia1c = Clause.newPlain1Clause("dia")	
	static final Clause dnaeosem01c = Clause.newRegex01Clause(PatternsPT.dnaeosem, "[dn][aeo]s?|em")
	static final Clause dnaeosem01nc = Clause.newRegex01Clause(PatternsPT.dnaeosem,,"[dn][aeo]s?|em", false)
	static final Clause dnaeosem1c = Clause.newRegex1Clause(PatternsPT.dnaeosem, "[dn][aeo]s?|em")
	static final Clause dnaeosem1nc = Clause.newRegex1Clause(PatternsPT.dnaeosem,,"[dn][aeo]s?|em", false)
	static final Clause dnao1nc = Clause.newRegex1Clause(~/[NDnd][ao]/,"[NDnd][ao]",false)	
	static final Clause dnoas1c = Clause.newRegex1Clause(~/[NDnd][ao]s?/,"[NDnd][ao]s?")	

	static final Clause do01c = Clause.newPlain01Clause("do")
	static final Clause dos1c = Clause.newPlain1Clause("dos")
	static final Clause durante1c = Clause.newPlain1Clause("durante")	
	static final Clause ee1nc = Clause.newPlain1Clause("é","é",false)
	static final Clause e01c = Clause.newPlain01Clause("e") 
	static final Clause e1c = Clause.newPlain1Clause("e") 
	static final Clause efoisao1c = Clause.newRegex1Clause(PatternsPT.efoisao, "é|foi|são") 
	static final Clause efoisao1nc = Clause.newRegex1Clause(PatternsPT.efoisao,"é|foi|são",false) 
	static final Clause em01c = Clause.newPlain01Clause("em") 
	static final Clause em1c = Clause.newPlain1Clause("em") 
	static final Clause Eem01c = Clause.newRegex01Clause(PatternsPT.Eem, "[Ee]m")
	static final Clause Eem1c = Clause.newRegex1Clause(PatternsPT.Eem, "[Ee]m")
	static final Clause emdneao01c = Clause.newRegex01Clause(PatternsPT.emdnaeo, "em|[dn][eao]") 
	static final Clause emdneao1c = Clause.newRegex1Clause(PatternsPT.emdnaeo, "em|[dn][eao]")   
	static final Clause emnao01c = Clause.newRegex01Clause(PatternsPT.emnao, "em|n[ao]") 
	static final Clause emnao1c = Clause.newRegex1Clause(PatternsPT.emnao, "em|n[ao]")    
	static final Clause emnaos01c = Clause.newRegex01Clause(PatternsPT.emnaos, "em|n[ao]s?")    
	static final Clause emnaos1c = Clause.newRegex1Clause(PatternsPT.emnaos, "em|n[ao]s?")    
	static final Clause emDesde1c = Clause.newRegex1Clause( ~/(?:[Ee]m|[Dd]esde)/, "[Ee]m|[Dd]esde", true)
	static final Clause desdeEmNao1c = Clause.newRegex1Clause( ~/(?:[Ee]m|[Nn][ao]|[Dd]esde)/, "[Ee]m|[Nn][ao]|[Dd]esde", true)
	static final Clause entre1c = Clause.newPlain1Clause("entre")
    
	static final Clause fazha1c = Clause.newRegex1Clause(~/(?:[Ff]az|[Hh]á)/, "[Ff]az|[Hh]á")	
	
	static final Clause fimFinaisMeados1c = Clause.newRegex1Clause(
		~/(?:fi[mn](ai)?s?|meados|inícios?|princípios?)/, "fi[mn](ai)?s?|meados|inícios?|princípios?")
	static final Clause janeiro1c = Clause.newPlain1Clause("Janeiro") 
 
	static final Clause madrugadaManhaMatinaNoiteTarde1c = Clause.newRegex1Clause(
		~/(?:madrugada|manhã|matina|noite|tarde)/, "madrugada|manhã|matina|noite|tarde")
	static final Clause maiormenor01c = Clause.newRegex01Clause(~/(?:maior|menor)/, "maior|menor")
	static final Clause mais1c = Clause.newPlain1Clause("mais")
	static final Clause menos1c = Clause.newPlain1Clause("menos")
	static final Clause minutos1c = Clause.newRegex1Clause(~/[Mm]inutos?/, "[Mm]inutos?")
    
	static final Clause nao1c = Clause.newRegex01Clause(~/n[ao]/, "n[ao]")
	static final Clause NaosNesstaos1nc = Clause.newRegex1Clause(
		~/(?:[Nn][ao]s?|[Nn]es[st][ea]s?)/, "[Nn][ao]s?|[Nn]es[st][ea]s?", false)

	static final Clause o01c = Clause.newPlain01Clause("o") 
	static final Clause Oo1c = Clause.newRegex1Clause(~/[Oo]/,"[Oo]") 
	static final Clause o1c = Clause.newPlain1Clause("o") 
	static final Clause os01c = Clause.newPlain01Clause("os") 
	static final Clause os1c = Clause.newPlain1Clause("os") 
	static final Clause ou1c =  Clause.newPlain1Clause("ou")

	static final Clause para1c = Clause.newPlain1Clause("para")		
	static final Clause pertoarredores1c = Clause.newRegex1Clause(~/(?:perto|arredore?s?)/,"perto|arredores?")		
	static final Clause por1c = Clause.newPlain1Clause("por")
	static final Clause por01c = Clause.newPlain01Clause("por")
	static final Clause porSlash1c = Clause.newRegex1Clause(PatternsPT.porSlash, "por|/")	
	static final Clause porSlash01c = Clause.newRegex01Clause(PatternsPT.porSlash, "por|/")	
	static final Clause povo1nc = Clause.newRegex1Clause(~/povos?/,"povos?",false)
	
	static final Clause que1c = Clause.newPlain1Clause("que")
	static final Clause rio1c = Clause.newPlain1Clause("Rio")  
  
	static final Clause se1c = Clause.newPlain1Clause("se")
	static final Clause segundos1c = Clause.newPlain1Clause("segundos")	

	static final Clause todaos1c = Clause.newRegex1Clause(~/tod[ao]s?/, "tod[ao]s?")
	static final Clause todo1c = Clause.newPlain1Clause("todo")
	
	static final Clause umuma1nc = Clause.newRegex1Clause(~/uma?/,"uma?",false)
	
	static final Clause vem1c = Clause.newPlain1Clause("vem")	
	static final Clause vezvezes1c = Clause.newRegex1Clause(~/vez(?:es)?/, "vez(es)?")
}