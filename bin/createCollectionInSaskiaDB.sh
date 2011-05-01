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
DEFAULT_COLLECTION_NAME = "My first collection"
DEFAULT_LANG = "en"
DEFAULT_PERMISSION="rwar--r--"
DEFAULT_COMMENT='My first collection'

SaskiaDB saskia_db = new DBValidator().validate(null, DEFAULT_SASKIA_DB, true)

CollectionTable ct = saskia_db.getDBTable("CollectionTable")
UserTable ut = saskia_db.getDBTable("UserTable")
Collection c = Collection.createNew(ct, [])

println "What is the collection name? (Default: $DEFAULT_COLLECTION_NAME)"
print "> "
String collection_name  = input.readLine().trim()
if (!collection_name) collection_name = DEFAULT_COLLECTION_NAME
c.col_name = collection_name

// refresh user cache
ut.refreshUserCache()

println "Owned by which user? (Available: ${ut.cacheIDUser.values()})"
println "Please enter either the id or the login."
print "> "
String user_id_or_login  = input.readLine().trim()
User user = ut.getFromIDorLogin(user_id_or_login)
if (!user) {
	println "Please enter a valid user. Exiting."
	System.exit(0)
}
c.col_owner = user

println "Collection language? (Default: ${DEFAULT_LANG})"
print "> "
String lang  = input.readLine().trim()
if (!lang) lang = DEFAULT_LANG
c.col_lang = lang

println "Collection permissions (user/group/others)? (Default: ${DEFAULT_PERMISSION})"
print "> "
String permission  = input.readLine().trim()
if (!permission) permission = DEFAULT_PERMISSION
c.col_permission = permission

println "Collection comments? (Default: ${DEFAULT_COMMENT})"
print "> "
String comment  = input.readLine().trim()
if (!comment) comment = DEFAULT_COMMENT
c.col_comment = comment

c.col_id = c.addThisToDB()
println "Collection added: $c"

