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
import saskia.bin.Configuration

/** This class is an interface for the NEName table in the WikiRembrandt database. 
  * It stores tagging information associated to a NE name.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  */
class NE {

	static String ne_table = "ne"
	Long ne_id
	NEName ne_name
	String ne_lang
	NECategory ne_category
	NEType ne_type
	NESubtype ne_subtype
	Entity ne_entity
	
	static SaskiaDB db = SaskiaDB.newInstance()
	static Logger log = Logger.getLogger("SaskiaDB")

	static Configuration conf = Configuration.newInstance()
	
	static Map type = ['ne_id':'Long', 'ne_name':'NEName', 'ne_lang':'String',
           'ne_category':'NECategory', 'ne_type':'NEType', 'ne_subtype':'NESubtype',
           'ne_entity':'Entity'] 
    
             	  
	// used by getFromNameAndLangAndClassificationAndEntity
	static LinkedHashMap<Long,NE> neKeyCache = \
           new LinkedHashMap(conf.getInt("saskia.ne.cache.number",1000), 0.75f, true) // true: access order.  

	static List<NE> queryDB(String query, ArrayList params = []) {
	    List<NE> res = []
	    NE n 
	    db.getDB().eachRow(query, params, {row  -> 
	    	n = new NE()
	        n.ne_id = row['ne_id'] 
	        if (row['ne_name']) n.ne_name = NEName.getFromID(row['ne_name'])
	        n.ne_lang = row['ne_lang']
	        if (row['ne_category']) n.ne_category = NECategory.getFromID(row['ne_category'])
	        if (row['ne_type']) n.ne_type = NEType.getFromID(row['ne_type'])
	        if (row['ne_subtype']) n.ne_subtype = NESubtype.getFromID(row['ne_subtype'])
	        if (row['ne_entity']) n.ne_entity = Entity.getFromID(row['ne_entity'])
                res << n
	   })
	   return (res ? res : null)
	}
		
	static Map listNEs(limit = 10, offset = 0, column = null, needle = null) {
	    // limit & offset can come as null... they ARE initialized...
	    if (!limit) limit = 10
	    if (!offset) offset = 0
		
	    String where = ""
	    String from = " FROM ${ne_table}"	
	    List params = []	
	    if (column && needle) {
		switch (type[column]) {
	/*	['ne_id':'Long', 'ne_name':'NEName', 'ne_lang':'String',
            	  'ne_category':'NECategory', 'ne_type':'NEType', 'ne_subtype':'NESubtype',
            	  'ne_entity':'Entity'] 
          */  	  
		   case 'String': where += " WHERE $column LIKE '%${needle}%'"; break
		   case 'Long': where += " WHERE $column=? "; params << Long.parseLong(needle); break
		   case 'NEName':  
		       from += ", ${NEName.nen_table}"; 
		       where += " WHERE $column=nen_id AND nen_name LIKE '%${needle}%'"; 
		   break
		   case 'NECategory':  
		       from += ", ${NECategory.nec_table}"; 
		       where += " WHERE $column=nec_id AND nec_category LIKE '%${needle}%'"; 
		   break
		   case 'NEType':  
		       from += ", ${NEType.net_table}"; 
		       where += " WHERE $column=net_id AND net_type LIKE '%${needle}%'"; 
		   break
		   case 'NESubtype':  
		       from += ", ${NESubtype.nes_table}"; 
		       where += " WHERE $column=nes_id AND nes_subtype LIKE '%${needle}%'"; 
		   break
		   case 'Entity':  
		       from += ", ${Entity.ent_table}"; 
		       where += " WHERE $column=ent_id AND ent_dbpedia_resource LIKE '%${needle}%'"; 
		   break
		}
	    }
	    
	    String query = "SELECT SQL_CALC_FOUND_ROWS ${ne_table}.* $from $where LIMIT ${limit} OFFSET ${offset} "+
	    "UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), NULL, '', NULL, NULL, NULL, NULL"
	    //log.debug "query = $query params = $params class = "+params*.class
	    List<NE> u 
	    try {u = queryDB(query, params) }
	    catch(Exception e) {log.error "Error getting NE list: ", e}
	    // last "user" is not the user... it's the count.
	    NE fake_ne = u.pop()
	    int total = (int)fake_ne.ne_id
	    log.debug "Returning "+u.size()+" results."
	    return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
		    "column":column, "value":needle]
	}
	 
	
	public Map toMap() {
	    return ["ne_id":ne_id, 
	            "ne_name":ne_name?.toMap(),
	            "ne_lang":ne_lang, 
	            "ne_category":ne_category?.toMap(), 
	            "ne_type":ne_type?.toMap(),
	            "ne_subtype":ne_subtype?.toMap(),
	            "ne_entity":ne_entity?.toMap()]
	}
	
	/** Get a NE from id.
	 * This has no cache, it's used by 
	 * NEMapping.groovy
	 * RenderNEDetailsStats.groovy
	 * 
	 * @param id The id as needle.
	 * return the NEName result object, or null
	 */
	static NE getFromID(long ne_id) {
		if (!ne_id) return null
		List<NE> ne = queryDB("SELECT * FROM ${ne_table} WHERE ne_id=?", [ne_id])
		log.debug "Querying for ne_id $ne_id got NE $ne." 
		if (ne) return ne[0] else return null
	}	
	
	 
	static List<NE> getFromEntity(Entity ent) {
	    if (!(ent?.ent_id)) return null
		List<NE> nes = queryDB("SELECT * FROM ${ne_table} WHERE ne_entity=?", [ent.ent_id])
	    log.trace "Querying for ne_entity ${ent.ent_id} got NEs $nes" 
	    return nes
	}	
	
	
	static int deleteNE(long ne_id) {
	    NE ne = NE.getFromID(ne_id)
	    return ne.deleteNE()
	}
	
	public int deleteNE() {
	    if (!ne_id) return null
	    def res = db.getDB().executeUpdate("DELETE FROM ${ne_table} WHERE ne_id=?", [ne_id])
	    neKeyCache.remove(getKey()) 
	    return res	    
	}
	
	/** Get a NE from name and language.
	 * This has no cache, since it's only used by ImportSelectedWikipediaDocument2SourceDocument.groovy
	 * 
	 * @param name NE string name
	 * @param lang NE language
	 * return the NE object, or null
	 */
	static List<NE> getFromNameAndLang(String name, String lang) {
	    if (!name || !lang)  return null
	    List<NE> ne = queryDB("SELECT * FROM ${ne_table}, ${NEName.nen_table} WHERE ne_name=nen_id and "+
		    "nen_name=? and ne_lang=?", [name, lang])
            log.debug "Querying for ne_name $name and ne_lang $lang got NE $ne." 
	    return ne
       }	
	
	// generate a hash key for cache map
	public String getKey() {
	    return "${ne_name?.nen_id} $ne_lang ${ne_category?.nec_id} ${ne_type?.net_id} ${ne_subtype?.nes_id} ${ne_entity?.ent_id}"
	}
    
	/** 
	 * This function DOES use the cache, it's the main function used by RembrandtedDoc 
	 * on syncing a tagged document to NE Pool 
	 */
       static NE getFromNameAndLangAndClassificationAndEntity(NEName nen, String lang, 
           NECategory nec, NEType net, NESubtype nes, Entity ent) {
	   		if (!nen) {
	       		log.warn "getFromNameAndLangAndClassificationAndEntity: tried a query with empty NEName!"
	       		return null
	   		}
    
           String key = "${nen?.nen_id} $lang ${nec?.nec_id} ${net?.net_id} ${nes?.nes_id} ${ent?.ent_id}" 
	   		if (neKeyCache.containsKey(key)) return neKeyCache[key] 
        
	   		String nec_string, net_string, nes_string, ent_string
	   		List params = [nen.nen_id, lang] 
   
	   		if (nec == null) { nec_string = "IS NULL"}
	   		else {nec_string = "=?"; params << nec.nec_id}
           if (net == null) { net_string = "IS NULL"}
           else {net_string = "=?"; params << net.net_id}
           if (nes == null) { nes_string = "IS NULL"}
           else {nes_string = "=?"; params << nes.nes_id} 	              
           if (ent == null) { ent_string = "IS NULL"}
           else {ent_string = "=?"; params << ent.ent_id}              
        
           List<NE> ne = queryDB("SELECT * FROM ${ne_table} WHERE ne_name=? and ne_lang=? and "+
           "ne_category ${nec_string} AND ne_type ${net_string} AND ne_subtype ${nes_string}"+
            " AND ne_entity ${ent_string}", params)
           if (ne) {
               neKeyCache[key] = ne[0]
               return ne[0]
           }
	   	    return null
        }
    
	/** 
	 * This function DOES NOT use the cache
	 */
       static NE getFromNameAndLangAndClassificationAndNonNullEntity(NEName nen, String lang, 
           NECategory nec, NEType net, NESubtype nes) {
	   		if (!nen) {
	       		log.warn "getFromNameAndLangAndClassificationAndEntity: tried a query with empty NEName!"
	       		return null
	   		}
    
	   		String nec_string, net_string, nes_string
	   		List params = [nen.nen_id, lang] 
   
	   		if (nec == null) { nec_string = "IS NULL"}
	   		else {nec_string = "=?"; params << nec.nec_id}
           if (net == null) { net_string = "IS NULL"}
           else {net_string = "=?"; params << net.net_id}
           if (nes == null) { nes_string = "IS NULL"}
           else {nes_string = "=?"; params << nes.nes_id} 	              
          
           List<NE> ne = queryDB("SELECT * FROM ${ne_table} WHERE ne_name=? and ne_lang=? and "+
           "ne_category ${nec_string} AND ne_type ${net_string} AND ne_subtype ${nes_string}"+
            " AND ne_entity IS NOT NULL", params)

           if (ne) {
           	   String key = "${ne[0].ne_name.nen_id} ${ne[0].ne_lang} ${ne[0].ne_category?.nec_id} ${ne[0].ne_type?.net_id} "+
				"${ne[0].ne_subtype?.nes_id} ${ne[0].ne_entity?.ent_id}" 
               if (!neKeyCache.containsKey(key)) neKeyCache[key] = ne[0]
               return ne[0]
           }
	   	    return null
        }
       
       static NE addThisToDB(Long nen_id, String lang, Long nec_id, Long net_id, Long nes_id, Long ent_id) {
        
	   		NE ne = new NE()

	   		ne.ne_name = NEName.getFromID(nen_id)
	   		ne.ne_lang = lang
	   		ne.ne_category = (nec_id ? NECategory.getFromID(nec_id) : null)
	   		ne.ne_type = (net_id ? NEType.getFromID(net_id) : null)
	   		ne.ne_subtype = (nes_id ? NESubtype.getFromID(nes_id) : null)
	   		ne.ne_entity = (ent_id ? Entity.getFromID(ent_id) : null)
    			ne.ne_id = ne.addThisToDB()
				return ne
			}
			
			public Long addThisToDB() {
	   		def res = db.getDB().executeInsert(
            "INSERT INTO ${ne_table}(ne_id, ne_name, ne_lang, ne_category, "+
            "ne_type, ne_subtype, ne_entity) VALUES(0,?,?,?,?,?,?)", 
            [ne_name.nen_id, ne_lang, ne_category.nec_id, ne_type?.net_id, ne_subtype?.nes_id, ne_entity?.ent_id])	

            ne_id = (long)res[0][0]
            neKeyCache[getKey()] = this
            return ne_id
    		}
       
       public updateNEName(NEName new_ne_name) {
	   println "updateNEName: replacing $ne_name into $new_ne_name for ne_id $ne_id" 
	   if (!new_ne_name || !ne_id) return null
	   def res = db.getDB().executeUpdate(
	      "UPDATE ${ne_table} SET ne_name=? where ne_id=?",[new_ne_name.nen_id, ne_id]) 
	      
	   if (res) {
	       ne_name = new_ne_name
	       String key = getKey()
	       if (neKeyCache.containsKey(key)) neKeyCache[key].ne_name = new_ne_name			       
	   }
	   return res
       }
	
       public updateNECategory(NECategory new_ne_category) {
	   if (!ne_id) return null
	   // new_ne_category can be null
	   def res = db.getDB().executeUpdate(
	      "UPDATE ${ne_table} SET ne_category=? where ne_id=?",[new_ne_category?.nec_id, ne_id]) 
	   if (res) {
	       ne_category = new_ne_category
	       String key = getKey()
	       if (neKeyCache.containsKey(key))  neKeyCache[key].ne_category = new_ne_category		
	       
	   }
	   return res
       }
	
       public updateNEType(NECategory new_ne_type) {
	   if (!ne_id) return null
	   // new_ne_category can be null
	   def res = db.getDB().executeUpdate(
	      "UPDATE ${ne_table} SET ne_type=? where ne_id=?",[new_ne_type?.net_id, ne_id]) 
	   if (res) {
	       ne_type = new_ne_type
	       String key = getKey()
	       if (neKeyCache.containsKey(key))  neKeyCache[key].ne_type = new_ne_type		
	       
	   }
	   return res
       }
       
       public updateNESubtype(NECategory new_ne_subtype) {
	   if (!ne_id) return null
	   // new_ne_category can be null
	   def res = db.getDB().executeUpdate(
	      "UPDATE ${ne_table} SET ne_subtype=? where ne_id=?",[new_ne_subtype?.nes_id, ne_id]) 
	   if (res) {
	       ne_subtype = new_ne_subtype
	       String key = getKey()
	       if (neKeyCache.containsKey(key))  neKeyCache[key].ne_subtype = new_ne_subtype		
	       
	   }
	   return res
       }
       
       public updateEntity(Entity new_entity) {
	   if (!ne_id) return null
	   // new_entity can be null
	   def res = db.getDB().executeUpdate(
	      "UPDATE ${ne_table} SET ne_entity=? where ne_id=?",[new_entity?.ent_id, ne_id]) 
	   if (res) {
	       ne_entity = new_entity
	       String key = getKey()
	       if (neKeyCache.containsKey(key))  neKeyCache[key].ne_entity = new_entity		
	       
	   }
	   return res
       }
       
 	static NE getFromNameAndDocSentenceTermAndClassification(
		   Collection collection, String nename, int s, int t, 
		   String c1, String c2, String c3) {
		log.debug "Querying for $collection, $nename, $s, $t, $c1, $c2, $c3"
		def c1q, c2q, c3q
		List args = [collection.col_id, s, t, nename]
		if (!c1 || c1 == "undefined" || c1 == "null") {c1 = null; c1q=" IS NULL"} else {args << c1; c1q="=?"}
		if (!c2 || c2 == "undefined" || c2 == "null") {c2 = null; c2q=" IS NULL"} else {args << c2; c2q="=?"}
		if (!c3 || c3 == "undefined" || c3 == "null") {c3 = null; c3q=" IS NULL"} else {args << c3; c3q="=?"}
		
		String query = "SELECT * FROM ne, ne_name, ne_category, ne_type, ne_subtype, "+
		"doc_has_ne, doc WHERE doc_collection=? and doc_id=dhn_doc and "+
		"dhn_sentence=? AND dhn_term=? AND dhn_ne=ne_id AND ne_name=nen_id AND "+
		"nen_name=? and ne_category=nec_id and ne_type=net_id and ne_subtype=nes_id AND "+
		"nec_category$c1q and net_type$c2q and nes_subtype$c3q"
	    List<NE> ne = queryDB(query, args)
	    return (ne ? ne : null)
        }
	
	public String toString() {
		return ""+ne_id+":"+ne_name
	}
}