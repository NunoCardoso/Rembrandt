
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
package saskia.patches

import saskia.io.*
import org.apache.log4j.*
import org.apache.commons.cli.*


/**
 * @author Nuno Cardoso
 *
 */
class FixNE {
    
    static Logger log = Logger.getLogger("Patches")

    List l
    SaskiaDB db
    static LinkedHashMap<Long,Entity> cache = new LinkedHashMap(10000, 0.75f, true) 
    
    public FixNE(List l) {
	this.l = l     
        db = SaskiaDB.newInstance()
    }

     public doIt() {
        l.each{it ->  
            log.info "Going for ${it}"
            String nec_string, net_string, nes_string
            List params = [it.ne_name_id, it.lang] 
            Long min_ne_id = it.min_ne_id
            
            if (it.category_id == null) { nec_string = "IS NULL"}
            else {nec_string = "=?"; params << it.category_id}
            if (it.type_id == null) { net_string = "IS NULL"}
            else {net_string = "=?"; params << it.type_id}
            if (it.subtype_id == null) { nes_string = "IS NULL"}
            else {nes_string = "=?"; params << it.subtype_id} 	              
          
            List<NE> nes = NE.queryDB("SELECT * FROM ne WHERE ne_name=? and ne_lang=? and "+
                    "ne_category ${nec_string} AND ne_type ${net_string} AND ne_subtype ${nes_string}"
                    , params)

            if (nes.size() == 1) {
        	log.warn "Hey, got only one NE, $nes. this means it's already fixed. Skipping to the next one"
                return
            }

            nes.each{ne -> 
            if (ne.ne_name != null && ne.ne_entity != null)  {
               cache[ne.ne_name.nen_id] = ne.ne_entity              
               log.debug "Adding entity ${ne.ne_entity} to cache for name ${ne.ne_name.nen_name}"
               }
            }
            
            List<Long> neids = nes*.ne_id
            //log.debug "Got nes, ids = ${neids}"         
            
            if (nes.size() == it.count) {
        	log.debug "${nes.size()} == ${it.count}, size matches."
            } else {
        	log.error "${nes.size()} != ${it.count}, what's the problem?!"                       
            }
            
            // now let's remove the min_ne_id
            neids = neids - min_ne_id
            // verify that it has all ids != min_ne_id
           // assert neids.size() + 1 == it.count
            
            List<Entity> ent = nes*.ne_entity.unique()
            List ent_nulls = ent.findAll{it == null}
            List ent_not_nulls = ent.findAll{it != null}
            log.debug "Got entities $ent"
                
            // if it's jut null, check cache.
            // the ne_name is the same for all NEs so there's no worry on gling for nes[0]
            Entity e = null
            
            if (ent == [null]) {
                log.debug "Got no entities in this NE list!"
                //log.debug "cache: $cache, searching for ${nes[0].ne_name.nen_id}"
                e = cache[nes[0].ne_name.nen_id]
                if (!e) {
                    log.debug "Nothing in caches from previous NEs, entity will still be null"
                } else {
                    log.debug "Got entity ${e} in cache of previous NEs. Will use it"
                }  
                
            } else {
                int number_of_entities =    ent_not_nulls.size()
                if (number_of_entities != 1) {
                    log.debug "There are ${ent_not_nulls} entities, != 1. Don't know what to do!" 
                } else {
                    log.debug "Good, there is a single entity ${ent_not_nulls}, will use it"
                    e = ent_not_nulls[0]
                }
            }
                
            // now it's time to update:
            //    1. min_ne_id  will have the entity_id
            //    2. doc_has_ne will have all dhn_nes != min_ne_id updated to min_ne_id
            //    3. in ne all ne_id != min_ne_id will be eliminated.
            String sql
            if (!e) sql = "UPDATE ne SET ne_entity=NULL WHERE ne_id=${min_ne_id}".toString()
            else sql = "UPDATE ne SET ne_entity=${e.ent_id} WHERE ne_id=${min_ne_id}".toString()
            def res 
            def res2 = 0
            def res3 = 0
            
            
            int tries = 10
            boolean done = false 
            while (tries > 0 && !done) {
        	try {
        	    db.getDB().withTransaction() {
        		res = db.getDB().executeUpdate(sql)  
        		neids.each{neid -> 
        			res2 += db.getDB().executeUpdate(
        			"UPDATE doc_has_ne SET dhn_ne=? WHERE dhn_ne=?",[min_ne_id, neid])
        			res3 += db.getDB().executeUpdate( "DELETE FROM ne WHERE ne_id=?",[neid])
                
        		}
        		done = true
        	    }
        	} catch(Exception ex) {
        	    tries--
        	    if (tries >= 0) {
        		log.warn "Got exception ${ex.getMessage()}. Will wait 10 seconds. Tries: $tries"
                        sleep(10000)
        	    } else {
        		log.warn "Got exception ${ex.getMessage()}. NO more retries. Exiting."
        		System.exit(0)
        	    }
        	    
                }
            }
            log.info "Ok, updated ${res2} doc_has_nes, deleted ${res3} nes"
        }    
    }
    /***
     * Create a fix-ne.sql with: 
     * 
     * select min(ne_id) as min, count(ne_id) as c, ne_name, ne_lang, ne_category, ne_type, 
     * ne_subtype from ne group by ne_name, ne_lang, ne_category, ne_type, ne_subtype having c > 1
     * 
     * It will print all duplicates within same nename, lang, cat, type and subtype, but with different entity. 
     * It will also print the count (filtered to > 1) and the minimum ID. 
     * 
     * Do: mysql -u root rembrandtpool < fix_ne.sql > fix_ne_output.sql
     * 
     * Then: tail -n +2   fix_ne_output.sql >  fix_ne_output2.sql to get rid of the first line (column header)
     * Now divide it to small bits 
     * 
     * split -l 10000 fix_ne_output2.sql 
     * 
     */
    public static void main (String[] args) {
              
        Options o = new Options()
        
        o.addOption("file",true,"File with stuff above")
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
          
        if (!cmd.hasOption("file")) {
            println "No --file arg. Please specify file. Exiting."
            System.exit(0)
        }
        
        int lines_total = 0
        int lines_read = 0
        int lines_skipped = 0
        
        List itens = []
                
        File f = new File(cmd.getOptionValue("file"))
        f.eachLine{l -> 
            List its = l.split(/\t/)
            if (its[0].matches(/\d+/)) {
                lines_total++
                lines_read++
                itens << [
                    "min_ne_id":Long.parseLong(its[0]), 
                    "count":Integer.parseInt(its[1]),
                    "ne_name_id":Long.parseLong(its[2]), 
                    "lang":its[3],
                    "category_id":(its[4] == "NULL" ? null : Long.parseLong(its[4])), 
                    "type_id":(its[5] == "NULL" ? null : Long.parseLong(its[5])),
                    "subtype_id":(its[6] == "NULL" ? null : Long.parseLong(its[6])),
                ]
                       	
            } else {
        	println "Skipping line $l"
                lines_skipped++
            }
        }      
        println "File read. Total: $lines_total entries, $lines_read read, $lines_skipped skipped."
        FixNE fix = new FixNE(itens)
        fix.doIt()
    }
}