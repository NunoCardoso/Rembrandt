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

/**
 * author Nuno Cardoso
 * This class outputs stats for the Rembrandt tags in documents.
 */
class HTMLstatistics {
      
   def numberNEs = 0
   def inLocality = false
   
   def static hash = [:]
   def static hashLocality = [:]
   def static hashUniqueLocality =[:]
   def static docs = []
   def static localityIDs=[]
  
   def resource 
   
   public parseFile(File file) {
	  resource = new XmlParser().parse(file )
   }
   
   private String extractText (node) {
     def temp = []
     if (node instanceof Node) {
	    if (hash.containsKey(node.name()) ) {
		//	println "adding another"
		    hash[node.name()]++
	    }
	    else {
		//println "adding a first time"
		hash[node.name()] = 1
	    }
	    if (node.name().equalsIgnoreCase("LOCALITY")) {
			inLocality = true
			def emid = node.'@emid'			
			if (hashLocality.containsKey(node.'@from')) 
			    hashLocality[node.'@from']++
			else hashLocality[node.'@from'] = 1
			
			if (!localityIDs.contains(emid)) {
			    if (hashUniqueLocality.containsKey(node.'@from')) 
					hashUniqueLocality[node.'@from']++
			    else  hashUniqueLocality[node.'@from'] = 1
			    localityIDs += emid
			}
	    }
        node.children().each {temp += extractText(it)}
     }
     if (node instanceof String) {
		if (!inLocality) temp += node 
		else inLocality = false
     }
    return temp.join(" ")
   }  
   
   public doit() {
          //System.err.println "reading HTML file: $resource"
    if (resource.name().equalsIgnoreCase("html") ) {
	     def docid = null, text = ''
		 resource.children().each{ node -> 
		   switch(node.name()) {
			   case ~/(?i)head/ : 
			       node.children().each{ node2 -> 
			           if (node2.name() =~ /(?i)title/) 
			               docid = node2.text()
			       }			   	   
			   break
			   
			   case ~/(?i)body/ : 
			   text =extractText(node)			       
			   break
		    }
		 }//each node	
		// docs.add(new Document(text, docid))

	 }  else if (resource.name().equalsIgnoreCase("htmlset") ) {
			
		resource.children().each{mainnode -> // mainnode are the HTML	
		    def docid = null, text = ''
			mainnode.children().each{ node -> 
			   
				switch(node.name()) {
				case ~/(?i)head/ : 
				    node.children().each{ node2 -> 
				    	if (node2.name() =~ /(?i)title/) 
				    	    docid = node2.text()
					}			   	   
				break
		   
				case ~/(?i)body/ : 
				    text =extractText(node)					       
				break
				}
			}//mainnode.childrem
			//   println "partialhash = $hash" 
		//	docs.add(new Document(text, docid))
		}
   }
   }
   // println "Docs.size = "+docs.size()

   public void getStats() {
   println "hash\n====="
   hash.keySet().sort().each{k -> println "$k:${hash[k]}"} 
   println "hashLocality\n=========" 
   hashLocality.keySet().sort().each{k-> println "$k:${hashLocality[k]}"} 
   println "hashUniqueLocality\n=========" 
   hashUniqueLocality.keySet().sort().each{k -> println "$k:${hashUniqueLocality[k]}"} 
   }
   

	static void main(args) {
	    def r = new HTMLstatistics()//conf)
	    new File(args[0]).eachFileRecurse{f ->
	       if (f.isFile()) {
		   	  println "Parsing file "+f.getName()
		      r.parseFile(f)
		      r.doit()
	       }
	    }
	    r.getStats()
	}
}
		