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
package saskia.dbpedia

import saskia.bin.Configuration
import org.apache.log4j.*

/** This class has utility methods around the DBpedia ontology. */
class DBpediaOntology {
    
    static DBpediaOntology _this
	static version
	static Logger log = Logger.getLogger("DBpediaAPI")
	static String ontologyPrefix = "http://dbpedia.org/ontology/"
	static Configuration conf = Configuration.newInstance()
	
	// it can't be named newInstance, otherwise it gets an infinite loop
	static DBpediaOntology getInstance() {
		if (!_this) {
			version = conf.get("saskia.dbpedia.version","3.5.1")
			log.info "Loading internal DBpedia "+"saskia.dbpedia.DBpediaOntology"+version.replaceAll(/\./,"")
			_this  = Class.forName("saskia.dbpedia.DBpediaOntology"+version.replaceAll(/\./,"")).newInstance()
			if (!_this) throw new IllegalStateException("Can't load DBpedia internals, no support for version $version")
		}
		//println "1 _this: "+_this
	//	println "1 thiscalsses: "+_this.classes
		return _this	
	}
	
	public String getVersion() {
		return version
	}
	
	/** The prefix for the DBpedia ontology */
	
	/** Get the array index of a class 
	 * @param name Class name
	 * @return the index
	 */
    public int idOf(String name) {
	    return _this.classes.indexOf(getShortName(name))
	}
    
	/** Get a DBpedia ontology name for an index 
	 * @param id The index
	 * @param shortName true to return a short name, false to return a full resource name
	 * @return the DBpedia class 
	 */
    public String getNameFromID(int id, boolean shortName=false) {
		return (shortName ? _this.classes[id] : ontologyPrefix+_this.classes[id])
    }

	/** Get a full DBpedia classe name, if it's not already in full format.
	 * @param name the DBpedia class name 
	 * @return the full DBpedia class name
	 */
	static String getFullName(String name) {
		if (name.startsWith(ontologyPrefix)) return name
		else return ontologyPrefix+name
	}
    
	/** Get a short DBpedia classe name, if it's not already in short format.
	 * @param name the DBpedia class name 
	 * @return the short DBpedia class name
	 */
	static String getShortName(String name) {
		if (name.startsWith(ontologyPrefix)) return name.replaceAll(/${ontologyPrefix}/, "")
		else return name
	}

	/** check if the name given can be a DBpedia ontology class.
	 * @param name the name 
	 * @return true if it's a DBpedia ontology class, false otherwise.
	 */
	public boolean isClass(String name) {
		if (!name) return null
		return _this.classes.contains(getShortName(name))
	}
	   
	   
	/** get sub classes for a DBpedia ontology class. Returns null if it's not a valid class,
	 * returns an empty ArrayList if it's a leaf node, and a list of DBpedia classes (in 
	 * short DBPedia names) with child nodes, if it has sub classes.
	 * @param name the name of the DBpedia ontology class, serving as needle.
	 * @return null if not a class, a list (empty or not) with child classes
	 */
// this top-down approach is disencouraged 
//	static List getSubClassesOf(String name) {}

		
	/** get sub classes for a DBpedia ontology class. 
	* @param name the DBpedia class name
	* @return null if name is not a class, a list (empty or not) with parent classes
	*/
	public List getSuperClassesOf(String name) {
		if (!isClass(name)) return null
		def currentclass = getShortName(name)
		def res = []
		while(currentclass) {
			def superclass = _this.relations[currentclass]
			if (superclass) res << superclass
			currentclass = superclass // if it's null, it will end the loop
		}	
		return res
	}
		
	/** check if a given class instance is a sub class of another instance. 
	* @param subclass the DBpedia class instance that's supposed to be a sub class
	* @param superclass the DBpedia class instance that's supposed to be a super class
	* @return null if one of them is not a DBpedia class, true/false self-explanatory.
	*/	
	public boolean isSubClassOf(String subclass, String superclass) {
		if (!isClass(subclass) || !isClass(superclass)) return false
		return getSuperClassesOf(subclass).contains(getShortName(superclass))
	}
	
	/** This method picks the broader (super) class from a list. Note that they must ALL be related. 
	 * That is, all pairs must have a isSubClassOf that returns true!
	 * The list must have at least 2 entries. For instance, ['PopulatedPlace','Place']. In this
	 * case, it'll return 'Place', as it's a broader (super) class of 'PopulatedPlace'. 
	 * @param listOfNames List of DBpedia class names to test
	 * @return null if the listOfNames doesn't hace at least 2 entries, or the broader class otherwise. 
	 */
	public String getBroaderClassFrom(List listOfNames) {
		if (!listOfNames) return null
		listOfNames = listOfNames.collect{getShortName(it)} // format to short names	
		def currentclass = listOfNames[0]
		if (listOfNames.size() < 2) return currentclass
		
		for(int i = 1; i< listOfNames.size(); i++) {
			if (listOfNames[i] != currentclass) { // let's compare different stuff
					if (isSubClassOf(currentclass, listOfNames[i])) currentclass = listOfNames[i]
			}
		}
		return currentclass
	}
	
	/** This method picks the narrower (sub) class from a list. Note that they must ALL be related. 
	 * That is, all pairs must have a isSubClassOf that returns true!
	 * The list must have at least 2 entries. For instance, ['PopulatedPlace','Place']. In this
	 * case, it'll return 'PopulatedPlace', as it's a narrower (sub) class of 'Place'. 
	 * @param listOfNames List of DBpedia class names to test
	 * @return null if the listOfNames doesn't hace at least 2 entries, or the narrower class otherwise. 
	 */
	public String getNarrowerClassFrom(List listOfNames) {
		if (!listOfNames) return null
		listOfNames = listOfNames.collect{getShortName(it)} // format to short names	
		def currentclass = listOfNames[0]
		if (listOfNames.size() < 2) return currentclass

		for(int i = 1; i< listOfNames.size(); i++) {
			if (listOfNames[i] != currentclass) { // let's compare different stuff
				if (isSubClassOf(listOfNames[i], currentclass)) currentclass = listOfNames[i]
			}
		}
		return currentclass
	}
    
	/** 
	 * Check if the class is a subclass of PLACE, or PLACE itself
	 * @param dbpedia_class The DBpedia class to check
	 * @return true if it's a place, false otherwise 
	 */
      public boolean isAPlace(String dbpedia_class) {
		if (!dbpedia_class) return null
		dbpedia_class = getShortName(dbpedia_class)
		if (dbpedia_class.equalsIgnoreCase("Place")) return true
	//	println getSuperClassesOf(dbpedia_class)
		return getSuperClassesOf(dbpedia_class).find{it == "Place"}
           // return (places.contains(dbpedia_class))
     }
}