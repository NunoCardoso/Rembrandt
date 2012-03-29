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

package saskia.test.index

import org.apache.log4j.*
import org.junit.*
import org.junit.runner.*

import saskia.bin.*
import saskia.db.obj.Collection
/**
 * @author Nuno Cardoso
 * Tester for WikipediaAPI.
 */
class GenerateGeoIndexTest extends GroovyTestCase {

	static Logger log = Logger.getLogger("JUnitTest")
	static Configuration conf = Configuration.newInstance()
	Collection collection
	String lang = "pt"
	static final int DOC_POOL_SIZE = 100
	int number_docs

	public GenerateGeoIndexTest() {
		collection = Collection.getFromID(8)
		if (!collection) {
			log.error "Don't know collection 8. Exiting."
			System.exit(0)
		}
		number_docs = collection.getNumberOfDocuments()
	}

	void testMain() {

		/** ITERATOR **/
		for (int i = number_docs; i > 0; i -= DOC_POOL_SIZE) {

			int limit = (i > DOC_POOL_SIZE ? DOC_POOL_SIZE : i)
			log.debug "Initial batch size: ${number_docs} Remaining: $i Next pool size: $limit"

			// problema: isto tem de ser feito quando há doc_latest_geo_signature nulls.

			Map docs = Doc.getBatchDocsAndNEsFromPoolToGenerateGeoSignatures(collection, limit)

			int fail = 0

			docs.each{doc_id, doc ->
				def pool = docs[doc_id]
				if (doc.lang != pool.lang) {fail++; log.info "doc $doc_id: doc.lang = ${doc.lang}, pool.lang=${pool.lang}"}
				if (doc.doc_original_id != pool.doc_original_id) {fail++; log.info "doc $doc_id: doc.doc_original_id = ${doc.doc_original_id}, pool.doc_original_id=${pool.doc_original_id}"}
				doc.nes.eachWithIndex{doc_ne, i2 ->
					def pool_ne = pool.nes[i2]
					if (doc_ne != pool_ne) {
						fail++
						if (doc_ne.section != pool_ne.section) log.info "doc $doc_id: doc_ne.section = ${doc_ne.section}, pool_ne.section = ${pool_ne.section}"
						if (doc_ne.sentence != pool_ne.sentence) log.info "doc $doc_id: doc_ne.sentence = ${doc_ne.sentence}, pool_ne.sentence = ${pool_ne.sentence}"
						if (doc_ne.term != pool_ne.term) log.info "doc $doc_id: doc_ne.term = ${doc_ne.term}, pool_ne.term = ${pool_ne.term}"
						if (doc_ne.name != pool_ne.name) log.info "doc $doc_id: doc_ne.name = ${doc_ne.name}, pool_ne.name = ${pool_ne.name}"
						if (doc_ne.type != pool_ne.type) log.info "doc $doc_id: doc_ne.type = ${doc_ne.type}, pool_ne.type = ${pool_ne.type}"
						if (doc_ne.subtype != pool_ne.subtype) log.info "doc $doc_id: doc_ne.subtype = ${doc_ne.subtype}, pool_ne.subtype = ${pool_ne.subtype}"
						if (doc_ne.entity != pool_ne.entity) log.info "doc $doc_id: doc_ne.entity = ${doc_ne.entity}, pool_ne.entity = ${pool_ne.entity}"
						if (doc_ne.dbpediaClass != pool_ne.dbpediaClass) log.info "doc $doc_id: doc_ne.dbpediaClass = ${doc_ne.dbpediaClass}, pool_ne.dbpediaClass = ${pool_ne.dbpediaClass}"

						//log.info "doc $doc_id: ne i=$i2: doc_ne=$doc_ne, pool_ne=$pool_ne"}
					}
				}
			}
			assert fail == 0
		}
	}
}

