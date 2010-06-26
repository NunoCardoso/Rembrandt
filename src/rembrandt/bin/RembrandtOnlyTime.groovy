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
 
 package rembrandt.bin

 import rembrandt.obj.*
 import saskia.bin.Configuration
 import rembrandt.io.*
 import org.apache.log4j.*
import org.apache.commons.cli.*

 /**
  * @author Nuno Cardoso
  * 
  * This is the Rembrandt only for time expressions.
  * It calls OnlyTime cores. 
  */
  class RembrandtOnlyTime extends Rembrandt {

    
    public RembrandtOnlyTime(Configuration conf) {
	super(conf)
    }
    
    /**
     * Changed to use only *OnlyTIME cores 
    */
    public Document releaseRembrandtOnDocument(Document doc) {    		

	/** Get metadata from document, so that we can select the proper core */
	String lang = (!doc.lang  ? conf.get("global.lang", System.getProperty('user.language')) : doc.lang)
	RembrandtCore currentcore = getCore(lang)
	
	// this method is thread-safe, all changes are made in the doc arg.
	currentcore.releaseRembrandtOnDocument(doc)	
        return doc
    }	
  	
    /**
     * changed to load only *OnlyTIME cores
     */
    private RembrandtCore getCore(String lang) {
	RembrandtCore coreToReturn = null
	String targetClassName = "rembrandt.bin.RembrandtCore"+(lang.toUpperCase())+"OnlyTIME"
	this.core.each{
	    if (it.class.name.equals(targetClassName)) {
		log.info "Recycling core $targetClassName."       
		coreToReturn = it
	    }
	}		
	if (!coreToReturn) {
	    log.info "Creating core "+targetClassName
	    try {
		coreToReturn = Class.forName(targetClassName).newInstance()	  
	    }catch(Exception e) {
		log.error "Can't load Rembrandt core."
		e.printStackTrace()
	    }
	    this.core.add(coreToReturn)
	    log.info "Initialized new core $targetClassName."
	} 
	return coreToReturn
    }
 	
    /**
     * Main method.
     */
    static void main(args) {
	
	Logger log = Logger.getLogger("RembrandtMain")  

	def rembrandt, conf, conffilepath
	Options o = new Options()
	o.addOption("conf", true, "Configuration file")
	o.addOption("help", false, "Gives this help information")
	    
	CommandLineParser parser = new GnuParser()
	CommandLine cmd = parser.parse(o, args)

	if (cmd.hasOption("help")) {
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp( "java rembrandt.bin.Rembrandt", o )
	    System.exit(0)
	}
 		
	if (!cmd.hasOption("conf")) {
	    log.info "No configuration file given. Using default configuration file."
	    conffilepath = Configuration.defaultconf
 	} else {
 	    conffilepath = cmd.getOptionValue("conf")
 	}
	log.info "Rembrandt version ${Rembrandt.getVersion()}. Welcome."
 
	rembrandt = new RembrandtOnlyTime(Configuration.newInstance(conffilepath))
	log.info "Invoking reader ${rembrandt.inputreader.class.name} to parse the input stream."  
 
	List<Document> docs = rembrandt.loadDocuments()
	log.info "Got ${docs.size()} doc(s). "
 
	// give labels if the doc does not have...
	String docid_header
	if (rembrandt.inputTypeParam == "System.in") docid_header = "stdin"
	if (rembrandt.inputTypeParam == "File") docid_header = rembrandt.inputfilename
 		
	rembrandt.printHeader()
 		 		
	/* stats stuff */
	def stats = new DocStats(docs.size())
	stats.begin()
	docs.eachWithIndex { doc, i->
	   if (!doc.docid) doc.docid = docid_header+"-"+(i+1)
	   stats.beginDoc(doc.docid)
	   // lang / rules selection? must all be included in the doc metadata.
	   doc = rembrandt.releaseRembrandtOnDocument(doc)
	   /* stats stuff */
	   rembrandt.printDoc(doc)
	   stats.endDoc()					   			  
	   stats.printMemUsage()	  
	}
	stats.end()
 	rembrandt.printFooter()
 	log.info "All Done. Have a nice day."
    }
}