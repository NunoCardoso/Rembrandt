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
package saskia.server

import saskia.bin.AskSaskia
import saskia.db.table.UserTable
import saskia.db.obj.User
import rembrandt.obj.Sentence
import rembrandt.gazetteers.SemanticClassificationDefinitions
import rembrandt.obj.NamedEntity

import saskia.util.I18n
import org.apache.log4j.Logger

public class AskSaskiaMapping extends WebServiceRestletMapping {

   Closure JSONanswer 
   Closure HTMLanswer 
   I18n i18n
   static Logger mainlog = Logger.getLogger("SaskiaServerMain")  
   static Logger errorlog = Logger.getLogger("SaskiaServerErrors")  
   static Logger processlog = Logger.getLogger("SaskiaServerProcessing")  

   public AskSaskiaMapping() {
    
      i18n = I18n.newInstance()
      AskSaskia saskia
    
      JSONanswer = {req, par, bind ->
        
        long session = System.currentTimeMillis()
        processlog.debug "Session $session triggered with $par" 
        
        /** GET **/
        String ne_text, lang, api_key
        if (par["GET"]["ne"]) ne_text =  par["GET"]["ne"]
        if (par["GET"]["lg"]) lang = par["GET"]["lg"]   

        if (par["GET"]["api_key"]) api_key = par["GET"]["api_key"]
 		  if (!api_key) api_key = par["POST"]["api_key"] 
        if (!api_key) api_key = par["COOKIE"]["api_key"]   
        if (!api_key) return sm.noAPIKeyMessage()
       
		  ServerMessage sm = new ServerMessage("AskSaskiaMapping", lang, bind, session, processlog)  

        if (!ne_text || !lang || !api_key) return sm.notEnoughVars()
        


        User user = UserTable.getFromAPIKey(api_key)           
        if (!user) return sm.userNotFound()
        if (!user.isEnabled()) return sm.userNotEnabled()
            
		  // 1.1 Action: tag
		
        saskia = AskSaskia.newInstance(lang)
        NamedEntity ne = new NamedEntity(terms:Sentence.simpleTokenize(ne_text))
        ne = saskia.answerMe(ne)
        SemanticClassificationDefinitions scd = Class.forName(
			"rembrandt.gazetteers."+lang+".SecondHAREMClassificationLabels"+(
        lang.toUpperCase())).newInstance()

        bind['terms'] = ne.terms
        List cl = [] 
        ne.classification.each{
            Map x= [:]
            if (it.c) x['category'] = scd.label[it.c]
            if (it.t) x['type'] = scd.label[it.t]
            if (it.s) x['subtype'] = scd.label[it.s]
            cl << x
        }
        bind['classification'] = cl
        bind['wikipediaPage'] = ne.wikipediaPage
        bind['dbpediaPage'] = ne.dbpediaPage
        sm.logProcessDebug("finished.") 
        return JSONHelper.toJSON(bind)	
		}
   }
}