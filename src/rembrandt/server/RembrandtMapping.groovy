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
package rembrandt.server

import org.apache.log4j.*

import rembrandt.bin.*
import rembrandt.io.*
import rembrandt.obj.Document
import saskia.bin.Configuration
import saskia.db.database.SaskiaDB
import saskia.db.obj.User
import saskia.db.table.UserTable
import saskia.server.ServerMessage
import saskia.util.I18n

public class RembrandtMapping extends WebServiceRestletMapping {

	/**
	 * nota: um dos problemas que gera infinite lopps é se um dos log.info chama variáveis que não existem.
	 * A closure tenta resolvê-las fora dela , e isso despoleta uma cadeia de requests a evitar.
	 */
	Closure JSONanswer
	Configuration conf
	SaskiaDB db
	List cores
	I18n i18n
	static Logger mainlog = Logger.getLogger("RembrandtServerMain")
	static Logger errorlog = Logger.getLogger("RembrandtServerErrors")
	static Logger processlog = Logger.getLogger("RembrandtServerProcessing")

	public RembrandtMapping(Configuration conf, SaskiaDB db) {

		this.conf=conf
		this.db=db
		List cores = []

		JSONanswer = {req, par, bind ->

			long session = System.currentTimeMillis()
			RembrandtCore core

			String lang, submissionlang, rules, doctitle, docbody, api_key, format

			/******************************/
			/* desanitation of input vars */
			/******************************/

			// POST
			if (par["POST"]["lg"]) lang = par["POST"]["lg"]
			if (!lang) lang = "pt"
			if (par["POST"]["slg"]) submissionlang = par["POST"]["slg"]
			if (!submissionlang) submissionlang="pt"
			rules = "harem"
			if (par["POST"]["dt"]) doctitle = par["POST"]["dt"]
			if (par["POST"]["db"]) docbody = par["POST"]["db"]
			if (par["POST"]["api_key"]) api_key = par["POST"]["api_key"]
			if (!api_key) api_key = par["COOKIE"]["api_key"]

			if (par["POST"]["f"]) format = par["POST"]["f"] else format = "rembrandt"

			ServerMessage sm = new ServerMessage("RembrandtMapping", lang, bind, session, processlog)

			// verification of api_key
			if (!api_key) return sm.noAPIKeyMessage()

			// verification of user
			User user = UserTable.getFromAPIKey(db, api_key)
			if (!user) return sm.userNotFound()
			if (! user.canExecuteAPICall()) return sm.dailyAPILimitExceeded()

			/****** LOAD CORE *****/
			try {
				core = Rembrandt.getCore(lang, rules)
			} catch(Exception e) {
				errorlog.error (session ? session+" ": "")+(signature ? signature+": ":"")+
						"Can't load Rembrandt core: "+e.printStackTrace()
				return sm.statusMessage(-1, "Can't load Rembrandt core", e.getMessage())
			}
			processlog.debug "Got Rembrandt core: $core"

			processlog.info "$session Got a request to tag the following doc:\n===========\nlang=${lang} slang=${submissionlang}\ndt=${doctitle}\ndb=${docbody}\n==========\n"

			Document doc= new Document(title:doctitle, body:docbody)

			doc = core.releaseRembrandtOnDocument(doc)

			Writer rw
			Map res = [:]
			if (format == "rembrandt") {
				rw = new RembrandtWriter(new RembrandtStyleTag(lang))
				if (doc.title || doc.body) res["document"] = [:]
				if (doc.title) res["document"]["title"] = rw.printDocumentHeadContent(doc)?.replaceAll(/\n/, " ")
				if (doc.body) res["document"]["body"] = rw.printDocumentBodyContent(doc)?.replaceAll(/\n/, " ")
				processlog.info "$session Returning tagged doc:\n==========\ndt=${res['document']['title']}\ndb=${res['document']['body']}\n==============\n"
				// formato dsbatista
			} else if (format == "dsb") {
				rw = new UnformattedWriter(new JustCategoryStyleTag(lang))
				if (doc.title || doc.body) res["document"] = [:]
				if (doc.title) res["document"]["title"] = rw.printDocumentHeadContent(doc)?.replaceAll(/\n/, " ")
				if (doc.body) res["document"]["body"] = rw.printDocumentBodyContent(doc)?.replaceAll(/\n/, " ")
				processlog.info "$session Returning tagged doc:\n==========\ndt=${res['document']['title']}\ndb=${res['document']['body']}\n==============\n"
			} else if (format == "json") {
				rw = new RembrandtWriter(new NoStyleTag(lang))
				res["doc"] = [
					"doc_content": [
						"title" : rw.printDocumentHeadContent(doc)?.replaceAll(/\n/, " "),
						"body" : rw.printDocumentBodyContent(doc)?.replaceAll(/\n/, " ")
					], 
					
				]
				res["nes"] = []
				
				doc.titleNEs?.each{ne -> 
					ne?.classification?.each{c -> 
						def ne_obj = [:]
						ne_obj["section"] = "T"
						ne_obj["term"] = ne.termIndex
						ne_obj["sentence"] = ne.sentenceIndex
						ne_obj["ne"] = [:]
						ne_obj["ne"]["ne_name"] = [
							"nen_name" : ne.printTerms(),
							"nen_nr_terms" : ne.terms.size()
						]
						if (c.c) ne_obj["ne"]["ne_category"] = [
							"nec_category": c.c
						] 
						if (c.t) ne_obj["ne"]["ne_type"] = [
							"net_type": c.t
						]
						if (c.s) ne_obj["ne"]["ne_subtype"] = [
							"nes_subtype": c.s
						]
						res["nes"] << ne_obj
					}
				}
				 
				doc.bodyNEs?.each{ne -> 
					ne?.classification?.each{c -> 
						def ne_obj = [:]
						ne_obj["section"] = "B"
						ne_obj["term"] = ne.termIndex
						ne_obj["sentence"] = ne.sentenceIndex
						ne_obj["ne"] = [:]
						ne_obj["ne"]["ne_name"] = [
							"nen_name" : ne.printTerms(),
							"nen_nr_terms" : ne.terms.size()
						]
						if (c.c) ne_obj["ne"]["ne_category"] = [
							"nec_category": c.c
						] 
						if (c.t) ne_obj["ne"]["ne_type"] = [
							"net_type": c.t
						]
						if (c.s) ne_obj["ne"]["ne_subtype"] = [
							"nes_subtype": c.s
						]
						res["nes"] << ne_obj
					}
				}
				processlog.info "$session Returning tagged doc:\n==========\ndt=${res['doc']['doc_content']['title']}\ndb=${res['doc']['doc_content']['body']}\n==============\n"
				
			}


			int calls = user.addAPIcount()
			processlog.info "$session User $user made a Rembrandt tagging request, now has $calls API daily calls\n"
			return sm.statusMessage(0,res)
		}
	}
}
