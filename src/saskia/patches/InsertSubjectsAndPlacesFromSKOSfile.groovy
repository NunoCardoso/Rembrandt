
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
package saskia.patches

import org.apache.log4j.Logger

import org.apache.commons.cli.*
import rembrandt.obj.* 
import saskia.db.table.*
import saskia.db.obj.*
import saskia.gazetteers.*
import saskia.obj.*
import saskia.db.database.SaskiaDB

import rembrandt.rules.SubjectAndPlaceDetector

/**
 * @author Nuno Cardoso
 *
 */
class InsertSubjectsAndPlacesFromSKOSfile {

    static Logger log = Logger.getLogger("Patches")
    SubjectAndPlaceDetector detector
    List subjects = []
    SaskiaDB db = SaskiaDB.newInstance()
    String ynae
    
    public InsertSubjectsAndPlacesFromSKOSfile() {
	detector = new SubjectAndPlaceDetector()
    }
    
    public Map parse(String line) {
	//print "LINE: $line "
	Map status = [good:0, bad:0]

	Sentence s = Sentence.simpleTokenize(line.replaceAll(/_/," "))
	Expando result = detector.process(s, "en")
		
	if (result?.subjectTerms && (
	  !s.containsTermText("stubs") && !s.containsTermText("Articles") && 
	  !s.containsTermText("articles") && !s.containsTermText("lists") &
	  !s.containsTermText("templates") && !s.containsTermText("infoboxes") && 
	  !s.containsTermText("navbox") && !s.containsTermText("boxes") &&
	  !(s.containsTermText("task") && s.containsTermText("force")) 
	)){
	    
	    println "FOUND in string $s"
	    SubjectGround sg = new SubjectGround()
	    
	    // subject 
	    def subjectID = result.subjectMatch?.answer
		  
	    //println "subjectID: $subjectID"
	    sg.sgr_subject = Subject.getFromID(subjectID)
	    
	    // geoscope
	    String dbpediaresource		    
	    // there is an associated place
	    if (result.placeAdjectiveTerms) {
		dbpediaresource = result.placeAdjectiveMatch.answer
	    }
	    if (result.placeNameTerms) {
		dbpediaresource = result.placeNameMatch.answer
	    } 	    
	    EntityTable e = EntityTable.getFromDBpediaResource(dbpediaresource)
	    Geoscope geo = e?.hasGeoscope()
	    sg.sgr_geoscope = geo
	    
	    // other stuff
	    sg.sgr_dbpedia_resource = "Category:"+line   
	    sg.sgr_dbpedia_class = null
	    sg.sgr_wikipedia_category = "en:"+line  
	    sg.sgr_comment = "From EN Wiki Cat"
				    
	    println "Preparing to insert SubjectGround $sg"
	    
	    List<SubjectGround> sgrs = SubjectGround.getFromSubjectIDAndGeoscopeID(subjectID, geo?.geo_id)
	    println "going for the question."
	    boolean writeit = true
	    if (sgrs) {
		if ((!ynae) || (ynae == "y") || (ynae == "n")) {
	             ynae = null
	             BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
	             while (!(ynae == "a" || ynae == "e" || ynae == "n" || ynae == "y")) {
	                println "There are SubjectGrounds: $sgrs. Overwrite it? ([y]es, [a]lways, [n]o, n[e]ver)"
	                    ynae = input.readLine().trim()
	                }
	            }
	            if ( (ynae == "n") || (ynae == "e")) {
	                log.warn "Skipping it."  
	                writeit = false
	                
	            } else {
	        	writeit = true
	            }
	    }
	    
	    if (writeit) sg.addThisToDB()	
	  
	} else {
	    println "NOT FOUND on line $line"
	    status.bad++
	}
	return status
	
    }
    
    static void main(args) {
	 
	 Options o = new Options()
	       
	 o.addOption("test",true,"tetx with this line")
	 o.addOption("file",true,"File with stuff above")
	 CommandLineParser parser = new GnuParser()
	 CommandLine cmd = parser.parse(o, args)
	 
	 if (!(cmd.hasOption("file") || cmd.hasOption("test")) )  {
	      log.fatal "No --file or --test arg. Please specify file or test. Exiting."
	      System.exit(0)
	 }
	 
	 InsertSubjectsAndPlacesFromSKOSfile skos = new InsertSubjectsAndPlacesFromSKOSfile()      
	 Map status_ = [good:0, bad:0]
	    
	 if (cmd.hasOption("file")) {           
	     File f = new File(cmd.getOptionValue("file"))
	     log.info "Reading file $f"
 
	     f.eachLine{l -> 
	     	List itens = l.split(/ /)
	     	def m = itens[0] =~ /<http:\/\/dbpedia.org\/resource\/Category:(.*)>/
	     	if (m.matches()) {
	     	    Map status = skos.parse(m.group(1))
	     	    status_.good += status.good
	     	    status_.bad += status.bad
    		
	     	}
    	     }
	 }
	 if (cmd.hasOption("test")) {     
	     Map status = skos.parse(cmd.getOptionValue("test"))
	     status_.good += status.good
	     status_.bad += status.bad
	 }
	 println "Done. $status_"
	 
     }
}
