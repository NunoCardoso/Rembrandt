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

import org.apache.log4j.Logger
import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * This is an interface for doc_geo_signature table, and also for 
 * GeoSignature generation
 *
 */
class DocGeoSignature {
    
    static String dgs_table = "doc_geo_signature"
    long dgs_id
    long dgs_document
    String dgs_document_original_id
    String dgs_signature
    Tag dgs_tag
    Date dgs_date_created
    
    static Configuration conf = Configuration.newInstance()
    static SaskiaDB db = SaskiaDB.newInstance()
    static Logger log = Logger.getLogger("SaskiaDB")
    static LinkedHashMap<Long,DocGeoSignature> idCache = \
           new LinkedHashMap(conf.getInt("saskia.doc_geo_signature.cache.number",1000), 0.75f, true) // true: access order.  

    static List<DocGeoSignature> queryDB(String query, ArrayList params = []) {
        List<DocGeoSignature> res = []
        DocGeoSignature g
        db.getDB().eachRow(query, params, {row  -> 
            g = new DocGeoSignature()
            g.dgs_id = row['dgs_id']
            g.dgs_document = row['dgs_document']
            if (row['dgs_signature']) g.dgs_signature = row['dgs_signature']            
            if (row['dgs_tag']) g.dgs_tag = Tag.getFromID(row['dgs_tag'])
            if (row['dgs_date_created']) g.dgs_date_created = (Date)row['dgs_date_created']
            if (g.dgs_id) res << g
        })
        return res
    }
    
    /** Get a Geoscope from id.
     * @param id The id as needle.
     * return the Geoscope result object, or null
     */
    static DocGeoSignature getFromID(long dgs_id) {
        if (!dgs_id) return null
        if (idCache.containsKey(dgs_id)) return idCache[dgs_id]
        List<DocGeoSignature> dgs = queryDB("SELECT * FROM ${dgs_table} WHERE dgs_id=?", [dgs_id])
        log.debug "Querying for dgs_id $dgs_id got DocGeoSignature $dgs." 
        if (dgs) {
           idCache[dgs_id] = dgs[0]
           return dgs[0] 
        }
        return null
    }	
    
    static List<DocGeoSignature> getBatchOfGeoSignatures(Collection collection, limit = 10,  offset = 0) {
        // limit & offset can come as null... they ARE initialized...
        if (!limit) limit = 10
        if (!offset) offset = 0 
        
        // it HAS TO FOLLOW RembrnadtedDoc order, so that LuceneIDs are the same! 
        
       
       // ORDER BY doc_id ASC ensures that the GeoSignatures are batched just like in other indexes, 
        // to ensure Lucene gets identical indexes for identical documents
         return queryDB("SELECT ${dgs_table}.*, ${RembrandtedDoc.doc_table}.doc_original_id "+
        "FROM ${dgs_table},  ${RembrandtedDoc.doc_table}, ${RembrandtedDoc.chd_table} "+
        "WHERE chd_collection=? AND chd_document=dgs_document AND chd_document=doc_id "+
        "ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id])      
    }
    
    public long addThisToDB() {	
        
        def res = db.getDB().executeInsert("INSERT INTO ${dgs_table}(dgs_document, " +
            "dgs_signature, dgs_tag, dgs_date_created) VALUES(?,?,?, NOW())", 
        [dgs_document, dgs_signature, dgs_tag.tag_id])
        long new_dgs_id = (long)res[0][0]
        log.debug "Inserted new DocGeoSignature for doc $dgs_document, got new_dgs_id $new_dgs_id"        
        idCache[new_dgs_id] = this
       return new_dgs_id
    }

    public String toString() {
        return "${dgs_id}:${dgs_document}"
    }
}