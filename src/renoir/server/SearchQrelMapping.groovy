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

import org.apache.log4j.Logger
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.Query as LuceneQuery
import org.apache.lucene.search.highlight.Highlighter
import org.apache.lucene.search.highlight.QueryScorer

import renoir.obj.*
import saskia.db.GeoSignature
import saskia.db.database.*
import saskia.db.obj.*
import saskia.db.table.*
import saskia.server.ServerMessage
import saskia.util.I18n

public class SearchQrelMapping extends WebServiceRestletMapping {

	Closure HTMLanswer
	Closure JSONanswer
	SaskiaDB db
	User user
	I18n i18n
	static Logger mainlog = Logger.getLogger("RenoirServerMain")
	static Logger errorlog = Logger.getLogger("RenoirServerErrors")
	static Logger processlog = Logger.getLogger("RenoirServerProcessing")

	public SearchQrelMapping(SaskiaDB db) {

		this.db = db
		CollectionTable collectionTable = db.getDBTable("CollectionTable")
		GeoscopeTable gt = db.getDBTable("GeoscopeTable")

		UserTable userTable = db.getDBTable("UserTable")
		DocTable docTable = db.getDBTable("DocTable")
		
		HTMLanswer = { req, par, bind ->
			return "Sorry, HTML mimetype is not handled. JSON only."
		}
		JSONanswer = { req, par, bind ->

			long session = System.currentTimeMillis()

			i18n = I18n.newInstance()
			Collection collection

			boolean maps = false
			int queryid 
			bind = [
				"status":-1,
				"message":"Unknown error"
			];
		
			try {
				queryid  = Integer.parseInt(par["GET"]["q"])
			} catch(Exception e) {
				bind["message"] = "no query id (q) parameter"
				return JSONHelper.toJSON(bind)
			}
			
			ServerMessage sm = new ServerMessage("SearchQrelMapping", "xx", bind, session, processlog)

			String api_key
			try {
				api_key = par["POST"]["api_key"]
			} catch (Exception e) {}
			if (!api_key) {
				try {
					api_key = par["COOKIE"]["api_key"]
				} catch (Exception e) {}
			}
			// don't do it... let us allow no api-key searches -> redirect them to guest user
			//	if (!api_key) return sm.noAPIKeyMessage()

			int limit = (par["POST"]["l"] ? Integer.parseInt(par["POST"]["l"]) : 20)
			int offset = (par["POST"]["o"] ?  Integer.parseInt(par["POST"]["o"]) : 0)

			if (par["POST"]["maps"]) {
				try {maps = Boolean.parseBoolean(par["POST"]["maps"])}
				catch(Exception e) {}
			}
			
			// collection checkup
			Long col_id
			String query

			try {
				db.getDB().eachRow("SELECT qcl_collection, que_query from query, query_collection "+
				"where que_qcollection=qcl_id and que_id=?", [queryid], {row -> 
					col_id = row["qcl_collection"]
					query = row["que_query"]
					query = query.replaceAll(/\Q[\E/,"").replaceAll(/\Q]\E/, " ").trim()
				})
			} catch(Exception e) {
				
			}
			
			try {
				collection = collectionTable.getFromID(col_id)
			} catch(Exception e) {
				return sm.collectionNotFound("Collection not found")
			}

			/***********************/
			/* user authenticating */
			/***********************/

			User user
			if (api_key) {
				user = userTable.getFromAPIKey(api_key)
				if (!user) return sm.userNotFound()
			} else {
				user = userTable.getFromLogin(User.guest)
			}

			if (!collectionTable.canRead(user, collection) && !user.isSuperUser())
				return sm.statusMessage(-1, i18n.servermessage['user_not_allowed_to_read_from_collection'][lang])

			def results = []
			List result = []
			db.getDB().eachRow("(SELECT SQL_CALC_FOUND_ROWS qrl_id,qrl_doc,qrl_qrel from qrel where qrl_query=? "+
			"order by qrl_qrel desc limit ${limit} offset ${offset} "+
			") UNION (SELECT CAST(FOUND_ROWS() as SIGNED INT),NULL, NULL)"
			, [queryid], {row -> 
				results << [
					"qrl_id": row["qrl_id"],
					"qrl_doc" : row["qrl_doc"],
					"qrl_qrel": row["qrl_qrel"]
				]
			})
			
			def x = results.pop()
			def total = x["qrl_id"]
			
			/******************/
			/* result parsing */
			/******************/

			bind["nr_results_shown"] = results.size()
			bind["nr_first_result"] = offset
			bind["nr_last_result"] = offset + results.size()
			bind["total"] = total

			results.eachWithIndex{r, i ->
				Map res
				// now let's convert doc_original_id from the collection to the text
				try {
					Doc doc = docTable.getFromID(r["qrl_doc"])
					String body = doc.getPlainText(doc.getBodyFromContent())
					String title = doc.getPlainText(doc.getTitleFromContent())
					TokenStream ts = new StandardAnalyzer().tokenStream("text", new StringReader(body))
					int qrel = r["qrl_qrel"]
					
					String truncate = (body.size() > 250 ? 
						body[0..250].replaceAll(/\n+/," ")+"(...)" : 
						body[0..(body.size())].replaceAll(/\n+/," ")
					)
					
					res = [
						'i':limit + i ,
						'title':title?.replaceAll(/\n/,""),
						'doc_id':doc.doc_id,
						'doc_original_id':doc.doc_original_id,
						'abstract':truncate,
						'size':doc.getBodyFromContent().size(),
						'date':""+doc.doc_date_created,
						'qrel':qrel
					]

					List coordinates = [], polylines = []
					if (maps) {
						// get from table doc_geo_signature the stuff for this document
						DocGeoSignature dgs = doc.getGeographicSignature()
						if (dgs) {
							// get the dgs_signature. Note that is has only ancestors/centroid/bb info, no shape.
							GeoSignature geosig = new GeoSignature(gt, dgs)
							// go to the Geoscope table and see if we can add shapes on it
							geosig.addPolylineInfo()

							/** gives a list of centroids. Each centroid is a Map, easily JSONable. */ 
							coordinates = geosig.places*.centroid
							res['coordinates'] = coordinates
							/** several polilynes, one for each entity in the document */
							polylines = geosig.places*.polyline
							res['polylines'] = polylines.findAll{it != null}
						}
					}
					result << res
				} catch(Exception ex) {
					errorlog.error "RENOIR result parsing went wrong: "+ex.printStackTrace()
					return sm.statusMessage(-1, "RENOIR result parsing went wrong:"+ ex.getMessage())
				}
			}

			// leave this like that
			bind['status'] = 0
			bind['result'] = result
			sm.logProcessDebug("OK")
		
			return JSONHelper.toJSON(bind)
		}
	}
}
