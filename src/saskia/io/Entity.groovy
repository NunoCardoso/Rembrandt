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

import saskia.bin.Configuration
import saskia.dbpedia.DBpediaResource
import org.apache.log4j.Logger

/** This class is an interface for the Entity table in the Saskia database. 
  * It stores information that grounds an entity.
  * Static methods are used to return results from DB, using where clauses.
  * Class methods are used to insert results to DB.  
  */
class Entity extends DBObject implements JSONable {

    static String ent_table = "entity"
    static String ent_has_geo_table = "entity_has_geoscope"    
    
    Long ent_id
    String ent_name
    String ent_dbpedia_resource
    String ent_dbpedia_class
    
    static Configuration conf = Configuration.newInstance()
	
    static LinkedHashMap<String,Entity> entityDBPediaResourceCache = \
        new LinkedHashMap(conf.getInt("saskia.entity.cache.number",1000), 0.75f, true) // true: access order.  
    static LinkedHashMap<Long,Entity> entityIDCache = \
        new LinkedHashMap(conf.getInt("saskia.entity.cache.number",1000), 0.75f, true) // true: access order.  

    static SaskiaDB db = SaskiaDB.newInstance()
    static Logger log = Logger.getLogger("Entity")
	
    static Map type = ['ent_id':'Long', 'ent_name':'String', 
            'ent_dbpedia_resource':'String', 'ent_dbpedia_class':'String'] 
           
    static List<Entity> queryDB(String query, List params = []) {
		List<Entity> res = []
		Entity e
		db.getDB().eachRow(query, params, {row -> 
	    	e = new Entity()
	    	e.ent_id=row['ent_id']
	    	e.ent_name = row['ent_name']
	    	e.ent_dbpedia_resource = row['ent_dbpedia_resource']
	    	e.ent_dbpedia_class=row['ent_dbpedia_class']
	    	res << e
		})
		return (res ? res : null)
    }
	
	public Map toMap() {
	    return ["ent_id":ent_id, "ent_name":ent_name, "ent_dbpedia_resource":ent_dbpedia_resource, 
	      "ent_dbpedia_class":ent_dbpedia_class]
	}
	
	public Map toSimpleMap() {
	    return toMap()
	}
	
	public Map getName() {
			if (!ent_name) return null
			Map m = [:]
			ent_name.split(/;/).each{it -> 
				List l = it.split(/:/)
				m[l[0]]=l[1]
			}
			return m
	}
	
	static Map listEntities(limit = 10, offset = 0, column = null, needle = null) {
	// limit & offset can come as null... they ARE initialized...
		if (!limit) limit = 10
		if (!offset) offset = 0
		
		String where = ""
		String from = " FROM ${ent_table}"	
		List params = []	
		if (column && needle) {
			switch (type[column]) {
				case 'String': where += " WHERE $column LIKE '%${needle}%'"; break
				case 'Long': where += " WHERE $column=? "; params << Long.parseLong(needle); break
			}
		}
	    
		String query = "SELECT SQL_CALC_FOUND_ROWS ${ent_table}.* $from $where LIMIT ${limit} OFFSET ${offset} "+
		"UNION SELECT CAST(FOUND_ROWS() as SIGNED INT), NULL, NULL, NULL"

		List<Entity> u 
		try {u = queryDB(query, params) }
		catch(Exception e) {log.error "Error getting Entity list: ", e}
		// last "item" it's the count.
		int total = (int)(u.pop().ent_id)
		log.debug "Returning "+u.size()+" results."
		return ["total":total, "offset":offset, "limit":limit, "page":u.size(), "result":u,
			"column":column, "value":needle]
	}
	
	/** Get an entity from id. 
	 * @param ent_id The id as needle.
	 * return the Entity for that id.
	 */
	static Entity getFromID(Long ent_id) {
		if (!ent_id) return null
		if (entityIDCache.containsKey(ent_id)) return entityIDCache[ent_id]
		
		List<Entity> e = queryDB("SELECT * FROM ${ent_table} WHERE ent_id=?", [ent_id])
		log.trace "Querying for ent_id $ent_id got Entity $e." 
		if (e) {
			entityIDCache[ent_id] = e[0]
			return e[0] 
		}
		return null 
	}	 
	
	static updateValue(Long ent_id, column, value) {
	    Entity ent = Entity.getFromID(ent_id)
	    if (!ent) return -1
	    return ent.updateValue(column, value)
	}
	
	public updateValue(column, value) {
	    def newvalue	    
	    switch (type[column]) {
	        case 'String': newvalue = value; break
	        case 'Long': newvalue = Long.parseLong(value); break
	    }
	    def res = db.getDB().executeUpdate("UPDATE ${ent_table} SET ${column}=? WHERE ent_id=?",[newvalue, ent_id])
	    if (ent_dbpedia_resource) entityDBPediaResourceCache[ent_dbpedia_resource][column] = newvalue
	    entityIDCache[ent_id][column] = newvalue	 
	    return res
	}
	
	/** Get from Wikipedia URL. 
	 * @param ent_wikipedia_page The full wikipedia URL.
	 * return the Entity associated to that URL
	 */
	
	static List<Entity> getFromName(String ent_name, String lang) {
		if (!ent_name || !lang) return null 
		String needle = "${lang}:${ent_name}"
		List<Entity> ents = queryDB("SELECT * FROM ${ent_table} WHERE ent_name REGEXP '^(.*;)?${needle}(;.*)?\$'",[])
		log.info "Querying for ent_name $needle got Entity $ents." 
		return ents
	}	
		
	/** Get from DBpedia resource. 
	 * This USES cache
	 * @param ent_dbpedia_resource The DBpedia resource.
	 * return the Entity associated to that resource.
	 */
	static Entity getFromDBpediaResource(String ent_dbpedia_resource) {
		// dbpedia_url can be null	    	
	    	List<Entity> e
            
	    	if (entityDBPediaResourceCache.containsKey(ent_dbpedia_resource)) return entityDBPediaResourceCache[ent_dbpedia_resource]
            
		if (!ent_dbpedia_resource) {
		    e = queryDB("SELECT * FROM ${ent_table} WHERE ent_dbpedia_resource IS NULL")
		}else  {
		   ent_dbpedia_resource = DBpediaResource.getShortName(ent_dbpedia_resource)  
		   e = queryDB("SELECT * FROM ${ent_table} WHERE ent_dbpedia_resource = ?", [ent_dbpedia_resource])
		}
	    	
		log.debug "Querying for ent_dbpedia_resource $ent_dbpedia_resource got Entity $e." 
		if (e) {
		    entityDBPediaResourceCache[ent_dbpedia_resource] = e[0]
		    return e[0]
		}
		return null
		
	}
	
	/** Get from DBpedia class. 
	 * @param dbpedia_url The DBpedia class.
	 * return the Entity associated to that resource.
	 */
	static List<Entity> getFromDBpediaClass(String ent_dbpedia_class) {
		// dbpedia_url can be null
		List<Entity> e
		if (!ent_dbpedia_class) 
		    e = queryDB("SELECT * FROM ${ent_table} WHERE ent_dbpedia_class IS NULL")
		else 
		   e = queryDB("SELECT * FROM ${ent_table} WHERE ent_dbpedia_class = ?", [ent_dbpedia_class])
		log.trace "Querying for ent_dbpedia_class $ent_dbpedia_class got Entity $e." 
		if (e) return e else return null
	}
    
	public Geoscope hasGeoscope() {
      Geoscope g
     	db.getDB().eachRow("SELECT * FROM ${ent_has_geo_table} WHERE ehg_entity = ?", [ent_id], {row -> 
         if (g) log.warn "Entity $ent has more than one link to a geoscope!!!"
          else g = Geoscope.getFromID(row["ehg_geoscope"])
        })
      return g
	}
     
	public associateWithGeoscope(long geo_id) { 
       db.getDB().executeInsert("INSERT IGNORE INTO ${ent_has_geo_table} VALUES(?,?)", 
       [ent_id, geo_id])
	}
        
	/** Add this Entity to the database. Note that a null is a valid insertion...
	 * return 1 if successfully inserted.
	 */	
	public Long addThisToDB() {
	    def res = db.getDB().executeInsert("INSERT INTO ${ent_table} VALUES(0,?,?,?)", 
	    [ent_name, ent_dbpedia_resource, ent_dbpedia_class] )

	    ent_id = (long)res[0][0]
	    entityDBPediaResourceCache[ent_dbpedia_resource] = this
	    entityIDCache[ent_id] = this
		 log.info "Adding entity to DB: ${this}"
	    return ent_id
	}	

   static int removeEntity(Long id) {
		Entity e = Entity.getFromID(id)
		return e?.removeThisFromDB()
   }
	
   public int removeThisFromDB() {
		if (!ent_id) return null
		def res = db.getDB().executeUpdate("DELETE FROM ${ent_table} WHERE ent_id=?", [ent_id])
		entityDBPediaResourceCache.remove(ent_dbpedia_resource)
		entityIDCache.remove(ent_id)
		log.info "Removing entity ${this} from DB, got $res"
		return res	    
   }
		
	boolean equals(Entity e) {
		return this.toMap().equals(e.toMap())
	}
	
	public String toString() {
		return "${ent_id}:${ent_dbpedia_resource}:${ent_dbpedia_class}"
	}
}