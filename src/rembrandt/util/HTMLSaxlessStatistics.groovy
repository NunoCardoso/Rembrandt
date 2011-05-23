/* Rembrandt Rembrandt
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


package rembrandt.util

import java.util.regex.*

/**
 * author Nuno Cardoso
 * This class outputs stats for the Rembrandt tags in documents, without SAX parser.
 */
class HTMLSaxlessStatistics {

	def numberNEs = 0

	def static hash = [:]
	def static hashUnique = [:]
	def static hashLocality = [:]
	def static hashUniqueLocality =[:]
	def static emIDs=[]
	def static localityIDs=[]

	// (?s) triggers the DOTAll, so that the 3rd group, (.), also matches spaces.
	def opentagpattern = ~/<([a-zA-Z]+)\s?([^>]*?)>(.)?/
	def attrspattern = ~/(\w+)="([^"]*?)"/ //"
	def m

	private countNode (cat, attrs, nextcharacter) {
		def node = [:]
		node['name']=cat
		if (attrs != "") {
			def m2 = attrspattern.matcher(attrs)
			while (m2.find()) node[m2.group(1)]=m2.group(2)
		}
		//  if (nextcharacter != null && nextcharacter.equals("<")) node["unique"]= false
		//  else node["unique"] = true


		if (hash.containsKey(node.name) ) hash[node.name]++
		else hash[node.name] = 1

		if (!node.name.equalsIgnoreCase("LOCALITY")) {
			if (!emIDs.contains(node.ID)) {
				if (hashUnique.containsKey(node.name) ) hashUnique[node.name]++
				else hashUnique[node.name] = 1
				emIDs += node.ID
			}
		}

		if (node.name.equalsIgnoreCase("LOCALITY")) {
			def nf = node.from
			// def nodesfrom = node.from.split(" ")
			// nodesfrom.each{nf ->
			if (hashLocality.containsKey(nf))  hashLocality[nf]++
			else hashLocality[nf] = 1


			if (!localityIDs.contains(node.emid)) {
				if (hashUniqueLocality.containsKey(nf)) hashUniqueLocality[nf]++
				else hashUniqueLocality[nf] = 1
				localityIDs += node.emid
			}
			//}
		}
	}

	public doit(File f) {
		int x = 0
		localityIDs=[]
		f.eachLine{

			m = opentagpattern.matcher(it)
			if (it.startsWith("<title>")) {
				localityIDs=[]
				emIDs = []
				x++
				if (x % 1000 == 0) println "Done $x docs."
			}
			while (m.find()) countNode(m.group(1),m.group(2), m.group(3))
		}
	}

	public void getStats() {
		println "\nhash"
		println "All\t\tDistinct"
		hash.keySet().sort().each{k -> println "$k:${hash[k]}\t$k:${hashUnique[k]}"}
		println "\nhashLocality"
		println "All\t\tDistinct"
		hashLocality.keySet().sort().each{k-> println "$k:${hashLocality[k]}\t$k:${hashUniqueLocality[k]}"}
	}

	static void main(args) {
		def r = new HTMLstatistics2()
		new File(args[0]).eachFileRecurse{f ->
			if (f.isFile()) {
				print "Parsing the file "+f.getName()+"... "
				r.doit(f)
				println "done."
			}
		}
		r.getStats()
	}
}
