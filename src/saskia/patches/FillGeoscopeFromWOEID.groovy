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

import saskia.db.table.Geoscope;
import saskia.io.*
import org.apache.log4j.*
import org.apache.commons.cli.*

/**
 * @author Nuno Cardoso
 */
class FillGeoscopeFromWOEID {
  
    static void main(args) {
        
        Options o = new Options()
        String fileseparator = System.getProperty("file.separator")
        
        o.addOption("woeid", true, "woeid")
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        
        if (!cmd.hasOption("woeid")) {
            println "No --woeid arg. Please specify the woeid. Exiting."
            System.exit(0)
        }
   
        Long woeid = Long.parseLong(cmd.getOptionValue("woeid")	)	
        println "Fetching geoscope for woeid $woeid"
        
        Geoscope geo = Geoscope.getFromWOEID(woeid)
		 println "Do I already have? $geo"
			if (geo) {
				println "Yes. Skipping."
			} else {
		 		geo = Geoscope.fetchPlaceForWOEID(woeid) 
				println "Fetched. Now is $geo"
			}
		}
     
}
