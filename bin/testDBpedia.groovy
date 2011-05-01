import com.hp.hpl.jena.query.*
import com.hp.hpl.jena.rdf.model.*

Query query = QueryFactory.create("SELECT ?s WHERE {?s <http://www.w3.org/2000/01/rdf-schema#label> \"Portugal\"@pt}")

QueryExecution qe = QueryExecutionFactory.sparqlService("http://xldb.di.fc.ul.pt/sparql/sparql", query, "http://dbpedia.org")
ResultSet results = qe.execSelect()
ResultSetFormatter.out(  results, query)
