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

package rembrandt.gazetteers
 
import rembrandt.obj.Clause
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality
import rembrandt.obj.ClassificationCriteria as CC 
import java.util.regex.Pattern
import rembrandt.gazetteers.CommonClassifications as SC

/**
 * @author Nuno Cardoso
 * Gazetteer of special clauses around NEs.
 */
class NEGazetteer {
	

    /***********************
     * NamedEntity Clauses *
     ***********************/
     
    /*** Collectable NEs ***/

    /** Anything, Anything Except **/
     
    static final Clause NE_Anything_1c = new Clause(name:"NE_Anything_1", cardinality:Cardinality.One, 
	    criteria:Criteria.NEMatch, NECriteria:[CC.AnyKnownOrUnknownCategory], collectable:true)

    static final Clause NE_Anything_Except_NUMERO_1c = new Clause(name:"NE_Anything_Except_NUMERO_1", 
	       cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	       NECriteria:[CC.NeverExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category], 
	       pattern:[SC.number], collectable:true)

    static final Clause NE_Anything_Except_NUMERO_TEMPO_VALOR_1c = new Clause(name:"NE_Anything_Except_NUMERO_TEMPO_VALOR_1", 
	       cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	       NECriteria:[CC.NeverExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category], 
	       pattern:[SC.number, SC.time, SC.value], collectable:true)
  
    static final Clause NE_Anything_Except_NUMERO_TEMPO_VALOR_EM_1c = new Clause(name:"NE_Anything_Except_NUMERO_TEMPO_VALOR_EM_1", 
	       cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	       NECriteria:[CC.NeverExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category], 
	       pattern:[SC.number, SC.time, SC.value, SC.unknown], collectable:true)
    
    /**NUMERO **/   
    
    static final Clause NE_NUMERO_1c = new Clause(name:"NE_NUMERO_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.number], collectable:true)
    //duplicate
    static final Clause NE_NUMERO2_1c = new Clause(name:"NE_NUMERO2_1", 	    
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.number], collectable:true)
    
    static final Clause NE_NUMERO_01c = new Clause(name:"NE_NUMERO_01", 
	    cardinality:Cardinality.ZeroOrOne, criteria:Criteria.NEMatch,  
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.number], collectable:true)
    //duplicate
    static final Clause NE_NUMERO2_01c = new Clause(name:"NE_NUMERO2_01", 
	    cardinality:Cardinality.ZeroOrOne, criteria:Criteria.NEMatch,  
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.number], collectable:true)
    
    static final Clause NE_NUMERO_ORDINAL_1c = new Clause(name:"NE_NUMERO_ORDINAL_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Type], 
	    pattern:[SC.number_ordinal], collectable:true)

    static final Clause NE_NUMERO_TEXTUAL_1c = new Clause(name:"NE_NUMERO_TEXTUAL_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Type], 
	    pattern:[SC.number_textual], collectable:true)
//duplicate
    static final Clause NE_NUMERO_TEXTUAL2_1c = new Clause(name:"NE_NUMERO_TEXTUAL2_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Type], 
	    pattern:[SC.number_textual], collectable:true)
 
    static final Clause NE_NUMERO_TEXTUAL_01c = new Clause(name:"NE_NUMERO_TEXTUAL_01", 
	    cardinality:Cardinality.ZeroOrOne, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Type],  
	    pattern:[SC.number_textual], collectable:true)
 

    /** EM **/ 
 	
    static final Clause NE_EM_1c = new Clause(name:"NE_EM_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.unknown], collectable:true)
   //duplicate
    static final Clause NE_EM2_1c = new Clause(name:"NE_EM2_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.unknown], collectable:true)

   /** VALOR/VALUE **/
 	
    static final Clause NE_VALOR_QUANTIDADE_1c = new Clause(name:"NE_VALOR_QUANTIDADE_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Type], 
	    pattern:[SC.value_quantity], collectable:true)
    
    static final Clause  NE_VALOR_CLASSIFICACAO_1c = new Clause(name:"NE_VALOR_CLASSIFICACAO_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Type], 
	    pattern:[SC.value_classification], collectable:true)
  
    static final Clause  NE_VALOR_MOEDA_1c = new Clause(name:"NE_VALOR_MOEDA_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Type], 
	    pattern:[SC.value_currency], collectable:true) 

    static final Clause  NE_NUMERO_VALOR_MOEDA_1c = new Clause(name:"NE_NUMERO_VALOR_MOEDA_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.ExistsAtLeastOneOfThem, CC.Type], 
	    pattern:[SC.number_numeral, SC.number_textual, SC.value_currency], collectable:true) 

    /** TEMPO/TIME **/
    
    static final Clause NE_TEMPO_01c = new Clause(name:"NE_TEMPO_01", 
	    cardinality:Cardinality.ZeroOrOne, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.time], collectable:true)

    static final Clause NE_TEMPO_1c = new Clause(name:"NE_TEMPO_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.time], collectable:true)
    //duplicate
    static final Clause NE_TEMPO_1c_duplicate = new Clause(name:"NE_NUMERO_1_dupliacte", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.time], collectable:true)
   
   static final Clause NE_TEMPO_NUMERO_1c = new Clause(name:"NE_TEMPO_NUMERO_1", 
	   cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	   NECriteria:[CC.AllOfThese, CC.ExistsAtLeastOneOfThem, CC.Category], 
	   pattern:[SC.time,SC.number], collectable:true)
    
    static final Clause NE_TEMPO_DATA_1c = new Clause(name:"NE_TEMPODATA_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch,
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Subtype], 
	    pattern:[SC.time_calendar_date], collectable:true) 
	
    static final Clause NE_TEMPO_DATA_01c = new Clause(name:"NE_TEMPODATA_01", 
	    cardinality:Cardinality.ZeroOrOne, criteria:Criteria.NEMatch,
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Subtype], 
	    pattern:[SC.time_calendar_date], collectable:true) 

    static final Clause NE_TEMPO_HORA_1c = new Clause(name:"NE_TEMPOHORA_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch,
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Subtype], 
	    pattern:[SC.time_calendar_hour], collectable:true) 

	/*** LOCAL ***/
	
   static final Clause NE_LOCAL_01c = new Clause(name:"NE_LOCAL_1", 
	    cardinality:Cardinality.ZeroOrOne, criteria:Criteria.NEMatch,
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.place], collectable:true) 

   static final Clause NE_LOCAL_1c = new Clause(name:"NE_LOCAL_1", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch,
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Category], 
	    pattern:[SC.place], collectable:true) 

	
    /*** Not Collectable NEs ***/
    
    static final Clause NE_ACONTECIMENTO_ORGANIZADO_1nc = new Clause(name:"NE_ACONTECIMENTO_ORGANIZADO_1nc", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Type], 
	    pattern:[SC.event_organized], collectable:false)
    
    static final Clause NE_PESSOA_CARGO_1nc = new Clause(name:"NE_PESSOA_CARGO_1nc", 
	    cardinality:Cardinality.One, criteria:Criteria.NEMatch, 
	    NECriteria:[CC.AllOfThese, CC.AllOfThem, CC.Type], 
	    pattern:[SC.person_position], collectable:false)

    /**************************
	 * Meaning & Query Clause *
     **************************/

    static final Clause meaningPT1c = new Clause(cardinality:Cardinality.One, criteria:Criteria.MeaningMatch, 
		collectable:true, pattern:rembrandt.gazetteers.pt.WikipediaCategoryDefinitionsPT.meanings)
    static final Clause meaningEN1c = new Clause(cardinality:Cardinality.One, criteria:Criteria.MeaningMatch, 
		collectable:true, pattern:rembrandt.gazetteers.en.WikipediaCategoryDefinitionsEN.meanings)
    static final Clause ie_meaningsPT1c = new Clause(cardinality:Cardinality.One, criteria:Criteria.MeaningMatch, 
		collectable:true, pattern:rembrandt.gazetteers.pt.InternalEvidenceDefinitionsPT.meanings)
    static final Clause ie_meaningsEN1c = new Clause(cardinality:Cardinality.One, criteria:Criteria.MeaningMatch, 
		collectable:true, pattern:rembrandt.gazetteers.en.InternalEvidenceDefinitionsEN.meanings)  
    static final Clause query1c = new Clause(cardinality:Cardinality.One, criteria:Criteria.MultipleTermMatch, 
	        collectable:true)   
}



