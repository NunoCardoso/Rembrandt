#!/usr/bin/env groovy 

import saskia.bin.Configuration
import saskia.dbpedia.*
import saskia.util.Native2AsciiWrapper

conf = Configuration.newInstance()
Native2AsciiWrapper n2a = Native2AsciiWrapper.newInstance()
conf.set("saskia.dbpedia.mode", "local")
conf.set("saskia.dbpedia.local.files", "./Jose_Saramago.nt")
DBpediaAPI db = DBpediaAPI.newInstance(conf)

def answer = db.sparql("SELECT ?s ?p ?o WHERE {?s ?p ?o}")
answer.each{ a-> 
   println "${a.s}\t${a.p}\t${a.o}"
}