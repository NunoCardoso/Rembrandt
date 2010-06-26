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
package saskia.io

import saskia.bin.Configuration
import org.apache.log4j.Logger
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import rembrandt.obj.Document
import rembrandt.obj.TimeGrounding
import rembrandt.obj.TimeGroundingType
import rembrandt.obj.NamedEntity
/**
 * @author Nuno Cardoso
 * XML representation of a TimeSignature
 * 
 * <TimeSignature totalcount="32">
 * <Doc id="35235" original_id="NYT_2525" lang="pt">
 * <Time count="0"> 
 *     <NE id="4144">"7th May, 2007</NE> 
 *     <TimeGrounding>!:Y+2007M05D07</TimeGrounding>
 *     <Index>20070507</Index>
 *  </Time>
 * </TimeSignature>
 *
 */
class TimeSignatureFactory {
    
    // for the tag
    static Logger log = Logger.getLogger("TimeSignature")
    static String timeSignatureVersionNumber = "1.0"
    static String timeSignatureVersionLabel="TimeSignature v"+timeSignatureVersionNumber
    static Configuration conf = Configuration.newInstance()
    RembrandtReader reader 
    Date resetDate
    
    public TimeSignatureFactory(Date rDate) {
        resetDate = rDate
    }

    String generate(doc_id, Map stuff) {
        
        // stuff has lang, title and body
        String lang = stuff.lang
        Date date = (Date)stuff.date
        reader = new RembrandtReader(new RembrandtStyleTag(
          conf.get("rembrandt.input.styletag.lang", lang)))
        
        // stuff can come with the doc, a Document already parsed, of the raw title and body
        Document doc = stuff.doc
       // println "stuff: $stuff doc.body_sentences = ${doc.body_sentences}"
        if (!doc) {
            doc = reader.createDocument(stuff.content)
            doc.preprocess()
        }

        log.debug "Got doc with ${doc.body_sentences.size()} body sentences and ${doc.bodyNEs.size()} body NEs"
        // let's get the time grounding for the  
	LinkedHashMap processedNEs = [:] // ent_id,  list with [Geoscope geo, List<Geoscope> ancestors, int count]
	                                 // LinkedHashMap so that we can sort it. 
        
        Map countYears = [:]
        Map countMonths = [:]
        
        int totalcount = 0
        // first round to count grounded years and months    
        
//        doc.bodyNEs.each{ne -> 
//            log.trace "Got ne $ne tg ${ne.tg}"
//        }
        doc.bodyNEs.findAll{it.tg != null}.each{ne -> 
            
            TimeGrounding tg = ne.tg
            log.debug "Ne ${ne.printTerms()} has tg $tg"
            totalcount++
            if (tg.datetype == TimeGroundingType.ABSOLUTE_DATE) {
        	
          //  println "ne $ne  tg: ${ne.tg}  ne.tg.year1  ${ne.tg.year1} ne.tg.month1 ${ne.tg.month1}"
    	   if (ne.tg.year1 > 0) {
    	       if (countYears.containsKey(ne.tg.year1)) countYears[ne.tg.year1]++ else countYears[ne.tg.year1] = 1
               }
    	   	if (ne.tg.month1 > 0) {
    	   	    if (countMonths.containsKey(ne.tg.month1)) countMonths[ne.tg.month1]++ else countMonths[ne.tg.month1] = 1
                }
            } 
            if (tg.datetype == TimeGroundingType.INTERVAL) {
        	// entre 1920 e 1929
                if (tg.year1 > 0 && tg.year2 > 0 && (tg.year2 - tg.year1 == 9)) {
                    (tg.year1)..(tg.year2).each{
                        if (countYears.containsKey(ne.tg.year1)) countYears[ne.tg.year1]++ else countYears[ne.tg.year1] = 1
                    }
                } else 
                // século XX - 1901 a 2000, outros intevalos não maiores que isso 
                if (tg.year1 > 0 && tg.year2 > 0 && (tg.year2 - tg.year1 <= 99)) {
                    (tg.year1)..(tg.year2).each{
                        if (countYears.containsKey(ne.tg.year1)) countYears[ne.tg.year1]++ else countYears[ne.tg.year1] = 1
                    }
                
                }
            } 
	}
    
	log.debug "found $totalcount elegible TIME NEs"
    
	int selectedYear
	int yearfreq = 0
	int selectedMonth
	int monthfreq = 0
    
	countYears.each{year,freq -> 
          //"Got year $year, freq $freq"
          if (yearfreq < freq) {
            yearfreq = freq
            selectedYear = year
          }
        }
	countMonths.each{month,freq -> 
          //"Got month $month, freq $freq"
                    if (monthfreq < freq) {
                monthfreq = freq
                selectedMonth = month
            }
	}
    
 	log.debug "Most frequent Year: $selectedYear, most frequent month: $selectedMonth"
	// second round to count grounded years and months       
	doc.bodyNEs.findAll{it.tg != null}.each{ne ->
           List index_list = []
           TimeGrounding tg = ne.tg
           if (tg.datetype == TimeGroundingType.ABSOLUTE_DATE && 
        	   // it has at leat a year, month or day in it
             (tg.year1 > 0 || tg.month1 > 0 || tg.day1 > 0) ) {
               String index = ""
               if (tg.year1 > 0) 
                   index += ne.tg.year1.toString() 
               else if (selectedYear) index += selectedYear
            
               if (tg.month1 > 0 && tg.year1 > 0) // add only the month to index if there's a year  
                     index += ne.tg.month1.toString().padLeft(2,'0') 
               else if (tg.day1 > 0 && selectedMonth && selectedYear) // fill out months only if we have a grounded day and there is a selectedYear
                   index += selectedMonth.toString().padLeft(2,'0')
            
               if (tg.day1 > 0) index += tg.day1.toString().padLeft(2,'0')
               if (index) index_list << index   
           }
        
           if (tg.datetype == TimeGroundingType.INTERVAL) {
            
               // entre 1920 e 1929
                if (tg.year1 > 0 && tg.year2 > 0 && (tg.year2 - tg.year1 == 9) && 
                 tg.year1.toString().size() == 4 & tg.year2.toString().size() == 4) {
                    String decade = tg.year1.toString().substring(0,3)
                    if (decade == tg.year2.toString().substring(0,3)) {
                        index_list << decade
                    }	
                } else   
                // entre 1901 e 2000
                if (tg.year1 > 0 && tg.year2 > 0 && (tg.year2 - tg.year1 == 99) && 
                tg.year1.toString().size() == 4 & tg.year2.toString().size() == 4 && 
                tg.year1.toString().endsWith("01") && tg.year2.toString().endsWith("00")) {
                    	index_list <<  tg.year1.toString().substring(0,2) // add 19
                        index_list << tg.year2.toString() // add 20000
                    	
                }  else {
                 // outro intervalo que não seja > a um século
                    if (tg.year1 > 0 && tg.year2 > 0 && (tg.year2 - tg.year1 < 99)) {
                        (tg.year1)..(tg.year2).each{
                            index_list << it.toString()
                        }
                    }
                }
           } 
         
           String key = ne.printTerms()+"-"+index_list.join(":") // give a unique key with NE terms and time index
           int count = 0
           if (processedNEs.containsKey(key)) count = processedNEs[key].count
        
           processedNEs[key]= [count:++count, index:index_list, name:ne.printTerms(), tg:tg] // para que o size seja pelo menos 0          
        }
	log.debug "Time Indexes: "+processedNEs.values().toList().collect{it.index}
    
    
        // now that we have Entity, Geoscope and ancestor's Geoscopes, let's update the XML factory 
        StringBuffer xml = new StringBuffer()
        // bigger counts in from as first criteria, biggest number of ancestors (~ more fine-grained) as second criteria
        
        //println "processedNEs before sort: $processedNEs" 
        processedNEs=processedNEs.sort({a, b -> b.value.count <=> a.value.count })
        //println "processedNEs after sort: $processedNEs" 

        int totalCount =  processedNEs.collect{it.value.count}.sum()
        if (totalCount == null) totalCount = 0
                
        xml.append "<TimeSignature version=\"${timeSignatureVersionNumber}\" totalcount=\"${totalCount}\">\n"
        xml.append "  <Doc id=\"${doc_id}\" original_id=\"${stuff.original_id}\" lang=\"${lang}\" />\n"
        if (date) xml.append "  <DocDateCreated>${String.format('%tY%<tm%<td', date)}</DocDateCreated>\n"
        processedNEs.each{key, it -> 
            if (it.index) { // only times with index are worth mentioning
                xml.append "  <Time count=\"${it.count}\">\n"
                xml.append "     <NE>${it.name}</NE>\n"
                xml.append "     <TimeGrounding>${it.tg.toString()}</TimeGrounding>\n"
                it.index.each{i -> xml.append "     <Index>$i</Index>\n"}
                xml.append "  </Time>\n"
            }
        }             
        xml.append "</TimeSignature>"
        
        return xml.toString()
                            
    } // method generate 
}// class