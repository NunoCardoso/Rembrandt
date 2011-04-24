#!/usr/bin/env groovy

import saskia.bin.Configuration
import saskia.db.SaskiaTestDB
import groovy.sql.Sql

Configuration conf = Configuration.newInstance()
SaskiaTestDB saskia_test_db 

saskia_test_db = new SaskiaTestDB(conf)

// don't connect now. First, let's create the database
// with the configuration parameters for the saskia test db.

String driver = conf.get(saskia_test_db.getDefault_conf_driver(), 
       saskia_test_db.getDefault_db_driver())

String url = conf.get(saskia_test_db.getDefault_conf_url(),
       saskia_test_db.getDefault_db_url())
        
String name = conf.get(saskia_test_db.getDefault_conf_name(),
       saskia_test_db.getDefault_db_name())

String user = conf.get(saskia_test_db.getDefault_conf_user(),
       saskia_test_db.getDefault_db_user())
        
String password = conf.get(saskia_test_db.getDefault_conf_password(),
       saskia_test_db.getDefault_db_password())
        
String param = conf.get(saskia_test_db.getDefault_conf_param(),
       saskia_test_db.getDefault_db_param())

param = saskia.util.XMLUtil.decodeAmpersand(param)
        
// ask for mysql root password

println "What is the root password for MySQL? (blank for none) "
BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
String db_root_password = input.readLine().trim()

Sql sql_db_root
try {
   sql_db_root = Sql.newInstance("$url/?${param}", "root", db_root_password, driver)
} catch(Exception e) {
   println "Couldn't connect to DB: "+e.getMessage()
   println "Is the DB server running? You should check it. Exiting."
   System.exit(0)
}

sql_db_root.execute "DROP DATABASE IF EXISTS ${name}".toString()
println "Database ${name} dropped."

sql_db_root.execute "CREATE DATABASE ${name}".toString()
println	"Database ${name} created."

sql_db_root.execute "GRANT ALL PRIVILEGES ON ${name}.* TO '${user}'@'localhost' IDENTIFIED BY '${password}'".toString()
println "Database permissions granted on ${name} to ${user}@localhost, password ${password}."

println "Logging out from root DB connection, establishing connection to ${name}..."

Sql sql_db_test
try {
   sql_db_test = Sql.newInstance("$url/${name}?${param}", user, password, driver)
} catch(Exception e) {
   println "Couldn't connect to DB: "+e.getMessage()
   println "Is the DB server running? You should check it. Exiting."
   System.exit(0)
}

String database_default_schema_file = conf.get("rembrandt.home.dir",".")
database_default_schema_file += "/db/Saskia_db_schema_latest.sql"

println "What is the Database schema file? (default: ${database_default_schema_file})"
input = new BufferedReader(new InputStreamReader(System.in))
String schema_file = input.readLine().trim()
if (!schema_file) schema_file = database_default_schema_file

println "Loading Database schema ${schema_file}..."
String schema = ""

// remove "SET " and comments from the sql file.
new File(schema_file).eachLine{
  if (!it.startsWith("SET ") && !it.startsWith("-- ")) schema += it
} 
List schemas = schema.split(";")

schemas.each{
   // let's just log the first part of the string
   println it?.findAll(/^[^`]+`[^`]+`/)?.getAt(0)?.trim()
   sql_db_test.execute it
}

// let's load the test collection: CD

