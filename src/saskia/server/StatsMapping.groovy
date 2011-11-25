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
package saskia.server

import saskia.db.database.SaskiaDB
import saskia.db.obj.*
import saskia.db.table.*

import saskia.stats.SaskiaStats
import org.apache.log4j.Logger
import saskia.util.I18n

public class StatsMapping extends WebServiceRestletMapping {

    Closure HTMLanswer
    Closure JSONanswer
    I18n i18n
	SaskiaDB db
	SaskiaStats stats 
    static Logger mainlog = Logger.getLogger("SaskiaServerMain")  
    static Logger errorlog = Logger.getLogger("SaskiaServerErrors")  
    static Logger processlog = Logger.getLogger("SaskiaServerProcessing")  

	public StatsMapping(SaskiaDB db) {
	  this.db = db
	  stats = new SaskiaStats()
      i18n = I18n.newInstance()
	  CollectionTable collectionTable = db.getDBTable("CollectionTable")
	   
		HTMLanswer = { req, par, bind ->
	    	String lang = par["GET"]["lg"] 
	    	Long collection_id 
	    	try {
        			collection_id = Long.parseLong(par["GET"]["ci"])
          } catch(Exception e) {e.printStackTrace()}
          if (!collection_id) return sm.notEnoughVars("ci=$ci")                                  
			 collection = collectionTable.getFromID(collection_id)
          if (!collection) return sm.noCollectionFound()

			return stats.renderFrontPage(collection, lang)	
	   }
	}
}
