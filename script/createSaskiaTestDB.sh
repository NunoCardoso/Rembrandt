#!/usr/bin/env groovy -Dfile.encoding=UTF-8

import saskia.bin.Configuration
import saskia.db.database.*
import saskia.db.table.*
import saskia.db.obj.*
import saskia.index.*
import saskia.imports.*
import saskia.util.validator.*
import groovy.sql.Sql

Configuration conf = Configuration.newInstance()
SaskiaTestDB saskia_test_db 
Sql sql_db_test
saskia_test_db = new SaskiaTestDB(conf)
String fileseparator = System.getProperty("file.separator")

// constants 
String DATABASE_DEFAULT_SCHEMA_FILE = conf.get("rembrandt.home.dir",".")
DATABASE_DEFAULT_SCHEMA_FILE += "/db/Saskia_db_schema_latest.sql"
String DATABASE_DEFAULT_DATA_FILE = conf.get("rembrandt.home.dir",".")
DATABASE_DEFAULT_DATA_FILE += "/db/Saskia_test_ne_data.sql"

String schema_file, data_file
Collection cd_collection

String CD_COLLECTION_DEFAULT_FILE = conf.get("rembrandt.home.dir",".")
CD_COLLECTION_DEFAULT_FILE += "/resources/test/collections/CDSegundoHAREM.xml"
CD_COLLECTION_DEFAULT_ENCODING = "UTF-8"

String CD_COLLECTION_NAME = "CD do Segundo HAREM"
String JUNIT_TEST_COLLECTION_NAME = "JUnit tests"

BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
String answer

boolean CREATE_TEST_DATABASE = true
boolean CREATE_TEST_DATABASE_SCHEMA = true
boolean CREATE_USERS_AND_COLLECTIONS = true
boolean LOAD_CD_COLLECTION = true
boolean ANNOTATE_TEST_COLLECTION = true
boolean ANALYSE_TEST_COLLECTION = true
boolean INDEX_TEST_COLLECTION = true

// processes

println ""
println "********************************************"
println "* Create Saskia Test DB - Rembrandt/Saskia *"
println "*                                          *"
println "* This script will create a test database  *"
println "* for JUnits and your own toy sandbox.     *"
println "********************************************"
println ""


println "Press Enter to continue"
input.readLine().trim()  
   
////// CODE ///////

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

println ""
println "1 === CREATE TEST DATABASE ==="
println "Create database (y/Y/yes) or skip it (n/N/no) ? (Default: y)"
print "> "
answer = input.readLine().trim()

if (answer.startsWith("n") || answer.startsWith("N")) CREATE_TEST_DATABASE = false
  
if (!CREATE_TEST_DATABASE) {        
   println "Skipping CREATE DATABASE..."
   try {
       sql_db_test = Sql.newInstance("$url/${name}?${param}", user, password, driver)
   } catch(Exception e) {
       println "Couldn't connect to DB: "+e.getMessage()
       println "Is the DB server running? You should check it. Exiting."
       System.exit(0)
   }

} else {
   println "Performing CREATE DATABASE..."

// ask for mysql root password

println "What is the root password for MySQL? (blank for none) "
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

println "Logging out from root DB connection, establishing 1st connection to ${name}..."

try {
   sql_db_test = Sql.newInstance("$url/${name}?${param}", user, password, driver)
} catch(Exception e) {
   println "Couldn't connect to DB: "+e.getMessage()
   println "Is the DB server running? You should check it. Exiting."
   System.exit(0)
}

}

saskia_test_db.connect()

println ""
println "2 === CREATE DATABASE SCHEMA ==="
println "Create database tables (y/Y/yes) or skip it (n/N/no) ? (Default: y)"
print "> "
answer = input.readLine().trim()
if (answer.startsWith("n") || answer.startsWith("N")) CREATE_TEST_DATABASE_SCHEMA = false

if (!CREATE_TEST_DATABASE_SCHEMA) {
   println "Skipping CREATE DATABASE SCHEMA..."
} else {
   println "Performing CREATE DATABASE SCHEMA..."

println "What is the Database schema file? (default: ${DATABASE_DEFAULT_SCHEMA_FILE})"
schema_file = input.readLine().trim()
if (!schema_file) schema_file = DATABASE_DEFAULT_SCHEMA_FILE

println "What is the Database data file? (default: ${DATABASE_DEFAULT_DATA_FILE})"
data_file = input.readLine().trim()
if (!data_file) data_file = DATABASE_DEFAULT_DATA_FILE

println "Loading Database schema ${schema_file}..."
String schema = ""

// remove "SET " and comments from the sql file.
new File(schema_file).eachLine{
  if (!it.startsWith("SET ") && !it.startsWith("-- ")) schema += it
} 
List schemas = schema.split(";")

if (!sql_db_test) {
   try {
       sql_db_test = Sql.newInstance("$url/${name}?${param}", user, password, driver)
  } catch(Exception e) {
       println "Couldn't connect to DB: "+e.getMessage()
       println "Is the DB server running? You should check it. Exiting."
      System.exit(0)
  }
}

schemas.each{
   // let's just log the first part of the string'
   println it?.findAll(/^[^`]+`[^`]+`/)?.getAt(0)?.trim()
   sql_db_test.execute it
}

}

println ""
println "3 === POPULATE DATABASE ==="
println "Populate database with basic collections / users / NE classifications (y/Y/yes) or skip it (n/N/no) ? (Default: y)"
print "> "
answer = input.readLine().trim()
if (answer.startsWith("n") || answer.startsWith("N")) CREATE_USERS_AND_COLLECTIONS  = false

if (!CREATE_USERS_AND_COLLECTIONS) {
   println "Skipping CREATE USERS and COLLECTIONS..."
} else {
   println "Performing CREATE USERS and COLLECTIONS..."


// let's create the guest and test user'
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
saskia.db.table.UserTable.createPassword('rembrandt'), 
saskia.db.table.UserTable.createPassword('rembrandt'), 
saskia.db.table.UserTable.createPassword('vanrijn'),
saskia.db.table.UserTable.createPassword('vanrijn'),
saskia.db.table.UserTable.createPassword('rembrandt_pub_key'), 
10, 10, 100000, 				// max_collections, max_tasks, max_docs_per_col, 
1000, 0, 0, new Date()]    // max_daily_api_calls, current_api_calls, total_api_calls, date_last_api_call

println "User rembrandt added."
 
// create the CD and test collection
sql_db_test.execute "INSERT INTO collection VALUES(?,?,?,?,?,?)",
[0,CD_COLLECTION_NAME,
 2, 'pt','rwar--r--','Second HAREM CD (http://www.linguateca.pt/HAREM)']

println "Collection ${CD_COLLECTION_NAME} added."

sql_db_test.execute "INSERT INTO collection VALUES(?,?,?,?,?,?)",
[0,"JUnit tests", 2, 'pt','rwar--r--','Collection for JUnit tests']

println "Collection ${JUNIT_TEST_COLLECTION_NAME} added."

new File(data_file).eachLine{
  sql_db_test.executeInsert it
}
println "Data from $data_file added."

}

println ""
println "4 === LOAD TEST COLLECTION ==="
println "Populate database with the Golden Collection from Second HAREM, a 129 document collection (y/Y/yes) or skip it (n/N/no) ? (Default: y)"
print "> "
answer = input.readLine().trim()
if (answer.startsWith("n") || answer.startsWith("N")) LOAD_CD_COLLECTION  = false

if (!LOAD_CD_COLLECTION) {
  println "Skipping LOAD TEST COLLECTION..."

  CollectionTable collectionTable = saskia_test_db.getDBTable("CollectionTable")
  cd_collection = collectionTable.getFromName(CD_COLLECTION_NAME)?.getAt(0)

} else {
   println "Performing LOAD TEST COLLECTION..."

// Load the CD collection
ImportGCSecondHarem_Clean_2_SourceDocument importer = new ImportGCSecondHarem_Clean_2_SourceDocument()

println "Where is the CD collection file? (default: ${CD_COLLECTION_DEFAULT_FILE})"
String cd_collection_file = input.readLine().trim()
if (!cd_collection_file) cd_collection_file = CD_COLLECTION_DEFAULT_FILE

println "What is the CD file encoding? (default: ${CD_COLLECTION_DEFAULT_ENCODING})"
String cd_collection_encoding = input.readLine().trim()
if (!cd_collection_encoding) cd_collection_encoding = CD_COLLECTION_DEFAULT_ENCODING

println "Importing test collection..."

importer.setDb(saskia_test_db)
println "DB: $saskia_test_db"

CollectionTable collectionTable = saskia_test_db.getDBTable("CollectionTable")
cd_collection = collectionTable.getFromName(CD_COLLECTION_NAME)?.getAt(0)
importer.setCollection(cd_collection)
println "Collection: $cd_collection"		
importer.setFile(new File(cd_collection_file))
importer.setEncoding(cd_collection_encoding)
println "File: $cd_collection_file <"+cd_collection_encoding+"> "	
		
importer.prepareInputStreamReader()
importer.importer()
println importer.statusMessage()

}

println ""
println "5. === ANNOTATE TEST COLLECTION ==="
println "Annotate with REMBRANDT the Golden Collection (y/Y/yes) or skip it (n/N/no) ? (Default: y)"
println "Warning: this may take a while..."
print "> "
answer = input.readLine().trim()
if (answer.startsWith("n") || answer.startsWith("N")) ANNOTATE_TEST_COLLECTION = false

if (!ANNOTATE_TEST_COLLECTION) {
  println "Skipping ANNOTATE TEST COLLECTION..."
} else {
   println "performing ANNOTATE TEST COLLECTION..."

   ImportSourceDocument_2_RembrandtedDocument importer = new ImportSourceDocument_2_RembrandtedDocument()
   importer.setDb(saskia_test_db)
   importer.setCollection(cd_collection)
   importer.setMode("multiple")
   importer.setDocs(130)
// importer.setDefaultanswer(answer)
   importer.prepareRembrandt()
   importer.importer()
   println importer.statusMessage()
}

println ""
println "6. === ANALYSE TEST COLLECTION ==="
println "Analyse the Golden Collection (y/Y/yes) or skip it (n/N/no) ? (Default: y)"
println "Warning: this may take a while..."
print "> "
answer = input.readLine().trim()
if (answer.startsWith("n") || answer.startsWith("N")) ANALYSE_TEST_COLLECTION = false

if (!ANALYSE_TEST_COLLECTION) {
  println "Skipping ANALYSE TEST COLLECTION..."
} else {
   println "performing ANALYSE TEST COLLECTION..."

   ImportRembrandtedDocument_2_NEPool importer = new ImportRembrandtedDocument_2_NEPool()
   importer.setDb(saskia_test_db)
   importer.setCollection(cd_collection)
   importer.setMode("multiple")
   importer.setDocs(130)
   importer.prepare()
   importer.importer()
   println importer.statusMessage()
}

String INDEX_ROOT_DIR = conf.get("saskia.index.dir","index")

println ""
println "7. === INDEX TEST COLLECTION ==="
println "Index the Golden Collection (y/Y/yes) or skip it (n/N/no) ? (Default: y)"
println "Warning: this may take a while..."
print "> "
answer = input.readLine().trim()
if (answer.startsWith("n") || answer.startsWith("N")) ANNOTATE_TEST_COLLECTION = false

if (!INDEX_TEST_COLLECTION) {
  println "Skipping INDEX TEST COLLECTION..."
} else {
   println "performing INDEX TEST COLLECTION..."

String indexdir = null

println "Creating term index with stem"
indexdir = IndexDirectoryValidator.buildIndexDirectory(
	 conf, cd_collection, GenerateTermIndexForCollection.termWithStemIndexDir)

def indexer = new GenerateTermIndexForCollection(
    cd_collection, indexdir, true)

indexer.index()
println indexer.statusMessage()

println "Creating term index without stem"
indexdir = IndexDirectoryValidator.buildIndexDirectory(
	conf, cd_collection, GenerateTermIndexForCollection.termWithoutStemIndexDir)

indexer = new GenerateTermIndexForCollection(
      cd_collection, indexdir, false)
indexer.index()
println indexer.statusMessage()

println "Creating NE index from the NE Pool"
indexdir = IndexDirectoryValidator.buildIndexDirectory(
	conf, cd_collection, GenerateNEIndexForCollection.NEIndexDirLabel)

indexer = new GenerateNEIndexForCollection(
   cd_collection, indexdir, "pool")
indexer.index()
println indexer.statusMessage()

println "Creating Entity index from the NE Pool"
indexdir = IndexDirectoryValidator.buildIndexDirectory(
	conf, cd_collection, GenerateEntityIndexForCollection.EntityIndexDirLabel)

indexer = new GenerateEntityIndexForCollection(
      cd_collection, indexdir, "pool")
indexer.index()
println indexer.statusMessage()

println  "Making GeoSignatures"
GenerateGeoSignatures ggs = new GenerateGeoSignatures()
ggs.setCollection(cd_collection)
ggs.setTag()
ggs.setDocs(130)
ggs.setSync("pool")
ggs.prepare()
ggs.generate()
println "Done. ${ggs.status}"

println "Creating Geo index from the NE Pool"
indexdir = IndexDirectoryValidator.buildIndexDirectory(
	conf, cd_collection, GenerateGeoIndexForCollection.geoIndexDirLabel)

indexer = new GenerateGeoIndexForCollection(
   cd_collection, indexdir)
indexer.index()
println indexer.statusMessage()

println  "Making TimeSignatures"
GenerateTimeSignatures gts = new GenerateTimeSignatures()
gts.setCollection(cd_collection)
gts.setTag()
gts.setDocs(130)
gts.setSync("pool")
gts.prepare()
gts.generate()
println "Done. ${gts.status}"

println "Creating Time index from the NE Pool"
indexdir = IndexDirectoryValidator.buildIndexDirectory(
	conf, cd_collection, GenerateTimeIndexForCollection.timeIndexDirLabel)

indexer = new GenerateTimeIndexForCollection(
   cd_collection, indexdir)
indexer.index()
println indexer.statusMessage()

}

