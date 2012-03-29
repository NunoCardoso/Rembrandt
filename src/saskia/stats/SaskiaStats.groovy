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

package saskia.stats

//import rembrandt
import java.text.SimpleDateFormat

import org.apache.log4j.*

import saskia.db.database.*
import saskia.db.obj.*
import saskia.db.table.*
import saskia.util.I18n

class SaskiaStats {

	static Logger log = Logger.getLogger("SaskiaStats")
	static String statsFrontPage = 'stats_front_page'
	SimpleDateFormat dateFormat
	SaskiaMainDB db
	I18n i18n
	static long NE_CACHE_REFRESH=(long)1000*60*60*24*30
	static long DOC_CACHE_REFRESH=(long)1000*60*60*24*30
	static long STATS_PAGE_CACHE_REFRESH=(long)1000*60*60*24*30

	public SaskiaStats() {
		db = SaskiaMainDB.newInstance()
		i18n =  I18n.newInstance()
	}

	public String renderFrontPage(Collection collection, String lang) {
		StringBuffer s = new StringBuffer()

		dateFormat = new SimpleDateFormat(i18n.dateformat[lang])
		log.debug "Asking cache for ${SaskiaStats.statsFrontPage}, ${collection_}, ${lang}"
		Cache c = db.getDBTable("CacheTable").getFromIDAndCollectionAndLang(SaskiaStats.statsFrontPage, collection_, lang)

		if (c && c.isCacheFresh()) {
			log.debug "Cache is new, let's use it."
			s.append "<DIV CLASS='stats-main'>\n"
			s.append c.cac_obj
			s.append "<DIV ID='stats-header'>${i18n.statslabel['generatedin'][lang]} "
			s.append dateFormat.format((Date)c.cac_date)+"</DIV>\n</DIV>\n"
		} else {
			log.debug "Cache is old or non-existent, time to refresh it."
			RenderFrontPageStats stat = new RenderFrontPageStats()
			String content = stat.render(collection_, lang)
			if (!c) c = new Cache()
			c.refreshCache(SaskiaStats.statsFrontPage, collection_, lang, content, STATS_PAGE_CACHE_REFRESH)
			s.append "<DIV CLASS='stats-main'>\n"
			s.append content
			s.append "<DIV ID='stats-header'>${i18n.statslabel['generatedin'][lang]} "
			s.append dateFormat.format(new Date())+"</DIV>\n</DIV>\n"
		}

		return s.toString()
	}

	public String renderNEPage(long ne_id, Collection collection, String lang) {
		StringBuffer s = new StringBuffer()
		log.debug "Request of stats for NE $ne_id, collection $collection, lang $lang";
		Cache c = db.getDBTable("CacheTable").getFromIDAndCollectionAndLang("NE:"+ne_id, collection, lang)
		dateFormat = new SimpleDateFormat(i18n.dateformat[lang])

		if (c && c.isCacheFresh()) {
			log.debug "Cache is new, let's use it."
			s.append "<DIV ID='rembrandt-detailne-${ne_id}' CLASS='stats-main'>\n"
			s.append c.cac_obj
			s.append "<DIV ID='stats-header'>${i18n.statslabel['generatedin'][lang]} "
			s.append dateFormat.format((Date)c.cac_date)+"</DIV>\n</DIV>\n"
		} else {
			log.debug "Cache is old or non-existent, time to refresh it."
			RenderNEDetailsStats stat = new RenderNEDetailsStats()
			String content = stat.render(ne_id, collection, lang)
			if (!c) c = new Cache()
			c.refreshCache("NE:"+ne_id, collection, lang, content, NE_CACHE_REFRESH)
			s.append "<DIV ID='rembrandt-detailne-${ne_id}'  CLASS='stats-main'>\n"
			s.append content
			s.append "<DIV ID='stats-header'>${i18n.statslabel['generatedin'][lang]} "
			s.append dateFormat.format(new Date())+"</DIV>\n</DIV>\n"
		}
	}

	public String renderDocPage(Doc doc, Collection collection, String lang) {
		StringBuffer s = new StringBuffer()
		log.debug "Request of stats for doc ${doc.doc_id}, collection $collection, lang $lang";
		dateFormat = new SimpleDateFormat(i18n.dateformat[lang])
		Cache c = db.getDBTable("CacheTable").getFromIDAndCollectionAndLang("DOC:"+doc.doc_id, collection, lang)
		if (c && c.isCacheFresh()) {
			log.debug "Cache is new, let's use it."
			s.append "<DIV ID='rembrandt-detaildoc-${doc.doc_id}'  CLASS='stats-main'>\n"
			s.append c.cac_obj
			s.append "<DIV ID='stats-header'>${i18n.statslabel['generatedin'][lang]} "
			s.append dateFormat.format((Date)c.cac_date)+"</DIV>\n</DIV>\n"
		} else {
			log.debug "Cache is old or non-existent, time to refresh it."
			RenderDocDetailsStats stat = new RenderDocDetailsStats()
			String content = stat.render(doc, collection, lang)
			if (!c) c = new Cache()
			c.refreshCache("DOC:"+doc.doc_id, collection, lang, content, DOC_CACHE_REFRESH)
			s.append "<DIV ID='rembrandt-detaildoc-${doc.doc_id}'  CLASS='stats-main'>\n"
			s.append content
			s.append "<DIV ID='stats-header'>${i18n.statslabel['generatedin'][lang]} "
			s.append dateFormat.format(new Date())+"</DIV>\n</DIV>\n"
		}
	}

	public void forceRefreshCacheOnFrontPage(Collection collection, String lang) {
		RenderFrontPageStats stat = new RenderFrontPageStats()
		String content = stat.render(collection, lang)
		Cache c = new Cache()
		c.refreshCache(SaskiaStats.statsFrontPage, collection, lang, content,
				SaskiaStats.STATS_PAGE_CACHE_REFRESH)
	}

	static void main(args) {

		SaskiaStats stats

		String usage = "Usage:\njava saskia.stats.SaskiaStats [-refreshMain ] collection lang\n"+
				"Learn it."

		if (!args || args.size() < 3) {
			println usage
			System.exit(0)
		}

		switch(args[0]) {

			case "-refreshMain":
				stats = new SaskiaStats()
				Collection collection
				if (args[1].matches(/^\d+$/)) {
					collection = Collection.getFromID(Long.parseLong(args[1]))
				} else {
					collection = Collection.getFromName(args[1])
				}
				if (!collection) {
					log.error "Collection ${args[1]} unknown. Exiting."
					return null
				}
				stats.forceRefreshCacheOnFrontPage(collection, args[2])
				println "Cache is now refreshed."
				break

			default:
				println usage
				break
		}
	}
}