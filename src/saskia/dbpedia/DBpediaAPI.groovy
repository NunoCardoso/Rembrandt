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

package saskia.dbpedia

 import com.hp.hpl.jena.sdb.SDBFactory
 import com.hp.hpl.jena.sdb.SDBException
 import com.hp.hpl.jena.sdb.Store
 import com.hp.hpl.jena.rdf.model.*
 import com.hp.hpl.jena.query.*
 import com.hp.hpl.jena.util.FileManager
 import saskia.bin.Configuration
 import saskia.util.Native2AsciiWrapper
 import org.apache.log4j.*
import java.net.URLEncoder

/** This class started as an API interface for DBpedia, but it can use any RDF source in several ways:
 * <UL>
 * <LI>First, as a web service (saskia.dbpedia.mode = webservice), where a sparqlService (as the 
 * DBpedia's http://dbpedia.org/sparl, the default saskia.dbpedia.url value)
 * <LI>As a set of files (saskia.dbpedia.mode = local), where a list of RDF/N3/N-Triples files (given by 
 * the saskia.dbpedia.local.fileFormat value, default to N-TRIPLE, and by saskia.dbpedia.local.file value)
 * <LI>As a Jena's SDB source (saskia.dbpedia.mode = sdb), where a sdb.ttl file is given in 
 * the saskia.dbpedia.sdb.ttl value. 
 * </UL>
 * 
 * There are methods for the most frequent DBpedia queries, and those queries are cached (use the 
 * saskia.dbpedia.cache.number value to change the default of 100 entries).
 */
class DBpediaAPI {
	
  Configuration conf
  String lang
  
  Model model // Model of the RDF
  Store store // connection to SDB, if configuration is 'sdb'

  static DBpediaAPI _this
  SPARQLPrefixes prefixes
  static String graphuri = "http://dbpedia.org"

  String mode
  String fileFormat
  List files = []

 // Native2AsciiWrapper n2a
  
  /** Cache map for queries on DBpedia's resource for a given Wikipedia page and/or title*/
  LinkedHashMap cacheWikiTitle2DBpediaResource
  LinkedHashMap cacheWikiPage2DBpediaResource
  
  /** Cache map for queries on DBpedia ontology classes for a resource */
  LinkedHashMap cacheDBpediaClass

  /** Cache map for queries on DBpedia ontology classes for a resource */
  LinkedHashMap cacheDBpediaEverything
 
  /** Cache map for queries on DBpedia SKOS categories for a resource */
  LinkedHashMap cacheDBpediaSKOScategories 
  
  /** Cached map for queries on DBpedia labels for a resource */
  LinkedHashMap cacheDBpediaLabels 
  
  def static Logger log = Logger.getLogger("DBpediaAPI")  
  def static Logger error = Logger.getLogger("SaskiaError")  

  /** private singleton constructor 
   * @param conf Confugration settings
   */
  private DBpediaAPI(Configuration conf) {
      
    this.conf=conf
    mode = conf.get('saskia.dbpedia.mode',"webservice") // use DBpedia by default
    //n2a = Native2AsciiWrapper.newInstance()
    prefixes = new SPARQLPrefixes()

    // initialize caches
    cacheWikiTitle2DBpediaResource = new LinkedHashMap(conf.getInt("saskia.dbpedia.cache.wikititle",1000), 0.75f, true) 
    cacheWikiPage2DBpediaResource = new LinkedHashMap(conf.getInt("saskia.dbpedia.cache.wikipage",1000), 0.75f, true) 
    cacheDBpediaClass = new LinkedHashMap(conf.getInt("saskia.dbpedia.cache.class",1000), 0.75f, true) 
    cacheDBpediaEverything = new LinkedHashMap(conf.getInt("saskia.dbpedia.cache.everything",1000), 0.75f, true) 
    cacheDBpediaSKOScategories = new LinkedHashMap(conf.getInt("saskia.dbpedia.cache.skos",1000), 0.75f, true) 
    cacheDBpediaLabels = new LinkedHashMap(conf.getInt("saskia.dbpedia.cache.label",1000), 0.75f, true) 
	
    switch(this.mode) {
    case "local":
	log.info "Mode: local files"
	this.fileFormat = conf.get("saskia.dbpedia.local.fileformat",'N-TRIPLE')
	if (!this.fileFormat) 
	    log.warn "No saskia.dbpedia.local.fileformat specified, assuming N-TRIPLE."
	if (!conf.getStrings("saskia.dbpedia.local.files")) 
	    log.warn "No saskia.dbpedia.local.files specified. Loading NO FILES."
		
	this.model = ModelFactory.createDefaultModel()
	conf.getStrings("saskia.dbpedia.local.files").each{file -> loadFile(file)} 
	break 
		 
    case "sdb":
	log.info "Mode: Jena's SDB"
	def sdb_file = conf.get("saskia.dbpedia.sdb.ttl")
	if (!sdb_file) {
	    log.warn "No saskia.dbpedia.sdb.ttl file specified, using default conf/sdb.ttl."
	    sdb_file = "conf/sdb.ttl"
	}
	def f = new File(sdb_file)
	if (!f.exists()) {
	    log.warn "SDB file $sdb_file not found. DBpedia connection is compromised. Exiting."
	    System.exit(0)
	}
	try {
	    this.store = SDBFactory.connectStore()  
	} catch(SDBException e) {
	    log.error "SDB file loaded but something went wrong with the SDB configuration, check it."    
	    error.error e	
	} catch(Exception e) {
	    error.error e
	}
	this.model = SDBFactory.connectDefaultModel(this.store) 
	break 
	
    case "webservice":
	log.info "Mode: Internet access to "+conf.get("saskia.dbpedia.url","http://dbpedia.org/sparql")
	 	 	
	// SETTING PROXIES
	boolean setProxy = conf.getBoolean("saskia.dbpedia.proxy.enabled",false)
	if (!setProxy) {
            System.setProperty("http.proxySet","false")   
            System.setProperty("http.proxyHost","")   
            System.setProperty("http.proxyPort","")   
            log.info "Proxy if OFF for DBpedia web service."
	} else {
            System.setProperty("http.proxySet","true")   
            System.setProperty("http.proxyHost",conf.get("saskia.dbpedia.proxy.host"))   
            System.setProperty("http.proxyPort",conf.get("saskia.dbpedia.proxy.port"))   
            log.info "Proxy if ON for DBpedia web service, set to "+
            System.getProperty("http.proxyHost")+":"+System.getProperty("http.proxyPort")
	}
        break
    }  
    log.info "DBpediaAPI initialized. mode: ${this.mode}"
  }
		
  private void loadFile(String file) {
      file = file.trim()
      log.debug "Going to read $file, of type ${this.fileFormat}"
      InputStream input = FileManager.get().open(file)
      if (input == null) 	log.warn "File: " + file + " not found."
      else {
	  def filename = file.split("/")
	  this.model.read(input,"",this.fileFormat)
	  input.close()
	  log.info "File ${filename[-1]} read as ${this.fileFormat}."
	  this.files += file
      }
  }
  /** Parse the SPARQL results.
   * @param results ResultSet from QueryEngine
   * @return list of results. 
   */
  private parseResults(ResultSet results) {
      def answers = [] 
      for ( ; results.hasNext() ; ) {
	  QuerySolution soln = results.nextSolution() 
	  def answer = [:]
	  soln.varNames().each{it -> answer[it]=soln[it]}
	  answers << answer
      }
      return answers
   }
	
  /**
   *  Public singleton access method. 
   */
  public static DBpediaAPI newInstance(conf_ = null) {
      Configuration c
      if (!conf_) c = Configuration.newInstance() else c = conf_    
      if (!_this) _this = new DBpediaAPI(c)
      return _this
   }
	
  /** Issue the SPARQL queries, get List with maps of answers. 
   * @param queryString SPARQL query.
   * @return answer list.
   */
  public List sparql(String queryString) throws QueryParseException, QueryException {
      if (!queryString) return null
	    
 	QueryExecution qe 
 	def answers
 	String prefix = prefixes.getAll()
 	log.debug "Got SPARQL query: "+queryString.replaceAll(/[\n\t\r]/,"")

 	// choose between sdb, local, internet
 	switch (this.mode) {
 	case ["local", "sdb"]:
 	    Query query = QueryFactory.create(prefix+queryString)
 	    try {
 		qe = QueryExecutionFactory.create(query, this.model)
 	    } catch (Exception e) {
 		log.error("Error making local/SDB SPARQL query "+queryString..replaceAll(/[\n\t\r]/,""), e)
 	    }
 	    break 
 	    
 	case ["webservice"]:
 	    try {
 		String dbpediaservice = conf.get("saskia.dbpedia.url","http://dbpedia.org/sparql")
 		qe = QueryExecutionFactory.sparqlService(dbpediaservice, prefix+queryString, graphuri)
 	    } catch (Exception e) {
 		log.error("Error making webservice SPARQL query "+queryString.replaceAll(/[\n\t\r]/,""), e)
 	    }
 	    break
	}
		
      if (!qe) {
	  log.fatal "SPARQL query failed, can't proceed. Check SPARQL connection for mode ${this.mode}, "+
	  "or turn DBpedia access off (saskia.dbpedia.enabled=false)."
	  System.exit(0)
      }
      ResultSet results
      try {
	  results = qe.execSelect()
	  answers = parseResults(results)
	  log.debug "result: "+answers
      } catch(com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP httpe) {
	  log.warn ("Is your network connection on?")
	  log.warn ("Got HttpException for query "+queryString.replaceAll(/[\n\t\r]/,"")+": "+httpe.getMessage())
     } catch (Exception he) {
	log.warn ("Got error: "+he.getMessage())
	log.warn ("Is your network connection on? Check the proxies.")
     }
      finally {qe?.close()} 
      log.debug "Answers: "+answers
      return answers
   }

  /** Get a label from a DBpedia resource 
   * @param dbpediaResource The DBpedia resource
   * @param lang the language to filter out the label
   * @return The label
   */
  public String getLabelFromDBpediaResource(String dbpediaResource, String lang) {
      // ensure it has a full name
      dbpediaResource = DBpediaResource.getFullName(dbpediaResource)
      def answers
      if (cacheDBpediaLabels.containsKey(dbpediaResource+"-"+lang)) {
	  answers = cacheDBpediaLabels.get(dbpediaResource+"-"+lang)
	  log.trace "DBpedia label for $dbpediaResource, lang $lang found in cache: $answers"
	  return answers
      } 	
      String query = "SELECT ?s WHERE { <$dbpediaResource> rdfs:label ?s . FILTER (lang(?s)=\"${lang}\") }"
	  answers = sparql(query)
	  if (answers)  {
	      answers = answers[0].s.toString().replaceAll(/@.*$/,"")	
	      // cache it and return
			cacheDBpediaLabels.put(dbpediaResource+"-"+lang, answers)
			log.trace "DBpedia label for $dbpediaResource: found: $answers"
			return answers
		}
		return null
	}
 	
	/** Get DBpedia's resource from a title, using wikipage-pt property 
	 *  @param title The title to be looked for as a rdfs:label.
	 *  @param lang The targeted language for the wikipedia URL generation.
	 *  @return The DBpedia resource.
	*/
	public String getDBpediaResourceFromWikipediaURL(String title, String lang) {
	     
		if (!title || !lang) return null

		// one must ensure that the title is correctly encoded
		// Wikipedia URL titles are uppercase on first letter
		def newTitle = title[0].toUpperCase() + title.substring(1) 
		newTitle = URLEncoder.encode(newTitle.replaceAll(/ /,"_"), 	    
			conf.get("global.encoding", System.getProperty('file.encoding')))
		
		// funny thing, DBpedia loads non-English URL links as <wikipage-${lang}> properties, but the English ones are in 
		//<http://xmlns.com/foaf/0.1/page> links. 
		String wikipediaLinkProperty
		if (lang.toLowerCase() == "en") {
		    wikipediaLinkProperty = "foaf:page"
		} else {
		    wikipediaLinkProperty = "<"+DBpediaProperty.propertyPrefix+"wikipage-${lang}>"
		    
		}
		
		def answers
		if (cacheWikiPage2DBpediaResource.containsKey(newTitle+"-"+lang)) {
		    answers = cacheWikiPage2DBpediaResource.get(newTitle+"-"+lang)
		    log.trace "DBpedia resource for Wiki URL $newTitle, lang $lang found in cache: $answers"
		    return answers
		} 	
		
	   	String query = "SELECT ?s WHERE { ?s $wikipediaLinkProperty <http://${lang}.wikipedia.org/wiki/${newTitle}> } "
		log.debug "getDBpediaResourceFromWikipediaURL query: "+query
		answers = sparql(query)

		if (answers) {
		    answers = answers[0]?.s?.toString()
			cacheWikiPage2DBpediaResource.put(newTitle+"-"+lang, answers)
			log.debug "DBpedia resource for Wiki URL $newTitle, lang $lang found: $answers"
			return answers
		} else {
			log.debug "DBpedia resource for Wiki URL $newTitle, lang $lang found no answers"
		}
		return null
	}
	
	/**  Get DBpedia's resource from a title, with spaces instead of underscores, 
	  * that is normally used to represent Wikipedia's page titles. 
	 *  @param title The title to be looked for as a rdfs:label.
	 *  @param lang The targeted language for the label.
	 *  @return The DBpedia resource.
	*/
	public String getDBpediaResourceFromWikipediaPageTitle(String title, String lang) {
	 //	ensure title has no encoding problems.
	    //title =  n2a.n2a(title)
		def answers
		if (cacheWikiTitle2DBpediaResource.containsKey(title+"-"+lang)) {
		    answers = cacheWikiTitle2DBpediaResource.get(title+"-"+lang)
		    log.trace "DBpedia resource for Wiki title $title, lang $lang found in cache: $answers"
		    return answers
		} 	
	 	
	 // if title has " , let's escape htem
	 title = title.replaceAll(/([^\\])"/) {all, g1 -> return ""+g1+"\\"+'"'} //"
		String query = "SELECT ?s WHERE { ?s rdfs:label \"${title}\"@${lang} }"
		
		answers = sparql(query)
		
		log.debug "getDBpediaResourceFromWikipediaPageTitle query: "+query
		if (!answers) {
			// let's try for English
			query = "SELECT ?s WHERE { ?s rdfs:label \"${title}\"@en }"
		   answers = sparql(query)
				log.debug "2nd try in en: getDBpediaResourceFromWikipediaPageTitle query: "+query

		}
		answers = answers.findAll{it.s.toString() =~ /^${DBpediaResource.resourcePrefix}[^:]*$/}	
		if (answers?.size() > 1) log.warn "Found more than 1 DBpedia resource for Wiki title: $answers"
		// get only the first one.
		if (answers) {
		    answers = answers[0]?.s?.toString()
			cacheWikiTitle2DBpediaResource.put(title+"-"+lang, answers)
			log.debug "DBpedia resource for Wiki page title $title, lang $lang found: $answers"
			return answers
		} else {
			log.debug "DBpedia resource for Wiki page title $title, lang $lang found no answers"
		}
		return null
	}

	public Map getEverythingFromDBpediaResource(String dbpediaResource) {
		// ensure it has a full name
		dbpediaResource = DBpediaResource.getFullName(dbpediaResource)
		def answers
		Map res = [:] 	
		if (cacheDBpediaEverything.containsKey(dbpediaResource)) {
	  		res = cacheDBpediaEverything.get(dbpediaResource)
	  		log.trace "DBpedia everything for $dbpediaResource, found in cache: $res"
	  		return res
      } 	
		String query = """SELECT ?y ?z WHERE { <$dbpediaResource> ?y ?z}"""
		answers = sparql(query)
		answers?.each{a -> 
			String key = a.y.toString()
		
			if (res.containsKey(key)) {
				// if there's an element, make room, convert it to a list
				if (res[key] instanceof String) res[key] = [res[key]]
				res[key] << a.z.toString()
			} else {
				res[key] = a.z.toString()
			}
		}
		cacheDBpediaEverything[dbpediaResource] = res
		return res
	}
	
	/** Get DBpedia's ontology classes from a given DBpedia resource
	 *  @param dbpediaResource The DBpedia resource name. It can be a short or full name
	 *  @return A list of ontology classes.
	 */
	public List getDBpediaOntologyClassFromDBpediaResource(String dbpediaResource) {
		// ensure it has a full name
		dbpediaResource = DBpediaResource.getFullName(dbpediaResource)
		def answers
		if (cacheDBpediaClass.containsKey(dbpediaResource)) {
		    answers = cacheDBpediaClass.get(dbpediaResource)
		    log.trace "DBpedia ontology classes for $dbpediaResource: found in cache: $answers"
		    return answers
		} 	
		String query = """SELECT ?s WHERE { <$dbpediaResource> rdf:type ?s}"""
		answers = sparql(query)
		if (answers) {
		    answers = answers*.s.collect{it.toString()}.findAll{
		    it.startsWith(DBpediaOntology.ontologyPrefix)}.unique()
		// cache it and return		
			cacheDBpediaClass.put(dbpediaResource, answers)
			log.trace "DBpedia ontology classes for $dbpediaResource: found: $answers"
			return answers
		}
		return null
	}
	
	/** Get DBpedia's SKOS categories from a given DBpedia resource for the skos:subject
	 *  @param dbpediaResource The DBpedia resource name. It can be a short or full name
	 *  @return A list of SKOS categories.
	 */
	public List getDBpediaSKOSCategoryFromDBpediaResource(String dbpediaResource) {
		// ensure it has a full name
	    dbpediaResource = DBpediaResource.getFullName(dbpediaResource)
		def answers 
		if (cacheDBpediaSKOScategories.containsKey(dbpediaResource)) {
		    answers = cacheDBpediaSKOScategories.get(dbpediaResource)
		    log.trace "SKOS categories for $dbpediaResource: found in cache: $answers"
		    return answers
		} 
		
		String query = """SELECT ?s WHERE { <$dbpediaResource> skos:subject ?s}"""
		answers = sparql(query)
		if (answers) {
		    answers = answers*.s.collect{it.toString()}
			// cache it and return
			cacheDBpediaSKOScategories.put(dbpediaResource, answers)
			log.trace "SKOS categories for $dbpediaResource: found: $answers"
			return answers
		}
		return null
	}
}
