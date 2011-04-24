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

import renoir.bin.Renoir
import renoir.bin.RenoirCore
import renoir.obj.QueryTag
import renoir.obj.RenoirQuery
import renoir.obj.RenoirQueryParser
import saskia.bin.Configuration
import saskia.db.GeoSignature
import saskia.db.database.SaskiaDB
import saskia.db.obj.Collection
import saskia.db.obj.DocGeoSignature
import saskia.db.obj.RembrandtedDoc
import saskia.db.obj.User
import saskia.db.table.CollectionTable
import saskia.db.table.RembrandtedDocTable
import saskia.db.table.UserTable
import saskia.server.ServerMessage
import saskia.util.I18n

public class SearchMapping extends WebServiceRestletMapping {

	Renoir renoir
	Closure JSONanswer
	User user
	Configuration conf = Configuration.newInstance()
	I18n i18n
	SaskiaDB db

	static Logger mainlog = Logger.getLogger("RenoirServerMain")
	static Logger errorlog = Logger.getLogger("RenoirServerErrors")
	static Logger processlog = Logger.getLogger("RenoirServerProcessing")

	public SearchMapping(SaskiaDB db) {

		this.db=db
		CollectionTable collectionTable = db.getDBTable("saskia.db.table.CollectionTable")
		UserTable userTable = db.getDBTable("saskia.db.table.UserTable")
		RembrandtedDocTable rembrandtedDocTable = db.getDBTable("saskia.db.table.RembrandtedDocTable")

		JSONanswer = {req, par, bind ->

			long session = System.currentTimeMillis()
			processlog.debug "Session $session triggered with $par"

			i18n = I18n.newInstance()
			Collection collection

			/******************************/
			/* desanitation of input vars */
			/******************************/

			// GET
			String query = java.net.URLDecoder.decode(par["GET"]["q"])

			// POST
			String lang = par["POST"]["lg"]
			if (!lang) lang="pt"

			ServerMessage sm = new ServerMessage("SearchMapping", lang, bind, session, processlog)

			String api_key = par["POST"]["api_key"]
			if (!api_key) api_key = par["COOKIE"]["api_key"]
			// don't do it... let us allow no api-key searches -> redirect them to guest user
			//	if (!api_key) return sm.noAPIKeyMessage()

			int limit = (par["POST"]["l"] ? Integer.parseInt(par["POST"]["l"]) : 10)
			int offset = (par["POST"]["o"] ?  Integer.parseInt(par["POST"]["o"]) : 0)

			// collection checkup
			Long col_id
			try {col_id = Long.parseLong( par["POST"]["ci"])}
			catch(Exception e) {return sm.notEnoughVars("ci=$ci")}
			collection = collectionTable.getFromID(col_id)
			if (!collection) return sm.collectionNotFound()

			// advanced options
			List tags
			String qe, model
			boolean stem = true, maps = false, partialscores = false

			if (par["POST"]["t"]) tags = QueryTag.parseTags(par["POST"]["t"])
			if (par["POST"]["as_qe"]) qe = par["POST"]["as_qe"]
			if (!qe) qe = "no"
			if (par["POST"]["as_model"]) model = par["POST"]["as_model"]
			if (!model) model = "bm25"
			if (par["POST"]["stem"]) {
				try {stem = Boolean.parseBoolean(par["POST"]["as_stem"])}
				catch(Exception e) {}
			}
			if (par["POST"]["maps"]) {
				try {maps = Boolean.parseBoolean(par["POST"]["as_maps"])}
				catch(Exception e) {}
			}
			if (par["POST"]["partialscores"]) {
				try {partialscores = Boolean.parseBoolean(par["POST"]["as_partialscores"])}
				catch(Exception e) {}
			}
			/***********************/
			/* user authenticating */
			/***********************/

			User user
			if (api_key) {
				user = UserTable.getFromAPIKey(api_key)
				if (!user) returm sm.userNotFound()
			} else {
				user = UserTable.getFromLogin(User.guest)
			}

			if (!xollectionTable.canRead(user, collection) && !user.isSuperUser())
				return sm.statusMessage(-1, i18n.servermessage['user_not_allowed_to_read_from_collection'][lang])

			/*****************************/
			/* RENOIR initialization */
			/*****************************/

			try {
				renoir = RenoirCore.getCore(conf, collection, null, stem)
			} catch(Exception e) {
				errorlog.error "Can't generate RenoirCore: "+e.printStackTrace()
				return sm.statusMessage(-1, "Can't generate RenoirCore:"+e.getMessage())
			}

			List result = []
			Map answer

			/*****************************/
			/* query parsing & generation
			 /*****************************/

			RenoirQuery q = RenoirQueryParser.parse(query)

			// meter aqui o limit e offset - this overwrites the parameters
			q.paramsForRenoir["limit"] = limit
			q.paramsForRenoir["offset"] = offset
			q.paramsForRenoir["search"] = true
			// meter o qe, model, tags. Tags are already parsed
			q.paramsForRenoir["qe"] =  qe
			q.paramsForRenoir["explain"] = partialscores
			q.paramsForLGTE["model"] =  model

			/***********/
			/* SEARCH! */
			/***********/

			// o meu maps e feedback ficam aqui
			try {
				processlog.debug "$session User $user submitted search $q"
				answer = renoir.search(q)
			}catch(Exception e) {
				errorlog.error "Error performing a Renoir search: "+e.printStackTrace()
				return sm.statusMessage(-1, "Search went wrong: "+e.getMessage())
			}

			/******************/
			/* result parsing */
			/******************/

			bind["nr_results_shown"] = answer["nr_results_shown"]
			bind["nr_first_result"] = answer["nr_first_result"]
			bind["nr_last_result"] = answer["nr_last_result"]
			bind["total"] = answer["total"]

			// PREPARE EXPLANATION
			Map explanation = [:]
			if (tags) explanation["tags"] = QueryTag.toJSON(tags)
			explanation["query"] = q.toString()

			bind["explanation"] = explanation

			// creating snippets and geo info
			QueryParser qp = new QueryParser("contents", new StandardAnalyzer())
			LuceneQuery lquery = qp.parse(q.toVisibleTerms())
			QueryScorer scorer = new QueryScorer(lquery)
			Highlighter h = new Highlighter(scorer)

			answer["result"].each{r ->
				Map res
				// now let's convert doc_original_id from the collection to the text
				try {
					RembrandtedDoc rdoc = RembrandtedDocTable.getFromOriginalDocIDandCollection(r["doc_original_id"], collection)
					String body = rdoc.getPlainText(rdoc.getBodyFromContent())
					String title = rdoc.getPlainText(rdoc.getTitleFromContent())
					TokenStream ts = new StandardAnalyzer().tokenStream("text", new StringReader(body))

					res = [
								'i':r["i"],
								'title':title.replaceAll(/\n/,""),
								'doc_id':rdoc.doc_id,
								'doc_original_id':rdoc.doc_original_id,
								'abstract':h.getBestFragments(ts, body, 5,"(...)").replaceAll("\n"," "),
								'size':rdoc.getBodyFromContent().size(),
								'date':""+rdoc.doc_date_created,
								'score':r["score"]
							]
					if (r["partialscore"]) res['partial_score'] = r["partialscore"]

					List coordinates = [], polylines = []
					if (maps) {
						// get from table doc_geo_signature the stuff for this document
						DocGeoSignature dgs = rdoc.getGeographicSignature()
						if (dgs) {
							// get the dgs_signature. Note that is has only ancestors/centroid/bb info, no shape.
							GeoSignature geosig = new GeoSignature(dgs)
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
					return sm.serverMessage(-1, "RENOIR result parsing went wrong:"+ ex.getMessage())
				}
			}

			// leave this like that
			bind['status'] = 0
			bind['result'] = result
			sm.logProcessDebug("OK")
			return JSONHelper.toJSON(bind)
		}
	}

	private String parse(String string) {
		string = r2t.parse(string)
		if (string) string = string.substring(0,(string.size() > 300 ? 300 : string.size()))
		// \n are not valid json chars
		return string.replaceAll(/\n/,"")
	}
}
