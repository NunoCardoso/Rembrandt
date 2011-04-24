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
package saskia.index

/**
 * @author Nuno Cardoso
 * 
 */
abstract class IndexGenerator {

	HashMap status
	
	public IndexGenerator() {
		this.status = [indexed:0, skipped:0, failed:0]
	}
	
	public abstract index();
	
	void syncToFile(File filestats, Map stats) {
		stats.each{k,v -> filestats << "$k:$v\n"}
	}

	public String statusMessage() {
		String message = "Done."
		status.each{k, v->
			if (v) {
				message << "$v doc(s) $k. "
			}
		}
		return message
	}	
	
	Map readFile(File filestats) {
		Map stats = [:]
		filestats.eachLine{l ->
			List items = l.trim().split(":")
			if (items[1].matches(/\d+/)) stats[items[0]] = Integer.parseInt(items[1])
			else stats[items[0]] =  items[1]
		}
		return stats
	}

}
	