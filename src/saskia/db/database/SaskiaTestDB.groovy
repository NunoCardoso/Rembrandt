/** This file is part of REMBRANDT - Named Entity Recognition Software
 *  (http://xldb.di.fc.ul.pt/Rembrandt)
 *  Copyright (c) 2008-2009, Nuno Cardoso, University of Lisboa and Linguateca.
 *
 *  REMBRANDT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  REMBRANDT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with REMBRANDT. If not, see <http://www.gnu.org/licenses/>.
 */

package saskia.db.database

import saskia.bin.Configuration

/**
 * @author Nuno Cardoso
 * This is the DB connect singleton.
 */
class SaskiaTestDB extends SaskiaDB {

	static SaskiaTestDB _this
	static String LABEL = "SaskiaTest"

	String default_db_driver = 'com.mysql.jdbc.Driver'
	String default_db_url = 'jdbc:mysql://127.0.0.1'
	String default_db_name = 'saskia_test'
	String default_db_user = 'saskia'
	String default_db_password = 'saskia'
	String default_db_param = 'useUnicode=yes&characterEncoding=UTF8&characterSetResults=UTF8&autoReconnect=true'

	String default_conf_driver = 'saskia.test.db.driver'
	String default_conf_url = 'saskia.test.db.url'
	String default_conf_name = 'saskia.test.db.name'
	String default_conf_user = 'saskia.test.db.user'
	String default_conf_password = 'saskia.test.db.password'
	String default_conf_param = 'saskia.test.db.param'

	private SaskiaTestDB(Configuration conf) {
		super(conf)
	}

	private resetDatabase() {
		//	super(conf)
	}

	/**
	 * Get new instance of the DBConnect
	 * @return new instance of DBConnect
	 */
	public static SaskiaTestDB newInstance(Configuration conf = null) {
		if (!conf) conf = Configuration.newInstance()
		if (_this == null) {
			_this = new SaskiaTestDB(conf)
			_this.connect()
		}
		return _this
	}

	public main(args) {
	}
}