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
 
package rembrandt.gazetteers.en

import rembrandt.obj.Clause
import rembrandt.obj.Criteria
import rembrandt.obj.Cardinality
import rembrandt.obj.ClassificationCriteria
import rembrandt.gazetteers.Patterns

/**
 * @author Nuno Cardoso
 * Gazetteer of common clauses for English.
 */
class ClausesEN {
    
	static final Clause aan1nc = Clause.newRegex1Clause(~/an?/, 'an?', false)
	static final Clause aboutAboveApproximatelyBelow1c  = Clause.newRegex1Clause(
		~/(?:about|above|approximately|below|more|less)/, "about|above|approximately|below|more|less")
	static final Clause afterBeforeTodayTomorrowYesterday1c = Clause.newRegex1Clause(
		~/(?:after|before|yesterday|tomorrow|today)/, "after|before|yesterday|tomorrow|today")
	static final Clause afterBeforeSince01c  = Clause.newRegex01Clause(
		~/(?:after|before|since)/,"after|before|since")
	static final Clause afterBeforeSince1c  = Clause.newRegex1Clause(
		~/(?:after|before|since)/,"after|before|since")
	static final Clause afterbBeforeSinceMorningAfternoonNight_01c = Clause.newRegex01Clause(
		~/(?:after|before|since|morning|afternoon|night)/, "after|before|since|morning|afternoon|night")   
	static final Clause afternoonMorningNight1c = Clause.newRegex1Clause(
		~/(?:afternoon|morning|evening|night)/, "afternoon|morning|evening|night")
	static final Clause all1c = Clause.newPlain1Clause('all')
	static final Clause and01c = Clause.newPlain01Clause('and')
	static final Clause and01nc = Clause.newPlain01Clause('and','and',false)
	static final Clause and1c = Clause.newPlain1Clause('and')
	static final Clause and1nc = Clause.newPlain1Clause('and','and',false)
	static final Clause andComma1c = Clause.newRegex1Clause(~/(?:and|,)/,'and|,')
	static final Clause andComma01c = Clause.newRegex01Clause(~/(?:and|,)/,'and|,')
	static final Clause andUntil1c = Clause.newRegex1Clause(~/(?:and|'til|until)/,"and|(un|')til",) //"
	static final Clause apostrophe1nc  = Clause.newPlain1Clause("'s","'s",false)
	static final Clause apostrophe01nc  = Clause.newPlain01Clause("'s","'s",false)	
	static final Clause apostrophe1c  = Clause.newPlain1Clause("'s")
	static final Clause apostrophe01c  = Clause.newPlain01Clause("'s")	
	static final Clause ation1c = Clause.newRegex1Clause(~/(?:at|[io]n)/,'at|[io]n' )

	static final Clause beginningEndFinal1c = Clause.newRegex1Clause(
		~/(?:begin(ning)?s?|ends?|finals?)/,"begin(ning)?s?|ends?|finals?")
	static final Clause between1c = Clause.newPlain1Clause('between')
	static final Clause betweenFromSince1c = Clause.newRegex1Clause(
		~/(?:[Bb]etween|[Ff]rom|[Ss]ince)/,'[Bb]etween|[Ff]rom|[Ss]ince')

	static final Clause during1c = Clause.newPlain1Clause('during')
	
	static final Clause earlierSooner01c  = Clause.newRegex01Clause(
		~/(?:earlier|sooner)/,"earlier|sooner")	

	static final Clause followingPastLastNext01c = Clause.newRegex01Clause(
		~/(?:following|past|last|next)/,"following|past|last|next")
		
 	static final Clause holidaysVacations01c = Clause.newRegex01Clause(
 		~/(?:holidays?|vacations?)/,"holidays?|vacations?")
 	
 	static final Clause ionSinceUntil1c = Clause.newRegex1Clause(
 		~/(?:[IiOo]n|[Ss]ince|[Uu]ntil)/, "[IiOo]n|[Ss]ince|[Uu]ntil")
 	
 	static final Clause Iin1c = Clause.newRegex1Clause(PatternsEN.Iin, "[Ii]n")
 	static final Clause Iin1nc = Clause.newRegex1Clause(PatternsEN.Iin, "[Ii]n",false)
 	static final Clause in01c = Clause.newPlain01Clause("in")
 	static final Clause in1c = Clause.newPlain1Clause("in")
	static final Clause inof1nc  = Clause.newRegex1Clause(~/(?:in|of)/,"in|of", false)
	static final Clause into1c = Clause.newRegex1Clause(~/(?:in|to)/,"in|to")
 	static final Clause is1c  = Clause.newPlain1Clause("is")
 	static final Clause is1nc  = Clause.newPlain1Clause("is","is",false)
 	
 	static final Clause moreLess1c  = Clause.newRegex1Clause(~/(?:more|less)/,"more|less")
  	static final Clause minutes1c  = Clause.newRegex1Clause(~/minutes?/,"minutes?")

	static final Clause near1c = Clause.newPlain1Clause('near')
	static final Clause neararoundalongbetween1c = Clause.newRegex1Clause(~/(?:near|around|along|between)/,
		"near|around|along|between")
	static final Clause nextPast01c = Clause.newRegex01Clause(~/(?:next|past)/,"next|past")
	static final Clause nowadaysrecently1c = Clause.newRegex1Clause(
	   ~/(?:now(?:adays)?|recently)/,"now(?:adays)?|recently")	
	
	static final Clause of01c = Clause.newPlain01Clause("of")
	static final Clause of1c = Clause.newPlain1Clause("of") 
	static final Clause of01nc = Clause.newPlain01Clause("of","of",false)
	static final Clause of1nc = Clause.newPlain1Clause("of","of",false)
	static final Clause ofComma01c = Clause.newRegex01Clause(~/(?:of|,)/,"of|,")
	static final Clause ofCommaSlash01c= Clause.newRegex01Clause(~/(?:of|,|\/)/,"of|,|/")

	static final Clause part01c = Clause.newPlain01Clause("part")
	static final Clause per01c = Clause.newPlain01Clause("per")		
	static final Clause per1c = Clause.newPlain1Clause("per")
	static final Clause percent1c = Clause.newPlain1Clause("percent")
		
	static final Clause seconds1c  = Clause.newRegex1Clause(~/seconds?/,"seconds?")
	static final Clause than1c = Clause.newPlain1Clause("than")
	static final Clause than01c = Clause.newPlain01Clause("than")
	
	static final Clause the01c = Clause.newPlain01Clause("the")
  	static final Clause the1c = Clause.newPlain1Clause("the")
	static final Clause the01nc = Clause.newPlain01Clause("the","the",false)
  	static final Clause the1nc = Clause.newPlain1Clause("the","the",false)
	static final Clause times1c = Clause.newPlain1Clause("times")

	static final Clause within1c = Clause.newPlain1Clause("within")
 }