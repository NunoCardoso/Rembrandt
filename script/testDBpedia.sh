#!/usr/bin/env groovy 

import saskia.dbpedia.DBpediaAPI
import saskia.bin.Configuration

Configuration conf = Configuration.newInstance()
//uncomment if you want to force a given resource and url
//with comment, it will use whetever is in the configuration

//conf.set("saskia.dbpedia.mode","webservice")
//conf.set("saskia.dbpedia.url","http://dbpedia.org/sparql")
dbpedia = DBpediaAPI.newInstance(conf)

String query = "Select ?x where {<http://dbpedia.org/resource/Portugal> rdfs:label ?x}"
if (args) query = args[0]
println dbpedia.sparql(query)