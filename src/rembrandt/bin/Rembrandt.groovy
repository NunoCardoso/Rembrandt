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
  * This is the main class. Reads a configuration file, starts up 
  * Rembrandt input, output and stderr streams, language, tagging style
  * and resources used for the classification. 
  * 
  * try "cat [File] | java rembrandt.bin.Rembrandt for a quick tagging; 
  * 
  * Rembrandt uses a default configuration file in ~/.rembrandt_renoir_saskia_conf.xml, 
  * if no --conf is passed in the command line. For more parameter tweaks, please read 
  * the documentation, they are passed on the configuration file or in -D params. 
  */
  class Rembrandt {
 
     static List<RembrandtCore> core = []
     Configuration conf
     
     def inputstream, inputstyle, inputreader 
     def outputstream, outputstyle, outputwriter
     def errputstream, errstyle, errwriter
     int inputstyleverbose, outputstyleverbose, errputstyleverbose     
     String inputFileName, outputFileName, errFileName

     String rembrandtRulesParamDefault = 'HAREM' 

     String rembrandtInputReaderParamDefault = 'rembrandt.io.HTMLDocumentReader'
     String rembrandtInputTagStyleParamDefault = 'rembrandt.io.RembrandtStyleTag'
     int rembrandtInputTagStyleVerboseParamDefault = 2
     String rembrandtInputEncodingParamDefault = System.getProperty('file.encoding') 

     String rembrandtOutputTagStyleParamDefault = 'rembrandt.io.RembrandtStyleTag'
     String rembrandtOutputWriterParamDefault = 'rembrandt.io.RembrandtWriter' 
     int rembrandtOutputTagStyleVerboseParamDefault = 2
     String rembrandtOutputEncodingParamDefault = System.getProperty('file.encoding') 

     String rembrandtErrTagStyleParamDefault = 'rembrandt.io.RembrandtStyleTag'   
     String rembrandtErrWriterParamDefault = 'rembrandt.io.UnformattedWriter'
     boolean rembrandtErrEnabledParamDefault = true   
     String rembrandtErrFileDefault = 'rembrandt.err.log'    
     int rembrandtErrTagStyleVerboseParamDefault = 3
     String rembrandtErrEncodingParamDefault = System.getProperty('file.encoding') 

     static Logger log = Logger.getLogger("RembrandtMain")  
         
     /**
      * Get Rembrandt version. Needs the jar in the CLASSPATH.
      * @return Rembrandt version in major version - build number format.
      */
     public static String getVersion() {
	String v = Rembrandt.class.getPackage().getImplementationVersion() 
        return (v? v : "Unknown-build")
     }
      
      /**
      * Main constructor. Sets up input and outpur streams, 
      * and reads input. After a successful initialization, invoke process() to 
      * start tagging the documents.
      * 
      * @param Configuration object. 
      */               
     public Rembrandt(conf) {
             
        this.conf=conf
        def outstream, instream, errstream
        
        
	/* input configuration */
        
        def inputStyleParam = conf.get("rembrandt.input.styletag")
        def inputStyleLang = conf.get("rembrandt.input.styletag.lang",conf.get("global.lang"))
        def inputStyleVerbose = conf.getInt("rembrandt.input.styletag.verbose", rembrandtInputTagStyleVerboseParamDefault)
        if (!inputStyleParam) {
            log.warn "No input style specified. Using $rembrandtInputTagStyleParamDefault"
            log.warn "Tag language set to $inputStyleLang, verbosity set to $inputStyleVerbose"
            inputStyleParam = rembrandtInputTagStyleParamDefault
        }
        inputstyle = Class.forName(inputStyleParam).newInstance(inputStyleLang)   
        
        def inputReaderParam = conf.get("rembrandt.input.reader")
        if (!inputReaderParam) {
            log.warn "No input reader specified. Using $rembrandtInputReaderParamDefault"
            inputReaderParam = rembrandtInputReaderParamDefault
	}
        inputreader = Class.forName(inputReaderParam).newInstance()

        def inputEncodingParam = conf.get("rembrandt.input.encoding")
        if (!inputEncodingParam) {
            log.warn "No input encoding specified. Using $rembrandtInputEncodingParamDefault"
            inputEncodingParam = rembrandtInputEncodingParamDefault
	}
 
        inputFileName = conf.get("rembrandt.input.file")
        if (inputFileName) {
            File f = new File(inputFileName)
                if (!f) throw new Exception("No file found. Please check rembrandt.input.file")
                inputstream = new InputStreamReader(new FileInputStream(f), inputEncodingParam) 
            	instream ="File ${inputFileName} <${inputstream.getEncoding()}>"
        } else {
            inputstream = new InputStreamReader(System.in, inputEncodingParam);
            instream ="System.in <${inputstream.getEncoding()}>"
        }

        /* output configuration */
        
        def outputStyleParam = conf.get("rembrandt.output.styletag")
        def outputStyleLang = conf.get("rembrandt.output.styletag.lang",conf.get("global.lang"))
        int outputStyleVerbose = conf.getInt("rembrandt.output.styletag.verbose", rembrandtOutputTagStyleVerboseParamDefault)
        if (!outputStyleParam) {
            log.warn "No output style specified. Using $rembrandtOutputTagStyleParamDefault"
            log.warn "Tag language set to $outputStyleLang, verbosity set to $outputStyleVerbose"
            outputStyleParam = rembrandtOutputTagStyleParamDefault
		}        
        outputstyle = Class.forName(outputStyleParam).newInstance(outputStyleLang)       

        def outputWriterParam = conf.get("rembrandt.output.writer")
        if (!outputWriterParam) {
            log.warn "No output writer specified. Using $rembrandtOutputWriterParamDefault"
            outputWriterParam = rembrandtOutputWriterParamDefault
		}
        outputwriter = Class.forName(outputWriterParam).newInstance(outputstyle)
		
        def outputEncodingParam = conf.get("rembrandt.output.encoding")
		
		if (!outputEncodingParam) {
	    	log.warn "No output encoding specified. Using $rembrandtOutputEncodingParamDefault"
	    	outputEncodingParam = rembrandtOutputEncodingParamDefault
		}
        
        outputFileName = conf.get("rembrandt.output.file")
        if (outputFileName) {
            File f = new File(outputFileName)
            if (!f) throw new Exception("Output file cannot be written. Please check rembrandt.output.file")
            outputstream = new OutputStreamWriter(new FileOutputStream(f), outputEncodingParam) 
            outstream ="File ${f.getName()} <${outputstream.getEncoding()}>"
        } else {
            outputstream = new OutputStreamWriter(System.out, outputEncodingParam);
            outstream ="System.out <${outputstream.getEncoding()}>"
        }
        	    
        /* stderr configuration */
        
        boolean errEnabled = conf.getBoolean("rembrandt.err.enabled", rembrandtErrEnabledParamDefault)
        if (!errEnabled) {
            errputstream = null
            errstream = "System.err disabled"
        } else {
            def errStyleParam = conf.get("rembrandt.err.styletag")
            def errStyleLang = conf.get("rembrandt.err.styletag.lang",conf.get("global.lang"))
            int errStyleVerbose = conf.getInt("rembrandt.err.styletag.verbose", rembrandtErrTagStyleVerboseParamDefault)
            if (!errStyleParam) {
        	log.warn "No err style specified. Using $rembrandtErrTagStyleParamDefault"
        	log.warn "Tag language set to $errStyleLang, verbosity set to $errStyleVerbose"
        	errStyleParam = rembrandtErrTagStyleParamDefault
            }        
            errstyle = Class.forName(errStyleParam).newInstance(errStyleLang)       

            def errWriterParam = conf.get("rembrandt.err.writer")
            if (!errWriterParam) {
        	log.warn "No err writer specified. Using $rembrandtErrWriterParamDefault"
        	errWriterParam = rembrandtErrWriterParamDefault
            }
            errwriter = Class.forName(errWriterParam).newInstance(errstyle)

            def errEncodingParam = conf.get("rembrandt.err.encoding")
            if (!errEncodingParam) {
        	log.warn "No err encoding specified. Using $rembrandtErrEncodingParamDefault"
        	errEncodingParam = rembrandtErrEncodingParamDefault
            }
        
            errFileName = conf.get("rembrandt.err.file")
            if (errFileName) {
           	File f = new File(errFileName)
           	if (!f) throw new Exception("Err file cannot be written. Please check rembrandt.err.file")
           	errstream = new OutputStreamWriter(new FileOutputStream(f), errEncodingParam) 
           	errstream ="File ${f.getName()} <${errstream.getEncoding()}>"
            } else {
        	errstream = new OutputStreamWriter(System.err, errEncodingParam);
        	errstream ="System.err <${errstream.getEncoding()}>"
            } 
        }   
 	    
        log.info ("IN: $instream, reader: ${inputreader.class.name}")    
        log.info ("OUT: $outstream, writer: ${outputwriter.class.name}, style:${outputstyle.class.name}")    
        log.info ("ERR: $errstream, writer: ${errwriter.class.name}, style:${errstyle.class.name}")    
     }	
 	
    /** 
     * Invokes the rembrandt.io.*Reader to parse the input stream into workable Documents.
     * @return List of Documents
     */
    List<Document> loadDocuments() {
	inputreader.processInputStream(inputstream)
	return inputreader.docs
    }
     
    /**
     * Prints a heads for the output stream
     */
    public void printHeader() {	
	if (outputstream) outputstream.write outputwriter.printHeader()		
	if (errputstream) errputstream.write errwriter.printHeader()
    }
	
    /**
     * As name says it all, releases REMBRANDT on document. It reads important vars from doc 
     * (id, lang and rules) to pick the correct RembrandtCore, then releases it. 
     * This is the method you should invoke on your little application. :)
     */
    public Document releaseRembrandtOnDocument(Document doc) {    		

	/** Get metadata from document, so that we can select the proper core */
	String lang = (!doc.lang  ? conf.get("global.lang", System.getProperty('user.language')) : doc.lang)
	String rules = (!doc.rules  ? conf.get("rembrandt.core.rules", "HAREM") : doc.rules)
	RembrandtCore currentcore = Rembrandt.getCore(lang, rules)
	
	// this method is thread-safe, all changes are made in the doc arg.
	currentcore.releaseRembrandtOnDocument(doc)	
        return doc
    }	
 			
 	 /**
 	   * Prints the document, invoking the Writer, for the output and err stream
 	   */
    public void printDoc(Document processedDoc) {
	if (outputstream) {
	    outputstream.write outputwriter.printDocument(processedDoc)		    
	    outputstream.flush()	
 	}
	if (errputstream) {
	    errputstream.write errwriter.printDocument(processedDoc)
	    errputstream.flush()	
 	}
   }
 
    /**
     * Prints a footer for the output and err stream
     */
    public void printFooter() {	
	if (outputstream) {		
	    outputstream.write outputwriter.printFooter() 
  	    outputstream.close()
	}
	if (errputstream) {
	    errputstream.write errwriter.printFooter()
	    errputstream.close()
	}
   }
 	
    /** Selects a RembrandtCore for the given language and rules.
     * Loads a new one if it is not loaded; picks one already 
     * running if it is already loaded.
     */
    static RembrandtCore getCore(String lang, String rules) {
	RembrandtCore coreToReturn = null
	String targetClassName = "rembrandt.bin.RembrandtCore"+(lang.toUpperCase())+"for"+rules.toUpperCase()
	core.each{
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
	    core << coreToReturn
	    log.info "Initialized new core $targetClassName."
	} 
	return coreToReturn
    }
 	
    /**
     * Main method.
     */
    static void main(args) {
 	    
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
 
	rembrandt = new Rembrandt(Configuration.newInstance(conffilepath))
	log.info "Invoking reader ${rembrandt.inputreader.class.name} to parse the input stream."  
 
	List<Document> docs = rembrandt.loadDocuments()
	log.info "Got ${docs.size()} doc(s). "
 
	// give labels if the doc does not have...
	String docid_header
	if (rembrandt.inputFileName) docid_header =rembrandt.inputFileName else docid_header = 'stdin'
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