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

import saskia.dbpedia.DBpediaAPI
import saskia.dbpedia.DBpediaOntology
import saskia.wikipedia.WikipediaDocument
import saskia.io.NE
import saskia.io.SourceDoc
import saskia.io.Collection
import saskia.io.DocStatus
import saskia.converters.WikipediaDocument2HTMLDocumentConverter
import saskia.bin.Configuration
import org.apache.log4j.*
import org.apache.commons.cli.*
import java.util.regex.Matcher
import rembrandt.io.SecondHAREMCollectionReader
import rembrandt.io.SecondHAREMStyleTag
import rembrandt.io.HTMLDocumentWriter
import rembrandt.io.UnformattedStyleTag
import rembrandt.obj.ListOfNE
/** 
 * This class imports NYT files to the Source Documents
 */
class GeneratePlainGCSecondHAREM {
	
	Configuration conf
	static Logger log = Logger.getLogger("SaskiaImports")
	Collection collection 
	String lang= "pt"
	String filename
    
	SecondHAREMCollectionReader reader
	HTMLDocumentWriter writer
    
	public GeneratePlainGCSecondHAREM(String filename) {

	    conf = Configuration.newInstance()
	    collection = Collection.getFromName("CD2 do Segundo HAREM")	    
	    if (!collection) 
		throw new IllegalStateException("Don't know where the collection is. Exiting.")
	    this.filename = filename
	    reader = new SecondHAREMCollectionReader(new SecondHAREMStyleTag(lang))
            writer = new HTMLDocumentWriter(new UnformattedStyleTag(lang))
	}
	
	public HashMap importDocs() {
	    
	    HashMap status = [imported:0, skipped:0]
        
            File f = new File(filename)
            InputStreamReader is = new InputStreamReader(new FileInputStream(f))
            reader.processInputStream(is)
           
	    reader.docs.each{doc -> 
	    	doc.bodyNEs = new ListOfNE() // to erase the ALT print styles
 	    	println writer.printDocument(doc)
 	    	status.imported++
	    }  
	    return status
      }

	
	static void main(args) {
	    
	    Options o = new Options()
	    o.addOption("file", true, "NYT collection file to load")
	    o.addOption("help", false, "Gives this help information")
	    
	    CommandLineParser parser = new GnuParser()
	    CommandLine cmd = parser.parse(o, args)

	    if (cmd.hasOption("help")) {
		HelpFormatter formatter = new HelpFormatter()
		formatter.printHelp( "java saskia.imports.ImportPlainGCNYTFile2SourceDocument", o )
		println "Make sure that the collection 'New York Times 2002-2005' is on Saskia"
		System.exit(0)
	    }

	    if (!cmd.hasOption("file")) {
		println "No --file arg. Please specify the file. Exiting."
		System.exit(0)
	    }
	    
        GeneratePlainGCSecondHAREM w2s = new GeneratePlainGCSecondHAREM(
		    cmd.getOptionValue("file"))
			
	    HashMap status = w2s.importDocs()
	                      
	    log.info "Done. ${status.imported} doc(s) imported, ${status.skipped} doc(s) skipped."
	}
}