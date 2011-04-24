#!/usr/bin/env groovy

import saskia.bin.Configuration
import saskia.db.SaskiaTestDB
import saskia.imports.ImportGCSecondHAREM_Clean_2_SourceDocument
import groovy.sql.Sql

Configuration conf = Configuration.newInstance()
SaskiaTestDB saskia_test_db 

saskia_test_db = new SaskiaTestDB(conf)

// constants 
String DATABASE_DEFAULT_SCHEMA_FILE = conf.get("rembrandt.home.dir",".")
DATABASE_DEFAULT_SCHEMA_FILE += "/db/Saskia_db_schema_latest.sql"

String CD_COLLECTION_DEFAULT_FILE = conf.get("rembrandt.home.dir",".")
CD_COLLECTION_DEFAULT_FILE += "/resource/test/collecitons/CDSegundoHAREM.xml"
CD_COLLECTION_DEFAULT_ENCODING = "ISO-8859-1"

String CD_COLLECTION_NAME = ImportGCSecondHAREM_Clean_2_SourceDocument.DEFAULT_COLLECTION_NAME
String JUNIT_TEST_COLLECTION_NAME = "JUnit tests"

boolean CREATE_TEST_DATABASE = false
boolean CREATE_TEST_DATABASE_SCHEMA = false
boolean CREATE_USERS_AND_COLLECTIONS = false
boolean LOAD_CD_COLLECTION = true

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

if (CREATE_TEST_DATABASE) {        
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

}

if (CREATE_TEST_DATABASE_SCHEMA) {

println "What is the Database schema file? (default: ${DATABASE_DEFAULT_SCHEMA_FILE})"
input = new BufferedReader(new InputStreamReader(System.in))
String schema_file = input.readLine().trim()
if (!schema_file) schema_file = DATABASE_DEFAULT_SCHEMA_FILE

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

}

IF (CREATE_USERS_AND_COLLECTIONS) {

// let's create the guest and test user
sql_db_test.execute "INSERT INTO user VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
[0,'guest',1,';guest;',0, // id, login, enabled, groups, superuser
'Guest','','', 			  // firstname, lastname, email,
'','',						  // password, tmp_password, 
'db924ad035a9523bcf92358fcb2329dac923bf9c','',	// api_key, tmp_api_key, 
'0eab732cb13efab35bcab30342cf0aeb',					// pub_key,
0, 1, 0,						  // max_collections, max_tasks, max_docs_per_col, 
1000, 0, 0, new Date()]	  // max_daily_api_calls, current_api_calls, total_api_calls, date_last_api_call

println "User guest added."

sql_db_test.execute "INSERT INTO user VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
[0,'rembrandt',1,';rembrandt;',0,					// id, login, enabled, groups, superuser
'Rembrandt','van Rijn','rembrandt@vanrijn.com', // firstname, lastname, email,
saskia.io.User.createPassword('rembrandt'), saskia.io.User.createPassword('rembrandt'), 
saskia.io.User.createPassword('vanrijn'), saskia.io.User.createPassword('vanrijn'),
saskia.io.User.createPassword('rembrandt_pub_key'), 
10, 10, 100000, 				// max_collections, max_tasks, max_docs_per_col, 
1000, 0, 0, new Date()]    // max_daily_api_calls, current_api_calls, total_api_calls, date_last_api_call

println "User rembrandt added."
 
// let's create the CD and test collection
sql_db_test.execute "INSERT INTO collection VALUES(?,?,?,?,?,?)",
[0,CD_COLLECTION_NAME,
 2, 'pt','rwar--r--','Second HAREM\'s CD (http://www.linguateca.pt/HAREM)']

println "Collection ${CD_COLLECTION_NAME} added."

sql_db_test.execute "INSERT INTO collection VALUES(?,?,?,?,?,?)",
[0,"JUnit tests", 2, 'pt','rwar--r--','Collection for JUnit tests']

println "Collection ${JUNIT_TEST_COLLECTION_NAME} added."

}

if (LOAD_CD_COLLECTION) {

// Load the CD collection
ImportGCSecondHAREM_Clean_2_SourceDocument importer = new ImportGCSecondHAREM_Clean_2_SourceDocument()

println "Where is the CD collection file? (default: ${CD_COLLECTION_DEFAULT_FILE})"
input = new BufferedReader(new InputStreamReader(System.in))
String cd_collection_file = input.readLine().trim()
if (!cd_collection_file) cd_collection_file = CD_COLLECTION_DEFAULT_FILE

println "What is the CD file encoding? (default: ${CD_COLLECTION_DEFAULT_ENCODING})"
input = new BufferedReader(new InputStreamReader(System.in))
String cd_collection_encoding = input.readLine().trim()
if (!cd_collection_encoding) cd_collection_encoding = CD_COLLECTION_DEFAULT_ENCODING


	importer.setCollection(collection)
		log.info "Collection: $collection"		
		importer.setFile(file)
		importer.setEncoding(encoding)
		log.info "File: $file <"+encoding+"> "	
		
		importer.prepareInputStreamReader()
		HashMap status = importer.importDocs()
		
		
		
resources/test/collections

