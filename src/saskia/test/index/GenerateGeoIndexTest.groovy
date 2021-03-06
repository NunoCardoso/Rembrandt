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
		number_docs = collection.getNumberOfRembrandtedDocuments()
	}

	void testMain() {

		/** ITERATOR **/
		for (int i = number_docs; i > 0; i -= DOC_POOL_SIZE) {

			int limit = (i > DOC_POOL_SIZE ? DOC_POOL_SIZE : i)
			log.debug "Initial batch size: ${number_docs} Remaining: $i Next pool size: $limit"

			// problema: isto tem de ser feito quando há doc_latest_geo_signature nulls.

			Map rdocs_rdoc = RembrandtedDoc.getBatchDocsAndNEsFromRDOCToGenerateGeoSignatures(collection, limit)
			Map rdocs_pool = RembrandtedDoc.getBatchDocsAndNEsFromPoolToGenerateGeoSignatures(collection, limit)

			int fail = 0

			rdocs_rdoc.each{doc_id, rdoc ->
				def pool = rdocs_pool[doc_id]
				if (rdoc.lang != pool.lang) {fail++; log.info "doc $doc_id: rdoc.lang = ${rdoc.lang}, pool.lang=${pool.lang}"}
				if (rdoc.doc_original_id != pool.doc_original_id) {fail++; log.info "doc $doc_id: rdoc.doc_original_id = ${rdoc.doc_original_id}, pool.doc_original_id=${pool.doc_original_id}"}
				rdoc.nes.eachWithIndex{rdoc_ne, i2 ->
					def pool_ne = pool.nes[i2]
					if (rdoc_ne != pool_ne) {
						fail++
						if (rdoc_ne.section != pool_ne.section) log.info "doc $doc_id: rdoc_ne.section = ${rdoc_ne.section}, pool_ne.section = ${pool_ne.section}"
						if (rdoc_ne.sentence != pool_ne.sentence) log.info "doc $doc_id: rdoc_ne.sentence = ${rdoc_ne.sentence}, pool_ne.sentence = ${pool_ne.sentence}"
						if (rdoc_ne.term != pool_ne.term) log.info "doc $doc_id: rdoc_ne.term = ${rdoc_ne.term}, pool_ne.term = ${pool_ne.term}"
						if (rdoc_ne.name != pool_ne.name) log.info "doc $doc_id: rdoc_ne.name = ${rdoc_ne.name}, pool_ne.name = ${pool_ne.name}"
						if (rdoc_ne.type != pool_ne.type) log.info "doc $doc_id: rdoc_ne.type = ${rdoc_ne.type}, pool_ne.type = ${pool_ne.type}"
						if (rdoc_ne.subtype != pool_ne.subtype) log.info "doc $doc_id: rdoc_ne.subtype = ${rdoc_ne.subtype}, pool_ne.subtype = ${pool_ne.subtype}"
						if (rdoc_ne.entity != pool_ne.entity) log.info "doc $doc_id: rdoc_ne.entity = ${rdoc_ne.entity}, pool_ne.entity = ${pool_ne.entity}"
						if (rdoc_ne.dbpediaClass != pool_ne.dbpediaClass) log.info "doc $doc_id: rdoc_ne.dbpediaClass = ${rdoc_ne.dbpediaClass}, pool_ne.dbpediaClass = ${pool_ne.dbpediaClass}"

						//log.info "doc $doc_id: ne i=$i2: rdoc_ne=$rdoc_ne, pool_ne=$pool_ne"}
					}
				}
			}
			assert fail == 0
		}
	}
}

