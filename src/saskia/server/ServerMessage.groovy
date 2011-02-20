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

import org.apache.log4j.Logger
import saskia.util.I18n

/**
 * @author Nuno Cardoso
 *
 */
class ServerMessage {
    
    static Logger log
    static I18n i18n = I18n.newInstance()
    String signature 
    String lang = "en"
    Map bind
    long session
    String action
    
    public ServerMessage(String signature, String lang, Map bind, long session, Logger processinglog) {
		this.signature = signature
		if (lang) this.lang = lang // keep 'en' if it is not defined
		this.bind = bind 
		this.session = session
		log = processinglog
   }

   public setAction(String action) {this.action = action}
    
   public String noAPIKeyMessage(message = null) {
		return statusMessage(-1, i18n.servermessage['no_api_key'][lang], message)
   } 

   public String dailyAPILimitExceeded(message = null) {
		return statusMessage(-1, i18n.servermessage['api_key_limit_exceeded'][lang], message)
   } 

   public String notEnoughVars(message = null) {
		return statusMessage(-1, i18n.servermessage['not_enough_vars'][lang], message)
    }
    
   public String notEnabled() {
		return statusMessage(-1, i18n.servermessage['user_not_enabled'][lang], message)
   }

	public String unknownAction(String message = this.action) {
		return statusMessage(-1, i18n.servermessage['action_unknown'][lang], message)
   }
    
	public String noSuperUser(String message = this.action) {
		return statusMessage(-1, i18n.servermessage['no_super_user'][lang])
   }
    public String userNotFound(String message = null) {
		return statusMessage(-1, i18n.servermessage['no_user_found'][lang], message)
    }

   public String collectionNotFound(String message = null) {
		return statusMessage(-1, i18n.servermessage['collection_not_found'][lang], message)
    }

	public String invalidID(String message = null) {
		return statusMessage(-1, i18n.servermessage['invalid_id'][lang], message)
    }
	
	public String insufficientPermissions(String message = null) {
		return statusMessage(-1, i18n.servermessage['insufficient_permissions'][lang], message)
    }
  

	public String logProcessDebug(message) {
		log.debug ""+(session ? session+" ": "")+(signature ? signature+": ":"")+ 
		(action ? action+": " : " ") + message
	}
	
   public String statusMessage(int status, statusmessage, String message = null) {
		Map bind = [:]     
		bind["status"] = status
		bind["message"] = statusmessage
		String desc
		if (status == -1) desc = bind else desc = "[status:$status, message:OK]"
		log.debug ""+(session ? session+" ": "")+(signature ? signature+": ":"")+ 
		(action ? action+": " : " ") + desc + (message ? " - $message" : "")
		return JSONHelper.toJSON(bind)	
	}
	
	// use this only to send stuff that an admin requests
	 public String statusMessageWithPubKey(int status, statusmessage, String usr_pub_key) {
		Map bind = [:]     
		bind["status"] = status
		bind["message"] = statusmessage
		bind["usr_pub_key"] = usr_pub_key
		String desc
		if (status == -1) desc = bind else desc = "[status:$status, message:OK]"
		log.debug ""+(session ? session+" ": "")+(signature ? signature+": ":"")+ 
		(action ? action+": " : " ") + desc
		return JSONHelper.toJSON(bind)	
	}
}
