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

package renoir.suggestion

import saskia.db.SaskiaDB
import saskia.db.WikipediaDB
import saskia.io.Geoscope
import saskia.bin.Configuration
import saskia.wikipedia.WikipediaAPI
import renoir.obj.QuestionType
import org.apache.log4j.*

/** This class fills the suggestion table */ 

class PopulateSuggestionDB {
    
   SaskiaDB db_saskia
   WikipediaDB db_wikipedia
   Logger log = Logger.getLogger("SuggestionDB")
   static String suggestion_table = "suggestion"
   static Configuration conf = Configuration.newInstance()
   String defaultquery =  "INSERT INTO ${suggestion_table}(sug_name, sug_type, sug_lang, sug_desc, "+
   "sug_ground, sug_score) VALUES(?,?,?,?,?,?)"
   SuggestionType type
   public static final int NE_THRESHOLD_COUNT = 5
   
   public PopulateSuggestionDB() {
		if (conf.getBoolean("saskia.wikipedia.enabled",true)) {
			db_wikipedia = WikipediaDB.newInstance()
		 }
       db_saskia = SaskiaDB.newInstance()
       conf = Configuration.newInstance()
    }
   
   
   public HashMap status(String lang) {
       def res = [:]
       db_saskia.getDB().eachRow("SELECT sug_type, COUNT(sug_type) as c from ${suggestion_table} WHERE "+
	       "sug_lang=? GROUP BY sug_type ORDER BY c DESC", [lang]) 
	       {row -> res[row['sug_type']] = row['c'] }
	   return res    
   }

    public void reset(String lang) {
         db_saskia.getDB().execute("DELETE from ${suggestion_table} WHERE sug_lang=?", [lang])      
    }
    
    public void deleteEntriesForType(String lang, String type) {
        db_saskia.getDB().execute("DELETE from ${suggestion_table} where sug_lang=? and sug_type=?".toString(), [lang, type])      
    }
    
   public int addWhereQuestions(String lang) {
       String type = SuggestionType.WhereQuestion.text()
       deleteEntriesForType(lang, type)
       int score = 0 
       int total = 0
    
       if (!SuggestionGazetteer.whereQuestions.containsKey(lang)) throw new IllegalStateException(
        "SuggestionGazetteer does not have Where question info for language $lang") 
    
       SuggestionGazetteer.whereQuestions[lang].each{wqk, wqv -> 	  
         // desc is null; ground has the index number of the QuestionType enum.
         db_saskia.getDB().executeInsert(defaultquery, [wqk, type, lang, null, wqv.ordinal(), score])
         total++
       }   	   
       return total
   }
   
   public int addPredicates(String lang) {
       String type = SuggestionType.Predicate.text()
       deleteEntriesForType(lang, type)
       int score = 0 
       int total = 0
       String query = defaultquery
    
     if (!SuggestionGazetteer.predicates.containsKey(lang)) throw new IllegalStateException(
        "SuggestionGazetteer does not have predicate info for language $lang") 
        
      SuggestionGazetteer.predicates[lang].each{pr -> 
         db_saskia.getDB().executeInsert(defaultquery, [pr, type, lang, null, null, score])
         total++  	   	 
     }
     return total
   }
   
   public int addOperators(String lang) {
       String type = SuggestionType.Operator.text()
       deleteEntriesForType(lang, type)
       int score = 0 
       int total = 0
    
    if (!SuggestionGazetteer.operators.containsKey(lang)) throw new IllegalStateException(
        "SuggestionGazetteer does not have operator info for language $lang") 
    
    SuggestionGazetteer.operators[lang].each{op -> 
        db_saskia.getDB().executeInsert(defaultquery, [op, type, lang, null, null, score])
        total++  	   	 
    }
       return total
   }
     
   public Map addNamedEntities(String lang) {
       String type = SuggestionType.NamedEntity.text()
       deleteEntriesForType(lang, type)
       int score = 0 
       int total = 0
       int added = 0
       int skipped = 0
    
        int limit = 1000
        int offset = 0
        int totalcats = 10000 // just a start value, it'll be changed to the real value
        
        HashMap batch = [:]
        
        while ( (offset) < totalcats) {
            log.debug "Getting NEs ${offset} to ${limit+offset}, total of ${totalcats}"
            //batch = [:]
            String select = "SELECT SQL_CALC_FOUND_ROWS ne_id, count(dhn_ne) as c, nen_name, TRIM(CONCAT(nec_category, ' ', IFNULL(net_type,''), ' ', "+
            "IFNULL(nes_subtype,'') ) ) AS ne_cts FROM ne_name, ne, ne_category, "+
            "ne_type, ne_subtype, doc_has_ne where dhn_ne=ne_id and nen_id=ne_name and ne_lang=? and ne_category=nec_id and ne_type=net_id and ne_subtype=nes_id "+
            "GROUP BY ne_id LIMIT $limit OFFSET $offset UNION SELECT FOUND_ROWS(), 0, '%%%TOTAL%%%', ''".toString()
            db_saskia.getDB().eachRow(select, [lang], {row ->
                long neid = (row['ne_id'] instanceof BigInteger ? row['ne_id'].longValue() : Long.parseLong(row['ne_id']) )
                if (row['nen_name'] == "%%%TOTAL%%%") {
                    totalcats = (int)neid
                } else {
                    if (row['c'] >= NE_THRESHOLD_COUNT) {
                	batch[neid]= ["name":row['nen_name'],"key":row['ne_cts']]          
                    } else {
                        log.debug "ne "+row['nen_name']+" only has "+row['c']+" occurrences, skipping."   
                    }
                }
            })
            offset += limit
          //  println "batch size: ${batch.size()}"
            
            if (batch.size() > 10000) {
                
              log.debug "Batch is full, updating DB..."
                
                try {
                    db_saskia.getDB().withTransaction{
                        
                        batch.each{neid, stuff -> 
                           // println "Got neid $neid stuff $stuff"
                            int nesize = stuff.name.size()
                            // format: {lang}:{ent};{lang}:{ent}, etc
                            if (nesize > 1 && nesize < 255) {
                                db_saskia.getDB().executeInsert(defaultquery, 
                                        [stuff.name, type, lang, stuff.key, ""+neid, score])
                                total++
                                added++
                            } else {
                                total++
                                skipped++
                            }
                        }
                    }  
                } catch (Exception e) {
                    log.error e.getMessage()
                }
                batch = [:]
            }
        }
    
    // leftovers
       if (batch) {
        try {
            db_saskia.getDB().withTransaction{
                
                batch.each{neid, stuff -> 
                    // println "Got neid $neid stuff $stuff"
                    int nesize = stuff.name.size()
                    // format: {lang}:{ent};{lang}:{ent}, etc
                    if (nesize > 1 && nesize < 255) {
                        db_saskia.getDB().executeInsert(defaultquery, 
                                [stuff.name, type, lang, stuff.key, ""+neid, score])
                        total++
                        added++
                    } else {
                        total++
                        skipped++
                    }
                }
            }  
        } catch (Exception e) {
            log.error e.getMessage()
        } 
       }
        return ['total':total, 'added':added, 'skipped':skipped]
    
      
   }
    
   public int addGeoscopes(String lang) {
       String type = SuggestionType.Geoscope.text()
       deleteEntriesForType(lang, type)

       int score = 0 
       int total = 0
       
       int limit = 500
       int offset = 0
       int totalcats = 1000 // just a start value, it'll be changed to the real value
    
       HashMap batch
     
       while ( offset < totalcats) {
	   log.debug "Getting Geoscopes ${offset} to ${limit+offset}, total of ${totalcats}"
	   batch = [:]
           String select = "SELECT SQL_CALC_FOUND_ROWS geo_name, geo_woeid from ${Geoscope.geo_table} "+
           "LIMIT $limit OFFSET $offset UNION SELECT '%%%TOTAL%%%', FOUND_ROWS()".toString()
           db_saskia.getDB().eachRow(select, {row -> 
            long woeid = (row['geo_woeid'] instanceof BigInteger ? row['geo_woeid'].longValue() : Long.parseLong(row['geo_woeid']) )
                            
             if (row['geo_name'] == "%%%TOTAL%%%") {
                totalcats = (int)woeid
            } else {
        	batch[woeid]= row['geo_name']            
            }
           })
           offset += limit
        
           try {
            db_saskia.getDB().withTransaction{
                
        	batch.each{woeid, geoname -> 
        	// format: {lang}:{ent};{lang}:{ent}, etc
        	   String real_geoname = geoname.split(/;/).find{it.startsWith("${lang}:")}
                   if (real_geoname) {
                      real_geoname = real_geoname.substring(3, real_geoname.size()) // trim lang info	
                      db_saskia.getDB().executeInsert(defaultquery, 
                       [real_geoname, type, lang, null, ""+woeid, score])
                       total++
                   }
                }
            }  
        } catch (Exception e) {
            log.error e.getMessage()
        }
      }

       return total
   }
   
   public int addDBpediaClasses(String lang) {
       String type = SuggestionType.DBpediaClass.text()
       deleteEntriesForType(lang, type)

       int score = 0 
       int total = 0
       def ontology = Class.forName("saskia.dbpedia.DBpediaOntologyDefinitions"+lang.toUpperCase())
        
       ontology.meanings.each{m -> 
	   // name, type, lang, score
	     	m.plain.each{p -> 
	     		// desc to null, ground to the DBpeica class name
	     		db_saskia.getDB().executeInsert(defaultquery, [p, type, lang, null, m.answer, score])
	     		total++
	     	}
	   }
       return total
   }
   
   public int addWikipediaCategories(String lang) {
       String type = SuggestionType.WikipediaCategory.text()
       deleteEntriesForType(lang, type)

       int score = 0 
       int total = 0
       def wiki = WikipediaAPI.newInstance(lang)
       String page_table = lang+"_page"
       
       int limit = 100
       int offset = 0
       int totalcats = 10000
       
       HashMap batch
       /* idea: get batches of $limit docs, process each batch until EOF. */
       
       while ( (offset+limit) < totalcats) {
	   log.debug "Getting Wikipedia category itens ${offset} to ${limit+offset}, total of ${totalcats}"
	   batch = [:]
	   String select = "SELECT SQL_CALC_FOUND_ROWS ${page_table}.page_id, "+
	   "${page_table}.page_title FROM ${page_table} WHERE ${page_table}.page_namespace=14 "+
	   "LIMIT $limit OFFSET $offset UNION SELECT  FOUND_ROWS(), '%%%TOTAL%%%'".toString()
	   db_wikipedia.getDB().eachRow(select, {row -> 
	      long pageid = (row['page_id'] instanceof BigInteger ? row['page_id'].longValue() : Long.parseLong(row['page_id']) )
        
	      if (row['page_title'] == "%%%TOTAL%%%") {
		  totalcats = (int)pageid	 
	      } else {
		  batch[pageid]= row['page_title'] 
	      }
	   })
	   offset += limit
	   		
	   batch.each{id, cat -> 
	   //println "id $id cat $cat"
	      if (! (cat.startsWith("!"))) {
		  //println "cat: $cat"
		  db_saskia.getDB().executeInsert(defaultquery, [cat.replaceAll("_"," "), type, lang, null, id, score])
		  total++
	      }
	   }
       }
       return total
   }
   
     static void main(args) {
    
    	Logger log = Logger.getLogger("SuggestionDB")
    	PopulateSuggestionDB s = new PopulateSuggestionDB()
    	
    	String lang
    	if (!args) {
    	    println "Usage: renoir.suggestion.PopulateSuggestionDB [lang]"
    	    System.exit(0)   
    	}
	 	
    	lang = args[0]
    	            
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in))		    
    	String line
    	println "SuggestionDB tool - LANG: $lang."
    	println "============================"
    	println "Commands: "
    	println "  status - Get a status on the suggestion table."
        println "  reset - Delete all table entries."
    	println "  refresh WQ - refresh Where Questions."
    	println "  refresh PR - refresh Precidates."
    	println "  refresh OP - refresh Operators."
    	println "  refresh NE - refresh Named Entities."
    	println "  refresh GS - refresh Geoscopes."
    	println "  refresh DB - refresh DBPedia classes."	
    	println "  refresh WC - refresh Wikipedia categories"
    	println "  refresh ALL - refresh everything"
	
    	while (true) {
    	    print "SuggestionDB> "
    	    line = input.readLine().trim()
    	    if (!line) break
    	    def tokens = line.split(/\s+/)
    	    def command = tokens[0]
    	                         
    	    switch(command) {
				
    	    case "status":
    		def status = s.status(lang)
    		status.each{k, v -> println "$k: $v"}
    	    break
            
            case "reset":
                s.reset(lang)
            break
                
    	    case "refresh":
    		def start1, start2, num
    		def what = tokens[1]
            
                switch(what) {
				    	
                case ['WQ','ALL']:				    	    
                    start1 = System.currentTimeMillis()	
                    num = s.addWhereQuestions(lang)
                    start2 = System.currentTimeMillis()	
                    log.info "Added ${num} Where questions for lang ${lang} in "+(start2-start1)/1000.0+" secs."
                    
                case ['PR','ALL']:				    	    
                    start1 = System.currentTimeMillis()	
                    num = s.addPredicates(lang)
                    start2 = System.currentTimeMillis()	
                    log.info "Added ${num} predicates for lang ${lang} in "+(start2-start1)/1000.0+" secs."
                    
                case ['OP','ALL']:				    	    
                    start1 = System.currentTimeMillis()	
                    num = s.addOperators(lang)
                    start2 = System.currentTimeMillis()	
                    log.info "Added ${num} operators for lang ${lang} in "+(start2-start1)/1000.0+" secs."	
                    
                case ['NE','ALL']:				    	    
                    start1 = System.currentTimeMillis()	
                    num = s.addNamedEntities(lang)
                    start2 = System.currentTimeMillis()	
                    log.info "Total ${num.total} NEs for lang ${lang}, added  ${num.added}, skipped ${num.skipped} (too big) in "+(start2-start1)/1000.0+" secs."
				    
                case ['GS','ALL']:				    	    
                    start1 = System.currentTimeMillis()	
                    num = s.addGeoscopes(lang)
                    start2 = System.currentTimeMillis()	
                    log.info "Added ${num} geoscopes for lang ${lang} in "+(start2-start1)/1000.0+" secs."
                    
                case ['DB','ALL']:				    	    
                    start1 = System.currentTimeMillis()	
                    num = s.addDBpediaClasses(lang)
                    start2 = System.currentTimeMillis()	
                    log.info "Added ${num} DBPedia classes for lang ${lang} in "+(start2-start1)/1000.0+" secs."
                    
                case ['WC','ALL']:				    	    
                    start1 = System.currentTimeMillis()
						  if (conf.getBoolean("saskia.wikipedia.enabled",true))
                    	  num = s.addWikipediaCategories(lang)
                    start2 = System.currentTimeMillis()	
                    log.info "Added ${num} Wikipedia categories for lang ${lang} in "+(start2-start1)/1000.0+" secs."
                }
    		break		    	
    	    }
	}//while true
    }//main
}