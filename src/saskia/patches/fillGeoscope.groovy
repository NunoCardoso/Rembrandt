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

import saskia.db.table.GeoscopeTable
import saskia.db.database.*
import saskia.io.*
import org.apache.log4j.*
import org.apache.commons.cli.*

/**
 * @author Nuno Cardoso
 */
class FillGeoscope {
  
    static void main(args) {
        
        Options o = new Options()
        String fileseparator = System.getProperty("file.separator")
        
        o.addOption("text", true, "text")
        o.addOption("lang", true, "lang")
        o.addOption("db", true, "db. SaskiaMain or SaskiaTest")
        o.addOption("help", false, "help")
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        
        if (!cmd.hasOption("text")) {
            println "No --text arg. Please specify the text needle. Exiting."
            System.exit(0)
        }
		
		if (!cmd.hasOption("db")) {
			println "No --db arg. Please specify the database. Exiting."
			System.exit(0)
		}
		
   		if (!cmd.hasOption("lang")) {
            println "No --lang arg. Please specify the language. Exiting."
            System.exit(0)
        }

        String text = cmd.getOptionValue("text")		
        String lang = cmd.getOptionValue("lang")	
        String db_ = cmd.getOptionValue("db")
		
		SaskiaDB db
		if (db_ == "SaskiaMain") db = SaskiaMainDB.newInstance()
		if (db_ == "SaskiaTest") db = SaskiaTestDB.newInstance()
		GeoscopeTable gt = new GeoscopeTable(db)
			
		println "Fetching geoscope for name $text, lang $lang"
        
//Geoscope fetchPlaceForWOEID(Long woeid) {
	
		List<Geoscope> geos = gt.getFromName(text, lang)
		if (geos) {
			println "Found geoscopes on DB: $geos"
			println "Exiting..."
			System.exit(0)
		}
		Geoscope geo = Geoscope.getNewGeoscopeForPlacename("text") 
		if (geo) {
			println "Fetched Geoscope, added to DB. IT's $geo"
		}
      }
}
