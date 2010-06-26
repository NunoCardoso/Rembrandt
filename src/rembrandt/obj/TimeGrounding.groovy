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
import java.util.List;
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * Container for time grounding info
 *
 */
class TimeGrounding {

    TimeGroundingType datetype = TimeGroundingType.UNKNOWN
    
    static final String period = "P"
    static final String year = "Y"
    static final String month = "M"
    static final String day = "D"
    static final String hour = "h"
    static final String minute = "m"
    static final String second = "s"

    static final Pattern tg_period_pattern = ~/(?x) ${period} (-?\d+) (\w+) / 
    static final Pattern tg_time_pattern = ~/(?x)^  (?: (${year}) ([+\.\-]) _*? (\d*?) )? \
	 (?: (${month}) (\d*?) )? (?: (${day})  (\d*?) )? (?: (${hour}) (\d*?) )? (?: (${minute}) (\d*?) )?\
	 (?: (${second}) (\d*?) )? $/

    int era1 = 0 // positive: A.C. negative:D.C. 
    int year1 = -1
    int month1  = -1
    int day1 = -1
    int hour1 = -1
    int minute1 = -1
    int second1 = -1
    
    int era2 = 0   
    int year2 = -1
    int month2 = -1
    int day2 = -1
    int hour2 = -1
    int minute2 = -1
    int second2 = -1
     
    String period_type = null
    int period_amount = 0
    
    public boolean equals(TimeGrounding tg) {
        // comparar eras só quando há anos 
	return (datetype == tg.datetype && (year1 == -1 && tg.year1 == -1 ? true : era1 == tg.era1 ) && year1 == tg.year1 && 
    month1 == tg.month1 && day1 == tg.day1 && hour1 == tg.hour1 && minute1 == tg.minute1 && second1 == tg.second1 &&  
    (year2 == -1 && tg.year2 == -1 ? true : era2 == tg.era2 )  && year2 == tg.year2 && month2 == tg.month2 && day2 == tg.day2 
    && hour2 == tg.hour2 && minute2 == tg.minute2 && second2 == tg.second2 && period_amount == tg.period_amount) 
    }

    public String dump() {
        return "e1:${era1},year1:${year1},month1:${month1},day1:${day1},hour1:${hour1},minute1:${minute1},second1:${second1},"+
        "e2:${era2},year2:${year2},month2:${month2},day2:${day2},hour2:${hour2},minute2:${minute2},second2:${second2},"+
        "Ptype:${period_type},Pamount:${period_amount}"
    }
    // : is splitter
    // format is (x)(: (y)(z\d{2,4)+ )+
    // x = type is ! - + , ; ≤ ≥  ~ 
    // y = PYMDhms ONLY! Year is padded to 5, rest is padded to 2. Padding is '.'
    // P is the period, followed by the letter and a number.
    // Year has a 5 character size. First one is a + or - for era, if we're using an year. If not, forget it.
    // 
    public String toString() {
	return ""+datetype?.text + ":"+
        (year1 < 0 ? "" : year + (era1 == 1 ? "+" : (era1 == 0 ? "." : "-") )  
            + year1.toString().padLeft(4,'_') ) + 
	(month1 < 0 ? "" : month + month1.toString().padLeft(2,'0')) + 
 	(day1 < 0 ? "" : day + day1.toString().padLeft(2,'0')) + 
        (hour1 < 0 ? "" : hour + hour1.toString().padLeft(2,'0')) + 
        (minute1 < 0 ? "" : minute + minute1.toString().padLeft(2,'0')) + 
        (second1 < 0 ? "" : second + second1.toString().padLeft(2,'0')) +     
        
        ( (year2 > -1 || month2 > -1 || day2 > -1 ||  hour2 > -1 || minute2 > -1 || second2 > -1) ?
           ":" +  (year2 < 0 ? "" : year + (era2 == 1 ? "+" : (era2 == 0 ? "." : "-") ) + year2.toString().padLeft(4,'_') ) + 
                (month2 < 0 ? "" : month + month2.toString().padLeft(2,'0')) + 
                (day2 < 0 ? "" : day + day2.toString().padLeft(2,'0')) + 
                (hour2 < 0 ? "" : hour + hour2.toString().padLeft(2,'0')) + 
                (minute2 < 0 ? "" : minute + minute2.toString().padLeft(2,'0')) + 
                (second2 < 0 ? "" : second + second2.toString().padLeft(2,'0'))  
                :  "" ) + 
        (period_type ? ":" + period + period_amount + period_type : "")         
    }
    
    static TimeGrounding parseString(String tg_string) {
        
        if (!tg_string) return null
        int s = tg_string.size()
        TimeGrounding tg = new TimeGrounding()       
        boolean found = false      
        tg.datetype = TimeGroundingType.getFromValue(tg_string.charAt(0).toString())
        if (s <= 2) return tg // second character is a :      
        
        List tg_strings = tg_string.substring(2, s).split(/:/)

        // 0 gives the date type. optional 1 gives a date. 
        // optional 2 gives either a date or a period. optional 3 gives a period
        tg_strings.eachWithIndex{it, i -> 
            it.find(tg_period_pattern) {all, period_amount_, period_type_ -> 
                tg.period_type = period_type_ 
                tg.period_amount = Integer.parseInt(period_amount_)
            }
            
            it.find(tg_time_pattern) {all,  year_label_,  era_, year_, 
            month_label_, month_, day_label_, day_, hour_label_, hour_, minute_label_, minute_, second_label_, second_ ->  
              // println "tou! $year_label_ , $era_, $year_ , $month_label_ , $month_ , $day_label_ , $day_ "
              found = true
              
              if (year_label_) {
        	  if (i == 0) {tg.era1 = (era_ == "." ? 0 : (era_ == "+" ? 1 : -1)) ; tg.year1 = (year_ ? Integer.parseInt(year_) : -1 ) }
                  if (i == 1) {tg.era2 = (era_ == "." ? 0 : (era_ == "+" ? 1 : -1)) ; tg.year2 = (year_ ? Integer.parseInt(year_) : -1 ) }
              }
              if (month_label_) {
                 if (i == 0) {tg.month1 = (month_ ? Integer.parseInt(month_) : -1 ) }
                 if (i == 1) {tg.month2 = (month_ ? Integer.parseInt(month_) : -1 ) }
              }
              if (day_label_) {
                 if (i == 0) {tg.day1 = (day_ ? Integer.parseInt(day_) : -1 ) }
            	 if (i == 1) {tg.day2 = (day_ ? Integer.parseInt(day_) : -1 ) }
    	      }
    	      if (hour_label_) {
    		 if (i == 0) {tg.hour1 = (hour_ ? Integer.parseInt(hour_) : -1 ) }
    	     	 if (i == 1) {tg.hour2 = (hour_ ? Integer.parseInt(hour_) : -1 ) }
    	      }
              if (minute_label_) {
                 if (i == 0) {tg.minute1 = (minute_ ? Integer.parseInt(minute_) : -1 ) }
                 if (i == 1) {tg.minute2 = (minute_ ? Integer.parseInt(minute_) : -1 ) }
              }
              if (second_label_) {
                 if (i == 0) {tg.second1 = (second_ ? Integer.parseInt(second_) : -1 ) }
                 if (i == 1) {tg.second2 = (second_ ? Integer.parseInt(second_) : -1 ) }
              }
           }
        }
        assert tg_string == tg.toString()
        if (!found) println "Oops, couldn't understand TG info"
        return tg
    } 
    
    public List<String> getSimpleTimeIndex() {
	List<String> indexes = []
	
	if (datetype == TimeGroundingType.ABSOLUTE_DATE) {
	    String index = ""
    	    if (year1 > 0) index += year1.toString()
    	    if (month1 > 0 && year1 > 0) // add only the month to index if there's a year  
                index += month1.toString().padLeft(2,'0') 
            if (day1 > 0 && month1 > 1 && year1 > 0) // fill out months only if we have a grounded day and there is a selectedYear
                index += day1.toString().padLeft(2,'0') 
            if (index) {
        	index += "*"
        	indexes << index
            }
        }
	else if (datetype == TimeGroundingType.INTERVAL) {
         
            // entre 1920 e 1929
             if (year1 > 0 && year2 > 0 && (year2 - year1 == 9) && 
              year1.toString().size() == 4 & year2.toString().size() == 4) {
                 String decade = year1.toString().substring(0,3)
                 if (decade == year2.toString().substring(0,3)) 
                    indexes << decade+"*"
                 	
             } else  
             // entre 1901 e 2000
             if (year1 > 0 && year2 > 0 && (year2 - year1 == 99) && 
             year1.toString().size() == 4 & year2.toString().size() == 4 && 
             year1.toString().endsWith("01") && year2.toString().endsWith("00")) {
                     indexes <<  year1.toString().substring(0,2)+"*" // add 19
                     indexes << year2.toString()+"*" // add 20000
                 	
             }  else {
              // outro intervalo que não seja > a um século
                 if (year1 > 0 && year2 > 0 && (year2 - year1 < 99)) {
                     (year1)..(year2).each{
                         indexes << it.toString()+"*"
                     }
                 }
             }
        }
	return indexes
    }
}