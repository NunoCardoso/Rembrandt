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

import java.util.LinkedHashMap;

import org.apache.log4j.Logger

import saskia.bin.Configuration;

/**
 * @author Nuno Cardoso
 * This is an interface for doc_time_signature table, and also for 
 * GeoSignature generation
 *
 */
class DocTimeSignature {
    
    static String dts_table = "doc_time_signature"
    long dts_id
    long dts_document
    String dts_document_original_id
    String dts_signature
    Tag dts_tag
    Date dts_date_created
    
    static Configuration conf = Configuration.newInstance()
    static SaskiaDB db = SaskiaDB.newInstance()
    static Logger log = Logger.getLogger("SaskiaDB")
    static LinkedHashMap<Long,DocTimeSignature> idCache = \
           new LinkedHashMap(conf.getInt("saskia.doc_time_signature.cache.number",1000), 0.75f, true) // true: access order.  

    static List<DocTimeSignature> queryDB(String query, ArrayList params = []) {
        List<DocTimeSignature> res = []
        DocTimeSignature g
        db.getDB().eachRow(query, params, {row  -> 
            g = new DocTimeSignature()
            g.dts_id = row['dts_id']
            g.dts_document = row['dts_document']
            if (row['dts_signature']) g.dts_signature = row['dts_signature']            
            if (row['dts_tag']) g.dts_tag = Tag.getFromID(row['dts_tag'])
            if (row['dts_date_created']) g.dts_date_created = (Date)row['dts_date_created']
            if (g.dts_id) res << g
        })
        return res
    }


    static DocTimeSignature getFromID(long dts_id) {
        if (!dts_id) return null
        if (idCache.containsKey(dts_id)) return idCache[dts_id]
        List<DocTimeSignature> dts = queryDB("SELECT * FROM ${dts_table} WHERE dts_id=?", [dts_id])
        log.debug "Querying for dts_id $dts_id got DocTimeSignature $dts." 
        if (dts) {
            idCache[dts_id] = dts[0]
            return dts[0] 
        }
        return null
    }	
    
    static List<DocTimeSignature> getBatchOfTimeSignatures(Collection collection, limit = 10,  offset = 0) {
        // limit & offset can come as null... they ARE initialized...
        if (!limit) limit = 10
        if (!offset) offset = 0
        
        // ORDER BY doc_id ASC ensures that the TimeSignatures are batched just like in other indexes, 
        // to ensure Lucene gets identical indexes for identical documents
        return queryDB("SELECT ${dts_table}.*, ${RembrandtedDoc.doc_table}.doc_original_id "+
        "FROM ${dts_table},  ${RembrandtedDoc.doc_table}, ${RembrandtedDoc.chd_table} "+
        "WHERE chd_collection=? AND chd_document=dts_document AND chd_document=doc_id "+
        "ORDER BY doc_id ASC LIMIT $limit OFFSET $offset",  [collection.col_id])    
        
        
    }
    
    public long addThisToDB() {	
        
        def res = db.getDB().executeInsert("INSERT INTO ${dts_table}(dts_document, " +
            "dts_signature, dts_tag, dts_date_created) VALUES(?,?,?, NOW())", 
        [dts_document, dts_signature, dts_tag.tag_id])
        long new_dts_id = (long)res[0][0]
        log.debug "Inserted new DocTimeSignature for doc $dts_document, got new_dts_id $new_dts_id"        
        idCache[new_dts_id] = this
        return new_dts_id
    }

    public String toString() {
        return "${dts_id}:${dts_document}"
    }
}