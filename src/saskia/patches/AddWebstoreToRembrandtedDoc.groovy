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
package saskia.patches

import org.apache.commons.cli.*
import org.apache.log4j.*

import pt.tumba.webstore.*
import saskia.db.SaskiaWebstore
import saskia.db.database.SaskiaMainDB
import saskia.db.obj.Collection

/**
 * @author Nuno Cardoso
 * 1 2 7 8
 */
class AddWebstoreToRembrandtedDoc {

	static void main(args) {

		Options o = new Options()
		String fileseparator = System.getProperty("file.separator")

		o.addOption("col", true, "Collection name or ID")
		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)


		if (!cmd.hasOption("col")) {
			println "No --col arg. Please specify the collection. Exiting."
			System.exit(0)
		}

		Collection collection
		try {
			collection = Collection.getFromID(Long.parseLong(cmd.getOptionValue("col")))
		} catch(Exception e) {
			collection = Collection.getFromName(cmd.getOptionValue("col"))
		}
		if (!collection) {
			println "Don't know collection ${cmd.getOptionValue('col')} to parse documents on. Exiting."
			System.exit(0)
		}

		println "Initializing collection $collection"
		SaskiaMainDB db = SaskiaMainDB.newInstance()

		String file = System.getenv()["WEBSTORE_CONFIG_FILE"]
		WebStore ws = new WebStore(new File(file))
		println "Webstore: $ws"
		ws.setDefaultCompressMode(WebStore.ZLIB)
		Volume[] volumes = ws.getVolumes(WebStore.WRITABLE)
		Volume volume
		volumes.each{v ->
			if (v.volId().equalsIgnoreCase(SaskiaWebstore.VOLUME_RDOC)) volume = v
		}
		println "Volume: $volume"

		int limit = 100
		int counter = 0
		int remaining = 0
		boolean first = true
		int processed_ok = 0
		int processed_ko = 0

		Map batch

		while ( first || remaining > 0 ) {
			batch = [:]
			if (first) {
				println "Starting with a batch of ${limit} docs."
				first = false
			} else {
				println "Getting batch ${counter} to ${limit+counter}, ${remaining} docs remaining."
			}

			// DON'T USE OFFSET. By issuing webstore=null, I'm already offseting it
			String select = "SELECT SQL_CALC_FOUND_ROWS doc_id, doc_original_id, doc_lang, doc_webstore FROM "+
					" doc WHERE doc_collection=? AND doc_webstore IS NULL "+
					"LIMIT $limit UNION SELECT FOUND_ROWS(), '%%%TOTAL%%%', '', ''"
			db.getDB().eachRow(select, [collection.col_id], {row ->

				if (row['doc_original_id'] == "%%%TOTAL%%%") {
					remaining = (int)row['doc_id']
				} else {
					long id = row['doc_id']
					db.getDB().eachRow("SELECT rdoc_body from rembrandted_doc where rdoc_doc=?", [id], {row2 ->
						batch[id] = row2['rdoc_body']
					})

					counter++
				}
			})

			//println batch
			try {
				db.getDB().withTransaction{
					batch.each{id, html ->
						try {
							Content content = new Content(html.getBytes())
							// store a content using the regular option
							Key key = ws.store(content, volume)
							String keyString = key.toString()
							// println "Key: $keyString"
							db.getDB().executeUpdate("UPDATE doc set doc_webstore=? where doc_id=?",
									[keyString, id])
							processed_ok++
						}  catch (Exception we) {
							println we.getMessage()
							processed_ko++
						}
					}
				}
			} catch (Exception e) {
				println e.getMessage()
				processed_ko++
			}
		}

		println "Processed ok: $processed_ok Ko: $processed_ko"
	}
}
