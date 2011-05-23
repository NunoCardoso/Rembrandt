/* Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package rembrandt.rules.harem.pt

import rembrandt.obj.Clause
import rembrandt.obj.Rule
import rembrandt.obj.RulePolicy
import rembrandt.obj.ConflictPolicy
import rembrandt.obj.Cardinality
import rembrandt.obj.Criteria
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.pt.PersonGazetteerPT
import rembrandt.gazetteers.pt.ClausesPT
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector
/**
 * @author Nuno Cardoso
 * external evidence rules for PESSOA category.
 */
class PersonRulesPT extends NamedEntityDetector {
   
    static List<Rule> rules
 
    /**
     * Main constructor
     */    
    public PersonRulesPT() {
	    
	rules = []
				   
   //{parentescoInclude1c! daeo? <EM>!} ex: {tio João}, {tio de Maria}
   rules.add(new Rule(id:"PersonRulesPT 1:parentescoInclude1", description:"{parentescoInclude1c! daeo? <EM>!}", 
   	sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
   		 PersonGazetteerPT.parentescoInclude1c, 
   		 ClausesPT.daeo01c,
   		 NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 

   //parentescoExclude1c! daeo? {<EM>} ex: sobrinho {José}, sobrinho da {Gra√ßa}
   rules.add(new Rule(id:"PersonRulesPT 2:parentescoExcludeList1", description:"parentescoExclude1c! daeo? {<EM>!}",
	sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
	     PersonGazetteerPT.parentescoExclude1nc, 
	     ClausesPT.daeo01nc,
             NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
         	   
    //{cargoInclude1c! <EM>!} ex: {presidente Cavaco}
   rules.add(new Rule(id:"PersonRulesPT 3:cargoIncludeList1", description:"{cargoInclude1c! <EM>!}",
	sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
	     PersonGazetteerPT.cargoIncludeSingle1c, 
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
    
   //{cargoInclude1c! de! <EM>!} ex: {presidente de Portugal} -> goes "CARGO"
   rules.add(new Rule(id:"PersonRulesPT 4:cargoIncludeList2", description:"{cargoInclude1c! daeo! <EM>!}",
	sc:SC.person_position, policy:RulePolicy.Clause, addpolicy:ConflictPolicy.GenerateALT, clauses:[ 
	     PersonGazetteerPT.cargoIncludeSingle1c, 
	     ClausesPT.daeo1c, 
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] ))
	    
   //cargoExclude1c! {<EM>!} ex: comandante {João}
    rules.add(new Rule(id:"PersonRulesPT 5:cargoExcludeList3", description:"cargoExclude1c! {<EM>!}",
	sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
	     PersonGazetteerPT.cargoExcludeSingle1nc, 
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 

	// cargoExclude1c! of! {<EM>!}¬†is too dangerous, <EM> can be other things than PERSON.     

   // PERSON INDIVIDUALGROUP
	     
    //{cargoIncludePlural1c! <EM>!} ex: {presidentes Miranda}	
    rules.add(new Rule(id:"PersonRulesPT 6:cargoIncludePluralList1", description:"{cargoIncludePlural1c! <EM>!}",
	sc:SC.person_individualgroup, policy:RulePolicy.Clause, clauses:[ 
	     PersonGazetteerPT.cargoIncludePlural1c, 
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
	
  // PERSON POSITIONGROUP
	    
     //{cargoIncludePlural1c! of! <EM>!} ex: {presidentes da UE} -> goes "GRUPOCARGO"
    rules.add(new Rule(id:"PersonRulesPT 7:cargoIncludePluralList2", description:"{cargoIncludePlural1c! of! <EM>!}",
	sc:SC.person_positiongroup, policy:RulePolicy.Clause, addpolicy:ConflictPolicy.GenerateALT, clauses:[ 
	     PersonGazetteerPT.cargoIncludePlural1c, 
	     ClausesPT.daeo1c, 
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] ))
 
   // PERSON INDIVIDUALGROUP
	 
   //cargoExcludePlural1c! {<EM>!} ex: comandantes {Teixeira}
    rules.add(new Rule(id:"PersonRulesPT 8:cargoExcludePluralList3", description:"cargoExcludePlural1c! {<EM>!}",
	    sc:SC.person_individualgroup, policy:RulePolicy.Clause, clauses:[ 
	     PersonGazetteerPT.cargoExcludePlural1nc, 
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
		
   // jobIncludeList <EM> (ex: arquitecto Basílio) -> tudo a PESSOA INDIVIDUAL.
   // jobIncludeList deaos <EM> (ex: arquitecto de Basílio) -> não sei, deixo estar... pode ser ORG, LOCAL, Etc..
   // jobExcludeList <EM> (ex: músico Basílio) -> só EM a PESSOA INDIVIDUAL.
   // jobExcludeList deaos <EM> (ex: músico de Basílio) -> não sei, deixo estar... pode ser ORG, LOCAL, Etc..
	     
   // PERSON INDIVIDUAL	     
	     
   //{jobInclude1c! <EM>!} ex: {doutor Jivago}	
    rules.add(new Rule(id:"PersonRulesPT 9:jobIncludeList1", description:"{jobInclude1c! <EM>!}",
	    sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
	     PersonGazetteerPT.jobInclude1c, 
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
	 
   //jobExclude1c! {<EM>!} ex: músico {João}
    rules.add(new Rule(id:"PersonRulesPT 10:jobExcludeList2", description:"jobExclude1c! {<EM>!}",
	    sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
	     PersonGazetteerPT.jobExclude1nc, 
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
		 
    // o [deputado|engenheiro|...]!  [português|democrata|...]! {<EM>!}
    rules.add(new Rule(id:"PersonRulesPT 11:otherPersonOccupation 1", description:"[deputado|engenheiro|...]! [português|democrata|...]! {<EM>!}",
	    sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
	     PersonGazetteerPT.jobAll1nc, 
	     PersonGazetteerPT.personAdjective01nc, 	         
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] ))				
	
     //[Liderado por|...]! {<EM>!}, etc
    rules.add(new Rule(id:"PersonRulesPT 12:LeadBy1", description:"[liderado por|...]! {<EM>!}",
	    sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
	     PersonGazetteerPT.personActionPrefix1nc,  
	     NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] ))					

    // {<EM>!} é|foi! uma! [architect|man|...]!
      rules.add(new Rule(id:"PersonRulesPT 13:<EM> é um XX",  description:"{<EM>!} é|foi! uma?! [arquitecto|homem|...]!",
	      sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
	    NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c, 
	    ClausesPT.efoisao1nc, ClausesPT.umuma1nc, 
	    PersonGazetteerPT.jobAllPlusOtherQualifier1nc] ))			
 
// PERSON PEOPLE
   
//  povo {<EM>!}
      rules.add(new Rule(id:"PersonRulesPT 14:POVO1", description:"{<EM>!} people!",
	      sc:SC.person_people, policy:RulePolicy.Clause, clauses:[  
	      PersonGazetteerPT.people1nc, 
	      NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c ] ))			
   }
}