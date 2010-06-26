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
package rembrandt.obj

/**
 * @author Nuno Cardoso
 * Types of time grounding
 */
enum TimeGroundingType {
    
    	/** when I don't have a clue (the null) */
    	UNKNOWN("?"),
        
        /** the date is unambiguously defined at least within a year span.
          * Example: 25 of December, 2007. December 2007. 2007. 
          * Note that, although 2007  spans a full year, it's pretty defined, thus that inference is easy to make.
          * Note that, for '25 December' it can be absolute (one instance) or periodical 
          * ( as in "in all days of 25 December I sleep").
          */
    	ABSOLUTE_DATE("!"), 
        
        /**
         * Relative datas are "open timeline areas" in relation to another date.
         * So, "two days before, " is a RELATIVE_DATE_IN_THE_PAST of -2Days.
         * It's up to the TimeSigature approach to convert relatives to absolutes
         * 
         * note that in "two days before 23 December", I can solve it to 21 December. 
         */
        RELATIVE_DATE_IN_THE_PAST("-"),
        
        /*
         * So, "two days after, " is a RELATIVE_DATE_IN_THE_FUTURES of +2Days.
        */
        RELATIVE_DATE_IN_THE_FUTURE("+"), 
        
        /*
         * So, "before 1970" is a RELATIVE_ALL_DATES_BEFORE 
         * use ≤ instead of < to avoid tag parsing problems!
         */
        RELATIVE_ALL_DATES_BEFORE("≤"),
           
        /*
         * So, "after 1970" is a RELATIVE_ALL_DATES_AFTER 
         */
        RELATIVE_ALL_DATES_AFTER("≥"),
        
        /** these ones are best represented in two dates */
        
        /**
         * INTERVAL has two dates, meaning a period in time (start and end).
         * Note that centuries are represented as intervals, so they span the beginning year and ending year.
	*/    
        INTERVAL("~"),
        
        /**
         * Represents a date that happens peridically. The first one is the point, the second one the offset.
         * ex: Christmas - point = 25 December, offset: 1 year.
         */
        PERIODICAL_DATE(','),
        
        PERIODICAL_INTERVAL(';')        
    
        public final String text 
        
        TimeGroundingType(String text) {  this.text = text}
        
        static TimeGroundingType getFromValue(String text) {
            return values().find{it.text == text}
        }
    
        public String toStrong() {
            return text
        }
}