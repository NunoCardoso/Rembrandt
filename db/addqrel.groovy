// groovy addqrel.groovy ../resources/eval/geoclef/qrels/qrelsGeoCLEFEN2005.txt

import groovy.sql.*


if (args.size() != 3) {
	println "Usage: groovy addqrel [qrel_file] [query_qcollection] [qrel_owner]"
}
def query_collection = Integer.parseInt(args[1])
def query_owner = Integer.parseInt(args[2])

def db
String driver = 'com.mysql.jdbc.Driver'
String url = 'jdbc:mysql://127.0.0.1'
String name = 'saskia'
String user = 'saskia'
String password = 'saskia'
String param = 'useUnicode=yes&characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true'

println "Connecting to $driver:$url/$name?${param}"
try {
	db = Sql.newInstance("$url/${name}?${param}", user, password, driver)
}  catch (Exception e) {
	println "Error in db"
	System.exit(0)
}
if (db) {
	println "db on!"
} else {
	println "Error in db"
	System.exit(0)	
}

new File(args[0]).eachLine{l -> 
	def m = l =~ /^(\d+) (\d) (.*) (\d)$/
	if (m.matches()) {
		def qid = Integer.parseInt(m.group(1))
		def qdoc = m.group(3)
		def qqrel = Integer.parseInt(m.group(4))

		def que_id 
		def que_doc
		db.eachRow("SELECT que_id FROM query where que_original_id=? and "+
		"que_qcollection=?", [qid, query_collection], {row -> 
			que_id = row[0]
		})
		
		db.eachRow("SELECT doc_id FROM doc where doc_original_id=?", [qdoc], {row -> 
			que_doc = row[0]
		})
		
//		println "INSERT INTO qrel values ($que_id, $que_doc, $query_owner, $qqrel)"
	 db.executeInsert("INSERT INTO qrel(qrl_query, qrl_doc, qrl_user, qrl_qrel) values "+
	"(?,?,?,?)", [que_id, que_doc,query_owner,qqrel])
	
		
	} else {
		println "Can't read line "+l
	}
}
