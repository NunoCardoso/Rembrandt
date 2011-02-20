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
 
package saskia.imports


class GetLeftovers {
	
	List leftovers 
	File output
	int processed = 0
	
	public GetLeftovers(List leftovers, File output) {
		this.leftovers = leftovers
		this.output = output
	}

	public doit(File file) {
		
		if (file.isDirectory()) { 	   
		    file.eachFileRecurse{f -> 
			println "Reading file ${f.name}..."

	    		String text = ""
			int docs_read = 0
			int docs_processed = 0
			Map docs = [:]
	    		String content = f.getText("UTF-8")
	    		println "Parsing file..."
			content.findAll(/(?si)<html>.*?<\/html>/) {all -> 
				all.find(/(?si)<TITLE>(.*?)<\/TITLE>/) {all2, g1 -> 
					docs[g1] = all
					docs_read++
				}
			}
			println "Read ${docs_read} docs in file."
	   		
			docs.entrySet().each{it -> 
				if (leftovers.contains(it.key)) {
					processed++
					docs_processed++		
					output.append(it.value)
				}
			}		
			
			println "Found and processed $docs_processed in this file that were in leftovers..." 
		   }
		}
		println "Total processed files: $processed."
	}

	
	static void main(args) {
	    
		 List leftovers = []
		
	    String usage = "Usage: saskia.imports.GetLeftovers [leftoverFile] [outputFile]\nLearn it.";
	  
		if (!args || args.size() != 2 ) {
		    println usage
		    System.exit(0)
		}

		File file_leftovers = new File(args[0])
		file_leftovers.eachLine{l -> 
			leftovers << l.replaceAll("> ","").trim()
		}

		println "Loaded "+leftovers.size()+" leftover doc ids"

		File output = new File(args[1])		
		GetLeftovers g = new GetLeftovers(leftovers, output)		
		// search all files 
		
		File file = new File("/collections/CLEF/longfiles-en-utf8")
		g.doit(file)
		file = new File("/collections/CLEF/longfiles-pt-utf8")
		g.doit(file)
	}

}