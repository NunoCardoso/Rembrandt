/* Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package saskia.db

import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * This is the DB connect singleton.
 */
class WikipediaDB extends DB {
 
    static WikipediaDB _this
    static conf = Configuration.newInstance()

    String default_db_driver = 'com.mysql.jdbc.Driver'
    String default_db_url = 'jdbc:mysql://127.0.0.1'
    String default_db_name = 'wikipedia'
    String default_db_user = 'wikipedia'
    String default_db_password = 'saskia'
    String default_db_param = 'useUnicode=yes&characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true' 
 
	 String default_conf_driver = 'saskia.wikipedia.db.driver'
	 String default_conf_url = 'saskia.wikipedia.db.url'
	 String default_conf_name = 'saskia.wikipedia.db.name'
	 String default_conf_user = 'saskia.wikipedia.db.user'
	 String default_conf_password = 'saskia.wikipedia.db.password'
	 String default_conf_param = 'saskia.wikipedia.db.param'
	
	/**
mysql> desc redirect;
+--------------+-----------------+------+-----+---------+-------+
| Field        | Type            | Null | Key | Default | Extra |
+--------------+-----------------+------+-----+---------+-------+
| rd_from      | int(8) unsigned | NO   | PRI | 0       |       | 
| rd_namespace | int(11)         | NO   | MUL | 0       |       | 
| rd_title     | varchar(255)    | NO   |     |         |       | 
+--------------+-----------------+------+-----+---------+-------+

mysql> desc page;   
+-----------------------+---------------------+------+-----+---------+----------------+
| Field                 | Type                | Null | Key | Default | Extra          |
+-----------------------+---------------------+------+-----+---------+----------------+
| page_id               | int(8) unsigned     | NO   | PRI | NULL    | auto_increment | 
| page_namespace        | int(11)             | NO   | MUL | 0       |                | 
| page_title            | varchar(255)        | NO   | MUL |         |                | 
| page_restrictions     | tinyblob            | NO   |     | NULL    |                | 
| page_counter          | bigint(20) unsigned | NO   |     | 0       |                | 
| page_is_redirect      | tinyint(1) unsigned | NO   |     | 0       |                | 
| page_is_new           | tinyint(1) unsigned | NO   |     | 0       |                | 
| page_random           | double unsigned     | NO   | MUL | 0       |                | 
| page_touched          | varchar(14)         | NO   |     |         |                | 
| page_latest           | int(8) unsigned     | NO   |     | 0       |                | 
| page_len              | int(8) unsigned     | NO   | MUL | 0       |                | 
| page_no_title_convert | tinyint(1)          | NO   |     | 0       |                | 
+-----------------------+---------------------+------+-----+---------+----------------+
12 rows in set (0.06 sec)

mysql> desc categorylinks;
+--------------+-----------------+------+-----+-------------------+-------+
| Field        | Type            | Null | Key | Default           | Extra |
+--------------+-----------------+------+-----+-------------------+-------+
| cl_from      | int(8) unsigned | NO   | PRI | 0                 |       | 
| cl_to        | varchar(255)    | NO   | PRI |                   |       | 
| cl_sortkey   | varchar(255)    | NO   | MUL |                   |       | 
| cl_timestamp | timestamp       | NO   |     | CURRENT_TIMESTAMP |       | 
+--------------+-----------------+------+-----+-------------------+-------+
4 rows in set (0.05 sec)

mysql> desc pagelinks;
+--------------+-----------------+------+-----+---------+-------+
| Field        | Type            | Null | Key | Default | Extra |
+--------------+-----------------+------+-----+---------+-------+
| pl_from      | int(8) unsigned | NO   | MUL | 0       |       | 
| pl_namespace | int(11)         | NO   |     | 0       |       | 
| pl_title     | varchar(255)    | NO   | MUL |         |       | 
+--------------+-----------------+------+-----+---------+-------+
3 rows in set (0.00 sec)

mysql> desc pt_text;
+-----------+-----------------+------+-----+---------+----------------+
| Field     | Type            | Null | Key | Default | Extra          |
+-----------+-----------------+------+-----+---------+----------------+
| old_id    | int(8) unsigned | NO   | PRI | NULL    | auto_increment | 
| old_text  | mediumblob      | NO   |     | NULL    |                | 
| old_flags | tinyblob        | NO   |     | NULL    |                | 
+-----------+-----------------+------+-----+---------+----------------+
3 rows in set (0.00 sec)

*/

//force it to be of namespace 0
	def static String getSelectIDandTitleFromRedirectionTitle(String lang = conf.get("global.lang")) {
		return "select p2.page_id, p2.page_title from ${lang}_page as p2, ${lang}_page as p, "+
		"${lang}_redirect as r where p.page_namespace=0 and p.page_id=r.rd_from "+
		"and r.rd_title=p2.page_title and r.rd_namespace = 0 and "+
		"p2.page_namespace=0 and p.page_title=?"
	}

	def static String getSelectIDandTitleFromRedirectionID(String lang = conf.get("global.lang")) {
		return "select p.page_id, p.page_title from ${lang}_page as p, "+
		"${lang}_redirect as r where r.rd_namespace = 0 and r.rd_from = ? "+
		"and r.rd_title=p.page_title and p.page_namespace=0"
	}
		
	def static String getSelectCategoriesFromPageDocumentFromID(String lang = conf.get("global.lang")) {
		return "select cl_to from ${lang}_categorylinks where cl_from = ?"
	}
	
	def static String getSelectCategoriesFromPageDocumentFromTitle(String lang = conf.get("global.lang")) {
		return "select cl_to from ${lang}_categorylinks, ${lang}_page where cl_from=page_id "+
		"and page_namespace=0 and page_title=?"
	}
			
	// force it to be of namespace 14
	def static String getSelectCategoryDocumentIDandTitleFromPageDocumentID(String lang = conf.get("global.lang")) {
		return "select p2.page_id, p2.page_title from ${lang}_page as p2, ${lang}_page as p where "+
	"p.page_title=p2.page_title and p2.page_namespace=14 and p.page_id=?"		
	}
	
	def static String getSelectCategoryDocumentIDandTitleFromPageDocumentTitle(String lang = conf.get("global.lang")) {
		return "select page_id, page_title from ${lang}_page where page_namespace=14 and page_title=?"
	}
				
	def static String getSelectTitleFromPageID(String lang = conf.get("global.lang")) {
		return "select page_title from ${lang}_page where page_id=?"
	}
	
	// force it to be of namespace 0
	def static String getSelectIDFromPageTitle(String lang = conf.get("global.lang")) {
		return "select page_id, page_is_redirect from ${lang}_page where page_title=? and "+
		"page_namespace=0"
	}
	
	// é preciso forçar p2.page_namespace=0 para não haver lixo de outros namespaces	
	def static String getSelectInlinkIDandTitleFromPageID(String lang = conf.get("global.lang")) {
		return "select p2.page_id, p2.page_title from ${lang}_pagelinks as pl, ${lang}_page as p, "+
	    "${lang}_page as p2 where pl.pl_title=p.page_title and pl_namespace=p.page_namespace "+
	    "and pl_from=p2.page_id and p2.page_namespace=0 and p.page_id=?"
	}
  // To avoid by title, because we don't know if it is a page doc or a category doc. 	
  //  def static String selectInlinkIDFromPageTitle = "select pl_from from "+
  //  conf.get('db.table.pagelinks')+" where pl_namespace = 0 and pl_title=?"
	 			
	def static String getSelectOutlinkIDandTitlePagesFromPageID(String lang = conf.get("global.lang")) {
		return "select p.page_id, p.page_title from ${lang}_pagelinks as pl, ${lang}_page as p where "+
	    "pl.pl_namespace=p.page_namespace and p.page_title=pl.pl_title and pl_from=?"
	}
  // forcei a namespace 0...
	def static String getSelectOutlinkIDandTitlePagesFromPageTitle(String lang = conf.get("global.lang")) {
		return "select p.page_id, p.page_title from ${lang}_pagelinks as pl, ${lang}_page as p where "+
	 	"pl.pl_namespace=p.page_namespace and p.page_id=pl_from and p.page_namespace=0 and pl_title=?"
	}

	def static String getSelectPageIdTitleWithCategory(String lang = conf.get("global.lang")) {
		return "select page_id, page_title from ${lang}_page, ${lang}_categorylinks where "+
		"page_id=cl_from and cl_to=?"
	}
	
	def static String getSelectCategoriesFromRegex(String regex, String lang = conf.get("global.lang")) { 
		//COLLATE instructions are needed to make consistency on case insensitive regexes
		return "select page_id, page_title from ${lang}_page where page_namespace=14 and "+
		"page_title RLIKE \""+regex.replaceAll(" ","_")+"\" COLLATE utf8_general_ci"
	}
	
	def static String getLanguageLink(String sourceLang = conf.get("global.lang")) {
	    return "select ll_title from ${sourceLang}_langlinks, ${sourceLang}_page where ll_lang=? "+
	 	"and ll_from = page_id and page_title=?"
	}
	
	def static String getRawWikipediaTextFromWikipediaPageID(String lang = conf.get("global.lang")) {
		return "SELECT ${lang}_text.old_text from ${lang}_text, ${lang}_page where "+
		"${lang}_page.page_latest=${lang}_text.old_id and ${lang}_page.page_id=?"
	}

	private WikipediaDB(conf) {
		super(conf)
	 }

    public static WikipediaDB newInstance() {
	    if (_this == null) {
			_this = new WikipediaDB(WikipediaDB.conf)
			_this.connect()
		  }
		  return _this
	} 
}