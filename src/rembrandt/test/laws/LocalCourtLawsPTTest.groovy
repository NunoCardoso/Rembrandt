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

package rembrandt.test.laws

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.Logger

import rembrandt.obj.Courthouse
import rembrandt.obj.NamedEntity as NE
import rembrandt.obj.CourtVeredict
import rembrandt.obj.Sentence
import rembrandt.obj.Term
import rembrandt.gazetteers.CommonClassifications as SC
import rembrandt.laws.harem.pt.*

/**
 * @author Nuno Cardoso
 * Tester of Laws for LOCAL in PT.
 */
class LocalCourtLawsPTTest extends GroovyTestCase {
     
    static Logger log = Logger.getLogger("RembrandtTest")
    Courthouse courthouse
     
    // I need a courthouse. It'll load all rules for PT and HAREM
    public LocalCourtLawsPTTest() {
	courthouse = Courthouse.newInstance("pt", "harem")
	 
	
    }
    
    void testLaws() {

	/********************************
	 *  Law 1.1: id:"P:LOC>=P:LOC", description:"P:LOCAL >= D:LOCAL -> D:Try-Disambiguate-Catch-Merge-Update-Bound" 
	 ********************************/
        String lawid = "P:LOC>=P:LOC"
	
	// plaintiffNE: place_human, defendantNE: place
	NE ne_place = new NE(terms:[new Term("Portugal",10)], sentenceIndex:0, termIndex:10, classification:[SC.place])
	NE ne_place_human = new NE(terms:[new Term("Portugal",10)], sentenceIndex:0, termIndex:10, classification:[SC.place_human])
	
	List<CourtVeredict> veredicts
	
	veredicts= courthouse.judgeThis(ne_place_human, [ne_place]) // plaintiff, defendants
	assert veredicts.size() == 1
	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead"
	
	// note: it's up to the disambiguation method to understand that it's not a good idea to disambiguate a detailed classification with a generic one. 
	// That's not a responsability of the laws.
	veredicts = courthouse.judgeThis(ne_place, [ne_place_human]) // plaintiff, defendants
	assert veredicts.size() == 1
	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead"

	/***********************************
	 *  Law 1.2: id:"P:LOC<P:LOC", description:"P:LOCAL < D:LOCAL -> P:Discard 
	 ***********************************/
        lawid = "P:LOC<P:LOC"	
	
	NE ne_place2 = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.place])
	veredicts= courthouse.judgeThis(ne_place_human, [ne_place2]) // plaintiff, defendants
	assert veredicts.size() == 1
	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead"

	/***********************************
	 *  Law 2.1: id:"P:LOC_HUM_RUA>=D:(E)LOC_HUM", description:"P:LOCAL_HUMANO_RUA >= D:LOCAL_HUMANO -> Try-Disamb-Catch-Merge-Update-Bound",
	 *          plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
	 *	    plaintiffEvidence:[[SC.place_human_street]],
	 *	    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Type]], 
	 *	    defendantEvidence:[[SC.place_human]],
	 ***********************************/
        lawid = "P:LOC_HUM_RUA>=D:(E)LOC_HUM"	
	 
    	NE ne_plaintiff = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.place_human_street])
   	NE ne_defendant1 = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.place_human])
   	NE ne_defendant2 = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.place_human, SC.organization])
    	veredicts = courthouse.judgeThis(ne_plaintiff, [ne_defendant1]) // plaintiff, defendants
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead"

    	veredicts = courthouse.judgeThis(ne_plaintiff, [ne_defendant2]) // plaintiff, defendants  	
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead"

	/***********************************
	 *  Law 2.2:id:"P:LOC_FIS_AGUACURSO>=D:(!A)LOC_HUM_AGUACURSO", 
		    description:"P:LOCAL_FISICO_AGUACURSO >= D:(!A)LOCAL_FISICO_AGUACURSO -> Try-Disamb-Catch-Replace-Update-Bound"
	 *          plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
		    plaintiffEvidence:[[SC.place_physical_watercourse]],
		    defendantCriteria:[[CC.NeverAllOfThese, CC.AllOfThem, CC.Subtype]], 
		    defendantEvidence:[[SC.place_physical_watercourse]],
	 ***********************************/
        lawid = "P:(A)LOC_FIS_AGUACURSO>=D:(E)LOC_FIS_AGUACURSO"	
        String lawid2 = "P:(A)LOC_FIS_AGUACURSO==D:(A)LOC_HUM_DIVISAO"
        String lawid3 = "P:(A)LOC_FIS_AGUACURSO>=D:(!E)LOC_HUM_AGUACURSO"
        
    	ne_plaintiff = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.place_physical_watercourse])
   	ne_defendant1 = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.person])
   	ne_defendant2 = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.person, SC.place_physical_watercourse])
        NE ne_defendant3 = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.place_human_division])
        
        veredicts = courthouse.judgeThis(ne_plaintiff, [ne_plaintiff]) 
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead" // should fail on NeverAllOfThese

    	veredicts = courthouse.judgeThis(ne_plaintiff, [ne_defendant1]) 	
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id == lawid3, "Got ${veredicts[0].law.id} instead"
 
    	veredicts = courthouse.judgeThis(ne_plaintiff, [ne_defendant3]) 	
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id == lawid2, "Got ${veredicts[0].law.id} instead"

    	/***********************************
	 *  Law 2.3:id:"P:LOC_FIS_ILHA>=D:(!A)LOC_FIS_ILHA", 
		    description:"P:LOCAL_FISICO_ILHA >= D:(!A)LOCAL_FISICO_ILHA -> Try-Disamb-Catch-Replace-Update-Bound",
	 *          plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
		    plaintiffEvidence:[[SC.place_physical_island]],
		    defendantCriteria:[[CC.NeverAllOfThese, CC.AllOfThem, CC.Subtype]], 
		    defendantEvidence:[[SC.place_physical_island]],
	 ***********************************/
        lawid = "P:LOC_FIS_ILHA>=D:(!A)LOC_FIS_ILHA"	
	 
    	ne_plaintiff = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.place_physical_island])
   	ne_defendant1 = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.person])
   	ne_defendant2 = new NE(terms:[new Term("Portugal",10), new Term("S.A.",11)], sentenceIndex:0, termIndex:10, classification:[SC.person, SC.place_physical_island])

        veredicts = courthouse.judgeThis(ne_plaintiff, [ne_plaintiff]) 
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id != lawid, "Got ${veredicts[0].law.id} instead" // should fail on NeverAllOfThese

    	veredicts = courthouse.judgeThis(ne_plaintiff, [ne_defendant1]) 	
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead"
 
    	veredicts = courthouse.judgeThis(ne_plaintiff, [ne_defendant2]) 	
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead"
    	
   	/***********************************
	 *  Law 3.1:id:"P:LOC>=D:[NUM|TEM|VAL]", description:"P:LOCAL >= D:[NUMERO|TEMPO|DATA|EM] -> ReplaceCompletely",
	 *         plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category], BC.ExactOrContains],
		    plaintiffEvidence:[[SC.place]],
		    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category]], 
		    defendantEvidence:[[SC.number, SC.time, SC.value]],
	 ***********************************/
        lawid = "P:LOC>=D:[NUM|TEM|VAL]"	
	 
    	ne_plaintiff = new NE(terms:[new Term("A",0)], sentenceIndex:0, termIndex:0, classification:[SC.place])
   	ne_defendant1 = new NE(terms:[new Term("A",0)], sentenceIndex:0, termIndex:0, classification:[SC.place, SC.number])
   	ne_defendant2 = new NE(terms:[new Term("A",0)], sentenceIndex:0, termIndex:0, classification:[SC.place, SC.number, SC.time])
   	ne_defendant3 = new NE(terms:[new Term("A",0)], sentenceIndex:0, termIndex:0, classification:[SC.number, SC.time])
 
        veredicts = courthouse.judgeThis(ne_plaintiff, [ne_plaintiff]) 
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id != lawid, "Got ${veredicts[0].law.id} instead" // should fail 

    	veredicts = courthouse.judgeThis(ne_plaintiff, [ne_defendant1]) 	
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead"
 
    	veredicts = courthouse.judgeThis(ne_plaintiff, [ne_defendant2]) 	
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead" 	
    	
    	veredicts = courthouse.judgeThis(ne_plaintiff, [ne_defendant3]) 	
    	assert veredicts.size() == 1
    	assert veredicts[0].law.id == lawid, "Got ${veredicts[0].law.id} instead"
/*	 
 
	 
	    

	    //  3.2 PESSOA INDIVIDUAL is eaten by RUA, same or bigger
	    //  Example: rua <PESSOA>Avelino</PESSOA> -> rua <RUA>Avelino</RUA>
	    // Example: <ORG> Escola Política</ORG> mas pode ser rua <RUA>Escola Polótica</RUA>

	    new Law(id:"P:LOC_HUM_STR>=D:(E)[PER|ORG]", description:"P:LOCAL_HUMANO_RUA >= D:[NUMERO|TEMPO|DATA|EM] -> Try-Disamb-Catch-Replace-Update-Bound",
		    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
		    plaintiffEvidence:[[SC.place_human_street]],
		    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.ExistsAtLeastOneOfThem, CC.Category]], 
		    defendantEvidence:[[SC.person, SC.organization]],
		    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries), 
	    
	    // 3.3 PESSOA POVO is eaten by LOCAL FISICO, same or bigger
	    //  Example: <POVO>Tapaj√≥s</POVO> -> rio <AGUACURSO>Tapaj√≥s</AGUACURSO>
	   new Law(id:"P:LOC_PHY>=D:PER_PEO", description:"P:LOCAL_FISICO >= D:PESSOA_POVO -> Try-Disamb-Catch-Replace-Update-Bound",
		    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Type], BC.ExactOrContains], 
		    plaintiffEvidence:[[SC.place_physical]],
		    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Type]], 
		    defendantEvidence:[[SC.person_people]],
		    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries), 

	    // 3.4 OBRA -> LOCAL
	    // Example: rua <OBRA REPRODUZIDA>Central do Brasil</OBRA> -> desambigua para LOCAL, ou então MERGE
	    new Law(id:"P:LOC_HUM>=D:MAS", description:"P:LOCAL_HUMANO >= D:OBRA -> Try-Disamb-Catch-Merge-Update-Bound",
		    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Type], BC.ExactOrContains], 
		    plaintiffEvidence:[[SC.place_human]],
		    defendantCriteria:[[CC.ExistsAtLeastOneOfThese, CC.AllOfThem, CC.Category]], 
		    defendantEvidence:[[SC.masterpiece]],
	  	    decision:CD.TryDisambiguateDefendant_CatchMergeClassification_UpdateBoundaries),
	    
	    // 3.5 ORGANIZACAO -> LOCAL 1
	    // Example: site da <ORG>SONAL</ORG> -> passa para LOCAL SITIO, exact
	    new Law(id:"P:LOC_VIR_SIT>=D:ORG", description:"LOCAL_VIRTUAL_SITE >= D:ORGANIZATION -> Try-Disamb-Catch-Repl-Update-Bound",
		    plaintiffCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Subtype], BC.ExactOrContains], 
		    plaintiffEvidence:[[SC.place_virtual_site]],
		    defendantCriteria:[[CC.AllOfThese, CC.AllOfThem, CC.Category]], 
		    defendantEvidence:[[SC.organization]],
		    decision:CD.TryDisambiguateDefendant_CatchReplaceClassification_UpdateBoundaries)
	]

	 */
    }  
}