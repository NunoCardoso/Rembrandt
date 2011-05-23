package saskia.dbpedia

class DBpediaResource {
    
    static String resourcePrefix = "http://dbpedia.org/resource/"
	
    static String getFullName(String name) {
	if (!name) return null
	if (!name.startsWith(resourcePrefix)) return resourcePrefix+name else return name
    }
	
    static boolean isFullNameDBpediaResource(String test) {
	if (!test) return false
	if (test.startsWith(resourcePrefix) ) return true
	return false
    }
    
    static String getShortName(String name) {
	if (!name) return null
	if (name.startsWith(resourcePrefix)) 
	    return name.substring(resourcePrefix.size(), name.size())
	    else return name
    }
}