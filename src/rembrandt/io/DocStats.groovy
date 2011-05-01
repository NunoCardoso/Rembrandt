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

package rembrandt.io

import org.apache.log4j.*
import java.lang.management.*

/**
 * @author Nuno Cardoso
 * This class computes partial and total times for document processes.
 * It also estimates a finish time.
 */
class DocStats {
    
    def parsedDocs, totalDocs
    def timeElapsed, remainingTime, avgTime
    def first, partialFirst, partialLast, last
    Logger log = Logger.getLogger("DocStats")
    MemoryMXBean mem 
    
    /**
     * Main constructor.
     * @param numberDocs number of documents to be processed. 0 by default.
     */
    public DocStats(numberDocs = 0) {
        totalDocs = numberDocs     
    }
    
    /**
     * Initializes the counters.
     */
    public void begin() {
        first = System.currentTimeMillis()
        parsedDocs = 0
        timeElapsed = 0
        remainingTime = 0
        avgTime = 0	
		log.info "---------------------"
		log.info "Task beginning: Annotate ${totalDocs} docs. Starting at ${new Date(first)}"
		log.info "---------------------"
    }
    
    /**
     * Terminates the counters, prints final stats.
     */
    public void end() {
        last = (System.currentTimeMillis() - first)
        def printLast = getTimeValue(last)
        def lastUnits = getTimeUnits(last)
		log.info "---------------------"
        log.info sprintf ("Task ending: Annotate %d docs. Done in %.1f%s.", totalDocs, printLast, lastUnits)
 		log.info "---------------------"
   }
    
    /**
     * Println memory usage, using the java.lang.management.* stuff.
     */
    public void printMemUsage() {
        mem = ManagementFactory.getMemoryMXBean()
        MemoryUsage heap =  mem.getHeapMemoryUsage()
        MemoryUsage nonheap =  mem.getNonHeapMemoryUsage()
        log.info "Heap Memory: init = "+((int) ((float)heap.getInit()/1024/1024))+"M " + 
                "used = "+((int) ((float)heap.getUsed()/1024/1024))+"M " + 
                "committed = "+((int) ((float)heap.getCommitted()/1024/1024))+"M " + 
                "max = "+((int) ((float)heap.getMax()/1024/1024))+"M "
        log.info "Non-Heap Memory: init = "+((int) ((float)nonheap.getInit()/1024/1024))+"M " + 
                "used = "+((int) ((float)nonheap.getUsed()/1024/1024))+"M " + 
                "committed = "+((int) ((float)nonheap.getCommitted()/1024/1024))+"M " + 
                "max = "+((int) ((float)nonheap.getMax()/1024/1024))+"M "
        log.info "---------------------" 
    }
    
    /** 
     * initializes partial counters.
     */
    public void beginDoc(docid = null) {
        partialFirst = System.currentTimeMillis()
		log.info "---------------------"
        log.info "Task status: annotating doc #${parsedDocs+1} of ${totalDocs} with "+(docid != null ? "id "+docid : "unknown id") 		  
		log.info "---------------------"
    }
    
    /** 
     * initializes partial counters.
     */
    public void beginBatchOfDocs(int howmany) {
        partialFirst = System.currentTimeMillis()
 		log.info "---------------------"
        log.info "Task status: annotating a batch of $howmany docs"
 		log.info "---------------------"
   }
    
    public void endBatchOfDocs(int howmany) {
        endDoc(howmany)
    } 
    
    /**
     * stops partial counters. Computes partial times.
     */
    public void endDoc(int howmany = 1) {
        parsedDocs += howmany
        partialLast = System.currentTimeMillis()
        
        def elapsed = (Float)(partialLast-partialFirst)
        timeElapsed += elapsed	
        remainingTime = ((Float)(totalDocs - parsedDocs))/((Float)parsedDocs)*timeElapsed
        avgTime = timeElapsed/parsedDocs
        
        def printElapsed = DocStats.getTimeValue(elapsed)
        def elapsedUnits = DocStats.getTimeUnits(elapsed)
        
        def printTimeElapsed = DocStats.getTimeValue(timeElapsed)
        def timeElapsedUnits = DocStats.getTimeUnits(timeElapsed)
        
        def printRemainingTime = DocStats.getTimeValue(remainingTime)   
        def timeRemainingUnits = DocStats.getTimeUnits(remainingTime)
        
        def printAvgTime = DocStats.getTimeValue(avgTime)
        def timeAvgUnits = DocStats.getTimeUnits(avgTime)
		log.info "---------------------"
        log.info sprintf ("Time: %.1f%s. Total: %.1f%s. Avg: %.1f%s/doc. ETF: %.1f%s (%s).", 
                printElapsed, elapsedUnits, printTimeElapsed, timeElapsedUnits, printAvgTime, timeAvgUnits,
                printRemainingTime, timeRemainingUnits, new Date((long)(first+remainingTime)).toGMTString())
    }
    
    /**
     * Get the human-readable time value
     * @param item time in milliseconds
     */
    public static getTimeValue(item) {
        return (item/1000.0 < 60.0 ? item/1000.0 : 
        (item/1000.0/60.0 < 60 ?  item/1000.0/60.0 : 
                (item/1000.0/60.0/60.0 < 24 ?  item/1000.0/60.0/60.0 : 
                        item/1000.0/60.0/60.0/24.0)))
    }
    
    /**
     * Get the proper time unit for the timespan given.
     * @param item time in milliseconds
     */
    public static getTimeUnits(item) {
        return (item/1000.0 < 60 ? "s" : 
        (item/1000.0/60.0 < 60 ?  "m" : 
                (item/1000.0/60.0/60.0 < 24 ?  "h" : "d")))
    }	  
}