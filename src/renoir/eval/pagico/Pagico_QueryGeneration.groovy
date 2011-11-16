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

package renoir.eval.pagico

import org.apache.log4j.Logger

import saskia.bin.Configuration
import renoir.bin.Renoir
import renoir.obj.*

/**
 * @author Nuno Cardoso
 *
 */
class Pagico_QueryGeneration {

	Configuration conf
	static Logger log = Logger.getLogger("RenoirTest")

	public List<String> topics = []
	String homedir, topicfile, queryfile, logfile, lang
	File topicf, queryf, logf


	public Pagico_QueryGeneration(topic_file, query_file, lang) {

		// initialize by reading the topics and qrels
		conf = Configuration.newInstance()
		this.lang = lang

		String fileseparator = System.getProperty("file.separator")
		homedir = conf.get("rembrandt.home.dir",".")

		logfile = homedir+fileseparator+"resources"+fileseparator+"eval"+
				fileseparator+"pagico"+fileseparator+"logs"+fileseparator+query_file+".querygenerationlog"
		logf = new File(logfile)

		topicfile = homedir+fileseparator+"resources"+fileseparator+"eval"+
				fileseparator+"pagico"+fileseparator+"topics"+fileseparator+topic_file
		topicf = new File(topicfile)
		if (!topicf.exists()) throw new IllegalStateException("Topic file $topicf not found!")
		topicf.eachLine{l -> topics << l.trim()}        // Force the

		log.info "Topic $topicfile read, with ${topics.size()} lines."
		logf.write "Topic $topicfile read, with ${topics.size()} lines."

		queryfile = homedir+fileseparator+"resources"+fileseparator+"eval"+
				fileseparator+"pagico"+fileseparator+"queries"+fileseparator+query_file

		queryf = new File(queryfile)
		if (queryf.exists()) {
			print "Overwritting existing query file $queryf (y/n)? > "
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
			String ynae
			while (!ynae || (ynae && !(ynae.equalsIgnoreCase("y") ||ynae.equalsIgnoreCase("n") ))) {
				ynae = input.readLine().trim()
			}
			if (ynae.equalsIgnoreCase("n")) {
				log.info "Exiting then."
				System.exit(0)
			}
		}

		log.info "Target queryfile: ${queryfile}"
		logf.write "Target queryfile: ${queryfile}"
	}
}
