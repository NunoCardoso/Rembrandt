#!/usr/bin/env groovy

import saskia.bin.Configuration
import saskia.db.database.*
import saskia.db.table.*
import saskia.db.obj.*
import saskia.imports.*
import groovy.sql.Sql
import saskia.util.validator.*

Configuration conf = Configuration.newInstance()
BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
String answer

DEFAULT_SASKIA_DB = "main"

DEFAULT_ENABLED = "1"
DEFAULT_GROUPS = "rembrandt"
DEFAULT_SUPERUSER = "0"

DEFAULT_MAX_COLLECIONS="10"
DEFAULT_MAX_TASKS="10"
DEFAULT_MAX_DOCS_PER_COLLECTION="100000"
DEFAULT_MAX_DAILY_API_CALLS="1000"

SaskiaDB saskia_db = new DBValidator().validate(null, DEFAULT_SASKIA_DB, true)

UserTable ut = saskia_db.getDBTable("UserTable")
User u = User.createNew(ut, [])

print "Login? > "
String login  = input.readLine().trim()
if (!login) {
	println "Please enter a valid login. Exiting."
	System.exit(0)
}
u.usr_login = login

println "User enabled (0/1)? (Default: ${DEFAULT_ENABLED})"
print "> "
String enabled  = input.readLine().trim()
if (!enabled) enabled = DEFAULT_ENABLED
u.usr_enabled = Integer.parseInt(enabled)

println "User groups? (Separate grouplist with ';' Default: ${u.usr_login})"
print "> "
String groups  = input.readLine().trim()
if (!groups) groups = u.usr_login
if (!groups.startsWith(";")) groups = ";"+groups 
if (!groups.endsWith(";")) groups = groups+";"
u.usr_groups = groups

println "Superuser (0/1)? (Default: ${DEFAULT_SUPERUSER})"
print "> "
String su  = input.readLine().trim()
if (!su) su = DEFAULT_SUPERUSER
u.usr_superuser = Integer.parseInt(su)


print "First name? > "
String firstname  = input.readLine().trim()

print "Last name? > "
String lastname  = input.readLine().trim()

print "Email? > "
String email  = input.readLine().trim()

u.usr_firstname = firstname
u.usr_lastname = lastname
u.usr_email = email

println "Password? (will store only its MD5 signature) "
print "> "
String password  = input.readLine().trim()

u.usr_password = UserTable.createPassword(password)
u.usr_tmp_password = u.usr_password

String api_key = UserTable.createPassword("rembrandt"+u.usr_login+password)
println "Generating API_KEY "+api_key
u.usr_api_key= api_key
u.usr_tmp_api_key= u.usr_api_key

String pub_key = UserTable.createPassword("pubkey"+u.usr_login)
println "Generating PUB_KEY "+pub_key
u.usr_pub_key= pub_key

println "Maximum collections for this user? (Default: $DEFAULT_MAX_COLLECIONS) "
print "> "
String max_col  = input.readLine().trim()
if (!max_col) max_col = DEFAULT_MAX_COLLECIONS
u.usr_max_number_collections = Integer.parseInt(max_col)

println "Maximum tasks for this user? (Default: $DEFAULT_MAX_TASKS) "
print "> "
String max_tasks  = input.readLine().trim()
if (!max_tasks) max_tasks = DEFAULT_MAX_TASKS
u.usr_max_number_tasks = Integer.parseInt(max_tasks)

println "Maximum docs per collection? (Default: $DEFAULT_MAX_DOCS_PER_COLLECTION) "
print "> "
String usr_max_docs_per_collection  = input.readLine().trim()
if (!usr_max_docs_per_collection) usr_max_docs_per_collection = DEFAULT_MAX_DOCS_PER_COLLECTION
u.usr_max_docs_per_collection = Integer.parseInt(usr_max_docs_per_collection)

println "Maximum daily api calls? (Default: $DEFAULT_MAX_DAILY_API_CALLS) "
print "> "
String usr_max_daily_api_calls  = input.readLine().trim()
if (!usr_max_daily_api_calls) usr_max_daily_api_calls = DEFAULT_MAX_DAILY_API_CALLS
u.usr_max_daily_api_calls = Integer.parseInt(usr_max_daily_api_calls)

u.usr_current_daily_api_calls = 0
u.usr_total_api_calls = 0
u.usr_date_last_api_call = new Date()

u.usr_id = u.addThisToDB()
println "User added: $u"
