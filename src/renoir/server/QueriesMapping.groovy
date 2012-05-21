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
package renoir.server

import java.net.URLDecoder

import org.apache.log4j.*

import saskia.db.database.SaskiaDB

public class QueriesMapping extends WebServiceRestletMapping {

	Closure HTMLanswer
	Closure JSONanswer
	SaskiaDB db
	static Logger log2 = Logger.getLogger("RenoirServer")

	public QueriesMapping(SaskiaDB db) {

		this.db = db

		HTMLanswer = { req, par, bind ->
			return "Sorry, HTML mimetype is not handled. JSON only."
		}
		JSONanswer = { req, par, bind ->

			int qc
			bind = [
				"status":-1,
				"message":"Unknown error"
			];
			
			try {
				qc  = Integer.parseInt(par["GET"]["q"])
			} catch(Exception e) {
				bind["message"] = "no query collection (q) parameter"
				return JSONHelper.toJSON(bind)
			}

			def collections = []
		
			def success = false
				
			db.getDB().eachRow("SELECT * from query where que_qcollection=?", [qc], {row -> 
				success = true
				collections << [
					"que_id": row["que_id"],
					"que_original_id" : row["que_original_id"],
					"que_query": row["que_query"]
				]
			})
		
			if (success) {
				bind["status"] = 0
				bind["message"] = collections
			}
		
			return JSONHelper.toJSON(bind)
		}
	}
}
