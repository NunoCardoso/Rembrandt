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
package rembrandt.server

import rembrandt.bin.*
import rembrandt.obj.Document
import saskia.bin.Configuration
import saskia.io.User
import saskia.util.I18n
import org.apache.log4j.*
import rembrandt.io.*

public class RembrandtMapping extends WebServiceRestletMapping {
    
    /**
     * nota: um dos problemas que gera infinite lopps é se um dos log.info chama variáveis que não existem.
     * A closure tenta resolvê-las fora dela , e isso despoleta uma cadeia de requests a evitar.
     */
	Closure JSONanswer
	Configuration conf
	List cores
        I18n i18n
        Logger log = Logger.getLogger("RembrandtServer")  
        Logger slog = Logger.getLogger("RembrandtTaggingServer")  
        
	public RembrandtMapping(Configuration conf) {
		    
	    this.conf=conf
		//log.info "Rembrandt version ${Rembrandt.getVersion()}, no cores loaded yet"    
	    cores = []
		
	    JSONanswer = {req, par, bind ->
	    // note that there's the 'log' for transaction reports. the 'slog' is for logging taggings.
	      long session = System.currentTimeMillis()
	      RembrandtCore core
        
	      String lang, submissionlang, rules, doctitle, docbody, api_key, format
	      if (par["POST"]["lg"]) lang = par["POST"]["lg"]
	      if (par["POST"]["slg"]) submissionlang = par["POST"]["slg"]
	      rules = "harem"
	      if (par["POST"]["dt"]) doctitle = par["POST"]["dt"]
	      if (par["POST"]["db"]) docbody = par["POST"]["db"]
	      if (par["POST"]["api_key"]) api_key = par["POST"]["api_key"]
	      if (par["POST"]["f"]) format = par["POST"]["f"] else format = "rembrandt"
        
	      if (!api_key) {
                     bind["status"] = -1
                     bind["message"] = i18n.servermessage['no_api_key'][lang]
                     log.info "${session} RembrandtMapping: $bind"
                     return JSONHelper.toJSON(bind)	
               }   
            
	       User user = User.getFromAPIKey(api_key)
	       if (!user) {
		    bind["status"] = -1
		    bind["message"] = i18n.servermessage['user_not_found'][lang]
		    log.info "${session} RembrandtMapping: $bind"
		    return JSONHelper.toJSON(bind)	
	       }   
        
	       if (! user.canExecuteAPICall()) {
                    bind["status"] = -1
                    bind["message"] = i18n.servermessage['api_key_limit_exceeded'][lang]
                    log.info "${session} RembrandtMapping: ${bind} - user $user, API key $api_key"
                    return JSONHelper.toJSON(bind)	
               }              
        
		/****** LOAD CORE *****/
 		String targetClassName = "rembrandt.bin.RembrandtCore"+(lang.toUpperCase())+
                    "for"+rules.toUpperCase()
		
		cores.each{ if (it.class.name.equals(targetClassName)) {core = it}  }
		if (!core) {
                     // slog.info "Creating core "+targetClassName
		   try {
		  //	coreToReturn = Thread.currentThread().getContextClassLoader().loadClass(targetClassName).newInstance()
		      if (lang == "pt") core = new RembrandtCorePTforHAREM()
		      if (lang == "en") core = new RembrandtCoreENforHAREM()
			 
		    } catch(Exception e) {
		         log.fatal "${session} RembrandtMapping: Can't load Rembrandt core: "+e.printStackTrace()
			  e.printStackTrace()
		    }
		    cores.add(core)
		     // slog.info "Initialized new core "+targetClassName+"."
		 } 
             
		 slog.info "$session Got a request to tag the following doc:\n===========\nlang=${lang} slang=${submissionlang}\ndt=${doctitle}\ndb=${docbody}\n==========\n"
            
		 Document doc= new Document(title:doctitle, body:docbody)
			
		 doc = core.releaseRembrandtOnDocument(doc)	
		    // the lang is the lang for the output tags. Right now, let's use the lang

		 Writer rw	 
		 if (format == "rembrandt") {
		     rw = new RembrandtWriter(new RembrandtStyleTag(lang))
		 } else if (format == "dsb") {
		     rw = new UnformattedWriter(new JustCategoryStyleTag(lang))
		 }
        
		 if (doc.title) bind["dt"]= rw.printDocumentTitleContent(doc)
		 if (doc.body) bind["db"]= rw.printDocumentBodyContent(doc)
		 bind["db"] = bind["db"].replaceAll(/\n/, " ")
		
		 slog.info "$session Returning tagged doc:\n==========\ndt=${bind['dt']}\ndb=${bind['db']}\n==============\n"
            
		 int calls = user.addAPIcount()
		 slog.info "$session User $user made a Rembrandt tagging request, now has $calls API daily calls\n"
		 log.info "${session} RembrandtMapping: OK"
		 return JSONHelper.toJSON(bind)		
	   }
	} 
}
