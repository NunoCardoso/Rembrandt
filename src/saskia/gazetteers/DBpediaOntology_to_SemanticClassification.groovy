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
 
package saskia.gazetteers

import saskia.dbpedia.DBpediaOntology
import saskia.bin.Configuration
import rembrandt.obj.SemanticClassification
import org.apache.log4j.*

/**
 * @author Nuno Cardoso
 * bom para saber, a partir de uma expressão, qual a classe DBpedia que quero.
 * Categorias Wikipédia podem ser vistas como expressões, porque não? Mas há melhores formas de fazer...
 * com a sua ontologia.
 */
class DBpediaOntology_to_SemanticClassification {

	static DBpediaOntology_to_SemanticClassification _this
	static Configuration conf = Configuration.newInstance()
	static Logger log = Logger.getLogger("DBpediaAPI")
	static version 
	
	static DBpediaOntology_to_SemanticClassification newInstance() {
		if (!_this) {
			version = conf.get("saskia.dbpedia.version","3.5.1")
		//	log.info "Loading internal DBpedia version $version"
			_this = Class.forName("saskia.gazetteers.DBpediaOntology"+version.replaceAll(/\./,"")+"_to_SemanticClassification").newInstance()
			if (!_this) throw new IllegalStateException("Can't load DBpedia internals, no support for version $version")
		}
		return _this	
	}
	
		
	public String getVersion() {
		return version
	}
	
    static SemanticClassification getClassificationFrom(String dbpediaOntology) {
	
		if (!dbpediaOntology) return null
		dbpediaOntology = dbpediaOntology.replaceAll(/${DBpediaOntology.ontologyPrefix}/,"")
		List<SemanticClassification> answer = []
		if (!this) DBpediaOntology_to_SemanticClassification.newInstance()
		
		_this.meanings.each{m -> 
	   		m.needle.each{n -> if (n.equals(dbpediaOntology)) answer << m.answer }
		}
		if (!answer) return null
		//if (answer.size() != 1) log.error "Returning more than one answer in DBpediaOntology2SecondHAREMClassification"
		return answer[0]
    }
    
 
}