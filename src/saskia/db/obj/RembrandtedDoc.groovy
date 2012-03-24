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
package saskia.db.obj

import java.util.Date

import org.apache.log4j.Logger

import rembrandt.obj.ListOfNE
import saskia.db.DocStatus
import saskia.db.table.*
import saskia.db.database.*
import saskia.db.SaskiaWebstore
import pt.tumba.webstore.exceptions.VolumeUnreachable
import groovy.sql.*
import saskia.dbpedia.DBpediaAPI
import saskia.dbpedia.DBpediaOntology
import saskia.dbpedia.DBpediaResource


/**
 * @author Nuno Cardoso
 *
 */
class RembrandtedDoc extends DBObject implements JSONable {

	/* doc table fields */
	Long doc_id
	String doc_original_id
	Collection doc_collection
	String doc_webstore
	Integer doc_version = 1
	String doc_lang
	Date doc_date_created
	Date doc_date_tagged
	DocStatus doc_proc
	DocStatus doc_sync
	Long doc_latest_geo_signature
	Long doc_latest_time_signature

	//This is obtained from the webstore
	String doc_content
	boolean retrieved_doc_content = false

	Job doc_job // if this RembrandtDoc is associated to a job, typically: R2P, GEO, TIM

	/* other fields gathered from other table data */
	ListOfNE NEs
	List<Tag> tags
	List<Geoscope> geoscopes
	List<Entity> entities

	static Date nulldate = new Date(0)
	DBpediaAPI dbpedia = DBpediaAPI.newInstance()
	DBpediaOntology dbpediaontology = DBpediaOntology.getInstance()

	static Logger log = Logger.getLogger("RembrandtedDoc")

	static Map type = ['doc_id':'Long', 'doc_original_id':'String', 'doc_collection':'Collection',
		'doc_webstore':'String', 'doc_version':'Integer',
		'doc_lang':'String', 'doc_date_created':'Date', 'doc_date_created':'Date',
		'doc_proc':'DocStatus', 'doc_sync':'DocStatus',
		'doc_latest_geo_signature':'Long', 'doc_latest_time_signature':'Long']

	public RembrandtedDoc(DBTable dbtable) {
		super(dbtable)
	}

	static RembrandtedDoc createNew(DBTable dbtable, row) {
		RembrandtedDoc r = new RembrandtedDoc(dbtable)
		if (row['doc_id']) r.doc_id = row['doc_id']
		if (row['doc_original_id']) r.doc_original_id = row['doc_original_id']
		if (row['doc_collection']) 
			r.doc_collection = (row['doc_collection'] instanceof Collection ?
			row['doc_collection'] : dbtable.getSaskiaDB().getDBTable("CollectionTable").getFromID(row['doc_collection']))

		if (row['doc_webstore']) r.doc_webstore = row['doc_webstore']
		if (row['doc_version']) r.doc_version = row['doc_version']
		if (row['doc_lang']) r.doc_lang = row['doc_lang']
		if (row['doc_date_created'] && (Date)row['doc_date_tagged'] != nulldate)
			r.doc_date_created = (Date)row['doc_date_created'] // it's a java.sql.Timestamp, a subclass of Date
		if (row['doc_date_tagged'] && (Date)row['doc_date_tagged'] != nulldate)
			r.doc_date_tagged = (Date)row['doc_date_tagged'] // it's a java.sql.Timestamp, a subclass of Date
		if (row['doc_proc']) 
			r.doc_proc = (row['doc_proc'] instanceof DocStatus ? 
			row['doc_proc'] : DocStatus.getFromValue(row['doc_proc']))
		if (row['doc_sync']) 
			r.doc_sync = (row['doc_sync'] instanceof DocStatus ? 
			row['doc_sync'] : DocStatus.getFromValue(row['doc_sync']))

		if (row['doc_latest_geo_signature']) r.doc_latest_geo_signature = row['doc_latest_geo_signature']
		if (row['doc_latest_time_signature']) r.doc_latest_time_signature = row['doc_latest_time_signature']

		// if it's a RembrandtedDoc coming from the DB
		if (row instanceof GroovyResultSet && row['doc_webstore']) { 
			 try {
				r.doc_content = dbtable.getWebstore().retrieve(r.doc_webstore)
				r.retrieved_doc_content = true
			}catch(Exception e) { log.warn e.getMessage() }

		// but it also may be a new RembrandtedDoc to be added to the DB...
		} 
		if (!(row instanceof GroovyResultSet) && !row['doc_webstore'] && row.containsKey('doc_content')) { 
			 try {
				r.doc_content = row['doc_content']
				r.retrieved_doc_content = false
			}catch(Exception e) { log.warn e.getMessage() }
		}
		return r 
	}

	public Map toMap() {

		return ["doc_id":doc_id, "doc_collection":doc_collection.toSimpleMap(),
			"doc_original_id":doc_original_id, "doc_version":doc_version,
			"doc_lang":doc_lang, "doc_date_created":doc_date_created,
			"doc_entity":getEntities()?.collect{it.toMap()},
			"doc_tag":getTags()?.collect{it.toMap()},
			"doc_webstore":doc_webstore,
			"doc_date_tagged":doc_date_tagged,
			"doc_proc":doc_proc,
			"doc_sync":doc_sync,
			"doc_content":[
				"title": getTitleFromContent()?.replaceAll(/\n/, " "),
				"body": getBodyFromContent()?.replaceAll(/\n/, " ")
			]
		]
	}

	public Map toSimpleMap() {
		return toMap()
	}

	/** Handle with care! */
	public updateValue(column, value) {
		def newvalue
		switch (type[column]) {
			case 'String': newvalue = value; break
			case 'Long': newvalue = Long.parseLong(value); break
		}
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().tablename} SET ${column}=? WHERE doc_id=?",
				[newvalue, doc_id])
		return res
	}

	public DocGeoSignature getGeographicSignature() {
		return getDBTable().getSaskiaDB()
		.getDBTable("DocGeoSignatureTable")
		.getFromID(doc_latest_geo_signature)
	}

	public DocTimeSignature getTimeSignature() {
		return getDBTable().getSaskiaDB()
		.getDBTable("DocTimeSignatureTable")
		.getFromID(doc_latest_time_signature)
	}

	/* there's a difference between tags = null and tags = [] - the last one says that I cheched the DB */
	/* it both updates the entities var, and returns it */
	List<Tag> getTags() {
		if (tags == null) {
			def tagtable = dbtable.getSaskiaDB().getDBTable("TagTable")
			tags = tagtable.queryDB(
				"SELECT * FROM ${tagtable.tablename}, doc_has_tag WHERE dtg_tag=tag_id and dtg_document=?",
					[doc_id])
		}
		return tags
	}
	
	/** Remove entries from doc_has_ne table. It will NOT erase entries on other tables.
	 *  This is required so that the document can be synced to the Saskia.
	 */
	public removeDocHasNEsFromPool() {
		def res = getDBTable().getSaskiaDB().getDB().execute(
			"DELETE FROM ${getDBTable().dhn_table} WHERE dhn_doc=?",
			[doc_id])
		log.debug "Deleting doc_has_ne for doc_id ${doc_id}, got result ${res}"
		return res
	}

	/** NE consistency methods
	 * These methods are an interface to handle the DB tables that contain NE info. 
	 * The Groovy objects are ListOfNE and NamedEntity - no need to create new ones. 
	 */
	public addNEsToSaskia(ListOfNE NEs, String title_or_body, String doc_lang) {

		def newid // temp variable to be used for auto-incremented numbers on new DB insertions

		// first: let's trigger a cache for NE category, type and subtype
		NECategoryTable neCategoryTable = getDBTable().getSaskiaDB().getDBTable("NECategoryTable")
		NETypeTable neTypeTable = getDBTable().getSaskiaDB().getDBTable("NETypeTable")
		NESubtypeTable neSubtypeTable = getDBTable().getSaskiaDB().getDBTable("NESubtypeTable")
		NENameTable neNameTable = getDBTable().getSaskiaDB().getDBTable("NENameTable")
		NETable neTable = getDBTable().getSaskiaDB().getDBTable("NETable")
		EntityTable entityTable = getDBTable().getSaskiaDB().getDBTable("EntityTable")

		neCategoryTable.createCache()
		neTypeTable.createCache()
		neSubtypeTable.createCache()

		// add categories, types and subtypes, while NEs are being read
		NEs.each{ne ->

			NEName nen = null
			//long rel_id = relations[Relation.default_relation]
			List<NE> nelist = []
			List<Entity> entitylist = []

			// Check categories
			ne.classification.each{cl ->

				// TODO: we're using RAW c, t and s.
				if (cl?.c && !neCategoryTable.all_category_id.containsKey(cl.c)) {
					log.trace "Adding a new category: ${cl.c}"
					NECategory nec = NECategory.createNew(neCategoryTable, [nec_category:cl.c])
					nec.nec_id = nec.addThisToDB()
					log.trace "Category  ${cl.c} has a new id: ${nec.nec_id}"
				}

				if (cl?.t && !neTypeTable.all_type_id.containsKey(cl.t)) {
					log.trace "Adding a new type: ${cl.t}"
					NEType net = NEType.createNew(neTypeTable, [net_type:cl.t])
					net.net_id = net.addThisToDB()
					log.trace "Type ${cl.t} has a new id: ${net.net_id}"
				}
				if (cl?.s && !neSubtypeTable.all_subtype_id.containsKey(cl.s)) {
					log.trace "Adding a new subtype: ${cl.s}"
					NESubtype nes = NESubtype.createNew(neSubtypeTable, [nes_subtype:cl.s])
					nes.nes_id = nes.addThisToDB()
					log.trace "Subtype ${cl.s} has a new id: ${nes.nes_id}"
				}
			}

			// get NE name. There's a cache underneath
			nen = neNameTable.getFromName(ne.printTerms())
			if (!nen) {
				log.trace "no NEName entry '${ne.printTerms()}' found on DB. Creating a new entry."
				nen = NEName.createNew(neNameTable, 
					[nen_name:ne.printTerms(), nen_nr_terms:ne.terms.size()])
				if (nen.nen_name.size() > 254) {
					// too big!
					log.warn "NE has a huge name, over 255 chars! ${nen.nen_name}"
					log.warn "NE will be skipped."
					ne = null
				} else {
					nen.nen_id = nen.addThisToDB()
				}
			} else {
				log.trace "Got NEName '${nen.nen_name}' from DB. Id is ${nen.nen_id}."
			}

			ne?.classification.each{c  ->

				NECategory nec = null
				if (c?.c) nec = neCategoryTable.getFromCategory(c.c)
				NEType net = null
				if (c?.t) net = neTypeTable.getFromType(c.t)
				NESubtype nes = null
				if (c?.s) nes = neSubtypeTable.getFromSubtype(c.s)

				def resources = ne.dbpediaPage[c]
				if (resources && resources.size() > 2)
					log.warn("Note: NE $ne for classification $c has more than one DBpedia resource: $resources. Going to use the first")

				Entity e = null

				resources?.each{resource ->
					e = entityTable.getFromDBpediaResource(DBpediaResource.getShortName(resource))
					if (!e) {
						log.trace "no DBpedia entry ${resource} on DB. Creating a new entry."
						// let's get a classification.
						List listOfClasses = dbpedia.getDBpediaOntologyClassFromDBpediaResource(resource)
						log.trace "Classifying DBpedia resource $resource generated classes ${listOfClasses}"
						log.trace "Narrower one: "+dbpediaontology.getNarrowerClassFrom(listOfClasses)
						e = Entity.createNew(entityTable, 
							[ent_dbpedia_resource:DBpediaResource.getShortName(resource),
							ent_dbpedia_class:dbpediaontology.getNarrowerClassFrom(listOfClasses)
							])
						e.ent_id = e.addThisToDB()
					}
				}  // each resource

				//ne2 is not really a NE, it's a NE from DB
				log.trace "Searching for NEs with name $nen, lang $doc_lang, class ${nec}-${net}-${nes}, entity $e"
				NE ne2 = neTable.getFromNameAndLangAndClassificationAndEntity(
					nen, doc_lang, nec, net, nes, e)

				if (!ne2) {
					log.trace "Not found, creating new NE ne_name=${nen.nen_name}, lang ${doc_lang}, ${nec}, ${net}, ${nes} and entity ${e}."
					ne2 = NE.createNew(neTable, [
						ne_name:nen,
						ne_lang:doc_lang,
						ne_category:nec,
						ne_type:net,
						ne_subtype:nes,
						ne_entity:e
					])
					ne2.ne_id = ne2.addThisToDB()
				} else {
					log.trace "Found NE ${ne2} with id ${ne2.ne_id}"
				}

				newid = getDBTable().getSaskiaDB().getDB().executeInsert(
					"INSERT INTO ${getDBTable().dhn_table} VALUES(0,?,?,?,?,?)",
						[
							doc_id,
							ne2.ne_id,
							title_or_body,
							ne.sentenceIndex,
							ne.termIndex
						])
				log.debug "Inserted doc_has_ne for doc.id ${doc_id}, ne ${ne2}, got ${newid}"

			}// each classification

		}// each NE

	}// method

	public int changeProcStatusInDBto(DocStatus status) {
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().tablename} SET doc_proc=? WHERE doc_id=?",
				[status.text(), doc_id])
		log.debug "Wrote proc status ${status}(${status.text()}) to doc_id ${doc_id}, ${res} rows were changed."
		return res
	}

	public int changeSyncStatusInDBto(DocStatus status) {
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"UPDATE ${getDBTable().tablename} SET doc_sync=? WHERE doc_id=?",
				[status.text(), doc_id])
		log.debug "Wrote sync status ${status}(${status.text()}) to doc_id ${doc_id}, ${res} rows were changed."
		return res
	}


	/**
	 * The IGNORE does not generate error for duplicate keys
	 */
	public associateWithEntity(Entity entity) {
		getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT IGNORE INTO ${getDBTable().die_table} VALUES(?,?)",
				[doc_id, entity.ent_id])
	}
	/**
	 * The IGNORE does not generate error for duplicate keys
	 */
	public associateWithTag(Tag tag) {
		//println "going for $doc_id, ${tag.tag_id}"
		getDBTable().getSaskiaDB().getDB().executeInsert(
				"INSERT IGNORE INTO ${getDBTable().dtg_table} VALUES(?,?)",
				[doc_id, tag.tag_id])
	}


	String getTitleFromContent() {
		String s = null
		doc_content?.find(/(?si)<TITLE>(.*?)<\/TITLE>/) {match, g1 -> s = g1.trim()}
		return s
	}

	String getBodyFromContent() {
		// println "rdoc_content = $rdoc_content"
		String s = null
		doc_content?.find(/(?si)<BODY>(.*?)<\/BODY>/) {match, g1 -> s = g1.trim()}
		return s
	}

	String getPlainText(String text) {
		// take off tags
		String res = text.replaceAll(/<[^>]*>/,"")
		// protect escaped stuff
		res = res.replaceAll(/\\([\Q[]{}\E])/) {all, g1 -> "_¤¤¤_${g1.charAt(0).hashCode()}_¤¤¤_"}
		// [ = 91, ] = 93, { = 123, } = 125

		// take off all {, [, ] and } that are not preceeded with \
		res = res.replaceAll(/([\Q{}[]\E])/) {all, g1 ->
			if (g1 == "]") return " " else return ""}

		res = res.replaceAll(/_¤¤¤_(\d+)_¤¤¤_/) {all, g1 ->
			switch (Integer.parseInt(g1)) {
				case 91: return "["; break;
				case 93: return "]"; break;
				case 123: return "{"; break;
				case 125: return "}"; break;
			}
		}
		return res
	}

	/** loads NE info from DB */
	public ListOfNE addNEsFromSaskia(String title_or_body) {
		// TODO
		ListOfNE NEs = new ListOfNE()
		return NEs
	}

	/** Add the fields in this object to the DB
	 * @return The new id for the doc table, from the DB
	 */
	
	public Long addThisToDB() {
		// try to ground the document to an entity

		try {
			String key
			if (doc_content) {
				 key = getDBTable().webstore.store(doc_content, SaskiaWebstore.VOLUME_RDOC)
				log.trace "Got content $doc_content, wrote to Webstore RDOC, got key $key"

				def res = getDBTable().getSaskiaDB().getDB().executeInsert(
						"INSERT INTO ${getDBTable().tablename}"+
						"(doc_original_id, doc_collection, doc_webstore, doc_version, doc_lang, "+
						"doc_date_created, doc_date_tagged) VALUES(?,?,?,?,?,?,NOW())",
						[
							doc_original_id,
							doc_collection.col_id,
							key,
							doc_version,
							doc_lang,
							doc_date_created
						])
				doc_id = (long) res[0][0]
				doc_webstore = key
				log.info "Inserted RembrandtedDoc into DB: ${this}"
			} else {
				log.error "Did NOT inserted RembrandtedDoc into DB: doc_content for {$this} is empty!"
			}
		} catch (VolumeUnreachable ve) {
			log.fatal "RembrandtedDoc can't proceed with addThisToDB. Reason: Webstore volume ${SaskiaWebstore.VOLUME_RDOC} is not running."
			log.fatal "Please launch the volume with 'webstore -l ${SaskiaWebstore.VOLUME_RDOC}' and try again."
			System.exit(0)
		} catch (Exception e) {
			log.error "Did NOT inserted RembrandtedDoc into DB: ${e.getMessage()}"
		}
		return doc_id
	}

	public Long replaceThisToDB() {
		def res

		try {
			String key
			if (doc_content) {
				// apagar o conteúdo anterior
				if (doc_webstore) {
					try {
						getDBTable().getWebstore().delete(doc_webstore, SaskiaWebstore.VOLUME_RDOC)
						log.info "Replacing RembrandtedDoc: deleted webstore $doc_webstore."
					} catch(Exception e) {
						log.error "Error while replacing RembrandtedDoc: "+e.getMessage()
					}
				}
				key = getWebstore().store(doc_content, SaskiaWebstore.VOLUME_RDOC)
				log.info "Got content (${doc_content.size()} bytes), wrote to Webstore RDOC, got key $key"
				
				res = getDBTable().getSaskiaDB().getDB().executeUpdate(
						"UPDATE ${getDBTable().tablename} SET "+
						"doc_original_id=?, doc_collection=?, doc_webstore=?, doc_lang=?, "+
						"doc_date_tagged=NOW(), doc_version=doc_version+1 WHERE doc_id=? ",
						[
							doc_original_id,
							doc_collection.col_id,
							key,
							doc_lang,
							doc_id
						])
				doc_version++
				log.info "Replaced RembrandtedDoc into DB: ${this}"
			} else {
				log.error "Did NOT replaced RembrandtedDoc into DB: doc_content for {$this} is empty!"
			}
		} catch (VolumeUnreachable ve) {
			log.fatal "RembrandtedDoc can't proceed with addThisToDB. Reason: Webstore volume ${SaskiaWebstore.VOLUME_RDOC} is not running."
			log.fatal "Please launch the volume with 'webstore -l ${SaskiaWebstore.VOLUME_RDOC}' and try again."
			System.exit(0)
		}
		catch (Exception e) {
			log.error "Did NOT replaced RembrandtedDoc into DB: ${e.getMessage()}"
		}
		return doc_id
	}

	public int removeThisFromDB() {
		if (!doc_id) return null
		def res = getDBTable().getSaskiaDB().getDB().executeUpdate(
				"DELETE FROM ${getDBTable().tablename} where doc_id=?",[doc_id])
		log.info "Removed RembrandtedDoc ${this} from DB, got $res"
		return res
	}

	public String toString() {
		return "RDoc(${doc_id});DocOriginalId(${doc_original_id});Webstore(${doc_webstore});Lang(${doc_lang})"
	}
}
