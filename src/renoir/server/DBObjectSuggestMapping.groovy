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
import saskia.db.obj.*
import saskia.db.table.*

/** This is for admin, where some SASKIA objects are queried by titles, and we want to get an id **/
public class DBObjectSuggestMapping extends WebServiceRestletMapping {

	Closure JSONanswer
	SaskiaDB db
	static Logger processlog = Logger.getLogger("RenoirServerProcessing")

	public DBObjectSuggestMapping(SaskiaDB db) {

		this.db = db
		CollectionTable collectionTable = db.getDBTable("CollectionTable")
		UserTable userTable = db.getDBTable("UserTable")
		TagTable tagTable = db.getDBTable("TagTable")
		TypeTable typeTable = db.getDBTable("TypeTable")
		NECategoryTable neCategoryTable = db.getDBTable("NECategoryTable")
		NETypeTable neTypeTable = db.getDBTable("NETypeTable")
		NESubtypeTable neSubtypeTable = db.getDBTable("NESubtypeTable")

		//	  JSON response
		JSONanswer = { req, par, bind ->

			def q = URLDecoder.decode(par["GET"]["q"], "UTF-8")
			def type
			def lang

			if (par["GET"]["t"])  type = par["GET"]["t"]
			if (par["GET"]["lg"]) lang = par["GET"]["lg"]


			List answer = []

			if (q && type) {

				/* from type is: 
				 * COLLECTION: col_owner(user)
				 * RDOC: doc_collection(colletion), *doc_tag*(tag), *doc_type*(type), 
				 * SDOC: sdoc_collection(colletion) 
				 * NE: ne_name(nename), ne_category(necategory), ne_type(type), ne_subtype(subtype), ne_entity(entity)
				 * ENTITY: *ent_geoscope*(geoscope)
				 * GEOSCOPE: *geo_entity*(entity), 
				 * SUBJECTGROUND: sgr_subject(subject), sgr_geoscope(geoscope),
				 * TASK: tsk_owner(user), tsk_collection(collection) 
				 */

				/* to type is:
				 * collection (doc_collection, sdoc_collection, tsk_collection) -> CACHE
				 * user (col_owner, tsk_owner) -> CACHE
				 * tag (dtg_tag) -> CACHE
				 * type (dht_type) -> CACHE
				 * nename (ne_name) -> DB
				 * necategory, netype, nesubtype -> CACHE
				 * entity (ne_entity, ehg_entity) -> DB
				 * geocope (ehg_geoscope, sgr_geoscope) -> DB
				 * subject (sgr_subject) -> CACHE			 
				 */


				switch(type) {

					/*** COLLECTION ****/
					case [
						"doc_collection",
						"sdoc_collection",
						"tsk_collection"
					]:
					// needle
						String newtype = "col_name"
					// type will work as column filter, needle will be the query
						List<Collection> res = collectionTable.filterFromColumnAndNeedle(
								collectionTable.getAllCollections().values().toList(), newtype, q)
						res.each{it -> answer << [it.col_id, it.col_name]}
						break

					/*** USER ***/
					case ["col_owner", "tsk_owner"]:
					// needle
						String newtype = "usr_login"
						Map res = userTable.listUsersForAdminUser(0,0,newtype, q)
						res['result'].each{it -> answer << [it.usr_id, it.usr_login]}
						break

					/*** TAG ***/
					case "dtg_tag":
						if (!tagTable.cache) tagTable.refreshCache()
						List<Tag> res = tagTable.cache.values().toList().findAll{it.tag_version =~ /(?i)${q}/}
						res.each{it -> answer << [it.tag_id, it.tag_version]}
						break

					/*** TYPE ***/
					case "dht_type":
						if (!typeTable.cache) typeTable.refreshCache()
						List<Type> res = typeTable.cache.values().toList().findAll{it.typ_name =~ /(?i)${q}/}
						res.each{it -> answer << [it.typ_id, it.typ_name]}
						break

					/*** NENAME ***/
					case "ne_name":
						db.getDB().eachRow("SELECT nen_id, nen_name FROM ne_name WHERE nen_name like('" +q+
						"%') ORDER BY nen_name", {row ->
							answer << [
								row['nen_id'],
								row['nen_name']
							]
						})
						break

					/*** NECATEGORY ***/
					case "ne_category":
						neCategoryTable.createCache() // it checks if there is one, relax
						List<NECategory> res = neCategoryTable.all_id_category.values().toList()
								.findAll{it.nec_category =~ /(?i)${q}/}
						res.each{answer << [it.nec_id, it.nec_category]}
						break

					/*** NETYPE ***/
					case "ne_type":
						neTypeTable.createCache() // it checks if there is one, relax
						List<NEType> res = neTypeTable.all_id_type.values().toList().findAll{it.net_type =~ /(?i)${q}/}
						res.each{answer << [it.net_id, it.net_type]}
						break

					/*** NESUBTYPE ***/
					case "ne_subtype":
						neSubtypeTable.createCache() // it checks if there is one, relax
						List<NESubtype> res = neSubtypeTable.all_id_subtype.values().toList().findAll{it.nes_subtype =~ /(?i)${q}/}
						res.each{answer << [it.nes_id, it.nes_subtype]}
						break

					/*** ENTITY ***/
					case ["ne_entity", "ehg_entity"]:
						db.getDB().eachRow("SELECT ent_id, ent_name FROM entity WHERE "+
						"ent_name like('%" +q+ "%') ORDER BY ent_name", {row ->
							answer << [
								row['ent_id'],
								row['ent_name']
							]
						})
						break

					/*** GEOSCOPE ***/
					case [
						"ehg_geoscope",
						"sgr_geoscope"
					]:
						db.getDB().eachRow("SELECT geo_id, geo_name FROM geoscope WHERE "+
						"geo_name like('%" +q+ "%') ORDER BY geo_name", {row ->
							answer << [
								row['geo_id'],
								row['geo_name']
							]
						})
						break

					/*** SUBJECT ***/
					case "sgr_subject":
						db.getDB().eachRow("SELECT sbj_id, sbj_subject FROM subject WHERE "+
						"sbj_subject like('%" +q+ "%') ORDER BY sbj_subject", {row ->
							answer << [
								row['sbj_id'],
								+row['sbj_subject']
							]
						})
						break
				}
			}

			bind["status"] = 0
			bind["message"] = answer
			return JSONHelper.toJSON(bind)

		}
	}
}
