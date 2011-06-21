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
package saskia.db
import saskia.bin.Configuration
import pt.tumba.webstore.*
import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 *
 */
class SaskiaWebstore {

	static SaskiaWebstore _this
	static String VOLUME_SDOC = "sdoc"
	static String VOLUME_RDOC = "rdoc"
	WebStore webstore
	Map<String,Volume> volumes

	static Logger log = Logger.getLogger("SaskiaMain")

	public static SaskiaWebstore newInstance() {
		if (!_this) _this = new SaskiaWebstore()
		return _this
	}

	private SaskiaWebstore() {
		log.info "SaskiaWebstore initializing..."
		Configuration conf = Configuration.newInstance()
		try {
			log.debug "Beginning Webstore..."
			String webstorefile = System.getenv()["WEBSTORE_CONFIG_FILE"]
			log.debug "Webstore config file: $webstorefile"
			webstore = new WebStore(new File(webstorefile))
			log.debug "Webstore: $webstore"
			webstore.setDefaultCompressMode(WebStore.ZLIB)
			Volume[] vs = webstore.getVolumes(WebStore.WRITABLE)
			log.debug "Volumes: $vs"
			volumes = [:]
			vs.each{v ->
				//      println "adding volume $v to key ${v.volId()}"
				volumes[v.volId()] = v
			}
			log.info "Webstore Initialized. Final volumes: $volumes"
			
		} catch(Exception e) {
			log.error "Webstore NOT initialized: "+ e.getMessage()
		}
	//	log.info "Webstore initialized with volumes ${volumes.keySet()}"
	}

	public boolean checkIfContainsVolume(String volume) {
		return volumes.containsKey(volume)
	}

	public String store(String content, String volume) {
		if (!content) throw IllegalStateException("Can't add null content to Webstore.")
		if (!volume) throw IllegalStateException("Please give a volume.")
		if (!volumes.containsKey(volume)) throw IllegalStateException("Don't have volume $volume in Webstore.")
		Content c = new Content(content.getBytes())
		// println "Content: $c Volume: ${volumes[volume]}"
		Key key = webstore.store(c, volumes[volume])
		// println "Got key $key ${key.toString()}"
		return key.toString()
	}

	public void delete(String key, String volume) {
		if (!volume) throw IllegalStateException("Please give a volume.")
		if (!volumes.containsKey(volume)) throw IllegalStateException("Don't have volume $volume in Webstore.")
		Key key_ = Key.toKey(key)
		if (!key) throw IllegalStateException("Illegal key $key")
		webstore.delete(key)
	}

	public String retrieve(String key) {
		Key key_ = Key.toKey(key)
		Content cont = webstore.retrieve(key_)
		return new String(cont.getData())//), "UTF-8")
	}
}
