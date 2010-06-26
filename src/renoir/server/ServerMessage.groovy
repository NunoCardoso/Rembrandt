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
package renoir.server


import org.apache.log4j.Logger
import saskia.util.I18n

/**
 * @author Nuno Cardoso
 *
 */
class ServerMessage {
    
    static Logger log2 = Logger.getLogger("RenoirService") 	
    static I18n i18n = I18n.newInstance()
    String signature 
    String lang = "en"
    Map bind
    long session
    String action
    
    public ServerMessage(String signature, String lang, Map bind, long session) {
	this.signature = signature
	if (lang) this.lang = lang // keep 'en' if it is not defined
	this.bind = bind 
	this.session = session
    }

    public setAction(String action) {this.action = action}
    
    public String noAPIKeyMessage() {
        Map bind = [:]
	bind["status"] = -1
        bind["message"] = i18n.servermessage['no_api_key'][lang]
        log2.debug "$bind"
        return JSONHelper.toJSON(bind)	
    } 
    
    public String notEnoughVars(message = null) {
	Map bind = [:]     
	bind["status"] = -1
	bind["message"] = i18n.servermessage['not_enough_vars'][lang]	                                                        
	log2.debug ""+(session ? session+" ": "")+(signature ? signature+": ":"")+ 
	(action ? action+": " : " ") + bind + (message ? " - $message" : "")
        return JSONHelper.toJSON(bind)	
    }
    
    public String notEnabled() {
	Map bind = [:]     
	bind["status"] = -1
	bind["message"] = i18n.servermessage['user_not_enabled'][lang]	                                                        
	log2.debug ""+(session ? session+" ": "")+(signature ? signature+": ":"")+ 
	(action ? action+": " : " ") + bind
        return JSONHelper.toJSON(bind)	
    }
    
    public String unknownAction(String action = this.action) {
	Map bind = [:]     
	bind["status"] = -1
	bind["message"] =i18n.servermessage['action_unknown'][lang]
	log2.debug "$session $signature:$action: $bind - action $action unknown"
        return JSONHelper.toJSON(bind)	
    }
    
    public String userNotFound(String message = null) {
	Map bind = [:]     
	bind["status"] = -1
	bind["message"] = i18n.servermessage['no_user_found'][lang]	                                                        
	log2.debug ""+(session ? session+" ": "")+(signature ? signature+":":"")+ 
	(action ? action+": " : " ") + bind +  (message ? " - $message" : "")
        return JSONHelper.toJSON(bind)	
    }
    
    public String noSuperUser() {
       return statusMessage(-1, i18n.servermessage['no_superuser'][lang])
    }
   
    public String statusMessage(int status, message) {
	Map bind = [:]     
	bind["status"] = status
	bind["message"] = message
	String desc
	if (status == -1) desc = bind else desc = "[status:$status, message:OK]"
	log2.debug "$session $signature: $desc"// - user_login ${user}"
	return JSONHelper.toJSON(bind)	
   }
   
}
