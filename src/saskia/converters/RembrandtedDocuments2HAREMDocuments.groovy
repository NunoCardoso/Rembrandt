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
package saskia.converters

import org.apache.log4j.*
import rembrandt.io.*
import saskia.bin.Configuration
import org.apache.commons.cli.*
import rembrandt.obj.Document
/** 
 * Esta classe lê um ficheiro que é um dump de uma colecção em formato REMBRANDT,
 * e escreve em formato HAREM II 
*/

class RembrandtedDocuments2HAREMDocuments {
	
	Logger log = Logger.getLogger("DocumentConverter")
	File input, output
	Configuration conf = Configuration.newInstance()
	RembrandtReader reader
	SecondHAREMDocumentWriter writer
	List raw_docs = []
	List parsed_docs = []
	def inputEncodingParam
	def outputEncodingParam
	def inputstream
	def outputstream

	public RembrandtedDocuments2HAREMDocuments(File input, File output) {
		this.input=input
		this.output=output
		reader = new RembrandtReader(new RembrandtStyleTag("pt"))
		writer = new SecondHAREMDocumentWriter(new SecondHAREMStyleTag("pt"))
	}
	
	void readInput() {
		
		  inputEncodingParam = conf.get("rembrandt.input.encoding")
        if (!inputEncodingParam) {
            log.warn "No input encoding specified. Using "+System.getProperty('file.encoding') 
            inputEncodingParam = System.getProperty('file.encoding')
			}
 
         inputstream = new InputStreamReader(
				new FileInputStream(input), inputEncodingParam) 
            log.info "Input file: ${input.getName()} <${inputstream.getEncoding()}>"
         
		   outputEncodingParam = conf.get("rembrandt.output.encoding")
        if (!outputEncodingParam) {
            log.warn "No output encoding specified. Using "+System.getProperty('file.encoding') 
            outputEncodingParam = System.getProperty('file.encoding') 
			}
 		
			outputstream = new OutputStreamWriter(new FileOutputStream(output), outputEncodingParam) 
			log.info "Output file: ${output.getName()} <${outputstream.getEncoding()}>"

		  BufferedReader br = new BufferedReader(inputstream)	    
        StringBuffer buffer = new StringBuffer()		    
        String line
		  String doc_original_id = null
		  Document doc
		
        while ((line = br.readLine()) != null) {  
				// vamos substituir o docid pelo doc_original_id
	        if (line.matches(/(?i)<META .*/)) {
		  			line.find(/(?i)DOC_ORIGINAL_ID="(.*?)"/) {all, g1 -> 
			 			doc_original_id = g1}
                     
			  } else {
           	  buffer.append(line+"\n")
              if (line.matches(/(?i)<\/DOC>/)) {
        				doc = reader.createDocument(buffer.toString())
						//println "doc_original_id = "+doc_original_id
						doc.docid = doc_original_id
						raw_docs << doc
        		  		buffer = new StringBuffer()
						doc = null
						doc_original_id=null
            	}
          }
        }
        // case there's no HTML tags
        if (buffer.toString().trim()) {
            raw_docs << reader.createDocument(buffer.toString())
        }
    }
 
	
	
	void parse() {
		log.info ("parsing "+raw_docs.size()+" docs.")
	 	raw_docs.each{raw_doc -> 
			log.info("Processing doc $raw_doc")
			String s = writer.printDocument(raw_doc)
			
			// pos-processamento: ALT
			
	/*	rural<ALT>| <EM ID="15" CATEG="LOCAL" TIPO="FISICO" SUBTIPO="AGUAMASSA">Delta do Mississipi</EM>|
 Delta do <EM ID="16" CATEG="LOCAL" TIPO="HUMANO" SUBTIPO="DIVISAO">Mississipi</EM></ALT>
*/
			s = s.replaceAll(/<ALT>\|\s/, " <ALT>")
			// a avaliaçaõ está a marimbar-se para termos fora das EM
			s = s.replaceAll(/<\/?P>/, "")
			parsed_docs << s
		}
	}
	
	void writeOutput() {
	 	output.write "<?xml version=\"1.0\" encoding=\""+outputEncodingParam+"\" ?>\n"
		output.append "<colHAREM versao=\"Rembrandt 1.2\">\n"

		parsed_docs.each{doc -> 
			output.append(doc)
		}
		
		output.append "</colHAREM>\n"
	}
	
		static void main(args) {    
        
		  Configuration conf = Configuration.newInstance()
        Options o = new Options()
        o.addOption("input", true, "target collection. Can be id or name")
        o.addOption("output", true, "dir for file output")
        
        CommandLineParser parser = new GnuParser()
        CommandLine cmd = parser.parse(o, args)
 
        if (!cmd.hasOption("input")) {
            println "No --input arg. Please specify the input file name. Exiting."
            System.exit(0)
        }
        if (!cmd.hasOption("output")) {
            println "No --output arg. Please specify the output file name. Exiting."
            System.exit(0)
        }

        RembrandtedDocuments2HAREMDocuments obj = new RembrandtedDocuments2HAREMDocuments(
			new File(cmd.getOptionValue("input")), new File(cmd.getOptionValue("output")))
		  
		  obj.readInput()	
		  obj.parse()
		  obj.writeOutput()
        println "Done."
    }   
}

