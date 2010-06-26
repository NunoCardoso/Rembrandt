
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
package renoir.bin

import org.apache.log4j.Logger
import saskia.io.Collection
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 *
 */
class RenoirCore {
    
    static Logger log = Logger.getLogger("RenoirMain")
    static HashMap<Collection, Renoir> cores = [:]
    static String fileseparator = System.getProperty("file.separator")
                                                
    private static Renoir getCore(Configuration conf, Collection col, String indexdir = null, Boolean stem) {
		 // println "getCore: col=$col, indexdir=$indexdir, stem=$stem"

        if (!col) throw new IllegalStateException("Can't generate a Renoir core without a collection.")
        if (stem == null) throw new IllegalStateException("Can't generate a Renoir core without stemming info.")
        Renoir coreToReturn = this.cores?.'${col.col_id}'?.'${stem.toString()}'
        if (coreToReturn) {
             log.info "Recycling core $coreToReturn."       
        } else {		
            log.info "Creating core for collection $col"
            
         if (!indexdir) {
        		indexdir = conf.get("rembrandt.home.dir")
        		if (!indexdir) throw new IllegalStateException("no rembrandt.home.dir.. are you sure the configuration file ws loaded?") 
        		if (!indexdir.endsWith(fileseparator)) indexdir += fileseparator
        		indexdir += Renoir.mainIndexDir  
        		indexdir += fileseparator+Renoir.mainCollectionPrefix+"-"+col.col_id
         }
         coreToReturn = new Renoir(conf, indexdir, col.col_lang, stem)
				if (!this.cores.containsKey(col.col_id)) this.cores[col.col_id] = [:]
            this.cores[col.col_id][stem.toString()] = coreToReturn
        }
        return coreToReturn
    }
}
