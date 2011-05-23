
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
import saskia.gazetteers.*
import saskia.obj.*
import rembrandt.rules.* 

/**
 * @author Nuno Cardoso
 *
 */
class GetSubjectsFromSKOSfile {

    static Logger log = Logger.getLogger("Patches")
    PlaceDetector pl_en
    List subjects = []
    
    public GetSubjectsFromSKOSfile() {
	pl_en = new PlaceDetector("en")
    }
    
    public Map parse(String line) {
	//print "LINE: $line "
	Map status = [good:0, bad:0]
	Sentence s = Sentence.simpleTokenize(line.replaceAll(/_/," "))
	Expando result = pl_en.process(s)
	if (result?.subjectTerms) {
	    if (result.placeAdjectiveTerms || result.placeNameTerms) {
		String subject = result.subjectTerms.join(" ")
		if (subject.endsWith("s")) {
		    println "GOOD: $subject in $result"
		    status.good++
		    subjects << subject
		} else {
		    println "BAD: $result"
		    status.bad++
		}
	    }
	    // chech if it 
	    // merge it, see if it's a plural
	} else {
	    println "BAD: $result"
	    status.bad++
	}
	return status
	
    }
    
    static void main(args) {
	 
	 Options o = new Options()
	        
	 o.addOption("file",true,"File with stuff above")
	 CommandLineParser parser = new GnuParser()
	 CommandLine cmd = parser.parse(o, args)
	 
	 if (!cmd.hasOption("file")) {
	      log.fatal "No --file arg. Please specify file. Exiting."
	      System.exit(0)
	 }
	        
	 Map status_ = [good:0, bad:0]
	 File f = new File(cmd.getOptionValue("file"))
	 log.info "Reading file $f"
	 
	 GetSubjectsFromSKOSfile skos = new GetSubjectsFromSKOSfile()
	 f.eachLine{l -> 
	    List itens = l.split(/ /)
	    def m = itens[0] =~ /<http:\/\/dbpedia.org\/resource\/Category:(.*)>/
	    if (m.matches()) {
		Map status = skos.parse(m.group(1))
		status_.good += status.good
		status_.bad += status.bad
		
	    }
	 }
	 skos.subjects = skos.subjects.sort().unique()
	 skos.subjects.each{println it}
     }
}
