#!/usr/bin/env groovy 

import saskia.dbpedia.DBpediaAPI
import saskia.bin.Configuration
import com.hp.hpl.jena.query.*
import com.hp.hpl.jena.rdf.model.*
import saskia.util.Native2AsciiWrapper

Configuration conf = Configuration.newInstance()
Native2AsciiWrapper n2a = Native2AsciiWrapper.newInstance()
if (args.size() <1) {
   println "Usage: getTriples.sh {pagename}"
   System.exit(0)
}

println "Querying for ${args[0]}, @pt" 

Query query = QueryFactory.create("SELECT ?s ?p ?o  WHERE {  ?s <http://www.w3.org/2000/01/rdf-schema#label> \"${args[0]}\"@pt . { {?s ?p ?o} UNION {?o ?p ?s} }  }")

QueryExecution qe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql",query)
ResultSet results = qe.execSelect()

File f = new File("temporary.tmp")
ResultSetFormatter.out( new FileOutputStream(f), results, query)

f.eachLine {line ->
  def pieces = line.split(/\Q|\E/) 
  if (pieces.size() == 4) {
            // surround 3rd piece with \" if it is just a number
            def m = pieces[3].trim() =~ /^[\d\.]*$/
            if (m.matches()) pieces[3] = "\""+pieces[3].trim()+"\""
            println pieces[1].trim()+" "+pieces[2].trim()+" "+n2a.n2a(pieces[3].trim())+" ."
    }
}

f.delete()



/*
    for ( ; results.hasNext() ; ) {
	QuerySolution soln = results.nextSolution() 
       	print "$q "
	def p = soln.get("p")
	Resource r = (Resource)p
        print "<$r> "
        def o = soln.get("o")
	if (o.isLiteral()) {
	     Literal l = (Literal)o

	     print l.getLexicalForm()
//	     def l2 = l.toString() //tem o @xx colado ao texto
//	     print l2.replaceAll(/(.*)(@\w\w)$/) {all, g1, g2 -> "\"${g1}\"${g2}"}
	} else if (o.isResource()) {
	   Resource r2 = (Resource)o
	   print "<$r2>"
	}
	println " ."
  }
*/