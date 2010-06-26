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
import rembrandt.obj.ConflictPolicy
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.en.PersonGazetteerEN
import rembrandt.gazetteers.en.ClausesEN
import rembrandt.gazetteers.NEGazetteer
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
import rembrandt.rules.NamedEntityDetector

/**
 * @author Nuno Cardoso
 * external evidence rules for PERSON category.
 */
class PersonRulesEN extends NamedEntityDetector {
   
    List<Rule> rules
    SemanticClassification sc
 
    /**
     * Main constructor
     */    
    public PersonRulesEN() {
	    
	rules = []
	
	/** PESSOA INDIVIDUAL */
	   
	// 1 {parentescoInclude1c! of? <EM>} ex: {brother Grimm}, {father of Mary}
	rules.add(new Rule(id:"PersonRulesEN 1:parentescoIncludeList1", description:"{parentescoInclude1c! of? <EM>!}", 
		sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
	   	PersonGazetteerEN.parentescoInclude1c, ClausesEN.of01c,
	   	NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 

	// 2 parentescoExclude1c! of? {<EM>} ex: nephew {Grimm}, sister of {Grace}
	rules.add(new Rule(id:"PersonRulesEN 2:parentescoExcludeList1", description:"parentescoExclude1nc! of? {<EM>!}",
		sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
		PersonGazetteerEN.parentescoExclude1nc, ClausesEN.of01nc,
	        NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 

       //3 {cargoInclude1c! <EM>!} ex: {president Grimm}
	rules.add(new Rule(id:"PersonRulesEN 3:cargoIncludeList1", description:"{cargoInclude1c! <EM>!}",
		sc:SC.person_position, policy:RulePolicy.Clause, clauses:[ 
		PersonGazetteerEN.cargoIncludeSingle1c, NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
	
	// 4 {cargoInclude1c! of! <EM>!} ex: {president of Portugal} -> goes "CARGO"
	rules.add(new Rule(id:"PersonRulesEN 4:cargoIncludeList2", description:"{cargoInclude1c! of! <EM>!}",
		sc:SC.person_position, policy:RulePolicy.Clause, addpolicy:ConflictPolicy.GenerateALT, clauses:[ 
		PersonGazetteerEN.cargoIncludeSingle1c, ClausesEN.of1c, NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c]  ))
		 
	// 5 cargoExclude1c! {<EM>!} ex: commander {Grimm}
	rules.add(new Rule(id:"PersonRulesEN 5:cargoExcludeList3", description:"cargoExclude1c! {<EM>!}",
		sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
		PersonGazetteerEN.cargoExcludeSingle1nc, NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
	
	// cargoExclude1c! of! {<EM>!}�is too dangerous, <EM> can be other things than PERSON.     
	
	//{cargoIncludePlural1c! <EM>!} ex: {presidents Grimm}	
	rules.add(new Rule(id:"PersonRulesEN 6:cargoIncludePluralList1", description:"{cargoIncludePlural1c! <EM>!}",
		sc:SC.person_individualgroup, policy:RulePolicy.Clause, clauses:[ 
		PersonGazetteerEN.cargoIncludePlural1c, NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 

	 //{cargoIncludePlural1c! of! <EM>!} ex: {presidents of UE} -> goes "GRUPOCARGO"
	rules.add(new Rule(id:"PersonRulesEN 7:cargoIncludePluralList2", description:"{cargoIncludePlural1c! of! <EM>!}",
		 sc:SC.person_positiongroup, policy:RulePolicy.Clause, addpolicy:ConflictPolicy.GenerateALT, clauses:[ 
		 PersonGazetteerEN.cargoIncludePlural1c, ClausesEN.of1c, NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] ))

	//cargoExcludePlural1c! {<EM>!} ex: commanders {Grimm}
	rules.add(new Rule(id:"PersonRulesEN 8:cargoExcludePluralList3", description:"cargoExclude1c! {<EM>!}",
		sc:SC.person_individualgroup, policy:RulePolicy.Clause, clauses:[ 
		PersonGazetteerEN.cargoExcludePlural1nc, NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
			
	   // jobIncludeList <EM> (ex: arquitecto Bas�lio) -> tudo a PESSOA INDIVIDUAL.
	   // jobIncludeList deaos <EM> (ex: arquitecto de Bas�lio) -> n�o sei, deixo estar... pode ser ORG, LOCAL, Etc..
	   // jobExcludeList <EM> (ex: m�sico Bas�lio) -> s� EM a PESSOA INDIVIDUAL.
	   // jobExcludeList deaos <EM> (ex: m�sico de Bas�lio) -> n�o sei, deixo estar... pode ser ORG, LOCAL, Etc..

	
	//{jobInclude1c! <EM>!} ex: {doctor Grimm}	
	rules.add(new Rule(id:"PersonRulesEN 9:jobIncludeList1", description:"{jobInclude1c! <EM>!}",
		sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
		PersonGazetteerEN.jobInclude1c, NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
		 
	//jobExclude1c! {<EM>!} ex: musician {Grimm}
	rules.add(new Rule(id:"PersonRulesEN 10:jobExcludeList2", description:"jobExclude1c! {<EM>!}",
		sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
		PersonGazetteerEN.jobExclude1nc, NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] )) 
			 
	 // the [portuguese|democrat|...]! [deputee|engineer|...]! {<EM>!}
	rules.add(new Rule(id:"PersonRulesEN 11:otherPersonOccupation", description:"[portuguese|democrat|...]! [deputee|engineer|...]! {<EM>!}",
		 sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
	         PersonGazetteerEN.personAdjective01nc, 
	         PersonGazetteerEN.jobAll1nc, 
	         NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] ))				
		
	 //[lead by|...]! {<EM>!}, etc 
	 rules.add(new Rule(id:"PersonRulesEN 12:LeadBy1", description:"[lead by|...]! {<EM>!}",
		 sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
		 PersonGazetteerEN.personActionPrefix1nc,  
		 NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c] ))					

		 
	// {<EM>!} is! [a|an]! [architect|man|...]!
	rules.add(new Rule(id:"PersonRulesEN 13:<EM> is a Y",  description:"{<EM>!} is! [a|an]! [architect|man|...]!",
		sc:SC.person_individual, policy:RulePolicy.Clause, clauses:[ 
		NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c, 
		ClausesEN.is1nc, ClausesEN.aan1nc, 
		PersonGazetteerEN.jobAllPlusOtherQualifier1nc] ))			

	 //  {<EM>!} people!
	rules.add(new Rule(id:"PersonRulesEN 13:POVO1", description:"{<EM>!} people!",
	        sc:SC.person_people, policy:RulePolicy.Clause, clauses:[  
		NEGazetteer.NE_Anything_Except_NUMERO_TEMPO_VALOR_1c, 
		PersonGazetteerEN.people1nc] ))					
	}
}