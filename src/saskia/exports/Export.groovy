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

package saskia.exports

import rembrandt.obj.Document
import saskia.bin.Configuration
import saskia.db.obj.*
import saskia.db.database.*
import saskia.db.DocStatus

import org.apache.log4j.Logger
import java.util.regex.*
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException

abstract class Export {

	static Logger log = Logger.getLogger("Exports")
	static String fileseparator = System.getProperty("file.separator")
	static Configuration conf = Configuration.newInstance()

	Collection collection
	SaskiaDB db
	File directory
	OutputStreamWriter out
	HashMap status
	String lang
	String encoding
	Integer docs
	
	public Export() {
		this.status = [exported:0, skipped:0, failed:0]
	}

	abstract exporter();

	public String statusMessage() {
		String message = "Done."
		status.each{k, v->
			if (v) {
				message << "$v doc(s) $k. "
			}
		}
		return message
	}	
}