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

import saskia.bin.SaskiaQuerier
import saskia.io.RembrandtedDoc
import saskia.io.Collection
import saskia.io.User
//import saskia.converters.RembrandtedDocument2PlainTextConverter
import saskia.util.I18n

public class SearchMapping extends WebServiceRestletMapping {

	SaskiaQuerier rpq
	//RembrandtedDocument2PlainTextConverter r2t
	Closure JSONanswer
	User user
	Collection collection
	I18n i18n
	
	public SearchMapping() {
	
	    rpq = new SaskiaQuerier()
	 //   r2t = new RembrandtedDocument2PlainTextConverter()
		

	    JSONanswer = {req, par, bind ->
	    
	    i18n = I18n.newInstance()
		String query = java.net.URLDecoder.decode(par["GET"]["q"])
		
		String collection_name = par["POST"]["c"]
		String user_login = par["POST"]["u"]
		String api_key = par["POST"]["api_key"]
		String lang = par["POST"]["lg"]
		int limit = (par["POST"]["l"] ? Integer.parseInt(par["POST"]["l"]) : 10)
		int offset = (par["POST"]["o"] ?  Integer.parseInt(par["POST"]["o"]) : 0)
		
                collection = Collection.getFromName(collection_name)
                if (!collection) {
                    bind["status"] = -1
                    bind["message"] = i18n.servermessage['collection_not_found'][lang]+"."
                    log.debug "Returning status -1, no collection found for c $collection_name"
                    return  JSONHelper.toJSON(bind)
                }
                
                if (api_key) {
                    user = User.getFromAPIKey(api_key)
                } else {
                    if (user_login) { 
                        if (user_login == User.guests[lang]) user_login=User.guest
                        user = User.getFromLogin(user_login)
                    } else {
                        user_login == User.guests[lang]
                        user = User.getFromLogin(user_login)
                    }            
                }
                
                log.debug "User: $user"
                		
		
		List result = []
		HashMap answer 
		try {
		    answer = rpq.queryWithTerms(query, 0, collection, limit, offset) 
		}catch(Exception e) {
		    bind["status"] = -1
		    bind["message"] = ""+e.printStackTrace()
		    return  JSONHelper.toJSON(bind)		    
		}
		
		bind["offset"] = answer["offset"]
		bind["limit"] = answer["limit"]
		bind["total"] = answer["total"]
		
		answer["result"].each{rdoc -> result << [           
			 'title':rdoc.getTitleFromContent(), 
			 'id':rdoc.doc_id, 
			 'abstract':rdoc.getBodyFromContent(),
			 'size':rdoc.getBodyFromContent().size(), 
			 'date':""+rdoc.doc_date_created,
			 'comment':""+rdoc.rdoc_comment
			 ]
		}
		    
		bind["result"] = result
		//log.debug result
		return JSONHelper.toJSON(bind)		
	    }
	 }
	
	 /*private String parse(String string) {
	     string = r2t.parse(string)
	 	if (string) string = string.substring(0,(string.size() > 300 ? 300 : string.size()))
	 		     // \n are not valid json chars
	 	return string.replaceAll(/\n/,"")
	 }*/
}
