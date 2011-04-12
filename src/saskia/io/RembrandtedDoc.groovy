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

package saskia.io

import org.apache.log4j.*
import java.util.Map;
import java.util.regex.Matcher
import rembrandt.obj.ListOfNE
import rembrandt.obj.Document
import rembrandt.io.RembrandtReader
import rembrandt.io.RembrandtStyleTag
import saskia.dbpedia.DBpediaResource
import saskia.dbpedia.DBpediaOntology
import saskia.dbpedia.DBpediaAPI
import saskia.bin.Configuration


/** This class is an interface for the RembrandtedDoc and Doc tables in the WikiRembrandt 
  * database. There is a 1:1 relation between them - Doc has the metadata, RembrandtDoc
  * the contents. The reason is that RembrandtedDoc has a FULLTEXT index and needs to be 
  * a MySQL myISAM table (InnoDB does not have FULLTEXT), but Doc is a InnoDB to support 
  * all the foreign keys and keep the integrity of the database (which MyISAM does not support yet).
  */

class RembrandtedDoc extends DBObject implements JSONable {

    static String tablename = "doc"
    static String die_table = "doc_is_entity"
    static String dhn_table = "doc_has_ne"
    static String dtg_table = "doc_has_tag"
	 static String job_doc_type_label = "RDOC"
	
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
	
    static Map type = ['doc_id':'Long', 'doc_original_id':'String', 'doc_collection':'Collection', 
		'doc_webstore':'String', 'doc_version':'Integer',
		'doc_lang':'String', 'doc_date_created':'Date', 'doc_date_created':'Date',
		'doc_proc':'DocStatus', 'doc_sync':'DocStatus', 
		'doc_latest_geo_signature':'Long', 'doc_latest_time_signature':'Long'] 
    
    //This is obtained from the webstore 
    String doc_content
    boolean retrieved_doc_content = false

 	 Job doc_job // if this RembrandtDoc is associated to a job, typically: R2P, GEO, TIM

    /* other fields gathered from other table data */
    ListOfNE NEs
    List<Tag> tags
    List<Geoscope> geoscopes
    List<Entity> entities
    
    // Document is already post_processed.
	 static Configuration conf = Configuration.newInstance()
    static SaskiaDB db = SaskiaDB.newInstance()
    static SaskiaWebstore webstore = SaskiaWebstore.newInstance()
    static Logger log = Logger.getLogger("RembrandtedDoc")
    String lang = Configuration.newInstance().get("global.lang")
    DBpediaAPI dbpedia = DBpediaAPI.newInstance()
	 DBpediaOntology dbpediaontology = DBpediaOntology.getInstance()
    static Date nulldate = new Date(0)
    static RembrandtReader reader = new RembrandtReader( new RembrandtStyleTag(
        conf.get("rembrandt.input.styletag.lang", conf.get("global.lang"))))

    static List<RembrandtedDoc> queryDB(String query, List params) {
		List<RembrandtedDoc> l = []
		RembrandtedDoc r 

		db.getDB().eachRow(query, params, {row  -> 
    	    r = new RembrandtedDoc()
    	    r.doc_id = row['doc_id']
    	    r.doc_original_id = row['doc_original_id']
    	    if (row['doc_collection']) r.doc_collection = Collection.getFromID(row['doc_collection'])
          r.doc_webstore = row['doc_webstore']
    	    r.doc_version = row['doc_version']
    	    if (row['doc_lang']) r.doc_lang = row['doc_lang']
    	    if (row['doc_date_created'] && (Date)row['doc_date_tagged'] != nulldate) 
    			r.doc_date_created = (Date)row['doc_date_created'] // it's a java.sql.Timestamp, a subclass of Date
    	    if (row['doc_date_tagged'] && (Date)row['doc_date_tagged'] != nulldate) 
    			r.doc_date_tagged = (Date)row['doc_date_tagged'] // it's a java.sql.Timestamp, a subclass of Date
    	    r.doc_proc = DocStatus.getFromValue(row['doc_proc'])			
    	    r.doc_sync = DocStatus.getFromValue(row['doc_sync'])			
    	    
    	    if (row['doc_latest_geo_signature']) r.doc_latest_geo_signature = row['doc_latest_geo_signature']
    	    if (row['doc_latest_time_signature']) r.doc_latest_time_signature = row['doc_latest_time_signature']	 
            if (r.doc_webstore) {  try {
        			r.doc_content = webstore.retrieve(r.doc_webstore)
        			r.retrieved_doc_content = true
            }catch(Exception e) { log.warn e.getMessage() }
          }
    	    l << r
		})
		return (l ? l : null)
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

    static Map listRembrandtedDocs(Collection collection, limit = 10,  offset = 0, column = null, needle = null) {

	// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0
	
	 	String where = "WHERE doc_collection=? "
	 	List params = [collection.col_id]
	 	if (column && needle) {
	     switch (type[column]) {
		  case 'String': where += " AND $column LIKE '%${needle}%'"; break
		  case 'Long': where += " AND $column=? "; params << Long.parseLong(needle); break
		  case 'DocStatus':  where += " AND $column = ?"; params << needle; break
		  case 'Date': where += " AND $column = ?"; params << needle; break
	     }
	 }
	
	String query = "SELECT SQL_CALC_FOUND_ROWS * FROM ${tablename} "+
		"$where LIMIT ${limit} OFFSET ${offset} UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), "+
		"NULL, NULL, NULL, NULL, NULL, NULL,NULL,NULL,NULL,NULL,NULL"
	log.debug "query = $query params = $params class = "+params*.class
	List u 
	try {u = queryDB(query, params) }
	catch(Exception e) {log.error "Error getting rembrandted doc list: ", e}
	// last "user" is not the user... it's the count.
	RembrandtedDoc fake_rdoc = u.pop()
	long total = fake_rdoc.doc_id
	log.debug "Returning "+u.size()+" results."
	return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
	        "column":column, "value":needle, "col_id":collection.col_id]
    }
    
   /** Handle with care! */
	public updateValue(column, value) {
	    def newvalue	    
	    switch (type[column]) {
	        case 'String': newvalue = value; break
	        case 'Long': newvalue = Long.parseLong(value); break
	    }
	    def res = db.getDB().executeUpdate("UPDATE ${tablename} SET ${column}=? WHERE doc_id=?",[newvalue, doc_id])
	    return res
	}
	

    static List<RembrandtedDoc> getBatchOfRembrandtedDocs(Collection collection, limit = 10,  offset = 0) {
        // limit & offset can come as null... they ARE initialized...
        if (!limit) limit = 10
        if (!offset) offset = 0
               
        return queryDB("SELECT SQL_CALC_FOUND_ROWS * "+
        " FROM  ${RembrandtedDoc.tablename}  WHERE doc_collection=? "+
        "ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id])      
        // ORDER BY doc_id ASC assures that these batches are ordered 
    }

   static List<RembrandtedDoc> getBatchOfRembrandtedDocsOrderedByOriginalDocId(Collection collection, limit = 10,  offset = 0) {
        // limit & offset can come as null... they ARE initialized...
        if (!limit) limit = 10
        if (!offset) offset = 0
               
        return queryDB("SELECT SQL_CALC_FOUND_ROWS * "+
        " FROM  ${RembrandtedDoc.tablename}  WHERE doc_collection=? "+
        "ORDER BY doc_original_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id])      
        // ORDER BY doc_original_id ASC assures that these batches are ordered 
    }
    
    static RembrandtedDoc getFromOriginalDocIDandCollection(String doc_original_id, Collection collection) {
			if (!doc_original_id || !collection) return null 
			List<RembrandtedDoc> l = queryDB("SELECT * FROM ${RembrandtedDoc.tablename} "+
			"WHERE doc_original_id=? AND doc_collection=?", 
			[doc_original_id, collection.col_id, ])
			log.trace "Querying Saskia for doc_original_id:$doc_original_id, collection $collection, got ${l}" 
			return (l ? l[0] : null) 
    }
    	
    /** Get a RembrandtedDoc from an id of the Doc table.
     * @param doc_id The id of the document. 
     * return the RembrandtedDoc
     */ 
    static RembrandtedDoc getFromID(long doc_id) {
		if (!doc_id) return null 
		List<RembrandtedDoc> l = queryDB("SELECT * FROM ${RembrandtedDoc.tablename} WHERE doc_id=?", [doc_id])
		log.info "Querying for doc_id $doc_id, got RembrandtedDoc ${l}" 
		return (l ? l[0] : null) 
    }

  /** Get a RembrandtedDoc from an id of the Doc table.
     * @param doc_id The id of the document. 
     * return the RembrandtedDoc
     */ 
    static RembrandtedDoc getFromOriginalID(String doc_original_id) {
		if (!doc_original_id) return null 
		List<RembrandtedDoc> l = queryDB("SELECT * FROM ${RembrandtedDoc.tablename} WHERE doc_original_id=?", [doc_original_id])
		log.info "Querying for doc_original_id $doc_original_id, got RembrandtedDoc ${l}" 
		return (l ? l[0] : null) 
    }

    /**
     * Get a batch of RembrandtedDocs from a list of ids
     */ 
    static List<RembrandtedDoc> getFromOriginalIDs(List<Long> doc_ids, Collection collection) {
	if (!doc_ids || !collection) return null
	String where = "("+doc_ids.join(",")+")"
	List<RembrandtedDoc> l = queryDB("SELECT * FROM ${RembrandtedDoc.tablename} WHERE "+
	"doc_collection=? AND doc_original_id IN "+where, [collection.col_id])
	return (l ? l : null) 
    }
	
    /** Get a batch of RembrandtedDocs good to sync NE pool
     * @param batchsize The batchsize, default to 100
     * This method is now treadsafe
     * return A list of RembrandtedDocs
     */ 
	static List<RembrandtedDoc> getBatchDocsToSyncNEPool(Task task, String process_signature,
		Collection collection, int limit = 30) {
		
		List<RembrandtedDoc> l = []
		RembrandtedDoc r
		 
		db.getDB().withTransaction{
	    log.info "Getting a set of $limit processable rembrandtedDocs to sync to NE pool"  
		
		def query = "SELECT * FROM ${RembrandtedDoc.tablename} WHERE doc_collection=? AND doc_proc IN "+
		DocStatus.whereConditionGoodToProcess()+" AND doc_sync in "+DocStatus.whereConditionGoodToSyncNEPool()+
		" AND doc_id NOT IN (select job_doc_id from "+Job.tablename+" where job_doc_type='"+
		 job_doc_type_label+"' AND job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+
		 ") LIMIT ${limit} FOR UPDATE"  
		// VERY IMPORTANT, the FOR UPDATE, it locks the table until the transaction is complete
 
		def params = [collection.col_id]	                  
		log.trace query
		log.trace params
			
		db.getDB().eachRow(query, params, {row ->  
			r = new RembrandtedDoc()
			r.doc_id = row['doc_id']
			r.doc_original_id = row['doc_original_id']
			r.doc_webstore = row['doc_webstore']
			if (row['doc_lang']) r.doc_lang = row['doc_lang']
			if (row['doc_date_created']) r.doc_date_created = row['doc_date_created'] // it's a java.sql.Timestamp, a subclass of Date
			if (row['doc_date_tagged']) r.doc_date_tagged = row['doc_date_tagged'] // it's a java.sql.Timestamp, a subclass of Date
			r.doc_proc = DocStatus.getFromValue(row['doc_proc'])			
			r.doc_sync = DocStatus.getFromValue(row['doc_sync'])			
	
			if (r.doc_webstore) r.doc_content = webstore.retrieve(r.doc_webstore)
			
			l << r
			
		// LET's create JOBS to mark the queue
			Job job = new Job(job_task:task, job_worker:process_signature, job_doc_type:job_doc_type_label,
			job_doc_id:r.doc_id, job_doc_edit:DocStatus.QUEUED, job_doc_edit_date:new Date())
			job.job_id = job.addThisToDB()
			r.doc_job = job
	    })
	}// with transaction	
        return (l ? l : null) 
    }
    
    /**
     * This is a call to be used for GeoSignature generation
     * 
    select doc_id, dhn_section, dhn_sentence, dhn_term, nen_name, ne_type, ne_subtype, ent_id, ent_dbpedia_class from doc, doc_has_ne, 
    ne_name, ne RIGHT JOIN entity on ent_id=ne_entity RIGHT JOIN entity_has_geoscope ON ehg_entity=ent_id where doc_id=dhn_doc and 
    dhn_ne=ne_id and ne_name=nen_id and ne_category=2 and doc_latest_geo_signature IS NULL LIMIT 10;
    +--------+-------------+--------------+----------+----------------------------+---------+------------+--------+------------------+
    | doc_id | dhn_section | dhn_sentence | dhn_term | nen_name                   | ne_type | ne_subtype | ent_id |ent_dbpedia_class |
    +--------+-------------+--------------+----------+----------------------------+---------+------------+--------+------------------+
    |      1 | B           |            0 |       29 | Estados Unidos da América  |       2 |          2 |        | Country          | 
    |     28 | B           |           35 |       20 | Estados Unidos da América  |       2 |          2 |        | Country          | 
    |     37 | B           |           27 |       20 | Estados Unidos da América  |       2 |          2 |        | Country          |  
     NOT THREAD SAFE!*/
    static Map getBatchDocsAndNEsFromPoolToGenerateGeoSignatures(Collection collection, int limit) {
        
        Map docs = [:]
        NEType.createCache()
        NESubtype.createCache() 
            
         db.getDB().withTransaction{
            
            log.info "Getting a set of $limit RembrandtedDocs to generate GeoSignatures"  
            db.getDB().eachRow("SELECT doc_id, doc_original_id, doc_lang from doc where "+
				"doc_collection=? AND doc_latest_geo_signature IS NULL "+
            "AND doc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND "+ "doc_sync in "+
            DocStatus.whereConditionSynced()+" AND doc_id NOT IN (select job_doc_id from job where "+
 				"job_doc_type='"+job_doc_type_label+"' and job_doc_edit NOT IN "+
				DocStatus.whereConditionUnlocked()+") LIMIT ${limit}", [collection.col_id], {row -> 
                String lang = row['doc_lang']
                long doc_id = (long) row['doc_id']
                docs[doc_id] = ['lang':lang, 'doc_original_id':row['doc_original_id'], nes:[]]
                
                //def labels = Class.forName("rembrandt.gazetteers.${lang.toLowerCase()}.SecondHAREMClassificationLabels${lang.toUpperCase()}").newInstance()  
                
                int ne_category_local_pt = NECategory.getIDforLOCAL("pt")
                int ne_category_local_en = NECategory.getIDforLOCAL("en")
                int ne_category_local_rm = NECategory.getIDforLOCAL("rembrandt")//
                //conf.get("rembrandt.output.styletag.lang",conf.get("global.lang")))

               // println "$ne_category_local_pt $ne_category_local_en $ne_category_local_rm"
                 
                def query = "SELECT dhn_section, dhn_sentence, dhn_term, nen_name, ne_id, ne_type, "+
                 "ne_subtype, ent_id,  ent_dbpedia_class FROM doc_has_ne, ne_name, ne RIGHT JOIN entity ON "+
                "ent_id=ne_entity WHERE dhn_ne=ne_id AND ne_name=nen_id AND "+
                "dhn_doc=${doc_id} AND (ne_category=${ne_category_local_pt} OR ne_category=${ne_category_local_en} "+
                "OR ne_category=${ne_category_local_rm})"
                
                //println "query : $query"
                db.getDB().eachRow(query, [], {row2 -> 
                    docs[doc_id].nes << [section:row2['dhn_section'], sentence:row2['dhn_sentence'],
                    term:row2['dhn_term'], name:row2['nen_name'], //neid:row2['ne_id'], 
                    type:NEType.all_id_type[row2['ne_type']], subtype:NESubtype.all_id_subtype[row2['ne_subtype']], 
                    entity:row2['ent_id'], dbpediaClass:row2['ent_dbpedia_class'] ]                  
                })                               
            })          
        }// with transaction  
       // println "Got docs: $docs"
        return docs
    }
    
    static Map getBatchDocsAndNEsFromRDOCToGenerateGeoSignatures(Collection collection, int limit) { 
 
        Map docs = [:]
        NEType.createCache()
        NESubtype.createCache() 

		List docs_list = 

		db.getDB().eachRow("SELECT doc_id, doc_original_id, doc_webstore, doc_lang from doc where "+
			"doc_collection=? AND doc_latest_geo_signature IS NULL "+
            "AND doc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND "+ 
//"doc_sync in "+DocStatus.whereConditionSynced()+" AND 
			"doc_id NOT IN (select job_doc_id from job where "+
 				"job_doc_type='"+job_doc_type_label+"' and job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+") "+
            " LIMIT ${limit}", [collection.col_id], {row -> 
					String lang = row['doc_lang']
                long doc_id = (long) row['doc_id']
                docs[doc_id] = ['lang':lang, 'doc_original_id':row['doc_original_id'], nes:[]]
                String content
				if (row['doc_webstore']) {  
					try {
        				content = webstore.retrieve(row['doc_webstore'])
                    }catch(Exception e) { log.warn e.getMessage() }
            	}
				Document doc = reader.createDocument(content)
             	doc.tokenize()	
		
			 doc.titleNEs?.each{ne -> 
				
				String section = 'T'
				 
            	ne.classification?.each{cl -> 
	
						NECategory category = (cl.c? NECategory.getFromCategory(cl.c) : null)
						NEType type = (cl.t? NEType.getFromType(cl.t) : null)
						NESubtype subtype = (cl.s? NESubtype.getFromSubtype(cl.s) : null)
					
					// use only those who are LOCAL
					
					if (cl.c == "@LOCAL") {
						Entity e = (ne.dbpediaPage.containsKey(cl) ? 
					   	(ne.dbpediaPage[cl] instanceof List ? 
						 (!ne.dbpediaPage[cl].isEmpty() ? Entity.getFromDBpediaResource(ne.dbpediaPage[cl][0]) : null)
						: Entity.getFromDBpediaResource(ne.dbpediaPage[cl])
					    ) : null)
					
						if (!e) {
							// if it does not have an Entity, let's check if NE can help us
							NEName ne_name = NEName.getFromName(ne.printTerms())
							NE ne2 = NE.getFromNameAndLangAndClassificationAndNonNullEntity(ne_name, lang, category, type, subtype)
							if (ne2 && ne2.ne_entity) e = ne2.ne_entity
						}
					
				
						// don't care about those who does not have Entity
						if (e) docs[doc_id].nes << [section:section, sentence:ne.sentenceIndex,
                    	term:ne.termIndex, name:ne.printTerms(), //neid:row2['ne_id'], 
					//category:category, 
						type:type, subtype:subtype, entity:e.ent_id, dbpediaClass:e.ent_dbpedia_class ]
					}
				} 
			}
			doc.bodyNEs?.each{ne -> 
				
				String section = 'B'
				 
            	ne.classification?.each{cl -> 
	
					NECategory category = (cl.c? NECategory.getFromCategory(cl.c) : null)
                    NEType type = (cl.t? NEType.getFromType(cl.t) : null)
                    NESubtype subtype = (cl.s? NESubtype.getFromSubtype(cl.s) : null)
					
					// use only those who are LOCAL
					
					if (cl.c == "@LOCAL") {
						Entity e = (ne.dbpediaPage.containsKey(cl) ? 
					   	(ne.dbpediaPage[cl] instanceof List ? 
						 (!ne.dbpediaPage[cl].isEmpty() ? Entity.getFromDBpediaResource(ne.dbpediaPage[cl][0]) : null)
						: Entity.getFromDBpediaResource(ne.dbpediaPage[cl])
					    ) : null)
					
						if (!e) {
							// if it does not have an Entity, let's check if NE can help us
							NEName ne_name = NEName.getFromName(ne.printTerms())
							NE ne2 = NE.getFromNameAndLangAndClassificationAndNonNullEntity(ne_name, lang, category, type, subtype)
							if (ne2 && ne2.ne_entity) e = ne2.ne_entity
						}
					
						// don't care about those who does not have Entity
						if (e) docs[doc_id].nes << [section:section, sentence:ne.sentenceIndex,
                    	term:ne.termIndex, name:ne.printTerms(), //neid:row2['ne_id'], 
					//category:category, 
						type:type, subtype:subtype, entity:e.ent_id, dbpediaClass:e.ent_dbpedia_class ]
					}
				} 
			}
		})
		return docs
	}

    /*NOT THREAD SAFE!*/
    static Map getBatchDocsAndNEsFromPoolToGenerateTimeSignatures(Collection collection, int limit) {
        
        Map docs = [:]
      
        // TODO : change to rdoc_content
        db.getDB().withTransaction{
            
            log.info "Getting a set of $limit RembrandtedDocs to generate TimeSignatures"  
            db.getDB().eachRow("SELECT doc_id, doc_original_id, doc_webstore, doc_date_created, doc_lang from ${RembrandtedDoc.tablename} " +
              " where doc_collection=? AND doc_latest_time_signature IS NULL AND "+
				  " doc_proc IN "+DocStatus.whereConditionGoodToProcess()+" AND "+
              " doc_sync IN "+DocStatus.whereConditionSynced()+" AND "+
					"doc_id NOT IN (select job_doc_id from job where "+
 				"job_doc_type='"+job_doc_type_label+"' and job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+") "+
              "LIMIT ${limit}", [collection.col_id], {row -> 
                
                  
                  String doc_content = webstore.retrieve(row['doc_webstore'])
                 
              	  docs[row['doc_id']] = [lang:row['doc_lang'], original_id:row['doc_original_id'], 
              	  date:(Date)row['doc_date_created'], content:doc_content ]                  
                            
            })      
        }
        return docs
    }
    
    static Map getBatchDocsAndNEsFromRDOCToGenerateTimeSignatures(Collection collection, int limit) {
        
        Map docs = [:]
      
        // TODO : change to rdoc_content
        db.getDB().withTransaction{
            
            log.info "Getting a set of $limit RembrandtedDocs to generate TimeSignatures"  
            db.getDB().eachRow("SELECT doc_id, doc_original_id, doc_webstore, doc_date_created, doc_lang from ${RembrandtedDoc.tablename} " +
              " where doc_collection=? AND doc_latest_time_signature IS NULL AND "+
				  " doc_proc IN "+DocStatus.whereConditionGoodToProcess()+
              " AND doc_id NOT IN (select job_doc_id from job where "+
 				"job_doc_type='"+job_doc_type_label+"' and job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+") "+
              "LIMIT ${limit}", [collection.col_id], {row -> 

                  String doc_content = webstore.retrieve(row['doc_webstore'])
                 
              	  docs[row['doc_id']] = [lang:row['doc_lang'], original_id:row['doc_original_id'], 
              	  date:(Date)row['doc_date_created'], content:doc_content ]                  
                            
            })      
        }
        return docs
    }
   /*NOT THREAD SAFE!*/
    static Map getBatchDocsAndNEsFromPoolToGenerateNEIndex(Collection collection, int limit = 10,  offset = 0) {
        
        Map docs = [:]
        NECategory.createCache()
        NEType.createCache()
        NESubtype.createCache() 
        
        db.getDB().withTransaction{
             
           // log.info "Getting a set of $limit RembrandtedDocs to generate GeoSignatures"  
            db.getDB().eachRow(
            
            "SELECT * FROM ${RembrandtedDoc.tablename} "+
                    "WHERE doc_collection=? "+
                    "ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",
            [collection.col_id], {row -> 
                        String lang = row['doc_lang']
                        long doc_id = (long) row['doc_id']
                        docs[doc_id] = ['lang':lang, 'doc_original_id':row['doc_original_id'], nes:[]]
                                        
                        def query = "SELECT dhn_section, dhn_sentence, dhn_term, ne_id, nen_name, ne_category, ne_type, "+
                                "ne_subtype, ne_entity FROM doc_has_ne, ne_name, ne WHERE dhn_ne=ne_id AND "+
								"ne_name=nen_id AND dhn_doc=${doc_id} "
                        
                        //println "query : $query"
                        db.getDB().eachRow(query, [], {row2 -> 
                            docs[doc_id].nes << [section:row2['dhn_section'], sentence:row2['dhn_sentence'],
                            term:row2['dhn_term'], name:row2['nen_name'], //neid:row2['ne_id'], 
                            category: (row2['ne_category'] == null ? null : NECategory.getFromID(row2['ne_category'])),
                            type: (row2['ne_type'] == null ? null : NEType.getFromID(row2['ne_type'])),
                            subtype: (row2['ne_subtype'] == null ? null : NESubtype.getFromID(row2['ne_subtype'])), 
							entity: (row2['ne_entity'] == null ? null : Entity.getFromID(row2['ne_entity']))]
                                             
                        })                               
                    })          
        }// with transaction  
        // println "Got docs: $docs"
        return docs
    }

   /*NOT THREAD SAFE!*/    
	static Map getBatchDocsAndNEsFromRDOCToGenerateNEIndex(Collection collection, int limit= 10, offset = 0) {

        Map rdocs = [:]
        NECategory.createCache()
        NEType.createCache()
        NESubtype.createCache() 

		List rdocs_list = RembrandtedDoc.getBatchOfRembrandtedDocs(collection, limit, offset)
		log.debug "Got ${rdocs?.size()} RembrandtedDoc(s)."
        rdocs_list?.each {rdoc ->
             Document doc = reader.createDocument(rdoc.doc_content)
             doc.tokenize()	
	
			rdocs[rdoc.doc_id] = ['lang':rdoc.lang, 'doc_original_id':rdoc.doc_original_id, nes:[]]
        			
			doc.titleNEs?.each{ne -> 
				
				String section = 'T'
				 
            	ne.classification?.each{cl -> 
	
					NECategory category = (cl.c? NECategory.getFromCategory(cl.c) : null)
                    NEType type = (cl.t? NEType.getFromType(cl.t) : null)
                    NESubtype subtype = (cl.s? NESubtype.getFromSubtype(cl.s) : null)

					Entity e = (ne.dbpediaPage.containsKey(cl) ? 
					   (ne.dbpediaPage[cl] instanceof List ? 
						 (!ne.dbpediaPage[cl].isEmpty() ? Entity.getFromDBpediaResource(ne.dbpediaPage[cl][0]) : null)
						: Entity.getFromDBpediaResource(ne.dbpediaPage[cl])
					    ) : null)
					
					if (!e) {
						// if it does not have an Entity, let's check if NE can help us
						if (!(cl.c == "@TEMPO" || cl.c == "@VALOR" || cl.c == "@NUMERO")) {
							NEName ne_name = NEName.getFromName(ne.printTerms())
							NE ne2 = NE.getFromNameAndLangAndClassificationAndNonNullEntity(ne_name, rdoc.lang, category, type, subtype)
							if (ne2 && ne2.ne_entity) e = ne2.ne_entity
						}
					}
			
					rdocs[rdoc.doc_id].nes << [section:section, sentence:ne.sentenceIndex,
                    term:ne.termIndex, name:ne.printTerms(), //neid:row2['ne_id'], 
					category:category, type:type, subtype:subtype, entity:e ]
				} 
			}
			doc.bodyNEs?.each{ne -> 
				
               	String section = 'B'
				 
            	ne.classification?.each{cl -> 
	
					NECategory category = (cl.c? NECategory.getFromCategory(cl.c) : null)
                    NEType type = (cl.t? NEType.getFromType(cl.t) : null)
                    NESubtype subtype = (cl.s? NESubtype.getFromSubtype(cl.s) : null)

					Entity e = (ne.dbpediaPage.containsKey(cl) ? 
					   (ne.dbpediaPage[cl] instanceof List ? 
						 (!ne.dbpediaPage[cl].isEmpty() ? Entity.getFromDBpediaResource(ne.dbpediaPage[cl][0]) : null)
						: Entity.getFromDBpediaResource(ne.dbpediaPage[cl])
					    ) : null)
					
					if (!e) {
						// if it does not have an Entity, let's check if NE can help us
						if (!(cl.c == "@TEMPO" || cl.c == "@VALOR" || cl.c == "@NUMERO")) {
							NEName ne_name = NEName.getFromName(ne.printTerms())
							NE ne2 = NE.getFromNameAndLangAndClassificationAndNonNullEntity(ne_name, rdoc.lang, category, type, subtype)
							if (ne2 && ne2.ne_entity) e = ne2.ne_entity
						}
					}
			
					rdocs[rdoc.doc_id].nes << [section:section, sentence:ne.sentenceIndex,
                    term:ne.termIndex, name:ne.printTerms(), //neid:row2['ne_id'], 
					category:category, type:type, subtype:subtype, entity:e ]
				} 
			}
		}
		return rdocs
	}

    /** Get a batch of RembrandtedDocs to sync FROM the NE pool.
     * This method is now threadsafe
     * @param batchsize The batchsize, default to 100
     * return A list of RembrandtedDocs
     */ 
	static List<RembrandtedDoc> getBatchDocsToSyncFromNEPool(Task task, Collection collection, 
		String process_signature, int batchSize = 30) {
	    List<RembrandtedDoc> l = []
		RembrandtedDoc r
	    
	    db.getDB().withTransaction{
		    log.info "Getting a set of $limit processable rembrandtedDocs to sync from NE pool"  

			def query = "SELECT HIGH_PRIORITY * FROM ${RembrandtedDoc.tablename} WHERE "+
			"doc_collection=? AND doc_proc IN "+
			DocStatus.whereConditionGoodToProcess()+" AND "+
			"doc_sync in "+DocStatus.whereConditionGoodToSyncFromNEPool()+
			" AND doc_id NOT IN (select job_doc_id from job where job_doc_type='"+job_doc_type_label+"' and "+
			"job_doc_edit NOT IN "+DocStatus.whereConditionUnlocked()+") LIMIT ${limit} FOR UPDATE"  // VERY IMPORTANT, the FOR UPDATE, it locks the table until the transaction is complete
		
			def params = [collection.col_id]
			log.trace query
			log.trace params
			
		    db.getDB().eachRow(query, params, {row ->  
				r = new RembrandtedDoc()
				r.doc_id = row['doc_id']
				r.doc_original_id = row['doc_original_id']
				r.doc_webstore = row['doc_webstore']
				if (row['doc_lang']) r.doc_lang = row['doc_lang']
				if (row['doc_date_created']) r.doc_date_created = row['doc_date_created'] // it's a java.sql.Timestamp, a subclass of Date
				if (row['doc_date_tagged']) r.doc_date_tagged = row['doc_date_tagged'] // it's a java.sql.Timestamp, a subclass of Date
				r.doc_proc = DocStatus.getFromValue(row['doc_proc'])			
				r.doc_sync = DocStatus.getFromValue(row['doc_sync'])			

				if (r.doc_webstore) r.doc_content = webstore.retrieve(r.doc_webstore)
			 	l << r
				
			// LET's create JOBS to mark the queue
			Job job = new Job(job_task:task, job_worker:process_signature, job_doc_type:job_doc_type_label,
			job_doc_id:r.doc_id, job_doc_edit:DocStatus.QUEUED, job_doc_edit_date:new Date())
			job.job_id = job.addThisToDB()
			r.doc_job = job
			
			})		
		}// with transaction	    
		return (l ? l : null) 		
	}
	
	/* there's a difference between entities = null and entity = [] - the last one says that I cheched the DB */
	/* it both updates the entities var, and returns it */
	List<Entity> getEntities() {
	   if (entities == null) {
	       List res = []
	       db.getDB().eachRow("SELECT die_entity FROM ${die_table} WHERE die_document=?",
		     [doc_id], {row -> res << Entity.getFromID(row[0])})
		   entities = res
	   }
	   return entities
	}
    
    public DocGeoSignature getGeographicSignature() {
            return DocGeoSignature.getFromID(doc_latest_geo_signature)
        }
        
    public DocTimeSignature getTimeSignature() {
            return DocTimeSignature.getFromID(doc_latest_time_signature)
        }
        
	/* there's a difference between tags = null and tags = [] - the last one says that I cheched the DB */
	/* it both updates the entities var, and returns it */
	List<Tag> getTags() {
	   if (tags == null) {
	       List res = []
	       db.getDB().eachRow("SELECT dtg_tag FROM ${dtg_table} WHERE dtg_document=?",
		     [doc_id], {row -> res << Tag.getFromID(row[0])})
		   tags = res
	   }
	   return tags
	}
	
 	

	public int changeProcStatusInDBto(DocStatus status) {	
		def res = db.getDB().executeUpdate("UPDATE ${RembrandtedDoc.tablename} SET doc_proc=? WHERE doc_id=?", 
			[status.text(), doc_id]) 
		log.debug "Wrote proc status ${status}(${status.text()}) to doc_id ${doc_id}, ${res} rows were changed."
		return res
	}

	public int changeSyncStatusInDBto(DocStatus status) {	
		def res = db.getDB().executeUpdate("UPDATE ${RembrandtedDoc.tablename} SET doc_sync=? WHERE doc_id=?", 
			[status.text(), doc_id]) 
		log.debug "Wrote sync status ${status}(${status.text()}) to doc_id ${doc_id}, ${res} rows were changed."
		return res
	}
	

	/**
	 * The IGNORE does not generate error for duplicate keys 
	 */
	public associateWithEntity(Entity entity) { 
	    db.getDB().executeInsert("INSERT IGNORE INTO ${die_table} VALUES(?,?)", 
		    [doc_id, entity.ent_id])
	}
    /**
     * The IGNORE does not generate error for duplicate keys 
     */
	public associateWithTag(Tag tag) { 
	    //println "going for $doc_id, ${tag.tag_id}"
	    db.getDB().executeInsert("INSERT IGNORE INTO ${dtg_table} VALUES(?,?)", 
		    [doc_id, tag.tag_id])
	}
    
    /**
     * Add a reference to the latest geo_signature for this document
     */
	static int addGeoSignatureIDtoDocID(long dgs_id, long doc_id) {
	    if (!dgs_id || !doc_id) return null
	    int res = db.getDB().executeUpdate("UPDATE ${RembrandtedDoc.tablename} SET doc_latest_geo_signature =? WHERE doc_id=?",
            [dgs_id, doc_id])
            return res
	}

    /**
     * Add a reference to the latest time_signature for this document
     */
    static int addTimeSignatureIDtoDocID(long dts_id, long doc_id) {
        if (!dts_id || !doc_id) return null
        int res = db.getDB().executeUpdate("UPDATE ${RembrandtedDoc.tablename} SET doc_latest_time_signature =? WHERE doc_id=?",
                [dts_id, doc_id])
        return res
    }
    
	/** Remove entries from doc_has_ne table. It will NOT erase entries on other tables.
	 *  This is required so that the document can be synced to the Saskia.
	 */
	public void removeDocHasNEsFromPool() {
	    def res = db.getDB().execute("DELETE FROM ${dhn_table} WHERE dhn_doc=?",[doc_id] )
	    log.debug "Deleting doc_has_ne for doc_id ${doc_id}, got result ${res}"
	}
	
	/** NE consistency methods
	  * These methods are an interface to handle the DB tables that contain NE info. 
	  * The Groovy objects are ListOfNE and NamedEntity - no need to create new ones. 
	  */
	public addNEsToSaskia(ListOfNE NEs, String title_or_body, String doc_lang) {
	
	    def newid // temp variable to be used for auto-incremented numbers on new DB insertions
		
	// first: let's trigger a cache for NE category, type and subtype
	    NECategory.createCache()
	    NEType.createCache()
	    NESubtype.createCache()
			
	// add categories, types and subtypes, while NEs are being read	
	    NEs.each{ne -> 

	    	NEName nen = null
	    	//long rel_id = relations[Relation.default_relation]
	    	List<NE> nelist = []
	    	List<Entity> entitylist = []
			
		// Check categories
	    	ne.classification.each{cl -> 
	    		
                // TODO: we're using RAW c, t and s. 
	    	    if (cl?.c && !NECategory.all_category_id.containsKey(cl.c)) {
	    		log.trace "Adding a new category: ${cl.c}"
	    		NECategory nec = new NECategory(nec_category:cl.c)
	    		nec.nec_id = nec.addThisToDB()
	    		log.trace "Category  ${cl.c} has a new id: ${nec.nec_id}"                                 
	    	    }
                
	    	    if (cl?.t && !NEType.all_type_id.containsKey(cl.t)) {
	    		log.trace "Adding a new type: ${cl.t}"
	    		NEType net = new NEType(net_type:cl.t)
	    		net.net_id = net.addThisToDB()
	    		log.trace "Type ${cl.t} has a new id: ${net.net_id}"                      
	    	    }
	    	    if (cl?.s && !NESubtype.all_subtype_id.containsKey(cl.s)) {
	    		log.trace "Adding a new subtype: ${cl.s}"
	    		NESubtype nes = new NESubtype(nes_subtype:cl.s)
	    		nes.nes_id = nes.addThisToDB()
	    		log.trace "Subtype ${cl.s} has a new id: ${nes.nes_id}"    		
	    	    }
	    	}
	
	// get NE name. There's a cache underneath
	nen = NEName.getFromName(ne.printTerms())
	if (!nen) {
	    log.trace "no NEName entry '${ne.printTerms()}' found on DB. Creating a new entry."
	    nen = new NEName(nen_name:ne.printTerms(), nen_nr_terms:ne.terms.size())	
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
            if (c?.c) nec = NECategory.getFromCategory(c.c)
            NEType net = null
            if (c?.t) net = NEType.getFromType(c.t)
            NESubtype nes = null
            if (c?.s) nes = NESubtype.getFromSubtype(c.s)

            def resources = ne.dbpediaPage[c]
            if (resources && resources.size() > 2) 
        	log.warn("Note: NE $ne for classification $c has more than one DBpedia resource: $resources. Going to use the first")

            Entity e = null
            
            resources?.each{resource -> 
                e = Entity.getFromDBpediaResource(DBpediaResource.getShortName(resource))
                if (!e) {
                    log.trace "no DBpedia entry ${resource} on DB. Creating a new entry."
                    // let's get a classification.
                    List listOfClasses = dbpedia.getDBpediaOntologyClassFromDBpediaResource(resource)
                    log.trace "Classifying DBpedia resource $resource generated classes ${listOfClasses}" 
                    log.trace "Narrower one: "+dbpediaontology.getNarrowerClassFrom(listOfClasses) 
                    e = new Entity(
                	ent_dbpedia_resource:DBpediaResource.getShortName(resource),
                	ent_dbpedia_class:dbpediaontology.getNarrowerClassFrom(listOfClasses)
                    )
                    e.ent_id = e.addThisToDB()
                }             
            }  // each resource     
         
            //ne2 is not really a NE, it's a NE from DB
            log.trace "Searching for NEs with name $nen, lang $doc_lang, class ${nec}-${net}-${nes}, entity $e"
            NE ne2 = NE.getFromNameAndLangAndClassificationAndEntity(nen, doc_lang, nec, net, nes, e)
            
            if (!ne2) {               
              log.trace "Not found, creating new NE ne_name=${nen.nen_name}, lang ${doc_lang}, ${nec}, ${net}, ${nes} and entity ${e}."
              ne2 = NE.addThisToDB(nen.nen_id, doc_lang, nec?.nec_id, net?.net_id, nes?.nes_id, e?.ent_id)   
	    } else {
		log.trace "Found NE ${ne2} with id ${ne2.ne_id}"
            }
        
            newid = db.getDB().executeInsert("INSERT INTO ${dhn_table} VALUES(0,?,?,?,?,?)", 
            [doc_id, ne2.ne_id, title_or_body, ne.sentenceIndex, ne.termIndex])
            log.debug "Inserted doc_has_ne for doc.id ${doc_id}, ne ${ne2}, got ${newid}"
            
        }// each classification
        
	}// each NE

    }// method
    
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
			if (doc_content) {
				String key = webstore.store(doc_content, SaskiaWebstore.VOLUME_RDOC)
				log.trace "Got content $doc_content, wrote to Webstore RDOC, got key $key"

				def res = db.getDB().executeInsert("INSERT INTO ${RembrandtedDoc.tablename}"+
				"(doc_original_id, doc_collection, doc_webstore, doc_version, doc_lang, "+
				"doc_date_created, doc_date_tagged) VALUES(?,?,?,?,?,?,NOW())", 
				[doc_original_id, doc_collection.col_id, key, doc_version, doc_lang, doc_date_created]) 
				doc_id = (long) res[0][0]
				doc_webstore = key     
				log.info "Inserted RembrandtedDoc into DB: ${this}"
			} else {
				log.error "Did NOT inserted RembrandtedDoc into DB: doc_content for {$this} is empty!"
			}
		} catch (Exception e) {
			log.error "Did NOT inserted RembrandtedDoc into DB: ${e.getMessage()}"
		}
		return doc_id
	}
	
	public Long replaceThisToDB() {
		def res
		try {
			if (doc_content) {
				String key = webstore.store(doc_content, SaskiaWebstore.VOLUME_RDOC)
				log.trace "Got content $doc_content, wrote to Webstore RDOC, got key $key"

 				res = db.getDB().executeUpdate("UPDATE ${RembrandtedDoc.tablename} SET "+
				"doc_original_id=?, doc_collection=?, doc_webstore=?, doc_lang=?, "+
				"doc_date_tagged=NOW(), doc_version=doc_version+1 WHERE doc_id=? ", 
				[doc_original_id, doc_collection.col_id, key, doc_lang, doc_id]) 
			  	doc_version++
				log.info "Replaced RembrandtedDoc into DB: ${this}"
			} else {
				log.error "Did NOT replaced RembrandtedDoc into DB: doc_content for {$this} is empty!"
			}
		} catch (Exception e) {
			log.error "Did NOT replaced RembrandtedDoc into DB: ${e.getMessage()}"
		}
		return doc_id
	}
	
	public int removeThisFromDB() {	
		if (!doc_id) return null
	   def res = db.getDB().executeUpdate(
		"DELETE FROM ${RembrandtedDoc.tablename} where doc_id=?",[doc_id]) 
		log.info "Removed RembrandtedDoc ${this} from DB, got $res"
		return res	    
	}
		
	public String toString() {
		return "RDoc(${doc_id});DocOriginalId(${doc_original_id});Webstore(${doc_webstore});Lang(${doc_lang})"
	}
}
