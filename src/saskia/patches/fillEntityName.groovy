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

import saskia.io.*
import org.apache.log4j.*
import org.apache.commons.cli.*
import saskia.dbpedia.DBpediaAPI
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 */
class FillEntityName {
  
    int number
    SaskiaDB db = SaskiaDB.newInstance()
	 DBpediaAPI dbpedia = DBpediaAPI.newInstance(Configuration.newInstance())

	public FillEntityName(int number) {
		this.number = number
	}
	
	public doit() {
		
		Map status = ["ok":0, "ko":0]
		String limit = ""
		if (number != 0) limit = "LIMIT $number" 
		String query = "SELECT * from entity where ent_name IS NULL and ent_dbpedia_resource IS NOT NULL ORDER BY ent_id $limit".toString()
		println query
		db.getDB().eachRow(query, [], {row -> 
		def res_pt = dbpedia.getLabelFromDBpediaResource(row['ent_dbpedia_resource'], 'pt')
		def res_en = dbpedia.getLabelFromDBpediaResource(row['ent_dbpedia_resource'], 'en')
		List res = []
		if (res_pt) res << "pt:"+res_pt
		if (res_en) res << "en:"+res_en
		try {
			int r = db.getDB().executeUpdate("UPDATE entity set ent_name=? where ent_id=?", [res.join(";"), row['ent_id']])
			status.ok += r
		} catch(Exception e) {
			println e.getMessage()
			status.ko++		
		}
		})
		return status
	}
    
	  
    static void main(args) {
        
        Options o = new Options()
		  int n 
        String fileseparator = System.getProperty("file.separator")
        
        o.addOption("n", true, "Number of those to tage (0 = all)")
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
        
        
        if (!cmd.hasOption("n")) {
            println "No --n arg. Setting to all."
            n = 0
        } else {
				try {
					n = Integer.parseInt(cmd.getOptionValue("n"))
				}catch(Exception e) {
					println "n musst be a number. Exiting."
					System.exit(0)
				}
			}		
        
			FillEntityName f = new FillEntityName(n)
			Map status = f.doit()
			println "Done. Status: "+status
 	 }
}

