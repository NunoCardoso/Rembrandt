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

package saskia.ontology


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
 import saskia.dbpedia.SPARQLPrefixes

/** 
 *  */
class GeoNetPT02API {
	
  Configuration conf
  String lang
  
  Model model // Model of the RDF
  Store store // connection to SDB, if configuration is 'sdb'

  static GeoNetPT02API _this
  SPARQLPrefixes prefixes
  static String graphuri = "http://geonetpt02.xldb.di.fc.ul.pt"
  String mode
  String fileFormat
  List files = []

  Native2AsciiWrapper n2a
  
  def static Logger log = Logger.getLogger("GeoNetPT02API")  
  def static Logger error = Logger.getLogger("SaskiaError")  

  /** private singleton constructor 
   * @param conf Confugration settings
   */
  private GeoNetPT02API(Configuration conf) {
      
    this.conf=conf
    mode = conf.get('saskia.geonetpt02.mode',"webservice") // use DBpedia by default
    n2a = Native2AsciiWrapper.newInstance()
    prefixes = new SPARQLPrefixes()

  
    switch(this.mode) {
    case "local":
	log.info "Mode: local files"
	this.fileFormat = conf.get("saskia.geonetpt02.local.fileformat",'N-TRIPLE')
	if (!this.fileFormat) 
	    log.warn "No saskia.geonetpt02.local.fileformat specified, assuming N-TRIPLE."
	if (!conf.getStrings("saskia.geonetpt02.local.files")) 
	    log.warn "No saskia.geonetpt02.local.files specified. Loading NO FILES."
		
	this.model = ModelFactory.createDefaultModel()
	conf.getStrings("saskia.geonetpt02.local.files").each{file -> loadFile(file)} 
	break 
		 
    case "sdb":
	log.info "Mode: Jena's SDB"
	def sdb_file = conf.get("saskia.geonetpt02.sdb.ttl")
	if (!sdb_file) {
	    log.warn "No saskia.geonetpt02.sdb.ttl file specified, using default conf/sdb.ttl."
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
	log.info "Mode: Internet access to "+conf.get("saskia.geonetpt02.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
	 	 	
	// SETTING PROXIES
	boolean setProxy = conf.getBoolean("saskia.geonetpt02.proxy.enabled",false)
	if (!setProxy) {
            System.setProperty("http.proxySet","false")   
            System.setProperty("http.proxyHost","")   
            System.setProperty("http.proxyPort","")   
            log.info "Proxy if OFF for DBpedia web service."
	} else {
            System.setProperty("http.proxySet","true")   
            System.setProperty("http.proxyHost",conf.get("saskia.geonetpt02.proxy.host"))   
            System.setProperty("http.proxyPort",conf.get("saskia.geonetpt02.proxy.port"))   
            log.info "Proxy if ON for DBpedia web service, set to "+
            System.getProperty("http.proxyHost")+":"+System.getProperty("http.proxyPort")
	}
        break
    }  
    log.info "GeoNetPT02API initialized. mode: ${this.mode}"
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
  public static GeoNetPT02API newInstance(conf_ = null) {
      Configuration c
      if (!conf_) c = Configuration.newInstance() else c = conf_    
      if (!_this) _this = new GeoNetPT02API(c)
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
 		log.error("Error making local/SDB SPARQL query "+queryString.replaceAll(/[\n\t\r]/,""), e)
 	    }
 	    break 
 	    
 	case ["webservice"]:
 	    try {
 		String dbpediaservice = conf.get("saskia.geonetpt02.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
 		qe = QueryExecutionFactory.sparqlService(dbpediaservice, prefix+queryString, graphuri)
 	    } catch (Exception e) {
 		log.error("Error making webservice SPARQL query "+queryString.replaceAll(/[\n\t\r]/,""), e)
 	    }
 	    break
	}
		
      if (!qe) {
	  log.fatal "SPARQL query failed, can't proceed. Check SPARQL connection for mode ${this.mode}, "+
	  "or turn DBpedia access off (saskia.geonetpt02.enabled=false)."
	  System.exit(0)
      }
      ResultSet results
      try {
	  results = qe.execSelect()
	  answers = parseResults(results)
	  log.debug "result: "+answers
      } catch(com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP httpe) {
	  log.warn ("Is your network connection on?")
	  log.warn ("Got HttpException for query "+queryString.replaceAll(/[\n\t\r]/,""), httpe)
     } catch (Exception he) {
	log.warn ("Got error: "+he.getMessage())
	log.warn ("Is your network connection on? Check the proxies.")
     }
      finally {qe?.close()} 
      log.debug "Answers: "+answers
      return answers
   }

 
}
