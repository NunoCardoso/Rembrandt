/* Saskia
 * Copyright (C) 2008- Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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

/** This class interfaces with the World Geographic Ontology (WGO) in RDF format */
class WGO_API {
	
  Configuration conf
  String lang
  Model model
  Store store // connection to SDB, if configuration is 'sdb'
  static WGO_API _this
  String mode
  String fileFormat
  Native2AsciiWrapper n2a
  List files = []

 	def static Logger log = Logger.getLogger("WGO_API")  

	private WGO_API(Configuration conf_) {
	    this.conf=conf_
		this.mode = this.conf.get('saskia.ontology.mode','webservice') // use DBpedia by default
		n2a = Native2AsciiWrapper.newInstance()
		
		switch(this.mode) {
		 case "local":
		     log.info "Mode: local files"
		     this.fileFormat = conf.get("saskia.ontology.local.fileformat",'N-TRIPLE')
			 if (!this.fileFormat) 
				log.warn "No saskia.ontology.local.fileformat specified, assuming N-TRIPLE."
			 if (!conf.getStrings("saskia.ontology.local.files")) 
				log.warn "No saskia.ontology.local.files specified. Loading NO FILES."
			 
		     this.model = ModelFactory.createDefaultModel()
		     conf.getStrings("saskia.ontology.local.files").each{file -> loadFile(file)} 
		 break 
		 
		 case "sdb":
		     log.info "Mode: Jena's SDB"
		     try {
			   def sdb_file = conf.get("saskia.ontology.sdb.ttl")
			   if (!sdb_file) {
				log.warn "No saskia.ontology.sdb.ttl file specified, using default conf/ontology_sdb.ttl."
				sdb_file = "conf/ontology_sdb.ttl"
			 }
			 def f = new File(sdb_file)
			 if (!f.exists()) {
				log.warn "SDB file $sdb_file not found. WGO connection is compromised. Exiting."
				System.exit(0)
			}
	
			 this.store = SDBFactory.connectStore() 
			   
		     }catch(SDBException e) {
			 	log.error "SDB file loaded but something went wrong with the SDB configuration, check it."    
			 	error.error e	
		     }catch(Exception e) {
			 	error.error e
			 }
		     this.model = SDBFactory.connectDefaultModel(this.store) 
		 break 

		 case "webservice":
		     log.info "Mode: Internet access to "+conf.get("saskia.ontology.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")+"."
		 break
		 }   	
    	log.info "WGO_API initialized. mode: ${this.mode}"
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
	
	private parseResults(ResultSet results) {
	 
	    def answers = [] 
	   	for ( ; results.hasNext() ; ) {
			QuerySolution soln = results.nextSolution() 
			def answer = [:]
			soln.varNames().each{it -> answer[it]=soln[it]}
			answers << answer
		}
		//log.debug answers
	    return answers
	}
	
	public static WGO_API newInstance(conf_ = null) {
	    Configuration c
	    if (!conf_) c = Configuration.newInstance() else c = conf_    
	    if (_this == null) _this = new WGO_API(c)
		return _this
	}
	
	public List sparql(String queryString) throws QueryParseException, QueryException {
 
	 	QueryExecution qe 
 	 	def answers
		
		String prefix = """
		PREFIX owl: <http://www.w3.org/2002/07/owl#>
		PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
		PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
		PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
		PREFIX foaf: <http://xmlns.com/foaf/0.1/>
		PREFIX dc: <http://purl.org/dc/elements/1.1/>
		PREFIX dcterms:  <http://purl.org/dc/terms/>
		PREFIX : <http://dbpedia.org/resource/>
		PREFIX dbpedia2: <http://dbpedia.org/property/>
		PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
		PREFIX dbpedia: <http://dbpedia.org/>
		PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
		"""
	    // choose between sdb, local, internet
		switch (this.mode) {
			case ["local", "sdb"]:
		 	  Query query = QueryFactory.create(prefix+queryString)
 	 		  try {
				qe = QueryExecutionFactory.create(query, this.model)
			 } catch (Exception e) {
			  e.printStackTrace()
			 }
			break 
			
			case ["webservice"]:
 	     	  try {
				String dbpediaservice = conf.get("saskia.ontology.url","http://xldb.di.fc.ul.pt/dbpedia/sparql")
				qe = QueryExecutionFactory.sparqlService(dbpediaservice, prefix+queryString, "http://geonetpt.xldb.di.fc.ul.pt")
			 } catch (Exception e) {
			  e.printStackTrace()
			 }
			break
		}
 	    if (!qe) {
			throw new IllegalStateException("QueryExecution was not initialized, can't make SPARQL "+
			"queries, check SPARQL connectios for mode ${this.mode}")
		}
		try {
		  log.debug "Issuing query: "+queryString.replaceAll(/[\n\t\r]/,"")
 	      ResultSet results = qe.execSelect()
 	      answers = parseResults(results)
		  log.debug "result: "+answers
 	      } catch (Exception e) {e.printStackTrace()} 
 	    finally {qe?.close()} 
	    return answers
 	}
 
	public List getFeatureResourcesFromPrefLabel(String name, String lang) {
		// labels are in lowercase
		name = name.toLowerCase()
		String query = "SELECT ?feat WHERE { ?feat skos:prefLabel \"$name\"@$lang }"
		def answers = sparql(query)
		if (answers) {
			answers = answers*.feat.findAll{it.toString().startsWith("http://wgo.xldb.fc.ul.pt/feature/")}
			log.debug "Filtering answers to $answers"
			return answers
		}
		return []
	}
	
	/** find a hasPart property between two lists of features */
	public List<Map> isPartOf(List smallerFeat, List largerFeat) {
		// labels are in lowercase
		def answer = []
		largerFeat.each{lf -> 
			smallerFeat.each{sf -> 
				String query = "SELECT ?prop WHERE { <$lf> ?prop <$sf> }"
				def answers = sparql(query)
				answers.each{a -> 
					if (a.prop.toString().equals("http://purl.org/dc/terms/hasPart"))
					 	answer << [sf:sf, lf:lf]
				}
			}
		}
		return answer
	}
	
}
