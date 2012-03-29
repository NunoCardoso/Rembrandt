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

package saskia.bin

import org.apache.log4j.*

import saskia.db.DocStatus;
import saskia.db.obj.*
import saskia.imports.RembrandtADocument

import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag

class Saskia {

	Saskia log = Logger.getLogger("Saskia")

	Saskia lang
	Saskia rw
	Saskia sd2rd
	Saskia rd2nepool
	Saskia conf
	public Saskia() {
		conf = Configuration.newInstance()
		this.lang= conf.get("global.lang")
		rw = new RembrandtWriter(new RembrandtStyleTag(
				conf.get("rembrandt.output.styletag.lang", this.lang)) )
		sd2rd = new RembrandtADocument()
	}


	static main(args) {
		Saskia rp = new Saskia()
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in))

		String line
		println "Saskia tool."
		println "==================="

		println "1. Commands for documents: "
		println "    info [docid] - Gives info for document."
		println "    proc [docid] [status] - sets status for document (status = ${DocStatus.values()})"
		println "    source [String] - search doc title"

		while (true) {
			print "Saskia> "
			line = input.readLine().trim()
			if (!line) break
				def tokens = line.split(/\s+/)
			def command = tokens[0]

			switch(command) {

				/**** INFO DOC ***/	

				case "info":
					long docid = Saskia.getLong(tokens[1])
					Doc doc = Doc.getFromID(docid)
					if (!doc)  {
						println "Did not found document with docid ${docid}." ; break
					}
					println "  ID: ${doc.doc_id} ORIGINAL ID: ${doc.doc_original_id} LANG: ${doc.doc_lang}"
					println "  DATE CREATED: ${doc.doc_date_created} DATE TAGGED: ${doc.doc_date_tagged}"
					println "  PROC STATUS: ${doc.doc_proc} SYNC STATUS: ${doc.doc_sync} EDIT STATUS: ${doc.doc_edit}"
					println "  CONTENT(300):${doc.doc_content.substring(0,300)}"
					break

				/**** SET DOC STATUS ***/	

				case "proc":
					long docid = Saskia.getLong(tokens[1])
					Doc doc = Doc.getFromID(docid)
					if (!doc)  {
						println "Did not found document with docid ${docid}." ; break
					}
					doc.changeProcStatusInDBto(DocStatus.getFromKey(tokens[2].toUpperCase()))
					println "Changed proc status of doc in DB to '${tokens[2].toUpperCase()}'"
					break

				/**** SEARCH FOR DOC TITLE ***/	

				default:
					println "Say what?"
					break
			}
		}//while true
	}

	public static long getLong(String s) {
		try {
			return  Long.parseLong(s)
		} catch(Exception e) {
			println "did not get/understood doc id."
			return null
		}
	}
}
