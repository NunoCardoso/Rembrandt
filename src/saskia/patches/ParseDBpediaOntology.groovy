
println "Reading file "+args[0]

File newf = new File(args[0]+".info")

println "Writing to file "+newf

String dbclass = null
List classes = []
List relations = []

new File(args[0]).eachLine{l -> 
	
	def m = l =~ ~/^\s*<owl:Class rdf:about="http:\/\/dbpedia.org\/ontology\/(.*)">\s*$/
	if (m.matches()) classes << '"'+m.group(1)+'"'
		
	m = l =~ ~/^\s*<rdfs:subClassOf rdf:resource="http:\/\/dbpedia.org\/ontology\/(.*)"><\/rdfs:subClassOf>\s*$/
	if (m.matches()) relations << classes[classes.size()-1]+':"'+m.group(1)+'"'
}
newf.write ""
newf.append classes.sort().join(", ")
newf.append relations.sort().join(", ")
 