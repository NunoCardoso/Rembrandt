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
import saskia.db.database.SaskiaMainDB

/**
 * @author Nuno Cardoso
 *
 */
class LoadSubjectFiles {

    static Logger log = Logger.getLogger("Patches")
    static void main(args) {
	 
	 Options o = new Options()
	        
	 o.addOption("file_en",true,"File with stuff above in English")
	 o.addOption("file_pt",true,"File with stuff above in PT")

	 CommandLineParser parser = new GnuParser()
	 CommandLine cmd = parser.parse(o, args)
	 
	 if (!cmd.hasOption("file_en")) {
	      log.fatal "No --file_en arg. Please specify file. Exiting."
	      System.exit(0)
	 }
	 if (!cmd.hasOption("file_pt")) {
	      log.fatal "No --file_pt arg. Please specify file. Exiting."
	      System.exit(0)
	 }
	 
	 Map status_ = [good:0, bad:0]
	    
	 List stuff_en = []
	 List stuff_pt = []
	 File cats_en = new File(cmd.getOptionValue("file_en"))
	 File cats_pt = new File(cmd.getOptionValue("file_pt"))
	 cats_en.eachLine{l -> stuff_en << l.trim()}
	 cats_pt.eachLine{l -> stuff_pt << l.trim()}
	 println "Got ${stuff_en.size()} EN entries, ${stuff_pt.size()} PT entries."
	 
	 SaskiaMainDB db = SaskiaMainDB.newInstance()
	 stuff_en.eachWithIndex{item_en, i -> 
	    String item_pt = stuff_pt[i]
	    Sentence s_en = Sentence.simpleTokenize(item_en)  
	    Sentence s_pt = Sentence.simpleTokenize(item_pt)  
	    String res_all

	    if (s_en) {
		res_all = "en:"
		s_en.each{t -> res_all += "["+t.text+"]" }
	    }
	    if (s_pt) {
		res_all += ";pt:"
		s_pt.each{t -> res_all += "["+t.text+"]" }
	    }
	    //println "Res: $res_all"
	    db.getDB().executeInsert("INSERT INTO subject VALUES(0, ?)",[res_all])
	 }	    
    }
}
