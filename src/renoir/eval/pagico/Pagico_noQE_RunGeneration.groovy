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
import saskia.db.obj.Collection;
import renoir.bin.Renoir
import renoir.obj.*
/**
 * @author Nuno Cardoso
 *
 */
class Pagico_noQE_RunGeneration {

    Configuration conf
    static Logger log = Logger.getLogger("RenoirTest")
	 
    public Pagico_noQE_RunGeneration() {}
    
    public static void main(args) {

		def db  = saskia.db.database.SaskiaMainDB.newInstance()
		List l = saskia.db.table.CollectionTable.getFromName(db, "Pagico")
		def o = new Pagico_Baseline_RunGeneration(
		"pagico_noqe.query", 
		"pagico_noqe.qrels", 
		"pagico_noqe.run",
		l[0],
		"pt", 
		true) // do_stem
		o.generate()
	}
}
